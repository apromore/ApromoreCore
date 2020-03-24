package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.text.XpdlPerformer;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Performers"> <xsd:annotation>
 *         <xsd:documentation>BPMN and XPDL</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:element
 *         ref="xpdl:Performer" maxOccurs="unbounded"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlPerformers extends XpdlCollections<XpdlPerformer> {

	public XpdlPerformers(String tag) {
		super(tag);
	}

	public XpdlPerformer create() {
		return new XpdlPerformer("Performer");
	}

}
