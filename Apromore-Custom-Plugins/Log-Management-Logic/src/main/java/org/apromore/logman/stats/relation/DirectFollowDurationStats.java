package org.apromore.logman.stats.relation;

import org.apromore.logman.classifier.SimpleEventClassifier;
import org.deckfour.xes.model.XTrace;

/**
 * This class is used to collect duration stats of the directly-follows relation
 * from logs based on a chosen attribute selected as an event classifier.
 * 
 * @author Bruce Nguyen
 *
 */
public class DirectFollowDurationStats extends RelationDurationStats {
	public DirectFollowDurationStats(SimpleEventClassifier classifier) {
		super(classifier);
	}
	
    @Override
    // Need to loop events here because it needs the DFSSReader to interpret
    // the DFSS relation and the integer-based list representation
    public void visitTrace(XTrace trace) {
//    	IntArrayList intTrace = new IntArrayList(trace.size());
//    	List<? extends XEvent> events = (trace instanceof AXTrace) ? ((AXTrace)trace).getActivities() : trace;
//    	XEvent previousEvent = null;
//        for(XEvent event : events) {
//            int valueIndex = attributeStore.getValueIndex(classifier.getIdentityAttribute(event), event);
//            intTrace.add(valueIndex);
//        }
//        relationLog.add(extractPairTrace(intTrace));
    }
	
	
}
