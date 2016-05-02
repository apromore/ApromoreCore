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

package org.apromore.service.impl;

import au.edu.qut.metrics.ComplexityCalculator;
import org.apromore.service.BPMNDiagramImporter;
import org.apromore.service.MeasurementsService;
import org.json.JSONObject;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

import org.springframework.stereotype.Service;


/**
 * Created by Adriano on 08/01/2016.
 */

@Service
public class MeasurementsServiceImpl implements MeasurementsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementsServiceImpl.class);

    private BPMNDiagramImporter diagramImporter;
    private JSONObject result;

    public MeasurementsServiceImpl() {
        diagramImporter = new BPMNDiagramImporterImpl();
    }

    @Override
    public String computeSimplicity(BPMNDiagram diagram) {
        if( diagram == null ) return null;
        result = new JSONObject();

        try {
            ComplexityCalculator cc = new ComplexityCalculator();
            Map<String, String> metrics;
            metrics = cc.computeComplexity(diagram, true, true, true, true, true, true, true, true, true);
            for (String key : metrics.keySet()) result.put(key, metrics.get(key));
            return result.toString();
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    public String computeSimplicity(String process) {
        ComplexityCalculator cc = new ComplexityCalculator();
        Map<String, String> metrics;
        BPMNDiagram diagram;
        result = new JSONObject();

        try {
            diagram = diagramImporter.importBPMNDiagram(process);
            metrics = cc.computeComplexity(diagram, true, true, true, true, true, true, true, true, true);
            for (String key : metrics.keySet()) result.put(key.toLowerCase(), metrics.get(key));
            return result.toString();
        } catch(Exception e) {
            return null;
        }
    }
}
