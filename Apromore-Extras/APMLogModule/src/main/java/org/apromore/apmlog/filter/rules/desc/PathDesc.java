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
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PathDesc extends AttributeDesc{

    public static String getDescription(LogFilterRule logFilterRule) {

        String mainAttrKey = getKeyLabel(logFilterRule.getKey());
        List<String> fromList = new ArrayList<>();
        List<String> toList = new ArrayList<>();

        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.FROM) {
                String fromVal = ruleValue.getStringValue();
                fromList.add(fromVal);
            }
            if (operationType == OperationType.TO) {
                String toVal = ruleValue.getStringValue();
                toList.add(toVal);
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(logFilterRule.getChoice() == Choice.RETAIN ? "Retain" : "Remove");
        sb.append(" all cases that contain the ");
        sb.append(logFilterRule.getFilterType() == FilterType.DIRECT_FOLLOW ?
                        "directly-follows relation " : "eventually-follows relation ");

        for (int i = 0; i < fromList.size(); i++) {
            String fromVal = fromList.get(i);
            for (int j = 0; j < toList.size(); j++) {
                String toVal = toList.get(j);
                sb.append("'" + fromVal + "' -> '" + toVal + "'");
                if (j < toList.size()-1) sb.append(" OR ");
            }
            if (i < fromList.size()-1) sb.append(", ");
        }

        sb.append(" between " + mainAttrKey + " nodes ");

        Set<RuleValue> secondaryValues = logFilterRule.getSecondaryValues();

        if (logFilterRule.getSecondaryValues() != null) {
            OperationType requireOpt = OperationType.UNKNOWN;
            String requireAttrKey = "";
            OperationType lowerBoundOpt = OperationType.UNKNOWN;
            OperationType upperBoundOpt = OperationType.UNKNOWN;
            long lowerBoundVal = -1;
            long upperBoundVal = -1;
            for (RuleValue ruleValue : secondaryValues) {
                if (ruleValue.getOperationType() == OperationType.EQUAL) {
                    requireOpt = OperationType.EQUAL;
                    requireAttrKey = ruleValue.getKey();
                }
                if (ruleValue.getOperationType() == OperationType.NOT_EQUAL) {
                    requireOpt = OperationType.NOT_EQUAL;
                    requireAttrKey = ruleValue.getKey();
                }
                if (ruleValue.getOperationType() == OperationType.GREATER) {
                    lowerBoundOpt = OperationType.GREATER;
                    lowerBoundVal = ruleValue.getLongValue();
                }
                if (ruleValue.getOperationType() == OperationType.GREATER_EQUAL) {
                    lowerBoundOpt = OperationType.GREATER_EQUAL;
                    lowerBoundVal = ruleValue.getLongValue();
                }
                if (ruleValue.getOperationType() == OperationType.LESS) {
                    upperBoundOpt = OperationType.LESS;
                    upperBoundVal = ruleValue.getLongValue();
                }
                if (ruleValue.getOperationType() == OperationType.LESS_EQUAL) {
                    upperBoundOpt = OperationType.LESS_EQUAL;
                    upperBoundVal = ruleValue.getLongValue();
                }
            }

            if (requireOpt == OperationType.EQUAL) {
                sb.append("and have same " + getKeyLabel(requireAttrKey) + " ");
            }
            if (requireOpt == OperationType.NOT_EQUAL) {
                sb.append("and have different " + getKeyLabel(requireAttrKey) + " ");
            }
            if (lowerBoundOpt != OperationType.UNKNOWN || upperBoundOpt != OperationType.UNKNOWN) {
                sb.append("and time interval ");
            }
            if (lowerBoundOpt == OperationType.GREATER) {
                sb.append("is greater than " + TimeUtil.durationStringOf(lowerBoundVal) + " ");
                if (upperBoundOpt != OperationType.UNKNOWN) sb.append("and ");
            }
            if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                sb.append("is greater than equal to " + TimeUtil.durationStringOf(lowerBoundVal) + " ");
                if (upperBoundOpt != OperationType.UNKNOWN) sb.append("and ");
            }
            if (upperBoundOpt == OperationType.LESS) {
                sb.append("is less than " + TimeUtil.durationStringOf(upperBoundVal) + " ");
            }
            if (upperBoundOpt == OperationType.LESS_EQUAL) {
                sb.append("is less than equal to " + TimeUtil.durationStringOf(upperBoundVal) + " ");
            }
        }

        return sb.toString();
    }
}
