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

import java.text.MessageFormat;
import java.util.List;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
import org.apromore.canoniser.yawl.internal.utils.ConversionUtils;
import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.JoinType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectFactory;
import org.apromore.cpf.RoutingType;
import org.apromore.cpf.SplitType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yawlfoundation.yawlschema.ControlTypeCodeType;
import org.yawlfoundation.yawlschema.ControlTypeType;

/**
 * Merges JOIN and SPLIT routing nodes with their pre/suceeding Task nodes. Adds artificial Tasks if there is not Task following a Routing Node.
 *
 * @author <a href="mailto:felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
 *
 */
public class RoutingNodeMacro extends ContextAwareRewriteMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoutingNodeMacro.class.getName());

    public RoutingNodeMacro(final CanonicalConversionContext context) {
        super(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros.RewriteMacro#rewrite(org.apromore.cpf.CanonicalProcessType)
     */
    @Override
    public boolean rewrite(final CanonicalProcessType cpf) throws CanoniserException {
        boolean hasFoundRoutingNodes = false;

        for (final NetType net : cpf.getNet()) {
            // TODO improve by using a "real" graph class instead of cleaning post/preset maps each time
            for (int i = 0; i < net.getNode().size(); i++) {
                final NodeType node = net.getNode().get(i);
                if (node instanceof JoinType) {
                    hasFoundRoutingNodes = true;
                    handleJoinNode((JoinType) node, net);
                    cleanupNet(net);
                    // Restart scanning
                    i = -1;
                } else if (node instanceof SplitType) {
                    hasFoundRoutingNodes = true;
                    handleSplitNode((SplitType) node, net);
                    cleanupNet(net);
                    // Restart scanning
                    i = -1;
                }
            }
        }
        return hasFoundRoutingNodes;
    }

    private void handleSplitNode(final SplitType splitNode, final NetType net) throws CanoniserException {
        final List<NodeType> preSet = getContext().getPreSet(splitNode.getId());
        if (preSet.size() != 1) {
            throw new CanoniserException((MessageFormat.format("Invalid CPF: {0}:{1} has {2}  predecessors! Split nodes MUST have 1 predecessor in CPF! ", splitNode.getClass().getSimpleName(), splitNode.getId(),
                    preSet.size())));
        } else {
            final NodeType prevNode = preSet.get(0);
            if (prevNode instanceof TaskType) {
                replaceSplitNodeBy(splitNode, (TaskType) prevNode, net);
                LOGGER.debug("Merged with previous Task {}", ConversionUtils.toString(prevNode));
            } else {
                // Just replace the Split with a Task
                final TaskType routingTask = convertRoutingToTask(splitNode);
                addNodeLater(routingTask);
                addEdgeLater(createEdge(prevNode, routingTask));
                replaceSplitNodeBy(splitNode, routingTask, net);
                LOGGER.debug("Added artificial Task {}", ConversionUtils.toString(routingTask));
            }
        }
    }

    private void replaceSplitNodeBy(final NodeType splitNode, final TaskType newNode, final NetType net) throws CanoniserException {
        // First mark split node as deleted
        deleteNodeLater(splitNode);

        // Set correct split on new Task
        final ControlTypeType splitCode = convertSplitCode(splitNode);
        LOGGER.debug("Rewriting SPLIT of type {}", splitCode.getCode());
        getContext().getControlFlowContext().setElementSplitType(newNode.getId(), splitCode);

        // Connect the post set of the former split node with the new node
        final List<NodeType> postSet = getContext().getPostSet(splitNode.getId());
        LOGGER.debug("Handling post set of removed SPLIT {}", ConversionUtils.nodesToString(postSet));
        for (final NodeType postNode : postSet) {
            EdgeType newEdge = createEdge(newNode, postNode);
            EdgeType oldEdge = findEdgeBetween(net, splitNode, postNode);
            copyEdge(newEdge, oldEdge);
            addEdgeLater(newEdge);
        }
    }

    private void copyEdge(final EdgeType newEdge, final EdgeType oldEdge) {
        newEdge.setConditionExpr(oldEdge.getConditionExpr());
        newEdge.setDefault(oldEdge.isDefault());
        newEdge.setOriginalID(oldEdge.getOriginalID());
        for (TypeAttribute attr: oldEdge.getAttribute()) {
            newEdge.getAttribute().add(attr);
        }
    }

    private EdgeType findEdgeBetween(final NetType net, final NodeType sourceNode, final NodeType targetNode) {
        for (EdgeType edge: net.getEdge()) {
            if (edge.getSourceId().equals(sourceNode.getId()) && edge.getTargetId().equals(targetNode.getId())) {
                return edge;
            }
        }
        throw new IllegalArgumentException("No edge between source and target node. Invalid use of 'findEdgeBetween'! This is probably a programming error!");
    }

    private void handleJoinNode(final JoinType joinNode, final NetType net) throws CanoniserException {
        final List<NodeType> postSet = getContext().getPostSet(joinNode.getId());
        if (postSet.size() != 1) {
            throw new CanoniserException(MessageFormat.format("Invalid CPF: {0}:{1} has {2}  successors! Join nodes MUST have 1 successor in CPF! ", joinNode.getClass().getSimpleName(), joinNode.getId(),
                    postSet.size()));
        } else {
            final NodeType nextNode = postSet.get(0);
            if (nextNode instanceof TaskType) {
                replaceJoinNodeBy(joinNode, (TaskType) nextNode, net);
                LOGGER.debug("Merged with next Task {}", ConversionUtils.toString(nextNode));
            } else {
                // Just replace the Join with a Task
                final TaskType routingTask = convertRoutingToTask(joinNode);
                addNodeLater(routingTask);
                addEdgeLater(createEdge(routingTask, nextNode));
                replaceJoinNodeBy(joinNode, routingTask, net);
                LOGGER.debug("Added artificial Task {}", ConversionUtils.toString(routingTask));
            }
        }
    }

    private void replaceJoinNodeBy(final NodeType joinNode, final TaskType newNode, final NetType net) throws CanoniserException {
        // First mark split node as deleted
        deleteNodeLater(joinNode);

        // Set correct split on new Task
        final ControlTypeType joinCode = convertJoinCode(joinNode);
        LOGGER.debug("Rewriting JOIN of type {}", joinCode.getCode());
        getContext().getControlFlowContext().setElementJoinType(newNode.getId(), joinCode);

        // Connect the pre set of the former join node with the new node
        final List<NodeType> preSet = getContext().getPreSet(joinNode.getId());
        LOGGER.debug("Handling pre set of removed JOIN {}", ConversionUtils.nodesToString(preSet));
        for (final NodeType preNode : preSet) {
            EdgeType newEdge = createEdge(preNode, newNode);
            EdgeType oldEdge = findEdgeBetween(net, preNode, joinNode);
            copyEdge(newEdge, oldEdge);
            addEdgeLater(newEdge);
        }
    }

    private ControlTypeType convertJoinCode(final NodeType joinNode) throws CanoniserException {
        final ControlTypeType controlType = new org.yawlfoundation.yawlschema.ObjectFactory().createControlTypeType();
        if (joinNode instanceof XORJoinType) {
            controlType.setCode(ControlTypeCodeType.XOR);
        } else if (joinNode instanceof ORJoinType) {
            controlType.setCode(ControlTypeCodeType.OR);
        } else if (joinNode instanceof ANDJoinType) {
            controlType.setCode(ControlTypeCodeType.AND);
        } else {
            throw new CanoniserException("Can not convert JOIN code without JOIN node.");
        }
        return controlType;
    }

    protected ControlTypeType convertSplitCode(final NodeType splitNode) throws CanoniserException {
        final ControlTypeType controlType = new org.yawlfoundation.yawlschema.ObjectFactory().createControlTypeType();
        if (splitNode instanceof XORSplitType) {
            controlType.setCode(ControlTypeCodeType.XOR);
        } else if (splitNode instanceof ORSplitType) {
            controlType.setCode(ControlTypeCodeType.OR);
        } else if (splitNode instanceof ANDSplitType) {
            controlType.setCode(ControlTypeCodeType.AND);
        } else {
            throw new CanoniserException("Can not convert SPLIT code without SPLIT node.");
        }
        return controlType;
    }

    private TaskType convertRoutingToTask(final RoutingType node) {
        final ObjectFactory cF = new ObjectFactory();
        final TaskType task = cF.createTaskType();
        task.setId(generateUUID());
        task.setOriginalID(node.getOriginalID());
        task.setName(node.getName());
        task.setConfigurable(node.isConfigurable());
        for (final TypeAttribute attr : node.getAttribute()) {
            task.getAttribute().add(attr);
        }
        return task;
    }

}
