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
package org.apromore.service.csvimporter.services.legecy;

import com.opencsv.CSVReader;
import org.apache.commons.io.input.ReaderInputStream;
import org.apromore.service.csvimporter.constants.Constants;
import org.apromore.service.csvimporter.dateparser.Parse;
import org.apromore.service.csvimporter.io.CSVFileReader;
import org.apromore.service.csvimporter.model.*;
import org.apromore.service.csvimporter.utilities.NameComparator;
import org.apromore.service.csvimporter.utilities.XEventComparator;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.*;

import static org.apromore.service.csvimporter.utilities.CSVUtilities.getMaxOccurringChar;

public class LogReaderImpl implements LogReader, Constants {


    private static final Logger LOGGER = LoggerFactory.getLogger(LogReaderImpl.class);
    private final Parse parse = new Parse();

    boolean preferMonthFirstChanged;
    private List<LogErrorReport> logErrorReport;
    private boolean validRow;


    @Override
    public LogModel readLogs(InputStream in, LogSample sample, String charset, boolean skipInvalidRow) throws Exception {

        Reader readerin = new InputStreamReader(in, charset);
        BufferedReader brReader = new BufferedReader(readerin);
        String firstLine = brReader.readLine();
        char separator = getMaxOccurringChar(firstLine);
        String[] header = firstLine.split("\\s*" + separator + "\\s*");

        InputStream in2 = new ReaderInputStream(brReader);
        CSVReader reader = new CSVFileReader().newCSVReader(in2, charset, separator);

        if (reader == null)
            return null;

        sample.validateSample();

        logErrorReport = new ArrayList<>();
        int lineIndex = 1; // set to 1 since first line is the header
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

        //XES
        XFactory xFactory = new XFactoryNaiveImpl();
        XConceptExtension concept = XConceptExtension.instance();
        XLifecycleExtension lifecycle = XLifecycleExtension.instance();
        XTimeExtension timestamp = XTimeExtension.instance();
        XOrganizationalExtension resourceXes = XOrganizationalExtension.instance();

        XLog xLog = xFactory.createLog();
        xLog.getExtensions().add(concept);
        xLog.getExtensions().add(lifecycle);
        xLog.getExtensions().add(timestamp);
        xLog.getExtensions().add(resourceXes);

        lifecycle.assignModel(xLog, XLifecycleExtension.VALUE_MODEL_STANDARD);


        while ((line = reader.readNext()) != null && isValidLineCount(lineIndex - 1)) {

            // new row, new event.
            validRow = true;
            lineIndex++;

            //empty row
            if (line.length == 0 || (line.length == 1 && (line[0].trim().equals("") || line[0].trim().equals("\n")))) {
                continue;
            }

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
                readLogs(in, sample, charset, skipInvalidRow);

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
                    return new LogModelXLogImpl(null, logErrorReport, rowLimitExceeded);
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

            //Construct a Trace if it's not exists
            if (getXTrace(caseId, xLog) == null) {
                XTrace xT = xFactory.createTrace();
                concept.assignName(xT, caseId);
                xLog.add(xT);
                assignEventsToTrace(
                        new LogEventModel(caseId, activity, endTimestamp, startTimestamp, otherTimestamps, resource, eventAttributes, caseAttributes),
                        xT);
                assignMyCaseAttributes(caseAttributes, xT);

            } else {
                XTrace xT = getXTrace(caseId, xLog);
                assignEventsToTrace(
                        new LogEventModel(caseId, activity, endTimestamp, startTimestamp, otherTimestamps, resource, eventAttributes, caseAttributes),
                        xT);
                assignMyCaseAttributes(caseAttributes, xT);
            }
        }

        if (!isValidLineCount(lineIndex - 1))
            rowLimitExceeded = true;


        return new LogModelXLogImpl(sortTraces(xLog), logErrorReport, rowLimitExceeded);
//        return new LogModelXLogImpl(xLog, logErrorReport, rowLimitExceeded);
    }

    private XTrace getXTrace(String caseId, XLog xLog) {
        XConceptExtension concept = XConceptExtension.instance();
        for (XTrace xT : xLog) {
            if (concept.extractName(xT).equalsIgnoreCase(caseId)) {
                return xT;
            }

        }
        return null;
    }

    public boolean isValidLineCount(int lineCount) {
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

    private static XLog sortTraces(XLog xLog) {

        XConceptExtension concept = XConceptExtension.instance();

        Comparator<String> nameOrder = new NameComparator();
        Comparator<XEvent> compareTimestamp = new XEventComparator();

        for (XTrace xTrace : xLog)
            xTrace.sort(compareTimestamp);

        xLog.sort((t1, t2) -> nameOrder.compare(concept.extractName(t1), concept.extractName(t2)));

        return xLog;
    }

    private void assignEventsToTrace(LogEventModel logEventModel, XTrace xTrace) {
        XEvent xEvent;

        if (logEventModel.getStartTimestamp() != null) {
            xEvent = createEvent(logEventModel, false);
            xTrace.add(xEvent);
        }
        xEvent = createEvent(logEventModel, true);
        xTrace.add(xEvent);
    }

    private void assignMyCaseAttributes(Map<String, String> caseAttributes, XTrace xTrace) {


        XAttributeMap xAttributeMap = xTrace.getAttributes();

        if (caseAttributes != null && !caseAttributes.isEmpty()) {

            XAttribute attribute;
            for (Map.Entry<String, String> entry : caseAttributes.entrySet()) {
                if (entry.getValue() != null && entry.getValue().trim().length() != 0 && !xAttributeMap.containsKey(entry.getKey())) {
                    attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                    xTrace.getAttributes().put(entry.getKey(), attribute);
                }
            }

        }
    }

    private XEvent createEvent(LogEventModel myEvent, Boolean isEndTimestamp) {

        XFactory xFactory = new XFactoryNaiveImpl();
        XEvent xEvent = xFactory.createEvent();

        XConceptExtension concept = XConceptExtension.instance();
        concept.assignName(xEvent, myEvent.getActivity());

        if (myEvent.getResource() != null) {
            XOrganizationalExtension resource = XOrganizationalExtension.instance();
            resource.assignResource(xEvent, myEvent.getResource());
        }

        XLifecycleExtension lifecycle = XLifecycleExtension.instance();
        XTimeExtension timestamp = XTimeExtension.instance();
        if (isEndTimestamp) {
            lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.COMPLETE);
            timestamp.assignTimestamp(xEvent, myEvent.getEndTimestamp());
        } else {
            lifecycle.assignStandardTransition(xEvent, XLifecycleExtension.StandardModel.START);
            timestamp.assignTimestamp(xEvent, myEvent.getStartTimestamp());
        }


        XAttribute attribute;
        if (myEvent.getOtherTimestamps() != null) {
            Map<String, Timestamp> otherTimestamps = myEvent.getOtherTimestamps();
            for (Map.Entry<String, Timestamp> entry : otherTimestamps.entrySet()) {
                attribute = new XAttributeTimestampImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }

        Map<String, String> eventAttributes = myEvent.getEventAttributes();
        for (Map.Entry<String, String> entry : eventAttributes.entrySet()) {
            if (entry.getValue() != null && entry.getValue().trim().length() != 0) {
                attribute = new XAttributeLiteralImpl(entry.getKey(), entry.getValue());
                xEvent.getAttributes().put(entry.getKey(), attribute);
            }
        }
        return xEvent;
    }
}
