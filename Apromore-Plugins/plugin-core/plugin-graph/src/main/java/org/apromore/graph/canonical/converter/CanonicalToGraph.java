/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2013 Felix Mannhardt.
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
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
 * #L%
 */

package org.apromore.graph.canonical.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.JoinType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.SplitType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.graph.canonical.AllocationStrategyEnum;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFExpression;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.CPFObject;
import org.apromore.graph.canonical.CPFObjectReference;
import org.apromore.graph.canonical.CPFResource;
import org.apromore.graph.canonical.CPFResourceReference;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.DirectionEnum;
import org.apromore.graph.canonical.HumanTypeEnum;
import org.apromore.graph.canonical.ICPFObject;
import org.apromore.graph.canonical.ICPFObjectReference;
import org.apromore.graph.canonical.ICPFResource;
import org.apromore.graph.canonical.ICPFResourceReference;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.graph.canonical.NonHumanTypeEnum;
import org.apromore.graph.canonical.ObjectRefTypeEnum;
import org.apromore.graph.canonical.ObjectTypeEnum;
import org.apromore.graph.canonical.ResourceTypeEnum;
import org.apromore.graph.util.GraphConstants;
import org.apromore.graph.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * GraphToCanonicalHelper. Used to help build and deconstruct a Graph from the CPF format.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CanonicalToGraph {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanonicalToGraph.class);

    /**
     * Builds a graph from the CPF XSD Format.
     * @param cpf the cpf format from the canoniser.
     * @return the JBpt CPF Graph representation
     */
    public Canonical convert(final CanonicalProcessType cpf) {
        Canonical g = new Canonical();

        g.setName(cpf.getName());
        g.setUri(cpf.getUri());
        g.setVersion(cpf.getVersion());
        g.setAuthor(cpf.getAuthor());
        g.setCreationDate(cpf.getCreationDate());
        g.setModifiedDate(cpf.getModificationDate());

        addAttributes(cpf, g);
        addObjects(cpf, g);
        addResources(cpf, g);
        constructNodeAndEdges(cpf, g);

        return g;
    }


    /* populate the Graph with attributes. */
    private void addAttributes(final CanonicalProcessType cpf, final Canonical g) {
        List<TypeAttribute> ats = cpf.getAttribute();
        for (TypeAttribute attr : ats) {
            g.setProperty(attr.getName(), attr.getValue(), attr.getAny());
        }
    }

    /* populate the Graph with the Objects. */
    private void addObjects(final CanonicalProcessType cpf, final Canonical g) {
        ICPFObject cpfObject;
        for (NetType net : cpf.getNet()) {
            for (ObjectType objectType : net.getObject()) {
                cpfObject = new CPFObject();
                cpfObject.setId(objectType.getId());
                cpfObject.setNetId(net.getId());
                cpfObject.setOriginalId(objectType.getOriginalID());
                if (objectType.getName() != null) {
                    cpfObject.setName(objectType.getName());
                } else {
                    cpfObject.setName(objectType.getId());
                }
                if (objectType.isConfigurable() != null) {
                    cpfObject.setConfigurable(objectType.isConfigurable());
                }
                if (objectType instanceof SoftType) {
                    cpfObject.setObjectType(ObjectTypeEnum.SOFT);
                    cpfObject.setSoftType(((SoftType) objectType).getType());
                } else {
                    cpfObject.setObjectType(ObjectTypeEnum.HARD);
                }

                for (TypeAttribute type : objectType.getAttribute()) {
                    cpfObject.setAttribute(type.getName(), type.getValue(), type.getAny());
                }

                g.getObjects().add(cpfObject);
            }
        }
    }

    /* populate the Graph with the Resources. */
    private void addResources(final CanonicalProcessType cpf, final Canonical g) {
        ICPFResource cpfResource;
        for (ResourceTypeType resourceType : cpf.getResourceType()) {
            cpfResource = new CPFResource();
            cpfResource.setId(resourceType.getId());
            cpfResource.setOriginalId(resourceType.getOriginalID());
            cpfResource.setSpecializationIds(resourceType.getSpecializationIds());
            if (resourceType.getName() != null) {
                cpfResource.setName(resourceType.getName());
            } else {
                cpfResource.setName(resourceType.getId());
            }
            if (resourceType.isConfigurable() != null) {
                cpfResource.setConfigurable(resourceType.isConfigurable());
            }
            if (resourceType instanceof HumanType) {
                cpfResource.setResourceType(ResourceTypeEnum.HUMAN);
                if (((HumanType) resourceType).getType() != null) {
                    cpfResource.setHumanType(HumanTypeEnum.fromValue(((HumanType) resourceType).getType().value()));
                }
            } else if (resourceType instanceof NonhumanType) {
                cpfResource.setResourceType(ResourceTypeEnum.NONHUMAN);
                if (((NonhumanType) resourceType).getType() != null) {
                    cpfResource.setNonHumanType(NonHumanTypeEnum.fromValue(((NonhumanType) resourceType).getType().value()));
                }
            }

            for (TypeAttribute type : resourceType.getAttribute()) {
                cpfResource.setAttribute(type.getName(), type.getValue(), type.getAny());
            }

            g.getResources().add(cpfResource);
        }
    }


    /* Add a node to the graph, could be of any of the types */
    private void constructNodeAndEdges(final CanonicalProcessType cpf, final Canonical g) {
        Map<String, CPFNode> flow = new HashMap<>();
        for (NetType net : cpf.getNet()) {
            flow.putAll(buildNodeListFromNet(net.getId(), net.getNode(), g));
            buildEdges(net.getEdge(), g, flow);
        }
    }


    /* Build the Node list for a single Net */
    private Map<String, CPFNode> buildNodeListFromNet(final String netId, final List<NodeType> nodes, final Canonical g) {
        Map<String, CPFNode> flow = new HashMap<>();
        for (NodeType node : nodes) {
            CPFNode output = new CPFNode();
            output.setGraph(g);
            populateNodeDetails(output, node, netId);

            if (node instanceof WorkType) {
                output = populateWorkDetails(output, (WorkType) node);
                if (node instanceof EventType) {
                    output.setNodeType(NodeTypeEnum.EVENT);
                    if (node instanceof MessageType) {
                        output = populateMessageDetails(output, (MessageType) node);
                    } else if (node instanceof TimerType) {
                        output = populateTimerDetails(output, (TimerType) node);
                    }
                } else if (node instanceof TaskType) {
                    output = populateTaskDetails(output, (TaskType) node);
                } else {
                    LOGGER.warn("Unknown Work Type in parsing CPF: " + node.getClass().toString());
                }
            } else if (node instanceof StateType) {
                output.setNodeType(NodeTypeEnum.STATE);
                output.setName(NodeTypeEnum.STATE.value());
            } else if (node instanceof SplitType) {
                if (node instanceof ORSplitType) {
                    output.setNodeType(NodeTypeEnum.ORSPLIT);
                    output.setName(NodeTypeEnum.ORSPLIT.value());
                } else if (node instanceof XORSplitType) {
                    output.setNodeType(NodeTypeEnum.XORSPLIT);
                    output.setName(NodeTypeEnum.XORSPLIT.value());
                } else if (node instanceof ANDSplitType) {
                    output.setNodeType(NodeTypeEnum.ANDSPLIT);
                    output.setName(NodeTypeEnum.ANDSPLIT.value());
                } else {
                    LOGGER.warn("Unknown Split Type in parsing CPF: " + node.getClass().toString());
                }
            } else if (node instanceof JoinType) {
                if (node instanceof ORJoinType) {
                    output.setNodeType(NodeTypeEnum.ORJOIN);
                    output.setName(NodeTypeEnum.ORJOIN.value());
                } else if (node instanceof XORJoinType) {
                    output.setNodeType(NodeTypeEnum.XORJOIN);
                    output.setName(NodeTypeEnum.XORJOIN.value());
                } else if (node instanceof ANDJoinType) {
                    output.setNodeType(NodeTypeEnum.ANDJOIN);
                    output.setName(NodeTypeEnum.ANDJOIN.value());
                } else {
                    LOGGER.warn("Unknown Join Type in parsing CPF: " + node.getClass().toString());
                }
            } else {
                LOGGER.warn("Unknown Node Type in parsing CPF: " + node.getClass().toString());
            }

            flow.put(node.getId(), output);
        }
        return flow;
    }

    /* Populate the Node with the standard Node details. */
    private void populateNodeDetails(CPFNode output, NodeType node, String netId) {
        output.setId(node.getId());
        output.setOriginalId(node.getOriginalID());
        output.setNetId(netId);
        output.setName(node.getName());
        addAttributes(output, node);
    }

    /* Populate the Node with the Work Node details. */
    private CPFNode populateWorkDetails(CPFNode output, WorkType node) {
        if (node.isTeamWork() != null) {
            output.setTeamWork(node.isTeamWork());
        }
        if (node.getAllocationStrategy() != null) {
            output.setAllocation(AllocationStrategyEnum.fromValue(node.getAllocationStrategy().value()));
        }

        if (node.getFilterByDataExpr() != null) {
            CPFExpression resDataExpr = new CPFExpression();
            resDataExpr.setDescription(node.getFilterByDataExpr().getDescription());
            resDataExpr.setExpression(node.getFilterByDataExpr().getExpression());
            resDataExpr.setLanguage(node.getFilterByDataExpr().getLanguage());
            resDataExpr.setReturnType(node.getFilterByDataExpr().getReturnType());
            output.setResourceDataExpr(resDataExpr);
        }

        if (node.getFilterByRuntimeExpr() != null) {
            CPFExpression resRunExpr = new CPFExpression();
            resRunExpr.setDescription(node.getFilterByRuntimeExpr().getDescription());
            resRunExpr.setExpression(node.getFilterByRuntimeExpr().getExpression());
            resRunExpr.setLanguage(node.getFilterByRuntimeExpr().getLanguage());
            resRunExpr.setReturnType(node.getFilterByRuntimeExpr().getReturnType());
            output.setResourceDataExpr(resRunExpr);
        }

        addCancelNodes(output, node);
        addCancelEdges(output, node);
        addInputExpression(output, node);
        addOutputExpression(output, node);
        addObjectReferences(output, node);
        addResourceReferences(output, node);

        return output;
    }

    /* Populate the Node with the Task Node details. */
    private CPFNode populateTaskDetails(CPFNode output, TaskType node) {
        output.setNodeType(NodeTypeEnum.TASK);
        if (node.isConfigurable() != null) {
            output.setConfigurable(node.isConfigurable());
        }
        if (node.getSubnetId() != null) {
            output.setSubNetId(node.getSubnetId());
            output.setExternal(true);
        }
        return output;
    }

    /* Populate the Node with the Message Node details. */
    private CPFNode populateMessageDetails(CPFNode output, MessageType node) {
        output.setNodeType(NodeTypeEnum.MESSAGE);
        if (node.getDirection() != null) {
            output.setDirection(DirectionEnum.valueOf(node.getDirection().toString()));
        }
        return output;
    }

    /* Populate the Node with the Timer Node details. */
    private CPFNode populateTimerDetails(CPFNode output, TimerType node) {
        output.setNodeType(NodeTypeEnum.TIMER);
        if (node.getTimeDuration() != null) {
            output.setTimeDuration(node.getTimeDuration().toString());
        }
        if (node.getTimeDate() != null) {
            output.setTimeDate(node.getTimeDate().toGregorianCalendar());
        }
        if (node.getTimeExpression() != null) {
            CPFExpression expr = new CPFExpression();
            expr.setDescription(node.getTimeExpression().getDescription());
            expr.setExpression(node.getTimeExpression().getExpression());
            expr.setLanguage(node.getTimeExpression().getLanguage());
            expr.setReturnType(node.getTimeExpression().getReturnType());
            output.setTimeExpression(expr);
        }
        return output;
    }


    /* Adds the Attributes to the Node */
    private void addAttributes(final CPFNode n, final NodeType node) {
        for (TypeAttribute attr : node.getAttribute()) {
            n.addAttribute(attr.getName(), attr.getValue(), attr.getAny());
        }
    }

    /* Adds the Input Expressions to the Node */
    private void addInputExpression(final CPFNode n, final WorkType node) {
        CPFExpression input;
        for (InputExpressionType inExpr : node.getInputExpr()) {
            input = new CPFExpression();
            input.setExpression(inExpr.getExpression());
            input.setLanguage(inExpr.getLanguage());
            input.setDescription(inExpr.getDescription());
            input.setReturnType(inExpr.getReturnType());
            n.addInputExpr(input);
        }
    }

    /* Adds the Input Expressions to the Node */
    private void addOutputExpression(final CPFNode n, final WorkType node) {
        CPFExpression output;
        for (OutputExpressionType outExpr : node.getOutputExpr()) {
            output = new CPFExpression();
            output.setExpression(outExpr.getExpression());
            output.setLanguage(outExpr.getLanguage());
            output.setDescription(outExpr.getDescription());
            output.setReturnType(outExpr.getReturnType());
            n.addOutputExpr(output);
        }
    }

    /* Add the Cancel Nodes. */
    private void addCancelNodes(final CPFNode work, final WorkType node) {
        for (CancellationRefType canType : node.getCancelNodeId()) {
            work.addCancelNode(canType.getRefId());
        }
    }

    /* Add the Cancel Edges. */
    private void addCancelEdges(final CPFNode work, final WorkType node) {
        for (CancellationRefType canType : node.getCancelNodeId()) {
            work.addCancelEdge(canType.getRefId());
        }
    }

    private void addObjectReferences(CPFNode output, WorkType node) {
        ICPFObjectReference objectReference;
        for (ObjectRefType objectRefType : node.getObjectRef()) {
            objectReference = new CPFObjectReference();
            if (objectRefType.getId() != null) {
                objectReference.setId(objectRefType.getId());
            } else {
                objectReference.setId(UUID.randomUUID().toString());
            }
            objectReference.setObjectId(objectRefType.getObjectId());
            objectReference.setOptional(objectRefType.isOptional());
            objectReference.setConsumed(objectRefType.isConsumed());
            if (objectRefType.getType() != null) {
                objectReference.setObjectRefType(ObjectRefTypeEnum.fromValue(objectRefType.getType().value()));
            }

            for (TypeAttribute type : objectRefType.getAttribute()) {
                objectReference.setAttribute(type.getName(), type.getValue(), type.getAny());
            }

            output.addObjectReference(objectReference);
        }
    }

    private void addResourceReferences(CPFNode output, WorkType node) {
        ICPFResourceReference resourceReference;
        for (ResourceTypeRefType resourceRef : node.getResourceTypeRef()) {
            resourceReference = new CPFResourceReference();
            if (resourceRef.getId() != null) {
                resourceReference.setId(resourceRef.getId());
            } else {
                resourceReference.setId(UUID.randomUUID().toString());
            }
            resourceReference.setResourceId(resourceRef.getResourceTypeId());
            resourceReference.setQualifier(resourceRef.getQualifier());

            for (TypeAttribute type : resourceRef.getAttribute()) {
                resourceReference.setAttribute(type.getName(), type.getValue(), type.getAny());
            }

            output.addResourceReference(resourceReference);
        }
    }


    /* Builds the list of Edges for this Model */
    private void buildEdges(final List<EdgeType> edgeTypes, final Canonical graph, final Map<String, CPFNode> nodes) {
        CPFNode source;
        CPFNode target;
        CPFExpression expr;

        for (EdgeType edge : edgeTypes) {
            source = nodes.get(edge.getSourceId());
            target = nodes.get(edge.getTargetId());

            if (source != null && target != null) {
                CPFEdge cpfEdge = graph.addEdge(edge.getId(), source, target);
                if (cpfEdge == null) {
                    LOGGER.warn("Edge " + edge.getId() + " from " + edge.getSourceId() + " to " + edge.getTargetId() + " couldn't be converted from canonical graph to CPF");
                    continue;
                }

                for (TypeAttribute attribute : edge.getAttribute()) {
                    cpfEdge.addAttribute(attribute.getName(), attribute.getValue(), attribute.getAny());
                }

                if (edge.getConditionExpr() != null) {
                    expr = new CPFExpression();
                    expr.setDescription(edge.getConditionExpr().getDescription());
                    expr.setExpression(edge.getConditionExpr().getExpression());
                    expr.setLanguage(edge.getConditionExpr().getLanguage());
                    expr.setReturnType(edge.getConditionExpr().getReturnType());
                    cpfEdge.setConditionExpr(expr);
                }

                if (edge.isDefault()) {
                    cpfEdge.setDefault(true);
                }

                graph.setNodeProperty(source.getId(), GraphConstants.TYPE, GraphUtil.getType(source));
                graph.setNodeProperty(target.getId(), GraphConstants.TYPE, GraphUtil.getType(target));
            } else {
                if (source == null) {
                    LOGGER.warn("Edge " + edge.getId() + " source " + edge.getSourceId() + " doesn't occur in the same process.");
                }
                if (target == null) {
                    LOGGER.warn("Edge " + edge.getId() + " target " + edge.getTargetId() + " doesn't occur in the same process.");
                }
            }
        }
    }

}
