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
package org.apromore.apmlog;

import org.apromore.apmlog.exceptions.EmptyInputException;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.apromore.apmlog.stats.TimeStatsProcessor;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import static org.apromore.apmlog.filter.types.FilterType.EVENT_EVENT_ATTRIBUTE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class CaseStatsTest {

    public static void testCaseDurationAfterEventAttrFilter(APMLog apmLog) throws UnsupportedEncodingException, EmptyInputException {

        String key = "concept:name";
        Set<String> selActVals = new HashSet<>(Arrays.asList("a", "c"));
        RuleValue rv2 = new RuleValue(EVENT_EVENT_ATTRIBUTE, OperationType.EQUAL, key, selActVals);
        Set<RuleValue> primaryValues2 = new HashSet<>(Arrays.asList(rv2));

        LogFilterRule logFilterRule2 = new LogFilterRuleImpl(Choice.REMOVE, Inclusion.ANY_VALUE, Section.EVENT,
                EVENT_EVENT_ATTRIBUTE, key, primaryValues2, null);

        List<LogFilterRule> criteria = Arrays.asList(logFilterRule2);
        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(criteria);
        APMLog filteredLog = apmLogFilter.getAPMLog();


        DoubleArrayList caseDurs = TimeStatsProcessor.getCaseDurations(filteredLog.getTraces());

        double[] expected = {900000.0,
                1200000.0,
                1700000.0,
                3000000.0,
                5100000.0};

        double[] result = {caseDurs.min(), caseDurs.median(), caseDurs.average(), caseDurs.max(), caseDurs.sum()};

        assertArrayEquals(expected, result, 0);
    }

}
