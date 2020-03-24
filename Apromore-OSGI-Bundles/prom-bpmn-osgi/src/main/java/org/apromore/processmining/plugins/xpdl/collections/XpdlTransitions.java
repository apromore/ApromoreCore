package org.apromore.processmining.plugins.xpdl.collections;

import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.plugins.xpdl.idname.XpdlTransition;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Transitions"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Transition" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlTransitions extends XpdlCollections<XpdlTransition> {

	public XpdlTransitions(String tag) {
		super(tag);
	}

	public XpdlTransition create() {
		return new XpdlTransition("Transition");
	}

	public void convertToBpmn(BPMNDiagram bpmn, DirectedGraphNode parent, XpdlActivitySets activitySets,
			Map<String, BPMNNode> id2node) {
		for (XpdlTransition transition : list) {
			transition.convertToBpmn(bpmn, parent, activitySets, id2node);
		}
	}
}
