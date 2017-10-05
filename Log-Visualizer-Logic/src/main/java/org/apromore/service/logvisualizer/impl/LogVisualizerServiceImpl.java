/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.service.logvisualizer.impl;

import com.raffaeleconforti.log.util.LogImporter;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.service.logvisualizer.LogVisualizerService;
import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.iterator.MutableIntIterator;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectLongHashMap;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;
import org.eclipse.collections.impl.tuple.Tuples;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.plugins.bpmn.BpmnDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

//import org.jgrapht.ext.*;
//import org.jgrapht.graph.DefaultDirectedGraph;
//import java.io.FileWriter;
//import java.io.IOException;

/**
 * Created by Raffaele Conforti on 01/12/2016.
 */
@Service
public class LogVisualizerServiceImpl extends DefaultParameterAwarePlugin implements LogVisualizerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerServiceImpl.class);

    private XEventClassifier full_classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
    private XEventClassifier name_classifier = new XEventNameClassifier();
    private XEventClassifier lifecycle_classifier = new XEventLifeTransClassifier();
    private XTimeExtension xte = XTimeExtension.instance();

    private DecimalFormat decimalFormat = new DecimalFormat("#.0");
    private boolean contain_start_events = false;

    private HashBiMap<String, Integer> simplified_names;

    private MutableList<IntIntPair> sorted_activity_frequency;
    private IntIntHashMap real_activity_frequency;

    private IntIntHashMap activity_frequency;
    private IntIntHashMap activity_max_frequency;
    private IntIntHashMap activity_min_frequency;

    private ObjectIntHashMap<Pair<Integer, Integer>> arcs_frequency;
    private ObjectIntHashMap<Pair<Integer, Integer>> arcs_max_frequency;
    private ObjectIntHashMap<Pair<Integer, Integer>> arcs_min_frequency;

    private ObjectLongHashMap<Pair<Integer, Integer>> arcs_duration;
    private ObjectLongHashMap<Pair<Integer, Integer>> arcs_max_duration;
    private ObjectLongHashMap<Pair<Integer, Integer>> arcs_min_duration;

    private MutableList<ObjectIntPair<Pair<Integer, Integer>>> sorted_arcs_frequency;

    private IntHashSet retained_activities;
    private Set<Pair<Integer, Integer>> retained_arcs;

    private String start_name = "|>";
    private String end_name = "[]";
    private int start = 1;
    private int end = 2;

    public final static boolean FREQUENCY = true;
    public final static boolean DURATION = false;

    public final static int AVG = 0;
    public final static int MAX = 1;
    public final static int MIN = 2;

    private final String START = "#C1C9B0";
    private final String END = "#C0A3A1";

    private final String BLUE_1 = "#F1EEF6";
    private final String BLUE_2 = "#BDC9E1";
    private final String BLUE_3 = "#74A9CF";
    private final String BLUE_4 = "#2B8CBE";
    private final String BLUE_5 = "#045A8D";

    private final String RED_1 = "#FEF0D9";
    private final String RED_2 = "#FDCC8A";
    private final String RED_3 = "#FC8D59";
    private final String RED_4 = "#E34A33";
    private final String RED_5 = "#B30000";

    private final String EDGE_START_COLOR_FREQUENCY = "#646464";
    private final String EDGE_END_COLOR_FREQUENCY  = "#292929";
    private final String EDGE_START_COLOR_DURATION  = "#646464";
    private final String EDGE_END_COLOR_DURATION  = "#292929";

    public static void main(String[] args) {
        LogVisualizerServiceImpl l = new LogVisualizerServiceImpl();
        XLog log = null;
        try {
//            log = ImportEventLog.importFromFile(new XFactoryNaiveImpl(), "/Volumes/Data/IdeaProjects/ApromoreCodeServerNew/Compare-Logic/src/test/resources/CAUSCONC-1/bpLog3.xes");
            log = LogImporter.importFromFile(new XFactoryNaiveImpl(), "/Volumes/Data/SharedFolder/Logs/Raw data after import.xes.gz");
//            log = LogImporter.importFromFile(new XFactoryNaiveImpl(), "/Volumes/Data/Dropbox/Demonstration examples/Discover Process Model/Synthetic Log with Subprocesses.xes.gz");
//            log = LogImporter.importFromFile(new XFactoryNaiveImpl(), "/Volumes/Data/SharedFolder/Logs/BPI2017 - Loan Application (NoiseFilter).xes.gz");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray s = l.generateJSONArrayFromLog(log, 0.39, 0, DURATION, MIN);
//        JSONArray s1 = l.generateJSONArrayFromLog(log, 0.4, 0, true);
//        System.out.println(s);
//        System.out.println(s1);
//        System.out.println(l.visualizeLog(log, 0.34, 0));
//        l.generateDOTFromLog(log, 0.0, 0.36);
    }

//    public void generateDOTFromLog(XLog log, double activities, double arcs) {
//        try {
//            BPMNDiagram bpmnDiagram = generateDiagramFromLog(log, activities, arcs, FREQUENCY, MIN);
//            generateDOTFromBPMN(bpmnDiagram);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void generateDOTFromBPMN(BPMNDiagram bpmnDiagram) throws JSONException, IOException {
//        IntegerNameProvider integerNameProvider = new IntegerNameProvider();
//        StringNameProvider stringNameProvider = new StringNameProvider();
//        ComponentAttributeProvider vertexAttributeProvider = new ComponentAttributeProvider() {
//            @Override
//            public Map<String, String> getComponentAttributes(Object o) {
//                return new HashMap<>();
//            }
//        };
//        ComponentAttributeProvider edgeAttributeProvider = new ComponentAttributeProvider() {
//            @Override
//            public Map<String, String> getComponentAttributes(Object o) {
//                return new HashMap<>();
//            }
//        };
//        DOTExporter dotExporter = new DOTExporter(integerNameProvider, stringNameProvider, null, vertexAttributeProvider, edgeAttributeProvider);
//        DefaultDirectedGraph graph = new DefaultDirectedGraph(String.class);
//
//        for(BPMNNode node : getNodes(bpmnDiagram)) {
//            graph.addVertex(node.getLabel());
//        }
//
//        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : getEdges(bpmnDiagram)) {
//            graph.addEdge(edge.getSource().getLabel(), edge.getTarget().getLabel(), edge.getSource().getLabel() + " " + edge.getTarget().getLabel());
//        }
//
//        dotExporter.export(new FileWriter("dot.dot"), graph);
//    }

    @Override
    public String visualizeLog(XLog log, double activities, double arcs) {
        try {
            BPMNDiagram bpmnDiagram = generateDiagramFromLog(log, activities, arcs, FREQUENCY, AVG);

            UIContext context = new UIContext();
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIPluginContext uiPluginContext = context.getMainPluginContext();
            BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(uiPluginContext, bpmnDiagram);
            BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);

            String sb = ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                    "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                    "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                    "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                    "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                    "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">") +
                    definitions.exportElements() +
                    "</definitions>";

            return sb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONArray generateJSONArrayFromLog(XLog log, double activities, double arcs, boolean frequency_vs_duration, int avg_vs_min_vs_max) {
        try {
            BPMNDiagram bpmnDiagram = generateDiagramFromLog(log, activities, arcs, frequency_vs_duration, avg_vs_min_vs_max);
            return generateJSONFromBPMN(bpmnDiagram, frequency_vs_duration, avg_vs_min_vs_max);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private BPMNDiagram generateDiagramFromLog(XLog log, double activities, double arcs, boolean frequency_vs_duration, int avg_vs_min_vs_max) {
        initializeDatastructures();
        log = removeUnrequiredEvents(log);
        List<IntList> simplified_log = simplifyLog(log);
        List<LongList> simplified_times_log = simplifyTimesLog(log);
        filterLog(simplified_log, simplified_times_log, activities);
        retained_arcs = selectArcs(arcs);

        BPMNDiagram bpmnDiagram = new BPMNDiagramImpl("");
        IntObjectHashMap<BPMNNode> map = new IntObjectHashMap();
        for(int i : retained_activities.toArray()) {
            BPMNNode node = bpmnDiagram.addActivity(simplified_names.inverse().get(i), false, false, false, false, false);
            map.put(i, node);
        }

        for(Pair<Integer, Integer> arc : retained_arcs) {
            BPMNNode source = map.get(arc.getOne());
            BPMNNode target = map.get(arc.getTwo());
            if(frequency_vs_duration == FREQUENCY) {
                if(avg_vs_min_vs_max == AVG) {
                    bpmnDiagram.addFlow(source, target, "[" + arcs_frequency.get(arc) + "]");
                }else if(avg_vs_min_vs_max == MAX) {
                    bpmnDiagram.addFlow(source, target, "[" + arcs_max_frequency.get(arc) + "]");
                }else {
                    bpmnDiagram.addFlow(source, target, "[" + arcs_min_frequency.get(arc) + "]");
                }
            }else {
                if(avg_vs_min_vs_max == AVG) {
                    bpmnDiagram.addFlow(source, target, "[" + arcs_duration.get(arc) / arcs_frequency.get(arc) + "]");
                }else if(avg_vs_min_vs_max == MAX) {
                    bpmnDiagram.addFlow(source, target, "[" + arcs_max_duration.get(arc) + "]");
                }else {
                    bpmnDiagram.addFlow(source, target, "[" + arcs_min_duration.get(arc) + "]");
                }
            }
        }
        return collapseStartCompleteActivities(bpmnDiagram);
    }

    private BPMNDiagram collapseStartCompleteActivities(BPMNDiagram bpmnDiagram) {
        BPMNDiagram diagram = new BPMNDiagramImpl("");

        Map<String, BPMNNode> nodes_map = new HashMap<>();
        for(BPMNNode node : bpmnDiagram.getNodes()) {
            String collapsed_name = getCollapsedEvent(node.getLabel());
            if(!nodes_map.containsKey(collapsed_name)) {
                BPMNNode collapsed_node = diagram.addActivity(collapsed_name, false, false, false, false, false);
                nodes_map.put(collapsed_name, collapsed_node);
            }
        }

        Set<Pair<String, String>> edges = new HashSet<>();
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
            String source_name = edge.getSource().getLabel();
            String target_name = edge.getTarget().getLabel();

            String collapsed_source_name = getCollapsedEvent(source_name);
            String collapsed_target_name = getCollapsedEvent(target_name);

            BPMNNode source = nodes_map.get(collapsed_source_name);
            BPMNNode target = nodes_map.get(collapsed_target_name);

            Pair pair = Tuples.pair(collapsed_source_name, collapsed_target_name);
            if(!collapsed_source_name.equals(collapsed_target_name) || isSingleTypeEvent(getEventNumber(source_name)) || isCompleteEvent(source_name)) {
                if(!edges.contains(pair)) {
                    diagram.addFlow(source, target, edge.getLabel());
                    edges.add(pair);
                }
            }
        }
        return diagram;
    }

    private void initializeDatastructures() {
        simplified_names = new HashBiMap<>();
        real_activity_frequency = new IntIntHashMap();

        activity_frequency = new IntIntHashMap();
        activity_max_frequency = new IntIntHashMap();
        activity_min_frequency = new IntIntHashMap();

        arcs_frequency = new ObjectIntHashMap<>();
        arcs_max_frequency = new ObjectIntHashMap<>();
        arcs_min_frequency = new ObjectIntHashMap<>();

        arcs_duration = new ObjectLongHashMap<>();
        arcs_max_duration = new ObjectLongHashMap<>();
        arcs_min_duration = new ObjectLongHashMap<>();
    }

    private XLog removeUnrequiredEvents(XLog log) {
        for(XTrace trace : log) {
            Iterator<XEvent> iterator = trace.iterator();
            while (iterator.hasNext()) {
                XEvent event = iterator.next();
                String name = full_classifier.getClassIdentity(event);
                if(name.contains("+") && !(isStartEvent(name) || isCompleteEvent(name))) {
                    iterator.remove();
                }
            }
        }
        return log;
    }

    private List<IntList> simplifyLog(XLog log) {
        List<IntList> simplified_log = new ArrayList<>();

        simplified_names.put(start_name, start);
        simplified_names.put(end_name, end);

        for(XTrace trace : log) {
            IntArrayList simplified_trace = new IntArrayList(trace.size());

            updateActivityFrequency(start, 1);

            simplified_trace.add(start);

            IntIntHashMap eventsCount = new IntIntHashMap();
            for(XEvent event : trace) {
                String name = full_classifier.getClassIdentity(event);
                if(isStartEvent(name)) contain_start_events = true;

                Integer simplified_event;
                if((simplified_event = getEventNumber(name)) == null) {
                    simplified_event = simplified_names.size() + 1;
                    simplified_names.put(name, simplified_event);
                }

                real_activity_frequency.addToValue(simplified_event, 1);

                eventsCount.addToValue(simplified_event, 1);
//                activity_frequency.addToValue(simplified_event, 1);
                simplified_trace.add(simplified_event);
            }

            for(int event : eventsCount.keySet().toArray()) {
                updateActivityFrequency(event, eventsCount.get(event));
            }

            updateActivityFrequency(end, 1);
            simplified_trace.add(end);

            simplified_log.add(simplified_trace);
        }

        sorted_activity_frequency = activity_frequency.keyValuesView().toList();
        sorted_activity_frequency.sort(new Comparator<IntIntPair>() {
            @Override
            public int compare(IntIntPair o1, IntIntPair o2) {
                return Integer.compare(o2.getTwo(), o1.getTwo());
            }
        });

        return simplified_log;
    }

    private void updateActivityFrequency(int activity, int frequency) {
        activity_frequency.addToValue(activity, frequency);

        Integer value = Math.max(frequency, activity_max_frequency.get(activity));
        activity_max_frequency.put(activity, value);

        value = Math.min(frequency, activity_min_frequency.get(activity));
        value = (value == 0 ? frequency : value);
        activity_min_frequency.put(activity, value);
    }

    private List<LongList> simplifyTimesLog(XLog log) {
        List<LongList> simplified_times_log = new ArrayList<>();

        for(XTrace trace : log) {
            LongArrayList simplified_times_trace = new LongArrayList(trace.size());

            for(int i = 0; i < trace.size(); i++) {
                XEvent event = trace.get(i);
                Long time = xte.extractTimestamp(event).getTime();
                if(i == 0 || i == trace.size() - 1) {
                    simplified_times_trace.add(time);
                }
                simplified_times_trace.add(time);
            }

            simplified_times_log.add(simplified_times_trace);
        }

        return simplified_times_log;
    }

    private void filterLog(List<IntList> log, List<LongList> times_log, double activities) {
        retained_activities = selectActivities(activities);

        for(int t = 0; t < log.size(); t++) {
            IntList trace = log.get(t);
            LongList time_trace = times_log.get(t);

            IntArrayList filtered_trace = new IntArrayList();
            LongArrayList filtered_time_trace = new LongArrayList();
            for(int i = 0; i < trace.size(); i++) {
                if(retained_activities.contains(trace.get(i))) {
                    filtered_trace.add(trace.get(i));
                    filtered_time_trace.add(time_trace.get(i));
                }
            }

            IntHashSet not_reached = new IntHashSet();
            IntHashSet not_reaching = new IntHashSet();
            for(int i = 0; i < filtered_trace.size(); i++) {
                if(i != 0) not_reached.add(filtered_trace.get(i));
                if(i != filtered_trace.size() - 1) not_reaching.add(filtered_trace.get(i));
            }

            ObjectIntHashMap<Pair<Integer, Integer>> arcsCount = new ObjectIntHashMap<>();
            for(int i = 0; i < filtered_trace.size() - 1; i++) {
                for(int j = i + 1; j < filtered_trace.size(); j++) {
                    if (isAcceptableTarget(filtered_trace, filtered_trace.get(i), filtered_trace.get(j))) {
                        Pair<Integer, Integer> arc = Tuples.pair(filtered_trace.get(i), filtered_trace.get(j));
                        arcsCount.addToValue(arc, 1);
//                        arcs_frequency.addToValue(arc, 1);
                        updateArcDuration(arc, (filtered_time_trace.get(j) - filtered_time_trace.get(i)));
//                        arcs_duration.addToValue(arc, (filtered_time_trace.get(j) - filtered_time_trace.get(i)));
                        not_reaching.remove(filtered_trace.get(i));
                        not_reached.remove(filtered_trace.get(j));
                        break;
                    }
                }
            }

            if(not_reaching.size() > 0 || not_reached.size() > 0) {
                for (int j = filtered_trace.size() - 1; j > 0; j--) {
                    if(not_reached.contains(filtered_trace.get(j))) {
                        for (int i = j - 1; i >= 0; i--) {
                            if (isAcceptableTarget(filtered_trace, filtered_trace.get(i), filtered_trace.get(j))) {
                                Pair<Integer, Integer> arc = Tuples.pair(filtered_trace.get(i), filtered_trace.get(j));
                                arcsCount.addToValue(arc, 1);
//                                arcs_frequency.addToValue(arc, 1);
                                updateArcDuration(arc, (filtered_time_trace.get(j) - filtered_time_trace.get(i)));
//                                arcs_duration.addToValue(arc, (filtered_time_trace.get(j) - filtered_time_trace.get(i)));
                                not_reaching.remove(filtered_trace.get(i));
                                not_reached.remove(filtered_trace.get(j));
                                break;
                            }
                        }
                    }
                }
                for (int j = filtered_trace.size() - 1; j > 0; j--) {
                    for (int i = j - 1; i >= 0; i--) {
                        if (not_reaching.contains(filtered_trace.get(i)) && isAcceptableTarget(filtered_trace, filtered_trace.get(i), filtered_trace.get(j))) {
                            Pair<Integer, Integer> arc = Tuples.pair(filtered_trace.get(i), filtered_trace.get(j));
                            arcsCount.addToValue(arc, 1);
//                            arcs_frequency.addToValue(arc, 1);
                            updateArcDuration(arc, (filtered_time_trace.get(j) - filtered_time_trace.get(i)));
//                            arcs_duration.addToValue(arc, (filtered_time_trace.get(j) - filtered_time_trace.get(i)));
                            not_reaching.remove(filtered_trace.get(i));
                            not_reached.remove(filtered_trace.get(j));
                            break;
                        }
                    }
                }
            }

            for(Pair<Integer, Integer> arc : arcsCount.keySet().toArray(new Pair[arcsCount.size()])) {
                updateArcFrequency(arc, arcsCount.get(arc));
            }
        }

        sorted_arcs_frequency = arcs_frequency.keyValuesView().toList();
        sorted_arcs_frequency.sort(new Comparator<ObjectIntPair<Pair<Integer, Integer>>>() {
            @Override
            public int compare(ObjectIntPair<Pair<Integer, Integer>> o1, ObjectIntPair<Pair<Integer, Integer>> o2) {
                return Integer.compare(o2.getTwo(), o1.getTwo());
            }
        });
    }

    private void updateArcFrequency(Pair<Integer, Integer> arc, int frequency) {
        arcs_frequency.addToValue(arc, frequency);

        Integer value = Math.max(frequency, arcs_max_frequency.get(arc));
        arcs_max_frequency.put(arc, value);

        value = Math.min(frequency, arcs_min_frequency.get(arc));
        value = (value == 0 ? frequency : value);
        arcs_min_frequency.put(arc, value);
    }

    private void updateArcDuration(Pair<Integer, Integer> arc, long duration) {
        arcs_duration.addToValue(arc, duration);

        Long value = Math.max(duration, arcs_max_duration.get(arc));
        arcs_max_duration.put(arc, value);

        value = Math.min(duration, arcs_min_duration.get(arc));
        value = (value == 0 ? duration: value);
        arcs_min_duration.put(arc, value);
    }

    private boolean isAcceptableTarget(IntArrayList trace, int source_event, int target_event) {
        if(source_event == 1 && target_event == 2) return false;

        String source_name = getEventFullName(source_event);
        String target_name = getEventFullName(target_event);

        if(source_event == 1) return (isStartEvent(target_name) || isSingleTypeEvent(target_event) || isSingleTypeEvent(trace, target_event));
        if(target_event == 2) return (isCompleteEvent(source_name) || isSingleTypeEvent(source_event) || isSingleTypeEvent(trace, source_event));

        if(isStartEvent(source_name)) {
            String expected_target_name = getCompleteEvent(source_name);
            if (!isSingleTypeEvent(source_event) && !isSingleTypeEvent(trace, source_event)) {
                return getEventNumber(expected_target_name) == target_event;
            }else {
                return isStartEvent(target_name) || isSingleTypeEvent(target_event) || isSingleTypeEvent(trace, target_event);
            }
        }else if(isCompleteEvent(source_name)) {
            return (isStartEvent(target_name) || isSingleTypeEvent(target_event) || isSingleTypeEvent(trace, target_event));
        }
        return false;
    }

    private IntHashSet selectActivities(double activities) {
        IntHashSet retained_activities = new IntHashSet();
        retained_activities.add(start);
        retained_activities.add(end);

        long max = real_activity_frequency.max();
        for(int i = 0; i < sorted_activity_frequency.size(); i++) {
            double current = sorted_activity_frequency.get(i).getTwo();
            if(current >= max * activities) {
                retained_activities.add(sorted_activity_frequency.get(i).getOne());
//            }else {
//                return retained_activities;
            }
        }

        if(contain_start_events) {
            MutableIntIterator iterator = retained_activities.intIterator();
            while (iterator.hasNext()) {
                int i = iterator.next();
                String name = getEventFullName(i);
                String name_to_check = "";

                if (isStartEvent(name)) name_to_check = getCompleteEvent(name);
                else if (isCompleteEvent(name)) name_to_check = getStartEvent(name);

                if (!isSingleTypeEvent(getEventNumber(name)) && !retained_activities.contains(getEventNumber(name_to_check))) {
                    iterator.remove();
                }
            }
        }
        return retained_activities;
    }

    private HashSet<Pair<Integer, Integer>> selectArcs(double arcs) {
        HashSet<Pair<Integer, Integer>> retained_arcs = new HashSet();

        double threshold = arcs_frequency.max() * arcs;

        retained_arcs.addAll(arcs_frequency.keySet());

        for(int i = sorted_arcs_frequency.size() - 1; i >= 0; i--) {
            double current = sorted_arcs_frequency.get(i).getTwo();
            Pair<Integer, Integer> arc = sorted_arcs_frequency.get(i).getOne();
            if(current < threshold) {
                if(retained_arcs.contains(arc)) {
                    retained_arcs.remove(arc);
                    if (!reachable(arc.getTwo(), retained_arcs) || !reaching(arc.getOne(), retained_arcs)) {
                        retained_arcs.add(arc);
                    }
                }
            }else {
                return retained_arcs;
            }
        }

        return retained_arcs;
    }

    private boolean reachable(int node, HashSet<Pair<Integer, Integer>> retained_arcs) {
        if(node == 1) return true;

        IntHashSet visited = new IntHashSet();
        IntArrayList reached = new IntArrayList();
        reached.add(1);

        while (reached.size() > 0) {
            int current = reached.removeAtIndex(0);
            for (Pair<Integer, Integer> arc : retained_arcs) {
                if(arc.getOne() == current) {
                    if(arc.getTwo() == node) {
                        return true;
                    }else if(!visited.contains(arc.getTwo())) {
                        visited.add(arc.getTwo());
                        reached.add(arc.getTwo());
                    }
                }
            }
        }

        return false;
    }

    private boolean reaching(int node, HashSet<Pair<Integer, Integer>> retained_arcs) {
        if(node == 2) return true;

        IntHashSet visited = new IntHashSet();
        IntArrayList reached = new IntArrayList();
        reached.add(2);

        while (reached.size() > 0) {
            int current = reached.removeAtIndex(0);
            for (Pair<Integer, Integer> arc : retained_arcs) {
                if(arc.getTwo() == current) {
                    if(arc.getOne() == node) {
                        return true;
                    }else if(!visited.contains(arc.getOne())) {
                        visited.add(arc.getOne());
                        reached.add(arc.getOne());
                    }
                }
            }
        }

        return false;
    }

    private JSONArray generateJSONFromBPMN(BPMNDiagram bpmnDiagram, boolean frequency_vs_duration, int avg_vs_max_vs_min) throws JSONException {
        JSONArray graph = new JSONArray();
        Map<BPMNNode, Integer> mapping = new HashMap<>();
        int i = 1;
        int start_node = -1;
        int end_node = -1;

        for(BPMNNode node : getNodes(bpmnDiagram)) {
            JSONObject jsonOneNode = new JSONObject();
            mapping.put(node, i);
            jsonOneNode.put("id", i);
            if(node.getLabel().equals(start_name)) {
                start_node = i;
                jsonOneNode.put("name", "");
                jsonOneNode.put("shape", "ellipse");
                jsonOneNode.put("color", START);
                jsonOneNode.put("width", "15px");
                jsonOneNode.put("height", "15px");
            }else if(node.getLabel().equals(end_name)) {
                end_node = i;
                jsonOneNode.put("name", "");
                jsonOneNode.put("shape", "ellipse");
                jsonOneNode.put("color", END);
                jsonOneNode.put("width", "15px");
                jsonOneNode.put("height", "15px");
            }else {
                if(frequency_vs_duration == FREQUENCY) jsonOneNode.put("name", node.getLabel().replace("'", "") + "\\n" + getEventFrequency(false, avg_vs_max_vs_min, node.getLabel()));
                else jsonOneNode.put("name", node.getLabel().replace("'", "") + "\\n" + convertMilliseconds("" + getEventDuration(avg_vs_max_vs_min, node.getLabel())));

                jsonOneNode.put("shape", "roundrectangle");

                if(frequency_vs_duration == FREQUENCY) jsonOneNode.put("color", getFrequencyColor(avg_vs_max_vs_min, node, bpmnDiagram.getNodes()));
                else jsonOneNode.put("color", getDurationColor(avg_vs_max_vs_min, node, bpmnDiagram.getNodes()));
                jsonOneNode.put("width", "60px");
                jsonOneNode.put("height", "20px");
            }
            JSONObject jsonDataNode = new JSONObject();
            jsonDataNode.put("data", jsonOneNode);
            graph.put(jsonDataNode);
            i++;
        }

        double maxWeight = 0.0;
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : getEdges(bpmnDiagram)) {
            String number = edge.getLabel();
            if (number.contains("[")) {
                number = number.substring(1, number.length() - 1);
            } else {
                number = "0";
            }
            maxWeight = Math.max(maxWeight, Double.parseDouble(number));
        }

        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : getEdges(bpmnDiagram)) {
            int source = mapping.get(edge.getSource());
            int target = mapping.get(edge.getTarget());
            String number = edge.getLabel();
            if(number.contains("[")) {
                number = number.substring(1, number.length() - 1);
            }else {
                number = "1";
            }

            JSONObject jsonOneLink = new JSONObject();
            jsonOneLink.put("source", source);
            jsonOneLink.put("target", target);

            if(source == start_node) jsonOneLink.put("style", "dashed");
            else if(target == end_node) jsonOneLink.put("style", "dashed");
            else jsonOneLink.put("style", "solid");

            BigDecimal bd = new BigDecimal((Double.parseDouble(number) * 100.0 / maxWeight));
            bd = bd.setScale(2, RoundingMode.HALF_UP);

            if (frequency_vs_duration == DURATION && (source == start_node || target == end_node)) {
                jsonOneLink.put("strength", 0);
                jsonOneLink.put("label", "");
            }else {
                jsonOneLink.put("strength", bd.doubleValue());
                if(frequency_vs_duration == FREQUENCY) {
                    jsonOneLink.put("label", number);
                }else {
                    jsonOneLink.put("label", convertMilliseconds(number));
                }
            }

            JSONObject jsonDataLink = new JSONObject();
            jsonDataLink.put("data", jsonOneLink);
            graph.put(jsonDataLink);
        }

        return graph;
    }

    private String convertMilliseconds(String number) {
        Double milliseconds = Double.parseDouble(number);
        Double seconds = milliseconds / 1000.0;
        Double minutes = seconds / 60.0;
        Double hours = minutes / 60.0;
        Double days = hours / 24.0;
        Double weeks = days / 7.0;
        Double months = days / 30.0;
        Double years = days / 365.0;

        if(years > 1) {
            return decimalFormat.format(years) + " yrs";
        }else if(months > 1) {
            return decimalFormat.format(months) + " mths";
        }else if(weeks > 1) {
            return decimalFormat.format(weeks) + " wks";
        }else if(days > 1) {
            return decimalFormat.format(days) + " d";
        }else if(hours > 1) {
            return decimalFormat.format(hours) + " hrs";
        }else if(minutes > 1) {
            return decimalFormat.format(minutes) + " mins";
        }else if(seconds > 1) {
            return decimalFormat.format(seconds) + " secs";
        }else if(milliseconds > 1){
            return decimalFormat.format(milliseconds) + " millis";
        }else {
            return "instant";
        }
    }

    private BPMNNode[] getNodes(BPMNDiagram bpmnDiagram) {
        Set<BPMNNode> nodes = bpmnDiagram.getNodes();
        BPMNNode[] array_nodes = nodes.toArray(new BPMNNode[nodes.size()]);
        Arrays.sort(array_nodes, new Comparator<BPMNNode>() {
            @Override
            public int compare(BPMNNode o1, BPMNNode o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }
        });
        return array_nodes;
    }

    private BPMNEdge<? extends BPMNNode, ? extends BPMNNode>[] getEdges(BPMNDiagram bpmnDiagram) {
        Set<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>> edges = bpmnDiagram.getEdges();
        BPMNEdge<? extends BPMNNode, ? extends BPMNNode>[] array_edges = edges.toArray(new BPMNEdge[edges.size()]);
        Arrays.sort(array_edges, new Comparator<BPMNEdge<? extends BPMNNode, ? extends BPMNNode>>() {
            @Override
            public int compare(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> o1, BPMNEdge<? extends BPMNNode, ? extends BPMNNode> o2) {
                if(o1.getSource().getLabel().equals(o2.getSource().getLabel())) {
                    return o1.getTarget().getLabel().compareTo(o2.getTarget().getLabel());
                }
                return o1.getSource().getLabel().compareTo(o2.getSource().getLabel());
            }
        });
        return array_edges;
    }

    private int getEventFrequency(boolean min, int avg_vs_max_vs_min, String event) {
        if(getEventNumber(event) == null) {
            String start_event = event + "+start";
            String complete_event = event + "+complete";
            if(getEventNumber(start_event) != null && getEventNumber(complete_event) != null) {
                if(min) {
                    return Math.min(getEventFrequency(min, avg_vs_max_vs_min, start_event), getEventFrequency(min, avg_vs_max_vs_min, complete_event));
                }else {
                    return Math.max(getEventFrequency(min, avg_vs_max_vs_min, start_event), getEventFrequency(min, avg_vs_max_vs_min, complete_event));
                }
            }else if(getEventNumber(start_event) != null) {
                return getEventFrequency(min, avg_vs_max_vs_min, start_event);
            }else {
                return getEventFrequency(min, avg_vs_max_vs_min, complete_event);
            }
        }else {
            if(avg_vs_max_vs_min == AVG) return activity_frequency.get(getEventNumber(event));
            else if(avg_vs_max_vs_min == MAX) return activity_max_frequency.get(getEventNumber(event));
            else return activity_min_frequency.get(getEventNumber(event));
        }
    }

    private long getEventDuration(int avg_vs_max_vs_min, String event) {
        if(getEventNumber(event) == null) {
            String start_event = event + "+start";
            String complete_event = event + "+complete";
            Integer start_event_number = getEventNumber(start_event);
            Integer complete_event_number = getEventNumber(complete_event);
            if(start_event_number != null && complete_event_number != null) {
                if(avg_vs_max_vs_min == AVG) return arcs_duration.get(Tuples.pair(start_event_number, complete_event_number));
                else if(avg_vs_max_vs_min == MAX) return arcs_max_duration.get(Tuples.pair(start_event_number, complete_event_number));
                else return arcs_min_duration.get(Tuples.pair(start_event_number, complete_event_number));
            }else return 0;
        }else return 0;
    }

    private String getFrequencyColor(int avg_vs_max_vs_min, BPMNNode node, Set<BPMNNode> nodes) {
        int max = 0;
        for(BPMNNode n : nodes) {
            max = Math.max(max, getEventFrequency(true, avg_vs_max_vs_min, n.getLabel()));
        }
        int step = max / 5;
        int node_frequency = getEventFrequency(true, avg_vs_max_vs_min, node.getLabel());
        if(node_frequency >= (max - (1 * step))) return BLUE_5;
        if(node_frequency >= (max - (2 * step))) return BLUE_4;
        if(node_frequency >= (max - (3 * step))) return BLUE_3;
        if(node_frequency >= (max - (4 * step))) return BLUE_2;
        return BLUE_1;
    }

    private String getDurationColor(int avg_vs_max_vs_min, BPMNNode node, Set<BPMNNode> nodes) {
        long max = 0;
        for(BPMNNode n : nodes) {
            max = Math.max(max, getEventDuration(avg_vs_max_vs_min, n.getLabel()));
        }
        long step = max / 5;
        long node_diration = getEventDuration(avg_vs_max_vs_min, node.getLabel());
        if(node_diration == 0) return RED_1;
        if(node_diration >= (max - (1 * step))) return RED_5;
        if(node_diration >= (max - (2 * step))) return RED_4;
        if(node_diration >= (max - (3 * step))) return RED_3;
        if(node_diration >= (max - (4 * step))) return RED_2;
        return RED_1;
    }

    private String getEventFullName(int event) {
        return simplified_names.inverse().get(event);
    }

    private Integer getEventNumber(String event) {
        return simplified_names.get(event);
    }
    
    private boolean isSingleTypeEvent(int event) {
        String name = getEventFullName(event);
        if(isStartEvent(name) && getEventNumber(getCompleteEvent(name)) != null) return false;
        if(isCompleteEvent(name) && getEventNumber(getStartEvent(name)) != null) return false;
        return true;
    }

    private boolean isSingleTypeEvent(IntArrayList trace, int event) {
        String name = getEventFullName(event);
        String collapsed_name = getCollapsedEvent(name);

        for(int i = 0; i < trace.size(); i++) {
            if(collapsed_name.equals(getCollapsedEvent(getEventFullName(trace.get(i)))) && !name.equals(getEventFullName(trace.get(i)))) {
                return false;
            }
        }

        return true;
    }

    private boolean isStartEvent(String name) {
        return name.endsWith("+start");
    }

    private boolean isCompleteEvent(String name) {
        return name.endsWith("+complete");
    }

    private String getStartEvent(String name) {
        return name.substring(0, name.length() - 8) + "start";
    }

    private String getCompleteEvent(String name) {
        return name.substring(0, name.length() - 5) + "complete";
    }

    private String getCollapsedEvent(String name) {
        if(isStartEvent(name)) return name.substring(0, name.length() - 6);
        if(isCompleteEvent(name)) return name.substring(0, name.length() - 9);
        return name;
    }

}
