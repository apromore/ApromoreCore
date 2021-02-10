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
package org.apromore.apmlog;

import org.eclipse.collections.impl.list.mutable.primitive.DoubleArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class provides the data that can be used in various chart-based data visualisation
 * such as Highcharts, Chart.js etc.
 *
 * @author Chii Chang (created: 14/02/2020)
 * Modified: Chii Chang (26/01/2021)
 */
public class DefaultChartDataCollection {

    private UnifiedMap<Long, Integer> caseOTSeriesData;
    private UnifiedMap<Long, Integer> eventOTSeriesData;
    private UnifiedMap<Double, Integer> caseDurationSeriesData;
    private UnifiedMap<Double, Integer> totalProcessingTimeSeriesData;
    private UnifiedMap<Double, Integer> averageProcessingTimeSeriesData;
    private UnifiedMap<Double, Integer> maxProcessingTimeSeriesData;
    private UnifiedMap<Double, Integer> totalWaitingTimeSeriesData;
    private UnifiedMap<Double, Integer> averageWaitingTimeSeriesData;
    private UnifiedMap<Double, Integer> maxWaitingTimeSeriesData;
    private UnifiedMap<Double, Integer> caseUtilizationSeriesData;
    private UnifiedMap<Integer, Integer> caseVariFreqMap;

    private long earliestTime = 0; //for case overtime
    private long latestTime = 0; //for case overtime
    private double minDuration = 0;
    private double maxDuration = 0;
    private final int MAX_UNIT = 50;

