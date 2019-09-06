package org.apromore.logman.relation;

import org.apromore.logman.log.activityaware.AXTrace;

public interface RelationChecker {
    boolean contains(AXTrace trace);
}
