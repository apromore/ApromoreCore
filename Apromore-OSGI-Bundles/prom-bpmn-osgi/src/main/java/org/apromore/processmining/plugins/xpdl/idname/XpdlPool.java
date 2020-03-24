package org.apromore.processmining.plugins.xpdl.idname;

import java.util.Arrays;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;
import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.collections.XpdlLanes;
import org.apromore.processmining.plugins.xpdl.graphics.collections.XpdlNodeGraphicsInfos;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Pool"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:Lanes"
 *         minOccurs="0"/> <xsd:element ref="xpdl:Object" minOccurs="0"/>
 *         <xsd:element ref="xpdl:NodeGraphicsInfos" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Id"
 *         type="xpdl:Id" use="required"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="Name" type="xsd:string"
 *         use="optional"> <xsd:annotation> <xsd:documentation>BPMN: Pool label
 *         in diagram</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="Orientation" use="optional"
 *         default="HORIZONTAL"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="HORIZONTAL"/>
 *         <xsd:enumeration value="VERTICAL"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:attribute name="Process"
 *         type="xpdl:IdRef" use="optional"> <xsd:annotation>
 *         <xsd:documentation>BPMN: Pointer to WorkflowProcess/@Id; presence
 *         indicates this pool is part of an internal (private)
 *         process.</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:attribute name="Participant" type="xsd:NMTOKEN" use="optional"/>
 *         <xsd:attribute name="BoundaryVisible" type="xsd:boolean"
 *         use="required"/> <xsd:attribute name="MainPool" type="xsd:boolean"
 *         use="optional" default="false"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlPool extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String orientation;
	private String process;
	private String participant;
	private String boundaryVisible;
	private String mainPool;

	/*
	 * Elements
	 */
	private XpdlLanes lanes;
	private XpdlObject object;
	private XpdlNodeGraphicsInfos nodeGraphicsInfos;

	public XpdlPool(String tag) {
		super(tag);

		orientation = null;
		process = null;
		participant = null;
		boundaryVisible = null;
		mainPool = null;

		lanes = null;
		object = null;
		nodeGraphicsInfos = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Lanes")) {
			lanes = new XpdlLanes("Lanes");
			lanes.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Object")) {
			object = new XpdlObject("Object");
			object.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("NodeGraphicsInfos")) {
			nodeGraphicsInfos = new XpdlNodeGraphicsInfos("NodeGraphicsInfos");
			nodeGraphicsInfos.importElement(xpp, xpdl);
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
		if (lanes != null) {
			s += lanes.exportElement();
		}
		if (object != null) {
			s += object.exportElement();
		}
		if (nodeGraphicsInfos != null) {
			s += nodeGraphicsInfos.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Orientation");
		if (value != null) {
			orientation = value;
		}
		value = xpp.getAttributeValue(null, "Process");
		if (value != null) {
			process = value;
		}
		value = xpp.getAttributeValue(null, "Participant");
		if (value != null) {
			participant = value;
		}
		value = xpp.getAttributeValue(null, "BoundaryVisible");
		if (value != null) {
			boundaryVisible = value;
		}
		value = xpp.getAttributeValue(null, "MainPool");
		if (value != null) {
			mainPool = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (orientation != null) {
			s += exportAttribute("Orientation", orientation);
		}
		if (process != null) {
			s += exportAttribute("Process", process);
		}
		if (participant != null) {
			s += exportAttribute("Participant", participant);
		}
		if (boundaryVisible != null) {
			s += exportAttribute("BoundaryVisible", boundaryVisible);
		}
		if (mainPool != null) {
			s += exportAttribute("MainPool", mainPool);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Orientation", orientation, Arrays.asList("HORIZONTAL", "VERTICAL"), false);
		checkBoolean(xpdl, "BoundaryVisible", boundaryVisible, true);
		checkBoolean(xpdl, "MainPool", mainPool, false);
	}

	public String getOrientation() {
		return orientation;
	}

	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}

	public String getProcess() {
		return process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getParticipant() {
		return participant;
	}

	public void setParticipant(String participant) {
		this.participant = participant;
	}

	public String getBoundaryVisible() {
		return boundaryVisible;
	}

	public void setBoundaryVisible(String boundaryVisible) {
		this.boundaryVisible = boundaryVisible;
	}

	public String getMainPool() {
		return mainPool;
	}

	public void setMainPool(String mainPool) {
		this.mainPool = mainPool;
	}

	public XpdlLanes getLanes() {
		return lanes;
	}

	public void setLanes(XpdlLanes lanes) {
		this.lanes = lanes;
	}

	public XpdlObject getObject() {
		return object;
	}

	public void setObject(XpdlObject object) {
		this.object = object;
	}

	public XpdlNodeGraphicsInfos getNodeGraphicsInfos() {
		return nodeGraphicsInfos;
	}

	public void setNodeGraphicsInfos(XpdlNodeGraphicsInfos nodeGraphicsInfos) {
		this.nodeGraphicsInfos = nodeGraphicsInfos;
	}

	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node) {
		
		if (id2node.get(id)==null) {
			Swimlane lane = bpmn.addSwimlane(getName(), null, SwimlaneType.POOL);
			id2node.put(id, lane);
		}
		
		if (lanes != null) {
			lanes.convertToBpmn(bpmn, id2node);
		}
	}
}
