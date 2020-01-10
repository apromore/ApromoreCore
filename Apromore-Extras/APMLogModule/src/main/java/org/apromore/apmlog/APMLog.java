package org.apromore.apmlog;

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
import java.util.*;

import static java.util.Map.Entry.comparingByValue;

public class APMLog  {

    private List<ATrace> traceList;
    private UnifiedMap<Integer, Integer> variantIdFreqMap;
    private HashBiMap<Integer, String> actIdNameMap;
    private UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap;
    private UnifiedMap<String, UnifiedMap<String, Integer>> caseAttributeValueFreqMap;
    private long minDuration = 0;
    private long maxDuration = 0;
    private String timeZone = "";
    private long startTime = -1;
    private long endTime = -1;
    private long caseVariantSize = 0;
    private long eventSize = 0;

    private UnifiedMap<String, ATrace> traceUnifiedMap;


    public APMLog(XLog xLog) {
        traceList = new ArrayList<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();
        caseAttributeValueFreqMap = new UnifiedMap<>();

        initData(xLog);
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
        for(XTrace xTrace : xLog) {
            for(int i=0; i < xTrace.size(); i++) {
                XEvent xEvent = xTrace.get(i);
                String conceptName = "";
                if(xEvent.getAttributes().get("concept:name") != null) {
                    conceptName = xEvent.getAttributes().get("concept:name").toString();
                }
                if(!actIdNameMap.containsValue(conceptName)) {
                    actIdNameMap.put(actIdCount, conceptName);
                    actIdCount += 1;
                }
            }
        }

        boolean containsVariantId = false;

        int tempVariId = 1;

        for(int i=0; i<xLog.size(); i++) {
            ATrace aTrace = new ATrace(xLog.get(i));

            traceUnifiedMap.put(aTrace.getCaseId(), aTrace);

            eventSize += aTrace.getEventSize();

            if(startTime == -1 || aTrace.getStartTimeMilli() < startTime) {
                startTime = aTrace.getStartTimeMilli();
            }
            if(endTime == -1 || aTrace.getEndTimeMilli() > endTime) {
                endTime = aTrace.getEndTimeMilli();
            }
            if(this.timeZone.equals("")) this.timeZone = aTrace.get(0).getTimeZone();

            /**
             * Event attributes
             */
            updateEventAttributeValueFreqMap(aTrace);

            /**
             * Case attributes
             */
            updateCaseAttributeValueFreqMap(aTrace);

            if(this.minDuration == 0 || aTrace.getDuration() < minDuration) {
                minDuration = aTrace.getDuration();
            }
            if(this.maxDuration == 0 || aTrace.getDuration() > maxDuration) {
                maxDuration = aTrace.getDuration();
            }


            this.traceList.add(aTrace);

            int variId = aTrace.getCaseVariantId();
            List<String> actNameList = aTrace.getActivityNameList();
            IntArrayList idList = getIntArrayListOf(actNameList);
            if(variId > 0) {
                containsVariantId = true;
                if (variantIdFreqMap.containsKey(variId)) {
                    int freq = variantIdFreqMap.get(variId) + 1;
                    variantIdFreqMap.put(variId, freq);
                } else {
                    variantIdFreqMap.put(variId, 1);
                }
                if(!variantIdActIdListMap.containsKey(variId)) {
                    variantIdActIdListMap.put(variId, idList);
                }
            } else { // does not have variant ID
                if(actIdListFreqMap.containsKey(idList)) {
                    int freq = actIdListFreqMap.get(idList) + 1;
                    actIdListFreqMap.put(idList, freq);
                } else {
                    actIdListFreqMap.put(idList, 1);
                }

                if(!tempActIdListToVIdMap.containsKey(idList)) {
                    tempActIdListToVIdMap.put(idList, tempVariId);
                    tempVariId += 1;
                }

                int vId = tempActIdListToVIdMap.get(idList);
                aTrace.setCaseVariantId(vId);
            }
        }




        UnifiedMap<Integer, Integer> initVIdToFinalVIdMap = new UnifiedMap<>();

        if(variantIdFreqMap.size() < 1 && actIdListFreqMap.size() > 0) {
            List<Map.Entry<IntArrayList, Integer>> list =
                    new ArrayList<>(actIdListFreqMap.entrySet());
            list.sort(comparingByValue());
            int idNum = 1;
            for(int i=(list.size()-1); i>=0; i--) {
                variantIdFreqMap.put(idNum, list.get(i).getValue());
//                originalVariantIdFreqMap.put(idNum, list.get(i).getValue());//2019-11-10
                if(!variantIdActIdListMap.containsKey(idNum)) {
                    variantIdActIdListMap.put(idNum, list.get(i).getKey());
                    int initVId = tempActIdListToVIdMap.get(list.get(i).getKey());
                    initVIdToFinalVIdMap.put(initVId, idNum);
                }
                idNum += 1;
            }
        }

        if(!containsVariantId) {
            for(int i=0; i < this.traceList.size(); i++) {
                ATrace aTrace = this.traceList.get(i);
                int iVId = aTrace.getCaseVariantId();
                int finalVId = initVIdToFinalVIdMap.get(iVId);
                aTrace.setCaseVariantId(finalVId);
            }
        }

        for(int i=0; i < xLog.size(); i++) {
            XTrace xTrace = xLog.get(i);
            ATrace aTrace = traceList.get(i);
            int variId = aTrace.getCaseVariantId();
            XAttribute attribute = new XAttributeLiteralImpl("case:variant", Integer.toString(variId));
            xTrace.getAttributes().put("case:variant", attribute);
        }

        caseVariantSize = variantIdFreqMap.size();

    }

