package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Condition"> <xsd:complexType mixed="true">
 *         <xsd:choice minOccurs="0"> <xsd:element ref="deprecated:Xpression"
 *         minOccurs="0"/> <xsd:element name="Expression"
 *         type="xpdl:ExpressionType" minOccurs="0"/> </xsd:choice>
 *         <xsd:attribute name="Type"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="CONDITION"/>
 *         <xsd:enumeration value="OTHERWISE"/> <xsd:enumeration
 *         value="EXCEPTION"/> <xsd:enumeration value="DEFAULTEXCEPTION"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlCondition extends XpdlElement {

	/*
	 * Attributes
	 */
	private String type;

	/*
	 * Elements
	 */
	private XpdlExpressionType xpressionDeprecated;
	private XpdlExpressionType expression;

	public XpdlCondition(String tag) {
		super(tag);

		type = null;

		xpressionDeprecated = null;
		expression = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Xpression")) {
			xpressionDeprecated = new XpdlExpressionType("Xpression");
			xpressionDeprecated.importElement(xpp, xpdl);
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
		if (xpressionDeprecated != null) {
			s += xpressionDeprecated.exportElement();
		}
		if (expression != null) {
			s += expression.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Type");
		if (value != null) {
			type = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (type != null) {
			s += exportAttribute("Type", type);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Type", type, Arrays.asList("CONDITION", "OTHERWISE", "EXCEPTION", "DEFAULTEXCEPTION"),
				false);
		int n = (xpressionDeprecated != null ? 1 : 0) + (expression != null ? 1 : 0);
		if (n > 1) {
			xpdl.log(tag, lineNumber, "Xpression and Expression are mutually exclusive");
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public XpdlExpressionType getXpressionDeprecated() {
		return xpressionDeprecated;
	}

	public void setXpressionDeprecated(XpdlExpressionType xpressionDeprecated) {
		this.xpressionDeprecated = xpressionDeprecated;
	}

	public XpdlExpressionType getExpression() {
		return expression;
	}

	public void setExpression(XpdlExpressionType expression) {
		this.expression = expression;
	}
}
