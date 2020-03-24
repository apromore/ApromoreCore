package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ExtendedAttribute"> <xsd:complexType mixed="true">
 *         <xsd:choice minOccurs="0" maxOccurs="unbounded"> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:choice> <xsd:attribute name="Name"
 *         type="xsd:NMTOKEN" use="required"/> <xsd:attribute name="Value"
 *         type="xsd:string"/> </xsd:complexType> </xsd:element>
 */
public class XpdlExtendedAttribute extends XpdlElement {

	/*
	 * Attributes
	 */
	private String name;
	private String value;

	public XpdlExtendedAttribute(String tag) {
		super(tag);

		name = null;
		value = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Name");
		if (value != null) {
			name = value;
		}
		value = xpp.getAttributeValue(null, "Value");
		if (value != null) {
			this.value = value;
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
		if (value != null) {
			s += exportAttribute("Value", value);
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
