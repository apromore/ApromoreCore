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
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.logobjects.ActivityInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UtilityClass
public class CaseCostFilter {

    public List<PTrace> filter(List<PTrace> traces, LogFilterRule logFilterRule) {

        CostFilterRuleData ruleData = new CostFilterRuleData(logFilterRule);
        boolean retain = ruleData.isRetain();

        if (ruleData.isInvalid()) {
            return retain ? new ArrayList<>() : traces;
        }

        double from = logFilterRule.getPrimaryNumericValueByOperationType(OperationType.GREATER_EQUAL).doubleValue();
        double to = logFilterRule.getPrimaryNumericValueByOperationType(OperationType.LESS_EQUAL).doubleValue();

        List<PTrace> matched = new ArrayList<>();
        for (PTrace trace : traces) {
            double caseCost = getCaseCost(trace, ruleData.getCostPerspective(), ruleData.getCostRates());
            if (caseCost >= from && caseCost <= to) {
                matched.add(trace);
            }
        }

        if (retain) {
            return matched;
        } else {
            traces.removeAll(matched);
            return traces;
        }
    }

    private static double getCaseCost(PTrace pTrace,
                                     String costPerspective,
                                     Map<String, Double> perspectiveValueCostRates) {
        List<ActivityInstance> activityInstanceList = pTrace.getActivityInstances();
        return activityInstanceList.stream()
                .collect(Collectors.summarizingDouble(x ->
                        CostFilterUtil.getActivityInstanceCost(x, costPerspective, perspectiveValueCostRates)))
                .getSum();
    }
}
