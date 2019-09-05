package org.apromore.logman.relation;

import org.apromore.logman.log.durationaware.AXTrace;

public interface RelationChecker {
    boolean contains(AXTrace trace);
}
