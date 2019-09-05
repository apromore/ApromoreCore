package org.apromore.logman.log.durationaware;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XTraceImpl;

/**
 * DurationAwareTrace is created from the raw trace by aggregating start and complete events
 * This is kept in an activity map
 * Once constructed, it is not allowed to add new events or change any events in this trace
 * It is only allowed to remove (filter out) events from this trace
 * This is to ensure the activity map is always maintained accordingly to the original data
 * at the time of the trace construction
 * @author Bruce Nguyen
 *
 */
public class ActivityAwareTrace extends XTraceImpl {
	private Map<XEvent,XEvent> eventMapping;
	
	public ActivityAwareTrace(XTrace trace) {
		super(trace.getAttributes());
		super.addAll(trace);
		this.buildEventMapping();
	}
	
	public Map<XEvent,XEvent> getEventMapping() {
		return Collections.unmodifiableMap(this.eventMapping);
	}
	
	// Pair start and complete events in the trace
	private void buildEventMapping() {
	    eventMapping = new HashMap<>();
		// to be implemented later
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
