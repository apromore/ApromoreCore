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

public class ReplayParams {
    
    private double maxCost;
    private int maxDepth;
    private double minHits;
    private double maxHits;
    private int MaxConsecutiveUnmatch;
    private double ActivityMatchCost;
    private double ActivitySkippedCost;
    private double EventSkippedCost;
    private double NonActivityMoveCost;
    private double traceChunkSize; //maximum length of trace to be searched one at a time
    private int MaxNumberOfNodesVisited;
    private double MaxActivitySkipPercent;
    private int MaxNodeDistance;
    private int TimelineSlots;
    private int TotalEngineSeconds;
    private int ProgressCircleBarRadius;
    private int SequenceTokenDiffThreshold;
    private String BacktrackingDebug;
    private String ExporeShortestPathDebug;
    private String ExactTraceFitnessCalculation;    
    private long MaxTimePerTrace;
    private long MaxTimeShortestPathExploration;
    private String CheckViciousCycle;
    private int CurrentShortestPath = Integer.MAX_VALUE; //number of activities
    private int StartEventToFirstEventDuration = 60; // default is 1 minute
    private int LastEventToEndEventDuration = 24*3600; // default is 1 day
    
    public int getStartEventToFirstEventDuration() {
        return this.StartEventToFirstEventDuration;
    }
    
    public void setStartEventToFirstEventDuration(int newDuration) {
        this.StartEventToFirstEventDuration = newDuration;
    }    
    
    public int getLastEventToEndEventDuration() {
        return this.LastEventToEndEventDuration;
    }
    
    public void setLastEventToEndEventDuration(int newDuration) {
        this.LastEventToEndEventDuration = newDuration;
    }       
    
    public int getCurrentShortestPath() {
        return this.CurrentShortestPath;
    }
    
    public void setCurrentShortestPath(int CurrentShortestPath) {
        this.CurrentShortestPath = CurrentShortestPath;
    }

    public boolean isCheckViciousCycle() {
        return CheckViciousCycle.toLowerCase().equals("true");
    }

    public void setCheckViciousCycle(String CheckViciousCycle) {
        this.CheckViciousCycle = CheckViciousCycle;
    }


    public boolean isExactTraceFitnessCalculation() {
        return ExactTraceFitnessCalculation.toLowerCase().equals("true");
    }

    public void setExactTraceFitnessCalculation(String ExactTraceFitnessCalculation) {
        this.ExactTraceFitnessCalculation = ExactTraceFitnessCalculation;
    }

    public long getMaxTimePerTrace() {
        return MaxTimePerTrace;
    }

    public void setMaxTimePerTrace(long MaxTimePerTrace) {
        this.MaxTimePerTrace = MaxTimePerTrace;
    }
    
    public long getMaxTimeShortestPathExploration() {
        return MaxTimeShortestPathExploration;
    }

    public void setMaxTimeShortestPathExploration(long MaxTimeShortestPathExploration) {
        this.MaxTimeShortestPathExploration = MaxTimeShortestPathExploration;
    }    
    
    public void setBacktrackingDebug(String BacktrackingDebug) {
        this.BacktrackingDebug = BacktrackingDebug;
    }
    
    public boolean isBacktrackingDebug() {
        return this.BacktrackingDebug.toLowerCase().equals("true");
    }
    
    public void setExploreShortestPathDebug(String ExporeShortestPathDebug) {
        this.ExporeShortestPathDebug = ExporeShortestPathDebug;
    }
    
    public boolean isExploreShortestPathDebug() {
        return this.ExporeShortestPathDebug.toLowerCase().equals("true");
    }    

    /**
     * The difference between token volume from two different logs 
     * running through the same sequence flow within a unit of time
     * @return 
     */
    public int getSequenceTokenDiffThreshold() {
        return SequenceTokenDiffThreshold;
    }

    public void setSequenceTokenDiffThreshold(int SequenceTokenDiffThreshold) {
        this.SequenceTokenDiffThreshold = SequenceTokenDiffThreshold;
    }


    public int getProgressCircleBarRadius() {
        return ProgressCircleBarRadius;
    }

    public void setProgressCircleBarRadius(int ProgressCircleBarRadius) {
        this.ProgressCircleBarRadius = ProgressCircleBarRadius;
    }


    public int getTotalEngineSeconds() {
        return TotalEngineSeconds;
    }

    public void setTotalEngineSeconds(int TotalEngineSeconds) {
        this.TotalEngineSeconds = TotalEngineSeconds;
    }


    public int getTimelineSlots() {
        return TimelineSlots;
    }

    public void setTimelineSlots(int TimelineSlots) {
        this.TimelineSlots = TimelineSlots;
    }


    public int getMaxNodeDistance() {
        return MaxNodeDistance;
    }

    public void setMaxNodeDistance(int MaxNodeDistance) {
        this.MaxNodeDistance = MaxNodeDistance;
    }


    public double getMaxActivitySkipPercent() {
        return MaxActivitySkipPercent;
    }

    public void setMaxActivitySkipPercent(double ActivitySkipPercent) {
        this.MaxActivitySkipPercent = ActivitySkipPercent;
    }


    public double getTraceChunkSize() {
        return traceChunkSize;
    }

    public void setTraceChunkSize(int traceChunkSize) {
        this.traceChunkSize = traceChunkSize;
    }
    
    public int getMaxNumberOfNodesVisited() {
        return MaxNumberOfNodesVisited;
    }

    public void setMaxNumberOfNodesVisited(int MaxNumberOfNodesVisited) {
        this.MaxNumberOfNodesVisited = MaxNumberOfNodesVisited;
    }    

    public double getActivityMatchCost() {
        return ActivityMatchCost;
    }

    public void setActivityMatchCost(double ActivityMatchCost) {
        this.ActivityMatchCost = ActivityMatchCost;
    }

    public double getEventSkipCost() {
        return EventSkippedCost;
    }

    public void setEventSkipCost(double EventSkippedCost) {
        this.EventSkippedCost = EventSkippedCost;
    }


    public double getActivitySkipCost() {
        return ActivitySkippedCost;
    }

    public void setActivitySkipCost(double ActivitySkippedCost) {
        this.ActivitySkippedCost = ActivitySkippedCost;
    }

    public double getNonActivityMoveCost() {
        return NonActivityMoveCost;
    }

    public void setNonActivityMoveCost(double NonActivityMoveCost) {
        this.NonActivityMoveCost = NonActivityMoveCost;
    }

    public int getMaxConsecutiveUnmatch() {
        return MaxConsecutiveUnmatch;
    }

    public void setMaxConsecutiveUnmatch(int MaxConsecutiveUnmatch) {
        this.MaxConsecutiveUnmatch = MaxConsecutiveUnmatch;
    }

    public double getMaxMatchPercent() {
        return maxHits;
    }

    public void setMaxMatchPercent(double maxHits) {
        this.maxHits = maxHits;
    }


    public double getMinMatchPercent() {
        return minHits;
    }

    public void setMinMatchPercent(double minHits) {
        this.minHits = minHits;
    }


    public double getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(double maxCost) {
        this.maxCost = maxCost;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

}
