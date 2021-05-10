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
import org.apromore.apmlog.stats.StatsUtil;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XLog;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
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
 * Modified: Chii Chang (17/03/2021)
 * Modified: Chii Chang (30/04/2021)
 * Modified: Chii Chang (05/05/2021)
 */
public class PLog implements APMLog {

    private APMLog apmLog;

    private static final Logger LOGGER = LoggerFactory.getLogger(PLog.class);

    private UnifiedMap<String, PTrace> pTraceUnifiedMap;
    private final List<PTrace> pTraceList = new ArrayList<>();
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

    public long maxCaseIdDigit = 0;
    public long minCaseIdDigit = 0;

    private long variantSize = 0;

    private HashBiMap<PTrace, Integer> caseIndexMap = new HashBiMap<>();
    private AAttributeGraph originalAttributeGraph;
    private AAttributeGraph previousAttributeGraph;

    private UnifiedMap<String, UnifiedSet<EventAttributeValue>> eventAttributeValues;
    private UnifiedMap<String, UnifiedSet<CaseAttributeValue>> caseAttributeValues;

    //*************
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

    // =========================================
    // Used by PD
    // =========================================
    public List<AActivity> getAllActivities() {
        return ((ImmutableLog) apmLog).getImmutableTraces().stream()
                .filter(x -> validTraceIndexBS.get(x.getImmutableIndex()))
                .flatMap(x -> x.getActivityList().stream())
                .collect(Collectors.toList());
    }

    public List<AActivity> getAllOriginalActivities() {
        return ((ImmutableLog) apmLog).getImmutableTraces().stream()
                .flatMap(x -> x.getActivityList().stream())
                .collect(Collectors.toList());
    }


    public DefaultChartDataCollection getDefaultChartDataCollection() {
        return defaultChartDataCollection;
    }

    public PLog(APMLog apmLog, boolean initIndexOnly) {
        this.apmLog = apmLog;

        if (!initIndexOnly) {
            initDefault();
        } else {
            int originalSize = apmLog.immutableSize();
            validTraceIndexBS = new BitSet(originalSize);

            pTraceList.clear();

            int index = 0;
            for (ATrace aTrace : apmLog.getTraceList()) {
                validTraceIndexBS.set(aTrace.getImmutableIndex());
                PTrace pTrace = new PTrace(index, aTrace, this);
                pTraceList.add(pTrace);
                index += 1;
            }
        }
    }

    public PLog(APMLog apmLog) {
        this.apmLog = apmLog;
        initDefault();
    }

    private void initDefault() {
        this.attributeGraph = apmLog.getAAttributeGraph();
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

        ImmutableLog immutableLog = (ImmutableLog) apmLog;

        this.validTraceIndexBS = new BitSet(immutableLog.getImmutableTraces().size());
        this.validTraceIndexBS.set(0, immutableLog.getImmutableTraces().size());
        this.originalValidTraceIndexBS = new BitSet(immutableLog.getImmutableTraces().size());
        this.originalValidTraceIndexBS.set(0, immutableLog.getImmutableTraces().size());


        for (ATrace aTrace : apmLog.getTraceList()) {
            int index = aTrace.getImmutableIndex();
            this.validTraceIndexBS.set(index);
            this.originalValidTraceIndexBS.set(index);
        }



        this.validTraceIndexBS.set(0, immutableLog.getImmutableTraces().size(), true);
        this.originalValidTraceIndexBS.set(0, immutableLog.getImmutableTraces().size(), true);


        List<ATrace> apmTraceList = immutableLog.getImmutableTraces();

        this.pTraceList.clear();
        this.originalPTraceList = new ArrayList<>(apmTraceList.size());


        caseDurationList = new DoubleArrayList(apmLog.getTraceList().size());

        caseIndexMap.clear();


        int mutableIndex = 0;
        for (ATrace aTrace : apmTraceList) {
            PTrace pTrace = new PTrace(mutableIndex, aTrace, this);
            this.pTraceList.add(pTrace);
            caseDurationList.add(pTrace.getDuration());

            caseIndexMap.put(pTrace, mutableIndex);


            this.pTraceUnifiedMap.put(aTrace.getCaseId(), pTrace);
            if(pTrace.getCaseIdDigit() > 0) {
                if(pTrace.getCaseIdDigit() > maxCaseIdDigit) maxCaseIdDigit = pTrace.getCaseIdDigit();
                if(minCaseIdDigit == 0 || pTrace.getCaseIdDigit() < minCaseIdDigit) minCaseIdDigit = pTrace.getCaseIdDigit();
            }

            this.originalPTraceList.add(pTrace);
            mutableIndex += 1;
        }


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


        UnifiedMap<Integer, Integer> apmVariantIdFreqMap = apmLog.getCaseVariantIdFrequencyMap();

        this.variantIdFreqMap = new UnifiedMap<>(apmVariantIdFreqMap);
        this.originalVariantIdFreqMap = new UnifiedMap<>(apmVariantIdFreqMap);

        caseVariantSize = variantIdFreqMap.size();

        this.originalActivityMaxOccurMap = new UnifiedMap<>(apmLog.getActivityMaxOccurMap());

    }

