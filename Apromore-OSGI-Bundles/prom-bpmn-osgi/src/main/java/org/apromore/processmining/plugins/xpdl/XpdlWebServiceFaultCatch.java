package org.apromore.processmining.plugins.xpdl;

import org.apromore.processmining.plugins.xpdl.idname.XpdlTransitionRef;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="WebServiceFaultCatch"> <xsd:complexType>
 *         <xsd:sequence> <xsd:element name="Message" type="xpdl:MessageType"
 *         minOccurs="0"/> <xsd:choice> <xsd:element ref="xpdl:BlockActivity"/>
 *         <xsd:element ref="xpdl:TransitionRef"/> </xsd:choice> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:attribute
 *         name="FaultName" type="xsd:NMTOKEN" use="optional"/>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlWebServiceFaultCatch extends XpdlElement {

	/*
	 * Attributes
	 */
	private String faultName;

	/*
	 * Elements
	 */
	private XpdlBlockActivity blockActivity;
	private XpdlTransitionRef transitionRef;

	public XpdlWebServiceFaultCatch(String tag) {
		super(tag);

		faultName = null;

		blockActivity = null;
		transitionRef = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("BlockActivity")) {
			blockActivity = new XpdlBlockActivity("BlockActivity");
			blockActivity.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TransitionRef")) {
			transitionRef = new XpdlTransitionRef("TransitionRef");
			transitionRef.importElement(xpp, xpdl);
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
		if (blockActivity != null) {
			s += blockActivity.exportElement();
		}
		if (transitionRef != null) {
			s += transitionRef.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "FaultName");
		if (value != null) {
			faultName = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (faultName != null) {
			s += exportAttribute("FaultName", faultName);
		}
		return s;
	}

	public XpdlBlockActivity getBlockActivity() {
		return blockActivity;
	}

	public void setBlockActivity(XpdlBlockActivity blockActivity) {
		this.blockActivity = blockActivity;
	}

	public String getFaultName() {
		return faultName;
	}

	public void setFaultName(String faultName) {
		this.faultName = faultName;
	}

	public XpdlTransitionRef getTransitionRef() {
		return transitionRef;
	}

	public void setTransitionRef(XpdlTransitionRef transitionRef) {
		this.transitionRef = transitionRef;
	}
}
