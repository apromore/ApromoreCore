/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.LaTrace;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Chii Chang
 */
public class PathFilter {
    public static boolean toKeep(LaTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(LaTrace trace, LogFilterRule logFilterRule) {

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

    private static boolean conformDirectFollow(LaTrace trace,
                                               String attributeKey,
                                               UnifiedSet<String> fromValSet,
                                               UnifiedSet<String> toValSet,
                                               LogFilterRule logFilterRule) {
        int matchCount = 0;
        List<AActivity> activityList = trace.getActivityList();
        for (int i = 0; i < activityList.size(); i++) {
            AActivity activity = activityList.get(i);
            String val = getAttributeValue(activity, attributeKey);

            if (fromValSet.contains("[Start]") && toValSet.contains(val) && i == 0) matchCount += 1;
            if (toValSet.contains("[End]") && fromValSet.contains(val) && i == activityList.size()-1) matchCount += 1;
            if (fromValSet.contains(val) && i < activityList.size()-1) {
                AActivity nextAct = activityList.get(i+1);
                String nextVal = getAttributeValue(nextAct, attributeKey);
                if (toValSet.contains(nextVal)) {
                    AEvent e1 = activity.getEventList().get(activity.getEventList().size()-1);
                    AEvent e2 = nextAct.getEventList().get(nextAct.getEventList().size()-1);
                    boolean conformRequirement = conformRequirement(e1, e2, logFilterRule);
                    if (conformRequirement) {
                        matchCount += 1;
                        break;
                    }
                }
            }
        }

        return matchCount > 0;
    }

    private static boolean conformEventualFollow(LaTrace trace,
                                                 String attributeKey,
                                                 UnifiedSet<String> fromValSet,
                                                 UnifiedSet<String> toValSet,
                                                 LogFilterRule logFilterRule) {
        int matchCount = 0;
        List<AActivity> activityList = trace.getActivityList();
        for (int i = 0; i < activityList.size(); i++) {
            AActivity activity = activityList.get(i);
            String val = getAttributeValue(activity, attributeKey);

            if (fromValSet.contains(val) && i < activityList.size()-1) {

                UnifiedSet<AActivity> matchedFollowUps = getMatchedFollowUpActivities(trace, attributeKey,
                        toValSet, i + 1);

                AEvent e1 = activity.getEventList().get(activity.getEventList().size()-1);

                for (AActivity followUpAct : matchedFollowUps) {
                    AEvent e2 = followUpAct.getEventList().get(followUpAct.getEventList().size()-1);
                    if (conformRequirement(e1, e2, logFilterRule)) {
                        matchCount += 1;
                        break;
                    }
                }
            }
        }

        return matchCount > 0;
    }

    private static UnifiedSet<AActivity> getMatchedFollowUpActivities(LaTrace trace,
                                                                      String attributeKey,
                                                                      UnifiedSet<String> toValSet,
                                                                      int fromIndex) {
        UnifiedSet<AActivity> followUpActSet = new UnifiedSet<>();

        List<AActivity> activityList = trace.getActivityList();
        for (int i = fromIndex; i < activityList.size(); i++) {
            AActivity activity = activityList.get(i);
            String val = getAttributeValue(activity, attributeKey);
            if (toValSet.contains(val)) followUpActSet.add(activity);
        }
        return followUpActSet;
    }



    private static String getAttributeValue(AActivity activity, String attributeKey) {
        AEvent event0 = activity.getEventList().get(0);
        UnifiedMap<String, String> eventAttributes = event0.getAttributeMap();
        String val = null;
        switch (attributeKey) {
            case "concept:name": val = event0.getName(); break;
            case "org:resource": val = event0.getResource(); break;
            case "lifecycle:transition": val = event0.getLifecycle(); break;
            default:
                if (eventAttributes.containsKey(attributeKey)) {
                    val = eventAttributes.get(attributeKey);
                }
                break;
        }
        return val;
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



    private static boolean conformRequirement(AEvent event1, AEvent event2, LogFilterRule logFilterRule) {
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
                if (!haveSameAttributeValue(event1, event2, requireAttrKey)) return false;
                else {
                    if (lowerBoundOpt == OperationType.GREATER) {
                        if (!haveIntervalGreater(event1, event2, lowerBoundVal)) return false;
                    }
                    if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                        if (!haveIntervalGreaterEqual(event1, event2, lowerBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS) {
                        if (!haveIntervalLess(event1, event2, upperBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS_EQUAL) {
                        if (!haveIntervalLessEqual(event1, event2, upperBoundVal)) return false;
                    }
                }
            }

            if (requireOpt == OperationType.NOT_EQUAL) {
                if (haveSameAttributeValue(event1, event2, requireAttrKey)) return false;
                else {
                    if (lowerBoundOpt == OperationType.GREATER) {
                        if (!haveIntervalGreater(event1, event2, lowerBoundVal)) return false;
                    }
                    if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                        if (!haveIntervalGreaterEqual(event1, event2, lowerBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS) {
                        if (!haveIntervalLess(event1, event2, upperBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS_EQUAL) {
                        if (!haveIntervalLessEqual(event1, event2, upperBoundVal)) return false;
                    }
                }
            }

            if (requireOpt == OperationType.UNKNOWN) {
                if (lowerBoundVal != -1) {
                    if (lowerBoundOpt == OperationType.GREATER) {
                        if (!haveIntervalGreater(event1, event2, lowerBoundVal)) return false;
                    }
                    if (lowerBoundOpt == OperationType.GREATER_EQUAL) {
                        if (!haveIntervalGreaterEqual(event1, event2, lowerBoundVal)) return false;
                    }
                }
                if (upperBoundVal != -1) {
                    if (upperBoundOpt == OperationType.LESS) {
                        if (!haveIntervalLess(event1, event2, upperBoundVal)) return false;
                    }
                    if (upperBoundOpt == OperationType.LESS_EQUAL) {
                        if (!haveIntervalLessEqual(event1, event2, upperBoundVal)) return false;
                    }
                }
            }

            return true;
        } else {
            return true;
        }
    }


    private static boolean haveSameAttributeValue(AEvent event1, AEvent event2, String attributeKey) {
        switch (attributeKey) {
            case "concept:name":
                if (event1.getName().equals(event2.getName())) return true;
                break;
            case "org:resource":
                if (event1.getResource().equals(event2.getResource())) return true;
                break;
            case "lifecycle:transition":
                if (event1.getLifecycle().equals(event2.getLifecycle())) return true;
                break;
            default:
                if (event1.getAttributeMap().keySet().contains(attributeKey) &&
                        event2.getAttributeMap().keySet().contains(attributeKey)) {
                    String val1 = event1.getAttributeValue(attributeKey);
                    String val2 = event2.getAttributeValue(attributeKey);
                    if (val1.equals(val2)) return true;
                }
                break;
        }

        return false;
    }

    private static boolean haveIntervalGreater(AEvent event1, AEvent event2, long interval) {
        long e1Time = event1.getTimestampMilli();
        long e2Time = event2.getTimestampMilli();
        long e1e2Interval = e2Time - e1Time;
        if(e1e2Interval > interval) return true;
        else return false;
    }

    private static boolean haveIntervalGreaterEqual(AEvent event1, AEvent event2, long interval) {
        long e1Time = event1.getTimestampMilli();
        long e2Time = event2.getTimestampMilli();
        long e1e2Interval = e2Time - e1Time;
        if(e1e2Interval >= interval) return true;
        else return false;
    }

    private static boolean haveIntervalLess(AEvent event1, AEvent event2, long interval) {
        long e1Time = event1.getTimestampMilli();
        long e2Time = event2.getTimestampMilli();
        long e1e2Interval = e2Time - e1Time;
        if(e1e2Interval < interval) return true;
        else return false;
    }

    private static boolean haveIntervalLessEqual(AEvent event1, AEvent event2, long interval) {
        long e1Time = event1.getTimestampMilli();
        long e2Time = event2.getTimestampMilli();
        long e1e2Interval = e2Time - e1Time;
        if(e1e2Interval <= interval) return true;
        else return false;
    }

}
