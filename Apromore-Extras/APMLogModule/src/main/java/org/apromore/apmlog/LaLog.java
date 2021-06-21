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
package org.apromore.apmlog;


import org.apromore.apmlog.filter.PTrace;
import org.apromore.apmlog.stats.AAttributeGraph;
import org.apromore.apmlog.stats.CaseAttributeValue;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.apromore.apmlog.stats.StatsUtil;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

public class LaLog implements APMLog {
    protected final List<ATrace> immutableTraces = new ArrayList<>(); // this is the immutable traces
    protected final List<ATrace> traceList = new ArrayList<>(); // this is mutable traces;
    protected UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> eventAttributeOccurMap;
    protected HashBiMap<String, Integer> activityNameBiMap = new HashBiMap<>();

    protected UnifiedMap<Integer, Integer> variantIdFreqMap;
    protected HashBiMap<Integer, String> actIdNameMap = new HashBiMap<>();
    protected UnifiedMap<String, Integer> activityMaxOccurMap = new UnifiedMap<>();
    protected String timeZone = "";
    protected long startTime = -1;
    protected long endTime = -1;
    protected long eventSize = 0;


    protected HashBiMap<IntArrayList, Integer> actNameIdxCId;

    protected ActivityNameMapper activityNameMapper;

    protected AAttributeGraph attributeGraph;

    protected DoubleArrayList caseDurationList;

    protected UnifiedMap<String, UnifiedSet<EventAttributeValue>> eventAttributeValues;
    protected UnifiedMap<String, UnifiedSet<CaseAttributeValue>> caseAttributeValues;

    protected final UnifiedMap<IntArrayList, Integer> actNameIndexesFreqMap = new UnifiedMap<>();
    protected final List<IntArrayList> traceActNameIndexes = new ArrayList<>();

    protected final List<AActivity> activities = new ArrayList<>();

    public void updateStats() {

        activities.clear();
        activities.addAll(traceList.stream().flatMap(x->x.getActivityList().stream()).collect(Collectors.toList()));

        actNameIdxCId = new HashBiMap<>();

        variantIdFreqMap = new UnifiedMap<>();

        actNameIndexesFreqMap.clear();

        traceActNameIndexes.clear();

        eventSize = 0;

        caseDurationList = new DoubleArrayList(traceList.size());


        UnifiedMap<String, UnifiedMap<String, IntArrayList>> caseAttrValOccurMap = new UnifiedMap<>();

        for (int i = 0; i < traceList.size(); i++) {

            ATrace trace = traceList.get(i);

            UnifiedMap<String, String> tAttrMap = trace.getAttributeMap();

            for (String attrKey : tAttrMap.keySet()) {
                String val = trace.getAttributeMap().get(attrKey);

                if (caseAttrValOccurMap.keySet().contains(attrKey)) {
                    UnifiedMap<String, IntArrayList> valOccurMap = caseAttrValOccurMap.get(attrKey);
                    if (valOccurMap.containsKey(val)) {
                        valOccurMap.get(val).add(i);
                    } else {
                        IntArrayList indexes = new IntArrayList();
                        indexes.add(i);
                        valOccurMap.put(val, indexes);
                    }
                } else {
                    IntArrayList indexes = new IntArrayList();
                    indexes.add(i);
                    UnifiedMap<String, IntArrayList> valOccurMap = new UnifiedMap<>();
                    valOccurMap.put(val, indexes);
                    caseAttrValOccurMap.put(attrKey, valOccurMap);
                }
            }

            caseDurationList.add(trace.getDuration());

            IntArrayList actNameIndexes = getActivityNameIndexes(trace);
            traceActNameIndexes.add(actNameIndexes);
            if (actNameIndexesFreqMap.containsKey(actNameIndexes)) {
                int freq = actNameIndexesFreqMap.get(actNameIndexes) + 1;
                actNameIndexesFreqMap.put(actNameIndexes, freq);
            } else {
                actNameIndexesFreqMap.put(actNameIndexes, 1);
            }

            if (startTime < 1 || trace.getStartTimeMilli() < startTime) startTime = trace.getStartTimeMilli();
            if (trace.getEndTimeMilli() > endTime) endTime = trace.getEndTimeMilli();

            if (trace instanceof PTrace) {
                BitSet vEvents = ((PTrace) trace).getValidEventIndexBitSet();
                eventSize += vEvents.cardinality();
            } else {
                eventSize += trace.getEventSize();
            }
        }

        caseAttributeValues = new UnifiedMap<>();


        for (String attrKey : caseAttrValOccurMap.keySet()) {
            UnifiedMap<String, IntArrayList> valOccurMap = caseAttrValOccurMap.get(attrKey);
            UnifiedSet<CaseAttributeValue> cavSet = new UnifiedSet<>();

            int maxOccurSize = 0;
            for (String val : valOccurMap.keySet()) {
                int size = valOccurMap.get(val).size();
                if (size > maxOccurSize) maxOccurSize = size;
            }

            for (String val : valOccurMap.keySet()) {
                CaseAttributeValue cav = new CaseAttributeValue(val, valOccurMap.get(val), traceList.size());
                cav.setRatio(100 * ( (double) cav.getCases() / maxOccurSize));
                cavSet.add(cav);
            }
            caseAttributeValues.put(attrKey, cavSet);
        }

        List<Map.Entry<IntArrayList, Integer>> list = new ArrayList<>(actNameIndexesFreqMap.entrySet());
        list.sort(comparingByValue());

        int cIdCount = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            cIdCount += 1;
            Map.Entry<IntArrayList, Integer> entry = list.get(i);
            IntArrayList ial = entry.getKey();
            int freq = entry.getValue();

            variantIdFreqMap.put(cIdCount, freq);
            actNameIdxCId.put(ial, cIdCount);
        }

