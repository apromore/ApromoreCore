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
package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.filter.APMLogFilter;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.apmlog.xes.XESAttributeCodes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class NodeDurationFilterTest {

    private static final String ATTR_VAL = "Prepare package";

    @Test
    void filter() throws Exception {
        APMLog log5cases = APMLogUnitTest.getImmutableLog("5cases", "files/5cases.xes");
        List<LogFilterRule> criteria = getFilterCriteria(Choice.REMOVE);
        APMLogFilter apmLogFilter = new APMLogFilter(log5cases);
        apmLogFilter.filter(criteria);
        assertEquals(2, apmLogFilter.getAPMLog().size());

        criteria = getFilterCriteria(Choice.RETAIN);
        apmLogFilter.filter(criteria);
        assertEquals(3, apmLogFilter.getAPMLog().size());
    }

    private List<LogFilterRule> getFilterCriteria(Choice choice) {
        FilterType filterType = FilterType.EVENT_ATTRIBUTE_DURATION;
        Inclusion inclusion = Inclusion.ALL_VALUES;

        double lowBoundVal = 0d;
        double upBoundVal = 40 * (1000 * 60);

        String lowBoundUnit = "days";
        String upBoundUnit = "days";

        Set<RuleValue> primaryValues = new HashSet<RuleValue>();

        String attrKey = XESAttributeCodes.CONCEPT_NAME;

        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, ATTR_VAL, lowBoundVal);

        ruleValue1.getCustomAttributes().put("unit", lowBoundUnit);

        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, ATTR_VAL, upBoundVal);

        ruleValue2.getCustomAttributes().put("unit", upBoundUnit);

        primaryValues.add(ruleValue1);
        primaryValues.add(ruleValue2);

        LogFilterRule logFilterRule = new LogFilterRuleImpl(choice, inclusion, Section.CASE,
                filterType, attrKey,
                primaryValues, null);

        return List.of(logFilterRule);
    }
}
