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
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.apromore.service.logimporter.model.LogErrorReport;
import org.apromore.service.logimporter.model.LogErrorReportImpl;
import org.apromore.service.logimporter.model.LogMetaData;
import org.apromore.service.logimporter.model.ParquetEventLogModel;

public class LogProcessorParquetImpl implements LogProcessorParquet {

    private boolean validRow;

    @Override
    public ParquetEventLogModel processLog(String[] line, String[] header, LogMetaData sample, int lineIndex,
                                           List<LogErrorReport> logErrorReport) {

        //Construct an event
        validRow = true;

        // Case id:
        if (line[sample.getCaseIdPos()] == null || line[sample.getCaseIdPos()].isEmpty()) {
            logErrorReport.add(new LogErrorReportImpl(lineIndex, sample.getCaseIdPos(), header[sample.getCaseIdPos()],
                "Case id is empty or has a null value!"));
            validRow = false;
        }

        // Activity
        if (line[sample.getActivityPos()] == null || line[sample.getActivityPos()].isEmpty()) {
            logErrorReport.add(
                new LogErrorReportImpl(lineIndex, sample.getActivityPos(), header[sample.getActivityPos()],
                    "Activity is empty or has a null value!"));
            validRow = false;
        }

        // End Timestamp
        Timestamp endTimestamp = parseTimestampValue(line[sample.getEndTimestampPos()], sample.getEndTimestampFormat(),
            sample.getTimeZone());
        if (endTimestamp == null) {
            logErrorReport.add(
                new LogErrorReportImpl(lineIndex, sample.getEndTimestampPos(), header[sample.getEndTimestampPos()],
                    "End timestamp Can not parse!"));
            validRow = false;
        } else {
            line[sample.getEndTimestampPos()] = endTimestamp.toString();
        }

        // Start Timestamp
        if (sample.getStartTimestampPos() != -1) {
            Timestamp startTimestamp =
                parseTimestampValue(line[sample.getStartTimestampPos()], sample.getStartTimestampFormat(),
                    sample.getTimeZone());
            if (startTimestamp == null) {
                logErrorReport.add(new LogErrorReportImpl(lineIndex, sample.getStartTimestampPos(),
                    header[sample.getStartTimestampPos()], "Start timestamp Can not parse!"));
                validRow = false;
            } else {
                line[sample.getStartTimestampPos()] = startTimestamp.toString();
            }
        }

        // Other timestamps
        if (!sample.getOtherTimestamps().isEmpty()) {
            for (Map.Entry<Integer, String> otherTimestamp : sample.getOtherTimestamps().entrySet()) {
                Timestamp tempTimestamp =
                    parseTimestampValue(line[otherTimestamp.getKey()], otherTimestamp.getValue(), sample.getTimeZone());
                if (tempTimestamp != null) {
                    line[otherTimestamp.getKey()] = tempTimestamp.toString();
                } else {
                    logErrorReport.add(
                        new LogErrorReportImpl(lineIndex, otherTimestamp.getKey(), header[otherTimestamp.getKey()],
                            "Other timestamp Can not parse!"));
                    validRow = false;
                }
            }
        }

        // Resource
        if (sample.getResourcePos() != -1) {
            if (line[sample.getResourcePos()] == null || line[sample.getResourcePos()].isEmpty()) {
                logErrorReport.add(
                    new LogErrorReportImpl(lineIndex, sample.getResourcePos(), header[sample.getResourcePos()],
                        "Resource is empty or has a null value!"));
                validRow = false;
            }
        }

        return new ParquetEventLogModel(line, validRow);
    }

    private Timestamp parseTimestampValue(String theValue, String format, String timeZone) {
        if (theValue != null && !theValue.isEmpty() && format != null && !format.isEmpty()) {
            return (timeZone == null || timeZone.isEmpty())
                ? parseToTimestamp(theValue, format, null)
                : parseToTimestamp(theValue, format, TimeZone.getTimeZone(timeZone));
        } else {
            return null;
        }
    }
}
