package org.apromore.processmining.plugins.xpdl;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="TriggerMultiple"> <xsd:annotation>
 *         <xsd:documentation> BPMN: if the TriggerType is Multiple then this
 *         must be present. </xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:sequence> <xsd:annotation> <xsd:documentation>
 *         BPMN: For Multiple, at least two triggers must be present.
 *         </xsd:documentation> </xsd:annotation> <xsd:element
 *         ref="xpdl:TriggerResultMessage" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerTimer" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ResultError" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerEscalation" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultCompensation" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerConditional" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultSignal" minOccurs="0"/> <xsd:any
 *         namespace="##other" processContents="lax" minOccurs="0"
 *         maxOccurs="unbounded"/> </xsd:sequence> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlTriggerMultiple extends XpdlElement {

	/*
	 * Elements
	 */
	private XpdlTriggerResultMessage triggerResultMessage;
	private XpdlTriggerTimer triggerTimer;
	private XpdlTriggerConditional triggerConditional;
	private XpdlTriggerResultLink triggerResultLink;

	public XpdlTriggerMultiple(String tag) {
		super(tag);

		triggerResultMessage = null;
		triggerTimer = null;
		triggerConditional = null;
		triggerResultLink = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("TriggerResultMessage")) {
			triggerResultMessage = new XpdlTriggerResultMessage("TriggerResultMessage");
			triggerResultMessage.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TriggerTimer")) {
			triggerTimer = new XpdlTriggerTimer("TriggerTimer");
			triggerTimer.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TriggerConditional")) {
			triggerConditional = new XpdlTriggerConditional("TriggerConditional");
			triggerConditional.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TriggerResultLink")) {
			triggerResultLink = new XpdlTriggerResultLink("TriggerResultLink");
			triggerResultLink.importElement(xpp, xpdl);
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
		if (triggerResultMessage != null) {
			s += triggerResultMessage.exportElement();
		}
		if (triggerTimer != null) {
			s += triggerTimer.exportElement();
		}
		if (triggerConditional != null) {
			s += triggerConditional.exportElement();
		}
		if (triggerResultLink != null) {
			s += triggerResultLink.exportElement();
		}
		return s;
	}

	public XpdlTriggerResultMessage getTriggerResultMessage() {
		return triggerResultMessage;
	}

	public void setTriggerResultMessage(XpdlTriggerResultMessage triggerResultMessage) {
		this.triggerResultMessage = triggerResultMessage;
	}

	public XpdlTriggerTimer getTriggerTimer() {
		return triggerTimer;
	}

	public void setTriggerTimer(XpdlTriggerTimer triggerTimer) {
		this.triggerTimer = triggerTimer;
	}

	public XpdlTriggerConditional getTriggerConditional() {
		return triggerConditional;
	}

	public void setTriggerConditional(XpdlTriggerConditional triggerConditional) {
		this.triggerConditional = triggerConditional;
	}

	public XpdlTriggerResultLink getTriggerResultLink() {
		return triggerResultLink;
	}

	public void setTriggerResultLink(XpdlTriggerResultLink triggerResultLink) {
		this.triggerResultLink = triggerResultLink;
	}

}
