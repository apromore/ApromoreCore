/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

package org.apromore.canoniser.bpmn.cpf;

// Java 2 Standard packages
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.Unmarshaller;

// Local classes
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.NodeType;

/**
 * As CPF elements are unmarshalled, populate their convenience fields.
 *
 * The implemented convenience fields are:
 * <ul>
 * <li>{@link CpfNodeType#getIncomingEdges}</li>
 * <li>{@link CpfNodeType#getOutgoingEdges}</li>
 * </ul>
 *
 * @author <a href="mailto:simon.raboczi@uqconnect.edu.au">Simon Raboczi</a>
 * @since 0.4
 */
public class CpfUnmarshallerListener extends Unmarshaller.Listener {

    /** Logger.  Named after the class. */
    private final Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

    /** Map from node IDs to node objects */
    private final Map<String, NodeType> nodeMap = new HashMap<String, NodeType>();  // TODO - diamond operator

    private final Map<String, Object> elementMap = new HashMap<String, Object>();  // TODO - diamond operator

    /** {@inheritDoc} */
    @Override
    public void afterUnmarshal(final Object target, final Object parent) {
        if (target instanceof EdgeType) {
            CpfEdgeType edge = (CpfEdgeType) target;
            elementMap.put(edge.getId(), edge);

            edge.setSourceRef(nodeMap.get(edge.getSourceId()));
            ((CpfNodeType) edge.getSourceRef()).getOutgoingEdges().add(edge);

            edge.setTargetRef(nodeMap.get(edge.getTargetId()));
            ((CpfNodeType) edge.getTargetRef()).getIncomingEdges().add(edge);

        } else if (target instanceof NodeType) {
            NodeType node = (NodeType) target;
            elementMap.put(node.getId(), node);
            nodeMap.put(node.getId(), node);
        }
    }

    Map<String, Object> getElementMap() { return elementMap; }
}
