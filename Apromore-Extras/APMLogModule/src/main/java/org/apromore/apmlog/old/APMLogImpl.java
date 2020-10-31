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
package org.apromore.apmlog.old;


import org.apromore.apmlog.*;
import org.apromore.apmlog.immutable.ImmutableActivity;
import org.apromore.apmlog.stats.AAttributeGraph;
import org.apromore.apmlog.util.Util;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

/**
 * @author Chii Chang (11/2019)
 * Modified: Chii Chang (03/02/2020)
 * Modified: Chii Chang (04/02/2020)
 * Modified: Chii Chang (12/02/2020)
 * Modified: Chii Chang (06/03/2020) - public APMLog(List<ATrace> inputTraceList)
 * Modified: Chii Chang (12/05/2020)
 */
public class APMLogImpl implements Serializable, APMLog {


    private List<ATrace> traceList;
    private UnifiedMap<Integer, Integer> variantIdFreqMap;
    private HashBiMap<Integer, String> actIdNameMap;
    private UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueCasesFreqMap;
    private UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap;
    private UnifiedMap<String, UnifiedMap<String, Integer>> caseAttributeValueFreqMap;
    private UnifiedMap<String, Integer> activityMaxOccurMap = new UnifiedMap<>();
    private double minDuration = 0;
    private double maxDuration = 0;
    private String timeZone = "";
    private long startTime = -1;
    private long endTime = -1;
    private long caseVariantSize = 0;
    private long eventSize = 0;

    private UnifiedMap<String, ATrace> traceUnifiedMap;

    private ActivityNameMapper activityNameMapper;

    private DefaultChartDataCollection defaultChartDataCollection;

    private AAttributeGraph aAttributeGraph;


    private static final Logger LOGGER = LoggerFactory.getLogger(APMLogImpl.class);

    public APMLogImpl(XLog xLog) {

        activityNameMapper = new ActivityNameMapper();

        traceList = new ArrayList<>();
        eventAttributeValueCasesFreqMap = new UnifiedMap<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();
        caseAttributeValueFreqMap = new UnifiedMap<>();
        activityMaxOccurMap = new UnifiedMap<>();

        LOGGER.info("start parsing XLog");
        initData(xLog);
        LOGGER.info("finish parsing XLog");
        LOGGER.info("done");

//        ImmutableLog log = LogFactory.convertXLog(xLog);
//
//        System.out.println("PAUSE");
    }


