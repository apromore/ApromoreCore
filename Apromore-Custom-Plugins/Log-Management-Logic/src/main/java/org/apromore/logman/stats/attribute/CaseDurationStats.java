package org.apromore.logman.stats.attribute;

import org.apromore.logman.LogManager;
import org.apromore.logman.event.LogFilteredEvent;
import org.apromore.logman.stats.StatsCollector;
import org.apromore.logman.utils.LogUtils;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.map.primitive.LongIntMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.map.mutable.primitive.IntLongHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.LongIntHashMap;

public class CaseDurationStats extends StatsCollector {
	//case index => case duration (milliseconds)
    private IntLongHashMap caseDurationMap = new IntLongHashMap(); 
    private int traceIndex;
    private XLog log;
    
    public LongIntMap getCaseDurationMap() {
    	LongIntHashMap durationToCaseCountMap = new LongIntHashMap();
    	for (int caseIndex : caseDurationMap.keySet().toArray()) {
    		durationToCaseCountMap.addToValue(caseDurationMap.get(caseIndex), 1);
    	}
    	return durationToCaseCountMap.toImmutable();
    }
    
    ///////////////////////// Collect statistics the first time //////////////////////////////
    
    @Override 
    public void startVisit(LogManager logManager) {
    	caseDurationMap.clear();
    	traceIndex = 0;
    }
    
    @Override 
    public void visitLog(XLog log) {
    	this.log = log;
    }
    
    @Override
    public void visitTrace(XTrace trace) {
        caseDurationMap.put(traceIndex, LogUtils.getDuration(trace));
    }    
    
    ///////////////////////// Update statistics //////////////////////////////
    
    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        for (XTrace trace: event.getDeletedTraces()) {
        	caseDurationMap.put(log.indexOf(trace), 0);
        }
        
        for (Pair<XTrace,XTrace> pair: event.getUpdatedTraces()) {
        	caseDurationMap.put(log.indexOf(pair.getOne()), LogUtils.getDuration(pair.getTwo()));
        }
    }    
}
