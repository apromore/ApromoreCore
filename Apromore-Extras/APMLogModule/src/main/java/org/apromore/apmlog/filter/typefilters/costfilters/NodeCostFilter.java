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

package org.apromore.apmlog.filter.typefilters.costfilters;

import lombok.experimental.UtilityClass;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.logobjects.ActivityInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class NodeCostFilter {

    public static List<PTrace> filter(List<PTrace> traces, LogFilterRule logFilterRule) {

        CostFilterRuleData ruleData = new CostFilterRuleData(logFilterRule);
        boolean retain = ruleData.isRetain();

        if (ruleData.isInvalid()) {
            return retain ? new ArrayList<>() : traces;
        }

        List<PTrace> matched = traces.stream()
                .filter(t -> conform(t, logFilterRule))
                .collect(Collectors.toList());

        if (retain) {
            return matched;
        } else {
            traces.removeAll(matched);
            return traces;
        }
    }

    private static boolean conform(PTrace pTrace, LogFilterRule logFilterRule) {
        String attribute = logFilterRule.getKey();
        String attrVal = logFilterRule.getPrimaryValues().iterator().next().getKey();
        Inclusion inclusion = logFilterRule.getInclusion();
        String costPerspective = logFilterRule.getCostPerspective();
        Map<String, Double> costRates = logFilterRule.getCostRates();
        double minCost = logFilterRule.getPrimaryNumericValueByOperationType(OperationType.GREATER_EQUAL).doubleValue();
        double maxCost = logFilterRule.getPrimaryNumericValueByOperationType(OperationType.LESS_EQUAL).doubleValue();

        List<ActivityInstance> acts = getValidActivityInstances(pTrace, attribute, attrVal, costPerspective);

        if (acts.isEmpty()) {
            return false;
        }

        return inclusion == Inclusion.ALL_VALUES ?
                acts.stream().allMatch(a -> withinRange(a, costPerspective, costRates, minCost, maxCost)) :
                acts.stream().anyMatch(a -> withinRange(a, costPerspective, costRates, minCost, maxCost));
    }

    private static List<ActivityInstance> getValidActivityInstances(PTrace pTrace,
                                                                      String attribute,
                                                                      String attributeValue,
                                                                      String costPerspective) {
        return pTrace.getActivityInstances().stream()
                .filter(a -> a.getAttributes().containsKey(attribute) &&
                        a.getAttributes().get(attribute).equals(attributeValue) &&
                        a.getAttributes().containsKey(costPerspective))
                .collect(Collectors.toList());
    }

    private static boolean withinRange(ActivityInstance activityInstance,
                                       String costPerspective,
                                       Map<String, Double> costRates,
                                       double minCost,
                                       double maxCost) {
        double actCost = CostFilterUtil.getActivityInstanceCost(activityInstance, costPerspective, costRates);
        return actCost >= minCost && actCost <= maxCost;
    }
}
