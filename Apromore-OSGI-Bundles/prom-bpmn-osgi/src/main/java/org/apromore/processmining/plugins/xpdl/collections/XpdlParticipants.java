package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlParticipant;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Participants"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Participant" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlParticipants extends XpdlCollections<XpdlParticipant> {

	public XpdlParticipants(String tag) {
		super(tag);
	}

	public XpdlParticipant create() {
		return new XpdlParticipant("Participant");
	}
}
