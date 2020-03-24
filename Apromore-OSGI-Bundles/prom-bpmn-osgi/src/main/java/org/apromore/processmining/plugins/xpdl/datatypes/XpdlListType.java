package org.apromore.processmining.plugins.xpdl.datatypes;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ListType"> <xsd:complexType> <xsd:group
 *         ref="xpdl:DataTypes"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlListType extends XpdlDataTypes {

	public XpdlListType(String tag) {
		super(tag);
	}

}
