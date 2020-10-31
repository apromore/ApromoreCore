/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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
package org.apromore.apmlog.immutable;


import org.apromore.apmlog.*;
import org.apromore.apmlog.stats.AAttributeGraph;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Map.Entry.comparingByValue;

public class ImmutableLog extends LaLog {



    public ImmutableLog() {
        traceList = new ArrayList<>();
        immutableTraces = new ArrayList<>();
        eventAttributeOccurMap = new UnifiedMap<>();
        activityNameBiMap = new HashBiMap<>();
        caseAttributeValueFreqMap = new UnifiedMap<>();
        eventAttributeValueCasesFreqMap = new UnifiedMap<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();
        variantIdFreqMap = new UnifiedMap<>();
    }

    public ImmutableLog(List<ATrace> traceList) {
        this.traceList = traceList;
        this.immutableTraces = traceList;
        eventAttributeOccurMap = new UnifiedMap<>();
        activityNameBiMap = new HashBiMap<>();
        caseAttributeValueFreqMap = new UnifiedMap<>();
        eventAttributeValueCasesFreqMap = new UnifiedMap<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();
        variantIdFreqMap = new UnifiedMap<>();
    }



//    public UnifiedMap<String, UnifiedMap<String, UnifiedSet<ImmutableActivity>>> getEventAttributeOccurMap() {
//        return eventAttributeOccurMap;
//    }
//
//    public HashBiMap<String, Integer> getActivityNameBiMap() {
//        return activityNameBiMap;
//    }
//
//
//
//
//
//    @Override
//    public void add(ATrace trace) {
//        traces.add(trace);
//    }
//
//    @Override
//    public DefaultChartDataCollection getDefaultChartDataCollection() {
//        return defaultChartDataCollection;
//    }
//
//    @Override
//    public UnifiedMap<String, Integer> getActivityMaxOccurMap() {
//        UnifiedMap<String, UnifiedSet<ImmutableActivity>> actsMap = eventAttributeOccurMap.get("concept:name");
//        int size = actsMap.size();
//        UnifiedMap<String, Integer> output = new UnifiedMap<>(size);
//        for (String actName : actsMap.keySet()) {
//            output.put(actName, actsMap.get(actName).size());
//        }
//        return output;
//    }
//
//    @Override
//    public ActivityNameMapper getActivityNameMapper() {
//        return null;
//    }
//
//    @Override
//    public ATrace get(String caseId) {
//        for (int i = 0; i < traces.size(); i++) {
//           if (traces.get(i).getCaseId().equals(caseId)) return traces.get(i);
//        }
//        return null;
//    }
//
//    @Override
//    public UnifiedMap<String, ATrace> getTraceUnifiedMap() {
//        UnifiedMap<String, ATrace> map = new UnifiedMap<>(traces.size());
//        for (int i = 0; i < traces.size(); i++) {
//            map.put(traces.get(i).getCaseId().intern(), traces.get(i));
//        }
//        return map;
//    }
//
//    @Override
//    public int getUniqueActivitySize() {
//        return eventAttributeOccurMap.get("concept:name").size();
//    }
//
//    @Override
//    public UnifiedMap<Integer, Integer> getCaseVariantIdFrequencyMap() {
//        return variantIdFreqMap;
//    }
//
//    @Override
//    public UnifiedMap<String, UnifiedMap<String, Integer>> getCaseAttributeValueFreqMap() {
//        return caseAttributeValueFreqMap;
//    }
//
//    @Override
//    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap() {
//        return eventAttributeValueFreqMap;
//    }
//
//    @Override
//    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueCasesFreqMap() {
//        return eventAttributeValueCasesFreqMap;
//    }
//
//    @Override
//    public List<String> getCaseAttributeNameList() {
//        List<String> nameList = new ArrayList<>(caseAttributeValueFreqMap.keySet());
//        Collections.sort(nameList);
//        return nameList;
//    }
//
//    @Override
//    public long getEventSize() {
//        return eventSize;
//    }
//
//    @Override
//    public void setEventSize(int eventSize) {
//        this.eventSize = eventSize;
//    }
//
//    @Override
//    public int getCaseVariantSize() {
//        return variantIdFreqMap.size();
//    }
//
//    @Override
//    public void setCaseVariantSize(int caseVariantSize) {
//
//    }
//
//    @Override
//    public List<String> getActivityNameList(int caseVariantId) {
//        IntArrayList actNameIndexes = actNameIdxCId.inverse().get(caseVariantId);
//        List<String> actNames = new ArrayList<>(actNameIndexes.size());
//        for (int i = 0; i < actNameIndexes.size(); i++) {
//            int index = actNameIndexes.get(i);
//            String actName = activityNameBiMap.inverse().get(index);
//            actNames.add(actName);
//        }
//        return actNames;
//    }
//
//    @Override
//    public void setVariantIdFreqMap(UnifiedMap<Integer, Integer> variantIdFreqMap) {
//        this.variantIdFreqMap = variantIdFreqMap;
//    }
//
//    @Override
//    public double getMinDuration() {
//        return minDuration;
//    }
//
//    @Override
//    public void setMinDuration(long minDuration) {
//        this.minDuration = minDuration;
//    }
//
//    @Override
//    public double getMaxDuration() {
//        return maxDuration;
//    }
//
//    @Override
//    public void setMaxDuration(long maxDuration) {
//        this.maxDuration = maxDuration;
//    }
//
//    @Override
//    public List<ATrace> getTraceList() {
//        return traces;
//    }
//
//    @Override
//    public void setTraceList(List<ATrace> traceList) {
//        this.traces = traceList;
//    }
//
//    @Override
//    public UnifiedSet<String> getEventNameSet() {
//
//
//        return null;
//    }
//
//    @Override
//    public UnifiedSet<String> getEventAttributeNameSet() {
//        UnifiedSet<String> nameSet = new UnifiedSet<>(eventAttributeValueFreqMap.keySet());
//        return nameSet;
//    }
//
//    @Override
//    public int size() {
//        return traces.size();
//    }
//
//    @Override
//    public ATrace get(int index) {
//        return traces.get(index);
//    }
//
//    @Override
//    public long getStartTime() {
//        return startTime;
//    }
//
//    @Override
//    public void setStartTime(long startTime) {
//        this.startTime = startTime;
//    }
//
//    @Override
//    public long getEndTime() {
//        return endTime;
//    }
//
//    @Override
//    public void setEndTime(long endTime) {
//        this.endTime = endTime;
//    }
//
//    @Override
//    public String getTimeZone() {
//        return null;
//    }
//
//    @Override
//    public String getMinDurationString() {
//        return Util.durationStringOf(minDuration);
//    }
//
//    @Override
//    public String getMaxDurationString() {
//        return Util.durationStringOf(maxDuration);
//    }
//
//
//    @Override
//    public double getAverageDuration() {
//        double durSum = 0;
//        for(int i=0; i < traces.size(); i++) {
//            durSum += traces.get(i).getDuration();
//        }
//        double avgDur = durSum / traces.size();
//        return avgDur;
//    }
//
//    @Override
//    public double getMedianDuration() {
//        List<Double> durList = new ArrayList<>();
//        for (int i=0; i<traces.size(); i++) {
//            durList.add(traces.get(i).getDuration());
//        }
//        Collections.sort(durList);
//        int medianIndex = traces.size() / 2;
//        return durList.get(medianIndex);
//    }
//
//    @Override
//    public String getAverageDurationString() {
//        return Util.durationShortStringOf(getAverageDuration());
//    }
//
//    @Override
//    public String getMedianDurationString() {
//        return Util.durationShortStringOf(getMedianDuration());
//    }
//
//    @Override
//    public String getStartTimeString() {
//        return Util.timestampStringOf(Util.millisecondToZonedDateTime(this.startTime));
//    }
//
//    @Override
//    public String getEndTimeString() {
//        return Util.timestampStringOf(Util.millisecondToZonedDateTime(this.endTime));
//    }
//
//
//
//    @Override
//    public XLog toXLog() {
//        return APMLogToXLog.getXLog(this);
//    }
//
//    @Override
//    public AAttributeGraph getAAttributeGraph() {
//        return null;
//    }

