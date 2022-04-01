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
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.Inclusion;
import org.apromore.apmlog.filter.types.OperationType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NodeDurationFilter extends AbstractAttributeDurationFilter {

    public static List<PTrace> filter(LogFilterRule rule, List<PTrace> traces) {
        Inclusion inclusion = rule.getInclusion();

        double durRangeFrom = 0, durRangeTo = 0;

        for (RuleValue ruleValue : rule.getPrimaryValues()) {
            OperationType operationType = ruleValue.getOperationType();
            if (operationType == OperationType.GREATER_EQUAL) durRangeFrom = ruleValue.getDoubleValue();
            if (operationType == OperationType.LESS_EQUAL) durRangeTo = ruleValue.getDoubleValue();
        }

        final double finFrom = durRangeFrom, finTo = durRangeTo;

        String k = rule.getKey();
        String v = rule.getPrimaryValues().iterator().next().getKey();

        List<PTrace> matched = inclusion == Inclusion.ALL_VALUES ?
                traces.stream()
                        .filter(t -> t.getActivityInstances().stream()
                                .anyMatch(a -> a.getAttributes().containsKey(k) && a.getAttributeValue(k).equals(v)))
                        .filter(t -> t.getActivityInstances().stream()
                                .filter(a -> a.getAttributes().containsKey(k) && a.getAttributeValue(k).equals(v))
                                .collect(Collectors.toList()).stream()
                                .allMatch(a -> a.getDuration() >= finFrom && a.getDuration() <= finTo))
                        .collect(Collectors.toList()) :
                traces.stream()
                        .filter(t -> t.getActivityInstances().stream()
                                .anyMatch(a -> a.getAttributes().containsKey(k) && a.getAttributeValue(k).equals(v)))
                        .filter(t -> t.getActivityInstances().stream()
                                .filter(a -> a.getAttributes().containsKey(k) && a.getAttributeValue(k).equals(v))
                                .collect(Collectors.toList()).stream()
                                .anyMatch(a -> a.getDuration() >= finFrom && a.getDuration() <= finTo))
                        .collect(Collectors.toList());

        Set<String> caseIds = matched.stream().map(PTrace::getCaseId).collect(Collectors.toSet());

        return rule.getChoice() == Choice.RETAIN ? matched :
                traces.stream().filter(t -> !caseIds.contains(t.getCaseId())).collect(Collectors.toList());

    }
}
