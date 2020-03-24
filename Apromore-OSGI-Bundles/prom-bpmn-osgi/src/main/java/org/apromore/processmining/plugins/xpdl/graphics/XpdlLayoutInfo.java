package org.apromore.processmining.plugins.xpdl.graphics;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="LayoutInfo"> <xsd:complexType> <xsd:attribute
 *         name="PixelsPerMillimeter" type="xsd:float" use="optional">
 *         <xsd:annotation> <xsd:documentation>Co-ordinates / Sizes are in
 *         pixels - this attribute specifies the number of pixels per millimeter
 *         used by application.</xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlLayoutInfo extends XpdlElement {

	/*
	 * Attributes
	 */
	private String pixelsPerMillimeter;

	public XpdlLayoutInfo(String tag) {
		super(tag);

		pixelsPerMillimeter = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "PixelsPerMillimeter");
		if (value != null) {
			pixelsPerMillimeter = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (pixelsPerMillimeter != null) {
			s += exportAttribute("PixelsPerMillimeter", pixelsPerMillimeter);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkDouble(xpdl, "PixelsPerMillimeter", pixelsPerMillimeter, false);
	}

	public String getPixelsPerMillimeter() {
		return pixelsPerMillimeter;
	}

	public void setPixelsPerMillimeter(String pixelsPerMillimeter) {
		this.pixelsPerMillimeter = pixelsPerMillimeter;
	}
}