    private void initData(XLog xLog) {

        traceUnifiedMap = new UnifiedMap<>();

        UnifiedMap<IntArrayList, Integer> actIdListFreqMap = new UnifiedMap<>();
        actIdNameMap = new HashBiMap<>();
        variantIdFreqMap = new UnifiedMap<>();

        UnifiedMap<Integer, IntArrayList> variantIdActIdListMap = new UnifiedMap<>();

        // initial vId, final vId

        UnifiedMap<IntArrayList, Integer> tempActIdListToVIdMap = new UnifiedMap<>();

        int actIdCount = 0;
        LOGGER.info(">>> Create actIdNameMap");
        for(XTrace xTrace : xLog) {

            for(int i=0; i < xTrace.size(); i++) {
                XEvent xEvent = xTrace.get(i);
                String conceptName = "";
                if(xEvent.getAttributes().get("concept:name") != null) {
                    conceptName = xEvent.getAttributes().get("concept:name").toString();
                }
                if(!actIdNameMap.containsValue(conceptName)) {
                    actIdNameMap.put(actIdCount, conceptName.intern());
                    actIdCount += 1;
                }
            }
        }
        LOGGER.info(">>> Create actIdNameMap DONE");

        boolean containsVariantId = false;

        int tempVariId = 1;

        LOGGER.info(">>> Create ATrace list");
        for(int i=0; i<xLog.size(); i++) {
            ATrace aTrace = new ATraceImpl(i, xLog.get(i), this);

            int idx = i + 1;
            System.out.println(idx + " / " + xLog.size());

            if (aTrace.size() > 0) {

                traceUnifiedMap.put(aTrace.getCaseId(), aTrace);

                eventSize += aTrace.getEventSize();

                if (startTime == -1 || aTrace.getStartTimeMilli() < startTime) {
                    startTime = aTrace.getStartTimeMilli();
                }
                if (endTime == -1 || aTrace.getEndTimeMilli() > endTime) {
                    endTime = aTrace.getEndTimeMilli();
                }
                if (this.timeZone.equals("")) this.timeZone = aTrace.get(0).getTimeZone();

                /**
                 * Event attributes
                 */
                updateEventAttributeValueFreqMap(aTrace);


                /**
                 * Case attributes
                 */
                updateCaseAttributeValueFreqMap(aTrace);

                if (this.minDuration == 0 || aTrace.getDuration() < minDuration) {
                    minDuration = aTrace.getDuration();
                }
                if (this.maxDuration == 0 || aTrace.getDuration() > maxDuration) {
                    maxDuration = aTrace.getDuration();
                }


                this.traceList.add(aTrace);

                int variId = aTrace.getCaseVariantId();
                List<String> actNameList = aTrace.getActivityNameList();
                IntArrayList idList = getIntArrayListOf(actNameList);
                if (variId > 0) {
                    containsVariantId = true;
                    if (variantIdFreqMap.containsKey(variId)) {
                        int freq = variantIdFreqMap.get(variId) + 1;
                        variantIdFreqMap.put(variId, freq);
                    } else {
                        variantIdFreqMap.put(variId, 1);
                    }
                    if (!variantIdActIdListMap.containsKey(variId)) {
                        variantIdActIdListMap.put(variId, idList);
                    }
                } else { // does not have variant ID
                    if (actIdListFreqMap.containsKey(idList)) {
                        int freq = actIdListFreqMap.get(idList) + 1;
                        actIdListFreqMap.put(idList, freq);
                    } else {
                        actIdListFreqMap.put(idList, 1);
                    }

                    if (!tempActIdListToVIdMap.containsKey(idList)) {
                        tempActIdListToVIdMap.put(idList, tempVariId);
                        tempVariId += 1;
                    }

                    int vId = tempActIdListToVIdMap.get(idList);
                    aTrace.setCaseVariantId(vId);
                }

                computeActivityOccurMaxMap(aTrace);
            }
        }
        LOGGER.info(">>> Create ATrace list DONE");



        UnifiedMap<Integer, Integer> initVIdToFinalVIdMap = new UnifiedMap<>();

        LOGGER.info(">>> Create Case Variant Frequency map");

        if(variantIdFreqMap.size() < 1 && actIdListFreqMap.size() > 0) {
            List<Map.Entry<IntArrayList, Integer>> list =
                    new ArrayList<>(actIdListFreqMap.entrySet());
            list.sort(comparingByValue());
            int idNum = 1;
            for(int i=(list.size()-1); i>=0; i--) {
                variantIdFreqMap.put(idNum, list.get(i).getValue());
//                originalVariantIdFreqMap.put(idNum, list.getById(i).getValue());//2019-11-10
                if(!variantIdActIdListMap.containsKey(idNum)) {
                    variantIdActIdListMap.put(idNum, list.get(i).getKey());
                    int initVId = tempActIdListToVIdMap.get(list.get(i).getKey());
                    initVIdToFinalVIdMap.put(initVId, idNum);
                }
                idNum += 1;
            }
        }

        LOGGER.info(">>> Create Case Variant Frequency map DONE");

        if(!containsVariantId) {
            LOGGER.info(">>> Assign Case Variant to Traces");
            for(int i=0; i < this.traceList.size(); i++) {
                ATrace aTrace = this.traceList.get(i);
                int iVId = aTrace.getCaseVariantId();
                int finalVId = initVIdToFinalVIdMap.get(iVId);
                aTrace.setCaseVariantId(finalVId);
            }
            LOGGER.info(">>> Assign Case Variant to Traces DONE");
        }

        /**
         * This part is required for LogFilter to filterlogic by case variant
         */
        LOGGER.info("*** Write case variant ID to the original XLog");
        for(int i=0; i < xLog.size(); i++) {
            XTrace xTrace = xLog.get(i);
            ATrace aTrace = traceList.get(i);
            int variId = aTrace.getCaseVariantId();
            XAttribute attribute = new XAttributeLiteralImpl("case:variant", Integer.toString(variId));
            xTrace.getAttributes().put("case:variant", attribute);
        }
        LOGGER.info("*** Write case variant ID complete");

//        originalCaseVariantSize = variantIdFreqMap.size();
        caseVariantSize = variantIdFreqMap.size();

        defaultChartDataCollection = new DefaultChartDataCollection(this);

//        LOGGER.info("*** Update attributeGraph");
//
//        for (int i = 0; i < traceList.size(); i++) {
//
//            traceList.get(i).updateAttributeGraph();
//            System.out.println( (i+1) + " / " + traceList.size());
//        }
//        aAttributeGraph = new AAttributeGraph(this);

        LOGGER.info("*** APMLog complete");
    }

