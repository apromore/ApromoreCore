/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.apmlog.filter;

import org.apromore.apmlog.*;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Chii Chang
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (06/03/2020)
 * Modified: Chii Chang (11/04/2020)
 * Modified: Chii Chang (12/05/2020)
 */
public class PLog {

    private APMLog apmLog;
    DefaultChartDataCollection defaultChartDataCollection;

    private static final Logger LOGGER = LoggerFactory.getLogger(PLog.class);

    private UnifiedMap<String, PTrace> pTraceUnifiedMap;
    private List<PTrace> pTraceList;
    private UnifiedMap<Integer, Integer> variantIdFreqMap;
    private HashBiMap<Integer, String> actIdNameMap;
    private UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueCasesFreqMap;
    private UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap;
    private UnifiedMap<String, UnifiedMap<String, Integer>> caseAttributeValueFreqMap;


    private long minDuration = 0;
    private long medianDuration = 0;
    private long averageDuration = 0;
    private long maxDuration = 0;
    private String timeZone = "";
    private long startTime = -1;
    private long endTime = -1;
    private long caseVariantSize = 0;
    private long eventSize = 0;

    private BitSet validTraceIndexBS;

    public BitSet originalValidTraceIndexBS;

    private long originalMinDuration = 0;
    private long originalMedianDuration = 0;
    private long originalAverageDuration = 0;
    private long originalMaxDuration = 0;
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
    public long previousMinDuration;
    public long previousMedianDuration = 0;
    public long previousAverageDuration = 0;
    public long previousMaxDuration;
    public long previousStartTime;
    public long previousEndTime;

    public long maxCaseIdDigit = 0;
    public long minCaseIdDigit = 0;

    private HashBiMap<PTrace, Integer> caseIndexMap = new HashBiMap<>();

    private UnifiedMap<String, Integer> activityMaxOccurMap;

    private ActivityNameMapper activityNameMapper;

    public APMLog getApmLog() {
        return apmLog;
    }


    public DefaultChartDataCollection getDefaultChartDataCollection() {
        return defaultChartDataCollection;
    }

