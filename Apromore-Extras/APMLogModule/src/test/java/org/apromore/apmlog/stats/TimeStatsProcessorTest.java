/**
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2021 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */
package org.apromore.apmlog.stats;

import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.logobjects.ImmutableLog;
import org.apromore.apmlog.logobjects.ImmutableTrace;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TimeStatsProcessorTest {

    private ActivityInstance getEmptyActivityInstance() {
        return new ActivityInstance(0, new ArrayList<>(), 0,
                "t1", 0, 0, 0, new UnifiedMap<>(), null);
    }

    private ImmutableTrace getEmptyTrace() {
        return new ImmutableTrace(0, "t1", new ArrayList<>(), new ArrayList<>(),
                new UnifiedMap<>(), null);
    }

    private ImmutableLog getEmptyImmutableLog() {
        return new ImmutableLog();
    }

    private PLog getEmptyPLog() {
        return new PLog(new ImmutableLog());
    }

    @Test
    public void getStartTime() {
        List<ActivityInstance> activityInstances = List.of(getEmptyActivityInstance());
        assertEquals(0, TimeStatsProcessor.getStartTime(activityInstances));
    }

    @Test
    public void getEndTime() {
        List<ActivityInstance> activityInstances = List.of(getEmptyActivityInstance());
        assertEquals(0, TimeStatsProcessor.getEndTime(activityInstances));
    }

    @Test
    public void getPLogDuration() {
        PLog emptyPLog = getEmptyPLog();
        assertEquals(0, TimeStatsProcessor.getPLogDuration(emptyPLog));
    }

    @Test
    public void getAPMLogDuration() {
        ImmutableLog emptyLog = getEmptyImmutableLog();
        assertEquals(0, TimeStatsProcessor.getAPMLogDuration(emptyLog));
    }

    @Test
    public void getCaseDurations() {
        assertEquals(0, TimeStatsProcessor.getCaseDurations(new ArrayList<>()).sum(), 0);
    }

    @Test
    public void getCaseDuration() {
        assertEquals(0, TimeStatsProcessor.getCaseDuration(getEmptyTrace()), 0);
    }

    @Test
    public void getCaseUtilization() {
        assertEquals(0, TimeStatsProcessor.getCaseUtilization(new ArrayList<>()), 0);
    }

    @Test
    public void getProcessingTimes() {
        assertEquals(0, TimeStatsProcessor.getProcessingTimes(new ArrayList<>()).sum(), 0);
    }

    @Test
    public void getWaitingTimes() {
        assertEquals(0, TimeStatsProcessor.getWaitingTimes(new ArrayList<>()).sum(), 0);
    }

    @Test
    public void getActivityInstanceDuration() {
        assertEquals(0, TimeStatsProcessor.getActivityInstanceDuration(getEmptyActivityInstance()), 0);
    }
}