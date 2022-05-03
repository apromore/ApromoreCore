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

package org.apromore.service.logimporter.dateparser;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil extends DatePatterns {

    private static final int DATE_STRING_MIN_LENGTH = 4;
    private static final String DATE_FORMAT_MILLISECOND = ".SSS";

    private DateUtil() {
    }

    /**
     * Convert the given date to a Calendar object. The TimeZone will be derived from the local
     * operating system's timezone.
     *
     * @param date The date to be converted to Calendar.
     * @return The Calendar object set to the given date and using the local timezone.
     */
    public static Calendar toCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * Convert the given date to a Calendar object with the given timezone.
     *
     * @param date     The date to be converted to Calendar.
     * @param timeZone The timezone to be set in the Calendar.
     * @return The Calendar object set to the given date and timezone.
     */
    public static Calendar toCalendar(Date date, TimeZone timeZone) {
        Calendar calendar = toCalendar(date);
        calendar.setTimeZone(timeZone);
        return calendar;
    }

    /**
     * Parse the given date string to date object and return a date instance based on the given
     * date string. This makes use of the {@link DateUtil#determineDateFormat(String)} to determine
     * the SimpleDateFormat pattern to be used for parsing.
     *
     * @param dateString The date string to be parsed to date object.
     * @return The parsed date object.
     * @throws ParseException If the date format pattern of the given date string is unknown, or if
     *                        the given date string or its actual date is invalid based on the date format pattern.
     */
    public static Date parse(String dateString) throws ParseException {
        String dateFormat = determineDateFormat(dateString);
        if (dateFormat == null) {
            throw new ParseException("Unknown date format.", 0);
        }
        return parse(dateString, dateFormat);
    }

    /**
     * Validate the actual date of the given date string based on the given date format pattern and
     * return a date instance based on the given date string.
     *
     * @param dateString The date string.
     * @param dateFormat The date format pattern which should respect the SimpleDateFormat rules.
     * @return The parsed date object.
     * @throws ParseException If the given date string or its actual date is invalid based on the
     *                        given date format pattern.
     * @see SimpleDateFormat
     */
    public static Date parse(String dateString, String dateFormat) throws ParseException {
        removeUnwantedChartsFromDate(dateString);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setLenient(false); // Don't automatically convert invalid date.
        return simpleDateFormat.parse(dateString);
    }

    public static Timestamp parseToTimestamp(String dateString, String dateFormat, TimeZone timeZone) {
        if (dateString == null || dateString.isEmpty() || dateFormat == null || dateFormat.isEmpty()
            || dateString.length() <= DATE_STRING_MIN_LENGTH) {
            return null;
        }

        removeUnwantedChartsFromDate(dateString);
        Date date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setLenient(false); // Don't automatically convert invalid date.
        if (timeZone != null) {
            simpleDateFormat.setTimeZone(timeZone);
        }

        try {
            date = simpleDateFormat.parse(dateString);
            Calendar calendar = toCalendar(date);
            return new Timestamp(calendar.getTimeInMillis());

        } catch (ParseException e) {
            try {
                if (dateFormat.contains(DATE_FORMAT_MILLISECOND)) {
                    dateString = dateString + ".0";
                    return new Timestamp(toCalendar(simpleDateFormat.parse(dateString)).getTimeInMillis());
                }
                date = simpleDateFormat.parse(dateString.replaceAll("\\W", " "));
                Calendar calendar = toCalendar(date);
                return new Timestamp(calendar.getTimeInMillis());

            } catch (ParseException parseException) {
                return null;
            }
        }
    }

    // Validators ---------------------------------------------------------------------------------

    /**
     * Checks whether the actual date of the given date string is valid. This makes use of the
     * {@link DateUtil#determineDateFormat(String)} to determine the SimpleDateFormat pattern to be
     * used for parsing.
     *
     * @param dateString The date string.
     * @return True if the actual date of the given date string is valid.
     */
    public static boolean isValidDate(String dateString) {
        removeUnwantedChartsFromDate(dateString);
        try {
            parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Checks whether the actual date of the given date string is valid based on the given date
     * format pattern.
     *
     * @param dateString The date string.
     * @param dateFormat The date format pattern which should respect the SimpleDateFormat rules.
     * @return True if the actual date of the given date string is valid based on the given date format pattern.
     * @see SimpleDateFormat
     */
    public static boolean isValidDate(String dateString, String dateFormat) {
        removeUnwantedChartsFromDate(dateString);
        try {
            parse(dateString, dateFormat);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Determine SimpleDateFormat pattern matching with the given date string. Returns null if
     * format is unknown. You can simply extend DateUtil with more formats if needed.
     *
     * @param dateString The date string to determine the SimpleDateFormat pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is unknown.
     * @see SimpleDateFormat
     */
    public static String determineDateFormat(String dateString) {

        String dateFormat = findMatchDateFormat(removeUnwantedChartsFromDate(dateString));

        if (dateFormat == null) {
            dateFormat = findMatchDateFormat(removeUnwantedChartsFromDate(dateString.replaceAll("\\W", " ")));
        }

        return dateFormat;
    }

    private static String findMatchDateFormat(String str) {
        for (String regexp : DATE_FORMAT_PATTERN_STORE.keySet()) {
            if (str.matches(regexp)) {
                return DATE_FORMAT_PATTERN_STORE.get(regexp);
            }
        }
        return null;
    }

    private static String removeUnwantedChartsFromDate(String str) {
        // Remove timezone: (?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])
        // Remove 1st, 2nd, ect: (?<=\d)(st|nd|rd|th)
        return str.replaceAll("(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])", "").replaceAll("(?<=\\d)(st|nd|rd|th)", "")
            .toLowerCase();
    }
}
