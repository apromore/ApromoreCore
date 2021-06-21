/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
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
package org.apromore.apmlog.stats;

import org.apromore.apmlog.AActivity;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.filter.types.FilterType;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Chii Chang
 */
public class StatsUtil {

    //==============================================================================================================
    // Public methods
    //==============================================================================================================

    public static Map<Integer, List<PTrace>> getVariantGroupMap(PLog pLog) {

        List<PTrace> validTraces = getValidTraces(pLog);

        return validTraces.stream()
                .collect(Collectors.groupingBy(PTrace::getCaseVariantId));
    }


    public static List<PTrace> getValidTraces(PLog pLog) {
        BitSet caseIndexBS = pLog.getValidTraceIndexBS();
        return pLog.getPTraceList().stream()
                .filter(x -> caseIndexBS.get(x.getImmutableIndex()))
                .collect(Collectors.toList());
    }

    private static UnifiedMap<String, UnifiedSet<EventAttributeValue>>
    getEventAttributeValuesByActivities(List<AActivity> activities, int traceSize) {
        UnifiedSet<String> keys = activities.stream()
                .parallel()
                .flatMap(x -> x.getAttributes().keySet().stream())
                .collect(Collectors.toCollection(UnifiedSet::new));

        UnifiedMap<String, Map<String, List<AActivity>>> eavaMap = new UnifiedMap<>(keys.size());

        for (String key : keys) {
            Map<String, List<AActivity>> groups = activities.stream()
                    .filter(x -> x.getAttributes().containsKey(key))
                    .collect(Collectors.groupingBy(x -> x.getAttributes().get(key)));

            eavaMap.put(key, groups);
        }

        UnifiedMap<String, UnifiedSet<EventAttributeValue>> eavMap = new UnifiedMap<>(keys.size());

        for (Map.Entry<String, Map<String, List<AActivity>>> entry : eavaMap.entrySet()) {

            eavMap.put(entry.getKey(), new UnifiedSet<>(entry.getValue().size()));

            Map<String, List<AActivity>> vals = entry.getValue();

            for (Map.Entry<String, List<AActivity>> valEntry : vals.entrySet()) {
                EventAttributeValue eav =
                        new EventAttributeValue(valEntry.getKey(),
                                new UnifiedSet<>(valEntry.getValue()), traceSize);

                eavMap.get(entry.getKey()).put(eav);
            }
        }

        return eavMap;
    }

    public static UnifiedMap<String, UnifiedSet<EventAttributeValue>> getEventAttributeValues(
            List<ATrace> aTraces) {

        List<AActivity> allActs = aTraces.stream()
                .parallel()
                .flatMap(x -> x.getActivityList().stream())
                .collect(Collectors.toList());

        return getEventAttributeValuesByActivities(allActs, aTraces.size());
    }


    public static UnifiedMap<String, UnifiedSet<EventAttributeValue>> getValidEventAttributeValues(
            List<PTrace> validTraces) {


        List<AActivity> validActivities = new ArrayList<>();

        for (PTrace pTrace : validTraces) {
            BitSet validEvents = pTrace.getValidEventIndexBitSet();
            if (validEvents != null) {
                List<AActivity> activityList = pTrace.getActivityList().stream()
                        .parallel()
                        .filter(x -> validEvents.get(x.getImmutableEventList().get(0).getIndex()))
                        .collect(Collectors.toList());
                validActivities.addAll(activityList);
            }
        }

        return getEventAttributeValuesByActivities(validActivities, validTraces.size());
    }

    public static Set<Integer> getCaseIndexes(List<ATrace> traces) {
        return traces.stream().map(x->x.getImmutableIndex()).collect(Collectors.toSet());
    }

