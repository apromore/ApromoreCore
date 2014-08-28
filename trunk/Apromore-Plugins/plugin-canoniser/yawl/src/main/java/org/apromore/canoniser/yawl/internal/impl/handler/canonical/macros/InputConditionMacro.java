/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.Collection;
import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.StateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputConditionMacro extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputConditionMacro.class);

    public InputConditionMacro(final CanonicalConversionContext context) {
        super(context);
    }

    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        boolean hasRewritten = false;

        for (final NetType net : cpf.getNet()) {
            Collection<NodeType> sourceNodes = getContext().getSourceNodes(net);
            if (sourceNodes.size() > 1) {
                throw new CanoniserException("InputConditionMacro can not work on a Net with multiple entry nodes!");
            } else {
                NodeType sourceNode = sourceNodes.iterator().next();
                if (sourceNode instanceof EventType) {
                    List<NodeType> sourceNodePostSet = getContext().getPostSet(sourceNode.getId());
                    if (sourceNodePostSet.size() == 1) {
                        NodeType nextNode = sourceNodePostSet.iterator().next();
                        if (nextNode instanceof StateType) {
                            mergeStateWithStartEvent(net, sourceNode, nextNode);
                            hasRewritten = true;
                        }
                    } else {
                        throw new CanoniserException("An event should never have a post set of size > 1 !");
                    }
                } else {
                    hasRewritten = hasRewritten || createArtificialStartEvent(net, sourceNode);
                }
            }

            // Only need to cleanup once, as we are just doing one rewriting per Net
            if (hasRewritten) {
                cleanupNet(net);
            }
        }

        return hasRewritten;
    }

    private void mergeStateWithStartEvent(final NetType net, final NodeType sourceNode, final NodeType nextState) {
        // Delete State of CPF, as the InputCondition in YAWL can have multiple successors (i.e is a State)
        // For the conversion later on we will treat Events like a YAWL Condition. This saves us from rewriting every Event
        deleteNodeLater(nextState);
        // Reconnect Nodes
        List<NodeType> statePostset = getContext().getPostSet(nextState.getId());
        for (NodeType node: statePostset) {
            addEdgeLater(createEdge(sourceNode, node));
        }
    }

    private boolean createArtificialStartEvent(final NetType net, final NodeType sourceNode) {
        // Add start Event before this Node to ensure Net has a Input Condition
        final EventType startEvent = createEvent();
        addNodeLater(startEvent);
        addEdgeLater(createEdge(startEvent, sourceNode));
        LOGGER.info("Adding an artificial start event {} for net {}", net.getId(), ConversionUtils.toString(startEvent));
        getContext().getMessageInterface().addMessage("Adding an artificial start event  {0} for net {1}", net.getId(), ConversionUtils.toString(startEvent));
        return true;
    }

    private EventType createEvent() {
        final ObjectFactory cpfFactory = new ObjectFactory();
        final EventType event = cpfFactory.createEventType();
        event.setId(generateUUID());
        return event;
    }

}
