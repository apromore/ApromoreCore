/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.service.csvimporter.services;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.schema.MessageType;
import org.apromore.service.csvimporter.dateparser.Parse;
import org.apromore.service.csvimporter.io.ParquetFileWriter;
import org.apromore.service.csvimporter.io.ParquetLocalFileReader;
import org.apromore.service.csvimporter.io.ParquetLocalFileWriter;
import org.apromore.service.csvimporter.model.*;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apromore.service.csvimporter.utilities.ParquetUtilities.createParquetSchema;
import static org.apromore.service.csvimporter.utilities.ParquetUtilities.getHeaderFromParquet;

class ParquetToParquetExporter implements ParquetExporter {
    private final Parse parse = new Parse();
    boolean preferMonthFirstChanged;
    private List<LogErrorReport> logErrorReport;
    private boolean validRow;

    @Override
    public LogModel generateParqeuetFile(InputStream in, LogSample sample, String charset, File outputParquet, boolean skipInvalidRow) throws Exception {

        ParquetLogSampleImpl parquetLogSample = (ParquetLogSampleImpl) sample;
        parquetLogSample.validateSample();

        //If file exist, delete it
        if (outputParquet.exists())
            outputParquet.delete();

        File tempFile = parquetLogSample.getParquetTempFile();
        if (tempFile == null)
            throw new Exception("Imported file cant be found!");

        //Read Parquet file
        ParquetLocalFileReader parquetLocalFileReader = new ParquetLocalFileReader(new Configuration(true), tempFile);
        MessageType tempFileSchema = parquetLocalFileReader.getSchema();
        ParquetReader<Group> reader = parquetLocalFileReader.getParquetReader();

        if (reader == null)
            return null;

        String[] header = getHeaderFromParquet(tempFileSchema).toArray(new String[0]);
        MessageType sampleSchema = createParquetSchema(header, sample);
        ParquetFileWriter writer = new ParquetLocalFileWriter().getParquetWriter(outputParquet, sampleSchema);

        logErrorReport = new ArrayList<>();
        int lineIndex = 0;
        int numOfValidEvents = 0;
        boolean preferMonthFirst = preferMonthFirstChanged = parse.getPreferMonthFirst();

        String[] line;

        String caseId;
        String activity;
        Timestamp endTimestamp;
        Timestamp startTimestamp;
        String resource;
        HashMap<String, String> caseAttributes;
        HashMap<String, String> eventAttributes;
        HashMap<String, Timestamp> otherTimestamps;

        String errorMessage = "Field is empty or has a null value!";
        boolean rowLimitExceeded = false;

        Group g;
        while ((g = reader.read()) != null && isValidLineCount(lineIndex)) {

            try {
                line = readGroup(g, tempFileSchema);
            } catch (Exception e) {
                logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null, "Cant read line. " + e.getMessage()));
                continue;
            }

            // new row, new event.
            validRow = true;
            lineIndex++;

            //empty row
            if (line.length == 0 || (line.length == 1 && (line[0].trim().equals("") || line[0].trim().equals("\n"))))
                continue;

            //Validate num of column
            if (header.length != line.length) {
                logErrorReport.add(new LogErrorReportImpl(lineIndex, 0, null, "Number of columns does not match the number of headers. Number of headers: (" + header.length + "). Number of columns: (" + line.length + ")"));
                continue;
            }

            //Construct an event
            startTimestamp = null;
            resource = null;
            caseAttributes = new HashMap<>();
            eventAttributes = new HashMap<>();
            otherTimestamps = new HashMap<>();


            // Case id:
            caseId = line[sample.getCaseIdPos()];
            if (caseId == null || caseId.isEmpty()) {
                invalidRow(new LogErrorReportImpl(lineIndex, sample.getCaseIdPos(), header[sample.getCaseIdPos()], errorMessage));
            }

            // Activity
            activity = line[sample.getActivityPos()];
            if (activity == null || activity.isEmpty()) {
                invalidRow(new LogErrorReportImpl(lineIndex, sample.getActivityPos(), header[sample.getActivityPos()], errorMessage));
            }

            // End Timestamp
            endTimestamp = parseTimestampValue(line[sample.getEndTimestampPos()], sample.getEndTimestampFormat());
            if (endTimestamp == null) {
                invalidRow(new LogErrorReportImpl(lineIndex, sample.getEndTimestampPos(), header[sample.getEndTimestampPos()], parse.getParseFailMess()));
            }
            // Start Timestamp
            if (sample.getStartTimestampPos() != -1) {
                startTimestamp = parseTimestampValue(line[sample.getStartTimestampPos()], sample.getStartTimestampFormat());
                if (startTimestamp == null) {
                    invalidRow(new LogErrorReportImpl(lineIndex, sample.getStartTimestampPos(), header[sample.getStartTimestampPos()], parse.getParseFailMess()));
                }
            }

            // Other timestamps
            if (!sample.getOtherTimestamps().isEmpty()) {
                for (Map.Entry<Integer, String> otherTimestamp : sample.getOtherTimestamps().entrySet()) {
                    Timestamp tempTimestamp = parseTimestampValue(line[otherTimestamp.getKey()], otherTimestamp.getValue());
                    if (tempTimestamp != null) {
                        otherTimestamps.put(header[otherTimestamp.getKey()], tempTimestamp);
                    } else {
                        invalidRow(new LogErrorReportImpl(lineIndex, otherTimestamp.getKey(), header[otherTimestamp.getKey()], parse.getParseFailMess()));
                    }
                }
            }

            // If PreferMonthFirst changed to True, we have to start over.
            if (!preferMonthFirst && preferMonthFirstChanged)
                generateParqeuetFile(in, sample, charset, outputParquet, skipInvalidRow);


            // Resource
            if (sample.getResourcePos() != -1) {
                resource = line[sample.getResourcePos()];
                if (resource == null || resource.isEmpty()) {
                    invalidRow(new LogErrorReportImpl(lineIndex, sample.getResourcePos(), header[sample.getResourcePos()], errorMessage));
                }
            }

            // If row is invalid, continue to next row.
            if (!validRow) {
                if (skipInvalidRow) {
                    continue;
                } else {
                    //Upon migrating to parquet, xlog need to be removed and LogModelXLogImpl need to be renamed
                    return new LogModelXLogImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents);
                }
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

            writer.write(new LogEventModel(caseId, activity, endTimestamp, startTimestamp, otherTimestamps, resource, eventAttributes, caseAttributes));
            numOfValidEvents++;
        }
        writer.close();

        if (!isValidLineCount(lineIndex))
            rowLimitExceeded = true;
        //Upon migrating to parquet, xlog need to be removed and LogModelXLogImpl need to be renamed
        return new LogModelXLogImpl(null, logErrorReport, rowLimitExceeded, numOfValidEvents);
    }

    private boolean isValidLineCount(int lineCount) {
        return true;
    }

    private Timestamp parseTimestampValue(String theValue, String format) {
        Timestamp stamp;
        if (format != null && !format.isEmpty()) {
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

    private void invalidRow(LogErrorReportImpl error) {
        logErrorReport.add(error);
        validRow = false;
    }

    private String[] readGroup(Group g, MessageType schema) throws UnsupportedEncodingException {

        String[] line = new String[schema.getColumns().size()];
        for (int j = 0; j < schema.getFieldCount(); j++) {

            String valueToString = g.getValueToString(j, 0);
            line[j] = valueToString;
        }
        return line;
    }
}