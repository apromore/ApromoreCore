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
/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.apmlog.filter;


import org.apromore.apmlog.*;
import org.apromore.apmlog.immutable.ImmutableLog;
import org.apromore.apmlog.stats.AAttributeGraph;
import org.apromore.apmlog.stats.CaseAttributeValue;
import org.apromore.apmlog.stats.EventAttributeValue;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Chii Chang
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (06/03/2020)
 * Modified: Chii Chang (11/04/2020)
 * Modified: Chii Chang (12/05/2020)
 * Modified: Chii Chang (10/11/2020)
 * Modified: Chii Chang (11/11/2020)
 * Modified: Chii Chang (06/01/2021)
 * Modified: Chii Chang (26/01/2021)
 */
public class PLog implements APMLog {

    private APMLog apmLog;

    private static final Logger LOGGER = LoggerFactory.getLogger(PLog.class);

    private UnifiedMap<String, PTrace> pTraceUnifiedMap;
    private List<PTrace> pTraceList;
    private double medianDuration = 0;
    private double averageDuration = 0;
    private long caseVariantSize = 0;
    private BitSet validTraceIndexBS;
    public BitSet originalValidTraceIndexBS;
    private double originalMinDuration = 0;
    private double originalMedianDuration = 0;
    private double originalAverageDuration = 0;
    private double originalMaxDuration = 0;
    private long originalStartTime = 0;
    private long originalEndTime = 0;
    private long originalCaseVariantSize = 0;
    private long originalEventSize = 0;

    private List<PTrace> originalPTraceList;
    private UnifiedMap<Integer, Integer> originalVariantIdFreqMap;

    private UnifiedMap<String, Integer> originalActivityMaxOccurMap;
    private UnifiedMap<String, Integer> previousActivityMaxOccurMap;

    public UnifiedMap<Integer, Integer> previousVariantIdFreqMap;
    public List<PTrace> previousPTraceList;

    public BitSet previousValidTraceIndexBS;

    public long previousCaseVariantSize;
    public long previousEventSize;
    public double previousMinDuration;
    public double previousMedianDuration = 0;
    public double previousAverageDuration = 0;
    public double previousMaxDuration;
    public long previousStartTime;
    public long previousEndTime;

    public long maxCaseIdDigit = 0;
    public long minCaseIdDigit = 0;

    private long variantSize = 0;

    private HashBiMap<PTrace, Integer> caseIndexMap = new HashBiMap<>();
    private AAttributeGraph originalAttributeGraph;
    private AAttributeGraph previousAttributeGraph;

    private UnifiedMap<String, UnifiedSet<EventAttributeValue>> eventAttributeValues;
    private UnifiedMap<String, UnifiedSet<CaseAttributeValue>> caseAttributeValues;

    //*************
    private List<ATrace> immutableTraces; // this is the immutable traces
    private UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> eventAttributeOccurMap;
    private HashBiMap<String, Integer> activityNameBiMap = new HashBiMap<>();
    private UnifiedMap<Integer, Integer> variantIdFreqMap;
    private HashBiMap<Integer, String> actIdNameMap = new HashBiMap<>();
    private UnifiedMap<String, Integer> activityMaxOccurMap = new UnifiedMap<>();
    private String timeZone = "";
    private long startTime = -1;
    private long endTime = -1;
    private long eventSize = 0;
    private HashBiMap<IntArrayList, Integer> actNameIdxCId;
    private ActivityNameMapper activityNameMapper;
    private DefaultChartDataCollection defaultChartDataCollection;
    private AAttributeGraph attributeGraph;
    private DoubleArrayList caseDurationList;
    //************

    public APMLog getApmLog() {
        return apmLog;
    }


    public DefaultChartDataCollection getDefaultChartDataCollection() {
        return defaultChartDataCollection;
    }

    public PLog(APMLog apmLog) {
        this.apmLog = apmLog;

        initDefault();

        updatePrevious();
    }

