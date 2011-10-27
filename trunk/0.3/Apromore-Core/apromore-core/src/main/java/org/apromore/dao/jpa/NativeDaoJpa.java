package org.apromore.dao.jpa;

import org.apromore.dao.NativeDao;
import org.apromore.dao.model.Annotation;
import org.apromore.dao.model.Canonical;
import org.apromore.dao.model.Native;
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
public class NativeDaoJpa extends JpaTemplate implements NativeDao {


    /**
     * Returns list of natives.
     * @see org.apromore.dao.NativeDao#findNativeByCanonical(int, String)
     * {@inheritDoc}
     */
    @Override
    public List<Native> findNativeByCanonical(final int processId, final String versionName) {
        return execute(new JpaCallback<List<Native>>() {

            @SuppressWarnings("unchecked")
            public List<Native> doInJpa(EntityManager em) {
                Query query = em.createNamedQuery(Native.FIND_NATIVE_TYPES);
                query.setParameter("processId", processId);
                query.setParameter("versionName", versionName);
                List<Native> natives = query.getResultList();
                if (natives.isEmpty()) {
                    return null;
                } else {
                    return natives;
                }
            }
        });
    }


    /**
     * Remove the Native.
     * @see org.apromore.dao.NativeDao#delete(org.apromore.dao.model.Native)
     * {@inheritDoc}
     */
    @Override
    public void save(Native natve) {
        persist(natve);
    }

    /**
     * Remove the Native.
     * @see org.apromore.dao.NativeDao#delete(org.apromore.dao.model.Native)
     * {@inheritDoc}
     */
    @Override
    public void update(Native natve) {
        merge(natve);
    }

    /**
     * Remove the Native.
     * @see org.apromore.dao.NativeDao#delete(org.apromore.dao.model.Native)
     * {@inheritDoc}
     */
    @Override
    public void delete(Native natve) {
         remove(natve);
    }
}
