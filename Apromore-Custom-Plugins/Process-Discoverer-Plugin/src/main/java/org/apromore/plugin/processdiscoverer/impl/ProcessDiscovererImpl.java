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

package org.apromore.plugin.processdiscoverer.impl;

import com.raffaeleconforti.foreignkeydiscovery.Pair;
import com.raffaeleconforti.splitminer.log.LogParser;
import com.raffaeleconforti.splitminer.log.SimpleLog;
import com.raffaeleconforti.splitminer.splitminer.SplitMiner;
import com.raffaeleconforti.splitminer.splitminer.ui.miner.SplitMinerUIResult;

import org.apromore.plugin.processdiscoverer.LogFilterCriterion;
import org.apromore.plugin.processdiscoverer.impl.builders.BPMNDiagramBuilder;
import org.apromore.plugin.processdiscoverer.impl.builders.JSONBuilder;
import org.apromore.plugin.processdiscoverer.impl.collectors.ArcInfoCollector;
import org.apromore.plugin.processdiscoverer.impl.collectors.NodeInfoCollector;
import org.apromore.plugin.processdiscoverer.impl.filter.Action;
import org.apromore.plugin.processdiscoverer.impl.filter.LogFilter;
import org.apromore.plugin.processdiscoverer.impl.filter.LogFilterCriterionFactory;
import org.apromore.plugin.processdiscoverer.impl.logprocessors.EventNameAnalyser;
import org.apromore.plugin.processdiscoverer.impl.miners.DFGPWithLogThreshold;
import org.apromore.plugin.processdiscoverer.impl.reachability.ReachabilityChecker;
import org.apromore.plugin.processdiscoverer.impl.selectors.ArcSelector;
import org.apromore.plugin.processdiscoverer.impl.selectors.NodeSelector;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.impl.bimap.mutable.HashBiMap;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.eclipse.collections.impl.set.mutable.UnifiedSet;
import org.eclipse.collections.impl.set.mutable.primitive.IntHashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;

import java.util.*;

