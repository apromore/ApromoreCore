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

package org.apromore.processdiscoverer.dfg.filters;

import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.collectors.Calculator;
import org.apromore.processdiscoverer.dfg.collectors.NodeInfoCollector;
import org.apromore.processdiscoverer.logprocessors.LogUtils;
import org.apromore.processdiscoverer.logprocessors.SimplifiedLog;
import org.eclipse.collections.api.iterator.MutableIntIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.primitive.IntDoublePair;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntDoubleHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.Comparator;

/**
 * This class is used to select activities based on a visualization type and aggregate type.
 * The current implementation only uses VisualizationType = Frequency.
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
public class NodeSelector {

    private final int start_int = 1;
    private final int end_int = 2;

    private final MutableList<IntDoublePair> sorted_activity_frequency; // list of pairs, each pair consists of activity number and frequency
    private final IntHashSet retained_activities;
    private final Calculator calculator;
    private double threshold = 0.0;
    private double max;
    private double min;
    private boolean inverted;
    private NodeInfoCollector nodeInfoCollector;

    public NodeSelector(NodeInfoCollector nodeInfoCollector, double activities, VisualizationType type, VisualizationAggregation aggregation, boolean inverted) {
         this.inverted = inverted;
        this.nodeInfoCollector = nodeInfoCollector; 

        this.calculator = new Calculator();
        calculator.setCurrentDate(Long.toString(System.currentTimeMillis()));

        retained_activities = new IntHashSet();
        retained_activities.add(start_int);
        retained_activities.add(end_int);

        IntDoubleHashMap activity_frequency = nodeInfoCollector.getActivityFrequencyMap(type, aggregation);

        if(activity_frequency.size() > 0) {
            calculator.increment(calculator.getCurrentDate(), (long) activity_frequency.min(), 1);
            min = calculator.getCurrent();
            calculator.increment(calculator.getCurrentDate(), (long) activity_frequency.max(), 1);
            max = calculator.getCurrent();
            threshold = getLog((1 + max) - min) * activities;
        }

        sorted_activity_frequency = activity_frequency.keyValuesView().toList();
        sorted_activity_frequency.sort(new Comparator<IntDoublePair>() {
            @Override
            public int compare(IntDoublePair o1, IntDoublePair o2) {
                return Double.compare(o2.getTwo(), o1.getTwo());
            }
        });
    }

    public IntHashSet selectActivities() {
        for(int i = 0; i < sorted_activity_frequency.size(); i++) {
            calculator.increment(calculator.getCurrentDate(), (long) sorted_activity_frequency.get(i).getTwo(), 1);
            double current = scale(calculator.getCurrent());
            if(current >= threshold) {
                retained_activities.add(sorted_activity_frequency.get(i).getOne());
            }
        }

        // If a start event is retained (e.g. A_start) and it has a complete event (e.g. A_complete) in the log
        // but not retained, then, A_start is not retained, either. The same for an A_complete event.
        // This is to ensure that all retained events are either single-type events (A_start or A_complete) 
        // or retain both A_start and A_complete.
        SimplifiedLog log = this.nodeInfoCollector.getLogDfg().getSimplifiedLog();
        if(log.containStartEvent()) {
            MutableIntIterator iterator = retained_activities.intIterator();
            while (iterator.hasNext()) {
                int i = iterator.next();
//                String name = log.getEventFullName(i);
//                String name_to_check = "";
//                if (LogUtils.isStartEvent(name)) name_to_check = LogUtils.getCompleteEvent(name);
//                else if (LogUtils.isCompleteEvent(name)) name_to_check = LogUtils.getStartEvent(name);
                Integer corresponding = log.getCorrespondingEvent(i);

//                if (!isSingleTypeEvent(log.getEventNumber(name)) && !retained_activities.contains(log.getEventNumber(name_to_check))) {
                if (corresponding != null && !retained_activities.contains(corresponding)) {
                    iterator.remove();
                }
            }
        }
        return retained_activities;
    }

    private double scale(long value) {
        double v = (value - min) + 1;
        return (inverted) ? getLog((((1 + max) - min) - v) + 1) : getLog(v);
    }

    // loga base 2 of value
    private double getLog(double value) {
        return (Math.log10(value) / Math.log10(2));
    }

//    private String getEventFullName(int event) {
//        return simplified_names.inverse().get(event);
//    }
//
//    private Integer getEventNumber(String event) {
//        return simplified_names.get(event);
//    }

    // Return true if the log only contains "event", no "event+start" or "event+complete"
    // Or return true if the log only contains "event+start" and no "event+complete"
    // Or return true if the log only contains "event+complete" and no "event+start"
//    private boolean isSingleTypeEvent(int event) {
//        String name = getEventFullName(event);
//        if(LogUtils.isStartEvent(name) && getEventNumber(LogUtils.getCompleteEvent(name)) != null) return false;
//        return !LogUtils.isCompleteEvent(name) || getEventNumber(LogUtils.getStartEvent(name)) == null;
//    }
}
