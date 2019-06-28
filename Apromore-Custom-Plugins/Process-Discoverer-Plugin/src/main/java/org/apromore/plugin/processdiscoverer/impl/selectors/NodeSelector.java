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

package org.apromore.plugin.processdiscoverer.impl.selectors;

import org.apromore.plugin.processdiscoverer.impl.VisualizationAggregation;
import org.apromore.plugin.processdiscoverer.impl.VisualizationType;
import org.apromore.plugin.processdiscoverer.impl.collectors.Calculator;
import org.apromore.plugin.processdiscoverer.impl.collectors.NodeInfoCollector;
import org.apromore.plugin.processdiscoverer.impl.logprocessors.EventNameAnalyser;
import org.eclipse.collections.api.iterator.MutableIntIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.primitive.IntDoublePair;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntDoubleHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;

import java.util.Comparator;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class NodeSelector {

    private final EventNameAnalyser eventNameAnalyser = new EventNameAnalyser();
    private final int start_int = 1;
    private final int end_int = 2;
    private final boolean contain_start_events;
    private final HashBiMap<String, Integer> simplified_names;

    private final MutableList<IntDoublePair> sorted_activity_frequency;
    private final IntHashSet retained_activities;
    private final Calculator calculator;
    private double threshold = 0.0;
    private double max;
    private double min;
    private boolean inverted;

    public NodeSelector(NodeInfoCollector nodeInfoCollector, double activities, boolean contain_start_events, VisualizationType type, VisualizationAggregation aggregation, boolean inverted) {
        this.inverted = inverted;
        this.contain_start_events = contain_start_events;
        this.simplified_names = nodeInfoCollector.getSimplified_names();

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

        // Remove those events that only are start event without complete events, 
        // or complete events without start events
        if(contain_start_events) {
            MutableIntIterator iterator = retained_activities.intIterator();
            while (iterator.hasNext()) {
                int i = iterator.next();
                String name = getEventFullName(i);
                String name_to_check = "";

                if (eventNameAnalyser.isStartEvent(name)) name_to_check = eventNameAnalyser.getCompleteEvent(name);
                else if (eventNameAnalyser.isCompleteEvent(name)) name_to_check = eventNameAnalyser.getStartEvent(name);

                if (!isSingleTypeEvent(getEventNumber(name)) && !retained_activities.contains(getEventNumber(name_to_check))) {
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

    private String getEventFullName(int event) {
        return simplified_names.inverse().get(event);
    }

    private Integer getEventNumber(String event) {
        return simplified_names.get(event);
    }

    // Return true if there is only "event", no "event+start" or "event+complete"
    // Or return true if the log only contains "event+start" and no "event+complete"
    // Or return true if the log only contains "event+complete" and no "event+start"
    private boolean isSingleTypeEvent(int event) {
        String name = getEventFullName(event);
        if(eventNameAnalyser.isStartEvent(name) && getEventNumber(eventNameAnalyser.getCompleteEvent(name)) != null) return false;
        return !eventNameAnalyser.isCompleteEvent(name) || getEventNumber(eventNameAnalyser.getStartEvent(name)) == null;
    }
}
