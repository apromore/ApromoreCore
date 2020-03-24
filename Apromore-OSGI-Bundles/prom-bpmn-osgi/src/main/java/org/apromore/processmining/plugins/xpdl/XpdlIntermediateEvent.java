package org.apromore.processmining.plugins.xpdl;

import java.util.Arrays;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.SubProcess;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Swimlane;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventType;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventUse;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="IntermediateEvent"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:choice minOccurs="0"> <xsd:element
 *         ref="xpdl:TriggerResultMessage" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerTimer" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ResultError" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerEscalation" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultCompensation" minOccurs="0"> <xsd:annotation>
 *         <xsd:documentation> BPMN: Must be present if if Trigger or ResultType
 *         is Compensation.[This event can be attached or throwing. Thus name of
 *         element should be TriggerResultCompensation.] </xsd:documentation>
 *         </xsd:annotation> </xsd:element> <xsd:element
 *         ref="xpdl:TriggerConditional" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultLink" minOccurs="0"> <xsd:annotation>
 *         <xsd:documentation> BPMN: Link event connects source and target nodes
 *         of the same process or subprocess. Equivalent to a sequence flow
 *         between source and target nodes. </xsd:documentation>
 *         </xsd:annotation> </xsd:element> <xsd:element
 *         ref="xpdl:TriggerResultCancel" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultSignal" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerIntermediateMultiple" minOccurs="0">
 *         <xsd:annotation> <xsd:documentation> BPMN: if the TriggerType is
 *         Multiple then this must be present. Only valid for attached event.
 *         [EventDetail elements are incorrect. They should be message, timer,
 *         error, conditional, signal, cancel.] </xsd:documentation>
 *         </xsd:annotation> </xsd:element> </xsd:choice> <xsd:attribute
 *         name="Trigger" use="required"> <xsd:simpleType> <xsd:restriction
 *         base="xsd:NMTOKEN"> <xsd:enumeration value="None"/> <xsd:enumeration
 *         value="Message"/> <xsd:enumeration value="Timer"/> <xsd:enumeration
 *         value="Error"/> <xsd:enumeration value="Escalation"/>
 *         <xsd:enumeration value="Cancel"/> <xsd:enumeration
 *         value="Conditional"/> <xsd:enumeration value="Link"/>
 *         <xsd:enumeration value="Signal"/> <xsd:enumeration
 *         value="Compensation"/> <xsd:enumeration value="Multiple"/>
 *         <xsd:enumeration value="ParallelMultiple"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="Implementation" use="optional" default="WebService">
 *         <xsd:annotation> <xsd:documentation>Required if the Trigger is
 *         Message</xsd:documentation> </xsd:annotation> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration
 *         value="WebService"/> <xsd:enumeration value="Other"/>
 *         <xsd:enumeration value="Unspecified"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:attribute name="Target"
 *         type="xpdl:Id" use="optional"> <xsd:annotation> <xsd:documentation>
 *         BPMN: Presence of attribute indicates attached intermediate event;
 *         attribute value points to the BPMN activity (task or subprocess) the
 *         event is attached to. Absence of the attribute indicates intermediate
 *         event in sequence flow. Pointer to Activity/@Id, where activity type
 *         must be a task or subprocess. </xsd:documentation> </xsd:annotation>
 *         </xsd:attribute> <xsd:attribute name="Interrupting"
 *         type="xsd:boolean" use="optional" default="true"> <xsd:annotation>
 *         <xsd:documentation>BPMN: Determine if the Event is
 *         Interrupting</xsd:documentation> </xsd:annotation> </xsd:attribute>
 *         <xsd:anyAttribute namespace="##other" processContents="lax"/>
 *         </xsd:complexType> </xsd:element>
 */
public class XpdlIntermediateEvent extends XpdlElement {

	/*
	 * Attributes
	 */
	private String trigger;
	private String implementation;
	private String target;

	/*
	 * Elements
	 */
	private XpdlTriggerResultMessage triggerResultMessage;
	private XpdlTriggerTimer triggerTimer;
	private XpdlResultError resultError;
	private XpdlTriggerResultCompensation triggerResultCompensation;
	private XpdlTriggerConditional triggerConditional;
	private XpdlTriggerResultLink triggerResultLink;
	private XpdlTriggerResultCancel triggerResultCancel;
	private XpdlTriggerResultSignal triggerResultSignal;
	private XpdlTriggerIntermediateMultiple triggerIntermediateMultiple;

