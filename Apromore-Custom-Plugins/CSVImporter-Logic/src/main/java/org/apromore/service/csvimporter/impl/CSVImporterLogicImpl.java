/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.csvimporter.impl;

import com.opencsv.CSVReader;
import org.apromore.service.csvimporter.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

public class CSVImporterLogicImpl implements CSVImporterLogic, Constants {


    @Override
    public LogSample sampleCSV(CSVReader reader, int sampleSize) throws IOException {

        List<String> header = Arrays.asList(reader.readNext());

        List<List<String>> lines = new ArrayList<>();
        String[] myLine;
        while ((myLine = reader.readNext()) != null && myLine.length == header.size() && lines.size() <= sampleSize) {
            lines.add(Arrays.asList(myLine));
        }

        return new LogSampleImpl(header, lines);
    }

    private final Parse parse = new Parse();

    boolean preferMonthFirstChanged;
    private List<LogErrorReport> logErrorReport;
    private boolean validRow;

    @Override
    public LogModel prepareXesModel(CSVReader reader, LogSample sample) throws IOException {
        logErrorReport = new ArrayList<>();
        int lineIndex = 1; // set to 1 since first line is the header
        boolean preferMonthFirst = preferMonthFirstChanged = parse.getPreferMonthFirst();
        try {

            String[] header = reader.readNext();

            List<LogEventModel> logData = new ArrayList<>();
            HashMap<String, Timestamp> otherTimestamps;
            HashMap<String, String> eventAttributes;
            HashMap<String, String> caseAttributes;
            String caseId;
            String activity;
            Timestamp endTimestamp;
            Timestamp startTimestamp;
            String resource;

            String[] line;

            while ((line = reader.readNext()) != null) { // new row, new event.
                validRow = true;
                lineIndex++;

                if (line.length == 0) {
                    continue;
                }

                if (header.length != line.length) {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, "Number of columns does not match the number of headers. Number of headers: (" + header.length + "). Number of columns: (" + line.length + ")."));
                    continue;
                }

                otherTimestamps = new HashMap<>();
                eventAttributes = new HashMap<>();
                caseAttributes = new HashMap<>();
                startTimestamp = null;
                resource = null;


                // Case id:
                caseId = line[sample.getUniqueAttributes().get(caseIdLabel)];
                if (caseId == null || caseId.isEmpty()) {
                    invalidRow(lineIndex, sample.getUniqueAttributes().get(caseIdLabel), header[sample.getUniqueAttributes().get(caseIdLabel)], "Case ID");
                }

                // Activity
                activity = line[sample.getUniqueAttributes().get(activityLabel)];
                if (activity == null || activity.isEmpty()) {
                    invalidRow(lineIndex, sample.getUniqueAttributes().get(activityLabel), header[sample.getUniqueAttributes().get(activityLabel)], "Activity");
                }

                // End Timestamp
                endTimestamp = parseTimestampValue(line[sample.getUniqueAttributes().get(endTimestampLabel)], sample.getEndTimestampFormat());
                if (endTimestamp == null) {
                    invalidRow(lineIndex, sample.getUniqueAttributes().get(endTimestampLabel), header[sample.getUniqueAttributes().get(endTimestampLabel)], "End Timestamp");
                }

                // Start Timestamp
                if (sample.getUniqueAttributes().get(startTimestampLabel) != -1) {
                    startTimestamp = parseTimestampValue(line[sample.getUniqueAttributes().get(startTimestampLabel)], sample.getStartTimestampFormat());
                    if (startTimestamp == null) {
                        invalidRow(lineIndex, sample.getUniqueAttributes().get(startTimestampLabel), header[sample.getUniqueAttributes().get(startTimestampLabel)], "Start Timestamp");
                    }
                }


                // Other timestamps
                if (!sample.getOtherTimestamps().isEmpty()) {
                    for (Map.Entry<Integer, String> otherTimestamp : sample.getOtherTimestamps().entrySet()) {
                        Timestamp tempTimestamp = parseTimestampValue(line[otherTimestamp.getKey()], otherTimestamp.getValue());
                        if (tempTimestamp != null) {
                            otherTimestamps.put(header[otherTimestamp.getKey()], tempTimestamp);
                        } else {
                            invalidRow(lineIndex, otherTimestamp.getKey(), header[otherTimestamp.getKey()], "Other timestamp");
                        }
                    }
                }

                // If PreferMonthFirst changed to True, we have to start over.
                if (!preferMonthFirst && preferMonthFirstChanged) {
                    prepareXesModel(reader, sample);
                }


                // Resource
                if (sample.getUniqueAttributes().get(resourceLabel) != -1) {
                    resource = line[sample.getUniqueAttributes().get(resourceLabel)];
                    if (resource == null) {
                        invalidRow(lineIndex, sample.getUniqueAttributes().get(resourceLabel), header[sample.getUniqueAttributes().get(resourceLabel)], "Resource");
                    }
                }

                // If row is invalid, continue to next row.
                if (!validRow) {
                    continue;
                }

                // Case Attributes
                if (sample.getCaseAttributesPos() != null && !sample.getCaseAttributesPos().isEmpty()) {
                    for (int columnPos : sample.getCaseAttributesPos()) {
                        caseAttributes.put(header[columnPos], line[columnPos]);
                    }
                }

                // Event Attributes
                if (sample.getEventAttributesPos() != null && !sample.getEventAttributesPos().isEmpty()) {
                    for (int columnPos : sample.getEventAttributesPos()) {
                        eventAttributes.put(header[columnPos], line[columnPos]);
                    }
                }

                logData.add(new LogEventModelImpl(caseId, activity, endTimestamp, startTimestamp, otherTimestamps, resource, eventAttributes, caseAttributes));
            }

            return new LogModelImpl(sortTraces(logData), logErrorReport);

        } catch (Exception e) {
           throw e;
        }

    }

    private Timestamp parseTimestampValue(String theValue, String format) {
        if(theValue == null || theValue.isEmpty()) return null;

        Timestamp stamp;
        if (format != null) {
            stamp = parse.tryParsingWithFormat(theValue, format);
            if (stamp == null) {
                stamp = parse.tryParsing(theValue);
                if (!preferMonthFirstChanged) {
                    preferMonthFirstChanged = parse.getPreferMonthFirst();
                }

            }
        } else {
            stamp = parse.tryParsing(theValue);
            if (!preferMonthFirstChanged) {
                preferMonthFirstChanged = parse.getPreferMonthFirst();
            }
        }
        return stamp;
    }

    private void invalidRow(int rowIndex, int columnIndex, String header, String error) {
        logErrorReport.add(new LogErrorReportImpl(rowIndex, columnIndex, header, "Invalid " + error + " value!"));
        validRow = false;
    }

    private static List<LogEventModel> sortTraces(List<LogEventModel> traces) {
        Comparator<String> nameOrder = new NameComparator();
        traces.sort((o1, o2) -> nameOrder.compare(o1.getCaseID(), o2.getCaseID()));
        return traces;
    }
}
