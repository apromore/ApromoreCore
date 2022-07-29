/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.logimporter.services.legacy;

import static org.apromore.service.logimporter.utilities.ParquetUtilities.getHeaderFromParquet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.dao.model.Log;
import org.apromore.service.logimporter.constants.Constants;
import org.apromore.service.logimporter.io.ParquetLocalFileReader;
import org.apromore.service.logimporter.model.LogErrorReportImpl;
import org.apromore.service.logimporter.model.LogEventModel;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.model.LogModel;
import org.apromore.service.logimporter.model.LogModelImpl;
import org.apromore.service.logimporter.model.ParquetLogMetaData;
import org.apromore.service.logimporter.services.LogProcessorImpl;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service("parquetLogImporter")
public class LogImporterParquetImpl extends AbstractLogImporter implements Constants {

    public static final String CONCEPT_NAME = "concept:name";
    private ParquetReader<Group> reader;

    @Override
    public LogModel importLog(InputStream in, LogMetaData logMetaData, String charset,
                              boolean skipInvalidRow, String username, Integer folderId, String logName)
        throws Exception {
        try {
            ParquetLogMetaData parquetLogSample = (ParquetLogMetaData) logMetaData;
            parquetLogSample.validateSample();

            File tempFile = parquetLogSample.getParquetTempFile();
            if (tempFile == null) {
                throw new Exception("Imported file cant be found!");
            }

            // Read Parquet file
            ParquetLocalFileReader parquetLocalFileReader =
                new ParquetLocalFileReader(new Configuration(true), tempFile);
            MessageType tempFileSchema = parquetLocalFileReader.getSchema();
            reader = parquetLocalFileReader.getParquetReader();

            if (reader == null) {
                return null;
            }

            String[] header = CollectionUtils.isEmpty(logMetaData.getHeader())
                ? getHeaderFromParquet(tempFileSchema).toArray(new String[0])
                : logMetaData.getHeader().toArray(new String[0]);

            logProcessor = new LogProcessorImpl();
            logErrorReport = new ArrayList<>();
            int lineIndex = 0;
            int numOfValidEvents = 0;
            String[] line;
            TreeMap<String, XTrace> tracesHistory = new TreeMap<String, XTrace>(); // Keep track of traces
            boolean rowLimitExceeded = false;
            LogEventModel logEventModel;

            // XES
            XFactory xfactory = new XFactoryNaiveImpl();
            final XConceptExtension concept = XConceptExtension.instance();
            final XLifecycleExtension lifecycle = XLifecycleExtension.instance();
            final XTimeExtension timestamp = XTimeExtension.instance();
            final XOrganizationalExtension resourceXes = XOrganizationalExtension.instance();

            XLog xlog;
            xlog = xfactory.createLog();
            xlog.getExtensions().add(concept);
            xlog.getExtensions().add(lifecycle);
            xlog.getExtensions().add(timestamp);
            xlog.getExtensions().add(resourceXes);
            lifecycle.assignModel(xlog, XLifecycleExtension.VALUE_MODEL_STANDARD);

            Group g;
            while ((g = reader.read()) != null) {

                // row in excess of the allowed limit
                if (!isValidLineCount(lineIndex + 1)) {
                    rowLimitExceeded = true;
                    break;
                }

                try {
                    line = readGroup(g, tempFileSchema);
                } catch (Exception e) {
                    logErrorReport
                        .add(new LogErrorReportImpl(lineIndex, 0, null, "Cant read line. " + e.getMessage()));
                    continue;
                }

                // new row, new event.
                lineIndex++;

                // empty row
                if (line.length == 0
                    || (line.length == 1 && (line[0].trim().equals("") || line[0].trim().equals("\n")))) {
                    continue;
                }

                // Validate num of column
                if (header.length != line.length) {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null,
                        "Number of columns does not match the number of headers. Number of headers: ("
                            + header.length + "). Number of columns: (" + line.length + ")"));
                    continue;
                }

                // Construct an event
                logEventModel = logProcessor.processLog(Arrays.asList(line), Arrays.asList(header),
                    logMetaData, lineIndex, logErrorReport);

                // If row is invalid, continue to next row.
                if (!logEventModel.isValid()) {
                    continue;
                }

                // Construct a Trace if it's not exists
                constructTrace(tracesHistory, logEventModel, xfactory, concept);
                numOfValidEvents++;
            }

