package org.apromore.logman.relation;

import java.util.Set;

import org.apromore.logman.log.durationaware.ActivityAwareTrace;
import org.deckfour.xes.model.XEvent;
import org.eclipse.collections.impl.tuple.Tuples;

public class EFSSReader implements EFRelationReader {

    @Override
    public ActivityMap read(ActivityAwareTrace trace) {
        Set<XEvent> startEvents = trace.getEventMapping().keySet();
        ActivityMap actMap = new ActivityMap();
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

}