    public APMLogImpl(List<ATrace> inputTraceList) {

        activityNameMapper = new ActivityNameMapper();

        traceList = new ArrayList<>();
        eventAttributeValueCasesFreqMap = new UnifiedMap<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();
        caseAttributeValueFreqMap = new UnifiedMap<>();
        activityMaxOccurMap = new UnifiedMap<>();


        traceUnifiedMap = new UnifiedMap<>();

        UnifiedMap<IntArrayList, Integer> actIdListFreqMap = new UnifiedMap<>();
        actIdNameMap = new HashBiMap<>();
        variantIdFreqMap = new UnifiedMap<>();

        UnifiedMap<Integer, IntArrayList> variantIdActIdListMap = new UnifiedMap<>();

        // initial vId, final vId

        UnifiedMap<IntArrayList, Integer> tempActIdListToVIdMap = new UnifiedMap<>();

        int actIdCount = 0;
        LOGGER.info(">>> Create actIdNameMap");

        for (int h = 0; h < inputTraceList.size(); h++) {
            ATrace aTrace = inputTraceList.get(h);

            for(int i=0; i < aTrace.size(); i++) {
                AEvent aEvent = aTrace.get(i);
                String actName = aEvent.getName();

                if(!actIdNameMap.containsValue(actName)) {
                    actIdNameMap.put(actIdCount, actName);
                    actIdCount += 1;
                }
            }
        }

        LOGGER.info(">>> Create actIdNameMap DONE");

        boolean containsVariantId = false;

        int tempVariId = 1;

        LOGGER.info(">>> Create ATrace list");
        for(int i=0; i<inputTraceList.size(); i++) {
            ATrace aTrace = inputTraceList.get(i);

            if (aTrace.size() > 0) {

                traceUnifiedMap.put(aTrace.getCaseId(), aTrace);

                eventSize += aTrace.getEventSize();

                if (startTime == -1 || aTrace.getStartTimeMilli() < startTime) {
                    startTime = aTrace.getStartTimeMilli();
                }
                if (endTime == -1 || aTrace.getEndTimeMilli() > endTime) {
                    endTime = aTrace.getEndTimeMilli();
                }
                if (this.timeZone.equals("")) this.timeZone = aTrace.get(0).getTimeZone();

                /**
                 * Event attributes
                 */
                updateEventAttributeValueFreqMap(aTrace);


                /**
                 * Case attributes
                 */
                updateCaseAttributeValueFreqMap(aTrace);

                if (this.minDuration == 0 || aTrace.getDuration() < minDuration) {
                    minDuration = aTrace.getDuration();
                }
                if (this.maxDuration == 0 || aTrace.getDuration() > maxDuration) {
                    maxDuration = aTrace.getDuration();
                }


                this.traceList.add(aTrace);

                int variId = aTrace.getCaseVariantId();
                List<String> actNameList = aTrace.getActivityNameList();
                IntArrayList idList = getIntArrayListOf(actNameList);
                if (variId > 0) {
                    containsVariantId = true;
                    if (variantIdFreqMap.containsKey(variId)) {
                        int freq = variantIdFreqMap.get(variId) + 1;
                        variantIdFreqMap.put(variId, freq);
                    } else {
                        variantIdFreqMap.put(variId, 1);
                    }
                    if (!variantIdActIdListMap.containsKey(variId)) {
                        variantIdActIdListMap.put(variId, idList);
                    }
                } else { // does not have variant ID
                    if (actIdListFreqMap.containsKey(idList)) {
                        int freq = actIdListFreqMap.get(idList) + 1;
                        actIdListFreqMap.put(idList, freq);
                    } else {
                        actIdListFreqMap.put(idList, 1);
                    }

                    if (!tempActIdListToVIdMap.containsKey(idList)) {
                        tempActIdListToVIdMap.put(idList, tempVariId);
                        tempVariId += 1;
                    }

                    int vId = tempActIdListToVIdMap.get(idList);
                    aTrace.setCaseVariantId(vId);
                }

                computeActivityOccurMaxMap(aTrace);
            }
        }
        LOGGER.info(">>> Create ATrace list DONE");



        UnifiedMap<Integer, Integer> initVIdToFinalVIdMap = new UnifiedMap<>();

        LOGGER.info(">>> Create Case Variant Frequency map");

        if(variantIdFreqMap.size() < 1 && actIdListFreqMap.size() > 0) {
            List<Map.Entry<IntArrayList, Integer>> list =
                    new ArrayList<>(actIdListFreqMap.entrySet());
            list.sort(comparingByValue());
            int idNum = 1;
            for(int i=(list.size()-1); i>=0; i--) {
                variantIdFreqMap.put(idNum, list.get(i).getValue());
//                originalVariantIdFreqMap.put(idNum, list.getById(i).getValue());//2019-11-10
                if(!variantIdActIdListMap.containsKey(idNum)) {
                    variantIdActIdListMap.put(idNum, list.get(i).getKey());
                    int initVId = tempActIdListToVIdMap.get(list.get(i).getKey());
                    initVIdToFinalVIdMap.put(initVId, idNum);
                }
                idNum += 1;
            }
        }

        LOGGER.info(">>> Create Case Variant Frequency map DONE");

        if(!containsVariantId) {
            LOGGER.info(">>> Assign Case Variant to Traces");
            for(int i=0; i < this.traceList.size(); i++) {
                ATrace aTrace = this.traceList.get(i);
                int iVId = aTrace.getCaseVariantId();
                int finalVId = initVIdToFinalVIdMap.get(iVId);
                aTrace.setCaseVariantId(finalVId);
            }
            LOGGER.info(">>> Assign Case Variant to Traces DONE");
        }

        caseVariantSize = variantIdFreqMap.size();

        defaultChartDataCollection = new DefaultChartDataCollection(this);
    }

