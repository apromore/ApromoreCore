package org.apromore.logman.relation;

import java.util.Set;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.impl.multimap.set.UnifiedSetMultimap;
import org.eclipse.collections.impl.tuple.Tuples;

public class EFSSReader implements EFRelationReader {
	private static EFSSReader singleton;
	
	public static EFSSReader instance() {
		if (singleton == null) singleton = new EFSSReader();
		return singleton;
	}
	
    @Override
    public Multimap<? extends XEvent, ? extends XEvent> read(AXTrace trace) {
        Set<XEvent> startEvents = trace.getEventMapping().keySet();
        UnifiedSetMultimap<Activity, Activity> actMap = new UnifiedSetMultimap<>();
        for (int i=0;i<trace.size();i++) {
            if (startEvents.contains(trace.get(i))) {
                Activity sourceAct = new Activity(trace.get(i), trace.getEventMapping().get(trace.get(i)));
                for (int j=i+1;j<trace.size();j++) {
                    if (startEvents.contains(trace.get(j))) {
                        Activity targetAct = new Activity(trace.get(j), trace.getEventMapping().get(trace.get(j)));
                        actMap.add(Tuples.pair(sourceAct, targetAct));
                    }
                }   
            }
        }
        return actMap;
    }
    
    @Override
    public Multimap<? extends XEvent, ? extends XEvent> read(XTrace trace) {
    	if (trace instanceof AXTrace) {
    		return this.read((AXTrace)trace);
    	}
    	else {
    		UnifiedSetMultimap<XEvent, XEvent> eventMap = new UnifiedSetMultimap<>();
            for (int i=0;i<trace.size();i++) {
                for (int j=i+1;j<trace.size();j++) {
                	eventMap.add(Tuples.pair(trace.get(i), trace.get(j)));
                }   
            }
            return eventMap;
    	}
    }

}
