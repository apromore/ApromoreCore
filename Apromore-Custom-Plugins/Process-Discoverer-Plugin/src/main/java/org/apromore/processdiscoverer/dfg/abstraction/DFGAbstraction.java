package org.apromore.processdiscoverer.dfg.abstraction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.Arc;
import org.apromore.processdiscoverer.dfg.LogDFG;
import org.apromore.processdiscoverer.dfg.collectors.ArcInfoCollector;
import org.apromore.processdiscoverer.dfg.collectors.FrequencySetPopulator;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramLayouter;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;

/**
 * This class represents the directly-follows graph abstraction of logs
 * @author Bruce Nguyen
 *
 */
public class DFGAbstraction extends AbstractAbstraction {
	public DFGAbstraction(LogDFG logDfg, AbstractionParams params) throws Exception {
		super(logDfg, params);
		this.diagram = logDfg.getDFG(params);
		this.updateWeights(params);
		this.layout = BPMNDiagramLayouter.layout(this.diagram);
	}
	
	// Create a DFGAbstraction based on an existing DFGAbstraction
	// The diagram would be the same as the input DFGAbstraction, but the
	// weights and other parameters are different
	public DFGAbstraction(DFGAbstraction dfgAbs, AbstractionParams params) throws Exception {
		super(dfgAbs.getLogDFG(), params);
		this.diagram = dfgAbs.getDiagram();
		this.updateWeights(params);
	}
	

	@Override
	protected void updateArcWeights(AbstractionParams params) {
		arcPrimaryWeights.clear();
		arcSecondaryWeights.clear();
		
		ArcInfoCollector arcInfoCollector = logDfg.getArcInfoCollector();
		SimplifiedLog log = this.logDfg.getSimplifiedLog();
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getEdges()) {
			List<Integer> sources = log.getCollapsedNameMapping().get(edge.getSource().getLabel());
			List<Integer> targets = log.getCollapsedNameMapping().get(edge.getTarget().getLabel());
			Set<Arc> foundArcs = new HashSet<>();
			for (int source: sources) {
				for (int target: targets) {
					Arc arc = logDfg.getArc(source, target);
					if (arc != null) {
						if (logDfg.isAcceptedArc(source, target, params)) {
							foundArcs.add(arc);
						}
					}
				}
			}
			
			LongArrayList primaryPopulation = new LongArrayList();
			for (Arc arc : foundArcs) {
				primaryPopulation.addAll(arcInfoCollector.getArcMeasurePopulation(arc, params.getPrimaryType()));
			}
			if (!primaryPopulation.isEmpty()) {
				arcPrimaryWeights.put(edge, FrequencySetPopulator.getAggregateInformation(primaryPopulation, params.getPrimaryAggregation()));
			}
			
			if (params.getSecondary()) {
				LongArrayList secondaryPopulation = new LongArrayList();
				for (Arc arc : foundArcs) {
					secondaryPopulation.addAll(arcInfoCollector.getArcMeasurePopulation(arc, params.getSecondaryType()));
				}
				if (!secondaryPopulation.isEmpty()) {
					arcSecondaryWeights.put(edge, FrequencySetPopulator.getAggregateInformation(secondaryPopulation, params.getSecondaryAggregation()));
				}
			}
			
		}
	}
	
	//Return null if not found
	public Double getEdgeWeight(String sourceLabel, String targetLabel, boolean secondary) {
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getEdges()) {
			if (edge.getSource().getLabel().equals(sourceLabel) && edge.getTarget().getLabel().equals(targetLabel)) {
				if (!secondary) {
					return arcPrimaryWeights.get(edge);
				}
				else {
					return arcSecondaryWeights.get(edge);
				}
			}
		}
		return null;
	}
	
	public double getTotalStartEventWeight(boolean secondary) {
		double total = 0d;
		for (Event eventNode: diagram.getEvents()) {
			if (eventNode.getEventType() == Event.EventType.START) {
				for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge: diagram.getOutEdges(eventNode)) {
					total += (secondary ? arcSecondaryWeights.get(edge) : arcPrimaryWeights.get(edge)); 
				}
			}
		}
		return total;
	}
}
