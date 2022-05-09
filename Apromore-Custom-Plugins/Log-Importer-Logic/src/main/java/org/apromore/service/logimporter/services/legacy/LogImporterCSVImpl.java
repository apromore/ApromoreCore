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

import static org.apromore.service.logimporter.utilities.ImporterStringUtils.getMaxOccurringChar;

import com.google.common.base.Splitter;
import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import org.apache.commons.io.input.ReaderInputStream;
import org.apromore.dao.model.Log;
import org.apromore.service.logimporter.constants.Constants;
import org.apromore.service.logimporter.io.CSVFileReader;
import org.apromore.service.logimporter.model.LogErrorReportImpl;
import org.apromore.service.logimporter.model.LogEventModel;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.model.LogModel;
import org.apromore.service.logimporter.model.LogModelImpl;
import org.apromore.service.logimporter.services.LogProcessorImpl;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(
    value = "bigdata.log-importer.enabled",
    havingValue = "false",
    matchIfMissing = true)
@Service("csvLogImporter")
public class LogImporterCSVImpl extends AbstractLogImporter implements Constants {

    private Reader readerin;
    private BufferedReader brReader;
    private InputStream in2;
    private CSVReader reader;

    @Override
    public LogModel importLog(InputStream in, LogMetaData logMetaData, String charset,
                              boolean skipInvalidRow, String username, Integer folderId, String logName)
        throws Exception {

        try {
            logMetaData.validateSample();
            // Read the header
            readerin = new InputStreamReader(in, Charset.forName(charset));
            brReader = new BufferedReader(readerin);
            String firstLine = brReader.readLine();
            firstLine = firstLine.replaceAll("\"", "");
            char separator = getMaxOccurringChar(firstLine);
            List<String> headerList = Splitter.on(separator).splitToList(firstLine);

            // Read the reset of the log
            in2 = new ReaderInputStream(brReader, charset);
            reader = new CSVFileReader().newCSVReader(in2, charset, separator);

            if (reader == null) {
                return null;
            }

            logProcessor = new LogProcessorImpl();
            logErrorReport = new ArrayList<>();
            int lineIndex = 1; // set to 1 since first line is the header
            int numOfValidEvents = 0;

            TreeMap<String, XTrace> tracesHistory = new TreeMap<String, XTrace>(); // Keep track of traces
            boolean rowLimitExceeded = false;

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

            LogEventModel logEventModel;
            String[] line;
            while ((line = reader.readNext()) != null) {

                // row in excess of the allowed limit
                if (!isValidLineCount(lineIndex)) {
                    rowLimitExceeded = true;
                    break;
                }

                // new row, new event.
                lineIndex++;

                // empty row
                if (line.length == 0
                    || (line.length == 1 && ("".equals(line[0].trim()) || "\n".equals(line[0].trim())))) {
                    continue;
                }

                // Validate num of column
                if (headerList.size() != line.length) {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null,
                        "Number of columns does not match " + "the number of headers. Number of headers: ("
                        + headerList.size() + "). Number of columns: (" + line.length + ")"));
                    continue;
                }

                // Construct an event
                logEventModel = logProcessor.processLog(Arrays.asList(line), headerList, logMetaData,
                    lineIndex, logErrorReport);

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

            // Import XES
            Log log = importXesLog(username, folderId, logName, skipInvalidRow, xlog);

            return new LogModelImpl(xlog, logErrorReport, rowLimitExceeded, numOfValidEvents, log);

        } finally {
            closeQuietly(in);
        }
    }

    private void closeQuietly(InputStream in) throws IOException {
        if (in != null) {
            in.close();
        }
        if (this.readerin != null) {
            this.readerin.close();
        }
        if (this.brReader != null) {
            this.brReader.close();
        }
        if (this.reader != null) {
            this.reader.close();
        }
        if (this.in2 != null) {
            this.in2.close();
        }
    }
}
