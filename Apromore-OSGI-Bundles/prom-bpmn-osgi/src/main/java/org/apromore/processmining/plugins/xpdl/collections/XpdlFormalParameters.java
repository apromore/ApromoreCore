package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlFormalParameter;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="FormalParameters"> <xsd:complexType>
 *         <xsd:sequence> <xsd:choice minOccurs="0"> <xsd:element
 *         ref="deprecated:FormalParameter" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:element ref="xpdl:FormalParameter"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:choice> <xsd:choice
 *         minOccurs="0"> <xsd:sequence> <xsd:element name="Extensions"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> </xsd:choice> </xsd:sequence>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlFormalParameters extends XpdlCollections<XpdlFormalParameter> {

	public XpdlFormalParameters(String tag) {
		super(tag);
	}

	public XpdlFormalParameter create() {
		return new XpdlFormalParameter("FormalParameter");
	}
}
