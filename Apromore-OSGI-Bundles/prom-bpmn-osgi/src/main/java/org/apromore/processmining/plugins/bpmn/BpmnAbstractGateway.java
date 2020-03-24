package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Gateway;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public abstract class BpmnAbstractGateway extends BpmnIncomingOutgoing {

	private String gatewayDirection;
	private String defaultFlow;
	
	public BpmnAbstractGateway(String tag) {
		super(tag);
		
		gatewayDirection = null;
		defaultFlow = null;
	}
	
	public void marshall(BPMNDiagram diagram, Gateway gateway){
		super.marshall(gateway);
		int incoming = 0;
		int outgoing = 0;
		for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> e: diagram.getEdges()){
			if(e.getTarget().equals(gateway)){
				BpmnIncoming in = new BpmnIncoming("incoming");
				in.setText(e.getEdgeID().toString().replace(" ", "_"));
				incomings.add(in);
				incoming++;
			}
			if(e.getSource().equals(gateway)){
				BpmnOutgoing out = new BpmnOutgoing("outgoing");
				out.setText(e.getEdgeID().toString().replace(" ", "_"));
				outgoings.add(out);
				outgoing++;
			}
		}
		if(incoming > 1 && outgoing > 1){
			gatewayDirection = "Mixed";
		}
		else if(incoming == 1 && outgoing > 1){
			gatewayDirection = "Diverging";
		}
		else if(incoming > 1 && outgoing == 1){
			gatewayDirection = "Converging";
		}
		else{
			gatewayDirection = "Unspecified";
		}
		if(gateway.getDefaultFlow() != null){
			defaultFlow = gateway.getDefaultFlow().getEdgeID().toString().replace(" ", "_");
		}
	}

	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "gatewayDirection");
		if (value != null) {
			gatewayDirection = value;
		}
		value = xpp.getAttributeValue(null, "default");
		if (value != null) {
			defaultFlow = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (gatewayDirection != null) {
			s += exportAttribute("gatewayDirection", gatewayDirection);
		}
		if(defaultFlow != null){
			s += exportAttribute("default", defaultFlow);
		}
		return s;
	}

	public abstract void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Swimlane lane);

	public abstract void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, Swimlane lane);
	
	public abstract void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, SubProcess subProcess);

	public abstract void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node, SubProcess subProcess);
}
