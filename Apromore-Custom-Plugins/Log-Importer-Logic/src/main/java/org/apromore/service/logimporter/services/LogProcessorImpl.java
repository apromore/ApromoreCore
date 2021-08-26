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
package org.apromore.service.logimporter.services;

import org.apromore.service.logimporter.model.LogErrorReport;
import org.apromore.service.logimporter.model.LogErrorReportImpl;
import org.apromore.service.logimporter.model.LogEventModelExt;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.utilities.FileUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.apromore.service.logimporter.dateparser.DateUtil.parseToTimestamp;

/**
 * @author 2021-08-18 18:15:00 frankma
 */
public class LogProcessorImpl implements LogProcessor {

    private List<Integer> maskPos;

    @Override
    public LogEventModelExt processLog(List<String> line, List<String> header, LogMetaData logMetaData, int lineIndex,
                                       List<LogErrorReport> logErrorReport) {
        // Construct an event
        Timestamp startTimestamp = null;
        String resource = null;
        HashMap<String, String> caseAttributes = new HashMap<>(logMetaData.getCaseAttributesPos().size());
        HashMap<String, String> eventAttributes = new HashMap<>(logMetaData.getEventAttributesPos().size());
        HashMap<String, Timestamp> otherTimestamps = new HashMap<>(logMetaData.getOtherTimestamps().size());
        boolean validRow = true;
        maskPos = logMetaData.getMaskPos();

        // Case id:
        assert logMetaData.getCaseIdPos() != -1;
        String caseId = line.get(logMetaData.getCaseIdPos());
        if (caseId == null || caseId.isEmpty()) {
            logErrorReport.add(new LogErrorReportImpl(lineIndex, logMetaData.getCaseIdPos(),
                    header.get(logMetaData.getCaseIdPos()), "Case id is empty or has a null value!"));
            validRow = false;
        } else if (applyMask(logMetaData.getCaseIdPos())) {
            caseId = FileUtils.sha256Hashing(caseId);
        }

        // Activity
        String activity = line.get(logMetaData.getActivityPos());
        if (activity == null || activity.isEmpty()) {
            logErrorReport.add(new LogErrorReportImpl(lineIndex, logMetaData.getActivityPos(),
                    header.get(logMetaData.getActivityPos()), "Activity is empty or has a null value!"));
            validRow = false;
        } else if (applyMask(logMetaData.getActivityPos())) {
            activity = FileUtils.sha256Hashing(activity);
        }

        // End Timestamp
        Timestamp endTimestamp = parseTimestampValue(line.get(logMetaData.getEndTimestampPos()),
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
            if (endTimestamp != null && startTimestamp == null) {
                startTimestamp = endTimestamp;
                validRow = true;
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
            } else if (applyMask(logMetaData.getResourcePos())) {
                resource = FileUtils.sha256Hashing(resource);
            }
        }

        // Case Attributes
        if (validRow && logMetaData.getCaseAttributesPos() != null && !logMetaData.getCaseAttributesPos().isEmpty()) {
            for (int columnPos : logMetaData.getCaseAttributesPos()) {
                caseAttributes.put(header.get(columnPos), applyMask(columnPos) ?
						FileUtils.sha256Hashing(line.get(columnPos)) :
                        line.get(columnPos));
            }
        }

        // Event Attributes
        if (validRow && logMetaData.getEventAttributesPos() != null && !logMetaData.getEventAttributesPos().isEmpty()) {
            for (int columnPos : logMetaData.getEventAttributesPos()) {
                eventAttributes.put(header.get(columnPos), applyMask(columnPos) ?
						FileUtils.sha256Hashing(line.get(columnPos)) :
                        line.get(columnPos));
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

    private boolean applyMask(int pos) {
        if (maskPos == null) {
            return false;
        } else {
            return maskPos.contains(pos);
        }
    }
}
