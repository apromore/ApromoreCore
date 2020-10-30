/**
 * MIT License
 *
 * Copyright (c) 2019 lin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2020 University of Tartu
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

import java.util.*;

/**
 * Predefine some date parser's rules, and support to customize new rules.
 *
 * @author sulin
 * @since 2019-09-12 14:34:29
 */
final class DateParserBuilder {

    static final String[] months = {
            "january",
            "february",
            "march",
            "april",
            "may",
            "june",
            "july",
            "august",
            "september",
            "october",
            "november",
            "december",
            "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec",
    };
    static final String[] weeks = {
            "monday",
            "tuesday",
            "wednesday",
            "thursday",
            "friday",
            "saturday",
            "sunday",
            "mon", "tue", "wed", "thu", "fri", "sat", "sun",
    };

    static final List<String> STANDARD_RULES = new ArrayList<>();

    static final List<String> CUSTOMIZED_RULES = new ArrayList<>();
    static final Map<String, RuleHandler> CUSTOMIZED_RULE_MAP = new HashMap<>();

    static {
        // support day of week, like 'Mon' or 'Monday,'
        for (String week : weeks) {
            register(String.format("(?<week>%s)\\W*", week));
        }

        for (String month : months) {
            // month-word at first, like 'may. 8th, 2009,' or 'may. 8th, 09'
            register(String.format("(?<month>%s)\\W+(?<day>\\d{1,2})(?:th)?\\W+(?<year>\\d{4})\\b", month));
            register(String.format("(?<month>%s)\\W+(?<day>\\d{1,2})(?:th)?\\W+(?<year>\\d{2})$", month));
            register(String.format("(?<month>%s)\\W+(?<day>\\d{1,2})(?:th)?\\W+(?<year>\\d{2})[^:\\d]", month));
            register(String.format("(?<month>%s)\\W+(?<day>\\d{1,2})(?:th)?\\W*", month));

            // month-word at middle, like '8th, may, 2009,' or '8th-may-09'
            register(String.format("(?<day>\\d{1,2})(?:th)?\\W+(?<month>%s)\\W+(?<year>\\d{4})\\b", month));
            register(String.format("(?<day>\\d{1,2})(?:th)?\\W+(?<month>%s)\\W+(?<year>\\d{2})$", month));
            register(String.format("(?<day>\\d{1,2})(?:th)?\\W+(?<month>%s)\\W+(?<year>\\d{2})[^:\\d]", month));
            register(String.format("(?<day>\\d{1,2})(?:th)?\\W+(?<month>%s)\\W*", month));

            // month-word at middle, like '2009-may-8th'
            register(String.format("(?<year>\\d{4})\\W+(?<month>%s)\\W+(?<day>\\d{1,2})(?:th)?\\W*", month));
        }

        // yyyy-MM-dd, yyyy/MM/dd...
        register("(?<year>\\d{4})\\W{1}(?<month>\\d{1,2})\\W{1}(?<day>\\d{1,2})[^\\d]?");

        // yyyy-MM, yyyy/MM...
        register("^(?<year>\\d{4})\\W{1}(?<month>\\d{1,2})$");

        // MM/dd/yyyy, dd/MM/yyyy
        register("(?<dayOrMonth>\\d{1,2}\\W{1}\\d{1,2})\\W{1}(?<year>\\d{4})[^\\d]?");

        // dd/MM/yy, MM/dd/yy
        register("(?<dayOrMonth>\\d{1,2}[./]\\d{1,2})[./](?<year>\\d{2})$");
        register("(?<dayOrMonth>\\d{1,2}[./]\\d{1,2})[./](?<year>\\d{2})[^:\\d]");

        // yyyy
        register(" ?(?<year>\\d{4})$");
        // yyyyMM
        register("^(?<year>\\d{4})(?<month>\\d{2})$");
        // yyyyMMdd
        register("^(?<year>\\d{4})(?<month>\\d{2})(?<day>\\d{2})$");
        // yyyyMMddhhmmss
        register("^(?<year>\\d{4})(?<month>\\d{2})(?<day>\\d{2})(?<hour>\\d{2})(?<minute>\\d{2})(?<second>\\d{2})$");

        // unixtime(10)
        register("^(?<unixsecond>\\d{10})$");

        // millisecond(13)
        register("^(?<millisecond>\\d{13})$");

        // microsecond(16)
        register("^(?<microsecond>\\d{16})$");

        // nanosecond(19)
        register("^(?<nanosecond>\\d{19})$");

        // at hh:mm:ss.SSSSZ
        register("\\W*(?:at )?(?<hour>\\d{1,2}):(?<minute>\\d{1,2})(?::(?<second>\\d{1,2}))?(?:[.,](?<ns>\\d{1,9}))?(?<zero>z)?");

        // +08:00
        register(" ?(?<zoneOffset>[-+]\\d{1,2}:?(?:\\d{2})?)");

        // 12 o’clock
        register(" ?(?<hour>\\d{1,2}) o’clock\\W*");

        // am, pm
        register(" ?(?<m>am|pm)\\W*");

        // (CEST) (GMT Daylight Time)
        register(" [(](?<zoneName>.+)[)]");

        // support all languages' default TimeZone
        for (String zoneId : TimeZone.getAvailableIDs()) {
            final TimeZone zone = TimeZone.getTimeZone(zoneId);
            final RuleHandler handler = (cs, matcher, dt) -> dt.zone = zone;

            String zoneIdStr = zone.getID().toLowerCase();
            register(String.format(" ?\\Q%s\\E", zoneIdStr), handler);
            register(String.format(" ?\\Q[%s]\\E", zoneIdStr), handler);
        }

        // support others no-standard 'timezone'
        register(" ?pdt", (cs, matcher, dt) -> dt.zone = TimeZone.getTimeZone("PST"));
        register(" ?cest", (cs, matcher, dt) -> dt.zone = TimeZone.getTimeZone("CET"));

        // MSK m=+0.000000001
        register(" msk m=[+-]\\d\\.\\d+");
    }

