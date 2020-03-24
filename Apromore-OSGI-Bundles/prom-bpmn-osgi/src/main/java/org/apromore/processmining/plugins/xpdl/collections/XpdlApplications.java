package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlApplication;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Applications"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Application" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlApplications extends XpdlCollections<XpdlApplication> {

	public XpdlApplications(String tag) {
		super(tag);
	}

	public XpdlApplication create() {
		return new XpdlApplication("Application");
	}

}
