package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.XpdlVendorExtension;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="VendorExtensions"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:VendorExtension" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlVendorExtensions extends XpdlCollections<XpdlVendorExtension> {

	public XpdlVendorExtensions(String tag) {
		super(tag);
	}

	public XpdlVendorExtension create() {
		return new XpdlVendorExtension("VendorExtension");
	}

}
