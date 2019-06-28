package org.apromore.processdiscoverer.dfg.abstraction;

import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.dfg.TraceDFG;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;

/**
 * 
 * @author Bruce Nguyen
 *
 */
public class TraceAbstraction extends AbstractAbstraction {
	private TraceDFG traceDfg;
	
	public TraceAbstraction(TraceDFG traceDfg, AbstractionParams params) {
		super(traceDfg.getLogDFG(), params);
		this.traceDfg = traceDfg;
		this.diagram = traceDfg.getDFG(params);
		this.updateWeights(params);
	}
	
	public TraceDFG getTraceDFG() {
		return this.traceDfg;
	}
	
	@Override
	protected void updateNodeWeights(AbstractionParams params) {
		// TODO Auto-generated method stub
	}
	
	@Override
	protected void updateArcWeights(AbstractionParams params) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateWeights(AbstractionParams params) {
		for(BPMNNode node : diagram.getNodes()) {
			String node_name = node.getLabel();
            if (node_name.contains("\\n")) {
                node_name = node_name.substring(0, node_name.indexOf("\\n"));
                String value = node.getLabel().substring(node.getLabel().indexOf("[") + 1, node.getLabel().length() - 1);
                nodePrimaryWeights.put(node, Double.parseDouble(value));
            }else {
            	nodePrimaryWeights.put(node, 0.0);
            }
            nodeSecondaryWeights.put(node, 1.0);
		}
		
		for (BPMNEdge edge: diagram.getEdges()) {
			String mainNumber = edge.getLabel();
            String secondaryNumber= "";
            if(mainNumber.contains("[")) {
                if(mainNumber.contains("\n")) {
                    secondaryNumber = mainNumber.substring(mainNumber.indexOf("\n"), mainNumber.length() - 1);
                    mainNumber = mainNumber.substring(1, mainNumber.indexOf("\n"));
                }else mainNumber = mainNumber.substring(1, mainNumber.length() - 1);
            }else {
                mainNumber = "1";
                secondaryNumber = "1";
            }
            mainNumber = fixNumber(mainNumber);
            arcPrimaryWeights.put(edge, Double.parseDouble(mainNumber));
            arcSecondaryWeights.put(edge, secondaryNumber.isEmpty() ? 0 : Double.parseDouble(secondaryNumber));
		}
		
	}


}
