package org.apromore.processmining.plugins.xpdl;

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
 *         <xsd:element name="EndEvent"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:choice> <xsd:element
 *         ref="xpdl:TriggerResultMessage" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ResultError" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerEscalation" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultCancel" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultCompensation" minOccurs="0"/> <xsd:element
 *         ref="xpdl:TriggerResultSignal" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ResultTerminate" minOccurs="0"/> <xsd:element
 *         ref="xpdl:ResultMultiple" minOccurs="0"/> </xsd:choice>
 *         <xsd:attribute name="Result" use="required"> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration value="None"/>
 *         <xsd:enumeration value="Message"/> <xsd:enumeration value="Error"/>
 *         <xsd:enumeration value="Escalation"/> <xsd:enumeration
 *         value="Cancel"/> <xsd:enumeration value="Compensation"/>
 *         <xsd:enumeration value="Signal"/> <xsd:enumeration
 *         value="Terminate"/> <xsd:enumeration value="Multiple"/>
 *         </xsd:restriction> </xsd:simpleType> </xsd:attribute> <xsd:attribute
 *         name="Implementation" use="optional" default="WebService">
 *         <xsd:annotation> <xsd:documentation>Required if the Trigger or Result
 *         is Message</xsd:documentation> </xsd:annotation> <xsd:simpleType>
 *         <xsd:restriction base="xsd:NMTOKEN"> <xsd:enumeration
 *         value="WebService"/> <xsd:enumeration value="Other"/>
 *         <xsd:enumeration value="Unspecified"/> </xsd:restriction>
 *         </xsd:simpleType> </xsd:attribute> <xsd:anyAttribute
 *         namespace="##other" processContents="lax"/> </xsd:complexType>
 *         </xsd:element>
 */
public class XpdlEndEvent extends XpdlElement {

	/*
	 * Attributes
	 */
	private String result;
	private String implementation;

	/*
	 * Elements
	 */
	private XpdlTriggerResultMessage triggerResultMessage;
	private XpdlResultError resultError;
	private XpdlTriggerResultCompensation triggerResultCompensation;
	private XpdlTriggerResultSignal triggerResultSignal;
	private XpdlResultMultiple resultMultiple;

	public XpdlEndEvent(String tag) {
		super(tag);

		result = null;
		implementation = null;

		triggerResultMessage = null;
		resultError = null;
		triggerResultCompensation = null;
		triggerResultSignal = null;
		resultMultiple = null;
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
		if (xpp.getName().equals("TriggerResultSignal")) {
			triggerResultSignal = new XpdlTriggerResultSignal("TriggerResultSignal");
			triggerResultSignal.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("ResultMultiple")) {
			resultMultiple = new XpdlResultMultiple("ResultMultiple");
			resultMultiple.importElement(xpp, xpdl);
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
		if (resultError != null) {
			s += resultError.exportElement();
		}
		if (triggerResultCompensation != null) {
			s += triggerResultCompensation.exportElement();
		}
		if (triggerResultSignal != null) {
			s += triggerResultSignal.exportElement();
		}
		if (resultMultiple != null) {
			s += resultMultiple.exportElement();
		}
		return s;
	}

	protected void importAttributes(XmlPullParser xpp, Xpdl xpdl) {
		super.importAttributes(xpp, xpdl);
		String value = xpp.getAttributeValue(null, "Result");
		if (value != null) {
			result = value;
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
		if (result != null) {
			s += exportAttribute("Result", result);
		}
		if (implementation != null) {
			s += exportAttribute("Implementation", implementation);
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		checkRequired(xpdl, "Result", result);
		checkRestriction(xpdl, "Result", result, Arrays.asList("None", "Message", "Error", "Cancel", "Compensation",
				"Signal", "Terminate", "Multiple"), false);
		checkRestriction(xpdl, "Implementation", implementation, Arrays.asList("WebService", "Other", "Unspecified"),
				false);
	}

	public void convertToBpmn(BPMNDiagram bpmn, String id, String name, DirectedGraphNode parent,
			Map<String, BPMNNode> id2node, Map<String, EventTrigger> eventTriggerMap) {
		if (parent == null) {
			id2node.put(id, bpmn.addEvent(name, EventType.END, eventTriggerMap.get(result), EventUse.THROW, null));
		} else {
			if (parent instanceof Swimlane) {
				id2node.put(id, bpmn.addEvent(name, EventType.END, eventTriggerMap.get(result), EventUse.THROW,(Swimlane) parent, null));
			} else {
				id2node.put(id, bpmn.addEvent(name, EventType.END, eventTriggerMap.get(result), EventUse.THROW,(SubProcess) parent, null));
			}
		}
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
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

	public XpdlTriggerResultSignal getTriggerResultSignal() {
		return triggerResultSignal;
	}

	public void setTriggerResultSignal(XpdlTriggerResultSignal triggerResultSignal) {
		this.triggerResultSignal = triggerResultSignal;
	}

	public XpdlResultMultiple getResultMultiple() {
		return resultMultiple;
	}

	public void setResultMultiple(XpdlResultMultiple resultMultiple) {
		this.resultMultiple = resultMultiple;
	}
}
