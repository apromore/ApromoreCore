package org.apromore.processmining.plugins.xpdl;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Codepage"> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:string"> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:extension>
 *         </xsd:simpleContent> </xsd:complexType> </xsd:element>
 */
public class XpdlCodepage extends XpdlElement {

	public XpdlCodepage(String tag) {
		super(tag);
	}
}
