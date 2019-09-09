package org.apromore.logman.stats.collector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;

public class EventsPerCaseStats extends StatsCollector {
    // number of events => number of cases containing the number of events
    private Map<Integer,Integer> eventToCaseMap = new HashMap<>(); 
    
    public Map<Integer,Integer> getEventCountMap() {
        return Collections.unmodifiableMap(eventToCaseMap);
    }
    
    ///////////////////////// Collect statistics the first time //////////////////////////////
    
    @Override
    public void visitTrace(AXTrace trace) {
        Integer count = trace.size();
        eventToCaseMap.put(count, !eventToCaseMap.containsKey(count) ? 1 : eventToCaseMap.get(count) + 1);
    }
    
    

    ///////////////////////// Update statistics //////////////////////////////
    
    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        // TODO Auto-generated method stub
        
    }
}
