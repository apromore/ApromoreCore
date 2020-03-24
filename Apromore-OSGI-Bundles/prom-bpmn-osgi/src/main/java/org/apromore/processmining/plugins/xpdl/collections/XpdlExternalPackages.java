package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlExternalPackage;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="ExternalPackages"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:ExternalPackage" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlExternalPackages extends XpdlCollections<XpdlExternalPackage> {

	public XpdlExternalPackages(String tag) {
		super(tag);
	}

	public XpdlExternalPackage create() {
		return new XpdlExternalPackage("ExternalPackage");
	}
}
