/*
 * Copyright © 2019 The University of Melbourne.
 *
 * This file is part of "Apromore".
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

package org.apromore.processdiscoverer.dfg.collectors;

import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.Arc;
import org.apromore.processdiscoverer.dfg.LogDFG;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.apromore.processdiscoverer.util.StringValues;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntDoubleHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class NodeInfoCollector {

    private final String plus_complete_code = StringValues.b[120]; //"+complete"
    private final String plus_start_code = StringValues.b[121]; //"+start"

    private final int number_of_traces;

    //private final HashBiMap<String, Integer> simplified_names; //map from activity name to number and vice versa 

    //key: activity number, values: frequency values of the activity in each trace
    private final IntObjectHashMap<LongArrayList> activity_frequency_set; 

    private final ArcInfoCollector arcInfoCollector;

    private final Calculator calculator;

    private int trace = -1; // current trace number
    
    private LogDFG logDfg;
    
    public NodeInfoCollector(LogDFG logDfg, ArcInfoCollector arcInfoCollector) {
    	this.logDfg = logDfg;
        this.number_of_traces = logDfg.getSimplifiedLog().size();

        activity_frequency_set = new IntObjectHashMap<>();

        this.arcInfoCollector = arcInfoCollector;

        this.calculator = new Calculator();
        calculator.setCurrentDate(Long.toString(System.currentTimeMillis()));
    }
    
    public LogDFG getLogDfg() {
    	return this.logDfg;
    }
   

//    public HashBiMap<String, Integer> getSimplified_names() {
//        return simplified_names;
//    }

    // compute aggregate meausure for each activity
    // return a map where key: activity, value: aggregate measure 
    public IntDoubleHashMap getActivityFrequencyMap(VisualizationType type, VisualizationAggregation aggregation) {
        IntDoubleHashMap map = new IntDoubleHashMap();
        for(int act : activity_frequency_set.keySet().toArray()) {
            map.put(act, getEventInfo(act, type, aggregation));
        }
        return map;
    }

    // Add up frequency to activity in the current trace
    // This method must be used in coordination with the nextTrace() method
    public void updateActivityFrequency(int activity, int frequency) {
        LongArrayList list = FrequencySetPopulator.retreiveEntry(activity_frequency_set, activity, number_of_traces);

        calculator.increment(calculator.getCurrentDate(), list.get(trace), frequency);
        list.set(trace, calculator.getCurrent());
    }

    private double getEventInfo(int event, VisualizationType type, VisualizationAggregation aggregation) {
        if(type == VisualizationType.FREQUENCY) return getNodeFrequency(event, aggregation);
        else return 0;
    }

    // compute aggregate frequency for node with event as number
    private double getNodeFrequency(int event, VisualizationAggregation aggregation) {
        return FrequencySetPopulator.getAggregateInformation(activity_frequency_set.get(event), aggregation);
    }

    /**
     * Compute aggregate frequency from a node name
     * @param min: true if use the Min between the start and complete event names
     * @param event: the node name. Note that the node name may not correspond with an 
     * event name because "start" and "complete" might have been removed. Therefore, this method
     * must use the real event names and choose between the "start" and "complete" event names 
     * @param aggregation
     * @return
     */
    public double getNodeFrequency(boolean min, String event, VisualizationAggregation aggregation) {
    	SimplifiedLog log = this.logDfg.getSimplifiedLog();
        if(event.isEmpty()) return 0;
        if(log.getEventNumber(event) == null) {
            String start_event = event + plus_start_code;
            String complete_event = event + plus_complete_code;
            if(log.getEventNumber(start_event) != null && log.getEventNumber(complete_event) != null) {
                if(min) {
                    return Math.min(getNodeFrequency(min, start_event, aggregation), getNodeFrequency(min, complete_event, aggregation));
                }else {
                    return Math.max(getNodeFrequency(min, start_event, aggregation), getNodeFrequency(min, complete_event, aggregation));
                }
            }else if(log.getEventNumber(start_event) != null) {
                return getNodeFrequency(min, start_event, aggregation);
            }else {
                return getNodeFrequency(min, complete_event, aggregation);
            }
        }else {
            return getNodeFrequency(log.getEventNumber(event), aggregation);
        }
    }

    
    /**
     * Compute aggregate time duration for a node with name <event>
     * Assume to have start and complete lifecycle for the same event 
     * @param event
     * @param aggregation
     * @return
     * TODO: this method only takes duration from start and complete events of the
     * same activity which are in a directly-follows relation. It does not perform
     * any more sophisticated matching technique for logs where start and complete
     * events of the same activity might not always directly follow each other.
     */
    public double getNodeDuration(String event, VisualizationAggregation aggregation) {
//        if(event.isEmpty()) return 0;
//        if(this.log.getEventNumber(event) == null) {
//            String start_event = event + plus_start_code;
//            String complete_event = event + plus_complete_code;
//            Integer start_event_number = this.log.getEventNumber(start_event);
//            Integer complete_event_number = this.log.getEventNumber(complete_event);
//            if(start_event_number != null && complete_event_number != null) {
//                return arcInfoCollector.getArcInfo(new Arc(start_event_number, complete_event_number), VisualizationType.DURATION, aggregation);
//            }else return 0;
//        }else return 0;
    	SimplifiedLog log = this.logDfg.getSimplifiedLog();
    	if (log.isEventCollapsedName(event)) {
    		List<Integer> pair = log.getStartCompletePair(event);
    		if (pair != null) {
    			Arc arc = this.logDfg.getArc(pair.get(0), pair.get(1));
    			if (arc != null) {
    				return arcInfoCollector.getArcInfo(arc, VisualizationType.DURATION, aggregation);
    			}
    			else {
    				return 0;
    			}
    		}
    		else {
    			return 0;
    		}
    	}
    	else { 
    		return 0;
    	}
    }

    public void nextTrace() {
        calculator.increment(calculator.getCurrentDate(), trace, 1);
        trace = (int) calculator.getCurrent();
    }
}
