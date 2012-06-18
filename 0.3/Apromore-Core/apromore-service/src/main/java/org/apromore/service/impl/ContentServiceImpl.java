package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.ContentDao;
import org.apromore.dao.EdgeDao;
import org.apromore.dao.NodeDao;
import org.apromore.dao.NonPocketNodeDao;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.NodeAttribute;
import org.apromore.dao.model.NonPocketNode;
import org.apromore.dao.model.ObjectRefType;
import org.apromore.dao.model.ObjectRefTypeAttribute;
import org.apromore.dao.model.ResourceRefType;
import org.apromore.dao.model.ResourceRefTypeAttribute;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.graph.JBPT.ICpfNode;
import org.apromore.graph.JBPT.ICpfObject;
import org.apromore.graph.JBPT.ICpfResource;
import org.apromore.service.ContentService;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map.Entry;

/**
 * Implementation of the FormatService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("ContentService")
@Transactional(propagation = Propagation.REQUIRED)
public class ContentServiceImpl implements ContentService {

    @Autowired @Qualifier("ContentDao")
    private ContentDao contentDao;
    @Autowired @Qualifier("EdgeDao")
    private EdgeDao edgeDao;
    @Autowired @Qualifier("NodeDao")
    private NodeDao nDao;
    @Autowired @Qualifier("NonPocketNodeDao")
    private NonPocketNodeDao nonPocketNodeDao;


    /**
     * @see org.apromore.service.ContentService#getMatchingContentId(String)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public String getMatchingContentId(String hash) {
        if (hash == null) {
            return null;
        }
        return contentDao.getContentByCode(hash).getContentId();
    }


    /**
     * @see org.apromore.service.ContentService#addContent(org.jbpt.graph.algo.rpst.RPSTNode, String, org.apromore.graph.JBPT.CPF)
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Content addContent(RPSTNode c, String hash, CPF g) {
        Content content = new Content();

        content.setBoundaryS(c.getEntry().getId());
        content.setBoundaryE(c.getExit().getId());
        content.setCode(hash);

        contentDao.save(content);

        addNodes(content, c.getFragment().getVertices(), g);
        addEdges(content, c.getFragmentEdges());

        return content;
    }

    /**
     * @see org.apromore.service.ContentService#addNodes(org.apromore.dao.model.Content, java.util.Collection, org.apromore.graph.JBPT.CPF)
     * {@inheritDoc}
     */
    @Override
    public void addNodes(Content content, Collection<CpfNode> vertices, CPF g) {
        for (ICpfNode v : vertices) {
            String type = g.getVertexProperty(v.getId(), Constants.TYPE);
            Node n = addNode(content, v, type);
            v.setId(String.valueOf(n.getVid()));
            if (!"Pocket".equals(type)) {
                addNonPacketNode(n.getVid());
            }
        }    
    }

    /**
     * @see org.apromore.service.ContentService#addNode
     * {@inheritDoc}
     */
    @Override
    public Node addNode(Content content, ICpfNode v, String vtype) {
        Node node = new Node();
        node.setContent(content);
        node.setVname(v.getName());
        node.setVtype(vtype);
        node.setConfiguration(v.isConfigurable());
        node.setCtype(v.getClass().getName());
        node.setOriginalId(v.getId());

        addObjects(node, v);
        addResources(node, v);
        addNodeAttributes(node, v);

        nDao.save(node);

        return node;
    }

    /**
     * @see org.apromore.service.ContentService#addNonPacketNode(String)
     * {@inheritDoc}
     */
    @Override
    public void addNonPacketNode(Integer vid) {
        NonPocketNode nonPockNode = new NonPocketNode();

        nonPockNode.setVid(vid);

        nonPocketNodeDao.save(nonPockNode);
    }

    /**
     * @see org.apromore.service.ContentService#addEdges(Content, java.util.Collection)
     * {@inheritDoc}
     */
    @Override
    public void addEdges(Content content, Collection<AbstractDirectedEdge> edges) {
        for (AbstractDirectedEdge e : edges) {
            Node source = nDao.findNode(Integer.valueOf(e.getSource().getId()));
            Node target = nDao.findNode(Integer.valueOf(e.getTarget().getId()));
            addEdge(content, e, source, target);
        }
    }

    /**
     * @see org.apromore.service.ContentService#addEdge(org.apromore.dao.model.Content, org.jbpt.graph.abs.AbstractDirectedEdge, org.apromore.dao.model.Node, org.apromore.dao.model.Node)
     * {@inheritDoc}
     */
    @Override
    public void addEdge(Content content, AbstractDirectedEdge e, Node source, Node target) {
        Edge edge = new Edge();
        
        edge.setContent(content);
        edge.setVerticesBySourceVid(source);
        edge.setVerticesByTargetVid(target);
        edge.setOriginalId(e.getId());
        edge.setCond("");
        edge.setDef(false);

        edgeDao.save(edge);
    }

    /**
     * @see org.apromore.service.ContentService#deleteContent(String)
     * {@inheritDoc}
     */
    @Override
    public void deleteContent(String contentId) {
        Content content = contentDao.findContent(contentId);
        contentDao.delete(content);
    }

    /**
     * @see org.apromore.service.ContentService#deleteContent(String)
     * {@inheritDoc}
     */
    @Override
    public void deleteEdgesOfContent(String contentId) {
        Content content = contentDao.findContent(contentId);
        for (Edge edge : content.getEdges()) {
            edgeDao.delete(edge);
        }
    }

    /**
     * @see org.apromore.service.ContentService#deleteContent(String)
     * {@inheritDoc}
     */
    @Override
    public void deleteVerticesOfContent(String contentId) {
        Content content = contentDao.findContent(contentId);
        for (Node node : content.getVertices()) {
            nDao.delete(node);
        }
    }



    private void addObjects(Node node, ICpfNode v) {
        ObjectRefType obj;
        for (ICpfObject cObj : v.getObjects()) {
            obj = new ObjectRefType();
            if (cObj.getId() != null) {
                obj.setId(Integer.valueOf(cObj.getId()));
            }
            obj.setConsumed(String.valueOf(cObj.getConsumed()));
            obj.setOptional(String.valueOf(cObj.getOptional()));
            obj.setOriginalId(cObj.getId());
            node.getObjectRefTypes().add(obj);

            addObjectAttributes(obj, cObj);
        }
    }

    private void addObjectAttributes(ObjectRefType obj, ICpfObject cObj) {
        ObjectRefTypeAttribute oAtt;
        for (Entry<String, String> e : cObj.getAttributes().entrySet()) {
            oAtt = new ObjectRefTypeAttribute();
            oAtt.setName(e.getKey());
            oAtt.setValue(e.getValue());
            obj.getObjectRefTypeAttributes().add(oAtt);
        }
    }


    private void addResources(Node node, ICpfNode v) {
        ResourceRefType resource;
        for (ICpfResource cRes : v.getResource()) {
            resource = new ResourceRefType();
            if (cRes.getId() != null) {
                resource.setId(Integer.valueOf(cRes.getId()));
            }
            resource.setQualifier(cRes.getQualifier());
            resource.setOptional(String.valueOf(cRes.getOptional()));
            node.getResourceRefTypes().add(resource);

            addResourceAttributes(resource, cRes);
        }
    }

    private void addResourceAttributes(ResourceRefType obj, ICpfResource cObj) {
        ResourceRefTypeAttribute rAtt;
        for (Entry<String, String> e : cObj.getAttributes().entrySet()) {
            rAtt = new ResourceRefTypeAttribute();
            rAtt.setName(e.getKey());
            rAtt.setValue(e.getValue());
            obj.getResourceRefTypeAttributes().add(rAtt);
        }
    }


    private void addNodeAttributes(Node node, ICpfNode v) {
        NodeAttribute nAtt;
        for (Entry<String, String> e : v.getAttributes().entrySet()) {
            nAtt = new NodeAttribute();
            nAtt.setName(e.getKey());
            nAtt.setValue(e.getValue());
            node.getAttributes().add(nAtt);
        }
    }




    /**
     * Set the Content DAO object for this class. Mainly for spring tests.
     * @param contentDaoJpa the Content Dao.
     */
    public void setContentDao(ContentDao contentDaoJpa) {
        contentDao = contentDaoJpa;
    }

    /**
     * Set the Edge DAO object for this class. Mainly for spring tests.
     * @param edgeDaoJpa the edge Dao.
     */
    public void setEdgeDao(EdgeDao edgeDaoJpa) {
        edgeDao = edgeDaoJpa;
    }

    /**
     * Set the Node DAO object for this class. Mainly for spring tests.
     * @param nodeDaoJpa the Node Dao.
     */
    public void setNodeDao(NodeDao nodeDaoJpa) {
        nDao = nodeDaoJpa;
    }

    /**
     * Set the Non Pocket Node DAO object for this class. Mainly for spring tests.
     * @param nonPocketNodeDaoJpa the Non Pocket Node Dao.
     */
    public void setNonPocketNodeDao(NonPocketNodeDao nonPocketNodeDaoJpa) {
        nonPocketNodeDao = nonPocketNodeDaoJpa;
    }
}