    public DefaultChartDataCollection(APMLog apmLog) {

        List<ATrace> traceList = apmLog.getTraceList();
        DoubleArrayList caseDurations = new DoubleArrayList(traceList.size());
        DoubleArrayList ttlProcessTimes = new DoubleArrayList(traceList.size());
        DoubleArrayList avgProcessTimes = new DoubleArrayList(traceList.size());
        DoubleArrayList maxProcessTimes = new DoubleArrayList(traceList.size());
        DoubleArrayList ttlWaitTimes = new DoubleArrayList(traceList.size());
        DoubleArrayList avgWaitTimes = new DoubleArrayList(traceList.size());
        DoubleArrayList maxWaitTimes = new DoubleArrayList(traceList.size());
        DoubleArrayList caseUtils = new DoubleArrayList();

        earliestTime = apmLog.getStartTime();
        latestTime = apmLog.getEndTime();
        minDuration = apmLog.getMinDuration();
        maxDuration = apmLog.getMaxDuration();

        for (ATrace aTrace : traceList) {
            int eventSize = aTrace.getEventSize();
            caseDurations.add( eventSize == 0 ? 0 : aTrace.getDuration() );
            ttlProcessTimes.add( eventSize == 0 ? 0 : aTrace.getTotalProcessingTime() );
            avgProcessTimes.add( eventSize == 0 ? 0 : aTrace.getAverageProcessingTime() );
            maxProcessTimes.add( eventSize == 0 ? 0 : aTrace.getMaxProcessingTime() );
            ttlWaitTimes.add( eventSize == 0 ? 0 : aTrace.getTotalWaitingTime() );
            avgWaitTimes.add( eventSize == 0 ? 0 : aTrace.getAverageWaitingTime() );
            maxWaitTimes.add( eventSize == 0 ? 0 : aTrace.getMaxWaitingTime() );
            caseUtils.add( eventSize == 0 ? 0 : aTrace.getCaseUtilization() );
        }

        caseDurations.sortThis();
        ttlProcessTimes.sortThis();
        avgProcessTimes.sortThis();
        maxProcessTimes.sortThis();
        ttlWaitTimes.sortThis();
        avgWaitTimes.sortThis();
        maxWaitTimes.sortThis();
        caseUtils.sortThis();


        /**
         * Create case variant series
         */
        this.caseVariFreqMap = apmLog.getCaseVariantIdFrequencyMap();

        /**
         * Create cotMap
         */
        List<Long> cotList = new ArrayList<>();
        long cotUnit = (latestTime - earliestTime) / 100;
        long cUnit = earliestTime;
        cotList.add(cUnit);
        for(int i=1; i < 100; i++) {
            cUnit += cotUnit;
            cotList.add(cUnit);
        }
        if (cUnit < latestTime) {
            cotList.add(latestTime);
        }

        TreeSortedMap<Long, Integer> cotMap = new TreeSortedMap<>();
        TreeSortedMap<Long, Integer> eotMap = new TreeSortedMap<>();
        for (long l : cotList) {
            cotMap.put(l, 0);
            eotMap.put(l, 0);
        }

        long interval = latestTime - earliestTime;

        /**
         * For case duration
         */
        TreeSortedMap<Double, Integer> caseDurMap = new TreeSortedMap<>();
        DoubleArrayList caseDurKeys = initDoubleSeriesMap(caseDurations, caseDurMap);

        /**
         * For total processing time
         */
        TreeSortedMap<Double, Integer> ttlPTMap = new TreeSortedMap<>();
        DoubleArrayList ttlPTKeys = initDoubleSeriesMap(ttlProcessTimes, ttlPTMap);

        /**
         * For average processing time
         */
        TreeSortedMap<Double, Integer> avgPTMap = new TreeSortedMap<>();
        DoubleArrayList avgPTKeys = initDoubleSeriesMap(avgProcessTimes, avgPTMap);

        /**
         * For max processing time
         */
        TreeSortedMap<Double, Integer> maxPTMap = new TreeSortedMap<>();
        DoubleArrayList maxPTKeys = initDoubleSeriesMap(maxProcessTimes, maxPTMap);

        /**
         * For total waiting time
         */
        TreeSortedMap<Double, Integer> ttlWTMap = new TreeSortedMap<>();
        DoubleArrayList ttlWTKeys = initDoubleSeriesMap(ttlWaitTimes, ttlWTMap);

        /**
         * For average waiting time
         */
        TreeSortedMap<Double, Integer> avgWTMap = new TreeSortedMap<>();
        DoubleArrayList avgWTKeys = initDoubleSeriesMap(avgWaitTimes, avgWTMap);

        /**
         * For max waiting time
         */
        TreeSortedMap<Double, Integer> maxWTMap = new TreeSortedMap<>();
        DoubleArrayList maxWTKeys = initDoubleSeriesMap(maxWaitTimes, maxWTMap);

        /**
         * For case utilization
         */
        TreeSortedMap<Double, Integer> caseUtilMap = new TreeSortedMap<>();
        DoubleArrayList caseUtilKeys = initDoubleSeriesMap(caseUtils, caseUtilMap);

        /**
         * Loop (2):
         * - Fill in ...
         */
        DoubleArrayList[] keysArray = new DoubleArrayList[] {
                caseDurKeys,
                ttlPTKeys,
                avgPTKeys,
                maxPTKeys,
                ttlWTKeys,
                avgWTKeys,
                maxWTKeys,
                caseUtilKeys
        };

        List<TreeSortedMap<Double, Integer>> seriesMapsList =
                Arrays.asList(caseDurMap, ttlPTMap, avgPTMap, maxPTMap, ttlWTMap, avgWTMap, maxWTMap, caseUtilMap);

        for(ATrace aTrace : traceList) {

            List<AEvent> aEventList = aTrace.getEventList();

            if(aEventList.size() > 0) {
                long sTime = aTrace.getStartTimeMilli();
                long eTime = aTrace.getEndTimeMilli();

                /**
                 * For case over time
                 */
                for(Long uT : cotMap.keySet()) {
                    if(uT >= sTime && uT <= eTime) {
                        int y = cotMap.get(uT) + 1;
                        cotMap.put(uT, y);
                    }
                }

                /**
                 * For event over time
                 */
                for(Long uT : eotMap.keySet()) {
                    if(uT >= sTime && uT <= eTime) {
                        int y = eotMap.get(uT) + aTrace.getEventSize();
                        eotMap.put(uT, y);
                    }
                }

                if(interval > 0) {
                    double[] valuesArray = new double[]{
                            aTrace.getDuration(),
                            aTrace.getTotalProcessingTime(),
                            aTrace.getAverageProcessingTime(),
                            aTrace.getMaxProcessingTime(),
                            aTrace.getTotalWaitingTime(),
                            aTrace.getAverageWaitingTime(),
                            aTrace.getMaxWaitingTime(),
                            aTrace.getCaseUtilization()
                    };

                    insertValuesToMaps(valuesArray, keysArray, seriesMapsList);
                }
            }
        }

        // create caseOTSeries data
        caseOTSeriesData = new UnifiedMap<>(cotMap);

        //create eventOTseries data
        eventOTSeriesData = new UnifiedMap<>(eotMap);

        if(interval > 0) {
            /**
             * Make case duration chart series
             */
            this.caseDurationSeriesData = new UnifiedMap<>(caseDurMap);

            /**
             * Make total processing time chart series
             */
            this.totalProcessingTimeSeriesData = new UnifiedMap<>(ttlPTMap);

            /**
             * Make average processing time chart series
             */
            this.averageProcessingTimeSeriesData = new UnifiedMap<>(avgPTMap);

            /**
             * Make max processing time chart series
             */
            this.maxProcessingTimeSeriesData = new UnifiedMap<>(maxPTMap);

            /**
             * Make total waiting time chart series
             */
            this.totalWaitingTimeSeriesData = new UnifiedMap<>(ttlWTMap);

            /**
             * Make average waiting time chart series
             */
            this.averageWaitingTimeSeriesData = new UnifiedMap<>(avgWTMap);

            /**
             * Make max waiting time chart series
             */

            this.maxWaitingTimeSeriesData = new UnifiedMap<>(maxWTMap);

            /**
             * Make case utilization chart series
             */
            this.caseUtilizationSeriesData = new UnifiedMap<>(caseUtilMap);
        }
    }



