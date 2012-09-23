package org.apromore.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apromore.common.Constants;
import org.apromore.dao.ContentDao;
import org.apromore.dao.FragmentVersionDagDao;
import org.apromore.dao.FragmentVersionDao;
import org.apromore.dao.ProcessBranchDao;
import org.apromore.dao.ProcessDao;
import org.apromore.dao.ProcessFragmentMapDao;
import org.apromore.dao.ProcessModelVersionDao;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.ObjectType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessFragmentMap;
import org.apromore.dao.model.ProcessModelAttribute;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.ResourceType;
import org.apromore.dao.model.User;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ImportException;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.NonEditableVersionException;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.graph.JBPT.ICpfAttribute;
import org.apromore.graph.JBPT.ICpfObject;
import org.apromore.graph.JBPT.ICpfResource;
import org.apromore.service.ComposerService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.LockService;
import org.apromore.service.RepositoryService;
import org.apromore.service.UserService;
import org.apromore.service.helper.ChangePropagator;
import org.apromore.service.model.NameValuePair;
import org.apromore.util.VersionNameUtil;
import org.apromore.util.XMLUtils;
import org.jbpt.pm.FlowNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;

/**
 * Implementation of the RepositoryService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("RepositoryService")
@Transactional(propagation = Propagation.REQUIRED)
public class RepositoryServiceImpl implements RepositoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryServiceImpl.class);

    @Autowired @Qualifier("ProcessBranchDao")
    private ProcessBranchDao bDao;
    @Autowired @Qualifier("ContentDao")
    private ContentDao cDao;
    @Autowired @Qualifier("ProcessDao")
    private ProcessDao pDao;
    @Autowired @Qualifier("ProcessBranchDao")
    private ProcessBranchDao pbDao;
    @Autowired @Qualifier("ProcessModelVersionDao")
    private ProcessModelVersionDao pmvDao;
    @Autowired @Qualifier("ProcessFragmentMapDao")
    private ProcessFragmentMapDao pfmDao;
    @Autowired @Qualifier("FragmentVersionDao")
    private FragmentVersionDao fvDao;
    @Autowired @Qualifier("FragmentVersionDagDao")
    private FragmentVersionDagDao fvdDao;

    @Autowired @Qualifier("UserService")
    private UserService uSrv;
    @Autowired @Qualifier("FormatService")
    private FormatService fSrv;
    @Autowired @Qualifier("FragmentService")
    private FragmentService frgSrv;
    @Autowired @Qualifier("LockService")
    private LockService lSrv;

    @Autowired @Qualifier("ComposerService")
    private ComposerService composer;
    @Autowired @Qualifier("DecomposerService")
    private DecomposerService decomposer;
    @Autowired @Qualifier("ChangePropagator")
    private ChangePropagator cPropagator;


    /**
     * @see RepositoryService#addProcessModel(String, String, String, String, String, String, String, String, String, org.apromore.graph.JBPT.CPF)
     *      {@inheritDoc}
     */
    @Override
    public ProcessModelVersion addProcessModel(final String processName, final String versionName, final String username, final String cpfURI,
            final String nativeType, final String domain, final String documentation, final String created, final String lastUpdated, final CPF proModGrap)
            throws ImportException {
        if (proModGrap == null || proModGrap.getVertices().isEmpty() || proModGrap.getEdges().isEmpty()) {
            LOGGER.error("Process " + processName + " Failed to import correctly.");
            throw new ImportException("Process " + processName + " Failed to import correctly.");
        } else if (pDao.getProcess(processName) != null) {
            LOGGER.error("Process " + processName + " was found to already exist in the Repository.");
            throw new ImportException("Process " + processName + " was found to already exist in the Repository.");
        }

        ProcessModelVersion result = null;
        try {
            int numVertices = proModGrap.countVertices();
            int numEdges = proModGrap.countEdges();

            Process process = insertProcess(processName, username, nativeType, domain);
            ProcessBranch branch = insertProcessBranch(process, created, lastUpdated, versionName);

            List<String> composingFragmentIds = new ArrayList<String>(0);
            FragmentVersion rootFV = decomposer.decompose(proModGrap, composingFragmentIds);
            ProcessModelVersion pdo = insertProcessModelVersion(proModGrap, branch, rootFV.getId(), numVertices, numEdges);
            insertProcessFragmentMappings(pdo, composingFragmentIds);
            result = pdo;
        } catch (Exception re) {
            LOGGER.error("Failed to add the process model " + processName, re);
        }
        return result;
    }

    /**
     * @see RepositoryService#updateProcessModel(org.apromore.graph.JBPT.CPF)
     *      {@inheritDoc}
     */
    @Override
    public void updateProcessModel(final CPF g) {
        String pmvid = g.getProperty(Constants.PROCESS_MODEL_VERSION_ID).getValue();
        if (pmvid != null && pmvid.length() != 0) {
            String branchId = g.getProperty(Constants.BRANCH_ID).getValue();
            updateProcessModel(pmvid, branchId, g);
            return;
        }

        String processName = g.getProperty(Constants.PROCESS_NAME).getValue();
        String branchName = g.getProperty(Constants.BRANCH_NAME).getValue();
        String versionNumber = g.getProperty(Constants.VERSION_NUMBER).getValue();
        String lockStatus = g.getProperty(Constants.LOCK_STATUS).getValue();

        if (lockStatus == null || Constants.UNLOCKED.equals(lockStatus)) {
            String msg = "Process model " + processName + " is not locked for the updating session.";
            LOGGER.error(msg);
        }
        if (processName == null || branchName == null || versionNumber == null) {
            String msg = "Process model does not contain sufficient information to be updated. "
                    + "Process model should contain the process name, branch name and the version number.";
            LOGGER.error(msg);
        }

        try {
            ProcessModelVersion pmVersion = pmvDao.getCurrentProcessModelVersion(processName, branchName);
            if (Integer.parseInt(versionNumber) != pmVersion.getVersionNumber()) {
                String msg = "CONFLICT! The process model " + processName + " - " + branchName
                        + " has been updated by another user. This process model version number: "
                        + versionNumber + ", Current process model version number: " + pmVersion.getVersionNumber();
                LOGGER.error(msg);
            }

            List<String> composingFragmentIds = new ArrayList<String>();
            FragmentVersion rootFV = decomposer.decompose(g, composingFragmentIds);
            cPropagator.propagateChangesWithLockRelease(pmVersion.getRootFragmentVersion().getUri(), rootFV.getUri(), composingFragmentIds);
        } catch (Exception re) {
            LOGGER.error("Failed to add the process model " + processName, re);
        }
    }

    /**
     * @see RepositoryService#updateProcessModel(String, String, org.apromore.graph.JBPT.CPF)
     *      {@inheritDoc}
     */
    @Override
    public void updateProcessModel(final String versionId, final String branchId, final CPF g) {//
        String versionNumber = g.getProperty(Constants.VERSION_NUMBER).getValue();
        String lockStatus = g.getProperty(Constants.LOCK_STATUS).getValue();
        if (lockStatus == null || Constants.UNLOCKED.equals(lockStatus)) {
            String msg = "Process model " + versionId + " is not locked for the updating session.";
            LOGGER.error(msg);
        }
        if (versionId == null || branchId == null || versionNumber == null) {
            String msg = "Process model does not contain sufficient information to be updated.";
            LOGGER.error(msg);
        }

        try {
            ProcessModelVersion pmVersion = pmvDao.getCurrentProcessModelVersion(Integer.parseInt(branchId));
            if (Integer.parseInt(versionNumber) != pmVersion.getVersionNumber()) {
                String msg = "CONFLICT! The process model " + branchId + " - " + versionId + " has been updated by another user." +
                        "\nThis process model version number: " + versionNumber +
                        "\nCurrent process model version number: " + pmVersion.getVersionNumber();
                LOGGER.error(msg);
            }

            List<String> composingFragmentIds = new ArrayList<String>();
            FragmentVersion rootFV = decomposer.decompose(g, composingFragmentIds);
            cPropagator.propagateChangesWithLockRelease(pmVersion.getRootFragmentVersion().getUri(), rootFV.getUri(), composingFragmentIds);
        } catch (Exception e) {
            String msg = "Failed to add the process model " + versionId;
            LOGGER.error(msg, e);
        }
    }


    /**
     * @see RepositoryService#getCanonicalFormat(org.apromore.dao.model.ProcessModelVersion)
     *      {@inheritDoc}
     */
    @Override
    public CPF getCanonicalFormat(final ProcessModelVersion pmv) {
        String processName = pmv.getProcessBranch().getProcess().getName();
        String branchName = pmv.getProcessBranch().getBranchName();
        return getCanonicalFormat(pmv, processName, branchName, false);
    }


    /**
     * @see RepositoryService#getCanonicalFormat(org.apromore.dao.model.ProcessModelVersion, String, String, boolean)
     *      {@inheritDoc}
     */
    @Override
    public CPF getCanonicalFormat(final ProcessModelVersion pmv, final String processName, final String branchName, final boolean lock) {
        CPF processModelGraph = null;
        try {
            processModelGraph = composer.compose(pmv.getRootFragmentVersion().getUri());
            processModelGraph.setProperty(Constants.PROCESS_NAME, processName);
            processModelGraph.setProperty(Constants.BRANCH_NAME, branchName);
            processModelGraph.setProperty(Constants.BRANCH_ID, pmv.getProcessBranch().getId().toString());
            processModelGraph.setProperty(Constants.VERSION_NUMBER, Integer.toString(pmv.getVersionNumber()));
            processModelGraph.setProperty(Constants.PROCESS_MODEL_VERSION_ID, pmv.getId().toString());
            processModelGraph.setProperty(Constants.ROOT_FRAGMENT_ID, pmv.getRootFragmentVersion().getId().toString());
            if (lock) {
                processModelGraph.setProperty(Constants.LOCK_STATUS, Constants.LOCKED);
            }
        } catch (ExceptionDao e) {
            String msg = "Failed to retrieve the current version of the process model " + processName + " - " + branchName;
            LOGGER.error(msg, e);
        }
        return processModelGraph;
    }


    /**
     * @see RepositoryService#getCurrentProcessModel(String, boolean)
     *      {@inheritDoc}
     */
    @Override
    public CPF getCurrentProcessModel(final String processName, final boolean lock) throws LockFailedException {
        return getCurrentProcessModel(processName, Constants.TRUNK_NAME, lock);
    }

    /**
     * @see RepositoryService#getCurrentProcessModel(String, String, boolean)
     *      {@inheritDoc}
     */
    @Override
    public CPF getCurrentProcessModel(final String processName, final String branchName, final boolean lock) throws LockFailedException {
        ProcessModelVersion pmv = pmvDao.getCurrentProcessModelVersion(processName, branchName);

        if (pmv == null) {
            return null;
        }
        if (lock) {
            boolean locked = lSrv.lockFragment(pmv.getRootFragmentVersion().getId());
            if (!locked) {
                throw new LockFailedException();
            }
        }

        return getCanonicalFormat(pmv, processName, branchName, lock);
    }

    /**
     * @see RepositoryService#getProcessModel(String, String, String)
     *      {@inheritDoc}
     */
    @Override
    public CPF getProcessModel(final String processName, final String branchName, final String versionName) {
        ProcessModelVersion pmv = pmvDao.getCurrentProcessModelVersion(processName, branchName, versionName);
        if (pmv == null) {
            return null;
        }

        return getCanonicalFormat(pmv, processName, branchName, false);
    }

    /**
     * @see RepositoryService#getFragment(org.apromore.graph.JBPT.CPF, java.util.List, boolean)
     *      {@inheritDoc}
     */
    @Override
    public CPF getFragment(final CPF g, final List<String> nodes, final boolean lock) throws LockFailedException, NonEditableVersionException {
        String processName = g.getProperty(Constants.PROCESS_NAME).getValue();
        String branchName = g.getProperty(Constants.BRANCH_NAME).getValue();
        Integer pmvid = Integer.parseInt(g.getProperty(Constants.PROCESS_MODEL_VERSION_ID).getValue());
        ProcessModelVersion pmv = pmvDao.getCurrentProcessModelVersion(processName, branchName);

        if (lock && !pmvid.equals(pmv.getId())) {
            String msg = "Process model " + processName + " - " + branchName
                    + " has been updated by another session. Aborting the update.";
            LOGGER.info(msg);
            throw new NonEditableVersionException(msg);
        }

        String fragmentUri = frgSrv.getFragmentUri(pmvid, g, nodes);
        return getFragment(fragmentUri, lock);
    }

    /**
     * @see RepositoryService#getFragment(String, boolean)
     *      {@inheritDoc}
     */
    @Override
    public CPF getFragment(final String fragmentUri, final boolean lock) throws LockFailedException {
        if (lock) {
            boolean locked = lSrv.lockFragmentByUri(fragmentUri);
            if (!locked) {
                throw new LockFailedException();
            }
        }

        CPF processModelGraph = null;
        try {
            processModelGraph = composer.compose(fragmentUri);
            processModelGraph.setProperty(Constants.ORIGINAL_FRAGMENT_ID, fragmentUri);
            if (lock) {
                processModelGraph.setProperty(Constants.LOCK_STATUS, Constants.LOCKED);
            }
        } catch (ExceptionDao e) {
            String msg = "Failed to retrieve the fragment " + fragmentUri;
            LOGGER.error(msg, e);
        }
        return processModelGraph;
    }

    /**
     * Updates the fragment if it doesn't conflict with concurrent modifications
     * to the same fragment. Change will be propagated to all process models
     * with instant change propagation policy.
     *
     * @param fg ProcessModelGraph containing the updated fragment.
     */
    @Override
    public String updateFragment(final CPF fg) {
        String updatedFragmentId = null;
        String lockStatus = fg.getProperty(Constants.LOCK_STATUS).getValue();

        if (lockStatus.equals(Constants.UNLOCKED)) {
            String msg = "Fragment is not locked for the current session.";
            LOGGER.error(msg);
            return updatedFragmentId;
        }

        try {
            List<String> composingFragmentIds = new ArrayList<String>();
            String originalFragmentId = fg.getProperty(Constants.ORIGINAL_FRAGMENT_ID).getValue();

            LOGGER.debug("Decomposing the fragment graph of fragment " + originalFragmentId + "...");
            updatedFragmentId = decomposer.decomposeFragment(fg, composingFragmentIds);
            frgSrv.setDerivation(updatedFragmentId, originalFragmentId);

            LOGGER.debug("Propagating changes of the fragment " + originalFragmentId + "...");
            cPropagator.propagateChangesWithLockRelease(originalFragmentId, updatedFragmentId, composingFragmentIds);
        } catch (Exception e) {
            String msg = "Failed to update fragment.";
            LOGGER.error(msg, e);
        }
        return updatedFragmentId;
    }


    /**
     * @see RepositoryService#deleteProcess(org.apromore.dao.model.Process)
     * {@inheritDoc}
     */
    @Override
    public void deleteProcess(final Process process) {
        pDao.delete(process);
    }


    /**
     * @see RepositoryService#deleteProcessModel(java.util.List)
     * {@inheritDoc}
     */
    @Override
    public void deleteProcessModel(final List<NameValuePair> models) {
        ProcessModelVersion pvid;
        for (NameValuePair entry : models) {
            try {
                LOGGER.debug("Retrieved the pvid of the current version of " + entry.getName() + " - " + entry.getValue() + " to be deleted.");
                pvid = pmvDao.getCurrentProcessModelVersion(entry.getName(), entry.getValue());
                deleteProcessModel(pvid);

                updateProcessTree(entry.getName(), entry.getValue());
            } catch (Exception e) {
                String msg = "Failed to delete the current version of the branch " + entry.getValue() + " of the process model " + entry.getValue();
                LOGGER.error(msg, e);
            }
        }
    }

    /* Update the branch links or remove if no processModelVersion is attached. */
    private void updateProcessTree(final String processName, final String branchName) {
        Process process = pDao.getProcess(processName);
        ProcessBranch pBranch = pbDao.getProcessBranchByProcessBranchName(process.getId(), branchName);

        if (process.getProcessBranches() != null && process.getProcessBranches().size() == 1 && pBranch.getProcessModelVersions().size() == 0) {
            deleteProcess(process);

        } else {
            ProcessModelVersion pmv = pmvDao.getCurrentProcessModelVersion(pBranch.getId());
            pBranch.setCurrentProcessModelVersionId(pmv);
            pbDao.update(pBranch);
        }
    }


    /* Inserts a new process into the DB. */
    private Process insertProcess(final String processName, final String username, final String natType, final String domain) throws ImportException {
        LOGGER.info("Executing operation Insert Process");
        Process process = new Process();

        try {
            User user = uSrv.findUserByLogin(username);
            NativeType nativeType = fSrv.findNativeType(natType);

            process.setName(processName);
            process.setUser(user);
            process.setDomain(domain);
            process.setNativeType(nativeType);
            pDao.save(process);
        } catch (Exception ex) {
            LOGGER.error("Importing a Process Failed: " + ex.toString());
            throw new ImportException(ex);
        }
        return process;
    }

    /* inserts a new branch into the DB. */
    private ProcessBranch insertProcessBranch(final Process process, final String created, final String lastUpdated, final String branchName)
            throws ImportException {
        LOGGER.info("Executing operation Insert Branch");
        ProcessBranch branch = new ProcessBranch();

        try {
            branch.setProcess(process);
            branch.setBranchName(branchName);
            branch.setCreationDate(created);
            branch.setLastUpdate(lastUpdated);
            bDao.save(branch);
        } catch (Exception ex) {
            LOGGER.error("Importing a Branch Failed: " + ex.toString());
            throw new ImportException(ex);
        }
        return branch;
    }

    /* inserts a new process model into the repository */
    private ProcessModelVersion insertProcessModelVersion(final CPF proModGrap, final ProcessBranch branch, final Integer rootFragmentVersionId, final int numVertices,
            final int numEdges) {
        ProcessModelVersion process = new ProcessModelVersion();
        ProcessModelVersion pmv = pmvDao.getMaxVersionProcessModel(branch);

        int versionNumber = 0;
        String lastVersionName = branch.getBranchName();
        if (pmv != null) {
            versionNumber = pmv.getVersionNumber() + 1;
            lastVersionName = pmv.getVersionName();
        } else {
            if (!lastVersionName.equals("0.1")) {
                lastVersionName = VersionNameUtil.getNextVersionName(lastVersionName);
            }
        }

        process.setVersionName(lastVersionName);
        process.setVersionNumber(versionNumber);
        process.setProcessBranch(branch);
        process.setRootFragmentVersion(fvDao.findFragmentVersion(rootFragmentVersionId));
        process.setNumEdges(numEdges);
        process.setNumVertices(numVertices);
        process.setLockStatus(Constants.NO_LOCK);

        addAttributesToProcessModel(proModGrap, process);
        addObjectsToProcessModel(proModGrap, process);
        addResourcesToProcessModel(proModGrap, process);

        pmvDao.save(process);

        return process;
    }


    /* Insert the Attributes to the ProcessModel */
    private void addAttributesToProcessModel(final CPF proModGrap, final ProcessModelVersion process) {
        ProcessModelAttribute pmvAtt;
        for (FlowNode node : proModGrap.getFlowNodes()) {
            for (Entry<String, ICpfAttribute> obj : ((CpfNode) node).getAttributes().entrySet()) {
                pmvAtt = new ProcessModelAttribute();
                pmvAtt.setName(obj.getKey());
                pmvAtt.setValue(obj.getValue().getValue());
                if (obj.getValue().getAny() instanceof Element) {
                    String anyXML = XMLUtils.anyElementToString((Element)obj.getValue().getAny());
                    // pmvAtt.setAny(anyXML);
                }
                process.getProcessModelAttributes().add(pmvAtt);
            }
        }
    }

    /* Insert the Objects to the ProcessModel TODO: Attributes */
    private void addObjectsToProcessModel(final CPF proModGrap, final ProcessModelVersion process) {
        ObjectType objTyp;
        for (FlowNode node : proModGrap.getFlowNodes()) {
            for (ICpfObject obj : ((CpfNode) node).getObjects()) {
                objTyp = new ObjectType();
                objTyp.setName(obj.getName());
                objTyp.setConfigurable(String.valueOf(obj.isConfigurable()));
                objTyp.setProcessModelVersion(process);
                //objTyp.setObjectTypeAttributes();

                process.getObjectTypes().add(objTyp);
            }
        }
    }

    /* Insert the Resources to the ProcessModel TODO: Attributes */
    private void addResourcesToProcessModel(final CPF proModGrap, final ProcessModelVersion process) {
        ResourceType resTyp;
        for (FlowNode node : proModGrap.getFlowNodes()) {
            for (ICpfResource obj : ((CpfNode) node).getResource()) {
                resTyp = new ResourceType();
                resTyp.setName(obj.getName());
                resTyp.setOriginalId(obj.getOriginalId());
                resTyp.setConfigurable(String.valueOf(obj.isConfigurable()));
                resTyp.setProcessModelVersion(process);
                //resTyp.setResourceTypeAttributes();

                process.getResourceTypes().add(resTyp);
            }
        }
    }


    /* Inserts a new Process Fragment Mapping to the repository */
    private void insertProcessFragmentMappings(final ProcessModelVersion pmv, final List<String> composingFragmentIds) {
        for (String uri : composingFragmentIds) {
            ProcessFragmentMap map = new ProcessFragmentMap();
            map.setProcessModelVersion(pmv);
            map.setFragmentVersion(fvDao.findFragmentVersionByURI(uri));
            pfmDao.save(map);
        }
    }


    private void deleteProcessModel(final ProcessModelVersion pvid) throws ExceptionDao {
        try {
            Integer rootFragmentVersion = pDao.getRootFragmentVersionId(pvid.getId());
            pmvDao.delete(pvid);
            deleteFragmentVersion(rootFragmentVersion);
        } catch (Exception e) {
            String msg = "Failed to delete the process model version " + pvid;
            LOGGER.error(msg, e);
            throw new ExceptionDao(msg, e);
        }
    }

    private void deleteFragmentVersion(final Integer fvid) throws ExceptionDao {
        List<FragmentVersionDag> childFragments = fvdDao.getChildMappings(fvid);
        Integer contentId = fvDao.getContentId(fvid);
        frgSrv.deleteChildRelationships(fvid);
        cDao.delete(cDao.findContent(contentId));

        for (FragmentVersionDag childFV : childFragments) {
            // TODO: Change this to not use the Integer Id
            deleteFragmentVersion(childFV.getChildFragmentVersionId().getId());
        }
    }


    /**
     * Set the Branch DAO object for this class. Mainly for spring tests.
     *
     * @param brnDAOJpa the branch Dao.
     */
    public void setBranchDao(final ProcessBranchDao brnDAOJpa) {
        bDao = brnDAOJpa;
    }

    /**
     * Set the Content DAO object for this class. Mainly for spring tests.
     *
     * @param cntDAOJpa the content Dao.
     */
    public void setContentDao(final ContentDao cntDAOJpa) {
        cDao = cntDAOJpa;
    }

    /**
     * Set the Process DAO object for this class. Mainly for spring tests.
     *
     * @param prsDAOJpa the process Dao.
     */
    public void setProcessDao(final ProcessDao prsDAOJpa) {
        pDao = prsDAOJpa;
    }

    /**
     * Set the Process Model Version DAO object for this class. Mainly for spring tests.
     *
     * @param pmvDAOJpa the process Model Version Dao.
     */
    public void setProcessModelVersionDao(final ProcessModelVersionDao pmvDAOJpa) {
        pmvDao = pmvDAOJpa;
    }

    /**
     * Set the Process Fragment Map DAO object for this class. Mainly for spring tests.
     *
     * @param pfmDAOJpa the process Fragment Map Dao.
     */
    public void setProcessFragmentMapDao(final ProcessFragmentMapDao pfmDAOJpa) {
        pfmDao = pfmDAOJpa;
    }

    /**
     * Set the Fragment Version DAO object for this class. Mainly for spring tests.
     *
     * @param frgDAOJpa the Fragment Version Dao.
     */
    public void setFragmentVersionDao(final FragmentVersionDao frgDAOJpa) {
        fvDao = frgDAOJpa;
    }

    /**
     * Set the Fragment Version Dag DAO object for this class. Mainly for spring tests.
     *
     * @param fvdDAOJpa the Fragment Version Dag Dao.
     */
    public void setFragmentVersionDagDao(final FragmentVersionDagDao fvdDAOJpa) {
        fvdDao = fvdDAOJpa;
    }

    /**
     * Set the User Service for this class. Mainly for spring tests.
     *
     * @param usrSrv the service
     */
    public void setUserService(final UserServiceImpl usrSrv) {
        this.uSrv = usrSrv;
    }

    /**
     * Set the Format Service for this class. Mainly for spring tests.
     *
     * @param fmtSrv the service
     */
    public void setFormatService(final FormatServiceImpl fmtSrv) {
        this.fSrv = fmtSrv;
    }

    /**
     * Set the Fragment Service for this class. Mainly for spring tests.
     *
     * @param frgsrv the service
     */
    public void setFragmentService(final FragmentService frgsrv) {
        this.frgSrv = frgsrv;
    }

    /**
     * Set the lock Service for this class. Mainly for spring tests.
     *
     * @param lsrv the service
     */
    public void setLockService(final LockService lsrv) {
        this.lSrv = lsrv;
    }

    /**
     * Set the ComposerServiceImpl for this class. Mainly for spring tests.
     *
     * @param comp the composer
     */
    public void setComposer(final ComposerService comp) {
        this.composer = comp;
    }

    /**
     * Set the DecomposerService for this class. Mainly for spring tests.
     *
     * @param decomp the DecomposerService
     */
    public void setDecomposer(final DecomposerService decomp) {
        this.decomposer = decomp;
    }

    /**
     * Set the Change Propagator for this class. Mainly for spring tests.
     *
     * @param changePropagator the Propagator
     */
    public void setChangePropagator(final ChangePropagator changePropagator) {
        this.cPropagator = changePropagator;
    }
}
