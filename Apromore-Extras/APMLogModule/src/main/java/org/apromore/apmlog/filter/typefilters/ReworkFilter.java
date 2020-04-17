/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReworkFilter {
    public static boolean toKeep(LaTrace pTrace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(pTrace, logFilterRule);
            default: return !conformRule(pTrace, logFilterRule);
        }
    }

    private static boolean conformRule(LaTrace pTrace, LogFilterRule logFilterRule) {


        Inclusion inclusion = logFilterRule.getInclusion();

        Map<String, Integer> actNameOccurMap = getActivityNameOccurMap(pTrace);

        Set<RuleValue> primaryVals = logFilterRule.getPrimaryValues();

        int matchCount = 0;

        for (RuleValue rv : primaryVals) {
            String key = rv.getKey();
            if (actNameOccurMap.keySet().contains(key)) {
                int occur = actNameOccurMap.get(key);
                OperationType operationType = rv.getOperationType();
                int intVal = rv.getIntValue();

                if (operationType == OperationType.GREATER && occur > intVal) matchCount += 1;
                if (operationType == OperationType.GREATER_EQUAL && occur >= intVal) matchCount += 1;
                if (operationType == OperationType.LESS && occur < intVal) matchCount += 1;
                if (operationType == OperationType.LESS_EQUAL && occur <= intVal) matchCount += 1;

            }
        }

        if (matchCount == 0) return false;
        if (inclusion == Inclusion.ALL_VALUES && matchCount < primaryVals.size()) return false;

        return true;
    }

    private static Map<String, Integer> getActivityNameOccurMap(LaTrace pTrace) {


        Map<String, Integer> actNameOccurMap = new HashMap<>();

        List<AActivity> aActivityList = pTrace.getActivityList();

        for (int i = 0; i < aActivityList.size(); i++) {
            String actName = aActivityList.get(i).getName();
            if (actNameOccurMap.containsKey(actName)) {
                int occur = actNameOccurMap.get(actName) + 1;
                actNameOccurMap.put(actName, occur);
            } else actNameOccurMap.put(actName, 1);
        }

        return actNameOccurMap;
    }
}
