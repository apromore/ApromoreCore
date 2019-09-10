package org.apromore.logman.log.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

/**
 * Group of all changes on log returned from filtering actions.
 * Note that deleted activities and deleted events may overlap, meaning
 * the activities already contain deleted events. Users of this class
 * should be aware to use these two lists appropriately, i.e. not to duplicate
 * the processing code. Similarly, the updated activities may contain some 
 * deleted events already. For example, if you want to check at the event level,
 * then check the list of deleted events only. However, if you want to check 
 * at the activity level, you can check the deleted and updated activities.
 * 
 * In the deleted event list, note that the trace here has already had the events deleted.
 * 
 * @author Bruce Nguyen
 *
 */
public class LogFilteredEvent {
	private Set<XTrace> deletedTraces;  
	private Map<XTrace, Set<XEvent>> deletedEvents; 
	private Map<XTrace, Set<Activity>> deletedActs;
	private Map<XTrace, Set<Activity>> updatedActs;
	
	public LogFilteredEvent() {
		deletedTraces = new HashSet<>();
		deletedEvents = new HashMap<>();
		deletedActs = new HashMap<>();
		updatedActs = new HashMap<>();
	}
	
	public void addDeletedTrace(XTrace trace) {
		deletedTraces.add(trace);
	}
	
	public void addDeletedEvent(XTrace trace, XEvent event) {
		if (!deletedEvents.containsKey(trace)) deletedEvents.put(trace, new HashSet<>());
		deletedEvents.get(trace).add(event);
	}
	
	public void addDeletedAct(XTrace trace, Activity act) {
		if (!deletedActs.containsKey(trace)) deletedActs.put(trace, new HashSet<>());
		deletedActs.get(trace).add(act);
	}
	
	public void addUpdatedAct(XTrace trace, Activity act) {
		if (!updatedActs.containsKey(trace)) updatedActs.put(trace, new HashSet<>());
		updatedActs.get(trace).add(act);
	}
	
	public Set<XTrace> getDeletedTraces() {
		return Collections.unmodifiableSet(deletedTraces);
	}
	
	public Map<XTrace, Set<XEvent>> getDeletedEvents() {
		return Collections.unmodifiableMap(deletedEvents);
	}
	
	public Set<XEvent> getAllDeletedEvents() {
		Set<XEvent> all = new HashSet<>();
		deletedEvents.values().forEach(c -> all.addAll(c));
		deletedTraces.forEach(c -> all.addAll(c));
		return all;
	}
	
	public Map<XTrace, Set<Activity>> getDeletedActs() {
		return Collections.unmodifiableMap(deletedActs);
	}
	
	public Set<Activity> getAllDeletedActs() {
		Set<Activity> all = new HashSet<>();
		deletedActs.values().forEach(c -> all.addAll(c));
		for (XTrace trace: deletedTraces) {
			if (trace instanceof AXTrace) {
				all.addAll(((AXTrace)trace).getActivities());
			}
		}
		return all;
	}
	
	public Map<XTrace, Set<Activity>> getUpdatedActs() {
		return Collections.unmodifiableMap(updatedActs);
	}
	
	public Set<Activity> getAllUpdatedActs() {
		Set<Activity> all = new HashSet<>();
		updatedActs.values().forEach(c -> all.addAll(c));
		return all;
	}
}
