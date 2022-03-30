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
package org.apromore.apmlog.customcalendartests;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CusCalCaseDurationFilterTest {

    public static void testCaseDurationFilter01(APMLog apmLog) throws Exception {

        FilterType filterType = FilterType.DURATION;

        double selectedMin = 2.59199999999E8;
        double selectedMax = 2.88E8;

        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, "", selectedMin);
        ruleValue1.setLongVal(Double.valueOf(selectedMin).longValue());
        ruleValue1.getCustomAttributes().put("unit", "Days");

        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, "", selectedMax);
        ruleValue2.setLongVal(Double.valueOf(selectedMax).longValue());
        ruleValue2.getCustomAttributes().put("unit", "Hours");

        Set<RuleValue> primaryValues = new HashSet<>(Arrays.asList(ruleValue1, ruleValue2));

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, "",
                primaryValues, null);

        List<LogFilterRule> criteria = Arrays.asList(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(criteria);
        APMLog filteredLog = apmLogFilter.getAPMLog();

        assertEquals(1, filteredLog.size());
        assertEquals("C1", filteredLog.get(0).getCaseId());
    }

}
