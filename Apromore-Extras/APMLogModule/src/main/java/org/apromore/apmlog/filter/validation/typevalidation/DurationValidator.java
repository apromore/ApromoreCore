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

import org.apache.commons.lang3.tuple.Pair;
import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
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

    public static LogFilterRule validateDoubleValues(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule logFilterRule = originalRule.clone();

        double[] array = null;
        switch (logFilterRule.getFilterType()) {
            case CASE_UTILISATION:
                array = apmLog.getTraces().stream().mapToDouble(ATrace::getCaseUtilization).toArray();
                break;
            case DURATION:
                array = apmLog.getTraces().stream().mapToDouble(ATrace::getDuration).toArray();
                break;
            case TOTAL_PROCESSING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> x.getProcessingTimes().sum()).toArray();
                break;
            case AVERAGE_PROCESSING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> x.getProcessingTimes().average()).toArray();
                break;
            case MAX_PROCESSING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> x.getProcessingTimes().max()).toArray();
                break;
            case TOTAL_WAITING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> x.getWaitingTimes().sum()).toArray();
                break;
            case AVERAGE_WAITING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> x.getWaitingTimes().average()).toArray();
                break;
            case MAX_WAITING_TIME:
                array = apmLog.getTraces().stream().mapToDouble(x -> x.getWaitingTimes().max()).toArray();
                break;
        }

        if (array == null) return null;

        DoubleArrayList dal = new DoubleArrayList(array);

        Set<RuleValue> priVals = replaceDoubleValues(logFilterRule.getPrimaryValues(), dal);

        if (priVals == null) return null;

        logFilterRule.setPrimaryValues(priVals);

        return logFilterRule;
    }


    public static LogFilterRule validateNodeDuration(LogFilterRule originalRule, APMLog apmLog) {
        LogFilterRule logFilterRule = originalRule.clone();

        String attributeKey = logFilterRule.getKey();
        String attributeValue = logFilterRule.getPrimaryValues().iterator().next().getKey();
        DoubleArrayList dal = getAttributeDurationList(apmLog, attributeKey, attributeValue);

        Set<RuleValue> ruleValues = replaceDoubleValues(logFilterRule.getPrimaryValues(), dal);

        logFilterRule.setPrimaryValues(ruleValues);

        return logFilterRule;
    }


    public static LogFilterRule validateArcDuration(LogFilterRule originalRule, APMLog apmLog) {

        LogFilterRule logFilterRule = originalRule.clone();

        String attributeKey = logFilterRule.getKey();

        Pair<String, String> fromtoPair = getFromToValPair(logFilterRule.getPrimaryValues());

        List<Double> durList = new ArrayList<>();

        for (ATrace trace : apmLog.getTraces()) {
            Set<String> eventAttrKeys = getEventAttributeKeys(trace);
            if (eventAttrKeys.contains(attributeKey)) {
                List<Double> arcDurs = getAttributeToAttributeDurationList(trace.getActivityInstances(), attributeKey,
                        fromtoPair.getKey(), fromtoPair.getValue());

                durList.addAll(arcDurs);
            }
        }

        if (durList.isEmpty()) return null;

        DoubleDoublePair ruleRangePair = getFromAndToDoubleValues(logFilterRule.getSecondaryValues());

        // ====================================================================================
        // Check if the rule range value covers any exist duration values.
        // If not, discard this rule
        // ====================================================================================
        List<Double> coveredVals = durList.stream()
                .filter(x -> x >= ruleRangePair.getOne() && x <= ruleRangePair.getTwo())
                .collect(Collectors.toList());

        if (coveredVals.isEmpty()) return null;

        double[] array = durList.stream().mapToDouble(x -> x).toArray();
        DoubleArrayList dal = new DoubleArrayList(array);

        Set<RuleValue> secoVals = replaceDoubleValues(logFilterRule.getSecondaryValues(), dal);
        logFilterRule.setSecondaryValues(secoVals);

        return logFilterRule;
    }
}
