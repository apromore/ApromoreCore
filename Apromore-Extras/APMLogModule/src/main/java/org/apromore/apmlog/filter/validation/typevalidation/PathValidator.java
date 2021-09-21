/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
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
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PathValidator extends AbstractLogFilterRuleValidator {

    private PathValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static LogFilterRule validate(LogFilterRule originalRule, APMLog apmLog) {
        LogFilterRule logFilterRule = originalRule.clone();

        String mainAttrKey = logFilterRule.getKey();

        UnifiedSet<EventAttributeValue> eavSet =
                apmLog.getImmutableEventAttributeValues().getOrDefault(mainAttrKey, null);

        if (eavSet == null) return null;

        Set<String> validVals = eavSet.stream().map(EventAttributeValue::getValue).collect(Collectors.toSet());

        Set<RuleValue> validRVs = logFilterRule.getPrimaryValues().stream()
                .filter(x -> validVals.contains(x.getStringValue()))
                .collect(Collectors.toSet());

        if (validRVs.isEmpty()) return null;

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

        if (fromList.isEmpty() || toList.isEmpty()) return null;

        logFilterRule.setPrimaryValues(validRVs);

        return logFilterRule;
    }
}
