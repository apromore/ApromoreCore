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

import java.util.LinkedHashMap;
import java.util.Map;

class DatePatterns {
    static Map<String, String> DATE_FORMAT_PATTERN_STORE;
    private static final LinkedHashMap<String, String> DATE_FORMAT_REGEXPS = new LinkedHashMap<>();
    private static final String REGEX_D1 = "\\d{1}";
    private static final String REGEX_D3 = "\\d{3}";

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
    //Month short name e.g. Feb
    private static final String MONTH_NAME_SHORT = "(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)";
    //Month long name e.g. February
    private static final String MONTH_NAME_LONG =
        "(january|february|march|april|may|june|july|august|september|october|november|december)";
    //Hour digit 1-23
    private static final String HOURS_DIGIT = "(0?[0-9]|1[0-9]|2[0-3])";
    //Hour digit 01-23
    private static final String HOURS_DIGIT_TWO_DIGIT = "([0-1][0-9]|2[0-3])";
    //Minutes or Second digit 01-60
    private static final String MINUTES_OR_SECOND_DIGIT = "(0?[0-9]|[1-5][0-9])";
    //Minutes or Second digit 1-60
    private static final String MINUTES_OR_SECOND_TWO_DIGIT = "([0-5][0-9])";
    //am/pm
    private static final String AM_PM = "(am|pm)";
    //TimeZone e.g., Z, +07 or +07:00
    private static final String TIME_ZONE = "(Z|[+-](?:2[0-3]|[01][0-9])(?::?(?:[0-5][0-9]))?)";

