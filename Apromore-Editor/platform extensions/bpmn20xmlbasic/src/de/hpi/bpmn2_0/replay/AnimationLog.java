package de.hpi.bpmn2_0.replay;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;
import org.joda.time.Interval;

public class AnimationLog {
    private Map<XTrace, ReplayTrace> traceMap = new HashMap();
    private String name = "";
    private DateTime startDate = null;
    private DateTime endDate = null;
    private Interval interval = null;
    private String color = "";
    private long calculationTime = 0; //in milliseconds
    private static final Logger LOGGER = Logger.getLogger(ReplayTrace.class.getCanonicalName());
    
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
    
    public void add(XTrace trace, ReplayTrace replayTrace) {
        traceMap.put(trace, replayTrace);
    }
    
    public Collection<ReplayTrace> getTraces() {
        return this.traceMap.values();
    }
    
    public double getCostBasedMoveLogFitness() {
        double avgCost = 0;
        for (ReplayTrace trace : this.getTraces()) {
            avgCost += trace.getCostBasedMoveLogFitness();
        }
        if (this.getTraces().size() > 0) {
            return 1.0*avgCost/this.getTraces().size();
        }
        else {
            return 1.00;
        }
    }
    
    public double getCostBasedMoveModelFitness() {
        double avgCost = 0;
        for (ReplayTrace trace : this.getTraces()) {
            avgCost += trace.getCostBasedMoveModelFitness();
        }
        if (this.getTraces().size() > 0) {
            return 1.0*avgCost/this.getTraces().size();
        }
        else {
            return 1.00;
        }
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
    
    public long getCalculationTime() {
        return this.calculationTime;
    }
    
    public void setCalculationTime(long calculationTime) {
        this.calculationTime = calculationTime;
    }
    
    /*
    * Return a map of intervals by sequenceIds for this log
    * Note that a trace might contain many overlapping intervals created from sequence flows    
    * key: sequence Id
    * value: list of intervals, each reprenseting a token transfer via the sequence flow (Id is the key)
    * The intervals of one sequenceId are sorted by start date, something look like below.
    * |--------------|
    *      |---------| 
    *      |---------|
    *        |------------------|
    *            |----------|
    *                  |-----------------------|
    */    
    public Map<String, SortedSet<Interval>> getIntervalMap() {
        Map<String, SortedSet<Interval>> sequenceByIds = new HashMap();
        
        SortedSet<Interval> transfers;
                
        for (ReplayTrace trace : traceMap.values()) {
            for (SequenceFlow seqFlow : trace.getSequenceFlows()) {
                if (!sequenceByIds.containsKey(seqFlow.getId())) {
                    transfers = new TreeSet<>(
                        new Comparator<Interval>() {
                            @Override
                            public int compare(Interval o1, Interval o2) {
                                if (o1.getStart().isBefore(o2.getStart())) {
                                    return -1;
                                } else {
                                    return +1;
                                }
                            }
                        });
                    sequenceByIds.put(seqFlow.getId(), transfers);
                }
                //LOGGER.info("Node1:" + seqFlow.getSourceRef().getName() + " id:" + seqFlow.getSourceRef().getId() + 
                //            "Node2:" + seqFlow.getTargetRef().getName() + " id:" + seqFlow.getTargetRef().getId());
                sequenceByIds.get(seqFlow.getId()).add(TimeUtilities.getInterval(seqFlow));
            }
        }
        return sequenceByIds;
    }

    

}