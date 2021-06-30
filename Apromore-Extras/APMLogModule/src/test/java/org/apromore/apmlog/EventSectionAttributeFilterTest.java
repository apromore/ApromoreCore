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
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.eclipse.collections.impl.map.immutable.ImmutableUnifiedMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class EventSectionAttributeFilterTest {

    public static void testResource(APMLog apmLog, APMLogUnitTest parent) throws Exception {
        String attrKey = "org:resource";
        Set<RuleValue> primaryValues = new HashSet<>();
        primaryValues.add(new RuleValue(
                FilterType.EVENT_EVENT_ATTRIBUTE, OperationType.EQUAL, attrKey, new HashSet<>(Arrays.asList("r2"))));
        UnifiedSet<String> expectedCaseIds = new UnifiedSet<>();
        expectedCaseIds.add("c2");
        expectedCaseIds.add("c3");
        runTest(apmLog, parent, FilterType.EVENT_EVENT_ATTRIBUTE,
                Inclusion.ANY_VALUE, attrKey, primaryValues, expectedCaseIds);
    }

    public static void testActivity(APMLog apmLog, APMLogUnitTest parent) throws Exception {
        String attrKey = "concept:name";
        Set<RuleValue> primaryValues = new HashSet<>();
        primaryValues.add(new RuleValue(
                FilterType.EVENT_EVENT_ATTRIBUTE, OperationType.EQUAL, attrKey, new HashSet<>(Arrays.asList("a", "c"))));
        UnifiedSet<String> expectedCaseIds = new UnifiedSet<>();
        expectedCaseIds.add("c1");
        expectedCaseIds.add("c2");
        expectedCaseIds.add("c3");
        runTest(apmLog, parent, FilterType.EVENT_EVENT_ATTRIBUTE,
                Inclusion.ANY_VALUE, attrKey, primaryValues, expectedCaseIds);
    }

    private static void runTest(APMLog apmLog, APMLogUnitTest parent, FilterType filterType,
                                Inclusion inclusion,
                                String attrKey,
                                Set<RuleValue> primaryValues,
                                UnifiedSet<String> expectedCaseIds) throws Exception {

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, inclusion, Section.EVENT,
                filterType, attrKey, primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();

        UnifiedMap<String, Boolean> expectedIdMatch = new UnifiedMap<>();

        for (String caseId : expectedCaseIds) {
            expectedIdMatch.put(caseId, false);
        }

        UnifiedSet<String> resultCaseIds = new UnifiedSet<>();

        System.out.println("Trace size:" + traceList.size());

        for (ATrace trace : traceList) {
            System.out.println(trace.getCaseId());
            resultCaseIds.add(trace.getCaseId());
        }

        boolean allMatch = true;

        for (String caseId : resultCaseIds) {
            if (!expectedCaseIds.contains(caseId)) allMatch = false;
        }

        for (String caseId : expectedCaseIds) {
            if (!resultCaseIds.contains(caseId)) allMatch = false;
        }

        if (!allMatch) {
            throw new AssertionError("TEST FAILED. RESULT TRACE LIST MISMATCH.");
        } else {
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (RuleValue rv : primaryValues) {
                sb.append(rv.getOperationType().toString());
                if (primaryValues.size() > 1 && count < primaryValues.size() - 1) {
                    sb.append(" + ");
                }
                count += 1;
            }
            parent.printString("'Rework & Repetition - "+sb+"' Test PASS.");
        }
    }

    public static void testEventAttrFreqAfterEventAttrFilter(APMLog apmLog, APMLogUnitTest parent) throws UnsupportedEncodingException, EmptyInputException {
        Set<RuleValue> primaryValues = new HashSet<>();

        Set<String> selectedVals = new HashSet<>(Arrays.asList("Turning & Milling Q.C.", "Laser Marking - Machine 7"));

        primaryValues.add(new RuleValue(FilterType.EVENT_EVENT_ATTRIBUTE, OperationType.EQUAL,
                "concept:name", selectedVals));

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.EVENT,
                FilterType.EVENT_EVENT_ATTRIBUTE, "concept:name", primaryValues, null);

        List<LogFilterRule> rules = Collections.singletonList(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);
        APMLog filteredLog = apmLogFilter.getAPMLog();

        ImmutableUnifiedMap<String, UnifiedSet<EventAttributeValue>> eavMap = filteredLog.getImmutableEventAttributeValues();

        int actSizeOfCFM = eavMap.get("concept:name").size();

        assertEquals(2, actSizeOfCFM);
    }
}
