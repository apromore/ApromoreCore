package org.apromore.processmining.plugins.xpdl.collections;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="OutputSets"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:OutputSet"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlOutputSets extends XpdlCollections<XpdlOutputSet> {

	public XpdlOutputSets(String tag) {
		super(tag);
	}

	public XpdlOutputSet create() {
		return new XpdlOutputSet("OutputSet");
	}

}
