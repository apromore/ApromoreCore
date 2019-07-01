package org.apromore.processdiscoverer.dfg.abstraction;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.LogDFG;
import org.apromore.processdiscoverer.dfg.collectors.NodeInfoCollector;
import org.apromore.processdiscoverer.dfg.vis.Layout;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class AbstractAbstraction implements Abstraction {
	protected BPMNDiagram diagram;
	protected Map<BPMNNode,Double> nodePrimaryWeights = new HashMap<>();
	protected Map<BPMNNode,Double> nodeSecondaryWeights = new HashMap<>();
	protected Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> arcPrimaryWeights = new HashMap<>();
	protected Map<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>,Double> arcSecondaryWeights = new HashMap<>();
	protected AbstractionParams params;
	protected LogDFG logDfg;
	protected Map<BPMNNode, Point> nodeLayoutMap;
	protected Layout layout;
	
	public AbstractAbstraction(LogDFG logDfg, AbstractionParams params) {
		this.logDfg = logDfg;
		this.params = params;
	}
	
	public LogDFG getLogDFG() {
		return this.logDfg;
	}
	
	protected void updateNodeWeights(AbstractionParams params) throws Exception {
		nodePrimaryWeights.clear();
		nodeSecondaryWeights.clear();
		
		NodeInfoCollector nodeInfoCollector = logDfg.getNodeInfoCollector();
		for (BPMNNode node: diagram.getNodes()) {
			if (node instanceof Activity) {
				if (params.getPrimaryType() == VisualizationType.FREQUENCY) {
					nodePrimaryWeights.put(node, nodeInfoCollector.getNodeFrequency(false, node.getLabel(), params.getPrimaryAggregation()));
				}
				else {
					nodePrimaryWeights.put(node, nodeInfoCollector.getNodeDuration(node.getLabel(), params.getPrimaryAggregation()));
				}
				
				if (params.getSecondary()) {
					if (params.getSecondaryType() == VisualizationType.FREQUENCY) {
						nodeSecondaryWeights.put(node, nodeInfoCollector.getNodeFrequency(false, node.getLabel(), params.getSecondaryAggregation()));
					}
					else {
						nodeSecondaryWeights.put(node, nodeInfoCollector.getNodeDuration(node.getLabel(), params.getSecondaryAggregation()));
					}
				}
			}
		}
	}
	
	protected abstract void updateArcWeights(AbstractionParams params);
	
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
	
    protected String fixNumber(String number) {
        try {
            Double.parseDouble(number);
        }catch (NumberFormatException nfe) {
            number = "0";
        }
        return number;
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
    
    protected Layout getLayout() {
    	return this.layout;
    }

}
