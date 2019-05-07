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

package org.apromore.plugin.processdiscoverer.service;

import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.apromore.plugin.processdiscoverer.LogFilterCriterion;
import org.apromore.plugin.processdiscoverer.impl.ProcessDiscovererImpl;
import org.apromore.plugin.processdiscoverer.impl.SearchStrategy;
import org.apromore.plugin.processdiscoverer.impl.VisualizationAggregation;
import org.apromore.plugin.processdiscoverer.impl.VisualizationType;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
@Service
public class ProcessDiscovererServiceImpl extends DefaultParameterAwarePlugin implements ProcessDiscovererService {
    
    @Override
    public Object[] generateJSONFromBPMNDiagram(BPMNDiagram bpmnDiagram) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(null);
        return processDiscoverer.generateJSONFromBPMNDiagram(bpmnDiagram);
    }

    @Override
    public Object[] generateJSONFromLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateJSONFromLog(attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, secondary, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public Object[] generateJSONWithGatewaysFromLog(XLog log, String attribute, double activities, double arcs, double parallelism, boolean preserve_connectivity, boolean prioritize_parallelism, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateJSONWithGatewaysFromLog(attribute, activities, arcs, parallelism, preserve_connectivity, prioritize_parallelism, inverted_nodes, inverted_arcs, secondary, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public JSONArray generateTraceModel(XLog log, String traceID, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateTraceModel(traceID, attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, secondary, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public BPMNDiagram generateDFGFromLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateDFGFromLog(attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public BPMNDiagram insertBPMNGateways(BPMNDiagram bpmnDiagram) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(null);
        return processDiscoverer.insertBPMNGateways(bpmnDiagram);
    }

    @Override
    public XLog generateFilteredLog(XLog log, String attribute, double activities, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateFilteredLog(attribute, activities, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria);
    }

    @Override
    public XLog generateFilteredFittedLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria, SearchStrategy searchStrategy) {
        ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
        return processDiscoverer.generateFilteredFittedLog(attribute, activities, arcs, preserve_connectivity, inverted_nodes, inverted_arcs, fixedType, fixedAggregation, primaryType, primaryAggregation, secondaryType, secondaryAggregation, filter_criteria, searchStrategy);
    }
    
    @Override
    public XLog filterUsingCriteria(XLog log, List<LogFilterCriterion> criteria) {
    	ProcessDiscovererImpl processDiscoverer = new ProcessDiscovererImpl(log);
    	return processDiscoverer.filterUsingCriteria(log, criteria);
    }

}
