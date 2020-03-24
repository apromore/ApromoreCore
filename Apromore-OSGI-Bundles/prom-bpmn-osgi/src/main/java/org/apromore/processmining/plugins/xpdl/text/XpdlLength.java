package org.apromore.processmining.plugins.xpdl.text;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Length"> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:string"> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:extension>
 *         </xsd:simpleContent> </xsd:complexType> </xsd:element>
 */
public class XpdlLength extends XpdlText {

	public XpdlLength(String tag) {
		super(tag);
	}

}
