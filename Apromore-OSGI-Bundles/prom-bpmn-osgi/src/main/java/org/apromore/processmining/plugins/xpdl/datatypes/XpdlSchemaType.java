package org.apromore.processmining.plugins.xpdl.datatypes;

import org.apromore.processmining.plugins.xpdl.XpdlElement;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="SchemaType"> <xsd:complexType> <xsd:sequence>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlSchemaType extends XpdlElement {

	public XpdlSchemaType(String tag) {
		super(tag);
	}

}
