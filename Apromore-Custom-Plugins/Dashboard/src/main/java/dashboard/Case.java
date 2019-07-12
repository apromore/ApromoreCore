package dashboard;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Case {

    private XTrace xTrace;
    private List<XEvent> xEvents = new ArrayList<XEvent>();
    private String caseId = "";
    private int eventsSize = 0;
    private int variantId = 0;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private long startTimeMilli;
    private long endTimeMilli;
    private long duration;
    private String startTimeString = "";
    private String endTimeString = "";
    private String durationString = "";
    private List<String> eventSequence = new ArrayList<String>();

    public Case(XTrace xTrace) {
        this.xTrace = xTrace;
        caseId = xTrace.getAttributes().get("concept:name").toString();
        eventsSize = numberOfEventsOf(xTrace);
        startTime = Util.zonedDateTimeOf(xTrace.get(0));
        endTime = Util.zonedDateTimeOf(xTrace.get(xTrace.size()-1));
        startTimeMilli = Util.epochMilliOf(startTime);
        endTimeMilli = Util.epochMilliOf(endTime);
        duration = endTimeMilli - startTimeMilli;
        startTimeString = Util.timestampStringOf(startTime);
        endTimeString = Util.timestampStringOf(endTime);
        durationString = Util.durationStringOf(duration);


        HashMap<ZonedDateTime, Integer> markedHM =
                new HashMap<ZonedDateTime, Integer>();
        for(int i=0; i<xTrace.size();i++){
            XEvent xEvent = xTrace.get(i);
            xEvents.add(xEvent);
            String eName = xEvent.getAttributes().get("concept:name").toString();

            String life = xEvent.getAttributes().get(
                    "lifecycle:transition").toString().toLowerCase();
            ZonedDateTime iZDT = Util.zonedDateTimeOf(xEvent);
            if(life.equals("start")) {
                for(int j=(i+1); j<xTrace.size(); j++) {
                    XEvent jEvent = xTrace.get(j);
                    String jName = jEvent.getAttributes().get("concept:name").toString();
                    String jLife = jEvent.getAttributes().get(
                            "lifecycle:transition").toString().toLowerCase();
                    if(jName.equals(eName) && jLife.equals("complete")) {
                        ZonedDateTime jZDT = Util.zonedDateTimeOf(jEvent);
                        markedHM.put(jZDT, 0);
                        break;
                    }
                }
                eventSequence.add(eName);
            }
            if(life.equals("complete") && !markedHM.containsKey(iZDT)){
                eventSequence.add(eName);
            }
        }
    }

    private static int numberOfEventsOf(XTrace xTrace){
        int size = 0;
//        HashMap<String, String> eventsHM =
//                new HashMap<String, String>();

        for(int i=0; i<xTrace.size();i++) {
            XEvent iEvent = xTrace.get(i);
            String iName = iEvent.getAttributes().get(
                    "concept:name").toString();
            String iLife = iEvent.getAttributes().get(
                    "lifecycle:transition").toString().toLowerCase();
            if(iLife.equals("start")) {
                boolean hasComplete = false;
                for(int j=i; j<xTrace.size(); j++) {
                    XEvent jEvent = xTrace.get(j);
                    String jName = jEvent.getAttributes().get(
                            "concept:name").toString();
                    String jLife = jEvent.getAttributes().get(
                            "lifecycle:transition").toString().toLowerCase();
                    if(jName.equals(iName) && jLife.equals("complete")) {
                        hasComplete = true;
                        break;
                    }
                }
                if(!hasComplete) {
                    size += 1;
                }
            }else if(iLife.equals("complete")){
                size += 1;
            }
        }
        return size;
//        return eventsHM.size();
    }

    public String getCaseId() {
        return caseId;
    }

    public int getEventsSize() {
        return eventsSize;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }

    public int getVariantId() {
        return variantId;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return endTime;
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

    public String getStartTimeString() {
        return startTimeString;
    }

    public String getEndTimeString() {
        return endTimeString;
    }

    public String getDurationString() {
        return durationString;
    }

    public List<String> getEventSequence() {
        return eventSequence;
    }

    public XTrace getXTrace() {
        return xTrace;
    }

    public List<XEvent> getXEvents() {
        return xEvents;
    }


}