    @Override
    public APMLog clone() {
//        UnifiedMap<String, ATrace> traceUnifiedMapForClone = new UnifiedMap<>();

        List<ATrace> traceListForClone = new ArrayList<>();

        for (int i = 0; i < this.traceList.size(); i++) {
            ATrace aTrace = this.traceList.get(i).clone();
//            traceUnifiedMapForClone.put(aTrace.getCaseId(), aTrace);
            traceListForClone.add(aTrace);
        }

        UnifiedMap<Integer, Integer> variIdFreqMapForClone = new UnifiedMap<>();

        for (int key : this.variantIdFreqMap.keySet()) {
            variIdFreqMapForClone.put(key, this.variantIdFreqMap.get(key));
        }

//        HashBiMap<Integer, String> actIdNameMapForClone = new HashBiMap<>();
//
//        for (int key : this.actIdNameMap.keySet()) {
//            actIdNameMapForClone.put(key, this.actIdNameMap.get(key));
//        }

        UnifiedMap<String, UnifiedMap<String, Integer>> eventAttrValCasesFreqMapForClone = new UnifiedMap<>();

        for (String key : this.eventAttributeValueCasesFreqMap.keySet()) {
            UnifiedMap<String, Integer> valFreqMapForClone = new UnifiedMap<>();
            UnifiedMap<String, Integer> valFreqMap = this.eventAttributeValueCasesFreqMap.get(key);

            for (String val : valFreqMap.keySet()) {
                valFreqMapForClone.put(val, valFreqMap.get(val));
            }

            eventAttrValCasesFreqMapForClone.put(key, valFreqMapForClone);
        }

        UnifiedMap<String, UnifiedMap<String, Integer>> eventAttrValFreqMapForClone = new UnifiedMap<>();

        for (String key : this.eventAttributeValueFreqMap.keySet()) {
            UnifiedMap<String, Integer> valFreqMapForClone = new UnifiedMap<>();
            UnifiedMap<String, Integer> valFreqMap = this.eventAttributeValueFreqMap.get(key);

            for (String val : valFreqMap.keySet()) {
                valFreqMapForClone.put(val, valFreqMap.get(val));
            }

            eventAttrValFreqMapForClone.put(key, valFreqMapForClone);
        }

        UnifiedMap<String, UnifiedMap<String, Integer>> caseAttrValFreqMapForClone = new UnifiedMap<>();

        for (String key : this.caseAttributeValueFreqMap.keySet()) {
            UnifiedMap<String, Integer> valFreqMapForClone = new UnifiedMap<>();
            UnifiedMap<String, Integer> valFreqMap = this.caseAttributeValueFreqMap.get(key);

            for (String val : valFreqMap.keySet()) {
                valFreqMapForClone.put(val, valFreqMap.get(val));
            }

            caseAttrValFreqMapForClone.put(key, valFreqMapForClone);
        }

        UnifiedMap<String, Integer> activityMaxOccurMapForClone = new UnifiedMap<>();

        for (String key : this.activityMaxOccurMap.keySet()) {
            activityMaxOccurMapForClone.put(key, this.activityMaxOccurMap.get(key));
        }


        ImmutableLog logClone = new ImmutableLog(traceListForClone,
                variIdFreqMapForClone,
//                actIdNameMapForClone,
                eventAttrValCasesFreqMapForClone,
                eventAttrValFreqMapForClone,
                caseAttrValFreqMapForClone,
//                traceUnifiedMapForClone,
                this.minDuration,
                this.maxDuration,
                this.timeZone,
                this.startTime,
                this.endTime,
//                this.caseVariantSize,
                this.eventSize,
                this.activityNameMapper,
                activityMaxOccurMapForClone);

        return logClone;
    }

