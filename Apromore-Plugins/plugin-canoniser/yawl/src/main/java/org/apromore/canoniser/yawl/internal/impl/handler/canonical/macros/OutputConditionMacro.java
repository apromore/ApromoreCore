/*
 * Copyright © 2009-2017 The Apromore Initiative.
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

public class OutputConditionMacro extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutputConditionMacro.class);

    public OutputConditionMacro(final CanonicalConversionContext context) {
        super(context);
    }

    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        boolean hasRewritten = false;

        for (final NetType net : cpf.getNet()) {
            Collection<NodeType> sinkNodes = getContext().getSinkNodes(net);
            if (sinkNodes.size() > 1) {
                throw new CanoniserException("OutputConditionMacro can not work on a Net with multiple exit nodes!");
            } else {
                NodeType sinkNode = sinkNodes.iterator().next();
                if (sinkNode instanceof EventType) {
                    List<NodeType> sinkNodePreSet = getContext().getPreSet(sinkNode.getId());
                    if (sinkNodePreSet.size() == 1) {
                        NodeType prevNode = sinkNodePreSet.iterator().next();
                        if (prevNode instanceof StateType) {
                            mergeStateWithExitEvent(net, sinkNode, prevNode);
                            hasRewritten = true;
                        }
                    } else {
                        throw new CanoniserException("An event should never have a post set of size > 1 !");
                    }
                } else {
                    hasRewritten = hasRewritten || createArtificialEndEvent(net, sinkNode);
                }
            }

            // Only need to cleanup once, as we are just doing one rewriting per Net
            if (hasRewritten) {
                cleanupNet(net);
            }
        }

        return hasRewritten;
    }

    private void mergeStateWithExitEvent(final NetType net, final NodeType sinkNode, final NodeType prevState) {
        // Delete State of CPF, as the OutputCondition in YAWL can have multiple predecessors (i.e is a State)
        // For the conversion later on we will treat Events like a YAWL Condition. This saves us from rewriting every Event
        deleteNodeLater(prevState);
        // Reconnect Nodes
        List<NodeType> statePreSet = getContext().getPreSet(prevState.getId());
        for (NodeType node: statePreSet) {
            addEdgeLater(createEdge(node, sinkNode));
        }
    }

    private boolean createArtificialEndEvent(final NetType net, final NodeType sinkNode) {
        // Add end Event after this Node to ensure Net has a Output Condition
        final EventType endEvent = createEvent();
        addNodeLater(endEvent);
        addEdgeLater(createEdge(sinkNode, endEvent));
        LOGGER.info("Ensure Net {} stops with an Ouput Condition, adding End Event {}", net.getId(), ConversionUtils.toString(endEvent));
        return true;
    }

    private EventType createEvent() {
        final ObjectFactory cpfFactory = new ObjectFactory();
        final EventType event = cpfFactory.createEventType();
        event.setId(generateUUID());
        return event;
    }

}
