package org.apromore.dao.jpa;

import org.apromore.dao.NativeTypeDao;
import org.apromore.dao.model.NativeType;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Hibernate implementation of the org.apromore.dao.NativeDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository(value = "NativeDao")
@Transactional(propagation = Propagation.REQUIRED)
public class NativeTypeDaoJpa extends JpaTemplate implements NativeTypeDao {


    /**
     * Returns list of NativeType.
     * @see org.apromore.dao.NativeTypeDao#findAllFormats()
     * {@inheritDoc}
     */
    @Override
    public List<NativeType> findAllFormats() {
        return execute(new JpaCallback<List<NativeType>>() {

            @SuppressWarnings("unchecked")
            public List<NativeType> doInJpa(EntityManager em) {
                Query query = em.createNamedQuery(NativeType.FIND_FORMAT);
                return query.getResultList();
            }
        });
    }

    /**
     * Find a particular Native Type.
     * @see org.apromore.dao.NativeTypeDao#findAllFormats()
     * {@inheritDoc}
     */
    @Override
    public NativeType findNativeType(final String nativeType) {
        return execute(new JpaCallback<NativeType>() {

            @SuppressWarnings("unchecked")
            public NativeType doInJpa(EntityManager em) {
                Query query = em.createNamedQuery(NativeType.FIND_FORMATS);
                query.setParameter("name", nativeType);
                return (NativeType) query.getSingleResult();
            }
        });
    }




    /**
     * Remove the NativeType.
     * @see org.apromore.dao.NativeTypeDao#delete(org.apromore.dao.model.NativeType)
     * {@inheritDoc}
     */
    @Override
    public void save(NativeType nativeType) {
        persist(nativeType);
    }

    /**
     * Remove the NativeType.
     * @see org.apromore.dao.NativeTypeDao#delete(org.apromore.dao.model.NativeType)
     * {@inheritDoc}
     */
    @Override
    public void update(NativeType nativeType) {
        merge(nativeType);
    }

    /**
     * Remove the NativeType.
     * @see org.apromore.dao.NativeTypeDao#delete(org.apromore.dao.model.NativeType)
     * {@inheritDoc}
     */
    @Override
    public void delete(NativeType nativeType) {
         remove(nativeType);
    }
}
