package org.apromore.processmining.plugins.xpdl.datatypes;

import org.apromore.processmining.plugins.xpdl.idname.XpdlIdName;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="DeclaredType"> <xsd:complexType> <xsd:sequence>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Id"
 *         type="xsd:IDREF" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlDeclaredType extends XpdlIdName {

	public XpdlDeclaredType(String tag) {
		super(tag);
	}

}
