package org.apromore.processmining.plugins.xpdl.text;

import org.apromore.processmining.plugins.xpdl.Xpdl;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Scale"> <xsd:complexType> <xsd:simpleContent>
 *         <xsd:extension base="xsd:short"> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:extension>
 *         </xsd:simpleContent> </xsd:complexType> </xsd:element>
 */
public class XpdlScale extends XpdlText {

	public XpdlScale(String tag) {
		super(tag);
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		try {
			Integer i = Integer.valueOf(text);
			if ((i < -32768) || (i > 32768)) {
				xpdl.log(tag, lineNumber, "Expected a short");
			}
		} catch (Exception ex) {
			xpdl.log(tag, lineNumber, ex.getMessage());
		}
	}
}
