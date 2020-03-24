package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.ContainableDirectedGraphElement;
import org.apromore.processmining.models.graphbased.directed.ContainingDirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;
import org.xmlpull.v1.XmlPullParser;

public class BpmnLane extends BpmnIdName {

	private Collection<BpmnText> flowNodeRefs;
	private BpmnLaneSet childLaneSet;
	private String partitionElement;
	
	public BpmnLane(String tag) {
		super(tag);
		
		flowNodeRefs = new HashSet<BpmnText>();
	}
	
	protected void importAttributes(XmlPullParser xpp, Bpmn bpmn) {
		super.importAttributes(xpp, bpmn);
		String value = xpp.getAttributeValue(null, "partitionElement");
		if (value != null) {
			partitionElement = value;
		}
	}

	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element.
			 */
			return true;
		}
		if (xpp.getName().equals("childLaneSet")) {
			BpmnLaneSet laneSet = new BpmnLaneSet("childLaneSet");
			laneSet.importElement(xpp, bpmn);
			childLaneSet = laneSet;
			return true;
		} else if(xpp.getName().equals("flowNodeRef")) {
			BpmnText flowNodeRef = new BpmnText("flowNodeRef");
			flowNodeRef.importElement(xpp, bpmn);
			flowNodeRefs.add(flowNodeRef);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if(partitionElement != null) {
			s += exportAttribute("partitionElement", partitionElement);
		}
		
		return s;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		if(childLaneSet != null) {
			s +=childLaneSet.exportElement();
		}
		for (BpmnText flowNodeRef : flowNodeRefs) {
			s += flowNodeRef.exportElement();
		}
		return s;
	}

	public void unmarshall(BPMNDiagram diagram, Map<String, BPMNNode> id2node,
			Map<String, Swimlane> id2lane, ContainingDirectedGraphNode parent) {
		Swimlane lane = diagram.addSwimlane(name, parent, SwimlaneType.LANE);
		lane.setPartitionElement(partitionElement);
		id2lane.put(id, lane);
		if(childLaneSet != null) {
			childLaneSet.unmarshall(diagram, id2node, id2lane, lane);
		}
		if(flowNodeRefs != null) {
			for(BpmnText flowNodeRef : flowNodeRefs) {
				String id = flowNodeRef.getText();
				BPMNNode node = id2node.get(id);
				if(node!=null){
				if(parent != null) {
					if (parent instanceof SubProcess) {
						((SubProcess)parent).getChildren().remove(node);
					}
				}
				node.setParentSubprocess(null);
				node.setParentSwimlane(lane);
				}
			}
		}
	}

	public void unmarshall(BPMNDiagram diagram, Collection<String> elements, Map<String, BPMNNode> id2node,
			Map<String, Swimlane> id2lane, ContainingDirectedGraphNode parent) {
		Swimlane lane = diagram.addSwimlane(name, parent, SwimlaneType.LANE);
		lane.setPartitionElement(partitionElement);
		id2lane.put(id, lane);
		if(childLaneSet != null) {
			childLaneSet.unmarshall(diagram, elements, id2node, id2lane, lane);
		}
		if(flowNodeRefs != null) {
			for(BpmnText flowNodeRef : flowNodeRefs) {
				String id = flowNodeRef.getText();
				BPMNNode node = id2node.get(id);
				if(node!=null){
				node.setParentSubprocess(null);
				node.setParentSwimlane(lane);
				}
			}
		}
	}
	
	/**
	 * Constructs a process model from a diagram
	 * 
	 * @param diagram
	 * @param lane
	 * @return "true" if at least one element has been added
	 */
	public boolean marshall(BPMNDiagram diagram, Swimlane swimlane) {
		
		super.marshall(swimlane);		
		
		partitionElement = swimlane.getPartitionElement();
		
		// Marshall flowNodeRefs
		flowNodeRefs.clear();
		boolean hasChildLanes = false;
		for (ContainableDirectedGraphElement child : swimlane.getChildren()) {	
			if(child instanceof BPMNNode) {
				BpmnText bpmnFlowNodeRef = new BpmnText("flowNodeRef");
				bpmnFlowNodeRef.setText(((BPMNNode)child).getId().toString().replace(' ', '_'));
				flowNodeRefs.add(bpmnFlowNodeRef);
			}
			if(child instanceof Swimlane) {
				hasChildLanes = true;
			}
		}
		
		if(hasChildLanes) {
			childLaneSet = new BpmnLaneSet("childLaneSet");
			childLaneSet.marshall(diagram, swimlane);
		}
		
		return !(flowNodeRefs.isEmpty()) || (childLaneSet != null);
	}
	
	public BpmnLaneSet getChildLaneSet() {
		return childLaneSet;
	}
	
	public Collection<BpmnText> getFlowNodeRef() {
		return flowNodeRefs;
	}
}
