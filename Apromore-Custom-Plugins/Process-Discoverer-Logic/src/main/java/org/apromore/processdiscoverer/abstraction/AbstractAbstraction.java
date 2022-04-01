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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apromore.logman.attribute.log.AttributeLog;
import org.apromore.processdiscoverer.Abstraction;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.layout.Layout;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;

/**
 * Generic Abstraction for an {@link AttributeLog}.
 * Uses a {@link BPMNDiagram} to commonly represent process maps and process models. 
 * Two weights can be defined on nodes and arcs called primary and secondary weights. These weights
 * are provided from the underlying AttributeLog. 
 * The abstraction has a layout applied on the BPMNDiagram provided from a layout engine.
 *   
 * @author Bruce Nguyen
 * 
 */
public abstract class AbstractAbstraction implements Abstraction {
    protected AttributeLog log;
	protected BPMNDiagram diagram;
	protected Map<BPMNNode,Double> nodePrimaryWeights = new HashMap<>();
	protected Map<BPMNNode,Double> nodeSecondaryWeights = new HashMap<>();
	protected Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> arcPrimaryWeights = new HashMap<>();
	protected Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> arcSecondaryWeights = new HashMap<>();
	protected AbstractionParams params;
	protected Layout layout;
	
	protected double minNodePrimaryWeight = Double.MAX_VALUE;
	protected double maxNodePrimaryWeight = 0;
	protected double minArcPrimaryWeight = Double.MAX_VALUE;
	protected double maxArcPrimaryWeight = 0;
	
	protected MutableObjectIntMap<BPMNNode> nodeIdMapping = ObjectIntMaps.mutable.empty();
	
	public AbstractAbstraction(AttributeLog log, AbstractionParams params) {
		this.log = log;
		this.params = params;
	}
	
	public AttributeLog getLog() {
	    return this.log;
	} 
	
	@Override
	public int getNodeId(BPMNNode node) {
		return nodeIdMapping.getIfAbsent(node, -1);
	}
	
	protected void updateNodeWeights(AbstractionParams params) throws Exception {
		nodePrimaryWeights.clear();
		nodeSecondaryWeights.clear();
		minNodePrimaryWeight = Double.MAX_VALUE;
		maxNodePrimaryWeight = 0;
		nodeIdMapping.clear();
		
		int nodeId = 0;
		for (BPMNNode node: diagram.getNodes()) {
			if (node instanceof Activity) {
				double nodePrimaryWeight = log.getGraphView().getNodeWeight(node.getLabel(), params.getPrimaryType(), 
						params.getPrimaryAggregation(), params.getPrimaryRelation());
				nodePrimaryWeights.put(node, nodePrimaryWeight);
				maxNodePrimaryWeight = Math.max(maxNodePrimaryWeight, nodePrimaryWeight);
            	minNodePrimaryWeight = Math.min(minNodePrimaryWeight, nodePrimaryWeight);
				if (params.getSecondary()) {
					nodeSecondaryWeights.put(node, log.getGraphView().getNodeWeight(node.getLabel(), params.getSecondaryType(), 
							params.getSecondaryAggregation(), params.getSecondaryRelation()));
				}
			}
			
			nodeIdMapping.put(node, nodeId);
			nodeId++;
		}
	}
	
	protected abstract void updateArcWeights(AbstractionParams params) throws Exception;
	
	public void updateWeights(AbstractionParams params) throws Exception {
		if (this.diagram == null) return;
		this.params = params;
		this.updateNodeWeights(params);
		this.updateArcWeights(params);
	}
	
	@Override
	public AbstractionParams getAbstractionParams() {
		return this.params;
	}

	@Override
	public BPMNDiagram getDiagram() {
		return this.diagram;
	}

	@Override
	public double getNodePrimaryWeight(BPMNNode node) {
		return nodePrimaryWeights.containsKey(node) ? nodePrimaryWeights.get(node) : 0;
	}

	@Override
	public double getNodeSecondaryWeight(BPMNNode node) {
		return nodeSecondaryWeights.containsKey(node) ? nodeSecondaryWeights.get(node) : 0;
	}

