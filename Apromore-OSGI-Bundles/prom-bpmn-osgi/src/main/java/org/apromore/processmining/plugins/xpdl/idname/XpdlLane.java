package org.apromore.processmining.plugins.xpdl.idname;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SwimlaneType;
import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.apromore.processmining.plugins.xpdl.collections.XpdlPerformers;
import org.apromore.processmining.plugins.xpdl.graphics.collections.XpdlNodeGraphicsInfos;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Lane"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence minOccurs="0"> <xsd:element
 *         ref="xpdl:Object" minOccurs="0"/> <xsd:element
 *         ref="xpdl:NodeGraphicsInfos" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Performers" minOccurs="0"/> <xsd:element name="NestedLane"
 *         minOccurs="0" maxOccurs="unbounded"> <xsd:complexType> <xsd:attribute
 *         name="LaneId" type="xsd:NMTOKEN" use="required"/> </xsd:complexType>
 *         </xsd:element> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xsd:NMTOKEN" use="required"/> <xsd:attribute
 *         name="Name" type="xsd:string" use="optional"/> <xsd:attribute
 *         name="ParentLane" type="xsd:NMTOKEN" use="optional"> <xsd:annotation>
 *         <xsd:documentation>Deprecated from BPMN1.0. </xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:attribute name="ParentPool"
 *         type="xsd:NMTOKEN" use="optional"> <xsd:annotation>
 *         <xsd:documentation>Deprecated from BPMN1.0. </xsd:documentation>
 *         </xsd:annotation> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlLane extends XpdlIdName {

	private class XpdlNestedLaneId extends XpdlElement {
		/*
		 * Attributes
		 */
		private String laneId;

		public XpdlNestedLaneId(String tag) {
			super(tag);

			laneId = null;
		}

		protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
			super.importAttributes(xpp, xpdl);
			String value = xpp.getAttributeValue(null, "LaneId");
			if (value != null) {
				laneId = value;
			}
		}

		/**
		 * Exports all attributes.
		 */
		protected String exportAttributes() {
			String s = super.exportAttributes();
			if (laneId != null) {
				s += exportAttribute("LaneId", laneId);
			}
			return s;
		}

		protected void checkValidity(Xpdl xpdl) {
			super.checkValidity(xpdl);
			checkRequired(xpdl, "LaneId", laneId);
		}
	}

	/*
	 * Attributes
	 */
	private String parentLane;
	private String parentPool;

	/*
	 * Elements
	 */
	private XpdlObject object;
	private XpdlNodeGraphicsInfos nodeGraphicsInfos;
	private XpdlPerformers performers;
	private final List<XpdlNestedLaneId> nestedLaneList;

	public XpdlLane(String tag) {
		super(tag);

		parentLane = null;
		parentPool = null;

		object = null;
		nodeGraphicsInfos = null;
		performers = null;
		nestedLaneList = new ArrayList<XpdlNestedLaneId>();
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
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
		if (xpp.getName().equals("Performers")) {
			performers = new XpdlPerformers("Performers");
			performers.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("NestedLane")) {
			XpdlNestedLaneId nestedLane = new XpdlNestedLaneId("NestedLane");
			nestedLane.importElement(xpp, xpdl);
			nestedLaneList.add(nestedLane);
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
		if (object != null) {
			s += object.exportElement();
		}
		if (nodeGraphicsInfos != null) {
			s += nodeGraphicsInfos.exportElement();
		}
		if (performers != null) {
			s += performers.exportElement();
		}
		for (XpdlNestedLaneId nestedLane : nestedLaneList) {
			s += nestedLane.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ParentLane");
		if (value != null) {
			parentLane = value;
		}
		value = xpp.getAttributeValue(null, "ParentPool");
		if (value != null) {
			parentPool = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (parentLane != null) {
			s += exportAttribute("ParentLane", parentLane);
		}
		if (parentPool != null) {
			s += exportAttribute("ParentPool", parentPool);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
	}

	public String getParentLane() {
		return parentLane;
	}

	public void setParentLane(String parentLane) {
		this.parentLane = parentLane;
	}

	public String getParentPool() {
		return parentPool;
	}

	public void setParentPool(String parentPool) {
		this.parentPool = parentPool;
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

	public XpdlPerformers getPerformers() {
		return performers;
	}

	public void setPerformers(XpdlPerformers performers) {
		this.performers = performers;
	}

	public List<XpdlNestedLaneId> getNestedLaneList() {
		return nestedLaneList;
	}

	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node) {
		if (id2node.get(id)==null) {
			Swimlane lane = bpmn.addSwimlane(getName(), null, SwimlaneType.LANE);
			id2node.put(id, lane);
		}
	}
}
