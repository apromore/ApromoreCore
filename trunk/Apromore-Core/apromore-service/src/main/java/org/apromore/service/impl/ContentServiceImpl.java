package org.apromore.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

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
import org.apromore.graph.JBPT.ICpfAttribute;
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
    public Integer getMatchingContentId(final String hash) {
        if (hash == null) {
            return null;
        }
        return contentDao.getContentByCode(hash).getId();
    }


    /**
     * @see org.apromore.service.ContentService#addContent(org.jbpt.graph.algo.rpst.RPSTNode, String, org.apromore.graph.JBPT.CPF, java.util.Map)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Content addContent(final RPSTNode c, final String hash, final CPF g, final Map<String, String> pocketIdMappings) {
        Content content = new Content();
        content.setCode(hash);
        contentDao.save(content);

        addNodes(content, c.getFragment().getVertices(), g, pocketIdMappings);
        addEdges(content, c.getFragmentEdges());

        content.setBoundaryS(c.getEntry().getId());
        content.setBoundaryE(c.getExit().getId());
        contentDao.update(content);

        return content;
    }

    /**
     * @see org.apromore.service.ContentService#addNodes(org.apromore.dao.model.Content, java.util.Collection, org.apromore.graph.JBPT.CPF, java.util.Map)
     * {@inheritDoc}
     */
    @Override
    public void addNodes(final Content content, final Collection<CpfNode> vertices, final CPF g, final Map<String, String> pocketIdMappings) {
        for (ICpfNode v : vertices) {
            String oldVId = v.getId();
            String type = g.getVertexProperty(v.getId(), Constants.TYPE);
            Node n = addNode(content, v, type);
            if (!"Pocket".equals(type)) {
                addNonPocketNode(n);
            } else {
                pocketIdMappings.put(oldVId, v.getId());
            }
        }
    }

    /**
     * @see org.apromore.service.ContentService#addNode
     *      {@inheritDoc}
     */
    @Override
    public Node addNode(final Content content, final ICpfNode v, final String vtype) {
        Node node = new Node();
        node.setContent(content);
        node.setName(v.getLabel() != null ? v.getLabel() : v.getName());
        node.setType(vtype);
        node.setConfiguration(v.isConfigurable());
        node.setCtype(v.getClass().getName());
        node.setOriginalId(v.getId());
        node.setUri(v.getId());

        addObjects(node, v);
        addResources(node, v);
        addNodeAttributes(node, v);

        nDao.save(node);

        return node;
    }

    /**
     * @see org.apromore.service.ContentService#addNonPocketNode(Node)
     *      {@inheritDoc}
     */
    @Override
    public void addNonPocketNode(final Node node) {
        NonPocketNode nonPockNode = new NonPocketNode();

        nonPockNode.setNode(node);

        nonPocketNodeDao.save(nonPockNode);
    }

    /**
     * @see org.apromore.service.ContentService#addEdges(Content, java.util.Collection)
     *      {@inheritDoc}
     */
    @Override
    public void addEdges(final Content content, final Collection<AbstractDirectedEdge> edges) {
        for (AbstractDirectedEdge e : edges) {
            Node source = nDao.findNodeByUri(e.getSource().getId());
            Node target = nDao.findNodeByUri(e.getTarget().getId());
            addEdge(content, e, source, target);
        }
    }

    /**
     * @see org.apromore.service.ContentService#addEdge(org.apromore.dao.model.Content, org.jbpt.graph.abs.AbstractDirectedEdge, org.apromore.dao.model.Node, org.apromore.dao.model.Node)
     *      {@inheritDoc}
     */
    @Override
    public void addEdge(final Content content, final AbstractDirectedEdge e, final Node source, final Node target) {
        Edge edge = new Edge();

        edge.setContent(content);
        edge.setVerticesBySourceVid(source);
        edge.setVerticesByTargetVid(target);
        edge.setOriginalId(e.getId());
        edge.setCond("");
        edge.setDef(false);
        edge.setUri(e.getId());

        edgeDao.save(edge);
    }

    /**
     * @see org.apromore.service.ContentService#deleteContent(Integer)
     *      {@inheritDoc}
     */
    @Override
    public void deleteContent(final Integer contentId) {
        Content content = contentDao.findContent(contentId);
        contentDao.delete(content);
    }

    /**
     * @see org.apromore.service.ContentService#deleteContent(Integer)
     *      {@inheritDoc}
     */
    @Override
    public void deleteEdgesOfContent(final Integer contentId) {
        Content content = contentDao.findContent(contentId);
        for (Edge edge : content.getEdges()) {
            edgeDao.delete(edge);
        }
    }

    /**
     * @see org.apromore.service.ContentService#deleteContent(Integer)
     *      {@inheritDoc}
     */
    @Override
    public void deleteVerticesOfContent(final Integer contentId) {
        Content content = contentDao.findContent(contentId);
        for (Node node : content.getNodes()) {
            nDao.delete(node);
        }
    }


    private void addObjects(final Node node, final ICpfNode v) {
        ObjectRefType obj;
        for (ICpfObject cObj : v.getObjects()) {
            obj = new ObjectRefType();
            if (cObj.getId() != null) {
                //TODO store in another field
                //obj.setId(Integer.valueOf(cObj.getId()));
            }
            obj.setConsumed(String.valueOf(cObj.getConsumed()));
            obj.setOptional(String.valueOf(cObj.getOptional()));
            obj.setOriginalId(cObj.getId());
            node.getObjectRefTypes().add(obj);

            addObjectAttributes(obj, cObj);
        }
    }

    private void addObjectAttributes(final ObjectRefType obj, final ICpfObject cObj) {
        ObjectRefTypeAttribute oAtt;
        for (Entry<String, ICpfAttribute> e : cObj.getAttributes().entrySet()) {
            oAtt = new ObjectRefTypeAttribute();
            oAtt.setName(e.getKey());
            oAtt.setValue(e.getValue().getValue());
            //TODO persist getAny() as XML string
            obj.getObjectRefTypeAttributes().add(oAtt);
        }
    }


    private void addResources(final Node node, final ICpfNode v) {
        ResourceRefType resource;
        for (ICpfResource cRes : v.getResource()) {
            resource = new ResourceRefType();
            if (cRes.getId() != null) {
                //TODO store in another field
                //resource.setId(Integer.valueOf(cRes.getId()));
            }
            resource.setQualifier(cRes.getQualifier());
            resource.setOptional(String.valueOf(cRes.getOptional()));
            node.getResourceRefTypes().add(resource);

            addResourceAttributes(resource, cRes);
        }
    }

    private void addResourceAttributes(final ResourceRefType obj, final ICpfResource cObj) {
        ResourceRefTypeAttribute rAtt;
        for (Entry<String, ICpfAttribute> e : cObj.getAttributes().entrySet()) {
            rAtt = new ResourceRefTypeAttribute();
            rAtt.setName(e.getKey());
            rAtt.setValue(e.getValue().getValue());
            //TODO persist getAny() as XML string
            obj.getResourceRefTypeAttributes().add(rAtt);
        }
    }


    private void addNodeAttributes(final Node node, final ICpfNode v) {
        NodeAttribute nAtt;
        for (Entry<String, ICpfAttribute> e : v.getAttributes().entrySet()) {
            nAtt = new NodeAttribute();
            nAtt.setName(e.getKey());
            nAtt.setValue(e.getValue().getValue());
            //TODO persist getAny() as XML string
            node.getAttributes().add(nAtt);
        }
    }


    /**
     * Set the Content DAO object for this class. Mainly for spring tests.
     *
     * @param contentDaoJpa the Content Dao.
     */
    public void setContentDao(final ContentDao contentDaoJpa) {
        contentDao = contentDaoJpa;
    }

    /**
     * Set the Edge DAO object for this class. Mainly for spring tests.
     *
     * @param edgeDaoJpa the edge Dao.
     */
    public void setEdgeDao(final EdgeDao edgeDaoJpa) {
        edgeDao = edgeDaoJpa;
    }

    /**
     * Set the Node DAO object for this class. Mainly for spring tests.
     *
     * @param nodeDaoJpa the Node Dao.
     */
    public void setNodeDao(final NodeDao nodeDaoJpa) {
        nDao = nodeDaoJpa;
    }

    /**
     * Set the Non Pocket Node DAO object for this class. Mainly for spring tests.
     *
     * @param nonPocketNodeDaoJpa the Non Pocket Node Dao.
     */
    public void setNonPocketNodeDao(final NonPocketNodeDao nonPocketNodeDaoJpa) {
        nonPocketNodeDao = nonPocketNodeDaoJpa;
    }
}
