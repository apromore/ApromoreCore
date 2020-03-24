package org.apromore.processmining.plugins.xpdl.idname;

import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlCondition;
import org.apromore.processmining.plugins.xpdl.collections.XpdlActivitySets;
import org.apromore.processmining.plugins.xpdl.collections.XpdlAssignments;
import org.apromore.processmining.plugins.xpdl.collections.XpdlExtendedAttributes;
import org.apromore.processmining.plugins.xpdl.graphics.collections.XpdlConnectorGraphicsInfos;
import org.apromore.processmining.plugins.xpdl.text.XpdlDescription;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Transition"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Condition" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Description" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ExtendedAttributes" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Assignments" minOccurs="0"/> <xsd:element ref="xpdl:Object"
 *         minOccurs="0"/> <xsd:element ref="xpdl:ConnectorGraphicsInfos"
 *         minOccurs="0"/> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Id" type="xpdl:Id" use="required"/> <xsd:attribute name="From"
 *         type="xpdl:IdRef" use="required"/> <xsd:attribute name="To"
 *         type="xpdl:IdRef" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:attribute name="Quantity"
 *         type="xsd:int" use="optional" default="1"> <xsd:annotation>
 *         <xsd:documentation>Used only in BPMN. Specifies number of tokens on
 *         outgoing transition.</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlTransition extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String from;
	private String to;
	private String quantity;

	/*
	 * Elements
	 */
	private XpdlCondition condition;
	private XpdlDescription description;
	private XpdlExtendedAttributes extendedAttributes;
	private XpdlAssignments assignments;
	private XpdlObject object;
	private XpdlConnectorGraphicsInfos connectorGraphicsInfos;

	public XpdlTransition(String tag) {
		super(tag);

		from = null;
		to = null;
		quantity = null;

		condition = null;
		description = null;
		extendedAttributes = null;
		assignments = null;
		object = null;
		connectorGraphicsInfos = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Condition")) {
			condition = new XpdlCondition("Condition");
			condition.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Description")) {
			description = new XpdlDescription("Description");
			description.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ExtendedAttributes")) {
			extendedAttributes = new XpdlExtendedAttributes("ExtendedAttributes");
			extendedAttributes.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Assignments")) {
			assignments = new XpdlAssignments("Assignments");
			assignments.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Object")) {
			object = new XpdlObject("Object");
			object.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ConnectorGraphicsInfos")) {
			connectorGraphicsInfos = new XpdlConnectorGraphicsInfos("ConnectorGraphicsInfos");
			connectorGraphicsInfos.importElement(xpp, xpdl);
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
		if (condition != null) {
			s += condition.exportElement();
		}
		if (description != null) {
			s += description.exportElement();
		}
		if (extendedAttributes != null) {
			s += extendedAttributes.exportElement();
		}
		if (assignments != null) {
			s += assignments.exportElement();
		}
		if (object != null) {
			s += object.exportElement();
		}
		if (connectorGraphicsInfos != null) {
			s += connectorGraphicsInfos.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "From");
		if (value != null) {
			from = value;
		}
		value = xpp.getAttributeValue(null, "To");
		if (value != null) {
			to = value;
		}
		value = xpp.getAttributeValue(null, "Quantity");
		if (value != null) {
			quantity = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (from != null) {
			s += exportAttribute("From", from);
		}
		if (to != null) {
			s += exportAttribute("To", to);
		}
		if (quantity != null) {
			s += exportAttribute("Quantity", quantity);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "From", from);
		checkRequired(xpdl, "To", to);
		checkInteger(xpdl, "Quantity", quantity, false);
	}

	public void convertToBpmn(BPMNDiagram bpmn, DirectedGraphNode parent, XpdlActivitySets activitySets,
			Map<String, BPMNNode> id2node) {
		if ((from != null) && (to != null)) {
			BPMNNode fromNode = id2node.get(from);
			BPMNNode toNode = id2node.get(to);
			if ((fromNode != null) && (toNode != null)) {
				if (parent == null) {
					bpmn.addFlow(fromNode, toNode, name);
				} else {
					if (parent instanceof SubProcess) {
						bpmn.addFlow(fromNode, toNode, (SubProcess)parent, name);
					}else
						bpmn.addFlow(fromNode, toNode, (Swimlane)parent, name);
				}

			}
		}
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public XpdlCondition getCondition() {
		return condition;
	}

	public void setCondition(XpdlCondition condition) {
		this.condition = condition;
	}

	public XpdlDescription getDescription() {
		return description;
	}

	public void setDescription(XpdlDescription description) {
		this.description = description;
	}

	public XpdlExtendedAttributes getExtendedAttributes() {
		return extendedAttributes;
	}

	public void setExtendedAttributes(XpdlExtendedAttributes extendedAttributes) {
		this.extendedAttributes = extendedAttributes;
	}

	public XpdlAssignments getAssignments() {
		return assignments;
	}

	public void setAssignments(XpdlAssignments assignments) {
		this.assignments = assignments;
	}

	public XpdlObject getObject() {
		return object;
	}

	public void setObject(XpdlObject object) {
		this.object = object;
	}

	public XpdlConnectorGraphicsInfos getConnectorGraphicsInfos() {
		return connectorGraphicsInfos;
	}

	public void setConnectorGraphicsInfos(XpdlConnectorGraphicsInfos connectorGraphicsInfos) {
		this.connectorGraphicsInfos = connectorGraphicsInfos;
	}
}
