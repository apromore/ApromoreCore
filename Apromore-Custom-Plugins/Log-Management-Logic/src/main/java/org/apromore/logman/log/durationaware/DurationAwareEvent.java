package org.apromore.logman.log.durationaware;

import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XEventImpl;

/**
 * DurationAwareEvent is created from aggregating a pair of start and complete events
 * @author Bruce Nguyen
 *
 */
public class DurationAwareEvent extends XEventImpl {
	private XEvent startEvent;
	private XEvent completeEvent;
	
	public DurationAwareEvent(XEvent start, XEvent complete) {
		super();
		this.startEvent = start;
		this.completeEvent = complete;
		this.setAttributes(complete.getAttributes());
	}
	
	public XEvent getStartEvent() {
		return this.startEvent;
	}
	
	public void setStartEvent(XEvent event) {
		this.startEvent = event;
	}
	
	public XEvent getCompleteEvent() {
		return this.completeEvent;
	}
	
	public void setCompleteEvent(XEvent event) {
		this.completeEvent = event;
	}
	
	public long getDuration() {
		return XTimeExtension.instance().extractTimestamp(completeEvent).getTime() - 
			XTimeExtension.instance().extractTimestamp(startEvent).getTime();
	}
	
//	public int getRawEventCounts() {
//		if (startEvent instanceof ArtificialEvent || completeEvent instanceof ArtificialEvent) {
//			return 1;
//		}
//		else {
//			return 2;
//		}
//	}
}
