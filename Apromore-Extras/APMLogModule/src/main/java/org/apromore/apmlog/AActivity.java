package org.apromore.apmlog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chii Chang (11/2019)
 */
public class AActivity  {
    private String name;
    private List<AEvent> eventList;
    private long startTimeMilli = 0;
    private long endTimeMilli = 0;
    private long duration = 0;

    public AActivity(String name, List<AEvent> eventList, long startTimeMilli, long endTimeMilli,
                     long duration) {
        this.name = name;
        this.eventList = eventList;
        this.startTimeMilli = startTimeMilli;
        this.endTimeMilli = endTimeMilli;
        this.duration = duration;
    }

    public AActivity(List<AEvent> eventList) {
        this.name = eventList.get(0).getName();
        this.eventList = eventList;
        this.startTimeMilli = eventList.get(0).getTimestampMilli();
        this.endTimeMilli = eventList.get(eventList.size()-1).getTimestampMilli();
        if(endTimeMilli > startTimeMilli) this.duration = endTimeMilli - startTimeMilli;
    }

    public String getName() {
        return name;
    }

    public List<AEvent> getEventList() {
        return eventList;
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

    public AActivity clone() {
        String clnName = this.name;
        List<AEvent> clnEventList = new ArrayList<>();
        for(int i = 0; i<this.eventList.size(); i++) {
            AEvent aEvent = this.eventList.get(i).clone();
            clnEventList.add(aEvent);
        }
        long clnStartTimeMilli = this.startTimeMilli;
        long clnEndTimeMilli = this.endTimeMilli;
        long clnDuration = this.duration;
        AActivity activity = new AActivity(clnName, clnEventList, clnStartTimeMilli,
                clnEndTimeMilli, clnDuration);
        return activity;
    }
}
