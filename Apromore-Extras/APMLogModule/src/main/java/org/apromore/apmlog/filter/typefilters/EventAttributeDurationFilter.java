/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;

import java.util.ArrayList;
import java.util.List;

public class EventAttributeDurationFilter extends AbstractAttributeDurationFilter {
    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(PTrace trace, LogFilterRule logFilterRule) {
        String attributeKey = logFilterRule.getKey();

        double durRangeFrom = 0, durRangeTo = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) durRangeFrom = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) durRangeTo = ruleValue.getDoubleValue();
        }

        String attributeValue = logFilterRule.getPrimaryValues().iterator().next().getKey();

        List<Double> durList = getAttributeDurationList(trace, attributeKey, attributeValue);

        if (durList.size() < 1) return false;

        if (containsInvalidDuration(durList, durRangeFrom, durRangeTo)) return false;

        for (double dur : durList) {
            if (dur >= durRangeFrom && dur <= durRangeTo) {
                return true;
            }
        }

        return false;
    }

    private static List<Double> getAttributeDurationList(PTrace trace, String key, String value) {
        List<Double> durList= new ArrayList<>();

        List<ActivityInstance> activityList = trace.getActivityInstances();

        for (ActivityInstance activity : activityList) {
            String attrVal = getAttributeValue(activity, key);
            if (attrVal.equals(value)) {
                durList.add(activity.getDuration());
            }
        }

        return durList;
    }

    private static String getAttributeValue(ActivityInstance activity, String key) {
        return activity.getAttributeValue(key);
    }
}
