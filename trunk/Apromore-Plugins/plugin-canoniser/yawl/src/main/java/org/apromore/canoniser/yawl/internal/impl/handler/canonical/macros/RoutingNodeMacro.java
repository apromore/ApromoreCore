/**
 * Copyright 2012, Felix Mannhardt
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.canoniser.yawl.internal.impl.handler.canonical.macros;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.canoniser.yawl.internal.impl.context.CanonicalConversionContext;
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
 * @author <a href="felix.mannhardt@smail.wir.h-brs.de">Felix Mannhardt (Bonn-Rhein-Sieg University oAS)</a>
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
            final ListIterator<NodeType> nodeIterator = net.getNode().listIterator();
            while (nodeIterator.hasNext()) {
                final NodeType node = nodeIterator.next();
                if (node instanceof JoinType) {
                    hasFoundRoutingNodes = true;
                    handleJoinNode(net, nodeIterator, node);
                } else if (node instanceof SplitType) {
                    hasFoundRoutingNodes = true;
                    handleSplitNode(net, nodeIterator, node);
                } else {
                    // Ignore
                }

            }
        }

        if (hasFoundRoutingNodes) {
            getContext().invalidateCPFCaches();
        }

        return hasFoundRoutingNodes;
    }

    private void handleSplitNode(final NetType net, final ListIterator<NodeType> nodeIterator, final NodeType node) throws CanoniserException {
        final List<NodeType> preSet = getContext().getPreSet(node.getId());
        if (preSet.size() != 1) {
            throw new CanoniserException("Split " + node.getId() + " has more than 1 predecessors!");
        } else {
            final NodeType prevNode = preSet.get(0);
            final ControlTypeType splitCode = convertSplitCode(node);
            LOGGER.debug("Rewriting SPLIT of type {}", splitCode.getCode());
            if (prevNode instanceof TaskType) {
                getContext().setElementSplitType(prevNode.getId(), splitCode);
                connectNodeWithPostSet(net.getEdge(), prevNode, getContext().getPostSet(node.getId()));
                nodeIterator.remove();
                LOGGER.debug("Merged with previous Task {}", prevNode.getId());
            } else {
                final TaskType routingTask = convertRoutingToTask((SplitType) node);
                getContext().setElementSplitType(routingTask.getId(), convertSplitCode(node));
                // Just replace the Split with a Task having the same ID. There is no need to re-link the Edges!
                nodeIterator.set(routingTask);
                LOGGER.debug("Added artificial Task {}", routingTask.getId());
            }
        }
    }

    private void connectNodeWithPostSet(final List<EdgeType> edgeList, final NodeType prevNode, final List<NodeType> postSet) {
        final Iterator<EdgeType> edgeIterator = edgeList.iterator();
        final Set<String> postSetIds = new HashSet<String>();
        for (final NodeType node : postSet) {
            postSetIds.add(node.getId());
        }
        while (edgeIterator.hasNext()) {
            final EdgeType edge = edgeIterator.next();
            // First remove edge from original Node
            if (edge.getSourceId().equals(prevNode.getId())) {
                edgeIterator.remove();
            }
            // Connect to next Node
            if (postSetIds.contains(edge.getTargetId())) {
                edge.setSourceId(prevNode.getId());
            }
        }
    }

    private void handleJoinNode(final NetType net, final ListIterator<NodeType> nodeIterator, final NodeType node) throws CanoniserException {
        final List<NodeType> postSet = getContext().getPostSet(node.getId());
        if (postSet.size() != 1) {
            throw new CanoniserException("Join " + node.getId() + " has more than 1 successors!");
        } else {
            final NodeType nextNode = postSet.get(0);
            final ControlTypeType joinCode = convertJoinCode(node);
            LOGGER.debug("Rewriting JOIN of type {}", joinCode.getCode());
            if (nextNode instanceof TaskType) {
                getContext().setElementJoinType(nextNode.getId(), joinCode);
                connectPreSetWithSuccesor(net.getEdge(), getContext().getPreSet(node.getId()), nextNode);
                nodeIterator.remove();
                LOGGER.debug("Merged with next Task {}", nextNode.getId());
            } else {
                final TaskType routingTask = convertRoutingToTask((JoinType) node);
                getContext().setElementJoinType(routingTask.getId(), convertJoinCode(node));
                // Just replace the Join with a Task having the same ID. There is no need to relink the Edges!
                nodeIterator.set(routingTask);
                LOGGER.debug("Added artificial Task {}", routingTask.getId());
            }
        }
    }

    private void connectPreSetWithSuccesor(final List<EdgeType> list, final List<NodeType> preSet, final NodeType nextNode) {
        final Iterator<EdgeType> edgeIterator = list.iterator();
        final Set<String> preSetIds = new HashSet<String>();
        for (final NodeType node : preSet) {
            preSetIds.add(node.getId());
        }
        while (edgeIterator.hasNext()) {
            final EdgeType edge = edgeIterator.next();
            // First remove edge from original Node
            if (edge.getTargetId().equals(nextNode.getId())) {
                edgeIterator.remove();
            }
            // Connect to next Node
            if (preSetIds.contains(edge.getSourceId())) {
                edge.setTargetId(nextNode.getId());
            }
        }
    }

    private ControlTypeType convertJoinCode(final NodeType joinNode) throws CanoniserException {
        final ControlTypeType controlType = getContext().getYawlObjectFactory().createControlTypeType();
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
        final ControlTypeType controlType = getContext().getYawlObjectFactory().createControlTypeType();
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
        task.setId(node.getId());
        task.setOriginalID(node.getOriginalID());
        task.setName(node.getName());
        task.setConfigurable(node.isConfigurable());
        for (final TypeAttribute attr : node.getAttribute()) {
            task.getAttribute().add(attr);
        }
        return task;
    }

}
