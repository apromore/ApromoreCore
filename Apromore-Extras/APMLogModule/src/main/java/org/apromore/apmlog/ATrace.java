package org.apromore.apmlog;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class ATrace {

    private String caseId = "";
    private int caseVariantId = 0;
    private long startTimeMilli = 0;
    private long endTimeMilli = 0;
    private long duration = 0;
    private boolean hasActivity = false;
    private long totalProcessingTime = 0;
    private long averageProcessingTime = 0;
    private long maxProcessingTime = 0;
    private long totalWaitingTime = 0;
    private long averageWaitingTime = 0;
    private long maxWaitingTime = 0;
    private double caseUtilization = 0.0;

    private List<AActivity> activityList;
    private List<AEvent> eventList;
    private UnifiedMap<String, UnifiedMap<String, Integer>> eventAttributeValueFreqMap; // 2019-10-24
    private UnifiedMap<String, String> attributeMap;
    private List<String> activityNameList;
    private UnifiedSet<String> eventNameSet;
    private BitSet validEventIndex;
    private long originalStartTimeMilli;
    private long originalEndTimeMilli;
    private long originalDuration = 0;
    private boolean originalHasActivity = false;
    private long originalTotalProcessingTime = 0;
    private long originalAverageProcessingTime = 0;
    private long originalMaxProcessingTime = 0;
    private long originalTotalWaitingTime = 0;
    private long originalAverageWaitingTime = 0;
    private long originalMaxWaitingTime = 0;
    private double originalCaseUtilization = 0;
    private List<AActivity> originalActivityList;
    private List<AEvent> originalEventList;
    private UnifiedMap<String, UnifiedMap<String, Integer>> originalEventAttributeValueFreqMap;
    private UnifiedMap<String, String> originalAttributeMap;
    private List<String> originalActivityNameList;
    private UnifiedSet<String> originalEventNameSet;

    public ATrace(XTrace xTrace) {

        activityList = new ArrayList<>();
        eventList = new ArrayList<>();
        eventAttributeValueFreqMap = new UnifiedMap<>();
        attributeMap = new UnifiedMap<>();

        XAttributeMap xAttributeMap = xTrace.getAttributes();
        for(String key : xAttributeMap.keySet()) {
            if(key.toLowerCase().equals("concept:name")) this.caseId = xAttributeMap.get(key).toString();
            else {
                this.attributeMap.put(key, xAttributeMap.get(key).toString());
            }
        }
        if(xTrace.getAttributes().containsKey("case:variant")) caseVariantId = new Integer(xTrace.getAttributes().get("case:variant").toString());
        // ELSE SET THE VARIANT ID from APMLog
        initStats(xTrace);
    }

    private void initStats(XTrace xTrace) {

        activityNameList = new ArrayList<>();
        eventNameSet = new UnifiedSet<>();

        // initiate the bitSet with fixed size
        validEventIndex = new BitSet(xTrace.size());

        if(containsActivity(xTrace)) {
            long waitCount = 0;
            long processCount = 0;

            UnifiedSet<XEvent> markedXEvent = new UnifiedSet<>();

            for(int i=0; i<xTrace.size(); i++) {
                XEvent xEvent = xTrace.get(i);
                AEvent iAEvent = new AEvent(xEvent);

                validEventIndex.set(i, true);

                long eventTime = iAEvent.getTimestampMilli();
                if(startTimeMilli == 0 || startTimeMilli > eventTime) {
                    startTimeMilli = eventTime;
                }
                if(endTimeMilli == 0 || endTimeMilli < eventTime) {
                    endTimeMilli = eventTime;
                }


                this.eventList.add(iAEvent);

                this.eventNameSet.put(iAEvent.getName());

                fillEventAttributeValueFreqMap(iAEvent);

                if(iAEvent.getLifecycle().equals("start")) {
                    String startEventName = iAEvent.getName();

                    /**
                     * Find the waiting time
                     */
                    long iTime = iAEvent.getTimestampMilli();

                    if(i > 0) {
                        for(int j=(i-1); j >=0; j--) {
                            XEvent preEvent = xTrace.get(j);
                            AEvent preAEvent = new AEvent(preEvent);
                            if(preAEvent.getLifecycle().equals("complete")) {
                                long preTime = preAEvent.getTimestampMilli();
                                if(iTime > preTime) {
                                    long waitingTime = iTime - preTime;
                                    this.totalWaitingTime += waitingTime;
                                    waitCount += 1;
                                    if(waitingTime > this.maxWaitingTime) {
                                        this.maxWaitingTime = waitingTime;
                                    }
                                }
                            }
                        }
                    }

                    boolean hasComplete = false;

                    /**
                     * Find the duration
                     */
                    if((i+1) <= (xTrace.size()-1)) {
                        for(int j=(i+1); j < xTrace.size(); j++) {
                            AEvent jAEvent = new AEvent(xTrace.get(j));
                            if(jAEvent.getName().equals(startEventName) &&
                                    jAEvent.getLifecycle().equals("complete")) {

                                long endTime = jAEvent.getTimestampMilli();
                                if(endTime > iTime) {
                                    long processTime = endTime - iTime;
                                    this.totalProcessingTime += processTime;
                                    if(processTime > this.maxProcessingTime) this.maxProcessingTime = processTime;
                                    processCount += 1;

                                    List<AEvent> aEventList = new ArrayList<>();
                                    aEventList.add(iAEvent);
                                    aEventList.add(jAEvent);
                                    AActivity aActivity = new AActivity(aEventList);
                                    this.activityList.add(aActivity);
                                    this.activityNameList.add(aActivity.getName());
                                    markedXEvent.put(xTrace.get(j));
                                    hasComplete = true;
                                }

                                break;
                            }
                        }
                    }

                    if(!hasComplete) { //consider as instant event
                        List<AEvent> aEventList = new ArrayList<>();
                        aEventList.add(iAEvent);
                        AActivity aActivity = new AActivity(aEventList);
                        this.activityList.add(aActivity);
                        this.activityNameList.add(aActivity.getName());
                    }
                }


                if(iAEvent.getLifecycle().equals("complete") && !markedXEvent.contains(xEvent)) {
                    List<AEvent> aEventList = new ArrayList<>();
                    aEventList.add(iAEvent);
                    AActivity aActivity = new AActivity(aEventList);
                    this.activityList.add(aActivity);
                    this.activityNameList.add(aActivity.getName());
                }
            }
            if(this.totalProcessingTime > 0 && processCount > 0) this.averageProcessingTime = this.totalProcessingTime / processCount;
            if(this.totalWaitingTime > 0 && waitCount > 0) this.averageWaitingTime = this.totalWaitingTime / waitCount;
        }else{
            for(int i=0; i<xTrace.size(); i++) {
                XEvent xEvent = xTrace.get(i);
                AEvent iAEvent = new AEvent(xEvent);

                validEventIndex.set(i, true);

                long eventTime = iAEvent.getTimestampMilli();
                if(startTimeMilli == 0 || startTimeMilli > eventTime) startTimeMilli = eventTime;
                if(endTimeMilli == 0 || endTimeMilli < eventTime) endTimeMilli = eventTime;

                this.eventList.add(iAEvent);
                this.eventNameSet.put(iAEvent.getName());

                fillEventAttributeValueFreqMap(iAEvent);

                if(!iAEvent.getLifecycle().equals("")) {
                    List<AEvent> aEventList = new ArrayList<>();
                    aEventList.add(iAEvent);
                    AActivity aActivity = new AActivity(aEventList);
                    this.activityList.add(aActivity);
                    this.activityNameList.add(aActivity.getName());
                }

            }
        }


        if(endTimeMilli > startTimeMilli) {
            this.duration = endTimeMilli - startTimeMilli;
            if(containsActivity(xTrace)) {
                this.caseUtilization = (double) this.totalProcessingTime / this.duration;
            }else{
                this.caseUtilization = 1.0;
            }
        }

        originalStartTimeMilli = startTimeMilli;
        originalEndTimeMilli = endTimeMilli;
        originalDuration = duration;
        originalHasActivity = hasActivity;
        originalTotalProcessingTime = totalProcessingTime;
        originalAverageProcessingTime = averageProcessingTime;
        originalMaxProcessingTime = maxProcessingTime;
        originalTotalWaitingTime = totalWaitingTime;
        originalAverageWaitingTime = averageWaitingTime;
        originalMaxWaitingTime = maxWaitingTime;
        originalCaseUtilization = caseUtilization;
        originalActivityList = activityList;
        originalEventList = eventList;
        originalEventAttributeValueFreqMap = eventAttributeValueFreqMap;
        originalAttributeMap = attributeMap;
        originalActivityNameList = activityNameList;
        originalEventNameSet = eventNameSet;

    }

    public void reset() {

        for(int i=0; i < validEventIndex.size(); i++) {
            validEventIndex.set(i, true);
        }

        startTimeMilli = originalStartTimeMilli;
        endTimeMilli = originalEndTimeMilli;
        duration = originalDuration;
        hasActivity = originalHasActivity;
        totalProcessingTime = originalTotalProcessingTime;
        averageProcessingTime = originalAverageProcessingTime;
        maxProcessingTime = originalMaxProcessingTime;
        totalWaitingTime = originalTotalWaitingTime;
        averageWaitingTime = originalAverageWaitingTime;
        maxWaitingTime = originalMaxWaitingTime;
        caseUtilization = originalCaseUtilization;

        this.activityList = originalActivityList;
        this.eventList = originalEventList;
        this.eventAttributeValueFreqMap = originalEventAttributeValueFreqMap;
        this.attributeMap = originalAttributeMap;
        this.activityNameList = originalActivityNameList;
        this.eventNameSet = originalEventNameSet;
    }

    public void update() {
        this.eventList = new ArrayList<>();
        for(int i=0; i < this.originalEventList.size(); i++) {
            if(validEventIndex.get(i) == true) eventList.add(this.originalEventList.get(i));
        }

        long waitCount = 0;
        long processCount = 0;

        this.activityList = new ArrayList<>();
        this.eventAttributeValueFreqMap = new UnifiedMap<>();
        this.attributeMap = new UnifiedMap<>();
        this.activityNameList = new ArrayList<>();
        this.eventNameSet = new UnifiedSet<>();

        UnifiedSet<AEvent> markedEvent = new UnifiedSet<>();

        for(int i=0; i<this.eventList.size(); i++) {

            AEvent iAEvent = this.eventList.get(i);

            long eventTime = iAEvent.getTimestampMilli();
            if(startTimeMilli == 0 || startTimeMilli > eventTime) {
                startTimeMilli = eventTime;
            }
            if(endTimeMilli == 0 || endTimeMilli < eventTime) {
                endTimeMilli = eventTime;
            }

            this.eventNameSet.put(iAEvent.getName());

            fillEventAttributeValueFreqMap(iAEvent);

            if(iAEvent.getLifecycle().equals("start")) {
                String startEventName = iAEvent.getName();

                /**
                 * Find the waiting time
                 */
                long iTime = iAEvent.getTimestampMilli();

                if(i > 0) {
                    for(int j=(i-1); j >=0; j--) {
                        AEvent preAEvent = this.eventList.get(j);
                        if(preAEvent.getLifecycle().equals("complete")) {
                            long preTime = preAEvent.getTimestampMilli();
                            if(iTime > preTime) {
                                long waitingTime = iTime - preTime;
                                this.totalWaitingTime += waitingTime;
                                waitCount += 1;
                                if(waitingTime > this.maxWaitingTime) {
                                    this.maxWaitingTime = waitingTime;
                                }
                            }
                        }
                    }
                }

                boolean hasComplete = false;

                /**
                 * Find the duration
                 */
                if((i+1) <= (this.eventList.size()-1)) {
                    for(int j=(i+1); j < this.eventList.size(); j++) {
                        AEvent jAEvent = this.eventList.get(j);
                        if(jAEvent.getName().equals(startEventName) &&
                                jAEvent.getLifecycle().equals("complete")) {

                            long endTime = jAEvent.getTimestampMilli();
                            if(endTime > iTime) {
                                long processTime = endTime - iTime;
                                this.totalProcessingTime += processTime;
                                if(processTime > this.maxProcessingTime) this.maxProcessingTime = processTime;
                                processCount += 1;

                                List<AEvent> aEventListForAct = new ArrayList<>();
                                aEventListForAct.add(iAEvent);
                                aEventListForAct.add(jAEvent);
                                AActivity aActivity = new AActivity(aEventListForAct);
                                this.activityList.add(aActivity);
                                markedEvent.put(jAEvent);
                                hasComplete = true;
                            }

                            break;
                        }
                    }
                }

                if(!hasComplete) { //consider as instant event
                    List<AEvent> aEventListForAct = new ArrayList<>();
                    aEventListForAct.add(iAEvent);
                    AActivity aActivity = new AActivity(aEventListForAct);
                    this.activityList.add(aActivity);
                    this.activityNameList.add(aActivity.getName());
                }
            }


            if(iAEvent.getLifecycle().equals("complete") && !markedEvent.contains(iAEvent)) {
                List<AEvent> aEventListForAct = new ArrayList<>();
                aEventListForAct.add(iAEvent);
                AActivity aActivity = new AActivity(aEventListForAct);
                this.activityList.add(aActivity);
                this.activityNameList.add(aActivity.getName());
            }
        }
        if(this.totalProcessingTime > 0 && processCount > 0) this.averageProcessingTime = this.totalProcessingTime / processCount;
        if(this.totalWaitingTime > 0 && waitCount > 0) this.averageWaitingTime = this.totalWaitingTime / waitCount;
    }

    private void fillEventAttributeValueFreqMap(AEvent aEvent) { //2019-10-31
        for(String key : aEvent.getAttributeMap().keySet()) {
            String iAValue = aEvent.getAttributeMap().get(key);
            if (this.eventAttributeValueFreqMap.containsKey(key)) {
                UnifiedMap<String, Integer> valueFreqMap = this.eventAttributeValueFreqMap.get(key);
                if(valueFreqMap.containsKey(iAValue)) {
                    int freq = valueFreqMap.get(iAValue) + 1;
                    valueFreqMap.put(iAValue, freq);
                    this.eventAttributeValueFreqMap.put(key, valueFreqMap);
                }else{
                    valueFreqMap.put(iAValue, 1);
                    this.eventAttributeValueFreqMap.put(key, valueFreqMap);
                }
            }else{
                UnifiedMap<String, Integer> valueFreqMap = new UnifiedMap<>();
                valueFreqMap.put(iAValue, 1);
                this.eventAttributeValueFreqMap.put(key, valueFreqMap);
            }
        }
    }

    public UnifiedMap<String, UnifiedMap<String, Integer>> getEventAttributeValueFreqMap() {
        return eventAttributeValueFreqMap;
    }

    private boolean containsActivity(XTrace xTrace) {
        for(int i=0; i<xTrace.size(); i++) {
            AEvent iEvent = new AEvent(xTrace.get(i));
            if(iEvent.getLifecycle().equals("start")) {
                this.hasActivity = true;
                return true;
            }
        }
        return false;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseVariantId(int caseVariantId) {
        this.caseVariantId = caseVariantId;
    }

    public int getCaseVariantId() {
        return caseVariantId;
    }

    public int getEventSize() {
        return this.eventList.size();
    }

    public long getStartTimeMilli() {
        return startTimeMilli;
    }

    public long getEndTimeMilli() {
        return endTimeMilli;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isHasActivity() {
        return hasActivity;
    }

    public List<AActivity> getActivityList() {
        return activityList;
    }

    public List<String> getActivityNameList() {
        return this.activityNameList;
    }

    public UnifiedSet<String> getEventNameSet() {
        return this.eventNameSet;
    }

    public UnifiedMap<String, String> getAttributeMap() {
        return attributeMap;
    }

    public List<AEvent> getEventList() {
        return eventList;
    }

    public int size() {
        return this.eventList.size();
    }

    public AEvent get(int index) {
        return this.eventList.get(index);
    }

    public long getTotalProcessingTime() {
        return totalProcessingTime;
    }

    public long getAverageProcessingTime() {
        return averageProcessingTime;
    }

    public long getMaxProcessingTime() {
        return maxProcessingTime;
    }

    public long getTotalWaitingTime() {
        return totalWaitingTime;
    }

    public long getAverageWaitingTime() {
        return averageWaitingTime;
    }

    public long getMaxWaitingTime() {
        return maxWaitingTime;
    }

    public double getCaseUtilization() {
        return caseUtilization;
    }

    public BitSet getValidEventIndex() {
        return validEventIndex;
    }
}
