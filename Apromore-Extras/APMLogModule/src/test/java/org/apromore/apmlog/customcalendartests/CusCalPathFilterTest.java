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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CusCalPathFilterTest {

    public static void testDirectFollowLessInterval01(APMLog apmLog) throws Exception {

        FilterType filterType = FilterType.DIRECT_FOLLOW;
        String mainKey = "concept:name";

        Set<RuleValue> primaryValues = new HashSet<>();
        primaryValues.add(new RuleValue(filterType, OperationType.FROM, mainKey, "A"));
        primaryValues.add(new RuleValue(filterType, OperationType.TO, mainKey, "B"));

        Set<RuleValue> secondaryValues = new HashSet<>();
        secondaryValues.add(new RuleValue(filterType, OperationType.LESS, mainKey, 287712000d));

        LogFilterRule rule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, mainKey,
                primaryValues, secondaryValues);

        List<LogFilterRule> criteria = Arrays.asList(rule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(criteria);
        APMLog filteredLog = apmLogFilter.getAPMLog();

        assertEquals(1, filteredLog.size());
        assertEquals("C2", filteredLog.get(0).getCaseId());
    }
}
