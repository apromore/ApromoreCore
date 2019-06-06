package org.apromore.processdiscoverer.dfg;

import java.util.Set;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.collectors.ArcInfoCollector;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramBuilder;
import org.apromore.processdiscoverer.logprocessors.LogUtils;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.bpmn.elements.Gateway;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public class BPMNAbstraction extends AbstractAbstraction {
	public BPMNAbstraction(LogDFG logDfg, AbstractionParams params) throws Exception {
		super(logDfg, params);
		this.diagram = logDfg.getBPMN(params);
		this.updateWeights(params);
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
		
        //----------------------------------------------------
        // The arcs on the discovered BPMN diagram are mapped with 
        // the arcs in the ArcInfoCollector to compute its weight.
        // It concludes that if there is an arc from A to B on the model,
        // it corresponds to a set of arcs on the DFG with source=A and target=B
        //----------------------------------------------------
    	ArcInfoCollector arcInfoCollector = this.logDfg.getArcInfoCollector();

    	for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : this.diagram.getEdges()) {
            BPMNNode source = edge.getSource();
            BPMNNode target = edge.getTarget();
            //--------------------------------------
            // Search for all directly-follows relations between source and target
            // Each directly-follows relation is called a "connecting arc" which
            // are stored in ArcInfoCollector. Given that source and target can be gateways,
            // connecting arcs are used to compute the total directly-follows count 
            // between source and target.
            //--------------------------------------
            Set<BPMNNode> sources = BPMNDiagramBuilder.getSources(this.diagram, edge.getSource(), new UnifiedSet<>());
            Set<BPMNNode> targets = BPMNDiagramBuilder.getTargets(this.diagram, edge.getTarget(), new UnifiedSet<>());
            Set<Arc> connecting_arcs = new UnifiedSet<>();
            SimplifiedLog log = this.logDfg.getSimplifiedLog();
            for (BPMNNode s : sources) {
                for (BPMNNode t : targets) {
                    Integer source_int = log.getEventNumber(this.getNodeLabel(s, false));
                    Integer target_int = log.getEventNumber(this.getNodeLabel(t, false));
                    Arc arc = new Arc(source_int, target_int);
                    if (source_int != null && target_int != null && arcInfoCollector.exists(arc))
                        connecting_arcs.add(arc);
                    if (log.containStartEvent() && log.containCompleteEvent()) {
                        source_int = log.getEventNumber(this.getNodeLabel(s, false));
                        target_int = log.getEventNumber(this.getNodeLabel(t, true));
                        if (source_int != null && target_int != null) {
                            arc = new Arc(source_int, target_int);
                            if (arcInfoCollector.exists(arc)) {
                                connecting_arcs.add(arc);
                            }
                        }
                    }

                }
            }
            this.arcPrimaryWeights.put(edge, 
            		this.computeCost(connecting_arcs, source, target, params.getPrimaryType(), params.getPrimaryAggregation()));
            if (params.getSecondary()) {
            	this.arcSecondaryWeights.put(edge, 
            		this.computeCost(connecting_arcs, source, target, params.getSecondaryType(), params.getSecondaryAggregation()));
            }
        }
    }
	
	private String getNodeLabel(BPMNNode node, boolean startcode) {
		if (node instanceof Activity) {
    		return node.getLabel() + (startcode ? LogUtils.PLUS_START_CODE.toString() : LogUtils.PLUS_COMPLETE_CODE.toString());
    	}
    	else if (node instanceof Event && ((Event) node).getEventType() == Event.EventType.START) {
    		return SimplifiedLog.START_NAME;
    	}
    	else if (node instanceof Event && ((Event) node).getEventType() == Event.EventType.END) {
    		return SimplifiedLog.END_NAME;
    	}
    	else {
    		return node.getLabel();
    	}
	}
    
    private double computeCost(Set<Arc> arcs, BPMNNode source, BPMNNode target, VisualizationType type, VisualizationAggregation aggregation) {
        double cost = 0;
//        IntHashSet sources = new IntHashSet();
//        IntHashSet targets = new IntHashSet();
//        for(Arc arc : arcs) {
//            sources.add(arc.getSource());
//            targets.add(arc.getTarget());
//        }
        ArcInfoCollector arcInfoCollector = this.logDfg.getArcInfoCollector();
        if(source instanceof Activity && target instanceof Activity) {
            for(Arc arc : arcs)
                cost += arcInfoCollector.getArcInfo(arc, type, aggregation);
            cost /= arcs.size();
        }else {
            double source_cost = computeCost(arcs, source, type, aggregation);
            double target_cost = computeCost(arcs, target, type, aggregation);
            cost = Math.max(source_cost, target_cost);
        }

        return cost;
    }
    
    private double computeCost(Set<Arc> arcs, BPMNNode node, VisualizationType type, VisualizationAggregation aggregation) {
        double cost = 0;
        ArcInfoCollector arcInfoCollector = this.logDfg.getArcInfoCollector();
        if(node instanceof Gateway) {
            if (((Gateway) node).getGatewayType() == Gateway.GatewayType.PARALLEL) {
                for (Arc arc : arcs) cost = Math.max(cost, arcInfoCollector.getArcInfo(arc, type, aggregation));
            } else {
                for (Arc arc : arcs) cost += arcInfoCollector.getArcInfo(arc, type, aggregation);
            }
        }else {
            for (Arc arc : arcs) cost += arcInfoCollector.getArcInfo(arc, type, aggregation);
        }
        return cost;
    }

}
