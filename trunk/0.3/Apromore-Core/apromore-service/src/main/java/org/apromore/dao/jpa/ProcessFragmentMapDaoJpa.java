package org.apromore.dao.jpa;

import org.apromore.dao.ProcessFragmentMapDao;
import org.apromore.dao.model.ProcessFragmentMap;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    public void save(ProcessFragmentMap processFragmentMap) {
        em.persist(processFragmentMap);
    }

    /**
     * @see org.apromore.dao.ProcessFragmentMapDao#delete(org.apromore.dao.model.ProcessFragmentMap)
     * {@inheritDoc}
     */
    @Override
    public ProcessFragmentMap update(ProcessFragmentMap processFragmentMap) {
        return em.merge(processFragmentMap);
    }

    /**
     * @see org.apromore.dao.ProcessFragmentMapDao#delete(org.apromore.dao.model.ProcessFragmentMap)
     * {@inheritDoc}
     */
    @Override
    public void delete(ProcessFragmentMap processFragmentMap) {
        em.remove(processFragmentMap);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working
     * @param em the entitymanager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