    public ImmutableLog(List<ATrace> traceList,
                      UnifiedMap<Integer, Integer> variantIdFreqMap,
//                      HashBiMap<Integer, String> actIdNameMap,
                      UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueCasesFreqMap,
                      UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap,
                      UnifiedMap<String, UnifiedMap<String, Integer>> caseAttributeValueFreqMap,
//                      UnifiedMap<String, ATrace> traceUnifiedMap,
                      double minDuration,
                      double maxDuration,
                      String timeZone,
                      long startTime,
                      long endTime,
//                      long caseVariantSize,
                      long eventSize,
                      ActivityNameMapper activityNameMapper,
                      UnifiedMap<String, Integer> activityMaxOccurMap) {
        this.immutableTraces = traceList;
        this.traceList = traceList;
        this.variantIdFreqMap = variantIdFreqMap;
        this.actIdNameMap = actIdNameMap;
        this.eventAttributeValueCasesFreqMap = eventAttributeValueCasesFreqMap;
        this.eventAttributeValueFreqMap = eventAttributeValueFreqMap;
        this.caseAttributeValueFreqMap = caseAttributeValueFreqMap;
//        this.traceUnifiedMap = traceUnifiedMap;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.timeZone = timeZone;
        this.startTime = startTime;
        this.endTime = endTime;
//        this.caseVariantSize = caseVariantSize;
        this.eventSize = eventSize;
        this.activityNameMapper = activityNameMapper;
        this.activityMaxOccurMap = activityMaxOccurMap;
        if (traceList.size() > 0) {
            if (traceList.get(0).getDuration() > 0) {
                defaultChartDataCollection = new DefaultChartDataCollection(this);
            }
        }
    }
}
