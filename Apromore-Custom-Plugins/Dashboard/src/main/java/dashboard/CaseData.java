package dashboard;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.zkoss.zul.ListModelArray;

import java.time.ZonedDateTime;
import java.util.*;

public class CaseData {

    private String logName = "";
    private XLog xLog;

    private List<Case> caseList = new ArrayList<Case>();

    ListModelArray lmaCases;
    ListModelArray lmaVariants;

    private long duration = 0;
    private long startTimeMilli = 0;
    private long endTimeMilli = 0;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private List<ZonedDateTime> timestamps = new ArrayList<ZonedDateTime>();

    public CaseData(String logName, XLog xLog){
        this.logName = logName;
        this.xLog = xLog;
        setData();
    }

    private void setData(){



        /**
         * K: variant ID, V: eventNameSequence
         */
        HashMap<Integer, List<String>> variantEventsHM =
                new HashMap<Integer, List<String>>();

        HashMap<Integer, Integer> variantCasesHM =
                new HashMap<Integer, Integer>();

        HashMap<Integer, List<Long>> variantDurationsHM =
                new HashMap<Integer, List<Long>>();


        int vID = 1;

        for(int i=0; i<xLog.size();i++) {
            XTrace xTrace = xLog.get(i);
            Case c = new Case(xTrace);

            if(c.getEventsSize() > 0) {
                List<String> eventSeq = c.getEventSequence();

                int matchedVID = 0;
                if(variantEventsHM.size() > 0){
                    for(Integer key : variantEventsHM.keySet()){
                        List<String> ls = variantEventsHM.get(key);
                        if(ls.equals(eventSeq)) {
                            matchedVID = key;
                            break;
                        }
                    }
                }
                if(matchedVID ==0) { // the eventSequence is new to the HM
                    matchedVID = vID;
                    variantEventsHM.put(matchedVID, eventSeq);
                    vID += 1; // increase the number for next new event sequence
                }
                if(variantCasesHM.containsKey(matchedVID)){
                    int y = variantCasesHM.get(matchedVID) + 1;
                    variantCasesHM.put(matchedVID, y);
                }else{
                    variantCasesHM.put(matchedVID, 1);
                }
                c.setVariantId(matchedVID);
                caseList.add(c);
                if(variantDurationsHM.containsKey(matchedVID)){
                    List<Long> durList = variantDurationsHM.get(matchedVID);
                    durList.add(c.getDuration());
                    variantDurationsHM.put(matchedVID, durList);
                }else{
                    List<Long> durList = new ArrayList<Long>();
                    durList.add(c.getDuration());
                    variantDurationsHM.put(matchedVID, durList);
                }
            }
        }

//        System.out.println(caseList);
//        System.out.println(variantEventsHM);
//        System.out.println(variantCasesHM);
//        System.out.println(variantDurationsHM);


        List<Variant> variantList = new ArrayList<Variant>();

        for(Integer key : variantCasesHM.keySet()) {
            int numCases = variantCasesHM.get(key);
            int numEvents = variantEventsHM.get(key).size();
            List<Long> durList = variantDurationsHM.get(key);
            long medDur = medianDurationOf(durList);
            long meaDur = meanDurationOf(durList);
            Variant v = new Variant(key, numCases, numEvents, medDur, meaDur);
            variantList.add(v);
        }

        System.out.println(variantList.toString());

        lmaCases = new ListModelArray(
                caseList.toArray(new Case[caseList.size()]));

        lmaVariants = new ListModelArray(
                variantList.toArray(new Variant[variantList.size()]));

        /*
            Get all timestamps
         */
        long earlisetTime = 0;
        long lastestTime = 0;
        for(int i=0; i<caseList.size();i++) {
            Case c = caseList.get(i);
            long sTime = c.getStartTimeMilli();
            long eTime = c.getEndTimeMilli();
            if(earlisetTime ==0 || sTime <= earlisetTime) {
                earlisetTime = sTime;
                startTime = c.getStartTime();
            }
            if(lastestTime ==0 || eTime >= lastestTime) {
                lastestTime = eTime;
                endTime = c.getEndTime();
            }
        }

        startTimeMilli = Util.epochMilliOf(startTime);
        endTimeMilli = Util.epochMilliOf(endTime);

        timestamps = Util.allTimestampsOf(xLog);

        Collections.sort(timestamps);

        duration = endTimeMilli - startTimeMilli;
    }


    public static long medianDurationOf(List<Long> durationList){

        Collections.sort(durationList);

        double median = 0;
        double pos1 = Math.floor((durationList.size() - 1.0) / 2.0);
        double pos2 = Math.ceil((durationList.size() - 1.0) / 2.0);
        if (pos1 == pos2 ) {
            median = durationList.get((int)pos1);
        } else {
            median = (durationList.get((int)pos1) + durationList.get((int)pos2)) / 2.0;
        }
        return new Double(median).longValue();
    }

