package org.apromore.service.helper;

import java.util.*;

import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.jpa.FragmentVersionDagDaoJpa;
import org.apromore.dao.jpa.FragmentVersionDaoJpa;
import org.apromore.dao.jpa.ProcessModelVersionDaoJpa;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.service.FragmentService;
import org.apromore.service.LockService;
import org.apromore.service.ProcessService;
import org.apromore.service.impl.FragmentServiceImpl;
import org.apromore.service.impl.LockServiceImpl;
import org.apromore.service.impl.ProcessServiceImpl;
import org.apromore.util.VersionNameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Chathura Ekanayake
 */
@Service("ChangePropagator")
@Transactional(propagation = Propagation.REQUIRED)
public class ChangePropagator {

    private static Logger LOGGER = LoggerFactory.getLogger(ChangePropagator.class);

    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;
    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fvdDao;
    @Autowired @Qualifier("ProcessModelVersionDao")
    private ProcessModelVersionDao pmvDao;

    @Autowired @Qualifier("LockService")
    private LockService lSrv;
    @Autowired @Qualifier("FragmentService")
    private FragmentService fSrv;
    @Autowired @Qualifier("ProcessService")
    private ProcessService pSrv;


    /**
     * Creates new versions for all ascendant fragments of originalFragment by
     * replacing originalFragment with updatedFragment. New versions will be
     * created for all process models which use any of the updated fragments as
     * its root fragment. This method also releases locks of all ascendant
     * fragments.
     * @param originalFragmentId the original Fragment Id
     * @param updatedFragmentId  the updated fragment Id
     */
    public void propagateChangesWithLockRelease(String originalFragmentId, String updatedFragmentId, List<String> composingFragmentIds)
            throws ExceptionDao {
        List<ProcessModelVersion> usedProcessModels = pmvDao.getUsedProcessModelVersionsByURI(originalFragmentId);
        for (ProcessModelVersion pmv : usedProcessModels) {
            createNewProcessModelVersion(pmv, updatedFragmentId, composingFragmentIds);
        }

        LOGGER.debug("Unlocking the original fragment: " + originalFragmentId);
        lSrv.unlockFragmentByURI(originalFragmentId);
        lSrv.unlockDescendantFragmentsByURI(originalFragmentId);

        LOGGER.debug("Propagating to parent fragments of fragment: " + originalFragmentId);
        List<String> lockedParentIds = fvDao.getLockedParentFragmentIdsByURI(originalFragmentId);
        for (String parentId : lockedParentIds) {
            propagateToParentsWithLockRelease(parentId, originalFragmentId, updatedFragmentId, composingFragmentIds);
        }
    }


    private void createNewProcessModelVersion(ProcessModelVersion pmv, String rootFragmentUri, List<String> composingFragmentIds)
            throws ExceptionDao {
        Double versionNumber = pmv.getVersionNumber() + 1;
        String versionName = VersionNameUtil.getNextVersionName(pmv.getVersionName());
        ProcessModelVersion pv = pSrv.addProcessModelVersion(pmv.getProcessBranch(), rootFragmentUri, versionNumber, versionName, 0, 0);
        fSrv.addProcessFragmentMappings(pv.getId(), composingFragmentIds);
    }

    private void propagateToParentsWithLockRelease(String parentUri, String originalFragmentId, String updatedFragmentId,
            List<String> composingFragmentIds) throws ExceptionDao {
        LOGGER.debug("Propagating - fragment: " + originalFragmentId + ", parent: " + parentUri);
        String newParentUri = createNewFragmentVersionByReplacingChild(parentUri, originalFragmentId, updatedFragmentId);
        composingFragmentIds.add(newParentUri);
        fillUnchangedDescendantIds(newParentUri, updatedFragmentId, composingFragmentIds);

        List<ProcessModelVersion> usedProcessModels = pmvDao.getUsedProcessModelVersionsByURI(parentUri);
        for (ProcessModelVersion pmv : usedProcessModels) {
            createNewProcessModelVersion(pmv, newParentUri, composingFragmentIds);
            lSrv.unlockProcessModelVersion(pmv.getId());
        }
        lSrv.unlockFragmentByURI(parentUri);

        List<String> nextLockedParentIds = fvDao.getLockedParentFragmentIdsByURI(parentUri);
        for (String nextParentId : nextLockedParentIds) {
            propagateToParentsWithLockRelease(nextParentId, parentUri, newParentUri, composingFragmentIds);
        }
        LOGGER.debug("Completed propagation - fragment: " + originalFragmentId + ", parent: " + parentUri);
    }

