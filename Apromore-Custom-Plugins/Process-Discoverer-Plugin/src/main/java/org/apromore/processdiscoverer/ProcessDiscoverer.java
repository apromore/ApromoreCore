/*
 * Copyright © 2019 The University of Melbourne.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.processdiscoverer;


import org.apromore.plugin.portal.processdiscoverer.LogFilterCriterion;
import org.apromore.processdiscoverer.dfg.BPMNAbstraction;
import org.apromore.processdiscoverer.dfg.DFGAbstraction;
import org.apromore.processdiscoverer.dfg.LogDFG;
import org.apromore.processdiscoverer.dfg.TraceAbstraction;
import org.apromore.processdiscoverer.dfg.TraceDFG;
import org.apromore.processdiscoverer.dfg.vis.JSONBuilder;
import org.apromore.processdiscoverer.logfilter.Action;
import org.apromore.processdiscoverer.logfilter.Containment;
import org.apromore.processdiscoverer.logfilter.Level;
import org.apromore.processdiscoverer.logfilter.LogFilter;
import org.apromore.processdiscoverer.logfilter.LogFilterCriterionFactory;
import org.apromore.processdiscoverer.logprocessors.LogUtils;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.apromore.processdiscoverer.logprocessors.TimeLog;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.json.JSONArray;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import java.util.*;


/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
public class ProcessDiscoverer {

    private XLog initial_log; //original log
    private XLog filtered_criteria_log; //log after applying filter criteria
    private List<LogFilterCriterion> criteria; // list of log filter criteria
    private LogDFG logDfg;
    private String attribute = "";
    

    public ProcessDiscoverer(XLog initial_log) {
        this.initial_log = initial_log;
    }

    public Object[] generateDFGJSON(AbstractionParams params, List<LogFilterCriterion> filter_criteria) throws Exception {
    	boolean filterCriteriaChanged = false;
    	if(this.criteria == null || !this.criteria.equals(filter_criteria)) {
        	this.criteria = new ArrayList<>(filter_criteria);
            this.filtered_criteria_log = LogFilter.filter(initial_log, addStartCompleteFilterCriterion(criteria));
            filterCriteriaChanged = true;
    	}
    	
    	if (filterCriteriaChanged || !params.getAttribute().equals(this.attribute)) {
    		attribute = params.getAttribute();
	    	SimplifiedLog simplified_log = new SimplifiedLog(filtered_criteria_log, params.getClassifier());
	    	TimeLog simplified_times_log = new TimeLog(filtered_criteria_log) ;
	    	logDfg = new LogDFG(simplified_log, simplified_times_log);
    	}
    	
    	DFGAbstraction dfgAbstraction = logDfg.getDFGAbstraction(params);
        JSONBuilder jsonBuilder = new JSONBuilder(dfgAbstraction);
        return new Object[] {jsonBuilder.generateJSONFromBPMN(false), dfgAbstraction.getDiagram()} ;
    }

    public Object[] generateBPMNJSON(AbstractionParams params, List<LogFilterCriterion> filter_criteria) throws Exception {
    	boolean filterCriteriaChanged = false;
    	if(this.criteria == null || !this.criteria.equals(filter_criteria)) {
        	this.criteria = new ArrayList<>(filter_criteria);
            this.filtered_criteria_log = LogFilter.filter(initial_log, addStartCompleteFilterCriterion(criteria));
            filterCriteriaChanged = true;
    	}
    	
    	if (filterCriteriaChanged || !params.getAttribute().equals(this.attribute)) {
    		attribute = params.getAttribute();
	    	SimplifiedLog simplified_log = new SimplifiedLog(filtered_criteria_log, params.getClassifier());
	    	TimeLog simplified_times_log = new TimeLog(filtered_criteria_log) ;
	    	logDfg = new LogDFG(simplified_log, simplified_times_log);
    	}
    	
    	BPMNAbstraction bpmnAbstraction = logDfg.getBPMNAbstraction(params);
        JSONBuilder jsonBuilder = new JSONBuilder(bpmnAbstraction);
        return new Object[] {jsonBuilder.generateJSONFromBPMN(false), bpmnAbstraction.getDiagram()} ;
    }

    public JSONArray generateTraceDFGJSON(String traceID, AbstractionParams params) throws Exception {
    	if(filtered_criteria_log == null || filtered_criteria_log.size() == 0) {
    		throw new Exception("No log abstraction has been done yet!");
    	}
        
        XTrace trace = null;
        XConceptExtension xce = XConceptExtension.instance();
        for(XTrace trace1 : this.filtered_criteria_log) {
            if(xce.extractName(trace1).equals(traceID)) {
                trace = trace1;
                break;
            }
        }
        
        if (trace == null) {
        	throw new Exception("The trace with ID = " + traceID + " is not in the current log abstraction!");
        }
       
        TraceDFG traceDfg = new TraceDFG(trace, this.logDfg);
        TraceAbstraction traceAbs = traceDfg.getTraceAbstraction(params);
        JSONBuilder jsonBuilder = new JSONBuilder(traceAbs);
        return jsonBuilder.generateJSONFromBPMN(false);
    }

    /**
     * Generate a directly-follows graph from log
     * Before the diagram is generated, ares are selected based on arc slider
     * Then, arcs and nodes are filtered out until they both form a connected DFG
     * On the generated diagram, the arc label contains the aggregate measures. 
     * However, the node label only contains the activity label.
     * @param params
     * @param criteria
     * @return
     * @throws Exception 
     */
    public BPMNDiagram generateDiagramFromLog(AbstractionParams params, List<LogFilterCriterion> criteria) throws Exception {
    	boolean filterCriteriaChanged = false;
    	if(this.criteria == null || !this.criteria.equals(criteria)) {
        	this.criteria = new ArrayList<>(criteria);
            this.filtered_criteria_log = LogFilter.filter(initial_log, addStartCompleteFilterCriterion(criteria));
            filterCriteriaChanged = true;
    	}
    	
    	if (filterCriteriaChanged || !params.getAttribute().equals(this.attribute)) {
    		attribute = params.getAttribute();
	    	SimplifiedLog simplified_log = new SimplifiedLog(filtered_criteria_log, params.getClassifier());
	    	TimeLog simplified_times_log = new TimeLog(filtered_criteria_log) ;
	    	logDfg = new LogDFG(simplified_log, simplified_times_log);
    	}
    	
    	return logDfg.getDFG(params);
    }
    
    // The diagram generated from a trace contains duration weight, not frequency weight, on arcs and nodes 
    // This is because a trace usually contains frequency of 1 for arcs and nodes
    // So, it is more informative to display a trace model with duration other than frequency weight
    // The duration is determined based on two consecutive events with the same concept:name and
    // one ending with "start" while the other one ending with "complete"
    // The node label will be 
    /**
     * Generate a directly-follows graph from a trace
     * This method is only used after abstraction has been generated for a log and the 
     * abstraction contains some traces
     * @param traceID
     * @param params
     * @return
     */
    public BPMNDiagram generateDiagramFromTrace(String traceID, AbstractionParams params) throws Exception {
    	if(filtered_criteria_log == null || filtered_criteria_log.size() == 0) {
    		throw new Exception("No log abstraction has been done yet!");
    	}
        
        XTrace trace = null;
        XConceptExtension xce = XConceptExtension.instance();
        for(XTrace trace1 : this.filtered_criteria_log) {
            if(xce.extractName(trace1).equals(traceID)) {
                trace = trace1;
                break;
            }
        }
        
        if (trace == null) {
        	throw new Exception("The trace with ID = " + traceID + " is not in the current log abstraction!");
        }
       
        TraceDFG traceDfg = new TraceDFG(trace, this.logDfg);
        return traceDfg.getDFG(params);
    }
    
    /**
     * Generate a BPMN model from a log 
     * Unlike generateDiagramFromLog, the arc filtering based on arc slider is used in DFGPWithLogThreshold for SplitMiner
     * On the diagram, the aggregate measure on the arcs must be populated to fit BPMN semantics
     * @param params
     * @param filter_criteria
     * @return
     * @throws Exception 
     */
    public BPMNDiagram generateBPMNFromLog(AbstractionParams params, List<LogFilterCriterion> filter_criteria) throws Exception {
		boolean filterCriteriaChanged = false;
		if(this.criteria == null || !this.criteria.equals(criteria)) {
			this.criteria = new ArrayList<>(criteria);
			this.filtered_criteria_log = LogFilter.filter(initial_log, addStartCompleteFilterCriterion(criteria));
			filterCriteriaChanged = true;
		}
		
		if (filterCriteriaChanged || !params.getAttribute().equals(this.attribute)) {
    		attribute = params.getAttribute();
			SimplifiedLog simplified_log = new SimplifiedLog(filtered_criteria_log, params.getClassifier());
			TimeLog simplified_times_log = new TimeLog(filtered_criteria_log);
			logDfg = new LogDFG(simplified_log, simplified_times_log);
		}
		
		return logDfg.getBPMN(params);
    }
    
    public XLog getFilteredLog() {
    	if (filtered_criteria_log != null) {
    		return filtered_criteria_log;
    	}
    	else {
    		return initial_log;
    	}
    }
    
    /**
     * Add filter to select only start and complete events in traces
     * @param criteria
     * @return new list of criteria with new criterion added
     */
    private  List<LogFilterCriterion> addStartCompleteFilterCriterion(List<LogFilterCriterion> criteria) {
    	Set<String> lifecycle = new UnifiedSet<>();
    	lifecycle.add(LogUtils.START_CODE);
    	lifecycle.add(LogUtils.START_CODE.toUpperCase());
        lifecycle.add(LogUtils.COMPLETE_CODE);
        lifecycle.add(LogUtils.COMPLETE_CODE.toUpperCase());
    	LogFilterCriterion criterion = LogFilterCriterionFactory.getLogFilterCriterion(
    			Action.RETAIN,
    			Containment.CONTAIN_ANY,
                Level.EVENT,
                LogUtils.LIFECYCLE_CODE.toString(),
                LogUtils.LIFECYCLE_CODE.toString(),
                lifecycle);
    	
    	List<LogFilterCriterion> newCriteria = new ArrayList<>(criteria);
    	newCriteria.add(criterion);
    	return newCriteria;
    }
}
