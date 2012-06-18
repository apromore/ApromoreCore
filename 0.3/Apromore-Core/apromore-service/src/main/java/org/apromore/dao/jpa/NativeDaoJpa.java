package org.apromore.dao.jpa;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.NativeDao;
import org.apromore.dao.model.Native;
import org.apromore.exception.NativeFormatNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Hibernate implementation of the org.apromore.dao.NativeDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class NativeDaoJpa implements NativeDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.NativeDao#findNativeByCanonical(Integer, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Native> findNativeByCanonical(final Integer branchId, final String versionName) {
        Query query = em.createNamedQuery(NamedQueries.GET_NATIVE_TYPES);
        query.setParameter("branchId", branchId);
        query.setParameter("versionName", versionName);
        return query.getResultList();
    }


    /**
     * @see org.apromore.dao.NativeDao#getNative(Integer, String, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Native getNative(final Integer branchId, final String version, final String nativeType)
            throws NoResultException, NonUniqueResultException {
        Query query = em.createNamedQuery(NamedQueries.GET_NATIVE);
        query.setParameter("branchId", branchId);
        query.setParameter("versionName", version);
        query.setParameter("nativeType", nativeType);
        return (Native) query.getSingleResult();
    }



    /**
     * @see org.apromore.dao.NativeDao#delete(org.apromore.dao.model.Native)
     * {@inheritDoc}
     */
    @Override
    public void save(Native natve) {
        em.persist(natve);
    }

    /**
     * @see org.apromore.dao.NativeDao#delete(org.apromore.dao.model.Native)
     * {@inheritDoc}
     */
    @Override
    public Native update(Native natve) {
        return em.merge(natve);
    }

    /**
     * @see org.apromore.dao.NativeDao#delete(org.apromore.dao.model.Native)
     * {@inheritDoc}
     */
    @Override
    public void delete(Native natve) {
         em.remove(natve);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working
     * @param em the entitymanager
     */
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

}
