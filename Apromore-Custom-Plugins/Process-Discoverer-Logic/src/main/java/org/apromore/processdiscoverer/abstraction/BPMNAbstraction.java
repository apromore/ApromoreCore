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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.logman.attribute.graph.MeasureRelation;
import org.apromore.logman.attribute.graph.MeasureType;
import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.bpmn.BPMNDiagramHelper;
import org.apromore.processdiscoverer.splitminer.ProcessDiscovererDFGP;
import org.apromore.processdiscoverer.splitminer.SimpleLogAdapter;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.splitminer.SplitMiner;
import org.apromore.splitminer.dfgp.DirectlyFollowGraphPlus;
import org.apromore.splitminer.log.SimpleLog;
import org.apromore.splitminer.ui.miner.SplitMinerUIResult;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;

/**
 * This class represents the BPMN abstraction of logs
 * It has a corresponding DFGAbstraction with the same type of nodes/arcs and weights.
 * This DFGAbstraction is used to calculate the weights on the model in matching them
 * with the weights on the DFG. 
 * In addition, it has another corresponding DFGAbstraction where weights of nodes
 * and arcs are frequency. This DFGAbstraction is needed for SplitMiner to discover 
 * a BPMN model. 
 * @author Bruce Nguyen
 *
 */
public class BPMNAbstraction extends AbstractAbstraction {
	private DFGAbstraction dfgAbstraction;
	private DFGAbstraction dfgAbsFreq;
	
	/**
	 * Create a new BPMNAbstraction of logs.
	 * @param logDfg
	 * @param params
	 * @param dfgAbstraction: the corresponding DFGAbstraction with the same types of nodes and weights
	 * @throws Exception
	 */
	protected BPMNAbstraction(AttributeLog log, DFGAbstraction dfgAbstraction, AbstractionParams params) throws Exception {
		super(log, params);
		this.dfgAbstraction = dfgAbstraction;
		this.diagram = mineBPMNDiagram(params, dfgAbstraction);
		this.updateWeights(params);
	}
	
	public DFGAbstraction getDFGAbstraction() {
		return this.dfgAbstraction;
	}
	
	public DFGAbstraction getFrequencyBasedDFGAbstraction() {
		return this.dfgAbsFreq;
	}
	
