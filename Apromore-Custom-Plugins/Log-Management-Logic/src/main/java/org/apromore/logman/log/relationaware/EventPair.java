package org.apromore.logman.log.relationaware;

import org.deckfour.xes.model.XEvent;

public class EventPair {
	private XEvent firstEvent;
	private XEvent secondEvent;
	
	public EventPair(XEvent firstEvent, XEvent secondEvent) {
		this.firstEvent = firstEvent;
		this.secondEvent = secondEvent;
	}
	
	public XEvent getFirstEvent() {
		return this.firstEvent;
	}
	
	public XEvent getSecondEvent() {
		return this.secondEvent;
	}
}
