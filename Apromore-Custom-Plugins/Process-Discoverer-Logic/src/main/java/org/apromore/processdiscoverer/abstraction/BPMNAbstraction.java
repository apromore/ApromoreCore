/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.processdiscoverer.abstraction;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.logman.attribute.graph.MeasureAggregation;
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
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway.GatewayType;
import org.apromore.splitminer.SplitMiner;
import org.apromore.splitminer.dfgp.DirectlyFollowGraphPlus;
import org.apromore.splitminer.log.SimpleLog;
import org.apromore.splitminer.ui.miner.SplitMinerUIResult;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
 
/**
 * This class represents a process model abstraction for an {@link AttributeLog}.
 * The underlying diagram is a BPMNDiagram. This diagram is created based on two corresponding {@link DFGAbstraction}
 * The diagram structure is always created based on a case-frequency DFGAbstraction where weights of nodes
 * and arcs are case frequency. This DFGAbstraction is used for SplitMiner to discover
 * BPMN models consistently when changing abstraction.
 * Another corresponding DFGAbstraction is kept to calculate the weights on the BPMN model in aligning them
 * with the DFG. For example, when the selected weight is mean frequency, this DFGAbstraction contains mean
 * frequency weights and the weights on the model abstraction is also mean frequency.
 * 
 * @author Bruce Nguyen
 *
 */
public class BPMNAbstraction extends AbstractAbstraction {
	private DFGAbstraction dfgAbstraction;
	
	/**
	 * Create a new BPMNAbstraction of logs.
	 * It's important to note that the BPMNAbstraction is created based on a DFGAbstraction
	 * Therefore, the BPMNAbstraction could be different if the DFGAbstraction has the same structure
	 * but different weights.
	 * @param logDfg
	 * @param params
	 * @param dfgAbstraction: the corresponding DFGAbstraction with the same types of nodes and weights
	 * @throws Exception
	 */
	public BPMNAbstraction(AttributeLog log, DFGAbstraction dfgAbstraction, AbstractionParams params) throws Exception {
		super(log, params);
		this.dfgAbstraction = dfgAbstraction;
		
		//Mine BPMN diagram based on a case frequency DFG
		DFGAbstraction dfgAbsFreq = null;
		if (params.getPrimaryType() == MeasureType.FREQUENCY &&
			params.getPrimaryAggregation() == MeasureAggregation.CASES &&
			params.getPrimaryRelation() == MeasureRelation.ABSOLUTE) {
			dfgAbsFreq = dfgAbstraction;
		}
		else {
			dfgAbsFreq = this.createFrequencyBasedDFGAbstraction(dfgAbstraction);
		}
		this.diagram = mineBPMNDiagram(params, dfgAbsFreq);
		this.updateWeights(params);
	}
	
	public DFGAbstraction getDFGAbstraction() {
		return this.dfgAbstraction;
	}
	
