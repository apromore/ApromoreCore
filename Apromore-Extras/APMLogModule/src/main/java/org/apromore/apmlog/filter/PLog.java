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
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.Map.Entry.comparingByValue;

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
 * Modified: Chii Chang (12/01/2021)
 */
public class PLog extends LaLog {

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

    private UnifiedMap<String, Integer> activityMaxOccurMap;

    private ActivityNameMapper activityNameMapper;

    private AAttributeGraph originalAttributeGraph;
    private AAttributeGraph previousAttributeGraph;

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

        pTraceUnifiedMap = new UnifiedMap<>();

        this.timeZone = apmLog.getTimeZone();

        this.validTraceIndexBS = new BitSet(apmLog.getImmutableTraces().size());
        this.originalValidTraceIndexBS = new BitSet(apmLog.getImmutableTraces().size());

        for (ATrace aTrace : apmLog.getTraceList()) {
            int index = aTrace.getImmutableIndex();
            this.validTraceIndexBS.set(index);
            this.originalValidTraceIndexBS.set(index);
        }

        LOGGER.info("init values for validTraceIndexBS, originalValidTraceIndexBS, previousValidTraceIndexBS");
        this.validTraceIndexBS.set(0, apmLog.getImmutableTraces().size(), true);
        this.originalValidTraceIndexBS.set(0, apmLog.getImmutableTraces().size(), true);

        /**
         * PERFORMANCE PROBLEM
         */

        this.pTraceList = new ArrayList<>();

        LOGGER.info("copy apmTraceList to pTraceList");

        List<ATrace> apmTraceList = apmLog.getTraceList();

        this.immutableTraces = apmLog.getImmutableTraces();
        this.traceList = new ArrayList<>(apmTraceList.size());
        this.pTraceList = new ArrayList<>(apmTraceList.size());
        this.originalPTraceList = new ArrayList<>(apmTraceList.size());

        caseDurationList = new DoubleArrayList(apmLog.getTraceList().size());

        for(int i=0; i < apmTraceList.size(); i++) {
            ATrace aTrace = apmTraceList.get(i);

            PTrace pTrace = new PTrace(aTrace, apmLog);

            this.traceList.add(pTrace);

            caseDurationList.add(pTrace.getDuration());

            caseIndexMap.put(pTrace, i);

            this.pTraceList.add(pTrace);
            this.pTraceUnifiedMap.put(aTrace.getCaseId(), pTrace);
            if(pTrace.caseIdDigit > 0) {
                if(pTrace.caseIdDigit > maxCaseIdDigit) maxCaseIdDigit = pTrace.caseIdDigit;
                if(minCaseIdDigit == 0 || pTrace.caseIdDigit < minCaseIdDigit) minCaseIdDigit = pTrace.caseIdDigit;
            }

            this.originalPTraceList.add(pTrace);
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

        this.caseAttributeValueFreqMap = apmLog.getCaseAttributeValueFreqMap();
        this.eventAttributeValueFreqMap = apmLog.getEventAttributeValueFreqMap();
        this.eventAttributeValueCasesFreqMap = apmLog.getEventAttributeValueCasesFreqMap();

        LOGGER.info("done");
    }


    public void updateStats(List<PTrace> filteredPTraceList) {
        startTime = 0;
        endTime = 0;

        actNameIdxCId = new HashBiMap<>();

        variantIdFreqMap = new UnifiedMap<>();

        UnifiedMap<IntArrayList, Integer> actNameIndexesFreqMap = new UnifiedMap<>();

        List<IntArrayList> traceActNameIndexes = new ArrayList<>();

        eventSize = 0;

        variantIdFreqMap = new UnifiedMap<>();

        pTraceList = new ArrayList<>(filteredPTraceList);
        traceList = new ArrayList<>(filteredPTraceList);

        pTraceUnifiedMap.clear();

        eventAttributeValueCasesFreqMap.clear();
        eventAttributeValueFreqMap.clear();
        caseAttributeValueFreqMap.clear();

        this.caseDurationList = new DoubleArrayList(pTraceList.size());

        for (int i = 0; i < pTraceList.size(); i++) {

            PTrace trace = pTraceList.get(i);

            caseDurationList.add(trace.getDuration());

            trace.update(i);

            pTraceUnifiedMap.put(trace.getCaseId(), trace);
            updateCaseAttributes(trace);

            int vari = trace.getCaseVariantId();

            if (variantIdFreqMap.containsKey(vari)) {
                int freq = variantIdFreqMap.get(vari) + 1;
                variantIdFreqMap.put(vari, freq);
            } else variantIdFreqMap.put(vari, 1);

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

            BitSet vEvents = trace.getValidEventIndexBitSet();
            eventSize += vEvents.cardinality();

        }

        variantSize = variantIdFreqMap.size();

        List<Map.Entry<IntArrayList, Integer>> list = new ArrayList<>(actNameIndexesFreqMap.entrySet());
        list.sort(comparingByValue());

        updateEventAttributeOccurMap();

        int size = eventAttributeOccurMap.size();
        eventAttributeValueFreqMap = new UnifiedMap<>(size);


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

        }

        updateActivityOccurMaxMap();

