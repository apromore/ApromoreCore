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
import org.apromore.apmlog.util.CalendarDuration;
import org.apromore.calendar.model.CalendarModel;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttributeArcDurationFilter extends AbstractAttributeDurationFilter {
    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(PTrace trace, LogFilterRule logFilterRule) {
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

        List<ActivityInstance> activityList = trace.getActivityInstances().stream()
                .filter(x -> trace.getValidEventIndexBS().get(x.getImmutableEventIndexes().get(0)))
                .collect(Collectors.toList());

        List<Double> durList = getAttributeToAttributeDurationList(activityList, attributeKey,
                fromVal, toVal, lowBoundVal, upBoundVal);

        if (containsInvalidDuration(durList, lowBoundVal, upBoundVal)) return false;

        return durList.size() > 0;
    }

    private static List<Double> getAttributeToAttributeDurationList(List<ActivityInstance> activityList, String attributeKey,
                                                                    String value1, String value2,
                                                                    double lowBoundVal, double upBoundVal) {

        CalendarModel calendarModel = activityList.get(0).getCalendarModel();

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
                        long act1EndTime = activity1.getEndTime();
                        long act2StartTime = activity2.getStartTime();
                        double duration = CalendarDuration.getDuration(calendarModel, act1EndTime, act2StartTime);
                        if (duration >= lowBoundVal && duration <= upBoundVal)
                            durList.add(duration);

                    }
                }
            }
        }

        return durList;
    }

}
