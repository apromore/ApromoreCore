package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.XpdlDataMapping;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="DataMappings"> <xsd:annotation>
 *         <xsd:documentation>XPDL and BPMN:Maps fields or properties between
 *         calling and called processes or subprocesses</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:element
 *         ref="xpdl:DataMapping" minOccurs="0" maxOccurs="unbounded"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlDataMappings extends XpdlCollections<XpdlDataMapping> {

	public XpdlDataMappings(String tag) {
		super(tag);
	}

	public XpdlDataMapping create() {
		return new XpdlDataMapping("DataMapping");
	}
}
