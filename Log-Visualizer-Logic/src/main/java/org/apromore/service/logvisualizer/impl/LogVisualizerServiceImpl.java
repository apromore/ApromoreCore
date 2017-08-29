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
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.block.predicate.primitive.IntPredicate;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
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
import java.util.*;

/**
 * Created by Raffaele Conforti on 01/12/2016.
 */
@Service
public class LogVisualizerServiceImpl extends DefaultParameterAwarePlugin implements LogVisualizerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerServiceImpl.class);

    private XEventClassifier full_classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
//    private XEventClassifier classifier = new XEventAndClassifier(new XEventNameClassifier());

    private HashBiMap<String, Integer> simplified_names;
    private IntIntHashMap activity_frequency;
    private IntIntHashMap real_activity_frequency;
    private MutableList<IntIntPair> sorted_activity_frequency;

    private ObjectIntHashMap<Pair<Integer, Integer>> arcs_frequency;
    private MutableList<ObjectIntPair<Pair<Integer, Integer>>> sorted_arcs_frequency;

    private IntHashSet retained_activities;
    private Set<Pair<Integer, Integer>> retained_arcs;

    private int start = 1;
    private int end = 2;

    public static void main(String[] args) {
        LogVisualizerServiceImpl l = new LogVisualizerServiceImpl();
        XLog log = null;
        try {
//            log = ImportEventLog.importFromFile(new XFactoryNaiveImpl(), "/Volumes/Data/IdeaProjects/ApromoreCodeServerNew/Compare-Logic/src/test/resources/CAUSCONC-1/bpLog3.xes");
            log = LogImporter.importFromFile(new XFactoryNaiveImpl(), "/Volumes/Data/Dropbox/Demonstration examples/Discover Process Model/Synthetic Log with Subprocesses.xes.gz");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONArray s = l.generateJSONArrayFromLog(log, 0.3, 1);
        System.out.println();
    }

    @Override
    public String visualizeLog(XLog log, double activities, double arcs) {
        try {
            BPMNDiagram bpmnDiagram = generateDiagramFromLog(log, activities, arcs);

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
    public JSONArray generateJSONArrayFromLog(XLog log, double activities, double arcs) {
        try {
            BPMNDiagram bpmnDiagram = generateDiagramFromLog(log, activities, arcs);
            return generateJSONFromBPMN(bpmnDiagram);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private BPMNDiagram generateDiagramFromLog(XLog log, double activities, double arcs) {
        initializeDatastructures();
        List<IntList> simplified_log = simplifyLog(log);
        filterLog(simplified_log, activities);
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
            bpmnDiagram.addFlow(source, target, "[" + arcs_frequency.get(arc) + "]");
        }
        return bpmnDiagram;
    }

    private void initializeDatastructures() {
        simplified_names = new HashBiMap<>();
        activity_frequency = new IntIntHashMap();
        real_activity_frequency = new IntIntHashMap();
        arcs_frequency = new ObjectIntHashMap<>();
    }

    private List<IntList> simplifyLog(XLog log) {
        List<IntList> simplified_log = new ArrayList<>();

        simplified_names.put("Start", start);
        simplified_names.put("End", end);

        for(XTrace trace : log) {
            IntArrayList simplified_trace = new IntArrayList(trace.size());

            activity_frequency.addToValue(start, 1);
            simplified_trace.add(start);

            for(XEvent event : trace) {
                String name = full_classifier.getClassIdentity(event);
                Integer simplified_event;
                if((simplified_event = simplified_names.get(name)) == null) {
                    simplified_event = simplified_names.size() + 1;
                    simplified_names.put(name, simplified_event);
                }

                activity_frequency.addToValue(simplified_event, 1);
                real_activity_frequency.addToValue(simplified_event, 1);
                simplified_trace.add(simplified_event);
            }
            activity_frequency.addToValue(end, 1);
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

    private List<IntList> filterLog(List<IntList> log, double activities) {
        List<IntList> filtered_log = new ArrayList<>();
        retained_activities = selectActivities(activities);

        for(IntList trace : log) {
            IntArrayList filtered_trace = (IntArrayList) trace.reject(new IntPredicate() {
                @Override
                public boolean accept(int i) {
                    return !retained_activities.contains(i);
                }
            });

            for(int i = 0; i < filtered_trace.size() - 1; i++) {
                arcs_frequency.addToValue(Tuples.pair(filtered_trace.get(i), filtered_trace.get(i + 1)), 1);
            }

            filtered_log.add(filtered_trace);
        }

        sorted_arcs_frequency = arcs_frequency.keyValuesView().toList();
        sorted_arcs_frequency.sort(new Comparator<ObjectIntPair<Pair<Integer, Integer>>>() {
            @Override
            public int compare(ObjectIntPair<Pair<Integer, Integer>> o1, ObjectIntPair<Pair<Integer, Integer>> o2) {
                return Integer.compare(o2.getTwo(), o1.getTwo());
            }
        });

        return filtered_log;
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
            }else {
                return retained_activities;
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

    private JSONArray generateJSONFromBPMN(BPMNDiagram bpmnDiagram) throws JSONException {
        JSONArray graph = new JSONArray();
        Map<BPMNNode, Integer> mapping = new HashMap<>();
        int i = 1;
        for(BPMNNode node : bpmnDiagram.getNodes()) {
            JSONObject jsonOneNode = new JSONObject();
            mapping.put(node, i);
            jsonOneNode.put("id", i);
            jsonOneNode.put("name", node.getLabel());
            JSONObject jsonDataNode = new JSONObject();
            jsonDataNode.put("data", jsonOneNode);
            graph.put(jsonDataNode);
            i++;
        }

        double maxWeight = 0.0;
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
            String number = edge.getLabel();
            if (number.contains("[")) {
                number = number.substring(1, number.length() - 1);
            } else {
                number = "0";
            }
            maxWeight = Math.max(maxWeight, Double.parseDouble(number));
        }

        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
            int source = mapping.get(edge.getSource());
            int target = mapping.get(edge.getTarget());
            String number = edge.getLabel();
            if(number.contains("[")) {
                number = number.substring(1, number.length() - 1);
            }else {
                number = "0";
            }

            JSONObject jsonOneLink = new JSONObject();
            jsonOneLink.put("source", source);
            jsonOneLink.put("target", target);
            jsonOneLink.put("strength", (Double.parseDouble(number) * 100.0 / maxWeight));
            jsonOneLink.put("label", number);
            JSONObject jsonDataLink = new JSONObject();
            jsonDataLink.put("data", jsonOneLink);
            graph.put(jsonDataLink);
        }

        return graph;
    }

}
