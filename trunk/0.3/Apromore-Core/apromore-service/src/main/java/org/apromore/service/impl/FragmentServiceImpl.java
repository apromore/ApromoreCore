package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.ProcessDao;
import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.FragmentVersionDagId;
import org.apromore.dao.model.ProcessFragmentMap;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.LockFailedException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.service.FragmentService;
import org.apromore.service.LockService;
import org.apromore.service.model.FDNode;
import org.apromore.service.model.FragmentDAG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Implementation of the FragmentService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("FragmentService")
@Transactional(propagation = Propagation.REQUIRED)
public class FragmentServiceImpl implements FragmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentServiceImpl.class);

    @Autowired @Qualifier("ProcessDao")
    private ProcessDao pDao;
    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;
    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fvdDao;
    @Autowired @Qualifier("ProcessModelVersionDao")
    private ProcessModelVersionDao pmvDao;

    @Autowired @Qualifier("LockService")
    private LockService lSrv;



    /**
     * @see org.apromore.service.ProcessService#addProcessFragmentMappings(Integer, java.util.List)
     * {@inheritDoc}
     */
    @Override
    public void addProcessFragmentMappings(Integer pmvid, List<String> composingFragmentIds) throws ExceptionDao {
        ProcessFragmentMap pfm;
        ProcessModelVersion pmv = pmvDao.findProcessModelVersion(pmvid);
        for (String composingFragmentId : composingFragmentIds) {
            pfm = new ProcessFragmentMap();
            pfm.setProcessModelVersion(pmv);
            pfm.setFragmentVersion(fvDao.findFragmentVersion(composingFragmentId));
        }
    }


    /**
     * @see FragmentService#getFragmentId(Integer, org.apromore.graph.JBPT.CPF, java.util.List)
     * {@inheritDoc}
     */
    @Override
    public String getFragmentId(Integer pmvid, CPF g, List<String> nodes) {
        FragmentDAG fdag = constructFragmentDAG(pmvid);
        List<String> originalNodes = getOriginalNodes(nodes, g);
        List<String> containingFragments = getContainingFragments(originalNodes, fdag);
        List<String> candidateContainingFragments = new ArrayList<String>();
        findCandidateContainingFragments(containingFragments.get(0), containingFragments, fdag, candidateContainingFragments);
        return findSmallestContainingFragment(candidateContainingFragments, fdag);
    }

    /**
     * @see FragmentService#getFragment(String, boolean)
     * {@inheritDoc}
     */
    @Override
    public CPF getFragment(String fragmentId, boolean lock) throws LockFailedException {
        CPF processModelGraph = null;
        try {
            if (lock) {
                LOGGER.debug("Obtaining a lock for the fragment " + fragmentId + "...");
                boolean locked = lSrv.lockFragment(fragmentId);
                if (!locked) {
                    throw new LockFailedException();
                }
            }

            LOGGER.debug("Composing the fragment " + fragmentId + "...");
            Composer composer = new Composer();
            processModelGraph = composer.compose(fragmentId);
            processModelGraph.setProperty(Constants.ORIGINAL_FRAGMENT_ID, fragmentId);

            if (lock) {
                processModelGraph.setProperty(Constants.LOCK_STATUS, Constants.LOCKED);
            }
        } catch (ExceptionDao e) {
            String msg = "Failed to retrieve the fragment " + fragmentId;
            LOGGER.error(msg, e);
            return processModelGraph;
        }

        return processModelGraph;
    }

    /**
     * @see FragmentService#getFragmentVersion(String)
     * {@inheritDoc}
     */
    @Override
    public FragmentVersion getFragmentVersion(String fragmentVersionId) {
        return fvDao.findFragmentVersion(fragmentVersionId);
    }

    /**
     * @see FragmentService#addFragmentVersion(org.apromore.dao.model.Content, java.util.Map, String, int, int, int, String)
     * {@inheritDoc}
     */
    @Override
    public FragmentVersion addFragmentVersion(Content cid, Map<String, String> childMappings, String derivedFrom,
            int lockStatus, int lockCount, int originalSize, String fragmentType) {
        String childMappingCode = calculateChildMappingCode(childMappings);

        FragmentVersion fragVersion = new FragmentVersion();
        fragVersion.setContent(cid);
        fragVersion.setChildMappingCode(childMappingCode);
        fragVersion.setDerivedFromFragment(derivedFrom);
        fragVersion.setLockStatus(lockStatus);
        fragVersion.setLockCount(lockCount);
        fragVersion.setFragmentType(fragmentType);
        fragVersion.setFragmentSize(originalSize);
        fvDao.save(fragVersion);

        addChildMappings(fragVersion, childMappings);

        return fragVersion;
    }

    /**
     * @see FragmentService#addChildMappings(org.apromore.dao.model.FragmentVersion, java.util.Map)
     * {@inheritDoc}
     */
    @Override
    public void addChildMappings(FragmentVersion fragVer, Map<String, String> childMappings) {
        Set<String> pocketIds = childMappings.keySet();
        for (String pid : pocketIds) {
            String cid = childMappings.get(pid);
            if (fragVer == null || cid == null || pid == null) {
                String msg = "Invalid child mapping parameters. child Id: " + cid + ", Pocket Id: " + pid;
                LOGGER.error(msg);
                //throw new ExceptionDao(msg);
            }

            FragmentVersionDagId id = new FragmentVersionDagId();
            id.setFragmentVersionId(fragVer.getFragmentVersionId());
            id.setChildFragmentVersionId(cid);
            id.setPocketId(pid);

            FragmentVersionDag fvd = new FragmentVersionDag();
            fvd.setFragmentVersionByFragVerId(fragVer);
            fvd.setFragmentVersionByChildFragVerId(fvDao.findFragmentVersion(cid));
            fvd.setId(id);

            fvdDao.save(fvd);
        }
    }

    @Override
    public FragmentVersion getMatchingFragmentVersionId(String contentId, Map<String, String> childMappings) {
        String childMappingCode = calculateChildMappingCode(childMappings);
        return fvDao.getMatchingFragmentVersionId(contentId, childMappingCode);
    }


    @Override
    public void deleteFragmentVersion(String fvid) {
        fvDao.delete(fvDao.findFragmentVersion(fvid));
    }

    @Override
    public void deleteChildRelationships(String fvid) {
        fvDao.delete(fvDao.findFragmentVersion(fvid));
    }

    @Override
    public void setDerivation(String fvid, String derivedFromFragmentId) {
        FragmentVersion fragVersion = fvDao.findFragmentVersion(fvid);
        fragVersion.setDerivedFromFragment(derivedFromFragmentId);
        fvDao.update(fragVersion);
    }





    private String calculateChildMappingCode(Map<String, String> childMapping) {
        StringBuilder buf = new StringBuilder();
        Set<String> pids = childMapping.keySet();
        PriorityQueue<String> q = new PriorityQueue<String>(pids);
        while (!q.isEmpty()) {
            String pid = q.poll();
            String cid = childMapping.get(pid);
            buf.append(pid).append(":").append(cid).append("|");
        }
        return buf.toString();
    }

    private void fillMatchingChildIds(String parentId, int minSize, int maxSize, List<String> matchingChildren) {
        Map<String, Integer> cs = fvDao.getChildFragmentsWithSize(parentId);
        for (String childId : cs.keySet()) {
            int size = cs.get(childId);
            if (size >= minSize) {
                if (size <= maxSize) {
                    matchingChildren.add(childId);
                }
                fillMatchingChildIds(childId, minSize, maxSize, matchingChildren);
            }
        }
    }

    private FragmentDAG constructFragmentDAG(final Integer pmvid) {
        FragmentDAG fdag = new FragmentDAG();
        String rootfid = pDao.getRootFragmentVersionId(pmvid);
        fillFragmentDAG(rootfid, null, fdag);
        return fdag;
    }

    private void fillFragmentDAG(String fragmentId, String parentId, FragmentDAG fdag) {
        if (fdag.contains(fragmentId)) {
            FDNode fdNode = fdag.getFragment(fragmentId);
            fdNode.getParentIds().add(parentId);
        } else {
            FDNode fdNode = new FDNode(fragmentId);
            fdag.addFragment(fdNode);
            fdNode.getParentIds().add(parentId);

            List<FragmentVersionDag> childIds = fvdDao.getChildMappings(fragmentId);
            fdNode.getChildIds().addAll(getChildIds(childIds));
            for (FragmentVersionDag childId : childIds) {
                fillFragmentDAG(childId.getId().getChildFragmentVersionId(), fragmentId, fdag);
            }
        }
    }

    private Collection<String> getChildIds(List<FragmentVersionDag> fdags) {
        List<String> id = new ArrayList<String>();
        for (FragmentVersionDag fdag : fdags) {
            id.add(fdag.getId().getChildFragmentVersionId());
        }
        return id;
    }

    private List<String> getOriginalNodes(List<String> nodes, CPF g) {
        List<String> originalNodes = new ArrayList<String>();
        for (String node : nodes) {
            if (g.isDuplicateNode(node)) {
                originalNodes.add(g.getOriginalNode(node));
            } else {
                originalNodes.add(node);
            }
        }
        return originalNodes;
    }

    private List<String> getContainingFragments(List<String> nodes, FragmentDAG fdag) {
        List<String> containingFragments = fvDao.getContainingFragments(nodes);
        Set<String> processFragmentIds = fdag.getFragmentIds();
        containingFragments.retainAll(processFragmentIds);
        return containingFragments;
    }

    private void findCandidateContainingFragments(String candidateFragment, List<String> containingFragments, FragmentDAG fdag,
            List<String> candidateContainingFragments) {
        boolean contained = fdag.isIncluded(candidateFragment, containingFragments);
        if (contained) {
            candidateContainingFragments.add(candidateFragment);
        } else {
            List<String> parents = fdag.getFragment(candidateFragment).getParentIds();
            for (String parent : parents) {
                findCandidateContainingFragments(parent, containingFragments, fdag, candidateContainingFragments);
            }
        }
    }

    private String findSmallestContainingFragment(List<String> candidateContainingFragments, FragmentDAG fdag) {
        if (candidateContainingFragments.size() == 1) {
            return candidateContainingFragments.get(0);
        }

        int fragmentNumber = 0;
        String smallestContainingFragment = null;
        while (smallestContainingFragment == null) {
            smallestContainingFragment = candidateContainingFragments.get(fragmentNumber);
            fragmentNumber++;
            for (String f : candidateContainingFragments) {
                if (f.equals(smallestContainingFragment))
                    continue;

                if (!fdag.isIncluded(smallestContainingFragment, f)) {
                    smallestContainingFragment = null;
                    break;
                }
            }
        }
        return smallestContainingFragment;
    }




    /**
     * Set the Process DAO for this class. Mainly for spring tests.
     * @param pdao the dao
     */
    public void setProcessDao(ProcessDao pdao) {
        pDao = pdao;
    }

    /**
     * Set the fragment Version DAO for this class. Mainly for spring tests.
     * @param fvdao the dao
     */
    public void setFragmentVersionDao(FragmentVersionDao fvdao) {
        fvDao = fvdao;
    }

    /**
     * Set the fragment Version DAG DAO for this class. Mainly for spring tests.
     * @param fvddao the dao
     */
    public void setFragmentVersionDagDao(FragmentVersionDagDao fvddao) {
        fvdDao = fvddao;
    }

    /**
     * Set the Process Model Version DAO object for this class. Mainly for spring tests.
     * @param pmvDAOJpa the process model version
     */
    public void setProcessModelVersionDao(ProcessModelVersionDao pmvDAOJpa) {
        pmvDao = pmvDAOJpa;
    }

    /**
    * Set the Lock Service for this class. Mainly for spring tests.
    * @param lsrv the Lock service
    */
    public void setLockService(LockService lsrv) {
        lSrv = lsrv;
    }
}
