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

import org.apromore.plugin.processdiscoverer.LogFilterCriterion;
import org.apromore.plugin.processdiscoverer.impl.SearchStrategy;
import org.apromore.plugin.processdiscoverer.impl.VisualizationAggregation;
import org.apromore.plugin.processdiscoverer.impl.VisualizationType;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import java.util.List;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 */
public interface ProcessDiscovererService {

    Object[] generateJSONFromBPMNDiagram(BPMNDiagram diagram);
    Object[] generateJSONFromLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    Object[] generateJSONWithGatewaysFromLog(XLog log, String attribute, double activities, double arcs, double parallelism, boolean preserve_connectivity, boolean prioritize_parallelism, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    JSONArray generateTraceModel(XLog log, String traceID, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, boolean secondary, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    BPMNDiagram generateDFGFromLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    BPMNDiagram insertBPMNGateways(BPMNDiagram bpmnDiagram);
    XLog generateFilteredLog(XLog log, String attribute, double activities, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria);
    XLog generateFilteredFittedLog(XLog log, String attribute, double activities, double arcs, boolean preserve_connectivity, boolean inverted_nodes, boolean inverted_arcs, VisualizationType fixedType, VisualizationAggregation fixedAggregation, VisualizationType primaryType, VisualizationAggregation primaryAggregation, VisualizationType secondaryType, VisualizationAggregation secondaryAggregation, List<LogFilterCriterion> filter_criteria, SearchStrategy searchStrategy);
    XLog filterUsingCriteria(XLog log, List<LogFilterCriterion> criteria);
}
