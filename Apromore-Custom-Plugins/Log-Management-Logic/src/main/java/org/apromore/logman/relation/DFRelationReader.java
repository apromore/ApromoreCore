package org.apromore.logman.relation;

import org.apromore.logman.log.durationaware.AXTrace;
import org.apromore.logman.log.durationaware.ActivityTrace;

/**
 * Interpret the directly-follows relation from a trace
 * The result is a trace of activities
 * @author Bruce Nguyen
 *
 */
public interface DFRelationReader {
    ActivityTrace read(AXTrace trace);
}
