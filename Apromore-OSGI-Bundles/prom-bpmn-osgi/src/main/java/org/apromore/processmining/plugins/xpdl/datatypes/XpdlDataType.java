package org.apromore.processmining.plugins.xpdl.datatypes;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="DataType"> <xsd:complexType> <xsd:group
 *         ref="xpdl:DataTypes"/> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlDataType extends XpdlDataTypes {

	public XpdlDataType(String tag) {
		super(tag);
	}
}
