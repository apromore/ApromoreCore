package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Icon"> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:string"> <xsd:attribute name="XCOORD"
 *         type="xsd:integer" use="optional"/> <xsd:attribute name="YCOORD"
 *         type="xsd:integer" use="optional"/> <xsd:attribute name="WIDTH"
 *         type="xsd:integer" use="optional"/> <xsd:attribute name="HEIGHT"
 *         type="xsd:integer" use="optional"/> <xsd:attribute name="SHAPE"
 *         use="optional" default="RoundRectangle"> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration
 *         value="RoundRectangle"/> <xsd:enumeration value="Rectangle"/>
 *         <xsd:enumeration value="Ellipse"/> <xsd:enumeration value="Diamond"/>
 *         <xsd:enumeration value="UpTriangle"/> <xsd:enumeration
 *         value="DownTriangle"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:extension> </xsd:simpleContent>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlIcon extends XpdlElement {

	/*
	 * Attributes
	 */
	private String xCoord;
	private String yCoord;
	private String width;
	private String height;
	private String shape;

	public XpdlIcon(String tag) {
		super(tag);

		xCoord = null;
		yCoord = null;
		width = null;
		height = null;
		shape = null;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "XCOORD");
		if (value != null) {
			xCoord = value;
		}
		value = xpp.getAttributeValue(null, "YCOORD");
		if (value != null) {
			yCoord = value;
		}
		value = xpp.getAttributeValue(null, "WIDTH");
		if (value != null) {
			width = value;
		}
		value = xpp.getAttributeValue(null, "HEIGHT");
		if (value != null) {
			height = value;
		}
		value = xpp.getAttributeValue(null, "SHAPE");
		if (value != null) {
			shape = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (xCoord != null) {
			s += exportAttribute("XCOORD", xCoord);
		}
		if (yCoord != null) {
			s += exportAttribute("YCOORD", yCoord);
		}
		if (width != null) {
			s += exportAttribute("WIDTH", width);
		}
		if (height != null) {
			s += exportAttribute("HEIGHT", height);
		}
		if (shape != null) {
			s += exportAttribute("SHAPE", shape);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "SHAPE", shape, Arrays.asList("RoundRectangle", "Rectangle", "Ellipse", "Diamond",
				"UpTriangle", "DownTriangle"), false);
	}

	public String getxCoord() {
		return xCoord;
	}

	public void setxCoord(String xCoord) {
		this.xCoord = xCoord;
	}

	public String getyCoord() {
		return yCoord;
	}

	public void setyCoord(String yCoord) {
		this.yCoord = yCoord;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}
}