    public DefaultChartDataCollection getDefaultChartDataCollection() {
        return defaultChartDataCollection;
    }

    public UnifiedMap<String, Integer> getActivityMaxOccurMap() {
        return activityMaxOccurMap;
    }

    public org.apromore.apmlog.ActivityNameMapper getActivityNameMapper() {
        return activityNameMapper;
    }

    //    private void updateEventAttributeValueSetMap(ATrace aTrace) {
//        for (String key : aTrace.getRawEventAttributeValueSetMap().keySet()) {
//            if (this.rawEventAttributeValueSetMap.containsKey(key)) {
//                this.rawEventAttributeValueSetMap.getById(key).addAll(aTrace.getRawEventAttributeValueSetMap().getById(key));
//            } else {
//                UnifiedSet<String> vals = new UnifiedSet<>();
//                vals.addAll(aTrace.getRawEventAttributeValueSetMap().getById(key));
//                this.rawEventAttributeValueSetMap.put(key, vals);
//            }
//        }
//    }

    private void computeActivityOccurMaxMap(ATrace aTrace) {
        UnifiedMap<String, Integer> actOccurFreq = new UnifiedMap<>();

        List<AActivity> aActivityList = aTrace.getActivityList();

        for (int i = 0; i < aActivityList.size(); i++) {
            AActivity aActivity = aActivityList.get(i);
            String conceptName = aActivity.getName();
            if (actOccurFreq.containsKey(conceptName)) {
                int freq = actOccurFreq.get(conceptName) + 1;
                actOccurFreq.put(conceptName, freq);
            } else actOccurFreq.put(conceptName, 1);
        }

        for (String actName : actOccurFreq.keySet()) {
            int actFreq = actOccurFreq.get(actName);
            if (!activityMaxOccurMap.containsKey(actName)) {
                activityMaxOccurMap.put(actName, actOccurFreq.get(actName));
            } else {
                int freqInRecord = activityMaxOccurMap.get(actName);
                if (actFreq > freqInRecord) freqInRecord = actFreq;
                activityMaxOccurMap.put(actName, freqInRecord);
            }
        }
    }

