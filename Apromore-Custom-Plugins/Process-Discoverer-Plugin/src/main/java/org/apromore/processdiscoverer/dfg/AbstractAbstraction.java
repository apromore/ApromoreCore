package org.apromore.processdiscoverer.dfg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.collectors.NodeInfoCollector;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

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
	
	public AbstractAbstraction(LogDFG logDfg, AbstractionParams params) {
		this.logDfg = logDfg;
		this.params = params;
	}
	
	protected void updateNodeWeights(AbstractionParams params) {
		nodePrimaryWeights.clear();
		nodeSecondaryWeights.clear();
		
		NodeInfoCollector nodeInfoCollector = logDfg.getNodeInfoCollector();
		for (BPMNNode node: diagram.getNodes()) {
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
	
	protected abstract void updateArcWeights(AbstractionParams params);
	
	protected void updateWeights(AbstractionParams params) {
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

}
