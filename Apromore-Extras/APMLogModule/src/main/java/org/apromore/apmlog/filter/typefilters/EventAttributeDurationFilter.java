/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

public class EventAttributeDurationFilter {
    public static boolean toKeep(ATrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(ATrace trace, LogFilterRule logFilterRule) {
        String attributeKey = logFilterRule.getKey();



        double durRangeFrom = 0, durRangeTo = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) durRangeFrom = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) durRangeTo = ruleValue.getDoubleValue();
        }

        String attributeValue = logFilterRule.getPrimaryValues().iterator().next().getKey();

        List<Double> durList = getAttributeDurationList(trace, attributeKey, attributeValue);

        if (durList.size() < 1) {
            return false;
        } else {
            for (double dur : durList) {
                if (dur >= durRangeFrom && dur <= durRangeTo) {
                    return true;
                }
            }
        }

        return false;
    }

    private static List<Double> getAttributeDurationList(ATrace trace, String key, String value) {
        List<Double> durList= new ArrayList<>();

        List<AActivity> activityList = trace.getActivityList();

        for (int i = 0; i < activityList.size(); i++) {
            AActivity activity = activityList.get(i);
            String attrVal = getAttributeValue(activity, key);
            if (attrVal.equals(value)) {
                durList.add(Long.valueOf(activity.getDuration()).doubleValue());
            }
        }

        return durList;
    }

    private static String getAttributeValue(AActivity activity, String key) {
        return activity.getAttributeValue(key);
//        switch (key) {
//            case "concept:name": return activity.getName();
//            case "org:resource": return activity.getEventList().get(0).getResource();
//            default:
//                UnifiedMap<String, String> attrMap = activity.getEventList().get(0).getAttributeMap();
//                return attrMap.get(key);
//        }
    }
}
