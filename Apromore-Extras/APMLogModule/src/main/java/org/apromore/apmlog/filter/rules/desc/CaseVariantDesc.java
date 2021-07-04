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
package org.apromore.apmlog.filter.rules.desc;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;

import java.util.BitSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CaseVariantDesc {

    public static String getDescription(LogFilterRule logFilterRule) {
        String desc = "";
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc += choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all cases where case variant is ";
        Set<RuleValue> ruleValues = logFilterRule.getPrimaryValues();
        if (ruleValues.size() == 1) desc += "equal to [";
        else desc += " [";

        List<RuleValue> ruleValueList = new ArrayList<>(ruleValues);
        Collections.sort(ruleValueList);

        List<Pair> pairList = getPairs(ruleValueList);

        for (int i = 0; i < pairList.size(); i++) {
            desc += pairList.get(i).toString();
            if (i < pairList.size() -1) desc += ", ";
        }


        desc += "]";

        return desc;
    }

    private static List<Pair> getPairs(List<RuleValue> ruleValueList) {

        List<Pair> pairList = new ArrayList<>();

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
                Pair pair = new Pair(intVal, stopIntVal);
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
