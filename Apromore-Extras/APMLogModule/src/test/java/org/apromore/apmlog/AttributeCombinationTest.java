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
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttributeCombinationTest {

    public static void testRetainEventEvent1(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.CASE_SECTION_ATTRIBUTE_COMBINATION;
        Choice choice =  Choice.RETAIN;
        Inclusion inclusion = Inclusion.ANY_VALUE;

        String section1 = "event";
        String section2 = "event";

        String firstKey = "org:resource";
        String secondKey = "concept:name";

        Set<RuleValue> firstValues = new HashSet<>();
        RuleValue rv1 = new RuleValue(filterType, OperationType.EQUAL, firstKey, "R2");
        rv1.getCustomAttributes().put("section", section1);
        firstValues.add(rv1);

        Set<RuleValue> secondaryValues = new HashSet<>();
        RuleValue rv2 = new RuleValue(filterType, OperationType.EQUAL, secondKey, new HashSet<>(Arrays.asList("B")));

        rv2.getCustomAttributes().put("section", section2);
        secondaryValues.add(rv2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, firstKey,
                firstValues, secondaryValues);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("1")) hasC1 = true;
            if (trace.getCaseId().equals("2")) hasC2 = true;
            if (trace.getCaseId().equals("3")) hasC3 = true;
            System.out.println(trace.getCaseId());
        }

        if (!hasC1 || hasC2 || !hasC3) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.\n");
        } else {
            parent.printString("'Attribute combination: eventAttr > eventAttr (1)' test PASS.\n");
        }
    }

    public static void testRetainEventCase1(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.CASE_SECTION_ATTRIBUTE_COMBINATION;
        Choice choice =  Choice.RETAIN;
        Inclusion inclusion = Inclusion.ANY_VALUE;

        String section1 = "event";
        String section2 = "case";

        String firstKey = "concept:name";
        String secondKey = "status";

        Set<RuleValue> firstValues = new HashSet<>();
        RuleValue rv1 = new RuleValue(filterType, OperationType.EQUAL, firstKey, "A");
        rv1.getCustomAttributes().put("section", section1);
        firstValues.add(rv1);

        Set<RuleValue> secondaryValues = new HashSet<>();
        RuleValue rv2 = new RuleValue(filterType, OperationType.EQUAL, secondKey, new HashSet<>(Arrays.asList("completed")));

        rv2.getCustomAttributes().put("section", section2);
        secondaryValues.add(rv2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, firstKey,
                firstValues, secondaryValues);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();
        boolean hasC1 = false;
        boolean hasC2 = false;
        boolean hasC3 = false;

        for (ATrace trace : traceList) {
            if (trace.getCaseId().equals("1")) hasC1 = true;
            if (trace.getCaseId().equals("2")) hasC2 = true;
            if (trace.getCaseId().equals("3")) hasC3 = true;
            System.out.println(trace.getCaseId());
        }

        if (!hasC1 || hasC2 || !hasC3) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.\n");
        } else {
            parent.printString("'Attribute combination: eventAttr > caseAttr (1)' test PASS.\n");
        }
    }
}
