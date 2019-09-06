package org.apromore.logman.relation;

import org.apromore.logman.log.activityaware.AXTrace;
import org.apromore.logman.log.activityaware.ActivityTrace;

/**
 * Interpret the directly-follows relation from a trace
 * The result is a trace of activities
 * @author Bruce Nguyen
 *
 */
public interface DFRelationReader {
    ActivityTrace read(AXTrace trace);
}
