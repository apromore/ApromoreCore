package org.apromore.processmining.plugins.xpdl.datatypes;

import org.apromore.processmining.plugins.xpdl.collections.XpdlCollections;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="RecordType"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Member" maxOccurs="unbounded"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlRecordType extends XpdlCollections<XpdlMember> {

	public XpdlRecordType(String tag) {
		super(tag);
	}

	public XpdlMember create() {
		return new XpdlMember("Member");
	}
}
