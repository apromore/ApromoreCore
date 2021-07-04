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
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReworkDesc {
    public static String getDescription(LogFilterRule logFilterRule) {
        StringBuilder sb = new StringBuilder();
        String choice = logFilterRule.getChoice().toString().toLowerCase();
        sb.append(choice.substring(0, 1).toUpperCase() + choice.substring(1) +
                " all cases where Activity ");

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

            sb.append("'" + key + "' occurs [");

            for (int i = 0; i < subDesc.size(); i++) {
                sb.append(subDesc.get(i));
                if (subDesc.size() > 1 && i == 0) {
                    sb.append(" and ");
                }
            }
            sb.append("] ");
            if (count < mergedDescMap.size()) {
                if (inclusion == Inclusion.ALL_VALUES) sb.append("AND ");
                else sb.append("OR ");
            }
        }

        return sb.toString();
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
