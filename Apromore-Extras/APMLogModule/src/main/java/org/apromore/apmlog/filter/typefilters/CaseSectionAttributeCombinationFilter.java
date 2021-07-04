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
import org.apromore.apmlog.filter.types.Inclusion;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CaseSectionAttributeCombinationFilter {
    public static boolean toKeep(PTrace trace, LogFilterRule logFilterRule) {
        Choice choice = logFilterRule.getChoice();
        switch (choice) {
            case RETAIN: return conformRule(trace, logFilterRule);
            default: return !conformRule(trace, logFilterRule);
        }
    }

    private static boolean conformRule(PTrace trace, LogFilterRule logFilterRule) {

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

    private static boolean conformCaseToCaseAttrValue(PTrace trace, String firstKey, String secondKey,
                                                      Set<String> primaryValues, Set<String> secondaryValues,
                                                      Inclusion inclusion) {

        String primVal = primaryValues.iterator().next();

        UnifiedMap<String, String> caseAttrMap = trace.getAttributes();

        if (!caseAttrMap.containsKey(firstKey)) return false;
        if (!caseAttrMap.containsKey(secondKey)) return false;
        if (!caseAttrMap.get(firstKey).equals(primVal)) return false;

        switch (inclusion) {
            case ANY_VALUE:
                return caseAttrMap.entrySet().stream()
                        .filter(x -> secondaryValues.contains(x.getValue()))
                        .findFirst()
                        .orElse(null) != null;
            case ALL_VALUES:
                return caseAttrMap.entrySet().stream()
                        .filter(x -> secondaryValues.contains(x.getValue()))
                        .map(x -> x.getValue())
                        .collect(Collectors.toSet()).size() >= secondaryValues.size();
        }

        return false;
    }

    private static boolean conformCaseToEventAttrValue(PTrace trace, String firstKey, String secondKey,
                                                       Set<String> primaryValues, Set<String> secondaryValues,
                                                       Inclusion inclusion) {

        String primVal = primaryValues.iterator().next();

        UnifiedMap<String, String> caseAttrMap = trace.getAttributes();
        if (!caseAttrMap.containsKey(firstKey)) return false;
        if (!caseAttrMap.get(firstKey).equals(primVal)) return false;

        List<ActivityInstance> activityList = trace.getActivityInstances();

        switch (inclusion) {
            case ANY_VALUE:
                return null != activityList.stream()
                        .filter(x -> x.getAttributes().containsKey(secondKey))
                        .filter(x -> secondaryValues.contains(x.getAttributes().get(secondKey)))
                        .findFirst().orElse(null);

            case ALL_VALUES:
                Set<ActivityInstance> matchedVal = activityList.stream()
                        .filter(x -> x.getAttributes().containsKey(secondKey))
                        .filter(x -> secondaryValues.contains(x.getAttributes().get(secondKey)))
                        .collect(Collectors.toSet());

                return matchedVal.size() >= secondaryValues.size();
        }

        return false;
    }


    private static boolean confirmEventToEventAttrValues(PTrace trace, String firstKey, String secondKey,
                                                         Set<String> primaryValues, Set<String> secondaryValues,
                                                         Inclusion inclusion) {
        List<ActivityInstance> activityList = trace.getActivityInstances();

        ActivityInstance priAct = activityList.stream()
                .filter(x -> x.getAttributes().containsKey(firstKey))
                .filter(x -> x.getAttributes().get(firstKey).equals(primaryValues.iterator().next()))
                .findFirst()
                .orElse(null);

        if (priAct == null) return false;

        switch (inclusion) {
            case ANY_VALUE:
                ActivityInstance ai = activityList.stream()
                        .filter(x -> x.getAttributes().containsKey(firstKey))
                        .filter(x -> x.getAttributes().get(firstKey).equals(primaryValues.iterator().next()))
                        .filter(x -> x.getAttributes().containsKey(secondKey))
                        .filter(x -> secondaryValues.contains(x.getAttributes().get(secondKey)))
                        .findFirst()
                        .orElse(null);

                return ai != null;
            case ALL_VALUES:
                Set<ActivityInstance> matchedVal = activityList.stream()
                        .filter(x -> x.getAttributes().containsKey(firstKey))
                        .filter(x -> x.getAttributes().get(firstKey).equals(primaryValues.iterator().next()))
                        .filter(x -> x.getAttributes().containsKey(secondKey))
                        .filter(x -> secondaryValues.contains(x.getAttributes().get(secondKey)))
                        .collect(Collectors.toSet());

                return matchedVal.size() >= secondaryValues.size();
        }

        return false;
    }


    private static boolean confirmEventToCaseAttrValues(PTrace trace, String firstKey, String secondKey,
                                                        Set<String> primaryValues, Set<String> secondaryValues,
                                                        Inclusion inclusion) {

        UnifiedMap<String, String> caseAttrMap = trace.getAttributes();

        if (!caseAttrMap.containsKey(secondKey)) return false;

        List<ActivityInstance> activityList = trace.getActivityInstances();

        switch (inclusion) {
            case ANY_VALUE:
                for (ActivityInstance act : activityList) {
                    String attrVal = getEventAttributeValue(act, firstKey);
                    if (attrVal != null && primaryValues.contains(attrVal)) {
                        String attrVal2 = caseAttrMap.get(secondKey);
                        if (attrVal2 != null && secondaryValues.contains(attrVal2)) {
                            return true;
                        }
                    }
                }
                break;
            case ALL_VALUES:
                UnifiedSet<String> matchedVals = new UnifiedSet<>();

                for (ActivityInstance act : activityList) {
                    String attrVal = getEventAttributeValue(act, firstKey);
                    if (attrVal != null && primaryValues.contains(attrVal)) {
                        String attrVal2 = caseAttrMap.get(secondKey);
                        if (attrVal2 != null && secondaryValues.contains(attrVal2)) {
                            matchedVals.add(attrVal2);
                        }
                    }
                }

                return matchedVals.size() == secondaryValues.size();
        }

        return false;
    }

    private static String getEventAttributeValue(ActivityInstance activityInstance, String key) {
        if (!activityInstance.getAttributes().containsKey(key)) return null;

        return activityInstance.getAttributes().containsKey(key) ? activityInstance.getAttributes().get(key).toString() : null;
    }
}
