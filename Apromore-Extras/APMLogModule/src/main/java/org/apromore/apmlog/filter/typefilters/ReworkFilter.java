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

import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chii Chang
 */
public class ReworkFilter {
    public static boolean toKeep(PTrace pTrace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(pTrace, logFilterRule);
            default: return !conformRule(pTrace, logFilterRule);
        }
    }

    private static boolean conformRule(PTrace pTrace, LogFilterRule logFilterRule) {

        Set<RuleValue> primaryVals = logFilterRule.getPrimaryValues();

        UnifiedMap<String, Integer> ruleActMatchMap = new UnifiedMap<>();

        for (RuleValue rv : primaryVals) {
            String key = rv.getKey();
            if (ruleActMatchMap.containsKey(key)) {
                int num = ruleActMatchMap.get(key) + 1;
                ruleActMatchMap.put(key, num);
            } else ruleActMatchMap.put(key, 1);
        }

        Inclusion inclusion = logFilterRule.getInclusion();

        Map<String, Integer> actNameOccurMap = getActivityNameOccurMap(pTrace);

        UnifiedMap<String, Integer> matchedCountMap = new UnifiedMap<>();
        for (String key : ruleActMatchMap.keySet()) {
            matchedCountMap.put(key, 0);
        }

        boolean reqNotOccur = false;

        for (RuleValue rv : primaryVals) {
            String rvKey = rv.getKey();
            OperationType operationType = rv.getOperationType();
            int intVal = rv.getIntValue();

            if (actNameOccurMap.keySet().contains(rvKey)) {
                int occur = actNameOccurMap.get(rvKey);
                boolean proceed = false;
                if (operationType == OperationType.GREATER) {
                    if (occur > intVal) {
                        proceed = true;
                    }
                }

                if (operationType == OperationType.GREATER_EQUAL) {
                    if (occur >= intVal) {
                        proceed = true;
                    }
                }

                if (operationType == OperationType.LESS) {
                    if (occur < intVal) {
                        proceed = true;
                    }
                }

                if (operationType == OperationType.LESS_EQUAL) {
                    if (occur <= intVal) {
                        proceed = true;
                    }
                }

                if (proceed) {
                    int count = matchedCountMap.get(rvKey) + 1;
                    matchedCountMap.put(rvKey, count);
                }
            } else {
                if (operationType == OperationType.GREATER_EQUAL && intVal == 0) reqNotOccur = true;
            }
        }

        if (reqNotOccur) {
            for (RuleValue rv : primaryVals) {
                String rvKey = rv.getKey();

                if (!actNameOccurMap.keySet().contains(rvKey)) {
                    int count = matchedCountMap.get(rvKey) + 1;
                    matchedCountMap.put(rvKey, count);
                }
            }
        }


        int totalMatchedRule = 0;

        for (String key : ruleActMatchMap.keySet()) {
            int reqMatch = ruleActMatchMap.get(key);
            int realMatch = matchedCountMap.get(key);
            if (reqMatch <= realMatch) totalMatchedRule += 1;
        }

        if (totalMatchedRule == 0){
            return false;
        }
        if (inclusion == Inclusion.ALL_VALUES && totalMatchedRule < ruleActMatchMap.size()){
            return false;
        }

        return true;
    }

    private static Map<String, Integer> getActivityNameOccurMap(PTrace pTrace) {


        Map<String, Integer> actNameOccurMap = new HashMap<>();

        List<ActivityInstance> activityInstanceList = pTrace.getActivityInstances();

        for (int i = 0; i < activityInstanceList.size(); i++) {
            String actName = activityInstanceList.get(i).getName();
            if (actNameOccurMap.containsKey(actName)) {
                int occur = actNameOccurMap.get(actName) + 1;
                actNameOccurMap.put(actName, occur);
            } else actNameOccurMap.put(actName, 1);
        }

        return actNameOccurMap;
    }
}
