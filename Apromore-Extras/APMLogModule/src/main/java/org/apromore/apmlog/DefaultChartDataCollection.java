/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.apmlog;

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class provides the data that can be used in various chart-based data visualisation
 * such as Highcharts, Chart.js etc.
 *
 * @author Chii Chang (created: 14/02/2020)
 */
public class DefaultChartDataCollection {

    private UnifiedMap<Long, Integer> caseOTSeriesData;
    private UnifiedMap<Long, Integer> eventOTSeriesData;
    private UnifiedMap<Long, Integer> caseDurationSeriesData;
    private UnifiedMap<Long, Integer> totalProcessingTimeSeriesData;
    private UnifiedMap<Long, Integer> averageProcessingTimeSeriesData;
    private UnifiedMap<Long, Integer> maxProcessingTimeSeriesData;
    private UnifiedMap<Long, Integer> totalWaitingTimeSeriesData;
    private UnifiedMap<Long, Integer> averageWaitingTimeSeriesData;
    private UnifiedMap<Long, Integer> maxWaitingTimeSeriesData;
    private UnifiedMap<Double, Integer> caseUtilizationSeriesData;

    private UnifiedMap<String, Long> totalProcessingTimeMap;
    private UnifiedMap<String, Long> averageProcessingTimeMap;
    private UnifiedMap<String, Long> maxProcessingTimeMap;
    private UnifiedMap<String, Long> totalWaitingTimeMap;
    private UnifiedMap<String, Long> averageWaitingTimeMap;
    private UnifiedMap<String, Long> maxWaitingTimeMap;
    private UnifiedMap<String, Double> caseUtilizationMap;

    private UnifiedMap<Integer, Integer> caseVariFreqMap;

    private long stepUnit = 1;
    private long earliestTime = 0; //for case overtime
    private long latestTime = 0; //for case overtime
    private long minDuration = 0;
    private long maxDuration = 0;
    private final int MAX_UNIT = 100;
    DecimalFormat decimalFormat = new DecimalFormat("###############.##");

