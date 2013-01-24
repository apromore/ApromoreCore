package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.service.LockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

/**
 * Implementation of the LockService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional
public class LockServiceImpl implements LockService {

    private FragmentVersionRepository fragmentVersionRepo;
    private FragmentVersionDagRepository fragmentVersionDagRepo;
    private ProcessModelVersionRepository processModelVersionRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param fragmentVersionRepository Fragment Version Repository.
     * @param fragmentVersionDagRepository Fragment Version Dag Repository.
     * @param processModelVersionRepository Process Model Version repository.
     */
    @Inject
    public LockServiceImpl(final FragmentVersionRepository fragmentVersionRepository,
            final FragmentVersionDagRepository fragmentVersionDagRepository, final ProcessModelVersionRepository processModelVersionRepository) {
        fragmentVersionRepo = fragmentVersionRepository;
        fragmentVersionDagRepo = fragmentVersionDagRepository;
        processModelVersionRepo = processModelVersionRepository;
    }



    /**
     * @see org.apromore.service.LockService#lockProcessModelVersion(Integer)
     *      {@inheritDoc}
     */
    @Override
    public boolean lockProcessModelVersion(Integer processModelVersionId) {
        ProcessModelVersion result = null;
        ProcessModelVersion prsModelVersion = processModelVersionRepo.findOne(processModelVersionId);

        if (prsModelVersion.getLockStatus().equals(Constants.NO_LOCK)) {
            prsModelVersion.setLockStatus(Constants.DIRECT_LOCK);
            result = processModelVersionRepo.save(prsModelVersion);
        }

        assert result != null;
        return !result.getLockStatus().equals(Constants.NO_LOCK);
    }

    /**
     * @see org.apromore.service.LockService#unlockProcessModelVersion(ProcessModelVersion)
     * {@inheritDoc}
     */
    @Override
    public void unlockProcessModelVersion(ProcessModelVersion processModelVersion) {
        processModelVersion.setLockStatus(Constants.NO_LOCK);
        processModelVersionRepo.save(processModelVersion);
    }


    /**
     * @see org.apromore.service.LockService#lockFragment(Integer)
     * {@inheritDoc}
     */
    @Override
    public boolean lockFragment(Integer fragmentVersionId) {
        FragmentVersion fragVersion = fragmentVersionRepo.findOne(fragmentVersionId);
        boolean locked = lockSingleFragment(fragVersion);
        if (!locked) {
            return false;
        }

        return lockAscendantCurrentFragments(fragVersion.getId()) && lockDescendantFragment(fragVersion.getId());
    }

    /**
     * @see org.apromore.service.LockService#lockFragmentByUri(String)
     * {@inheritDoc}
     */
    @Override
    public boolean lockFragmentByUri(String fragmentUri) {
        FragmentVersion fragVersion = fragmentVersionRepo.findFragmentVersionByUri(fragmentUri);
        boolean locked = lockSingleFragment(fragVersion);
        if (!locked) {
            return false;
        }

        return lockAscendantCurrentFragments(fragVersion.getId()) && lockDescendantFragment(fragVersion.getId());
    }


    /**
     * @see org.apromore.service.LockService#lockSingleFragment(org.apromore.dao.model.FragmentVersion)
     *      {@inheritDoc}
     */
    @Override
    public boolean lockSingleFragment(FragmentVersion fragVersion) {
        FragmentVersion result = null;

        if (fragVersion.getLockStatus().equals(Constants.NO_LOCK)) {
            fragVersion.setLockStatus(Constants.DIRECT_LOCK);
            result = fragmentVersionRepo.save(fragVersion);
        }

        assert result != null;
        return !result.getLockStatus().equals(Constants.NO_LOCK);
    }

    /**
     * @see org.apromore.service.LockService#unlockFragment(FragmentVersion)
     * {@inheritDoc}
     */
    @Override
    public void unlockFragment(final FragmentVersion fragmentVersion) {
        fragmentVersion.setLockStatus(Constants.NO_LOCK);
        fragmentVersionRepo.save(fragmentVersion);
    }

    /**
     * @see org.apromore.service.LockService#unlockFragmentByURI(String)
     * {@inheritDoc}
     */
    @Override
    public void unlockFragmentByURI(final String uri){
        FragmentVersion fragVersion = fragmentVersionRepo.findFragmentVersionByUri(uri);
        fragVersion.setLockStatus(Constants.NO_LOCK);
        fragmentVersionRepo.save(fragVersion);
    }

    /**
     * @see org.apromore.service.LockService#unlockAscendantFragments(FragmentVersion)
     * {@inheritDoc}
     */
    @Override
    public void unlockAscendantFragments(FragmentVersion fragmentVersion) {
        List<FragmentVersion> parents = fragmentVersionRepo.getLockedParentFragments(fragmentVersion);
        for (FragmentVersion parent : parents) {
            decrementParentLocks(parent.getId());
            unlockAscendantFragments(parent);
        }
    }


    /**
     * @see org.apromore.service.LockService#unlockDescendantFragmentsByURI(String)
     *      {@inheritDoc}
     */
    @Override
    public void unlockDescendantFragmentsByURI(final String uri) {
        unlockDescendantFragments(fragmentVersionRepo.findFragmentVersionByUri(uri));
    }

    /**
     * @see org.apromore.service.LockService#unlockDescendantFragments(FragmentVersion)
     *      {@inheritDoc}
     */
    @Override
    public void unlockDescendantFragments(FragmentVersion fragmentVersion) {
        unlockChildFragments(fragmentVersion);
        Set<FragmentVersionDag> childIds = fragmentVersion.getChildFragmentVersionDags(); //fragmentVersionDagRepo.getChildMappings(fragmentVersionDag.getId());
        for (FragmentVersionDag childId : childIds) {
            unlockDescendantFragments(childId.getChildFragmentVersion());
        }
    }

    /**
     * @see org.apromore.service.LockService#isUsedInCurrentProcessModel(FragmentVersion)
     *      {@inheritDoc}
     */
    @Override
    public boolean isUsedInCurrentProcessModel(FragmentVersion fragVersion) {
        int maxVersion;
        int currentVersion;
        boolean usedInCurrentProcessModel = false;

        Map<String, Integer> maxVersions = processModelVersionRepo.getMaxModelVersions(fragVersion.getId());
        Map<String, Integer> currentVersions = processModelVersionRepo.getCurrentModelVersions(fragVersion.getId());

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
    private boolean lockAscendantCurrentFragments(Integer fragmentId) {
        List<FragmentVersion> parents = fragmentVersionRepo.getParentFragments(fragmentId);

        for (FragmentVersion parent : parents) {
            if (isUsedInCurrentProcessModel(parent)) {
                boolean parentLocked = lockSingleFragmentInParentMode(parent);
                if (!parentLocked) {
                    return false;
                }

                boolean ascendantsLocked = lockAscendantCurrentFragments(parent.getId());
                if (!ascendantsLocked) {
                    return false;
                }
            }
        }
        return true;
    }

    private void decrementParentLocks(Integer fragmentId) {
        FragmentVersion fd = fragmentVersionRepo.findOne(fragmentId);
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
        fragmentVersionRepo.save(fd);
    }

    private boolean lockSingleFragmentInParentMode(FragmentVersion fragVersion) {
        fragVersion.setLockStatus(Constants.INDIRECT_LOCK);
        fragVersion.setLockCount(fragVersion.getLockCount() + 1);
        FragmentVersion result = fragmentVersionRepo.save(fragVersion);

        return result.getLockStatus().equals(Constants.INDIRECT_LOCK);
    }

    /* Locks the Descendant fragments, only pass the id as the object isn't needed. */
    private boolean lockDescendantFragment(Integer fragmentId) {
        List<FragmentVersionDag> childIds = fragmentVersionDagRepo.getChildMappings(fragmentId);
        int lockedChildren = lockChildren(fragmentId);
        return lockedChildren == childIds.size();
    }

    private int lockChildren(Integer fragmentId) {
        int updated = 0;
        FragmentVersion fragmentVersion = fragmentVersionRepo.findOne(fragmentId);
        List<FragmentVersion> frags = fragmentVersionRepo.getChildFragmentsByFragmentVersion(fragmentVersion);
        for (FragmentVersion frag : frags) {
            if (frag.getLockStatus().equals(Constants.NO_LOCK) || frag.getLockStatus().equals(Constants.DIRECT_LOCK)) {
                updated++;
                frag.setLockStatus(Constants.DIRECT_LOCK);
                fragmentVersionRepo.save(frag);
            }
        }
        return updated;
    }

    private void unlockChildFragments(FragmentVersion fragmentVersion) {
        List<FragmentVersion> frags = fragmentVersionRepo.getChildFragmentsByFragmentVersion(fragmentVersion);
        for (FragmentVersion frag : frags) {
            frag.setLockStatus(Constants.NO_LOCK);
            fragmentVersionRepo.save(frag);
        }
    }
}
