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

import org.apromore.service.csvimporter.dateparser.Parse;
import org.apromore.service.csvimporter.model.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogProcessorImpl implements LogProcessor {
    private final Parse parse = new Parse();
    private String caseId;
    private String activity;
    private Timestamp endTimestamp;
    private Timestamp startTimestamp;
    private String resource;
    private HashMap<String, String> caseAttributes;
    private HashMap<String, String> eventAttributes;
    private HashMap<String, Timestamp> otherTimestamps;
    private final String errorMessage = "Field is empty or has a null value!";
    private boolean validRow;


    @Override
    public LogEventModelExt processLog(List<String> line, List<String> header, LogSample sample, int lineIndex, List<LogErrorReport> logErrorReport) {

        //Construct an event
        startTimestamp = null;
        resource = null;
        caseAttributes = new HashMap<>();
        eventAttributes = new HashMap<>();
        otherTimestamps = new HashMap<>();
        validRow = true;

        // Case id:
        caseId = line.get(sample.getCaseIdPos());
        if (caseId == null || caseId.isEmpty()) {
            logErrorReport.add(new LogErrorReportImpl(lineIndex, sample.getCaseIdPos(), header.get(sample.getCaseIdPos()), errorMessage));
            validRow = false;
        }

        // Activity
        activity = line.get(sample.getActivityPos());
        if (activity == null || activity.isEmpty()) {
            logErrorReport.add(new LogErrorReportImpl(lineIndex, sample.getActivityPos(), header.get(sample.getActivityPos()), errorMessage));
            validRow = false;
        }

        // End Timestamp
        endTimestamp = parseTimestampValue(line.get(sample.getEndTimestampPos()), sample.getEndTimestampFormat());
        if (endTimestamp == null) {
            logErrorReport.add(new LogErrorReportImpl(lineIndex, sample.getEndTimestampPos(), header.get(sample.getEndTimestampPos()), parse.getParseFailMess()));
            validRow = false;
        }
        // Start Timestamp
        if (sample.getStartTimestampPos() != -1) {
            startTimestamp = parseTimestampValue(line.get(sample.getStartTimestampPos()), sample.getStartTimestampFormat());
            if (startTimestamp == null) {
                logErrorReport.add(new LogErrorReportImpl(lineIndex, sample.getStartTimestampPos(), header.get(sample.getStartTimestampPos()), parse.getParseFailMess()));
                validRow = false;
            }
        }

        // Other timestamps
        if (!sample.getOtherTimestamps().isEmpty()) {
            for (Map.Entry<Integer, String> otherTimestamp : sample.getOtherTimestamps().entrySet()) {
                Timestamp tempTimestamp = parseTimestampValue(line.get(otherTimestamp.getKey()), otherTimestamp.getValue());
                if (tempTimestamp != null) {
                    otherTimestamps.put(header.get(otherTimestamp.getKey()), tempTimestamp);
                } else {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, otherTimestamp.getKey(), header.get(otherTimestamp.getKey()), parse.getParseFailMess()));
                    validRow = false;
                }
            }
        }

        // Resource
        if (sample.getResourcePos() != -1) {
            resource = line.get(sample.getResourcePos());
            if (resource == null || resource.isEmpty()) {
                logErrorReport.add(new LogErrorReportImpl(lineIndex, sample.getResourcePos(), header.get(sample.getResourcePos()), errorMessage));
                validRow = false;
            }
        }

        // Case Attributes
        if (sample.getCaseAttributesPos() != null && !sample.getCaseAttributesPos().isEmpty()) {
            for (int columnPos : sample.getCaseAttributesPos()) {
                caseAttributes.put(header.get(columnPos), line.get(columnPos));
            }
        }

        // Event Attributes
        if (sample.getEventAttributesPos() != null && !sample.getEventAttributesPos().isEmpty()) {
            for (int columnPos : sample.getEventAttributesPos()) {
                eventAttributes.put(header.get(columnPos), line.get(columnPos));
            }
        }

        return new LogEventModelExt(caseId, activity, endTimestamp, startTimestamp, otherTimestamps, resource, eventAttributes, caseAttributes, validRow);
    }

    private Timestamp parseTimestampValue(String theValue, String format) {
        Timestamp stamp;
        if (format != null && !format.isEmpty()) {
            stamp = parse.tryParsingWithFormat(theValue, format);
            if (stamp == null) {
                stamp = parse.tryParsing(theValue);
            }
        } else {
            stamp = parse.tryParsing(theValue);
        }
        return stamp;
    }
}