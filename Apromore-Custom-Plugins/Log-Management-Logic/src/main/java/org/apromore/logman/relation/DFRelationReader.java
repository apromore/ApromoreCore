package org.apromore.logman.relation;

import java.util.List;

import org.apromore.logman.log.activityaware.AXTrace;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

/**
 * Interpret the directly-follows relation from a trace
 * The result is a trace of activities
 * @author Bruce Nguyen
 *
 */
public interface DFRelationReader {
	List<? extends XEvent> read(AXTrace trace);
	List<? extends XEvent> read(XTrace trace);
}
