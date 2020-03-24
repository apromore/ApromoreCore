package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.expressiontype.XpdlExpressionType;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ActualParameters"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element name="ActualParameter"
 *         type="xpdl:ExpressionType" minOccurs="0" maxOccurs="unbounded"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlActualParameters extends XpdlCollections<XpdlExpressionType> {

	public XpdlActualParameters(String tag) {
		super(tag);
	}

	public XpdlExpressionType create() {
		return new XpdlExpressionType("ActualParameter");
	}
}
