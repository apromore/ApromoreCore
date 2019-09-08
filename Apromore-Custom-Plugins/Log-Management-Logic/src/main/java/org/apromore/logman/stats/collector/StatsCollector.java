package org.apromore.logman.stats.collector;

import org.apromore.logman.log.LogVisitor;
import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilterListener;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.deckfour.xes.model.XEvent;

public abstract class StatsCollector implements LogVisitor, LogFilterListener {
    public void initialize() {
        
    }
    
    public void reset() {
        
    }
    
    @Override
    public void visitLog(AXLog log) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitTrace(AXTrace trace) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitActivity(Activity act) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitEvent(XEvent event) {
    }

    @Override
    public void onLogFiltered(LogFilteredEvent event) {
        // TODO Auto-generated method stub
        
    }
}
