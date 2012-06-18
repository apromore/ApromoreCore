package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.service.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Implementation of the LockService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("LockService")
@Transactional(propagation = Propagation.REQUIRED)
public class LockServiceImpl implements LockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockServiceImpl.class);

    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fragVersionDao;
    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fragVersionDagDao;
    @Autowired @Qualifier("ProcessModelVersionDao")
    private ProcessModelVersionDao prsModelVersionDao;



    /**
     * @see org.apromore.service.LockService#lockProcessModelVersion(Integer)
     * {@inheritDoc}
     */
    @Override
    public boolean lockProcessModelVersion(Integer processModelVersionId) {
        ProcessModelVersion result = null;
        ProcessModelVersion prsModelVersion = prsModelVersionDao.findProcessModelVersion(processModelVersionId);

        if (prsModelVersion.getLockStatus().equals(Constants.NO_LOCK)) {
            prsModelVersion.setLockStatus(Constants.DIRECT_LOCK);
            result = prsModelVersionDao.update(prsModelVersion);
        }

        assert result != null;
        return !result.getLockStatus().equals(Constants.NO_LOCK);
    }

    /**
     * @see org.apromore.service.LockService#unlockProcessModelVersion(Integer)
     * {@inheritDoc}
     */
    @Override
    public void unlockProcessModelVersion(Integer processModelVersionId) {
        ProcessModelVersion prsModelVersion = prsModelVersionDao.findProcessModelVersion(processModelVersionId);
        prsModelVersion.setLockStatus(Constants.NO_LOCK);
        prsModelVersionDao.update(prsModelVersion);
    }


    /**
     * @see org.apromore.service.LockService#lockFragment(String)
     * {@inheritDoc}
     */
    @Override
    public boolean lockFragment(String fragmentVersionId) {
        FragmentVersion fragVersion = fragVersionDao.findFragmentVersion(fragmentVersionId);
        boolean locked = lockSingleFragment(fragVersion);
        if (!locked) {
            return false;
        }

        return lockAscendantCurrentFragments(fragVersion.getFragmentVersionId()) &&
                lockDescendantFragment(fragVersion.getFragmentVersionId());
    }


    /**
     * @see org.apromore.service.LockService#lockSingleFragment(org.apromore.dao.model.FragmentVersion)
     * {@inheritDoc}
     */
    @Override
    public boolean lockSingleFragment(FragmentVersion fragVersion) {
        FragmentVersion result = null;

        if (fragVersion.getLockStatus().equals(Constants.NO_LOCK)) {
            fragVersion.setLockStatus(Constants.DIRECT_LOCK);
            result = fragVersionDao.update(fragVersion);
        }

        assert result != null;
        return !result.getLockStatus().equals(Constants.NO_LOCK);
    }

    /**
     * @see org.apromore.service.LockService#unlockFragment(String)
     * {@inheritDoc}
     */
    @Override
    public void unlockFragment(String fragmentId) {
        FragmentVersion fragVersion = fragVersionDao.findFragmentVersion(fragmentId);
        fragVersion.setLockStatus(Constants.NO_LOCK);
        fragVersionDao.update(fragVersion);
    }

    /**
     * @see org.apromore.service.LockService#unlockFragment(String)
     * {@inheritDoc}
     */
    @Override
    public void unlockAscendantFragments(String fragmentId) {
        List<String> parentIds = fragVersionDao.getLockedParentFragmentIds(fragmentId);
        for (String parentId : parentIds) {
            decrementParentLocks(parentId);
            unlockAscendantFragments(parentId);
        }
    }

    /**
     * @see org.apromore.service.LockService#unlockDescendantFragments(String)
     * {@inheritDoc}
     */
    @Override
    public void unlockDescendantFragments(String fragmentId) {
        unlockDescendantFragments(fragVersionDagDao.findFragmentVersionDag(fragmentId));
    }

    /**
     * @see org.apromore.service.LockService#unlockDescendantFragments(FragmentVersionDag)
     * {@inheritDoc}
     */
    @Override
    public void unlockDescendantFragments(FragmentVersionDag fragmentVersionDag) {
        unlockChildFragments(fragmentVersionDag.getFragmentVersionByFragVerId());
        List<FragmentVersionDag> childIds = fragVersionDagDao.getChildMappings(fragmentVersionDag.getFragmentVersionByFragVerId().getFragmentVersionId());
        for (FragmentVersionDag childId : childIds) {
            unlockDescendantFragments(childId);
        }
    }

    /**
     * @see org.apromore.service.LockService#isUsedInCurrentProcessModel(FragmentVersion)
     * {@inheritDoc}
     */
    @Override
    public boolean isUsedInCurrentProcessModel(FragmentVersion fragVersion) {
        int maxVersion;
        int currentVersion;
        boolean usedInCurrentProcessModel = false;

        Map<String, Integer> maxVersions = prsModelVersionDao.getMaxModelVersions(fragVersion.getFragmentVersionId());
        Map<String, Integer> currentVersions = prsModelVersionDao.getCurrentModelVersions(fragVersion.getFragmentVersionId());

        for (String branchId : maxVersions.keySet()) {
            maxVersion = maxVersions.get(branchId);
            currentVersion = currentVersions.get(branchId);
            if (maxVersion == currentVersion) {
                usedInCurrentProcessModel = true;
                break;
            }
        }

        return usedInCurrentProcessModel;
    }




    /* Locks the Ascendant fragments, only pass the id as the object isn't needed. */
    private boolean lockAscendantCurrentFragments(String fragmentId) {
        List<FragmentVersion> parents = fragVersionDao.getParentFragments(fragmentId);

        for (FragmentVersion parent : parents) {
            if (isUsedInCurrentProcessModel(parent)) {
                boolean parentLocked = lockSingleFragmentInParentMode(parent);
                if (!parentLocked) {
                    return false;
                }

                boolean ascendantsLocked = lockAscendantCurrentFragments(parent.getFragmentVersionId());
                if (!ascendantsLocked) {
                    return false;
                }
            }
        }
        return true;
    }

    private void decrementParentLocks(String fragmentId) {
        FragmentVersion fd = fragVersionDao.getFragmentData(fragmentId);
        Integer lockStatus = fd.getLockStatus();
        Integer lockCount = fd.getLockCount();
        if (lockStatus.equals(Constants.INDIRECT_LOCK)) {
            lockCount--;
            if (lockCount == 0) {
                lockStatus = Constants.NO_LOCK;
            }
        }

        fd.setLockStatus(lockStatus);
        fd.setLockCount(lockCount);
        fragVersionDao.update(fd);
    }

    private boolean lockSingleFragmentInParentMode(FragmentVersion fragVersion) {
        fragVersion.setLockStatus(Constants.INDIRECT_LOCK);
        fragVersion.setLockCount(fragVersion.getLockCount() + 1);
        FragmentVersion result = fragVersionDao.update(fragVersion);

        return result.getLockStatus().equals(Constants.INDIRECT_LOCK);
    }

    /* Locks the Descendant fragments, only pass the id as the object isn't needed. */
    private boolean lockDescendantFragment(String fragmentId) {
        List<FragmentVersionDag> childIds = fragVersionDagDao.getChildMappings(fragmentId);
        int lockedChildren = lockChildren(fragmentId);
        return lockedChildren == childIds.size();
    }

    private int lockChildren(String fragmentId) {
        int updated = 0;
        FragmentVersion fd = fragVersionDao.getFragmentData(fragmentId);
        List<FragmentVersion> frags =  fragVersionDagDao.getChildFragmentsByFragmentVersion(fd.getFragmentVersionId());
        for (FragmentVersion frag : frags) {
            if (frag.getLockStatus().equals(Constants.NO_LOCK) || frag.getLockStatus().equals(Constants.DIRECT_LOCK)) {
                updated++;
                frag.setLockStatus(Constants.DIRECT_LOCK);
                fragVersionDao.update(frag);
            }
        }
        return updated;
    }

    private void unlockChildFragments(FragmentVersion fragmentVersion) {
        List<FragmentVersion> frags =  fragVersionDagDao.getChildFragmentsByFragmentVersion(fragmentVersion.getFragmentVersionId());
        for (FragmentVersion frag : frags) {
            fragmentVersion.setLockStatus(Constants.NO_LOCK);
            fragVersionDao.update(fragmentVersion);
        }
    }






    /**
     * Set the FragmentVersion DAO object for this class. Mainly for spring tests.
     * @param fragVersionDAOJpa the FragmentVersion Dao.
     */
    public void setFragVersionDao(FragmentVersionDao fragVersionDAOJpa) {
        fragVersionDao = fragVersionDAOJpa;
    }

    /**
     * Set the FragmentVersionDag DAO object for this class. Mainly for spring tests.
     * @param fragVersionDagDAOJpa the FragmentVersionDag Dao.
     */
    public void setFragVersionDagDao(FragmentVersionDagDao fragVersionDagDAOJpa) {
        fragVersionDagDao = fragVersionDagDAOJpa;
    }

    /**
     * Set the Process Model Version DAO object for this class. Mainly for spring tests.
     * @param prsModelVersionDAOJpa the process model version Dao.
     */
    public void setPrsModelVersionDao(ProcessModelVersionDao prsModelVersionDAOJpa) {
        prsModelVersionDao = prsModelVersionDAOJpa;
    }

}