    private void updateEventAttributeValueFreqMap(ATrace aTrace) {
        for(String key : aTrace.getEventAttributeValueFreqMap().keySet()) {

            if (this.eventAttributeValueCasesFreqMap.containsKey(key)) {
                UnifiedMap<String, Integer> valueFreqMapOfTrace = aTrace.getEventAttributeValueFreqMap().get(key);
                UnifiedMap<String, Integer> valCaseFreqMapOfLog = this.eventAttributeValueCasesFreqMap.get(key);
                for(String attrValue : valueFreqMapOfTrace.keySet()) {
                    if (valCaseFreqMapOfLog.keySet().contains(attrValue)) {
                        int cFreq = valCaseFreqMapOfLog.get(attrValue) + 1;
                        valCaseFreqMapOfLog.put(attrValue, cFreq);
                    } else {
                        valCaseFreqMapOfLog.put(attrValue, 1);
                    }
                    this.eventAttributeValueCasesFreqMap.put(key, valCaseFreqMapOfLog);
                }
            } else {
                UnifiedMap<String, Integer> valueCaseFreqMapOfLog = new UnifiedMap<>();
                UnifiedMap<String, Integer> valueFreqMapOfTrace = aTrace.getEventAttributeValueFreqMap().get(key);

                for(String attrValue : valueFreqMapOfTrace.keySet()) {
                    valueCaseFreqMapOfLog.put(attrValue, 1);
                }
                this.eventAttributeValueCasesFreqMap.put(key, valueCaseFreqMapOfLog);
            }


            if (this.eventAttributeValueFreqMap.containsKey(key)) {
                UnifiedMap<String, Integer> valueFreqMapOfTrace = aTrace.getEventAttributeValueFreqMap().get(key);
                UnifiedMap<String, Integer> valueFreqMapOfLog = this.eventAttributeValueFreqMap.get(key);


                for(String attrValue : valueFreqMapOfTrace.keySet()) {
                    int attrFreqOfTrace = valueFreqMapOfTrace.get(attrValue);
                    if(valueFreqMapOfLog.containsKey(attrValue)) {
                        int freqSum = valueFreqMapOfLog.get(attrValue) + attrFreqOfTrace;
                        valueFreqMapOfLog.put(attrValue, freqSum);
                    }else{
                        valueFreqMapOfLog.put(attrValue, attrFreqOfTrace);
                    }
                }
                this.eventAttributeValueFreqMap.put(key, valueFreqMapOfLog);
            }else{
                UnifiedMap<String, Integer> valueFreqMapOfLog = new UnifiedMap<>();
                UnifiedMap<String, Integer> valueFreqMapOfTrace = aTrace.getEventAttributeValueFreqMap().get(key);

                for(String attrValue : valueFreqMapOfTrace.keySet()) {
                    int attrFreqOfTrace = valueFreqMapOfTrace.get(attrValue);
                    valueFreqMapOfLog.put(attrValue, attrFreqOfTrace);
                }
                this.eventAttributeValueFreqMap.put(key, valueFreqMapOfLog);
            }
        }
//        System.out.println(eventAttributeValueFreqMap);
    }

//    private void updateCaseAttributeValueSetMap(ATrace aTrace) {
//        for(String key : aTrace.getAttributeMap().keySet()) {
//            String attrValue = aTrace.getAttributeMap().getById(key);
//            if (this.rawCaseAttributeValueSetMap.containsKey(key)) {
//                this.rawCaseAttributeValueSetMap.getById(key).put(attrValue);
//            } else {
//                UnifiedSet<String> vals = new UnifiedSet<>();
//                vals.put(key);
//                this.rawCaseAttributeValueSetMap.put(key, vals);
//            }
//        }
//    }

