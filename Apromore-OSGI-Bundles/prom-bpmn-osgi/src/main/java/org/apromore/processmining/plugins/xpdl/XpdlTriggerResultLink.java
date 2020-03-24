package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TriggerResultLink"> <xsd:annotation>
 *         <xsd:documentation> BPMN: if the Trigger or Result Type is Link then
 *         this must be present. </xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="CatchThrow" use="optional"
 *         default="CATCH"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="CATCH"/> <xsd:enumeration
 *         value="THROW"/> </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:attribute name="Name" type="xsd:NMTOKEN" use="optional">
 *         <xsd:annotation> <xsd:documentation> The link can only be used within
 *         one process as a shorthand for a long sequence flow .
 *         </xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlTriggerResultLink extends XpdlElement {

	/*
	 * Attributes
	 */
	private String catchThrow;
	private String name;

	public XpdlTriggerResultLink(String tag) {
		super(tag);

		catchThrow = null;
		name = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "CatchThrow");
		if (value != null) {
			catchThrow = value;
		}
		value = xpp.getAttributeValue(null, "Name");
		if (value != null) {
			name = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (catchThrow != null) {
			s += exportAttribute("CatchThrow", catchThrow);
		}
		if (name != null) {
			s += exportAttribute("Name", name);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "CatchThrow", catchThrow, Arrays.asList("CATCH", "THROW"), false);
	}

	public String getCatchThrow() {
		return catchThrow;
	}

	public void setCatchThrow(String catchThrow) {
		this.catchThrow = catchThrow;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
