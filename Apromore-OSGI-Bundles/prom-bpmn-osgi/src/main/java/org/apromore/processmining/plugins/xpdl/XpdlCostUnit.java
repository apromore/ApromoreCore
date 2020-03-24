package org.apromore.processmining.plugins.xpdl;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="CostUnit"> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:string"> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:extension>
 *         </xsd:simpleContent> </xsd:complexType> </xsd:element>
 */
public class XpdlCostUnit extends XpdlElement {

	public XpdlCostUnit(String tag) {
		super(tag);
	}
}