    private void initDefault() {
        eventAttributeOccurMap = new UnifiedMap<>(apmLog.getEventAttributeOccurMap());
        variantSize = apmLog.getCaseVariantSize();
        activityNameBiMap = apmLog.getActivityNameBiMap();

        originalAttributeGraph = apmLog.getAAttributeGraph();

        this.defaultChartDataCollection = apmLog.getDefaultChartDataCollection();

        this.activityNameMapper = apmLog.getActivityNameMapper();

        this.activityMaxOccurMap = new UnifiedMap<>(apmLog.getActivityMaxOccurMap());
        this.originalActivityMaxOccurMap =  new UnifiedMap<>(apmLog.getActivityMaxOccurMap());

        pTraceUnifiedMap = new UnifiedMap<>();

        this.timeZone = apmLog.getTimeZone();

        this.eventAttributeValues = new UnifiedMap<>(apmLog.getEventAttributeValues());
        this.caseAttributeValues = new UnifiedMap<>(apmLog.getCaseAttributeValues());

        this.attributeGraph = apmLog.getAAttributeGraph();


        this.validTraceIndexBS = new BitSet(apmLog.getImmutableTraces().size());
        this.validTraceIndexBS.set(0, apmLog.getImmutableTraces().size());
        this.originalValidTraceIndexBS = new BitSet(apmLog.getImmutableTraces().size());
        this.originalValidTraceIndexBS.set(0, apmLog.getImmutableTraces().size());


        for (ATrace aTrace : apmLog.getTraceList()) {
            int index = aTrace.getImmutableIndex();
            this.validTraceIndexBS.set(index);
            this.originalValidTraceIndexBS.set(index);
        }


        LOGGER.info("init values for validTraceIndexBS, originalValidTraceIndexBS, previousValidTraceIndexBS");
        this.validTraceIndexBS.set(0, apmLog.getImmutableTraces().size(), true);
        this.originalValidTraceIndexBS.set(0, apmLog.getImmutableTraces().size(), true);


        List<ATrace> apmTraceList = apmLog.getTraceList();

        this.immutableTraces = apmLog.getImmutableTraces();
        this.pTraceList = new ArrayList<>(apmTraceList.size());
        this.originalPTraceList = new ArrayList<>(apmTraceList.size());


        caseDurationList = new DoubleArrayList(apmLog.getTraceList().size());

        caseIndexMap.clear();


        int mutableIndex = 0;
        for (ATrace aTrace : apmTraceList) {
            PTrace pTrace = new PTrace(aTrace, apmLog);
            this.pTraceList.add(pTrace);
            caseDurationList.add(pTrace.getDuration());

            caseIndexMap.put(pTrace, mutableIndex);


            this.pTraceUnifiedMap.put(aTrace.getCaseId(), pTrace);
            if(pTrace.caseIdDigit > 0) {
                if(pTrace.caseIdDigit > maxCaseIdDigit) maxCaseIdDigit = pTrace.caseIdDigit;
                if(minCaseIdDigit == 0 || pTrace.caseIdDigit < minCaseIdDigit) minCaseIdDigit = pTrace.caseIdDigit;
            }

            this.originalPTraceList.add(pTrace);
            mutableIndex += 1;
        }


        LOGGER.info("do the rest of copies");
        this.caseVariantSize = apmLog.getCaseVariantSize();
        this.originalCaseVariantSize = apmLog.getCaseVariantSize();

        this.eventSize = apmLog.getEventSize();
        this.originalEventSize = apmLog.getEventSize();

        this.originalMinDuration = apmLog.getMinDuration();

        this.medianDuration = apmLog.getMedianDuration();
        this.originalMedianDuration = apmLog.getMedianDuration();

        this.averageDuration = apmLog.getAverageDuration();
        this.originalAverageDuration = apmLog.getAverageDuration();

        this.originalMaxDuration = apmLog.getMaxDuration();

        this.startTime = apmLog.getStartTime();
        this.originalStartTime = apmLog.getStartTime();

        this.endTime = apmLog.getEndTime();
        this.originalEndTime = apmLog.getEndTime();


        LOGGER.info("copy case variant id freq map");
        UnifiedMap<Integer, Integer> apmVariantIdFreqMap = apmLog.getCaseVariantIdFrequencyMap();
        LOGGER.info("done");

        this.variantIdFreqMap = new UnifiedMap<>(apmVariantIdFreqMap);
        this.originalVariantIdFreqMap = new UnifiedMap<>(apmVariantIdFreqMap);

        caseVariantSize = variantIdFreqMap.size();

        this.originalActivityMaxOccurMap = new UnifiedMap<>(apmLog.getActivityMaxOccurMap());

        LOGGER.info("done");
    }

