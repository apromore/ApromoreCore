package org.apromore.dao.jpa;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.NativeTypeDao;
import org.apromore.dao.model.NativeType;
import org.springframework.stereotype.Repository;

/**
 * Hibernate implementation of the org.apromore.dao.NativeDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
public class NativeTypeDaoJpa implements NativeTypeDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.NativeTypeDao#findAllFormats()
     * {@inheritDoc}
     */
    @Override
    public List<NativeType> findAllFormats() {
        Query query = em.createNamedQuery(NamedQueries.GET_NATIVE_TYPE_FORMAT);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.NativeTypeDao#findAllFormats()
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public NativeType findNativeType(final String nativeType) {
        Query query = em.createNamedQuery(NamedQueries.GET_NATIVE_TYPE_FORMATS);
        query.setParameter("name", nativeType);
        List<NativeType> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
