package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.datatypes.XpdlTypeDeclaration;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TypeDeclarations"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:TypeDeclaration" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlTypeDeclarations extends XpdlCollections<XpdlTypeDeclaration> {

	public XpdlTypeDeclarations(String tag) {
		super(tag);
	}

	public XpdlTypeDeclaration create() {
		return new XpdlTypeDeclaration("TypeDeclaration");
	}

}
