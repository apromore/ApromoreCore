package org.apromore.logman.stats.relation;

import org.apromore.logman.AttributeStore;
import org.apromore.logman.LogManager;
import org.apromore.logman.classifier.SimpleEventClassifier;
import org.apromore.logman.stats.StatsCollector;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.impl.factory.Lists;

/**
 * This class is used to collect occurrence count statistics of event relations
 * from logs based on a chosen attribute selected as an event classifier.
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class RelationCountStats extends StatsCollector {
	protected XLog log;
	protected AttributeStore attributeStore;
	protected SimpleEventClassifier classifier;
	// List of traces in the log, each trace contains a set of mapping: pair of index-based events => occurrence count 
	protected MutableList<MutableObjectIntMap<IntIntPair>> relationLog = Lists.mutable.empty();

	public RelationCountStats(SimpleEventClassifier classifier) {
		this.classifier = classifier;
	}
	
	public ImmutableList<MutableObjectIntMap<IntIntPair>> getDirectlyFollowTraces() {
		return relationLog.toImmutable();
	}
	
	// Get the list of count for the pair, each element corresponds to its count in the corresponding trace 
	public ImmutableList<Integer> get(IntIntPair pair) {
		return relationLog.select(a -> a.keySet().contains(pair)).collect(b -> b.get(pair)).toImmutable();
	}
	
    @Override
    public void startVisit(LogManager logManager) {
    	attributeStore = logManager.getAttributeStore();
    	relationLog.clear();
    }
    
    @Override
    public void visitLog(XLog log) {
    	this.log = log;
    }
    
    @Override
    public void visitTrace(XTrace trace) {
    	
    }
	
	
}
