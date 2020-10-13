/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.processdiscoverer.abstraction;


import org.apromore.logman.attribute.graph.AttributeLogGraph;
import org.apromore.logman.attribute.graph.filtering.FilteredGraph;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.logman.attribute.log.AttributeTrace;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;

/**
 * This class builds an abstraction based on a <b>AttributeLog</b>
 * Extend the constructor to create a builder for different type of graphs.
 * It is stateful to remember the created abstraction such that it knows whether to 
 * update weights only to save time or create a new abstraction. In addition, it should
 * also know how to create a new BPMNAbstraction based on a current DFGAbstraction. 
 *
 * @author Bruce Nguyen
 *
 */
public class AbstractionManager {
    private AttributeLog log;
    private AttributeLogGraph graph;
    private DFGAbstraction dfgAbstraction;
    private BPMNAbstraction bpmnAbstraction;
    
    public AbstractionManager(AttributeLog log) {
        this.log = log;
        this.dfgAbstraction = null;
        this.bpmnAbstraction = null;
        this.graph = log.getGraphView();
    }
    
    public AttributeLog getLog() {
        return this.log;
    }
    
    public AttributeLogGraph getGraph() {
        return this.graph;
    }
    
    public DFGAbstraction createDFGAbstraction(AbstractionParams params) throws Exception {
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
	    	
    		if (!log.isDataStatusChanged() && !attributeChanged && !thresholdChanged && !structuralWeightChanged && 
    				!elementSelectionOrderChanged) {
    			dfgAbstraction.updateWeights(params);
    			return dfgAbstraction;
    		}
        }
        
    	if (structuralWeightChanged || attributeChanged) {
    		graph.sortNodesAndArcs(params.getFixedType(), params.getFixedAggregation());
    	}
    	graph.buildSubGraphs(params.invertedNodes());
        long timer = System.currentTimeMillis();
        FilteredGraph filteredGraph = graph.filter(params.getNodeSelectThreshold(), params.getArcSelectThreshold());
        this.dfgAbstraction = new DFGAbstraction(log, filteredGraph, params);
        log.resetDataStatus();
        System.out.println("Select a graph based on node/arc sliders and convert it to BPMNDiagram: " + 
                (System.currentTimeMillis() - timer) + " ms.");
        return dfgAbstraction;
    }
    
    /**
     * Create a BPMN abstraction of this LogDFG
     * @param params
     * @param dfgAbstraction: the corresponding DFGAbstraction with the same type of nodes/arcs and weights
     * @return
     * @throws Exception
     */
    public BPMNAbstraction createBPMNAbstraction(AbstractionParams params, Abstraction dfgAbstraction) throws Exception {
        if (!(dfgAbstraction instanceof DFGAbstraction)) {
            throw new Exception("Invalid Abstraction param while expecting a graph abstraction");
        }
        
        if (bpmnAbstraction != null) {
            AbstractionParams currentParams = bpmnAbstraction.getAbstractionParams();
            // The diagram is unchanged, only need to update weights
            if (!log.isDataStatusChanged() &&
                    
                currentParams.getCorrepondingDFG() == params.getCorrepondingDFG() &&    
                
                currentParams.getAttribute() == params.getAttribute() &&
                    
                currentParams.getNodeSelectThreshold() == params.getNodeSelectThreshold() &&
                currentParams.getArcSelectThreshold() == params.getArcSelectThreshold() &&
                currentParams.getParallelismLevel() == params.getParallelismLevel() &&
                
                currentParams.getFixedType() == params.getFixedType() &&
                currentParams.getFixedAggregation() == params.getFixedAggregation() &&
                
                currentParams.invertedNodes() == params.invertedNodes() &&
                currentParams.invertedArcs() == params.invertedArcs()) {
                
                bpmnAbstraction.updateWeights(params);
                return bpmnAbstraction;
            }
        }
        
        bpmnAbstraction = new BPMNAbstraction(log, (DFGAbstraction)dfgAbstraction, params);
        log.resetDataStatus();
        
        return bpmnAbstraction;
    }
    
    public TraceAbstraction createTraceAbstraction(AttributeTrace attTrace, AttributeLog log, AbstractionParams params) {
        return new TraceAbstraction(attTrace, log, params);
    }
    
    public void cleanUp() {
        graph.clear();
        if (dfgAbstraction != null) dfgAbstraction.cleanUp();
        if (bpmnAbstraction != null) bpmnAbstraction.cleanUp();
    }

}