    public void updateStats() {
        updateStats(this.getPTraceList());
    }

    public void updateStats(List<PTrace> filteredPTraceList) {
        startTime = 0;
        endTime = 0;

        actNameIdxCId = new HashBiMap<>();

        variantIdFreqMap = new UnifiedMap<>();

        List<IntArrayList> traceActNameIndexes = new ArrayList<>();

        eventSize = 0;

        variantIdFreqMap = new UnifiedMap<>();

        pTraceList = filteredPTraceList;

        pTraceUnifiedMap.clear();

        this.caseDurationList = new DoubleArrayList(pTraceList.size());

        UnifiedMap<String, UnifiedMap<String, IntArrayList>> caseAttrValOccurMap = new UnifiedMap<>();

        int index = 0;

        for (PTrace trace : pTraceList) {

            UnifiedMap<String, String> tAttrMap = trace.getAttributeMap();

            for (String attrKey : tAttrMap.keySet()) {
                String val = trace.getAttributeMap().get(attrKey);

                if (caseAttrValOccurMap.keySet().contains(attrKey)) {
                    UnifiedMap<String, IntArrayList> valOccurMap = caseAttrValOccurMap.get(attrKey);
                    if (valOccurMap.containsKey(val)) {
                        valOccurMap.get(val).add(index);
                    } else {
                        IntArrayList indexes = new IntArrayList();
                        indexes.add(index);
                        valOccurMap.put(val, indexes);
                    }
                } else {
                    IntArrayList indexes = new IntArrayList();
                    indexes.add(index);
                    UnifiedMap<String, IntArrayList> valOccurMap = new UnifiedMap<>();
                    valOccurMap.put(val, indexes);
                    caseAttrValOccurMap.put(attrKey, valOccurMap);
                }
            }

            caseDurationList.add(trace.getDuration());

            trace.update(index);

            pTraceUnifiedMap.put(trace.getCaseId(), trace);

            int vari = trace.getCaseVariantId();

            if (variantIdFreqMap.containsKey(vari)) {
                int freq = variantIdFreqMap.get(vari) + 1;
                variantIdFreqMap.put(vari, freq);
            } else variantIdFreqMap.put(vari, 1);

            IntArrayList actNameIndexes = getActivityNameIndexes(trace);
            traceActNameIndexes.add(actNameIndexes);

            if (startTime < 1 || trace.getStartTimeMilli() < startTime) startTime = trace.getStartTimeMilli();
            if (trace.getEndTimeMilli() > endTime) endTime = trace.getEndTimeMilli();

            BitSet vEvents = trace.getValidEventIndexBitSet();
            eventSize += vEvents.cardinality();

            index += 1;
        }

        caseAttributeValues = new UnifiedMap<>();

        for (String attrKey : caseAttrValOccurMap.keySet()) {
            UnifiedMap<String, IntArrayList> valOccurMap = caseAttrValOccurMap.get(attrKey);
            UnifiedSet<CaseAttributeValue> cavSet = new UnifiedSet<>();

            int[] arr = valOccurMap.entrySet().stream().mapToInt(x -> x.getValue().size()).toArray();
            IntArrayList ial = new IntArrayList(arr);

            int maxOccurSize = ial.max();

            for (String val : valOccurMap.keySet()) {
                CaseAttributeValue cav = new CaseAttributeValue(val, valOccurMap.get(val), pTraceList.size());
                cav.setRatio(100 * ( (double) cav.getCases() / maxOccurSize));
                cavSet.add(cav);
            }
            caseAttributeValues.put(attrKey, cavSet);
        }

        variantSize = variantIdFreqMap.size();

        updateEventAttributeOccurMap();

        eventAttributeValues = new UnifiedMap<>();

        for (String key : eventAttributeOccurMap.keySet()) {  // !!!!! performance issue
            UnifiedMap<String, UnifiedSet<AActivity>> valOccurMap = eventAttributeOccurMap.get(key);

            UnifiedMap<String, Integer> valCaseFreqMap = new UnifiedMap<>(valOccurMap.size());

            UnifiedSet<EventAttributeValue> attrVals = new UnifiedSet<>();
            int maxCasesOfCSEventAttrVal = 0;


            for (String val : valOccurMap.keySet()) {

                UnifiedSet<AActivity> occurSet = valOccurMap.get(val);

                int[] array = occurSet.stream().mapToInt(s -> s.getMutableTraceIndex()).toArray();
                List<Integer> traceIndexList = IntStream.of(array).boxed().collect(Collectors.toList());
                UnifiedSet<Integer> uniqueTraceIndexes = new UnifiedSet<>(traceIndexList);
                List<Integer> uniqueTraceIndexList = new ArrayList<>(uniqueTraceIndexes);

                int[] array2 = uniqueTraceIndexList.stream().mapToInt(s -> s).toArray();

                IntArrayList traceIndexes = new IntArrayList(array2);

                valCaseFreqMap.put(val, traceIndexes.size());

                try {
                    attrVals.add(new EventAttributeValue(val, traceIndexes, pTraceList.size(), occurSet));
                } catch (Exception e) {
                    System.out.println("");
                }
                if (traceIndexes.size() > maxCasesOfCSEventAttrVal) maxCasesOfCSEventAttrVal = traceIndexes.size();
            }

            for (EventAttributeValue v : attrVals) {
                v.setRatio(100 * ( (double) v.getCases() / maxCasesOfCSEventAttrVal));
            }
            eventAttributeValues.put(key, attrVals);
        }

        updateActivityOccurMaxMap();

        attributeGraph = new AAttributeGraph(this);
    }

