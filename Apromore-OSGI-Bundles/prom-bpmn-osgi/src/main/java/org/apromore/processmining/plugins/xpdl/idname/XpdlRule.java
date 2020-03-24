package org.apromore.processmining.plugins.xpdl.idname;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Rule"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element name="Expression" type="xpdl:ExpressionType"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Id"
 *         type="xsd:NMTOKEN" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlRule extends XpdlIdName {

	/*
	 * Elements
	 */
	private XpdlExpressionType expression;

	public XpdlRule(String tag) {
		super(tag);

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

	public XpdlExpressionType getExpression() {
		return expression;
	}

	public void setExpression(XpdlExpressionType expression) {
		this.expression = expression;
	}
}
