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
import org.apromore.plugin.portal.processdiscoverer.LogFilterCriterion;
import org.apromore.processdiscoverer.AbstractionParams;
import org.apromore.processdiscoverer.ProcessDiscoverer;
import org.apromore.processdiscoverer.VisualizationAggregation;
import org.apromore.processdiscoverer.VisualizationType;
import org.apromore.processdiscoverer.dfg.abstraction.BPMNAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.DFGAbstraction;
import org.apromore.processdiscoverer.dfg.abstraction.TraceAbstraction;
import org.apromore.processdiscoverer.dfg.vis.BPMNDiagramBuilder;
import org.apromore.processdiscoverer.logfilter.LogFilter;
import org.apromore.processdiscoverer.logprocessors.SearchStrategy;
import org.deckfour.xes.model.XLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Implementatation of ProcessDiscovererService
 * Every method is a one-off service only, no information is retained between different calls.
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 05/08/2018.
 * Modified by Bruce Nguyen
 */
@Service
public class ProcessDiscovererServiceImpl extends DefaultParameterAwarePlugin implements ProcessDiscovererService {
    @Override
    public Object[] generateDFGJSON(XLog log, AbstractionParams params) throws Exception {
        ProcessDiscoverer processDiscoverer = new ProcessDiscoverer();
        return processDiscoverer.generateDFGJSON(log, params);
    }

    @Override
    public Object[] generateBPMNJSON(XLog log, AbstractionParams params) throws Exception {
        ProcessDiscoverer processDiscoverer = new ProcessDiscoverer();
        DFGAbstraction dfgAbstraction = processDiscoverer.generateDFGAbstraction(log, params);
        return processDiscoverer.generateBPMNJSON(log, params, dfgAbstraction);
    }
    
    @Override
    public DFGAbstraction generateDFGAbstraction(XLog log, AbstractionParams params) throws Exception {
        ProcessDiscoverer processDiscoverer = new ProcessDiscoverer();
        return processDiscoverer.generateDFGAbstraction(log, params);
    }
    
    @Override
    public BPMNAbstraction generateBPMNAbstraction(XLog log, AbstractionParams params) throws Exception {
    	ProcessDiscoverer processDiscoverer = new ProcessDiscoverer();
    	DFGAbstraction dfgAbstraction = processDiscoverer.generateDFGAbstraction(log, params);
    	return processDiscoverer.generateBPMNAbstraction(log, params, dfgAbstraction);
    }

}
