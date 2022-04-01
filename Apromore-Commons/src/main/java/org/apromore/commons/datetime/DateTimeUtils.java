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
package org.apromore.commons.datetime;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apromore.commons.datetime.Constants;

/**
 * Various date and time utility functions
 * <p>
 * TO DO: Need to refactor further these duplicates
 * <p>
 * ApromoreCore/Apromore-Extras/APMLogModule/src/main/java/org/apromore/apmlog/util/TimeUtil.java
 * ApromoreEE/Dashboard/src/main/java/dashboard/util/Util.java
 * ApromoreEE/FilterEE/src/main/java/org/apromore/plugin/portal/logfilteree/util/Util.java
 */
public final class DateTimeUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeUtils.class.getName());

    private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
            .appendOptional(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"))
            .appendOptional(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            .appendOptional(DateTimeFormatter.ofPattern("dd/MM/yyyy[ HH:mm:ss]"))
            .appendOptional(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .appendOptional(DateTimeFormatter.ISO_ZONED_DATE_TIME)
            .appendOptional(DateTimeFormatter.ofPattern("[[MMMM][MMM] dd, yyyy][yyyy-MM-dd][MM/dd/yyyy]"))
            .appendOptional(Constants.DATE_TIME_FORMATTER_HUMANIZED)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .toFormatter(Locale.ENGLISH);

    /**
     * Parse a date string in various predefined formats to a LocalDateTime
     * <p>
     * TO DO: - To be optimized when all formats are standardized
     *
     * @param timestamp A date string
     * @return LocalDateTime
     */
    public static LocalDateTime parse(String timestamp) {
        return dateTimeFormatter.parse(timestamp, LocalDateTime::from);
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

    /**
     * Normalize a date string in various predefined formats to a simple format with i18n support
     *
     * @param dateTimeStr A date string
     * @param formatter   A DateTimeFormatter
     * @return Normalized date for display (dd MMM yy, HH:mm)
     */
    public static String normalize(String dateTimeStr, DateTimeFormatter formatter) {
        LocalDateTime dateTime = parse(dateTimeStr);

        if (dateTime != null) {
            return dateTime.format(formatter); // e.g. DateTimeFormatter.ofPattern("yyyy年MM月dd日, HH:mm", Locale.JAPAN)
        }
        return dateTimeStr;
    }

    public static ZonedDateTime toZonedDateTime(long milliseconds) {
        Instant instant = Instant.ofEpochMilli(milliseconds);
        return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static String format(ZonedDateTime zonedDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, Locale.ENGLISH);
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

    public static String humanize(LocalDateTime localDateTime) {
        return localDateTime.format(Constants.DATE_TIME_FORMATTER_HUMANIZED);
    }

    public static String humanize(OffsetDateTime offsetDateTime) {
        return offsetDateTime.format(Constants.DATE_TIME_FORMATTER_HUMANIZED);
    }
}