    private IntArrayList getActivityNameIndexes(ATrace aTrace) {
        IntArrayList nameIndexes = new IntArrayList(aTrace.getActivityList().size());
        List<AActivity> activityList = aTrace.getActivityList();
        for (int i = 0; i < activityList.size(); i++) {
            int actNameIndex = activityNameBiMap.get(activityList.get(i).getName());
            nameIndexes.add(actNameIndex);
        }
        return nameIndexes;
    }


    private void updateEventAttributeOccurMap() {
        eventAttributeOccurMap = new UnifiedMap<>();

        for (PTrace pTrace: pTraceList) {
            for (AActivity activity: pTrace.getActivityList()) {
                LogFactory.fillAttributeOccurMap(activity, eventAttributeOccurMap);
            }
        }
    }


    public void reset() {

        resetPrevious();

        eventAttributeOccurMap = new UnifiedMap<>(apmLog.getEventAttributeOccurMap());
        variantSize = apmLog.getCaseVariantSize();
        activityNameBiMap = apmLog.getActivityNameBiMap();

        this.defaultChartDataCollection = apmLog.getDefaultChartDataCollection();

        this.activityNameMapper = apmLog.getActivityNameMapper();

        this.activityMaxOccurMap = new UnifiedMap<>(apmLog.getActivityMaxOccurMap());

        pTraceUnifiedMap = new UnifiedMap<>();

        this.timeZone = apmLog.getTimeZone();

        this.eventAttributeValues = new UnifiedMap<>(apmLog.getEventAttributeValues());
        this.caseAttributeValues = new UnifiedMap<>(apmLog.getCaseAttributeValues());

        this.attributeGraph = apmLog.getAAttributeGraph();

        this.validTraceIndexBS = new BitSet(apmLog.getImmutableTraces().size());
        this.validTraceIndexBS.set(0, apmLog.getImmutableTraces().size());

        for (ATrace aTrace : apmLog.getTraceList()) {
            int index = aTrace.getImmutableIndex();
            this.validTraceIndexBS.set(index);
        }


        LOGGER.info("init values for validTraceIndexBS, originalValidTraceIndexBS, previousValidTraceIndexBS");
        this.validTraceIndexBS.set(0, apmLog.getImmutableTraces().size(), true);


        List<ATrace> apmTraceList = apmLog.getTraceList();

        this.immutableTraces = apmLog.getImmutableTraces();
        this.pTraceList = new ArrayList<>(apmTraceList.size());

        caseDurationList = new DoubleArrayList(apmLog.getTraceList().size());

        caseIndexMap.clear();

        this.pTraceList = new ArrayList<>(originalPTraceList);

        for (PTrace pTrace : pTraceList) {
            caseDurationList.add(pTrace.getDuration());
            caseIndexMap.put(pTrace, pTrace.getImmutableIndex());

            this.pTraceUnifiedMap.put(pTrace.getCaseId(), pTrace);
            if(pTrace.caseIdDigit > 0) {
                if(pTrace.caseIdDigit > maxCaseIdDigit) maxCaseIdDigit = pTrace.caseIdDigit;
                if(minCaseIdDigit == 0 || pTrace.caseIdDigit < minCaseIdDigit) minCaseIdDigit = pTrace.caseIdDigit;
            }
        }

        LOGGER.info("do the rest of copies");
        this.caseVariantSize = apmLog.getCaseVariantSize();
        this.eventSize = apmLog.getEventSize();
        this.medianDuration = apmLog.getMedianDuration();
        this.averageDuration = apmLog.getAverageDuration();
        this.startTime = apmLog.getStartTime();
        this.endTime = apmLog.getEndTime();

        LOGGER.info("copy case variant id freq map");
        UnifiedMap<Integer, Integer> apmVariantIdFreqMap = apmLog.getCaseVariantIdFrequencyMap();

        LOGGER.info("done");

        this.variantIdFreqMap = new UnifiedMap<>(apmVariantIdFreqMap);
        caseVariantSize = variantIdFreqMap.size();

        LOGGER.info("reset complete");

    }