	private DFGAbstraction createFrequencyBasedDFGAbstraction(DFGAbstraction dfgAbs) throws Exception {
		AbstractionParams caseFreqParams = dfgAbs.getAbstractionParams().clone();
		caseFreqParams.setPrimaryMeasure(MeasureType.FREQUENCY, MeasureAggregation.CASES, MeasureRelation.ABSOLUTE);
		caseFreqParams.setSecondary(false); //secondary measure is not needed in mining BPMN
		return new DFGAbstraction(dfgAbs, caseFreqParams);
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
			if (params.getSecondary()) updateArcDurationWeightFromDFG(params, arcSecondaryWeights);
		}
		else {
			updateArcDurationWeightFromDFG(params, arcPrimaryWeights);
			if (params.getSecondary()) updateArcFrequencyWeights(params, nodeSecondaryWeights, arcSecondaryWeights);
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
    		arcWeights.put(edge, this.getEdgeDurationWeightFromDFG(edge, params, arcWeights==this.arcSecondaryWeights));
        }
    }
	
	/**
	 * Duration weight is calculated with some exceptions (zero weights)
	 * @param edge: BPMN edge
	 * @param params
	 * @param secondary: true if this is to compute secondary weights
	 * @return: weight of the BPMN edge taken from the DFG
	 */
	private double getEdgeDurationWeightFromDFG(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge, AbstractionParams params,
										boolean secondary) {
		
		BPMNNode source = edge.getSource();
		BPMNNode target = edge.getTarget();
		if (source instanceof Gateway && target instanceof Gateway) { // gateway to gateway
			return 0d;
		}
		else if (source instanceof Gateway && diagram.getOutEdges(source).size() == 1 &&
    	         target instanceof Activity) { // a merge gateway to an activity
    	    return 0d;
	    }
	    else if (target instanceof Gateway && diagram.getInEdges(target).size() == 1 &&
	             source instanceof Activity) { // an activity to a split gateway
            return 0d;
        }
		
		double[] weightResult = this.mapBPMNToDFGWeight(edge, params, secondary);
		double weight = weightResult[0];
		double count = weightResult[1];
        return (count > 0) ? Math.ceil(1.0*weight/count) : 0d;
        
	}
	
	/**
	 * Frequency weight is mapped to the DFG weights
	 * @param edge: BPMN edge
	 * @param params
	 * @param secondary
	 * @return weight of the BPMN edge taken from the DFG
	 */
	private double getEdgeFrequencyWeightFromDFG(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge, AbstractionParams params,
										boolean secondary) {
		double[] weightResult = this.mapBPMNToDFGWeight(edge, params, secondary);
		return weightResult[0];
	}
	
	
	/**
	 * Compute edge weight by aligning it to the DFG. This alignment is approximate only.
	 * Note that duration has different rule than frequency weight. For example, duration
	 * at non-branching edge of gateways doesn't need to be the sum of all branching edges.
	 * In addition, the duration weight is the average of all component weights.
	 * @param edge
	 * @param params
	 * @param secondary
	 * @return: Return a pair {weight, count} for the input edge
	 */
	private double[] mapBPMNToDFGWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge, AbstractionParams params,
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
        return new double[]{weight, count};
	}
	
	/**
	 * Update arc frequency weights.
	 * The current method starts from adjacent edges of activity nodes and then cascade
	 * to all other edges until all the edges are calculated
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
		// This update ignores BPMN gateway semantics. All gateways are considered as XOR.
		// Be caution of race condition: adjacent edges of nested gateways can create a circular
		// dependency which enters an endless loop to calculate weights for all.
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : this.diagram.getEdges()) {
			edgeWeights.put(edge, this.getEdgeFrequencyWeightFromDFG(edge, params, edgeWeights==this.arcSecondaryWeights));
		}
		
		// Adjust edges to have the same weight as the adjacent node.
		// On the corresponding DFG, edge weight is always less than or equal to node weight due to arc filtering
		for (BPMNNode node : diagram.getNodes()) {
			if (node instanceof Activity) {
				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> inEdge = diagram.getInEdges(node).iterator().next();
				BPMNEdge<? extends BPMNNode, ? extends BPMNNode> outEdge = diagram.getOutEdges(node).iterator().next();
				if (edgeWeights.containsKey(inEdge) && edgeWeights.get(inEdge) != nodeWeights.get(node)) {
					edgeWeights.put(inEdge, nodeWeights.get(node));
				}
				if (edgeWeights.containsKey(outEdge) && edgeWeights.get(outEdge) != nodeWeights.get(node)) {
					edgeWeights.put(outEdge, nodeWeights.get(node));
				}
			}
		}
		
		// Perform weight calculation for edges between gateways
		// This is done based on cascading on the model starting from edges adjacent to activity nodes
        List<BPMNNode> bfsGateways = this.getNodesByBFS(startEvent, new HashSet<BPMNNode>(diagram.getGateways()));
        int totalNumberOfGateways = bfsGateways.size();
        Set<BPMNNode> remainingGateways = new HashSet<>();
        Set<BPMNNode> finishedGateways = new HashSet<>();
        while (finishedGateways.size() < totalNumberOfGateways) {
        	remainingGateways.clear();
        	remainingGateways.addAll(bfsGateways);
        	remainingGateways.removeAll(finishedGateways);
        	for (BPMNNode node : remainingGateways) {
        		UnweightedEdgeCheckResult result = calculateUnweightedEdge((Gateway)node, edgeWeights);
	            if (result.hasUnweightedEdge && !result.hasTooManyUnweightedEdge) {
	            	if (result.unWeightedEdge != null) {
	            		edgeWeights.put(result.unWeightedEdge, result.weight);
	            		finishedGateways.add(node);
	            	}
	            }
	            else if (!result.hasUnweightedEdge) { // don't repeat for a gateway with fully weighted edges
	            	finishedGateways.add(node);
	            }
        	}
        }
		
		// Fix the gateway adjacent to the end event, if exist
		// This is to make the end event look nice
		BPMNEdge<? extends BPMNNode, ? extends BPMNNode> endEdge = diagram.getInEdges(endEvent).iterator().next();
		if (endEdge.getSource() instanceof Gateway) {
			updateGatewayEdgeFrequency(endEdge, (Gateway)endEdge.getSource(), edgeWeights);
		}
	}
	
	private class UnweightedEdgeCheckResult {
		public UnweightedEdgeCheckResult(boolean hasUnweightedEdge, boolean hasTooManyUnweightedEdge,
										BPMNEdge<? extends BPMNNode, ? extends BPMNNode> unWeightedEdge, double weight) {
			this.hasUnweightedEdge = hasUnweightedEdge;
			this.hasTooManyUnweightedEdge = hasTooManyUnweightedEdge;
			this.unWeightedEdge = unWeightedEdge;
			this.weight = weight;
		}
		public boolean hasUnweightedEdge = false;
		public boolean hasTooManyUnweightedEdge = false;
		public BPMNEdge<? extends BPMNNode, ? extends BPMNNode> unWeightedEdge = null;
		public double weight = 0;
	}
	
	// Calculate weight for an edge adjacent to a given gateway
	// Only able to do if there is only one unweighted edge adjacent to the gateway
	// Return a pair: {the edge to update weight, the calculated weight for the edge}
	// Or an empty array if no recommended edge is found to update weight
	private UnweightedEdgeCheckResult calculateUnweightedEdge(Gateway node, Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> edgeWeights) {
		Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> unweightedEdges = new HashSet<>();
		double totalBranchingEdgeWeight = 0;
		double nonBranchingEdgeWeight = -1; // the merging or splitting edge
		double maxBranchingEdgeWeight = 0;
		
		// Check input edges to the gateway
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getInEdges(node)) {
			if (edgeWeights.containsKey(edge)) {
				if (diagram.getInEdges(node).size()==1) { //non-branching edge
					nonBranchingEdgeWeight = edgeWeights.get(edge);
				}
				else {
					totalBranchingEdgeWeight += edgeWeights.get(edge);
					if (edgeWeights.get(edge) > maxBranchingEdgeWeight) {
						maxBranchingEdgeWeight = edgeWeights.get(edge);
					}
				}
			}
			else {
				unweightedEdges.add(edge);
			}
			
			if (unweightedEdges.size() >= 2) { //not able to calculate edge weight if there are 2 or more unweighted edges
				break;
			}
			
		}
		
		// Check output edges from the gateway
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getOutEdges(node)) {
			if (unweightedEdges.size() >= 2) {
				break;
			}
			
			if (edgeWeights.containsKey(edge)) {
				if (diagram.getOutEdges(node).size() == 1) { //non-branching edge
					nonBranchingEdgeWeight = edgeWeights.get(edge);
				}
				else {
					totalBranchingEdgeWeight += edgeWeights.get(edge);
					if (edgeWeights.get(edge) > maxBranchingEdgeWeight) {
						maxBranchingEdgeWeight = edgeWeights.get(edge);
					}
				}
			}
			else {
				unweightedEdges.add(edge);
			}
		}
		
		if (unweightedEdges.size() == 0) {
			return new UnweightedEdgeCheckResult(false, false, null, 0);
		}
		else if (unweightedEdges.size() == 1) { // gateway semantics are considered here
			BPMNEdge<? extends BPMNNode, ? extends BPMNNode> foundEdge = unweightedEdges.iterator().next();
			double foundEdgeWeight = 0;
			if (nonBranchingEdgeWeight == -1) { // the found edge is a non-branching edge
				if (node.getGatewayType() == GatewayType.DATABASED || node.getGatewayType() == GatewayType.INCLUSIVE) {
					foundEdgeWeight = totalBranchingEdgeWeight;
				}
				else if (node.getGatewayType() == GatewayType.PARALLEL) {
					foundEdgeWeight = maxBranchingEdgeWeight;
				}
			}
			else { // the found edge is a branching edge
				if (node.getGatewayType() == GatewayType.DATABASED || node.getGatewayType() == GatewayType.INCLUSIVE) {
					foundEdgeWeight = nonBranchingEdgeWeight - totalBranchingEdgeWeight;
					if (foundEdgeWeight <= 0) foundEdgeWeight = 1d;
				}
				else if (node.getGatewayType() == GatewayType.PARALLEL) {
					foundEdgeWeight = nonBranchingEdgeWeight;
				}
			}
			
			return new UnweightedEdgeCheckResult(true, false, foundEdge, foundEdgeWeight);
		}
		else {
			return new UnweightedEdgeCheckResult(true, true, null, 0);
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
