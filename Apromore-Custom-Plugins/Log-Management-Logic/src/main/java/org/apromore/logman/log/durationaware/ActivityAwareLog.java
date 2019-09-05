package org.apromore.logman.log.durationaware;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.logfilter.criteria.LogFilterCriterion;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;

/** 
 * This log contains only ActivityAwareTrace
 * @author Bruce Nguyen
 *
 */
public class ActivityAwareLog extends XLogImpl {
	private XLog rawLog;	
	
	public ActivityAwareLog(XLog log) {
		super(log.getAttributes());
		this.rawLog = log;
		for (XTrace trace: log) {
			this.add(new ActivityAwareTrace(trace));
		}
	}
	
	public XLog getRawLog() {
		return this.rawLog;
	}
	
	
//	public void filter(List<LogFilterCriterion> filterCriteria) {
//		//Mark filtered out events
//		Set<XEvent> removedEvents = new HashSet<>();
//		for (XTrace trace: this) {
//			Iterator<XEvent> iterator = trace.iterator();
//			while (iterator.hasNext()) {
//				DurationAwareEvent dEvent = (DurationAwareEvent)iterator.next();
//				if (removedEvents.contains(dEvent.getStartEvent()) && 
//						removedEvents.contains(dEvent.getCompleteEvent())) {
//					iterator.remove();
//				}
//				else if (removedEvents.contains(dEvent.getStartEvent())) {
//					dEvent.setStartEvent(dEvent.getCompleteEvent());
//				}
//				else if (removedEvents.contains(dEvent.getCompleteEvent())) {
//					dEvent.setCompleteEvent(dEvent.getStartEvent());
//				}
//			}
//		}
//	}
}
