/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.service.bpmndiagramimporter;

import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;

/**
 * Created by Adriano on 29/10/2015.
 */
public interface BPMNDiagramImporter {
    BPMNDiagram importBPMNDiagram(String xmlProcess) throws Exception;
}
