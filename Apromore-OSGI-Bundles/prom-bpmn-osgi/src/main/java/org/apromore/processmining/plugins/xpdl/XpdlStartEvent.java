package org.apromore.processmining.plugins.xpdl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="StartEvent"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:choice minOccurs="0"> <xsd:element
 *         ref="xpdl:TriggerResultMessage" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerTimer" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ResultError" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerEscalation" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultCompensation" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerConditional" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultSignal" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerMultiple" minOccurs="0"/> </xsd:choice>
 *         <xsd:attribute name="Trigger" use="required"> <xsd:annotation>
 *         <xsd:documentation>BPMN: Trigger or Result type for the
 *         event</xsd:documentation> </xsd:annotation> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration value="None"/>
 *         <xsd:enumeration value="Message"/> <xsd:enumeration value="Timer"/>
 *         <xsd:enumeration value="Error"/> <xsd:enumeration
 *         value="Escalation"/> <xsd:enumeration value="Compensation"/>
 *         <xsd:enumeration value="Conditional"/> <xsd:enumeration
 *         value="Signal"/> <xsd:enumeration value="Multiple"/> <xsd:enumeration
 *         value="ParallelMultiple"/> </xsd:restriction> </xsd:simpleType>
 *         </xsd:attribute> <xsd:attribute name="Implementation" use="optional"
 *         default="WebService"> <xsd:annotation> <xsd:documentation>Required if
 *         the Trigger is Message</xsd:documentation> </xsd:annotation>
 *         <xsd:simpleType> <xsd:restriction base="xsd:NMTOKEN">
 *         <xsd:enumeration value="WebService"/> <xsd:enumeration
 *         value="Other"/> <xsd:enumeration value="Unspecified"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="Interrupting" type="xsd:boolean" use="optional" default="true">
 *         <xsd:annotation> <xsd:documentation>BPMN: Determine if the Event is
 *         Interrupting</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlStartEvent extends XpdlElement {

	/*
	 * Attributes
	 */
	private String trigger;
	private String implementation;

	/*
	 * Elements
	 */
	private XpdlTriggerResultMessage triggerResultMessage;
	private XpdlTriggerTimer triggerTimer;
	private XpdlTriggerConditional triggerConditional;
	private XpdlTriggerResultSignal triggerResultSignal;
	private XpdlTriggerMultiple triggerMultiple;

	public XpdlStartEvent(String tag) {
		super(tag);

		trigger = null;
		implementation = null;

		triggerResultMessage = null;
		triggerTimer = null;
		triggerConditional = null;
		triggerResultSignal = null;
		triggerMultiple = null;
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
		if (xpp.getName().equals("TriggerResultSignal")) {
			triggerResultSignal = new XpdlTriggerResultSignal("TriggerResultSignal");
			triggerResultSignal.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TriggerMultiple")) {
			triggerMultiple = new XpdlTriggerMultiple("TriggerMultiple");
			triggerMultiple.importElement(xpp, xpdl);
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
		if (triggerResultSignal != null) {
			s += triggerResultSignal.exportElement();
		}
		if (triggerMultiple != null) {
			s += triggerMultiple.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Trigger");
		if (value != null) {
			trigger = value;
		}
		value = xpp.getAttributeValue(null, "Implementation");
		if (value != null) {
			implementation = value;
		}
	}

	/**
	 * Exports all attributes.
	 */
	protected String exportAttributes() {
		String s = super.exportAttributes();
		if (trigger != null) {
			s += exportAttribute("Trigger", trigger);
		}
		if (implementation != null) {
			s += exportAttribute("Implementation", implementation);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Trigger", trigger,
				new ArrayList<String>(Arrays.asList("None", "Message", "Timer", "Conditional", "Signal", "Multiple")) {
					/**
			 * 
			 */
					private static final long serialVersionUID = -4488549092665137956L;

					@Override
					public boolean contains(Object temp) {
						boolean result = false;
						if (temp instanceof String) {
							for (String element : this) {
								if (element.equalsIgnoreCase("" + temp)) {
									result = true;
									break;
								}
							}
						}
						return result;
					}
				}, true);
		checkRestriction(xpdl, "Implementation", implementation, Arrays.asList("WebService", "Other", "Unspecified"),
				false);
	}

	public void convertToBpmn(BPMNDiagram bpmn, String id, String name, DirectedGraphNode parent,
			Map<String, BPMNNode> id2node, Map<String, EventTrigger> eventTriggerMap) {
		if (parent == null) {
			id2node.put(id, bpmn.addEvent(name, EventType.START, eventTriggerMap.get(trigger), EventUse.CATCH, null));
		} else {
			if (parent instanceof Swimlane) {
				id2node.put(id, bpmn.addEvent(name, EventType.START, eventTriggerMap.get(trigger), EventUse.CATCH,
						(Swimlane) parent, null));
			} else {
				id2node.put(id, bpmn.addEvent(name, EventType.START, eventTriggerMap.get(trigger), EventUse.CATCH,
						(SubProcess) parent, null));
			}
		}

	}

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
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

	public XpdlTriggerResultSignal getTriggerResultSignal() {
		return triggerResultSignal;
	}

	public void setTriggerResultSignal(XpdlTriggerResultSignal triggerResultSignal) {
		this.triggerResultSignal = triggerResultSignal;
	}

	public XpdlTriggerMultiple getTriggerMultiple() {
		return triggerMultiple;
	}

	public void setTriggerMultiple(XpdlTriggerMultiple triggerMultiple) {
		this.triggerMultiple = triggerMultiple;
	}
}
