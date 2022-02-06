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

import static org.apromore.service.logimporter.dateparser.DateUtil.parseToTimestamp;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apromore.service.logimporter.model.LogErrorReport;
import org.apromore.service.logimporter.model.LogErrorReportImpl;
import org.apromore.service.logimporter.model.LogEventModel;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.utilities.FileUtils;

/**
 * @author 2021-08-18 18:15:00 frankma
 */
public class LogProcessorImpl implements LogProcessor {

    private List<Integer> maskPos;

    @Override
    public LogEventModel processLog(List<String> line, List<String> header, LogMetaData logMetaData, int lineIndex,
                                    List<LogErrorReport> logErrorReport) {
        // Construct an event

        HashMap<String, String> caseAttributes = new HashMap<>(logMetaData.getCaseAttributesPos().size());
        HashMap<String, String> eventAttributes = new HashMap<>(logMetaData.getEventAttributesPos().size());
        HashMap<String, Timestamp> otherTimestamps = new HashMap<>(logMetaData.getOtherTimestamps().size());
        boolean validRow = true;
        maskPos = logMetaData.getMaskPos();

        // CaseId
        assert logMetaData.getCaseIdPos() != LogMetaData.HEADER_ABSENT;
        String caseId = getStringAttribute(line, header, logMetaData.getCaseIdPos(), lineIndex, logErrorReport,
            "Case id is empty or has a null value!");

        // Activity
        String activity = getStringAttribute(line, header, logMetaData.getActivityPos(), lineIndex, logErrorReport,
            "Activity is empty or has a null value!");

        // End Timestamp
        Timestamp endTimestamp = getTimestampAttribute(line, header, logMetaData.getEndTimestampPos(), lineIndex,
            logMetaData.getEndTimestampFormat(), logMetaData.getTimeZone(), logErrorReport,
            "Invalid end timestamp due to wrong format or daylight saving!");

        // Start Timestamp
        Timestamp startTimestamp = getTimestampAttribute(line, header, logMetaData.getStartTimestampPos(), lineIndex,
            logMetaData.getStartTimestampFormat(), logMetaData.getTimeZone(), null, null);

        if (endTimestamp != null && startTimestamp == null) {
            startTimestamp = endTimestamp;
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
                        header.get(otherTimestamp.getKey()),
                        "Invalid other timestamp due to wrong format or daylight saving!"));
                    validRow = false;
                    break;
                }
            }
        }

        // Resource
        String resource = getStringAttribute(line, header, logMetaData.getResourcePos(), lineIndex, logErrorReport,
            "Resource is empty or has a null value!");

        // Role
        String role = getStringAttribute(line, header, logMetaData.getRolePos(), lineIndex, logErrorReport,
            "Role is empty or has a null value!");

        if (caseId == null || caseId.isEmpty()
            || activity == null || activity.isEmpty()
            || endTimestamp == null
            || (logMetaData.getResourcePos() != LogMetaData.HEADER_ABSENT && (resource == null || resource.isEmpty()))
            || (logMetaData.getRolePos() != LogMetaData.HEADER_ABSENT && (role == null || role.isEmpty()))) {
            validRow = false;
        }

        // Case Attributes
        getAttributesMap(line, header, logMetaData.getCaseAttributesPos(), caseAttributes, validRow);

        // Event Attributes
        getAttributesMap(line, header, logMetaData.getEventAttributesPos(), eventAttributes, validRow);

        // Perspective
        if (validRow && logMetaData.getPerspectivePos() != null && !logMetaData.getPerspectivePos().isEmpty()) {
            for (int columnPos : logMetaData.getPerspectivePos()) {
                String perspective = line.get(columnPos);
                if (perspective == null || perspective.isEmpty()) {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, columnPos,
                        header.get(columnPos), "Perspective is empty or has a null value!"));
                    validRow = false;
                }
            }
        }

        return LogEventModel.builder()
            .caseID(caseId)
            .activity(activity)
            .endTimestamp(endTimestamp)
            .startTimestamp(startTimestamp)
            .otherTimestamps(otherTimestamps)
            .resource(resource)
            .role(role)
            .eventAttributes(eventAttributes)
            .caseAttributes(caseAttributes)
            .valid(validRow)
            .build();
    }

    private void getAttributesMap(
        final List<String> line, final List<String> header, List<Integer> attributePositions,
        final HashMap<String, String> attributeHeaderToValue, boolean validRow) {

        if (validRow && attributePositions != null && !attributePositions.isEmpty()) {
            for (int columnPos : attributePositions) {
                attributeHeaderToValue.put(header.get(columnPos), applyMask(columnPos) ?
                    FileUtils.sha256Hashing(line.get(columnPos)) :
                    line.get(columnPos));
            }
        }
    }

    private String getStringAttribute(
        final List<String> line, final List<String> header, int attributePosition, int lineIndex,
        final List<LogErrorReport> logErrorReport, final String errorMsg) {

        String attributeValue = null;
        if (attributePosition != LogMetaData.HEADER_ABSENT && line.size() > attributePosition) {

            attributeValue = line.get(attributePosition);
            if (logErrorReport != null && (attributeValue == null || attributeValue.isEmpty())) {
                logErrorReport.add(new LogErrorReportImpl(lineIndex, attributePosition,
                    header.get(attributePosition), errorMsg));
            } else if (applyMask(attributePosition)) {

                attributeValue = FileUtils.sha256Hashing(attributeValue);
            }
        }

        return attributeValue;
    }

    private Timestamp getTimestampAttribute(
        final List<String> line, final List<String> header, int attributePosition, int lineIndex,
        final String timestampFormat, final String timeZone,
        final List<LogErrorReport> logErrorReport, final String errorMsg) {

        Timestamp attributeValue = null;
        if (attributePosition != LogMetaData.HEADER_ABSENT && line.size() > attributePosition) {

            attributeValue = parseTimestampValue(line.get(attributePosition),
                timestampFormat, timeZone);
            if (logErrorReport != null && attributeValue == null) {
                logErrorReport.add(new LogErrorReportImpl(lineIndex, attributePosition,
                    header.get(attributePosition), errorMsg));
            }
        }

        return attributeValue;
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
