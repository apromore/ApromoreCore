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
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.filter.validation.ValidatedFilterRule;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.stats.LogStatsAnalyzer;
import org.eclipse.collections.api.tuple.primitive.DoubleDoublePair;
import org.eclipse.collections.api.tuple.primitive.LongLongPair;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractLogFilterRuleValidator {

    protected static List<ActivityInstance> getAllActivities(APMLog apmLog) {
        return apmLog.getTraces().stream()
                .flatMap(x -> x.getActivityInstances().stream())
                .collect(Collectors.toList());
    }

    protected static Set<String> getValidAttributeValues(Set<String> ruleValues, Set<String> existValues) {
        return ruleValues.stream()
                .filter(existValues::contains)
                .collect(Collectors.toSet());
    }

    protected static Set<String> getExistValues(String section, String attrKey, APMLog apmLog) {
        return section.equalsIgnoreCase("case") ?
                LogStatsAnalyzer.getUniqueCaseAttributeValues(apmLog.getTraces(), attrKey) :
                LogStatsAnalyzer.getUniqueEventAttributeValues(apmLog.getActivityInstances(), attrKey);
    }

    protected static LongLongPair getFromAndToLongValues(LogFilterRule logFilterRule) {
        long from = 0, to = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) from = ruleValue.getLongValue();
            if (operationType == OperationType.LESS_EQUAL) to = ruleValue.getLongValue();
        }
        return PrimitiveTuples.pair(from, to);
    }

    protected static DoubleDoublePair getFromAndToDoubleValues(Set<RuleValue> ruleValues) {
        double from = 0, to = 0;
        for (RuleValue ruleValue : ruleValues) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) from = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) to = ruleValue.getDoubleValue();
        }

        return PrimitiveTuples.pair(from, to);
    }

    protected static ValidatedFilterRule replaceLongValues(LogFilterRule logFilterRule, LongArrayList lal) {

        LongLongPair pair = getFromAndToLongValues(logFilterRule);

        long validFrom = Math.max(pair.getOne(), lal.min());
        long validTo = Math.min(pair.getTwo(), lal.max());

        if (validFrom > validTo)
            return createInvalidFilterRuleResult(logFilterRule);

        if (validFrom == lal.min() && validTo == lal.max())
            return createInvalidFilterRuleResult(logFilterRule);

        return replaceLongValues(logFilterRule, validFrom, validTo);
    }

    protected static ValidatedFilterRule replaceLongValues(LogFilterRule originalRule, long from, long to) {
        LogFilterRule validatedRule = originalRule.deepClone();

        for (RuleValue ruleValue : validatedRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) ruleValue.setLongVal(from);
            if (operationType == OperationType.LESS_EQUAL) ruleValue.setLongVal(to);
        }

        LongLongPair pair = getFromAndToLongValues(originalRule);
        boolean substituted = pair.getOne() != from || pair.getTwo() != to;

        return new ValidatedFilterRule(originalRule, validatedRule, true, substituted);
    }

    protected static Set<RuleValue> replaceDoubleValues(Set<RuleValue> ruleValues, DoubleArrayList dal) {

        DoubleDoublePair valPair = getFromAndToDoubleValues(ruleValues);

        double validFrom = Math.max(valPair.getOne(), dal.min());
        double validTo = Math.min(valPair.getTwo(), dal.max());

        if (validFrom >= validTo) return null;
        if (validFrom == dal.min() && validTo == dal.max()) return null;

        for (RuleValue ruleValue : ruleValues) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) ruleValue.setDoubleVal(validFrom);
            if (operationType == OperationType.LESS_EQUAL) ruleValue.setDoubleVal(validTo);
        }
        return ruleValues;
    }

    protected static DoubleArrayList getAttributeDurationList(APMLog apmLog, String key, String value) {
        List<ActivityInstance> allLogActs = getAllActivities(apmLog);

        double[] array = allLogActs.stream()
                .filter(x -> x.getAttributes().containsKey(key) && x.getAttributeValue(key).equals(value))
                .mapToDouble(ActivityInstance::getDuration)
                .toArray();

        return new DoubleArrayList(array);
    }

    protected static Set<String> getEventAttributeKeys(ATrace trace) {
        return trace.getActivityInstances().stream()
                .flatMap(x -> x.getAttributes().keySet().stream())
                .collect(Collectors.toSet());
    }

    protected static Pair<String, String> getFromToValPair(Set<RuleValue> ruleValues) {
        String fromVal = "", toVal = "";
        for (RuleValue ruleValue : ruleValues) {
            if (ruleValue.getOperationType() == OperationType.FROM) fromVal = ruleValue.getStringValue();
            if (ruleValue.getOperationType() == OperationType.TO) toVal = ruleValue.getStringValue();
        }

        return Pair.of(fromVal, toVal);
    }

    protected static List<Double> getAttributeToAttributeDurationList(List<ActivityInstance> activityList,
                                                                    String attributeKey,
                                                                    String value1,
                                                                    String value2) {
        List<Double> durList= new ArrayList<>();

        for (int i = 0; i < activityList.size(); i++) {
            if (i < activityList.size()-1) {
                ActivityInstance activity1 = activityList.get(i);
                ActivityInstance activity2 = activityList.get(i+1);

                UnifiedMap<String, String> attrMap1 = activity1.getAttributes();
                UnifiedMap<String, String> attrMap2 = activity2.getAttributes();

                if (attrMap1.containsKey(attributeKey) && attrMap2.containsKey(attributeKey)) {
                    String actVal1 = attrMap1.get(attributeKey);
                    String actVal2 = attrMap2.get(attributeKey);
                    if (actVal1.equals(value1) && actVal2.equals(value2)) {
                        double act1EndTime = activity1.getEndTime();
                        double act2StartTime = activity2.getStartTime();
                        double duration = act2StartTime > act1EndTime ? act2StartTime - act1EndTime : 0;

                        durList.add(duration);
                    }
                }
            }
        }

        return durList;
    }

    protected static ValidatedFilterRule createInvalidFilterRuleResult(LogFilterRule logFilterRule) {
        return new ValidatedFilterRule(logFilterRule, logFilterRule, false, false);
    }
}
