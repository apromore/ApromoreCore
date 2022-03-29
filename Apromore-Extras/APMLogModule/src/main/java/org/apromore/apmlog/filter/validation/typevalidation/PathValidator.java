/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.apmlog.filter.validation.typevalidation;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.validation.ValidatedFilterRule;
import org.apromore.apmlog.stats.LogStatsAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PathValidator extends AbstractLogFilterRuleValidator {

    private PathValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static ValidatedFilterRule validate(LogFilterRule originalRule, APMLog apmLog) {
        LogFilterRule validatedRule = originalRule.deepClone();

        String mainAttrKey = validatedRule.getKey();

        Set<String> eavKeys = LogStatsAnalyzer.getUniqueEventAttributeKeys(apmLog.getActivityInstances());

        if (eavKeys.isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        Set<RuleValue> secoVals = validatedRule.getSecondaryValues();
        if (secoVals != null && !secoVals.isEmpty()) {
            RuleValue rvReqAttr = secoVals.stream().filter(x -> x.getOperationType() == OperationType.EQUAL).findFirst().orElse(null);
            if (rvReqAttr != null && !eavKeys.contains(rvReqAttr.getKey()))
                return createInvalidFilterRuleResult(originalRule);
        }

        Set<String> validVals =
                LogStatsAnalyzer.getUniqueEventAttributeValues(apmLog.getActivityInstances(), mainAttrKey);

        if (originalRule.getFilterType().equals(FilterType.DIRECT_FOLLOW)) {
            validVals.add("[Start]");
            validVals.add("[End]");
        }

        Set<RuleValue> validRVs = validatedRule.getPrimaryValues().stream()
                .filter(x -> validVals.contains(x.getStringValue()))
                .collect(Collectors.toSet());

        if (validRVs.isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        for (RuleValue ruleValue : validRVs) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.FROM) {
                String fromVal = ruleValue.getStringValue();
                fromList.add(fromVal);
            }
            if (operationType == OperationType.TO) {
                String toVal = ruleValue.getStringValue();
                toList.add(toVal);
            }
        }

        if (fromList.isEmpty() || toList.isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        validatedRule.setPrimaryValues(validRVs);

        boolean substituted = !areEqual(originalRule, validatedRule);

        return new ValidatedFilterRule(originalRule, validatedRule, true, substituted);
    }

    public static boolean areEqual(LogFilterRule rule1, LogFilterRule rule2) {

        Set<String> rule1From = rule1.getPrimaryValues().stream()
                .filter(x -> x.getOperationType() == OperationType.FROM)
                .map(RuleValue::getStringValue)
                .collect(Collectors.toSet());

        Set<String> rule1To = rule1.getPrimaryValues().stream()
                .filter(x -> x.getOperationType() == OperationType.TO)
                .map(RuleValue::getStringValue)
                .collect(Collectors.toSet());

        Set<String> rule2From = rule2.getPrimaryValues().stream()
                .filter(x -> x.getOperationType() == OperationType.FROM)
                .map(RuleValue::getStringValue)
                .collect(Collectors.toSet());

        Set<String> rule2To = rule2.getPrimaryValues().stream()
                .filter(x -> x.getOperationType() == OperationType.TO)
                .map(RuleValue::getStringValue)
                .collect(Collectors.toSet());

        boolean equalFrom = rule1From.stream().distinct().filter(rule2From::contains).count() == rule1From.size();
        boolean equalTo = rule1To.stream().distinct().filter(rule2To::contains).count() == rule1To.size();

        return equalFrom && equalTo;
    }
}