	@Override
	public double getArcPrimaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
		return arcPrimaryWeights.containsKey(edge) ? arcPrimaryWeights.get(edge) : 0;
	}

	@Override
	public double getArcSecondaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
		return arcSecondaryWeights.containsKey(edge) ? arcSecondaryWeights.get(edge) : 0;
	}
	
	@Override
	public double getMinNodePrimaryWeight() {
		return minNodePrimaryWeight;
	}
	
	@Override
	public double getMaxNodePrimaryWeight() {
		return maxNodePrimaryWeight;
	}
	
	@Override
	public double getMinEdgePrimaryWeight() {
		return minArcPrimaryWeight;
	}
	
	@Override
	public double getMaxEdgePrimaryWeight() {
		return maxArcPrimaryWeight;
	}
	
	@Override
	public double getNodeRelativePrimaryWeight(BPMNNode node) {
		double min = getMinNodePrimaryWeight();
    	double max = getMaxNodePrimaryWeight();
    	double node_weight = getNodePrimaryWeight(node);
    	double node_percentage_weight = 0;
    	if (min==max || max==0) {
    		node_percentage_weight = -1;
    	}
    	else {
    		node_percentage_weight = (node_weight - min) / (max - min);
    		if(Double.isNaN(node_percentage_weight)) node_percentage_weight = -1; // in case min and max are too small
    	}
        return node_percentage_weight;
	}
	
	@Override
	// Return -1 if overflow
	public double getEdgeRelativePrimaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge) {
		double min = getMinEdgePrimaryWeight();
    	double max = getMaxEdgePrimaryWeight();
    	double edge_weight = getArcPrimaryWeight(edge);
    	double edge_percentage_weight = 0;
    	if (max==0) {
    		edge_percentage_weight = -1;
    	}
    	else if (min==max) {
    	    edge_percentage_weight = -1;
    	}
    	else {
    		edge_percentage_weight = (edge_weight - min) / (max - min);
    		if(Double.isNaN(edge_percentage_weight)) edge_percentage_weight = -1; // in case min and max are too small
    	}
        return edge_percentage_weight;
	}
    
    //Breadth-first traversal from a given source s and a given set of in-scope nodes
    protected List<BPMNNode> getNodesByBFS(BPMNNode source, Collection<BPMNNode> selectedNodes) {
        // Mark all the vertices as not visited(By default set as false) 
    	Map<BPMNNode, Boolean> visited = new HashMap<>();
    	for (BPMNNode node: diagram.getNodes()) {
    		visited.put(node, false);
    	}
  
        // Create a queue for BFS 
        LinkedList<BPMNNode> queue = new LinkedList<BPMNNode>(); 
  
        // Mark the current node as visited and enqueue it 
        visited.put(source, Boolean.TRUE); 
        queue.add(source);

        // BFS by queue and collect traversed nodes in order 
        List<BPMNNode> traversedNodes = new ArrayList<>();
        BPMNNode queueHead = null;
        while (queue.size() != 0) { 
        	queueHead = queue.poll(); 
        	if (selectedNodes.contains(queueHead)) traversedNodes.add(queueHead);
            // Get all adjacent vertices of the dequeued vertex s 
            // If a adjacent has not been visited, then mark it visited and enqueue it 
            for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getOutEdges(queueHead)) {
            	BPMNNode adjacentNode = edge.getTarget();
                if (!visited.get(adjacentNode)) { 
                    visited.put(adjacentNode, true); 
                    queue.add(adjacentNode); 
                } 
            }
        }
        
        return traversedNodes;
    } 
    
    @Override
    public Layout getLayout() {
    	return this.layout;
    }
    
    @Override
    public void setLayout(Layout layout) {
        if (this.layout != null) this.layout.cleanUp();
        this.layout = layout;
    }
    
    @Override
    public boolean equal(Abstraction other) {
        // Check the same diagram structures
        MutableMap<BPMNNode, BPMNNode> nodeMapping = Maps.mutable.empty(); 
        MutableMap<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>, BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edgeMapping = Maps.mutable.empty();
        if (!this.getDiagram().checkSimpleEqualityWithMapping(other.getDiagram(), nodeMapping, edgeMapping)) {
            return false;
        }
        
        // Check node and edge weights
        for (BPMNNode node: nodeMapping) {
            if (nodePrimaryWeights.get(node) != other.getNodePrimaryWeight(nodeMapping.get(node))) {
                return false;
            }
        }
        for (BPMNNode node: nodeMapping) {
            if (getNodePrimaryWeight(node) != other.getNodePrimaryWeight(nodeMapping.get(node))) {
                return false;
            }
            if (getNodeSecondaryWeight(node) != other.getNodeSecondaryWeight(nodeMapping.get(node))) {
                return false;
            }
        }
        for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: edgeMapping) {
            if (getArcPrimaryWeight(edge) != other.getArcPrimaryWeight(edgeMapping.get(edge))) {
                return false;
            }
            if (getArcSecondaryWeight(edge) != other.getArcSecondaryWeight(edgeMapping.get(edge))) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public void cleanUp() {
        nodePrimaryWeights.clear();
        nodeSecondaryWeights.clear();
        arcPrimaryWeights.clear();
        arcSecondaryWeights.clear();
        if (layout != null) layout.cleanUp();
    }
    
}
