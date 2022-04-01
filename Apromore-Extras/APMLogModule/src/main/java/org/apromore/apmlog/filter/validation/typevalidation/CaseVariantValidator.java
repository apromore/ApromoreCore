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
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.stats.LogStatsAnalyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CaseVariantValidator extends AbstractLogFilterRuleValidator {

    private CaseVariantValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static ValidatedFilterRule validateCaseVariant(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule clone = originalRule.deepClone();

        List<Map.Entry<String, List<Map.Entry<Integer, List<ActivityInstance>>>>> groups =
                LogStatsAnalyzer.getCaseVariantGroups(apmLog.getActivityInstances());

        Set<Integer> validVals = clone.getPrimaryValuesInString().stream()
                .filter(x -> Integer.parseInt(x) >= 1 && Integer.parseInt(x) <= groups.size())
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(HashSet::new));

        if (validVals.isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        Set<RuleValue> primaryValues = new HashSet<>();

        for (int val : validVals) {
            primaryValues.add(new RuleValue(FilterType.CASE_VARIANT, OperationType.EQUAL, "case:variant", val));
        }

        Set<Integer> originalVals = originalRule.getPrimaryValuesInString().stream()
                .map(Integer::valueOf)
                .collect(Collectors.toCollection(HashSet::new));

        boolean substituted =
                originalVals.stream().distinct().filter(validVals::contains).count() != originalVals.size();

        clone.setPrimaryValues(primaryValues);

        return new ValidatedFilterRule(originalRule, clone, true, substituted);
    }
}
