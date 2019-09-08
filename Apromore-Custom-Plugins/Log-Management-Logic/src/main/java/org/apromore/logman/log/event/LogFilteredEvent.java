package org.apromore.logman.log.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class LogFilteredEvent {
	private Set<Integer> deletedTraces; // list of trace indexes 
	private Map<Integer, Set<Integer>> deletedEvents; // traceIndex => list of event index in the trace
	
	public LogFilteredEvent() {
		deletedTraces = new HashSet<Integer>();
		deletedEvents = new HashMap<>();
	}
	
	public void addDeletedTrace(int traceIndex) {
		deletedTraces.add(traceIndex);
	}
	
	public void addDeletedEvent(int traceIndex, int eventIndex) {
		if (!deletedEvents.containsKey(traceIndex)) {
			deletedEvents.put(traceIndex, new HashSet<>());
		}
		deletedEvents.get(traceIndex).add(eventIndex);
	}
	
	public Set<Integer> getDeletedTraces() {
		return Collections.unmodifiableSet(deletedTraces);
	}
	
	public Map<Integer, Set<Integer>> getDeletedEvents() {
		return Collections.unmodifiableMap(deletedEvents);
	}
}
