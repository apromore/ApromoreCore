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
package org.apromore.apmlog.filter.typefilters;

import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.List;
import java.util.Set;

/**
 * @author Chii Chang
 */
public class PathFilter {
    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
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
            if (val != null && fromValSet.contains(val) && conformRequirement(lastAct, null, logFilterRule)) {
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

            if (val != null && toValSet.contains(val) && activityList.get(0) != act1) {
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

                List<ActivityInstance> subList = activityList.subList(i, activityList.size());

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

    private static UnifiedSet<ActivityInstance> getMatchedFollowUpActivities(PTrace trace,
                                                                             String attributeKey,
                                                                             UnifiedSet<String> toValSet,
                                                                             int fromIndex) {
        UnifiedSet<ActivityInstance> followUpActSet = new UnifiedSet<>();

        List<ActivityInstance> activityList =
                trace.getActivityInstances().subList(fromIndex, trace.getActivityInstances().size());

        for (ActivityInstance activity : activityList) {
            String val = getAttributeValue(activity, attributeKey);
            if (toValSet.contains(val)) followUpActSet.add(activity);
        }
        return followUpActSet;
    }



    private static String getAttributeValue(ActivityInstance activity, String key) {
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



    private static boolean conformRequirement(ActivityInstance act1, ActivityInstance act2, LogFilterRule logFilterRule) {
        Set<RuleValue> secondaryValues = logFilterRule.getSecondaryValues();
        if (secondaryValues != null) {
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
                if (!haveSameAttributeValue(act1, act2, requireAttrKey)) return false;
                else {
                    if (lowerBoundOpt == OperationType.GREATER) {
                        if (!confirmInterval(lowerBoundOpt, act1, act2, lowerBoundVal)) return false;
                    }
                    if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                        if (!confirmInterval(lowerBoundOpt, act1, act2, lowerBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS) {
                        if (!confirmInterval(upperBoundOpt, act1, act2, upperBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS_EQUAL) {
                        if (!confirmInterval(upperBoundOpt, act1, act2, upperBoundVal)) return false;
                    }
                }
            }

            if (requireOpt == OperationType.NOT_EQUAL) {
                if (haveSameAttributeValue(act1, act2, requireAttrKey)) return false;
                else {
                    if (lowerBoundOpt == OperationType.GREATER) {
                        if (!confirmInterval(lowerBoundOpt, act1, act2, lowerBoundVal)) return false;
                    }
                    if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                        if (!confirmInterval(lowerBoundOpt, act1, act2, lowerBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS) {
                        if (!confirmInterval(upperBoundOpt, act1, act2, upperBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS_EQUAL) {
                        if (!confirmInterval(upperBoundOpt, act1, act2, upperBoundVal)) return false;
                    }
                }
            }

            if (requireOpt == OperationType.UNKNOWN) {
                if (lowerBoundVal != -1) {
                    if (lowerBoundOpt == OperationType.GREATER) {
                        if (!confirmInterval(lowerBoundOpt, act1, act2, lowerBoundVal)) return false;
                    }
                    if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                        if (!confirmInterval(lowerBoundOpt, act1, act2, lowerBoundVal)) return false;
                    }
                }
                if (upperBoundVal != -1) {
                    if (upperBoundOpt == OperationType.LESS) {
                        if (!confirmInterval(upperBoundOpt, act1, act2, upperBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS_EQUAL) {
                        if (!confirmInterval(upperBoundOpt, act1, act2, upperBoundVal)) return false;
                    }
                }
            }

            return true;
        } else {
            return true;
        }
    }


    private static boolean haveSameAttributeValue(ActivityInstance act1, ActivityInstance act2, String key) {
        if (act2 == null) return false;
        if (!act1.getAttributes().containsKey(key) || !act2.getAttributes().containsKey(key)) return false;

        return act1.getAttributeValue(key).equals(act2.getAttributeValue(key));
    }

    private static boolean confirmInterval(OperationType operationType, ActivityInstance act1, ActivityInstance act2,
                                           double interval) {

        double duration = 0;

        if (act2 == null) {
            duration = act1.getDuration();
        } else {
            long st = act1.getStartTime();
            long et = act2.getEndTime();
            duration = et > st ? et - st : 0;
        }

        switch (operationType) {
            case GREATER: return duration > interval;
            case GREATER_EQUAL: return duration >= interval;
            case LESS: return duration < interval;
            case LESS_EQUAL: return duration <= interval;
            default: return false;
        }
    }

}