    //yyMMdd
    private static final String DATE_NO_SPACE_YEAR_TWO_DIGIT_MONTH_DAY =
        YEAR_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}";
    //yyddMM
    private static final String DATE_NO_SPACE_YEAR_TWO_DIGIT_DAY_MONTH =
        YEAR_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}";
    //ddMMyy
    private static final String DATE_NO_SPACE_DAY_MONTH_YEAR_TWO_DIGIT =
        DAY_DIGIT_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}" + YEAR_TWO_DIGIT + "{1}";
    //MMddyy
    private static final String DATE_NO_SPACE_MONTH_DAY_YEAR_TWO_DIGIT =
        MONTH_DIGIT_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}" + YEAR_TWO_DIGIT + "{1}";
    //dd-MM-yy
    private static final String DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT =
        DAY_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}" + "-" + YEAR_TWO_DIGIT + "{1}";
    //MM-dd-yy
    private static final String DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT =
        MONTH_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}" + "-" + YEAR_TWO_DIGIT + "{1}";
    //yy-MM-dd
    private static final String DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY =
        YEAR_TWO_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}";
    //yy-dd-MM
    private static final String DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH =
        YEAR_TWO_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}";
    //dd/MM/yy
    private static final String DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT =
        DAY_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}" + "/" + YEAR_TWO_DIGIT + "{1}";
    //MM/dd/yy
    private static final String DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT =
        MONTH_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}" + "/" + YEAR_TWO_DIGIT + "{1}";
    //yy/MM/dd
    private static final String DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY =
        YEAR_TWO_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}";
    //yy/dd/MM
    private static final String DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH =
        YEAR_TWO_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}";
    //dd MM yy
    private static final String DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT =
        DAY_DIGIT + "{1}" + "\\s+" + MONTH_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}";
    //MM dd yy
    private static final String DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT =
        MONTH_DIGIT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}";
    //yy MM dd
    private static final String DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY =
        YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_DIGIT + "{1}" + "\\s+" + DAY_DIGIT + "{1}";
    //yy dd MM
    private static final String DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH =
        YEAR_TWO_DIGIT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_DIGIT + "{1}";

    //yyyyMMdd
    private static final String DATE_NO_SPACE_YEAR_MONTH_DAY =
        YEAR_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}";
    //yyyyddMM
    private static final String DATE_NO_SPACE_YEAR_DAY_MONTH =
        YEAR_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}";
    //ddMMyyyy
    private static final String DATE_NO_SPACE_DAY_MONTH_YEAR =
        DAY_DIGIT_TWO_DIGIT + "{1}" + MONTH_DIGIT_TWO_DIGIT + "{1}" + YEAR_DIGIT + "{1}";
    //MMddyyyy
    private static final String DATE_NO_SPACE_MONTH_DAY_YEAR =
        MONTH_DIGIT_TWO_DIGIT + "{1}" + DAY_DIGIT_TWO_DIGIT + "{1}" + YEAR_DIGIT + "{1}";
    //dd-MM-yyyy
    private static final String DATE_DASH_DAY_MONTH_YEAR =
        DAY_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}" + "-" + YEAR_DIGIT + "{1}";
    //MM-dd-yyyy
    private static final String DATE_DASH_MONTH_DAY_YEAR =
        MONTH_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}" + "-" + YEAR_DIGIT + "{1}";
    //yyyy-MM-dd
    private static final String DATE_DASH_YEAR_MONTH_DAY =
        YEAR_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}";
    //yyyy-dd-MM
    private static final String DATE_DASH_YEAR_DAY_MONTH =
        YEAR_DIGIT + "{1}" + "-" + DAY_DIGIT + "{1}" + "-" + MONTH_DIGIT + "{1}";
    //dd/MM/yyyy
    private static final String DATE_SLASH_DAY_MONTH_YEAR =
        DAY_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}" + "/" + YEAR_DIGIT + "{1}";
    //MM/dd/yyyy
    private static final String DATE_SLASH_MONTH_DAY_YEAR =
        MONTH_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}" + "/" + YEAR_DIGIT + "{1}";
    //yyyy/MM/dd
    private static final String DATE_SLASH_YEAR_MONTH_DAY =
        YEAR_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}";
    //yyyy/dd/MM
    private static final String DATE_SLASH_YEAR_DAY_MONTH =
        YEAR_DIGIT + "{1}" + "/" + DAY_DIGIT + "{1}" + "/" + MONTH_DIGIT + "{1}";
    //dd MM yyyy
    private static final String DATE_SPACE_DAY_MONTH_YEAR =
        DAY_DIGIT + "{1}" + "\\s+" + MONTH_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}";
    //MM dd yyyy
    private static final String DATE_SPACE_MONTH_DAY_YEAR =
        MONTH_DIGIT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}";
    //yyyy MM dd
    private static final String DATE_SPACE_YEAR_MONTH_DAY =
        YEAR_DIGIT + "{1}" + "\\s+" + MONTH_DIGIT + "{1}" + "\\s+" + DAY_DIGIT + "{1}";
    //yyyy dd MM
    private static final String DATE_SPACE_YEAR_DAY_MONTH =
        YEAR_DIGIT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_DIGIT + "{1}";

    //HH mm
    private static final String TIME_SPACE = HOURS_DIGIT + "{1}" + "\\s+" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HH mm ss
    private static final String TIME_SPACE_SECOND =
        HOURS_DIGIT + "{1}" + "\\s+" + MINUTES_OR_SECOND_DIGIT + "{1}" + "\\s+" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HHmm
    private static final String TIME_NO_SPACE = HOURS_DIGIT_TWO_DIGIT + "{1}" + MINUTES_OR_SECOND_TWO_DIGIT + "{1}";
    //HHmmss
    private static final String TIME_NO_SPACE_SECOND =
        HOURS_DIGIT_TWO_DIGIT + "{1}" + MINUTES_OR_SECOND_TWO_DIGIT + "{1}" + MINUTES_OR_SECOND_TWO_DIGIT + "{1}";
    //HH:mm
    private static final String TIME_COLON = HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HH:mm:ss
    private static final String TIME_COLON_SECOND =
        HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //mm:ss
    private static final String TIME_COLON_MIN_SEC =
        MINUTES_OR_SECOND_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}";
    //HH:mm a
    private static final String TIME_COLON_AM_PM =
        HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}" + "\\s+" + AM_PM + "{1}";
    //HH:mm:ss a
    private static final String TIME_COLON_SECOND_AM_PM =
        HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}" + "\\s+"
        + AM_PM + "{1}";
    //HH:mma
    private static final String TIME_COLON_AM_PM_NO_SPACE =
        HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}" + AM_PM + "{1}";
    //HH:mm:ssa
    private static final String TIME_COLON_SECOND_AM_PM_NO_SPACE =
        HOURS_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}" + ":" + MINUTES_OR_SECOND_DIGIT + "{1}" + AM_PM
        + "{1}";

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
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "$",
            "dd MMM yyyy");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "$",
            "dd MMMM yyyy");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "$",
            "MMM dd yyyy");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "$",
            "MMMM dd yyyy");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "$",
            "yyyy MMM dd");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "$",
            "yyyy MMMM dd");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE + "$", "yyyyMMddHHmm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_NO_SPACE + "$", "yyyyMMdd HHmm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON + "$", "dd-MM-yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON + "$", "MM-dd-yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON + "$", "yyyy-MM-dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON + "$", "yyyy-dd-MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON + "$", "dd/MM/yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON + "$", "MM/dd/yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON + "$", "yyyy/MM/dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON + "$", "yyyy/dd/MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "t" + "{1}" + TIME_COLON + "$", "dd-MM-yyyy'T'HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "t" + "{1}" + TIME_COLON + "$", "MM-dd-yyyy'T'HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "t" + "{1}" + TIME_COLON + "$", "yyyy-MM-dd'T'HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "t" + "{1}" + TIME_COLON + "$", "yyyy-dd-MM'T'HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_COLON + "$", "dd MM yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_COLON + "$", "MM dd yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_COLON + "$", "yyyy MM dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_COLON + "$", "yyyy dd MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_AM_PM + "$", "dd-MM-yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_AM_PM + "$", "MM-dd-yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM + "$", "yyyy-MM-dd HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM + "$", "yyyy-dd-MM HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_AM_PM + "$",
            "dd/MM/yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_AM_PM + "$",
            "MM/dd/yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM + "$",
            "yyyy/MM/dd HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM + "$",
            "yyyy/dd/MM HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "t" + "{1}" + TIME_COLON_AM_PM + "$",
            "dd-MM-yyyy'T'HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "t" + "{1}" + TIME_COLON_AM_PM + "$",
            "MM-dd-yyyy'T'HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "t" + "{1}" + TIME_COLON_AM_PM + "$",
            "yyyy-MM-dd'T'HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "t" + "{1}" + TIME_COLON_AM_PM + "$",
            "yyyy-dd-MM'T'HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_AM_PM + "$",
            "dd MM yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_AM_PM + "$",
            "MM dd yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM + "$",
            "yyyy MM dd HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM + "$",
            "yyyy dd MM HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "dd-MM-yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "MM-dd-yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yyyy-MM-dd HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yyyy-dd-MM HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "dd/MM/yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "MM/dd/yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yyyy/MM/dd HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yyyy/dd/MM HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "t" + "{1}" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "dd-MM-yyyy'T'HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "t" + "{1}" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "MM-dd-yyyy'T'HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "t" + "{1}" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yyyy-MM-dd'T'HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "t" + "{1}" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yyyy-dd-MM'T'HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "dd MM yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "MM dd yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yyyy MM dd HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yyyy dd MM HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_SPACE + "$", "dd MM yyyy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_SPACE + "$", "MM dd yyyy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_SPACE + "$", "yyyy MM dd HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_SPACE + "$", "yyyy dd MM HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "dd MMM yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "dd MMMM yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "MMM dd yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "MMMM dd yyyy HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "yyyy MMM dd HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "yyyy MMMM dd HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "dd MMM yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "dd MMMM yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "MMM dd yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "MMMM dd yyyy HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "yyyy MMM dd HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "yyyy MMMM dd HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "dd MMM yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "dd MMMM yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "MMM dd yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "MMMM dd yyyy HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "yyyy MMM dd HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "yyyy MMMM dd HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "dd MMM yyyy HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "dd MMMM yyyy HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "MMM dd yyyy HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "MMMM dd yyyy HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "yyyy MMM dd HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "yyyy MMMM dd HH mm");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE_SECOND + "$", "yyyyMMddHHmmss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_NO_SPACE_SECOND + "$",
            "yyyyMMdd HHmmss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND + "$",
            "dd-MM-yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND + "$",
            "MM-dd-yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "$",
            "yyyy-MM-dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\/+" + TIME_COLON_SECOND + "$",
            "yyyy-MM-dd/HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "$",
            "yyyy-dd-MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\/+" + TIME_COLON_SECOND + "$",
            "yyyy-dd-MM/HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "t" + "{1}" + TIME_COLON_SECOND + "$",
            "dd-MM-yyyy'T'HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "t" + "{1}" + TIME_COLON_SECOND + "$",
            "MM-dd-yyyy'T'HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND + "$",
            "yyyy-MM-dd'T'HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "t" + "{1}" + TIME_COLON_SECOND + "$",
            "yyyy-dd-MM'T'HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND + "$",
            "dd/MM/yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND + "$",
            "MM/dd/yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "$",
            "yyyy/MM/dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "$",
            "yyyy/dd/MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND + "$",
            "dd MM yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND + "$",
            "MM dd yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "$",
            "yyyy MM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "$",
            "yyyy dd MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "dd-MM-yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "MM-dd-yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yyyy-MM-dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yyyy-dd-MM HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "t" + "{1}" + TIME_COLON_SECOND_AM_PM + "$",
            "dd-MM-yyyy'T'HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "t" + "{1}" + TIME_COLON_SECOND_AM_PM + "$",
            "MM-dd-yyyy'T'HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND_AM_PM + "$",
            "yyyy-MM-dd'T'HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "t" + "{1}" + TIME_COLON_SECOND_AM_PM + "$",
            "yyyy-dd-MM'T'HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "dd/MM/yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "MM/dd/yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yyyy/MM/dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yyyy/dd/MM HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "dd MM yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "MM dd yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yyyy MM dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yyyy dd MM HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "dd-MM-yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "MM-dd-yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yyyy-MM-dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yyyy-dd-MM HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "t" + "{1}" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "dd-MM-yyyy'T'HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "t" + "{1}" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "MM-dd-yyyy'T'HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yyyy-MM-dd'T'HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "t" + "{1}" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yyyy-dd-MM'T'HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "dd/MM/yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "MM/dd/yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yyyy/MM/dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yyyy/dd/MM HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "dd MM yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "MM dd yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yyyy MM dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yyyy dd MM HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_SPACE_SECOND + "$",
            "dd MM yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_SPACE_SECOND + "$",
            "MM dd yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_SPACE_SECOND + "$",
            "yyyy MM dd HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_SPACE_SECOND + "$",
            "yyyy dd MM HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "dd MMM yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "dd MMMM yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "MMM dd yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "MMMM dd yyyy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "yyyy MMM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "yyyy MMMM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "dd MMM yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "dd MMMM yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "MMM dd yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "MMMM dd yyyy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "yyyy MMM dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "yyyy MMMM dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "dd MMM yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "dd MMMM yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "MMM dd yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "MMMM dd yyyy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "yyyy MMM dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "yyyy MMMM dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "dd MMM yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "dd MMMM yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "MMM dd yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "MMMM dd yyyy HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "yyyy MMM dd HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "yyyy MMMM dd HH mm ss");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE_SECOND + REGEX_D1 + "$",
            "yyyyMMddHHmmssS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_NO_SPACE_SECOND + REGEX_D1 + "$",
            "yyyyMMdd HHmmssS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "dd-MM-yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "MM-dd-yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "yyyy-MM-dd HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "\\s+" + DATE_DASH_YEAR_MONTH_DAY + "$",
            "HH:mm:ss.S yyyy-MM-dd");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "yyyy-dd-MM HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_DAY_MONTH_YEAR + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "dd-MM-yyyy'T'HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_MONTH_DAY_YEAR + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "MM-dd-yyyy'T'HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "yyyy-MM-dd'T'HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_DAY_MONTH + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "yyyy-dd-MM'T'HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "dd/MM/yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "MM/dd/yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "yyyy/MM/dd HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "yyyy/dd/MM HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "dd MM yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "MM dd yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "yyyy MM dd HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$",
            "yyyy dd MM HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$",
            "dd MM yyyy HH mm ss S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$",
            "MM dd yyyy HH mm ss S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$",
            "yyyy MM dd HH mm ss S");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$",
            "yyyy dd MM HH mm ss S");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$", "dd MMM yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$", "dd MMMM yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$", "MMM dd yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$", "MMMM dd yyyy HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$", "yyyy MMM dd HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D1 + "$", "yyyy MMMM dd HH:mm:ss.S");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$", "dd MMM yyyy HH mm ss S");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$", "dd MMMM yyyy HH mm ss S");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$", "MMM dd yyyy HH mm ss S");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$", "MMMM dd yyyy HH mm ss S");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$", "yyyy MMM dd HH mm ss S");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D1 + "$", "yyyy MMMM dd HH mm ss S");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + TIME_NO_SPACE_SECOND + REGEX_D3 + "$",
            "yyyyMMddHHmmssSSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_NO_SPACE_SECOND + REGEX_D3 + "$",
            "yyyyMMdd HHmmssSSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "dd-MM-yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "MM-dd-yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yyyy-MM-dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yyyy-dd-MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_DAY_MONTH_YEAR + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "dd-MM-yyyy'T'HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_MONTH_DAY_YEAR + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "MM-dd-yyyy'T'HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yyyy-MM-dd'T'HH:mm:ss.SSS");
        // ISO 8601 Data elements and interchange formats
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D3 + TIME_ZONE + "$",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_DAY_MONTH + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yyyy-dd-MM'T'HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "dd/MM/yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "MM/dd/yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yyyy/MM/dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yyyy/dd/MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "dd MM yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "MM dd yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yyyy MM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yyyy dd MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$",
            "dd MM yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$",
            "MM dd yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_MONTH_DAY + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$",
            "yyyy MM dd HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_DAY_MONTH + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$",
            "yyyy dd MM HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "dd MMM yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "dd MMMM yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "MMM dd yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "MMMM dd yyyy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "yyyy MMM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "yyyy MMMM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "dd MMM yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "dd MMMM yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "MMM dd yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "MMMM dd yyyy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "yyyy MMM dd HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "yyyy MMMM dd HH mm ss SSS");

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
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "$",
            "dd MMM yy");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "$",
            "dd MMMM yy");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "$",
            "MMM dd yy");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "$",
            "MMMM dd yy");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "$",
            "yy MMM dd");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "$",
            "yy MMMM dd");

        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_NO_SPACE + "$",
            "yyMMdd HHmm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON + "$", "dd-MM-yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON + "$", "MM-dd-yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON + "$", "yy-MM-dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON + "$", "yy-dd-MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON + "$",
            "dd-MM-yy'T'HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON + "$",
            "MM-dd-yy'T'HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "t" + "{1}" + TIME_COLON + "$",
            "yy-MM-dd'T'HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "t" + "{1}" + TIME_COLON + "$",
            "yy-dd-MM'T'HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON + "$",
            "dd/MM/yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON + "$",
            "MM/dd/yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON + "$",
            "yy/MM/dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON + "$",
            "yy/dd/MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON + "$",
            "dd MM yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON + "$",
            "MM dd yy HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON + "$",
            "yy MM dd HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON + "$",
            "yy dd MM HH:mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM + "$",
            "dd-MM-yy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM + "$",
            "MM-dd-yy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM + "$",
            "yy-MM-dd HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM + "$",
            "yy-dd-MM HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_AM_PM + "$",
            "dd-MM-yy'T'HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_AM_PM + "$",
            "MM-dd-yy'T'HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "t" + "{1}" + TIME_COLON_AM_PM + "$",
            "yy-MM-dd'T'HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "t" + "{1}" + TIME_COLON_AM_PM + "$",
            "yy-dd-MM'T'HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM + "$",
            "dd/MM/yy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM + "$",
            "MM/dd/yy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM + "$",
            "yy/MM/dd HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM + "$",
            "yy/dd/MM HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM + "$",
            "dd MM yy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM + "$",
            "MM dd yy HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM + "$",
            "yy MM dd HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM + "$",
            "yy dd MM HH:mm a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "dd-MM-yy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "MM-dd-yy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yy-MM-dd HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yy-dd-MM HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "dd-MM-yy'T'HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "MM-dd-yy'T'HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "t" + "{1}" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yy-MM-dd'T'HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "t" + "{1}" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yy-dd-MM'T'HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "dd/MM/yy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "MM/dd/yy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yy/MM/dd HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yy/dd/MM HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "dd MM yy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "MM dd yy HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yy MM dd HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_AM_PM_NO_SPACE + "$",
            "yy dd MM HH:mma");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_SPACE + "$",
            "dd MM yy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_SPACE + "$",
            "MM dd yy HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_SPACE + "$",
            "yy MM dd HH mm");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_SPACE + "$",
            "yy dd MM HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "dd MMM yy HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "dd MMMM yy HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "MMM dd yy HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "MMMM dd yy HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "yy MMM dd HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON + "$", "yy MMMM dd HH:mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "dd MMM yy HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "dd MMMM yy HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "MMM dd yy HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "MMMM dd yy HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "yy MMM dd HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM + "$", "yy MMMM dd HH:mm a");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "dd MMM yy HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "dd MMMM yy HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "MMM dd yy HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "MMMM dd yy HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "yy MMM dd HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_AM_PM_NO_SPACE + "$", "yy MMMM dd HH:mma");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "dd MMM yy HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "dd MMMM yy HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "MMM dd yy HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "MMMM dd yy HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "yy MMM dd HH mm");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE + "$", "yy MMMM dd HH mm");


        DATE_FORMAT_REGEXPS.put("^" + DATE_NO_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_NO_SPACE_SECOND + "$",
            "yyMMdd HHmmss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "$",
            "dd-MM-yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "$",
            "MM-dd-yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "$",
            "yy-MM-dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "$",
            "yy-dd-MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_SECOND + "$",
            "dd-MM-yy'T'HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_SECOND + "$",
            "MM-dd-yy'T'HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND + "$",
            "yy-MM-dd'T'HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "t" + "{1}" + TIME_COLON_SECOND + "$",
            "yy-dd-MM'T'HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "$",
            "dd/MM/yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "$",
            "MM/dd/yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "$",
            "yy/MM/dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "$",
            "yy/dd/MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "$",
            "dd MM yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "$",
            "MM dd yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "$",
            "yy MM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "$",
            "yy dd MM HH:mm:ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "dd-MM-yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "MM-dd-yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yy-MM-dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yy-dd-MM HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_SECOND_AM_PM + "$",
            "dd-MM-yy'T'HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_SECOND_AM_PM + "$",
            "MM-dd-yy'T'HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND_AM_PM + "$",
            "yy-MM-dd'T'HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "t" + "{1}" + TIME_COLON_SECOND_AM_PM + "$",
            "yy-dd-MM'T'HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "dd/MM/yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "MM/dd/yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yy/MM/dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yy/dd/MM HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "dd MM yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "MM dd yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yy MM dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM + "$",
            "yy dd MM HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "dd-MM-yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "MM-dd-yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yy-MM-dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yy-dd-MM HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "dd-MM-yy'T'HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "MM-dd-yy'T'HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yy-MM-dd'T'HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "t" + "{1}" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yy-dd-MM'T'HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "dd/MM/yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "MM/dd/yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yy/MM/dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yy/dd/MM HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "dd MM yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "MM dd yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yy MM dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$",
            "yy dd MM HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_SPACE_SECOND + "$",
            "dd MM yy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_SPACE_SECOND + "$",
            "MM dd yy HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_SPACE_SECOND + "$",
            "yy MM dd HH mm ss");
        DATE_FORMAT_REGEXPS.put("^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_SPACE_SECOND + "$",
            "yy dd MM HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "dd MMM yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "dd MMMM yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "MMM dd yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "MMMM dd yy HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "yy MMM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "$", "yy MMMM dd HH:mm:ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "dd MMM yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "dd MMMM yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "MMM dd yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "MMMM dd yy HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "yy MMM dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM + "$", "yy MMMM dd HH:mm:ss a");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "dd MMM yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "dd MMMM yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "MMM dd yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "MMMM dd yy HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "yy MMM dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND_AM_PM_NO_SPACE + "$", "yy MMMM dd HH:mm:ssa");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "dd MMM yy HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "dd MMMM yy HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "MMM dd yy HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "MMMM dd yy HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "yy MMM dd HH mm ss");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "$", "yy MMMM dd HH mm ss");

        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_NO_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_NO_SPACE_SECOND + REGEX_D3 + "$",
            "yyMMdd HHmmssSSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "dd-MM-yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "MM-dd-yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yy-MM-dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yy-dd-MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_DAY_MONTH_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "dd-MM-yy'T'HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_MONTH_DAY_YEAR_TWO_DIGIT + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "MM-dd-yy'T'HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_MONTH_DAY + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yy-MM-dd'T'HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_DASH_YEAR_TWO_DIGIT_DAY_MONTH + "t" + "{1}" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yy-dd-MM'T'HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SLASH_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "dd/MM/yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SLASH_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "MM/dd/yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SLASH_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yy/MM/dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SLASH_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yy/dd/MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "dd MM yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "MM dd yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yy MM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$",
            "yy dd MM HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_DAY_MONTH_YEAR_TWO_DIGIT + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$",
            "dd MM yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_MONTH_DAY_YEAR_TWO_DIGIT + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$",
            "MM dd yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_YEAR_TWO_DIGIT_MONTH_DAY + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$",
            "yy MM dd HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DATE_SPACE_YEAR_TWO_DIGIT_DAY_MONTH + "\\s+" + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$",
            "yy dd MM HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "dd MMM yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "dd MMMM yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "MMM dd yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "MMMM dd yy HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "yy MMM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_COLON_SECOND + "\\." + REGEX_D3 + "$", "yy MMMM dd HH:mm:ss.SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "dd MMM yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + DAY_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "dd MMMM yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "MMM dd yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+" + YEAR_TWO_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "MMMM dd yy HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_SHORT + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "yy MMM dd HH mm ss SSS");
        DATE_FORMAT_REGEXPS.put(
            "^" + YEAR_TWO_DIGIT + "{1}" + "\\s+" + MONTH_NAME_LONG + "{1}" + "\\s+" + DAY_DIGIT + "{1}" + "\\s+"
            + TIME_SPACE_SECOND + "\\s+" + REGEX_D3 + "$", "yy MMMM dd HH mm ss SSS");

        DATE_FORMAT_PATTERN_STORE = DATE_FORMAT_REGEXPS;
    }
}
