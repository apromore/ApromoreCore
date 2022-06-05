/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.util.TimeUtil;
import org.apromore.apmlog.xes.XESAttributeCodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Chii Chang
 * modified: 2022-05-30 by Chii Chang
 * modified: 2022-06-03 by Chii Chang
 */
public class PathDesc extends AttributeDesc{

    @Getter
    @AllArgsConstructor
    @ToString
    static class AttributeConstraint {
        private String attribute;
        private OperationType operationType;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class IntervalBound {
        private OperationType operationType;
        private long value;
    }

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
                sb.append("'").append(fromVal).append("' -> '").append(toVal).append("'");
                if (j < toList.size()-1) sb.append(" OR ");
            }
            if (i < fromList.size()-1) sb.append(", ");
        }

        sb.append(" between ").append(mainAttrKey).append(" nodes ");

        configRequirements(sb, logFilterRule);

        return sb.toString();
    }

    private static void configRequirements(StringBuilder sb, LogFilterRule logFilterRule) {
        Set<RuleValue> secondaryValues = logFilterRule.getSecondaryValues();
        Set<RuleValue> thirdlyValues = logFilterRule.getThirdlyValues();

        if (thirdlyValues != null && !thirdlyValues.isEmpty()) {
            configNumericReqInvolved(thirdlyValues, secondaryValues, sb);
            return;
        }

        AttributeConstraint ac = getAttributeConstraint(secondaryValues);

        if (ac != null) {
            String desc = getAttributeConstraintByOperationType(ac.operationType, ac.getAttribute());
            sb.append(desc);
        }

        String timeIntervalDesc = getTimeIntervalDesc(secondaryValues);
        if (!timeIntervalDesc.isEmpty()) {
            sb.append(timeIntervalDesc);
        }
    }

    private static void configNumericReqInvolved(Set<RuleValue> thirdlyValues,
                                                 Set<RuleValue> secondaryValues,
                                                 StringBuilder sb) {

        RuleValue rv3 = thirdlyValues.iterator().next();
        String reqKey = rv3.getKey();
        OperationType operationType = rv3.getOperationType();
        String operationText = getAttributeConstraintByOperationType(operationType, reqKey);
        sb.append(operationText);

        if (secondaryValues == null || secondaryValues.isEmpty()) {
            return;
        }

        String timeIntervalDesc = getTimeIntervalDesc(secondaryValues);
        sb.append(timeIntervalDesc);
    }

    private static String getTimeIntervalDesc(Set<RuleValue> secondaryValues) {
        IntervalBound lowerBound = getIntervalLowerBound(secondaryValues);
        IntervalBound upperBound = getIntervalUpperBound(secondaryValues);

        StringBuilder sb = new StringBuilder();

        if (lowerBound != null || upperBound != null) {
            sb.append(" and time interval ");
        }

        if (lowerBound != null) {
            OperationType lowerBoundOpt = lowerBound.getOperationType();
            long lowerBoundVal = lowerBound.getValue();
            if (lowerBoundOpt == OperationType.GREATER) {
                sb.append("is greater than ").append(TimeUtil.durationStringOf(lowerBoundVal));
            } else {
                sb.append("is greater than equal to ").append(TimeUtil.durationStringOf(lowerBoundVal));
            }

            if (upperBound != null) {
                sb.append("and ");
            }
        }

        if (upperBound != null) {
            OperationType upperBoundOpt = upperBound.getOperationType();
            long upperBoundVal = upperBound.getValue();

            if (upperBoundOpt == OperationType.LESS) {
                sb.append("is less than ").append(TimeUtil.durationStringOf(upperBoundVal));
            }
            if (upperBoundOpt == OperationType.LESS_EQUAL) {
                sb.append("is less than equal to ").append(TimeUtil.durationStringOf(upperBoundVal));
            }
        }

        return sb.toString();
    }

    private static String getAttributeConstraintByOperationType(OperationType operationType,
                                                                String reqKey) {
        String operationText;

        switch (operationType) {
            case EQUAL:
                operationText = getAttributeConstraintDesc(reqKey, "Equal to ");
                break;
            case NOT_EQUAL:
                operationText = getAttributeConstraintDesc(reqKey, "Different than ");
                break;
            case GREATER:
                operationText = getAttributeConstraintDesc(reqKey, "Greater than ");
                break;
            case GREATER_EQUAL:
                operationText = getAttributeConstraintDesc(reqKey, "Greater than or Equal to ");
                break;
            case LESS:
                operationText = getAttributeConstraintDesc(reqKey, "Less than ");
                break;
            case LESS_EQUAL:
                operationText = getAttributeConstraintDesc(reqKey, "Less than or Equal to ");
                break;
            default:
                operationText = "";
                break;
        }

        return operationText;
    }

    private static String getAttributeConstraintDesc(String reqKey, String input) {
        return "and the '" + reqKey + "' in the From node is " + input + "that in the To node";
    }

    public static AttributeConstraint getAttributeConstraint(Set<RuleValue> secondaryValues) {
        Set<OperationType> types = Set.of(OperationType.EQUAL, OperationType.NOT_EQUAL);
        return secondaryValues.stream()
                .filter(x -> types.contains(x.getOperationType()))
                .map(x -> new AttributeConstraint(XESAttributeCodes.getDisplayLabelForSingle(x.getKey()),
                        x.getOperationType()))
                .findFirst()
                .orElse(null);
    }

    public static IntervalBound getIntervalLowerBound(Set<RuleValue> secondaryValues) {
        Set<OperationType> operationTypes = Set.of(OperationType.GREATER, OperationType.GREATER_EQUAL);
        return getIntervalBoundOf(operationTypes, secondaryValues);
    }

    public static IntervalBound getIntervalUpperBound(Set<RuleValue> secondaryValues) {
        Set<OperationType> operationTypes = Set.of(OperationType.LESS, OperationType.LESS_EQUAL);
        return getIntervalBoundOf(operationTypes, secondaryValues);
    }

    private static IntervalBound getIntervalBoundOf(Set<OperationType> operationTypes, Set<RuleValue> secondaryValues) {
        if (secondaryValues == null) {
            return null;
        }

        return secondaryValues.stream()
                .filter(x -> operationTypes.contains(x.getOperationType()))
                .map(x -> new IntervalBound(x.getOperationType(), x.getLongValue()))
                .findFirst()
                .orElse(null);
    }
}
