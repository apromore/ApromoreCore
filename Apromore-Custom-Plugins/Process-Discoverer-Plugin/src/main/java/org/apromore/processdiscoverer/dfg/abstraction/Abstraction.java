package org.apromore.processdiscoverer.dfg.abstraction;

import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

/**
 * Abstraction represents an abstraction of log or trace.
 * It can be a directly-follows graph or a BPMN diagram model
 * In addition, it is affected by different abstraction parameters
 * The DFG or BPMN model is stored in a diagram.
 * The nodes and edges on the diagram can be two types of weights: primary and secondary
 * and they can be displayed at the same time 
 * @author Bruce Nguyen
 *
 */
public interface Abstraction {
	AbstractionParams getAbstractionParams();
	BPMNDiagram getDiagram();
	double getNodePrimaryWeight(BPMNNode node);
	double getNodeSecondaryWeight(BPMNNode node);
	double getArcPrimaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge);
	double getArcSecondaryWeight(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge);
}