	public XpdlIntermediateEvent(String tag) {
		super(tag);

		trigger = null;
		implementation = null;
		target = null;

		triggerResultMessage = null;
		triggerTimer = null;
		resultError = null;
		triggerResultCompensation = null;
		triggerConditional = null;
		triggerResultLink = null;
		triggerResultCancel = null;
		triggerResultSignal = null;
		triggerIntermediateMultiple = null;
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
		if (xpp.getName().equals("ResultError")) {
			resultError = new XpdlResultError("ResultError");
			resultError.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TriggerResultCompensation")) {
			triggerResultCompensation = new XpdlTriggerResultCompensation("TriggerResultCompensation");
			triggerResultCompensation.importElement(xpp, xpdl);
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
		if (xpp.getName().equals("TriggerResultCancel")) {
			triggerResultCancel = new XpdlTriggerResultCancel("TriggerResultCancel");
			triggerResultCancel.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TriggerResultSignal")) {
			triggerResultSignal = new XpdlTriggerResultSignal("TriggerResultSignal");
			triggerResultSignal.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("TriggerIntermediateMultiple")) {
			triggerIntermediateMultiple = new XpdlTriggerIntermediateMultiple("TriggerIntermediateMultiple");
			triggerIntermediateMultiple.importElement(xpp, xpdl);
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
		if (resultError != null) {
			s += resultError.exportElement();
		}
		if (triggerResultCompensation != null) {
			s += triggerResultCompensation.exportElement();
		}
		if (triggerConditional != null) {
			s += triggerConditional.exportElement();
		}
		if (triggerResultLink != null) {
			s += triggerResultLink.exportElement();
		}
		if (triggerResultCancel != null) {
			s += triggerResultCancel.exportElement();
		}
		if (triggerResultSignal != null) {
			s += triggerResultSignal.exportElement();
		}
		if (triggerIntermediateMultiple != null) {
			s += triggerIntermediateMultiple.exportElement();
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
		value = xpp.getAttributeValue(null, "Target");
		if (value != null) {
			target = value;
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
		if (target != null) {
			s += exportAttribute("Target", target);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRestriction(xpdl, "Trigger", trigger, Arrays.asList("None", "Message", "Timer", "Error", "Cancel",
				"Conditional", "Link", "Signal", "Compensation", "Multiple"), true);
		checkRestriction(xpdl, "Implementation", implementation, Arrays.asList("WebService", "Other", "Unspecified"),
				false);
	}

	public void convertToBpmn(BPMNDiagram bpmn, String id, String name, DirectedGraphNode parent,
			Map<String, BPMNNode> id2node, Map<String, EventTrigger> eventTriggerMap) {
		Activity boundaryActivity = null;
		if (target != null) {
			BPMNNode targetNode = id2node.get(target);
			if (targetNode instanceof Activity) {
				boundaryActivity = (Activity) targetNode;
			}
		}
		EventUse eventUse = EventUse.CATCH;
		if (triggerResultMessage != null && triggerResultMessage.getCatchThrow().equals("THROW")) {
			eventUse = EventUse.THROW;
		}
		if (parent == null) {
			id2node.put(id, bpmn.addEvent(name, EventType.INTERMEDIATE, eventTriggerMap.get(trigger), eventUse, boundaryActivity));
		} else {
			if (parent instanceof Swimlane) {
				id2node.put(id, bpmn.addEvent(name, EventType.INTERMEDIATE, eventTriggerMap.get(trigger), eventUse,
						(Swimlane)parent, boundaryActivity));
			} else {
				id2node.put(id, bpmn.addEvent(name, EventType.INTERMEDIATE, eventTriggerMap.get(trigger), eventUse,
						(SubProcess)parent, boundaryActivity));
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

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
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

	public XpdlResultError getResultError() {
		return resultError;
	}

	public void setResultError(XpdlResultError resultError) {
		this.resultError = resultError;
	}

	public XpdlTriggerResultCompensation getTriggerResultCompensation() {
		return triggerResultCompensation;
	}

	public void setTriggerResultCompensation(XpdlTriggerResultCompensation triggerResultCompensation) {
		this.triggerResultCompensation = triggerResultCompensation;
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

	public XpdlTriggerResultCancel getTriggerResultCancel() {
		return triggerResultCancel;
	}

	public void setTriggerResultCancel(XpdlTriggerResultCancel triggerResultCancel) {
		this.triggerResultCancel = triggerResultCancel;
	}

	public XpdlTriggerResultSignal getTriggerResultSignal() {
		return triggerResultSignal;
	}

	public void setTriggerResultSignal(XpdlTriggerResultSignal triggerResultSignal) {
		this.triggerResultSignal = triggerResultSignal;
	}

	public XpdlTriggerIntermediateMultiple getTriggerIntermediateMultiple() {
		return triggerIntermediateMultiple;
	}

	public void setTriggerIntermediateMultiple(XpdlTriggerIntermediateMultiple triggerIntermediateMultiple) {
		this.triggerIntermediateMultiple = triggerIntermediateMultiple;
	}
}
