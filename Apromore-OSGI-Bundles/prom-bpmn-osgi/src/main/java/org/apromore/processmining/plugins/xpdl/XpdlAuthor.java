package org.apromore.processmining.plugins.xpdl;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Author"> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:string"> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:extension>
 *         </xsd:simpleContent> </xsd:complexType> </xsd:element>
 */

public class XpdlAuthor extends XpdlElement {

	public XpdlAuthor(String tag) {
		super(tag);
	}
}
