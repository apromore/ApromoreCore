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
package org.apromore.apmlog.filter.validation;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.validation.typevalidation.AttributeValidator;
import org.apromore.apmlog.filter.validation.typevalidation.BetweenValidator;
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

            FilterType filterType = rule.getFilterType();

            switch (filterType) {
                case CASE_ID:
                    validatedFilterRuleList.add(CaseIdValidator.validateCaseId(rule, apmLog));
                    break;
                case CASE_VARIANT:
                    validatedFilterRuleList.add(CaseVariantValidator.validateCaseVariant(rule, apmLog));
                    break;
                case CASE_CASE_ATTRIBUTE:
                    validatedFilterRuleList.add(AttributeValidator.validateCaseAttribute(rule, apmLog));
                    break;
                case CASE_EVENT_ATTRIBUTE:
                case EVENT_EVENT_ATTRIBUTE:
                    validatedFilterRuleList.add(AttributeValidator.validateEventAttribute(rule, apmLog));
                    break;
                case CASE_SECTION_ATTRIBUTE_COMBINATION:
                    validatedFilterRuleList.add(AttributeValidator.validateAttributeCombination(rule, apmLog));
                    break;
                case CASE_TIME:
                case STARTTIME:
                case ENDTIME:
                case EVENT_TIME:
                    validatedFilterRuleList.add(TimeframeValidator.validateTimeframe(rule, apmLog));
                    break;
                case CASE_LENGTH:
                    validatedFilterRuleList.add(CaseLengthValidator.validateCaseLength(rule, apmLog));
                    break;
                case CASE_UTILISATION:
                case DURATION:
                case TOTAL_PROCESSING_TIME:
                case AVERAGE_PROCESSING_TIME:
                case MAX_PROCESSING_TIME:
                case TOTAL_WAITING_TIME:
                case AVERAGE_WAITING_TIME:
                case MAX_WAITING_TIME:
                    validatedFilterRuleList.add(DurationValidator.validateDoubleValues(rule, apmLog));
                    break;
                case EVENT_ATTRIBUTE_DURATION:
                    validatedFilterRuleList.add(DurationValidator.validateNodeDuration(rule, apmLog));
                    break;
                case ATTRIBUTE_ARC_DURATION:
                    validatedFilterRuleList.add(DurationValidator.validateArcDuration(rule, apmLog));
                    break;
                case DIRECT_FOLLOW:
                case EVENTUAL_FOLLOW:
                    validatedFilterRuleList.add(PathValidator.validate(rule, apmLog));
                    break;
                case REWORK_REPETITION:
                    validatedFilterRuleList.add(ReworkValidator.validate(rule, apmLog));
                    break;
                case BETWEEN:
                    ValidatedFilterRule vfr = BetweenValidator.validate(rule, apmLog);
                    if (vfr != null) {
                        validatedFilterRuleList.add(vfr);
                    }
                    break;
                default:
                    validatedFilterRuleList.add(new ValidatedFilterRule(rule, rule, true, false));
                    break;
            }
        }

        return validatedFilterRuleList;
    }

}
