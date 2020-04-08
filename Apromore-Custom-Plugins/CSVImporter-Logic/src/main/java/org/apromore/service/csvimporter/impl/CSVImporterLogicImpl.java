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
import org.apache.commons.lang3.exception.ExceptionUtils;
import java.io.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.InvalidCSVException;
import org.apromore.service.csvimporter.LogEventModel;
import org.apromore.service.csvimporter.LogModel;
import org.apromore.service.csvimporter.LogSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVImporterLogicImpl implements CSVImporterLogic, Constants {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVImporterLogicImpl.class);

    @Override
    public LogSample sampleCSV(CSVReader reader, int sampleSize) throws InvalidCSVException, IOException {

        // Obtain the header
        List<String> header = Arrays.asList(reader.readNext());

        // Obtain the sample of lines
        List<List<String>> lines = new ArrayList<>();
        String[] myLine = null;
        while ((myLine = reader.readNext()) != null && myLine.length == header.size() && lines.size() <= sampleSize) {
            lines.add(Arrays.asList(myLine));
        }

        // Construct the sample (no mutation expected after this point, although this isn't enforced by the code))
        return new LogSampleImpl(header, lines);
    }


    private final Parse parse = new Parse();
    private ArrayList<String> invalidRows;
    private int errorCount;


    @Override
    public LogModel prepareXesModel(CSVReader reader, LogSample sample, double errorAcceptance) throws InvalidCSVException, IOException {

        // If any of the mandatory fields are missing show alert message to the user and return
        StringBuilder headNOTDefined = validateMainAttributes(sample.getUniqueAttributes());
        if (headNOTDefined.length() != 0) {
            throw new InvalidCSVException(headNOTDefined.toString());
        }
        invalidRows = new ArrayList<>();
        errorCount = 0;
        int lineIndex = 0;
        int emptyLine = 0;
        boolean errorCheck = false;

        try {

            // read first line from CSV as header
            String[] header = reader.readNext();

            // create model "LogEventModel" of the log data
            // We set mandatory fields and other fields are set with hash map
            List<LogEventModel> logData = new ArrayList<>();
            HashMap<String, Timestamp> otherTimestamps;
            HashMap<String, String> eventAttributes;
            HashMap<String, String> caseAttributes;

            String caseId;
            String activity;
            Timestamp endTimestamp;
            Timestamp startTimestamp = null;
            String resource = null;

            String errorMessage = null;

            for (Iterator<String[]> it = reader.iterator(); emptyLine < 50 && isValidLineCount(lineIndex);) {
                String[] line = it.next();
                if(line == null) {
                    // if line is empty, move to next iteration, until 50 lines are empty
                    emptyLine++;
                    continue;
                } else {
                    lineIndex++;
                }

                if (header.length != line.length) {
                    invalidRows.add("Row: " + lineIndex + ", Error: number of columns does not match" +
                            " number of headers. " + "Number of headers: " + header.length + "," +
                            " Number of columns: " + line.length + ".\n");
                    errorCount++;
                    continue;
                }

                try {
                    otherTimestamps = new HashMap<>();
                    eventAttributes = new HashMap<>();
                    caseAttributes = new HashMap<>();

                    // Case id:
                    caseId = line[sample.getUniqueAttributes().get(caseIdLabel)];
                    if(caseId == null || caseId.isEmpty()){
                        invalidRow(lineIndex, "Case ID");
                        continue;
                    }

                    // Activity
                    activity = line[sample.getUniqueAttributes().get(activityLabel)];
                    if(activity == null || activity.isEmpty()){
                        invalidRow(lineIndex, "Activity");
                        continue;
                    }

                    // End Timestamp
                    endTimestamp = parseTimestampValue(line[sample.getUniqueAttributes().get(endTimestampLabel)], sample.getEndTimestampFormat());
                    if(endTimestamp == null){
                        invalidRow(lineIndex, "End Timestamp");
                        continue;
                    }

                    // Start Timestamp
                    if (sample.getUniqueAttributes().get(startTimestampLabel) != -1) {
                        startTimestamp = parseTimestampValue(line[sample.getUniqueAttributes().get(startTimestampLabel)], sample.getStartTimestampFormat());
                        if(startTimestamp == null){
                            invalidRow(lineIndex, "Start Timestamp");
                            continue;
                        }
                    }

                    // Resource
                    if (sample.getUniqueAttributes().get(resourceLabel) != -1) {
                        resource = line[sample.getUniqueAttributes().get(resourceLabel)];
                        if(resource == null){
                            invalidRow(lineIndex, "Resource");
                            continue;
                        }
                    }

                    // Other timestamps
                    if(!sample.getOtherTimestamps().isEmpty()){
                        for (Map.Entry<Integer, String> otherTimestamp : sample.getOtherTimestamps().entrySet()) {
                            Timestamp tempTimestamp = parseTimestampValue(line[otherTimestamp.getKey()], otherTimestamp.getValue());
                            if(tempTimestamp!=null){
                                otherTimestamps.put(header[otherTimestamp.getKey()], tempTimestamp);
                            } else {
                                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = dateFormat.parse("01/01/1900");
                                long time = date.getTime();
                                Timestamp tempTime = new Timestamp(time);

                                otherTimestamps.put(header[otherTimestamp.getKey()],tempTime);

                                invalidRow(lineIndex, "Timestamp (" + header[otherTimestamp.getKey()] + ")");
                                errorCheck = true;
                                break;
                            }
                        }
                        continue;
                    }

                    // Case Attributes
                    if(sample.getCaseAttributesPos() != null && !sample.getCaseAttributesPos().isEmpty()){
                        for (int columnPos: sample.getCaseAttributesPos()) {
                            caseAttributes.put(header[columnPos], line[columnPos]);
                        }
                    }

                    // Event Attributes
                    if(sample.getEventAttributesPos()!= null && !sample.getEventAttributesPos().isEmpty()){
                        for (int columnPos: sample.getEventAttributesPos()) {
                            eventAttributes.put(header[columnPos], line[columnPos]);
                        }
                    }

                    logData.add(new LogEventModelImpl(caseId, activity, endTimestamp, startTimestamp, otherTimestamps, resource, eventAttributes, caseAttributes));
                } catch (Exception e) {
                    errorMessage = ExceptionUtils.getStackTrace(e);
                    e.printStackTrace();
                    errorCount++;
                    if (line.length > 4) {
                        invalidRows.add("Row: " + (lineIndex) + ", Error: Something went wrong. Content: " + line[0] + "," +
                                line[1] + "," + line[2] + "," + line[3] + " ...");
                        errorCount++;
                    } else {
                        invalidRows.add("Row: " + (lineIndex ) + ", Error: Content: " + " Empty, or too short for display.");
                        errorCount++;
                    }
                }

            }

            if (errorCount > (lineIndex * errorAcceptance)) {
                String notificationMessage = "Detected more than " + errorAcceptance * 100 + "% of the log with errors. Please make sure input file is a valid CSV file. \n" +
                        "\n Invalid rows: \n";

                for (int i = 0; i < Math.min(5, invalidRows.size()); i++) {
                    notificationMessage = notificationMessage + invalidRows.get(i) + "\n";
                }
                LOGGER.error(errorMessage);

//                throw new InvalidCSVException(notificationMessage, invalidRows);

            }

            return new LogModelImpl(sortTraces(logData), lineIndex, errorCount, invalidRows, errorCheck);

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


    private StringBuilder validateMainAttributes(Map<String, Integer> mainAttributes) {
        String[] attributesToCheck = {caseIdLabel, activityLabel, endTimestampLabel};
        StringBuilder importMessage = new StringBuilder();
        String messingField;
        String mess;
        for (String attribute: attributesToCheck) {
            if (mainAttributes.get(attribute) == -1) {
                switch(attribute) {
                    case caseIdLabel:
                        messingField = "Case ID!";
                        break;
                    case activityLabel:
                        messingField = "Activity!";
                        break;
                    default:
                        messingField = "End Timestamp!";
                }
                mess = "No column has been selected as " + messingField;
                if (importMessage.length() == 0) {
                    importMessage.append(mess);
                } else {
                    importMessage.append(System.lineSeparator()).append(mess);
                }
            }
        }
        return importMessage;
    }


    private Timestamp parseTimestampValue(String theValue, String format){
        Timestamp stamp;
        if(format!= null){
            stamp = parse.tryParsingWithFormat(theValue, format);
            if(stamp == null){
                stamp = parse.tryParsing(theValue);
            }
        }
        else{
            stamp = parse.tryParsing(theValue);
        }
        return stamp;
    }


    private void invalidRow(int lineIndex, String invalidAttribute){
        invalidRows.add("Row: " + (lineIndex) + ", Error: Invalid" + invalidAttribute +" value! \n");
        errorCount++;
    }
    public boolean isValidLineCount(int lineCount) {
        return true;
    }
    private static List<LogEventModel> sortTraces(List<LogEventModel> traces) {
        Comparator<String> nameOrder = new NameComparator();
        traces.sort((o1, o2) -> nameOrder.compare(o1.getCaseID(), o2.getCaseID()));
        return traces;
    }
}
