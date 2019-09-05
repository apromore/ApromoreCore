package org.apromore.logman.relation;

import java.util.Set;
import java.util.Map.Entry;

import org.apromore.logman.log.durationaware.ActivityAwareTrace;
import org.deckfour.xes.model.XEvent;

/**
 * Directly follow trace where activities are arranged based on the start event
 * The relation between consecutive activities is based on start to start events
 * @author Bruce Nguyen
 *
 */
public class DFSSReader implements DFRelationReader {
    @Override
    public ActivityTrace read(ActivityAwareTrace trace) {
        ActivityTrace actTrace = new ActivityTrace();
        Set<XEvent> startEvents = trace.getEventMapping().keySet();
        for (XEvent event : trace) {
            if (startEvents.contains(event)) {
                actTrace.add(new Activity(event, trace.getEventMapping().get(event)));
            }
        }
        return actTrace;
    }
}
