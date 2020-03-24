package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TransitionRestriction"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element ref="xpdl:Join" minOccurs="0"/>
 *         <xsd:element ref="xpdl:Split" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlTransitionRestriction extends XpdlElement {

	/*
	 * Elements
	 */
	private XpdlJoin join;
	private XpdlSplit split;

	public XpdlTransitionRestriction(String tag) {
		super(tag);

		join = null;
		split = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Join")) {
			join = new XpdlJoin("Join");
			join.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("Split")) {
			split = new XpdlSplit("Split");
			split.importElement(xpp, xpdl);
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
		if (join != null) {
			s += join.exportElement();
		}
		if (split != null) {
			s += split.exportElement();
		}
		return s;
	}

	public XpdlJoin getJoin() {
		return join;
	}

	public void setJoin(XpdlJoin join) {
		this.join = join;
	}

	public XpdlSplit getSplit() {
		return split;
	}

	public void setSplit(XpdlSplit split) {
		this.split = split;
	}
}
