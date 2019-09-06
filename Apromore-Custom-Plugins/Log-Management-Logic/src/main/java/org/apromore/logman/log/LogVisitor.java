package org.apromore.logman.log;

import org.deckfour.xes.model.XAttributable;

/**
 * This interface represents any task to do on a log at any level
 * It can be calculating statistics, creating a new type of log,
 * summarizing information of logs, etc.
 * @author Bruce Nguyen
 *
 */
public interface LogVisitor {
    public void beforeVisit();
    public void visit(XAttributable attribute);
}
