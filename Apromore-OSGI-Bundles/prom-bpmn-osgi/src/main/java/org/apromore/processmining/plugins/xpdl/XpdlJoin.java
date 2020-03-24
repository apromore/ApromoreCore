package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Join"> <xsd:complexType> <xsd:sequence> <xsd:any
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
 *         name="IncomingCondtion" type="xsd:string"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlJoin extends XpdlElement {

	/*
	 * Attributes
	 */
	private String type;
	private String exclusiveType;
	private String incomingCondition;

	public XpdlJoin(String tag) {
		super(tag);

		type = null;
		exclusiveType = null;
		incomingCondition = null;
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
		value = xpp.getAttributeValue(null, "IncomingCondition");
		if (value != null) {
			incomingCondition = value;
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
		if (incomingCondition != null) {
			s += exportAttribute("IncomingCondition", incomingCondition);
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

	public String getIncomingCondition() {
		return incomingCondition;
	}

	public void setIncomingCondition(String incomingCondition) {
		this.incomingCondition = incomingCondition;
	}
}