    private void updateCaseAttributeValueFreqMap(ATrace aTrace) {
        for(String key : aTrace.getAttributeMap().keySet()) {
            String attrValue = aTrace.getAttributeMap().get(key);
            if (this.caseAttributeValueFreqMap.containsKey(key)) {
                UnifiedMap<String, Integer> valueFreqMap = this.caseAttributeValueFreqMap.get(key);
                if(valueFreqMap.containsKey(attrValue)) {
                    int freq = valueFreqMap.get(attrValue) + 1;
                    valueFreqMap.put(attrValue, freq);
                    this.caseAttributeValueFreqMap.put(key, valueFreqMap);
                }else{
                    valueFreqMap.put(attrValue, 1);
                    this.caseAttributeValueFreqMap.put(key, valueFreqMap);
                }
            } else {
                UnifiedMap<String, Integer> valueFreqMap = new UnifiedMap<>();
                valueFreqMap.put(attrValue, 1);
                this.caseAttributeValueFreqMap.put(key, valueFreqMap);
            }
        }
    }

    public ATrace get(String caseId) {
        if(traceUnifiedMap.containsKey(caseId)) return this.traceUnifiedMap.get(caseId);
        else return null;
    }

    public UnifiedMap<String, ATrace> getTraceUnifiedMap() {
        return traceUnifiedMap;
    }

    private IntArrayList getIntArrayListOf(List<String> stringList) {
        IntArrayList ial = new IntArrayList();
        for(int i=0; i< stringList.size(); i++) {
            ial.add(this.actIdNameMap.inverse().get(stringList.get(i)));
        }
        return ial;
    }

    public int getUniqueActivitySize() {
        UnifiedSet<String> uniqueActNameSet = new UnifiedSet<>();
        for(int i=0; i<this.traceList.size(); i++) {
            List<AActivity> actList = this.traceList.get(i).getActivityList();
            for(int j=0; j<actList.size(); j++) {
                if(!uniqueActNameSet.contains(actList.get(j).getName())) {
                    uniqueActNameSet.put(actList.get(j).getName());
                }
            }
        }
        return uniqueActNameSet.size();
    }

    public UnifiedMap<Integer, Integer> getCaseVariantIdFrequencyMap() {
        return this.variantIdFreqMap;
    }

    public UnifiedMap<String, UnifiedMap<String, Integer>> getCaseAttributeValueFreqMap() { //2019-10-31
        return caseAttributeValueFreqMap;
    }

    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap() {
        return eventAttributeValueFreqMap;
    }

    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueCasesFreqMap() {
        return eventAttributeValueCasesFreqMap;
    }

    public List<String> getCaseAttributeNameList() {
        List<String> nameList = new ArrayList<>(caseAttributeValueFreqMap.keySet());
        Collections.sort(nameList);
        return nameList;
    }

    public long getEventSize() {
        int size = 0;
        for(ATrace aTrace : traceList) {
            size += aTrace.getEventSize();
        }
        return size;
    }

    public void setEventSize(int eventSize) {
        this.eventSize = eventSize;
    }

//    public UnifiedMap<String, UnifiedSet<String>> getRawEventAttributeValueSetMap() {
//        return rawEventAttributeValueSetMap;
//    }
//
//    public UnifiedMap<String, UnifiedSet<String>> getRawCaseAttributeValueSetMap() {
//        return rawCaseAttributeValueSetMap;
//    }

    public long getCaseVariantSize() {
        UnifiedSet<Integer> variSet = new UnifiedSet<>();
        for(ATrace aTrace : traceList) {
            if(!variSet.contains(aTrace.getCaseVariantId())) variSet.put(aTrace.getCaseVariantId());
        }
        return  variSet.size();
    }

    public void setCaseVariantSize(int caseVariantSize) {
        this.caseVariantSize = caseVariantSize;
    }

