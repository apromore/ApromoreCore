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
import org.apromore.apmlog.stats.CaseAttributeValue;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.eclipse.collections.impl.map.immutable.ImmutableUnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.stream.Collectors;

public class AttributeValidator extends AbstractLogFilterRuleValidator {

    private AttributeValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static LogFilterRule validateEventAttribute(LogFilterRule logFilterRule, APMLog apmLog) {
        String attrKey = logFilterRule.getKey();
        ImmutableUnifiedMap<String, UnifiedSet<EventAttributeValue>> eavMap = apmLog.getImmutableEventAttributeValues();

        if (!eavMap.containsKey(attrKey)) return null;

        Set<String> existValues =
                eavMap.get(attrKey).stream().map(EventAttributeValue::getValue).collect(Collectors.toSet());

        return validatePrimaryAttributeValues(logFilterRule, existValues);
    }

    public static LogFilterRule validateCaseAttribute(LogFilterRule logFilterRule, APMLog apmLog) {
        String attrKey = logFilterRule.getKey();
        ImmutableUnifiedMap<String, UnifiedSet<CaseAttributeValue>> cavMap = apmLog.getImmutableCaseAttributeValues();

        if (!cavMap.containsKey(attrKey)) return null;

        Set<String> existValues =
                cavMap.get(attrKey).stream().map(CaseAttributeValue::getValue).collect(Collectors.toSet());

        return validatePrimaryAttributeValues(logFilterRule, existValues);
    }

    private static LogFilterRule validatePrimaryAttributeValues(@NotNull LogFilterRule originalRule,
                                                                @NotNull Set<String> existValues) {
        if (originalRule.getPrimaryValues() == null || originalRule.getPrimaryValues().isEmpty())
            return null;

        LogFilterRule logFilterRule = originalRule.clone();

        Set<String> ruleVals = logFilterRule.getPrimaryValues().iterator().next().getStringSetValue();
        Set<String> validValues = ruleVals.stream().filter(existValues::contains).collect(Collectors.toSet());

        if (validValues.isEmpty())
            return null;

        logFilterRule.getPrimaryValues().iterator().next().setObjectVal(validValues);

        return logFilterRule;
    }

    public static LogFilterRule validateAttributeCombination(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule logFilterRule = originalRule.clone();

        RuleValue primRV = logFilterRule.getPrimaryValues().iterator().next();
        String primSect = primRV.getCustomAttributes().get("section");
        String primKey = logFilterRule.getPrimaryValues().iterator().next().getKey();

        Set<String> existPrimVals = getExistValues(primSect, primKey, apmLog);
        Set<String> validPrimVals = getValidAttributeValues(primRV.getStringSetValue(), existPrimVals);

        if (validPrimVals == null || validPrimVals.isEmpty()) return null;

        primRV.setObjectVal(validPrimVals);
        primRV.setStringVal(validPrimVals.iterator().next());

        RuleValue secoRV = logFilterRule.getSecondaryValues().iterator().next();
        String secoSect = secoRV.getCustomAttributes().get("section");
        String secoKey = secoRV.getKey();

        Set<String> existSecoVals = getExistValues(secoSect, secoKey, apmLog);
        Set<String> validSecoVals = getValidAttributeValues(secoRV.getStringSetValue(), existSecoVals);

        if (validSecoVals == null || validSecoVals.isEmpty()) return null;

        secoRV.setObjectVal(validSecoVals);

        return logFilterRule;
    }
}
