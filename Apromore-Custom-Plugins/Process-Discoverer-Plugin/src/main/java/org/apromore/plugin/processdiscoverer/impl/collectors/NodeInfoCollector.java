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

package org.apromore.plugin.processdiscoverer.impl.collectors;

import org.apromore.plugin.processdiscoverer.impl.Arc;
import org.apromore.plugin.processdiscoverer.impl.VisualizationAggregation;
import org.apromore.plugin.processdiscoverer.impl.VisualizationType;
import org.apromore.plugin.processdiscoverer.impl.util.FrequencySetPopulator;
import org.apromore.plugin.processdiscoverer.impl.util.StringValues;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntDoubleHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;

import java.nio.charset.StandardCharsets;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class NodeInfoCollector {

    private final String plus_complete_code = StringValues.b[120];
    private final String plus_start_code = StringValues.b[121];

    private final int number_of_traces;

    private final HashBiMap<String, Integer> simplified_names; //map from activity name to number and vice versa 

    //key: activity number, values: frequency values of the activity in each trace
    private final IntObjectHashMap<LongArrayList> activity_frequency_set; 

    private final ArcInfoCollector arcInfoCollector;

    private final Calculator calculator;

    int trace = -1;

    public NodeInfoCollector(int number_of_traces,
                             HashBiMap<String, Integer> simplified_names,
                             ArcInfoCollector arcInfoCollector) {
        this.number_of_traces = number_of_traces;
        this.simplified_names = simplified_names;

        activity_frequency_set = new IntObjectHashMap<>();

        this.arcInfoCollector = arcInfoCollector;

        this.calculator = new Calculator();
        calculator.method9(Long.toString(System.currentTimeMillis()));
    }

    public HashBiMap<String, Integer> getSimplified_names() {
        return simplified_names;
    }

    public IntDoubleHashMap getActivityFrequencyMap(VisualizationType type, VisualizationAggregation aggregation) {
        IntDoubleHashMap map = new IntDoubleHashMap();
        for(int act : activity_frequency_set.keySet().toArray()) {
            map.put(act, getEventInfo(act, type, aggregation));
        }
        return map;
    }

    // Add up frequency to activity
    public void updateActivityFrequency(int activity, int frequency) {
        LongArrayList list = FrequencySetPopulator.retreiveEntry(activity_frequency_set, activity, number_of_traces);

        calculator.method10(calculator.method5(), list.get(trace), frequency);
        list.set(trace, calculator.method6());
    }

    private double getEventInfo(int event, VisualizationType type, VisualizationAggregation aggregation) {
        if(type == VisualizationType.FREQUENCY) return getNodeFrequency(event, aggregation);
        else return 0;
    }

    private double getNodeFrequency(int event, VisualizationAggregation aggregation) {
        return FrequencySetPopulator.getAggregateInformation(activity_frequency_set.get(event), aggregation);
    }

    public double getNodeFrequency(boolean min, String event, VisualizationAggregation aggregation) {
        if(event.isEmpty()) return 0;
        if(getEventNumber(event) == null) {
            String start_event = event + plus_start_code;
            String complete_event = event + plus_complete_code;
            if(getEventNumber(start_event) != null && getEventNumber(complete_event) != null) {
                if(min) {
                    return Math.min(getNodeFrequency(min, start_event, aggregation), getNodeFrequency(min, complete_event, aggregation));
                }else {
                    return Math.max(getNodeFrequency(min, start_event, aggregation), getNodeFrequency(min, complete_event, aggregation));
                }
            }else if(getEventNumber(start_event) != null) {
                return getNodeFrequency(min, start_event, aggregation);
            }else {
                return getNodeFrequency(min, complete_event, aggregation);
            }
        }else {
            return getNodeFrequency(getEventNumber(event), aggregation);
        }
    }

    public double getNodeDuration(String event, VisualizationAggregation aggregation) {
        if(event.isEmpty()) return 0;
        if(getEventNumber(event) == null) {
            String start_event = event + plus_start_code;
            String complete_event = event + plus_complete_code;
            Integer start_event_number = getEventNumber(start_event);
            Integer complete_event_number = getEventNumber(complete_event);
            if(start_event_number != null && complete_event_number != null) {
                return arcInfoCollector.getArcInfo(new Arc(start_event_number, complete_event_number), VisualizationType.DURATION, aggregation);
            }else return 0;
        }else return 0;
    }

    private Integer getEventNumber(String event) {
        return simplified_names.get(event);
    }

    public void nextTrace() {
        calculator.method10(calculator.method5(), trace, 1);
        trace = (int) calculator.method6();
    }
}
