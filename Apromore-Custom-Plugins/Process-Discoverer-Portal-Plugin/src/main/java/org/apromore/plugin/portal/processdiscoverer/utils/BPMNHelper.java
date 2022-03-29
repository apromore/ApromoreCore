/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
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

package org.apromore.plugin.portal.processdiscoverer.utils;

import lombok.experimental.UtilityClass;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNEdge;
import org.apromore.processmining.models.graphbased.directed.bpmn.BPMNNode;

@UtilityClass
public class BPMNHelper {
    public static boolean isStartNode(BPMNDiagram d, BPMNNode n) {
        return d.getInEdges(n).isEmpty();
    }

    public static boolean isEndNode(BPMNDiagram d, BPMNNode n) {
        return d.getOutEdges(n).isEmpty();
    }

    public static boolean isStartingOrEndingEdge(BPMNEdge<? extends BPMNNode, ? extends BPMNNode> edge, BPMNDiagram d) {
        return isStartNode(d, edge.getSource()) || isEndNode(d, edge.getTarget());
    }

}