    public void updateStats() {
        updateTraceStats();
        List<PTrace> traces = getPTraceList();
        caseAttributeValues = StatsUtil.getValidCaseAttributeValues(traces);
        eventAttributeValues = StatsUtil.getValidEventAttributeValues(traces);
    }

    public void updateTraceStats() {
        int index = 0;
        for (PTrace pTrace : pTraceList) {
            if (validTraceIndexBS.get(pTrace.getImmutableIndex())) {
                pTrace.updateStats(index);
                index += 1;
            }
        }
    }

    public IntArrayList getActivityNameIndexes(PTrace trace) {
        IntArrayList nameIndexes = new IntArrayList(trace.getActivityList().size());
        List<AActivity> activityList = trace.getActivityList();
        for (AActivity aActivity : activityList) {
            int actNameIndex = activityNameBiMap.get(aActivity.getName());
            nameIndexes.add(actNameIndex);
        }
        return nameIndexes;
    }

    public void resetIndex() {
        setValidTraceIndexBS(originalValidTraceIndexBS);
        setPTraceList(originalPTraceList);
        for (PTrace pTrace : pTraceList) {
            pTrace.setValidEventIndexBS(pTrace.getOriginalValidEventIndexBS());
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
        this.pTraceList.clear();
        this.pTraceList.addAll(pTraceList);
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

    public ActivityNameMapper getActivityNameMapper() {
        return activityNameMapper;
    }


    public ATrace get(String caseId) {
        return apmLog.get(caseId);
    }


    public UnifiedMap<String, ATrace> getTraceUnifiedMap() {
        return apmLog.getTraceUnifiedMap();
    }

    @Override
    public int getUniqueActivitySize() {
        UnifiedSet<EventAttributeValue> actEAVSet = getEventAttributeValues().get("concept:name");
        if (actEAVSet == null) return 0;

        return actEAVSet.size();
    }

    public PTrace getPTrace(int index) {
        return this.pTraceList.get(index);
    }

    public ATrace get(int index) {
        return apmLog.get(index);
    }


    public ATrace getImmutable(int index) {
        return apmLog.get(index);
    }


    public int immutableSize() {
        return apmLog.size();
    }

    public long getStartTime() {
        long[] array = getPTraceList().stream().mapToLong(x -> x.getStartTimeMilli()).toArray();
        LongArrayList lal = new LongArrayList(array);
        return lal.min();
    }


    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    public long getEndTime() {
        long[] array = getPTraceList().stream().mapToLong(x -> x.getEndTimeMilli()).toArray();
        LongArrayList lal = new LongArrayList(array);
        return lal.min();
    }


    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }


    public String getTimeZone() {
        return this.timeZone;
    }


    public String getMinDurationString() {
        return Util.durationShortStringOf(getMinDuration());
    }


    public String getMaxDurationString() {
        return Util.durationShortStringOf(getMaxDuration());
    }

    public PTrace getPTraceById(String caseId) {
        return pTraceUnifiedMap.get(caseId);
    }

    public UnifiedMap<Integer, Integer> getCaseVariantIdFrequencyMap() {
        Map<Integer, List<PTrace>> variGroupMap = StatsUtil.getVariantGroupMap(this);
        UnifiedMap<Integer, Integer> output = new UnifiedMap<>(variGroupMap.size());
        for (int vId : variGroupMap.keySet()) {
            output.put(vId, variGroupMap.get(vId).size());
        }
        return output;
    }

    public List<String> getActivityNameList(int caseVariantId) {
        PTrace pTrace = getPTraceList().stream()
                .filter(x -> x.getCaseVariantId() == caseVariantId)
                .findFirst()
                .orElse(null);

        if (pTrace == null) return null;

        return pTrace.getActivityNameList();
    }

    public UnifiedSet<String> getEventAttributeNameSet() {
        UnifiedMap<String, UnifiedSet<EventAttributeValue>> eavMap = StatsUtil.getValidEventAttributeValues(pTraceList);
        if (eavMap == null || eavMap.isEmpty()) return null;

        return new UnifiedSet<>(eavMap.keySet());
    }

    public List<String> getCaseAttributeNameList() {
        UnifiedMap<String, UnifiedSet<CaseAttributeValue>> cavMap = StatsUtil.getValidCaseAttributeValues(pTraceList);
        if (cavMap == null || cavMap.isEmpty()) return null;

        return new ArrayList<>(cavMap.keySet());
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


    public long getCaseVariantSize() {
        Map<Integer, List<PTrace>> groups = getPTraceList().stream()
                .collect(Collectors.groupingBy(PTrace::getCaseVariantId));
        return groups.size();
    }


    public long getEventSize() {
        return getPTraceList().stream().mapToLong(PTrace::getEventSize).sum();
    }


    public void setEventSize(int eventSize) {
        this.eventSize = eventSize;
    }


    public double getMedianDuration() {
        return medianDuration;
    }


    public String getAverageDurationString() {
        return Util.durationShortStringOf(getAverageDuration());
    }


    public String getMedianDurationString() {
        return Util.durationShortStringOf(getMedianDuration());
    }


    public String getStartTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(this.startTime));
    }


