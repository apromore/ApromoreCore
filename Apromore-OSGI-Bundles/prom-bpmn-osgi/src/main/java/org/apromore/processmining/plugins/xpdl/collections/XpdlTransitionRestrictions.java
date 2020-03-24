package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.XpdlTransitionRestriction;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TransitionRestrictions"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:TransitionRestriction"
 *         minOccurs="0" maxOccurs="unbounded"/> <xsd:any namespace="##other"
 *         processContents="lax" minOccurs="0" maxOccurs="unbounded"/>
 *         </xsd:sequence> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlTransitionRestrictions extends XpdlCollections<XpdlTransitionRestriction> {

	public XpdlTransitionRestrictions(String tag) {
		super(tag);
	}

	public XpdlTransitionRestriction create() {
		return new XpdlTransitionRestriction("TransitionRestriction");
	}

}
