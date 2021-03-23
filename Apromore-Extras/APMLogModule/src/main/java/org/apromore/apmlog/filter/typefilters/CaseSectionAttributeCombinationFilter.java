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

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.AEvent;
import org.apromore.apmlog.ATrace;

import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;
import java.util.stream.Collectors;

public class CaseSectionAttributeCombinationFilter {
    public static boolean toKeep(ATrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(ATrace trace, LogFilterRule logFilterRule) {

        Set<String> primaryValues = logFilterRule.getPrimaryValuesInString();
        Set<RuleValue> secoRV = logFilterRule.getSecondaryValues();

        if (secoRV == null || secoRV.isEmpty()) return false;

        Set<String> secondaryValues = (Set<String>) secoRV.iterator().next().getObjectVal();

        String firstKey = logFilterRule.getPrimaryValues().iterator().next().getKey();
        String secondKey = secoRV.iterator().next().getKey();

        String sect1 = logFilterRule.getPrimaryValues().iterator().next().getCustomAttributes().get("section");
        String sect2 = secoRV.iterator().next().getCustomAttributes().get("section");

        Inclusion inclusion = logFilterRule.getInclusion();

        if (sect1.equals("case") && sect2.equals("case")) {
            return conformCaseToCaseAttrValue(trace, firstKey, secondKey, primaryValues, secondaryValues, inclusion);
        } else if (sect1.equals("case") && sect2.equals("event")) {
            return conformCaseToEventAttrValue(trace, firstKey, secondKey, primaryValues, secondaryValues, inclusion);
        } else if (sect1.equals("event") && sect2.equals("event")) {
            return confirmEventToEventAttrValues(trace, firstKey, secondKey, primaryValues, secondaryValues, inclusion);
        } else if (sect1.equals("event") && sect2.equals("case")) {
            return confirmEventToCaseAttrValues(trace, firstKey, secondKey, primaryValues, secondaryValues, inclusion);
        }

        return false;
    }

    private static boolean conformCaseToCaseAttrValue(ATrace trace, String firstKey, String secondKey,
                                                      Set<String> primaryValues, Set<String> secondaryValues,
                                                      Inclusion inclusion) {
        UnifiedMap<String, String> caseAttrMap = trace.getAttributeMap();
        if (!caseAttrMap.containsKey(firstKey)) return false;
        if (!caseAttrMap.containsKey(secondKey)) return false;
        else {
            if (!primaryValues.contains(caseAttrMap.get(firstKey))) return false;
            else {
                switch (inclusion) {
                    case ALL_VALUES:
                        Set<String> matchedValues = new HashSet<>();

                        for (String caseAttrKey : caseAttrMap.keySet()) {
                            if (secondaryValues.contains(caseAttrMap.get(caseAttrKey))) {
                                matchedValues.add(caseAttrMap.get(caseAttrKey));
                            }
                        }

                        return matchedValues.size() == secondaryValues.size();
                    case ANY_VALUE:
                        for (String caseAttrKey : caseAttrMap.keySet()) {
                            if (secondaryValues.contains(caseAttrMap.get(caseAttrKey))) {
                                return true;
                            }
                        }
                }
            }
        }
        return false;
    }

    private static boolean conformCaseToEventAttrValue(ATrace trace, String firstKey, String secondKey,
                                                       Set<String> primaryValues, Set<String> secondaryValues,
                                                       Inclusion inclusion) {
        UnifiedMap<String, String> caseAttrMap = trace.getAttributeMap();
        if (!caseAttrMap.containsKey(firstKey)) return false;
        else {
            String primVal = primaryValues.iterator().next();
            if (!caseAttrMap.get(firstKey).equals(primVal)) return false;

            Map<String, List<AActivity>> grouped = trace.getActivityList().stream()
                    .filter(x -> x.getAllAttributes().containsKey(secondKey) &&
                            secondaryValues.contains(x.getAllAttributes().get(secondKey)))
                    .collect(Collectors.groupingBy(x -> x.getAllAttributes().get(secondKey)));

            LongSummaryStatistics valActSizes = grouped.entrySet().stream()
                    .collect(Collectors.summarizingLong(x -> x.getValue().size()));

            if (inclusion == Inclusion.ALL_VALUES) {
                return valActSizes.getMin() > 0 && valActSizes.getCount() == secondaryValues.size();
            } else return valActSizes.getMax() > 0;

        }
    }

    private static String getConfirmedActivityAttrValue(AActivity activity, String attrKey, Set<String> values) {

        AEvent event0 = activity.getImmutableEventList().get(0);
        switch (attrKey) {
            case "concept:name":
                if (values.contains(activity.getName())) return activity.getName();
                break;
            case "org:resource":
                if (values.contains(activity.getResource())) return activity.getResource();
                break;
            case "lifecycle:transition":
                if (values.contains(event0.getLifecycle())) return event0.getLifecycle();
                break;
        }
        return null;
    }

    private static boolean confirmEventToEventAttrValues(ATrace trace, String firstKey, String secondKey,
                                                         Set<String> primaryValues, Set<String> secondaryValues,
                                                         Inclusion inclusion) {
        List<AActivity> activityList = trace.getActivityList();

        switch (inclusion) {
            case ALL_VALUES:
                UnifiedSet<String> matchedVals = new UnifiedSet<>();
                for (AActivity act : activityList) {
                    AEvent event0 = act.getImmutableEventList().get(0);
                    String confirmedVal =
                            getConformedEventAttrValue(event0, firstKey, secondKey, primaryValues, secondaryValues);
                    if (confirmedVal != null) matchedVals.add(confirmedVal);
                }

                return matchedVals.size() == secondaryValues.size();
            case ANY_VALUE:
                for (AActivity act : activityList) {
                    AEvent event0 = act.getImmutableEventList().get(0);
                    String confirmedVal =
                            getConformedEventAttrValue(event0, firstKey, secondKey, primaryValues, secondaryValues);
                    if (confirmedVal != null) return true;
                }
                break;
        }

        return false;
    }

    private static String getConformedEventAttrValue(AEvent event, String firstKey, String secondKey,
                                                     Set<String> primaryValues, Set<String> secondaryValues) {

        switch (firstKey) {
            case "concept:name":
                if (primaryValues.contains(event.getName())) {
                    return conformEventAttributeKeyValue(event, secondKey, secondaryValues);
                }
                break;
            case "org:resource":
                if (primaryValues.contains(event.getResource())) {
                    return conformEventAttributeKeyValue(event, secondKey, secondaryValues);
                }
                break;
            case "lifecycle:transition":
                if (primaryValues.contains(event.getLifecycle())) {
                    return conformEventAttributeKeyValue(event, secondKey, secondaryValues);
                }
                break;
            default:
                if (!event.getAttributeMap().keySet().contains(firstKey)) return null;

                String val = event.getAttributeValue(firstKey);
                if (primaryValues.contains(val)) {
                    return conformEventAttributeKeyValue(event, secondKey, secondaryValues);
                }

                break;
        }

        return null;
    }

    private static String conformCaseAttributeKeyValue(ATrace trace, String attributeKey, Set<String> values) {
        UnifiedMap<String, String> attrMap = trace.getAttributeMap();
        if (!attrMap.containsKey(attributeKey)) return null;
        return values.contains(attrMap.get(attributeKey)) ? attrMap.get(attributeKey) : null;
    }

    private static String conformEventAttributeKeyValue(AEvent event, String attributeKey, Set<String> values) {

        switch (attributeKey) {
            case "concept:name":
                if (values.contains(event.getName())) return event.getName();
                break;
            case "org:resource":
                if (values.contains(event.getResource())) return event.getResource();
                break;
            case "lifecycle:transition":
                if (values.contains(event.getLifecycle())) return event.getLifecycle();
                break;
            default:
                if (!event.getAttributeMap().keySet().contains(attributeKey)) return null;

                String val = event.getAttributeValue(attributeKey);
                if (values.contains(val)) return val;

                break;
        }

        return null;
    }

    private static boolean confirmEventToCaseAttrValues(ATrace trace, String firstKey, String secondKey,
                                                        Set<String> primaryValues, Set<String> secondaryValues,
                                                        Inclusion inclusion) {
        UnifiedMap<String, String> caseAttrMap = trace.getAttributeMap();

        List<AActivity> activityList = trace.getActivityList();

        switch (inclusion) {
            case ALL_VALUES:
                UnifiedSet<String> matchedVals = new UnifiedSet<>();
                for (int i = 0; i < activityList.size(); i++) {
                    AEvent event0 = activityList.get(i).getImmutableEventList().get(0);
                    String attrVal = getEventAttributeValue(event0, firstKey);
                    if (attrVal != null) {
                        if (primaryValues.contains(attrVal)) {
                            String attrVal2 = caseAttrMap.get(secondKey);
                            if (attrVal2 != null) {
                                if (secondaryValues.contains(attrVal2)) {
                                    matchedVals.add(attrVal2);
                                }
                            }
                        }
                    }
                }

                return matchedVals.size() == secondaryValues.size();
            case ANY_VALUE:
                for (int i = 0; i < activityList.size(); i++) {
                    AEvent event0 = activityList.get(i).getImmutableEventList().get(0);
                    String attrVal = getEventAttributeValue(event0, firstKey);
                    if (attrVal != null) {
                        if (primaryValues.contains(attrVal)) {
                            String attrVal2 = caseAttrMap.get(secondKey);
                            if (attrVal2 != null) {
                                if (secondaryValues.contains(attrVal2)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                break;
        }

        return false;
    }

    private static String getEventAttributeValue(AEvent event, String attributeKey) {
        switch (attributeKey) {
            case "concept:name": return event.getName();
            case "org:resource": return event.getResource();
            default:
                UnifiedMap<String, String> attrMap = event.getAttributeMap();
                return attrMap.containsKey(attributeKey) ? attrMap.get(attributeKey) : null;
        }
    }
}
