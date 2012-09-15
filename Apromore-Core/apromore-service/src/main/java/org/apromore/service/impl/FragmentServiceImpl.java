package org.apromore.service.impl;

import org.apromore.common.Constants;
import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.RepositoryException;
import org.apromore.exception.SerializationException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.ICpfNode;
import org.apromore.service.CanoniserService;
import org.apromore.service.ContentService;
import org.apromore.service.FragmentService;
import org.apromore.service.LockService;
import org.apromore.service.helper.RPSTNodeCopy;
import org.apromore.service.model.FDNode;
import org.apromore.service.model.FragmentDAG;
import org.apromore.util.FragmentUtil;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.util.*;

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
    @Autowired @Qualifier("ContentDao")
    private ContentDao contentDao;
    @Autowired @Qualifier("NodeDao")
    private NodeDao nDao;

    @Autowired @Qualifier("ContentService")
    private ContentService csrv;
    @Autowired @Qualifier("CanoniserService")
    private CanoniserService cSrv;
    @Autowired @Qualifier("LockService")
    private LockService lSrv;


    /**
     * @see org.apromore.service.FragmentService#addProcessFragmentMappings(Integer, java.util.List)
     * {@inheritDoc}
     */
    @Override
    public void addProcessFragmentMappings(Integer pmvid, List<String> composingFragmentIds) throws ExceptionDao {
        ProcessFragmentMap pfm;
        ProcessModelVersion pmv = pmvDao.findProcessModelVersion(pmvid);
        for (String composingFragmentId : composingFragmentIds) {
            pfm = new ProcessFragmentMap();
            pfm.setProcessModelVersion(pmv);
            pfm.setFragmentVersion(fvDao.findFragmentVersionByURI(composingFragmentId));
        }
    }


    /**
     * @see FragmentService#getFragmentUri(Integer, org.apromore.graph.JBPT.CPF, java.util.List)
     * {@inheritDoc}
     */
    @Override
    public String getFragmentUri(Integer pmvid, CPF g, List<String> nodes) {
        FragmentDAG fdag = constructFragmentDAG(pmvid);
        List<String> originalNodes = getOriginalNodes(nodes, g);
        List<String> containingFragments = getContainingFragments(originalNodes, fdag);
        List<String> candidateContainingFragments = new ArrayList<String>();
        findCandidateContainingFragments(containingFragments.get(0), containingFragments, fdag, candidateContainingFragments);
        return findSmallestContainingFragment(candidateContainingFragments, fdag);
    }

    /**
     * @see FragmentService#getFragmentAsEPML(Integer)
     * {@inheritDoc}
     */
    @Override
    public String getFragmentAsEPML(Integer fragmentId) throws RepositoryException {
        String xml;
        try {
            CPF g = getFragment(fragmentId, false);
            xml = cSrv.CPFtoString(cSrv.serializeCPF(g));
        } catch (LockFailedException e) {
            throw new RepositoryException(e);
        } catch (SerializationException e) {
            throw new RepositoryException(e);
        } catch (JAXBException e) {
            throw new RepositoryException(e);
        }
        return xml;
    }

    /**
     * @see FragmentService#getFragment(Integer, boolean)
     * {@inheritDoc}
     */
    @Override
    public CPF getFragment(Integer fragmentId, boolean lock) throws LockFailedException {
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
            ComposerServiceImpl composerServiceImpl = new ComposerServiceImpl();
            FragmentVersion fv = fvDao.findFragmentVersion(fragmentId);
            processModelGraph = composerServiceImpl.compose(fv.getUri());
            processModelGraph.setProperty(Constants.ORIGINAL_FRAGMENT_ID, fragmentId.toString());

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
     * @see FragmentService#getFragmentVersion(Integer)
     * {@inheritDoc}
     */
    @Override
    public FragmentVersion getFragmentVersion(Integer fragmentVersionId) {
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
        fragVersion.setUri(UUID.randomUUID().toString());
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
     *      {@inheritDoc}
     */
    @Override
    public void addChildMappings(FragmentVersion fragVer, Map<String, String> childMappings) {
        Set<String> pocketIds = childMappings.keySet();
        for (String pid : pocketIds) {
            String cid = childMappings.get(pid);
            if (fragVer == null || cid == null || pid == null) {
                String msg = "Invalid child mapping parameters. child Id: " + cid + ", Pocket Id: " + pid;
                LOGGER.error(msg);
            }

            FragmentVersionDag fvd = new FragmentVersionDag();
            fvd.setChildFragmentVersionId(fvDao.findFragmentVersionByURI(cid));
            fvd.setFragmentVersionId(fragVer);
            fvd.setPocketId(pid);

            fvdDao.save(fvd);
        }
    }

    @Override
    public FragmentVersion storeFragment(String fragmentCode, RPSTNodeCopy fCopy, CPF g) {
        Content c = new Content();
        c.setBoundaryS(fCopy.getEntry().getId());
        c.setBoundaryE(fCopy.getExit().getId());
        c.setCode(fragmentCode);
        contentDao.save(c);

        FragmentVersion f = new FragmentVersion();
        f.setContent(c);
        f.setLockStatus(-1);
        f.setLockCount(-1);
        f.setFragmentType(fCopy.getReadableNodeType());
        f.setFragmentSize(fCopy.getSize());
        fvDao.save(f);

        for (ICpfNode v : fCopy.getVertices()) {
            String vtype = FragmentUtil.getType(v);

            Node node = new Node();
            node.setContent(c);
            node.setName(v.getLabel() != null ? v.getLabel() : v.getName());
            node.setType(vtype);
            node.setConfiguration(v.isConfigurable());
            node.setCtype(v.getClass().getName());
            node.setOriginalId(v.getId());
            nDao.save(node);
            v.setId(String.valueOf(node.getId()));
        }

        for (AbstractDirectedEdge e : fCopy.getEdges()) {
            Node source = nDao.findNode(Integer.valueOf(e.getSource().getId()));
            Node target = nDao.findNode(Integer.valueOf(e.getTarget().getId()));
            csrv.addEdge(c, e, source, target);
        }

        return f;
    }

    @Override
    public FragmentVersion getMatchingFragmentVersionId(Integer contentId, Map<String, String> childMappings) {
        String childMappingCode = calculateChildMappingCode(childMappings);
        return fvDao.getMatchingFragmentVersionId(contentId, childMappingCode);
    }


    @Override
    public void deleteFragmentVersion(Integer fvid) {
        fvDao.delete(fvDao.findFragmentVersion(fvid));
    }

    @Override
    public void deleteChildRelationships(Integer fvid) {
        fvDao.delete(fvDao.findFragmentVersion(fvid));
    }

    @Override
    public void setDerivation(String fragVersionUri, String derivedFromFragmentId) {
        FragmentVersion fragVersion = fvDao.findFragmentVersionByURI(fragVersionUri);
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

    private FragmentDAG constructFragmentDAG(final Integer pmvid) {
        FragmentDAG fdag = new FragmentDAG();
        String rootFUri = pDao.getRootFragmentVersionURI(pmvid);
        fillFragmentDAG(rootFUri, null, fdag);
        return fdag;
    }

    private void fillFragmentDAG(String fragmentUri, String parentUri, FragmentDAG fdag) {
        if (fdag.contains(fragmentUri)) {
            FDNode fdNode = fdag.getFragment(fragmentUri);
            fdNode.getParentIds().add(parentUri);
        } else {
            FDNode fdNode = new FDNode(fragmentUri);
            fdag.addFragment(fdNode);
            fdNode.getParentIds().add(parentUri);

            List<FragmentVersionDag> childIds = fvdDao.getChildMappingsByURI(fragmentUri);
            fdNode.getChildIds().addAll(getChildIds(childIds));
            for (FragmentVersionDag childId : childIds) {
                fillFragmentDAG(childId.getChildFragmentVersionId().getUri(), fragmentUri, fdag);
            }
        }
    }

    private Collection<String> getChildIds(List<FragmentVersionDag> fdags) {
        List<String> id = new ArrayList<String>();
        for (FragmentVersionDag fdag : fdags) {
            id.add(fdag.getChildFragmentVersionId().getUri());
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
        List<String> containingFragments = fvDao.getContainingFragmentsByURI(nodes);
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
     *
     * @param pdao the dao
     */
    public void setProcessDao(ProcessDao pdao) {
        pDao = pdao;
    }

    /**
     * Set the fragment Version DAO for this class. Mainly for spring tests.
     *
     * @param fvdao the dao
     */
    public void setFragmentVersionDao(FragmentVersionDao fvdao) {
        fvDao = fvdao;
    }

    /**
     * Set the fragment Version DAG DAO for this class. Mainly for spring tests.
     *
     * @param fvddao the dao
     */
    public void setFragmentVersionDagDao(FragmentVersionDagDao fvddao) {
        fvdDao = fvddao;
    }

    /**
     * Set the Process Model Version DAO object for this class. Mainly for spring tests.
     *
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

    /**
     * Set the Canoniser Service for this class. Mainly for spring tests.
     * @param csrv the Canoniser service
     */
    public void setCanoniserService(CanoniserService csrv) {
        cSrv = csrv;
    }
}
