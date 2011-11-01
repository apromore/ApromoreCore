package org.apromore.dao.jpa;

import org.apromore.dao.CanonicalDao;
import org.apromore.dao.ProcessDao;
import org.apromore.dao.model.Canonical;
import org.apromore.dao.model.Process;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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



    /**
     * Returns a Canonical
     * @see org.apromore.dao.CanonicalDao#findByProcessId(long)
     * {@inheritDoc}
     */
    @Override
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
