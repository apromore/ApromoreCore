package org.apromore.dao.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.EdgeDao;
import org.apromore.dao.ExpressionDao;
import org.apromore.dao.NamedQueries;
import org.apromore.dao.model.Edge;
import org.apromore.dao.model.Expression;
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
public class ExpressionDaoJpa implements ExpressionDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.ExpressionDao#save(org.apromore.dao.model.Expression)
     * {@inheritDoc}
     */
    @Override
    public void save(final Expression expression) {
        em.persist(expression);
    }

    /**
     * @see org.apromore.dao.ExpressionDao#update(org.apromore.dao.model.Expression)
     * {@inheritDoc}
     */
    @Override
    public Expression update(final Expression expression) {
        return em.merge(expression);
    }

    /**
     * @see org.apromore.dao.ExpressionDao#delete(org.apromore.dao.model.Expression)
     * {@inheritDoc}
     */
    @Override
    public void delete(final Expression expression) {
        em.remove(expression);
    }



    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