    /**
     * This method calculates weights on the BPMN model based on the current LogDFG
     * Mainly it aims to create a mapping from an edge on the model to a set of arcs on the DFG
     * Therefore, the weight on the edge is the sum of those weights on the arcs.
     * TODO: this mapping is inaccurate as it does not consider all the semantics of BPMN gateways.
     * At the moment, it only considers all gateways as XORs. 
     * @param params
     */
	@Override
	protected void updateArcWeights(AbstractionParams params) {
		arcPrimaryWeights.clear();
		arcSecondaryWeights.clear();
		
		if (params.getPrimaryType() == MeasureType.FREQUENCY) {
			updateArcFrequencyWeights(params, nodePrimaryWeights, arcPrimaryWeights);
			if (params.getSecondary()) {
				updateArcDurationWeightFromDFG(params, arcSecondaryWeights);
			}
		}
		else {
			updateArcDurationWeightFromDFG(params, arcPrimaryWeights);
			if (params.getSecondary()) {
				updateArcFrequencyWeights(params, nodeSecondaryWeights, arcSecondaryWeights);
			}
		}
		
		minArcPrimaryWeight = Double.MAX_VALUE;
        maxArcPrimaryWeight = 0;
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : this.diagram.getEdges()) {
		    maxArcPrimaryWeight = Math.max(maxArcPrimaryWeight, arcPrimaryWeights.get(edge));
	        minArcPrimaryWeight = Math.min(minArcPrimaryWeight, arcPrimaryWeights.get(edge));
        }
	}
	
	private void updateArcDurationWeightFromDFG(AbstractionParams params,
											Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> arcWeights) {
    	for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : this.diagram.getEdges()) {
            BPMNNode source = edge.getSource();
            BPMNNode target = edge.getTarget();
            
    	    if (source instanceof Gateway && diagram.getOutEdges(source).size() == 1 &&
    	            target instanceof Activity) { // a merge gateway to an activity
    	        arcWeights.put(edge, 0d);
    	    }
    	    else if (target instanceof Gateway && diagram.getInEdges(target).size() == 1 &&
    	            source instanceof Activity) { // an activity to a split gateway
                arcWeights.put(edge, 0d);
            }
            else {
            	arcWeights.put(edge, this.getEdgeWeightFromDFG(edge, params, arcWeights==this.arcSecondaryWeights));
            }
        }
    }
	
	/**
	 * Compute edge weight by aligning it to the DFG.
	 * If there are more than one DFG edges found to corresponding with the BPMN edge,
	 * the returning result is the average weight of all DFG edges.
	 * This alignment is approximate only.
	 * @param edge
	 * @param params
	 * @param secondary: true if this is to compute secondary weights
	 * @return
	 */
	private double getEdgeWeightFromDFG(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge, AbstractionParams params,
										boolean secondary) {
		Set<BPMNNode> sources = BPMNDiagramHelper.getSources(this.diagram, edge.getSource(), new UnifiedSet<>());
        Set<BPMNNode> targets = BPMNDiagramHelper.getTargets(this.diagram, edge.getTarget(), new UnifiedSet<>());
        double weight = 0d;
        int count = 0;
        for (BPMNNode s : sources) {
            for (BPMNNode t : targets) {
            	Double dfgWeight = this.dfgAbstraction.getEdgeWeight(s.getLabel(), t.getLabel(), secondary);
            	if (dfgWeight != null) {
            		count++;
                	weight += dfgWeight;
            	}
            }
        }
        if (count > 0) { //absolute frequency must be always rounded number
            boolean isAbsoluteFrequency = (!secondary && params.getPrimaryType()==MeasureType.FREQUENCY && 
                                              params.getPrimaryRelation()==MeasureRelation.ABSOLUTE) ||
                                          (secondary && params.getSecondaryType()==MeasureType.FREQUENCY && 
                                              params.getSecondaryRelation()==MeasureRelation.ABSOLUTE);
            return isAbsoluteFrequency ? Math.ceil(1.0*weight/count) : (1.0*weight/count);
        }
        else {
        	return 0d;
        }
        
	}
	
	/**
	 * Update arc frequency weights.
	 * Note that the weight of single output edge from the start event should be equal to
	 * the total weights of corresponding edges on the corresponding DFG. It could be less
	 * than the total starting weight of the start event on the corresponding DFG.
	 * @param params: abstraction parameters
	 * @param nodeWeights: contains node weights already calculated
	 * @param arcWeights: the arc weights to be updated
	 */
	private void updateArcFrequencyWeights(AbstractionParams params, 
										Map<BPMNNode,Double> nodeWeights,
										Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> edgeWeights) {
		Event startEvent = null, endEvent = null;
		for (Event event : diagram.getEvents()) {
			if (event.getEventType() == Event.EventType.START) {
				startEvent = event;
			}
			else if (event.getEventType() == Event.EventType.END) {
				endEvent = event;
			}
		}
		
		// Update edges based on the corresponding DFG
		// This is a rough alignment by matching an edge on the model with
		// a set of edges on the DFG
		// Note: this update ignores BPMN gateway semantics. All gateways
		// are considered as XOR.
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : this.diagram.getEdges()) {
			edgeWeights.put(edge, this.getEdgeWeightFromDFG(edge, params, edgeWeights==this.arcSecondaryWeights));
		}
		
		// Adjust weight of edges adjacent to activity nodes as the previous 
		// step does not consider matching to activity frequency leading to 
		// weight of edges could be higher than weight of the adjacent activities.
		// Note that weight of edges is always less than weight of nodes on the 
		// corresponding DFG due to arc filtering
		for (BPMNNode node : diagram.getNodes()) {
			if (node instanceof Activity) {
				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> inEdge = diagram.getInEdges(node).iterator().next();
				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> outEdge = diagram.getOutEdges(node).iterator().next();
				if (edgeWeights.containsKey(inEdge) && edgeWeights.get(inEdge) > nodeWeights.get(node)) {
					edgeWeights.put(inEdge, nodeWeights.get(node));
				}
				if (edgeWeights.containsKey(outEdge) && edgeWeights.get(outEdge) > nodeWeights.get(node)) {
					edgeWeights.put(outEdge, nodeWeights.get(node));
				}
			}
		}
		
		
		// Fix the merging or splitting edge of gateways to tally with the total weight on the branching edges
		// This is because the previous step ignores the semantics of BPMN gateways
		// Using bread-first search to do in order from the start event down to the end event. 
		// This is to prioritize fixing the early gateways in the order first.
		// If this step makes the input/output edges of activity nodes higher weight than the node itself,
		// it's acceptable because the reason could be the DFG does not show full arcs or the model does not
		// have fitness of 1.
		List<BPMNNode> bfsGateways = this.getNodesByBFS(startEvent, new HashSet<BPMNNode>(diagram.getGateways()));
		for (BPMNNode node: bfsGateways) {
			double totalBranch = this.getBranchTotalSemanticWeight((Gateway)node, edgeWeights);		
			BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge = 
						(diagram.getInEdges(node).size() == 1) ? diagram.getInEdges(node).iterator().next() : diagram.getOutEdges(node).iterator().next();
			if (edgeWeights.get(edge) != totalBranch) {
				updateGatewayEdgeFrequency(edge, (Gateway)node, edgeWeights);
			}
		}
		
		// Finally, fix the gateway adjacent to the end event, if exist
		// This is to make the end event look nice
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> endEdge = diagram.getInEdges(endEvent).iterator().next();
		if (endEdge.getSource() instanceof Gateway) {
			updateGatewayEdgeFrequency(endEdge, (Gateway)endEdge.getSource(), edgeWeights);
		}
	}
		
	// Return the semantic total weight of branch edges of a gateway
	// If AND: returns the largest weight among branch edges
	// If XOR/OR: returns the total weight 
	private double getBranchTotalSemanticWeight(Gateway node, Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> edgeWeights) {
		Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges;
		edges = (diagram.getInEdges(node).size() >= 2) ? diagram.getInEdges(node) : diagram.getOutEdges(node);
		double total = 0d;
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : edges) {
			if (node.getGatewayType() == Gateway.GatewayType.PARALLEL) {
				total = Math.max(total, edgeWeights.get(edge));
			}
			else {
				total += edgeWeights.get(edge);
			}
		}
		return total;
	}
	
	
	/**
	 * Update the weight an edge adjacent to a gateway node given that
	 * all other edges in that gateway have been updated
	 * @param targetEdge: the target edge to be updated 
	 * @param gateway: the node (one of two ends) of the edge
	 * @param arcWeights: the map to be updated
	 */
	private void updateGatewayEdgeFrequency(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> targetEdge, Gateway node,
												Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> edgeWeights) {
		// the target edge is a splitting or merging edge
		if ((diagram.getInEdges(node).contains(targetEdge) && diagram.getInEdges(node).size() == 1) ||
				(diagram.getOutEdges(node).contains(targetEdge) && diagram.getOutEdges(node).size() == 1)) { 
			Collection<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = 
					(diagram.getInEdges(node).size() == 1) ? diagram.getOutEdges(node) : diagram.getInEdges(node);
					
			//double maxWeight = this.dfgAbstraction.getTotalStartEventWeight(edgeWeights == this.arcSecondaryWeights);
			double total = this.getBranchTotalSemanticWeight(node, edgeWeights);
			//if (total > maxWeight) total = maxWeight;
			edgeWeights.put(targetEdge, total);
		}
		// the target edge is a branch edge in a split gateway
		else if (diagram.getInEdges(node).size() == 1) { 
			BPMNEdge<? extends BPMNNode, ? extends BPMNNode> inEdge = diagram.getInEdges(node).iterator().next();
			double inWeight = edgeWeights.get(inEdge);
			if (node.getGatewayType() == Gateway.GatewayType.PARALLEL) {
				edgeWeights.put(targetEdge, inWeight);
			}
			else { //XOR and OR
				for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getOutEdges(node)) {
					if (edge != targetEdge) inWeight -= edgeWeights.get(edge);
				}
				if (inWeight < 0) inWeight = 0d;
				edgeWeights.put(targetEdge, inWeight);
			}
		}
		// the target edge is a branch edge in a merge gateway
		else if (diagram.getOutEdges(node).size() == 1) { // merging gateway
			BPMNEdge<? extends BPMNNode, ? extends BPMNNode> outEdge = diagram.getOutEdges(node).iterator().next();
			double outWeight = edgeWeights.get(outEdge);
			if (node.getGatewayType() == Gateway.GatewayType.PARALLEL) {
				edgeWeights.put(targetEdge, outWeight);
			}
			else { //XOR and OR
				for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getInEdges(node)) {
					if (edge != targetEdge) outWeight -= edgeWeights.get(edge);
				}
				if (outWeight < 0) outWeight = 0d;
				edgeWeights.put(targetEdge, outWeight);
			}
		}
	}

    @Override
    public BPMNDiagram getValidBPMNDiagram() {
        return diagram;
    }
    
    /**
     * Mine a BPMN model from an input DFGAbstraction. 
     * @param params
     * @param dfgAbs: DFGAbstraction
     * @return
     * @throws Exception
     */
    private BPMNDiagram mineBPMNDiagram(AbstractionParams params, DFGAbstraction dfgAbs) throws Exception {
        long timer = System.currentTimeMillis();
        SimpleLog simpleLog = SimpleLogAdapter.getSimpleLog(log);
        System.out.println("Prepare SimpleLog for SplitMiner: " + (System.currentTimeMillis() - timer) + " ms.");
        
        timer = System.currentTimeMillis();
        DirectlyFollowGraphPlus dfgp = new ProcessDiscovererDFGP(simpleLog, dfgAbs, 0.0, params.getParallelismLevel(), params.prioritizeParallelism());
        System.out.println("Prepare DFG for SplitMiner: " + (System.currentTimeMillis() - timer) + " ms.");
        
        timer = System.currentTimeMillis();
        SplitMiner splitMiner = new SplitMiner(false, true, SplitMinerUIResult.StructuringTime.NONE);
        
        try {
            BPMNDiagram bpmnDiagram = splitMiner.discoverFromDFGP(dfgp);
            BPMNDiagramHelper.updateStandardEventLabels(bpmnDiagram);
            System.out.println("Mine BPMN model by SplitMiner: " + (System.currentTimeMillis() - timer) + " ms.");
            return bpmnDiagram;
        }
        catch (Exception ex) {
            throw new Exception("Mining BPMN model failed due to an internal error! Please check the sytem logs.");
        }
    }
    
}
