package org.apromore.processmining.plugins.xpdl;

import java.util.HashMap;
import java.util.Map;

import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.apromore.processmining.models.graphbased.directed.bpmn.elements.Event.EventTrigger;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author hverbeek
 * 
 *         <xsd:element name="Event"> <xsd:annotation>
 *         <xsd:documentation>BPMN</xsd:documentation> </xsd:annotation>
 *         <xsd:complexType> <xsd:choice> <xsd:element ref="xpdl:StartEvent"
 *         minOccurs="0"/> <xsd:element ref="xpdl:IntermediateEvent"
 *         minOccurs="0"/> <xsd:element ref="xpdl:EndEvent" minOccurs="0"/>
 *         </xsd:choice> <xsd:anyAttribute namespace="##other"
 *         processContents="lax"/> </xsd:complexType> </xsd:element>
 */
public class XpdlEvent extends XpdlElement {

	/*
	 * Elements
	 */
	private XpdlStartEvent startEvent;
	private XpdlIntermediateEvent intermediateEvent;
	private XpdlEndEvent endEvent;

	public XpdlEvent(String tag) {
		super(tag);

		startEvent = null;
		intermediateEvent = null;
		endEvent = null;
	}

	protected boolean importElements(XmlPullParser xpp, Xpdl xpdl) {
		if (super.importElements(xpp, xpdl)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("StartEvent")) {
			startEvent = new XpdlStartEvent("StartEvent");
			startEvent.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("IntermediateEvent")) {
			intermediateEvent = new XpdlIntermediateEvent("IntermediateEvent");
			intermediateEvent.importElement(xpp, xpdl);
			return true;
		}
		if (xpp.getName().equals("EndEvent")) {
			endEvent = new XpdlEndEvent("EndEvent");
			endEvent.importElement(xpp, xpdl);
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
		if (startEvent != null) {
			s += startEvent.exportElement();
		}
		if (intermediateEvent != null) {
			s += intermediateEvent.exportElement();
		}
		if (endEvent != null) {
			s += endEvent.exportElement();
		}
		return s;
	}

	protected void checkValidity(Xpdl xpdl) {
		super.checkValidity(xpdl);
		int nr = (startEvent != null ? 1 : 0) + (intermediateEvent != null ? 1 : 0) + (endEvent != null ? 1 : 0);
		if (nr > 1) {
			xpdl.log(tag, lineNumber, "Expected only one type of event");
		}
	}

	public void convertToBpmn(BPMNDiagram bpmn, String id, String name, DirectedGraphNode parent, Map<String, BPMNNode> id2node) {
		Map<String, EventTrigger> eventTriggerMap = new HashMap<String, EventTrigger>() {
			private static final long serialVersionUID = -7540595389022504040L;
			{
				put("Message", EventTrigger.MESSAGE);
				put("None", EventTrigger.NONE);
				put("Timer", EventTrigger.TIMER);
				put("Rule", EventTrigger.CONDITIONAL);
				put("Link", EventTrigger.LINK);
				put("Multiple", EventTrigger.MULTIPLE);
				put("Error", EventTrigger.ERROR);
				put("Compensation", EventTrigger.COMPENSATION);
				put("Cancel", EventTrigger.CANCEL);
				put("Terminate", EventTrigger.TERMINATE);
				put("Signal", EventTrigger.SIGNAL);
			}
		};
		if (startEvent != null) {
			startEvent.convertToBpmn(bpmn, id, name, parent, id2node, eventTriggerMap);
		} else if (intermediateEvent != null) {
			intermediateEvent.convertToBpmn(bpmn, id, name, parent, id2node, eventTriggerMap);
		} else if (endEvent != null) {
			endEvent.convertToBpmn(bpmn, id, name, parent, id2node, eventTriggerMap);
		} else {
			(new XpdlIntermediateEvent("IntermediateEvent")).convertToBpmn(bpmn, id, name, parent, id2node, eventTriggerMap);
		}
	}

	public XpdlStartEvent getStartEvent() {
		return startEvent;
	}

	public void setStartEvent(XpdlStartEvent startEvent) {
		this.startEvent = startEvent;
	}

	public XpdlIntermediateEvent getIntermediateEvent() {
		return intermediateEvent;
	}

	public void setIntermediateEvent(XpdlIntermediateEvent intermediateEvent) {
		this.intermediateEvent = intermediateEvent;
	}

	public XpdlEndEvent getEndEvent() {
		return endEvent;
	}

	public void setEndEvent(XpdlEndEvent endEvent) {
		this.endEvent = endEvent;
	}
}
