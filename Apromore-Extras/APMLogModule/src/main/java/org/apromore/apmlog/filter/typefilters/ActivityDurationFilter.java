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
import org.apromore.apmlog.LaTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;

import java.util.ArrayList;
import java.util.List;

public class ActivityDurationFilter {

    public static boolean toKeep(LaTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(LaTrace trace, LogFilterRule logFilterRule) {
        String attributeKey = logFilterRule.getKey();



        double durRangeFrom = 0, durRangeTo = 0;
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) durRangeFrom = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) durRangeTo = ruleValue.getDoubleValue();
        }

        List<Double> durList = getActivityDurationList(trace, attributeKey);
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

    private static List<Double> getActivityDurationList(LaTrace trace, String activityName) {
        List<Double> durList= new ArrayList<>();

        List<AActivity> activityList = trace.getActivityList();

        for (int i = 0; i < activityList.size(); i++) {
            AActivity activity = activityList.get(i);
            if (activity.getName().equals(activityName)) {
                durList.add(Long.valueOf(activity.getDuration()).doubleValue());
            }
        }

        return durList;


//        List<Long> durList= new ArrayList<>();
//
//        BitSet validEventsIndexBS = trace.getValidEventIndexBitSet();
//        UnifiedSet<Integer> validEventIndexes = new UnifiedSet<>();
//        List<AEvent> eventList = trace.getEventList();
//        for (int i = 0; i < eventList.size(); i++) {
//            if (validEventsIndexBS.get(i)) {
//                validEventIndexes.add(i);
//            }
//        }
//
//        List<AActivity> activityList = trace.getActivityList();
//        for (int i = 0; i < activityList.size(); i++) {
//            AActivity activity = activityList.get(i);
//            boolean allEventsValid = true;
//            List<AEvent> actEvents = activity.getEventList();
//            for (int j = 0; j < actEvents.size(); j++) {
//                if (!validEventIndexes.contains(actEvents.get(j).getIndex())) {
//                    allEventsValid = false;
//                    break;
//                }
//            }
//            if (allEventsValid) {
//                String actName = activity.getName();
//                if (actName.equals(activityName) ) {
//                    durList.add(activity.getDuration());
//                }
//            }
//        }
//
//        return durList;
    }
}
