package org.apromore.processdiscoverer.splitminer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apromore.processdiscoverer.dfg.abstraction.DFGAbstraction;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

import com.raffaeleconforti.splitminer.log.SimpleLog;
import com.raffaeleconforti.splitminer.splitminer.dfgp.DFGEdge;
import com.raffaeleconforti.splitminer.splitminer.dfgp.DFGNode;
import com.raffaeleconforti.splitminer.splitminer.dfgp.DirectlyFollowGraphPlus;

/**
 * This class is used as a bridging DFGP created from a DFGAbstraction
 * The DFGAbstraction here must be based on frequency and the weight 
 * of nodes and arcs must be total (cumulative) as it is used by SplitMiner
 * @author Bruce Nguyen
 *
 */
public class ProcessDiscovererDFGP extends DirectlyFollowGraphPlus {
	private DFGAbstraction dfgAbs;

	public ProcessDiscovererDFGP(SimpleLog log, DFGAbstraction dfgAbs, double percentileFrequencyThreshold, double parallelismsThreshold,
						boolean parallelismsFirst) throws Exception {
		super(log, percentileFrequencyThreshold, parallelismsThreshold, parallelismsFirst);
		this.dfgAbs = dfgAbs;
	}
	
	public DFGAbstraction getDFGAbstraction() {
		return this.dfgAbs;
	}
	
	@Override
	protected void buildDirectlyFollowsGraph() {
		// These variables must be initialized because they will be used
		// in the addNode() and addEdge() method calls. However, this implicit
		// initialization should be avoided as it breaks the class encapsulation
        nodes = new HashMap<>();
        edges = new HashSet<>();
        outgoings = new HashMap<>();
        incomings = new HashMap<>();
        dfgp = new HashMap<>();
        
//        DFGNode autogenStart = new DFGNode(events.get(startcode), startcode);
//        this.addNode(autogenStart);
//        autogenStart.increaseFrequency(log.size());
//        DFGNode  autogenEnd = new DFGNode(events.get(endcode), endcode);
//        this.addNode(autogenEnd);
        
        Map<BPMNNode, DFGNode> nodeMap = new HashMap<>();
		for (BPMNNode node: dfgAbs.getDiagram().getNodes()) {
			DFGNode dfgNode = new DFGNode(node.getLabel(), log.getReverseMap().get(node.getLabel()));
			dfgNode.increaseFrequency((int)dfgAbs.getNodePrimaryWeight(node));
            this.addNode(dfgNode);
            nodeMap.put(node, dfgNode);
		}
		
		for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : dfgAbs.getDiagram().getEdges()) {
			DFGEdge dfgEdge = new DFGEdge(nodeMap.get(edge.getSource()), nodeMap.get(edge.getTarget()));
			dfgEdge.increaseFrequency((int)dfgAbs.getArcPrimaryWeight(edge));
            this.addEdge(dfgEdge);
		}
	}
	
}
