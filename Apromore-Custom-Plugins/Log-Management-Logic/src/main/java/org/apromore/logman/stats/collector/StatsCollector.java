package org.apromore.logman.stats.collector;

import org.apromore.logman.LogManager;
import org.apromore.logman.log.LogVisitor;
import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.apromore.logman.log.event.LogFilterListener;
import org.apromore.logman.log.event.LogFilteredEvent;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * Abstract class represents all statistics calculation object.
 * It is advised to initialize and register all statstics calculation objects
 * with the LogManager all at once at the start of a program
 * In addition, a StatsCollector should take care not to duplicate its
 * log data retrieval when accessing the log elements at different levels to 
 * ensure the accuracy of its calculation
 * 
 * @author Bruce Nguyen
 *
 */
public abstract class StatsCollector implements LogVisitor, LogFilterListener {
    @Override
    public void startVisit(LogManager logManager) {
    }
    
    @Override
    public void finishVisit() {
    	
    }
    
    @Override
    public void visitLog(XLog log) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void visitTrace(XTrace trace) {
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
    public void onLogFiltered(LogFilteredEvent filterEvent) {
        // TODO Auto-generated method stub
        
    }
}
