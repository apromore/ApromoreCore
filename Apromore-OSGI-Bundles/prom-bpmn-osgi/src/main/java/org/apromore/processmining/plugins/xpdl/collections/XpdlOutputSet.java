package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.XpdlOutput;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="OutputSet"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:Output"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlOutputSet extends XpdlCollections<XpdlOutput> {

	public XpdlOutputSet(String tag) {
		super(tag);
	}

	public XpdlOutput create() {
		return new XpdlOutput("Output");
	}
}
