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

package org.apromore.service.logimporter.services;

import static org.apromore.service.logimporter.dateparser.DateUtil.parseToTimestamp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apromore.service.logimporter.exception.InvalidLogMetadataException;
import org.apromore.service.logimporter.model.LogErrorReport;
import org.apromore.service.logimporter.model.LogErrorReportImpl;
import org.apromore.service.logimporter.model.LogEventModel;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.utilities.FileUtils;

/**
 * Logic to process a single line of a log.
 *
 * @author 2021-08-18 18:15:00 frankma
 */
public class LogProcessorImpl implements LogProcessor {

    private List<Integer> maskPos;

    @Override
    public LogEventModel processLog(List<String> line, List<String> header, LogMetaData logMetaData, int lineIndex,
                                    List<LogErrorReport> logErrorReport) throws InvalidLogMetadataException {

        if (logErrorReport == null) {
            logErrorReport = new ArrayList<>();
        }
        final int initialErrorLogSize = logErrorReport.size();

        // Construct an event
        final HashMap<String, String> caseAttributes = new HashMap<>(logMetaData.getCaseAttributesPos().size());
        final HashMap<String, String> eventAttributes = new HashMap<>(logMetaData.getEventAttributesPos().size());
        final HashMap<String, Timestamp> otherTimestamps = new HashMap<>(logMetaData.getOtherTimestamps().size());
        maskPos = logMetaData.getMaskPos();

        // CaseId
        if (logMetaData.getCaseIdPos() == LogMetaData.HEADER_ABSENT) {
            throw new InvalidLogMetadataException("No Case Id in metadata");
        }

        final String caseId = getStringAttribute(line, header, logMetaData.getCaseIdPos(), lineIndex, logErrorReport,
            "Case id is empty or has a null value!");

        // Activity
        final String activity = getStringAttribute(line, header, logMetaData.getActivityPos(), lineIndex,
            logErrorReport, "Activity is empty or has a null value!");

        // End Timestamp
        final Timestamp endTimestamp = getTimestampAttribute(line, header, logMetaData.getEndTimestampPos(), lineIndex,
            logMetaData.getEndTimestampFormat(), logMetaData.getTimeZone(), logErrorReport,
            "Invalid end timestamp due to wrong format or daylight saving!");

        // Start Timestamp
        Timestamp startTimestamp = getTimestampAttribute(line, header, logMetaData.getStartTimestampPos(), lineIndex,
            logMetaData.getStartTimestampFormat(), logMetaData.getTimeZone(), null, null);

        if (endTimestamp != null && startTimestamp == null) {
            startTimestamp = endTimestamp;
        }

        // Other timestamps
        getTimestampMap(line, header, logMetaData.getOtherTimestamps(), lineIndex, logMetaData.getTimeZone(),
            logErrorReport, otherTimestamps);

        // Resource
        final String resource = getStringAttribute(line, header, logMetaData.getResourcePos(), lineIndex,
            logErrorReport, "Resource is empty or has a null value!");

        // Role
        final String role = getStringAttribute(line, header, logMetaData.getRolePos(), lineIndex, logErrorReport,
            "Role is empty or has a null value!");

        // Case Attributes
        getAttributesMap(line, header, logMetaData.getCaseAttributesPos(), caseAttributes);

        // Event Attributes
        getAttributesMap(line, header, logMetaData.getEventAttributesPos(), eventAttributes);

        // Perspective
        validatePerspectives(line, header, logMetaData.getPerspectivePos(), lineIndex, logErrorReport);


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
            .valid(initialErrorLogSize == logErrorReport.size())
            .build();
    }

    private void validatePerspectives(
        final List<String> line, final List<String> header, List<Integer> perspectivePositions, int lineIndex,
        final List<LogErrorReport> logErrorReport) {
        if (perspectivePositions != null && !perspectivePositions.isEmpty()) {
            for (int columnPos : perspectivePositions) {
                String perspective = line.get(columnPos);
                if (perspective == null || perspective.isEmpty()) {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, columnPos,
                        header.get(columnPos), "Perspective is empty or has a null value!"));
                }
            }
        }
    }

    private void getTimestampMap(
        final List<String> line, final List<String> header, HashMap<Integer, String> otherTimestamps,
        int lineIndex, final String timeZone, final List<LogErrorReport> logErrorReport,
        final HashMap<String, Timestamp> timestampAttributeMap) {
        if (!otherTimestamps.isEmpty()) {
            for (Map.Entry<Integer, String> otherTimestamp : otherTimestamps.entrySet()) {
                Timestamp tempTimestamp = parseTimestampValue(line.get(otherTimestamp.getKey()),
                    otherTimestamp.getValue(), timeZone);
                if (tempTimestamp != null) {
                    timestampAttributeMap.put(header.get(otherTimestamp.getKey()), tempTimestamp);
                } else {
                    logErrorReport.add(new LogErrorReportImpl(lineIndex, otherTimestamp.getKey(),
                        header.get(otherTimestamp.getKey()),
                        "Invalid other timestamp due to wrong format or daylight saving!"));
                    break;
                }
            }
        }
    }

    private void getAttributesMap(
        final List<String> line, final List<String> header, List<Integer> attributePositions,
        final HashMap<String, String> attributeHeaderToValue) {

        if (attributePositions != null && !attributePositions.isEmpty()) {
            for (int columnPos : attributePositions) {
                if (line.size() > columnPos) {
                    attributeHeaderToValue.put(header.get(columnPos), applyMask(columnPos)
                        ? FileUtils.sha256Hashing(line.get(columnPos)) : line.get(columnPos));
                }
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
