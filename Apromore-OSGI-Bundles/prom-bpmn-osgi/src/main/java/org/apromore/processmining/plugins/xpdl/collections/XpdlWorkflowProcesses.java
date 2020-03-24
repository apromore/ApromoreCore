package org.apromore.processmining.plugins.xpdl.collections;

import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.plugins.xpdl.idname.XpdlWorkflowProcess;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="WorkflowProcesses"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:WorkflowProcess" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlWorkflowProcesses extends XpdlCollections<XpdlWorkflowProcess> {

	public XpdlWorkflowProcesses(String tag) {
		super(tag);
	}

	public XpdlWorkflowProcess create() {
		return new XpdlWorkflowProcess("WorkflowProcess");
	}

	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node, XpdlPools pools) {
		for (XpdlWorkflowProcess workflowProcess : list) {
			workflowProcess.convertToBpmn(bpmn, id2node, pools);
		}
//		if (!list.isEmpty()) {
//		list.get(0).convertToBpmn(bpmn, id2node);
//	}
	}
}
