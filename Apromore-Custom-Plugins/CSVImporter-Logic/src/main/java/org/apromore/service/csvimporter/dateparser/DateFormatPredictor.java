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

import com.github.sisyphsu.retree.ReMatcher;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.nio.CharBuffer.wrap;

public class DateFormatPredictor {

    private final ReMatcher matcher;
    private final DateBuilder dt = new DateBuilder();

    private final List<String> rules;
    private final Set<String> standardRules;
    private final Map<String, RuleHandler> customizedRuleMap;

    private String input;
    private boolean preferMonthFirst;

    private StringBuilder dateTimeFormat;

    DateFormatPredictor(List<String> rules, Set<String> stdRules, Map<String, RuleHandler> cstRules, boolean preferMonthFirst) {
        this.rules = rules;
        this.standardRules = stdRules;
        this.customizedRuleMap = cstRules;
        this.preferMonthFirst = preferMonthFirst;
        this.matcher = new ReMatcher(this.rules.toArray(new String[0]));
    }

    /**
     * Create an new DateParserBuilder which could be used for initialize DateParser.
     *
     * @return DateParserBuilder instance
     */
    public static DateParserBuilder newBuilder() {
        return new DateParserBuilder();
    }

    /**
     * If parser cannot distinguish the dd/mm and mm/dd, preferMonthFirst will help it determine.
     *
     * @param preferMonthFirst Prefer dd/mm or mm/dd
     */
    public void setPreferMonthFirst(boolean preferMonthFirst) {
        this.preferMonthFirst = preferMonthFirst;
    }

    public boolean getPreferMonthFirst() {
        return this.preferMonthFirst;
    }

    /**
     * Parse the specified String into Date
     *
     * @param str The original String like '2019-10-01 00:10:20 +0800'
     * @return The parsed Date
     */
    public Date parseDate(String str) {
        this.dt.reset();
        this.input = str;
        this.parse(buildInput(str));
        return dt.toDate();
    }

    /**
     * Parse the specified String into Calendar
     *
     * @param str The original String like '2019-10-01 00:10:20 +0800'
     * @return The parsed Calendar
     */
    public String parseCalendar(String str) {
        dateTimeFormat = new StringBuilder(str);
        this.dt.reset();
        this.input = str;
        this.parse(buildInput(str));
        return dateTimeFormat.toString();
    }

    /**
     * Parse the specified string into LocalDateTime
     *
     * @param str The original String
     * @return The parsed LocalDateTime
     */
    public LocalDateTime parseDateTime(String str) {
        this.dt.reset();
        this.input = str;
        this.parse(buildInput(str));
        return dt.toLocalDateTime();
    }

    /**
     * Parse the specified string into OffsetDateTime
     *
     * @param str The original String
     * @return The parsed OffsetDateTime
     */
    public OffsetDateTime parseOffsetDateTime(String str) {
        this.dt.reset();
        this.input = str;
        this.parse(buildInput(str));
        return dt.toOffsetDateTime();
    }

    /**
     * Execute datetime's parsing
     */
    private void parse(final DateFormatPredictor.CharArray input) {
        matcher.reset(input);
        int offset = 0;
        int oldEnd = -1;
        while (matcher.find(offset)) {
            if (oldEnd == matcher.end()) {
                throw error(offset, "empty matching at " + offset);
            }
            if (standardRules.contains(matcher.re())) {
                this.parseStandard(input, offset);
            } else {
                RuleHandler handler = customizedRuleMap.get(matcher.re());
                handler.handle(input, matcher, dt);
            }
            offset = matcher.end();
            oldEnd = offset;
        }
        if (offset != input.length()) {
            throw error(offset);
        }
    }

    /**
     * Parse datetime use standard rules.
     */
    void parseStandard(DateFormatPredictor.CharArray input, int offset) {
        for (int index = 1; index <= matcher.groupCount(); index++) {
            final String groupName = matcher.groupName(index);
            final int startOff = matcher.start(index);
            final int endOff = matcher.end(index);

            System.out.println("groupName " + groupName);
            System.out.println("startOff " + startOff);
            System.out.println("endOff " + endOff);
            if (groupName == null) {
                throw error(offset, "Hit invalid standard rule: " + matcher.re());
            }
            if (startOff == -1 && endOff == -1) {
                continue;
            }
            switch (groupName) {
                case "week":
                    parseWeek(input, startOff, endOff);
                    break;
                case "year":
                    parseYear(input, startOff, endOff);
                    break;
                case "month":
                    parseMonth(input, startOff, endOff);
                    break;
                case "day":
                    dateTimeFormat.replace(startOff, endOff, "dd");
                    break;
                case "hour":
                    dateTimeFormat.replace(startOff, endOff, "HH");
                    break;
                case "minute":
                    dateTimeFormat.replace(startOff, endOff, "mm");
                    break;
                case "second":
                    dateTimeFormat.replace(startOff, endOff, "ss");
                    break;
                case "ns":
                    parseNano(startOff, endOff);
                    break;
                case "m":
                    dateTimeFormat.replace(startOff, endOff, "a");
                    break;
                case "zero":
                    dateTimeFormat.replace(startOff, endOff, "Z");
                    break;
                case "zoneOffset":
                    dateTimeFormat.replace(startOff, endOff, "X");
                    break;
                case "zoneName":
                    dateTimeFormat.replace(startOff, endOff, "z");
                    break;
                case "dayOrMonth":
                    parseDayOrMonth(input, startOff, endOff);
                    break;
                case "unixsecond":
                    dateTimeFormat.append("unixsecond");
                    break;
                case "millisecond":
                    dateTimeFormat.append("millisecond");
                    break;
                case "microsecond":
                    dateTimeFormat.append("microsecond");
                    break;
                case "nanosecond":
                    dateTimeFormat.append("nanosecond");
                    break;
                default:
                    throw error(offset, "Hit invalid standard rule: " + matcher.re());
            }
        }
    }

