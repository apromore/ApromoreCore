package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ParticipantType"> <xsd:complexType> <xsd:sequence>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Type"
 *         use="required"> <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="RESOURCE_SET"/> <xsd:enumeration
 *         value="RESOURCE"/> <xsd:enumeration value="ROLE"/> <xsd:enumeration
 *         value="ORGANIZATIONAL_UNIT"/> <xsd:enumeration value="HUMAN"/>
 *         <xsd:enumeration value="SYSTEM"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlParticipantType extends XpdlElement {

	/*
	 * Attributes
	 */
	private String type;

	public XpdlParticipantType(String tag) {
		super(tag);

		type = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Type");
		if (value != null) {
			type = value;
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
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Type", type, Arrays.asList("RESOURCE_SET", "RESOURCE", "ROLE", "ORGANIZATIONAL_UNIT",
				"HUMAN", "SYSTEM"), true);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
