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
