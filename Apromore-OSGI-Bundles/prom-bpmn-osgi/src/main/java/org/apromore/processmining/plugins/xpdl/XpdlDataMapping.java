package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="DataMapping"> <xsd:annotation>
 *         <xsd:documentation>Deprecated in XPDL 2.2, use
 *         FormalParameters</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element name="Actual"
 *         type="xpdl:ExpressionType"/> <xsd:element name="TestValue"
 *         type="xpdl:ExpressionType" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Formal"
 *         type="xsd:string" use="required"/> <xsd:attribute name="Direction"
 *         default="IN"> <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="IN"/> <xsd:enumeration value="OUT"/>
 *         <xsd:enumeration value="INOUT"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlDataMapping extends XpdlElement {

	/*
	 * Attributes
	 */
	private String formal;
	private String direction;

	/*
	 * Elements
	 */
	private XpdlExpressionType actual;
	private XpdlExpressionType testValue;

	public XpdlDataMapping(String tag) {
		super(tag);

		formal = null;
		direction = null;

		actual = null;
		testValue = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Actual")) {
			actual = new XpdlExpressionType("Actual");
			actual.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TestValue")) {
			testValue = new XpdlExpressionType("TestValue");
			testValue.importElement(xpp, xpdl);
			return true;
		}
		/*
		 * Unknown tag
		 */
		return false;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		if (actual != null) {
			s += actual.exportElement();
		}
		if (testValue != null) {
			s += testValue.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Formal");
		if (value != null) {
			formal = value;
		}
		value = xpp.getAttributeValue(null, "Direction");
		if (value != null) {
			direction = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (formal != null) {
			s += exportAttribute("Formal", formal);
		}
		if (direction != null) {
			s += exportAttribute("Direction", direction);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "Formal", formal);
		checkRestriction(xpdl, "Direction", direction, Arrays.asList("IN", "OUT", "INOUT"), false);
	}

	public String getFormal() {
		return formal;
	}

	public void setFormal(String formal) {
		this.formal = formal;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public XpdlExpressionType getActual() {
		return actual;
	}

	public void setActual(XpdlExpressionType actual) {
		this.actual = actual;
	}

	public XpdlExpressionType getTestValue() {
		return testValue;
	}

	public void setTestValue(XpdlExpressionType testValue) {
		this.testValue = testValue;
	}
}
