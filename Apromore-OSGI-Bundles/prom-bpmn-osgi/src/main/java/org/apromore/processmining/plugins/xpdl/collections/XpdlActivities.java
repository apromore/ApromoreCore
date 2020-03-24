package org.apromore.processmining.plugins.xpdl.collections;

import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.plugins.xpdl.idname.XpdlActivity;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Activities"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Activity" minOccurs="0" maxOccurs="unbounded">
 *         <xsd:annotation> <xsd:documentation>BPMN: corresponds to a flow
 *         object, which could be a BPMN activity, gateway, or
 *         event</xsd:documentation> </xsd:annotation> </xsd:element> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlActivities extends XpdlCollections<XpdlActivity> {

	public XpdlActivities(String tag) {
		super(tag);
	}

	public XpdlActivity create() {
		return new XpdlActivity("Activity");
	}

	public void convertToBpmn(BPMNDiagram bpmn, DirectedGraphNode parent, XpdlActivitySets activitySets,
			Map<String, BPMNNode> id2node) {
		for (XpdlActivity activity : list) {
			activity.convertToBpmn(bpmn, parent, activitySets, id2node);
		}
	}
}
