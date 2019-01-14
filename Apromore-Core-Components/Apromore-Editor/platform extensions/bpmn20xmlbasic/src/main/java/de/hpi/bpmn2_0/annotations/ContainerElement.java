/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package de.hpi.bpmn2_0.annotations;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.bpmndi.di.DiagramElement;
import de.hpi.bpmn2_0.model.connector.Edge;

import java.util.List;

/**
 * @author Sven Wagner-Boysen
 *         <p/>
 *         Annotaion to identify container elements such as subprocesses and
 *         sub choreographies.
 */
public interface ContainerElement {

    public List<FlowElement> getFlowElement();

    public List<DiagramElement> _getDiagramElements();

    public List<Edge> getChildEdges();
}
