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
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.*;

import static java.util.Map.Entry.comparingByValue;

public class LaLog implements APMLog {
    public List<ATrace> immutableTraces; // this is the immutable traces
    public List<ATrace> traceList; // this is mutable traces;
    public UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> eventAttributeOccurMap;
    public HashBiMap<String, Integer> activityNameBiMap = new HashBiMap<>();

    public UnifiedMap<Integer, Integer> variantIdFreqMap;
    public HashBiMap<Integer, String> actIdNameMap = new HashBiMap<>();
    public UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueCasesFreqMap;
    public UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap;
    public UnifiedMap<String, UnifiedMap<String, Integer>> caseAttributeValueFreqMap;
    public UnifiedMap<String, Integer> activityMaxOccurMap = new UnifiedMap<>();
    public String timeZone = "";
    public long startTime = -1;
    public long endTime = -1;
    public long eventSize = 0;


    public HashBiMap<IntArrayList, Integer> actNameIdxCId;

    public ActivityNameMapper activityNameMapper;

    public DefaultChartDataCollection defaultChartDataCollection;

    public AAttributeGraph attributeGraph;

    public DoubleArrayList caseDurationList;

    public void updateStats() {

        actNameIdxCId = new HashBiMap<>();

        variantIdFreqMap = new UnifiedMap<>();

        UnifiedMap<IntArrayList, Integer> actNameIndexesFreqMap = new UnifiedMap<>();

        List<IntArrayList> traceActNameIndexes = new ArrayList<>();

        eventSize = 0;

        caseDurationList = new DoubleArrayList(traceList.size());

        for (int i = 0; i < traceList.size(); i++) {

            ATrace trace = traceList.get(i);

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

        int size = eventAttributeOccurMap.size();
        eventAttributeValueFreqMap = new UnifiedMap<>(size);

        int counter = 0;

        for (String key : eventAttributeOccurMap.keySet()) {
            UnifiedMap<String, UnifiedSet<AActivity>> valOccurMap = eventAttributeOccurMap.get(key);
            UnifiedMap<String, Integer> valFreqMap = new UnifiedMap<>(valOccurMap.size());

            UnifiedMap<String, Integer> valCaseFreqMap = new UnifiedMap<>(valOccurMap.size());

            for (String val : valOccurMap.keySet()) {
                int freq = valOccurMap.get(val).size();
                valFreqMap.put(val, freq);

                UnifiedSet<AActivity> occurSet = valOccurMap.get(val);
                UnifiedSet<Integer> traceIndexes = new UnifiedSet<>();
                for (AActivity act : occurSet) {
                    int traceIndex = act.getMutableTraceIndex();
                    if (!traceIndexes.contains(traceIndex)) traceIndexes.add(traceIndex);
                }
                valCaseFreqMap.put(val, traceIndexes.size());
            }
            eventAttributeValueFreqMap.put(key, valFreqMap);
            eventAttributeValueCasesFreqMap.put(key, valCaseFreqMap);

            counter += 1;
        }

        defaultChartDataCollection = new DefaultChartDataCollection(this);

        attributeGraph = new AAttributeGraph(this);
    }

    public IntArrayList getActivityNameIndexes(ATrace aTrace) {
        IntArrayList nameIndexes = new IntArrayList(aTrace.getActivityList().size());
        List<AActivity> activityList = aTrace.getActivityList();
        for (int i = 0; i < activityList.size(); i++) {
            int actNameIndex = activityNameBiMap.get(activityList.get(i).getName());
            nameIndexes.add(actNameIndex);
        }
        return nameIndexes;
    }

    public void setEventAttributeOccurMap(UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>>
                                                  eventAttributeOccurMap) {
        this.eventAttributeOccurMap = eventAttributeOccurMap;
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
    public DoubleArrayList getCaseDurations() {
        return caseDurationList;
    }

    @Override
    public DefaultChartDataCollection getDefaultChartDataCollection() {
        return defaultChartDataCollection;
    }

    @Override
    public UnifiedMap<String, Integer> getActivityMaxOccurMap() {
        UnifiedMap<String, UnifiedSet<AActivity>> actsMap = eventAttributeOccurMap.get("concept:name");
        int size = actsMap.size();
        UnifiedMap<String, Integer> output = new UnifiedMap<>(size);
        for (String actName : actsMap.keySet()) {
            output.put(actName, actsMap.get(actName).size());
        }
        return output;
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
        return eventAttributeOccurMap.get("concept:name").size();
    }

    @Override
    public UnifiedMap<Integer, Integer> getCaseVariantIdFrequencyMap() {
        return variantIdFreqMap;
    }

    @Override
    public UnifiedMap<String, UnifiedMap<String, Integer>> getCaseAttributeValueFreqMap() {
        return caseAttributeValueFreqMap;
    }

    @Override
    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap() {
        return eventAttributeValueFreqMap;
    }

    @Override
    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueCasesFreqMap() {
        return eventAttributeValueCasesFreqMap;
    }

    @Override
    public List<String> getCaseAttributeNameList() {
        List<String> nameList = new ArrayList<>(caseAttributeValueFreqMap.keySet());
        Collections.sort(nameList);
        return nameList;
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
    public void setCaseVariantSize(int caseVariantSize) {

    }

    @Override
    public List<String> getActivityNameList(int caseVariantId) {
        IntArrayList actNameIndexes = actNameIdxCId.inverse().get(caseVariantId);
        List<String> actNames = new ArrayList<>(actNameIndexes.size());
        for (int i = 0; i < actNameIndexes.size(); i++) {
            int index = actNameIndexes.get(i);
            String actName = activityNameBiMap.inverse().get(index);
            actNames.add(actName);
        }
        return actNames;
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
    public void setMinDuration(double minDuration) {
    }

    @Override
    public double getMaxDuration() {
        return !caseDurationList.isEmpty() ? caseDurationList.max() : 0;
    }

    @Override
    public void setMaxDuration(double maxDuration) {
    }

    @Override
    public List<ATrace> getImmutableTraces() {
        return immutableTraces;
    }

    @Override
    public List<ATrace> getTraceList() {
        return traceList;
    }

    @Override
    public void setTraceList(List<ATrace> traceList) {
        this.traceList = traceList;
    }


    @Override
    public UnifiedSet<String> getEventAttributeNameSet() {
        UnifiedSet<String> nameSet = new UnifiedSet<>(eventAttributeValueFreqMap.keySet());
        return nameSet;
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
    public XLog toXLog() {
        return APMLogToXLog.getXLog(this);
    }

    @Override
    public AAttributeGraph getAAttributeGraph() {
        return attributeGraph;
    }

    @Override
    public UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> getEventAttributeOccurMap() {
        return eventAttributeOccurMap;
    }

    @Override
    public UnifiedMap<Integer, Integer> getVariantIdFreqMap() {
        return variantIdFreqMap;
    }

    @Override
    public APMLog clone() {
        return null;
    }

}
