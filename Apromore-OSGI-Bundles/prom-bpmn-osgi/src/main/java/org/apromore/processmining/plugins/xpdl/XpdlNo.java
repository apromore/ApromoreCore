package org.apromore.processmining.plugins.xpdl;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="No"> <xsd:complexType> <xsd:sequence> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlNo extends XpdlElement {

	public XpdlNo(String tag) {
		super(tag);
	}

}
