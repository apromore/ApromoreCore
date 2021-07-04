/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.apmlog.histogram;

import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.HashSet;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Set;
import java.util.stream.Collectors;

public class TimeHistogram {

    // ===================================================================================================
    // Public methods
    // ===================================================================================================

    public static UnifiedMap<Long, IntArrayList> getCaseOverTimeMap(List<ATrace> traces, int size) {

        long[] allST = traces.stream().mapToLong(ATrace::getStartTime).toArray();
        long[] allET = traces.stream().mapToLong(ATrace::getEndTime).toArray();
        LongArrayList lalST = new LongArrayList(allST);
        LongArrayList lalET = new LongArrayList(allET);

        long earliest = lalST.min();
        long latest = lalET.max();
        long unit = (latest - earliest) / size;

        UnifiedMap<Long, IntArrayList> timeFreqMap = new UnifiedMap<>();

        long current = earliest - unit;
        while (current < latest) {
            current += unit;
            if (current > latest) current = latest;
            timeFreqMap.put(current, new IntArrayList());
        }

        for (ATrace trace : traces) {
            long st = trace.getStartTime();
            long et = trace.getEndTime();
            for (long ts : timeFreqMap.keySet()) {
                long pts = ts - unit;
                if (withinTime(pts, ts, st, et)) {
                    timeFreqMap.get(ts).add(trace.getImmutableIndex());
                }
            }
        }

        return timeFreqMap;
    }

    public static UnifiedMap<Long, Set<ActivityInstance>> getActivityOverTimeMap(List<ActivityInstance>
                                                                                                 activities,
                                                                                 int size) {

        LongSummaryStatistics allST = activities.stream()
                .collect(Collectors.summarizingLong(ActivityInstance::getStartTime));
        LongSummaryStatistics allET = activities.stream()
                .collect(Collectors.summarizingLong(ActivityInstance::getEndTime));

        long earliest = allST.getMin();
        long latest = allET.getMax();
        long unit = (latest - earliest) / size;

        UnifiedMap<Long, Set<ActivityInstance>> timeOccurMap = new UnifiedMap<>();

        long current = earliest;
        timeOccurMap.put(current, new HashSet<>());

        while (current < latest) {
            current += unit;
            if (current >= latest) current = latest;

            final long t = current;
            final long pt = t - unit;

            Set<ActivityInstance> matched = activities.stream()
                    .filter(x -> withinTime(pt, t, x.getStartTime(), x.getEndTime()))
                    .collect(Collectors.toSet());

            timeOccurMap.put(current, matched);
        }

        return timeOccurMap;
    }


    public static boolean withinTime(long targetFrom, long targetTo, long ruleTimeFrom, long ruleTimeTo) {
        return (ruleTimeFrom <= targetFrom && ruleTimeTo <= targetTo && ruleTimeTo >= targetFrom) ||
                (ruleTimeFrom >= targetFrom && ruleTimeTo >= targetTo && ruleTimeFrom <= targetTo) ||
                (ruleTimeFrom <= targetFrom && ruleTimeTo >= targetTo) ||
                (ruleTimeFrom >= targetFrom && ruleTimeTo <= targetTo);
    }

    // ===================================================================================================
    // Private methods
    // ===================================================================================================
}