    public void resetPrevious() {

        if(previousPTraceList != null) {
            attributeGraph = previousAttributeGraph;
            this.pTraceList = previousPTraceList;

            double[] durArr = pTraceList.stream().mapToDouble(s -> s.getDuration()).toArray();

            caseDurationList = new DoubleArrayList(durArr);

            caseVariantSize = previousCaseVariantSize;
            eventSize = previousEventSize;
            startTime = previousStartTime;
            endTime = previousEndTime;
            variantIdFreqMap = previousVariantIdFreqMap;
            activityMaxOccurMap = previousActivityMaxOccurMap;

            for (int i = 0; i < validTraceIndexBS.length(); i++) {
                validTraceIndexBS.set(i, previousValidTraceIndexBS.get(i));
            }
        } else {
            reset();
        }
    }

    public void updatePrevious() {

        previousAttributeGraph = this.attributeGraph;
        previousPTraceList = this.pTraceList;

        for (PTrace previousPTrace: previousPTraceList) {
            previousPTrace.updatePrevious();
        }

        previousCaseVariantSize = caseVariantSize;
        previousEventSize = eventSize;
        previousStartTime = startTime;
        previousEndTime = endTime;
        previousVariantIdFreqMap = variantIdFreqMap;
        previousActivityMaxOccurMap = activityMaxOccurMap;

        previousValidTraceIndexBS = new BitSet(apmLog.size());

        for (int i = 0; i < previousValidTraceIndexBS.length(); i++) {
            previousValidTraceIndexBS.set(i, validTraceIndexBS.get(i));
        }
    }

