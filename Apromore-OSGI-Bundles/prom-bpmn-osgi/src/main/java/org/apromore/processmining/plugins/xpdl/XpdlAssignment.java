package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Assignment"> <xsd:annotation>
 *         <xsd:documentation>BPMN and XPDL</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:element
 *         name="Target" type="xpdl:ExpressionType"> <xsd:annotation>
 *         <xsd:documentation> lvalue expression of the assignment, in XPDL may
 *         be the name of a DataField, in BPMN name of a Property, in XPATH a
 *         reference </xsd:documentation> </xsd:annotation> </xsd:element>
 *         <xsd:element name="Expression" type="xpdl:ExpressionType">
 *         <xsd:annotation> <xsd:documentation>rvalue expression of the
 *         assignment</xsd:documentation> </xsd:annotation> </xsd:element>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="AssignTime" use="optional" default="Start"> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration value="Start"/>
 *         <xsd:enumeration value="End"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */

public class XpdlAssignment extends XpdlElement {

	/*
	 * Attributes
	 */
	private String assignTime;

	/*
	 * Elements
	 */
	private XpdlExpressionType target;
	private XpdlExpressionType expression;

	public XpdlAssignment(String tag) {
		super(tag);

		assignTime = null;

		target = null;
		expression = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Target")) {
			target = new XpdlExpressionType("Target");
			target.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Expression")) {
			expression = new XpdlExpressionType("Expression");
			expression.importElement(xpp, xpdl);
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
		if (target != null) {
			s += target.exportElement();
		}
		if (expression != null) {
			s += expression.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "AssignTime");
		if (value != null) {
			assignTime = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (assignTime != null) {
			s += exportAttribute("AssignTime", assignTime);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "AssignTime", assignTime, Arrays.asList("Start", "End"), false);
	}

	public String getAssignTime() {
		return assignTime;
	}

	public void setAssignTime(String assignTime) {
		this.assignTime = assignTime;
	}

	public XpdlExpressionType getTarget() {
		return target;
	}

	public void setTarget(XpdlExpressionType target) {
		this.target = target;
	}

	public XpdlExpressionType getExpression() {
		return expression;
	}

	public void setExpression(XpdlExpressionType expression) {
		this.expression = expression;
	}
}