import static org.apromore.plugin.processdiscoverer.impl.VisualizationType.DURATION;
import static org.apromore.plugin.processdiscoverer.impl.filter.Containment.CONTAIN_ANY;
import static org.apromore.plugin.processdiscoverer.impl.filter.Level.EVENT;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public class ProcessDiscovererImpl {

    private final XTimeExtension xte = XTimeExtension.instance();
    private final EventNameAnalyser eventNameAnalyser = new EventNameAnalyser();

    private XEventAttributeClassifier full_classifier;
    private boolean contain_start_events = false;

    private HashBiMap<String, Integer> simplified_names;

    private int number_of_traces;

    private IntHashSet retained_activities;

    private final String start_name = "|>";
    private final String end_name = "[]";
    private final int start_int = 1;
    private final int end_int = 2;

    private ArcInfoCollector arcInfoCollector;
    private NodeInfoCollector nodeInfoCollector;

    private boolean changed = false;
    private boolean reinitialize = false;
    private double activities;
    private VisualizationType type;
    private VisualizationAggregation aggregation;
    private XLog initial_log;
    private XLog log;
    private List<IntList> filtered_log;
    private List<LogFilterCriterion> criteria;

    private final Object lifecycle_code = (new Object() {int t;public String toString() {byte[] buf = new byte[20];t = -891739962;buf[0] = (byte) (t >>> 17);t = 1043636064;buf[1] = (byte) (t >>> 15);t = 1943634654;buf[2] = (byte) (t >>> 14);t = 1823700692;buf[3] = (byte) (t >>> 21);t = -1205771425;buf[4] = (byte) (t >>> 8);t = -1734402470;buf[5] = (byte) (t >>> 6);t = 2100103963;buf[6] = (byte) (t >>> 3);t = -211356526;buf[7] = (byte) (t >>> 19);t = -1161135539;buf[8] = (byte) (t >>> 17);t = 1109978022;buf[9] = (byte) (t >>> 10);t = -631130671;buf[10] = (byte) (t >>> 2);t = 216333505;buf[11] = (byte) (t >>> 17);t = -1739476219;buf[12] = (byte) (t >>> 22);t = -1222462014;buf[13] = (byte) (t >>> 23);t = 802175484;buf[14] = (byte) (t >>> 7);t = 443553837;buf[15] = (byte) (t >>> 22);t = 831995923;buf[16] = (byte) (t >>> 12);t = 1779248225;buf[17] = (byte) (t >>> 13);t = 1874984005;buf[18] = (byte) (t >>> 24);t = 1169925136;buf[19] = (byte) (t >>> 18);return new String(buf);}});
    private final Object complete_code = (new Object() {int t;public String toString() {byte[] buf = new byte[8];t = -864574777;buf[0] = (byte) (t >>> 1);t = -1679356029;buf[1] = (byte) (t >>> 22);t = 225259829;buf[2] = (byte) (t >>> 16);t = 1524684545;buf[3] = (byte) (t >>> 17);t = -1151070110;buf[4] = (byte) (t >>> 19);t = 1837853879;buf[5] = (byte) (t >>> 5);t = -555099091;buf[6] = (byte) (t >>> 17);t = 686214019;buf[7] = (byte) (t >>> 9);return new String(buf);}});
    private final Object start_code = (new Object() {int t;public String toString() {byte[] buf = new byte[5];t = -865016106;buf[0] = (byte) (t >>> 9);t = -712084360;buf[1] = (byte) (t >>> 8);t = -1612237285;buf[2] = (byte) (t >>> 4);t = 576207150;buf[3] = (byte) (t >>> 7);t = 402814201;buf[4] = (byte) (t >>> 8);return new String(buf);}});
    private final Object plus_complete_code = (new Object() {int t;public String toString() {byte[] buf = new byte[9];t = -865052970;buf[0] = (byte) (t >>> 9);t = -712088712;buf[1] = (byte) (t >>> 8);t = -1612237061;buf[2] = (byte) (t >>> 4);t = 576206510;buf[3] = (byte) (t >>> 7);t = 402813177;buf[4] = (byte) (t >>> 8);t = 432969432;buf[5] = (byte) (t >>> 1);t = 1922143684;buf[6] = (byte) (t >>> 10);t = 979594504;buf[7] = (byte) (t >>> 23);t = -647776980;buf[8] = (byte) (t >>> 22);return new String(buf);}});
    private final Object plus_start_code = (new Object() {int t;public String toString() {byte[] buf = new byte[6];t = -866688612;buf[0] = (byte) (t >>> 17);t = 1398311833;buf[1] = (byte) (t >>> 3);t = -370552023;buf[2] = (byte) (t >>> 10);t = -1332882267;buf[3] = (byte) (t >>> 23);t = -85601998;buf[4] = (byte) (t >>> 17);t = 354225481;buf[5] = (byte) (t >>> 14);return new String(buf);}});

    public ProcessDiscovererImpl(XLog initial_log) {
        this.initial_log = initial_log;
    }

    public Object[] generateJSONFromBPMNDiagram(BPMNDiagram bpmnDiagram) {
        try {
            JSONBuilder jsonBuilder = new JSONBuilder(nodeInfoCollector, null, null);
            return new Object[] {jsonBuilder.generateJSONFromBPMN(bpmnDiagram, null, true, false, false), bpmnDiagram};
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object[] generateJSONFromLog(String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        try {
            full_classifier = new XEventAttributeClassifier(attribute, new String[] {attribute, lifecycle_code.toString()});
            BPMNDiagram bpmnDiagram = generateDiagramFromLog(attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
            JSONBuilder jsonBuilder = new JSONBuilder(nodeInfoCollector, primaryAggregation, secondaryAggregation);
            return new Object[] {jsonBuilder.generateJSONFromBPMN(bpmnDiagram, type, false, secondary, false), bpmnDiagram};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object[] generateJSONWithGatewaysFromLog(String attribute, double activities, double arcs, double parallelism, boolean preserve_connectivity, boolean prioritize_parallelism, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        try {
            BPMNDiagram bpmnDiagram = generateBPMNFromLog(attribute, activities, arcs, parallelism, preserve_connectivity, prioritize_parallelism, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
            JSONBuilder jsonBuilder = new JSONBuilder(nodeInfoCollector, primaryAggregation, secondaryAggregation);
            return new Object[] {jsonBuilder.generateJSONFromBPMN(bpmnDiagram, type, true, secondary, false), bpmnDiagram};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONArray generateTraceModel(String traceID, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        try {
            full_classifier = new XEventAttributeClassifier(attribute, new String[] {attribute, lifecycle_code.toString()});
            BPMNDiagram bpmnDiagram = generateDiagramFromTrace(traceID, attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
            JSONBuilder jsonBuilder = new JSONBuilder(nodeInfoCollector, primaryAggregation, secondaryAggregation);
            return jsonBuilder.generateJSONFromBPMN(bpmnDiagram, DURATION, false, secondary, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BPMNDiagram generateBPMNFromLog(String attribute, double activities, double arcs, double parallelism, boolean preserve_connectivity, boolean prioritize_parallelism, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        try {
            reinitialize = true;
            XLog filtered = generateFilteredLog(attribute, activities, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
            generateFilteredLog(attribute, activities, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
            reinitialize = false;
            Set<String> transitions = new UnifiedSet<>();
            String complete = null;
            String start = null;
            for (XTrace trace : filtered) {
                for (XEvent event : trace) {
                    String lifecycle = event.getAttributes().get(lifecycle_code.toString()).toString();
                    if(lifecycle.toLowerCase().equals(complete_code.toString())) complete = lifecycle;
                    if(lifecycle.toLowerCase().equals(start_code.toString())) start = lifecycle;
                    transitions.add(lifecycle);
                    if (transitions.size() > 1 && complete != null && start != null) break;
                }
                if (transitions.size() > 1 && complete != null && start != null) break;
            }
            if (transitions.size() > 1) {
                Set<String> lifecycle = new UnifiedSet<>();
                lifecycle.add(complete);
                LogFilterCriterion criterion = LogFilterCriterionFactory.getLogFilterCriterion(Action.RETAIN,
                        CONTAIN_ANY,
                        EVENT,
                        lifecycle_code.toString(),
                        lifecycle_code.toString(),
                        lifecycle);
                List<LogFilterCriterion> criteria = new ArrayList<>(1);
                criteria.add(criterion);
                filtered = LogFilter.filter(filtered, criteria);
            }

            SplitMiner splitMiner = new SplitMiner();
            SimpleLog simpleLog = LogParser.getSimpleLog(filtered, new XEventAttributeClassifier(attribute, attribute));
            BPMNDiagram bpmnDiagram = splitMiner.mineBPMNModel(simpleLog, new DFGPWithLogThreshold(simpleLog, arcs, parallelism, prioritize_parallelism, preserve_connectivity, inverted_arcs), SplitMinerUIResult.StructuringTime.NONE);
            BPMNDiagramBuilder diagramBuilder = new BPMNDiagramBuilder(arcInfoCollector);
            Map<BPMNNode, BPMNNode> map = new UnifiedMap<>();
            for (BPMNNode node : bpmnDiagram.getNodes()) {
                map.put(node, diagramBuilder.addNode(node));
            }
            for (BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
                BPMNNode source = map.get(edge.getSource());
                BPMNNode target = map.get(edge.getTarget());
                Set<BPMNNode> sources = getSources(bpmnDiagram, edge.getSource(), new UnifiedSet<>());
                Set<BPMNNode> targets = getTargets(bpmnDiagram, edge.getTarget(), new UnifiedSet<>());
                Set<Arc> connecting_arcs = new UnifiedSet<>();
                for (BPMNNode s : sources) {
                    for (BPMNNode t : targets) {
                        BPMNNode s_m = map.get(s);
                        BPMNNode t_m = map.get(t);
                        Integer source_int = simplified_names.get(s_m.getLabel() + ((s_m instanceof Activity) ? plus_complete_code.toString() : ""));
                        Integer target_int = simplified_names.get(t_m.getLabel() + ((t_m instanceof Activity) ? plus_complete_code.toString() : ""));
                        Arc arc = new Arc(source_int, target_int);
                        if (source_int != null && target_int != null && arcInfoCollector.exists(arc))
                            connecting_arcs.add(arc);
                        if (transitions.size() > 1) {
                            source_int = simplified_names.get(s_m.getLabel() + ((s_m instanceof Activity) ? plus_complete_code.toString() : ""));
                            target_int = simplified_names.get(t_m.getLabel() + ((t_m instanceof Activity) ? plus_start_code.toString() : ""));
                            if (source_int != null && target_int != null) {
                                arc = new Arc(source_int, target_int);
                                if (arcInfoCollector.exists(arc)) {
                                    connecting_arcs.add(arc);
                                }
                            }
                        }

                    }
                }
                diagramBuilder.addArc(connecting_arcs, source, target, primaryType, primaryAggregation, secondaryType, secondaryAggregation);
            }
            return diagramBuilder.getBpmnDiagram();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Set<BPMNNode> getTargets(BPMNDiagram diagram, BPMNNode node, Set<BPMNNode> visited) {
        Set<BPMNNode> nodes = new UnifiedSet<>();
        if(node instanceof Activity || node instanceof Event) {
            nodes.add(node);
            return nodes;
        }
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getOutEdges(node)) {
            BPMNNode target = edge.getTarget();
            if(!visited.contains(target)) {
                if (target instanceof Activity || target instanceof Event) {
                    visited.add(target);
                    nodes.add(target);
                } else {
                    nodes.addAll(getTargets(diagram, target, visited));
                }
            }
        }
        return nodes;
    }

    private Set<BPMNNode> getSources(BPMNDiagram diagram, BPMNNode node, Set<BPMNNode> visited) {
        Set<BPMNNode> nodes = new UnifiedSet<>();
        if(node instanceof Activity || node instanceof Event) {
            nodes.add(node);
            return nodes;
        }
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : diagram.getInEdges(node)) {
            BPMNNode source = edge.getSource();
            if(!visited.contains(source)) {
                if (source instanceof Activity || source instanceof Event) {
                    visited.add(source);
                    nodes.add(source);
                } else {
                    nodes.addAll(getSources(diagram, source, visited));
                }
            }
        }
        return nodes;
    }

    public BPMNDiagram generateDFGFromLog(String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        try {
            full_classifier = new XEventAttributeClassifier(attribute, new String[] {attribute, lifecycle_code.toString()});
            return generateDiagramFromLog(attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void prepareLogForInitialization(String attribute, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> criteria) {
        if(reinitialize || this.type != primaryType || this.aggregation != primaryAggregation || !this.criteria.equals(criteria)) {
            XLog log = filterUsingCriteria(initial_log, criteria);
            number_of_traces = log.size();
            initializeDatastructures(primaryType, primaryAggregation, secondaryType, secondaryAggregation);
            changed = true;
        }
    }

    private List<IntList> initialization(String attribute, double activities, boolean inverted_nodes, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> criteria) {
        prepareLogForInitialization(attribute, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
        if(changed || this.activities != activities) {
            changed = false;
            this.activities = activities;
            List<IntList> simplified_log = simplifyLog(log);
            List<LongList> simplified_times_log = simplifyTimesLog(log);
            this.filtered_log = filterSimplifiedLog(simplified_log, simplified_times_log, activities, inverted_nodes, fixedType, fixedAggregation);
        }
        return this.filtered_log;
    }

    public XLog generateFilteredLog(String attribute, double activities, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> criteria) {
        full_classifier = new XEventAttributeClassifier(attribute, new String[] {attribute, lifecycle_code.toString()});
        List<IntList> filtered_log = initialization(attribute, activities, inverted_nodes, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
        XFactory factory = new XFactoryNaiveImpl();
        XLog filtered_xlog = factory.createLog(log.getAttributes());
        for(int trace = 0; trace < filtered_log.size(); trace++) {
            XTrace filtered_xtrace = factory.createTrace(log.get(trace).getAttributes());
            IntList filtered_trace = filtered_log.get(trace);
            int unfiltered_event = 0;
            for(int event = 1; event < filtered_trace.size() - 1; event++) {
                while(!full_classifier.getClassIdentity(log.get(trace).get(unfiltered_event)).equalsIgnoreCase(getEventFullName(filtered_trace.get(event)))) {
                    unfiltered_event++;
                }

                filtered_xtrace.add(log.get(trace).get(unfiltered_event));
                unfiltered_event++;
            }
            if(filtered_xtrace.size() > 0) {
                filtered_xlog.add(filtered_xtrace);
            }
        }

        return filtered_xlog;
    }

    public XLog generateFilteredFittedLog(String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> criteria, SearchStrategy searchStrategy) {
        full_classifier = new XEventAttributeClassifier(attribute, new String[] {attribute, lifecycle_code.toString()});
        List<IntList> filtered_log = initialization(attribute, activities, inverted_nodes, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
        ArcSelector arcSelector = new ArcSelector(arcInfoCollector, arcs, preserve_connectivity, fixedType, fixedAggregation, inverted_arcs);
        Set<Arc> retained_arcs = arcSelector.selectArcs();

        XLog original_log = generateFilteredLog(attribute, activities, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);

        XFactory factory = new XFactoryNaiveImpl();
        XLog filtered_xlog = factory.createLog(log.getAttributes());

        LogFitter logFitter = new LogFitter(factory);
        for(int trace = 0; trace < filtered_log.size(); trace++) {
            XTrace filtered_xtrace = logFitter.fitTrace(original_log.get(trace), filtered_log.get(trace), retained_arcs, true, searchStrategy);
            if(filtered_xtrace != null) {
                filtered_xlog.add(filtered_xtrace);
            }
        }

        return filtered_xlog;
    }

    public double measureFitness(String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> criteria, SearchStrategy searchStrategy) {
        full_classifier = new XEventAttributeClassifier(attribute, new String[] {attribute, lifecycle_code.toString()});
        prepareLogForInitialization(attribute, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
        List<IntList> simplified_log = simplifyLog(log);
        List<LongList> simplified_times_log = simplifyTimesLog(log);
        filterSimplifiedLog(simplified_log, simplified_times_log, activities, inverted_nodes, fixedType, fixedAggregation);
        ArcSelector arcSelector = new ArcSelector(arcInfoCollector, arcs, preserve_connectivity, fixedType, fixedAggregation, inverted_arcs);
        Set<Arc> retained_arcs = arcSelector.selectArcs();

        XFactory factory = new XFactoryNaiveImpl();

        LogFitter logFitter = new LogFitter(factory);
        return logFitter.measureFitness(simplified_log, retained_arcs, searchStrategy);
    }

    private BPMNDiagram generateDiagramFromLog(String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> criteria) {
        initialization(attribute, activities, inverted_nodes, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, criteria);
        ArcSelector arcSelector = new ArcSelector(arcInfoCollector, arcs, preserve_connectivity, fixedType, fixedAggregation, inverted_arcs);
        Set<Arc> retained_arcs = arcSelector.selectArcs();

        BPMNDiagramBuilder bpmnDiagramBuilder = new BPMNDiagramBuilder(arcInfoCollector);
        IntObjectHashMap<BPMNNode> map = new IntObjectHashMap<>();

        boolean cycle = true;
        IntHashSet candidate_nodes = retained_activities;
        Set<Arc> candidate_arcs = retained_arcs;
        IntHashSet new_candidate_nodes;
        Set<Arc> new_candidate_arcs;
        ReachabilityChecker reachabilityChecker = new ReachabilityChecker();
        while (cycle) {
            cycle = false;
            new_candidate_nodes = new IntHashSet();
            for(int i : candidate_nodes.toArray()) {
                boolean input = false;
                boolean output = false;
                for (Arc arc : candidate_arcs) {
                    if (arc.getSource() == i && arc.getTarget() != i && candidate_nodes.contains(arc.getTarget()) && reachabilityChecker.reachable(i, candidate_arcs)) output = true;
                    if (arc.getTarget() == i && arc.getSource() != i && candidate_nodes.contains(arc.getSource()) && reachabilityChecker.reaching(i, candidate_arcs)) input = true;
                }
                if ((input && output) || (i == start_int && output) || (i == end_int && input)) {
                    new_candidate_nodes.add(i);
                }
            }
            if(new_candidate_nodes.size() < candidate_nodes.size()) {
                cycle = true;
            }
            candidate_nodes = new_candidate_nodes;

            new_candidate_arcs = new UnifiedSet<>();
            for(Arc arc : candidate_arcs) {
                if(candidate_nodes.contains(arc.getSource()) && candidate_nodes.contains(arc.getTarget())) {
                    new_candidate_arcs.add(arc);
                }
            }
            candidate_arcs = new_candidate_arcs;
        }

        for(int i : candidate_nodes.toArray()) {
            BPMNNode node = bpmnDiagramBuilder.addNode(simplified_names.inverse().get(i));
            map.put(i, node);
        }

        for(Arc arc : candidate_arcs) {
            BPMNNode source = map.get(arc.getSource());
            BPMNNode target = map.get(arc.getTarget());
            bpmnDiagramBuilder.addArc(arc, source, target, primaryType, primaryAggregation, secondaryType, secondaryAggregation);
        }

        return collapseStartCompleteActivities(bpmnDiagramBuilder.getBpmnDiagram());
    }

    private BPMNDiagram generateDiagramFromTrace(String traceID, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> criteria) {
        full_classifier = new XEventAttributeClassifier(attribute, new String[] {attribute, lifecycle_code.toString()});
        log = filterUsingCriteria(initial_log, criteria);
        XTrace trace = null;
        XConceptExtension xce = XConceptExtension.instance();
        XTimeExtension xte = XTimeExtension.instance();
        for(XTrace trace1 : log) {
            if(xce.extractName(trace1).equals(traceID)) {
                trace = trace1;
                break;
            }
        }

        arcInfoCollector = new ArcInfoCollector(1);
        nodeInfoCollector = new NodeInfoCollector(1, new HashBiMap<>(), arcInfoCollector);
        BPMNDiagramBuilder bpmnDiagramBuilder = new BPMNDiagramBuilder(arcInfoCollector);
        BPMNNode lastNode = bpmnDiagramBuilder.addNode(start_name);
        for(int i = 0; i < trace.size(); i++) {
            XEvent event = trace.get(i);
            if(full_classifier.getClassIdentity(event).endsWith("complete")) {
                String name = xce.extractName(event);
                int previous_start = getStart(trace, i);
                if(previous_start > -1) {
                    Date date1 = xte.extractTimestamp(trace.get(previous_start));
                    Date date2 = xte.extractTimestamp(event);
                    Long diff = date2.getTime() - date1.getTime();
                    name += "\\n\\n[" + diff.toString() + "]";
                }

                BPMNNode node = bpmnDiagramBuilder.addNode(name);
                String label = "";

                int previous_complete = getPreviousComplete(trace, i);
                if (previous_complete > -1) {
                    Date date1 = xte.extractTimestamp(trace.get(previous_complete));
                    Date date2 = xte.extractTimestamp(event);
                    Long diff = date2.getTime() - date1.getTime();
                    label = "[" + diff.toString() + "]";
                }
                bpmnDiagramBuilder.addFlow(lastNode, node, label);
                lastNode = node;
            }
        }
        BPMNNode node = bpmnDiagramBuilder.addNode(end_name);
        bpmnDiagramBuilder.addFlow(lastNode, node, "");
        return bpmnDiagramBuilder.getBpmnDiagram();
    }

    private int getStart(XTrace trace, int pos) {
        XEvent event = trace.get(pos);
        XConceptExtension xce = XConceptExtension.instance();
        for(int i = pos - 1; i >= 0; i--) {
            XEvent event1 = trace.get(i);
            if(full_classifier.getClassIdentity(event1).endsWith("start") && xce.extractName(event).equals(xce.extractName(event1))) {
                return i;
            }
        }
        return -1;
    }

    private int getPreviousComplete(XTrace trace, int pos) {
        for(int i = pos - 1; i >= 0; i--) {
            XEvent event1 = trace.get(i);
            if(full_classifier.getClassIdentity(event1).endsWith("complete")) {
                return i;
            }
        }
        return -1;
    }

    private XLog filterUsingCriteria(XLog initial_log, List<LogFilterCriterion> criteria) {
        this.criteria = new ArrayList<>(criteria);
        this.simplified_names = null;
        this.log = LogFilter.filter(initial_log, criteria);
        return this.log;
    }

    private BPMNDiagram collapseStartCompleteActivities(BPMNDiagram bpmnDiagram) {
        BPMNDiagramBuilder bpmnDiagramBuilder = new BPMNDiagramBuilder(arcInfoCollector);

        Map<String, BPMNNode> nodes_map = new HashMap<>();
        for(BPMNNode node : bpmnDiagram.getNodes()) {
            String collapsed_name = eventNameAnalyser.getCollapsedEvent(node.getLabel());
            if(!nodes_map.containsKey(collapsed_name)) {
                BPMNNode collapsed_node = bpmnDiagramBuilder.addNode(collapsed_name);
                nodes_map.put(collapsed_name, collapsed_node);
            }
        }

        Set<Pair<String, String>> edges = new HashSet<>();
        for(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge : bpmnDiagram.getEdges()) {
            String source_name = edge.getSource().getLabel();
            String target_name = edge.getTarget().getLabel();

            String collapsed_source_name = eventNameAnalyser.getCollapsedEvent(source_name);
            String collapsed_target_name = eventNameAnalyser.getCollapsedEvent(target_name);

            BPMNNode source = nodes_map.get(collapsed_source_name);
            BPMNNode target = nodes_map.get(collapsed_target_name);

            Pair<String, String> pair = new Pair<>(collapsed_source_name, collapsed_target_name);
            if(!collapsed_source_name.equals(collapsed_target_name) || isSingleTypeEvent(getEventNumber(source_name)) || eventNameAnalyser.isCompleteEvent(source_name)) {
                if(!edges.contains(pair)) {
                    bpmnDiagramBuilder.addFlow(source, target, edge.getLabel());
                    edges.add(pair);
                }
            }
        }
        return bpmnDiagramBuilder.getBpmnDiagram();
    }

    private void initializeDatastructures(VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation) {
        this.type = primaryType;
        this.aggregation = primaryAggregation;
        simplified_names = new HashBiMap<>();

        arcInfoCollector = new ArcInfoCollector(number_of_traces);
        nodeInfoCollector = new NodeInfoCollector(number_of_traces, simplified_names, arcInfoCollector);
    }

    
    private List<IntList> simplifyLog(XLog log) {
        List<IntList> simplified_log = new ArrayList<>();

        simplified_names.put(start_name, start_int);
        simplified_names.put(end_name, end_int);

        for(XTrace trace : log) {
            IntArrayList simplified_trace = new IntArrayList(trace.size());

            nodeInfoCollector.nextTrace();
            nodeInfoCollector.updateActivityFrequency(start_int, 1);

            simplified_trace.add(start_int);

            IntIntHashMap eventsCount = new IntIntHashMap();
            for(XEvent event : trace) {
                String name = full_classifier.getClassIdentity(event);
                if(name.contains("+")) {
                    String prename = name.substring(0, name.indexOf("+"));
                    String postname = name.substring(name.indexOf("+"));
                    name = prename + postname.toLowerCase();
                }
                if(eventNameAnalyser.isStartEvent(name)) contain_start_events = true;

                Integer simplified_event;
                if((simplified_event = getEventNumber(name)) == null) {
                    simplified_event = simplified_names.size() + 1;
                    simplified_names.put(name, simplified_event);
                }

                eventsCount.addToValue(simplified_event, 1);
                simplified_trace.add(simplified_event);
            }

            for(int event : eventsCount.keySet().toArray()) {
                nodeInfoCollector.updateActivityFrequency(event, eventsCount.get(event));
            }

            nodeInfoCollector.updateActivityFrequency(end_int, 1);
            simplified_trace.add(end_int);

            simplified_log.add(simplified_trace);
        }

        return simplified_log;
    }

    private List<LongList> simplifyTimesLog(XLog log) {
        List<LongList> simplified_times_log = new ArrayList<>();

        for(XTrace trace : log) {
            LongArrayList simplified_times_trace = new LongArrayList(trace.size());

            for(int i = 0; i < trace.size(); i++) {
                XEvent event = trace.get(i);
                Long time = xte.extractTimestamp(event).getTime();
                if(i == 0) {
                    simplified_times_trace.add(time);
                }
                if(i == trace.size() - 1) {
                    simplified_times_trace.add(time);
                }
                simplified_times_trace.add(time);
            }

            simplified_times_log.add(simplified_times_trace);
        }

        return simplified_times_log;
    }

    private List<IntList> filterSimplifiedLog(List<IntList> log, List<LongList> times_log, double activities, boolean inverted_nodes, VisualizationType fixedType, VisualizationAggregation fixedAggregation) {
        NodeSelector nodeSelector = new NodeSelector(nodeInfoCollector, activities, contain_start_events, fixedType, fixedAggregation, inverted_nodes);
        retained_activities = nodeSelector.selectActivities();
        List<IntList> filtered_log = new ArrayList<>(log.size());

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
            filtered_log.add(filtered_trace);

            IntHashSet not_reached = new IntHashSet();
            IntHashSet not_reaching = new IntHashSet();
            for(int i = 0; i < filtered_trace.size(); i++) {
                if(i != 0) not_reached.add(filtered_trace.get(i));
                if(i != filtered_trace.size() - 1) not_reaching.add(filtered_trace.get(i));
            }
            Long trace_duration = filtered_time_trace.get(filtered_time_trace.size() - 1) - filtered_time_trace.get(0);

            ObjectIntHashMap<Arc> arcsCount = new ObjectIntHashMap<>();
            for(int i = 0; i < filtered_trace.size() - 1; i++) {
                createArc(arcsCount, not_reached, not_reaching, filtered_trace.get(i), filtered_trace.get(i + 1), filtered_time_trace.get(i + 1) - filtered_time_trace.get(i));
            }

            for(Arc arc : arcsCount.keySet().toArray(new Arc[arcsCount.size()])) {
                arcInfoCollector.updateArcFrequency(arc, arcsCount.get(arc));
                arcInfoCollector.consolidateArcImpact(arc, trace_duration);
            }
            arcInfoCollector.nextTrace();
        }

        return filtered_log;
    }

    private void createArc(ObjectIntHashMap<Arc> arcsCount, IntHashSet not_reached, IntHashSet not_reaching, int source, int target, long duration) {
        Arc arc = new Arc(source, target);
        arcsCount.addToValue(arc, 1);
        arcInfoCollector.updateArcDuration(arc, duration);
        arcInfoCollector.updateArcImpact(arc, duration);
        not_reaching.remove(source);
        not_reached.remove(target);
    }

    public BPMNDiagram insertBPMNGateways(BPMNDiagram bpmnDiagram) {
        return BPMNDiagramBuilder.insertBPMNGateways(bpmnDiagram);
    }

    private String getEventFullName(int event) {
        return simplified_names.inverse().get(event);
    }

    private Integer getEventNumber(String event) {
        return simplified_names.get(event);
    }
    
    private boolean isSingleTypeEvent(int event) {
        String name = getEventFullName(event);
        if(eventNameAnalyser.isStartEvent(name) && getEventNumber(eventNameAnalyser.getCompleteEvent(name)) != null) return false;
        return !eventNameAnalyser.isCompleteEvent(name) || getEventNumber(eventNameAnalyser.getStartEvent(name)) == null;
    }

}
