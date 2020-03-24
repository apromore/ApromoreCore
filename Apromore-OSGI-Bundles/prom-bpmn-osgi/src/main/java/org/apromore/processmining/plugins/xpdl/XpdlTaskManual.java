package org.apromore.processmining.plugins.xpdl;

import org.apromore.processmining.plugins.xpdl.collections.XpdlPerformers;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TaskManual"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:element ref="xpdl:Performers"
 *         minOccurs="0"/> <xsd:any namespace="##other" processContents="lax"
 *         minOccurs="0" maxOccurs="unbounded"/> </xsd:sequence>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlTaskManual extends XpdlElement {

	/*
	 * Elements
	 */
	private XpdlPerformers performers;

	public XpdlTaskManual(String tag) {
		super(tag);

		performers = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("Performers")) {
			performers = new XpdlPerformers("Performers");
			performers.importElement(xpp, xpdl);
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
		if (performers != null) {
			s += performers.exportElement();
		}
		return s;
	}

	public XpdlPerformers getPerformers() {
		return performers;
	}

	public void setPerformers(XpdlPerformers performers) {
		this.performers = performers;
	}
}
