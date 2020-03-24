package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="EnumerationValue"> <xsd:complexType>
 *         <xsd:sequence> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="Name" type="xsd:NMTOKEN" use="required"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlEnumerationValue extends XpdlElement {

	/*
	 * Attributes
	 */
	private String name;

	public XpdlEnumerationValue(String tag) {
		super(tag);

		name = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Name");
		if (value != null) {
			name = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (name != null) {
			s += exportAttribute("Name", name);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "Name", name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