    /**
     * Parse an subsequence which represent dd/mm or mm/dd, it should be more smart for different locales.
     */
    void parseDayOrMonth(DateFormatPredictor.CharArray input, int from, int to) {
        System.out.println("from " + from);
        System.out.println("to " + to);
        char next = input.data[from + 1];
        int a, b;
        if (next < '0' || next > '9') {
            a = parseNum(input, from, from + 1);
            b = parseNum(input, from + 2, to);
        } else {
            a = parseNum(input, from, from + 2);
            b = parseNum(input, from + 3, to);
        }
        if (a > 31 || b > 31 || a == 0 || b == 0 || (a > 12 && b > 12)) {
            throw error(from, "Invalid DayOrMonth at " + from);
        }
        if (a > 12) {
            this.preferMonthFirst = false;
            dateTimeFormat.replace(from, from + 2, "dd");
            dateTimeFormat.replace(from + 3, to, "MM");
        }
        if (b > 12 || preferMonthFirst) {
            this.preferMonthFirst = true;
            dateTimeFormat.replace(from, from + 2, "MM");
            dateTimeFormat.replace(from + 3, to, "dd");
        } else {
            dateTimeFormat.replace(from, from + 2, "dd");
            dateTimeFormat.replace(from + 3, to, "MM");
        }
    }

    /**
     * Parse an subsequence which represent year, like '2019', '19' etc
     */
    void parseYear(DateFormatPredictor.CharArray input, int from, int to) {
        int len = to - from;
        if (len == 4) {
            dateTimeFormat.replace(from, to, "yyyy");
        } else if (len == 2) {
            dateTimeFormat.replace(from, to, "yy");
        } else {
            throw error(from, "Invalid year at " + from);
        }
    }

    /**
     * Parse an subsequence which suffix second, like '.2000', '.3186369', '.257000000' etc
     * It should be treated as ms/us/ns.
     */
    void parseNano(int from, int to) {
        int len = to - from;
        for (int i = 0; i < len; i++) {
            dateTimeFormat.replace(from, to, "S");
        }
    }

    /**
     * Parse an subsequence which represent week, like 'Monday', 'mon' etc
     */
    void parseWeek(DateFormatPredictor.CharArray input, int from, int to) {
        if (from - to > 3) {
            dateTimeFormat.replace(from, to, "EEEE");
        } else {
            dateTimeFormat.replace(from, to, "EEE");
        }
    }

    /**
     * Parse an subsequence which represent month, like '12', 'Feb' etc
     */
    void parseMonth(DateFormatPredictor.CharArray input, int from, int to) {
        int numOfChar = to - from;
        if (numOfChar <= 2) {
            dateTimeFormat.replace(from, to, "MM");
        } else if (numOfChar == 3) {
            dateTimeFormat.replace(from, to, "MMM");
        } else if (numOfChar > 3) {
            dateTimeFormat.replace(from, to, "MMMM");
        } else {
            throw error(from, "Invalid month at " + from);
        }
    }

    private DateTimeParseException error(int offset) {
        return error(offset, String.format("Text %s cannot parse at %d", input, offset));
    }

    private DateTimeParseException error(int offset, String msg) {
        return new DateTimeParseException(msg, input, offset);
    }

    /**
     * Parse an subsequence which represent an number, like '1234'
     */
    static int parseNum(DateFormatPredictor.CharArray input, int from, int to) {
        int num = 0;
        for (int i = from; i < to; i++) {
            num = num * 10 + (input.data[i] - '0');
        }
        return num;
    }

    static DateFormatPredictor.CharArray buildInput(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (ch >= 'A' && ch <= 'Z') {
                chars[i] = (char) (ch + 32);
            }
        }
        return new DateFormatPredictor.CharArray(chars);
    }

    private static class CharArray implements CharSequence {

        char[] data;

        public CharArray(char[] data) {
            this.data = data;
        }

        @Override
        public int length() {
            return data.length;
        }

        @Override
        public char charAt(int index) {
            return data[index];
        }

        @Override
        public CharSequence subSequence(int start, int end) {

            return wrap(Arrays.copyOfRange(data, start, end));
        }
    }

    private static int[] NSS = {100000000, 10000000, 1000000, 100000, 10000, 1000, 100, 10, 1};
}
