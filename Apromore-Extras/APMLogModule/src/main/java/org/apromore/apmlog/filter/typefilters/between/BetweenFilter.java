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
import org.apromore.apmlog.logobjects.ActivityInstance;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BetweenFilter {

    private BetweenFilter() {
        throw new IllegalStateException("Utility class");
    }

    public static List<PTrace> filter(List<PTrace> traces, LogFilterRule logFilterRule) {
        return traces.stream()
                .map(x -> filterTrace(x, logFilterRule))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static PTrace filterTrace(PTrace trace, LogFilterRule logFilterRule) {

        BetweenFilterRuleAlt rule = new BetweenFilterRuleAlt(logFilterRule);

        if (rule.getSourceRuleValue() == null || rule.getTargetRuleValue() == null ||
                isMissingCustomValue(rule.getSourceRuleValue(), rule.getTargetRuleValue()))
            return trace;

        List<ActivityInstance> activityInstanceList = new ArrayList<>(trace.getActivityInstances());
        Pair<Integer, Integer> fromToPair = getFromToPair(activityInstanceList, rule);

        if (rule.getChoice() == Choice.RETAIN && fromToPair.getLeft() == -1 && fromToPair.getRight() == -1)
            return null;

        BitSet bitSet = getUpdatedBitSet(rule.getChoice(), fromToPair, trace, activityInstanceList);
        trace.setValidEventIndexBS(bitSet);

        return trace;
    }

    private static Pair<Integer, Integer> getFromToPair(List<ActivityInstance> activityInstanceList,
                                                        BetweenFilterRuleAlt rule) {

        Pair<Integer, Integer> fromToPair = Pair.of(-1, -1);

        if (rule.getSource().equals(START) && !rule.getTarget().equals(END))
            fromToPair = updateByStartToTarget(fromToPair, rule, activityInstanceList);
        else if (!rule.getSource().equals(START) && rule.getTarget().equals(END))
            fromToPair = updateBySourceToEnd(fromToPair, rule, activityInstanceList);
        else
            fromToPair = getFromSourceToTarget(rule, activityInstanceList);

        return fromToPair;
    }

    private static Pair<Integer, Integer> updateByStartToTarget(Pair<Integer, Integer> fromToPair,
                                                                BetweenFilterRuleAlt rule,
                                                                List<ActivityInstance> activityInstanceList) {

        int index = getIndexOf(rule.getAttribute(), rule.getTarget(), rule.isTargetFirstOccur(), activityInstanceList);
        if (!rule.isIncludeTo())
            index -= 1;

        if (index >= 0)
            fromToPair = Pair.of(0, index);

        return fromToPair;
    }

    private static Pair<Integer, Integer> updateBySourceToEnd(Pair<Integer, Integer> fromToPair,
                                                              BetweenFilterRuleAlt rule,
                                                              List<ActivityInstance> activityInstanceList) {

        int index = getIndexOf(rule.getAttribute(), rule.getSource(), rule.isSourceFirstOccur(), activityInstanceList);
        if (!rule.isIncludeFrom())
            index += 1;

        if (index >= 0)
            fromToPair = Pair.of(index, activityInstanceList.size() - 1);

        return fromToPair;
    }

    private static BitSet getUpdatedBitSet(Choice choice,
                                           Pair<Integer, Integer> fromToPair,
                                           PTrace trace,
                                           List<ActivityInstance> activityInstanceList) {

        List<ActivityInstance> selected = activityInstanceList.subList(fromToPair.getLeft(), fromToPair.getRight() + 1);

        List<Integer> indexes = selected.stream()
                .flatMap(x -> x.getImmutableEventIndexes().stream())
                .collect(Collectors.toList());

        BitSet bitSet = new BitSet(trace.getOriginalValidEventIndexBS().size());

        if (choice == Choice.RETAIN) {
            for (int index : indexes) {
                bitSet.set(index);
            }
        } else {
            bitSet.set(0, trace.getOriginalValidEventIndexBS().size());
            for (int index : indexes) {
                bitSet.set(index, false);
            }
        }
        return bitSet;
    }

    private static boolean isMissingCustomValue(RuleValue rvFrom, RuleValue rvTo) {
        return !rvFrom.getCustomAttributes().containsKey(INCLUDE_SELECTION) ||
                !rvTo.getCustomAttributes().containsKey(INCLUDE_SELECTION) ||
                !rvFrom.getCustomAttributes().containsKey(FIRST_OCCURRENCE) ||
                !rvTo.getCustomAttributes().containsKey(FIRST_OCCURRENCE);
    }

    private static Pair<Integer, Integer> getFromSourceToTarget(BetweenFilterRuleAlt rule,
                                                                List<ActivityInstance> activityInstances) {

        int sourceIndex = rule.isSourceFirstOccur() ?
                getFirstIndexOf(rule.getAttribute(), rule.getSource(), activityInstances) :
                getLastIndexOf(rule.getAttribute(), rule.getSource(), activityInstances);
        int targetIndex = rule.isTargetFirstOccur() ?
                getFirstIndexOf(rule.getAttribute(), rule.getTarget(), activityInstances) :
                getLastIndexOf(rule.getAttribute(), rule.getTarget(), activityInstances);

        // =================================================================================
        // Requirements:
        //
        // If no event in a case matches the source activity of the Between filter,
        // then all events in the trace are retained.
        //
        // If no event in a case matches the target activity of the Between filter,
        // then all events in the trace are retained.
        //
        // If in a trace T, the index of the left anchor is > the index of the right anchor, deletes the entire trace.
        //
        // If in a trace, the index of the left anchor is = the index of the right anchor,
        // and (SourceIncluded || TargetIncluded) is false, the the entire trace is deleted.
        // In this statement, the indexes are defined as above.
        // =================================================================================
        if (sourceIndex == -1 || targetIndex == -1)
            return Pair.of(0, activityInstances.size() -1);

        int from = rule.isIncludeFrom() ? sourceIndex : sourceIndex + 1;
        int to = rule.isIncludeTo() ? targetIndex : targetIndex - 1;

        if (from > to || from < 0 || to > activityInstances.size() - 1)
            return Pair.of(-1, -1);

        return Pair.of(from, to);
    }

    private static int getIndexOf(String key,
                                  String value,
                                  boolean firstOccurrence,
                                  List<ActivityInstance> activityInstances) {
        return firstOccurrence ? getFirstIndexOf(key, value, activityInstances) :
                getLastIndexOf(key, value, activityInstances);
    }

    private static int getFirstIndexOf(String key, String value, List<ActivityInstance> activityInstances) {
        return activityInstances.stream()
                .filter(x -> x.getAttributes().containsKey(key))
                .filter(x -> x.getAttributeValue(key).equals(value))
                .mapToInt(activityInstances::indexOf)
                .findFirst()
                .orElse(-1);
    }

    private static int getLastIndexOf(String key, String value, List<ActivityInstance> activityInstances) {
        return Lists.reverse(activityInstances).stream()
                .filter(x -> x.getAttributes().containsKey(key))
                .filter(x -> x.getAttributeValue(key).equals(value))
                .mapToInt(activityInstances::indexOf)
                .findFirst()
                .orElse(-1);
    }

}
