package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.XpdlAssignment;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Assignments"> <xsd:annotation>
 *         <xsd:documentation>BPMN and XPDL</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:sequence> <xsd:element
 *         ref="xpdl:Assignment" maxOccurs="unbounded"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlAssignments extends XpdlCollections<XpdlAssignment> {

	public XpdlAssignments(String tag) {
		super(tag);
	}

	public XpdlAssignment create() {
		return new XpdlAssignment("Assignment");
	}

}
