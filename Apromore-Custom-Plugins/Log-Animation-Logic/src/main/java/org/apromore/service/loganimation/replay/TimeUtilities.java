/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2015 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.service.loganimation.replay;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Seconds;

public class TimeUtilities {
    public static ArrayList<Interval> divide(Interval v1, Interval v2) {
        ArrayList<Interval> divide = new ArrayList();
        Interval overlap = v1.overlap(v2);
        
        if (overlap != null) {
            long overlapStart = overlap.getStartMillis();
            long overlapEnd = overlap.getEndMillis();
            
            long v1Start = v1.getStartMillis();
            long v1End = v1.getEndMillis();
            
            long v2Start = v2.getStartMillis();
            long v2End = v2.getEndMillis();
            
            long minStart = Math.min(v1Start, v2Start);
            long maxEnd = Math.max(v1End, v2End);
            
            divide.add(new Interval(minStart, overlapStart));
            divide.add(overlap);
            divide.add(new Interval(overlapEnd, maxEnd));
        }
        return divide;
    }
    
    //Convert from a sequence flow to an interval
    public static Interval getInterval(SequenceFlow flow) {
        return new Interval(((TraceNode)flow.getSourceRef()).getStart(),((TraceNode)flow.getTargetRef()).getComplete());
    }
    
    //Input interval set must have been sorted in increasing order of start date
    //The start and end date of each interval in a set are projected onto a timeline
    //As a result, the timeline contains all start/end dates of all intervals
    //Return: a map with key is interval, value is the number of transfer containing the key transfer
    //|---------|---|   |------|      |---------|
    //|--|--------|     |---------|        |-------|
    //|------|        |------|    
    //Result
    //|3-|-3-|-2|2|2|0|1|-3--|2|-1|-0-|-1--|--2-|-1|
    private static Map<Interval,Integer> aggregateIntervals(SortedSet<Interval> intervals) {
        SortedSet<DateTime> timeline = new TreeSet<>(
                                new Comparator<DateTime>() {
                                    @Override
                                    public int compare(DateTime o1, DateTime o2) {
                                        if (o1.isBefore(o2)) {
                                            return -1;
                                        }
                                        else {
                                            return +1;
                                        }
                                    }
                                }); 
        
        //-----------------------------------------------------
        //First, create an ordered timeline based on intervals
        //The date milestone on the timeline is the start and end date of each interval
        //Note: if two datetime are equal, two duplicate elements are on the timeline
        //-----------------------------------------------------        
        Set<DateTime> starts = new HashSet();
        Set<DateTime> ends = new HashSet();
        for (Interval interval : intervals) {
            timeline.add(interval.getStart()); //added datetime will be arranged in timing order
            starts.add(interval.getStart());
            timeline.add(interval.getEnd());
            ends.add(interval.getEnd());
        }
        
        //------------------------------------------------
        //Then, traverse the timeline to count tokens
        //current-next traverses every interval on the timeline formed by two 
        //consecutive datetime points
        //------------------------------------------------
        DateTime current = null;
        DateTime next = null;
        Iterator<DateTime> iterator = timeline.iterator();
        int intervalCount = 0;
        Map<Interval, Integer> intervalCountMap = new HashMap();
        
        while (iterator.hasNext()) {
            if (current == null) {
                current = iterator.next();
            } else {
                current = next;
            }
            
            if (iterator.hasNext()) {
                next = iterator.next();
                
                if (starts.contains(current)) {
                    intervalCount++;
                }
                if (ends.contains(current)) {
                    intervalCount--;
                }
                
                if (current.isBefore(next)) {
                    intervalCountMap.put(new Interval(current,next), intervalCount);
                }
            }
        }
        
        return intervalCountMap;
    }
    
    /*
    * intervals: set of intervals, each set is for one sequenceId (the key). These intervals can overlap
    * Return: set of non-overlapping intervals, each set is for one sequenceId (the key). From the
    * input set of intervals, if two intervals overlap, they are splited into three non-overlapping intervals, 
    * one is the overlapping interval and the other twos are the non-overlapping inverval of each original interval
    * The interger value is the number of overlaps (2 or more if the input intervals set contains more intervals).
    */
    public static Map<String,Map<Interval,Integer>> aggregateIntervalMap(Map<String,SortedSet<Interval>> intervalMap) {
        Map<String,Map<Interval,Integer>> sequenceFlowTransferMap = new HashMap<>();
        
        for (String sequenceId : intervalMap.keySet()) {
            sequenceFlowTransferMap.put(sequenceId, TimeUtilities.aggregateIntervals(intervalMap.get(sequenceId)));
        }
        return sequenceFlowTransferMap;
    }
    
