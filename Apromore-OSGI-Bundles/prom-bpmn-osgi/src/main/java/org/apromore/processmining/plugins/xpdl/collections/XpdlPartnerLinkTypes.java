package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.XpdlPartnerLinkType;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="PartnerLinkTypes"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:PartnerLinkType"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlPartnerLinkTypes extends XpdlCollections<XpdlPartnerLinkType> {

	public XpdlPartnerLinkTypes(String tag) {
		super(tag);
	}

	public XpdlPartnerLinkType create() {
		return new XpdlPartnerLinkType("PartnerLinkType");
	}
}
