package org.apromore.processmining.plugins.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.xmlpull.v1.XmlPullParser;

public class BpmnLaneSet extends BpmnIdName {

	private Collection<BpmnLane> lanes;
	
	public BpmnLaneSet(String tag) {
		super(tag);
		lanes = new ArrayList<BpmnLane>();
		id="node_" + lanes.hashCode();
	}

	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element.
			 */
			return true;
		}
		if (xpp.getName().equals("lane")) {
			BpmnLane lane = new BpmnLane("lane");
			lane.importElement(xpp, bpmn);
			lanes.add(lane);
			return true;
		} 
		/*
		 * Unknown tag.
		 */
		return false;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (BpmnLane lane : lanes) {
			s += lane.exportElement();
		}
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node, Map<String, Swimlane> id2lane,
			ContainingDirectedGraphNode parent) {
		for (BpmnLane bpmnLane : lanes) {
			bpmnLane.unmarshall(diagram, id2node, id2lane, parent);
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node,
			Map<String, Swimlane> id2lane, ContainingDirectedGraphNode parent) {
		for (BpmnLane bpmnLane : lanes) {
			bpmnLane.unmarshall(diagram, elements, id2node, id2lane, parent);
		}
	}
	
	/**
	 * Constructs a process model from diagram
	 * 
	 * @param diagram
	 * @param parent
	 * @return "true" if at least one element has been added
	 */
	public boolean marshall(BPMNDiagram diagram, ContainingDirectedGraphNode parent) {
		
		lanes.clear();
		
		// Marshall lanes
		for (Swimlane lane : diagram.getLanes(parent)) {
			BpmnLane bpmnLane = new BpmnLane("lane");
			bpmnLane.marshall(diagram, lane);
			lanes.add(bpmnLane);
		}	
		return !(lanes.isEmpty());
	}
	
	public Collection<BpmnLane> getAllChildLanes() {
		Collection<BpmnLane> allLanes = new HashSet<BpmnLane>();
		allLanes.addAll(lanes);
		for(BpmnLane lane : lanes) {
			if(lane.getChildLaneSet() != null) {
				Collection<BpmnLane> tempLanes = lane.getChildLaneSet().getAllChildLanes();
				allLanes.addAll(tempLanes);
			}
		}
		return allLanes;
	}
}
