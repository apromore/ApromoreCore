package org.apromore.service.impl;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.cpf.NetType;
import org.apromore.cpf.NodeType;
import org.apromore.cpf.TaskType;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.ContentRepository;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.NetRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Content;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Net;
import org.apromore.dao.model.Node;
import org.apromore.dao.model.Object;
import org.apromore.dao.model.ObjectAttribute;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelAttribute;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.ProcessUser;
import org.apromore.dao.model.Resource;
import org.apromore.dao.model.ResourceAttribute;
import org.apromore.dao.model.User;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.exception.LockFailedException;
import org.apromore.exception.RepositoryException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.IAttribute;
import org.apromore.graph.canonical.ICPFObject;
import org.apromore.graph.canonical.ICPFResource;
import org.apromore.graph.canonical.ObjectTypeEnum;
import org.apromore.graph.canonical.ResourceTypeEnum;
import org.apromore.manager.client.helper.PluginHelper;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.ComposerService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.LockService;
import org.apromore.service.ProcessService;
import org.apromore.service.UserService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.DecanonisedProcess;
import org.apromore.service.model.NameValuePair;
import org.apromore.service.search.SearchExpressionBuilder;
import org.apromore.util.StreamUtil;
import org.apromore.util.VersionNameUtil;
import org.apromore.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Element;
import org.wfmc._2008.xpdl2.PackageType;

