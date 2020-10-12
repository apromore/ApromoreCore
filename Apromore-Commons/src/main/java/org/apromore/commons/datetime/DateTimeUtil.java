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
package org.apromore.commons.datetime;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apromore.commons.datetime.Constants;

/**
 * Various date and time utility functions
 * <p>
 * TO DO:
 * Need to refactor further these duplicates
 * <p>
 * ApromoreCore/Apromore-Extras/APMLogModule/src/main/java/org/apromore/apmlog/util/TimeUtil.java
 * ApromoreEE/Dashboard/src/main/java/dashboard/util/Util.java
 * ApromoreEE/FilterEE/src/main/java/org/apromore/plugin/portal/logfilteree/util/Util.java
 */
public final class DateTimeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeUtil.class.getName());

    public static LocalDateTime parse(String timestamp, DateTimeFormatter formatter) {
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.parse(timestamp, formatter);
        } catch (Exception e) {
            LOGGER.error("Fail to parse a timestamp", e);
            dateTime = null;
        }
        return dateTime;
    }

    /**
     * Parse a date string in various predefined formats to a LocalDateTime
     * <p>
     * TO DO:
     * - To be optimized when all formats are standardized
     *
     * @param dateTimeStr A date string
     * @return LocalDateTime
     */
    public static LocalDateTime parse(String timestamp) {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); // 05-05-2020 22:37:05
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"); // 05/05/2020 00:00:00
        DateTimeFormatter outputFormatter = Constants.DATE_TIME_FORMATTER_HUMANIZED;
        LocalDateTime dateTime;

        // For now, must perform various test for backward compatibility
        dateTime = parse(timestamp, outputFormatter); // check for correct format
        if (dateTime == null) {
            dateTime = parse(timestamp, formatter1);
            if (dateTime == null) {
                if (timestamp.length() < 19) {
                    timestamp += " 00:00:00";
                }
                dateTime = parse(timestamp, formatter2);
            }
            return dateTime;
        }
        return dateTime;
    }

    /**
     * Normalize a date string in various predefined formats to a simple format
     *
     * @param dateTimeStr A date string
     * @return Normalized date for display (dd MMM yy, HH:mm)
     */
    public static String normalize(String dateTimeStr) {
        LocalDateTime dateTime = parse(dateTimeStr);

        if (dateTime != null) {
            return dateTime.format(Constants.DATE_TIME_FORMATTER_HUMANIZED);
        }
        return dateTimeStr;
    }

    public static ZonedDateTime toZonedDateTime(long milliseconds) {
        Instant instant = Instant.ofEpochMilli(milliseconds);
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static String format(ZonedDateTime zonedDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return zonedDateTime.format(formatter);
    }

    public static String format(long milliseconds, String format) {
        return format(toZonedDateTime(milliseconds), format);
    }

    public static String formatDefault(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, Constants.DATE_TIME_FORMAT_DEFAULT);
    }

    public static String formatDefault(long milliseconds) {
        return formatDefault(toZonedDateTime(milliseconds));
    }

    public static String humanize(long milliseconds) {
        return format(milliseconds, Constants.DATE_TIME_FORMAT_HUMANIZED);
    }

}
