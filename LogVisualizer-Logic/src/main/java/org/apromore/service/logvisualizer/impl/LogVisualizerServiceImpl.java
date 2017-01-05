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

import au.edu.qut.context.FakePluginContext;
import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.service.logvisualizer.LogVisualizerService;
import org.deckfour.xes.classification.XEventAndClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventLifeTransClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.fuzzyminer.algorithms.fuzzycg2fuzzypn.FuzzyCGToFuzzyPN;
import org.processmining.fuzzyminer.algorithms.preprocessing.LogFilterer;
import org.processmining.fuzzyminer.algorithms.preprocessing.LogPreprocessor;
import org.processmining.fuzzyminer.models.causalgraph.FuzzyCausalGraph;
import org.processmining.fuzzyminer.models.fuzzypetrinet.FuzzyPetrinet;
import org.processmining.fuzzyminer.plugins.FuzzyCGMiner;
import org.processmining.fuzzyminer.plugins.FuzzyMinerSettings;
import org.processmining.fuzzyminer.plugins.FuzzyPNMinerPlugin;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramImpl;
import org.processmining.models.graphbased.directed.bpmn.BPMNNode;
import org.processmining.models.graphbased.directed.fuzzymodel.FMClusterNode;
import org.processmining.models.graphbased.directed.fuzzymodel.FMEdge;
import org.processmining.models.graphbased.directed.fuzzymodel.FMNode;
import org.processmining.models.graphbased.directed.fuzzymodel.MutableFuzzyGraph;
import org.processmining.models.graphbased.directed.fuzzymodel.attenuation.Attenuation;
import org.processmining.models.graphbased.directed.fuzzymodel.attenuation.NRootAttenuation;
import org.processmining.models.graphbased.directed.fuzzymodel.metrics.MetricsRepository;
import org.processmining.models.graphbased.directed.fuzzymodel.transform.BestEdgeTransformer;
import org.processmining.models.graphbased.directed.fuzzymodel.transform.ConcurrencyEdgeTransformer;
import org.processmining.models.graphbased.directed.fuzzymodel.transform.FastTransformer;
import org.processmining.models.graphbased.directed.fuzzymodel.transform.FuzzyEdgeTransformer;
import org.processmining.plugins.fuzzymap.util.FuzzyBusinessProcessMap;
import org.processmining.plugins.fuzzymodel.miner.FuzzyMinerPlugin;
import org.processmining.plugins.fuzzymodel.miner.ui.FastTransformerPanel;
import org.processmining.plugins.fuzzymodel.miner.ui.FuzzyModelPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raffaele Conforti on 01/12/2016.
 */
@Service
public class LogVisualizerServiceImpl extends DefaultParameterAwarePlugin implements LogVisualizerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogVisualizerServiceImpl.class);

    @Override
    public void visualizeLog(XLog log, double activities, double arcs) {
        try {
            XEventClassifier classifier = new XEventAndClassifier(new XEventNameClassifier(), new XEventLifeTransClassifier());
            XLogInfo logInfo = XLogInfoFactory.createLogInfo(log, classifier);
            MetricsRepository metrics = MetricsRepository.createRepository(logInfo);
            Attenuation attenuation = new NRootAttenuation(2.7, 5);
            int maxDistance = 4;
            FuzzyMinerPlugin fuzzyMinerPlugin = new FuzzyMinerPlugin();
            PluginContext context = new FakePluginContext();
            metrics = fuzzyMinerPlugin.mineGeneric(context, log, metrics, attenuation, maxDistance);
            MutableFuzzyGraph mutableFuzzyGraph = new FuzzyModelPanel(context, metrics).getExportFuzzyGraphObjects();

            ConcurrencyEdgeTransformer concurrencyEdgeTransformer = new ConcurrencyEdgeTransformer(context);
            concurrencyEdgeTransformer.setPreserveThreshold(1);
            concurrencyEdgeTransformer.setRatioThreshold(1);
            concurrencyEdgeTransformer.transform(mutableFuzzyGraph);

            FuzzyEdgeTransformer edgeFilter = new FuzzyEdgeTransformer(context);
            edgeFilter.setPreservePercentage(arcs);
            edgeFilter.setSignificanceCorrelationRatio(1);
            edgeFilter.transform(mutableFuzzyGraph);

            FastTransformer nodeFilter = new FastTransformer(context);
            nodeFilter.setThreshold(activities);
            nodeFilter.transform(mutableFuzzyGraph);

            Set<FMNode> nodes = mutableFuzzyGraph.getNodes();
            BPMNDiagram bpmnDiagram = new BPMNDiagramImpl("Fuzzy Model");
            Map<FMNode, BPMNNode> nodesMap = new HashMap<>();

            for(FMNode node : nodes) {
                String name = node.getElementName();
                if(node instanceof FMClusterNode) {
                    FMClusterNode clusterNode = (FMClusterNode) node;
                    Set<FMNode>  primitives = clusterNode.getPrimitives();
                    double best = 0;
                    for(FMNode primitive : primitives) {
                        if(best < primitive.getSignificance()) {
                            best = primitive.getSignificance();
                            name = primitive.getElementName();
                        }
                    }
                }
                nodesMap.put(node, bpmnDiagram.addActivity(name, false, false, false, false, false));
            }

            Set<FMEdge<? extends FMNode, ? extends FMNode>> edges = mutableFuzzyGraph.getEdges();
            for(FMEdge edge : edges) {
                bpmnDiagram.addFlow(nodesMap.get(edge.getSource()), nodesMap.get(edge.getTarget()), "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
