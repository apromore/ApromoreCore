package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlArtifact;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Artifacts"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence maxOccurs="unbounded"> <xsd:element
 *         ref="xpdl:Artifact"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlArtifacts extends XpdlCollections<XpdlArtifact> {

	public XpdlArtifacts(String tag) {
		super(tag);
	}

	public XpdlArtifact create() {
		return new XpdlArtifact("Artifact");
	}

}
