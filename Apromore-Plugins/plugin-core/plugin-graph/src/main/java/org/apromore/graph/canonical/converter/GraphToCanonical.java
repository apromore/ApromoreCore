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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apromore.cpf.ANDJoinType;
import org.apromore.cpf.ANDSplitType;
import org.apromore.cpf.AllocationStrategyEnum;
import org.apromore.cpf.CancellationRefType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.ConditionExpressionType;
import org.apromore.cpf.DirectionEnum;
import org.apromore.cpf.EdgeType;
import org.apromore.cpf.EventType;
import org.apromore.cpf.HardType;
import org.apromore.cpf.HumanType;
import org.apromore.cpf.HumanTypeEnum;
import org.apromore.cpf.InputExpressionType;
import org.apromore.cpf.InputOutputType;
import org.apromore.cpf.MessageType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.NonhumanType;
import org.apromore.cpf.NonhumanTypeEnum;
import org.apromore.cpf.ORJoinType;
import org.apromore.cpf.ORSplitType;
import org.apromore.cpf.ObjectRefType;
import org.apromore.cpf.ObjectType;
import org.apromore.cpf.OutputExpressionType;
import org.apromore.cpf.ResourceDataFilterExpressionType;
import org.apromore.cpf.ResourceRuntimeFilterExpressionType;
import org.apromore.cpf.ResourceTypeRefType;
import org.apromore.cpf.ResourceTypeType;
import org.apromore.cpf.SoftType;
import org.apromore.cpf.StateType;
import org.apromore.cpf.TaskType;
import org.apromore.cpf.TimerExpressionType;
import org.apromore.cpf.TimerType;
import org.apromore.cpf.TypeAttribute;
import org.apromore.cpf.WorkType;
import org.apromore.cpf.XORJoinType;
import org.apromore.cpf.XORSplitType;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFExpression;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.IAttribute;
import org.apromore.graph.canonical.ICPFObject;
import org.apromore.graph.canonical.ICPFObjectReference;
import org.apromore.graph.canonical.ICPFResource;
import org.apromore.graph.canonical.ICPFResourceReference;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.graph.canonical.ObjectTypeEnum;
import org.apromore.graph.canonical.ResourceTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * GraphToCanonicalHelper. Used to help build and deconstruct a Graph from the CPF format.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class GraphToCanonical {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphToCanonical.class);


    /**
     * Builds a Canonical Format Type from the graph.
     * @param graph the cpf format from the canoniser.
     * @return the CPF Ty[pe from the Graph
     */
    public CanonicalProcessType convert(final Canonical graph) {
        CanonicalProcessType c = new CanonicalProcessType();

        c.setName(graph.getName());
        c.setUri(graph.getVersion());
        c.setVersion(graph.getVersion());
        c.setAuthor(graph.getAuthor());
        c.setCreationDate(graph.getCreationDate());
        c.setModificationDate(graph.getModifiedDate());

        addAttributesToCpf(graph, c);
        addResourcesToCpf(graph, c);

        buildNets(graph, c);

        return c;
    }


    /* Build the Nets used in the Canonical Format from the graph. */
    private void buildNets(final Canonical graph, final CanonicalProcessType c) {
        NetType net;
        Map<String, NetData> netData = buildNetNodeEdgeMap(graph);

        for (String netId : netData.keySet()) {
            net = new NetType();
            net.setId(netId);

            addObjectsToNet(net, graph, netData.get(netId).getNodes());
            for (CPFNode node : netData.get(netId).getNodes()) {
                buildNode(net, node);
            }
            for (CPFEdge edge : netData.get(netId).getEdges()) {
                buildEdge(net, edge);
            }

            c.getNet().add(net);
        }
    }


    /* Builds a list of Nodes and Edges corresponding to a Net Id. */
    private Map<String, NetData> buildNetNodeEdgeMap(Canonical graph) {
        NetData netData;
        Map<String, NetData> data = new HashMap<>();
        for (CPFEdge edge : graph.getEdges()) {
            if (!data.containsKey(edge.getSource().getNetId())) {
                netData = new NetData();
            } else {
                netData = data.get(edge.getSource().getNetId());
            }
            netData.addEdge(edge);
            netData.addNode(edge.getSource());
            netData.addNode(edge.getTarget());
            data.put(edge.getSource().getNetId(), netData);
        }
        return data;
    }

    /* Add the Attributes from the graph to the CPF. */
    private void addAttributesToCpf(Canonical graph, CanonicalProcessType c) {
        TypeAttribute typeA;
        for (String propName : graph.getProperties().keySet()) {
            typeA = new TypeAttribute();
            typeA.setName(propName);
            typeA.setValue(graph.getProperty(propName).getValue());
            typeA.setAny(graph.getProperty(propName).getAny());
            c.getAttribute().add(typeA);
        }
    }

    /* Add the Resources from the graph to the CPF. */
    private void addResourcesToCpf(Canonical graph, CanonicalProcessType c) {
        for (ICPFResource cpfResource : graph.getResources()) {
            if (cpfResource.getResourceType() != null) {
                if (cpfResource.getResourceType().equals(ResourceTypeEnum.HUMAN)) {
                    c.getResourceType().add(constructHumanResource(cpfResource));
                } else if (cpfResource.getResourceType().equals(ResourceTypeEnum.NONHUMAN)) {
                    c.getResourceType().add(constructNonHumanResource(cpfResource));
                }
            } else {
                c.getResourceType().add(constructResource(cpfResource));
            }
        }
    }

    private NonhumanType constructNonHumanResource(ICPFResource cpfResource) {
        NonhumanType resourceTypeType = new NonhumanType();
        if (cpfResource.getNonHumanType() != null) {
            resourceTypeType.setType(NonhumanTypeEnum.fromValue(cpfResource.getNonHumanType().value()));
        }
        addResourceDetails(cpfResource, resourceTypeType);
        return resourceTypeType;
    }

    private HumanType constructHumanResource(ICPFResource cpfResource) {
        HumanType resourceTypeType = new HumanType();
        if (cpfResource.getHumanType() != null) {
            resourceTypeType.setType(HumanTypeEnum.fromValue(cpfResource.getHumanType().value()));
        }
        addResourceDetails(cpfResource, resourceTypeType);
        return resourceTypeType;
    }

    private ResourceTypeType constructResource(ICPFResource cpfResource) {
        ResourceTypeType resourceTypeType = new ResourceTypeType();
        addResourceDetails(cpfResource, resourceTypeType);
        return resourceTypeType;
    }

    /* Add the Details to the resource. */
    private void addResourceDetails(ICPFResource cpfResource, ResourceTypeType resourceTypeType) {
        resourceTypeType.setId(cpfResource.getId());
        resourceTypeType.setName(cpfResource.getName());
        resourceTypeType.setOriginalID(cpfResource.getOriginalId());
        resourceTypeType.setConfigurable(cpfResource.isConfigurable());
    }


    private void addObjectsToNet(NetType net, Canonical graph, Set<CPFNode> nodes) {
        ICPFObject cpfObject;
        for (CPFNode node : nodes) {
            for (ICPFObjectReference cpfObjRef : node.getObjectReferences()) {
                if (!netContainsObject(net.getObject(), cpfObjRef.getObjectId())) {
                    cpfObject = findObjectType(graph, cpfObjRef.getObjectId());
                    if (cpfObject != null) {
                        if (cpfObject.getObjectType() != null) {
                            if (cpfObject.getObjectType().equals(ObjectTypeEnum.SOFT)) {
                                net.getObject().add(buildSoftTypeObject(cpfObject));
                            } else if (cpfObject.getObjectType().equals(ObjectTypeEnum.HARD)) {
                                net.getObject().add(buildHardTypeObject(cpfObject));
                            }
                        } else {
                            net.getObject().add(buildObject(cpfObject));
                        }
                    } else {
                        LOGGER.warn("Graph contains Object references that don't have a corresponding object. " + cpfObjRef.getObjectId());
                    }
                }
            }
        }
    }

    /* Does the Net Contain an object with this ID? */
    private boolean netContainsObject(List<ObjectType> object, String objectId) {
        boolean result = false;
        for (ObjectType objType : object) {
            if (objType.getId().equals(objectId)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /* Try and Find the Object Type using the ObjectId. */
    private ICPFObject findObjectType(Canonical graph, final String objectId) {
        ICPFObject objectType = null;
        for (ICPFObject cpfObject : graph.getObjects()) {
            if (cpfObject.getId().equals(objectId)) {
                objectType = cpfObject;
                break;
            }
        }
        return objectType;
    }

    /* Build the Soft Object Type. */
    private SoftType buildSoftTypeObject(ICPFObject cpfObject) {
        SoftType objectType = new SoftType();
        objectType.setId(cpfObject.getId());
        objectType.setName(cpfObject.getName());
        objectType.setOriginalID(cpfObject.getOriginalId());
        objectType.setConfigurable(cpfObject.isConfigurable());
        objectType.setType(cpfObject.getSoftType());
        objectType.getAttribute().addAll(buildAttributeList(cpfObject.getAttributes()));
        return objectType;
    }

    /* Build the Hard Object Type. */
    private HardType buildHardTypeObject(ICPFObject cpfObject) {
        HardType objectType = new HardType();
        addObjectDetails(cpfObject, objectType);
        return objectType;
    }

    private ObjectType buildObject(ICPFObject cpfObject) {
        ObjectType objectType = new ObjectType();
        addObjectDetails(cpfObject, objectType);
        return objectType;
    }

    /* Add the Details to the Object. */
    private void addObjectDetails(ICPFObject cpfObject, ObjectType objectType) {
        objectType.setId(cpfObject.getId());
        objectType.setName(cpfObject.getName());
        objectType.setOriginalID(cpfObject.getOriginalId());
        objectType.setConfigurable(cpfObject.isConfigurable());
        objectType.getAttribute().addAll(buildAttributeList(cpfObject.getAttributes()));
    }


    private List<TypeAttribute> buildAttributeList(final Map<String, IAttribute> attributes) {
        TypeAttribute typAtt;
        List<TypeAttribute> atts = new ArrayList<>();
        for (Entry<String, IAttribute> e : attributes.entrySet()) {
            typAtt = new TypeAttribute();
            typAtt.setName(e.getKey());
            typAtt.setValue(e.getValue().getValue());
            typAtt.setAny(e.getValue().getAny());
            atts.add(typAtt);
        }
        return atts;
    }

    /* Builds an Edge for this Net. */
    private void buildEdge(NetType net, final CPFEdge edge) {
        EdgeType edgeType = new EdgeType();
        if (edge.getOriginalId() == null) {
            edgeType.setId(edge.getId());
        } else {
            edgeType.setId(edge.getOriginalId());
        }
        edgeType.setOriginalID(edge.getOriginalId());
        edgeType.setDefault(edge.isDefault());
        if (edge.getConditionExpr() != null) {
            ConditionExpressionType conditionExpr = new ConditionExpressionType();
            conditionExpr.setDescription(edge.getConditionExpr().getDescription());
            conditionExpr.setExpression(edge.getConditionExpr().getExpression());
            conditionExpr.setLanguage(edge.getConditionExpr().getLanguage());
            conditionExpr.setReturnType(edge.getConditionExpr().getReturnType());
            edgeType.setConditionExpr(conditionExpr);
        }
        if (edge.getSource() != null) {
            edgeType.setSourceId(edge.getSource().getId());
        } else {
            LOGGER.warn("Edge(" + edge.getId() + ") found with NULL source.");
        }
        if (edge.getTarget() != null) {
            edgeType.setTargetId(edge.getTarget().getId());
        } else {
            LOGGER.warn("Edge(" + edge.getId() + ") found with NULL target.");
        }

        updateAttributes(edgeType, edge);

        net.getEdge().add(edgeType);
    }

    private void updateAttributes(EdgeType output, CPFEdge edge) {
        TypeAttribute typeAttribute;
        for (Entry<String, IAttribute> attribute : edge.getAttributes().entrySet()) {
            typeAttribute = new TypeAttribute();
            typeAttribute.setName(attribute.getKey());
            typeAttribute.setValue(attribute.getValue().getValue());
            typeAttribute.setAny(attribute.getValue().getAny());
            output.getAttribute().add(typeAttribute);
        }
    }

    /* Builds a Node for this Net. */
    private void buildNode(NetType net, final CPFNode node) {
        if (node.getNodeType() != null ) {
            if (node.getNodeType().equals(NodeTypeEnum.MESSAGE)) {
                net.getNode().add(constructMessageType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.TIMER)) {
                net.getNode().add(constructTimerType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.TASK)) {
                net.getNode().add(constructTaskType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.EVENT)) {
                net.getNode().add(constructEventType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.STATE)) {
                net.getNode().add(constructStateType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.ORJOIN)) {
                net.getNode().add(constructORJoinType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.XORJOIN)) {
                net.getNode().add(constructXORJoinType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.ANDJOIN)) {
                net.getNode().add(constructANDJoinType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.ORSPLIT)) {
                net.getNode().add(constructORSplitType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.XORSPLIT)) {
                net.getNode().add(constructXORSplitType(node));
            } else if (node.getNodeType().equals(NodeTypeEnum.ANDSPLIT)) {
                net.getNode().add(constructANDSplitType(node));
            } else {
                LOGGER.warn("Unknown Node type: " + node.getNodeType());
            }
        } else {
            LOGGER.warn("Unknown Node type: " + node.getNodeType());
        }
    }

    /* Construct the Message Node Type. */
    private NodeType constructMessageType(final CPFNode node) {
        MessageType type = new MessageType();
        updateWorkNodeData(type, node);

        if (node.getDirection() != null) {
            type.setDirection(DirectionEnum.valueOf(node.getDirection().toString()));
        }

        return type;
    }

    /* Construct the Timer Node Type. */
    private NodeType constructTimerType(final CPFNode node) {
        TimerType type = new TimerType();
        updateWorkNodeData(type, node);

        if (node.getTimeDate() != null) {
            XMLGregorianCalendar date;
            try {
                date = DatatypeFactory.newInstance().newXMLGregorianCalendar(node.getTimeDate());
            } catch (DatatypeConfigurationException e) {
                date = null;
            }
            type.setTimeDate(date);
        }
        if (node.getTimeDuration() != null && !node.getTimeDuration().equals("")) {
            Duration duration;
            try {
                duration = DatatypeFactory.newInstance().newDuration(node.getTimeDuration());
            } catch (DatatypeConfigurationException e) {
                duration = null;
            }
            type.setTimeDuration(duration);
        }
        if (node.getTimeExpression() != null) {
            TimerExpressionType timeExpr = new TimerExpressionType();
            timeExpr.setDescription(node.getTimeExpression().getDescription());
            timeExpr.setExpression(node.getTimeExpression().getExpression());
            timeExpr.setLanguage(node.getTimeExpression().getLanguage());
            timeExpr.setReturnType(node.getTimeExpression().getReturnType());
            type.setTimeExpression(timeExpr);
        }

        return type;
    }

    /* Construct the Task Node Type. */
    private NodeType constructTaskType(final CPFNode node) {
        TaskType type = new TaskType();
        updateWorkNodeData(type, node);
        type.setSubnetId(node.getSubNetId());
        return type;
    }

    /* Construct the Event Node Type. */
    private NodeType constructEventType(final CPFNode node) {
        EventType type = new EventType();
        updateWorkNodeData(type, node);
        return type;
    }

    /* Construct the State Node Type. */
    private NodeType constructStateType(final CPFNode node) {
        StateType type = new StateType();
        updateNodeData(type, node);
        return type;
    }

    /* Construct the OR Join Node Type. */
    private NodeType constructORJoinType(final CPFNode node) {
        ORJoinType type = new ORJoinType();
        updateNodeData(type, node);
        return type;
    }

    /* Construct the XOrJoin Node Type. */
    private NodeType constructXORJoinType(final CPFNode node) {
        XORJoinType type = new XORJoinType();
        updateNodeData(type, node);
        return type;
    }

    /* Construct the ANDJoin Node Type. */
    private NodeType constructANDJoinType(final CPFNode node) {
        ANDJoinType type = new ANDJoinType();
        updateNodeData(type, node);
        return type;
    }

    /* Construct the OR Split Node Type. */
    private NodeType constructORSplitType(final CPFNode node) {
        ORSplitType type = new ORSplitType();
        updateNodeData(type, node);
        return type;
    }

    /* Construct the XOrJoin Node Type. */
    private NodeType constructXORSplitType(final CPFNode node) {
        XORSplitType type = new XORSplitType();
        updateNodeData(type, node);
        return type;
    }

    /* Construct the ANDJoin Node Type. */
    private NodeType constructANDSplitType(final CPFNode node) {
        ANDSplitType type = new ANDSplitType();
        updateNodeData(type, node);
        return type;
    }


    private void updateNodeData(NodeType output, final CPFNode node) {
        output.setId(node.getId());
        output.setOriginalID(node.getOriginalId());
        output.setName(node.getName());
        if (node.isConfigurable()) {
            output.setConfigurable(node.isConfigurable());
        }

        updateAttributes(output, node);
    }

    private void updateWorkNodeData(WorkType output, final CPFNode node) {
        updateNodeData(output, node);
        if (node.getAllocation() != null) {
            output.setAllocationStrategy(AllocationStrategyEnum.fromValue(node.getAllocation().value()));
        }
        if (node.getResourceDataExpr() != null) {
            ResourceDataFilterExpressionType resDataExpr = new ResourceDataFilterExpressionType();
            resDataExpr.setDescription(node.getResourceDataExpr().getDescription());
            resDataExpr.setExpression(node.getResourceDataExpr().getExpression());
            resDataExpr.setLanguage(node.getResourceDataExpr().getLanguage());
            resDataExpr.setReturnType(node.getResourceDataExpr().getReturnType());
            output.setFilterByDataExpr(resDataExpr);
        }
        if (node.getResourceRuntimeExpr() != null) {
            ResourceRuntimeFilterExpressionType resRunExpr = new ResourceRuntimeFilterExpressionType();
            resRunExpr.setDescription(node.getResourceRuntimeExpr().getDescription());
            resRunExpr.setExpression(node.getResourceRuntimeExpr().getExpression());
            resRunExpr.setLanguage(node.getResourceRuntimeExpr().getLanguage());
            resRunExpr.setReturnType(node.getResourceRuntimeExpr().getReturnType());
            output.setFilterByRuntimeExpr(resRunExpr);
        }

        updateCancelNodes(output, node);
        updateCancelEdges(output, node);
        updateInputExpressions(output, node);
        updateOutputExpressions(output, node);
        updateObjectReferences(output, node);
        updateResourceReferences(output, node);
    }

    /* Updates the List of Cancel Nodes for a Work Node. */
    private void updateCancelNodes(WorkType output, final CPFNode node) {
        CancellationRefType canRef;
        for (String cancelNode : node.getCancelNodes()) {
            canRef = new CancellationRefType();
            canRef.setRefId(cancelNode);
            output.getCancelNodeId().add(canRef);
        }
    }

    /* Updates the List of Cancel Edges for a Work Node. */
    private void updateCancelEdges(WorkType output, final CPFNode node) {
        CancellationRefType canRef;
        for (String cancelEdge : node.getCancelEdges()) {
            canRef = new CancellationRefType();
            canRef.setRefId(cancelEdge);
            output.getCancelNodeId().add(canRef);
        }
    }

    /* Updates the List of Input Expressions. */
    private void updateInputExpressions(WorkType output, final CPFNode node) {
        InputExpressionType inputExpr;
        for (CPFExpression expression : node.getInputExpr()) {
            inputExpr = new InputExpressionType();
            inputExpr.setExpression(expression.getExpression());
            inputExpr.setLanguage(expression.getLanguage());
            inputExpr.setDescription(expression.getDescription());
            inputExpr.setReturnType(expression.getReturnType());
            output.getInputExpr().add(inputExpr);
        }
    }

    /* Updates the List of Output Expressions. */
    private void updateOutputExpressions(WorkType output, final CPFNode node) {
        OutputExpressionType outputExpr;
        for (CPFExpression expression : node.getOutputExpr()) {
            outputExpr = new OutputExpressionType();
            outputExpr.setExpression(expression.getExpression());
            outputExpr.setLanguage(expression.getLanguage());
            outputExpr.setDescription(expression.getDescription());
            outputExpr.setReturnType(expression.getReturnType());
            output.getOutputExpr().add(outputExpr);
        }
    }

    private void updateObjectReferences(WorkType output, final CPFNode node) {
        ObjectRefType objectRefType;
        for (ICPFObjectReference objectReference : node.getObjectReferences()) {
            objectRefType = new ObjectRefType();
            objectRefType.setId(objectReference.getId());
            objectRefType.setObjectId(objectReference.getObjectId());
            objectRefType.setConsumed(objectReference.isConsumed());
            objectRefType.setOptional(objectReference.isOptional());
            if (objectReference.getObjectRefType() != null) {
                objectRefType.setType(InputOutputType.fromValue(objectReference.getObjectRefType().value()));
            }
            output.getObjectRef().add(objectRefType);
        }
    }

    private void updateResourceReferences(WorkType output, final CPFNode node) {
        ResourceTypeRefType resourceRefType;
        for (ICPFResourceReference resourceReference : node.getResourceReferences()) {
            resourceRefType = new ResourceTypeRefType();
            resourceRefType.setId(resourceReference.getId());
            resourceRefType.setResourceTypeId(resourceReference.getResourceId());
            resourceRefType.setQualifier(resourceReference.getQualifier());
            output.getResourceTypeRef().add(resourceRefType);
        }
    }

    private void updateAttributes(NodeType output, CPFNode node) {
        TypeAttribute typeAttribute;
        for (Entry<String, IAttribute> attribute : node.getAttributes().entrySet()) {
            typeAttribute = new TypeAttribute();
            typeAttribute.setName(attribute.getKey());
            typeAttribute.setValue(attribute.getValue().getValue());
            typeAttribute.setAny(attribute.getValue().getAny());
            output.getAttribute().add(typeAttribute);
        }
    }

    /**
     * Class to store the Nodes and Edges for a particular Net.
     */
    public class NetData {
        private Set<CPFNode> nodes = new HashSet<>();
        private Set<CPFEdge> edges = new HashSet<>();

        public Set<CPFNode> getNodes() {
            return nodes;
        }

        public void setNodes(Set<CPFNode> newNodes) {
            nodes = newNodes;
        }

        public void addNode(CPFNode newNode) {
            nodes.add(newNode);
        }

        public Set<CPFEdge> getEdges() {
            return edges;
        }

        public void setEdges(Set<CPFEdge> newEdges) {
            edges = newEdges;
        }

        public void addEdge(CPFEdge newEdge) {
            edges.add(newEdge);
        }
    }

}
