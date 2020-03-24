package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Loop"> <xsd:annotation> <xsd:documentation>BPMN
 *         (and possibly XPDL)</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:choice> <xsd:element ref="xpdl:LoopStandard"/>
 *         <xsd:element ref="xpdl:LoopMultiInstance"/> </xsd:choice>
 *         <xsd:attribute name="LoopType" use="required"> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration
 *         value="Standard"/> <xsd:enumeration value="MultiInstance"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlLoop extends XpdlElement {

	/*
	 * Attributes
	 */
	private String loopType;

	/*
	 * Elements
	 */
	private XpdlLoopStandard loopStandard;
	private XpdlLoopMultiInstance loopMultiInstance;

	public XpdlLoop(String tag) {
		super(tag);

		loopType = null;

		loopStandard = null;
		loopMultiInstance = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("LoopStandard")) {
			loopStandard = new XpdlLoopStandard("LoopStandard");
			loopStandard.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("LoopMultiInstance")) {
			loopMultiInstance = new XpdlLoopMultiInstance("LoopMultiInstance");
			loopMultiInstance.importElement(xpp, xpdl);
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
		if (loopStandard != null) {
			s += loopStandard.exportElement();
		}
		if (loopMultiInstance != null) {
			s += loopMultiInstance.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "LoopType");
		if (value != null) {
			loopType = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (loopType != null) {
			s += exportAttribute("LoopType", loopType);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "LoopType", loopType);
		int nr = (loopStandard != null ? 1 : 0) + (loopMultiInstance != null ? 1 : 0);
		if (nr > 1) {
			xpdl.log(tag, lineNumber, "Expected one loop type");
		}
	}

	public boolean hasType(String type) {
		return type.equals(loopType);
	}

	public String getLoopType() {
		return loopType;
	}

	public void setLoopType(String loopType) {
		this.loopType = loopType;
	}

	public XpdlLoopStandard getLoopStandard() {
		return loopStandard;
	}

	public void setLoopStandard(XpdlLoopStandard loopStandard) {
		this.loopStandard = loopStandard;
	}

	public XpdlLoopMultiInstance getLoopMultiInstance() {
		return loopMultiInstance;
	}

	public void setLoopMultiInstance(XpdlLoopMultiInstance loopMultiInstance) {
		this.loopMultiInstance = loopMultiInstance;
	}
}
