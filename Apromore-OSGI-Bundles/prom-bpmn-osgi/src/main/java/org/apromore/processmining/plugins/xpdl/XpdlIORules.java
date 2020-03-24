package org.apromore.processmining.plugins.xpdl;

import java.util.ArrayList;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="IORules"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element name="Expression"
 *         type="xpdl:ExpressionType" maxOccurs="unbounded"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlIORules extends XpdlElement {

	/*
	 * Elements
	 */
	private final List<XpdlExpressionType> expressionList;

	public XpdlIORules(String tag) {
		super(tag);

		expressionList = new ArrayList<XpdlExpressionType>();
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Expression")) {
			XpdlExpressionType expression = new XpdlExpressionType("Expression");
			expression.importElement(xpp, xpdl);
			expressionList.add(expression);
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
		for (XpdlExpressionType expression : expressionList) {
			s += expression.exportElement();
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
	}

	public List<XpdlExpressionType> getExpressionList() {
		return expressionList;
	}
}
