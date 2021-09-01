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
package org.apromore.apmlog.stats;

import org.apromore.apmlog.APMLog;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.filter.PLog;
import org.apromore.apmlog.util.CalendarDuration;
import org.apromore.calendar.model.CalendarModel;
import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;

import java.util.List;

public class TimeStatsProcessor {

    private TimeStatsProcessor() {
        throw new IllegalStateException("Utility class");
    }

    public static long getStartTime(List<ActivityInstance> activityInstances) {
        long[] allST = activityInstances.stream()
                .mapToLong(ActivityInstance::getStartTime)
                .toArray();

        LongArrayList dalST = new LongArrayList(allST);
        return dalST.min();
    }

    public static long getEndTime(List<ActivityInstance> activityInstances) {
        long[] allET = activityInstances.stream()
                .mapToLong(ActivityInstance::getEndTime)
                .toArray();

        LongArrayList dalET = new LongArrayList(allET);
        return dalET.max();
    }

    public static long getPLogDuration(PLog log) {
        long st = log.getStartTime();
        long et = log.getEndTime();
        return CalendarDuration.getDuration(log.getCalendarModel(), st, et);
    }

    public static long getAPMLogDuration(APMLog log) {
        long et = log.getStartTime();
        long st = log.getEndTime();
        return CalendarDuration.getDuration(log.getCalendarModel(), st, et);
    }

    public static DoubleArrayList getCaseDurations(List<ATrace> traces) {
        double[] array = traces.stream().mapToDouble(ATrace::getDuration).toArray();
        return new DoubleArrayList(array);
    }

    public static double getCaseDuration(ATrace aTrace) {
        CalendarModel calendarModel = aTrace.getCalendarModel();

        try {
            long st = aTrace.getActivityInstances().get(0).getStartTime();
            long et = aTrace.getActivityInstances().get(aTrace.getActivityInstances().size() - 1).getEndTime();
            return CalendarDuration.getDuration(calendarModel, st, et);
        } catch (Exception e) {
            return 0;
        }
    }

    public static double getCaseUtilization(List<ActivityInstance> activityInstances,
                                            DoubleArrayList procTimes,
                                            DoubleArrayList waitTimes) {
        if (activityInstances.isEmpty()) return 0;

        double ttlPT = procTimes.sum();
        double ttlWT = waitTimes.sum();
        double dur = activityInstances.get(activityInstances.size() - 1).getEndTime() -
                activityInstances.get(0).getStartTime();

        return ttlPT > 0 && ttlWT > 0 ? ttlPT / (ttlPT + ttlWT) :
                (ttlPT > 0 && ttlPT < dur ? ttlPT / dur : 1.0);
    }

    public static double getCaseUtilization(List<ActivityInstance> activityInstances) {
        double ttlPT = getProcessingTimes(activityInstances).sum();
        double ttlWT = getWaitingTimes(activityInstances).sum();
        double dur = activityInstances.get(activityInstances.size() - 1).getEndTime() -
                activityInstances.get(0).getStartTime();

        return ttlPT > 0 && ttlWT > 0 ? ttlPT / (ttlPT + ttlWT) :
                (ttlPT > 0 && ttlPT < dur ? ttlPT / dur : 1.0);
    }

    public static DoubleArrayList getProcessingTimes(List<ActivityInstance> activityInstances) {
        double[] allProcTimeArray = activityInstances.stream().mapToDouble(ActivityInstance::getDuration).toArray();
        return new DoubleArrayList(allProcTimeArray);
    }

    public static DoubleArrayList getWaitingTimes(List<ActivityInstance> activityInstances) {
        double[] waitTimesArray = activityInstances.stream()
                .filter(x -> x != activityInstances.get(activityInstances.size() - 1))
                .mapToDouble(x -> getDurationBetween(x, activityInstances.get(activityInstances.indexOf(x) + 1)))
                .toArray();
        return new DoubleArrayList(waitTimesArray);
    }

    private static double getDurationBetween(ActivityInstance fromNode, ActivityInstance toNode) {
        long fromET = fromNode.getEndTime();
        long toST = toNode.getStartTime();
        CalendarModel calendarModel = fromNode.getCalendarModel();
        return CalendarDuration.getDuration(calendarModel, fromET, toST);
    }

    public static double getActivityInstanceDuration(ActivityInstance activityInstance) {
        long st = activityInstance.getStartTime();
        long et = activityInstance.getEndTime();
        CalendarModel calendarModel = activityInstance.getCalendarModel();
        return CalendarDuration.getDuration(calendarModel, st, et);
    }

}
