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
import org.apromore.apmlog.filter.validation.ValidatedFilterRule;
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

    public static ValidatedFilterRule validateEventAttribute(LogFilterRule originalRule, APMLog apmLog) {
        String attrKey = originalRule.getKey();
        ImmutableUnifiedMap<String, UnifiedSet<EventAttributeValue>> eavMap = apmLog.getImmutableEventAttributeValues();

        if (!eavMap.containsKey(attrKey))
            return createInvalidFilterRuleResult(originalRule);

        Set<String> existValues =
                eavMap.get(attrKey).stream().map(EventAttributeValue::getValue).collect(Collectors.toSet());

        return validatePrimaryAttributeValues(originalRule, existValues);
    }

    public static ValidatedFilterRule validateCaseAttribute(LogFilterRule originalRule, APMLog apmLog) {
        String attrKey = originalRule.getKey();
        ImmutableUnifiedMap<String, UnifiedSet<CaseAttributeValue>> cavMap = apmLog.getImmutableCaseAttributeValues();

        if (!cavMap.containsKey(attrKey))
            return createInvalidFilterRuleResult(originalRule);

        Set<String> existValues =
                cavMap.get(attrKey).stream().map(CaseAttributeValue::getValue).collect(Collectors.toSet());

        return validatePrimaryAttributeValues(originalRule, existValues);
    }

    private static ValidatedFilterRule validatePrimaryAttributeValues(@NotNull LogFilterRule originalRule,
                                                                      @NotNull Set<String> existValues) {
        if (originalRule.getPrimaryValues() == null || originalRule.getPrimaryValues().isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        LogFilterRule validatedRule = originalRule.clone();

        Set<String> ruleVals = validatedRule.getPrimaryValues().iterator().next().getStringSetValue();
        Set<String> validValues = ruleVals.stream().filter(existValues::contains).collect(Collectors.toSet());

        if (validValues.isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        boolean substituted = ruleVals.stream().distinct().filter(validValues::contains).count() != ruleVals.size();

        validatedRule.getPrimaryValues().iterator().next().setObjectVal(validValues);

        return new ValidatedFilterRule(originalRule, validatedRule, true, substituted);
    }

    public static ValidatedFilterRule validateAttributeCombination(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule validatedRule = originalRule.clone();

        RuleValue primRV = validatedRule.getPrimaryValues().iterator().next();
        String primSect = primRV.getCustomAttributes().get("section");
        String primKey = validatedRule.getPrimaryValues().iterator().next().getKey();

        Set<String> existPrimVals = getExistValues(primSect, primKey, apmLog);
        Set<String> validPrimVals = getValidAttributeValues(primRV.getStringSetValue(), existPrimVals);

        boolean samePrimVals = primRV.getStringSetValue().stream()
                .distinct().filter(validPrimVals::contains).count() == primRV.getStringSetValue().size();

        if (validPrimVals.isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        primRV.setObjectVal(validPrimVals);
        primRV.setStringVal(validPrimVals.iterator().next());

        RuleValue secoRV = validatedRule.getSecondaryValues().iterator().next();
        String secoSect = secoRV.getCustomAttributes().get("section");
        String secoKey = secoRV.getKey();

        Set<String> allExistSecoVals = getExistValues(secoSect, secoKey, apmLog);
        Set<String> validSecoVals = getValidAttributeValues(secoRV.getStringSetValue(), allExistSecoVals);

        boolean sameSecoVals = secoRV.getStringSetValue().stream()
                .distinct().filter(validSecoVals::contains).count() == secoRV.getStringSetValue().size();

        if (validSecoVals.isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        secoRV.setObjectVal(validSecoVals);

        boolean substituted = !samePrimVals || !sameSecoVals;

        return new ValidatedFilterRule(originalRule, validatedRule, true, substituted);
    }
}