    public void updateActivityOccurMaxMap() {

        UnifiedMap<String, Integer> actMaxOccur = new UnifiedMap<>();

        for (PTrace pTrace: pTraceList) {
            List<AActivity> aActivityList = pTrace.getActivityList();

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

    /* ----------------- Export method ------------------ */


    public int getCaseVariantSumAfterFilter() {
        UnifiedMap<List<String>, Integer> variFreqMap = new UnifiedMap<>();
        HashBiMap<List<String>, Integer> variantIdMap = new HashBiMap<>();
        UnifiedMap<Integer, Integer> variIdFreqMap = new UnifiedMap<>();


        for(PTrace pTrace: pTraceList) {
            List<String> actNameList = pTrace.getActivityNameList();
            if (variFreqMap.containsKey(actNameList)) {
                int freq = variFreqMap.get(actNameList) + 1;
                variFreqMap.put(actNameList, freq);
            } else variFreqMap.put(actNameList, 1);
        }

        List<UnifiedMap.Entry<List<String>, Integer> > list = new ArrayList<UnifiedMap.Entry<List<String>, Integer>>(variFreqMap.entrySet());
        Collections.sort(list, new Comparator<UnifiedMap.Entry<List<String>, Integer>>() {
            @Override
            public int compare(Map.Entry<List<String>, Integer> o1, Map.Entry<List<String>, Integer> o2) {
                if (o1.getValue() > o2.getValue()) return -1;
                else if (o1.getValue() < o2.getValue()) return 1;
                else return 0;
            }
        });

        for (int i=0; i < list.size(); i++) {
            List<String> actNameList = list.get(i).getKey();
            int freq = list.get(i).getValue();
            int id = i + 1;
            variantIdMap.put(actNameList, id);
            variIdFreqMap.put(id, freq);
        }

        for (PTrace pTrace: pTraceList) {
            List<String> actNameList = pTrace.getActivityNameList();
            pTrace.setCaseVariantId(variantIdMap.get(actNameList));
        }

        return variFreqMap.size();
    }

    public void setAttributeGraph(AAttributeGraph attributeGraph) {
        this.attributeGraph = attributeGraph;
    }

    public AAttributeGraph getAttributeGraph() {
        return attributeGraph;
    }



    /* ----------------- SET methods ------------------ */


    public void setMedianDuration(double medianDuration) {
        this.medianDuration = medianDuration;
    }

    public void setAverageDuration(double averageDuration) {
        this.averageDuration = averageDuration;
    }

    public void setCaseVariantSize(long caseVariantSize) {
        this.caseVariantSize = caseVariantSize;
    }

    public void setEventSize(long eventSize) {
        this.eventSize = eventSize;
    }

    public void setPTraceList(List<PTrace> pTraceList) {
        this.pTraceList = pTraceList;
    }

    public void setVariantIdFreqMap(UnifiedMap<Integer, Integer> variantIdFreqMap) {
        this.variantIdFreqMap = variantIdFreqMap;
    }

    public void setValidTraceIndexBS(BitSet validTraceIndexBS) {
        this.validTraceIndexBS = validTraceIndexBS;
    }

    /* ----------------- GET methods ------------------ */

    public void setActivityMaxOccurMap(UnifiedMap<String, Integer> activityMaxOccurMap) {
        this.activityMaxOccurMap = activityMaxOccurMap;
    }

    public UnifiedMap<String, Integer> getActivityMaxOccurMap() {
        return activityMaxOccurMap;
    }

    @Override
    public ActivityNameMapper getActivityNameMapper() {
        return activityNameMapper;
    }

    public PTrace get(int index) {
        return this.pTraceList.get(index);
    }

    @Override
    public ATrace getImmutable(int index) {
        return immutableTraces.get(index);
    }

    @Override
    public int immutableSize() {
        return immutableTraces.size();
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
        return this.timeZone;
    }

    @Override
    public String getMinDurationString() {
        return Util.durationShortStringOf(getMinDuration());
    }

    @Override
    public String getMaxDurationString() {
        return Util.durationShortStringOf(getMaxDuration());
    }

    public PTrace get(String caseId) {
        return pTraceUnifiedMap.get(caseId);
    }

    @Override
    public UnifiedMap<String, ATrace> getTraceUnifiedMap() {
        UnifiedMap<String, ATrace> map = new UnifiedMap<>(pTraceList.size());
        for (PTrace pTrace : pTraceList) {
            map.put(pTrace.getCaseId().intern(), pTrace);
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

    public List<String> getActivityNameList(int caseVariantId) {
        for(PTrace pTrace : pTraceList) {
            if(pTrace.getCaseVariantId() == caseVariantId){
                return pTrace.getActivityNameList();
            }
        }
        return null;
    }

    public UnifiedSet<String> getEventAttributeNameSet() {
        UnifiedSet<String> validNames = new UnifiedSet<>();

        for (String key : eventAttributeOccurMap.keySet()) {
            int qty = eventAttributeOccurMap.get(key).size();
            if(qty < 100000 && !key.equals("concept:name") && !key.equals("org:resource")) {
                validNames.put(key);
            }
        }
        return validNames;
    }

    public List<String> getCaseAttributeNameList() {
        Set<String> names = caseAttributeValues.keySet();
        if (names.contains("case:variant")) names.remove("case:variant");
        return new ArrayList<>(names);
    }

    public int size() {
        return this.pTraceList.size();
    }


    public BitSet getValidTraceIndexBS() {
        return validTraceIndexBS;
    }

    public List<PTrace> getPTraceList() {
        return pTraceList;
    }

    public PTrace getPTrace(String traceId) {
        return pTraceUnifiedMap.get(traceId);
    }

    @Override
    public long getCaseVariantSize() {
        return variantSize;
    }

    @Override
    public long getEventSize() {
        return eventSize;
    }

    @Override
    public void setEventSize(int eventSize) {
        this.eventSize = eventSize;
    }


    public double getMedianDuration() {
        return medianDuration;
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
    public APMLog clone() {
        return null;
    }

    @Override
    public XLog toXLog() {
        return null;
    }

    @Override
    public AAttributeGraph getAAttributeGraph() {
        return attributeGraph;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    @Override
    public UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> getEventAttributeOccurMap() {
        return this.eventAttributeOccurMap;
    }

    @Override
    public void add(ATrace trace) {

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
    public UnifiedMap<Integer, Integer> getVariantIdFreqMap() {
        return variantIdFreqMap;
    }

    @Override
    public DoubleArrayList getCaseDurations() {
        return caseDurationList;
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
    public List<ATrace> getTraceList() {
        return pTraceList.stream()
                .collect(Collectors.toList());
    }

    @Override
    public void setTraceList(List<ATrace> traceList) {
        pTraceList.clear();
        for (ATrace aTrace : traceList) {
            pTraceList.add( new PTrace(aTrace, apmLog) );
        }
        updateStats();
    }

    public List<PTrace> getOriginalPTraceList() {
        return originalPTraceList;
    }

    public UnifiedMap<Integer, Integer> getOriginalVariantIdFreqMap() {
        return originalVariantIdFreqMap;
    }

    public long getOriginalCaseVariantSize() {
        return originalCaseVariantSize;
    }

    public long getOriginalEventSize() {
        return originalEventSize;
    }

    public BitSet getOriginalValidTraceIndexBS() {
        return originalValidTraceIndexBS;
    }

    public BitSet getPreviousValidTraceIndexBS() {
        return previousValidTraceIndexBS;
    }

    public UnifiedMap<String, PTrace> getPTraceUnifiedMap() {
        return pTraceUnifiedMap;
    }

    @Override
    public UnifiedMap<String, UnifiedSet<EventAttributeValue>> getEventAttributeValues() {
        return eventAttributeValues;
    }

    @Override
    public UnifiedMap<String, UnifiedSet<CaseAttributeValue>> getCaseAttributeValues() {
        return caseAttributeValues;
    }

    /**
     * A custom PTrace list that maintains the original PTrace list
     * while each PTrace contains up-to-date event BitSet.
     * The event BitSet of the invalid PTrace of this list
     * contains 'false' for all the elements of the event BitSet
     * @return List<PTrace>
     */
    public List<PTrace> getCustomPTraceList() {
        BitSet currentBS = this.validTraceIndexBS;

        List<PTrace> theCusPTraceList = new ArrayList<>();

        for (int i = 0; i < originalPTraceList.size(); i++) {
            PTrace pt = originalPTraceList.get(i);
            if(!currentBS.get(i)) {
                pt.setValidEventIndexBS(new BitSet(pt.getEventSize()));
            }
            theCusPTraceList.add(pt);
        }
        return theCusPTraceList;
    }

    /**
     * This method creates a new APMLog. It should be used only after filter editing is completed.
     * @return
     */
    public APMLog toAPMLog() {

        return new ImmutableLog(this);

    }
}
