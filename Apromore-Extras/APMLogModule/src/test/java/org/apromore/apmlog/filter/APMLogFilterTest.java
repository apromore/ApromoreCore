/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
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
import org.apromore.apmlog.logobjects.ImmutableLog;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}