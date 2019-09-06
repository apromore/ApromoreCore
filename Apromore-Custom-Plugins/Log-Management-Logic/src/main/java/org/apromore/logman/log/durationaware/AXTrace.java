package org.apromore.logman.log.durationaware;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.apromore.logman.log.Constants;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

/**
 * This class object is created from a raw trace by aggregating start and complete events
 * It is the same as the original trace, but events are paired to form activities (event mapping)
 * Once constructed, it is not allowed to add new events or change any events in this trace
 * It is only allowed to remove (filter out) events from this trace
 * There is a set of rules used to maintain the event mapping after event removal 
 * This ensures the event mapping is always maintained based on the original raw trace
 * @author Bruce Nguyen
 *
 */
public class AXTrace extends XTraceImpl {
	private Map<XEvent,XEvent> eventMapping;
	
	public AXTrace(XTrace trace) {
		super(trace.getAttributes());
		super.addAll(trace);
		this.buildEventMapping();
	}
	
	public Map<XEvent, XEvent> getEventMapping() {
		return Collections.unmodifiableMap(this.eventMapping);
	}
	
	public Set<Activity> getActivities() {
	    Set<Activity> acts = new HashSet<>();
	    for (Entry<XEvent, XEvent> entry : eventMapping.entrySet()) {
	        acts.add(new Activity(entry.getKey(), entry.getValue()));
	    }
	    return acts;
	}
	
	// Pair start and complete events in the trace
	private void buildEventMapping() {
	    eventMapping = new HashMap<>();
	    Queue<XEvent> startEvents = new LinkedList<>();
		for (XEvent event : this) {
		    if (LogUtils.getLifecycleTransition(event).equalsIgnoreCase(Constants.LIFECYCLE_START)) {
		        startEvents.add(event);
		    }
		    else if (LogUtils.getLifecycleTransition(event).equalsIgnoreCase(Constants.LIFECYCLE_COMPLETE)) {
		        XEvent matchedStart = startEvents.poll();
		        if (matchedStart != null) {
		            eventMapping.put(matchedStart, event);
		        }
		        else {
		            eventMapping.put(event, event);
		        }
		    }
		}
		while (!startEvents.isEmpty()) {
		    XEvent event = startEvents.poll();
		    eventMapping.put(event, event);
		}
	}
	
	public Date getStartTime() {
	    return (this.isEmpty() ? null : XTimeExtension.instance().extractTimestamp(this.get(0)));
	}
	
	public Date getEndTime() {
        return (this.isEmpty() ? null : XTimeExtension.instance().extractTimestamp(this.get(this.size()-1)));
    }

	public long getDuration() {
	    return (this.getStartTime().toInstant().toEpochMilli() - this.getStartTime().toInstant().toEpochMilli());
	}
	
	   
    private void updateEventMapping(Collection<?> removed) {
        for (XEvent source : eventMapping.keySet()) {
            XEvent target = eventMapping.get(source);
            boolean sourceRemoved = removed.contains(source);
            boolean targetRemoved = removed.contains(target);
            if (sourceRemoved && targetRemoved) {
                eventMapping.remove(source);
            }
            else if (sourceRemoved) {
                eventMapping.put(target, target);
            }
            else if (targetRemoved) {
                eventMapping.put(source, source);
            }
        }
    }   
	
	@Override 
	public boolean add(XEvent e) {
	    return false;
	}
	
	@Override
	public void add(int index, XEvent element) {
	    // do nothing
	}
	
	@Override
	public boolean addAll(Collection<? extends XEvent> c) {
	    return false;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends XEvent> c) {
	    return false;
	}
	
	@Override
	public void replaceAll(UnaryOperator<XEvent> operator) {
	    // do nothing
	}
	
	@Override
	public XEvent set(int index, XEvent element) {
	    //do nothing
	    return null;
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
	    return false;
	}
	
	public Spliterator<XEvent> spliterator() {
	    return null;
	}
	
	@Override
    public boolean remove(Object o) {
        boolean success = super.remove(o);
        if (success) {
            this.updateEventMapping(Collections.singleton(o));
        }
        return success;
    }
	
	@Override
	public boolean removeAll(Collection<?> c) {
	    boolean success = super.removeAll(c);
	    if (success) updateEventMapping(c);
	    return success;
	}
	
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
	    Collection<XEvent> removed = new HashSet<>();
	    for (int i=fromIndex;i<=toIndex;i++) {
	        removed.add(this.get(i));
	    }
	    super.removeRange(fromIndex, toIndex);
	    this.updateEventMapping(removed);
	}
	
	@Override
	public boolean removeIf(Predicate<? super XEvent> filter) {
	    Collection<XEvent> removed = Arrays.asList((XEvent[])this.stream().filter(filter).toArray());
	    boolean success = super.removeIf(filter);
	    if (success) this.updateEventMapping(removed);
	    return success;
	}
	
	@Override
	public void clear() {
		super.clear();
		eventMapping.clear();
	}
}
