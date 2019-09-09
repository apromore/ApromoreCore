package org.apromore.logman.log;

import org.apromore.logman.LogManager;
import org.apromore.logman.log.activityaware.AXLog;
import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.Activity;
import org.deckfour.xes.model.XEvent;

/**
 * This interface represents any task to do on a log at any level
 * It can be calculating statistics, creating a new type of log,
 * summarizing information of logs, etc.
 * @author Bruce Nguyen
 *
 */
public interface LogVisitor {
	public void start(LogManager logManager);
    public void visitLog(AXLog log);
    public void visitTrace(AXTrace trace);
    public void visitActivity(Activity act);
    public void visitEvent(XEvent event);
    public void finish();
}
