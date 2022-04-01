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
package org.apromore.apmlog.filter.typefilters.between;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.rules.LogFilterRule;
import org.apromore.apmlog.filter.rules.RuleValue;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.END;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.FIRST_OCCURRENCE;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.INCLUDE_SELECTION;
import static org.apromore.apmlog.filter.typefilters.between.BetweenFilterSupport.START;
import org.apromore.apmlog.filter.types.Choice;
import org.apromore.apmlog.filter.types.OperationType;
import org.apromore.apmlog.logobjects.ActivityInstance;

import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

public class BetweenFilter {

    private BetweenFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<PTrace> filter(List<PTrace> traces, LogFilterRule logFilterRule) {
        boolean retain = logFilterRule.getChoice() == Choice.RETAIN;
        return traces.stream()
                .filter(x -> retain == (getFromToPair(x, logFilterRule) != null))
                .collect(Collectors.toList());
    }

    private static Pair<ActivityInstance, ActivityInstance> getFromToPair(PTrace pTrace, LogFilterRule logFilterRule) {
        ActivityInstance sourceNode = getSourceNode(pTrace, logFilterRule);
        if (sourceNode == null)
            return null;

        ActivityInstance targetNode = getTargetNode(sourceNode, pTrace, logFilterRule);
        if (targetNode == null)
            return null;

        // ==================================================================
        // Update Trace content
        // ==================================================================
        int sourceEventIndex = sourceNode.getFirstEventIndex();
        int targetEventIndex = targetNode.getImmutableEventIndexes()
                .get(targetNode.getImmutableEventIndexes().size()-1);
        BitSet indexes = new BitSet(pTrace.getImmutableEvents().size());
        indexes.set(sourceEventIndex, targetEventIndex + 1);
        pTrace.setValidEventIndexBS(indexes);

        return Pair.of(sourceNode, targetNode);
    }

    private static ActivityInstance getSourceNode(PTrace pTrace, LogFilterRule logFilterRule) {
        String attribute = logFilterRule.getKey();
        RuleValue rvFrom = findValue(OperationType.FROM, logFilterRule);
        if (rvFrom == null)
            return null;

        if (rvFrom.getStringValue().equals(START))
            return pTrace.getFirst();

        boolean sourceFirstOccur = Boolean.parseBoolean(rvFrom.getCustomAttributes().get(FIRST_OCCURRENCE));
        boolean includeFrom = Boolean.parseBoolean(rvFrom.getCustomAttributes().get(INCLUDE_SELECTION));
        List<ActivityInstance> actList = sourceFirstOccur ? pTrace.getActivityInstances() :
                Lists.reverse(pTrace.getActivityInstances());

        ActivityInstance rawSource = actList.stream()
                .filter(x -> x.getAttributes().containsKey(attribute))
                .filter(x -> x.getAttributeValue(attribute).equals(rvFrom.getStringValue()))
                .findFirst().orElse(null);

        if (rawSource == null)
            return null;

        return includeFrom ? rawSource : pTrace.getNextOf(rawSource);
    }

    private static ActivityInstance getTargetNode(ActivityInstance sourceNode,
                                                  PTrace pTrace,
                                                  LogFilterRule logFilterRule) {
        String attribute = logFilterRule.getKey();
        RuleValue rvTo = findValue(OperationType.TO, logFilterRule);
        if (rvTo == null)
            return null;

        if (rvTo.getStringValue().equals(END))
            return pTrace.getLast();

        boolean targetFirstOccur = Boolean.parseBoolean(rvTo.getCustomAttributes().get(FIRST_OCCURRENCE));
        boolean includeTo = Boolean.parseBoolean(rvTo.getCustomAttributes().get(INCLUDE_SELECTION));

        List<ActivityInstance> rawActList = pTrace.getActivityInstances();
        int sourceIndex = pTrace.getActivityInstances().indexOf(sourceNode);
        if (sourceIndex + 1 >= rawActList.size())
            return null;

        List<ActivityInstance> subList = rawActList.subList(sourceIndex + 1, rawActList.size());
        List<ActivityInstance> operationList = targetFirstOccur ? subList : Lists.reverse(subList);

        ActivityInstance rawTarget = operationList.stream()
                .filter(x -> x.getAttributes().containsKey(attribute))
                .filter(x -> x.getAttributeValue(attribute).equals(rvTo.getStringValue()))
                .findFirst().orElse(null);

        if (rawTarget == null)
            return null;

        int targetIndex = pTrace.getActivityInstances().indexOf(rawTarget);
        return includeTo ? rawTarget : pTrace.getActivityInstances().get(targetIndex - 1);

    }

    public static RuleValue findValue(OperationType operationType, LogFilterRule logFilterRule) {
        return logFilterRule.getPrimaryValues().stream()
                .filter(x -> x.getOperationType() == operationType).findFirst().orElse(null);
    }

}
