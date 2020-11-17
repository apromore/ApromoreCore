/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.sorted.mutable.TreeSortedMap;

import java.text.DecimalFormat;
import java.util.*;

/**
 * This class provides the data that can be used in various chart-based data visualisation
 * such as Highcharts, Chart.js etc.
 *
 * @author Chii Chang (created: 14/02/2020)
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

    private UnifiedMap<String, Double> totalProcessingTimeMap;
    private UnifiedMap<String, Double> averageProcessingTimeMap;
    private UnifiedMap<String, Double> maxProcessingTimeMap;
    private UnifiedMap<String, Double> totalWaitingTimeMap;
    private UnifiedMap<String, Double> averageWaitingTimeMap;
    private UnifiedMap<String, Double> maxWaitingTimeMap;
    private UnifiedMap<String, Double> caseUtilizationMap;

    private UnifiedMap<Integer, Integer> caseVariFreqMap;

    private double stepUnit = 1;
    private long earliestTime = 0; //for case overtime
    private long latestTime = 0; //for case overtime
    private double minDuration = 0;
    private double maxDuration = 0;
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

        List<Double> allTotalProcessingTimes = new ArrayList<>();
        List<Double> allAverageProcessingTimes = new ArrayList<>();
        List<Double> allMaxProcessingTimes = new ArrayList<>();
        List<Double> allTotalWaitingTimes = new ArrayList<>();
        List<Double> allAverageWaitingTimes = new ArrayList<>();
        List<Double> allMaxWaitingTimes = new ArrayList<>();
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
        TreeSortedMap<Double, Integer> caseDurMap = new TreeSortedMap<>(); // Long as duration, Integer as frequency
        double cateUnit = 0;
        if(interval > 0) {
            int numOfUnits = MAX_UNIT;
            double first = minDuration;
            double last = maxDuration;
            cateUnit = (last - first) / numOfUnits;
            this.stepUnit = cateUnit;
            double current = first;
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
        TreeSortedMap<Double, Integer> ttlPTMap = new TreeSortedMap<>();
        double ttlPTUnit = fillMap(allTotalProcessingTimes, interval, ttlPTMap);

        /**
         * For average processing time
         */
        TreeSortedMap<Double, Integer> avgPTMap = new TreeSortedMap<>();
        double avgPTUnit = fillMap(allAverageProcessingTimes, interval, avgPTMap);

        /**
         * For max processing time
         */
        TreeSortedMap<Double, Integer> maxPTMap = new TreeSortedMap<>();
        double maxPTUnit = fillMap(allMaxProcessingTimes, interval, maxPTMap);

        /**
         * For total waiting time
         */
        TreeSortedMap<Double, Integer> ttlWTMap = new TreeSortedMap<>();
        double ttlWTUnit = fillMap(allTotalWaitingTimes, interval, ttlWTMap);

        /**
         * For average waiting time
         */
        TreeSortedMap<Double, Integer> avgWTMap = new TreeSortedMap<>();
        double avgWTUnit = fillMap(allAverageWaitingTimes, interval, avgWTMap);

        /**
         * For max waiting time
         */
        TreeSortedMap<Double, Integer> maxWTMap = new TreeSortedMap<>();
        double maxWTUnit = fillMap(allMaxWaitingTimes, interval, maxWTMap);

        /**
         * For case utilization
         */
        TreeSortedMap<Double, Integer> caseUtilMap = new TreeSortedMap<>();
        double caseUtilUnit = 0;
        if (allCaseUtils.size() > 0 ) {
            if (interval > 0) {
                Collections.sort(allCaseUtils);
                int numOfUnits = (MAX_UNIT / 2);
                double first = allCaseUtils.get(0);
                double last = allCaseUtils.get(allCaseUtils.size() - 1);
                caseUtilUnit = (last - first) / numOfUnits;
                double current = first - caseUtilUnit;
                for (int i = 0; i < numOfUnits; i++) {
                    current += caseUtilUnit;
                    caseUtilMap.put(current, 0);
                }
                caseUtilMap.put(last, 0);
                caseUtilMap.put(last, 0);
            }
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
                    double caseDuration = aTrace.getDuration();
                    for(double key : caseDurMap.keySet()) {
                        double p = key - cateUnit;
                        if(caseDuration <= key && caseDuration > p) {
                            int y = caseDurMap.get(key) + 1;
                            caseDurMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Total processing time
                     */
                    double ttlPT = totalProcessingTimeMap.get(caseName);
                    for(double key : ttlPTMap.keySet()) {
                        double p = key - ttlPTUnit;
                        if(ttlPT > p && ttlPT <= key) {
                            int y = ttlPTMap.get(key) + 1;
                            ttlPTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Average processing time
                     */
                    double avgPT = averageProcessingTimeMap.get(caseName);
                    for(double key : avgPTMap.keySet()) {
                        double p = key - avgPTUnit;
                        if(avgPT > p && avgPT <= key) {
                            int y = avgPTMap.get(key) + 1;
                            avgPTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Max processing time
                     */
                    double maxPT = maxProcessingTimeMap.get(caseName);
                    for(double key : maxPTMap.keySet()) {
                        double p = key - maxPTUnit;
                        if(maxPT > p && maxPT <= key) {
                            int y = maxPTMap.get(key) + 1;
                            maxPTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Total waiting time
                     */
                    double ttlWT = totalWaitingTimeMap.get(caseName);
                    for(double key : ttlWTMap.keySet()) {
                        double p = key - ttlWTUnit;
                        if(ttlWT > p && ttlWT <= key) {
                            int y = ttlWTMap.get(key) + 1;
                            ttlWTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Average waiting time
                     */
                    double avgWT = averageWaitingTimeMap.get(caseName);
                    for(double key : avgWTMap.keySet()) {
                        double p = key - avgWTUnit;
                        if(avgWT > p && avgWT <= key) {
                            int y = avgWTMap.get(key) + 1;
                            avgWTMap.put(key, y);
                            break;
                        }
                    }
                    /**
                     * Max waiting time
                     */
                    double maxWT = maxWaitingTimeMap.get(caseName);
                    for(double key : maxWTMap.keySet()) {
                        double p = key - maxWTUnit;
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
            for(double key : caseDurMap.keySet()) {
                int y = caseDurMap.get(key);
                this.caseDurationSeriesData.put(key, y);
            }

            /**
             * Make total processing time chart series
             */
            this.totalProcessingTimeSeriesData = new UnifiedMap<>();
            for(double key : ttlPTMap.keySet()) {
                int y = ttlPTMap.get(key);
                this.totalProcessingTimeSeriesData.put(key, y);
            }
            /**
             * Make average processing time chart series
             */
            this.averageProcessingTimeSeriesData = new UnifiedMap<>();
            for(double key : avgPTMap.keySet()) {
                int y = avgPTMap.get(key);
                this.averageProcessingTimeSeriesData.put(key, y);
            }
            /**
             * Make max processing time chart series
             */
            this.maxProcessingTimeSeriesData = new UnifiedMap<>();
            for(double key : maxPTMap.keySet()) {
                int y = maxPTMap.get(key);
                this.maxProcessingTimeSeriesData.put(key, y);
            }
            /**
             * Make total waiting time chart series
             */
            this.totalWaitingTimeSeriesData = new UnifiedMap<>();
            for(double key : ttlWTMap.keySet()) {
                int y = ttlWTMap.get(key);
                this.totalWaitingTimeSeriesData.put(key, y);
            }
            /**
             * Make average waiting time chart series
             */
            this.averageWaitingTimeSeriesData = new UnifiedMap<>();
            for(double key : avgWTMap.keySet()) {
                int y = avgWTMap.get(key);
                this.averageWaitingTimeSeriesData.put(key, y);
            }
            /**
             * Make max waiting time chart series
             */
            this.maxWaitingTimeSeriesData = new UnifiedMap<>();
            for(double key : maxWTMap.keySet()) {
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

    private double fillMap(List<Double> allValueList, double interval,
                           TreeSortedMap<Double, Integer> seriesMap) {
        double unit = 0;
        if (allValueList.size() > 0 ) {
            if (interval > 0) {
                Collections.sort(allValueList);
                int numOfUnits = MAX_UNIT;
                double first = allValueList.get(0);
                double last = allValueList.get(allValueList.size() - 1);
                unit = (last - first) / numOfUnits;
                double current = first - unit;
                for (int i = 0; i <= numOfUnits; i++) {
                    if (i == numOfUnits) current = last;
                    else current += unit;
                    seriesMap.put(current, 0);
                }
            }
        }
        return unit;
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
