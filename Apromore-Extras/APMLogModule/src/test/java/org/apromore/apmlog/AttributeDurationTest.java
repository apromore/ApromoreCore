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
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AttributeDurationTest {
    public static void testRetainAttributeDuration1(APMLog apmLog, APMLogUnitTest parent)
            throws UnsupportedEncodingException, EmptyInputException {
        FilterType filterType = FilterType.EVENT_ATTRIBUTE_DURATION;
        Choice choice =  Choice.RETAIN;
        Inclusion inclusion = Inclusion.ANY_VALUE;

        double lowBoundVal = 1000 * 60 * 60 * 24d;
        double upBoundVal = 1000 * 60 * 60 * 36d;

        String lowBoundUnit = "days";
        String upBoundUnit = "days";

        Set<RuleValue> primaryValues = new HashSet<RuleValue>();

        String attrKey = "org:resource";
        String attrVal = "R1";

        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrVal, lowBoundVal);

        ruleValue1.getCustomAttributes().put("unit", lowBoundUnit);

        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrVal, upBoundVal);

        ruleValue2.getCustomAttributes().put("unit", upBoundUnit);

        primaryValues.add(ruleValue1);
        primaryValues.add(ruleValue2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, null);

        List<LogFilterRule> rules = new ArrayList<>();
        rules.add(logFilterRule);

        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(rules);

        List<ATrace> traceList = apmLogFilter.getAPMLog().getTraces();

        assertTrue(traceList.size() == 1);
        assertTrue(traceList.get(0).getCaseId().equals("1"));
    }
}
