package org.apromore.processmining.plugins.xpdl.datatypes;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ArrayType"> <xsd:complexType> <xsd:group
 *         ref="xpdl:DataTypes"/> <xsd:attribute name="LowerIndex"
 *         type="xsd:NMTOKEN" use="required"/> <xsd:attribute name="UpperIndex"
 *         type="xsd:NMTOKEN" use="required"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */

public class XpdlArrayType extends XpdlDataTypes {

	/*
	 * Attributes
	 */
	private String lowerIndex;
	private String upperIndex;

	public XpdlArrayType(String tag) {
		super(tag);

		lowerIndex = null;
		upperIndex = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "LowerIndex");
		if (value != null) {
			lowerIndex = value;
		}
		value = xpp.getAttributeValue(null, "UpperIndex");
		if (value != null) {
			upperIndex = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (lowerIndex != null) {
			s += exportAttribute("LowerIndex", lowerIndex);
		}
		if (upperIndex != null) {
			s += exportAttribute("UpperIndex", upperIndex);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "LowerIndex", lowerIndex);
		checkRequired(xpdl, "UpperIndex", upperIndex);
	}

	public String getLowerIndex() {
		return lowerIndex;
	}

	public void setLowerIndex(String lowerIndex) {
		this.lowerIndex = lowerIndex;
	}

	public String getUpperIndex() {
		return upperIndex;
	}

	public void setUpperIndex(String upperIndex) {
		this.upperIndex = upperIndex;
	}
}
