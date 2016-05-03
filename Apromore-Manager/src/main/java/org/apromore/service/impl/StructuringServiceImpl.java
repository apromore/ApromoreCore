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

import org.apromore.service.BPMNDiagramImporter;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apromore.service.StructuringService;
import org.springframework.stereotype.Service;


@Service
public class StructuringServiceImpl implements StructuringService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StructuringServiceImpl.class);

	public StructuringServiceImpl() { }

	@Override
	public Map<Long, String> getErrors() { return null;	}

	@Override
	public String structureBPMNModel(BPMNDiagram diagram) throws Exception {
		au.edu.qut.structuring.StructuringService ss = new au.edu.qut.structuring.StructuringService();
		au.edu.qut.bpmn.exporter.impl.BPMNDiagramExporterImpl exporter = new au.edu.qut.bpmn.exporter.impl.BPMNDiagramExporterImpl();
		BPMNDiagram structuredDiagram = ss.structureDiagram(diagram);

		return exporter.exportBPMNDiagram(structuredDiagram);
	}

	@Override
	public String structureBPMNModel(String xmlProcess) throws Exception {
		BPMNDiagramImporter diagramImporter = new BPMNDiagramImporterImpl();
		BPMNDiagram diagram = diagramImporter.importBPMNDiagram(xmlProcess);

		return structureBPMNModel(diagram);
	}
}
