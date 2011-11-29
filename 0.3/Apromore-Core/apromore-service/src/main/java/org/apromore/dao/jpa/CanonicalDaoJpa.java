package org.apromore.dao.jpa;

import org.apromore.dao.CanonicalDao;
import org.apromore.dao.model.Canonical;
import org.apromore.exception.CanonicalFormatNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * Hibernate implementation of the org.apromore.dao.CanonicalDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository(value = "CanonicalDao")
@Transactional(propagation = Propagation.REQUIRED)
public class CanonicalDaoJpa extends JpaTemplate implements CanonicalDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(CanonicalDaoJpa.class);


    /**
     * Returns a Canonical
     * @see org.apromore.dao.CanonicalDao#findByProcessId(long)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Canonical> findByProcessId(final long processId) {
        return execute(new JpaCallback<List<Canonical>>() {

            @SuppressWarnings("unchecked")
            public List<Canonical> doInJpa(EntityManager em) {
                Query query = em.createNamedQuery(Canonical.FIND_BY_PROCESS_ID);
                query.setParameter("processId", processId);
                return query.getResultList();
            }
        });
    }


    /**
     * Returns the Canonical format as XML.
     * @see org.apromore.dao.CanonicalDao#getCanonical(long, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Canonical getCanonical(final long processId, final String version) throws CanonicalFormatNotFoundException {
        Canonical result = execute(new JpaCallback<Canonical>() {
            @SuppressWarnings("unchecked")
            public Canonical doInJpa(EntityManager em) throws PersistenceException {
                Query query = em.createNamedQuery(Canonical.GET_CANONICAL);
                query.setParameter("processId", processId);
                query.setParameter("versionName", version);
                return (Canonical) query.getSingleResult();
            }
        });
        if (result == null) {
            throw new CanonicalFormatNotFoundException("The Canonical Process Model for Process (" + processId + "," + version +
                    ") cannot be found.");
        }
        return result;
    }



    /**
     * Remove the Canonical.
     * @see org.apromore.dao.CanonicalDao#delete(org.apromore.dao.model.Canonical)
     * {@inheritDoc}
     */
    @Override
    public void save(Canonical canonical) {
        persist(canonical);
    }

    /**
     * Remove the Canonical.
     * @see org.apromore.dao.CanonicalDao#delete(org.apromore.dao.model.Canonical)
     * {@inheritDoc}
     */
    @Override
    public void update(Canonical canonical) {
        merge(canonical);
    }

    /**
     * Remove the Canonical.
     * @see org.apromore.dao.CanonicalDao#delete(org.apromore.dao.model.Canonical)
     * {@inheritDoc}
     */
    @Override
    public void delete(Canonical canonical) {
         remove(canonical);
    }
}
