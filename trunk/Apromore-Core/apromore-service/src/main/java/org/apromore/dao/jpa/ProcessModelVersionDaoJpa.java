package org.apromore.dao.jpa;

import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
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
public class ProcessModelVersionDaoJpa implements ProcessModelVersionDao {

    @PersistenceContext
    private EntityManager em;

    private static final String GET_ALL_PROCESSES = "SELECT pmv FROM ProcessModelVersion pmv, ProcessBranch pb WHERE pb.branchId = " +
            "pmv.processBranch.branchId ";
    private static final String GET_LATEST_PROCESSES = "AND pb.creationDate in (SELECT max(pb2.creationDate) FROM ProcessBranch pb2 WHERE " +
            "pb2.branchId = pmv.processBranch.branchId GROUP BY pb2.branchId)";
    private static final String GET_ALL_PRO_SORT = " ORDER by pb.branchId, pb.creationDate ";


    /**
     * @see org.apromore.dao.ProcessModelVersionDao#findProcessModelVersion(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModelVersion findProcessModelVersion(final Integer processModelVersionId) {
        return em.find(ProcessModelVersion.class, processModelVersionId);
    }


    /**
     * @see org.apromore.dao.ProcessModelVersionDao#findProcessModelVersionByBranch(Integer, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModelVersion findProcessModelVersionByBranch(final Integer branchId, final String branchName) {
        Query query = em.createNamedQuery(NamedQueries.GET_PROCESS_MODEL_VERSION_BY_BRANCH);
        query.setParameter("id", branchId);
        query.setParameter("name", branchName);
        return (ProcessModelVersion) query.getSingleResult();
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getUsedProcessModelVersions(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<ProcessModelVersion> getUsedProcessModelVersions(final Integer fragmentVersionId) {
        Query query = em.createNamedQuery(NamedQueries.GET_USED_PROCESS_MODEL_VERSIONS);
        query.setParameter("id", fragmentVersionId);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getUsedProcessModelVersionsByURI(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<ProcessModelVersion> getUsedProcessModelVersionsByURI(final String uri) {
        Query query = em.createNamedQuery(NamedQueries.GET_USED_PROCESS_MODEL_VERSIONS);
        query.setParameter("uri", uri);
        return query.getResultList();
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getCurrentVersion(Integer, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModelVersion getCurrentVersion(Integer processId, String processName) {
        Query query = em.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL);
        query.setParameter("processId", processId);
        query.setParameter("versionName", processName);

        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return (ProcessModelVersion) results.get(0);
        }
        throw new NonUniqueResultException();
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getCurrentProcessModelVersion(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModelVersion getCurrentProcessModelVersion(final Integer branchId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL_VERSION_A);
        query.setParameter("branchId", branchId);
        return (ProcessModelVersion) query.getSingleResult();
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getCurrentProcessModelVersion(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModelVersion getCurrentProcessModelVersion(Integer processId, String versionName) {
        Query query = em.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL_VERSION);
        query.setParameter("processId", processId);
        query.setParameter("versionName", versionName);
        return (ProcessModelVersion) query.getSingleResult();
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getCurrentProcessModelVersion(String, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModelVersion getCurrentProcessModelVersion(final String processName, final String branchName) {
        Query query = em.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL_VERSION_B);
        query.setParameter("processName", processName);
        query.setParameter("branchName", branchName);
        return (ProcessModelVersion) query.getSingleResult();
    }


    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getCurrentProcessModelVersion(String, String, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModelVersion getCurrentProcessModelVersion(final String processName, final String branchName, final String versionName) {
        Query query = em.createNamedQuery(NamedQueries.GET_CURRENT_PROCESS_MODEL_VERSION_C);
        query.setParameter("processName", processName);
        query.setParameter("branchName", branchName);
        query.setParameter("versionName", versionName);
        return (ProcessModelVersion) query.getSingleResult();
    }


    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getMaxModelVersions(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getMaxModelVersions(final Integer fragmentVersionId) {
        Query query = em.createNamedQuery(NamedQueries.GET_MAX_MODEL_VERSIONS);
        query.setParameter("fragmentVersionId", fragmentVersionId);
        return (Map<String, Integer>) query.getResultList();
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getCurrentModelVersions(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getCurrentModelVersions(final Integer fragmentVersionId) {
        Query query = em.createNamedQuery(NamedQueries.GET_CURRENT_MODEL_VERSIONS);
        query.setParameter("fragmentVersionId", fragmentVersionId);
        return (Map<String, Integer>) query.getResultList();
    }


    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getMaxVersionProcessModel(org.apromore.dao.model.ProcessBranch)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessModelVersion getMaxVersionProcessModel(final ProcessBranch branch) {
        Query query = em.createNamedQuery(NamedQueries.GET_MAX_VERSION_PROCESS_MODEL);
        query.setParameter("branchId", branch.getId());

        List results = query.getResultList();
        if (results.isEmpty()) {
            return null;
        } else if (results.size() == 1) {
            return (ProcessModelVersion) results.get(0);
        }
        throw new NonUniqueResultException();
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#getAllProcessModelVersions(boolean)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<ProcessModelVersion> getAllProcessModelVersions(final boolean isLatestVersion) {
        StringBuilder strQry = new StringBuilder();
        strQry.append(GET_ALL_PROCESSES);
        if (isLatestVersion) {
            strQry.append(GET_LATEST_PROCESSES);
        }
        strQry.append(GET_ALL_PRO_SORT);

        Query query = em.createQuery(strQry.toString());
        return (List<ProcessModelVersion>) query.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Integer> getRootFragments(int minSize) {
        Query query = em.createNamedQuery(NamedQueries.GET_ROOT_FRAGMENT_IDS_ABOVE_SIZE);
        query.setParameter("minSize", minSize);
        return (List<Integer>) query.getResultList();
    }


    /**
     * @see org.apromore.dao.ProcessModelVersionDao#delete(org.apromore.dao.model.ProcessModelVersion)
     * {@inheritDoc}
     */
    @Override
    public void save(final ProcessModelVersion processModelVersionId) {
        em.persist(processModelVersionId);
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#delete(org.apromore.dao.model.ProcessModelVersion)
     * {@inheritDoc}
     */
    @Override
    public ProcessModelVersion update(final ProcessModelVersion processModelVersionId) {
        return em.merge(processModelVersionId);
    }

    /**
     * @see org.apromore.dao.ProcessModelVersionDao#delete(org.apromore.dao.model.ProcessModelVersion)
     * {@inheritDoc}
     */
    @Override
    public void delete(final ProcessModelVersion processModelVersionId) {
        em.remove(processModelVersionId);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
