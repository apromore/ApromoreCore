package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.XpdlExtendedAttribute;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ExtendedAttributes"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:ExtendedAttribute"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlExtendedAttributes extends XpdlCollections<XpdlExtendedAttribute> {

	public XpdlExtendedAttributes(String tag) {
		super(tag);
	}

	public XpdlExtendedAttribute create() {
		return new XpdlExtendedAttribute("ExtendedAttribute");
	}
}
