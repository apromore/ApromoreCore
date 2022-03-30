/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.apmlog.filter;

import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.logobjects.ImmutableLog;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Set;
import java.util.stream.Collectors;

class APMLogFilterTest {

    @Test
    void filterCaseVariant() throws Exception {
        ImmutableLog log = APMLogUnitTest.getImmutableLog("Production_Data(2021)", "files/Production_Data(2021).xes");
        APMLogFilter apmLogFilter = new APMLogFilter(log);
        List<LogFilterRule> criteria = new ArrayList<>(Arrays.asList(getCaseVariantRule(1, 49), getCaseVariantRule(11, 40)));
        apmLogFilter.filter(criteria);
        assertEquals(30, apmLogFilter.getPLog().size());
        criteria.add(getCaseVariantRule(6, 10));
        apmLogFilter.filter(criteria);
        assertEquals(5, apmLogFilter.getPLog().size());
    }

    private LogFilterRule getCaseVariantRule(int fromVariant, int toVariant) {
        Set<RuleValue> primaryValues = new HashSet<>();
        for (int i = fromVariant; i <= toVariant; i++) {
            primaryValues.add(new RuleValue(FilterType.CASE_VARIANT, OperationType.EQUAL,
                    "case:variant", i));
        }

        return new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ALL_VALUES, Section.CASE,
                FilterType.CASE_VARIANT, "case:variant",
                primaryValues, null);
    }

    LogFilterRule getEventTimeFilterRule() {
        FilterType filterType = FilterType.EVENT_TIME;
        String attrKey = "event:timeframe";

        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, 1306477508227L);
        RuleValue rv2 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, 1310270958993L);
        Set<RuleValue> primaryValues = Set.of(rv1, rv2);

        return new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, attrKey,
                primaryValues, null);
    }

    @Test
    void getTimeFilteredActivityInstances() throws Exception {
        ImmutableLog log = APMLogUnitTest.getImmutableLog("p2ps", "files/p2ps.xes.gz");
        PLog pLog = new PLog(log);

        LogFilterRule rule = getEventTimeFilterRule();
        List<ActivityInstance> actList = APMLogFilter.getTimeFilteredActivityInstances(rule, pLog.getPTraces());
        long minTime = actList.stream().collect(Collectors.summarizingLong(ActivityInstance::getStartTime)).getMin();
        long maxTime = actList.stream().collect(Collectors.summarizingLong(ActivityInstance::getEndTime)).getMax();
        assertEquals(567, actList.size());
        assertEquals(1306510500000L, minTime);
        assertEquals(1310248500000L, maxTime);

        APMLogFilter apmLogFilter = new APMLogFilter(log);
        List<LogFilterRule> criteria = List.of(rule);
        apmLogFilter.filter(criteria);
        pLog = apmLogFilter.getPLog();
        assertEquals(75, pLog.size());
        assertEquals(567, pLog.getActivityInstances().size());
        assertEquals(1306510500000L, pLog.getStartTime());
        assertEquals(1310248500000L, pLog.getEndTime());
    }
}