package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlAssociation;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Associations"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence maxOccurs="unbounded"> <xsd:element
 *         ref="xpdl:Association"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlAssociations extends XpdlCollections<XpdlAssociation> {

	public XpdlAssociations(String tag) {
		super(tag);
	}

	public XpdlAssociation create() {
		return new XpdlAssociation("Association");
	}

}
