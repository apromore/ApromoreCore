package org.apromore.dao.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.NodeDao;
import org.apromore.dao.model.Node;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.NodeDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class NodeDaoJpa implements NodeDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.NodeDao#findNode(Integer)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Node findNode(Integer nodeId) {
        return em.find(Node.class, nodeId);
    }


    /**
     * @see org.apromore.dao.NodeDao#getContentIDs()
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getContentIDs() {
        Query query = em.createNamedQuery(NamedQueries.GET_CONTENT_IDS);
        return (List<String>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.NodeDao#getVertexByContent(String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Node> getVertexByContent(final String contentID) {
        Query query = em.createNamedQuery(NamedQueries.GET_VERTICES_BY_CONTENT);
        query.setParameter("contentId", contentID);
        return (List<Node>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.NodeDao#getStoredVertices()
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getStoredVertices() {
        Query query = em.createNamedQuery(NamedQueries.GET_STORED_VERTICES);
        return (Integer) query.getSingleResult();
    }


    /**
     * @see org.apromore.dao.NodeDao#save(org.apromore.dao.model.Node)
     *      {@inheritDoc}
     */
    @Override
    public void save(final Node node) {
        em.persist(node);
    }

    /**
     * @see org.apromore.dao.NodeDao#update(org.apromore.dao.model.Node)
     *      {@inheritDoc}
     */
    @Override
    public Node update(final Node node) {
        return em.merge(node);
    }

    /**
     * @see org.apromore.dao.NodeDao#delete(org.apromore.dao.model.Node)
     *      {@inheritDoc}
     */
    @Override
    public void delete(final Node node) {
        em.remove(node);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working
     *
     * @param em the entitymanager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
