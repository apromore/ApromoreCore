package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;

import java.util.*;

public class CaseIDDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all cases where case ID is ";
        Set<RuleValue> ruleValues = logFilterRule.getPrimaryValues();
        if (ruleValues.size() == 1) desc += "equal to [";
        else desc += "in [";

        List<RuleValue> ruleValueList = new ArrayList<RuleValue>(ruleValues);
        Collections.sort(ruleValueList);

        if (allNumeric(ruleValueList)) {
            List<CaseVariantDesc.Pair> pairList = getPairs(ruleValueList);

            for (int i = 0; i < pairList.size(); i++) {
                desc += pairList.get(i).toString();
                if (i < pairList.size() -1) desc += ", ";
            }
        } else {
            for (int i = 0; i < ruleValueList.size(); i++) {
                desc += ruleValueList.get(i).getStringValue();
                if (i < ruleValueList.size() -1) desc += ", ";
            }
        }

        desc += "]";

        return desc;
    }

    private static boolean allNumeric(List<RuleValue> ruleValueList) {
        for (RuleValue rv : ruleValueList) {
            String stringValue = rv.getStringValue();
            if (!stringValue.matches("-?\\d+(\\.\\d+)?") || stringValue.contains("_")) {
                return false;
            }
        }
        return true;
    }

    private static List<CaseVariantDesc.Pair> getPairs(List<RuleValue> ruleValueList) {

        List<CaseVariantDesc.Pair> pairList = new ArrayList<CaseVariantDesc.Pair>();

        BitSet marked  = new BitSet(ruleValueList.size());
        for (int i = 0; i < ruleValueList.size(); i++) {
            if (!marked.get(i)) {
                RuleValue ruleValue = ruleValueList.get(i);
                int intVal = ruleValue.getIntValue();
                int stopIndex = i;

                if (ruleValueList.size() > 1) {
                    for (int j = (i + 1); j < ruleValueList.size(); j++) {
                        RuleValue jRuleValue = ruleValueList.get(j);
                        int jIntVal = jRuleValue.getIntValue();

                        int preRuleIntVal = ruleValueList.get(j-1).getIntValue();

                        if (jIntVal == preRuleIntVal + 1) {
                            marked.set(j, true);
                            if (j == ruleValueList.size()-1) {
                                stopIndex = j;
                            }
                        } else {
                            stopIndex = j -1;
                            break;
                        }

                    }
                }


                int stopIntVal = ruleValueList.get(stopIndex).getIntValue();
                CaseVariantDesc.Pair pair = new CaseVariantDesc.Pair(intVal, stopIntVal);
                pairList.add(pair);
            }
        }

        return pairList;
    }

    static class Pair {
        public int fromVal, toVal;
        public Pair(int fromVal, int toVal){
            this.fromVal = fromVal;
            this.toVal = toVal;
        }
        public String toString() {
            if (fromVal != toVal) return fromVal + " to " + toVal;
            else return fromVal + "";
        }
    }
}
