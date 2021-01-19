/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

import static org.apromore.service.csvimporter.dateparser.DateUtil.parseToTimestamp;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apromore.service.csvimporter.model.LogErrorReport;
import org.apromore.service.csvimporter.model.LogErrorReportImpl;
import org.apromore.service.csvimporter.model.LogEventModelExt;
import org.apromore.service.csvimporter.model.LogMetaData;

public class LogProcessorImpl implements LogProcessor {
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
    public LogEventModelExt processLog(List<String> line, List<String> header, LogMetaData logMetaData, int lineIndex,
	    List<LogErrorReport> logErrorReport) {
	// Construct an event
	startTimestamp = null;
	resource = null;
	caseAttributes = new HashMap<>();
	eventAttributes = new HashMap<>();
	otherTimestamps = new HashMap<>();
	validRow = true;

	// Case id:
	caseId = line.get(logMetaData.getCaseIdPos());
	if (caseId == null || caseId.isEmpty()) {
	    logErrorReport.add(new LogErrorReportImpl(lineIndex, logMetaData.getCaseIdPos(),
		    header.get(logMetaData.getCaseIdPos()), "Case id is empty or has a null value!"));
	    validRow = false;
	}

	// Activity
	activity = line.get(logMetaData.getActivityPos());
	if (activity == null || activity.isEmpty()) {
	    logErrorReport.add(new LogErrorReportImpl(lineIndex, logMetaData.getActivityPos(),
		    header.get(logMetaData.getActivityPos()), "Activity is empty or has a null value!"));
	    validRow = false;
	}

	// End Timestamp
	endTimestamp = parseTimestampValue(line.get(logMetaData.getEndTimestampPos()),
		logMetaData.getEndTimestampFormat(), logMetaData.getTimeZone());
	if (endTimestamp == null) {
	    logErrorReport.add(new LogErrorReportImpl(lineIndex, logMetaData.getEndTimestampPos(),
		    header.get(logMetaData.getEndTimestampPos()), "End timestamp Can not parse!"));
	    validRow = false;
	}
	// Start Timestamp
	if (logMetaData.getStartTimestampPos() != -1) {
	    startTimestamp = parseTimestampValue(line.get(logMetaData.getStartTimestampPos()),
		    logMetaData.getStartTimestampFormat(), logMetaData.getTimeZone());
	    if (startTimestamp == null) {
		logErrorReport.add(new LogErrorReportImpl(lineIndex, logMetaData.getStartTimestampPos(),
			header.get(logMetaData.getStartTimestampPos()), "Start timestamp Can not parse!"));
		validRow = false;
	    }
	}

	// Other timestamps
	if (!logMetaData.getOtherTimestamps().isEmpty()) {
	    for (Map.Entry<Integer, String> otherTimestamp : logMetaData.getOtherTimestamps().entrySet()) {
		Timestamp tempTimestamp = parseTimestampValue(line.get(otherTimestamp.getKey()),
			otherTimestamp.getValue(), logMetaData.getTimeZone());
		if (tempTimestamp != null) {
		    otherTimestamps.put(header.get(otherTimestamp.getKey()), tempTimestamp);
		} else {
		    logErrorReport.add(new LogErrorReportImpl(lineIndex, otherTimestamp.getKey(),
			    header.get(otherTimestamp.getKey()), "Other timestamp Can not parse!"));
		    validRow = false;
		    break;
		}
	    }
	}

	// Resource
	if (logMetaData.getResourcePos() != -1) {
	    resource = line.get(logMetaData.getResourcePos());
	    if (resource == null || resource.isEmpty()) {
		logErrorReport.add(new LogErrorReportImpl(lineIndex, logMetaData.getResourcePos(),
			header.get(logMetaData.getResourcePos()), "Resource is empty or has a null value!"));
		validRow = false;
	    }
	}

	// Case Attributes
	if (validRow && logMetaData.getCaseAttributesPos() != null && !logMetaData.getCaseAttributesPos().isEmpty()) {
	    for (int columnPos : logMetaData.getCaseAttributesPos()) {
		caseAttributes.put(header.get(columnPos), line.get(columnPos));
	    }
	}

	// Event Attributes
	if (validRow && logMetaData.getEventAttributesPos() != null && !logMetaData.getEventAttributesPos().isEmpty()) {
	    for (int columnPos : logMetaData.getEventAttributesPos()) {
		eventAttributes.put(header.get(columnPos), line.get(columnPos));
	    }

	}

	return new LogEventModelExt(caseId, activity, endTimestamp, startTimestamp, otherTimestamps, resource,
		eventAttributes, caseAttributes, validRow);
    }

    private Timestamp parseTimestampValue(String theValue, String format, String timeZone) {
	if (theValue != null && !theValue.isEmpty() && format != null && !format.isEmpty()) {
	    return (timeZone == null || timeZone.isEmpty()) ? parseToTimestamp(theValue, format, null)
		    : parseToTimestamp(theValue, format, TimeZone.getTimeZone(timeZone));
	} else {
	    return null;
	}
    }
}