    public PLog(APMLog apmLog) {
        this.apmLog = apmLog;

        this.defaultChartDataCollection = apmLog.getDefaultChartDataCollection();

        this.activityNameMapper = apmLog.getActivityNameMapper();

        this.activityMaxOccurMap = new UnifiedMap<>(apmLog.getActivityMaxOccurMap());

        pTraceUnifiedMap = new UnifiedMap<>();

        this.timeZone = apmLog.getTimeZone();

        this.validTraceIndexBS = new BitSet(apmLog.getTraceList().size());
        this.originalValidTraceIndexBS = new BitSet(apmLog.getTraceList().size());
        this.previousValidTraceIndexBS = new BitSet(apmLog.getTraceList().size());

        LOGGER.info("init values for validTraceIndexBS, originalValidTraceIndexBS, previousValidTraceIndexBS");
        this.validTraceIndexBS.set(0, apmLog.getTraceList().size(), true);
        this.originalValidTraceIndexBS.set(0, apmLog.getTraceList().size(), true);
        this.previousValidTraceIndexBS.set(0, apmLog.getTraceList().size(), true);

        /**
         * PERFORMANCE PROBLEM
         */

        this.pTraceList = new ArrayList<>();

        LOGGER.info("copy apmTraceList to pTraceList");


        List<ATrace> apmTraceList = apmLog.getTraceList();
        for(int i=0; i < apmTraceList.size(); i++) {
            ATrace aTrace = apmTraceList.get(i);



            PTrace pTrace = new PTrace(aTrace, apmLog);

            caseIndexMap.put(pTrace, i);

            this.pTraceList.add(pTrace);
            this.pTraceUnifiedMap.put(aTrace.getCaseId(), pTrace);
            if(pTrace.caseIdDigit > 0) {
                if(pTrace.caseIdDigit > maxCaseIdDigit) maxCaseIdDigit = pTrace.caseIdDigit;
                if(minCaseIdDigit == 0 || pTrace.caseIdDigit < minCaseIdDigit) minCaseIdDigit = pTrace.caseIdDigit;
            }
        }

        this.originalPTraceList = new ArrayList<>(this.pTraceList);
        this.previousPTraceList = new ArrayList<>(this.pTraceList);
//        this.customPTraceList = new ArrayList<>(this.pTraceList);


        System.out.println("done");

        LOGGER.info("do the rest of copies");
        this.caseVariantSize = apmLog.getCaseVariantSize();
        this.originalCaseVariantSize = apmLog.getCaseVariantSize();
        this.previousCaseVariantSize = apmLog.getCaseVariantSize();

        this.eventSize = apmLog.getEventSize();
        this.originalEventSize = apmLog.getEventSize();
        this.previousEventSize = apmLog.getEventSize();

        this.minDuration = apmLog.getMinDuration();
        this.originalMinDuration = apmLog.getMinDuration();
        this.previousMinDuration = apmLog.getMinDuration();

        this.medianDuration = apmLog.getMedianDuration();
        this.originalMedianDuration = apmLog.getMedianDuration();
        this.previousMedianDuration = apmLog.getMedianDuration();

        this.averageDuration = apmLog.getAverageDuration();
        this.originalAverageDuration = apmLog.getAverageDuration();
        this.originalAverageDuration = apmLog.getAverageDuration();

        this.maxDuration = apmLog.getMaxDuration();
        this.originalMaxDuration = apmLog.getMaxDuration();
        this.previousMaxDuration = apmLog.getMaxDuration();

        this.startTime = apmLog.getStartTime();
        this.originalStartTime = apmLog.getStartTime();
        this.previousStartTime = apmLog.getStartTime();

        this.endTime = apmLog.getEndTime();
        this.originalEndTime = apmLog.getEndTime();
        this.previousEndTime = apmLog.getEndTime();


        LOGGER.info("copy case variant id freq map");
        UnifiedMap<Integer, Integer> apmVariantIdFreqMap = apmLog.getCaseVariantIdFrequencyMap();
        LOGGER.info("done");

        this.variantIdFreqMap = new UnifiedMap<>(apmVariantIdFreqMap);
        this.originalVariantIdFreqMap = new UnifiedMap<>(apmVariantIdFreqMap);
        this.previousVariantIdFreqMap = new UnifiedMap<>(apmVariantIdFreqMap);

        this.originalActivityMaxOccurMap = new UnifiedMap<>(apmLog.getActivityMaxOccurMap());
        this.previousActivityMaxOccurMap = new UnifiedMap<>(apmLog.getActivityMaxOccurMap());


        HashBiMap<Integer, String> aiMap = apmLog.getActIdNameMap();

        this.actIdNameMap = new HashBiMap<>(aiMap);

        this.caseAttributeValueFreqMap = apmLog.getCaseAttributeValueFreqMap();
        this.eventAttributeValueFreqMap = apmLog.getEventAttributeValueFreqMap();
        this.eventAttributeValueCasesFreqMap = apmLog.getEventAttributeValueCasesFreqMap();

        LOGGER.info("done");
    }

    public void reset() {

        for(int i=0; i<this.originalPTraceList.size(); i++) {
            this.originalPTraceList.get(i).reset();
        }
        pTraceList = originalPTraceList;
        caseVariantSize = originalCaseVariantSize;
        eventSize = originalEventSize;
        minDuration = originalMinDuration;
        maxDuration = originalMaxDuration;
        startTime = originalStartTime;
        endTime = originalEndTime;
        variantIdFreqMap = originalVariantIdFreqMap;
        activityMaxOccurMap = originalActivityMaxOccurMap;

        for(int i=0; i<validTraceIndexBS.length(); i++) {
            validTraceIndexBS.set(i, true);
        }
    }

