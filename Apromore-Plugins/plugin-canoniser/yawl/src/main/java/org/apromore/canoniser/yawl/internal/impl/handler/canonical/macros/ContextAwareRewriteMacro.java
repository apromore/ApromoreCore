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
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.XORJoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Macro that is aware of the current Conversion Context.
 * 
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 * 
 */
public abstract class ContextAwareRewriteMacro implements RewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContextAwareRewriteMacro.class);

    private final CanonicalConversionContext context;

    private final Set<String> markNodeDeleted;
    private final Set<String> markEdgeDeleted;

    private final Collection<NodeType> markNodeAdded;
    private final Collection<EdgeType> markEdgeAdded;

    public ContextAwareRewriteMacro(final CanonicalConversionContext context) {
        this.context = context;
        this.markNodeDeleted = new HashSet<String>();
        this.markEdgeDeleted = new HashSet<String>();
        this.markNodeAdded = new ArrayList<NodeType>();
        this.markEdgeAdded = new ArrayList<EdgeType>();
    }

    /**
     * Return the conversion context
     * 
     * @return
     */
    protected CanonicalConversionContext getContext() {
        return context;
    }

    /**
     * The returned UUID will be the a new one for each call.
     * 
     * @return the newly generated UUID
     */
    protected String generateUUID() {
        return this.context.getUuidGenerator().getUUID(null);
    }

    protected void cleanupNet(final NetType net) {
        final Iterator<NodeType> nodeIterator = net.getNode().iterator();

        while (nodeIterator.hasNext()) {
            final NodeType nextNode = nodeIterator.next();
            if (markNodeDeleted.contains(nextNode.getId())) {
                LOGGER.debug("Removing {} with ID {}", nextNode.getClass().getSimpleName(), nextNode.getId());
                nodeIterator.remove();
            }
        }

        for (final NodeType node : markNodeAdded) {
            LOGGER.debug("Adding {} with ID {}", node.getClass().getSimpleName(), node.getId());
            net.getNode().add(node);
        }

        final ListIterator<EdgeType> iterator = net.getEdge().listIterator();
        while (iterator.hasNext()) {
            final EdgeType edge = iterator.next();
            if (markNodeDeleted.contains(edge.getSourceId()) || markNodeDeleted.contains(edge.getTargetId())
                    || markEdgeDeleted.contains(edge.getId())) {
                LOGGER.debug("Removing Edge {}", new String[] { ConversionUtils.toString(edge) });
                iterator.remove();
            }
        }

        for (final EdgeType edge : markEdgeAdded) {
            LOGGER.debug("Adding Edge {} from {} to {}", new String[] { edge.getId(), edge.getSourceId(), edge.getTargetId() });
            net.getEdge().add(edge);
        }

        // Invalidate our Lookup Maps as we've changed Nodes/Edges
        getContext().invalidateCPFCaches();

        // Clearing for next Step
        markEdgeAdded.clear();
        markEdgeDeleted.clear();
        markNodeAdded.clear();
        markNodeDeleted.clear();
    }

    protected void addNodeLater(final NodeType node) {
        markNodeAdded.add(node);
    }

    protected void addEdgeLater(final EdgeType node) {
        markEdgeAdded.add(node);
    }

    protected void deleteNodeLater(final NodeType node) {
        markNodeDeleted.add(node.getId());
    }

    protected void deleteEdgeLater(final EdgeType edge) {
        markEdgeDeleted.add(edge.getId());
    }

    protected TaskType testFollowedByTask(final NodeType node) {
        return testFollowedBy(node, TaskType.class);
    }

    protected MessageType testFollowedByMessage(final NodeType node) {
        return testFollowedBy(node, MessageType.class);
    }

    protected TimerType testFollowedByTimer(final NodeType node) {
        return testFollowedBy(node, TimerType.class);
    }

    protected ANDJoinType testFollowedByANDJoin(final NodeType node) {
        return testFollowedBy(node, ANDJoinType.class);
    }

    protected XORJoinType testFollowedByXORJoin(final NodeType node) {
        return testFollowedBy(node, XORJoinType.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends NodeType> T testFollowedBy(final NodeType node, final Class<T> nodeType) {
        // Be NULL safe for better readability
        if (node == null) {
            return null;
        }
        for (final NodeType nextNode : getContext().getPostSet(node.getId())) {
            if (nodeType.isInstance(nextNode)) {
                return (T) nextNode;
            }
        }
        return null;
    }

    protected EdgeType createEdge(final NodeType sourceNode, final NodeType targetNode) {
        final EdgeType edge = new ObjectFactory().createEdgeType();
        edge.setId(generateUUID());
        edge.setSourceId(sourceNode.getId());
        edge.setTargetId(targetNode.getId());
        LOGGER.debug("Creating Edge from {} to {}", edge.getSourceId(), edge.getTargetId());
        return edge;
    }

}
