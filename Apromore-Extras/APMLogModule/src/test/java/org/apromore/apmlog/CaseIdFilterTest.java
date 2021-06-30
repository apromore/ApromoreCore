/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
import org.apromore.apmlog.filter.types.*;
import org.apromore.apmlog.util.Util;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;

public class CaseIdFilterTest {
    public static void test1(APMLog originalLog) throws EmptyInputException {

        Choice choice =  Choice.REMOVE;

        BitSet bitSet = new BitSet(originalLog.size());
        bitSet.set(0);
        bitSet.set(1);
        bitSet.set(2);
        bitSet.set(3);

        Set<RuleValue> primaryValues = new HashSet<>();

        RuleValue rv = new RuleValue(FilterType.CASE_ID, OperationType.EQUAL,"case:id", bitSet);
        primaryValues.add(rv);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, Inclusion.ALL_VALUES, Section.CASE,
                FilterType.CASE_ID, "case:id",
                primaryValues, null);

        List<LogFilterRule> rules = Arrays.asList(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(originalLog);
        apmLogFilter.filter(rules);
        APMLog apmLog = apmLogFilter.getAPMLog();

        Object[] expected = new Object[]{"3011"};
        Object[] result = apmLog.getTraces().stream()
                .map(ATrace::getCaseId)
                .toArray();

        assertArrayEquals(expected, result);

    }
}
