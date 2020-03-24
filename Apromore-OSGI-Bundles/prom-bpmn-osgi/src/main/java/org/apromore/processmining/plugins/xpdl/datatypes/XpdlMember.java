package org.apromore.processmining.plugins.xpdl.datatypes;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Member"> <xsd:complexType> <xsd:group
 *         ref="xpdl:DataTypes"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlMember extends XpdlDataTypes {

	public XpdlMember(String tag) {
		super(tag);
	}

}
