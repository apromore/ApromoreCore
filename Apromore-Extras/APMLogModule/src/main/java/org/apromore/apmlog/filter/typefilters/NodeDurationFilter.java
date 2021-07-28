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

import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.logobjects.ActivityInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NodeDurationFilter extends AbstractAttributeDurationFilter {

    public static List<PTrace> filter(LogFilterRule rule, List<PTrace> traces) {
        Inclusion inclusion = rule.getInclusion();
        Choice choice = rule.getChoice();
        String attributeKey = rule.getKey();
        String attributeValue = rule.getPrimaryValues().iterator().next().getKey();

        double durRangeFrom = 0, durRangeTo = 0;
        for (RuleValue ruleValue : rule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) durRangeFrom = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) durRangeTo = ruleValue.getDoubleValue();
        }

        List<PTrace> matchedTraces = new ArrayList<>();

        final double from = durRangeFrom, to = durRangeTo;

        for (PTrace trace : traces) {
            List<ActivityInstance> matchedActs = trace.getActivityInstances().stream()
                    .filter(x -> x.getAttributes().containsKey(attributeKey))
                    .filter(x->x.getAttributeValue(attributeKey).equals(attributeValue))
                    .collect(Collectors.toList());

            List<ActivityInstance> timeMatchedActs = matchedActs.stream()
                    .filter(x -> x.getDuration() >= from && x.getDuration() <= to)
                    .collect(Collectors.toList());

            if (!matchedActs.isEmpty() && !timeMatchedActs.isEmpty() &&
                    (inclusion == Inclusion.ALL_VALUES ?
                            matchedActs.size() == timeMatchedActs.size() : timeMatchedActs.size() > 0)
            ) {
                matchedTraces.add(trace);
            }
        }

        if (choice == Choice.RETAIN) return matchedTraces;

        // in condition of REMOVE
        traces.removeAll(matchedTraces);
        return traces;

    }
}
