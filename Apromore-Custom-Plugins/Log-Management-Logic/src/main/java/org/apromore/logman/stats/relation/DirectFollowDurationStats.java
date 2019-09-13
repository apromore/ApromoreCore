package org.apromore.logman.stats.relation;

import java.util.List;

import org.apromore.logman.classifier.SimpleEventClassifier;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

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
    	MutableObjectLongMap<IntIntPair> tracePairs = ObjectLongMaps.mutable.empty();
    	List<? extends XEvent> events = (trace instanceof AXTrace) ? ((AXTrace)trace).getActivities() : trace;
    	XEvent previousEvent = null;
        for(XEvent event : events) {
        	if (previousEvent == null) {
        		previousEvent = event;
        	}
        	else {
        		int previousValueIndex = attributeStore.getValueIndex(classifier.getIdentityAttribute(previousEvent), previousEvent);
        		long previousTimestamp = LogUtils.getTimestamp(previousEvent);
        		
        		int currentValueIndex = attributeStore.getValueIndex(classifier.getIdentityAttribute(event), event);
        		long currentTimestamp = LogUtils.getTimestamp(event);
        		long duration = currentTimestamp - previousTimestamp;
        		
        		tracePairs.addToValue(PrimitiveTuples.pair(previousValueIndex, currentValueIndex), duration);
        	}
            
            
        }
        relationLog.add(tracePairs);
    }
	
	
}
