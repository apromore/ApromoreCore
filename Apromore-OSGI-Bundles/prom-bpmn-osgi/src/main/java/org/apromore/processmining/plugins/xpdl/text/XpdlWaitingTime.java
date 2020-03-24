package org.apromore.processmining.plugins.xpdl.text;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="WaitingTime"> <xsd:complexType>
 *         <xsd:simpleContent> <xsd:extension base="xsd:string">
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:extension> </xsd:simpleContent> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlWaitingTime extends XpdlText {

	public XpdlWaitingTime(String tag) {
		super(tag);
	}

}
