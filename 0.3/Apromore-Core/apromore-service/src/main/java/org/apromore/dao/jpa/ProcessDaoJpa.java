package org.apromore.dao.jpa;

import org.apromore.dao.ProcessDao;
import org.apromore.dao.model.Process;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Hibernate implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository(value = "ProcessDao")
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessDaoJpa extends JpaTemplate implements ProcessDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessDaoJpa.class.getName());

    /* This is for complex queries, no point putting in models for these. */
    public static final String GET_ALL_PROCESSES = "SELECT p, coalesce(r.id.ranking, 0) FROM Process p, ProcessRanking r WHERE p.processId = r.id.processId ";
    public static final String GET_ALL_PRO_SORT = " ORDER by p.processId";
    
    
    /**
     * Returns all the processes.
     * @see org.apromore.dao.ProcessDao#getAllProcesses(String)
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> getAllProcesses(final String conditions) {
        return execute(new JpaCallback<List<Object[]>>() {

            @SuppressWarnings("unchecked")
            public List<Object[]> doInJpa(EntityManager em) {
                StringBuffer strQry = new StringBuffer();
                strQry.append(GET_ALL_PROCESSES);
                if (conditions != null && conditions.isEmpty()) {
                    strQry.append(conditions);
                }
                strQry.append(GET_ALL_PRO_SORT);

                LOGGER.debug("Query: " + strQry.toString());

                Query query = em.createQuery(strQry.toString());
                return query.getResultList();
            }
        });
    }

    /**
     * Returns all distinct domains from the Processes table.
     * @see org.apromore.dao.ProcessDao#getAllDomains()
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllDomains() {
        return execute(new JpaCallback<List<String>>() {

            @SuppressWarnings("unchecked")
            public List<String> doInJpa(EntityManager em) {
                Query query = em.createNamedQuery(Process.GET_All_DOMAINS);
                return query.getResultList();
            }
        });
    }



    /**
     * Remove the process.
     * @see org.apromore.dao.ProcessDao#delete(Process)
     * {@inheritDoc}
     */
    @Override
    public void save(Process process) {
        persist(process);
    }

    /**
     * Remove the User.
     * @see org.apromore.dao.ProcessDao#delete(Process)
     * {@inheritDoc}
     */
    @Override
    public void update(Process process) {
        merge(process);
    }

    /**
     * Remove the User.
     * @see org.apromore.dao.ProcessDao#delete(Process)
     * {@inheritDoc}
     */
    @Override
    public void delete(Process process) {
         remove(process);
    }
}
