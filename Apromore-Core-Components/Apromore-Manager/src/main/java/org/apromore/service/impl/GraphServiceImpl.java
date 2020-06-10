/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.service.impl;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.processconfiguration.ConfigurationAnnotation;
import com.processconfiguration.TGatewayType;
import org.apromore.common.Constants;
import org.apromore.dao.EdgeRepository;
import org.apromore.dao.NodeRepository;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.EdgeAttribute;
import org.apromore.dao.model.Expression;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.NodeAttribute;
import org.apromore.dao.model.ObjectAttribute;
import org.apromore.dao.model.ObjectRef;
import org.apromore.dao.model.ObjectRefAttribute;
import org.apromore.dao.model.Resource;
import org.apromore.dao.model.ResourceAttribute;
import org.apromore.dao.model.ResourceRef;
import org.apromore.dao.model.ResourceRefAttribute;
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
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.NodeTypeEnum;
import org.apromore.graph.canonical.NonHumanTypeEnum;
import org.apromore.graph.canonical.ObjectTypeEnum;
import org.apromore.graph.canonical.ResourceTypeEnum;
import org.apromore.service.GraphService;
import org.apromore.util.FragmentUtil;
import org.apromore.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the GraphService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class GraphServiceImpl implements GraphService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphServiceImpl.class);

    private EdgeRepository edgeRepo;
    private NodeRepository nodeRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param edgeRepository Edge Repository.
     * @param nodeRepository Node repository.
     */
    @Inject
    public GraphServiceImpl(final EdgeRepository edgeRepository, final NodeRepository nodeRepository) {
        edgeRepo = edgeRepository;
        nodeRepo = nodeRepository;
    }



    @Override
    @Transactional(readOnly = true)
    public Canonical fillNodesByFragment(final Canonical procModelGraph, final String fragmentURI) {
        INode v;
        List<Node> nodes = nodeRepo.getNodesByFragmentURI(fragmentURI);
        for (Node node : nodes) {
            v = buildNodeByType(node, procModelGraph);
            procModelGraph.addNode((CPFNode) v);
            procModelGraph.setNodeProperty(node.getUri(), Constants.TYPE, FragmentUtil.getType(v));
        }
        return procModelGraph;
    }

    @Override
    @Transactional(readOnly = true)
    public Canonical fillEdgesByFragmentURI(final Canonical procModelGraph, final String fragmentURI) {
        List<Edge> edges = edgeRepo.getEdgesByFragmentURI(fragmentURI);
        for (Edge edge : edges) {
            CPFNode v1 = procModelGraph.getNode(edge.getSourceNode().getUri());
            CPFNode v2 = procModelGraph.getNode(edge.getTargetNode().getUri());
            if (v1 != null && v2 != null) {
                CPFEdge cpfEdge = new CPFEdge(procModelGraph, edge.getOriginalId(), v1, v2);

                if (edge.getConditionExpression() != null) {
                    CPFExpression cpfExpression = new CPFExpression();
                    cpfExpression.setDescription( edge.getConditionExpression().getDescription() );
                    cpfExpression.setExpression(  edge.getConditionExpression().getExpression()  );
                    cpfExpression.setLanguage(    edge.getConditionExpression().getLanguage()    );
                    cpfExpression.setReturnType(  edge.getConditionExpression().getReturnType()  );
                    cpfEdge.setConditionExpr(cpfExpression);
                }

                if (edge.getDef() != null) {
                    cpfEdge.setDefault(edge.getDef());
                }

                cpfEdge.setOriginalId(edge.getOriginalId());

                for (EdgeAttribute attribute: edge.getAttributes()) {
                    if ("bpmn_cpf/extensions".equals(attribute.getName())) {
                        Element element = XMLUtils.stringToAnyElement(attribute.getAny());
                        if ("http://www.processconfiguration.com".equals(element.getNamespaceURI()) && "configurationAnnotation".equals(element.getLocalName())) {
                            cpfEdge.addAttribute("bpmn_cpf/extensions", null, createConfigurationAnnotation(element, procModelGraph));
                        } else {
                            cpfEdge.addAttribute("bpmn_cpf/extensions", null, XMLUtils.stringToAnyElement(attribute.getAny()));
                        }
                    } else {
                        cpfEdge.addAttribute(attribute.getName(), attribute.getValue(), XMLUtils.stringToAnyElement(attribute.getAny()));
                    }
                }
            } else {
                if (v1 == null && v2 != null) {
                    LOGGER.info("Null source node found for the edge terminating at " + v2.getId() + " = " + v2.getName() + " in fragment " + fragmentURI);
                }
                if (v2 == null && v1 != null) {
                    LOGGER.info("Null target node found for the edge originating at " + v1.getId() + " = " + v1.getName() + " in fragment " + fragmentURI);
                }
                if (v1 == null && v2 == null) {
                    LOGGER.info("Null source and target nodes found for an edge in fragment " + fragmentURI);
                }
            }
        }
        return procModelGraph;
    }

    @Override
    @Transactional(readOnly = true)
    public Canonical fillEdgesByFragmentURINoError(final Canonical procModelGraph, final String fragmentURI) {
        List<Edge> edges = edgeRepo.getEdgesByFragmentURI(fragmentURI);
        for (Edge edge : edges) {
            CPFNode v1 = procModelGraph.getNode(edge.getSourceNode().getUri());
            CPFNode v2 = procModelGraph.getNode(edge.getTargetNode().getUri());
            if (v1 != null && v2 != null) {
                CPFEdge cpfEdge = new CPFEdge(procModelGraph, edge.getOriginalId(), v1, v2);

                if (edge.getConditionExpression() != null) {
                    CPFExpression cpfExpression = new CPFExpression();
                    cpfExpression.setDescription( edge.getConditionExpression().getDescription() );
                    cpfExpression.setExpression(  edge.getConditionExpression().getExpression()  );
                    cpfExpression.setLanguage(    edge.getConditionExpression().getLanguage()    );
                    cpfExpression.setReturnType(  edge.getConditionExpression().getReturnType()  );
                    cpfEdge.setConditionExpr(cpfExpression);
                }

                if (edge.getDef() != null) {
                    cpfEdge.setDefault(edge.getDef());
                }

                cpfEdge.setOriginalId(edge.getOriginalId());

                for (EdgeAttribute attribute: edge.getAttributes()) {
                    if ("bpmn_cpf/extensions".equals(attribute.getName())) {
                        Element element = XMLUtils.stringToAnyElementNoError(attribute.getAny());
                        if ("http://www.processconfiguration.com".equals(element.getNamespaceURI()) && "configurationAnnotation".equals(element.getLocalName())) {
                            cpfEdge.addAttribute("bpmn_cpf/extensions", null, createConfigurationAnnotation(element, procModelGraph));
                        } else {
                            cpfEdge.addAttribute("bpmn_cpf/extensions", null, element);
                        }
                    } else {
                        cpfEdge.addAttribute(attribute.getName(), attribute.getValue(), XMLUtils.stringToAnyElementNoError(attribute.getAny()));
                    }
                }
            } else {
                if (v1 == null && v2 != null) {
                    LOGGER.info("Null source node found for the edge terminating at " + v2.getId() + " = " + v2.getName() + " in fragment " + fragmentURI);
                }
                if (v2 == null && v1 != null) {
                    LOGGER.info("Null target node found for the edge originating at " + v1.getId() + " = " + v1.getName() + " in fragment " + fragmentURI);
                }
                if (v1 == null && v2 == null) {
                    LOGGER.info("Null source and target nodes found for an edge in fragment " + fragmentURI);
                }
            }
        }
        return procModelGraph;
    }


    /* Build the correct type of Node so we don't loss Information */
    private INode buildNodeByType(final Node node, Canonical canonical) {
        INode result = null;
        if (node.getNodeType() != null) {
            if (node.getNodeType().equals(NodeTypeEnum.MESSAGE)) {
                result = constructMessageNode(node, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.TIMER)) {
                result = constructTimerNode(node, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.TASK)) {
                result = constructTaskNode(node, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.EVENT)) {
                result = constructEventNode(node, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.STATE)) {
                result = constructSpecialNode(node, NodeTypeEnum.STATE, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.ORSPLIT)) {
                result = constructSpecialNode(node, NodeTypeEnum.ORSPLIT, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.XORSPLIT)) {
                result = constructSpecialNode(node, NodeTypeEnum.XORSPLIT, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.ANDSPLIT)) {
                result = constructSpecialNode(node, NodeTypeEnum.ANDSPLIT, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.ORJOIN)) {
                result = constructSpecialNode(node, NodeTypeEnum.ORJOIN, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.XORJOIN)) {
                result = constructSpecialNode(node, NodeTypeEnum.XORJOIN, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.ANDJOIN)) {
                result = constructSpecialNode(node, NodeTypeEnum.ANDJOIN, canonical);
            } else if (node.getNodeType().equals(NodeTypeEnum.POCKET)) {
                result = new CPFNode();
                result.setGraph(canonical);
                addNodeDetails(node, result, canonical);
            } else {
                LOGGER.warn("Unknown Node Type in parsing Node from DB: " + node.getNodeType().value());
            }
        } else {
            result = new CPFNode();
            result.setGraph(canonical);
            addNodeDetails(node, result, canonical);
        }
        return result;
    }

    /* Populate the Node with the Message Node details. */
    private INode constructMessageNode(final Node node, Canonical canonical) {
        INode cpfNode = new CPFNode();
        cpfNode.setGraph(canonical);
        cpfNode.setNodeType(NodeTypeEnum.MESSAGE);

        addNodeDetails(node, cpfNode, canonical);
        addWorkDetails(node, cpfNode, canonical);

        if (node.getMessageDirection() != null) {
            cpfNode.setDirection(DirectionEnum.valueOf(node.getMessageDirection().toString()));
        }

        return cpfNode;
    }

    /* Populate the Node with the Auditable Node details. */
    private INode constructEventNode(final Node node, Canonical canonical) {
        INode cpfNode = new CPFNode();
        cpfNode.setGraph(canonical);
        cpfNode.setNodeType(NodeTypeEnum.EVENT);

        addNodeDetails(node, cpfNode, canonical);
        addWorkDetails(node, cpfNode, canonical);

        return cpfNode;
    }

    /* Populate the Node with the Timer Node details. */
    private INode constructTimerNode(final Node node, Canonical canonical) {
        INode cpfNode = new CPFNode();
        cpfNode.setGraph(canonical);
        cpfNode.setNodeType(NodeTypeEnum.TIMER);

        addNodeDetails(node, cpfNode, canonical);
        addWorkDetails(node, cpfNode, canonical);

        if (node.getTimeDuration() != null) {
            cpfNode.setTimeDuration(node.getTimeDuration());
        }
        if (node.getTimeDate() != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(node.getTimeDate());
            cpfNode.setTimeDate(cal);
        }
        if (node.getTimerExpression() != null) {
            CPFExpression expr = new CPFExpression();
            expr.setDescription(node.getTimerExpression().getDescription());
            expr.setExpression(node.getTimerExpression().getExpression());
            expr.setLanguage(node.getTimerExpression().getLanguage());
            expr.setReturnType(node.getTimerExpression().getReturnType());
            cpfNode.setTimeExpression(expr);
        }

        return cpfNode;
    }

    /* Populate the Node with the Task Node details. */
    private INode constructTaskNode(final Node node, Canonical canonical) {
        INode cpfNode = new CPFNode();
        cpfNode.setGraph(canonical);
        cpfNode.setNodeType(NodeTypeEnum.TASK);

        addNodeDetails(node, cpfNode, canonical);
        addWorkDetails(node, cpfNode, canonical);

        if (node.getConfiguration() != null) {
            cpfNode.setConfigurable(node.getConfiguration());
        }
        if (node.getSubProcess() != null) {
            cpfNode.setSubNetId(node.getSubProcess().getOriginalId());
            cpfNode.setExternal(true);
        }

        return cpfNode;
    }

    /* Populate the Node with the State Node details. */
    private INode constructSpecialNode(final Node node, final NodeTypeEnum type, Canonical canonical) {
        INode cpfNode = new CPFNode();
        addNodeDetails(node, cpfNode, canonical);

        cpfNode.setGraph(canonical);
        cpfNode.setNodeType(type);
        cpfNode.setName("");

        return cpfNode;
    }


    /* Add the Node Specific Details to the Node. */
    private void addNodeDetails(final Node node, INode cpfNode, Canonical canonical) {
        cpfNode.setName(node.getName());
        cpfNode.setId(node.getUri());
        cpfNode.setNetId(node.getNetId());
        cpfNode.setOriginalId(node.getOriginalId());

        addNodeAttributes(node, cpfNode, canonical);
    }

    /* Add the Work Node Specific Details to the Node. */
    private void addWorkDetails(final Node node, INode cpfNode, Canonical canonical) {
        if (node.getTeamWork() != null) {
            cpfNode.setTeamWork(node.getTeamWork());
        }
        if (node.getAllocation() != null) {
            cpfNode.setAllocation(node.getAllocation());
        }

        if (node.getResourceDataExpression() != null) {
            CPFExpression resDataExpr = new CPFExpression();
            resDataExpr.setDescription(node.getResourceDataExpression().getDescription());
            resDataExpr.setExpression(node.getResourceDataExpression().getExpression());
            resDataExpr.setLanguage(node.getResourceDataExpression().getLanguage());
            resDataExpr.setReturnType(node.getResourceDataExpression().getReturnType());
            cpfNode.setResourceDataExpr(resDataExpr);
        }

        if (node.getResourceRunExpression() != null) {
            CPFExpression resRunExpr = new CPFExpression();
            resRunExpr.setDescription(node.getResourceRunExpression().getDescription());
            resRunExpr.setExpression(node.getResourceRunExpression().getExpression());
            resRunExpr.setLanguage(node.getResourceRunExpression().getLanguage());
            resRunExpr.setReturnType(node.getResourceRunExpression().getReturnType());
            cpfNode.setResourceDataExpr(resRunExpr);
        }

        addCancelNodes(node, cpfNode);
        addCancelEdges(node, cpfNode);
        addInputExpression(node, cpfNode);
        addOutputExpression(node, cpfNode);
        addObjects(node, cpfNode, canonical);
        addResources(node, cpfNode, canonical);
    }


    /* Adds the Input Expressions to the Node */
    private void addInputExpression(final Node node, INode cpfNode) {
        CPFExpression input;
        for (Expression inExpr : node.getInputExpressions()) {
            input = new CPFExpression();
            input.setExpression(inExpr.getExpression());
            input.setLanguage(inExpr.getLanguage());
            input.setDescription(inExpr.getDescription());
            input.setReturnType(inExpr.getReturnType());
            cpfNode.addInputExpr(input);
        }
    }

    /* Adds the Input Expressions to the Node */
    private void addOutputExpression(final Node node, INode cpfNode) {
        CPFExpression output;
        for (Expression outExpr : node.getOutputExpressions()) {
            output = new CPFExpression();
            output.setExpression(outExpr.getExpression());
            output.setLanguage(outExpr.getLanguage());
            output.setDescription(outExpr.getDescription());
            output.setReturnType(outExpr.getReturnType());
            cpfNode.addOutputExpr(output);
        }
    }

    /* Add the Cancel Nodes. */
    private void addCancelNodes(final Node node, INode cpfNode) {
        if (node.getCancelNodes() != null) {
            for (Node cancelNode : node.getCancelNodes()) {
                cpfNode.getCancelNodes().add(cancelNode.getUri());
            }
        }
    }

    /* Add the Cancel Edges. */
    private void addCancelEdges(final Node node, INode cpfNode) {
        if (node.getCancelEdges() != null) {
            for (Edge cancelEdge : node.getCancelEdges()) {
                cpfNode.getCancelEdges().add(cancelEdge.getUri());
            }
        }
    }

    /* Add the Attributes to the Node. */
    private void addNodeAttributes(final Node node, INode cpfNode, final Canonical procModelGraph) {
        if (node.getAttributes() != null) {
            for (NodeAttribute attribute : node.getAttributes()) {
                if ("bpmn_cpf/extensions".equals(attribute.getName())) {
                    Element element = XMLUtils.stringToAnyElement(attribute.getAny());
                    if ("http://www.processconfiguration.com".equals(element.getNamespaceURI()) && "configurationAnnotation".equals(element.getLocalName())) {
                        cpfNode.addAttribute("bpmn_cpf/extensions", null, createConfigurationAnnotation(element, procModelGraph));
                    } else if ("http://www.processconfiguration.com".equals(element.getNamespaceURI()) && "configurable".equals(element.getLocalName())) {
                        cpfNode.setConfigurable(true);
                    } else {
                        cpfNode.addAttribute("bpmn_cpf/extensions", null, XMLUtils.stringToAnyElement(attribute.getAny()));
                    }
                } else {
                    cpfNode.addAttribute(attribute.getName(), attribute.getValue(), XMLUtils.stringToAnyElement(attribute.getAny()));
                }
            }
        }
    }

    /**
     * Convert a configuration annotation from a DOM Element representation to a JAXB object
     *
     * @param element  the DOM representation
     * @param canonical  required for the sake of the {@link Canonical.variantMap} to fix IDREFs that JAXB can't relink correctly
     * @return the JAXB representation
     */
    private ConfigurationAnnotation createConfigurationAnnotation(Element element, Canonical canonical) {
        ConfigurationAnnotation configurationAnnotation = new ConfigurationAnnotation();

        NodeList configurationList = element.getElementsByTagNameNS("http://www.processconfiguration.com", "configuration");
        for (int i=0; i < configurationList.getLength(); i++) {
            Element child = (Element) configurationList.item(i);
            ConfigurationAnnotation.Configuration configuration = new ConfigurationAnnotation.Configuration();
            if (child.hasAttribute("name")) {
                configuration.setName(child.getAttribute("name"));
            }
            if (child.hasAttribute("type")) {
                configuration.setType(TGatewayType.fromValue(child.getAttribute("type")));
            }
            canonical.variantMap.put(configuration, child.getAttribute("variantRef"));
            configurationAnnotation.getConfiguration().add(configuration);
        }
        return configurationAnnotation;
    }


    /* Add Objects to the Graph, Both to the Objects and Object references. */
    private void addObjects(final Node node, INode cpfNode, Canonical canonical) {
        addObjectReferences(node, cpfNode);

        if (node.getObjectRefs() != null) {
            ICPFObject cpfObject;
            for (ObjectRef objectRef : node.getObjectRefs()) {
                org.apromore.dao.model.Object object = objectRef.getObject();

                if (!canonicalContainsObject(canonical.getObjects(), object)) {
                    cpfObject = new CPFObject();
                    cpfObject.setName(object.getName());
                    cpfObject.setNetId(object.getNetId());
                    cpfObject.setOriginalId(object.getUri());
                    if (object.getUri() != null) {
                        cpfObject.setId(object.getUri());
                    } else {
                        cpfObject.setId(UUID.randomUUID().toString());
                    }
                    if (object.getConfigurable() != null) {
                        cpfObject.setConfigurable(object.getConfigurable());
                    }
                    if (object.getType() != null) {
                        cpfObject.setObjectType(object.getType());
                        if (object.getType().equals(ObjectTypeEnum.SOFT)) {
                            cpfObject.setSoftType(object.getSoftType());
                        }
                    }

                    for (ObjectAttribute attrib : object.getObjectAttributes()) {
                        if (attrib.getAny() != null) {
                            cpfObject.setAttribute(attrib.getName(), attrib.getValue(), XMLUtils.stringToAnyElement(attrib.getAny()));
                        }
                    }

                    canonical.addObject(cpfObject);
                }
            }
        }
    }

    /* Add Resources to the Graph, Both to the Resources and Resource references. */
    private void addResources(final Node node, INode cpfNode, Canonical canonical) {
        addResourceReferences(node, cpfNode);

        if (node.getResourceRefs() != null) {
            ICPFResource cpfResource;
            for (ResourceRef resourceRef : node.getResourceRefs()) {
                Resource resource = resourceRef.getResource();

                if (!canonicalContainsResource(canonical.getResources(), resource)) {
                    cpfResource = new CPFResource();
                    cpfResource.setName(resource.getName());
                    cpfResource.setOriginalId(resource.getOriginalId());
                    if (resource.getUri() != null) {
                        cpfResource.setId(resource.getUri());
                    } else {
                        cpfResource.setId(UUID.randomUUID().toString());
                    }
                    if (resource.getConfigurable() != null) {
                        cpfResource.setConfigurable(resource.getConfigurable());
                    }
                    if (resource.getType() != null) {
                        cpfResource.setResourceType(resource.getType());
                        if (resource.getType().equals(ResourceTypeEnum.HUMAN)) {
                            if (resource.getTypeName() != null) {
                                cpfResource.setHumanType(HumanTypeEnum.fromValue(resource.getTypeName()));
                            }
                        } if (resource.getType().equals(ResourceTypeEnum.NONHUMAN)) {
                            if (resource.getTypeName() != null) {
                                cpfResource.setNonHumanType(NonHumanTypeEnum.fromValue(resource.getTypeName()));
                            }
                        }
                    }
                    for (Resource specialRes : resource.getSpecialisations()) {
                        cpfResource.getSpecializationIds().add(specialRes.getUri());
                    }
                    for (ResourceAttribute attrib : resource.getResourceAttributes()) {
                        cpfResource.setAttribute(attrib.getName(), attrib.getValue(), XMLUtils.stringToAnyElement(attrib.getAny()));
                    }

                    canonical.addResource(cpfResource);
                }
            }
        }
    }

    // Add Object References to the graph.
    private void addObjectReferences(final Node node, INode cpfNode) {
        ICPFObjectReference objectReference;
        if (node.getObjectRefs() != null) {
            for (ObjectRef objectRef : node.getObjectRefs()) {
                objectReference = new CPFObjectReference();
                if (objectRef.getId() != null) {
                    objectReference.setId("O_" + objectRef.getId().toString());
                } else {
                    objectReference.setId(UUID.randomUUID().toString());
                }
                objectReference.setObjectId(objectRef.getObject().getUri());
                objectReference.setOptional(objectRef.getOptional());
                objectReference.setConsumed(objectRef.getConsumed());
                objectReference.setObjectRefType(objectRef.getType());

                for (ObjectRefAttribute type : objectRef.getObjectRefAttributes()) {
                    if (type.getAny() != null) {
                        objectReference.setAttribute(type.getName(), type.getValue(), XMLUtils.stringToAnyElement(type.getAny()));
                    }
                }

                cpfNode.addObjectReference(objectReference);
            }
        }
    }

    private void addResourceReferences(final Node node, INode cpfNode) {
        ICPFResourceReference resourceReference;
        if (node.getResourceRefs() != null) {
            for (ResourceRef resourceRef : node.getResourceRefs()) {
                resourceReference = new CPFResourceReference();
                if (resourceRef.getId() != null) {
                    resourceReference.setId("R_" + resourceRef.getId().toString());
                } else {
                    resourceReference.setId(UUID.randomUUID().toString());
                }
                resourceReference.setResourceId(resourceRef.getResource().getUri());
                resourceReference.setQualifier(resourceRef.getQualifier());

                for (ResourceRefAttribute type : resourceRef.getResourceRefAttributes()) {
                    resourceReference.setAttribute(type.getName(), type.getValue(), XMLUtils.stringToAnyElement(type.getAny()));
                }

                cpfNode.addResourceReference(resourceReference);
            }
        }
    }


    /* See if the Resource already exists in the Canonical Graph. Don't add duplicates. */
    private boolean canonicalContainsResource(Set<ICPFResource> resources, Resource resource) {
        boolean result = false;
        for (ICPFResource cpfResource : resources) {
            if (cpfResource.getId().equals(resource.getUri())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /* See if the Resource already exists in the Canonical Graph. Don't add duplicates. */
    private boolean canonicalContainsObject(Set<ICPFObject> objects, org.apromore.dao.model.Object object) {
        boolean result = false;
        for (ICPFObject cpfObject : objects) {
            if (cpfObject.getId().equals(object.getUri())) {
                result = true;
                break;
            }
        }
        return result;
    }

}
