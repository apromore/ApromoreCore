package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;

import java.util.*;

public class ReworkDesc {
    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) +
                " all cases where their activities contain ";

        Inclusion inclusion = logFilterRule.getInclusion();

        Set<RuleValue> primaryValues = logFilterRule.getPrimaryValues();

        Map<String, List<String>> mergedDescMap = new HashMap<>();
        for (RuleValue rv : primaryValues) {
            mergedDescMap.put(rv.getKey(), new ArrayList<>());
        }

        for (RuleValue rv : primaryValues) {
            String key = rv.getKey();
            int occur = rv.getIntValue();


            String subDesc = "";
            if (rv.getOperationType() == OperationType.GREATER) {
                if (occur == 1) subDesc = "more than " + occur + " time";
                else subDesc = "more than " + occur + " times";
            }

            if (rv.getOperationType() == OperationType.GREATER_EQUAL) {
                if (occur == 1) subDesc= "at least " + occur + " time";
                else subDesc= "at least " + occur + " times";
            }

            if (rv.getOperationType() == OperationType.LESS) {
                if (occur == 1) subDesc= "less than " + occur + " time";
                else subDesc= "less than " + occur + " times";
            }

            if (rv.getOperationType() == OperationType.LESS_EQUAL) {
                if (occur == 1) subDesc= "no more than " + occur + " time";
                else subDesc= "no more than " + occur + " times";
            }

            mergedDescMap.get(key).add(subDesc);
        }

        int count = 0;
        for (String key : mergedDescMap.keySet()) {
            count += 1;
            List<String> subDesc = mergedDescMap.get(key);

            subDesc = reorderList(subDesc);

            desc += key + " (occur ";

            for (int i = 0; i < subDesc.size(); i++) {
                desc += subDesc.get(i);
                if (subDesc.size() > 1 && i == 0) {
                    desc += " and ";
                }
            }
            desc += ") ";
            if (count < mergedDescMap.size()) {
                if (inclusion == Inclusion.ALL_VALUES) desc += "AND ";
                else desc += "OR ";
            }
        }





        return desc;
    }

    private static List<String> reorderList(List<String> descList) {
        if (descList.size() > 1) {
            if (!descList.get(descList.size()-1).contains("less than") &&
            !descList.get(descList.size()-1).contains("no more than")) {
                String lowerBoundDesc = descList.get(descList.size()-1);
                descList.remove(descList.size()-1);
                descList.add(0, lowerBoundDesc);
            }
        }
        return descList;
    }
}
