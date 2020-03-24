package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.XpdlPartnerLink;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="PartnerLinks"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:PartnerLink" maxOccurs="unbounded"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlPartnerLinks extends XpdlCollections<XpdlPartnerLink> {

	public XpdlPartnerLinks(String tag) {
		super(tag);
	}

	public XpdlPartnerLink create() {
		return new XpdlPartnerLink("PartnerLink");
	}
}