    public List<String> getActivityNameList(int caseVariantId) { //2019-10-31
        for(ATrace aTrace : traceList) {
            if(aTrace.getCaseVariantId() == caseVariantId) return aTrace.getActivityNameList();
        }
        return null;
    }

//    public UnifiedMap<Integer, Integer> getOriginalVariantIdFreqMap() {
//        return originalVariantIdFreqMap;
//    }

    public void setVariantIdFreqMap(UnifiedMap<Integer, Integer> variantIdFreqMap) {
        this.variantIdFreqMap = variantIdFreqMap;
    }

    @Override
    public UnifiedMap<Integer, Integer> getVariantIdFreqMap() {
        return variantIdFreqMap;
    }

//    public BitSet getValidTraceIndexBS() {
//        return validTraceIndexBS;
//    }

    public double getMinDuration() { //2019-10-16
        return minDuration;
    }

    public void setMinDuration(double minDuration) {
        this.minDuration = minDuration;
    }

    public double getMaxDuration() {//2019-10-16
        return maxDuration;
    }

    public void setMaxDuration(double maxDuration) {
        this.maxDuration = maxDuration;
    }

    @Override
    public List<ATrace> getImmutableTraces() {
        return traceList;
    }


    public List<ATrace> getTraceList() {
        return traceList;
    }

    public void setTraceList(List<ATrace> traceList) {
        this.traceList = traceList;
    }

    public UnifiedSet<String> getEventNameSet() {
        UnifiedSet<String> nameSet = new UnifiedSet<>();
        for(int i=0; i<this.traceList.size(); i++) {
            UnifiedSet<String> enSet = traceList.get(i).getEventNameSet();
            nameSet.addAll(enSet);
        }
        return nameSet;
    }

    public UnifiedSet<String> getEventAttributeNameSet() {
        UnifiedSet<String> validNames = new UnifiedSet<>();
        for(String key : this.eventAttributeValueFreqMap.keySet()) {
            int qty = this.eventAttributeValueFreqMap.get(key).size();
            if(qty < 100000) validNames.put(key);
        }
        return validNames;
    }

    public int size() {
        return this.traceList.size();
    }

    public ATrace get(int index) {
        return this.traceList.get(index);
    }

    @Override
    public ATrace getImmutable(int index) {
        return traceList.get(index);
    }

