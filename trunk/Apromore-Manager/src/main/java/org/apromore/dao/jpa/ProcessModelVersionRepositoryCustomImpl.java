package org.apromore.dao.jpa;

import org.apromore.dao.ProcessModelVersionRepositoryCustom;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class ProcessModelVersionRepositoryCustomImpl implements ProcessModelVersionRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    /**
     * @see org.apromore.dao.ProcessRepositoryCustom#findAllProcesses(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getMaxModelVersions(Integer fragmentVersionId) {
        Map<String, Integer> maxModels = new HashMap<String, Integer>();
        Query query = em.createQuery("SELECT pmv.processBranch.id, max(pmv.versionNumber) " +
                "FROM ProcessModelVersion pmv, ProcessFragmentMap pfm " +
                "WHERE pmv.id = pfm.processModelVersion.id AND pfm.fragmentVersion.id = :id " +
                "GROUP BY pmv.processBranch.id");
        query.setParameter("id", fragmentVersionId);

        List<Object[]> pmvBranches = (List<Object[]>) query.getResultList();
        for (Object[] obj : pmvBranches) {
            maxModels.put((String) obj[0], (Integer) obj[1]);
        }

        return maxModels;
    }


    /**
     * @see org.apromore.dao.ProcessRepositoryCustom#findAllProcesses(String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getCurrentModelVersions(Integer fragmentVersionId) {
        Map<String, Integer> currentModels = new HashMap<String, Integer>();
        Query query = em.createQuery("SELECT pmv1.processBranch.id, max(pmv1.versionNumber) " +
                "FROM ProcessModelVersion pmv1, ProcessModelVersion pmv2, ProcessFragmentMap pfm " +
                "WHERE pmv2.id = pfm.processModelVersion.id AND pfm.fragmentVersion.id = :id " +
                "  AND pmv2.processBranch.id = pmv1.processBranch.id GROUP BY pmv1.processBranch.id");

        query.setParameter("id", fragmentVersionId);

        List<Object[]> pmvBranches = (List<Object[]>) query.getResultList();
        for (Object[] obj : pmvBranches) {
            currentModels.put((String) obj[0], (Integer) obj[1]);
        }

        return currentModels;
    }
}
