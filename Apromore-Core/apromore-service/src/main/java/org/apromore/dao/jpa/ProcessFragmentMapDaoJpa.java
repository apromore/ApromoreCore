package org.apromore.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apromore.dao.ProcessFragmentMapDao;
import org.apromore.dao.model.ProcessFragmentMap;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.ProcessFragmentMapDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessFragmentMapDaoJpa implements ProcessFragmentMapDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.ProcessFragmentMapDao#delete(org.apromore.dao.model.ProcessFragmentMap)
     * {@inheritDoc}
     */
    @Override
    public void save(final ProcessFragmentMap processFragmentMap) {
        em.persist(processFragmentMap);
    }

    /**
     * @see org.apromore.dao.ProcessFragmentMapDao#delete(org.apromore.dao.model.ProcessFragmentMap)
     * {@inheritDoc}
     */
    @Override
    public ProcessFragmentMap update(final ProcessFragmentMap processFragmentMap) {
        return em.merge(processFragmentMap);
    }

    /**
     * @see org.apromore.dao.ProcessFragmentMapDao#delete(org.apromore.dao.model.ProcessFragmentMap)
     * {@inheritDoc}
     */
    @Override
    public void delete(final ProcessFragmentMap processFragmentMap) {
        em.remove(processFragmentMap);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
