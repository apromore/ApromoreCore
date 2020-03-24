package org.apromore.processmining.plugins.xpdl.collections;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="InputSets"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:InputSet"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlInputSets extends XpdlCollections<XpdlInputSet> {

	public XpdlInputSets(String tag) {
		super(tag);
	}

	public XpdlInputSet create() {
		return new XpdlInputSet("InputSet");
	}
}
