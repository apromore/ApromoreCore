package org.apromore.logman.relation;

import org.apromore.logman.log.activityaware.AXTrace;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.multimap.Multimap;

/**
 * Interpret the eventually-follows relation from a trace
 * The result is a multi-map of activity (i.e. a graph) 
 * @author Bruce Nguyen
 *
 */
public interface EFRelationReader {
    Multimap<? extends XEvent, ? extends XEvent> read(AXTrace trace);
    Multimap<? extends XEvent, ? extends XEvent> read(XTrace trace);
}
