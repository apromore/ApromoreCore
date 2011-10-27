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
                Query query = em.createNamedQuery(NativeType.FIND_FORMATS);
                List<NativeType> natives = query.getResultList();
                if (natives.isEmpty()) {
                    return null;
                } else {
                    return natives;
                }
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