    public DefaultChartDataCollection(APMLog apmLog) {

        this.totalProcessingTimeMap = new UnifiedMap<>();
        this.averageProcessingTimeMap = new UnifiedMap<>();
        this. maxProcessingTimeMap = new UnifiedMap<>();
        this.totalWaitingTimeMap = new UnifiedMap<>();
        this.averageWaitingTimeMap = new UnifiedMap<>();
        this.maxWaitingTimeMap = new UnifiedMap<>();
        this.caseUtilizationMap = new UnifiedMap<>();

        this.caseVariFreqMap = new UnifiedMap<>(); //2019-11-13

        List<Long> allTotalProcessingTimes = new ArrayList<>();
        List<Long> allAverageProcessingTimes = new ArrayList<>();
        List<Long> allMaxProcessingTimes = new ArrayList<>();
        List<Long> allTotalWaitingTimes = new ArrayList<>();
        List<Long> allAverageWaitingTimes = new ArrayList<>();
        List<Long> allMaxWaitingTimes = new ArrayList<>();
        List<Double> allCaseUtils = new ArrayList<>();

        earliestTime = apmLog.getStartTime();
        latestTime = apmLog.getEndTime();
        minDuration = apmLog.getMinDuration();
        maxDuration = apmLog.getMaxDuration();

        for(int i = 0; i< apmLog.getTraceList().size() ; i++) {

            ATrace aTrace = apmLog.getTraceList().get(i);

            List<AEvent> aEventList = aTrace.getEventList();

            if(aEventList.size() > 0) {
                String caseName = aTrace.getCaseId();

                /**
                 * Set the performance stats
                 */
                totalProcessingTimeMap.put(caseName, aTrace.getTotalProcessingTime());
                averageProcessingTimeMap.put(caseName, aTrace.getAverageProcessingTime());
                maxProcessingTimeMap.put(caseName, aTrace.getMaxProcessingTime());
                totalWaitingTimeMap.put(caseName, aTrace.getTotalWaitingTime());
                averageWaitingTimeMap.put(caseName, aTrace.getAverageWaitingTime());
                maxWaitingTimeMap.put(caseName, aTrace.getMaxWaitingTime());
                caseUtilizationMap.put(caseName, aTrace.getCaseUtilization());

                allTotalProcessingTimes.add(aTrace.getTotalProcessingTime());
                allAverageProcessingTimes.add(aTrace.getAverageProcessingTime());
                allMaxProcessingTimes.add(aTrace.getMaxProcessingTime());
                allTotalWaitingTimes.add(aTrace.getTotalWaitingTime());
                allAverageWaitingTimes.add(aTrace.getAverageWaitingTime());
                allMaxWaitingTimes.add(aTrace.getMaxWaitingTime());
                allCaseUtils.add(aTrace.getCaseUtilization());

            }
        }

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
        for(int i=0; i<cotList.size(); i++) {
            cotMap.put(cotList.get(i), 0);
        }

        /**
         * Create eotMap (events over time) based on the cotList values
         */
        TreeSortedMap<Long, Integer> eotMap = new TreeSortedMap<>();
        for(int i=0; i<cotList.size(); i++) {
            eotMap.put(cotList.get(i), 0);
        }


        long interval = latestTime - earliestTime;

        /**
         * For case duration
         */
        TreeSortedMap<Long, Integer> caseDurMap = new TreeSortedMap<>(); // Long as duration, Integer as frequency
        long cateUnit = 0;
        if(interval > 0) {
            int numOfUnits = MAX_UNIT;
            long first = minDuration;
            long last = maxDuration;
            cateUnit = (last - first) / numOfUnits;
            this.stepUnit = cateUnit;
            long current = first;
            caseDurMap.put(current, 0);
            for(int i=1; i < numOfUnits; i++) {
                current += cateUnit;
                caseDurMap.put(current, 0);
            }
            if (current < last) {
                caseDurMap.put(last, 0);
            }

            this.minDuration = first;
            this.maxDuration = last;
        }

        /**
         * For total processing time
         */
        TreeSortedMap<Long, Integer> ttlPTMap = new TreeSortedMap<>();
        long ttlPTUnit = 0;
        if(interval > 0) {
            Collections.sort(allTotalProcessingTimes);
            int numOfUnits = MAX_UNIT;
            long first = allTotalProcessingTimes.get(0);
            long last = allTotalProcessingTimes.get(allTotalProcessingTimes.size() - 1);
            ttlPTUnit = (last - first) / numOfUnits;
            long current = first - ttlPTUnit;
            for(int i=0; i <= numOfUnits; i++) {
                if(i == numOfUnits) current = last;
                else current += ttlPTUnit;
                ttlPTMap.put(current, 0);
            }
        }
        /**
         * For average processing time
         */
        TreeSortedMap<Long, Integer> avgPTMap = new TreeSortedMap<>();
        long avgPTUnit = 0;
        if(interval > 0) {
            Collections.sort(allAverageProcessingTimes);
            long first = allAverageProcessingTimes.get(0);
            long last = allAverageProcessingTimes.get(allAverageProcessingTimes.size() - 1);
            avgPTUnit = (last - first) / MAX_UNIT;
            long current = first - avgPTUnit;
            for(int i=0; i <= MAX_UNIT; i++) {
                if(i == MAX_UNIT) current = last;
                else current += avgPTUnit;
                avgPTMap.put(current, 0);
            }
        }
        /**
         * For max processing time
         */
        TreeSortedMap<Long, Integer> maxPTMap = new TreeSortedMap<>();
        long maxPTUnit = 0;
        if(interval > 0) {
            Collections.sort(allMaxProcessingTimes);
            long first = allMaxProcessingTimes.get(0);
            long last = allMaxProcessingTimes.get(allMaxProcessingTimes.size() - 1);
            maxPTUnit = (last - first) / MAX_UNIT;
            long current = first - maxPTUnit;
            for(int i=0; i <= MAX_UNIT; i++) {
                if(i == MAX_UNIT) current = last;
                else current += maxPTUnit;
                maxPTMap.put(current, 0);
            }
        }
        /**
         * For total waiting time
         */
        TreeSortedMap<Long, Integer> ttlWTMap = new TreeSortedMap<>();
        long ttlWTUnit = 0;
        if(interval > 0) {
            Collections.sort(allTotalWaitingTimes);
            int numOfUnits = MAX_UNIT;
            long first = allTotalWaitingTimes.get(0);
            long last = allTotalWaitingTimes.get(allTotalWaitingTimes.size() - 1);
            ttlWTUnit = (last - first) / numOfUnits;
            long current = first - ttlWTUnit;
            for(int i=0; i <= numOfUnits; i++) {
                if(i == numOfUnits) current = last;
                else current += ttlWTUnit;
                ttlWTMap.put(current, 0);
            }
        }
        /**
         * For average waiting time
         */
        TreeSortedMap<Long, Integer> avgWTMap = new TreeSortedMap<>();
        long avgWTUnit = 0;
        if(interval > 0) {
            Collections.sort(allAverageWaitingTimes);
            int numOfUnits = MAX_UNIT;
            long first = allAverageWaitingTimes.get(0);
            long last = allAverageWaitingTimes.get(allAverageWaitingTimes.size() - 1);
            avgWTUnit = (last - first) / numOfUnits;
            long current = first - avgWTUnit;
            for(int i=0; i <= numOfUnits; i++) {
                if(i == numOfUnits) current = last;
                else current += avgWTUnit;
                avgWTMap.put(current, 0);
            }
        }
        /**
         * For max waiting time
         */
        TreeSortedMap<Long, Integer> maxWTMap = new TreeSortedMap<>();
        long maxWTUnit = 0;
        if(interval > 0) {
            Collections.sort(allMaxWaitingTimes);
            int numOfUnits = MAX_UNIT;
            long first = allMaxWaitingTimes.get(0);
            long last = allMaxWaitingTimes.get(allMaxWaitingTimes.size() - 1);
            maxWTUnit = (last - first) / numOfUnits;
            long current = first - maxWTUnit;
            for(int i=0; i <= numOfUnits; i++) {
                if(i == numOfUnits) current = last;
                else current += maxWTUnit;
                maxWTMap.put(current, 0);
            }
        }
        /**
         * For case utilization
         */
        TreeSortedMap<Double, Integer> caseUtilMap = new TreeSortedMap<>();
        double caseUtilUnit = 0;
        if(interval > 0) {
            Collections.sort(allCaseUtils);
            int numOfUnits = MAX_UNIT;
            double first = allCaseUtils.get(0);
            double last = allCaseUtils.get(allCaseUtils.size() - 1);
            caseUtilUnit = (last - first) / numOfUnits;
            double current = first - caseUtilUnit;
            for(int i=0; i < numOfUnits; i++) {
                current += caseUtilUnit;
                caseUtilMap.put(current, 0);
            }
            caseUtilMap.put(last, 0);
            caseUtilMap.put(last, 0);
        }

        /**
         * Loop (2):
         * - Fill in ...
         */
        for(int i = 0; i< apmLog.size(); i++) {
            ATrace aTrace = apmLog.get(i);

            List<AEvent> aEventList = aTrace.getEventList();

            if(aEventList.size() > 0) {
                String caseName = aTrace.getCaseId();

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
                    /**
                     * Case duration
                     */
                    long caseDuration = aTrace.getDuration();
                    for(Long key : caseDurMap.keySet()) {
                        long p = key - cateUnit;
                        if(caseDuration <= key && caseDuration > p) {
                            int y = caseDurMap.get(key) + 1;
                            caseDurMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Total processing time
                     */
                    long ttlPT = totalProcessingTimeMap.get(caseName);
                    for(Long key : ttlPTMap.keySet()) {
                        long p = key - ttlPTUnit;
                        if(ttlPT > p && ttlPT <= key) {
                            int y = ttlPTMap.get(key) + 1;
                            ttlPTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Average processing time
                     */
                    long avgPT = averageProcessingTimeMap.get(caseName);
                    for(Long key : avgPTMap.keySet()) {
                        long p = key - avgPTUnit;
                        if(avgPT > p && avgPT <= key) {
                            int y = avgPTMap.get(key) + 1;
                            avgPTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Max processing time
                     */
                    long maxPT = maxProcessingTimeMap.get(caseName);
                    for(Long key : maxPTMap.keySet()) {
                        long p = key - maxPTUnit;
                        if(maxPT > p && maxPT <= key) {
                            int y = maxPTMap.get(key) + 1;
                            maxPTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Total waiting time
                     */
                    long ttlWT = totalWaitingTimeMap.get(caseName);
                    for(Long key : ttlWTMap.keySet()) {
                        long p = key - ttlWTUnit;
                        if(ttlWT > p && ttlWT <= key) {
                            int y = ttlWTMap.get(key) + 1;
                            ttlWTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Average waiting time
                     */
                    long avgWT = averageWaitingTimeMap.get(caseName);
                    for(Long key : avgWTMap.keySet()) {
                        long p = key - avgWTUnit;
                        if(avgWT > p && avgWT <= key) {
                            int y = avgWTMap.get(key) + 1;
                            avgWTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Max waiting time
                     */
                    long maxWT = maxWaitingTimeMap.get(caseName);
                    for(Long key : maxWTMap.keySet()) {
                        long p = key - maxWTUnit;
                        if(maxWT > p && maxWT <= key) {
                            int y = maxWTMap.get(key) + 1;
                            maxWTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Case utilization
                     */
                    double caseUtil = caseUtilizationMap.get(caseName);
                    for(Double key : caseUtilMap.keySet()) {
                        double p = key - caseUtilUnit;
                        double n = key + caseUtilUnit;
                        if(caseUtil >= p && caseUtil <= n) {
                            int y =caseUtilMap.get(key) + 1;
                            caseUtilMap.put(key, y);
                            break;
                        }
                    }
                }
            }
        }



        // create caseOTSeries data
        caseOTSeriesData = new UnifiedMap<>();
        for(Long l : cotMap.keySet()) {
            caseOTSeriesData.put(l, cotMap.get(l));
        }

        //create eventOTseries data
        eventOTSeriesData = new UnifiedMap<>();
        for(Long l : eotMap.keySet()) {
            eventOTSeriesData.put(l, eotMap.get(l));
        }

        if(interval > 0) {


            /**
             * Make case duration chart series
             */
            this.caseDurationSeriesData = new UnifiedMap();
            for(Long key : caseDurMap.keySet()) {
                int y = caseDurMap.get(key);
                this.caseDurationSeriesData.put(key, y);
            }

            /**
             * Make total processing time chart series
             */
            this.totalProcessingTimeSeriesData = new UnifiedMap<>();
            for(Long key : ttlPTMap.keySet()) {
                int y = ttlPTMap.get(key);
                this.totalProcessingTimeSeriesData.put(key, y);
            }
            /**
             * Make average processing time chart series
             */
            this.averageProcessingTimeSeriesData = new UnifiedMap<>();
            for(Long key : avgPTMap.keySet()) {
                int y = avgPTMap.get(key);
                this.averageProcessingTimeSeriesData.put(key, y);
            }
            /**
             * Make max processing time chart series
             */
            this.maxProcessingTimeSeriesData = new UnifiedMap<>();
            for(Long key : maxPTMap.keySet()) {
                int y = maxPTMap.get(key);
                this.maxProcessingTimeSeriesData.put(key, y);
            }
            /**
             * Make total waiting time chart series
             */
            this.totalWaitingTimeSeriesData = new UnifiedMap<>();
            for(Long key : ttlWTMap.keySet()) {
                int y = ttlWTMap.get(key);
                this.totalWaitingTimeSeriesData.put(key, y);
            }
            /**
             * Make average waiting time chart series
             */
            this.averageWaitingTimeSeriesData = new UnifiedMap<>();
            for(Long key : avgWTMap.keySet()) {
                int y = avgWTMap.get(key);
                this.averageWaitingTimeSeriesData.put(key, y);
            }
            /**
             * Make max waiting time chart series
             */
            this.maxWaitingTimeSeriesData = new UnifiedMap<>();
            for(Long key : maxWTMap.keySet()) {
                int y = maxWTMap.get(key);
                this.maxWaitingTimeSeriesData.put(key, y);
            }
            /**
             * Make case utilization chart series
             */
            this.caseUtilizationSeriesData = new UnifiedMap<>();
            for(Double key : caseUtilMap.keySet()) {
                int y = caseUtilMap.get(key);
                this.caseUtilizationSeriesData.put(key, y);
            }
        }
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public long getMinDuration() {
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

    public UnifiedMap<Long, Integer> getCaseDurationSeriesData() {
        return caseDurationSeriesData;
    }

    public UnifiedMap<Double, Integer> getCaseUtilizationSeriesData() {
        return caseUtilizationSeriesData;
    }

    public UnifiedMap<Long, Integer> getTotalProcessingTimeSeriesData() {
        return totalProcessingTimeSeriesData;
    }

    public UnifiedMap<Long, Integer> getAverageProcessingTimeSeriesData() {
        return averageProcessingTimeSeriesData;
    }

    public UnifiedMap<Long, Integer> getMaxProcessingTimeSeriesData() {
        return maxProcessingTimeSeriesData;
    }

    public UnifiedMap<Long, Integer> getTotalWaitingTimeSeriesData() {
        return totalWaitingTimeSeriesData;
    }

    public UnifiedMap<Long, Integer> getAverageWaitingTimeSeriesData() {
        return averageWaitingTimeSeriesData;
    }

    public UnifiedMap<Long, Integer> getMaxWaitingTimeSeriesData() {
        return maxWaitingTimeSeriesData;
    }
}
