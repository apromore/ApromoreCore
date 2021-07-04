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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ProcureToPayAdvFilterTest {

    public static void run(APMLog apmLog, APMLogUnitTest parent) throws EmptyInputException {
        List<LogFilterRule> criteria = Arrays.asList(getRetainActivitiesRule(), getRemoveDirectFollow1(),
                getRemoveDirectFollow2(), getRemoveDirectFollow3());
        APMLogFilter apmLogFilter = new APMLogFilter(apmLog);
        apmLogFilter.filter(criteria);
        APMLog filteredLog = apmLogFilter.getAPMLog();

        assertEquals(242, filteredLog.getTraces().size());
    }

    private static LogFilterRule getRetainActivitiesRule() {
        Choice choice = Choice.RETAIN;
        FilterType filterType = FilterType.EVENT_EVENT_ATTRIBUTE;
        String attrCode = "concept:name";
        Set<String> selectedVals = new HashSet<>(Arrays.asList("Authorize Supplier's Invoice payment",
                "Release Supplier's Invoice"));
        Set<RuleValue> primaryValues = new HashSet<>();
        RuleValue rv1 = new RuleValue(filterType, OperationType.EQUAL, attrCode, selectedVals);
        primaryValues.add(rv1);
        return new LogFilterRuleImpl(choice, Inclusion.ANY_VALUE, Section.EVENT,
                filterType, attrCode, primaryValues, null);
    }

    private static LogFilterRule getRemoveDirectFollow1() {
        return getRemoveDirectFollowResourceRule("Karalda Nimwada", "Pedro Alvares", "org:resource");
    }

    private static LogFilterRule getRemoveDirectFollow2() {
        return getRemoveDirectFollowResourceRule("Pedro Alvares", "Karalda Nimwada", "org:resource");
    }

    private static LogFilterRule getRemoveDirectFollow3() {
        return getRemoveDirectFollowResourceRule("[Start]", "Authorize Supplier's Invoice payment",
                "concept:name");
    }

    private static LogFilterRule getRemoveDirectFollowResourceRule(String from, String to, String key) {
        Choice choice = Choice.REMOVE;
        FilterType filterType = FilterType.DIRECT_FOLLOW;
        Set<RuleValue> primaryValues = new HashSet<>();
        RuleValue ruleValueFrom = new RuleValue(filterType, OperationType.FROM, key, from);
        primaryValues.add(ruleValueFrom);
        RuleValue ruleValueTo = new RuleValue(filterType, OperationType.TO, key, to);
        primaryValues.add(ruleValueTo);
        return new LogFilterRuleImpl(choice, Inclusion.ANY_VALUE, Section.CASE,
                filterType, key,
                primaryValues, null);
    }
}
