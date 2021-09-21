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
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.validation.typevalidation.AttributeValidator;
import org.apromore.apmlog.filter.validation.typevalidation.CaseIdValidator;
import org.apromore.apmlog.filter.validation.typevalidation.CaseLengthValidator;
import org.apromore.apmlog.filter.validation.typevalidation.CaseVariantValidator;
import org.apromore.apmlog.filter.validation.typevalidation.DurationValidator;
import org.apromore.apmlog.filter.validation.typevalidation.PathValidator;
import org.apromore.apmlog.filter.validation.typevalidation.ReworkValidator;
import org.apromore.apmlog.filter.validation.typevalidation.TimeframeValidator;

import java.util.ArrayList;
import java.util.List;

public class FilterRuleValidator {

    public static List<ValidatedFilterRule> validate(List<LogFilterRule> logFilterRules, APMLog apmLog) {
        List<ValidatedFilterRule> validatedFilterRuleList = new ArrayList<>(logFilterRules.size());

        for (LogFilterRule rule : logFilterRules) {
            LogFilterRule validatedRule = validate(rule, apmLog);
            boolean substituted = validatedRule != null &&
                    !validatedRule.getFilterRuleDesc().equals(rule.getFilterRuleDesc());

            ValidatedFilterRule vfr = new ValidatedFilterRule(rule,
                    validatedRule != null ? validatedRule : rule,
                    validatedRule != null,
                    substituted);

            validatedFilterRuleList.add(vfr);
        }

        return validatedFilterRuleList;
    }

    private static LogFilterRule validate(LogFilterRule logFilterRule, APMLog apmLog) {

        switch (logFilterRule.getFilterType()) {
            case CASE_VARIANT:
                return CaseVariantValidator.validateCaseVariant(logFilterRule, apmLog);
            case CASE_ID:
                return CaseIdValidator.validateCaseId(logFilterRule, apmLog);
            case CASE_EVENT_ATTRIBUTE:
            case EVENT_EVENT_ATTRIBUTE:
                return AttributeValidator.validateEventAttribute(logFilterRule, apmLog);
            case CASE_CASE_ATTRIBUTE:
                return AttributeValidator.validateCaseAttribute(logFilterRule, apmLog);
            case CASE_SECTION_ATTRIBUTE_COMBINATION:
                return AttributeValidator.validateAttributeCombination(logFilterRule, apmLog);
            case CASE_TIME:
            case STARTTIME:
            case ENDTIME:
            case EVENT_TIME:
                return TimeframeValidator.validateTimeframe(logFilterRule, apmLog);
            case CASE_LENGTH:
                return CaseLengthValidator.validateCaseLength(logFilterRule, apmLog);
            case CASE_UTILISATION:
            case DURATION:
            case TOTAL_PROCESSING_TIME:
            case AVERAGE_PROCESSING_TIME:
            case MAX_PROCESSING_TIME:
            case TOTAL_WAITING_TIME:
            case AVERAGE_WAITING_TIME:
            case MAX_WAITING_TIME:
                return DurationValidator.validateDoubleValues(logFilterRule, apmLog);
            case EVENT_ATTRIBUTE_DURATION:
                return DurationValidator.validateNodeDuration(logFilterRule, apmLog);
            case ATTRIBUTE_ARC_DURATION:
                return DurationValidator.validateArcDuration(logFilterRule, apmLog);
            case DIRECT_FOLLOW:
            case EVENTUAL_FOLLOW:
                return PathValidator.validate(logFilterRule, apmLog);
            case REWORK_REPETITION:
                return ReworkValidator.validate(logFilterRule, apmLog);
            default:
                break;
        }

        return logFilterRule;
    }

}
