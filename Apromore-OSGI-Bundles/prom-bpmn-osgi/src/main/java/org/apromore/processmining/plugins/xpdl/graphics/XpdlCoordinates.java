package org.apromore.processmining.plugins.xpdl.graphics;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Coordinates"> <xsd:annotation>
 *         <xsd:documentation>BPMN and XPDL</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="XCoordinate" type="xsd:double" use="optional"/> <xsd:attribute
 *         name="YCoordinate" type="xsd:double" use="optional"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlCoordinates extends XpdlElement {

	/*
	 * Attributes
	 */
	private String xCoordinate;
	private String yCoordinate;

	public XpdlCoordinates(String tag) {
		super(tag);

		xCoordinate = null;
		yCoordinate = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "XCoordinate");
		if (value != null) {
			xCoordinate = value;
		}
		value = xpp.getAttributeValue(null, "YCoordinate");
		if (value != null) {
			yCoordinate = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (xCoordinate != null) {
			s += exportAttribute("XCoordinate", xCoordinate);
		}
		if (yCoordinate != null) {
			s += exportAttribute("YCoordinate", yCoordinate);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkDouble(xpdl, "XCoordinate", xCoordinate, false);
		checkDouble(xpdl, "YCoordinate", yCoordinate, false);
	}

	public String getxCoordinate() {
		return xCoordinate;
	}

	public void setxCoordinate(String xCoordinate) {
		this.xCoordinate = xCoordinate;
	}

	public String getyCoordinate() {
		return yCoordinate;
	}

	public void setyCoordinate(String yCoordinate) {
		this.yCoordinate = yCoordinate;
	}
}
