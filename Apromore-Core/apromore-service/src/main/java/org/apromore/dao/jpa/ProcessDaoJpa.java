package org.apromore.dao.jpa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.ProcessDao;
import org.apromore.dao.model.Process;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessDaoJpa implements ProcessDao {

    @PersistenceContext
    private EntityManager em;

    /** The start of the manual search query. */
    public static final String GET_ALL_PROCESSES = "SELECT p FROM Process p ";
    /** The order by for the manual search query. */
    public static final String GET_ALL_PRO_SORT = " ORDER by p.id";


    /**
     * @see org.apromore.dao.ProcessDao#findProcess(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Process findProcess(final Integer processId) {
        return em.find(Process.class, processId);
    }


    /**
     * @see org.apromore.dao.ProcessDao#getProcesses()
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Process> getProcesses() {
        Query query = em.createNamedQuery(NamedQueries.GET_All_PROCESSES);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ProcessDao#getAllProcesses(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Process> getAllProcesses(final String conditions) {
        StringBuilder strQry = new StringBuilder(0);
        strQry.append(GET_ALL_PROCESSES);
        if (conditions != null && !conditions.isEmpty()) {
            strQry.append(conditions);
        }
        strQry.append(GET_ALL_PRO_SORT);

        Query query = em.createQuery(strQry.toString());
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ProcessDao#getAllDomains()
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<String> getAllDomains() {
        Query query = em.createNamedQuery(NamedQueries.GET_All_DOMAINS);
        return query.getResultList();
    }


    /**
     * @see org.apromore.dao.ProcessDao#getProcess(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Process getProcess(final Integer processId) {
        Query query = em.createNamedQuery(NamedQueries.GET_PROCESS_BY_ID);
        query.setParameter("id", processId);
        List<Process> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * @see org.apromore.dao.ProcessDao#getProcess(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Process getProcess(final String processName) {
        Query query = em.createNamedQuery(NamedQueries.GET_PROCESS_BY_NAME);
        query.setParameter("name", processName);
        List<Process> result = query.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * @see org.apromore.dao.ProcessDao#getRootFragmentVersionId(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Integer getRootFragmentVersionId(final Integer processModelVersionId) {
        Query query = em.createNamedQuery(NamedQueries.GET_ROOT_FRAGMENT_PROCESS_MODEL);
        query.setParameter("id", processModelVersionId);
        return (Integer) query.getSingleResult();
    }

    /**
     * @see org.apromore.dao.ProcessDao#getRootFragmentVersionURI(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public String getRootFragmentVersionURI(final Integer processModelVersionUri) {
        Query query = em.createNamedQuery(NamedQueries.GET_ROOT_FRAGMENT_PROCESS_MODEL_URI);
        query.setParameter("uri", processModelVersionUri);
        return (String) query.getSingleResult();
    }


    /**
     * @see org.apromore.dao.ProcessDao#getCurrentProcessModels()
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<Integer, int[]> getCurrentProcessModels() {
        Map<Integer, int[]> results = new HashMap<Integer, int[]>(0);

        Query query = em.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODELS);
        List<Object[]> res = (List<Object[]>) query.getResultList();
        for (Object[] obj : res) {
            int[] versionInfo = new int[2];
            versionInfo[0] = (Integer) obj[1];
            versionInfo[1] = (Integer) obj[2];
            results.put((Integer) obj[0], versionInfo);
        }
        return results;
    }


    /**
     * @see org.apromore.dao.ProcessDao#delete(Process)
     * {@inheritDoc}
     */
    @Override
    public void save(final Process process) {
        em.persist(process);
    }

    /**
     * @see org.apromore.dao.ProcessDao#delete(Process)
     * {@inheritDoc}
     */
    @Override
    public Process update(final Process process) {
        return em.merge(process);
    }

    /**
     * @see org.apromore.dao.ProcessDao#delete(Process)
     * {@inheritDoc}
     */
    @Override
    public void delete(final Process process) {
        em.remove(process);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
