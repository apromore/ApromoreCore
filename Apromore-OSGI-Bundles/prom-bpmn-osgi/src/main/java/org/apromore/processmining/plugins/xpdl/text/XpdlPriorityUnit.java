package org.apromore.processmining.plugins.xpdl.text;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="PriorityUnit"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:string">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlPriorityUnit extends XpdlText {

	public XpdlPriorityUnit(String tag) {
		super(tag);
	}

}
