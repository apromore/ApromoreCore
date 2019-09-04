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
 * This log must be an XLog as it will replace the original log
 * in all log operations: filter, select, etc.
 * If classifierAttribuge = concept:name, it will aggregates start and complete events
 * Otherwise, it just copies properties and all traces from the raw XLog
 * There is a communication channel established between this log and he raw XLog
 * such that any changes on this log will be updated into the raw XLog and vice versa 
 * For example, the follower filter will operate on the DurationAwareLog while
 * the attribute filter operates on the raw XLog
 * @author Bruce Nguyen
 *
 */
public class DurationAwareLog extends XLogImpl {
	private XLog rawLog;	
	
	public DurationAwareLog(XLog log) {
		super(log.getAttributes());
		this.rawLog = log;
		for (XTrace trace: log) {
			this.add(new DurationAwareTrace(trace));
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
