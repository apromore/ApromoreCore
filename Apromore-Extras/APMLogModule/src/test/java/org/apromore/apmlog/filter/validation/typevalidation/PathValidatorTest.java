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

class PathValidatorTest extends FilterRuleValidatorTest {

    @Test
    void validate() {
        FilterType filterType = FilterType.EVENTUAL_FOLLOW;
        String mainKey = XESAttributeCodes.CONCEPT_NAME;
        String secoKeyVal = XESAttributeCodes.ORG_RESOURCE;

        RuleValue rvt1 = new RuleValue(filterType, OperationType.TO, mainKey, "Warehouse check for the order");
        RuleValue rvt2 = new RuleValue(filterType, OperationType.TO, mainKey, "Order modification");
        RuleValue rvf1 = new RuleValue(filterType, OperationType.FROM, mainKey, "Proceed order");

        Set<RuleValue> primaryValues = Set.of(rvt1, rvt2, rvf1);

        RuleValue secVal1 = new RuleValue(filterType, OperationType.EQUAL, secoKeyVal, secoKeyVal);

        Set<RuleValue> secondaryValues = Set.of(secVal1);

        LogFilterRule rule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, mainKey,
                primaryValues, secondaryValues);

        try {
            List<LogFilterRule> criteria = List.of(rule);
            APMLog logEF2 = getLog("5 cases EFollow (2).xes");
            List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, logEF2);
            assertTrue(validatedRules.get(0).isApplicable());
            assertFalse(validatedRules.get(0).isSubstituted());

            APMLog logMod = getLog("5 cases EFollow MOD2.xes");
            validatedRules = FilterRuleValidator.validate(criteria, logMod);
            assertTrue(validatedRules.get(0).isApplicable());
            assertTrue(validatedRules.get(0).isSubstituted());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test
    void validateDirectFollowToEnd() {
        FilterType filterType = FilterType.DIRECT_FOLLOW;
        String mainKey = XESAttributeCodes.CONCEPT_NAME;

        RuleValue r1v1 = new RuleValue(filterType, OperationType.FROM, mainKey, "Prepare package");
        RuleValue r1v2 = new RuleValue(filterType, OperationType.TO, mainKey, "[End]");

        Set<RuleValue> primaryValues = Set.of(r1v1, r1v2);

        LogFilterRule rule = new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ANY_VALUE, Section.CASE,
                filterType, mainKey,
                primaryValues, null);

        try {
            List<LogFilterRule> criteria = List.of(rule);
            APMLog logEF2 = getLog("5 cases EFollow (2).xes");
            List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, logEF2);
            assertTrue(validatedRules.get(0).isApplicable());
            assertFalse(validatedRules.get(0).isSubstituted());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}