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
import org.apromore.apmlog.stats.CustomTriple;
import org.apromore.apmlog.util.Util;
import org.apromore.calendar.builder.CalendarModelBuilder;
import org.apromore.calendar.model.CalendarModel;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CusCalArcDurFilterTestSupport {

    public static void run(APMLog apmLog) throws Exception {

        // remove calendar
        apmLog.setCalendarModel(null);

        CustomTriple customTriple = new CustomTriple(apmLog.get(1).getActivityInstances().get(0),
                apmLog.get(1).getActivityInstances().get(1), "concept:name");

        assertEquals("3 d", Util.durationStringOf(customTriple.getDuration()));

        // add calendar
        CalendarModelBuilder caleBuilder = new CalendarModelBuilder();
        CalendarModel calendarModel = caleBuilder.with5DayWorking().build();
        apmLog.setCalendarModel(calendarModel);

        assertEquals("24 hrs", Util.durationStringOf(customTriple.getDuration()));

        FilterType filterType = FilterType.ATTRIBUTE_ARC_DURATION;
        String attrKey = "concept:name";
        String attrVal1 = "A";
        String attrVal2 = "B";

        RuleValue priRuleValue1 = new RuleValue(filterType, OperationType.FROM, attrKey, attrVal1);
        RuleValue priRuleValue2 = new RuleValue(filterType, OperationType.TO, attrKey, attrVal2);
        priRuleValue1.getCustomAttributes().put("base", "true");

        Set<RuleValue> primaryValues = new HashSet<>(Arrays.asList(priRuleValue1, priRuleValue2));

        RuleValue secRuleVal1 = new RuleValue(filterType, OperationType.GREATER_EQUAL,
                "B", 4.8040336134E7);
        secRuleVal1.getCustomAttributes().put("unit", "Hours");

        RuleValue secRuleVal2 = new RuleValue(filterType, OperationType.LESS_EQUAL,
                "B", 8.64E7);
        secRuleVal2.getCustomAttributes().put("unit", "Hours");

        Set<RuleValue> secValues = new HashSet<>();

        secValues.add(secRuleVal1);
        secValues.add(secRuleVal2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                FilterType.ATTRIBUTE_ARC_DURATION, attrKey,
                primaryValues, secValues);

        List<LogFilterRule> criteria = Arrays.asList(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(criteria);
        APMLog filteredLog = apmLogFilter.getAPMLog();

        assertEquals(1, filteredLog.size());
    }

}