    private void insertValuesToMaps(double[] valuesArray, DoubleArrayList[] keysArray,
                                    List<TreeSortedMap<Double, Integer>> seriesMapsList) {
        for (int i = 0; i < valuesArray.length; i++) {
            insertValueToMap(valuesArray[i], keysArray[i], seriesMapsList.get(i));
        }
    }

    private void insertValueToMap(double value, DoubleArrayList keys, TreeSortedMap<Double, Integer> seriesMap) {
        for (int j = 0; j < keys.size(); j++) {
            double key = keys.get(j);
            if (value <= key) {
                int y = seriesMap.get(key) + 1;
                seriesMap.put(key, y);
                break;
            }
        }
    }

    private DoubleArrayList initDoubleSeriesMap(DoubleArrayList values, TreeSortedMap<Double, Integer> seriesMap) {
        if (values.isEmpty()) return new DoubleArrayList();

        DoubleArrayList keys = new DoubleArrayList();

        double minVal = values.min();
        double maxVal = values.max();
        double unit = ( maxVal - minVal ) / MAX_UNIT;
        double current = minVal;
        seriesMap.put(current, 0);
        keys.add(current);
        while (current < maxVal) {
            current += unit;
            double key = current < maxVal ? current : maxVal;
            seriesMap.put( key, 0 );
            keys.add(key);
        }
        return keys;
    }

    public double getMaxDuration() {
        return maxDuration;
    }

    public double getMinDuration() {
        return minDuration;
    }

    public long getEarliestTime() {
        return earliestTime;
    }

    public long getLatestTime() {
        return latestTime;
    }


    public UnifiedMap<Integer, Integer> getCaseVariFreqMap() {
        return caseVariFreqMap;
    }

    public UnifiedMap<Long, Integer> getCaseOTSeriesData() {
        return caseOTSeriesData;
    }

    public UnifiedMap<Long, Integer> getEventOTSeriesData() {
        return eventOTSeriesData;
    }

    public UnifiedMap<Double, Integer> getCaseDurationSeriesData() {
        return caseDurationSeriesData;
    }

    public UnifiedMap<Double, Integer> getCaseUtilizationSeriesData() {
        return caseUtilizationSeriesData;
    }

    public UnifiedMap<Double, Integer> getTotalProcessingTimeSeriesData() {
        return totalProcessingTimeSeriesData;
    }

    public UnifiedMap<Double, Integer> getAverageProcessingTimeSeriesData() {
        return averageProcessingTimeSeriesData;
    }

    public UnifiedMap<Double, Integer> getMaxProcessingTimeSeriesData() {
        return maxProcessingTimeSeriesData;
    }

    public UnifiedMap<Double, Integer> getTotalWaitingTimeSeriesData() {
        return totalWaitingTimeSeriesData;
    }

    public UnifiedMap<Double, Integer> getAverageWaitingTimeSeriesData() {
        return averageWaitingTimeSeriesData;
    }

    public UnifiedMap<Double, Integer> getMaxWaitingTimeSeriesData() {
        return maxWaitingTimeSeriesData;
    }
}
