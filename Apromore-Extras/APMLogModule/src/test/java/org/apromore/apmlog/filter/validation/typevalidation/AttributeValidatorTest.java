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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class AttributeValidatorTest extends FilterRuleValidatorTest {

    public static LogFilterRule getSingleValueRule(FilterType filterType, String key, String value, String section) {
        RuleValue r1 = new RuleValue(filterType, OperationType.EQUAL, key, value);
        return getRuleImpl(filterType, key, r1, section);
    }

    private LogFilterRule getMultiValueRule(Set<String> values) {
        RuleValue r1 = new RuleValue(FilterType.CASE_EVENT_ATTRIBUTE, OperationType.EQUAL,
                XESAttributeCodes.CONCEPT_NAME, values);
        return getRuleImpl(FilterType.CASE_EVENT_ATTRIBUTE, XESAttributeCodes.CONCEPT_NAME, r1, "event");
    }

    private static LogFilterRuleImpl getRuleImpl(FilterType filterType, String key, RuleValue r1, String section) {
        r1.putCustomAttribute("section", section);
        Set<RuleValue> rvs1 = Set.of(r1);

        return new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, key,
                rvs1, null);
    }

    @Test
    void validateEventAttribute() throws Exception {
        LogFilterRule rule = getSingleValueRule(FilterType.CASE_EVENT_ATTRIBUTE, "org:group",
                "Product Management", "event");
        List<LogFilterRule> criteria = List.of(rule);

        APMLog apmLog = getLog("5 cases EFollow (2).xes");
        List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
        assertEquals(validatedRules.get(0).getFilterRule().getFilterRuleDesc(), rule.getFilterRuleDesc());

        apmLog = getLog("5 cases EFollow MOD2.xes");
        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
        assertEquals(validatedRules.get(0).getFilterRule().getFilterRuleDesc(), rule.getFilterRuleDesc());

        rule = getSingleValueRule(FilterType.CASE_EVENT_ATTRIBUTE, "org:group",
                "Website Order", "event");
        criteria = List.of(rule);

        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertFalse(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        apmLog = getLog("5 cases EFollow (2).xes");
        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
        assertEquals(validatedRules.get(0).getFilterRule().getFilterRuleDesc(), rule.getFilterRuleDesc());
    }

    @Test
    void testMultiValueEventAttrValidator() throws Exception {
        LogFilterRule rule = getMultiValueRule(
                Set.of("Proceed order", "Prepare package",
                        "Tranfer items from warehouse 2 one 'two three four five' and six"));
        List<LogFilterRule> criteria = List.of(rule);

        APMLog apmLog = getLog("5 cases EFollow (2).xes");
        List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, apmLog);

        Set<String> val1Set = validatedRules.get(0).getFilterRule().getPrimaryValues().iterator().next().getStringSetValue();

        assertTrue(validatedRules.get(0).isApplicable());
        assertTrue(validatedRules.get(0).isSubstituted());
        assertEquals(2, val1Set.size());
    }

    @Test
    void validateCaseAttribute() throws Exception {
        FilterType filterType = FilterType.CASE_CASE_ATTRIBUTE;
        String key1 = "Customer ID";

        RuleValue r1 = new RuleValue(filterType, OperationType.EQUAL, key1, "A1025");
        r1.putCustomAttribute("section", "case");
        Set<RuleValue> rvs1 = Set.of(r1);

        LogFilterRule rule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, key1,
                rvs1, null);

        List<LogFilterRule> criteria = List.of(rule);

        APMLog apmLog = getLog("5 cases EFollow (2).xes");
        List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
        assertEquals(validatedRules.get(0).getFilterRule().getFilterRuleDesc(), rule.getFilterRuleDesc());

        apmLog = getLog("5 cases EFollow MOD2.xes");
        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
        assertEquals(validatedRules.get(0).getFilterRule().getFilterRuleDesc(), rule.getFilterRuleDesc());

        r1 = new RuleValue(filterType, OperationType.EQUAL, key1, "Z3000");
        r1.putCustomAttribute("section", "case");
        rvs1 = Set.of(r1);
        rule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, key1,
                rvs1, null);
        criteria = List.of(rule);

        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
        assertEquals(validatedRules.get(0).getFilterRule().getFilterRuleDesc(), rule.getFilterRuleDesc());

        apmLog = getLog("5 cases EFollow (2).xes");
        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertFalse(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
    }

    @Test
    void validateAttributeCombination()  throws Exception{

        LogFilterRule rule = getAttrCombFilterRule(
                "event", "concept:name", "Proceed order",
                "event", "org:group", "Website Order");

        List<LogFilterRule> criteria = List.of(rule);

        APMLog apmLog = getLog("5 cases EFollow (2).xes");

        List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
        assertEquals(validatedRules.get(0).getFilterRule().getFilterRuleDesc(), rule.getFilterRuleDesc());

        apmLog = getLog("5 cases EFollow MOD2.xes");
        validatedRules = FilterRuleValidator.validate(criteria, apmLog);

        assertFalse(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        // =================================================================
        // test - incompatible case attribute key as primary key
        // =================================================================
        rule = getAttrCombFilterRule(
                "case", "Product ID", "PDT2020",
                "event", "org:resource", "Georg");
        criteria = List.of(rule);

        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        apmLog = getLog("5 cases EFollow (2).xes");
        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertFalse(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        // =================================================================
        // test - incompatible case attribute key as secondary key
        // =================================================================
        rule = getAttrCombFilterRule(
                "event", "concept:name", "Proceed order",
                "case", "Product ID", "PDT2020");

        criteria = List.of(rule);
        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertFalse(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());

        apmLog = getLog("5 cases EFollow MOD2.xes");
        validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertTrue(validatedRules.get(0).isApplicable());
        assertFalse(validatedRules.get(0).isSubstituted());
    }

    private LogFilterRule getAttrCombFilterRule(String sec1, String key1, String val1,
                                            String sec2, String key2, String val2) {

        FilterType filterType = FilterType.CASE_SECTION_ATTRIBUTE_COMBINATION;

        RuleValue r1 = new RuleValue(filterType, OperationType.EQUAL, key1, val1);
        r1.putCustomAttribute("section", sec1);

        Set<RuleValue> rvs1 = Set.of(r1);

        RuleValue r2 = new RuleValue(filterType, OperationType.EQUAL, key2, Set.of(val2));
        r2.putCustomAttribute("section", sec2);

        Set<RuleValue> rvs2 = Set.of(r2);

        return new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, key1, rvs1, rvs2);
    }

}