    @Override
    public int immutableSize() {
        return traceList.size();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTimeZone() { //2019-10-20
        return timeZone;
    }

    public String getMinDurationString() {
        return Util.durationShortStringOf(this.minDuration);
    }

    public String getMaxDurationString() {
        return Util.durationShortStringOf(this.maxDuration);
    }

    public HashBiMap<Integer, String> getActIdNameMap() {
        return actIdNameMap;
    }

    public double getAverageDuration() {
        double durSum = 0;
        for(int i=0; i < traceList.size(); i++) {
            durSum += traceList.get(i).getDuration();
        }
        double avgDur = durSum / traceList.size();
        return avgDur;
    }

    public double getMedianDuration() {
        List<Double> durList = new ArrayList<>();
        for (int i=0; i<traceList.size(); i++) {
            durList.add(traceList.get(i).getDuration());
        }
        Collections.sort(durList);
        int medianIndex = traceList.size() / 2;
        return durList.get(medianIndex);
    }

    public String getAverageDurationString() {
        long durSum = 0;
        for(int i=0; i < traceList.size(); i++) {
            durSum += traceList.get(i).getDuration();
        }
        long avgDur = durSum / traceList.size();
        return Util.durationShortStringOf(avgDur);
    }



    public String getMedianDurationString() {
        List<Double> durList = new ArrayList<>();
        for (int i=0; i<traceList.size(); i++) {
            durList.add(traceList.get(i).getDuration());
        }
        Collections.sort(durList);
        int medianIndex = traceList.size() / 2;
        return Util.durationShortStringOf(durList.get(medianIndex));
    }

    public String getStartTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(this.startTime));
    }

    public String getEndTimeString() {
        return Util.timestampStringOf(Util.millisecondToZonedDateTime(this.endTime));
    }


    private void resetDuration() {
        this.minDuration = -1;
        this.maxDuration = -1;
        for(int i=0; i<traceList.size(); i++) {
            ATrace aTrace = traceList.get(i);
            if(this.minDuration == -1 || aTrace.getDuration() < minDuration) minDuration = aTrace.getDuration();
            if(this.maxDuration == -1 || aTrace.getDuration() > maxDuration) maxDuration = aTrace.getDuration();
        }
//        System.out.println(maxDuration);
    }

    public APMLogImpl(List<ATrace> traceList,
                      UnifiedMap<Integer, Integer> variantIdFreqMap,
                      HashBiMap<Integer, String> actIdNameMap,
                      UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueCasesFreqMap,
                      UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap,
                      UnifiedMap<String, UnifiedMap<String, Integer>> caseAttributeValueFreqMap,
                      UnifiedMap<String, ATrace> traceUnifiedMap,
                      double minDuration,
                      double maxDuration,
                      String timeZone,
                      long startTime,
                      long endTime,
                      long caseVariantSize,
                      long eventSize,
                      ActivityNameMapper activityNameMapper,
                      UnifiedMap<String, Integer> activityMaxOccurMap) {
        this.traceList = traceList;
        this.variantIdFreqMap = variantIdFreqMap;
        this.actIdNameMap = actIdNameMap;
        this.eventAttributeValueCasesFreqMap = eventAttributeValueCasesFreqMap;
        this.eventAttributeValueFreqMap = eventAttributeValueFreqMap;
        this.caseAttributeValueFreqMap = caseAttributeValueFreqMap;
        this.traceUnifiedMap = traceUnifiedMap;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.timeZone = timeZone;
        this.startTime = startTime;
        this.endTime = endTime;
        this.caseVariantSize = caseVariantSize;
        this.eventSize = eventSize;
        this.activityNameMapper = activityNameMapper;
        this.activityMaxOccurMap = activityMaxOccurMap;
        defaultChartDataCollection = new DefaultChartDataCollection(this);
    }


    public APMLogImpl clone() {

        UnifiedMap<String, ATrace> traceUnifiedMapForClone = new UnifiedMap<>();

        List<ATrace> traceListForClone = new ArrayList<>();

        for (int i = 0; i < this.traceList.size(); i++) {
            ATrace aTrace = this.traceList.get(i).clone();
            traceUnifiedMapForClone.put(aTrace.getCaseId(), aTrace);
            traceListForClone.add(aTrace);
        }

        UnifiedMap<Integer, Integer> variIdFreqMapForClone = new UnifiedMap<>();

        for (int key : this.variantIdFreqMap.keySet()) {
            variIdFreqMapForClone.put(key, this.variantIdFreqMap.get(key));
        }

        HashBiMap<Integer, String> actIdNameMapForClone = new HashBiMap<>();

        for (int key : this.actIdNameMap.keySet()) {
            actIdNameMapForClone.put(key, this.actIdNameMap.get(key));
        }

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


        APMLogImpl apmLogClone = new APMLogImpl(traceListForClone,
                variIdFreqMapForClone,
                actIdNameMapForClone,
                eventAttrValCasesFreqMapForClone,
                eventAttrValFreqMapForClone,
                caseAttrValFreqMapForClone,
                traceUnifiedMapForClone,
                this.minDuration,
                this.maxDuration,
                this.timeZone,
                this.startTime,
                this.endTime,
                this.caseVariantSize,
                this.eventSize,
                this.activityNameMapper,
                activityMaxOccurMapForClone);

        return apmLogClone;
    }

    public XLog toXLog() {
        return APMLogToXLog.getXLog(this);
    }

    @Override
    public AAttributeGraph getAAttributeGraph() {
        return null;
    }

    @Override
    public UnifiedMap<String, UnifiedMap<String, UnifiedSet<AActivity>>> getEventAttributeOccurMap() {
        return null;
    }

    @Override
    public void add(ATrace trace) {

    }

    @Override
    public HashBiMap<String, Integer> getActivityNameBiMap() {
        return null;
    }

    @Override
    public void setActivityNameBiMap(HashBiMap<String, Integer> activityNameBiMap) {

    }


}
