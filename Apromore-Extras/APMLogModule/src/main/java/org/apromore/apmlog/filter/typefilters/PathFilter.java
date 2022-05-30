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
package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.rules.desc.PathDesc;
import static org.apromore.apmlog.filter.rules.desc.PathDesc.getIntervalLowerBound;
import static org.apromore.apmlog.filter.rules.desc.PathDesc.getIntervalUpperBound;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.util.CalendarDuration;
import org.apromore.apmlog.util.Util;
import org.apromore.calendar.model.CalendarModel;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.List;
import java.util.Set;

/**
 * @author Chii Chang
 * modified: 2022-05-30 by Chii Chang
 */
public class PathFilter {

    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        if (choice == Choice.RETAIN) {
            return conformRule(trace, logFilterRule);
        }
        return !conformRule(trace, logFilterRule);
    }

    private static boolean conformRule(PTrace trace, LogFilterRule logFilterRule) {

        FilterType filterType = logFilterRule.getFilterType();

        String attributeKey = logFilterRule.getKey();

        UnifiedSet<String> fromValSet = getFromValSet(logFilterRule);
        UnifiedSet<String> toValSet = getToValSet(logFilterRule);


        switch (filterType) {
            case DIRECT_FOLLOW:
                return conformDirectFollow(trace, attributeKey, fromValSet, toValSet, logFilterRule);

            case EVENTUAL_FOLLOW:
                return conformEventualFollow(trace, attributeKey, fromValSet, toValSet, logFilterRule);
        }

        return false;

    }

    private static boolean conformDirectFollow(PTrace trace,
                                               String attributeKey,
                                               UnifiedSet<String> fromValSet,
                                               UnifiedSet<String> toValSet,
                                               LogFilterRule logFilterRule) {

        int matchCount = 0;
        List<ActivityInstance> activityList = trace.getActivityInstances();

        Set<RuleValue> secoVals = logFilterRule.getSecondaryValues();

        if (fromValSet.contains("[Start]") && (secoVals == null || secoVals.isEmpty())) {
            ActivityInstance firstAct = activityList.get(0);
            String val = getAttributeValue(firstAct, attributeKey);
            if (val != null && toValSet.contains(val)) {
                return true;
            }
        }

        if (toValSet.contains("[End]")) {
            ActivityInstance lastAct = activityList.get(activityList.size() - 1);
            String val = getAttributeValue(lastAct, attributeKey);
            if (val != null && fromValSet.contains(val)) {
                return true;
            }
        }

        for (ActivityInstance act1 : activityList) {
            String val = getAttributeValue(act1, attributeKey);

            if (val != null && fromValSet.contains(val) && activityList.get(activityList.size()-1) != act1) {
                ActivityInstance act2 = trace.getNextOf(act1);
                String nextVal = getAttributeValue(act2, attributeKey);
                if (nextVal != null && toValSet.contains(nextVal) && conformRequirement(act1, act2, logFilterRule)) {
                    matchCount += 1;
                }
            }

            if (val != null && toValSet.contains(val) && trace.getPreviousOf(act1) != null) {
                ActivityInstance pAct = trace.getPreviousOf(act1);
                String pVal = getAttributeValue(pAct, attributeKey);
                if (pVal != null && fromValSet.contains(pVal) && conformRequirement(pAct, act1, logFilterRule)) {
                    matchCount += 1;
                }
            }
        }

        return matchCount > 0;
    }

    private static boolean conformEventualFollow(PTrace trace,
                                                 String attributeKey,
                                                 UnifiedSet<String> fromValSet,
                                                 UnifiedSet<String> toValSet,
                                                 LogFilterRule logFilterRule) {
        List<ActivityInstance> activityList = trace.getActivityInstances();

        if (activityList.size() < 2) return false;

        for (int i = 0; i < activityList.size(); i++) {
            ActivityInstance act1 = activityList.get(i);
            String val = getAttributeValue(act1, attributeKey);

            if (val != null && fromValSet.contains(val) && i < activityList.size()-1) {

                List<ActivityInstance> subList = activityList.subList(i + 1, activityList.size());

                for (ActivityInstance nAct : subList) {
                    String nVal = getAttributeValue(nAct, attributeKey);

                    if (nVal != null && toValSet.contains(nVal)) {
                        if (conformRequirement(act1, nAct, logFilterRule)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private static String getAttributeValue(ActivityInstance activity, String key) {
        if (activity == null || key == null)
            return null;

        return activity.getAttributes().containsKey(key) ? activity.getAttributeValue(key) : null;
    }

    private static UnifiedSet<String> getFromValSet(LogFilterRule logFilterRule) {
        UnifiedSet<String> valSet = new UnifiedSet<>();
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.FROM) valSet.add(ruleValue.getStringValue());
        }
        return valSet;
    }

    private static UnifiedSet<String> getToValSet(LogFilterRule logFilterRule) {
        UnifiedSet<String> valSet = new UnifiedSet<>();
        for (RuleValue ruleValue : logFilterRule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.TO) valSet.add(ruleValue.getStringValue());
        }
        return valSet;
    }

    private static boolean conformRequirement(ActivityInstance act1,
                                              ActivityInstance act2,
                                              LogFilterRule logFilterRule) {

        Set<RuleValue> secondaryValues = logFilterRule.getSecondaryValues();
        Set<RuleValue> thirdlyValues = logFilterRule.getThirdlyValues();

        if (thirdlyValues != null && !thirdlyValues.isEmpty()) {
            return confirmRequirementWithThirdlyValues(thirdlyValues, secondaryValues, act1, act2);
        }

        RuleValue consRV = getConstraintValue(secondaryValues);

        if (consRV != null && !isTextConstraintMatched(consRV.getKey(), consRV.getOperationType(), act1, act2)) {
            return false;
        }

        return confirmIntervalBounds(secondaryValues, act1, act2);
    }

    private static RuleValue getConstraintValue(Set<RuleValue> secondaryValues) {
        if (secondaryValues == null) {
            return null;
        }

        return secondaryValues.stream()
                .filter(x -> x.getOperationType() == OperationType.EQUAL ||
                        x.getOperationType() == OperationType.NOT_EQUAL)
                .findFirst()
                .orElse(null);
    }

    private static boolean confirmInterval(OperationType operationType, ActivityInstance act1, ActivityInstance act2,
                                           double interval) {

        CalendarModel calendarModel = act1.getCalendarModel();

        double duration;

        if (act2 == null) {
            duration = act1.getDuration();
        } else {
            long st = act1.getStartTime();
            long et = act2.getEndTime();
            duration = CalendarDuration.getDuration(calendarModel, st, et);
        }

        switch (operationType) {
            case GREATER: return duration > interval;
            case GREATER_EQUAL: return duration >= interval;
            case LESS: return duration < interval;
            case LESS_EQUAL: return duration <= interval;
            default: return false;
        }
    }


    private static boolean confirmRequirementWithThirdlyValues(Set<RuleValue> thirdlyValues,
                                                               Set<RuleValue> secondaryValues,
                                                               ActivityInstance act1,
                                                               ActivityInstance act2) {
        RuleValue rv3 = thirdlyValues.iterator().next();
        String reqAttr = rv3.getKey();
        OperationType rv3OptType = rv3.getOperationType();
        if (!act1.getAttributes().containsKey(reqAttr) || !act2.getAttributes().containsKey(reqAttr)) {
            return false;
        }

        String act1ReqValStr = act1.getAttributeValue(reqAttr);
        String act2ReqValStr = act2.getAttributeValue(reqAttr);

        if (!Util.isNumeric(act1ReqValStr) || !Util.isNumeric(act2ReqValStr)) {
            return false;
        }

        boolean attrReqMatched = isNumericConstraintMatched(reqAttr, rv3OptType, act1, act2);

        if (!attrReqMatched) {
            return false;
        }

        if (secondaryValues == null) {
            return true;
        }

        return confirmIntervalBounds(secondaryValues, act1, act2);
    }

    private static boolean confirmIntervalBounds(Set<RuleValue> secondaryValues,
                                                 ActivityInstance act1,
                                                 ActivityInstance act2) {
        PathDesc.IntervalBound lowerBound = getIntervalLowerBound(secondaryValues);
        PathDesc.IntervalBound upperBound = getIntervalUpperBound(secondaryValues);

        boolean lowerBoundMatched = true;
        boolean upperBoundMatched = true;

        if (lowerBound != null) {
            lowerBoundMatched = confirmInterval(lowerBound.getOperationType(), act1, act2, lowerBound.getValue());
        }

        if (upperBound != null) {
            upperBoundMatched = confirmInterval(upperBound.getOperationType(), act1, act2, upperBound.getValue());
        }

        return lowerBoundMatched && upperBoundMatched;
    }

    private static boolean isTextConstraintMatched(String attribute,
                                                   OperationType operationType,
                                                   ActivityInstance sourceAct,
                                                   ActivityInstance targetAct) {
        String act1ReqValStr = sourceAct.getAttributeValue(attribute);
        String act2ReqValStr = targetAct.getAttributeValue(attribute);

        if (operationType == OperationType.EQUAL) {
            return act1ReqValStr.equals(act2ReqValStr);
        } else {
            return !act1ReqValStr.equals(act2ReqValStr);
        }
    }

    private static boolean isNumericConstraintMatched(String attribute,
                                                      OperationType operationType,
                                                      ActivityInstance sourceAct,
                                                      ActivityInstance targetAct) {
        String act1ReqValStr = sourceAct.getAttributeValue(attribute);
        String act2ReqValStr = targetAct.getAttributeValue(attribute);

        if (!Util.isNumeric(act1ReqValStr) || !Util.isNumeric(act2ReqValStr)) {
            return false;
        }

        double act1ReqVal = Double.parseDouble(act1ReqValStr);
        double act2ReqVal = Double.parseDouble(act2ReqValStr);

        switch (operationType) {
            case EQUAL:
                return act1ReqVal == act2ReqVal;
            case NOT_EQUAL:
                return act1ReqVal != act2ReqVal;
            case GREATER:
                return act1ReqVal > act2ReqVal;
            case GREATER_EQUAL:
                return act1ReqVal >= act2ReqVal;
            case LESS:
                return act1ReqVal < act2ReqVal;
            case LESS_EQUAL:
                return act1ReqVal <= act2ReqVal;
            default:
                return false;
        }
    }
}
