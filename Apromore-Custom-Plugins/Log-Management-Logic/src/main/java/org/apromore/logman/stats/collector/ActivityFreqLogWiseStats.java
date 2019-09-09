package org.apromore.logman.stats.collector;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilteredEvent;

public class ActivityFreqLogWiseStats extends StatsCollector {
    @Override
    public void visitActivity(Activity act) {
        
    }
    
    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        // TODO Auto-generated method stub
        
    }
}
