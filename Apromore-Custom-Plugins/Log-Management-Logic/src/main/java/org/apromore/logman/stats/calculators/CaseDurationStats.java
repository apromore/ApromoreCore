package org.apromore.logman.stats.calculators;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apromore.logman.log.activityaware.AXTrace;

public class CaseDurationStats extends StatsCollector {
  //number of events => number of cases containing the number of events
    private Map<Long,Integer> caseDurationToCaseMap = new HashMap<>(); 
    
    public Map<Long,Integer> getCaseDurationMap() {
        return Collections.unmodifiableMap(caseDurationToCaseMap);
    }
    
    @Override
    public void visitTrace(AXTrace trace) {
        long dur = trace.getDuration();
        caseDurationToCaseMap.put(dur, !caseDurationToCaseMap.containsKey(dur) ? 1 : caseDurationToCaseMap.get(dur) + 1);
    }    
}