        attributeGraph = new AAttributeGraph(this);
        defaultChartDataCollection = new DefaultChartDataCollection(this);
    }


    private void updateCaseAttributes(PTrace pTrace) {

        UnifiedMap<String, String> map = pTrace.getAttributeMap();
        for (String key : map.keySet()) {
            String val = map.get(key);

            if (!key.equals("concept:name") && !key.equals("case:variant")) {
                if (caseAttributeValueFreqMap.containsKey(key)) {
                    UnifiedMap<String, Integer> valFreqMap = caseAttributeValueFreqMap.get(key);
                    if (valFreqMap.containsKey(val)) {
                        int freq = valFreqMap.get(val) + 1;
                        valFreqMap.put(val, freq);
                    } else valFreqMap.put(val, 1);
                } else {
                    UnifiedMap<String, Integer> valFreqMap = new UnifiedMap<>();
                    valFreqMap.put(val, 1);
                    caseAttributeValueFreqMap.put(key, valFreqMap);
                }
            }
        }
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

        attributeGraph = originalAttributeGraph;

        this.traceList.clear();
        this.pTraceList.clear();

        for(int i=0; i<this.originalPTraceList.size(); i++) {
            this.originalPTraceList.get(i).reset();

            PTrace pTrace = this.originalPTraceList.get(i);

            pTrace.reset();

            this.traceList.add(pTrace);
            this.pTraceList.add(pTrace);
        }
        pTraceList = originalPTraceList;
        caseVariantSize = originalCaseVariantSize;
        eventSize = originalEventSize;
        startTime = originalStartTime;
        endTime = originalEndTime;
        variantIdFreqMap = originalVariantIdFreqMap;
        activityMaxOccurMap = originalActivityMaxOccurMap;

        for(int i=0; i<validTraceIndexBS.length(); i++) {
            validTraceIndexBS.set(i, true);
        }

        updateEventAttributeOccurMap();

        this.updateStats(pTraceList);
    }

    public void resetPrevious() {

        if(previousPTraceList != null) {
            attributeGraph = previousAttributeGraph;
            this.pTraceList = previousPTraceList;

            this.traceList.clear();

            caseDurationList = new DoubleArrayList(pTraceList.size());

            for (PTrace pTrace: pTraceList) {
                pTrace.resetPrevious();
                this.traceList.add(pTrace);
                caseDurationList.add(pTrace.getDuration());
            }

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

    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueCasesFreqMap() {
        return eventAttributeValueCasesFreqMap;
    }

    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap() {
        return eventAttributeValueFreqMap;
    }

    public UnifiedMap<String, UnifiedMap<String, Integer>> getCaseAttributeValueFreqMap() {
        return caseAttributeValueFreqMap;
    }

    public void setActivityMaxOccurMap(UnifiedMap<String, Integer> activityMaxOccurMap) {
        this.activityMaxOccurMap = activityMaxOccurMap;
    }

    public UnifiedMap<String, Integer> getActivityMaxOccurMap() {
        return activityMaxOccurMap;
    }

    public PTrace get(int index) {
        return this.pTraceList.get(index);
    }

    public PTrace get(String caseId) {
        return pTraceUnifiedMap.get(caseId);
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
        List<String> nameList = new ArrayList<>(caseAttributeValueFreqMap.keySet());
        Collections.sort(nameList);
        return nameList;
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


    public double getMedianDuration() {
        return medianDuration;
    }

    public double getAverageDuration() {
        return averageDuration;
    }


    @Override
    public UnifiedMap<Integer, Integer> getVariantIdFreqMap() {
        return variantIdFreqMap;
    }

    public List<PTrace> getOriginalPTraceList() {
        List<HashBiMap.Entry<PTrace, Integer> > list =
                new ArrayList<HashBiMap.Entry<PTrace, Integer> >(caseIndexMap.entrySet());


        Collections.sort(list, new Comparator<HashBiMap.Entry<PTrace, Integer>>() {
            @Override
            public int compare(HashBiMap.Entry<PTrace, Integer> o1, HashBiMap.Entry<PTrace, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        List<PTrace> theOPTraceList = new ArrayList<>();

        for (HashBiMap.Entry<PTrace, Integer> entry: list) {
            PTrace pTrace = entry.getKey();
            theOPTraceList.add(pTrace);
        }

        return theOPTraceList;
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

        UnifiedMap<String, ATrace> traceUM = new UnifiedMap<>();

        List<ATrace> traceList = new ArrayList<>();
        for(PTrace pTrace: pTraceList) {
            ATrace aTrace = pTrace.toATrace();
            traceList.add(aTrace);
            traceUM.put(aTrace.getCaseId(), aTrace);
        }

        ImmutableLog apmLog = new ImmutableLog(traceList,
                variantIdFreqMap,
                eventAttributeValueCasesFreqMap,
                eventAttributeValueFreqMap,
                caseAttributeValueFreqMap,
                caseDurationList,
                timeZone,
                startTime,
                endTime,
                eventSize,
                activityNameMapper,
                activityMaxOccurMap);

        apmLog.setEventAttributeOccurMap(new UnifiedMap<>(eventAttributeOccurMap));
        apmLog.setActivityNameBiMap(activityNameBiMap);

        apmLog.updateStats();

        return apmLog;

    }
}
