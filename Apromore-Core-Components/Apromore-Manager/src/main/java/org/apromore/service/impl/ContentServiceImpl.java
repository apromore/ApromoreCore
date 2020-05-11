/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.service.impl;

import static java.util.Map.Entry;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.processconfiguration.Configurable;
import com.processconfiguration.ConfigurationAnnotation;
import org.apromore.dao.EdgeRepository;
import org.apromore.dao.ExpressionRepository;
import org.apromore.dao.NodeRepository;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.EdgeAttribute;
import org.apromore.dao.model.Expression;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.NodeAttribute;
import org.apromore.dao.model.Object;
import org.apromore.dao.model.ObjectRef;
import org.apromore.dao.model.ObjectRefAttribute;
import org.apromore.dao.model.Resource;
import org.apromore.dao.model.ResourceRef;
import org.apromore.dao.model.ResourceRefAttribute;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFExpression;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.DirectionEnum;
import org.apromore.graph.canonical.IAttribute;
import org.apromore.graph.canonical.ICPFObjectReference;
import org.apromore.graph.canonical.ICPFResourceReference;
import org.apromore.graph.canonical.INode;
import org.apromore.service.ContentService;
import org.apromore.service.helper.OperationContext;
import org.apromore.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

/**
 * Implementation of the FormatService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ContentServiceImpl implements ContentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentServiceImpl.class);

    @Inject
    private NodeRepository nRepository;
    @Inject
    private EdgeRepository eRepository;
    @Inject
    private ExpressionRepository exRepository;



    /**
     * @see org.apromore.service.ContentService#addNode(org.apromore.graph.canonical.INode, String, java.util.Set, java.util.Set)
     * {@inheritDoc}
     */
    @Override
    public Node addNode(final INode cpfNode, final String graphType, Set<org.apromore.dao.model.Object> objects, Set<Resource> resources) {
        LOGGER.trace("Adding new Node: " + cpfNode.getId());

        try {
            Node node = new Node();
            node.setName(cpfNode.getName());
            node.setUri(cpfNode.getId());
            node.setOriginalId(cpfNode.getOriginalId());
            node.setNetId(cpfNode.getNetId());
            node.setGraphType(graphType);
            node.setNodeType(cpfNode.getNodeType());
            node.setAllocation(cpfNode.getAllocation());
            node.setConfiguration(cpfNode.isConfigurable());
            node.setTeamWork(cpfNode.isTeamWork());
            if (cpfNode.getTimeDate() != null) {
                node.setTimeDate(cpfNode.getTimeDate().getTime());
            }
            if (cpfNode.getTimeDuration() != null) {
                node.setTimeDuration(cpfNode.getTimeDuration());
            }
            if (cpfNode.getDirection() != null) {
                node.setMessageDirection(DirectionEnum.valueOf(cpfNode.getDirection().toString()));
            }

            addNodeExpressions(cpfNode, node);
            addObjectReferences(node, cpfNode, objects);
            addResourceReferences(node, cpfNode, resources);
            addNodeAttributes(node, cpfNode);

            return nRepository.save(node);
        } catch (Exception e) {
            LOGGER.error("Unable to add Node(" + cpfNode.getId() + "): " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * @see org.apromore.service.ContentService#addEdge(org.apromore.graph.canonical.CPFEdge, org.apromore.dao.model.FragmentVersion, org.apromore.service.helper.OperationContext)
     * {@inheritDoc}
     */
    @Override
    public Edge addEdge(final CPFEdge cpfEdge, FragmentVersion fv, OperationContext op) {
        LOGGER.trace("Adding new Edge " + cpfEdge.getOriginalId() + " id=" + cpfEdge.getId() + " attributes=" + cpfEdge.getAttributes());

        try {
            Edge edge = new Edge();
            edge.setUri(cpfEdge.getId());
            edge.setOriginalId(cpfEdge.getOriginalId());

            edge.setSourceNode(nRepository.findNodeByUriAndFragmentVersion(cpfEdge.getSource().getId(), fv.getId()));
            if (edge.getSourceNode() == null) {
                edge.setSourceNode(findNode(op.getPersistedNodes().values(), cpfEdge.getSource().getId()));
            }

            edge.setTargetNode(nRepository.findNodeByUriAndFragmentVersion(cpfEdge.getTarget().getId(), fv.getId()));
            if (edge.getTargetNode() == null) {
                edge.setTargetNode(findNode(op.getPersistedNodes().values(), cpfEdge.getTarget().getId()));
            }

            if (cpfEdge.getConditionExpr() != null) {
                Expression expr = new Expression();
                expr.setDescription(cpfEdge.getConditionExpr().getDescription());
                expr.setExpression(cpfEdge.getConditionExpr().getExpression());
                expr.setLanguage(cpfEdge.getConditionExpr().getLanguage());
                expr.setReturnType(cpfEdge.getConditionExpr().getReturnType());
                edge.setConditionExpression(exRepository.save(expr));
            }

            addEdgeAttributes(edge, cpfEdge);

            if (edge.getSourceNode() == null || edge.getTargetNode() == null) {
                LOGGER.error("Either the Source or Target nodes are null. Please check!");
            }

            return eRepository.save(edge);
        } catch (Exception e) {
            LOGGER.error("Unable to add Edge(" + cpfEdge.getOriginalId() + "): " + e.getMessage());
        }
        return null;
    }


    private void addNodeAttributes(final Node node, final INode v) {
        NodeAttribute nAtt;
        for (Entry<String, IAttribute> e : v.getAttributes().entrySet()) {
            nAtt = new NodeAttribute();
            nAtt.setName(e.getKey());
            nAtt.setValue(e.getValue().getValue());
            java.lang.Object any = e.getValue().getAny();
            if (any instanceof Element) {
                nAtt.setAny(XMLUtils.anyElementToString((Element) any));
            }
            else if (any instanceof Configurable ||
                     any instanceof ConfigurationAnnotation) {
                nAtt.setAny(XMLUtils.extensionElementToString(any));
            }
            else if (any != null) {
                throw new IllegalArgumentException("Parsed an unsupported extension: " + any);
            }
            nAtt.setNode(node);
            node.getAttributes().add(nAtt);
        }
    }


    /* Add the Object References to the Node. */
    private void addObjectReferences(Node node, INode cpfNode, Set<Object> objects) {
        ObjectRef objectRef;
        if (cpfNode.getObjectReferences() != null) {
            for (ICPFObjectReference cpfObjRef : cpfNode.getObjectReferences()) {
                if (cpfObjRef.getObjectId() != null) {
                    objectRef = new ObjectRef();

                    objectRef.setNode(node);
                    objectRef.setConsumed(cpfObjRef.isConsumed());
                    objectRef.setOptional(cpfObjRef.isOptional());
                    objectRef.setType(cpfObjRef.getObjectRefType());
                    objectRef.setObject(findObject(objects, cpfObjRef.getObjectId()));
                    if (objectRef.getObject() != null) {
                        node.getObjectRefs().add(objectRef);
                        addObjectAttributes(objectRef, cpfObjRef);
                    } else {
                        LOGGER.warn("Object Reference for Node(" + cpfNode.getId() + ") not created, Could not find Object with id: " +
                                cpfObjRef.getObjectId());
                    }
                } else {
                    LOGGER.warn("Object Reference for Node(" + cpfNode.getId() + ") found with a NULL Object ID. Not Adding Reference");
                }
            }
        }
    }

    /* Add Attributes to the Object Reference. */
    private void addObjectAttributes(final ObjectRef obj, final ICPFObjectReference cObj) {
        ObjectRefAttribute oAtt;
        for (Map.Entry<String, IAttribute> e : cObj.getAttributes().entrySet()) {
            oAtt = new ObjectRefAttribute();
            oAtt.setName(e.getKey());
            oAtt.setValue(e.getValue().getValue());
            if (e.getValue().getAny() instanceof Element) {
                oAtt.setAny(XMLUtils.anyElementToString((Element) e.getValue().getAny()));
            }
            oAtt.setObjectRef(obj);

            obj.getObjectRefAttributes().add(oAtt);
        }
    }

    /* Add the Resource Reference to the Node. */
    private void addResourceReferences(Node node, INode cpfNode, Set<Resource> resources) {
        ResourceRef resourceRef;
        if (cpfNode.getResourceReferences() != null) {
            for (ICPFResourceReference cpfResRef : cpfNode.getResourceReferences()) {
                if (cpfResRef.getResourceId() != null) {
                    resourceRef = new ResourceRef();

                    resourceRef.setNode(node);
                    resourceRef.setQualifier(cpfResRef.getQualifier());
                    resourceRef.setResource(findResourceType(resources, cpfResRef.getResourceId()));
                    if (resourceRef.getResource() != null) {
                        node.getResourceRefs().add(resourceRef);
                        addResourceAttributes(resourceRef, cpfResRef);
                    } else {
                        LOGGER.warn("Resource Reference for Node(" + cpfNode.getId() + ") not created, Could not find Resource with id: " +
                            cpfResRef.getResourceId());
                    }
                } else {
                    LOGGER.warn("Resource Reference for Node(" + cpfNode.getId() + ") found with a NULL Resource ID. Not Adding Reference");
                }
            }
        }
    }

    /* Add Attributes to the Object Reference. */
    private void addResourceAttributes(final ResourceRef resourceRef, final ICPFResourceReference cpfResRef) {
        ResourceRefAttribute resourceAttribute;
        for (Map.Entry<String, IAttribute> e : cpfResRef.getAttributes().entrySet()) {
            resourceAttribute = new ResourceRefAttribute();
            resourceAttribute.setName(e.getKey());
            resourceAttribute.setValue(e.getValue().getValue());
            if (e.getValue().getAny() instanceof Element) {
                resourceAttribute.setAny(XMLUtils.anyElementToString((Element) e.getValue().getAny()));
            }
            resourceAttribute.setResourceRef(resourceRef);

            resourceRef.getResourceRefAttributes().add(resourceAttribute);
        }
    }

    /* Adds the Expression to a Node. */
    private void addNodeExpressions(INode cpfNode, Node node) {
        Expression expr;
        if (cpfNode.getInputExpr() != null) {
            for (CPFExpression cpfExpression : cpfNode.getInputExpr()) {
                expr = new Expression();
                expr.setDescription(cpfExpression.getDescription());
                expr.setExpression(cpfExpression.getExpression());
                expr.setLanguage(cpfExpression.getLanguage());
                expr.setReturnType(cpfExpression.getReturnType());
                expr.setInputNode(node);
                node.getInputExpressions().add(exRepository.save(expr));
            }
        }
        if (cpfNode.getOutputExpr() != null) {
            for (CPFExpression cpfExpression : cpfNode.getOutputExpr()) {
                expr = new Expression();
                expr.setDescription(cpfExpression.getDescription());
                expr.setExpression(cpfExpression.getExpression());
                expr.setLanguage(cpfExpression.getLanguage());
                expr.setReturnType(cpfExpression.getReturnType());
                expr.setOutputNode(node);
                node.getOutputExpressions().add(exRepository.save(expr));
            }
        }
        if (cpfNode.getTimeExpression() != null) {
            expr = new Expression();
            expr.setDescription(cpfNode.getTimeExpression().getDescription());
            expr.setExpression(cpfNode.getTimeExpression().getExpression());
            expr.setLanguage(cpfNode.getTimeExpression().getLanguage());
            expr.setReturnType(cpfNode.getTimeExpression().getReturnType());
            node.setTimerExpression(exRepository.save(expr));
        }
        if (cpfNode.getResourceDataExpr() != null) {
            expr = new Expression();
            expr.setDescription(cpfNode.getResourceDataExpr().getDescription());
            expr.setExpression(cpfNode.getResourceDataExpr().getExpression());
            expr.setLanguage(cpfNode.getResourceDataExpr().getLanguage());
            expr.setReturnType(cpfNode.getResourceDataExpr().getReturnType());
            node.setResourceDataExpression(exRepository.save(expr));
        }
        if (cpfNode.getResourceRuntimeExpr() != null) {
            expr = new Expression();
            expr.setDescription(cpfNode.getResourceRuntimeExpr().getDescription());
            expr.setExpression(cpfNode.getResourceRuntimeExpr().getExpression());
            expr.setLanguage(cpfNode.getResourceRuntimeExpr().getLanguage());
            expr.setReturnType(cpfNode.getResourceRuntimeExpr().getReturnType());
            node.setResourceRunExpression(exRepository.save(expr));
        }
    }


    /* Add the Attributes to an Edge. */
    private void addEdgeAttributes(final Edge edge, final CPFEdge cpfEdge) {
        EdgeAttribute edgeAttribute;
        for (Entry<String, IAttribute> e : cpfEdge.getAttributes().entrySet()) {
            edgeAttribute = new EdgeAttribute();
            edgeAttribute.setName(e.getKey());
            edgeAttribute.setValue(e.getValue().getValue());
            java.lang.Object any = e.getValue().getAny();
            if (any instanceof Element) {
                edgeAttribute.setAny(XMLUtils.anyElementToString((Element) any));
            }
            else if (any instanceof ConfigurationAnnotation) {
                edgeAttribute.setAny(XMLUtils.extensionElementToString(any));
            }
            edgeAttribute.setEdge(edge);
            edge.getAttributes().add(edgeAttribute);
        }
    }




    /**
     * Used specifically for the Cancel Nodes and Edges.
     * @see ContentService#updateCancelNodes(org.apromore.service.helper.OperationContext)
     */
    public void updateCancelNodes(OperationContext operationContext) {
        if (operationContext != null) {
            if ((operationContext.getCpfNodes() != null && operationContext.getCpfEdges() != null)) {
                for (CPFNode cpfNode : operationContext.getCpfNodes()) {
                    addCancelNodes(operationContext, cpfNode, operationContext.getPersistedNodes().get(cpfNode.getId()) /*findNode(operationContext.getNodes(), cpfNode.getId())*/);
                    addCancelEdges(operationContext, cpfNode, operationContext.getPersistedNodes().get(cpfNode.getId()) /*findNode(operationContext.getNodes(), cpfNode.getId())*/);
                }
            }
        }
    }

    /* Adds the links to the Cancel Nodes for this task. */
    private void addCancelNodes(OperationContext operationContext, CPFNode cpfNode, Node node) {
        if (cpfNode.getCancelNodes() != null) {
            for (String nodeId : cpfNode.getCancelNodes()) {
                node.getCancelNodes().add(operationContext.getPersistedNodes().get(nodeId) /*findNode(operationContext.getNodes(), nodeId)*/);
                nRepository.save(node);
            }
        }
    }

    /* Adds the links to the Cancel Edges for this task.  */
    private void addCancelEdges(OperationContext operationContext, CPFNode cpfNode, Node node) {
        if (cpfNode.getCancelEdges() != null) {
            for (String edgeId : cpfNode.getCancelEdges()) {
                node.getCancelEdges().add(operationContext.getPersistedEdges().get(edgeId) /*findEdge(operationContext.getEdges(), edgeId)*/);
                nRepository.save(node);
            }
        }
    }


    /* Given the ObjectId, it finds the Object record and returns it. */
    private org.apromore.dao.model.Object findObject(Collection<Object> objects, String objectId) {
        org.apromore.dao.model.Object found = null;
        for (Object obj : objects) {
            if (obj.getUri().equals(objectId)) {
                found = obj;
                break;
            }
        }
        if (found == null) {
            LOGGER.warn("Could not find Object with Id: " + objectId);
        }
        return found;
    }

    /* Given the ResourceId, it finds the Resource record and returns it. */
    private Resource findResourceType(Collection<Resource> resources, String resourceId) {
        Resource found = null;
        for (Resource res : resources) {
            if (res.getUri().equals(resourceId)) {
                found = res;
                break;
            }
        }
        if (found == null) {
            LOGGER.warn("Could not find Resource with Id: " + resourceId);
        }
        return found;
    }

    /* Given the the NodeId, it find the Node record and returns it. */
    private Node findNode(Collection<Node> nodes, String nodeId) {
        Node found = null;
        for (Node node : nodes) {
            if (node.getUri().equals(nodeId)) {
                found = node;
                break;
            }
        }
        if (found == null) {
            LOGGER.warn("Could not find Node with Id: " + nodeId);
        }
        return found;
    }

    /* Given the the edgeId, it find the Edge record and returns it. */
    private Edge findEdge(Collection<Edge> edges, String edgeId) {
        Edge found = null;
        for (Edge edge : edges) {
            if (edge.getUri().equals(edgeId)) {
                found = edge;
                break;
            }
        }
        if (found == null) {
            LOGGER.warn("Could not find Edge with Id: " + edgeId);
        }
        return found;
    }
}
