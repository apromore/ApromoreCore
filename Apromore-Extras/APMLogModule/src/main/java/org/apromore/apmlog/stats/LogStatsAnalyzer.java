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
package org.apromore.apmlog.stats;

import com.google.common.collect.Lists;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.util.CalendarDuration;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LogStatsAnalyzer {

    private static final UnifiedSet<String> invalidCaseAttributeKeys = UnifiedSet.newSetWith("case:variant");
    private static final UnifiedSet<String> invalidEventAttributeKeys = UnifiedSet.newSetWith("activity", "resource");

    // ===================================================================================================
    // Common data
    // ===================================================================================================

    public static Set<String> getUniqueCaseAttributeKeys(List<ATrace> traceList) {
        return traceList.stream().flatMap(x -> x.getAttributes().keySet().stream()
                .filter(n -> !invalidCaseAttributeKeys.contains(n.toLowerCase()))
        ).collect(Collectors.toSet());
    }

    public static Set<String> getUniqueEventAttributeKeys(List<ActivityInstance> activityInstanceList) {
        return activityInstanceList.stream().flatMap(x -> x.getAttributes().keySet().stream()
                .filter(n -> !invalidEventAttributeKeys.contains(n.toLowerCase()))
        ).collect(Collectors.toSet());
    }

    public static Set<String> getUniqueCaseAttributeValues(List<ATrace> traceList, String attribute) {
        return traceList.stream().filter(x -> x.getAttributes().containsKey(attribute))
                .map(x -> x.getAttributes().get(attribute)).collect(Collectors.toSet());
    }

    public static Set<String> getUniqueEventAttributeValues(List<ActivityInstance> activityInstanceList,
                                                            String attribute) {
        return activityInstanceList.stream().filter(x -> x.getAttributes().containsKey(attribute))
                .map(x -> x.getAttributes().get(attribute)).collect(Collectors.toSet());
    }

    public static double getCaseUtilizationOf(ATrace trace) {
        DoubleArrayList procTimes = getProcessingTimesOf(trace);
        DoubleArrayList waitTimes = getWaitingTimesOf(trace);
        if (procTimes.sum() <= 0)
            return 1;

        return procTimes.sum() / (procTimes.sum() + waitTimes.sum());
    }

    public static DoubleArrayList getProcessingTimesOf(ATrace trace) {
        double[] array = trace.getActivityInstances().stream().mapToDouble(ActivityInstance::getDuration).toArray();
        return array.length == 0 ? new DoubleArrayList(0.0) : new DoubleArrayList(array);
    }

    public static DoubleArrayList getWaitingTimesOf(ATrace trace) {
        double[] array = trace.getActivityInstances().stream()
                .filter(n -> trace.getActivityInstanceIndexMap().get(n) < trace.getActivityInstances().size() - 1)
                .mapToDouble(n -> CalendarDuration.getDuration(trace.getCalendarModel(), n.getEndTime(),
                        trace.getNextOf(n).getStartTime()))
                .toArray();
        return array.length == 0 ? new DoubleArrayList(0.0) : new DoubleArrayList(array);
    }

    public static boolean withinRange(Number value, Number rangeFrom, Number rangeTo) {
        return value.doubleValue() >= rangeFrom.doubleValue() && value.doubleValue() <= rangeTo.doubleValue();
    }

    public static List<Map.Entry<String, List<Map.Entry<Integer, List<ActivityInstance>>>>>
    getCaseVariantGroups(PLog pLog) {
        return getCaseVariantGroups(pLog.getActivityInstances());
    }

    public static List<Map.Entry<String, List<Map.Entry<Integer, List<ActivityInstance>>>>>
    getCaseVariantGroups(List<ActivityInstance> activityInstanceList) {
        return Lists.reverse(activityInstanceList.stream()
                .collect(Collectors.groupingBy(ActivityInstance::getImmutableTraceIndex)).entrySet().stream()
                .collect(Collectors.groupingBy(entry ->
                        Arrays.toString(entry.getValue().stream()
                                .mapToInt(ActivityInstance::getNameIndicator).toArray()))).entrySet().stream()
                .sorted(Comparator.comparing(x -> x.getValue().size())).collect(Collectors.toList()));
    }

    public static Map<Integer, List<ATrace>> getCaseVariantsByPerspective(List<ATrace> traces, String attribute) {

        List<List<ATrace>> rawList = Lists.reverse(traces.stream()
                .collect(Collectors.groupingBy(trace -> getVariantIndicator(trace, attribute)))
                .values().stream()
                .sorted(Comparator.comparing(List<ATrace>::size)
                        .thenComparing(v -> getVariantIndicatorArray(v.get(0), attribute).length))
                .collect(Collectors.toList()));

        Map<Integer, List<ATrace>> output = new HashMap<>();

        int count = 0;
        for (List<ATrace> atList : rawList) {
            count += 1;
            final int index = count;
            output.put(index, atList);
        }

        return output;
    }

    private static String getVariantIndicator(ATrace trace, String attribute) {
        return Arrays.toString(getVariantIndicatorArray(trace, attribute));
    }

    private static Object[] getVariantIndicatorArray(ATrace trace, String attribute) {
        return trace.getActivityInstances().stream()
                .filter(act -> act.getAttributes().containsKey(attribute))
                .map(act -> act.getAttributeValue(attribute))
                .toArray();
    }

    // ===================================================================================================
    // PLog data
    // ===================================================================================================
    public static PTrace findCaseById(String caseId, PLog pLog) {
        return pLog.getPTraces().stream()
                .filter(x -> x.getCaseId().equals(caseId))
                .findFirst()
                .orElse(null);
    }

    public static List<PTrace> getValidTraces(PLog pLog) {
        BitSet caseIndexBS = pLog.getValidTraceIndexBS();
        return pLog.getOriginalPTraces().stream()
                .filter(x -> caseIndexBS.get(x.getImmutableIndex()))
                .collect(Collectors.toList());
    }

    public static List<ActivityInstance> getValidActivitiesOf(PTrace trace) {
        BitSet validEventBS = trace.getValidEventIndexBS();
        return trace.getOriginalActivityInstances().stream()
                .filter(x -> validEventBS.get(x.getFirstEventIndex()))
                .collect(Collectors.toList());
    }

    public static long getUniqueEventAttributeValueSize(String key, PLog pLog) {
        return getUniqueEventAttributeValueSize(key, pLog.getActivityInstances());
    }

    public static long getUniqueEventAttributeValueSize(String key, List<ActivityInstance> activityInstances) {
        return activityInstances.stream().filter(x -> x.getAttributes().containsKey(key))
                .map(x -> x.getAttributeValue(key)).collect(Collectors.toSet()).size();
    }

    public static long getUniqueCaseAttributeValueSize(String key, PLog pLog) {
        return pLog.getPTraces().stream().filter(x -> x.getAttributes().containsKey(key))
                .map(x -> x.getAttributes().get(key)).collect(Collectors.toSet()).size();
    }

    public static Map<String, Number> getEventAttributeValueCaseFrequencies(String key, PLog pLog) {
        return getEventAttributeValueCaseFrequencies(key, pLog.getActivityInstances());
    }

    public static Map<String, Number> getEventAttributeValueCaseFrequencies(String key,
                                                                            List<ActivityInstance> activityInstances) {
        return activityInstances.stream()
                .filter(x -> x.getAttributes().containsKey(key))
                .collect(Collectors.groupingBy(x -> x.getAttributes().get(key))).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x ->
                        x.getValue().stream()
                                .map(ActivityInstance::getImmutableTraceIndex).collect(Collectors.toSet()).size()
                ));
    }

    public static Map<String, Number> getEventAttributeValueTotalFrequencies(String key, PLog pLog) {
        return pLog.getActivityInstances().stream()
                .filter(x -> x.getAttributes().containsKey(key))
                .collect(Collectors.groupingBy(x -> x.getAttributes().get(key))).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, x -> x.getValue().size() ));
    }

}
