package org.apromore.logman.relation;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.ActivityMap;

/**
 * Interpret the eventually-follows relation from a trace
 * The result is a multi-map of activity (i.e. a graph) 
 * @author Bruce Nguyen
 *
 */
public interface EFRelationReader {
    ActivityMap read(AXTrace trace);
}
