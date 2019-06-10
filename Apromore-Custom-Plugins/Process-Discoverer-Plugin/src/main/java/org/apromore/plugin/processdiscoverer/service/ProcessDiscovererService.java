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

import org.apromore.plugin.portal.processdiscoverer.LogFilterCriterion;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.logprocessors.SearchStrategy;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import java.util.List;

/**
 * This interface and its implementations provide service-oriented interface into ProcessDiscoverer 
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
public interface ProcessDiscovererService {
    Object[] generateDFGJSON(XLog log, AbstractionParams params) throws Exception;
    Object[] generateBPMNJSON(XLog log, AbstractionParams params) throws Exception;
    BPMNDiagram generateDFGFromLog(XLog log, AbstractionParams params) throws Exception;
    BPMNDiagram generateBPMNFromLog(XLog log, AbstractionParams params) throws Exception;
    BPMNDiagram insertBPMNGateways(BPMNDiagram bpmnDiagram);
}
