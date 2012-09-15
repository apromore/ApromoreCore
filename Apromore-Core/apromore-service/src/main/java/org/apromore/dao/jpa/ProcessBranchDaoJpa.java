package org.apromore.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apromore.dao.NamedQueries;
import org.apromore.dao.ProcessBranchDao;
import org.apromore.dao.model.ProcessBranch;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Hibernate implementation of the org.apromore.dao.ProcessBranchDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class ProcessBranchDaoJpa implements ProcessBranchDao {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.ProcessBranchDao#findProcessBranch(Integer)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessBranch findProcessBranch(final Integer branchId) {
        return em.find(ProcessBranch.class, branchId);
    }


    /**
     * @see org.apromore.dao.ProcessBranchDao#getProcessBranchByProcessBranchName(Integer, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public ProcessBranch getProcessBranchByProcessBranchName(final Integer processId, final String branchName) {
        Query query = em.createNamedQuery(NamedQueries.GET_BRANCH_BY_PROCESS_BRANCH_NAME);
        query.setParameter("processId", processId);
        query.setParameter("name", branchName);
        return (ProcessBranch) query.getSingleResult();
    }


    /**
     * @see org.apromore.dao.ProcessBranchDao#save(org.apromore.dao.model.ProcessBranch)
     * {@inheritDoc}
     */
    @Override
    public void save(final ProcessBranch branch) {
        em.persist(branch);
    }

    /**
     * @see org.apromore.dao.ProcessBranchDao#update(org.apromore.dao.model.ProcessBranch)
     * {@inheritDoc}
     */
    @Override
    public ProcessBranch update(final ProcessBranch branch) {
        return em.merge(branch);
    }

    /**
     * @see org.apromore.dao.ProcessBranchDao#delete(org.apromore.dao.model.ProcessBranch)
     * {@inheritDoc}
     */
    @Override
    public void delete(final ProcessBranch branch) {
        em.remove(branch);
    }


    /**
     * Sets the Entity Manager. No way around this to get Unit Testing working.
     * @param newEm the entitymanager
     */
    public void setEntityManager(final EntityManager newEm) {
        this.em = newEm;
    }

}