    public static UnifiedMap<String, UnifiedSet<CaseAttributeValue>> getCaseAttributeValues(List<ATrace> traceList) {


        UnifiedSet<String> allKeys = traceList.stream()
                .parallel()
                .flatMap(x -> x.getAttributeMap().keySet().stream())
                .collect(Collectors.toCollection(UnifiedSet::new));

        // (2) for each key, group traces with values
        UnifiedMap<String, Map<String, List<ATrace>>> keyValCaseOccurMap = new UnifiedMap<>(allKeys.size());

        for (String key : allKeys) {
            Map<String, List<ATrace>> grouped = traceList.stream()
                    .filter(x -> x.getAttributeMap().containsKey(key))
                    .collect(Collectors.groupingBy(x -> x.getAttributeMap().get(key)));
            keyValCaseOccurMap.put(key, grouped);
        }

        // (3) create CaseAttributeValues
        UnifiedMap<String, UnifiedSet<CaseAttributeValue>> caseAttributeValues = new UnifiedMap<>(allKeys.size());

        for (Map.Entry<String, Map<String, List<ATrace>>> entry : keyValCaseOccurMap.entrySet()) {
            String attrKey = entry.getKey();
            Map<String, List<ATrace>> valOccurMap = entry.getValue();

            UnifiedSet<CaseAttributeValue> cavSet = new UnifiedSet<>(valOccurMap.size());

            int[] arr = valOccurMap.entrySet().stream().mapToInt(x -> x.getValue().size()).toArray();
            IntArrayList ial = new IntArrayList(arr);

            int maxOccurSize = ial.max();

            for (Map.Entry<String, List<ATrace>> voe: valOccurMap.entrySet()) {
                int[] occurredIndexes = voe.getValue().stream()
                        .mapToInt(ATrace::getImmutableIndex)
                        .toArray();
                IntArrayList indexes = new IntArrayList(occurredIndexes);

                CaseAttributeValue cav = new CaseAttributeValue(voe.getKey(), indexes, traceList.size());
                cav.setRatio(100 * ( (double) cav.getCases() / maxOccurSize));
                cavSet.add(cav);
            }
            caseAttributeValues.put(attrKey, cavSet);
        }

        return caseAttributeValues;
    }

    public static UnifiedMap<String, UnifiedSet<CaseAttributeValue>> getValidCaseAttributeValues(List<PTrace> pTraceList) {

        //
        // (1) get all keys
        // (2) for each key, group traces with values
        // (3) use the group generated in (2) to produce the final map
        //


        // (1) get all keys

        UnifiedSet<String> allKeys = pTraceList.stream()
                .flatMap(x -> x.getAttributeMap().keySet().stream())
                .collect(Collectors.toCollection(UnifiedSet::new));

        // (2) for each key, group traces with values
        UnifiedMap<String, Map<String, List<PTrace>>> keyValCaseOccurMap = new UnifiedMap<>(allKeys.size());

        for (String key : allKeys) {
            Map<String, List<PTrace>> grouped = pTraceList.stream()
                    .filter(x -> x.getAllAttributes().containsKey(key))
                    .collect(Collectors.groupingBy(x -> x.getAllAttributes().get(key)));
            keyValCaseOccurMap.put(key, grouped);
        }

        // (3) create CaseAttributeValues
        UnifiedMap<String, UnifiedSet<CaseAttributeValue>> caseAttributeValues = new UnifiedMap<>(allKeys.size());

        for (Map.Entry<String, Map<String, List<PTrace>>> entry : keyValCaseOccurMap.entrySet()) {
            String attrKey = entry.getKey();
            Map<String, List<PTrace>> valOccurMap = entry.getValue();

            UnifiedSet<CaseAttributeValue> cavSet = new UnifiedSet<>(valOccurMap.size());

            int[] arr = valOccurMap.entrySet().stream().mapToInt(x -> x.getValue().size()).toArray();
            IntArrayList ial = new IntArrayList(arr);

            int maxOccurSize = ial.max();

            for (Map.Entry<String, List<PTrace>> voe: valOccurMap.entrySet()) {
                int[] occurredIndexes = voe.getValue().stream()
                        .mapToInt(PTrace::getImmutableIndex)
                        .toArray();
                IntArrayList indexes = new IntArrayList(occurredIndexes);

                CaseAttributeValue cav = new CaseAttributeValue(voe.getKey(), indexes, pTraceList.size());
                cav.setRatio(100 * ( (double) cav.getCases()) / maxOccurSize);
                cavSet.add(cav);
            }
            caseAttributeValues.put(attrKey, cavSet);
        }

        return caseAttributeValues;
    }

