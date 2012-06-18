package org.apromore.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the GraphService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("GraphService")
@Transactional(propagation = Propagation.REQUIRED)
public class GraphServiceImpl implements GraphService {

    @Autowired @Qualifier("ContentDao")
    private ContentDao contentDao;
    @Autowired @Qualifier("EdgeDao")
    private EdgeDao edgeDao;
    @Autowired @Qualifier("NodeDao")
    private NodeDao nDao;


    /**
     * @see org.apromore.service.GraphService#getContent(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Content getContent(String fragmentVersionId) {
        return contentDao.getContentByFragmentVersion(fragmentVersionId);
    }

    /**
     * @see org.apromore.service.GraphService#getContentIds()
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getContentIds() {
        return nDao.getContentIDs();
    }

    /**
     * @see org.apromore.service.GraphService#getGraph(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public CPF getGraph(String contentID){
        CPF g = new CPF();
        fillNodes(g, contentID);
        fillEdges(g, contentID);
        return g;
    }

    /**
     * @see org.apromore.service.GraphService#getGraph(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void fillNodes(CPF procModelGraph, String contentID) {
        FlowNode v;
        List<Node> nodes = nDao.getVertexByContent(contentID);
        for (Node node : nodes) {
            v = buildNodeByType(node);
            procModelGraph.addVertex(v);
            procModelGraph.setVertexProperty(String.valueOf(node.getVid()), Constants.TYPE, node.getVtype());
        }
    }


    /**
     * @see org.apromore.service.GraphService#getGraph(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void fillEdges(CPF procModelGraph, String contentID) {
        List<Edge> edges = edgeDao.getEdgesByContent(contentID);
        for (Edge edge : edges) {
            FlowNode v1 = procModelGraph.getVertex(String.valueOf(edge.getVerticesBySourceVid().getVid()));
            FlowNode v2 = procModelGraph.getVertex(String.valueOf(edge.getVerticesByTargetVid().getVid()));
            procModelGraph.addEdge(v1, v2);
        }
    }




    /* Build the correct type of Node so we don't loss Information */
    private FlowNode buildNodeByType(Node node) {
        FlowNode result = null;
        if (node.getCtype().equals(CpfNode.class.getName())) {
            result = new CpfNode(node.getVname());
            result.setId(String.valueOf(node.getVid()));
        } else if (node.getCtype().equals(CpfMessage.class.getName())) {
            result = new CpfMessage(node.getVname());
            result.setId(String.valueOf(node.getVid()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfTimer.class.getName())) {
            result = new CpfTimer(node.getVname());
            result.setId(String.valueOf(node.getVid()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfTask.class.getName())) {
            result = new CpfTask(node.getVname());
            result.setId(String.valueOf(node.getVid()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfEvent.class.getName())) {
            result = new CpfEvent(node.getVname());
            result.setId(String.valueOf(node.getVid()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfOrGateway.class.getName())) {
            result = new CpfOrGateway(node.getVname());
            result.setId(String.valueOf(node.getVid()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        } else if (node.getCtype().equals(CpfXorGateway.class.getName())) {
            result = new CpfXorGateway(node.getVname());
            result.setId(String.valueOf(node.getVid()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        }  else if (node.getCtype().equals(CpfAndGateway.class.getName())) {
            result = new CpfAndGateway(node.getVname());
            result.setId(String.valueOf(node.getVid()));
            addResources((CpfNode) result, node);
            addObjects((CpfNode) result, node);
            addNodeAttributes((CpfNode) result, node);
        }
        return result;
    }


    private void addObjects(CpfNode result, Node node) {
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

    private void addResources(CpfNode result, Node node) {
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


    private void addNodeAttributes(CpfNode result, Node node) {
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
     * @param cntDAOJpa the content Dao.
     */
    public void setContentDao(ContentDao cntDAOJpa) {
        contentDao = cntDAOJpa;
    }

    /**
     * Set the Edge DAO object for this class. Mainly for spring tests.
     * @param edgeDAOJpa the Edge Dao.
     */
    public void setEdgeDao(EdgeDao edgeDAOJpa) {
        edgeDao = edgeDAOJpa;
    }

    /**
     * Set the Node DAO object for this class. Mainly for spring tests.
     * @param nodeDAOJpa the Node Dao.
     */
    public void setNodeDao(NodeDao nodeDAOJpa) {
        nDao = nodeDAOJpa;
    }

}
