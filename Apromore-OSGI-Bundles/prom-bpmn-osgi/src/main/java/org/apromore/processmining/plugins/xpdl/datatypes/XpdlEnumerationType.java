package org.apromore.processmining.plugins.xpdl.datatypes;

import org.apromore.processmining.plugins.xpdl.XpdlEnumerationValue;
import org.apromore.processmining.plugins.xpdl.collections.XpdlCollections;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="EnumerationType"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:EnumerationValue" maxOccurs="unbounded"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlEnumerationType extends XpdlCollections<XpdlEnumerationValue> {

	public XpdlEnumerationType(String tag) {
		super(tag);
	}

	public XpdlEnumerationValue create() {
		return new XpdlEnumerationValue("EnumerationValue");
	}
}
