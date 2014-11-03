package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class AnimationLog {
    Map<XTrace, ReplayTrace> traceMap = new HashMap();
    String name = "";
    DateTime startDate = null;
    DateTime endDate = null;
    Interval interval = null;
    String color = "";
    
    public static int TIMELINE_DURATION = 120; //120 seconds
    
    public AnimationLog() {
//        traceMap = traces;
    }
    
    public Map<XTrace, ReplayTrace> getTraceMap() {
        return this.traceMap;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public DateTime getStartDate() {
        return this.startDate;
    }
    
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }
    
    public DateTime getEndDate() {
        return this.endDate;
    }
    
    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;        
    }    
    
    public Interval getInterval() {
        if (this.startDate != null && this.endDate != null) {
            return new Interval(this.startDate, this.endDate);
        } else {
            return null;
        }
    }    
    
    public Collection<ReplayTrace> getTraces() {
        return this.traceMap.values();
    }
    
    
    
    public boolean isEmpty() {
        return this.traceMap.isEmpty();
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return this.color;
    }
    
    //Return a hashmap
    //key: sequence Id
    //value: list of intervals, each reprenseting a token transfer via the sequence flow (Id is the key)
    //Note that a trace might contain many transfers via a sequence flow, at different time interval
    public Map<String, SortedSet<Interval>> getTokenTransfers() {
        Map<String, SortedSet<Interval>> sequenceByIds = new HashMap();
        
        SortedSet<Interval> transfers;
                
        for (ReplayTrace trace : traceMap.values()) {
            for (SequenceFlow seqFlow : trace.getSequenceFlows()) {
                if (!sequenceByIds.containsKey(seqFlow.getId())) {
                    transfers = new TreeSet<>(
                        new Comparator<Interval>() {
                            @Override
                            public int compare(Interval o1, Interval o2) {
                                return o1.getStart().compareTo((o2.getStart()));
                            }
                        });
                    sequenceByIds.put(seqFlow.getId(), transfers);
                }
                sequenceByIds.get(seqFlow.getId()).add(TimeUtilities.getInterval(seqFlow));
            }
        }
        return sequenceByIds;
    }
    
    /*
    public void analyzeSequenceFlows() {
        Map<String,List<Interval>> sequenceByIds = this.getSequenceIntervalsById();
        for (String id : sequenceByIds.keySet()) {
            Collections.sort(sequenceByIds.get(id), new IntervalStartComparator());
        }
    }
    */
    

}