package org.apromore.processmining.plugins.bpmn;

import java.util.Collection;
import java.util.HashSet;

import org.xmlpull.v1.XmlPullParser;

public class BpmnIncomingOutgoing extends BpmnIdName {
	
	protected Collection<BpmnIncoming> incomings;
	protected Collection<BpmnOutgoing> outgoings;
	
	public BpmnIncomingOutgoing(String tag) {
		super(tag);
		
		incomings = new HashSet<BpmnIncoming>();
		outgoings = new HashSet<BpmnOutgoing>();
	}

	protected boolean importElements(XmlPullParser xpp, Bpmn bpmn) {
		if (super.importElements(xpp, bpmn)) {
			/*
			 * Start tag corresponds to a known child element of an XPDL node.
			 */
			return true;
		}
		if (xpp.getName().equals("incoming")) {
			BpmnIncoming incoming = new BpmnIncoming("incoming");
			incoming.importElement(xpp, bpmn);
			incomings.add(incoming);
			return true;
		} else if (xpp.getName().equals("outgoing")) {
			BpmnOutgoing outgoing = new BpmnOutgoing("outgoing");
			outgoing.importElement(xpp, bpmn);
			outgoings.add(outgoing);
			return true;
		}
		/*
		 * Unknown tag.
		 */
		return false;
	}
	
	protected String exportElements() {
		/*
		 * Export node child elements.
		 */
		String s = super.exportElements();
		for (BpmnIncoming incoming : incomings) {
			s += incoming.exportElement();
		}
		for (BpmnOutgoing outgoing : outgoings) {
			s += outgoing.exportElement();
		}
		return s;
	}

}
