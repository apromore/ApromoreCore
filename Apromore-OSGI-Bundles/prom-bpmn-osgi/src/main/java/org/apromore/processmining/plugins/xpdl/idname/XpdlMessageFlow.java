package org.apromore.processmining.plugins.xpdl.idname;

import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.graphics.collections.XpdlConnectorGraphicsInfos;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="MessageFlow"> <xsd:annotation>
 *         <xsd:documentation>:BPMN:</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence minOccurs="0"> <xsd:element
 *         name="Message" type="xpdl:MessageType" minOccurs="0"/> <xsd:element
 *         ref="xpdl:Object" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ConnectorGraphicsInfos" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Id"
 *         type="xsd:NMTOKEN" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:attribute name="Source"
 *         type="xpdl:IdRef" use="required"/> <xsd:attribute name="Target"
 *         type="xpdl:IdRef" use="required"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlMessageFlow extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String source;
	private String target;

	/*
	 * Elements
	 */
	private XpdlMessageType message;
	private XpdlObject object;
	private XpdlConnectorGraphicsInfos connectorsGraphicsInfos;

	public XpdlMessageFlow(String tag) {
		super(tag);

		source = null;
		target = null;

		message = null;
		object = null;
		connectorsGraphicsInfos = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Message")) {
			message = new XpdlMessageType("Message");
			message.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Object")) {
			object = new XpdlObject("Object");
			object.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ConnectorGraphicsInfos")) {
			connectorsGraphicsInfos = new XpdlConnectorGraphicsInfos("ConnectorGraphicsInfos");
			connectorsGraphicsInfos.importElement(xpp, xpdl);
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
		if (message != null) {
			s += message.exportElement();
		}
		if (object != null) {
			s += object.exportElement();
		}
		if (connectorsGraphicsInfos != null) {
			s += connectorsGraphicsInfos.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Source");
		if (value != null) {
			source = value;
		}
		value = xpp.getAttributeValue(null, "Target");
		if (value != null) {
			target = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (source != null) {
			s += exportAttribute("Source", source);
		}
		if (target != null) {
			s += exportAttribute("Target", target);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "Source", source);
		checkRequired(xpdl, "Target", target);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public XpdlMessageType getMessage() {
		return message;
	}

	public void setMessage(XpdlMessageType message) {
		this.message = message;
	}

	public XpdlObject getObject() {
		return object;
	}

	public void setObject(XpdlObject object) {
		this.object = object;
	}

	public XpdlConnectorGraphicsInfos getConnectorsGraphicsInfos() {
		return connectorsGraphicsInfos;
	}

	public void setConnectorsGraphicsInfos(XpdlConnectorGraphicsInfos connectorsGraphicsInfos) {
		this.connectorsGraphicsInfos = connectorsGraphicsInfos;
	}
	
	public void convertToBpmn(BPMNDiagram bpmn, Map<String, BPMNNode> id2node) {
		BPMNNode sourceNode = id2node.get(source);
		BPMNNode targetNode = id2node.get(target);
		if (sourceNode != null && targetNode != null) {
			bpmn.addMessageFlow(sourceNode, targetNode, name == null ? "" : name);
		}
	}

}
