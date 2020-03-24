package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.collections.XpdlTransitionRefs;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Split"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:TransitionRefs" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Type">
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="XOR"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in BPMN1.1</xsd:documentation>
 *         </xsd:annotation> </xsd:enumeration> <xsd:enumeration
 *         value="Exclusive"/> <xsd:enumeration value="OR"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in BPMN1.1</xsd:documentation>
 *         </xsd:annotation> </xsd:enumeration> <xsd:enumeration
 *         value="Inclusive"/> <xsd:enumeration value="Complex"/>
 *         <xsd:enumeration value="AND"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in BPMN1.1</xsd:documentation>
 *         </xsd:annotation> </xsd:enumeration> <xsd:enumeration
 *         value="Parallel"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:attribute name="ExclusiveType" use="optional"
 *         default="Data"> <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="Data"/> <xsd:enumeration value="Event"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="OutgoingCondition" type="xsd:string"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlSplit extends XpdlElement {

	/*
	 * Attributes
	 */
	private String type;
	private String exclusiveType;
	private String outgoingCondition;

	/*
	 * Elements
	 */
	private XpdlTransitionRefs transitionRefs;

	public XpdlSplit(String tag) {
		super(tag);

		type = null;
		exclusiveType = null;
		outgoingCondition = null;

		transitionRefs = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("TransitionRefs")) {
			transitionRefs = new XpdlTransitionRefs("TransitionRefs");
			transitionRefs.importElement(xpp, xpdl);
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
		if (transitionRefs != null) {
			s += transitionRefs.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Type");
		if (value != null) {
			type = value;
		}
		value = xpp.getAttributeValue(null, "ExclusiveType");
		if (value != null) {
			exclusiveType = value;
		}
		value = xpp.getAttributeValue(null, "OutgoingCondition");
		if (value != null) {
			outgoingCondition = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (type != null) {
			s += exportAttribute("Type", type);
		}
		if (exclusiveType != null) {
			s += exportAttribute("ExclusiveType", exclusiveType);
		}
		if (outgoingCondition != null) {
			s += exportAttribute("GrouOutgoingConditionp", outgoingCondition);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Type", type, Arrays.asList("XOR", "Exclusive", "OR", "Inclusive", "Complex", "AND",
				"Parallel"), false);
		checkRestriction(xpdl, "ExclusiveType", exclusiveType, Arrays.asList("Data", "Event"), false);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getExclusiveType() {
		return exclusiveType;
	}

	public void setExclusiveType(String exclusiveType) {
		this.exclusiveType = exclusiveType;
	}

	public String getOutgoingCondition() {
		return outgoingCondition;
	}

	public void setOutgoingCondition(String outgoingCondition) {
		this.outgoingCondition = outgoingCondition;
	}

	public XpdlTransitionRefs getTransitionRefs() {
		return transitionRefs;
	}

	public void setTransitionRefs(XpdlTransitionRefs transitionRefs) {
		this.transitionRefs = transitionRefs;
	}

}
