package org.apromore.logman.stats.attribute;

import org.apromore.logman.LogManager;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.apromore.logman.stats.StatsCollector;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;

public class EventsPerCaseStats extends StatsCollector {
	private XLog log;
    // trace index => number of events
    private IntIntHashMap caseEventCountMap = new IntIntHashMap();
    
    private int traceIndex;
    
    public IntIntMap getEventCountMap() {
    	IntIntHashMap eventCountCaseCountMap = new IntIntHashMap();
    	for (int caseIndex : caseEventCountMap.keySet().toArray()) {
    		eventCountCaseCountMap.addToValue(caseEventCountMap.get(caseIndex), 1);
    	}
    	return eventCountCaseCountMap.toImmutable();
    }
    
    ///////////////////////// Collect statistics the first time //////////////////////////////
    
    @Override
    public void startVisit(LogManager logManager) {
    	caseEventCountMap.clear();
    	traceIndex = 0;
    }
    
    @Override
    public void visitLog(XLog log) {
    	this.log = log;
    }
    
    @Override
    public void visitTrace(XTrace trace) {
    	caseEventCountMap.addToValue(traceIndex, trace.size());
        traceIndex++;
    }
    
    

    ///////////////////////// Update statistics //////////////////////////////
    
    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        for (XTrace trace: event.getDeletedTraces()) {
        	caseEventCountMap.put(log.indexOf(trace), 0);
        }
        
        for (Pair<XTrace,XTrace> pair: event.getUpdatedTraces()) {
        	caseEventCountMap.put(log.indexOf(pair.getOne()), pair.getTwo().size());
        }
    }
}
