package org.apromore.processmining.plugins.xpdl.text;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Performer"> <xsd:annotation> <xsd:documentation>A
 *         String or Expression designating the Performer</xsd:documentation>
 *         </xsd:annotation> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:string"> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:extension>
 *         </xsd:simpleContent> </xsd:complexType> </xsd:element>
 */
public class XpdlPerformer extends XpdlText {

	public XpdlPerformer(String tag) {
		super(tag);
	}
}