    private void updateEventAttributeValueFreqMap(ATrace aTrace) {
        for(String key : aTrace.getEventAttributeValueFreqMap().keySet()) {
            if (this.eventAttributeValueFreqMap.containsKey(key)) {
                UnifiedMap<String, Integer> valueFreqMapOfTrace =
                        aTrace.getEventAttributeValueFreqMap().get(key);
                UnifiedMap<String, Integer> valueFreqMapOfLog =
                        this.eventAttributeValueFreqMap.get(key);
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
                UnifiedMap<String, Integer> valueFreqMapOfTrace =
                        aTrace.getEventAttributeValueFreqMap().get(key);

                for(String attrValue : valueFreqMapOfTrace.keySet()) {
                    int attrFreqOfTrace = valueFreqMapOfTrace.get(attrValue);
                    valueFreqMapOfLog.put(attrValue, attrFreqOfTrace);
                }
                this.eventAttributeValueFreqMap.put(key, valueFreqMapOfLog);
            }
        }
    }

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

    public List<String> getCaseAttributeNameList() {
        List<String> nameList = new ArrayList<>(caseAttributeValueFreqMap.keySet());
        Collections.sort(nameList);
        return nameList;
    }

    public int getEventSize() {
        int size = 0;
        for(ATrace aTrace : traceList) {
            size += aTrace.getEventSize();
        }
        return size;
    }

    public void setEventSize(int eventSize) {
        this.eventSize = eventSize;
    }

    public int getCaseVariantSize() {
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

//    public BitSet getValidTraceIndexBS() {
//        return validTraceIndexBS;
//    }

    public long getMinDuration() { //2019-10-16
        return minDuration;
    }

    public void setMinDuration(long minDuration) {
        this.minDuration = minDuration;
    }

    public long getMaxDuration() {//2019-10-16
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
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

    public long getAverageDuration() {
        long durSum = 0;
        for(int i=0; i < traceList.size(); i++) {
            durSum += traceList.get(i).getDuration();
        }
        long avgDur = durSum / traceList.size();
        return avgDur;
    }

    public long getMedianDuration() {
        List<Long> durList = new ArrayList<>();
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
        List<Long> durList = new ArrayList<>();
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
        System.out.println(maxDuration);
    }

    public APMLog(List<ATrace> traceList,
                  UnifiedMap<Integer, Integer> variantIdFreqMap,
                  HashBiMap<Integer, String> actIdNameMap,
                  UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap,
                  UnifiedMap<String, UnifiedMap<String, Integer>> caseAttributeValueFreqMap,
                  UnifiedMap<String, ATrace> traceUnifiedMap,
                  long minDuration,
                  long maxDuration,
                  String timeZone,
                  long startTime,
                  long endTime,
                  long caseVariantSize,
                  long eventSize) {
        this.traceList = traceList;
        this.variantIdFreqMap = variantIdFreqMap;
        this.actIdNameMap = actIdNameMap;
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
    }
}
