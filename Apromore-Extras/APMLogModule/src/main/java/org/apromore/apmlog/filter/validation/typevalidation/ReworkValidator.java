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
import org.apromore.apmlog.stats.EventAttributeValue;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Set;
import java.util.stream.Collectors;

public class ReworkValidator extends AbstractLogFilterRuleValidator {

    private ReworkValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static LogFilterRule validate(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule logFilterRule = originalRule.clone();

        String attrKey = logFilterRule.getKey();

        UnifiedSet<EventAttributeValue> eavSet =
                apmLog.getImmutableEventAttributeValues().getOrDefault(attrKey, null);

        if (eavSet == null) return null;

        Set<String> validVals = eavSet.stream().map(EventAttributeValue::getValue).collect(Collectors.toSet());

        Set<RuleValue> validValues = logFilterRule.getPrimaryValues().stream()
                .filter(x -> validVals.contains(x.getKey()))
                .collect(Collectors.toSet());

        if (validValues.isEmpty()) return null;

        logFilterRule.setPrimaryValues(validValues);

        return logFilterRule;
    }
}
