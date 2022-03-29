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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apromore.dao.model.Log;
import org.apromore.service.logimporter.constants.Constants;
import org.apromore.service.logimporter.io.XLSReader;
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
import org.springframework.stereotype.Service;

@Service("xlsxLogImporter")
public class LogImporterXLSXImpl extends AbstractLogImporter implements Constants {

    private static final int BUFFER_SIZE = 2048;
    private static final int DEFAULT_NUMBER_OF_ROWS = 100;

    @Override
    public LogModel importLog(InputStream in, LogMetaData logMetaData, String charset,
                              boolean skipInvalidRow, String username, Integer folderId, String logName)
        throws Exception {

        try (Workbook workbook = new XLSReader().readXLS(in, DEFAULT_NUMBER_OF_ROWS, BUFFER_SIZE)) {
            logMetaData.validateSample();
            if (workbook == null) {
                throw new Exception("Unable to import file");
            }

            // Process first sheet only
            Sheet sheet = workbook.getSheetAt(0);
            // Get the header
            if (sheet == null) {
                throw new Exception("Unable to import file");
            }

            String[] header = logMetaData.getHeader().toArray(new String[0]);
            logProcessor = new LogProcessorImpl();
            logErrorReport = new ArrayList<>();
            int lineIndex = 0;
            int numOfValidEvents = 0;
            String[] line;
            TreeMap<String, XTrace> tracesHistory = new TreeMap<String, XTrace>(); // Keep track of traces
            boolean rowLimitExceeded = false;

            // XES
            XFactory xfactory = new XFactoryNaiveImpl();
            XConceptExtension concept = XConceptExtension.instance();
            XLifecycleExtension lifecycle = XLifecycleExtension.instance();
            XTimeExtension timestamp = XTimeExtension.instance();
            XOrganizationalExtension resourceXes = XOrganizationalExtension.instance();

            XLog xlog = xfactory.createLog();
            xlog.getExtensions().add(concept);
            xlog.getExtensions().add(lifecycle);
            xlog.getExtensions().add(timestamp);
            xlog.getExtensions().add(resourceXes);

            lifecycle.assignModel(xlog, XLifecycleExtension.VALUE_MODEL_STANDARD);
            LogEventModel logEventModel;

            for (Row r : sheet) {
                // Skip header
                if (r.getRowNum() == 0) {
                    continue;
                }

                // row in excess of the allowed limit
                if (!isValidLineCount(lineIndex + 1)) {
                    rowLimitExceeded = true;
                    break;
                }

                // new row, new event.
                lineIndex++;

                // Validate num of column
                if (r.getPhysicalNumberOfCells() > header.length) {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null,
                        "Number of columns does not match the number of headers. Number of headers: ("
                        + header.length + "). Number of columns: (" + r.getPhysicalNumberOfCells()
                        + ")"));
                    continue;
                }

                line = new String[header.length];
                // Get the rows
                for (Cell c : r) {
                    line[c.getColumnIndex()] = c.getStringCellValue();
                }

                // empty row
                if (line.length == 0
                    || (line.length == 1 && (line[0].trim().equals("") || line[0].trim().equals("\n")))) {
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

            // Import XES when there is no invalid row
            Log log = importXesLog(username, folderId, logName, skipInvalidRow, xlog);

            return new LogModelImpl(xlog, logErrorReport, rowLimitExceeded, numOfValidEvents, log);
        } catch (Exception e) {
            throw e;
        } finally {
            in.close();
        }
    }

}
