package org.apromore.logman.relation;

import java.util.Set;
import java.util.Map;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.activityaware.ActivityTrace;
import org.deckfour.xes.model.XEvent;

/**
 * Directly follow trace where activities are arranged based on the start event
 * The relation between consecutive activities is based on start to start events
 * @author Bruce Nguyen
 *
 */
public class DFSSReader implements DFRelationReader {
    @Override
    public ActivityTrace read(AXTrace trace) {
        ActivityTrace actTrace = new ActivityTrace();
        Map<XEvent, XEvent> eventMapping = trace.getEventMapping();
        Set<XEvent> startEvents = eventMapping.keySet();
        for (XEvent event : trace) {
            if (startEvents.contains(event)) {
                actTrace.add(new Activity(event, eventMapping.get(event)));
            }
        }
        return actTrace;
    }
}