/**
 * Implementation of the UserService Contract.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private AnnotationRepository annotationRepo;
    private ContentRepository contentRepo;
    private NativeRepository nativeRepo;
    private NetRepository netRepo;
    private ProcessBranchRepository processBranchRepo;
    private ProcessRepository processRepo;
    private FragmentVersionRepository fragmentVersionRepo;
    private FragmentVersionDagRepository fragmentVersionDagRepo;
    private ProcessModelVersionRepository processModelVersionRepo;
    private CanonicalConverter converter;
    private CanoniserService canoniserSrv;
    private LockService lService;
    private UserService userSrv;
    private FormatService formatSrv;
    private FragmentService fService;
    private ComposerService composerSrv;
    private DecomposerService decomposerSrv;
    private UserInterfaceHelper ui;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepo Annotations repository.
     * @param contentRepo Content Repository
     * @param nativeRepo Native Repository.
     * @param netRepo Net Repository.
     * @param processBranchRepo Process Branch Map Repository.
     * @param processRepo Process Repository
     * @param fragmentVersionRepo Fragment Version Repository.
     * @param fragmentVersionDagRepo Fragment Version Dag Repository.
     * @param processModelVersionRepo Process Model Version Repository.
     * @param converter Canonical Format Converter.
     * @param canoniserSrv Canoniser Service.
     * @param lService Lock Service.
     * @param userSrv User Service
     * @param fService Fragment Service
     * @param formatSrv Format Service.
     * @param composerSrv composer Service.
     * @param decomposerSrv decomposer Service.
     * @param ui User Interface Helper.
     */
    @Inject
    public ProcessServiceImpl(final AnnotationRepository annotationRepo, final ContentRepository contentRepo, final NetRepository netRepo,
            final NativeRepository nativeRepo, final ProcessBranchRepository processBranchRepo, ProcessRepository processRepo,
            final FragmentVersionRepository fragmentVersionRepo, final FragmentVersionDagRepository fragmentVersionDagRepo,
            final ProcessModelVersionRepository processModelVersionRepo, final CanonicalConverter converter,
            final CanoniserService canoniserSrv, final LockService lService, final UserService userSrv, final FragmentService fService,
            final FormatService formatSrv, final @Qualifier("composerServiceImpl") ComposerService composerSrv, final DecomposerService decomposerSrv,
            final UserInterfaceHelper ui) {
        this.annotationRepo = annotationRepo;
        this.contentRepo = contentRepo;
        this.nativeRepo = nativeRepo;
        this.netRepo = netRepo;
        this.processBranchRepo = processBranchRepo;
        this.processRepo = processRepo;
        this.fragmentVersionRepo = fragmentVersionRepo;
        this.fragmentVersionDagRepo = fragmentVersionDagRepo;
        this.processModelVersionRepo = processModelVersionRepo;
        this.converter = converter;
        this.canoniserSrv = canoniserSrv;
        this.lService = lService;
        this.fService = fService;
        this.userSrv = userSrv;
        this.formatSrv = formatSrv;
        this.composerSrv = composerSrv;
        this.decomposerSrv = decomposerSrv;
        this.ui = ui;
    }

    /**
     * @see org.apromore.service.ProcessService#readProcessSummaries(String)
     *      {@inheritDoc}
     */
    @Override
    public ProcessSummariesType readProcessSummaries(final String searchExpression) {
        ProcessSummariesType processSummaries = null;

        try {
            // Firstly, do we need to use the searchExpression
            SearchExpressionBuilder seb = new SearchExpressionBuilder();
            String conditions = seb.buildSearchConditions(searchExpression);
            LOGGER.debug("Search Expression Builder output: " + conditions);

            // Now... Build the Object tree from this list of processes.
            processSummaries = ui.buildProcessSummaryList(conditions, null);
        } catch (UnsupportedEncodingException usee) {
            LOGGER.error("Failed to get Process Summaries: " + usee.toString());
        }

        return processSummaries;
    }


    /**
     * @see org.apromore.service.ProcessService#importProcess(String, String, Double, String, org.apromore.service.model.CanonisedProcess, java.io.InputStream, String, String, String, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProcessModelVersion importProcess(final String username, final String processName, final Double versionNumber, final String natType,
            final CanonisedProcess cpf, final InputStream nativeXml, final String domain, final String documentation,
            final String created, final String lastUpdate) throws ImportException {
        LOGGER.info("Executing operation canoniseProcess");
        ProcessModelVersion pmv;

        try {
            User user = userSrv.findUserByLogin(username);
            NativeType nativeType = formatSrv.findNativeType(natType);
            Process process = insertProcess(processName, user, nativeType, domain);
            pmv = addProcess(process, processName, versionNumber, Constants.TRUNK_NAME, created, lastUpdate, cpf);
            formatSrv.storeNative(processName, pmv, nativeXml, created, lastUpdate, user, nativeType, Constants.INITIAL_ANNOTATION, cpf);
        } catch (UserNotFoundException | JAXBException| ImportException e) {
            LOGGER.error("Failed to import process {} with native type {}", processName, natType);
            LOGGER.error("Original exception was: ", e);
            throw new ImportException(e);
        }

        return pmv;
    }

    /**
     * @see ProcessService#updateProcess
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProcessModelVersion updateProcess(final Integer processId, final String processName, final String originalBranchName,
            final String newBranchName, final Double versionNumber, final Double originalVersionNumber, final Boolean createNewBranch,
            final User user, final String lockStatus, final NativeType nativeType, final CanonisedProcess cpf, final InputStream nativeXML)
            throws ImportException, RepositoryException {
        Canonical graph;
        FragmentVersion rootFragment;
        ProcessModelVersion processModelVersion = null;
        if (lockStatus == null || Constants.UNLOCKED.equals(lockStatus)) {
            throw new RepositoryException("Process model " + processName + " is not locked for the updating session.");
        }
        if (processName == null || originalBranchName == null || originalVersionNumber == null) {
            throw new RepositoryException("Process Name, Branch Name and Version Number need to be supplied to update a process model!");
        }

        try {
            ProcessModelVersion pmVersion = processModelVersionRepo.getProcessModelVersion(processId, originalBranchName, originalVersionNumber);
            if (pmVersion != null) {
                if (versionNumber.equals(pmVersion.getVersionNumber())) {
                    LOGGER.error("CONFLICT! The process model " + processName + " - " + originalBranchName + " has been updated by another user." +
                            "\nThis process model version number: " + versionNumber + "\nCurrent process model version number: " +
                            pmVersion.getVersionNumber());
                }

                graph = converter.convert(cpf.getCpt());
                rootFragment = decomposerSrv.decompose(graph, pmVersion);
                if (rootFragment != null) {
                    propagateChangesWithLockRelease(pmVersion.getRootFragmentVersion(), rootFragment, pmVersion.getFragmentVersions());
                }

                String now = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
                formatSrv.storeNative(processName, pmVersion, nativeXML, now, now, user, nativeType, versionNumber.toString(), cpf);
            } else {
                LOGGER.error("unable to find the Process Model to update.");
            }

        } catch (RepositoryException | JAXBException e) {
            throw new RepositoryException("Failed to Update process model.", e);
        }
        return processModelVersion;
    }



    /**
     * @see org.apromore.service.ProcessService#exportProcess(String, Integer, String, Double, String, String, boolean, java.util.Set)
     *      {@inheritDoc}
     */
    @Override
    public ExportFormatResultType exportProcess(final String name, final Integer processId, final String branch, final Double version,
            final String format, final String annName, final boolean withAnn, Set<RequestParameterType<?>> canoniserProperties)
            throws ExportFormatException {
        try {
            CanonicalProcessType cpt = getCurrentProcessModel(name, branch, false);

            // TODO XML model of web service should not already be used here, but in ManagerEndpoint
            ExportFormatResultType exportResult = new ExportFormatResultType();
            if ((withAnn && format.startsWith(Constants.INITIAL_ANNOTATION)) || format.startsWith(Constants.ANNOTATIONS)) {
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(nativeRepo.getNative(processId, version, format).getContent(), "text/xml")));
            } else if (format.equals(Constants.CANONICAL)) {
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(canoniserSrv.CPFtoString(cpt), "text/xml")));
            } else {
                DecanonisedProcess dp;
                if (withAnn) {
                    String annotation = annotationRepo.getAnnotation(processId, branch, version, annName).getContent();
                    AnnotationsType anf = ANFSchema.unmarshalAnnotationFormat(new ByteArrayDataSource(annotation, "text/xml").getInputStream(), false).getValue();
                    dp = canoniserSrv.deCanonise(processId, branch, format, cpt, anf, canoniserProperties);
                } else {
                    dp = canoniserSrv.deCanonise(processId, branch, format, cpt, null, canoniserProperties);
                }
                exportResult.setMessage(PluginHelper.convertFromPluginMessages(dp.getMessages()));
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(dp.getNativeFormat(), "text/xml")));
            }
            return exportResult;
        } catch (Exception e) {
            LOGGER.error("Failed to export process model {} to format {}", name, format);
            LOGGER.error("Original exception was: ", e);
            throw new ExportFormatException(e);
        }
    }


    /**
     * @see org.apromore.service.ProcessService#updateProcessMetaData(Integer, String, String, String, Double, Double, String)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void updateProcessMetaData(final Integer processId, final String processName, final String domain, final String username,
            final Double preVersion, final Double newVersion, final String ranking) throws UpdateProcessException {
        LOGGER.info("Executing operation update process meta data.");
        try {
            Process process = processRepo.findOne(processId);
            process.setDomain(domain);
            process.setName(processName);
            process.setUser(userSrv.findUserByLogin(username));

            ProcessModelVersion processModelVersion = processModelVersionRepo.getCurrentProcessModelVersion(processId, preVersion);
            ProcessBranch branch = processModelVersion.getProcessBranch();
            branch.setRanking(ranking);

            updateNativeRecords(processModelVersion.getNatives(), processName, username, newVersion);

            processRepo.save(process);
            processModelVersionRepo.save(processModelVersion);
            processBranchRepo.save(branch);
        } catch (Exception e) {
            throw new UpdateProcessException(e.getMessage(), e.getCause());
        }
    }


    /**
     * @see ProcessService#addProcessModelVersion(ProcessBranch, FragmentVersion, Double, int, int)
     *      {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProcessModelVersion addProcessModelVersion(final ProcessBranch branch, final FragmentVersion rootFragmentVersion,
                final Double versionNumber, final int numVertices, final int numEdges) throws ExceptionDao {
        ProcessModelVersion pmv = new ProcessModelVersion();

        pmv.setProcessBranch(branch);
        pmv.setRootFragmentVersion(rootFragmentVersion);
        pmv.setVersionNumber(versionNumber);
        pmv.setNumVertices(numVertices);
        pmv.setNumEdges(numEdges);

        return processModelVersionRepo.save(pmv);
    }




    /**
     * @see ProcessService#getCanonicalFormat(org.apromore.dao.model.ProcessModelVersion)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public CanonicalProcessType getCanonicalFormat(final ProcessModelVersion pmv) {
        String processName = pmv.getProcessBranch().getProcess().getName();
        String branchName = pmv.getProcessBranch().getBranchName();
        return getCanonicalFormat(pmv, processName, branchName, false);
    }


    /**
     * @see ProcessService#getCanonicalFormat(ProcessModelVersion, String, String, boolean)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public CanonicalProcessType getCanonicalFormat(final ProcessModelVersion pmv, final String processName, final String branchName, final boolean lock) {
        Canonical canonical;
        CanonicalProcessType tmp;
        CanonicalProcessType result = new CanonicalProcessType();
        try {
            canonical = composerSrv.compose(pmv.getRootFragmentVersion());
            canonical.setProperty(Constants.PROCESS_NAME, processName);
            canonical.setProperty(Constants.BRANCH_NAME, branchName);
            canonical.setProperty(Constants.BRANCH_ID, pmv.getProcessBranch().getId().toString());
            canonical.setProperty(Constants.VERSION_NUMBER, Double.toString(pmv.getVersionNumber()));
            canonical.setProperty(Constants.PROCESS_MODEL_VERSION_ID, pmv.getId().toString());
            canonical.setProperty(Constants.ROOT_FRAGMENT_ID, pmv.getRootFragmentVersion().getId().toString());
            if (lock) {
                canonical.setProperty(Constants.LOCK_STATUS, Constants.LOCKED);
            }

            tmp = converter.convert(canonical);

            result.getNet().addAll(tmp.getNet());
            result.getResourceType().addAll(tmp.getResourceType());
            result.getAttribute().addAll(tmp.getAttribute());
            if (pmv.getNet() != null) {
                result.setName(processName);
                result.setUri(pmv.getProcessBranch().getProcess().getId().toString());
                result.setAuthor(pmv.getProcessBranch().getProcess().getUser().getUsername());
                result.setCreationDate(pmv.getProcessBranch().getCreationDate());
                result.setModificationDate(pmv.getProcessBranch().getLastUpdate());
                result.setVersion(Double.toString(pmv.getVersionNumber()));
                result.getRootIds().add(pmv.getNet().getNetUri());
            }
        } catch (ExceptionDao e) {
            String msg = "Failed to retrieve the current version of the process model " + processName + " - " + branchName;
            LOGGER.error(msg, e);
        }
        return result;
    }


    /**
     * @see ProcessService#getCurrentProcessModel(String, String, boolean)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public CanonicalProcessType getCurrentProcessModel(final String processName, final String branchName, final boolean lock)
            throws LockFailedException {
        ProcessModelVersion pmv = processModelVersionRepo.getCurrentProcessModelVersion(processName, branchName);

        if (pmv == null) {
            return null;
        }
        if (lock) {
            boolean locked = lService.lockFragment(pmv.getRootFragmentVersion().getId());
            if (!locked) {
                throw new LockFailedException();
            }
        }

        return getCanonicalFormat(pmv, processName, branchName, lock);
    }


    /**
     * Creates new versions for all ascendant fragments of originalFragment by
     * replacing originalFragment with updatedFragment. New versions will be
     * created for all process models which use any of the updated fragments as
     * its root fragment. This method also releases locks of all ascendant
     * fragments.
     * @param originalFragment the original fragment id
     * @param updatedFragment the updated fragment id
     * @param composingFragments the composing fragments
     */
    @Override
    @Transactional(readOnly = false)
    public void propagateChangesWithLockRelease(final FragmentVersion originalFragment, final FragmentVersion updatedFragment,
            final Set<FragmentVersion> composingFragments) throws RepositoryException {
        // create new versions for all process models, which use this fragment as the root fragment, and unlock those process models.
        List<ProcessModelVersion> usedProcessModels = processModelVersionRepo.getUsedProcessModelVersions(originalFragment);
        for (ProcessModelVersion pmv : usedProcessModels) {
            createNewProcessModelVersion(pmv, updatedFragment, composingFragments);
        }

        // unlock the fragment
        LOGGER.info("Unlocking the original fragment: " + originalFragment);
        lService.unlockFragment(originalFragment);

        // release locks of all descendant fragments of the original fragment
        lService.unlockDescendantFragments(originalFragment);

        // create new version for all ascendant fragments
        LOGGER.info("Propagating to parent fragments of fragment: " + originalFragment);
        List<FragmentVersion> lockedParents = fragmentVersionRepo.getLockedParentFragments(originalFragment);

        for (FragmentVersion parent : lockedParents) {
            propagateToParentsWithLockRelease(parent, originalFragment, updatedFragment, composingFragments);
        }
    }




    /**
     * @see ProcessService#deleteProcess(org.apromore.dao.model.Process)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteProcess(final Process process) {
        processRepo.delete(process);
    }

    /**
     * @see ProcessService#deleteProcessModel(java.util.List)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteProcessModel(final List<NameValuePair> models) {
        ProcessModelVersion pvid;
        for (NameValuePair entry : models) {
            try {
                LOGGER.debug("Retrieving the Process Model of the current version of " + entry.getName() + " - " + entry.getValue() + " to be deleted.");
                pvid = processModelVersionRepo.getCurrentProcessModelVersion(entry.getName(), entry.getValue());

                Process process = pvid.getProcessBranch().getProcess();
                processRepo.delete(process);

                deleteProcessModelVersion(pvid);
            } catch (Exception e) {
                String msg = "Failed to delete the current version of the branch " + entry.getValue() + " of the process model " + entry.getValue();
                LOGGER.error(msg, e);
            }
        }
    }



    /* Does the processing of ImportProcess. */
    @Transactional(readOnly = false)
    private ProcessModelVersion addProcess(final Process process, final String processName, final Double versionNumber, final String branchName,
            final String created, final String lastUpdated, final CanonisedProcess cpf) throws ImportException {
        if (cpf == null) {
            LOGGER.error("Process " + processName + " Failed to import correctly.");
            throw new ImportException("Process " + processName + " Failed to import correctly.");
        } else if (processRepo.getProcessByNameAndBranchName(processName, branchName) != null) {
            LOGGER.error("Process " + processName + " was found to already exist in the Repository.");
            throw new ImportException("Process " + processName + " was found to already exist in the Repository.");
        }

        Canonical can;
        FragmentVersion rootFragment;
        ProcessModelVersion pmv;
//        OperationContext netRoot = new OperationContext();
//        Map<String, ProcessModelVersion> models = new HashMap<String, ProcessModelVersion>(0);
        try {
            LOGGER.info("Starting to process: " + cpf.getCpt().getUri() + " - " + cpf.getCpt().getName());
            ProcessBranch branch = insertProcessBranch(process, created, lastUpdated, branchName);

            can = converter.convert(cpf.getCpt());
            pmv = createProcessModelVersion(process, branch, versionNumber, can, cpf.getCpt().getUri());
            rootFragment = decomposerSrv.decompose(can, pmv);
            if (rootFragment != null) {
                pmv.setRootFragmentVersion(rootFragment);
            } else {
                throw new ImportException("The Root Fragment Version can not be NULL. please check logs for other errors!");
            }


            /* This is for Sub processes, doesn't seem to work correctly just yet. */
//            for (NetType net : cpf.getCpt().getNet()) {
//                if (net.getNode() != null || net.getNode().size() > 0) {
//                    LOGGER.info("Starting to process Net: " + net.getId() + " - " + net.getName());
//
//                    can = converter.convert(createNet(cpf, net));
//                    pmv = createProcessModelVersion(process, branch, versionNumber, can, net.getId());
//                    netRoot = decomposerSrv.decompose(can, pmv);
//                    if (netRoot != null) {
//                        pmv.setRootFragmentVersion(netRoot.getCurrentFragment());
//                        models.put(net.getId(), pmv);
//                    }
//                }
//            }
//            createSubProcessReferences(cpf, models);
//            createRootProcessReferences(process, cpf, models);
        } catch (RepositoryException re) {
            throw new ImportException("Failed to add the process model " + processName, re);
        }
        return pmv;
    }



    /* Delete a Process Model */
    private void deleteProcessModelVersion(final ProcessModelVersion pmv) throws ExceptionDao {
        try {
            // Check is the ProcessModelVersion used by any other Branch (Check the sourceProcessModelVersionId column in branch).
            if (processBranchRepo.countProcessModelBeenForked(pmv) > 0) {
                LOGGER.error("There are other branches forked from this Process Model.");
            } else {
                FragmentVersion fragmentVersion = pmv.getRootFragmentVersion();

                // Delete the FragmentVersions
                deleteFragmentVersion(fragmentVersion, true);
            }
        } catch (Exception e) {
            String msg = "Failed to delete the process model version " + pmv.getId();
            LOGGER.error(msg, e);
            throw new ExceptionDao(msg, e);
        }
    }

    /* Delete a Fragment Version from the Database. Check if it is used by other PMV or FV first. */
    private void deleteFragmentVersion(FragmentVersion fragmentVersion, boolean rootFragmentVersion) {
        long processCount = processModelVersionRepo.countFragmentUsesInProcessModels(fragmentVersion);
        long fragmentCount =  fragmentVersionRepo.countFragmentUsesInFragmentVersions(fragmentVersion);
        if ((rootFragmentVersion && processCount == 1 && fragmentCount == 0) ||
                (!rootFragmentVersion && processCount == 0 && fragmentCount == 0)) {
            List<FragmentVersion> children = fragmentVersionRepo.getChildFragmentsByFragmentVersion(fragmentVersion);
            Content content = fragmentVersion.getContent();
            fragmentVersionDagRepo.deleteChildRelationships(fragmentVersion);
            fragmentVersionRepo.delete(fragmentVersion);
            contentRepo.delete(content);
            for (FragmentVersion child : children) {
                deleteFragmentVersion(child, false);
            }
        }
    }


    /* Update a list of native process models with this new meta data, */
    private void updateNativeRecords(final Set<Native> natives, final String processName, final String username, final Double version)
            throws CanoniserException, JAXBException {
        for (Native n : natives) {
            String natType = n.getNativeType().getNatType();
            InputStream inStr = new ByteArrayInputStream(n.getContent().getBytes());
            CanonisedProcess cp = canoniserSrv.canonise(natType, inStr, new HashSet<RequestParameterType<?>>(0));

            //TODO why is this done here? apromore should not know about native format outside of canonisers
            if (natType.compareTo("XPDL 2.1") == 0) {
                PackageType pakType = StreamUtil.unmarshallXPDL(inStr);
                StreamUtil.copyParam2XPDL(pakType, processName, version.toString(), username, null, null);
                n.setContent(StreamUtil.marshallXPDL(pakType));
            }
        }
    }

    /* Inserts a new process into the DB. */
    private Process insertProcess(final String processName, final User user, final NativeType nativeType, final String domain)
            throws ImportException {
        LOGGER.info("Executing operation Insert Process");
        Process process = new Process();

        try {
            process.setName(processName);
            process.setUser(user);
            process.setDomain(domain);
            process.setNativeType(nativeType);

            ProcessUser processUser = new ProcessUser();
            processUser.setProcess(process);
            processUser.setUser(user);
            processUser.setHasRead(true);
            processUser.setHasWrite(true);
            processUser.setHasOwnership(true);

            user.getProcessUsers().add(processUser);
            process.getProcessUsers().add(processUser);

            return processRepo.save(process);
        } catch (Exception ex) {
            LOGGER.error("Importing a Process Failed: " + ex.toString());
            throw new ImportException(ex);
        }
    }

    /* inserts a new branch into the DB. */
    private ProcessBranch insertProcessBranch(final Process process, final String created, final String lastUpdated, final String name) throws ImportException {
        LOGGER.info("Executing operation Insert Branch");
        ProcessBranch branch = new ProcessBranch();

        try {
            branch.setProcess(process);
            branch.setBranchName(name);
            branch.setCreationDate(created);
            branch.setLastUpdate(lastUpdated);

            process.getProcessBranches().add(branch);

            return processBranchRepo.save(branch);
        } catch (Exception ex) {
            LOGGER.error("Importing a Branch Failed: " + ex.toString());
            throw new ImportException(ex);
        }
    }

    private ProcessModelVersion createProcessModelVersion(final Process process, final ProcessBranch branch, final Double versionNumber,
            final Canonical proModGrap, final String netId) {
        ProcessModelVersion processModel = new ProcessModelVersion();

        processModel.setProcessBranch(branch);
        processModel.setOriginalId(netId);
        processModel.setVersionNumber(versionNumber);
        processModel.setNumEdges(proModGrap.countEdges());
        processModel.setNumVertices(proModGrap.countVertices());
        processModel.setLockStatus(Constants.NO_LOCK);

        branch.setCurrentProcessModelVersion(processModel);

        addAttributesToProcessModel(proModGrap, processModel);
        addObjectsToProcessModel(proModGrap, processModel);
        addResourcesToProcessModel(proModGrap, processModel);
        updateResourcesOnProcessModel(proModGrap.getResources(), processModel);

        return processModelVersionRepo.save(processModel);
    }


    /* Insert the Attributes to the ProcessModel */
    private void addAttributesToProcessModel(final Canonical proModGrap, final ProcessModelVersion process) {
        ProcessModelAttribute pmvAtt;
        for (CPFNode node : proModGrap.getNodes()) {
            for (Map.Entry<String, IAttribute> obj : node.getAttributes().entrySet()) {
                pmvAtt = new ProcessModelAttribute();
                pmvAtt.setName(obj.getKey());
                pmvAtt.setValue(obj.getValue().getValue());
                if (obj.getValue().getAny() instanceof Element) {
                    pmvAtt.setValue(XMLUtils.anyElementToString((Element) obj.getValue().getAny()));
                }
                pmvAtt.setProcessModelVersion(process);
                process.getProcessModelAttributes().add(pmvAtt);
            }
        }
    }

    /* Insert the Objects to the ProcessModel TODO: Attributes */
    private void addObjectsToProcessModel(final Canonical proModGrap, final ProcessModelVersion process) {
        Object objTyp;
        if (proModGrap.getObjects() != null) {
            for (ICPFObject cpfObj : proModGrap.getObjects()) {
                objTyp = new org.apromore.dao.model.Object();
                objTyp.setUri(cpfObj.getId());
                objTyp.setName(cpfObj.getName());
                objTyp.setNetId(cpfObj.getNetId());
                objTyp.setConfigurable(cpfObj.isConfigurable());
                objTyp.setProcessModelVersion(process);
                if (cpfObj.getObjectType().equals(ObjectTypeEnum.HARD)) {
                    objTyp.setType(ObjectTypeEnum.HARD);
                } else {
                    objTyp.setType(ObjectTypeEnum.SOFT);
                    objTyp.setSoftType(cpfObj.getSoftType());
                }

                addObjectAttributes(objTyp, cpfObj);

                process.getObjects().add(objTyp);
            }
        }
    }

    /* Add Attributes to the Object Reference. */
    private void addObjectAttributes(final org.apromore.dao.model.Object object, final ICPFObject cpfObject) {
        ObjectAttribute objAtt;
        for (Map.Entry<String, IAttribute> e : cpfObject.getAttributes().entrySet()) {
            objAtt = new ObjectAttribute();
            objAtt.setName(e.getKey());
            objAtt.setValue(e.getValue().getValue());
            objAtt.setObject(object);

            object.getObjectAttributes().add(objAtt);
        }
    }

    /* Insert the Resources to the ProcessModel TODO: Attributes */
    private void addResourcesToProcessModel(final Canonical proModGrap, final ProcessModelVersion process) {
        Resource resTyp;
        if (proModGrap.getResources() != null) {
            for (ICPFResource cpfRes : proModGrap.getResources()) {
                resTyp = new Resource();
                resTyp.setUri(cpfRes.getId());
                resTyp.setName(cpfRes.getName());
                resTyp.setOriginalId(cpfRes.getOriginalId());
                resTyp.setConfigurable(cpfRes.isConfigurable());
                if (cpfRes.getResourceType() != null) {
                    if (cpfRes.getResourceType().equals(ResourceTypeEnum.HUMAN)) {
                        resTyp.setType(ResourceTypeEnum.HUMAN);
                        if (cpfRes.getHumanType() != null) {
                            resTyp.setTypeName(cpfRes.getHumanType().value());
                        }
                    } else {
                        resTyp.setType(ResourceTypeEnum.NONHUMAN);
                        if (cpfRes.getNonHumanType() != null) {
                            resTyp.setTypeName(cpfRes.getNonHumanType().value());
                        }
                    }
                }
                resTyp.setProcessModelVersion(process);

                addResourceAttributes(resTyp, cpfRes);

                process.getResources().add(resTyp);
            }
        }
    }

    /* Add Attributes to the Object Reference. */
    private void addResourceAttributes(final Resource resource, final ICPFResource cpfResource) {
        ResourceAttribute resAtt;
        for (Map.Entry<String, IAttribute> e : cpfResource.getAttributes().entrySet()) {
            resAtt = new ResourceAttribute();
            resAtt.setName(e.getKey());
            resAtt.setValue(e.getValue().getValue());
            resAtt.setResource(resource);

            resource.getResourceAttributes().add(resAtt);
        }
    }

    /* Update to Process Models Resource information, specifically the Specialisation Id's. This can't be done at
       time of original entry as we might not have all the Resources in memory. */
    private void updateResourcesOnProcessModel(Set<ICPFResource> resources, ProcessModelVersion processModel) {
        if (resources != null) {
            for (ICPFResource cpfRes : resources) {
                addSpecialisations(processModel, cpfRes, findResource(processModel.getResources(), cpfRes.getId()));
            }
        }
    }

    /* Update the Resource with it's specialisations Ids. */
    private void addSpecialisations(ProcessModelVersion processModel, ICPFResource cpfRes, Resource resource) {
        if (cpfRes.getSpecializationIds() != null) {
            for (String resourceId : cpfRes.getSpecializationIds()) {
                resource.getSpecialisations().add(findResource(processModel.getResources(), resourceId));
            }
        }
    }


    /* Finds the Resource using the resource Id supplied. */
    private Resource findResource(Set<Resource> resources, String resourceId) {
        Resource found = null;
        for (Resource resource : resources) {
            if (resource.getUri().equals(resourceId)) {
                found = resource;
                break;
            }
        }
        if (found == null) {
            LOGGER.warn("Could not find Resource with Id: " + resourceId);
        }
        return found;
    }


    /* Create a Net used for the Creation of the Process Model. */
    private CanonicalProcessType createNet(CanonisedProcess cpf, NetType net) {
        CanonicalProcessType cpt = new CanonicalProcessType();

        cpt.setUri(net.getId());
        cpt.setName(cpf.getCpt().getName());
        cpt.setAuthor(cpf.getCpt().getAuthor());
        cpt.setDataTypes(cpf.getCpt().getDataTypes());
        cpt.setCreationDate(cpf.getCpt().getCreationDate());
        cpt.setModificationDate(cpf.getCpt().getModificationDate());

        cpt.getNet().add(net);
        cpt.getRootIds().addAll(cpf.getCpt().getRootIds());
        cpt.getAttribute().addAll(cpf.getCpt().getAttribute());
        cpt.getResourceType().addAll(cpf.getCpt().getResourceType());

        return cpt;
    }

    /* Updates the Node Sub processes if they have any. */
    private void createSubProcessReferences(CanonisedProcess cpf, Map<String, ProcessModelVersion> models) {
        for (NetType net : cpf.getCpt().getNet()) {
            for (NodeType nodeType : net.getNode()) {
                if ((nodeType instanceof TaskType) && ((TaskType) nodeType).getSubnetId() != null) {
                    Node node = findNode(nodeType.getId(), models);
                    if (node != null) {
                        node.setSubProcess(models.get(((TaskType) nodeType).getSubnetId()));
                    } else {
                        LOGGER.debug("Tried to find a node that doesn't exist: " + nodeType.getId());
                    }
                }
            }
        }
    }


    /* Create Net records so we can determine the root processes for a model. */
    private void createRootProcessReferences(Process process, CanonisedProcess cpf, Map<String, ProcessModelVersion> models) {
        if (cpf.getCpt().getNet().size() == 1) {
            createNetRecord(process, models, cpf.getCpt().getNet().get(0));
        } else {
            for (NetType netType : cpf.getCpt().getNet()) {
                if (cpf.getCpt().getRootIds() != null) {
                    for (String rootId : cpf.getCpt().getRootIds()) {
                        if (rootId.equals(netType.getId())) {
                            createNetRecord(process, models, netType);
                        }
                    }
                }
            }
        }
    }


    /* Create a net Record for the Process Model. */
    private void createNetRecord(Process process, Map<String, ProcessModelVersion> models, NetType netType) {
        ProcessModelVersion pmv = models.get(netType.getId());

        if (pmv != null) {
            Net net = new Net();
            net.setNetUri(netType.getId());
            net.setProcess(process);
            net.setProcessModelVersion(pmv);

            pmv.setNet(net);
            process.getNets().add(net);

            netRepo.save(net);
        }
    }

    /* Finds a node in the maybe multiple process model versions by it's URI. */
    private Node findNode(final String nodeUri, final Map<String, ProcessModelVersion> models) {
        Node found = null;
        for (ProcessModelVersion pmv : models.values()) {
            for (FragmentVersion fv : pmv.getFragmentVersions()) {
                for (Node node : fv.getContent().getNodes()) {
                    if (node.getUri().equals(nodeUri)) {
                        found = node;
                        break;
                    }
                }
            }
        }
        return found;
    }


    private void propagateToParentsWithLockRelease(FragmentVersion parent, FragmentVersion originalFragment,
            FragmentVersion updatedFragment, Set<FragmentVersion> composingFragments) throws RepositoryException {
        LOGGER.info("Propagating - fragment: " + originalFragment + ", parent: " + parent);
        FragmentVersion newParent = createNewFragmentVersionByReplacingChild(parent, originalFragment, updatedFragment);
        composingFragments.add(newParent);
        fillUnchangedDescendants(newParent, updatedFragment, composingFragments);

        List<ProcessModelVersion> usedProcessModels = processModelVersionRepo.getUsedProcessModelVersions(parent);
        for (ProcessModelVersion pmv : usedProcessModels) {
            createNewProcessModelVersion(pmv, newParent, composingFragments);
            lService.unlockProcessModelVersion(pmv);
        }
        lService.unlockFragment(parent);

        List<FragmentVersion> nextLockedParents = fragmentVersionRepo.getLockedParentFragments(parent);
        for (FragmentVersion nextParent : nextLockedParents) {
            propagateToParentsWithLockRelease(nextParent, parent, newParent, composingFragments);
        }
        LOGGER.debug("Completed propagation - fragment: " + originalFragment + ", parent: " + parent);
    }


    private FragmentVersion createNewFragmentVersionByReplacingChild(FragmentVersion fragmentVersion, FragmentVersion oldChildFragmentVersion,
            FragmentVersion newChildFragmentVersion) {
        int lockType = 0;
        int lockCount = 0;
        if (fragmentVersion.getLockStatus() == 1) {
            if (fragmentVersion.getLockCount() > 1) {
                lockType = 1;
                lockCount = fragmentVersion.getLockCount() - 1;
            }
        }

        Map<String, String> childMappings = new HashMap<String, String>(0);
        Set<FragmentVersionDag> childFragmentVersionDags = fragmentVersion.getChildFragmentVersionDags();
        for (FragmentVersionDag childFragment : childFragmentVersionDags) {
            childMappings.put(childFragment.getPocketId(), childFragment.getChildFragmentVersion().getId().toString());
            if (childFragment.getChildFragmentVersion().equals(oldChildFragmentVersion)) {
                childMappings.put(childFragment.getPocketId(), newChildFragmentVersion.getId().toString());
            }
        }

        return fService.addFragmentVersion(null, fragmentVersion.getContent(), childMappings, fragmentVersion.getId().toString(),
                lockType, lockCount, fragmentVersion.getFragmentSize(), fragmentVersion.getFragmentType());
    }

    private void fillUnchangedDescendants(FragmentVersion parent, FragmentVersion updatedChild, Set<FragmentVersion> composingFragments) {
        List<FragmentVersion> allChilds = fragmentVersionRepo.getChildFragmentsByFragmentVersion(parent);
        for (FragmentVersion child : allChilds) {
            if (child.equals(updatedChild)) {
                composingFragments.add(child);
                fillDescendants(child, composingFragments);
            }
        }
    }

    private void fillDescendants(FragmentVersion fragment, Set<FragmentVersion> composingFragments) {
        List<FragmentVersion> allChilds = fragmentVersionRepo.getChildFragmentsByFragmentVersion(fragment);
        for (FragmentVersion child : allChilds) {
            composingFragments.add(child);
            fillDescendants(child, composingFragments);
        }
    }

    private void createNewProcessModelVersion(ProcessModelVersion pmv, FragmentVersion rootFragment,
            Set<FragmentVersion> composingFragments) throws RepositoryException {
        try {
            // TODO: fix this shit....
            Double versionNumber = pmv.getVersionNumber() + 1;
            String versionName = VersionNameUtil.getNextVersionName(pmv.getVersionNumber());
            ProcessModelVersion pdo = addProcessModelVersion(pmv.getProcessBranch(), rootFragment, versionNumber, 0, 0);
            //ProcessDAO.addProcessFragmentMappings(pdo.getProcessModelVersionId(), composingFragmentIds);
        } catch (ExceptionDao de) {
            throw new RepositoryException(de);
        }
    }

    /* From the list of process model versions, it returns the first one it finds. */
    private ProcessModelVersion findRootProcessModelVersion(List<ProcessModelVersion> pmVersion) {
        ProcessModelVersion root = null;
        if (pmVersion != null && !pmVersion.isEmpty()) {
            for (ProcessModelVersion pmv : pmVersion) {
                if (pmv.getNet() != null) {
                    root = pmv;
                }
            }
        }
        return root;
    }

    /* Finds the NetType for the PMV passed in or returns null. */
    private NetType findNetTypeForPMV(List<NetType> nets, ProcessModelVersion pmv) {
        NetType netType = null;
        for (NetType net : nets) {
            if (net.getId().equals(pmv.getOriginalId())) {
                netType = net;
                break;
            }
        }
        return netType;
    }

}
