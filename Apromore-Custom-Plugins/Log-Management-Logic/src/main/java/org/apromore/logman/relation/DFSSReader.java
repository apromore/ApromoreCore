package org.apromore.logman.relation;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.activityaware.ActivityTrace;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

/**
 * Directly follow trace where activities are arranged based on the start event
 * The relation between consecutive activities is based on start to start events
 * @author Bruce Nguyen
 *
 */
public class DFSSReader implements DFRelationReader {
	private static DFSSReader singleton;
	
	public static DFSSReader instance() {
		if (singleton == null) singleton = new DFSSReader();
		return singleton;
	}
	
    @Override
    public List<? extends XEvent> read(AXTrace trace) {
    	List<Activity> actTrace = new ArrayList<Activity>();
        Map<XEvent, XEvent> eventMapping = trace.getEventMapping();
        Set<XEvent> startEvents = eventMapping.keySet();
        for (XEvent event : trace) {
            if (startEvents.contains(event)) {
                actTrace.add(new Activity(event, eventMapping.get(event)));
            }
        }
        return actTrace;
    }
    
    @Override
    public List<? extends XEvent> read(XTrace trace) {
    	if (trace instanceof AXTrace) {
    		return this.read((AXTrace)trace);
    	}
    	else {
	    	return trace;
    	}
    }    
}
