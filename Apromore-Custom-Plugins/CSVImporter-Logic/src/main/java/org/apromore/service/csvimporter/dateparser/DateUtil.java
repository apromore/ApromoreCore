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
package org.apromore.service.csvimporter.dateparser;

import org.apromore.service.csvimporter.utilities.InvalidCSVException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.*;

public class DateUtil {

    //Day digit 1-31
    private static final String DAY_DIGIT = "(0?[1-9]|[12][0-9]|3[01])";
    //Month digit 1-12 
    private static final String MONTH_DIGIT = "(0?[1-9]|1[012])";
    //Year digit 1000-2999
    private static final String YEAR_DIGIT = "([12][0-9]{3}{1})";
    //Month short name e.g Feb
    private static final String MONTH_NAME_SHORT = "(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)";
    //Month long name e.g February
    private static final String MONTH_NAME_LONG = "(January|February|March|April|May|June|July|August|September|October|November|December)";
    //Hour digit 1-24
    private static final String HOURS_DIGIT = "(0?[0-9]|1[0-9]|2[0-4])";
    //Minutes or Second digit 1-60
    private static final String MINUTES_OR_SECOND_DIGIT = "(0?[0-9]|[1-5][0-9]|60)";

    //yyyyMMdd
    private static final String DATE_NO_SPACE_YEAR_MONTH_DAY = YEAR_DIGIT + "{1}" + MONTH_DIGIT + "{1}" + DAY_DIGIT + "{1}";
    //yyyyddMM
    private static final String DATE_NO_SPACE_YEAR_DAY_MONTH = YEAR_DIGIT + "{1}" + DAY_DIGIT + "{1}" + MONTH_DIGIT + "{1}";
    //ddMMyyyy
    private static final String DATE_NO_SPACE_DAY_MONTH_YEAR = DAY_DIGIT + "{1}" + MONTH_DIGIT + "{1}" + YEAR_DIGIT + "{1}";
    //MMddyyyy
    private static final String DATE_NO_SPACE_MONTH_DAY_YEAR = MONTH_DIGIT + "{1}" + DAY_DIGIT + "{1}" + YEAR_DIGIT + "{1}";
    //dd-MM-yyyy
    private static final String DATE_DASH_DAY_MONTH_YEAR = DAY_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}" + "-" + YEAR_DIGIT + "{1}";
    //MM-dd-yyyy
    private static final String DATE_DASH_MONTH_DAY_YEAR = MONTH_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}" + "-" + YEAR_DIGIT + "{1}";
    //yyyy-MM-dd
    private static final String DATE_DASH_YEAR_MONTH_DAY = YEAR_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}";
    //yyyy-dd-MM
    private static final String DATE_DASH_YEAR_DAY_MONTH = YEAR_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}";
    //dd/MM/yyyy
    private static final String DATE_SLASH_DAY_MONTH_YEAR = DAY_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}" + "/" + YEAR_DIGIT + "{1}";
    //MM/dd/yyyy
    private static final String DATE_SLASH_MONTH_DAY_YEAR = MONTH_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}" + "/" + YEAR_DIGIT + "{1}";
    //yyyy/MM/dd
    private static final String DATE_SLASH_YEAR_MONTH_DAY = YEAR_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}";
    //yyyy/dd/MM
    private static final String DATE_SLASH_YEAR_DAY_MONTH = YEAR_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}";
    //dd MM yyyy
    private static final String DATE_SPACE_DAY_MONTH_YEAR = DAY_DIGIT + "{1}" + "\\s" + MONTH_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}";
    //MM dd yyyy
    private static final String DATE_SPACE_MONTH_DAY_YEAR = MONTH_DIGIT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}";
    //yyyy MM dd
    private static final String DATE_SPACE_YEAR_MONTH_DAY = YEAR_DIGIT + "{1}" + "\\s" + MONTH_DIGIT + "{1}" + "\\s" + DAY_DIGIT + "{1}";
    //yyyy dd MM
    private static final String DATE_SPACE_YEAR_DAY_MONTH = YEAR_DIGIT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + MONTH_DIGIT + "{1}";


    //HHMM
    private static final String TIME_NO_SPACE = HOURS_DIGIT + "{1}" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HHMMSS
    private static final String TIME_NO_SPACE_SECOND = HOURS_DIGIT + "{1}" + MINUTES_OR_SECOND_DIGIT + "{1}" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HH:MM
    private static final String TIME_COLON = HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HH:MM:SS
    private static final String TIME_COLON_SECOND = HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}";


    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<String, String>() {{
        put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "$", "yyyyMMdd");
        put("^" + DATE_NO_SPACE_YEAR_DAY_MONTH + "$", "yyyyddMM");
        put("^" + DATE_NO_SPACE_DAY_MONTH_YEAR + "$", "ddMMyyyy");
        put("^" + DATE_NO_SPACE_MONTH_DAY_YEAR + "$", "MMddyyyy");
        put("^" + DATE_DASH_DAY_MONTH_YEAR + "$", "dd-MM-yyyy");
        put("^" + DATE_DASH_MONTH_DAY_YEAR + "$", "MM-dd-yyyy");
        put("^" + DATE_DASH_YEAR_MONTH_DAY + "$", "yyyy-MM-dd");
        put("^" + DATE_DASH_YEAR_DAY_MONTH + "$", "yyyy-dd-MM");
        put("^" + DATE_SLASH_DAY_MONTH_YEAR + "$", "dd/MM/yyyy");
        put("^" + DATE_SLASH_MONTH_DAY_YEAR + "$", "MM/dd/yyyy");
        put("^" + DATE_SLASH_YEAR_MONTH_DAY + "$", "yyyy/MM/dd");
        put("^" + DATE_SLASH_YEAR_DAY_MONTH + "$", "yyyy/dd/MM");
        put("^" + DATE_SPACE_DAY_MONTH_YEAR + "$", "dd MM yyyy");
        put("^" + DATE_SPACE_MONTH_DAY_YEAR + "$", "MM dd yyyy");
        put("^" + DATE_SPACE_YEAR_MONTH_DAY + "$", "yyyy MM dd");
        put("^" + DATE_SPACE_YEAR_DAY_MONTH + "$", "yyyy dd MM");
        put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "$", "dd MMM yyyy");
        put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "$", "dd MMMM yyyy");

        put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE + "$", "yyyyMMddHHmm");
        put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_NO_SPACE + "$", "yyyyMMdd HHmm");
        put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON + "$", "dd-MM-yyyy HH:mm");
        put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON + "$", "MM-dd-yyyy HH:mm");
        put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON + "$", "yyyy-MM-dd HH:mm");
        put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON + "$", "yyyy-dd-MM HH:mm");
        put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON + "$", "dd/MM/yyyy HH:mm");
        put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON + "$", "MM/dd/yyyy HH:mm");
        put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON + "$", "yyyy/MM/dd HH:mm");
        put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON + "$", "yyyy/dd/MM HH:mm");
        put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s" + TIME_COLON + "$", "dd MM yyyy HH:mm");
        put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s" + TIME_COLON + "$", "MM dd yyyy HH:mm");
        put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_COLON + "$", "yyyy MM dd HH:mm");
        put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s" + TIME_COLON + "$", "yyyy dd MM HH:mm");
        put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "dd MMM yyyy HH:mm");
        put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "dd MMMM yyyy HH:mm");

        put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE_SECOND + "$", "yyyyMMddHHmmss");
        put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_NO_SPACE_SECOND + "$", "yyyyMMdd HHmmss");
        put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "$", "dd-MM-yyyy HH:mm:ss");
        put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "$", "MM-dd-yyyy HH:mm:ss");
        put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "$", "yyyy-MM-dd HH:mm:ss");
        put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "$", "yyyy-dd-MM HH:mm:ss");
        put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "$", "dd/MM/yyyy HH:mm:ss");
        put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "$", "MM/dd/yyyy HH:mm:ss");
        put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "$", "yyyy/MM/dd HH:mm:ss");
        put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "$", "yyyy/dd/MM HH:mm:ss");
        put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "$", "dd MM yyyy HH:mm:ss");
        put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "$", "MM dd yyyy HH:mm:ss");
        put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "$", "yyyy MM dd HH:mm:ss");
        put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "$", "yyyy dd MM HH:mm:ss");
        put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "dd MMM yyyy HH:mm:ss");
        put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "dd MMMM yyyy HH:mm:ss");

        put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE_SECOND + "\\." + "\\d{3}" + "$", "yyyyMMddHHmmss.SSS");
        put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_NO_SPACE_SECOND + "\\." + "\\d{3}" + "$", "yyyyMMdd HHmmss.SSS");
        put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd-MM-yyyy HH:mm:ss.SSS");
        put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MM-dd-yyyy HH:mm:ss.SSS");
        put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy-MM-dd HH:mm:ss.SSS");
        put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy-dd-MM HH:mm:ss.SSS");
        put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd/MM/yyyy HH:mm:ss.SSS");
        put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MM/dd/yyyy HH:mm:ss.SSS");
        put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy/MM/dd HH:mm:ss.SSS");
        put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy/dd/MM HH:mm:ss.SSS");
        put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd MM yyyy HH:mm:ss.SSS");
        put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MM dd yyyy HH:mm:ss.SSS");
        put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy MM dd HH:mm:ss.SSS");
        put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy dd MM HH:mm:ss.SSS");
        put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd MMM yyyy HH:mm:ss.SSS");
        put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd MMMM yyyy HH:mm:ss.SSS");

    }};

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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setLenient(false); // Don't automatically convert invalid date.
        return simpleDateFormat.parse(dateString);
    }

    public static Timestamp parseToTimestamp(String dateString, String dateFormat) {
        try {
            if (dateString == null || dateFormat.isEmpty())
                throw new Exception("Field is empty or has a null date value!");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            simpleDateFormat.setLenient(false); // Don't automatically convert invalid date.
            Date date = simpleDateFormat.parse(dateString);
            Calendar calendar = toCalendar(date);
            return new Timestamp(calendar.getTimeInMillis());

        } catch (Exception e) {
            return null;
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
     * @return True if the actual date of the given date string is valid based on the given date
     * format pattern.
     * @see SimpleDateFormat
     */
    public static boolean isValidDate(String dateString, String dateFormat) {
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
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return DATE_FORMAT_REGEXPS.get(regexp);
            }
        }
        return null; // Unknown format.
    }
}
