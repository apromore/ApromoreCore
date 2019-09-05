package org.apromore.logman.relation;

import org.apromore.logman.log.durationaware.ActivityAwareTrace;

public interface RelationChecker {
    boolean contains(ActivityAwareTrace trace);
}
