package org.apromore.processmining.plugins.xpdl;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TriggerConditional"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element name="Expression"
 *         type="xpdl:ExpressionType" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="ConditionName" type="xsd:string" use="optional"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlTriggerConditional extends XpdlElement {

	/*
	 * Attributes
	 */
	private String conditionName;

	/*
	 * Elements
	 */
	private XpdlExpressionType expression;

	public XpdlTriggerConditional(String tag) {
		super(tag);

		conditionName = null;

		expression = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
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
		if (expression != null) {
			s += expression.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "ConditionName");
		if (value != null) {
			conditionName = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (conditionName != null) {
			s += exportAttribute("ConditionName", conditionName);
		}
		return s;
	}

	public String getConditionName() {
		return conditionName;
	}

	public void setConditionName(String conditionName) {
		this.conditionName = conditionName;
	}

	public XpdlExpressionType getExpression() {
		return expression;
	}

	public void setExpression(XpdlExpressionType expression) {
		this.expression = expression;
	}
}