    public static List<AActivity> getValidActivitiesOf(PTrace trace) {
        BitSet validEventBS = trace.getValidEventIndexBitSet();
        return trace.getOriginalActivityList().stream()
                .filter(x -> validEventBS.get(x.getEventIndexes().get(0)))
                .collect(Collectors.toList());
    }

    public static Map<String, List<CustomTriple>> getTargetNodeDataBySourceNode(String key,
                                                                                String sourceNode,
                                                                                PLog pLog,
                                                                                UnifiedSet<EventAttributeValue> set) {

        EventAttributeValue sourceEav = set.stream()
                .filter(x -> x.getValue().equals(sourceNode))
                .findFirst()
                .orElse(null);

        if (sourceEav == null) return null;

        // target activity, source activity
        Map<AActivity, AActivity> validFollows = getValidNextActivities(key, sourceEav, pLog);

        List<CustomTriple> triples = new ArrayList<>(validFollows.size());
        for (Map.Entry<AActivity, AActivity> pair : validFollows.entrySet()) {
            // target activity, source activity, target attribute value
            triples.add(CustomTriple.of(pair.getKey(), pair.getValue(), pair.getValue().getAllAttributes().get(key)));
        }

        return triples.stream().collect(Collectors.groupingBy(CustomTriple::getValue));
    }




    //==============================================================================================================
    // Private support methods
    //==============================================================================================================

    /**
     *
     * @param key
     * @param v
     * @param pLog
     * @return Map of source activity, target activity
     */
    private static Map<AActivity, AActivity> getValidNextActivities(String key, EventAttributeValue v, PLog pLog) {
        BitSet validTraceBS = pLog.getValidTraceIndexBS();
        List<PTrace> traces = pLog.getOriginalPTraceList();

        return v.getOccurActivities().stream()
                .filter(x -> validTraceBS.get(x.getImmutableTraceIndex()) &&
                        x.getImmutableIndex() < traces.get(
                                x.getImmutableTraceIndex()).getOriginalActivityList().size() &&
                        isValidEvent(x.getEventIndexes().get(0), traces.get(x.getImmutableTraceIndex())) &&
                        getFollowupActivity(x, traces.get(x.getImmutableTraceIndex()), key) != null)
                .collect(Collectors.toMap(x -> x,
                        x -> getFollowupActivity(x, traces.get(x.getImmutableTraceIndex()), key)));

    }

    private static AActivity getFollowupActivity(AActivity sourceActivity, PTrace pTrace, String reqKey) {

        if (sourceActivity.getImmutableIndex() + 1 >= pTrace.getOriginalActivityList().size()) return null;

        return pTrace.getActivityList().stream()
                .filter(x -> x.getAllAttributes().containsKey(reqKey))
                .filter(x -> x.getImmutableIndex() > sourceActivity.getImmutableIndex())
                .findFirst()
                .orElse(null);
    }

    public static AActivity getValidPreviousActivity(AActivity activity, PTrace trace) {
        return trace.getPreviousActivityOf(activity);
    }

    public static double getArcDurationOf(AActivity indegree, AActivity outdegree) {
        double inET = indegree.getEndTimeMilli();
        double outST = outdegree.getStartTimeMilli();
        return outST > inET ? outST - inET : 0;
    }

    private static boolean isValidEvent(int eventIndex, PTrace pTrace) {
        return pTrace.getValidEventIndexBitSet().get(eventIndex);
    }

}
