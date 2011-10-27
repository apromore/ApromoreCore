package org.apromore.dao.jpa;

import org.apromore.dao.ProcessDao;
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
 * Hibernate implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository(value = "ProcessDao")
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessDaoJpa extends JpaTemplate implements ProcessDao {


    /**
     * Returns all the processes.
     * @see org.apromore.dao.ProcessDao#getAllProcesses()
     * {@inheritDoc}
     */
    @Override
    public List<Object[]> getAllProcesses() {
        return execute(new JpaCallback<List<Object[]>>() {

            @SuppressWarnings("unchecked")
            public List<Object[]> doInJpa(EntityManager em) {
                Query query = em.createNamedQuery(Process.GET_ALL_PROCESSES);
                List<Object[]> processes = query.getResultList();
                if (processes.isEmpty()) {
                    return null;
                } else {
                    return processes;
                }
            }
        });
    }

    /**
     * Returns all distinct domains from the Processes table.
     * @see org.apromore.dao.ProcessDao#getAllDomains()
     * {@inheritDoc}
     */
    @Override
    public List<Object> getAllDomains() {
        return execute(new JpaCallback<List<Object>>() {

            @SuppressWarnings("unchecked")
            public List<Object> doInJpa(EntityManager em) {
                Query query = em.createNamedQuery(Process.GET_All_DOMAINS);
                List<Object> processes = query.getResultList();
                if (processes.isEmpty()) {
                    return null;
                } else {
                    return processes;
                }
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
