package org.apromore.processmining.plugins.xpdl.idname;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Category"> <xsd:annotation> <xsd:documentation>
 *         BPMN (and XPDL??Allows arbitrary grouping of various types of
 *         elements, for reporting.)</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:attribute name="Id" type="xsd:NMTOKEN"
 *         use="required"/> <xsd:attribute name="Name" type="xsd:string"
 *         use="optional"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */

public class XpdlCategory extends XpdlIdName {

	public XpdlCategory(String tag) {
		super(tag);
	}
}