    public void resetPrevious() {

        if(previousPTraceList != null) {
            this.pTraceList = previousPTraceList;
            for (int i = 0; i < pTraceList.size(); i++) {
                pTraceList.get(i).resetPrevious();
            }

            caseVariantSize = previousCaseVariantSize;
            eventSize = previousEventSize;
            minDuration = previousMinDuration;
            maxDuration = previousMaxDuration;
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

        previousPTraceList = this.pTraceList;

        for (int i = 0; i < previousPTraceList.size(); i++) {
            previousPTraceList.get(i).updatePrevious();
        }

        previousCaseVariantSize = caseVariantSize;
        previousEventSize = eventSize;
        previousMinDuration = minDuration;
        previousMaxDuration = maxDuration;
        previousStartTime = startTime;
        previousEndTime = endTime;
        previousVariantIdFreqMap = variantIdFreqMap;
        previousActivityMaxOccurMap = activityMaxOccurMap;

        for (int i = 0; i < previousValidTraceIndexBS.length(); i++) {
            previousValidTraceIndexBS.set(i, validTraceIndexBS.get(i));
        }
    }

    public void updateActivityOccurMaxMap() {

        UnifiedMap<String, Integer> actMaxOccur = new UnifiedMap<>();

        for (int i = 0; i < this.pTraceList.size(); i++) {
            PTrace pTrace = pTraceList.get(i);
            List<AActivity> aActivityList = pTrace.getActivityList();

            UnifiedMap<String, Integer> actOccurFreq = new UnifiedMap<>();

            for (int j = 0; j < aActivityList.size(); j++) {
                AActivity aActivity = aActivityList.get(j);
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


        for(int i=0; i < this.pTraceList.size(); i++) {
            PTrace pTrace = this.pTraceList.get(i);
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

        for (int i = 0; i < pTraceList.size(); i++) {
            PTrace pTrace = pTraceList.get(i);
            List<String> actNameList = pTrace.getActivityNameList();
            pTraceList.get(i).setCaseVariantId(variantIdMap.get(actNameList));
        }

        return variFreqMap.size();
    }


    /**
     * This method creates a new APMLog. It should be used only after filter editing is completed.
     * @return
     */
    public APMLog toAPMLog() {

        UnifiedMap<String, ATrace> traceUM = new UnifiedMap<>();

        List<ATrace> traceList = new ArrayList<>();
        for(int i=0; i < this.pTraceList.size(); i++) {
            ATrace aTrace = this.pTraceList.get(i).toATrace();
            traceList.add(aTrace);
            traceUM.put(aTrace.getCaseId(), aTrace);
        }

        APMLog apmLog = new APMLog(traceList,
                variantIdFreqMap,
                actIdNameMap,
                eventAttributeValueCasesFreqMap,
                eventAttributeValueFreqMap,
                caseAttributeValueFreqMap,
                traceUM,
                minDuration,
                maxDuration,
                timeZone,
                startTime,
                endTime,
                variantIdFreqMap.size(),
                eventSize,
                activityNameMapper,
                activityMaxOccurMap);

        return apmLog;
    }

    /* ----------------- SET methods ------------------ */

    public void setMinDuration(long minDuration) {
        this.minDuration = minDuration;
    }

    public void setMedianDuration(long medianDuration) {
        this.medianDuration = medianDuration;
    }

    public void setAverageDuration(long averageDuration) {
        this.averageDuration = averageDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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

    public List<String> getActivityNameList(int caseVariantId) { //2019-10-31
        for(PTrace pTrace : pTraceList) {
            if(pTrace.getCaseVariantId() == caseVariantId){
                return pTrace.getActivityNameList();
            }
        }
        return null;
    }

    public UnifiedSet<String> getEventAttributeNameSet() {
        UnifiedSet<String> validNames = new UnifiedSet<>();
        for(String key : this.eventAttributeValueFreqMap.keySet()) {
            int qty = this.eventAttributeValueFreqMap.get(key).size();
            if(qty < 100000) validNames.put(key);
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

    public String getTimeZone() {
        return timeZone;
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
        return caseVariantSize;
    }

    public long getEventSize() {
        return eventSize;
    }

    public long getMinDuration() {
        return minDuration;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public long getMedianDuration() {
        return medianDuration;
    }

    public long getAverageDuration() {
        return averageDuration;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

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

        for (int i=0; i<list.size(); i++) {
            PTrace pTrace = list.get(i).getKey();
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
            PTrace pTrace = originalPTraceList.get(i);
            String theId = pTrace.getCaseId();
            PTrace pt = this.pTraceUnifiedMap.get(theId);
            if(!currentBS.get(i)) {
                pt.getValidEventIndexBitSet().clear();
                pt.getOriginalEventList().clear();
            } else {
                pt.setOriginalValidEventIndexBS(pTrace.getValidEventIndexBitSet());
            }
            theCusPTraceList.add(pt);
        }
        return theCusPTraceList;
    }
}