            // Sort and feed xLog
            sortAndFeedLog(tracesHistory, xlog);

            validateXLog(xlog);

            // Import XES when there is no invalid row
            Log log = importXesLog(username, folderId, logName, skipInvalidRow, xlog);

            return new LogModelImpl(xlog, logErrorReport, rowLimitExceeded, numOfValidEvents, log);

        } finally {
            closeQuietly(in);
        }
    }

    private String[] readGroup(Group g, MessageType schema) {

        String[] line = new String[schema.getColumns().size()];
        for (int j = 0; j < schema.getFieldCount(); j++) {
            String valueToString;
            try {
                valueToString = g.getValueToString(j, 0);
            } catch (RuntimeException e) {
                valueToString = "";
            }
            line[j] = valueToString;
        }
        return line;
    }

    private void closeQuietly(InputStream in) throws IOException {
        if (in != null) {
            in.close();
        }
        if (this.reader != null) {
            this.reader.close();
        }
    }

    private void validateXLog(XLog log) {
        if (!log.get(0).getAttributes().containsKey(CONCEPT_NAME)) {
            return;
        }

        for (XTrace trace : log) {
            String name = trace.getAttributes().get(CONCEPT_NAME).toString().trim();
            trace.getAttributes().put(CONCEPT_NAME,
                new XAttributeLiteralImpl(CONCEPT_NAME, name));
        }

        Map<String, List<XTrace>> caseIdMap = log.stream()
            .collect(Collectors.groupingBy(x -> x.getAttributes().get(CONCEPT_NAME).toString().trim()));

        for (Map.Entry<String, List<XTrace>> entry : caseIdMap.entrySet()) {

            Set<String> attrKeys = entry.getValue().stream()
                .flatMap(x -> x.getAttributes().keySet().stream())
                .collect(Collectors.toSet());
            attrKeys.remove(CONCEPT_NAME);
            Map<String, Set<String>> attrUniqueVal = new HashMap<>();
            for (String s : attrKeys) {
                Set<String> vals = entry.getValue().stream()
                    .filter(x -> x.getAttributes().containsKey(s))
                    .map(x -> x.getAttributes().get(s).toString().trim())
                    .collect(Collectors.toSet());
                attrUniqueVal.put(s, vals);
            }

            boolean anyMoreThan1 = attrUniqueVal.entrySet().stream().anyMatch(x -> x.getValue().size() > 1);

            if (anyMoreThan1) { // modify the case ID
                for (int i = 0; i < entry.getValue().size(); i++) {
                    String caseId = entry.getKey() + " (" + (i + 1) + ")";
                    entry.getValue().get(i).getAttributes().put(CONCEPT_NAME,
                        new XAttributeLiteralImpl(CONCEPT_NAME, caseId));
                }
            } else { // joint traces
                XTrace trace0 = entry.getValue().get(0);
                List<XTrace> others =  entry.getValue().subList(1, entry.getValue().size());
                for (XTrace trace : others) {
                    trace0.addAll(new ArrayList<>(trace));
                }

                List<XEvent> events = trace0.stream()
                    .sorted(Comparator.comparing(this::getTimestamp))
                    .collect(Collectors.toList());

                trace0.clear();
                trace0.addAll(events);

                log.removeAll(others);
            }
        }
    }

    private long getTimestamp(XEvent event) {
        try {
            ZonedDateTime zdt = zonedDateTimeOf(event);
            return epochMilliOf(zdt);
        } catch (Exception e) {
            return 0;
        }
    }

    private long epochMilliOf(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
    }

    private ZonedDateTime zonedDateTimeOf(XEvent event) {
        String timestamp = event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP).toString();

        timestamp = validateTimestamp(timestamp);

        return ZonedDateTime.parse(timestamp);
    }

    private String validateTimestamp(String timestamp) {
        //0000-00-00T00:00:00.000+00:00
        String charAt10 = timestamp.substring(10, 11);
        String validTimestamp = timestamp;
        if (charAt10.equals(" ")) {
            validTimestamp = timestamp.substring(0, 10) + "T" + timestamp.substring(11);
        }
        return validTimestamp;
    }
}