package org.apromore.processmining.plugins.xpdl;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Cost"> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:string"> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:extension>
 *         </xsd:simpleContent> </xsd:complexType> </xsd:element>
 */
public class XpdlCost extends XpdlElement {

	public XpdlCost(String tag) {
		super(tag);
	}
}
