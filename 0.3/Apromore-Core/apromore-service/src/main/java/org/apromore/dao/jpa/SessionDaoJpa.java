package org.apromore.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apromore.dao.SessionDao;
import org.apromore.dao.model.EditSession;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.SessionDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class SessionDaoJpa implements SessionDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.SessionDao#findSession(int)
     * {@inheritDoc}
     */
    @Override
    public EditSession findSession(final int sessionId) {
        return em.find(EditSession.class, sessionId);
    }



    /**
     * @see org.apromore.dao.SessionDao#save(org.apromore.dao.model.EditSession)
     * {@inheritDoc}
     */
    @Override
    public void save(final EditSession session) {
        em.persist(session);
    }

    /**
     * @see org.apromore.dao.SessionDao#update(org.apromore.dao.model.EditSession)
     * {@inheritDoc}
     */
    @Override
    public EditSession update(final EditSession session) {
        return em.merge(session);
    }

    /**
     * @see org.apromore.dao.SessionDao#delete(org.apromore.dao.model.EditSession)
     * {@inheritDoc}
     */
    @Override
    public void delete(final EditSession session) {
        em.remove(session);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
