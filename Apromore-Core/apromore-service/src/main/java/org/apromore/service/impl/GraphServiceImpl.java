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
import org.apromore.graph.canonical.AndJoin;
import org.apromore.graph.canonical.AndSplit;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.CanonicalObject;
import org.apromore.graph.canonical.Event;
import org.apromore.graph.canonical.IObject;
import org.apromore.graph.canonical.IResource;
import org.apromore.graph.canonical.Message;
import org.apromore.graph.canonical.OrJoin;
import org.apromore.graph.canonical.OrSplit;
import org.apromore.graph.canonical.Resource;
import org.apromore.graph.canonical.Task;
import org.apromore.graph.canonical.Timer;
import org.apromore.graph.canonical.XOrJoin;
import org.apromore.graph.canonical.XOrSplit;
import org.apromore.service.GraphService;
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
    public Canonical getGraph(final Integer contentID) {
        Canonical g = new Canonical();
        fillNodes(g, contentID);
        fillEdges(g, contentID);
        return g;
    }

    /**
     * @see org.apromore.service.GraphService#fillNodes(org.apromore.graph.canonical.Canonical, Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void fillNodes(final Canonical procModelGraph, final Integer contentID) {
        org.apromore.graph.canonical.Node v;
        List<Node> nodes = nDao.getVertexByContent(contentID);
        for (Node node : nodes) {
            v = buildNodeByType(node);
            procModelGraph.addVertex(v);
            procModelGraph.setNodeProperty(String.valueOf(node.getId()), Constants.TYPE, Constants.FUNCTION);
        }
    }


    /**
     * @see org.apromore.service.GraphService#fillEdges(org.apromore.graph.canonical.Canonical, Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void fillEdges(final Canonical procModelGraph, final Integer contentID) {
        List<Edge> edges = edgeDao.getEdgesByContent(contentID);
        for (Edge edge : edges) {
            org.apromore.graph.canonical.Node v1 = procModelGraph.getNode(String.valueOf(edge.getVerticesBySourceVid().getId()));
            org.apromore.graph.canonical.Node v2 = procModelGraph.getNode(String.valueOf(edge.getVerticesByTargetVid().getId()));
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
     * @see org.apromore.service.GraphService#fillNodesByFragmentId(org.apromore.graph.canonical.Canonical, Integer)
     * {@inheritDoc}
     */
    @Override
    public void fillNodesByFragmentId(final Canonical procModelGraph, final Integer fragmentID) {
        org.apromore.graph.canonical.Node v;
        List<Node> nodes = nDao.getVertexByFragment(fragmentID);
        for (Node node : nodes) {
            v = buildNodeByType(node);
            procModelGraph.addVertex(v);
            procModelGraph.setNodeProperty(String.valueOf(node.getId()), Constants.TYPE, node.getType());
        }
    }

    /**
     * @see org.apromore.service.GraphService#fillEdgesByFragmentId(org.apromore.graph.canonical.Canonical, Integer)
     * {@inheritDoc}
     */
    @Override
    public void fillEdgesByFragmentId(final Canonical procModelGraph, final Integer fragmentID) {
        List<Edge> edges = edgeDao.getEdgesByFragment(fragmentID);
        for (Edge edge : edges) {
            org.apromore.graph.canonical.Node v1 = procModelGraph.getNode(String.valueOf(edge.getVerticesBySourceVid().getId()));
            org.apromore.graph.canonical.Node v2 = procModelGraph.getNode(String.valueOf(edge.getVerticesByTargetVid().getId()));
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
    private org.apromore.graph.canonical.Node buildNodeByType(final Node node) {
        org.apromore.graph.canonical.Node result = null;
        if (node.getCtype().equals(org.apromore.graph.canonical.Node.class.getName())) {
            result = new org.apromore.graph.canonical.Node(node.getName());
            result.setId(String.valueOf(node.getId()));
        } else if (node.getCtype().equals(Message.class.getName())) {
            result = constructMessageNode(node);
        } else if (node.getCtype().equals(Timer.class.getName())) {
            result = constructTimerNode(node);
        } else if (node.getCtype().equals(Task.class.getName())) {
            result = constructTaskNode(node);
        } else if (node.getCtype().equals(Event.class.getName())) {
            result = constructEventNode(node);
        } else if (node.getCtype().equals(OrSplit.class.getName()) || node.getCtype().equals(OrJoin.class.getName())) {
            result = constructOrNode(node);
        } else if (node.getCtype().equals(XOrSplit.class.getName()) || node.getCtype().equals(XOrJoin.class.getName())) {
            result = constructXOrNode(node);
        } else if (node.getCtype().equals(AndSplit.class.getName()) || node.getCtype().equals(AndJoin.class.getName())) {
            result = constructAndNode(node);
        }
        return result;
    }


    private void addObjects(final org.apromore.graph.canonical.Node result, final Node node) {
        IObject object;
        for (ObjectRefType obj : node.getObjectRefTypes()) {
            object = new CanonicalObject();
            object.setId(String.valueOf(obj.getId()));
            object.setOptional(Boolean.valueOf(obj.getOptional()));
            object.setConsumed(Boolean.valueOf(obj.getConsumed()));
            object.setOriginalId(obj.getOriginalId());
//            addObjectAttributes(result, obj);
            result.addObject(object);
        }
    }

    private void addResources(final org.apromore.graph.canonical.Node result, final Node node) {
        IResource resource;
        for (ResourceRefType res : node.getResourceRefTypes()) {
            resource = new Resource();
            resource.setId(String.valueOf(res.getId()));
            resource.setOptional(Boolean.valueOf(res.getOptional()));
            resource.setQualifier(res.getQualifier());
            //addResourceAttributes(result, res);
            result.addResource(resource);
        }
    }


    private void addNodeAttributes(final org.apromore.graph.canonical.Node result, final Node node) {
        for (NodeAttribute n : node.getAttributes()) {
            result.addAttribute(n.getName(), n.getValue());
        }
    }

//    private void addObjectAttributes(CanonicalNode result, ObjectRefType node) {
//        for (ObjAttribute n : node.getAttributes()) {
//            result.addAttribute(n.getName(), n.getValue());
//        }
//    }

//    private void addResourceAttributes(CanonicalNode result, Resource node) {
//        for (ResourceAttribute n : node.getAttributes()) {
//            result.addAttribute(n.getName(), n.getValue());
//        }
//    }

    private org.apromore.graph.canonical.Node constructAndNode(final Node node) {
        org.apromore.graph.canonical.Node result;
        if (node.getCtype().equals(AndSplit.class.getName())) {
            result = new AndSplit(node.getName());
        } else {
            result = new AndJoin(node.getName());
        }
        result.setId(String.valueOf(node.getId()));
        addResources(result, node);
        addObjects(result, node);
        addNodeAttributes(result, node);
        return result;
    }

    private org.apromore.graph.canonical.Node constructXOrNode(final Node node) {
        org.apromore.graph.canonical.Node result;
        if (node.getCtype().equals(AndSplit.class.getName())) {
            result = new XOrSplit(node.getName());
        } else {
            result = new XOrJoin(node.getName());
        }
        result.setId(String.valueOf(node.getId()));
        addResources(result, node);
        addObjects(result, node);
        addNodeAttributes(result, node);
        return result;
    }

    private org.apromore.graph.canonical.Node constructOrNode(final Node node) {
        org.apromore.graph.canonical.Node result;
        if (node.getCtype().equals(AndSplit.class.getName())) {
            result = new OrSplit(node.getName());
        } else {
            result = new OrJoin(node.getName());
        }
        result.setId(String.valueOf(node.getId()));
        addResources(result, node);
        addObjects(result, node);
        addNodeAttributes(result, node);
        return result;
    }

    private org.apromore.graph.canonical.Node constructEventNode(final Node node) {
        org.apromore.graph.canonical.Node result;
        result = new Event(node.getName());
        result.setId(String.valueOf(node.getId()));
        addResources(result, node);
        addObjects(result, node);
        addNodeAttributes(result, node);
        return result;
    }

    private org.apromore.graph.canonical.Node constructTaskNode(final Node node) {
        org.apromore.graph.canonical.Node result;
        result = new Task(node.getName());
        result.setId(String.valueOf(node.getId()));
        addResources(result, node);
        addObjects(result, node);
        addNodeAttributes(result, node);
        return result;
    }

    private org.apromore.graph.canonical.Node constructTimerNode(final Node node) {
        org.apromore.graph.canonical.Node result;
        result = new Timer(node.getName());
        result.setId(String.valueOf(node.getId()));
        addResources(result, node);
        addObjects(result, node);
        addNodeAttributes(result, node);
        return result;
    }

    private org.apromore.graph.canonical.Node constructMessageNode(final Node node) {
        org.apromore.graph.canonical.Node result;
        result = new Message(node.getName());
        result.setId(String.valueOf(node.getId()));
        addResources(result, node);
        addObjects(result, node);
        addNodeAttributes(result, node);
        return result;
    }




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
