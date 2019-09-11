package org.apromore.logman.log;

import org.apromore.logman.LogManager;
import org.apromore.logman.log.activityaware.Activity;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * This interface represents any task to do at different structural 
 * elements of a log. 

 * @author Bruce Nguyen
 */
public interface LogVisitor {
	// use to prepare data structure before visiting every elements of the log
	public void startVisit(LogManager logManager);
	
	// visit at the top level (can be used to prepare some data structures)
    public void visitLog(XLog log);
    
    // visit each trace 
    public void visitTrace(XTrace trace);
    
    // visit each activity
    public void visitActivity(Activity act);
    
    // visit each event
    public void visitEvent(XEvent event);
    
    // use to clean temporary data structures after the visit
    public void finishVisit();
}