    //interval:                         |----|
    //intervalMap: |-------9------|-6--|---5---|--3----|--7---|-1-|
    //return: 5
    private static Integer getIntervalCount(Interval interval, Map<Interval,Integer> intervalMap) {
        for (Interval mapInterval : intervalMap.keySet()) {
            if (mapInterval.contains(interval)) {
                return intervalMap.get(mapInterval);
            }
        }
        return 0;
    }
    
    //Compare two interval maps
    //The intervals in each map are consecutive but non-overlapping (like a timeline)
    //Return: resulting interval map containing intervals, the value is the 
    //difference in values of two input interval maps, can be plus or minus value
    //It is equal to value1 - value2
    //map1:   |-------8-------|---5----|----2-----|-------10--------|
    //map2:       |--5---|---3----|----9-----|----8-----|-------4--------|
    //result: |-8-|---3--|--5-|-2-|-/4-|-/7--|-/6-|--2--|-----6-----|-/4-|
    public static Map<Interval,Integer> compareIntervalMaps(
                                                Map<Interval,Integer> map1, 
                                                Map<Interval,Integer> map2) {
        
        Map<Interval,Integer> resultMap = new HashMap();
        SortedSet<DateTime> timeline = new TreeSet<>(
                                new Comparator<DateTime>() {
                                    @Override
                                    public int compare(DateTime o1, DateTime o2) {
                                        if (o1.isBefore(o2)) {
                                            return -1;
                                        }
                                        else if (o1.isEqual(o2)) { //if two points are the same, only one is added
                                            return 0;
                                        }
                                        else {
                                            return +1;
                                        }
                                    }
                                }); 
        
        //-------------------------------------------
        // Put all start and end points of all intervals on a timeline
        // Note: if two points of time are the same, only one is kept on timeline
        //-------------------------------------------
        for (Interval interval : map1.keySet()) {
            timeline.add(interval.getStart()); //added datetime will be arranged in timing order
            timeline.add(interval.getEnd());
        }
        for (Interval interval : map2.keySet()) {
            timeline.add(interval.getStart()); 
            timeline.add(interval.getEnd());
        }      
        
        //----------------------------------------------
        //Traverse the timeline and compare intervals of map1,map2
        //current-next traverses every interval on the timeline formed 
        //by two consecutive datetime points
        //----------------------------------------------
        DateTime current = null;
        DateTime next = null;
        Interval timelineInterval;
        Iterator<DateTime> iterator = timeline.iterator();
        while (iterator.hasNext()) {
            if (current == null) {
                current = iterator.next();
            } else {
                current = next;
            }
            
            if (iterator.hasNext()) {
                next = iterator.next();
                if (current.isBefore(next)) {
                    timelineInterval = new Interval(current,next);
                    resultMap.put(timelineInterval, getIntervalCount(timelineInterval,map1) - 
                                                getIntervalCount(timelineInterval,map2));
                }
            }            
        }
        return resultMap;
    }
    
    public static SortedSet<DateTime> sortDates(Set<DateTime> dateSet) {
        
        SortedSet<DateTime> timeline = new TreeSet<>(
                                new Comparator<DateTime>() {
                                    @Override
                                    public int compare(DateTime o1, DateTime o2) {
                                        return o1.compareTo(o2);
                                    }
                                }); 
        
        for (DateTime dateE : dateSet) {
            timeline.add(dateE);
        }
        
        return timeline;
    }
    
    /*
     * dateSet contains sorted dates which may have many date clusters (dates
     * close to each other in a proximity). 
     * This method selects only the start date for each cluster and arrange
     * them in a sorted set. 
     */
    public static ArrayList<DateTime> selectClusterStarts(SortedSet<DateTime> dateSet, int gapSeconds) {
        ArrayList<DateTime> dateList = new ArrayList();
        
        for (DateTime date : dateSet) {
            if (dateList.isEmpty()) {
                dateList.add(date);
            }
            else {
                if (Seconds.secondsBetween(dateList.get(dateList.size()-1), date).getSeconds() > gapSeconds) {
                    dateList.add(date);
                }
            }
        }
        
        return dateList;
    }
    
}
