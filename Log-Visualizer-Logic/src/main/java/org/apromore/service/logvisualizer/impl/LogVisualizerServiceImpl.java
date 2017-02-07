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

import au.edu.qut.util.ImportEventLog;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.service.logvisualizer.LogVisualizerService;
import org.apromore.service.logvisualizer.fuzzyminer.model.*;
import org.apromore.service.logvisualizer.fuzzyminer.transformer.*;
import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIContext;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.Pair;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.bpmn.elements.Activity;
import org.processmining.models.graphbased.directed.bpmn.elements.Event;
import org.processmining.models.graphbased.directed.fuzzymodel.attenuation.Attenuation;
import org.processmining.models.graphbased.directed.fuzzymodel.attenuation.NRootAttenuation;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.MetricsRepository;
import org.processmining.plugins.bpmn.BpmnDefinitions;
import org.processmining.plugins.fuzzymodel.miner.FuzzyMinerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti on 01/12/2016.
 */
@Service
public class LogVisualizerServiceImpl extends DefaultParameterAwarePlugin implements LogVisualizerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerServiceImpl.class);
    private int count = 1;

    public static void main(String[] args)  {
        LogVisualizerServiceImpl l = new LogVisualizerServiceImpl();
        XLog log = null;
        try {
            log = ImportEventLog.importFromFile(new XFactoryNaiveImpl(), "/Volumes/Data/SharedFolder/Logs/ArtificialLess.xes.gz");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s = l.visualizeLog(log, 0, 0);
        System.out.println();
        String s1 = l.visualizeLog(log, 0, 0.5);
    }

    @Override
    public String visualizeLog(XLog log, double activities, double arcs) {
        try {
            XEventClassifier classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
            XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);
            MetricsRepository metrics = MetricsRepository.createRepository(logInfo);
            Attenuation attenuation = new NRootAttenuation(2.7, 5);
            int maxDistance = 4;
            FuzzyMinerPlugin fuzzyMinerPlugin = new FuzzyMinerPlugin();
            UIContext uiContext = new UIContext();
            PluginContext pluginContext = uiContext.getMainPluginContext();
            metrics = fuzzyMinerPlugin.mineGeneric(pluginContext, log, metrics, attenuation, maxDistance);
            MutableFuzzyGraph mutableFuzzyGraph = new FuzzyModelPanel(pluginContext, metrics).getExportFuzzyGraphObjects();
            mutableFuzzyGraph.initializeGraph();
            mutableFuzzyGraph.setEdgeImpls();

//            SimpleTransformer simpleTransformer = new SimpleTransformer();
//            simpleTransformer.setThreshold(activities);

//            NewConcurrencyEdgeTransformer newConcurrencyEdgeTransformer = new NewConcurrencyEdgeTransformer();
//            newConcurrencyEdgeTransformer.setPreserveThreshold(arcs);
//            newConcurrencyEdgeTransformer.setRatioThreshold(arcs);

//            ConcurrencyEdgeTransformer concurrencyEdgeTransformer = new ConcurrencyEdgeTransformer();
//            concurrencyEdgeTransformer.setPreserveThreshold(arcs);
//            concurrencyEdgeTransformer.setRatioThreshold(arcs);

            FuzzyEdgeTransformer fuzzyEdgeTransformer = new FuzzyEdgeTransformer();
            fuzzyEdgeTransformer.setPreservePercentage(arcs);
            fuzzyEdgeTransformer.setSignificanceCorrelationRatio(arcs);

//            BestEdgeTransformer bestEdgeTransformer = new BestEdgeTransformer();

            FastTransformer nodeFilter = new FastTransformer();
//            nodeFilter.addPreTransformer(simpleTransformer);
            nodeFilter.addPreTransformer(fuzzyEdgeTransformer);
//            nodeFilter.addInterimTransformer(bestEdgeTransformer);
            nodeFilter.setThreshold(activities);
            nodeFilter.transform(mutableFuzzyGraph);

//            fuzzyEdgeTransformer.transform(mutableFuzzyGraph);
//            mutableFuzzyGraph.initializeGraph();
//            mutableFuzzyGraph.setEdgeImpls();

            Set<FMNode> nodes = mutableFuzzyGraph.getNodes();
            BPMNDiagram bpmnDiagram = new BPMNDiagramImpl("Fuzzy Model");
            Map<String, BPMNNode> nodesMap = new HashMap<>();

            for(FMNode node : nodes) {
                createNode(nodesMap, bpmnDiagram, node);
            }

            Set<Pair<String, String>> edgesAdded = new HashSet<>();
            Set<FMEdge<? extends FMNode, ? extends FMNode>> edges = mutableFuzzyGraph.getEdges();
            for (FMEdge<? extends FMNode, ? extends FMNode> edge : edges) {
                try {
                    if(edge.getCorrelation() >= 1 - arcs)
                    createEdge(nodesMap, bpmnDiagram, edgesAdded, edge);
                }catch (NullPointerException e) {
                    createNode(nodesMap, bpmnDiagram, edge.getSource());
                    createNode(nodesMap, bpmnDiagram, edge.getTarget());
                    createEdge(nodesMap, bpmnDiagram, edgesAdded, edge);
                }
            }

            Event start = bpmnDiagram.addEvent("Start", Event.EventType.START, Event.EventTrigger.NONE, Event.EventUse.CATCH, null);
            Set<String> firstActivity = new HashSet<>();
            Set<Pair<String, String>> startEdge = new HashSet<>();
            for(XTrace trace : log) {
                String name1 = XConceptExtension.instance().extractName(trace.get(0));
                String name2 = XLifecycleExtension.instance().extractTransition(trace.get(0));
                String name = name1 + " (" + name2 + ")";
                firstActivity.add(name);
            }
            for(String name : firstActivity) {
                BPMNNode node = nodesMap.get(name);
                if(node != null) {
                    Pair<String, String> pair = new Pair<>("Start", node.getLabel());
                    if (!startEdge.contains(pair)){
                        bpmnDiagram.addFlow(start, nodesMap.get(name), "");
                        startEdge.add(pair);
                    }
                }
            }

            Event end = bpmnDiagram.addEvent("End", Event.EventType.END, Event.EventTrigger.NONE, Event.EventUse.THROW, null);
            Set<String> lastActivity = new HashSet<>();
            Set<Pair<String, String>> endEdge = new HashSet<>();
            for(XTrace trace : log) {
                String name1 = XConceptExtension.instance().extractName(trace.get(trace.size() - 1));
                String name2 = XLifecycleExtension.instance().extractTransition(trace.get(trace.size() - 1));
                String name = name1 + " (" + name2 + ")";
                lastActivity.add(name);
            }
            for(String name : lastActivity) {
                BPMNNode node = nodesMap.get(name);
                if(node != null) {
                    Pair<String, String> pair = new Pair<>(node.getLabel(), "End");
                    if (!endEdge.contains(pair)){
                        bpmnDiagram.addFlow(nodesMap.get(name), end, "");
                        endEdge.add(pair);
                    }
                }
            }

            UIContext context = new UIContext();
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIPluginContext uiPluginContext = context.getMainPluginContext();
            BpmnDefinitions.BpmnDefinitionsBuilder definitionsBuilder = new BpmnDefinitions.BpmnDefinitionsBuilder(uiPluginContext, bpmnDiagram);
            BpmnDefinitions definitions = new BpmnDefinitions("definitions", definitionsBuilder);

            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n " +
                    "xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n " +
                    "xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n " +
                    "xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n " +
                    "targetNamespace=\"http://www.omg.org/bpmn20\"\n " +
                    "xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">");

            sb.append(definitions.exportElements());
            sb.append("</definitions>");
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void createEdge(Map<String, BPMNNode> nodesMap, BPMNDiagram bpmnDiagram, Set<Pair<String, String>> edgesAdded, FMEdge<? extends FMNode, ? extends FMNode> edge) {
        FMNode source = edge.getSource();
        BPMNNode source1 = nodesMap.get(standardizeName(source.getLabel()));

        FMNode target = edge.getTarget();
        BPMNNode target1 = nodesMap.get(standardizeName(target.getLabel()));

        Pair<String, String> pair = new Pair<>(
                standardizeName(source1.getLabel()),
                standardizeName(target1.getLabel()));
        if(!edgesAdded.contains(pair)) {
            bpmnDiagram.addFlow(source1, target1, "");
            edgesAdded.add(pair);
        }
    }

    private void createNode(Map<String, BPMNNode> nodesMap, BPMNDiagram bpmnDiagram, FMNode node) {
        if(node instanceof FMClusterNode) {
            FMClusterNode clusterNode = (FMClusterNode) node;
            clusterNode.setLabel(clusterNode.getElementName().replaceAll(" ", "") + " " + clusterNode.getEventType() + " " + clusterNode.getSignificance());
        }
        if(nodesMap.get(standardizeName(node.getLabel())) == null) {
            if (node instanceof FMClusterNode) {
                FMClusterNode clusterNode = (FMClusterNode) node;
                Set<FMNode> primitives = clusterNode.getPrimitives();
                double best = 0;
                String name = "";
                String original = standardizeName(node.getLabel());
                for (FMNode primitive : primitives) {
                    if (best < primitive.getSignificance()) {
                        best = primitive.getSignificance();
                        name = standardizeName(primitive.getLabel());
                    }
                }
                Activity activity = bpmnDiagram.addActivity(name, false, false, false, false, false);
                nodesMap.put(original, activity);
                for (FMNode primitive : primitives) {
                    name = standardizeName(primitive.getLabel());
                    nodesMap.put(name, activity);
                }
            } else {
                String name = standardizeName(node.getLabel());
                nodesMap.put(name, bpmnDiagram.addActivity(name, false, false, false, false, false));
            }
        }
    }

    private String standardizeName(String name) {
        if(!name.contains("(")) {
            String name1 = name.substring(0, name.indexOf(" "));
            String name2 = name.substring(name.indexOf(" ") + 1, name.lastIndexOf(" "));
            return name1 + " (" + name2 + ")";
        }
        return name;
    }

}
