/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.AActivity;

import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttributeArcDurationFilter {
    public static boolean toKeep(ATrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(ATrace trace, LogFilterRule logFilterRule) {
        String attributeKey = logFilterRule.getKey();

        String fromVal = "", toVal = "";
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            if (ruleValue.getOperationType() == OperationType.FROM) {
                fromVal = ruleValue.getStringValue();
            }
            if (ruleValue.getOperationType() == OperationType.TO) {
                toVal = ruleValue.getStringValue();
            }
        }


        double lowBoundVal = 0, upBoundVal = 0;
        for (RuleValue ruleValue : logFilterRule.getSecondaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) lowBoundVal = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) upBoundVal = ruleValue.getDoubleValue();
        }

        List<Double> durList = getAttributeToAttributeDurationList(trace, attributeKey,
                fromVal, toVal, lowBoundVal, upBoundVal);

        return durList.size() > 0;
    }

    private static List<Double> getAttributeToAttributeDurationList(ATrace trace, String attributeKey,
                                                                    String value1, String value2,
                                                                    double lowBoundVal, double upBoundVal) {
        List<Double> durList= new ArrayList<>();

        List<AActivity> activityList = trace.getActivityList().stream()
                .filter(x -> trace.getValidEventIndexBitSet().get(x.getEventIndexes().get(0)))
                .collect(Collectors.toList());

        for (int i = 0; i < activityList.size(); i++) {
            if (i < activityList.size()-1) {
                AActivity activity1 = activityList.get(i);
                AActivity activity2 = activityList.get(i+1);

                UnifiedMap<String, String> attrMap1 = activity1.getAllAttributes();
                UnifiedMap<String, String> attrMap2 = activity2.getAllAttributes();

                if (attrMap1.containsKey(attributeKey) && attrMap2.containsKey(attributeKey)) {
                    String actVal1 = attrMap1.get(attributeKey);
                    String actVal2 = attrMap2.get(attributeKey);
                    if (actVal1.equals(value1) && actVal2.equals(value2)) {
                        double act1EndTime = activity1.getEndTimeMilli();
                        double act2StartTime = activity2.getStartTimeMilli();
                        double duration = act2StartTime > act1EndTime ? act2StartTime - act1EndTime : 0;
                        if (duration >= lowBoundVal && duration <= upBoundVal) {
                            durList.add(duration);
                        }
                    }
                }
            }
        }

        return durList;
    }

}
