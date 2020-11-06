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

    private static Map<String, String> DATE_FORMAT_PATTERN_STORE;
    private static LinkedHashMap<String, String> DATE_FORMAT_REGEXPS = new LinkedHashMap<>();

    //Day digit 1-31
    private static final String DAY_DIGIT = "(0?[1-9]|[12][0-9]|3[01])";
    //Day digit 01-31
    private static final String DAY_DIGIT_TWO_DIGIT = "(0[1-9]|[12][0-9]|3[01])";
    //Month digit 1-12 
    private static final String MONTH_DIGIT = "(0?[1-9]|1[012])";
    //Month digit 01-12
    private static final String MONTH_DIGIT_TWO_DIGIT = "(0[1-9]|1[012])";
    //Year two digit 00-99
    private static final String YEAR_TWO_DIGIT = "([0-9]{2})";
    //Year digit 1000-2999
    private static final String YEAR_DIGIT = "([12][0-9]{3})";
    //Week day first letter e.g. T, M
    private static final String WEEK_DAY_NAME_FIRST_LETTER = "(m|tu|w|th|f|sa|su)";
    //Week day short name e.g. Tus
    private static final String WEEK_DAY_NAME_SHORT = "(mon|tue|wed|thu|fri|sat|sun)";
    //Week day long name e.g. Tus
    private static final String WEEK_DAY_NAME_LONG = "(monday|tuesday|wednesday|thursday|friday|saturday|sunday)";
    //Month short name e.g. Feb
    private static final String MONTH_NAME_SHORT = "(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)";
    //Month long name e.g. February
    private static final String MONTH_NAME_LONG = "(january|february|march|april|may|june|july|august|september|october|november|december)";
    //Hour digit 01-24
    private static final String HOURS_DIGIT = "([0-1][0-9]|2[0-3])";
    //Minutes or Second digit 01-60
    private static final String MINUTES_OR_SECOND_DIGIT = "([0-5][0-9])";
