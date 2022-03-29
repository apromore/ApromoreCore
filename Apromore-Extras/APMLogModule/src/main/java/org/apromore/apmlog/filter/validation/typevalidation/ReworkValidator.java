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
import org.apromore.apmlog.filter.validation.ValidatedFilterRule;
import org.apromore.apmlog.stats.LogStatsAnalyzer;

import java.util.Set;
import java.util.stream.Collectors;

public class ReworkValidator extends AbstractLogFilterRuleValidator {

    private ReworkValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static ValidatedFilterRule validate(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule validatedRule = originalRule.deepClone();

        String attrKey = validatedRule.getKey();

        Set<String> validVals =
                LogStatsAnalyzer.getUniqueEventAttributeValues(apmLog.getActivityInstances(), attrKey);

        Set<RuleValue> validValues = validatedRule.getPrimaryValues().stream()
                .filter(x -> validVals.contains(x.getKey()))
                .collect(Collectors.toSet());

        if (validValues.isEmpty()) return null;

        validatedRule.setPrimaryValues(validValues);

        Set<String> origVals = originalRule.getPrimaryValues().stream().map(RuleValue::getKey).collect(Collectors.toSet());
        Set<String> valiVals = validValues.stream().map(RuleValue::getKey).collect(Collectors.toSet());
        boolean substituted = origVals.stream().distinct().filter(valiVals::contains).count() != origVals.size();

        return new ValidatedFilterRule(originalRule, validatedRule, true, substituted);
    }
}
