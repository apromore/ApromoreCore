package org.apromore.processmining.plugins.xpdl.datatypes;

import java.util.ArrayList;
import java.util.List;

import org.apromore.processmining.plugins.xpdl.Xpdl;
import org.apromore.processmining.plugins.xpdl.XpdlElement;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="UnionType"> <xsd:complexType> <xsd:sequence>
 *         <xsd:element ref="xpdl:Member" maxOccurs="unbounded"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlUnionType extends XpdlElement {

	/*
	 * Elements
	 */
	private final List<XpdlMember> memberList;

	public XpdlUnionType(String tag) {
		super(tag);

		memberList = new ArrayList<XpdlMember>();
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Member")) {
			XpdlMember member = new XpdlMember("Member");
			member.importElement(xpp, xpdl);
			memberList.add(member);
			return true;
		}
		/*
		 * Unknown tag
		 */
		return false;
	}

	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (XpdlMember member : memberList) {
			s += member.exportElement();
		}
		return s;
	}

	public List<XpdlMember> getMemberList() {
		return memberList;
	}

}
