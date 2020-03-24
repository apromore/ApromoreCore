package org.apromore.processmining.plugins.xpdl.text;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Responsible"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:string">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlResponsible extends XpdlText {

	public XpdlResponsible(String tag) {
		super(tag);
	}

}
