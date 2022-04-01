/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.service.loganimation.replay;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;

public class AnimationLog {
    private XLog xlog = null;
    private Definitions diagram;
    private Map<XTrace, ReplayTrace> traceMap = new HashMap();
    private Set<XTrace> unplayTraces = new HashSet();
    private String name = "";
    private String fileName = "";
    private DateTime startDate = null;
    private DateTime endDate = null;
    private String color = "";
    private double exactTraceFitnessFormulaTime = 0; //in milliseconds
    private double approxTraceFitnessFormulaTime = 0; //in milliseconds
    private double minBoundMoveOnModel = 0;
    private static final Logger LOGGER = Logger.getLogger(AnimationLog.class.getCanonicalName());
    private CaseMapping caseMapping; // mapping caseID to case index for space efficiency
    
    public AnimationLog(XLog xlog) {
        this.xlog = xlog;
        this.caseMapping = new CaseMapping(xlog);
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
    
    public String getFileName() {
        return this.fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    public XLog getXLog() {
        return this.xlog;
    }
    
    public void setDiagram(Definitions diagram) {
    	this.diagram = diagram;
    }
    
    public int getCaseIndexFromId(String caseId) {
    	return this.caseMapping.getIndex(caseId);
    }
    
    public int getNumberOfCases() {
    	return caseMapping != null ? caseMapping.size() : 0;
    }
    
    public DateTime getStartDate() {
        if (startDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(2050, 1, 1);
            DateTime logStartDate = new DateTime(cal.getTime());
            
            for (ReplayTrace trace : this.getTraces()) {
                if (logStartDate.isAfter(trace.getStartDate())) {
                    logStartDate = trace.getStartDate();
                }
            }
            startDate = logStartDate;
        }
        return startDate;
    }
    
    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }
    
    public DateTime getEndDate() {
        if (endDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(1920, 1, 1);
            DateTime logEndDate = new DateTime(cal.getTime());
            
            for (ReplayTrace trace : this.getTraces()) {
                if (logEndDate.isBefore(trace.getEndDate())) {
                    logEndDate = trace.getEndDate();
                }
            }
            endDate = logEndDate;
        }
        return endDate;
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
    
    public void addUnreplayTrace(XTrace trace) {
        this.unplayTraces.add(trace);
    }
    
    public Set<XTrace> getUnplayTraces() {
        return this.unplayTraces;
    }
    
    public String getUnplayTracesString() {
        String unreplay = "Unreplay: ";
        for (XTrace trace : this.unplayTraces) {
            unreplay += LogUtility.getConceptName(trace) + ",";
        }
        return unreplay;
    }
    
    public Collection<ReplayTrace> getTraces() {
        return this.traceMap.values();
    }
    
    public List<ReplayTrace> getTracesWithOriginalOrder() {
    	List<ReplayTrace> orderedTraces = new ArrayList<ReplayTrace>();
    	for (XTrace xtrace : this.xlog) {
    		if (traceMap.containsKey(xtrace)) {
    			orderedTraces.add(traceMap.get(xtrace));
    		}
    	}
    	return orderedTraces;
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
    
    public double getTraceFitness(double minBoundMoveCostOnModel) {
        double totalFitness = 0;
        for (ReplayTrace trace : this.getTraces()) {
            totalFitness += trace.getTraceFitness(minBoundMoveCostOnModel);
        }
        if (this.getTraces().size() > 0) {
            return 1.0*totalFitness/this.getTraces().size();
        }
        else {
            return 1.00;
        }        
    }  
    
    /**
     * Get the approximate trace fitness. This is approximate measure since
     * the calculation is based on approximate fitness value of every trace
     * The moves for every trace alignment are not optimal and the minimum
     * move on model cost is also approximated for the whole log
     * @return 
     */
    public double getApproxTraceFitness() {
        double minMMCost = this.getApproxMinMoveModelCost();
        double totalTraceFitness = 0;
        for (ReplayTrace trace : this.getTraces()) {
            totalTraceFitness += trace.getTraceFitness(minMMCost);
        }        
        if (this.getTraces().size() > 0) {
            return 1.0*totalTraceFitness/this.getTraces().size();
        }
        else {
            return 1.00;
        }
    }
    
    /**
     * Get the minimum move model cost assuming that all events in every trace
     * does not match any activities on the model.
     * @return 
     */
    public double getApproxMinMoveModelCost() {
        double minUpperMMCost = Double.MAX_VALUE;
        for (ReplayTrace trace : this.getTraces()) {
            if (minUpperMMCost > trace.getUpperMoveCostOnModel()) {
                minUpperMMCost = trace.getUpperMoveCostOnModel();
            }
        }    
        return minUpperMMCost;
    }
    
    public void setMinBoundMoveOnModel(double minBoundMoveOnModel) {
        this.minBoundMoveOnModel = minBoundMoveOnModel;
    }
    
    public double getMinBoundMoveOnModel() {
        return this.minBoundMoveOnModel;
    }
    
    public int getReliableTraceCount() {
        int count = 0;
        for (ReplayTrace trace : this.getTraces()) {
            if (trace.isReliable()) {
                count++;
            }
        }
        return count;
    }
    
    public String getUnReliableTraceIDs() {
        String unreliableTraceIDs = "Unreliable:";
        for (ReplayTrace trace : this.getTraces()) {
            if (!trace.isReliable()) {
                unreliableTraceIDs += trace.getId() + ",";
            }
        }
        return unreliableTraceIDs;
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
    
    public double getExactTraceFitnessFormulaTime() {
        return this.exactTraceFitnessFormulaTime;
    }
    
    public void setExactTraceFitnessFormulaTime(double exactTraceFitnessFormulaTime) {
        this.exactTraceFitnessFormulaTime = exactTraceFitnessFormulaTime;
    }
    
    public double getApproxTraceFitnessFormulaTime() {
        return this.approxTraceFitnessFormulaTime;
    }
    
    public void setApproxTraceFitnessFormulaTime(double approxTraceFitnessFormulaTime) {
        this.approxTraceFitnessFormulaTime = approxTraceFitnessFormulaTime;
    }    
    
    public long getAlgoRuntime() {
        long totalRuntime = 0;
        for (ReplayTrace trace : this.getTraces()) {
            totalRuntime += trace.getAlgoRuntime();
        }
        return totalRuntime;
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
//                LOGGER.info("Node1:" + seqFlow.getSourceRef().getName() + " id:" + seqFlow.getSourceRef().getId() +
//                            "Node2:" + seqFlow.getTargetRef().getName() + " id:" + seqFlow.getTargetRef().getId());
                sequenceByIds.get(seqFlow.getId()).add(TimeUtilities.getInterval(seqFlow));
            }
        }
        return sequenceByIds;
    }

    public void clear() {
        this.xlog = null;
        for (ReplayTrace trace : this.traceMap.values()) {
            trace.clear();
        }
        traceMap.clear();
        
        for (XTrace xTrace : this.unplayTraces) {
            xTrace.clear();
        }
        unplayTraces.clear();
    }  

}
