package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlPage;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Pages"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Page" minOccurs="0" maxOccurs="unbounded"/>
 *         <xsd:any namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlPages extends XpdlCollections<XpdlPage> {

	public XpdlPages(String tag) {
		super(tag);
	}

	public XpdlPage create() {
		return new XpdlPage("Page");
	}
}