    public static long meanDurationOf(List<Long> durationList){
        long sum = 0;

        for(int i=0; i<durationList.size();i++){
            long dur = durationList.get(i);
            sum+=dur;
        }
        long result = sum / durationList.size();
        return result;
    }

    public int getNumberOfEvents(){
        int num = 0;
//        for(int i=0; i<xLog.size();i++){
//            XTrace xTrace = xLog.get(i);
//            for(int j=0; j<xTrace.size(); j++){
//                XEvent xEvent = xTrace.get(j);
//                String life = "";
//                if(xEvent.getAttributes().containsKey("lifecycle:transition")){
//                    life = xEvent.getAttributes().get(
//                            "lifecycle:transition").toString().toLowerCase();
//                }
//                if(life.equals("complete")){
//                    num += 1;
//                }
//            }
//        }
//        System.out.println(num);
        for(int i=0; i<caseList.size();i++) {
            num += caseList.get(i).getEventsSize();
        }
        return num;
    }


    public int getNumberOfCases(){
        return caseList.size();
    }

    public int getNumberOfActivities(){
        return Util.allActivityNamesOf(xLog).size();
    }

    public long getMedianCaseDuration(){
        List<Long> caseDurations = new ArrayList<Long>();
        int numOfCases = xLog.size();
        for(int i=0; i<xLog.size(); i++){
//            caseDurations.add(new Double(traces.get(i).getDuration()));
            caseDurations.add(Util.durationOf(xLog.get(i)).toMillis());
        }
        Collections.sort(caseDurations);
        double median = 0;
        double pos1 = Math.floor((caseDurations.size() - 1.0) / 2.0);
        double pos2 = Math.ceil((caseDurations.size() - 1.0) / 2.0);
        if (pos1 == pos2 ) {
            median = caseDurations.get((int)pos1);
        } else {
            median = (caseDurations.get((int)pos1) + caseDurations.get((int)pos2)) / 2.0;
        }
        return new Double(median).longValue();
    }

    public long getMeanCaseDuration(){
        long totalDuration = 0;
        for(int i=0; i<xLog.size();i++){
//            totalDuration += traces.get(i).getDuration();
            totalDuration += Util.durationOf(xLog.get(i)).toMillis();
        }
        totalDuration = totalDuration / xLog.size();
        return totalDuration;
    }

//    public ZonedDateTime getStartTimestamp(){
//        ZonedDateTime earliestZDT = null;
//        for(int i=0; i<xLog.size(); i++){
//            XTrace xTrace = xLog.get(i);
//            ZonedDateTime traceStartTime = Util.zonedDateTimeOf(xTrace.get(i));
//            if((earliestZDT == null) || (traceStartTime.isBefore(earliestZDT))){
//                earliestZDT = traceStartTime;
//            }
//        }
//        return earliestZDT;
//    }
//
//    public ZonedDateTime getEndTimestamp(){
//        ZonedDateTime latestZDT = null;
//        for(int i=0; i<xLog.size(); i++){
//            XTrace xTrace = xLog.get(i);
//            ZonedDateTime traceEndTime =
//                    Util.zonedDateTimeOf(xTrace.get(xTrace.size()-1));
//            if((latestZDT == null) || (traceEndTime.isAfter(latestZDT))){
//                latestZDT = traceEndTime;
//            }
//        }
//        return latestZDT;
//    }

//    public static long medianDuration(List<Case> cases){
//
//        List<Long> caseDurations = new ArrayList<Long>();
//
//        for(int i=0; i<cases.size();i++){
//            long dur = cases.get(i).getDuration();
//            caseDurations.add(dur);
//        }
//
//        Collections.sort(caseDurations);
//
//        double median = 0;
//        double pos1 = Math.floor((caseDurations.size() - 1.0) / 2.0);
//        double pos2 = Math.ceil((caseDurations.size() - 1.0) / 2.0);
//
//        if (pos1 == pos2 ) {
//            median = caseDurations.get((int)pos1);
//        } else {
//            median = (caseDurations.get((int)pos1) + caseDurations.get((int)pos2)) / 2.0;
//        }
//
//        return new Double(median).longValue();
//    }

//    public static long meanDuration(List<Case> cases){
//        long sum = 0;
//
//        for(int i=0; i<cases.size();i++){
//            long dur = cases.get(i).getDuration();
//            sum+=dur;
//        }
//        long result = sum / cases.size();
//        return result;
//    }

    public ListModelArray getLmaCases() {
        return lmaCases;
    }

    public ListModelArray getLmaVariants() {
        return lmaVariants;
    }

    public long getDuration() {
        return duration;
    }

    public long getStartTimeMilli() {
        return startTimeMilli;
    }

    public long getEndTimeMilli() {
        return endTimeMilli;
    }

    public List<Case> getCaseList() {
        return caseList;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
    }

    public List<ZonedDateTime> getTimestamps() {
        return timestamps;
    }

    public String getLogName() {
        return logName;
    }

    public XLog getXLog() {
        return xLog;
    }
}
