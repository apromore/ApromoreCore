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

package org.apromore.service.loganimation.json;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apromore.service.loganimation.replay.AnimationLog;
import org.apromore.service.loganimation.replay.ReplayParams;
import org.apromore.service.loganimation.replay.TimeUtilities;
import org.apromore.service.loganimation.replay.TraceNode;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;

/*
* Animation timing is shown via the timeline bar which embeds two timing aspects:
* the real time in event log file and the internal clock of the animation engine (SVG).
* Thus, there is a conversion ratio between the event log time and the animation engine time
* called TimeConversionRatio, meaning how many seconds of the engine is equivalent to
* one second of data time. This ratio is used to convert the real data time to
* the animation engine time.
* The timeline has a number of even slots. Each slot represents a duration of time
* in the event log (SlotDataUnit) and a duration of time of animation engine (SlotEngineUnit)
* If the event log spans a long duration, the timeline slot will have large SlotDataUnit value,
* the number of slots on the timeline should also be increased. If the event log has short duration,
* the timeline slot will have small SlotDataUnit value.
* The SlotEngineUnit can be used to set the speed of the tick movement on the timeline bar
* The SlotDataUnit can be used to show the location of a specific event date on the timeline bar
*/
public class AnimationJSONBuilder2 {
    private List<AnimationLog> animations = null;
    private Interval totalRealInterval = null; //total time interval of all logs
    private ReplayParams params;
    
    public AnimationJSONBuilder2(List<AnimationLog> animations, Definitions diagram, ReplayParams params) {
        this.animations = animations;
        this.params = params;
        
        Set<DateTime> dateSet = new HashSet<>();
        for (AnimationLog animationLog : animations) {
            dateSet.add(animationLog.getStartDate());
            dateSet.add(animationLog.getEndDate());
        }
        SortedSet<DateTime> sortedDates = TimeUtilities.sortDates(dateSet);
        totalRealInterval = new Interval(sortedDates.first(), sortedDates.last());
    }
    
    //Proportion between animation and data time
    protected double getTimeConversionRatio() {
        return 1.0*params.getTotalEngineSeconds()/(totalRealInterval.toDurationMillis()/1000);
    }
    
    public JSONObject parseLogCollection() throws JSONException {
        JSONObject collectionObj = new JSONObject();
        
        JSONArray logs = new JSONArray();
        for(AnimationLog log : this.animations) {
            logs.put(this.parseLog(log));
        }
        
        collectionObj.put("logs", logs);
        collectionObj.put("timeline", this.parseTimeline(animations));
        return collectionObj;
    }
    
    public JSONObject parseLog(AnimationLog animationLog) throws JSONException {
        DecimalFormat df = new DecimalFormat("#.###");
        JSONObject json = new JSONObject();
        
        json.put("name", animationLog.getName());
        json.put("filename", animationLog.getFileName());
        json.put("color", animationLog.getColor());
        json.put("total", animationLog.getTraces().size() + animationLog.getUnplayTraces().size());
        json.put("play", animationLog.getTraces().size());
        json.put("startDateLabel", animationLog.getStartDate().toString());
        json.put("endDateLabel", animationLog.getEndDate().toString());
        json.put("startLogDateLabel", animationLog.getStartDate().plus(((long)params.getStartEventToFirstEventDuration())*1000));
        json.put("endLogDateLabel", animationLog.getEndDate().minus(((long)params.getLastEventToEndEventDuration())*1000));
        json.put("unplayTraces", animationLog.getUnplayTracesString());
        json.put("reliable", animationLog.getReliableTraceCount());
        json.put("unreliableTraces", animationLog.getUnReliableTraceIDs());
        json.put("exactTraceFitness", !params.isExactTraceFitnessCalculation() ? 0 : df.format(animationLog.getTraceFitness(animationLog.getMinBoundMoveOnModel())));
        
        return json;
    }
    
    protected JSONObject parseTimeline(Collection<AnimationLog> animations) throws JSONException {
        JSONObject json = new JSONObject();
        
        json.put("startDateLabel", totalRealInterval.getStart().toString());
        json.put("endDateLabel", totalRealInterval.getEnd().toString());
        json.put("startLogDateLabel", totalRealInterval.getStart().plus(((long)params.getStartEventToFirstEventDuration())*1000));
        json.put("endLogDateLabel", totalRealInterval.getEnd().minus(((long)params.getLastEventToEndEventDuration())*1000));
        json.put("timelineSlots", params.getTimelineSlots());
        json.put("totalEngineSeconds", params.getTotalEngineSeconds());
        json.put("timezone", ZoneId.systemDefault().toString());

        return json;
    }
    
    protected JSONObject parseSequenceFlow(AnimationLog animationLog, SequenceFlow sequenceFlow) throws JSONException {
        
        if (((TraceNode)(sequenceFlow.getSourceRef())).getStart() == null ||
                ((TraceNode)sequenceFlow.getTargetRef()).getStart() == null) {
            return null;
        }
                
        JSONObject json = new JSONObject();
        json.put("id", sequenceFlow.getId());
        
        //DateTime logStart = animationLog.getStartDate();
        DateTime logStart = totalRealInterval.getStart(); //Bruce 15.06.2015: fix bug of playing multiple logs with different start dates
        DateTime start = ((TraceNode)sequenceFlow.getSourceRef()).getComplete();
        DateTime end = ((TraceNode)sequenceFlow.getTargetRef()).getStart();
        double begin = Seconds.secondsBetween(logStart, start).getSeconds()*this.getTimeConversionRatio();
        //double duration = Seconds.secondsBetween(start, end).getSeconds()*this.getTimeConversionRatio();
        double duration = (end.getMillis() - start.getMillis())*this.getTimeConversionRatio(); // in milliseconds
        
        DecimalFormat df = new DecimalFormat("#.#####");
        json.put("begin", df.format(begin));
        json.put("dur", df.format(1.0*duration/1000));

        return json;
    }
    
    protected JSONObject parseNode(AnimationLog animationLog, TraceNode node) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", node.getId());
        
        //DateTime logStart = animationLog.getStartDate();
        DateTime logStart = totalRealInterval.getStart(); //Bruce 15.06.2015: fix bug of multiple logs with different start dates
        DateTime start = node.getStart();
        DateTime end = node.getComplete();
        double begin = Seconds.secondsBetween(logStart, start).getSeconds()*this.getTimeConversionRatio();
        //double duration = Seconds.secondsBetween(start, end).getSeconds()*this.getTimeConversionRatio();
        double duration = (end.getMillis() - start.getMillis())*this.getTimeConversionRatio(); // in milliseconds
        
        DecimalFormat df = new DecimalFormat("#.#####");
        json.put("begin", df.format(begin));
        json.put("dur", df.format(1.0*duration/1000));
        
        if (node.isActivitySkipped()) {
            json.put("isVirtual", "true");
        } else {
            json.put("isVirtual", "false");
        }
        return json;
    }
    
    public void clear() {
        params = null;
        for (AnimationLog animLog : animations) {
            animLog.clear();
        }
        animations.clear();
        animations = null;
    }
    
}
