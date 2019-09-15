package org.apromore.logman.stats.relation;

import org.apromore.logman.classifier.SimpleEventClassifier;
import org.apromore.logman.relation.EFSSReader;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.multimap.Multimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

/**
 * This class is used to collect occurrence count stats of the eventually-follows relation
 * from logs based on a chosen attribute selected as an event classifier.
 * The statistics include 
 * 
 * @author Bruce Nguyen
 *
 */
public class EventuallyFollowCountStats extends RelationCountStats {
	public EventuallyFollowCountStats(SimpleEventClassifier classifier) {
		super(classifier);
	}
	
    @Override
    // Need to loop events here because it needs the DFSSReader to interpret
    // the DFSS relation and the integer-based list representation
    public void visitTrace(XTrace trace) {
    	MutableObjectIntMap<IntIntPair> mapIntTrace = ObjectIntMaps.mutable.empty();
        Multimap<? extends XEvent, ? extends XEvent> mapTrace = EFSSReader.instance().read(trace); 
        for (Pair<? extends XEvent, ?extends XEvent> pair: mapTrace.keyValuePairsView()) {
        	XEvent source = pair.getOne();
        	XEvent target = pair.getTwo();
            int sourceValue = attribute.getValueIndex(classifier.getIdentityAttribute(source), source);
            int targetValue = attribute.getValueIndex(classifier.getIdentityAttribute(target), target);
            mapIntTrace.addToValue(PrimitiveTuples.pair(sourceValue, targetValue), 1);
        }
        relationLog.add(mapIntTrace);
    }
	
	
}
