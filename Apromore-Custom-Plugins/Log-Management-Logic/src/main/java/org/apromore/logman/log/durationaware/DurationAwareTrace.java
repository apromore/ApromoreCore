package org.apromore.logman.log.durationaware;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.eclipse.collections.api.tuple.Pair;

/**
 * DurationAwareTrace is created from the raw trace by aggregating start and complete events
 * This is kept in an activity map
 * @author Bruce Nguyen
 *
 */
public class DurationAwareTrace extends XTraceImpl {
	private XTrace trace;
	private Map<XEvent,XEvent> activityMap;
	
	public DurationAwareTrace(XTrace trace) {
		super(trace.getAttributes());
		this.trace = trace;
		this.buildActivityMap();
	}
	
	public XTrace getTrace() {
		return this.trace;
	}
	
	public Map<XEvent,XEvent> getActivityMap() {
		return Collections.unmodifiableMap(this.activityMap);
	}
	
	// Pair start and complete events in the trace
	private void buildActivityMap() {
		
	}
	
	@Override
	public boolean remove(Object o) {
		if (!(o instanceof XEvent)) return false;
		XEvent event = (XEvent)o;
		for (XEvent source : activityMap.keySet()) {
			XEvent target = activityMap.get(source);
			if (source == event && source == target) {
				activityMap.remove(source);
			}
			else if (event == source) {
				activityMap.put(target, target);
			}
			else if (event == target) {
				activityMap.put(source, source);
			}
		}
		return super.remove(o);
	}
	
	@Override
	public void clear() {
		super.clear();
		this.activityMap.clear();
	}
}
