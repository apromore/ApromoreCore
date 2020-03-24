package org.apromore.processmining.plugins.xpdl.idname;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Page"> <xsd:complexType> <xsd:sequence> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:attribute name="Id"
 *         type="xpdl:Id" use="required"/> <xsd:attribute name="Height"
 *         type="xsd:double" use="optional"/> <xsd:attribute name="Width"
 *         type="xsd:double" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlPage extends XpdlIdName {

	/*
	 * Attributes
	 */
	private String height;
	private String width;

	public XpdlPage(String tag) {
		super(tag);

		height = null;
		width = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Height");
		if (value != null) {
			height = value;
		}
		value = xpp.getAttributeValue(null, "Width");
		if (value != null) {
			width = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (height != null) {
			s += exportAttribute("Height", height);
		}
		if (width != null) {
			s += exportAttribute("Width", width);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkDouble(xpdl, "Height", height, false);
		checkDouble(xpdl, "Width", width, false);
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}
}
