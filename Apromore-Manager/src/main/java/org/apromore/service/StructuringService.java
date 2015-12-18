/*
 * Copyright © 2009-2015 The Apromore Initiative.
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

package org.apromore.service;

import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

import java.util.Map;

public interface StructuringService {

	Map<Long, String> getErrors();

	BPMNDiagram getStructuredDiagram();

	BPMNDiagram structureDiagram(BPMNDiagram diagram) throws Exception;

	String structureBPMNModel(BPMNDiagram diagram) throws Exception;

	String structureBPMNModel(String xmlProcess) throws Exception;
}
