package org.apromore.logman.stats.calculators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apromore.logman.log.activityaware.AXTrace;

public class EventsPerCaseStats extends StatsCollector {
    //number of events => number of cases containing the number of events
    private Map<Integer,Integer> eventToCaseMap = new HashMap<>(); 

    public Map<Integer,Integer> getEventCountMap() {
        return Collections.unmodifiableMap(eventToCaseMap);
    }
    
    @Override
    public void visitTrace(AXTrace trace) {
        Integer count = trace.size();
        eventToCaseMap.put(count, !eventToCaseMap.containsKey(count) ? 1 : eventToCaseMap.get(count) + 1);
    }


}
