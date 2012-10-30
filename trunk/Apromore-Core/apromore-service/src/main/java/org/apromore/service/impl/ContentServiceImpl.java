package org.apromore.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apromore.common.Constants;
import org.apromore.dao.ContentDao;
import org.apromore.dao.EdgeDao;
import org.apromore.dao.NodeDao;
import org.apromore.dao.NonPocketNodeDao;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.Expression;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.NodeAttribute;
import org.apromore.dao.model.NonPocketNode;
import org.apromore.dao.model.ObjectRefType;
import org.apromore.dao.model.ObjectRefTypeAttribute;
import org.apromore.dao.model.ResourceRefType;
import org.apromore.dao.model.ResourceRefTypeAttribute;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.IAttribute;
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.IObject;
import org.apromore.graph.canonical.IResource;
import org.apromore.service.ContentService;
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
    public Integer getMatchingContentId(final String hash) {
        if (hash == null) {
            return null;
        }
        return contentDao.getContentByCode(hash).getId();
    }


    /**
     * @see org.apromore.service.ContentService#addContent(org.apromore.graph.canonical.Canonical, String, org.apromore.graph.canonical.Canonical, java.util.Map)
     *      {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Content addContent(final Canonical c, final String hash, final Canonical g, final Map<String, String> pocketIdMappings) {
        Content content = new Content();
        content.setCode(hash);
        contentDao.save(content);

        addNodes(content, c.getNodes(), g, pocketIdMappings);
        addEdges(content, c.getEdges());

        content.setBoundaryS(c.getEntry().getId());
        content.setBoundaryE(c.getExit().getId());
        contentDao.update(content);

        return content;
    }

    /**
     * @see org.apromore.service.ContentService#addNodes(org.apromore.dao.model.Content, java.util.Collection, org.apromore.graph.canonical.Canonical, java.util.Map)
     * {@inheritDoc}
     */
    @Override
    public void addNodes(final Content content, final Collection<org.apromore.graph.canonical.Node> vertices, final Canonical g,
            final Map<String, String> pocketIdMappings) {
        for (org.apromore.graph.canonical.Node v : vertices) {
            String oldVId = v.getId();
            String type = g.getNodeProperty(v.getId(), Constants.TYPE);
            Node n = addNode(content, v, type);
            if (!"Pocket".equals(type)) {
                addNonPocketNode(n);
            } else {
                pocketIdMappings.put(oldVId, n.getUri());
            }
        }
    }

    /**
     * @see org.apromore.service.ContentService#addNode(org.apromore.dao.model.Content, org.apromore.graph.canonical.Node, String)
     *      {@inheritDoc}
     */
    @Override
    public Node addNode(final Content content, final org.apromore.graph.canonical.Node v, final String vtype) {
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
     * @see org.apromore.service.ContentService#addEdges(org.apromore.dao.model.Content, java.util.Set)
     *      {@inheritDoc}
     */
    @Override
    public void addEdges(final Content content, final Set<org.apromore.graph.canonical.Edge> edges) {
        for (org.apromore.graph.canonical.Edge e : edges) {
            Node source = nDao.findNodeByUri(e.getSource().getId());
            Node target = nDao.findNodeByUri(e.getTarget().getId());
            addEdge(content, e, source, target);
        }
    }

    /**
     * @see org.apromore.service.ContentService#addEdge(org.apromore.dao.model.Content, org.apromore.graph.canonical.Edge, org.apromore.dao.model.Node, org.apromore.dao.model.Node)
     *      {@inheritDoc}
     */
    @Override
    public void addEdge(final Content content, final org.apromore.graph.canonical.Edge e, final Node source, final Node target) {
        Edge edge = new Edge();

        edge.setContent(content);
        edge.setVerticesBySourceVid(source);
        edge.setVerticesByTargetVid(target);
        edge.setOriginalId(e.getId());
        edge.setUri(e.getId());

        if (e.getConditionExpr() != null) {
            edge.setExpression(addExpression(e.getConditionExpr()));
        }

        edgeDao.save(edge);
    }

    private Expression addExpression(org.apromore.graph.canonical.Expression expression) {
        Expression expr = new Expression();

        expr.setDescription(expression.getDescription());
        expr.setExpression(expression.getExpression());
        expr.setLanguage(expression.getLanguage());
        expr.setReturnType(expression.getReturnType());
        //exprDao.save(expr);
        return expr;
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


    private void addObjects(final Node node, final INode v) {
        ObjectRefType obj;
        for (IObject cObj : v.getObjects()) {
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

    private void addObjectAttributes(final ObjectRefType obj, final IObject cObj) {
        ObjectRefTypeAttribute oAtt;
        for (Entry<String, IAttribute> e : cObj.getAttributes().entrySet()) {
            oAtt = new ObjectRefTypeAttribute();
            oAtt.setName(e.getKey());
            oAtt.setValue(e.getValue().getValue());
            //TODO persist getAny() as XML string
            obj.getObjectRefTypeAttributes().add(oAtt);
        }
    }


    private void addResources(final Node node, final INode v) {
        ResourceRefType resource;
        for (IResource cRes : v.getResource()) {
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

    private void addResourceAttributes(final ResourceRefType obj, final IResource cObj) {
        ResourceRefTypeAttribute rAtt;
        for (Entry<String, IAttribute> e : cObj.getAttributes().entrySet()) {
            rAtt = new ResourceRefTypeAttribute();
            rAtt.setName(e.getKey());
            rAtt.setValue(e.getValue().getValue());
            //TODO persist getAny() as XML string
            obj.getResourceRefTypeAttributes().add(rAtt);
        }
    }


    private void addNodeAttributes(final Node node, final INode v) {
        NodeAttribute nAtt;
        for (Entry<String, IAttribute> e : v.getAttributes().entrySet()) {
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
