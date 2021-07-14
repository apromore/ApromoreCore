/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

package org.apromore.processdiscoverer;

import org.apromore.logman.attribute.graph.filtering.FilteredGraph;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.processdiscoverer.abstraction.BPMNAbstraction;
import org.apromore.processdiscoverer.abstraction.DFGAbstraction;
import org.apromore.processdiscoverer.abstraction.TraceAbstraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;

/**
 * ProcessDiscoverer is in charge of creating graph and BPMN abstractions from logs.
 * 
 * @author Bruce Nguyen
 */
public class ProcessDiscoverer {
    private DFGAbstraction dfgAbstraction;
    private boolean isDFGAbstractionValid = false;
    private BPMNAbstraction bpmnAbstraction;
    private boolean isBPMNAbstractionValid = false;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDiscoverer.class);
    
    /**
     * To notify ProcessDiscoverer to regenerate new abstractions from logs
     */
    public void invalidateAbstraction() {
        isDFGAbstractionValid = false;
        isBPMNAbstractionValid = false;
    }
    
    public Abstraction generateDFGAbstraction(@NonNull AttributeLog log, @NonNull AbstractionParams params) throws Exception {
        boolean attributeChanged = true;
        boolean thresholdChanged = true;
        boolean structuralWeightChanged = true;
        boolean elementSelectionOrderChanged = true;
        
        if (dfgAbstraction != null) {
            AbstractionParams currentParams = dfgAbstraction.getAbstractionParams();
            attributeChanged = (currentParams.getAttribute() != params.getAttribute());
            thresholdChanged  = (currentParams.getNodeSelectThreshold() != params.getNodeSelectThreshold() ||
                            currentParams.getArcSelectThreshold() != params.getArcSelectThreshold());
            structuralWeightChanged = (currentParams.getFixedType() != params.getFixedType() ||
                                    currentParams.getFixedAggregation() != params.getFixedAggregation());
            elementSelectionOrderChanged = (currentParams.invertedNodes() != params.invertedNodes());
            
            if (isDFGAbstractionValid && !attributeChanged && !thresholdChanged && !structuralWeightChanged &&
                    !elementSelectionOrderChanged) {
                dfgAbstraction.updateWeights(params);
                return dfgAbstraction;
            }
        }
        
        // The state has changed, cannot reuse the current DFGAbstraction, must build subgraphs again
        if (structuralWeightChanged || attributeChanged || !isDFGAbstractionValid) {
            log.getGraphView().sortNodesAndArcs(params.getFixedType(), params.getFixedAggregation());
        }
        log.getGraphView().buildSubGraphs(params.invertedNodes());
        long timer = System.currentTimeMillis();
        FilteredGraph filteredGraph = log.getGraphView().filter(params.getNodeSelectThreshold(), params.getArcSelectThreshold());
        this.dfgAbstraction = new DFGAbstraction(log, filteredGraph, params);
        this.isDFGAbstractionValid = true;
        LOGGER.debug("Select a graph based on node/arc sliders and convert it to BPMNDiagram: {} ms.",
                System.currentTimeMillis() - timer);
        
        return dfgAbstraction;
    }
    
    public Abstraction generateBPMNAbstraction(@NonNull AttributeLog log, @NonNull AbstractionParams params, @NonNull Abstraction dfgAbstraction) throws Exception {
        if (bpmnAbstraction != null) {
            AbstractionParams currentParams = bpmnAbstraction.getAbstractionParams();
            boolean attributeChanged = (currentParams.getAttribute() != params.getAttribute());
            boolean thresholdChanged  = (currentParams.getNodeSelectThreshold() != params.getNodeSelectThreshold() ||
                            currentParams.getArcSelectThreshold() != params.getArcSelectThreshold() ||
                            currentParams.getParallelismLevel() != params.getParallelismLevel()) ;
            boolean structuralWeightChanged = (currentParams.getFixedType() != params.getFixedType() ||
                                    currentParams.getFixedAggregation() != params.getFixedAggregation());
            boolean elementSelectionOrderChanged = (currentParams.invertedNodes() != params.invertedNodes());
            
            // The diagram is unchanged, only need to update weights
            if (isBPMNAbstractionValid && !attributeChanged && !thresholdChanged && !structuralWeightChanged &&
                    !elementSelectionOrderChanged) {
                bpmnAbstraction.updateWeights(params);
                return bpmnAbstraction;
            }
        }
        
        bpmnAbstraction = new BPMNAbstraction(log, (DFGAbstraction)dfgAbstraction, params);
        isBPMNAbstractionValid = true;
        
        return bpmnAbstraction;
    }
    
    // This method is one-off to view a trace
    public Abstraction generateTraceAbstraction(@NonNull AttributeLog log, @NonNull String traceID, @NonNull AbstractionParams params) throws Exception {
    	AttributeTrace attTrace = log.getTraceFromTraceId(traceID);
        if (attTrace == null) {
        	throw new Exception("The trace with ID = " + traceID + " is not in the current log (may have been filtered out)!");
        }
       
        return new TraceAbstraction(attTrace, log, params);
    }
    
    // A door to clean up memory as PD logic is memory-intensive
    public void cleanUp() {
        if (dfgAbstraction != null) dfgAbstraction.cleanUp();
        if (bpmnAbstraction != null) bpmnAbstraction.cleanUp();
        System.out.println("PD-Logic cleanup is done!");
    }
    
}
