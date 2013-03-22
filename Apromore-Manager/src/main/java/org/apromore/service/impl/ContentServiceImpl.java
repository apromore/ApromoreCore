package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.ContentRepository;
import org.apromore.dao.EdgeRepository;
import org.apromore.dao.ExpressionRepository;
import org.apromore.dao.NodeRepository;
import org.apromore.dao.NonPocketNodeRepository;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.EdgeAttribute;
import org.apromore.dao.model.Expression;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.NodeAttribute;
import org.apromore.dao.model.NonPocketNode;
import org.apromore.dao.model.Object;
import org.apromore.dao.model.ObjectRef;
import org.apromore.dao.model.ObjectRefAttribute;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.Resource;
import org.apromore.dao.model.ResourceRef;
import org.apromore.dao.model.ResourceRefAttribute;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFExpression;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.IAttribute;
import org.apromore.graph.canonical.ICPFObjectReference;
import org.apromore.graph.canonical.ICPFResourceReference;
import org.apromore.graph.canonical.INode;
import org.apromore.service.ContentService;
import org.apromore.service.helper.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import static java.util.Map.Entry;

/**
 * Implementation of the FormatService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ContentServiceImpl implements ContentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentServiceImpl.class);

    private ContentRepository cRepository;
    private NodeRepository nRepository;
    private NonPocketNodeRepository npnRepository;
    private EdgeRepository eRepository;
    private ExpressionRepository exRepository;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param cRepo Content Repository.
     * @param nRepo Node Repository.
     * @param eRepo Edge Repository.
     * @param exRepo Expression Repository.
     */
    @Inject
    public ContentServiceImpl(final ContentRepository cRepo, final NodeRepository nRepo, final NonPocketNodeRepository npnRepo,
            final EdgeRepository eRepo, final ExpressionRepository exRepo) {
        cRepository = cRepo;
        nRepository = nRepo;
        npnRepository = npnRepo;
        eRepository = eRepo;
        exRepository = exRepo;
    }



    /**
     * @see org.apromore.service.ContentService#getContentByCode(String)
     * {@inheritDoc}
     */
    @Override
    public Content getContentByCode(final String hash) {
        if (hash == null) {
            return null;
        }
        return cRepository.getContentByCode(hash);
    }

    /**
     * @see org.apromore.service.ContentService#deleteContent(Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteContent(final Integer contentId) {
        cRepository.delete(contentId);
    }

    /**
     * @see org.apromore.service.ContentService#addContent(org.apromore.dao.model.ProcessModelVersion, org.apromore.graph.canonical.Canonical, String, org.apromore.service.helper.OperationContext, java.util.Map)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = false)
    public Content addContent(ProcessModelVersion pmv, Canonical c, String hash, OperationContext op, Map<String, String> pocketIdMappings) {
        LOGGER.info("Adding the Fragment Content: " + hash);

        op.addAllCpfNodes(c.getNodes());
        op.addAllCpfEdges(c.getEdges());

        Content content = new Content();
        content.setCode(hash);
        content = cRepository.save(content);

        addNodes(content, c.getNodes(), op, pocketIdMappings, pmv.getObjects(), pmv.getResources());
        addEdges(content, c.getEdges());

        if (c.getEntry() != null) {
            content.setBoundaryS(findNode(content.getNodes(), c.getEntry().getId()));
        } else {
            LOGGER.warn("Content Entry Node can not be found using: " + c.getId());
        }
        if (c.getExit() != null) {
            content.setBoundaryE(findNode(content.getNodes(), c.getExit().getId()));
        } else {
            LOGGER.warn("Content Exit Node can not be found using: " + c.getId());
        }

        return content;
    }



    /* Add the nodes to the DB. */
    private void addNodes(final Content content, final Collection<CPFNode> nodes, final OperationContext g,
            final Map<String, String> pocketIdMappings, Set<Object> objs, Set<Resource> ress) {
        for (CPFNode node : nodes) {
            String oldId = node.getId();
            String type = g.getGraph().getNodeProperty(node.getId(), Constants.TYPE);

            Node n = addNode(content, node, type, objs, ress);
            if (n != null) {
                content.getNodes().add(n);

                if (!"Pocket".equals(type)) {
                    addNonPocketNode(n);
                } else {
                    pocketIdMappings.put(oldId, n.getUri());
                }
            }
        }
    }

    /* Add a single Node */
    private Node addNode(final Content content, final INode cpfNode, final String graphType,
             Set<org.apromore.dao.model.Object> objects, Set<Resource> resources) {
        LOGGER.info("Adding new Node: " + cpfNode.getId());

        try {
            Node node = new Node();
            node.setContent(content);
            node.setName(cpfNode.getName());
            node.setUri(cpfNode.getId());
            node.setNetId(cpfNode.getNetId());
            node.setOriginalId(cpfNode.getOriginalId());
            node.setGraphType(graphType);
            node.setNodeType(cpfNode.getNodeType());
            node.setAllocation(cpfNode.getAllocation());
            node.setConfiguration(cpfNode.isConfigurable());
            node.setTeamWork(cpfNode.isTeamWork());
            if (cpfNode.getTimeDate() != null) {
                node.setTimeDate(cpfNode.getTimeDate().getTime());
            }
            if (node.getTimeDuration() != null) {
                node.setTimeDuration(cpfNode.getTimeDuration());
            }

            addNodeExpressions(cpfNode, node);
            addObjectReferences(node, cpfNode, objects);
            addResourceReferences(node, cpfNode, resources);
            addNodeAttributes(node, cpfNode);

            return nRepository.save(node);
        } catch (Exception e) {
            LOGGER.error("Unable to add Node(" + cpfNode.getId() + "): " + e.getMessage());
        }
        return null;
    }

    private void addNodeAttributes(final Node node, final INode v) {
        NodeAttribute nAtt;
        for (Entry<String, IAttribute> e : v.getAttributes().entrySet()) {
            nAtt = new NodeAttribute();
            nAtt.setName(e.getKey());
            nAtt.setValue(e.getValue().getValue());
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



    /* Adds the Edges to the DB. */
    private void addEdges(final Content content, final Set<CPFEdge> edges) {
        for (CPFEdge e : edges) {
            addEdge(content, e);
        }
    }

    /* Adding an edge to the DB. */
    private Edge addEdge(final Content content, final CPFEdge cpfEdge) {
        LOGGER.info("Adding new Edge: " + cpfEdge.getId());

        try {
            Edge edge = new Edge();
            edge.setContent(content);
            edge.setUri(cpfEdge.getId());
            edge.setOriginalId(cpfEdge.getOriginalId());
            edge.setSourceNode(findNode(content.getNodes(), cpfEdge.getSource().getId()));
            edge.setTargetNode(findNode(content.getNodes(), cpfEdge.getTarget().getId()));

            if (cpfEdge.getConditionExpr() != null) {
                Expression expr = new Expression();
                expr.setDescription(cpfEdge.getConditionExpr().getDescription());
                expr.setExpression(cpfEdge.getConditionExpr().getExpression());
                expr.setLanguage(cpfEdge.getConditionExpr().getLanguage());
                expr.setReturnType(cpfEdge.getConditionExpr().getReturnType());
                edge.setConditionExpression(exRepository.save(expr));
            }

            addEdgeAttributes(edge, cpfEdge);

            content.getEdges().add(edge);

            return eRepository.save(edge);
        } catch (Exception e) {
            LOGGER.error("Unable to add Edge(" + cpfEdge.getId() + "): " + e.getMessage());
        }
        return null;
    }

    /* Add the Attributes to an Edge. */
    private void addEdgeAttributes(final Edge edge, final CPFEdge cpfEdge) {
        EdgeAttribute edgeAttribute;
        for (Entry<String, IAttribute> e : cpfEdge.getAttributes().entrySet()) {
            edgeAttribute = new EdgeAttribute();
            edgeAttribute.setName(e.getKey());
            edgeAttribute.setValue(e.getValue().getValue());
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
                    addCancelNodes(operationContext, cpfNode, findNode(operationContext.getNodes(), cpfNode.getId()));
                    addCancelEdges(operationContext, cpfNode, findNode(operationContext.getNodes(), cpfNode.getId()));
                }
            }
        }
    }

    /* Adds the links to the Cancel Nodes for this task. */
    private void addCancelNodes(OperationContext operationContext, CPFNode cpfNode, Node node) {
        if (cpfNode.getCancelNodes() != null) {
            for (String nodeId : cpfNode.getCancelNodes()) {
                node.getCancelNodes().add(findNode(operationContext.getNodes(), nodeId));
            }
        }
    }

    /* Adds the links to the Cancel Edges for this task.  */
    private void addCancelEdges(OperationContext operationContext, CPFNode cpfNode, Node node) {
        if (cpfNode.getCancelEdges() != null) {
            for (String edgeId : cpfNode.getCancelEdges()) {
                node.getCancelEdges().add(findEdge(operationContext.getEdges(), edgeId));
            }
        }
    }



    /* Add a Non pocket Node */
    private NonPocketNode addNonPocketNode(final Node node) {
        NonPocketNode nonPockNode = new NonPocketNode();
        nonPockNode.setNode(node);
        node.getNonPocketNodes().add(nonPockNode);
        return npnRepository.save(nonPockNode);
    }

    /* Given the ObjectId, it finds the Object record and returns it. */
    private org.apromore.dao.model.Object findObject(Set<Object> objects, String objectId) {
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
    private Resource findResourceType(Set<Resource> resources, String resourceId) {
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
    private Node findNode(Set<Node> nodes, String nodeId) {
        Node found = null;
        for (Node node : nodes) {
            if (node.getUri().equals(nodeId)) {
                found = node;
                break;
            }
        }
        //if (found == null) {
        //    LOGGER.warn("Could not find Node with Id: " + nodeId);
        //}
        return found;
    }

    /* Given the the NodeId, it find the Node record and returns it. */
    private Edge findEdge(Set<Edge> edges, String edgeId) {
        Edge found = null;
        for (Edge edge : edges) {
            if (edge.getUri().equals(edgeId)) {
                found = edge;
                break;
            }
        }
        //if (found == null) {
        //    LOGGER.warn("Could not find Edge with Id: " + edgeId);
        //}
        return found;
    }
}