    public String getEndTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(this.endTime));
    }

    @Override
    public APMLog clone() {
        return null;
    }


    public XLog toXLog() {
        return null;
    }


    public AAttributeGraph getAAttributeGraph() {
        return attributeGraph;
    }

    public double getAverageDuration() {
        return averageDuration;
    }

    public void add(ATrace trace) {

    }


    public HashBiMap<String, Integer> getActivityNameBiMap() {
        return activityNameBiMap;
    }


    public void setActivityNameBiMap(HashBiMap<String, Integer> activityNameBiMap) {
        this.activityNameBiMap = activityNameBiMap;
    }


    public UnifiedMap<Integer, Integer> getVariantIdFreqMap() {
        return getCaseVariantIdFrequencyMap();
    }


    public DoubleArrayList getCaseDurations() {
        double[] array = getPTraceList().stream().mapToDouble(PTrace::getDuration).toArray();
        return new DoubleArrayList(array);
    }


    public double getMinDuration() {
        return !caseDurationList.isEmpty() ? caseDurationList.min() : 0;
    }



    public double getMaxDuration() {
        return !caseDurationList.isEmpty() ? caseDurationList.max() : 0;
    }

    public List<ATrace> getImmutableTraces() {
        return apmLog.getTraceList();
    }

    public List<ATrace> getTraceList() {
        return apmLog.getTraceList().stream()
                .filter(x -> validTraceIndexBS.get(x.getImmutableIndex()))
                .collect(Collectors.toList());
    }

    public void setTraceList(List<ATrace> traceList) {
        pTraceList.clear();
        int mutableIndex = 0;
        for (ATrace aTrace : traceList) {
            pTraceList.add( new PTrace(mutableIndex, aTrace, this) );
            mutableIndex += 1;
        }
    }

    public List<PTrace> getOriginalPTraceList() {
        List<PTrace> originalPTraces = new ArrayList<>(apmLog.size());
        int mutableIndex = 0;
        for (ATrace aTrace : apmLog.getImmutableTraces()) {
            originalPTraces.add(new PTrace(mutableIndex, aTrace, this));
            mutableIndex += 1;
        }
        return originalPTraces;
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

    public UnifiedMap<String, PTrace> getPTraceUnifiedMap() {
        return pTraceUnifiedMap;
    }

    public APMLog getOriginalAPMLog() {
        return apmLog;
    }

    public UnifiedMap<String, UnifiedSet<EventAttributeValue>> getEventAttributeValues() {
        List<PTrace> validTraces = StatsUtil.getValidTraces(this);
        return StatsUtil.getValidEventAttributeValues(validTraces);
    }

    public UnifiedMap<String, UnifiedSet<CaseAttributeValue>> getCaseAttributeValues() {
        List<PTrace> validTraces = StatsUtil.getValidTraces(this);
        return StatsUtil.getValidCaseAttributeValues(validTraces);
    }

    @Override
    public UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> getEventAttributeOccurMap() {
        return null;
    }

    @Override
    public DoubleArrayList getTraceProcessingTimes() {
        double[] array = pTraceList.stream().mapToDouble(x -> x.getProcessingTimes().sum()).toArray();
        return new DoubleArrayList(array);
    }

    @Override
    public DoubleArrayList getTraceWaitingTimes() {
        double[] array = pTraceList.stream().mapToDouble(x -> x.getWaitingTimes().sum()).toArray();
        return new DoubleArrayList(array);
    }

    private PTrace getPTraceByImmutableIndex(int index) {
        return pTraceList.stream()
                .filter(x -> x.getImmutableIndex() == index)
                .findFirst()
                .orElse(null);
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

        List<ATrace> immutableTraces = apmLog.getTraceList();

        int mutableIndex = 0;

        for (ATrace aTrace : immutableTraces) {
            PTrace pt = null;
            if (!currentBS.get(aTrace.getImmutableIndex())) {
                pt = new PTrace(mutableIndex, aTrace, this);
                pt.setValidEventIndexBS(new BitSet(aTrace.getEventSize()));
            } else {
                pt = getPTraceByImmutableIndex(aTrace.getImmutableIndex());
            }

            if (pt != null) {
                theCusPTraceList.add(pt);
                mutableIndex += 1;
            }
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
