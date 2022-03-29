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

import org.apache.commons.lang3.tuple.Pair;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.validation.ValidatedFilterRule;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.eclipse.collections.api.tuple.primitive.DoubleDoublePair;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DurationValidator extends AbstractLogFilterRuleValidator {

    private DurationValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static ValidatedFilterRule validateDoubleValues(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule validatedRule = originalRule.deepClone();

        double[] array = null;
        switch (validatedRule.getFilterType()) {
            case CASE_UTILISATION:
                array = apmLog.getTraces().stream().mapToDouble(LogStatsAnalyzer::getCaseUtilizationOf).toArray();
                break;
            case DURATION:
                array = apmLog.getTraces().stream().mapToDouble(ATrace::getDuration).toArray();
                break;
            case TOTAL_PROCESSING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> LogStatsAnalyzer.getProcessingTimesOf(x).sum()).toArray();
                break;
            case AVERAGE_PROCESSING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> LogStatsAnalyzer.getProcessingTimesOf(x).average()).toArray();
                break;
            case MAX_PROCESSING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> LogStatsAnalyzer.getProcessingTimesOf(x).max()).toArray();
                break;
            case TOTAL_WAITING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> LogStatsAnalyzer.getWaitingTimesOf(x).sum()).toArray();
                break;
            case AVERAGE_WAITING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> LogStatsAnalyzer.getWaitingTimesOf(x).average()).toArray();
                break;
            case MAX_WAITING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> LogStatsAnalyzer.getWaitingTimesOf(x).max()).toArray();
                break;
            default:
                break;
        }

        if (array == null)
            return createInvalidFilterRuleResult(originalRule);

        DoubleArrayList dal = new DoubleArrayList(array);

        Set<RuleValue> priVals = replaceDoubleValues(validatedRule.getPrimaryValues(), dal);

        if (priVals == null)
            return createInvalidFilterRuleResult(originalRule);

        validatedRule.setPrimaryValues(priVals);

        Set<Long> originalDoubles =
                originalRule.getPrimaryValues().stream().map(RuleValue::getLongValue).collect(Collectors.toSet());
        Set<Long> validatedDoubles = priVals.stream().map(RuleValue::getLongValue).collect(Collectors.toSet());
        boolean substituted = originalDoubles.stream()
                .distinct().filter(validatedDoubles::contains).count() != originalDoubles.size();

        return new ValidatedFilterRule(originalRule, validatedRule, true, substituted);
    }


    public static ValidatedFilterRule validateNodeDuration(LogFilterRule originalRule, APMLog apmLog) {
        LogFilterRule validatedRule = originalRule.deepClone();

        Set<String> eventKeys = apmLog.getActivityInstances().stream()
                .flatMap(x -> x.getAttributes().keySet().stream()).collect(Collectors.toSet());

        String attributeKey = validatedRule.getKey();
        if (!eventKeys.contains(attributeKey))
            return createInvalidFilterRuleResult(originalRule);

        String attributeValue = validatedRule.getPrimaryValues().iterator().next().getKey();
        Set<String> existVals = apmLog.getActivityInstances().stream()
                .filter(x -> x.getAttributes().containsKey(attributeKey))
                .map(x -> x.getAttributeValue(attributeKey)).collect(Collectors.toSet());

        if (!existVals.contains(attributeValue))
            return createInvalidFilterRuleResult(originalRule);

        DoubleArrayList dal = getAttributeDurationList(apmLog, attributeKey, attributeValue);

        Set<RuleValue> validatedVals = replaceDoubleValues(validatedRule.getPrimaryValues(), dal);

        if (validatedVals == null)
            return createInvalidFilterRuleResult(originalRule);

        validatedRule.setPrimaryValues(validatedVals);

        Set<Long> origDurVals = originalRule.getPrimaryValues().stream()
                .map(RuleValue::getLongValue).collect(Collectors.toSet());
        Set<Long> valiDurVals = validatedVals.stream().map(RuleValue::getLongValue).collect(Collectors.toSet());

        boolean substituted =
                origDurVals.stream().distinct().filter(valiDurVals::contains).count() != origDurVals.size();

        return new ValidatedFilterRule(originalRule, validatedRule, true, substituted);
    }


    public static ValidatedFilterRule validateArcDuration(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule validatedRule = originalRule.deepClone();

        String attributeKey = validatedRule.getKey();

        Pair<String, String> fromtoPair = getFromToValPair(validatedRule.getPrimaryValues());

        List<Double> durList = new ArrayList<>();

        for (ATrace trace : apmLog.getTraces()) {
            Set<String> eventAttrKeys = getEventAttributeKeys(trace);
            if (eventAttrKeys.contains(attributeKey)) {
                List<Double> arcDurs = getAttributeToAttributeDurationList(trace.getActivityInstances(), attributeKey,
                        fromtoPair.getKey(), fromtoPair.getValue());

                durList.addAll(arcDurs);
            }
        }

        if (durList.isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        DoubleDoublePair ruleRangePair = getFromAndToDoubleValues(validatedRule.getSecondaryValues());

        // ====================================================================================
        // Check if the rule range value covers any exist duration values.
        // If not, discard this rule
        // ====================================================================================
        List<Double> coveredVals = durList.stream()
                .filter(x -> x >= ruleRangePair.getOne() && x <= ruleRangePair.getTwo())
                .collect(Collectors.toList());

        if (coveredVals.isEmpty())
            return createInvalidFilterRuleResult(originalRule);

        double[] array = durList.stream().mapToDouble(x -> x).toArray();
        DoubleArrayList dal = new DoubleArrayList(array);

        Set<RuleValue> secoVals = replaceDoubleValues(validatedRule.getSecondaryValues(), dal);

        if (secoVals == null)
            return createInvalidFilterRuleResult(originalRule);

        validatedRule.setSecondaryValues(secoVals);

        Set<Long> originalSecoDurVals = originalRule.getSecondaryValues().stream()
                .map(RuleValue::getLongValue).collect(Collectors.toSet());
        Set<Long> validatedSecoDurVals = secoVals.stream()
                .map(RuleValue::getLongValue).collect(Collectors.toSet());
        boolean substituted = originalSecoDurVals.stream()
                .distinct().filter(validatedSecoDurVals::contains).count() != originalSecoDurVals.size();

        return new ValidatedFilterRule(originalRule, validatedRule, true, substituted);
    }
}