    static synchronized void register(String re) {
        if (!STANDARD_RULES.contains(re)) {
            STANDARD_RULES.add(re);
        }
    }

    static synchronized void register(String re, RuleHandler handler) {
        if (!CUSTOMIZED_RULE_MAP.containsKey(re)) {
            CUSTOMIZED_RULES.add(re);
        }
        CUSTOMIZED_RULE_MAP.put(re, handler);
    }

    private boolean preferMonthFirst = false;
    private final List<String> rules = new ArrayList<>();
    private final Set<String> standardRules = new HashSet<>();
    private final Map<String, RuleHandler> customizedRuleMap = new HashMap<>();

    DateParserBuilder() {
        // predefined standard rules
        this.rules.addAll(DateParserBuilder.STANDARD_RULES);
        this.standardRules.addAll(DateParserBuilder.STANDARD_RULES);
        // predefined customized rules
        this.rules.addAll(DateParserBuilder.CUSTOMIZED_RULES);
        this.customizedRuleMap.putAll(DateParserBuilder.CUSTOMIZED_RULE_MAP);
    }

    /**
     * Mark this parser prefer mm/dd or not.
     *
     * @param preferMonthFirst True means prefer mm/dd, False means prefer dd/mm.
     * @return This
     */
    public DateParserBuilder preferMonthFirst(boolean preferMonthFirst) {
        this.preferMonthFirst = preferMonthFirst;
        return this;
    }

    /**
     * Add an standard rule which could parse the specified subsequence.
     *
     * @param rule Standard rule which should have some specified groupName
     * @return This
     */
    public DateParserBuilder addRule(String rule) {
        if (!standardRules.contains(rule)) {
            rules.add(rule);
            standardRules.add(rule);
        }
        return this;
    }

    /**
     * Add an customized rule which could parse any subsequence.
     *
     * @param rule    The parsing rule in regex
     * @param handler The parsing callback
     * @return This
     */
    public DateParserBuilder addRule(String rule, RuleHandler handler) {
        if (!customizedRuleMap.containsKey(rule)) {
            rules.add(rule);
        }
        customizedRuleMap.put(rule, handler);
        return this;
    }

    /**
     * Build the final DateParser instance.
     *
     * @return DateParser
     */
    public DateParser build() {
        return new DateParser(rules, standardRules, customizedRuleMap, preferMonthFirst);
    }

}