        for (int i = 0; i < traceList.size(); i++) {
            ATrace trace = traceList.get(i);
            IntArrayList iIAL = traceActNameIndexes.get(i);
            int cId = actNameIdxCId.get(iIAL);
            trace.setCaseVariantId(cId);
        }

        eventAttributeValues = StatsUtil.getEventAttributeValues(traceList);

        updateActivityOccurMaxMap();

        attributeGraph = new AAttributeGraph(this);
    }

    public Set<Integer> getCaseIndexes() {
        return StatsUtil.getCaseIndexes(traceList);
    }

    protected void updateCaseVariants() {

        UnifiedSet<EventAttributeValue> eavSet = getEventAttributeValues().get("concept:name");
        actIdNameMap = new HashBiMap<>();
        activityNameBiMap = new HashBiMap<>(eavSet.size());
        int index = 0;
        for (EventAttributeValue eav : eavSet) {
            activityNameBiMap.put(eav.getValue(), index);
            actIdNameMap.put(index, eav.getValue());
            index += 1;
        }

        Map<String, List<ATrace>> groups = getTraceList().stream()
                .collect(Collectors.groupingBy(x -> x.getActivityNameIndexString(activityNameBiMap)));

        List<Map.Entry<String, List<ATrace>>> sorted = groups.entrySet().stream()
                .sorted( (f1, f2) -> Long.compare(f2.getValue().size(), f1.getValue().size()) )
                .collect(Collectors.toList());

        variantIdFreqMap = new UnifiedMap<>(sorted.size());

        int variId = 1;
        for (Map.Entry<String, List<ATrace>> entry : sorted) {

            variantIdFreqMap.put(variId, entry.getValue().size());

            for (ATrace aTrace : entry.getValue()) {
                aTrace.setCaseVariantId(variId);
            }
            variId += 1;
        }
    }

    protected void updateActivityOccurMaxMap() {

        UnifiedMap<String, Integer> actMaxOccur = new UnifiedMap<>();

        for (ATrace aTrace: traceList) {
            List<AActivity> aActivityList = aTrace.getActivityList();

            UnifiedMap<String, Integer> actOccurFreq = new UnifiedMap<>();

            for (AActivity aActivity: aActivityList) {
                String actName = aActivity.getName();
                if (actOccurFreq.containsKey(actName)) {
                    int freq = actOccurFreq.get(actName) + 1;
                    actOccurFreq.put(actName, freq);
                } else actOccurFreq.put(actName, 1);
            }

            for (String actName : actOccurFreq.keySet()) {
                int freq = actOccurFreq.get(actName);
                if (actMaxOccur.containsKey(actName)) {
                    int currentMax = actMaxOccur.get(actName);
                    if (freq > currentMax) actMaxOccur.put(actName, freq);
                } else actMaxOccur.put(actName, freq);
            }
        }

        this.activityMaxOccurMap = actMaxOccur;

    }

    protected IntArrayList getActivityNameIndexes(ATrace aTrace) {
        IntArrayList nameIndexes = new IntArrayList(aTrace.getActivityList().size());
        List<AActivity> activityList = aTrace.getActivityList();
        for (int i = 0; i < activityList.size(); i++) {
            int actNameIndex = activityNameBiMap.get(activityList.get(i).getName());
            nameIndexes.add(actNameIndex);
        }
        return nameIndexes;
    }

    protected void setEventAttributeOccurMap(UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>>
                                                  eventAttributeOccurMap) {
        this.eventAttributeOccurMap = eventAttributeOccurMap;
    }


    protected void setTraces(List<ATrace> traces) {
        setTraceList(traces);
    }

    protected void setImmutableTraces(List<ATrace> traces) {
        immutableTraces.clear();
        immutableTraces.addAll(traces);
    }

    @Override
    public void add(ATrace trace) {
        immutableTraces.add(trace);
        traceList.add(trace);
    }

    @Override
    public HashBiMap<String, Integer> getActivityNameBiMap() {
        return activityNameBiMap;
    }

    @Override
    public void setActivityNameBiMap(HashBiMap<String, Integer> activityNameBiMap) {
        this.activityNameBiMap = activityNameBiMap;
    }

    @Override
    public UnifiedMap<String, Integer> getActivityMaxOccurMap() {
        return activityMaxOccurMap;
    }

    @Override
    public ActivityNameMapper getActivityNameMapper() {
        return activityNameMapper;
    }

    @Override
    public ATrace get(String caseId) {
        for (int i = 0; i < traceList.size(); i++) {
            if (traceList.get(i).getCaseId().equals(caseId)) return traceList.get(i);
        }
        return null;
    }

    @Override
    public UnifiedMap<String, ATrace> getTraceUnifiedMap() {
        UnifiedMap<String, ATrace> map = new UnifiedMap<>(traceList.size());
        for (int i = 0; i < traceList.size(); i++) {
            map.put(traceList.get(i).getCaseId().intern(), traceList.get(i));
        }
        return map;
    }

    @Override
    public int getUniqueActivitySize() {
        return eventAttributeValues.get("concept:name").size();
    }

    @Override
    public UnifiedMap<Integer, Integer> getCaseVariantIdFrequencyMap() {
        return variantIdFreqMap;
    }

    @Override
    public List<String> getCaseAttributeNameList() {
        return new ArrayList<>(caseAttributeValues.keySet());
    }

    @Override
    public long getEventSize() {
        return eventSize;
    }

    @Override
    public void setEventSize(int eventSize) {
        this.eventSize = eventSize;
    }

    @Override
    public long getCaseVariantSize() {
        return variantIdFreqMap.size();
    }

    @Override
    public List<String> getActivityNameList(int caseVariantId) {
        return eventAttributeOccurMap.containsKey("concept:name") ?
                new ArrayList<>(eventAttributeOccurMap.get("concept:name").keySet()) : null;
    }

    @Override
    public void setVariantIdFreqMap(UnifiedMap<Integer, Integer> variantIdFreqMap) {
        this.variantIdFreqMap = variantIdFreqMap;
    }

    @Override
    public double getMinDuration() {
        return !caseDurationList.isEmpty() ? caseDurationList.min() : 0;
    }

    @Override
    public double getMaxDuration() {
        return !caseDurationList.isEmpty() ? caseDurationList.max() : 0;
    }

    @Override
    public List<ATrace> getImmutableTraces() {
        return immutableTraces;
    }

    @Override
    public final List<ATrace> getTraceList() {
        return traceList;
    }

    @Override
    public void setTraceList(List<ATrace> traceList) {
        this.traceList.clear();
        this.traceList.addAll(traceList);
    }


    @Override
    public UnifiedSet<String> getEventAttributeNameSet() {
        return new UnifiedSet<>(eventAttributeValues.keySet());
    }

    @Override
    public int size() {
        return traceList.size();
    }

    @Override
    public ATrace get(int index) {
        return traceList.get(index);
    }

    @Override
    public int immutableSize() {
        return immutableTraces.size();
    }

    @Override
    public ATrace getImmutable(int index) {
        return immutableTraces.get(index);
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    @Override
    public long getEndTime() {
        return endTime;
    }

    @Override
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    @Override
    public String getTimeZone() {
        return null;
    }

    @Override
    public String getMinDurationString() {
        return Util.durationStringOf(getMinDuration());
    }

    @Override
    public String getMaxDurationString() {
        return Util.durationStringOf(getMaxDuration());
    }


    @Override
    public double getAverageDuration() {
        return caseDurationList.average();
    }

    @Override
    public double getMedianDuration() {
        return caseDurationList.median();
    }

    @Override
    public String getAverageDurationString() {
        return Util.durationShortStringOf(getAverageDuration());
    }

    @Override
    public String getMedianDurationString() {
        return Util.durationShortStringOf(getMedianDuration());
    }

    @Override
    public String getStartTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(this.startTime));
    }

    @Override
    public String getEndTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(this.endTime));
    }

    @Override
    public List<AActivity> getActivities() {
        return activities;
    }

    @Override
    public XLog toXLog() {
        return APMLogToXLog.getXLog(this);
    }

    @Override
    public AAttributeGraph getAAttributeGraph() {
        return attributeGraph;
    }

    @Override
    public UnifiedMap<Integer, Integer> getVariantIdFreqMap() {
        return variantIdFreqMap;
    }

    @Override
    public DoubleArrayList getCaseDurations() {
        return caseDurationList;
    }

    @Override
    public DoubleArrayList getTraceProcessingTimes() {
        double[] array = traceList.stream().mapToDouble(x -> x.getProcessingTimes().sum()).toArray();
        return new DoubleArrayList(array);
    }

    @Override
    public DoubleArrayList getTraceWaitingTimes() {
        double[] array = traceList.stream().mapToDouble(x -> x.getWaitingTimes().sum()).toArray();
        return new DoubleArrayList(array);
    }

    public UnifiedMap<String, UnifiedSet<EventAttributeValue>> getEventAttributeValues() {
        return eventAttributeValues;
    }

    public UnifiedMap<String, UnifiedSet<CaseAttributeValue>> getCaseAttributeValues() {
        return caseAttributeValues;
    }

    public UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> getEventAttributeOccurMap() {
        return eventAttributeOccurMap;
    }
}
