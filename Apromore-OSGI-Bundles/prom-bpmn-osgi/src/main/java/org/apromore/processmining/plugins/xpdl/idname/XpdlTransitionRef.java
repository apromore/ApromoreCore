package org.apromore.processmining.plugins.xpdl.idname;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TransitionRef"> <xsd:complexType> <xsd:sequence>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute name="Id"
 *         type="xpdl:IdRef" use="required"/> <xsd:attribute name="Name"
 *         type="xsd:string" use="optional"/> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlTransitionRef extends XpdlIdName {

	public XpdlTransitionRef(String tag) {
		super(tag);
	}

}
