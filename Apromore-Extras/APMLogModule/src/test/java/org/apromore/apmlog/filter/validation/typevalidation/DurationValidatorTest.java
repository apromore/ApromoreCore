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
import org.apromore.apmlog.xes.XESAttributeCodes;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

class DurationValidatorTest extends FilterRuleValidatorTest {

    private LogFilterRule getFromToRule(FilterType filterType, double from, double to) {

        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, "", from);
        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, "", to);

        Set<RuleValue> primaryValues = Set.of(ruleValue1, ruleValue2);

        return new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, "",
                primaryValues, null);
    }

    @Test
    void validateDoubleValues() throws Exception {
        // =====================================
        // test case utilization validation
        // =====================================
        APMLog logMod = getLog("5 cases EFollow MOD2.xes");
        List<LogFilterRule> criteria = List.of(getFromToRule(FilterType.CASE_UTILISATION, 0.777, 1.0));
        List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, logMod);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        APMLog logEF2 = getLog("5 cases EFollow (2).xes");
        validatedRules = FilterRuleValidator.validate(criteria, logEF2);
        assertFalse(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        // =====================================
        // test case duration validation
        // =====================================
        criteria = List.of(getFromToRule(FilterType.DURATION, 0, 1.8E7));
        validatedRules = FilterRuleValidator.validate(criteria, logMod);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        validatedRules = FilterRuleValidator.validate(criteria, logEF2);
        assertTrue(validatedRules.get(0).isApplicable());
        assertTrue(validatedRules.get(0).isSubstituted());

        // =====================================
        // test total processing time validation
        // =====================================
        criteria = List.of(getFromToRule(FilterType.TOTAL_PROCESSING_TIME, 1.08E7, 1.368E7));
        validatedRules = FilterRuleValidator.validate(criteria, logMod);
        assertTrue(validatedRules.get(0).isApplicable());
        assertTrue(validatedRules.get(0).isSubstituted());

        validatedRules = FilterRuleValidator.validate(criteria, logEF2);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
    }

    @Test
    void validateNodeDuration() throws Exception {
        FilterType filterType = FilterType.EVENT_ATTRIBUTE_DURATION;
        String attrVal = "Prepare package";
        RuleValue ruleValue1 = new RuleValue(filterType, OperationType.GREATER_EQUAL, attrVal, 3000000.0);
        ruleValue1.getCustomAttributes().put("unit", "Minutes");

        RuleValue ruleValue2 = new RuleValue(filterType, OperationType.LESS_EQUAL, attrVal, 6360000.0);
        ruleValue2.getCustomAttributes().put("unit", "Minutes");

        Set<RuleValue> primaryValues = Set.of(ruleValue1, ruleValue2);

        LogFilterRule rule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ALL_VALUES, Section.CASE,
                filterType, XESAttributeCodes.CONCEPT_NAME,
                primaryValues, null);

        List<LogFilterRule> criteria = List.of(rule);

        APMLog logEF2 = getLog("5 cases EFollow (2).xes");
        List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, logEF2);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        APMLog logMod = getLog("5 cases EFollow MOD2.xes");
        validatedRules = FilterRuleValidator.validate(criteria, logMod);
        assertTrue(validatedRules.get(0).isApplicable());
        assertTrue(validatedRules.get(0).isSubstituted());
    }

    @Test
    void validateArcDuration() throws Exception {
        FilterType filterType = FilterType.ATTRIBUTE_ARC_DURATION;
        String attrKey = XESAttributeCodes.CONCEPT_NAME;
        String attrVal1 = "Warehouse check for the order";
        String attrVal2 = "Prepare package";
        String secRuleKey = "Prepare package";

        RuleValue priRuleValue1 = new RuleValue(filterType, OperationType.FROM, attrKey, attrVal1);
        RuleValue priRuleValue2 = new RuleValue(filterType, OperationType.TO, attrKey, attrVal2);

        priRuleValue1.getCustomAttributes().put("base", "true");

        Set<RuleValue> primaryValues = Set.of(priRuleValue1, priRuleValue2);

        RuleValue secRuleVal1 = new RuleValue(filterType, OperationType.GREATER_EQUAL,
                secRuleKey, 1.08E7);
        secRuleVal1.getCustomAttributes().put("unit", "Hours");

        RuleValue secRuleVal2 = new RuleValue(filterType, OperationType.LESS_EQUAL,
                secRuleKey, 6.648E7);
        secRuleVal2.getCustomAttributes().put("unit", "Hours");

        Set<RuleValue> secValues = Set.of(secRuleVal1, secRuleVal2);

        LogFilterRule rule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                FilterType.ATTRIBUTE_ARC_DURATION, attrKey,
                primaryValues, secValues);

        List<LogFilterRule> criteria = List.of(rule);

        APMLog logEF2 = getLog("5 cases EFollow (2).xes");
        List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, logEF2);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        APMLog logMod = getLog("5 cases EFollow MOD2.xes");
        validatedRules = FilterRuleValidator.validate(criteria, logMod);
        assertTrue(validatedRules.get(0).isApplicable());
        assertTrue(validatedRules.get(0).isSubstituted());
    }
}