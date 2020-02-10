/*
 * Copyright © 2009-2019 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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
        List<String> header = new ArrayList<>();
        Collections.addAll(header, reader.readNext());
        if (header.isEmpty()) {
            throw new InvalidCSVException("Could not parse file!");
        }

        // Obtain the sample of lines
        List<List<String>> lines = new ArrayList<>();
        for (String[] s = reader.readNext(); s != null && lines.size() < sampleSize; s = reader.readNext()) {
            if(s!=null && s.length > 2){
                lines.add(Arrays.asList(s));
            }
        }

        // Construct the sample (no mutation expected after this point, although this isn't enforced by the code))
        return new LogSampleImpl(header, lines);
    }

    @Override
    public LogModel prepareXesModel(CSVReader reader, LogSample sample, double errorAcceptance) throws InvalidCSVException, IOException {
        int errorCount = 0;
        int lineCount = 0;
        int finishCount = 0;
        boolean errorCheck = false;

        ArrayList<String> invalidRows = new ArrayList<>();
        try {

            // read first line from CSV as header
            String[] header = reader.readNext();

            // If any of the mandatory fields are missing show alert message to the user and return
            StringBuilder headNOTDefined = checkFields(sample.getHeads());
            if (headNOTDefined.length() != 0) {
                throw new InvalidCSVException(headNOTDefined.toString());
            }


            // create model "LogEventModel" of the log data
            // We set mandatory fields and other fields are set with hash map
            List<LogEventModel> logData = new ArrayList<>();
            HashMap<String, Timestamp> otherTimestamps;
            HashMap<String, String> others;
            HashMap<String, String> caseAttributes;
            Timestamp startTimestamp = null;
            String resourceCol = null;
            String errorMessage = null;

            for (Iterator<String[]> it = reader.iterator(); finishCount < 50 && isValidLineCount(lineCount);) {
                String[] line = it.next();
                boolean rowGTG = true;
                if(line == null) {
                    // if line is empty, move to next iteration, until 50 lines are empty
                    finishCount++;
                    continue;
                }

                if (line != null && line.length > 2) {
                    lineCount++;
                    try {
                        otherTimestamps = new HashMap<>();
                        others = new HashMap<>();
                        caseAttributes = new HashMap<>();

                        for (int p = 0; p <= line.length - 1; p++) {
                            if (sample.getOtherTimeStampsPos().get(p) != null) {
                                otherTimestamps.put(header[p], Parse.parseTimestamp(line[p], sample.getOtherTimeStampsPos().get(p)));
                            } else if(!sample.getCaseAttributesPos().isEmpty() && sample.getCaseAttributesPos().contains(p)){
                                caseAttributes.put(header[p], line[p]);
                            }
                            else if (p != sample.getHeads().get(caseid) && p != sample.getHeads().get(activity) &&
                                    p != sample.getHeads().get(timestamp) && p != sample.getHeads().get(tsStart) &&
                                    p != sample.getHeads().get(resource) && (sample.getIgnoredPos().isEmpty() || !sample.getIgnoredPos().contains(p)) &&
                                    (sample.getCaseAttributesPos().isEmpty() || !sample.getCaseAttributesPos().contains(p))) {

                                others.put(header[p], line[p]);
                                if (header.length != line.length) {
                                    invalidRows.add("Row: " + (lineCount) + ", Error: number of columns does not match" +
                                            " number of headers. " + "Number of headers: " + header.length + "," +
                                            " Number of columns: " + line.length + ".\n");
                                    errorCount++;
                                    rowGTG = false;
                                    break;
                                }

                            }
                        }
                        Timestamp tStamp = Parse.parseTimestamp(line[sample.getHeads().get(timestamp)], sample.getTimestampFormat());

                        if (sample.getHeads().get(tsStart) != -1) {
                            startTimestamp = Parse.parseTimestamp(line[sample.getHeads().get(tsStart)], sample.getStartTsFormat());
                            if (startTimestamp == null) {
                                if (tStamp != null) {
                                    startTimestamp = tStamp;
                                    invalidRows.add("Row: " + (lineCount) + ", Warning: Start time stamp field is invalid. Copying end timestamp field into start timestamp");
                                } else {
                                    invalidRows.add("Row: " + (lineCount) + ", Error: Start time stamp field is invalid.");
                                    errorCount++;
                                }
                            }
                        }

                        // Notify if resource field is empty
                        if (sample.getHeads().get(resource) != -1) {
                            resourceCol = line[sample.getHeads().get(resource)];
                        }
                        // check if end stimestamp field is null
                        if (tStamp == null) {
                            if (startTimestamp != null) {
                                tStamp = startTimestamp;
                                invalidRows.add("Row: " + (lineCount) + ", Warning: End time stamp field is invalid. Copying start timestamp field into end timestamp");
                            } else {
                                invalidRows.add("Row: " + (lineCount) + ", Error: End time stamp field is empty.");
                                errorCount++;
                                continue;
                            }
                        }
                        if (otherTimestamps != null) {
                            for (Map.Entry<String, Timestamp> entry : otherTimestamps.entrySet()) {
                                if (entry.getKey() != null) {
                                    if (entry.getValue() == null) {
                                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date = dateFormat.parse("01/01/1900");
                                        long time = date.getTime();
                                        Timestamp tempTime = new Timestamp(time);
                                        entry.setValue(tempTime);
                                        invalidRows.add("Row: " + (lineCount) + ", Error: " + entry.getKey() +
                                                " field is invalid timestamp.");
                                        errorCheck = true;
                                    }
                                }
                            }
                        }
                        if (rowGTG) {
                            logData.add(new LogEventModelImpl(line[sample.getHeads().get(caseid)], line[sample.getHeads().get(activity)], tStamp, startTimestamp, otherTimestamps, resourceCol, others, caseAttributes));
                        }
                    } catch (Exception e) {
                        errorMessage = ExceptionUtils.getStackTrace(e);
                        e.printStackTrace();
                        errorCount++;
                        if (line.length > 4) {
                            invalidRows.add("Row: " + (lineCount) + ", Error: Something went wrong. Content: " + line[0] + "," +
                                    line[1] + "," + line[2] + "," + line[3] + " ...");
                            errorCount++;
                        } else {
                            invalidRows.add("Row: " + (lineCount ) + ", Error: Content: " + " Empty, or too short for display.");
                            errorCount++;
                        }
                    }
                }
            }

            if (errorCount > (lineCount * errorAcceptance)) {
                String notificationMessage = "Detected more than " + errorAcceptance * 100 + "% of the log with errors. Please make sure input file is a valid CSV file. \n" +
                        "\n Invalid rows: \n";

                for (int i = 0; i < Math.min(5, invalidRows.size()); i++) {
                    notificationMessage = notificationMessage + invalidRows.get(i) + "\n";
                }
                LOGGER.error(errorMessage);

//                throw new InvalidCSVException(notificationMessage, invalidRows);

            }

            return new LogModelImpl(sortTraces(logData), lineCount, errorCount, invalidRows, errorCheck);

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isValidLineCount(int lineCount) {
        return true;
    }

    /**
     * Gets the pos.
     *
     * @param col  the col: array which has possible names for each of the mandatory fields.
     * @param elem the elem: one item of the CSV line array
     * @return the pos: boolean value confirming if the elem is the required element.
     */
    private static boolean getPos(String[] col, String elem) {
        if (col == timestampValues || col == StartTsValues) {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::equals);
        } else {
            return Arrays.stream(col).anyMatch(elem.toLowerCase()::contains);
        }
    }

    /**
     * Check fields.
     * <p>
     * Check if all mandatory fields are found in the file, otherwise, construct a message based on the missed fields.
     *
     * @param posMap the pos map
     * @return the string builder
     */
    private StringBuilder checkFields(Map<String, Integer> posMap) {
        String[] fieldsToCheck = {caseid, activity, timestamp};
        StringBuilder importMessage = new StringBuilder();

        for (int f = 0; f <= fieldsToCheck.length - 1; f++) {
            if (posMap.get(fieldsToCheck[f]) == -1) {
                String mess = "No " + fieldsToCheck[f] + " defined!";
                importMessage = (importMessage.length() == 0 ? importMessage.append(mess) : importMessage.append(", ").append(mess));
            }
        }

        return importMessage;
    }


    private static List<LogEventModel> sortTraces(List<LogEventModel> traces) {
        Comparator<String> nameOrder = new NameComparator();
        traces.sort((o1, o2) -> nameOrder.compare(o1.getCaseID(), o2.getCaseID()));
        return traces;
    }
}