    private void fillUnchangedDescendantIds(String parentUri, String updatedChildId, List<String> composingFragmentIds) throws ExceptionDao {
        List<FragmentVersionDag> allChild = fvdDao.getChildMappingsByURI(parentUri);
        for (FragmentVersionDag child : allChild) {
            if (!child.getChildFragmentVersionId().getUri().equals(updatedChildId)) {
                composingFragmentIds.add(child.getChildFragmentVersionId().getUri());
                fillDescendantIds(child.getChildFragmentVersionId().getId(), composingFragmentIds);
            }
        }
    }

    private void fillDescendantIds(Integer fragmentId, List<String> composingFragmentIds) throws ExceptionDao {
        List<FragmentVersionDag> allChild = fvdDao.getChildMappings(fragmentId);
        for (FragmentVersionDag child : allChild) {
            composingFragmentIds.add(child.getChildFragmentVersionId().getUri());
            fillDescendantIds(child.getChildFragmentVersionId().getId(), composingFragmentIds);
        }
    }

    private String createNewFragmentVersionByReplacingChild(String fragmentUri, String oldChildId, String newChildId) throws ExceptionDao {
        FragmentVersion fv = fvDao.findFragmentVersionByURI(fragmentUri);
        int lockType = 0;
        int lockCount = 0;
        if (fv.getLockStatus() == 1) {
            if (fv.getLockCount() > 1) {
                lockType = 1;
                lockCount = fv.getLockCount() - 1;
            }
        }

        Map<String, String> childMappings = createChildMap(fvdDao.getChildMappingsByURI(fragmentUri));
        Set<String> pockets = childMappings.keySet();
        for (String pocketId : pockets) {
            String childId = childMappings.get(pocketId);
            if (childId.equals(oldChildId)) {
                childMappings.put(pocketId, newChildId);
            }
        }

        // TODO size of the new fragment has to calculated correctly by considering the sizes of the old child and new child
        return fSrv.addFragmentVersion(fv.getContent(), childMappings, fragmentUri, lockType, lockCount, fv.getFragmentSize(),
                fv.getFragmentType()).getUri();
    }


    private Map<String, String> createChildMap(List<FragmentVersionDag> fvds) {
        Map<String, String> childMappings = new HashMap<String, String>();
        for (FragmentVersionDag fvd : fvds) {
            childMappings.put(fvd.getPocketId(), fvd.getChildFragmentVersionId().getUri());
        }
        return childMappings;
    }


    /**
     * Set the Fragment Version DAO object for this class. Mainly for spring tests.
     *
     * @param frgDAOJpa the Fragment Version Dao.
     */
    public void setFragmentVersionDao(FragmentVersionDaoJpa frgDAOJpa) {
        fvDao = frgDAOJpa;
    }

    /**
     * Set the Fragment Version Dag DAO object for this class. Mainly for spring tests.
     *
     * @param fvdDAOJpa the Fragment Version Dag Dao.
     */
    public void setFragmentVersionDagDao(FragmentVersionDagDaoJpa fvdDAOJpa) {
        fvdDao = fvdDAOJpa;
    }

    /**
     * Set the Process Model Version DAO object for this class. Mainly for spring tests.
     *
     * @param pmvDAOJpa the Process Model Version Dao.
     */
    public void setProcessModelVersionDao(ProcessModelVersionDaoJpa pmvDAOJpa) {
        pmvDao = pmvDAOJpa;
    }

    /**
     * Set the Lock Service object for this class. Mainly for spring tests.
     *
     * @param lSrvImpl the Lock Service
     */
    public void setLockService(LockServiceImpl lSrvImpl) {
        lSrv = lSrvImpl;
    }

    /**
     * Set the Fragment Service object for this class. Mainly for spring tests.
     *
     * @param fSrvImpl the Fragment Service
     */
    public void setFragmentService(FragmentServiceImpl fSrvImpl) {
        fSrv = fSrvImpl;
    }

    /**
     * Set the Process Service object for this class. Mainly for spring tests.
     *
     * @param pSrvImpl the Process Service
     */
    public void setProcessService(ProcessServiceImpl pSrvImpl) {
        pSrv = pSrvImpl;
    }

}
