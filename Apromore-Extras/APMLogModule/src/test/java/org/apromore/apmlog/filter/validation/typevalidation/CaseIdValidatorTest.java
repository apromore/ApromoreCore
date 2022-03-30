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
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.BitSet;
import java.util.List;
import java.util.Set;

public class CaseIdValidatorTest extends FilterRuleValidatorTest {

    public static LogFilterRule getCaseIdValidatorTestRule() {
        BitSet bitSet = new BitSet(5);
        List<Integer> keptInexes = List.of(0, 3, 1, 4);
        for (int idx : keptInexes) {
            bitSet.set(idx);
        }

        UnifiedMap<String, String> customAttr = new UnifiedMap<>();
        customAttr.put("3011", String.valueOf(0));
        customAttr.put("3010", String.valueOf(3));
        customAttr.put("A1001", String.valueOf(1));
        customAttr.put("3007", String.valueOf(4));

        RuleValue rv = new RuleValue(FilterType.CASE_ID, OperationType.EQUAL,"case:id", bitSet);
        rv.setCustomAttributes(customAttr);
        Set<RuleValue> primaryValues = Set.of(rv);

        return new LogFilterRuleImpl(Choice.RETAIN, Inclusion.ALL_VALUES, Section.CASE,
                FilterType.CASE_ID, "case:id",
                primaryValues, null);
    }

    @Test
    void validateCaseId() throws Exception {
        List<LogFilterRule> criteria = List.of(getCaseIdValidatorTestRule());
        APMLog log = getLog("5 cases EFollow MOD2.xes");

        List<ValidatedFilterRule> vfrList = FilterRuleValidator.validate(criteria, log);
        assertTrue(vfrList.get(0).isApplicable());
        assertEquals(vfrList.get(0).getFilterRule().getFilterRuleDesc(), criteria.get(0).getFilterRuleDesc());

        log = getLog("5 cases EFollow (2).xes");
        vfrList = FilterRuleValidator.validate(criteria, log);
        assertTrue(vfrList.get(0).isApplicable());
        assertTrue(vfrList.get(0).isSubstituted());
        assertNotEquals(vfrList.get(0).getFilterRule().getFilterRuleDesc(), criteria.get(0).getFilterRuleDesc());
    }
}