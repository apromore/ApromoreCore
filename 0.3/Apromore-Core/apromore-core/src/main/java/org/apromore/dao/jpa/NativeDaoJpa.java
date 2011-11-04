package org.apromore.dao.jpa;

import org.apromore.dao.NativeDao;
import org.apromore.dao.model.Native;
import org.apromore.exception.NativeFormatNotFoundException;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
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
     * @see org.apromore.dao.NativeDao#findNativeByCanonical(long, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Native> findNativeByCanonical(final long processId, final String versionName) {
        return execute(new JpaCallback<List<Native>>() {

            @SuppressWarnings("unchecked")
            public List<Native> doInJpa(EntityManager em) {
                Query query = em.createNamedQuery(Native.FIND_NATIVE_TYPES);
                query.setParameter("processId", processId);
                query.setParameter("versionName", versionName);
                return query.getResultList();
            }
        });
    }


    /**
     * Returns the Native format as XML.
     * @see org.apromore.dao.NativeDao#getNative(long, String, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public String getNative(final long processId, final String version, final String nativeType) throws NativeFormatNotFoundException {
        String result = execute(new JpaCallback<String>() {
            @SuppressWarnings("unchecked")
            public String doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery(Native.GET_NATIVE);
                query.setParameter("processId", processId);
                query.setParameter("versionName", version);
                query.setParameter("nativeType", nativeType);
                return (String) query.getSingleResult();
            }
        });
        if (result == null) {
            throw new NativeFormatNotFoundException("The Native Process Model for Process (" + processId + "," + version +
                    ") cannot be found.");
        }
        return result;
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
