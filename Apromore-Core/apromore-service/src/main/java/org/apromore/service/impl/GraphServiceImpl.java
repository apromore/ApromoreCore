package org.apromore.service.impl;

import java.util.List;

import org.apromore.common.Constants;
import org.apromore.dao.ContentDao;
import org.apromore.dao.EdgeDao;
import org.apromore.dao.NodeDao;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.NodeAttribute;
import org.apromore.dao.model.ObjectRefType;
import org.apromore.dao.model.ResourceRefType;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfAndGateway;
import org.apromore.graph.JBPT.CpfEvent;
import org.apromore.graph.JBPT.CpfMessage;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.graph.JBPT.CpfObject;
import org.apromore.graph.JBPT.CpfOrGateway;
import org.apromore.graph.JBPT.CpfResource;
import org.apromore.graph.JBPT.CpfTask;
import org.apromore.graph.JBPT.CpfTimer;
import org.apromore.graph.JBPT.CpfXorGateway;
import org.apromore.graph.JBPT.ICpfObject;
import org.apromore.graph.JBPT.ICpfResource;
import org.apromore.service.GraphService;
import org.jbpt.pm.FlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the GraphService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("GraphService")
@Transactional(propagation = Propagation.REQUIRED)
public class GraphServiceImpl implements GraphService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphServiceImpl.class);

    @Autowired @Qualifier("ContentDao")
    private ContentDao contentDao;
    @Autowired @Qualifier("EdgeDao")
    private EdgeDao edgeDao;
    @Autowired @Qualifier("NodeDao")
    private NodeDao nDao;


    /**
     * @see org.apromore.service.GraphService#getContent(Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Content getContent(final Integer fragmentVersionId) {
        return contentDao.getContentByFragmentVersion(fragmentVersionId);
    }

    /**
     * @see org.apromore.service.GraphService#getContentIds()
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getContentIds() {
        return nDao.getContentIDs();
    }

    /**
     * @see org.apromore.service.GraphService#getGraph(Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CPF getGraph(final Integer contentID) {
        CPF g = new CPF();
        fillNodes(g, contentID);
        fillEdges(g, contentID);
        return g;
    }

    /**
     * @see org.apromore.service.GraphService#fillNodes(org.apromore.graph.JBPT.CPF, Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void fillNodes(final CPF procModelGraph, final Integer contentID) {
        FlowNode v;
        List<Node> nodes = nDao.getVertexByContent(contentID);
        for (Node node : nodes) {
            v = buildNodeByType(node);
            procModelGraph.addVertex(v);
            procModelGraph.setVertexProperty(String.valueOf(node.getId()), Constants.TYPE, Constants.FUNCTION);
        }
    }


    /**
     * @see org.apromore.service.GraphService#fillEdges(org.apromore.graph.JBPT.CPF, Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void fillEdges(final CPF procModelGraph, final Integer contentID) {
        List<Edge> edges = edgeDao.getEdgesByContent(contentID);
        for (Edge edge : edges) {
            FlowNode v1 = procModelGraph.getVertex(String.valueOf(edge.getVerticesBySourceVid().getId()));
            FlowNode v2 = procModelGraph.getVertex(String.valueOf(edge.getVerticesByTargetVid().getId()));
            if (v1 != null && v2 != null) {
                procModelGraph.addEdge(v1, v2);
            } else {
                if (v1 == null && v2 != null) {
                    LOGGER.info("Null source node found for the edge terminating at " + v2.getId() + " = " + v2.getName() + " in content " + contentID);
                }
                if (v2 == null && v1 != null) {
                    LOGGER.info("Null target node found for the edge originating at " + v1.getId() + " = " + v1.getName() + " in content " + contentID);
                }
                if (v1 == null && v2 == null) {
                    LOGGER.info("Null source and target nodes found for an edge in content " + contentID);
                }
            }
        }
    }

    /**
     * @see org.apromore.service.GraphService#fillNodesByFragmentId(org.apromore.graph.JBPT.CPF, Integer)
     * {@inheritDoc}
     */
    @Override
    public void fillNodesByFragmentId(final CPF procModelGraph, final Integer fragmentID) {
        FlowNode v;
        List<Node> nodes = nDao.getVertexByFragment(fragmentID);
        for (Node node : nodes) {
            v = buildNodeByType(node);
            procModelGraph.addVertex(v);
            procModelGraph.setVertexProperty(String.valueOf(node.getId()), Constants.TYPE, node.getType());
        }
    }

    /**
     * @see org.apromore.service.GraphService#fillEdgesByFragmentId(org.apromore.graph.JBPT.CPF, Integer)
     * {@inheritDoc}
     */
    @Override
    public void fillEdgesByFragmentId(final CPF procModelGraph, final Integer fragmentID) {
        List<Edge> edges = edgeDao.getEdgesByFragment(fragmentID);
        for (Edge edge : edges) {
            FlowNode v1 = procModelGraph.getVertex(String.valueOf(edge.getVerticesBySourceVid().getId()));
            FlowNode v2 = procModelGraph.getVertex(String.valueOf(edge.getVerticesByTargetVid().getId()));
            if (v1 != null && v2 != null) {
                procModelGraph.addEdge(v1, v2);
            } else {
                if (v1 == null && v2 != null) {
                    LOGGER.info("Null source node found for the edge terminating at " + v2.getId() + " = " + v2.getName() + " in fragment " + fragmentID);
                }
                if (v2 == null && v1 != null) {
                    LOGGER.info("Null target node found for the edge originating at " + v1.getId() + " = " + v1.getName() + " in fragment " + fragmentID);
                }
                if (v1 == null && v2 == null) {
                    LOGGER.info("Null source and target nodes found for an edge in fragment " + fragmentID);
                }
            }
        }
    }



    /* Build the correct type of Node so we don't loss Information */
    private FlowNode buildNodeByType(final Node node) {
        FlowNode result = null;
        if (node.getCtype().equals(CpfNode.class.getName())) {
            result = new CpfNode(node.getName());
            result.setId(String.valueOf(node.getId()));
        } else if (node.getCtype().equals(CpfMessage.class.getName())) {
            result = new CpfMessage(node.getName());
            result.setId(String.valueOf(node.getId()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfTimer.class.getName())) {
            result = new CpfTimer(node.getName());
            result.setId(String.valueOf(node.getId()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfTask.class.getName())) {
            result = new CpfTask(node.getName());
            result.setId(String.valueOf(node.getId()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfEvent.class.getName())) {
            result = new CpfEvent(node.getName());
            result.setId(String.valueOf(node.getId()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfOrGateway.class.getName())) {
            result = new CpfOrGateway(node.getName());
            result.setId(String.valueOf(node.getId()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfXorGateway.class.getName())) {
            result = new CpfXorGateway(node.getName());
            result.setId(String.valueOf(node.getId()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfAndGateway.class.getName())) {
            result = new CpfAndGateway(node.getName());
            result.setId(String.valueOf(node.getId()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        }
        return result;
    }


    private void addObjects(final CpfNode result, final Node node) {
        ICpfObject object;
        for (ObjectRefType obj : node.getObjectRefTypes()) {
            object = new CpfObject();
            object.setId(String.valueOf(obj.getId()));
            object.setOptional(Boolean.valueOf(obj.getOptional()));
            object.setConsumed(Boolean.valueOf(obj.getConsumed()));
            object.setOriginalId(obj.getOriginalId());
//            addObjectAttributes(result, obj);
            result.addObject(object);
        }
    }

    private void addResources(final CpfNode result, final Node node) {
        ICpfResource resource;
        for (ResourceRefType res : node.getResourceRefTypes()) {
            resource = new CpfResource();
            resource.setId(String.valueOf(res.getId()));
            resource.setOptional(Boolean.valueOf(res.getOptional()));
            resource.setQualifier(res.getQualifier());
            //addResourceAttributes(result, res);
            result.addResource(resource);
        }
    }


    private void addNodeAttributes(final CpfNode result, final Node node) {
        for (NodeAttribute n : node.getAttributes()) {
            result.addAttribute(n.getName(), n.getValue());
        }
    }

//    private void addObjectAttributes(CpfNode result, ObjectRefType node) {
//        for (ObjAttribute n : node.getAttributes()) {
//            result.addAttribute(n.getName(), n.getValue());
//        }
//    }

//    private void addResourceAttributes(CpfNode result, Resource node) {
//        for (ResourceAttribute n : node.getAttributes()) {
//            result.addAttribute(n.getName(), n.getValue());
//        }
//    }


    /**
     * Set the Content DAO object for this class. Mainly for spring tests.
     *
     * @param cntDAOJpa the content Dao.
     */
    public void setContentDao(final ContentDao cntDAOJpa) {
        contentDao = cntDAOJpa;
    }

    /**
     * Set the Edge DAO object for this class. Mainly for spring tests.
     *
     * @param edgeDAOJpa the Edge Dao.
     */
    public void setEdgeDao(final EdgeDao edgeDAOJpa) {
        edgeDao = edgeDAOJpa;
    }

    /**
     * Set the Node DAO object for this class. Mainly for spring tests.
     *
     * @param nodeDAOJpa the Node Dao.
     */
    public void setNodeDao(final NodeDao nodeDAOJpa) {
        nDao = nodeDAOJpa;
    }

}