//    //TimeZone e.g., Z, +07 or +07:00
//    private static final String TIME_ZONE = "(Z|[+-](?:2[0-3]|[01][0-9])(?::?(?:[0-5][0-9]))?)";

    //yyMMdd
    private static final String DATE_NO_SPACE_YEAR_TWO_DIGIT_MONTH_DAY = YEAR_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}";
    //yyddMM
    private static final String DATE_NO_SPACE_YEAR_TWO_DIGIT_DAY_MONTH = YEAR_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}";
    //ddMMyy
    private static final String DATE_NO_SPACE_DAY_MONTH_YEAR_TWO_DIGIT = DAY_DIGIT_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}" + YEAR_TWO_DIGIT + "{1}";
    //MMddyy
    private static final String DATE_NO_SPACE_MONTH_DAY_YEAR_TWO_DIGIT = MONTH_DIGIT_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}" + YEAR_TWO_DIGIT + "{1}";
    //dd-MM-yy
    private static final String DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT = DAY_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}" + "-" + YEAR_TWO_DIGIT + "{1}";
    //MM-dd-yy
    private static final String DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT = MONTH_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}" + "-" + YEAR_TWO_DIGIT + "{1}";
    //yy-MM-dd
    private static final String DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY = YEAR_TWO_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}";
    //yy-dd-MM
    private static final String DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH = YEAR_TWO_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}";
    //dd/MM/yy
    private static final String DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT = DAY_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}" + "/" + YEAR_TWO_DIGIT + "{1}";
    //MM/dd/yy
    private static final String DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT = MONTH_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}" + "/" + YEAR_TWO_DIGIT + "{1}";
    //yy/MM/dd
    private static final String DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY = YEAR_TWO_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}";
    //yy/dd/MM
    private static final String DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH = YEAR_TWO_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}";
    //dd MM yy
    private static final String DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT = DAY_DIGIT + "{1}" + "\\s" + MONTH_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}";
    //MM dd yy
    private static final String DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT = MONTH_DIGIT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}";
    //yy MM dd
    private static final String DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY = YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_DIGIT + "{1}" + "\\s" + DAY_DIGIT + "{1}";
    //yy dd MM
    private static final String DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH = YEAR_TWO_DIGIT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + MONTH_DIGIT + "{1}";

    //yyyyMMdd
    private static final String DATE_NO_SPACE_YEAR_MONTH_DAY = YEAR_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}";
    //yyyyddMM
    private static final String DATE_NO_SPACE_YEAR_DAY_MONTH = YEAR_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}";
    //ddMMyyyy
    private static final String DATE_NO_SPACE_DAY_MONTH_YEAR = DAY_DIGIT_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}" + YEAR_DIGIT + "{1}";
    //MMddyyyy
    private static final String DATE_NO_SPACE_MONTH_DAY_YEAR = MONTH_DIGIT_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}" + YEAR_DIGIT + "{1}";
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

    //HH:mm
    private static final String TIME_SPACE = HOURS_DIGIT + "{1}" + "\\s" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HH:mm:ss
    private static final String TIME_SPACE_SECOND = HOURS_DIGIT + "{1}" + "\\s" + MINUTES_OR_SECOND_DIGIT + "{1}" + "\\s" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HHmm
    private static final String TIME_NO_SPACE = HOURS_DIGIT + "{1}" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HHmmss
    private static final String TIME_NO_SPACE_SECOND = HOURS_DIGIT + "{1}" + MINUTES_OR_SECOND_DIGIT + "{1}" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HH:mm
    private static final String TIME_COLON = HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HH:mm:ss
    private static final String TIME_COLON_SECOND = HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}";

    static {

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "$", "yyyyMMdd");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_DAY_MONTH + "$", "yyyyddMM");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_DAY_MONTH_YEAR + "$", "ddMMyyyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_MONTH_DAY_YEAR + "$", "MMddyyyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "$", "dd-MM-yyyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "$", "MM-dd-yyyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "$", "yyyy-MM-dd");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "$", "yyyy-dd-MM");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "$", "dd/MM/yyyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "$", "MM/dd/yyyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "$", "yyyy/MM/dd");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "$", "yyyy/dd/MM");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "$", "dd MM yyyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "$", "MM dd yyyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "$", "yyyy MM dd");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "$", "yyyy dd MM");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "$", "dd MMM yyyy");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "$", "dd MMMM yyyy");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "$", "MMM dd yyyy");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "$", "MMMM dd yyyy");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "$", "yyyy MMM dd");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "$", "yyyy MMMM dd");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE + "$", "yyyyMMddHHmm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_NO_SPACE + "$", "yyyyMMdd HHmm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON + "$", "dd-MM-yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON + "$", "MM-dd-yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON + "$", "yyyy-MM-dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON + "$", "yyyy-dd-MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON + "$", "dd/MM/yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON + "$", "MM/dd/yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON + "$", "yyyy/MM/dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON + "$", "yyyy/dd/MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s" + TIME_COLON + "$", "dd MM yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s" + TIME_COLON + "$", "MM dd yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_COLON + "$", "yyyy MM dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s" + TIME_COLON + "$", "yyyy dd MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s" + TIME_SPACE + "$", "dd MM yyyy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s" + TIME_SPACE + "$", "MM dd yyyy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_SPACE + "$", "yyyy MM dd HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s" + TIME_SPACE + "$", "yyyy dd MM HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "dd MMM yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "dd MMMM yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "MMM dd yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "MMMM dd yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "yyyy MMM dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "yyyy MMMM dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "dd MMM yyyy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "dd MMMM yyyy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "MMM dd yyyy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "MMMM dd yyyy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "yyyy MMM dd HH mm");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "yyyy MMMM dd HH mm");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE_SECOND + "$", "yyyyMMddHHmmss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_NO_SPACE_SECOND + "$", "yyyyMMdd HHmmss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "$", "dd-MM-yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "$", "MM-dd-yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "$", "yyyy-MM-dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "$", "yyyy-dd-MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "$", "dd/MM/yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "$", "MM/dd/yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "$", "yyyy/MM/dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "$", "yyyy/dd/MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "$", "dd MM yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "$", "MM dd yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "$", "yyyy MM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "$", "yyyy dd MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s" + TIME_SPACE_SECOND + "$", "dd MM yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s" + TIME_SPACE_SECOND + "$", "MM dd yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_SPACE_SECOND + "$", "yyyy MM dd HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s" + TIME_SPACE_SECOND + "$", "yyyy dd MM HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "dd MMM yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "dd MMMM yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "MMM dd yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "MMMM dd yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "yyyy MMM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "yyyy MMMM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "dd MMM yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "dd MMMM yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "MMM dd yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "MMMM dd yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "yyyy MMM dd HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "yyyy MMMM dd HH mm ss");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE_SECOND + "\\d{3}" + "$", "yyyyMMddHHmmssSSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_NO_SPACE_SECOND + "\\d{3}" + "$", "yyyyMMdd HHmmssSSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd-MM-yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MM-dd-yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy-MM-dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy-dd-MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd/MM/yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MM/dd/yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy/MM/dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy/dd/MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd MM yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MM dd yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy MM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy dd MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "dd MM yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "MM dd yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "yyyy MM dd HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "yyyy dd MM HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd MMM yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd MMMM yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MMM dd yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MMMM dd yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy MMM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yyyy MMMM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "dd MMM yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "dd MMMM yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "MMM dd yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "MMMM dd yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "yyyy MMM dd HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "yyyy MMMM dd HH mm ss SSS");

        //yy
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "$", "yyMMdd");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "$", "yyddMM");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "$", "ddMMyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "$", "MMddyy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "$", "dd-MM-yy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "$", "MM-dd-yy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "$", "yy-MM-dd");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "$", "yy-dd-MM");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "$", "dd/MM/yy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "$", "MM/dd/yy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "$", "yy/MM/dd");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "$", "yy/dd/MM");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "$", "dd MM yy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "$", "MM dd yy");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "$", "yy MM dd");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "$", "yy dd MM");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "$", "dd MMM yy");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "$", "dd MMMM yy");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "$", "MMM dd yy");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "$", "MMMM dd yy");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "$", "yy MMM dd");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "$", "yy MMMM dd");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_NO_SPACE + "$", "yyMMdd HHmm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_COLON + "$", "dd-MM-yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_COLON + "$", "MM-dd-yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_COLON + "$", "yy-MM-dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_COLON + "$", "yy-dd-MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_COLON + "$", "dd/MM/yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_COLON + "$", "MM/dd/yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_COLON + "$", "yy/MM/dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_COLON + "$", "yy/dd/MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_COLON + "$", "dd MM yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_COLON + "$", "MM dd yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_COLON + "$", "yy MM dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_COLON + "$", "yy dd MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_SPACE + "$", "dd MM yy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_SPACE + "$", "MM dd yy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_SPACE + "$", "yy MM dd HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_SPACE + "$", "yy dd MM HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "dd MMM yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "dd MMMM yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "MMM dd yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "MMMM dd yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "yy MMM dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON + "$", "yy MMMM dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "dd MMM yy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "dd MMMM yy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "MMM dd yy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "MMMM dd yy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "yy MMM dd HH mm");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE + "$", "yy MMMM dd HH mm");


        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_NO_SPACE_SECOND + "$", "yyMMdd HHmmss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "$", "dd-MM-yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "$", "MM-dd-yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "$", "yy-MM-dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "$", "yy-dd-MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "$", "dd/MM/yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "$", "MM/dd/yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "$", "yy/MM/dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "$", "yy/dd/MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "$", "dd MM yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "$", "MM dd yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "$", "yy MM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "$", "yy dd MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_SPACE_SECOND + "$", "dd MM yy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_SPACE_SECOND + "$", "MM dd yy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_SPACE_SECOND + "$", "yy MM dd HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_SPACE_SECOND + "$", "yy dd MM HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "dd MMM yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "dd MMMM yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "MMM dd yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "MMMM dd yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "yy MMM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "$", "yy MMMM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "dd MMM yy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "dd MMMM yy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "MMM dd yy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "MMMM dd yy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "yy MMM dd HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "$", "yy MMMM dd HH mm ss");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_NO_SPACE_SECOND + "\\d{3}" + "$", "yyMMdd HHmmssSSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd-MM-yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MM-dd-yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yy-MM-dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yy-dd-MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd/MM/yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MM/dd/yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yy/MM/dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yy/dd/MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd MM yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MM dd yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yy MM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yy dd MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "dd MM yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "MM dd yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "yy MM dd HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "yy dd MM HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd MMM yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "dd MMMM yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MMM dd yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "MMMM dd yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yy MMM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_COLON_SECOND + "\\." + "\\d{3}" + "$", "yy MMMM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "dd MMM yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DAY_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "dd MMMM yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "MMM dd yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + YEAR_TWO_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "MMMM dd yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_SHORT + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "yy MMM dd HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "{1}" + "\\s" + MONTH_NAME_LONG + "{1}" + "\\s" + DAY_DIGIT + "{1}" + "\\s" + TIME_SPACE_SECOND + "\\s" + "\\d{3}" + "$", "yy MMMM dd HH mm ss SSS");

        DATE_FORMAT_REGEXPS.put("^" + YEAR_TWO_DIGIT + "$", "yy");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "$", "yyyy");
        DATE_FORMAT_REGEXPS.put("^" + YEAR_DIGIT + "{1}" + MONTH_DIGIT + "{1}" + "$", "yyyyMM");

        DATE_FORMAT_REGEXPS.put("^" + WEEK_DAY_NAME_SHORT + "{1}" + "$", "EEE");
        DATE_FORMAT_REGEXPS.put("^" + WEEK_DAY_NAME_LONG + "{1}" + "$", "EEEE");
        DATE_FORMAT_REGEXPS.put("^" + WEEK_DAY_NAME_FIRST_LETTER + "{1}" + "$", "EEEEE");

        DATE_FORMAT_REGEXPS.put("^" + TIME_NO_SPACE + "{1}" + "$", "HHmm");
        DATE_FORMAT_REGEXPS.put("^" + TIME_NO_SPACE_SECOND + "{1}" + "$", "HHmmss");
        DATE_FORMAT_REGEXPS.put("^" + TIME_NO_SPACE_SECOND + "{1}" + "\\." + "\\d{3}" + "$", "HHmmss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + TIME_COLON + "{1}" + "$", "HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + TIME_COLON_SECOND + "{1}" + "$", "HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + TIME_COLON_SECOND + "{1}" + "\\." + "\\d{3}" + "$", "HH:mm:ss.SSS");

        DATE_FORMAT_PATTERN_STORE = DATE_FORMAT_REGEXPS;
    }

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

    public static Timestamp parseToTimestamp(String dateString, String dateFormat) {
        removeUnwantedChartsFromDate(dateString);
        try {
            if (dateString == null || dateFormat.isEmpty())
                throw new Exception("Field is empty or has a null date value!");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
            simpleDateFormat.setLenient(false); // Don't automatically convert invalid date.
            Date date = simpleDateFormat.parse(dateString);

            if (date == null)
                date = simpleDateFormat.parse(dateString.replaceAll("\\W", " "));

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
     * @return True if the actual date of the given date string is valid based on the given date
     * format pattern.
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

//        if(dateString.matches("^(\\d{10})$"))
//            return "unixtime";
//
//        if(dateString.matches("^(\\d{13})$"))
//            return "millisecond";
//
//        if(dateString.matches("^(\\d{16})$"))
//            return "microsecond";
//
//        if(dateString.matches("^(\\d{19})$"))
//            return "nanosecond";

        String dateFormat = findMatchDateFormat(removeUnwantedChartsFromDate(dateString));

        if (dateFormat == null)
            dateFormat = findMatchDateFormat(dateString.replaceAll("\\W", " "));

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
        //Remove timezone
        str.replaceAll("(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])", "");
        //Remove 1st, 2nd, ect
        str.replaceAll("(?<=\\d)(st|nd|rd|th)", "");

        return str.toLowerCase();
    }
}
