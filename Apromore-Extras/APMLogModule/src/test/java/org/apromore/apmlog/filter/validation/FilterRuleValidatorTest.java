/**
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
package org.apromore.apmlog.filter.validation;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.APMLogUnitTest;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.validation.typevalidation.AttributeValidatorTest;
import org.apromore.apmlog.filter.validation.typevalidation.CaseIdValidatorTest;
import org.apromore.apmlog.logobjects.ImmutableLog;
import org.apromore.apmlog.xes.XLogToImmutableLog;
import org.deckfour.xes.model.XLog;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.List;

public class FilterRuleValidatorTest {

    @Test
    public void validate() throws Exception {
        LogFilterRule caseIdRule = CaseIdValidatorTest.getCaseIdValidatorTestRule();
        LogFilterRule orgGroupRule = AttributeValidatorTest.getSingleValueRule(FilterType.CASE_EVENT_ATTRIBUTE,
                "org:group", "Product Management", "event");
        List<LogFilterRule> criteria = List.of(caseIdRule, orgGroupRule);
        APMLog apmLog = getLog("5 cases EFollow (2).xes");
        List<ValidatedFilterRule> validatedRules = FilterRuleValidator.validate(criteria, apmLog);
        assertFalse(validatedRules.isEmpty());
        assertEquals(2, validatedRules.size());
        assertTrue(validatedRules.get(0).isApplicable());
        assertTrue(validatedRules.get(0).isSubstituted());
    }

    public static ImmutableLog getLog(String filename) throws Exception {
        int fileExtIndex = filename.indexOf(".");
        String nameWithoutExt = filename.substring(0, fileExtIndex);
        XLog xLog = APMLogUnitTest.getXLog("files/" + filename);
        return XLogToImmutableLog.convertXLog(nameWithoutExt, xLog);
    }

}