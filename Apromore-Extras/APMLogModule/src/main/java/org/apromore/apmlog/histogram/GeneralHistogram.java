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

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apromore.apmlog.ATrace;
import org.apromore.apmlog.logobjects.ActivityInstance;
import org.apromore.apmlog.util.Util;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneralHistogram {

    public static List<Triple> getCasesOverTime(List<ATrace> traces, long xMin, long xMax, int xSize) {
        if (xMax < 1) return new ArrayList<>(); // return empty data

        long unit = (xMax - xMin) / xSize;

        UnifiedMap<Long, IntArrayList> timeFreqMap = getTimeFreqMap(xMin, xMax, unit);

        for (ATrace trace : traces) {
            long st = trace.getStartTime();
            long et = trace.getEndTime();
            for (long ts : timeFreqMap.keySet()) {
                long pts = ts - unit;
                if (TimeHistogram.withinTime(pts, ts, st, et)) {
                    timeFreqMap.get(ts).add(trace.getImmutableIndex());
                }
            }
        }

        return getOverTimeData(timeFreqMap, unit);
    }

    public static List<Triple> getActivityOverTime(List<ActivityInstance> activityInstances, long xMin, long xMax, int xSize) {
        if (xMax < 1) return new ArrayList<>(); // return empty data

        long unit = (xMax - xMin) / xSize;

        UnifiedMap<Long, IntArrayList> timeFreqMap = getTimeFreqMap(xMin, xMax, unit);

        int globalIndex = 0;
        for (ActivityInstance activityInstance : activityInstances) {
            long st = activityInstance.getStartTime();
            long et = activityInstance.getEndTime();
            for (long ts : timeFreqMap.keySet()) {
                long pts = ts - unit;
                if (TimeHistogram.withinTime(pts, ts, st, et)) {
                    timeFreqMap.get(ts).add(globalIndex);
                }
            }
            globalIndex += 1;
        }

        return getOverTimeData(timeFreqMap, unit);
    }

    public static List<Triple> getCaseUtilization(List<ATrace> traces, double xMax, double xSize) {
        double utilMax = xMax;
        double utilMin = 0;
        double utilUnit = (xMax - utilMin) / xSize;

        double utilCurrent = utilMin;

        UnifiedMap<Double, Integer> utilCountMap = new UnifiedMap<>();
        utilCountMap.put(utilCurrent, 0);

        while (utilCurrent < utilMax) {
            utilCurrent += utilUnit;
            utilCountMap.put(utilCurrent <= utilMax ? utilCurrent : utilMax, 0);
        }
        List<Double> utilKeys = new ArrayList<>(utilCountMap.keySet());
        Collections.sort(utilKeys);


        for (ATrace trace : traces) {
            double caseUtil = xMax == 100 ? trace.getCaseUtilization() * 100 : trace.getCaseUtilization();
            for (double util : utilKeys) {
                double prior = util - utilUnit;
                if (caseUtil > prior && caseUtil <= util) {
                    int uCount = utilCountMap.get(util) + 1;
                    utilCountMap.put(util, uCount);
                    break;
                }
            }
        }

        List<Triple> data = new ArrayList<>();

        for (double x : utilKeys) {
            int y = utilCountMap.get(x);
            String name = "";
            if (x > 0) {
                String from = (Util.df3.format((x - utilUnit) *
                        (xMax == 100 ? 1 : 100))) + "%";
                String to = (Util.df3.format(x *
                        (xMax == 100 ? 1 : 100))) + "%";
                name = "From " + from + " to " + to;
            } else {
                name = "instant";
            }

            data.add(new ImmutableTriple(name, y, x));
        }

        return data;
    }

    // =========================================================================================
    // Private operations
    // =========================================================================================

    private static UnifiedMap<Long, IntArrayList> getTimeFreqMap(long min, long max, long unit) {
        UnifiedMap<Long, IntArrayList> timeFreqMap = new UnifiedMap<>();

        long current = min - unit;
        while (current < max) {
            current += unit;
            if (current > max) current = max;
            timeFreqMap.put(current, new IntArrayList());
        }

        return timeFreqMap;
    }

    private static List<Triple> getOverTimeData(UnifiedMap<Long, IntArrayList> timeFreqMap, long unit) {
        List<Long> keys = new ArrayList<>(timeFreqMap.keySet());
        Collections.sort(keys);

        List<Triple> data = new ArrayList<>();

        for (long x : keys) {
            String name = Util.timestampRangeStringOf((x - unit) + 1, x);
            int y = timeFreqMap.get(x).size();

            Triple triple = new ImmutableTriple(name, y, x);
            data.add(triple);
        }

        return data;
    }
}
