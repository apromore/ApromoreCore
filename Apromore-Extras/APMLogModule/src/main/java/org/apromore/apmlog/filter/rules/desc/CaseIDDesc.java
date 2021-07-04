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
import java.util.Map;
import java.util.Set;

/**
 * @author Chii Chang
 */
public class CaseIDDesc {

    public static String getDescription(LogFilterRule logFilterRule) {

        StringBuilder desc = new StringBuilder();
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        desc.append(choice.substring(0, 1).toUpperCase() + choice.substring(1) + " all cases where case ID is ");
        Set<RuleValue> ruleValues = logFilterRule.getPrimaryValues();

        if (ruleValues != null && !ruleValues.isEmpty()) {

            Map<String, String> labels = ruleValues.iterator().next().getCustomAttributes();

            if (labels.size() == 1) desc.append("equal to [");
            else desc.append(" [");

            int count = 0;
            for (String s : labels.keySet()) {
                desc.append(s.intern());
                if (count < labels.size() - 1) desc.append(", ");

                count += 1;
            }
        }

        desc.append("]");
        return desc.toString();

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

    private static List<Pair> getPairs(List<RuleValue> ruleValues) {

        boolean hasDecimal = true;

        for (RuleValue rv : ruleValues) {
            String sVal = rv.getStringValue();
            if (!sVal.contains(".")) {
                hasDecimal = false;
                break;
            }
        }

        List<Pair> pairList = new ArrayList<Pair>();

        List<Number> allVals;

        if (!hasDecimal) {
            List<Long> allLongVals = new ArrayList<>();
            for (RuleValue rv : ruleValues) {
                allLongVals.add(Long.valueOf(rv.getStringValue()));
            }
            Collections.sort(allLongVals);
            allVals = new ArrayList<>(allLongVals);
        } else {
            List<Double> allDoubleVals = new ArrayList<>();
            for (RuleValue rv : ruleValues) {
                allDoubleVals.add(Double.valueOf(rv.getStringValue()));
            }
            Collections.sort(allDoubleVals);
            allVals = new ArrayList<>(allDoubleVals);
        }




        BitSet marked  = new BitSet(allVals.size());

        for (int i = 0; i < allVals.size(); i++) {
            if (!marked.get(i)) {

                Number val = allVals.get(i);

                int stopIndex = i;

                if (allVals.size() > 1) {
                    for (int j = (i + 1); j < allVals.size(); j++) {
                        Number jVal = allVals.get(j);

                        Number preVal = allVals.get(j-1);

                        if (jVal.doubleValue() == preVal.doubleValue() + 1) {
                            marked.set(j, true);
                            if (j == allVals.size()-1) {
                                stopIndex = j;
                            }
                        } else {
                            stopIndex = j -1;
                            break;
                        }

                    }
                }

                Number stopVal = allVals.get(stopIndex);

                Pair pair = new Pair(val, stopVal);
                pairList.add(pair);
            }
        }

        return pairList;
    }

    static class Pair {
        public Number fromVal, toVal;
        public Pair(Number fromVal, Number toVal){
            this.fromVal = fromVal;
            this.toVal = toVal;
        }
        public String toString() {
            if (fromVal.doubleValue() != toVal.doubleValue()) return fromVal + " to " + toVal;
            else return fromVal + "";
        }
    }
}
