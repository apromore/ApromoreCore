package de.hpi.bpmn2_0.animation;

import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.replay.AnimatedSequenceFlow;
import de.hpi.bpmn2_0.replay.AnimationLog;
import de.hpi.bpmn2_0.replay.ReplayParams;
import de.hpi.bpmn2_0.replay.ReplayTrace;
import de.hpi.bpmn2_0.replay.TimeUtilities;
import de.hpi.bpmn2_0.replay.TraceNode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class AnimationJSONBuilder {
    //public final int TOTAL_TIMELINE_SLOTS = 120; 
    //public final int TOTAL_TIMELINE_SECONDS = 120;
    private ArrayList<AnimationLog> animations = null;
    private Interval totalRealInterval = null; //total time interval of all logs
    private ReplayParams params;
    private static final Logger LOGGER = Logger.getLogger(AnimationJSONBuilder.class.getCanonicalName());
    
    public AnimationJSONBuilder(ArrayList<AnimationLog> animations, ReplayParams params) {
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
    
    //Number of data seconds represented by every timeline slot
    protected double getSlotDataUnit() {
        return 1.0*totalRealInterval.toDurationMillis()/(1000*params.getTimelineSlots());
    }
    
    //Number of animation engine seconds represented by every timeline slot
    protected double getSlotEngineUnit() {
        return 1.0*params.getTotalEngineSeconds()/params.getTimelineSlots();
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
        collectionObj.put("tracedates", this.parseTraceStartDates());
        //collectionObj.put("sequenceAnalysis", this.parseSequenceAnalysis());
        return collectionObj;
    }
    
    public JSONObject parseLog(AnimationLog animationLog) throws JSONException {
        JSONObject json = new JSONObject();
        
        json.put("name", animationLog.getName());
        json.put("color", animationLog.getColor());
        json.put("traceCount", animationLog.getTraces().size());
        json.put("moveLogFitness", animationLog.getCostBasedMoveLogFitness());
        json.put("moveModelFitness", animationLog.getCostBasedMoveModelFitness());
        json.put("calculationTime", 1.0*animationLog.getCalculationTime()/1000);
        json.put("progress", this.parseLogProgress(animationLog));
        json.put("tokenAnimations", this.parseTraces(animationLog));

        return json;
    }
    
    /*
    * Timeline represents two values: the real time data frome event log and the time 
    * used for animation. The latter is usually shorter than the former, but in rare cases it can be greater
    * Timeline has a fixed number of slots contained in params.getTimelineSlots()
    * Timeline has a fixed number of seconds to play contained in params.getTimelineSeconds()
    * Timeline starts from the start date to the end date of the log 
    */
    protected JSONObject parseTimeline(Collection<AnimationLog> animations) throws JSONException {
        JSONObject json = new JSONObject();
        
        // log timelines
        JSONArray logTimelines = new JSONArray();
        for(AnimationLog log : this.animations) {
            logTimelines.put(this.parseLogTimeline(log));
        }
        
        //bar labels
        /*
        JSONArray barLabels = new JSONArray();
        Duration dur = new Duration(Double.valueOf(this.getSlotDataUnit()*1000).longValue());
        DateTime slotTime = totalRealInterval.getStart();
        for(int i=1;i<=params.getTimelineSlots()+1;i++) {
            barLabels.put(slotTime.toString());
            slotTime = slotTime.plus(dur);
        }
        */
        
        json.put("logs", logTimelines);
        json.put("startDateLabel", totalRealInterval.getStart().toString());
        json.put("startDateSlot", 0); //datepos is the ordinal number of the time bar on the timeline
        json.put("endDateLabel", totalRealInterval.getEnd().toString());
        json.put("endDateSlot", params.getTimelineSlots());
        json.put("timelineSlots", params.getTimelineSlots());
        json.put("totalEngineSeconds", params.getTotalEngineSeconds());
        json.put("slotEngineUnit", this.getSlotEngineUnit());
        json.put("slotDataUnit", this.getSlotDataUnit());        
        json.put("timeConversionRatio", this.getTimeConversionRatio());
        //json.put("barLabels", barLabels);

        return json;
    }
    
    protected JSONObject parseLogTimeline(AnimationLog log) throws JSONException {
        JSONObject json = new JSONObject();
        
        json.put("startDateLabel", log.getStartDate().toString());
        if (log.getStartDate().isEqual(totalRealInterval.getStart())) {
            json.put("startDatePos", 0);
        } else {
            json.put("startDatePos", Seconds.secondsBetween(totalRealInterval.getStart(), log.getStartDate()).getSeconds()/
                                     this.getSlotDataUnit());
        }
        
        json.put("endDateLabel", log.getEndDate().toString());
        if (log.getEndDate().isEqual(totalRealInterval.getEnd())) {
            json.put("endDatePos", params.getTimelineSlots());
        } else {
            json.put("endDatePos", Seconds.secondsBetween(totalRealInterval.getStart(), totalRealInterval.getEnd()).getSeconds()/
                                     this.getSlotDataUnit());
        }        
        
        json.put("color", log.getColor());
        return json;
    }
    
    //represent all token flows in the log
    protected JSONObject parseTrace(AnimationLog animationLog, ReplayTrace trace) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("caseId", trace.getId());
        
        //------------------------------------------
        // Parse sequence flows
        //------------------------------------------
        JSONArray paths = new JSONArray();
        JSONObject jsonSequence;
        for (SequenceFlow seqFlow : trace.getSequenceFlows()) {
            if (!(seqFlow instanceof AnimatedSequenceFlow)) {
                jsonSequence = this.parseSequenceFlow(animationLog, seqFlow);
                if (jsonSequence != null) {
                    paths.put(jsonSequence);
                }
            }
        }
        json.put("paths", paths);
        
        //------------------------------------------
        // Parse activities
        //------------------------------------------
        JSONArray nodes = new JSONArray();
        JSONObject jsonNode;
        for (TraceNode node : trace.getNodes()) {
            if (node.isActivity()) {
                jsonNode = this.parseNode(animationLog, node);
                if (jsonNode != null) {
                    nodes.put(jsonNode);
                }
            }
        }
        json.put("nodes", nodes);        
        
        return json;
    }    
    
    protected JSONObject parseSequenceFlow(AnimationLog animationLog, SequenceFlow sequenceFlow) throws JSONException {
        
        if (((TraceNode)(sequenceFlow.getSourceRef())).getStart() == null || 
                ((TraceNode)sequenceFlow.getTargetRef()).getStart() == null) {
            return null;
        }
                
        JSONObject json = new JSONObject();
        json.put("id", sequenceFlow.getId());
        
        DateTime logStart = animationLog.getStartDate();
        DateTime start = ((TraceNode)sequenceFlow.getSourceRef()).getComplete();
        DateTime end = ((TraceNode)sequenceFlow.getTargetRef()).getStart();
        double begin = Seconds.secondsBetween(logStart, start).getSeconds()*this.getTimeConversionRatio();
        double duration = Seconds.secondsBetween(start, end).getSeconds()*this.getTimeConversionRatio();
        
        DecimalFormat df = new DecimalFormat("#.#####");        
        json.put("begin", df.format(begin));
        json.put("dur", df.format(duration));

        /*
        if (((TraceNode)sequenceFlow.getSourceRef()).isVirtual()) {
            json.put("sourceIsVirtual", "true");
        } else {
            json.put("sourceIsVirtual", "false");
        }
        */
            

        return json;
    }     
    
    protected JSONObject parseNode(AnimationLog animationLog, TraceNode node) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", node.getId());
        
        DateTime logStart = animationLog.getStartDate();
        DateTime start = node.getStart();
        DateTime end = node.getComplete();
        double begin = Seconds.secondsBetween(logStart, start).getSeconds()*this.getTimeConversionRatio();
        double duration = Seconds.secondsBetween(start, end).getSeconds()*this.getTimeConversionRatio();
        
        DecimalFormat df = new DecimalFormat("#.#####");        
        json.put("begin", df.format(begin));
        json.put("dur", df.format(duration));
        
        if (node.isActivitySkipped()) {
            json.put("isVirtual", "true");
        } else {
            json.put("isVirtual", "false");
        }        
        return json;
    }
    
    
    
    protected JSONArray parseTraces(AnimationLog animationLog) throws JSONException {
        JSONArray jsonTraces = new JSONArray();
        
        for (ReplayTrace trace : animationLog.getTraces()) {
            jsonTraces.put(this.parseTrace(animationLog, trace));
        }
        
        return jsonTraces;
    }

    protected JSONObject parseLogProgress(AnimationLog animationLog) throws JSONException {
        String keyTimes = "";
        String values = "";
        
        
        double begin = Seconds.secondsBetween(totalRealInterval.getStart(), animationLog.getStartDate()).getSeconds()*this.getTimeConversionRatio();
        double duration = params.getTotalEngineSeconds() - begin;        
        
        //-------------------------------------------
        // Calcuate keyTimes and values
        // Go every timeline slot, calculate the slot point of time, then count
        // the number of traces containing that timestamp
        //-------------------------------------------
        DateTime slotTimestamp;
        int traceCount;
        double keyTimeVal;
        double totalVal = 2*Math.PI*params.getProgressCircleBarRadius(); //the perimeter of the progress circle bar
        DecimalFormat df = new DecimalFormat("#.##");
        for (int i=1; i<=params.getTimelineSlots(); i++) {
            keyTimeVal = 1.0*i/params.getTimelineSlots();
            slotTimestamp = totalRealInterval.getStart().plusSeconds(Double.valueOf(i*this.getSlotDataUnit()).intValue());
            traceCount = 0;
            for (ReplayTrace trace : animationLog.getTraces()) {
                if (trace.getInterval().getEnd().isBefore(slotTimestamp) || 
                    trace.getInterval().getEnd().isEqual(slotTimestamp)) {
                    traceCount++;
                }
            }
            if (i==1) {
                keyTimes += "0;"; //required by SVG keytimes specification
                values += "0;"; //required by SVG values specification
            }
            else if (i==params.getTimelineSlots()) {
                keyTimes += "1;"; //required by SVG keyTimes specification
                values += (Math.round((1.0*traceCount/animationLog.getTraces().size())*totalVal) + ";");
            }
            else {
                keyTimes += (df.format(keyTimeVal) + ";");
                values += (Math.round((1.0*traceCount/animationLog.getTraces().size())*totalVal) + ";");
            }
        }
        if (keyTimes.length() > 0) {
            keyTimes = keyTimes.substring(0, keyTimes.length()-1);
        }
        if (values.length() > 0) {
            values = values.substring(0, values.length()-1);
        }
        
        JSONObject json = new JSONObject();
        json.put("keyTimes", keyTimes);
        json.put("values", values);
        
        return json;
    }
    
    protected JSONArray parseTraceStartDates() {
        SortedSet<DateTime> sortedDates = new TreeSet<>(
                                        new Comparator<DateTime>() {
                                            @Override
                                            public int compare(DateTime o1, DateTime o2) {
                                                return o1.compareTo(o2);
                                            }
                                        });         
        for (AnimationLog log : animations) {
            for (ReplayTrace trace : log.getTraces()) {
                sortedDates.add(trace.getStartDate());
            }
        }
        
        JSONArray jsonArray = new JSONArray(); 
        for (DateTime dateTime : sortedDates) {
            jsonArray.put(dateTime.getMillis());
        }
        
        return jsonArray;
    }
    
    protected JSONArray parseSequenceAnalysis() throws JSONException {
        JSONArray jsonSequences = new JSONArray();
        JSONObject jsonObject;
        
        if (this.animations.size() >= 2) {
            Map<String,Map<Interval,Integer>> log1Map = TimeUtilities.aggregateIntervalMap(this.animations.get(0).getIntervalMap());
            Map<String,Map<Interval,Integer>> log2Map = TimeUtilities.aggregateIntervalMap(this.animations.get(1).getIntervalMap());
            Map<Interval,Integer> compare;
            for (String sequenceId : log1Map.keySet()) {
                if (log2Map.containsKey(sequenceId)) {
                    compare = TimeUtilities.compareIntervalMaps(log1Map.get(sequenceId), log2Map.get(sequenceId));
                    for (Interval interval : compare.keySet()) {
                        if (Math.abs(compare.get(interval)) > params.getSequenceTokenDiffThreshold()) {
                            jsonObject = new JSONObject();
                            jsonObject.put("sequenceId", sequenceId);
                            jsonObject.put("from", this.getEngineTime(interval.getStart()));
                            jsonObject.put("to", this.getEngineTime(interval.getEnd()));
                            jsonSequences.put(jsonObject);
                        }
                    }
                }
            }
        }
        
        return jsonSequences;
    }
    
    /*
    * Convert from data time to engine time
    * Use to convert a real datetime value to a "begin" value for animation
    */
    private int getEngineTime(DateTime dataTime) {
        double engineTime = Seconds.secondsBetween(totalRealInterval.getStart(), dataTime).getSeconds()*this.getTimeConversionRatio();
        return Double.valueOf(engineTime).intValue();
    }
    
}