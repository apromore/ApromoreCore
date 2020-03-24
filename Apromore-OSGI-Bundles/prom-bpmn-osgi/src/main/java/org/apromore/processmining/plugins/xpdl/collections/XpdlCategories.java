package org.apromore.processmining.plugins.xpdl.collections;

import org.apromore.processmining.plugins.xpdl.idname.XpdlCategory;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Categories"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Category" maxOccurs="unbounded"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlCategories extends XpdlCollections<XpdlCategory> {

	public XpdlCategories(String tag) {
		super(tag);
	}

	public XpdlCategory create() {
		return new XpdlCategory("Category");
	}

}
