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
package org.apromore.apmlog.filter.validation.typevalidation;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.types.Section;
import org.apromore.apmlog.filter.validation.FilterRuleValidator;
import org.apromore.apmlog.filter.validation.FilterRuleValidatorTest;
import org.apromore.apmlog.filter.validation.ValidatedFilterRule;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class TimeframeValidatorTest extends FilterRuleValidatorTest {

    @Test
    void validateTimeframe() throws Exception {
        FilterType filterType = FilterType.CASE_TIME;
        String attrKey = "case:timeframe";
        long from = 1306087161211L;
        long to = 1306277370922L;
        RuleValue rv1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrKey, from);
        RuleValue rv2 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrKey, to);
        Set<RuleValue> primaryValues = new HashSet<RuleValue>();
        primaryValues.add(rv1);
        primaryValues.add(rv2);

        LogFilterRule rule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                FilterType.CASE_TIME, "case:timeframe",
                primaryValues, null);
        List<LogFilterRule> criteria = List.of(rule);
        APMLog apmLog = getLog("5 cases EFollow MOD2.xes");
        List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertTrue(validatedRules.get(0).isSubstituted());
    }
}