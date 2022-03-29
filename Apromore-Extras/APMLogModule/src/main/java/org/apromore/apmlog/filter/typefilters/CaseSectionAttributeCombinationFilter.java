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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CaseSectionAttributeCombinationFilter {

    private List<PTrace> traces;
    private boolean retain;
    private boolean includeAll;
    private String primaryValue;
    private Set<String> secondaryValues;
    private String key1;
    private String key2;
    private boolean caseToCase;
    private boolean caseToEvent;
    private boolean eventToEvent;
    private boolean eventToCase;

    private static final String CASE_TEXT = "case";
    private static final String EVENT_TEXT = "event";

    public CaseSectionAttributeCombinationFilter(LogFilterRule rule) {
        initRuleValues(rule);
    }

    public static CaseSectionAttributeCombinationFilter of(LogFilterRule rule) {
        return new CaseSectionAttributeCombinationFilter(rule);
    }

    private void initRuleValues(LogFilterRule rule) {
        retain = rule.getChoice() == Choice.RETAIN;
        includeAll = rule.getInclusion() == Inclusion.ALL_VALUES;
        primaryValue = rule.getPrimaryValuesInString().iterator().next();
        Set<RuleValue> secoRV = rule.getSecondaryValues();
        secondaryValues = (Set<String>) secoRV.iterator().next().getObjectVal();

        key1 = rule.getPrimaryValues().iterator().next().getKey();
        key2 = secoRV.iterator().next().getKey();

        String sect1 = rule.getPrimaryValues().iterator().next().getCustomAttributes().get("section");
        String sect2 = secoRV.iterator().next().getCustomAttributes().get("section");

        caseToCase = sect1.equals(CASE_TEXT) && sect2.equals(CASE_TEXT);
        caseToEvent = sect1.equals(CASE_TEXT) && sect2.equals(EVENT_TEXT);
        eventToEvent = sect1.equals(EVENT_TEXT) && sect2.equals(EVENT_TEXT);
        eventToCase = sect1.equals(EVENT_TEXT) && sect2.equals(CASE_TEXT);
    }

    public List<PTrace> filter(List<PTrace> traces) {
        this.traces = traces;

        List<PTrace> matched = includeAll ? getMatchedByAllValues() : getMatchedByAnyValues();

        Set<String> cIds = matched.stream().map(PTrace::getCaseId).collect(Collectors.toSet());

        return retain ? matched : traces.stream()
                .filter(t -> !cIds.contains(t.getCaseId())).collect(Collectors.toList());
    }

    private List<PTrace> getMatchedByAllValues() {
        List<PTrace> matched = traces;

        if (caseToCase) {
            matched = getMatchedTraceToTrace();
        } else if (caseToEvent) {
            matched = traces.stream()
                    .filter(t -> t.getAttributes().containsKey(key1) &&
                            t.getAttributes().get(key1).equals(primaryValue))
                    .filter(t -> t.getActivityInstances().stream().anyMatch(x -> x.getAttributes().containsKey(key2)))
                    .filter(t -> t.getActivityInstances().stream()
                            .filter(x -> secondaryValues.contains(x.getAttributeValue(key2)))
                            .collect(Collectors.groupingBy(x -> x.getAttributeValue(key2)))
                            .keySet().size() >= secondaryValues.size())
                    .collect(Collectors.toList());

        } else if (eventToEvent) {
            matched = traces.stream()
                    .filter(t -> t.getActivityInstances().stream()
                            .anyMatch(x -> x.getAttributes().containsKey(key1) && x.getAttributes().containsKey(key2)))
                    .filter(t -> t.getActivityInstances().stream()
                            .filter(x -> x.getAttributeValue(key1).equals(primaryValue))
                            .filter(x -> secondaryValues.contains(x.getAttributeValue(key2)))
                            .collect(Collectors.groupingBy(x -> x.getAttributeValue(key2)))
                            .keySet().size() >= secondaryValues.size())
                    .collect(Collectors.toList());
        } else if (eventToCase) {
            matched = traces.stream()
                    .filter(t -> t.getAttributes().containsKey(key2) &&
                            secondaryValues.contains(t.getAttributes().get(key2)))
                    .filter(t -> t.getActivityInstances().stream()
                            .anyMatch(x -> x.getAttributes().containsKey(key1) &&
                                    x.getAttributeValue(key1).equals(primaryValue)))
                    .collect(Collectors.toList());
        }

        return matched;
    }

    private List<PTrace> getMatchedByAnyValues() {
        List<PTrace> matched = traces;

        if (caseToCase) {
            matched = getMatchedTraceToTrace();
        } else if (caseToEvent) {
            matched = traces.stream()
                    .filter(t -> t.getAttributes().containsKey(key1) && t.getAttributes().get(key1).equals(primaryValue))
                    .filter(t -> t.getActivityInstances().stream()
                            .anyMatch(x -> x.getAttributes().containsKey(key2) &&
                                    secondaryValues.contains(x.getAttributeValue(key2))))
                    .collect(Collectors.toList());
        } else if (eventToEvent) {
            matched = traces.stream()
                    .filter(t -> t.getActivityInstances().stream()
                            .anyMatch(x -> x.getAttributes().containsKey(key1) && x.getAttributes().containsKey(key2)))
                    .filter(t -> t.getActivityInstances().stream()
                            .anyMatch(x -> x.getAttributeValue(key1).equals(primaryValue) &&
                                    secondaryValues.contains(x.getAttributeValue(key2))))
                    .collect(Collectors.toList());
        } else if (eventToCase) {
            matched = traces.stream()
                    .filter(t -> t.getAttributes().containsKey(key2) &&
                            secondaryValues.contains(t.getAttributes().get(key2)))
                    .filter(t -> t.getActivityInstances().stream()
                            .anyMatch(x -> x.getAttributes().containsKey(key1) &&
                                    x.getAttributeValue(key1).equals(primaryValue)))
                    .collect(Collectors.toList());
        }

        return matched;
    }

    private List<PTrace> getMatchedTraceToTrace() {
        return traces.stream()
                .filter(t -> t.getAttributes().containsKey(key1) && t.getAttributes().get(key1).equals(primaryValue))
                .filter(t -> t.getAttributes().containsKey(key2) && secondaryValues.contains(t.getAttributes().get(key2)))
                .collect(Collectors.toList());
    }
}
