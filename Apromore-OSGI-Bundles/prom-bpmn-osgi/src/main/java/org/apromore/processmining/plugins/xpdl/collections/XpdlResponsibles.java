package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.text.XpdlResponsible;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Responsibles"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Responsible" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlResponsibles extends XpdlCollections<XpdlResponsible> {

	public XpdlResponsibles(String tag) {
		super(tag);
	}

	public XpdlResponsible create() {
		return new XpdlResponsible("Responsible");
	}
}
