package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlDataField;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="DataFields"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:DataField" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlDataFields extends XpdlCollections<XpdlDataField> {

	public XpdlDataFields(String tag) {
		super(tag);
	}

	public XpdlDataField create() {
		return new XpdlDataField("DataField");
	}
}
