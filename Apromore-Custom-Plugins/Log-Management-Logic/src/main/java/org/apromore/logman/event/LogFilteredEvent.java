package org.apromore.logman.event;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.tuple.Pair;

/**
 * Group of all changes on log returned from filtering actions.
 * Note that events and activities in deleted traces are not include in the other results
 * For example, events in deleted traces are not include in the deletedEvents result.
 * Apart from that, other results are related.
 * So, programs using this class should update separately for deleted traces.
 * 
 * @author Bruce Nguyen
 *
 */
public class LogFilteredEvent {
	private Set<XEvent> deletedEvents;
	private Set<Activity> deletedActs; //events of activities are included in deletedEvents
	// pair = {old act, new act}, deleted events in these acts are included in deletedEvents
	private Set<Pair<Activity, Activity>> updatedActs; 		
	
	// pair = {old trace, new trace}, these are traces affected by deletedEvents, deletedActs and updatedActs
	private Set<Pair<XTrace, XTrace>> updatedTraces; 
	
	//events and activities in these traces are NOT included in deletedEvents and deletedActs
	private Set<XTrace> deletedTraces; 
	
	
	
	public LogFilteredEvent() {
		deletedTraces = new HashSet<>();
		updatedTraces = new HashSet<>();
		deletedEvents = new HashSet<>();
		deletedActs = new HashSet<>();
		updatedActs = new HashSet<>();
	}
	
	public void addDeletedTrace(XTrace trace) {
		deletedTraces.add(trace);
	}
	
	public void addUpdatedTrace(Pair<XTrace, XTrace> tracePair) {
		updatedTraces.add(tracePair);
	}
	
	public void addDeletedEvent(XEvent event) {
		deletedEvents.add(event);
	}
	
	public void addDeletedAct(Activity act) {
		deletedActs.add(act);
	}
	
	public void addUpdatedAct(Pair<Activity,Activity> act) {
		updatedActs.add(act);
	}
	
	public Set<XTrace> getDeletedTraces() {
		return Collections.unmodifiableSet(deletedTraces);
	}
	
	public Set<Pair<XTrace,XTrace>> getUpdatedTraces() {
		return Collections.unmodifiableSet(updatedTraces);
	}	
	
	public Set<XEvent> getDeletedEvents() {
		return Collections.unmodifiableSet(deletedEvents);
	}
	
	public Set<XEvent> getAllDeletedEvents() {
		Set<XEvent> result = new HashSet<XEvent>();
		result.addAll(deletedEvents);
		for (XTrace trace : deletedTraces) {
			result.addAll(trace);
		}
		return Collections.unmodifiableSet(result);
	}	
		
	public Set<Activity> getDeletedActs() {
		return Collections.unmodifiableSet(deletedActs);
	}
	
	public Set<Activity> getAllDeletedActs() {
		Set<Activity> result = new HashSet<Activity>();
		result.addAll(deletedActs);
		for (XTrace trace : deletedTraces) {
			if (trace instanceof AXTrace) {
				result.addAll(((AXTrace)trace).getActivities());
			}
		}
		return Collections.unmodifiableSet(result);
	}
	
	public Set<Pair<Activity,Activity>> getUpdatedActs() {
		return Collections.unmodifiableSet(updatedActs);
	}
}
