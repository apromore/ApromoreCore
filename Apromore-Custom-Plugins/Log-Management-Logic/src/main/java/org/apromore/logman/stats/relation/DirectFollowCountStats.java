package org.apromore.logman.stats.relation;

import java.util.List;

import org.apromore.logman.classifier.SimpleEventClassifier;
import org.apromore.logman.log.activityaware.AXTrace;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;

/**
 * This class is used to collect occurrence count statistics of the directly-follows relation
 * from logs based on a chosen attribute selected as an event classifier.
 * 
 * @author Bruce Nguyen
 *
 */
public class DirectFollowCountStats extends RelationCountStats {
	public DirectFollowCountStats(SimpleEventClassifier classifier) {
		super(classifier);
	}
	
    @Override
    // Need to loop events here because it needs the DFSSReader to interpret
    // the DFSS relation and the integer-based list representation
    public void visitTrace(XTrace trace) {
    	if (attribute == null) return;
    	
    	IntArrayList intTrace = new IntArrayList(trace.size());
    	List<? extends XEvent> events = (trace instanceof AXTrace) ? ((AXTrace)trace).getActivities() : trace;
        for(XEvent event : events) {
            int valueIndex = attribute.getValueIndex(classifier.getIdentityAttribute(event), event);
            intTrace.add(valueIndex);
        }
        relationLog.add(extractPairTrace(intTrace));
    }

	// A pair can be repeated in a trace
	private MutableObjectIntMap<IntIntPair> extractPairTrace(IntArrayList trace) {
		int[] ints = trace.toArray();
		for(int i = 0; i<ints.length-1; i++) { 
			ints[i] = ints[i+1];
		}
		IntList trace2 = new IntArrayList(ints);
		MutableList<IntIntPair> pairs = trace.zipInt(trace2); // a pair can repeat in this list
		pairs.remove(pairs.size()-1); //the last match is not a valid pair
		
		//Turn to an occurrence count map
		MutableObjectIntMap<IntIntPair> mapTrace = ObjectIntMaps.mutable.empty(); 
		for (IntIntPair pair : pairs) {
			mapTrace.addToValue(pair, 1);
		}
		return mapTrace;
	}
	
	
}
