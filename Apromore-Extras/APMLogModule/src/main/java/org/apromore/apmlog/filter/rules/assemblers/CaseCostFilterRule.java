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

package org.apromore.apmlog.filter.rules.assemblers;

import lombok.experimental.UtilityClass;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.LogFilterRuleImpl;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.FilterType;
import org.apromore.apmlog.filter.types.OperationType;

import java.util.Set;

@UtilityClass
public class CaseCostFilterRule {
    public static LogFilterRule of(boolean retain, CostOptions costOptions) {
        FilterType filterType = FilterType.CASE_COST;
        String attribute = costOptions.getCostPerspective();
        double minCost = costOptions.getMinCost();
        double maxCost = costOptions.getMaxCost();
        RuleValue rvMin = new RuleValue(filterType, OperationType.GREATER_EQUAL, attribute, minCost);
        RuleValue rvMax = new RuleValue(filterType, OperationType.LESS_EQUAL, attribute, maxCost);
        Set<RuleValue> ruleValueSet = Set.of(rvMin, rvMax);

        return LogFilterRuleImpl.init(filterType, retain, ruleValueSet)
                .withCostRates(costOptions.getCurrency(), costOptions.getCostPerspective(),
                        costOptions.getCostRates());
    }
}
