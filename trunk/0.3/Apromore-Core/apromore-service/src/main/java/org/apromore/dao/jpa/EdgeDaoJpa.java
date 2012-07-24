package org.apromore.dao.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.EdgeDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Edge;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.EdgeDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class EdgeDaoJpa implements EdgeDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.EdgeDao#getEdgesByContent(String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Edge> getEdgesByContent(final String contentID) {
        Query query = em.createNamedQuery(NamedQueries.GET_EDGES_BY_CONTENT);
        query.setParameter("contentId", contentID);
        return (List<Edge>) query.getResultList();
    }


    /**
     * @see org.apromore.dao.EdgeDao#getStoredEdges()
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getStoredEdges() {
        Query query = em.createNamedQuery(NamedQueries.GET_STORED_EDGES);
        return (Integer) query.getSingleResult();
    }


    /**
     * @see org.apromore.dao.EdgeDao#save(org.apromore.dao.model.Edge)
     *      {@inheritDoc}
     */
    @Override
    public void save(final Edge edge) {
        em.persist(edge);
    }

    /**
     * @see org.apromore.dao.EdgeDao#update(org.apromore.dao.model.Edge)
     *      {@inheritDoc}
     */
    @Override
    public Edge update(final Edge edge) {
        return em.merge(edge);
    }

    /**
     * @see org.apromore.dao.EdgeDao#delete(org.apromore.dao.model.Edge)
     *      {@inheritDoc}
     */
    @Override
    public void delete(final Edge edge) {
        em.remove(edge);
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
