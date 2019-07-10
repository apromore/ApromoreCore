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

import org.apromore.processdiscoverer.dfg.LogDFG;
import org.apromore.processdiscoverer.dfg.TraceDFG;
import org.apromore.processdiscoverer.dfg.abstraction.BPMNAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.DFGAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.TraceAbstraction;
import org.apromore.processdiscoverer.dfg.vis.JSONBuilder;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.apromore.processdiscoverer.logprocessors.TimeLog;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.json.JSONArray;

/**
 * The internal status of this class is represented by the abstraction parameters. 
 * If any of these are changed, the internal data (LogDFG) must be recreated.
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
public class ProcessDiscoverer implements ProcessDiscovererService {
    private AbstractionParams params;
    private XLog log; //log after applying filter criteria
    private LogDFG logDfg;
    
    public AbstractionParams getAbstractionParams() {
    	return this.params;
    }
    
    public XLog getLog() {
    	return this.log;
    }

    @Override
    public Object[] generateDFGJSON(XLog log, AbstractionParams params) throws Exception {
    	DFGAbstraction dfgAbstraction = null;
    	if (params.getCorrepondingDFG() != null) {
    		dfgAbstraction = params.getCorrepondingDFG();
    	}
    	else {
    		dfgAbstraction = this.generateDFGAbstraction(log, params);
    	}
    	
    	// Temporary handling because the new layout generates rare errors 
    	if (dfgAbstraction.getLayout() != null) {
    		JSONBuilder jsonBuilder = new JSONBuilder(dfgAbstraction);
            return new Object[] {jsonBuilder.generateJSONFromBPMN(false), dfgAbstraction} ;
    	}
    	else {
    		return null;
    	}
    }
    
    @Override
    public DFGAbstraction generateDFGAbstraction(XLog log, AbstractionParams params) throws Exception {
    	boolean statusChanged = false;
    	if (this.log != log || this.params == null || !params.getAttribute().equals(this.params.getAttribute())) {
    		statusChanged = true;
    		this.params = params;
    		this.log = log;
    	}
    	
    	if (statusChanged) {
	    	SimplifiedLog simplified_log = new SimplifiedLog(log, params.getClassifier());
	    	TimeLog simplified_times_log = new TimeLog(log) ;
	    	logDfg = new LogDFG(simplified_log, simplified_times_log);
    	}
    	
    	return logDfg.getDFGAbstraction(params);
    }

    @Override
    public Object[] generateBPMNJSON(XLog log, AbstractionParams params, DFGAbstraction dfgAbstraction) throws Exception {
    	BPMNAbstraction bpmnAbstraction = this.generateBPMNAbstraction(log, params, dfgAbstraction);
    	if (bpmnAbstraction.getLayout() != null) {
	        JSONBuilder jsonBuilder = new JSONBuilder(bpmnAbstraction);
	        return new Object[] {jsonBuilder.generateJSONFromBPMN(false), bpmnAbstraction} ;
    	}
    	else {
    		return null;
    	}
    }
    
    @Override
    public BPMNAbstraction generateBPMNAbstraction(XLog log, AbstractionParams params, DFGAbstraction dfgAbstraction) throws Exception {
    	boolean statusChanged = false;
    	if (this.log != log || this.params == null || !params.getAttribute().equals(this.params.getAttribute())) {
    		statusChanged = true;
    		this.params = params;
    		this.log = log;
    	}
    	
    	if (statusChanged) {
	    	SimplifiedLog simplified_log = new SimplifiedLog(log, params.getClassifier());
	    	TimeLog simplified_times_log = new TimeLog(log) ;
	    	logDfg = new LogDFG(simplified_log, simplified_times_log);
    	}
    	
    	return logDfg.getBPMNAbstraction(params, dfgAbstraction);
    }
    
    // This method does not affect the internal status of ProcessDiscoverer object
    @Override
    public JSONArray generateTraceDFGJSON(String traceID, AbstractionParams params) throws Exception {
        TraceAbstraction traceAbs = this.generateTraceAbstraction(traceID, params);
        JSONBuilder jsonBuilder = new JSONBuilder(traceAbs);
        return jsonBuilder.generateJSONFromBPMN(false);
    }
    
    // This method does not affect the internal status of ProcessDiscoverer object
    @Override
    public TraceAbstraction generateTraceAbstraction(String traceID, AbstractionParams params) throws Exception {
    	if(this.log == null || this.log.size() == 0) {
    		throw new Exception("No log abstraction has been done yet!");
    	}
        
        XTrace trace = null;
        XConceptExtension xce = XConceptExtension.instance();
        for(XTrace trace1 : this.log) {
            if(xce.extractName(trace1).equals(traceID)) {
                trace = trace1;
                break;
            }
        }
        
        if (trace == null) {
        	throw new Exception("The trace with ID = " + traceID + " is not in the current log abstraction!");
        }
       
        TraceDFG traceDfg = new TraceDFG(trace, this.logDfg);
        return traceDfg.getTraceAbstraction(params);
    }
    
}
