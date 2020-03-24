package org.apromore.processmining.plugins.xpdl;

import java.util.LinkedList;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.deprecated.XpdlTool;
import org.apromore.processmining.plugins.xpdl.idname.XpdlSubFlow;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Implementation"> <xsd:complexType> <xsd:choice
 *         minOccurs="0"> <xsd:element ref="xpdl:No" minOccurs="0">
 *         <xsd:annotation> <xsd:documentation>BPMN: corresponds to a task with
 *         unspecified TaskType</xsd:documentation> </xsd:annotation>
 *         </xsd:element> <xsd:element ref="deprecated:Tool" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:element ref="xpdl:Task" minOccurs="0">
 *         <xsd:annotation> <xsd:documentation>BPMN: corresponds to a task with
 *         specified TaskType</xsd:documentation> </xsd:annotation>
 *         </xsd:element> <xsd:element ref="xpdl:SubFlow" minOccurs="0">
 *         <xsd:annotation> <xsd:documentation>BPMN: corresponds to Reusable
 *         subprocess. May run in different pool or same
 *         pool.</xsd:documentation> </xsd:annotation> </xsd:element>
 *         <xsd:element ref="xpdl:Reference" minOccurs="0"/> </xsd:choice>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlImplementation extends XpdlElement {

	/*
	 * Elements
	 */
	private XpdlNo no;
	private List<XpdlTool> toolList;
	private XpdlTask task;
	private XpdlSubFlow subFlow;
	private XpdlReference reference;

	public XpdlImplementation(String tag) {
		super(tag);

		no = null;
		toolList = new LinkedList<XpdlTool>();
		task = null;
		subFlow = null;
		reference = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("No")) {
			no = new XpdlNo("No");
			no.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Tool")) {
			XpdlTool tool = new XpdlTool("Tool");
			tool.importElement(xpp, xpdl);
			toolList.add(tool);
			return true;
		}
		if (xpp.getName().equals("Task")) {
			task = new XpdlTask("Task");
			task.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("SubFlow")) {
			subFlow = new XpdlSubFlow("SubFlow");
			subFlow.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Reference")) {
			reference = new XpdlReference("Reference");
			reference.importElement(xpp, xpdl);
			return true;
		}
		/*
		 * Unknown tag
		 */
		return false;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		if (no != null) {
			s += no.exportElement();
		}
		if (toolList!=null&&!toolList.isEmpty()){
			for (XpdlTool tool : toolList) {
				s += tool.exportElement();
			}
		}
		if (task != null) {
			s += task.exportElement();
		}
		if (subFlow != null) {
			s += subFlow.exportElement();
		}
		if (reference != null) {
			s += reference.exportElement();
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
	}

	public XpdlNo getNo() {
		return no;
	}

	public void setNo(XpdlNo no) {
		this.no = no;
	}

	public XpdlTask getTask() {
		return task;
	}

	public void setTask(XpdlTask task) {
		this.task = task;
	}

	public XpdlSubFlow getSubFlow() {
		return subFlow;
	}

	public void setSubFlow(XpdlSubFlow subFlow) {
		this.subFlow = subFlow;
	}

	public XpdlReference getReference() {
		return reference;
	}

	public void setReference(XpdlReference reference) {
		this.reference = reference;
	}

	public List<XpdlTool> getToolList() {
		return toolList;
	}

	public void setToolList(List<XpdlTool> toolList) {
		this.toolList = toolList;
	}

}
