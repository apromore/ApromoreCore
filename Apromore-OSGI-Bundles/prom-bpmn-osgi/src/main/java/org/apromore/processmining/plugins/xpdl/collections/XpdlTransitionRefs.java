package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlTransitionRef;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TransitionRefs"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:TransitionRef" minOccurs="0"
 *         maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlTransitionRefs extends XpdlCollections<XpdlTransitionRef> {

	public XpdlTransitionRefs(String tag) {
		super(tag);
	}

	public XpdlTransitionRef create() {
		return new XpdlTransitionRef("TransitionRef");
	}
}
