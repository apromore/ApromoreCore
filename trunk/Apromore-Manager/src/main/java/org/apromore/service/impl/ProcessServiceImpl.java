/**
 *  Copyright 2013
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apromore.service.impl;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.anf.ANFSchema;
import org.apromore.anf.AnnotationsType;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.common.Constants;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.FragmentVersionDagRepository;
import org.apromore.dao.FragmentVersionRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.FragmentVersion;
import org.apromore.dao.model.FragmentVersionDag;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
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
import org.apromore.service.AnnotationService;
import org.apromore.service.CanonicalConverter;
import org.apromore.service.CanoniserService;
import org.apromore.service.ComposerService;
import org.apromore.service.DecomposerService;
import org.apromore.service.FormatService;
import org.apromore.service.FragmentService;
import org.apromore.service.LockService;
import org.apromore.service.ProcessService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.OperationContext;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.CanonisedProcess;
import org.apromore.service.model.DecanonisedProcess;
import org.apromore.service.model.NameValuePair;
import org.apromore.service.search.SearchExpressionBuilder;
import org.apromore.util.StreamUtil;
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
 * Implementation of the ProcessService Contract.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private AnnotationRepository annotationRepo;
    private NativeRepository nativeRepo;
    private ProcessBranchRepository processBranchRepo;
    private ProcessRepository processRepo;
    private FragmentVersionRepository fragmentVersionRepo;
    private FragmentVersionDagRepository fragmentVersionDagRepo;
    private ProcessModelVersionRepository processModelVersionRepo;
    private CanonicalConverter converter;

    private AnnotationService annotationSrv;
    private CanoniserService canoniserSrv;
    private LockService lService;
    private UserService userSrv;
    private FormatService formatSrv;
    private FragmentService fService;
    private ComposerService composerSrv;
    private DecomposerService decomposerSrv;
    private UserInterfaceHelper ui;
    private WorkspaceService workspaceSrv;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepo Annotations repository.
     * @param nativeRepo Native Repository.
     * @param processBranchRepo Process Branch Map Repository.
     * @param processRepo Process Repository
     * @param fragmentVersionRepo Fragment Version Repository.
     * @param fragmentVersionDagRepo Fragment Version Dag Repository.
     * @param processModelVersionRepo Process Model Version Repository.
     * @param converter Canonical Format Converter.
     * @param annotationSrv Annotation Processing Service
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
    public ProcessServiceImpl(final AnnotationRepository annotationRepo,
            final NativeRepository nativeRepo, final ProcessBranchRepository processBranchRepo, ProcessRepository processRepo,
            final FragmentVersionRepository fragmentVersionRepo, final FragmentVersionDagRepository fragmentVersionDagRepo,
            final ProcessModelVersionRepository processModelVersionRepo, final CanonicalConverter converter, final AnnotationService annotationSrv,
            final CanoniserService canoniserSrv, final LockService lService, final UserService userSrv, final FragmentService fService,
            final FormatService formatSrv, final @Qualifier("composerServiceImpl") ComposerService composerSrv, final DecomposerService decomposerSrv,
            final UserInterfaceHelper ui, final WorkspaceService workspaceService) {
        this.annotationRepo = annotationRepo;
        this.nativeRepo = nativeRepo;
        this.processBranchRepo = processBranchRepo;
        this.processRepo = processRepo;
        this.fragmentVersionRepo = fragmentVersionRepo;
        this.fragmentVersionDagRepo = fragmentVersionDagRepo;
        this.processModelVersionRepo = processModelVersionRepo;
        this.converter = converter;
        this.annotationSrv = annotationSrv;
        this.canoniserSrv = canoniserSrv;
        this.lService = lService;
        this.fService = fService;
        this.userSrv = userSrv;
        this.formatSrv = formatSrv;
        this.composerSrv = composerSrv;
        this.decomposerSrv = decomposerSrv;
        this.ui = ui;
        this.workspaceSrv = workspaceService;
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
     * @see org.apromore.service.ProcessService#importProcess(String, Integer, String, Double, String, org.apromore.service.model.CanonisedProcess, String, String, String, String)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProcessModelVersion importProcess(final String username, final Integer folderId, final String processName, final Double versionNumber,
            final String natType, final CanonisedProcess cpf, final String domain, final String documentation,
            final String created, final String lastUpdate) throws ImportException {
        LOGGER.debug("Executing operation canoniseProcess");
        ProcessModelVersion pmv;

        try {
            User user = userSrv.findUserByLogin(username);
            NativeType nativeType = formatSrv.findNativeType(natType);
            Process process = insertProcess(processName, user, nativeType, domain, folderId);

            pmv = addProcess(process, processName, versionNumber, Constants.TRUNK_NAME, created, lastUpdate, cpf, nativeType);
            workspaceSrv.addProcessToFolder(process.getId(), folderId);
            formatSrv.storeNative(processName, pmv, created, lastUpdate, user, nativeType, Constants.INITIAL_ANNOTATION, cpf);
        } catch (UserNotFoundException | JAXBException | IOException e) {
            LOGGER.error("Failed to import process {} with native type {}", processName, natType);
            LOGGER.error("Original exception was: ", e);
            throw new ImportException(e);
        }

        return pmv;
    }

    /**
     * @see org.apromore.service.ProcessService#updateProcess(Integer, String, String, String, Double, Double, Boolean, org.apromore.dao.model.User, String, org.apromore.dao.model.NativeType, org.apromore.service.model.CanonisedProcess)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProcessModelVersion updateProcess(final Integer processId, final String processName, final String originalBranchName,
            final String newBranchName, final Double versionNumber, final Double originalVersionNumber, final Boolean createNewBranch,
            final User user, final String lockStatus, final NativeType nativeType, final CanonisedProcess cpf)
            throws ImportException, RepositoryException {
        ProcessModelVersion pmv;

        try {
            pmv = updateExistingProcess(processId, processName, originalBranchName, versionNumber, originalVersionNumber, lockStatus, cpf, nativeType);

            String now = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
            formatSrv.storeNative(processName, pmv, now, now, user, nativeType, versionNumber.toString(), cpf);
        } catch (RepositoryException | JAXBException | IOException e) {
            LOGGER.error("Failed to update process {}", processName);
            LOGGER.error("Original exception was: ", e);
            throw new RepositoryException("Failed to Update process model.", e);
        }

        return pmv;
    }


    /**
     * @see org.apromore.service.ProcessService#exportProcess(String, Integer, String, Double, String, String, boolean, java.util.Set)
     * {@inheritDoc}
     */
    @Override
    public ExportFormatResultType exportProcess(final String name, final Integer processId, final String branch, final Double version,
            final String format, final String annName, final boolean withAnn, Set<RequestParameterType<?>> canoniserProperties)
            throws ExportFormatException {
        try {
            ExportFormatResultType exportResult = new ExportFormatResultType();

            // Work out if we are looking at the original format or native format for this model.
            if (isRequestForNativeFormat(processId, version, format)) {
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(nativeRepo.getNative(processId, version, format).getContent(),
                        "text/xml")));
            } else {
                CanonicalProcessType cpt = getProcessModelVersion(processId, name, branch, version, false);
                Process process = null;
                if (format.equals(Constants.CANONICAL)) {
                    exportResult.setNative(new DataHandler(new ByteArrayDataSource(canoniserSrv.CPFtoString(cpt), Constants.XML_MIMETYPE)));
                } else {
                    DecanonisedProcess dp;
                    AnnotationsType anf = null;
                    process = processRepo.findOne(processId);
                    if (withAnn) {
                        String annotation = annotationRepo.getAnnotation(processId, branch, version, annName).getContent();
                        if (annotation != null && !annotation.equals("")) {
                            ByteArrayDataSource dataSource = new ByteArrayDataSource(annotation, Constants.XML_MIMETYPE);
                            anf = ANFSchema.unmarshalAnnotationFormat(dataSource.getInputStream(), false).getValue();
                        }

                        anf = annotationSrv.preProcess(process.getNativeType().getNatType(), format, cpt, anf);
                    } else if (annName == null) {
                        anf = annotationSrv.preProcess(null, format, cpt, anf);
                    }

                    if (process != null && format.startsWith("Annotations")) {
                        dp = canoniserSrv.deCanonise(process.getNativeType().getNatType(), cpt, anf, canoniserProperties);
                    } else {
                        dp = canoniserSrv.deCanonise(format, cpt, anf, canoniserProperties);
                    }

                    exportResult.setMessage(PluginHelper.convertFromPluginMessages(dp.getMessages()));
                    exportResult.setNative(new DataHandler(new ByteArrayDataSource(dp.getNativeFormat(), Constants.XML_MIMETYPE)));
                }
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
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void updateProcessMetaData(final Integer processId, final String processName, final String domain, final String username,
            final Double preVersion, final Double newVersion, final String ranking) throws UpdateProcessException {
        LOGGER.debug("Executing operation update process meta data.");
        try {
            Process process = processRepo.findOne(processId);
            process.setDomain(domain);
            process.setName(processName);
            process.setRanking(ranking);
            process.setUser(userSrv.findUserByLogin(username));

            ProcessModelVersion processModelVersion = processModelVersionRepo.getCurrentProcessModelVersion(processId, preVersion);
            ProcessBranch branch = processModelVersion.getProcessBranch();

            updateNative(processModelVersion.getNativeDocument(), processName, username, newVersion);

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
        pmv.setCreateDate(SimpleDateFormat.getDateInstance().format(new Date()));
        pmv.setLastUpdateDate(pmv.getCreateDate());

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
        CanonicalProcessType tmp = new CanonicalProcessType();
        try {
            canonical = composerSrv.compose(pmv.getRootFragmentVersion());
            canonical.setProperty(Constants.PROCESS_NAME, processName);
            canonical.setProperty(Constants.BRANCH_NAME, branchName);
            canonical.setProperty(Constants.BRANCH_ID, pmv.getProcessBranch().getId().toString());
            canonical.setProperty(Constants.VERSION_NUMBER, Double.toString(pmv.getVersionNumber()));
            canonical.setProperty(Constants.PROCESS_MODEL_VERSION_ID, pmv.getId().toString());
            canonical.setProperty(Constants.ROOT_FRAGMENT_ID, pmv.getRootFragmentVersion().getId().toString());
            if (pmv.getProcessBranch().getProcess().getNativeType() != null) {
                canonical.setProperty(Constants.INITIAL_FORMAT, pmv.getProcessBranch().getProcess().getNativeType().getNatType());
            } else {
                canonical.setProperty(Constants.INITIAL_FORMAT, null);
            }
            if (lock) {
                canonical.setProperty(Constants.LOCK_STATUS, Constants.LOCKED);
            }

            tmp = converter.convert(canonical);
        } catch (ExceptionDao e) {
            String msg = "Failed to retrieve the current version of the process model " + processName + " - " + branchName;
            LOGGER.error(msg, e);
        }
        return tmp;
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
     * @see ProcessService#getProcessModelVersion(Integer, String, String, Double, boolean)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public CanonicalProcessType getProcessModelVersion(final Integer processId, final String processName, final String branchName, final Double version,
            final boolean lock) throws LockFailedException {
        ProcessModelVersion pmv = processModelVersionRepo.getProcessModelVersion(processId, branchName, version);

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
            final Set<FragmentVersion> composingFragments, final Double newVersionNumber) throws RepositoryException {
        // create new versions for all process models, which use this fragment as the root fragment, and unlock those process models.
        List<ProcessModelVersion> usedProcessModels = processModelVersionRepo.getUsedProcessModelVersions(originalFragment);
        for (ProcessModelVersion pmv : usedProcessModels) {
            createNewProcessModelVersion(pmv, updatedFragment, composingFragments, newVersionNumber);
        }

        // unlock the fragment
        LOGGER.debug("Unlocking the original fragment: " + originalFragment);
        lService.unlockFragment(originalFragment);

        // release locks of all descendant fragments of the original fragment
        lService.unlockDescendantFragments(originalFragment);

        // create new version for all ascendant fragments
        LOGGER.debug("Propagating to parent fragments of fragment: " + originalFragment);
        List<FragmentVersion> lockedParents = fragmentVersionRepo.getLockedParentFragments(originalFragment);

        for (FragmentVersion parent : lockedParents) {
            propagateToParentsWithLockRelease(parent, originalFragment, updatedFragment, composingFragments, newVersionNumber);
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

                if (pvid != null) {
                    Process process = pvid.getProcessBranch().getProcess();
                    Set<ProcessBranch> branches = process.getProcessBranches();

                    // Only delete the version selected, but if there is only a single version then remove all of the process
                    if (branches.size() > 1 || (branches.size() == 1 && pvid.getProcessBranch().getProcessModelVersions().size() > 1)) {
                        ProcessBranch branch = pvid.getProcessBranch();
                        List<ProcessModelVersion> pmvs = pvid.getProcessBranch().getProcessModelVersions();
                        branch.setCurrentProcessModelVersion(getPreviousVersion(pmvs, pvid));
                        branch.getProcessModelVersions().remove(pvid);
                        deleteProcessModelVersion(pvid);
                        processBranchRepo.save(branch);
                    } else {
                        deleteProcessModelVersion(pvid);
                        processRepo.delete(process);
                    }
                }
            } catch (Exception e) {
                String msg = "Failed to delete the current version of the branch " + entry.getValue() + " of the process model " + entry.getValue();
                LOGGER.error(msg, e);
            }
        }
    }

    private ProcessModelVersion getPreviousVersion(List<ProcessModelVersion> pmvs, ProcessModelVersion pvid) {
        ProcessModelVersion result = null;
        for (ProcessModelVersion pmv : pmvs) {
            if (result == null) {
                result = pmv;
            }
            if (pmv.getId() < pvid.getId() && pmv.getId() > result.getId()) {
                result = pmv;
            }
        }
        return result;
    }


    /* Does the processing of ImportProcess. */
    @Transactional(readOnly = false)
    private ProcessModelVersion addProcess(final Process process, final String processName, final Double versionNumber, final String branchName,
            final String created, final String lastUpdated, final CanonisedProcess cpf, NativeType nativeType) throws ImportException {
        if (cpf == null) {
            LOGGER.error("Process " + processName + " Failed to import correctly.");
            throw new ImportException("Process " + processName + " Failed to import correctly.");
        } else if (processRepo.getProcessByNameAndBranchName(processName, branchName) != null) {
            LOGGER.error("Process " + processName + " was found to already exist in the Repository.");
            throw new ImportException("Process " + processName + " was found to already exist in the Repository.");
        }

        Canonical can;
        OperationContext rootFragment;
        ProcessModelVersion pmv;
        try {
            ProcessBranch branch = insertProcessBranch(process, created, lastUpdated, branchName);

            can = converter.convert(cpf.getCpt());
            pmv = createProcessModelVersion(branch, versionNumber, can, cpf.getCpt().getUri());
            pmv.setNativeType(nativeType);
            if (can.getEdges().size() > 0 && can.getNodes().size() > 0) {
                rootFragment = decomposerSrv.decompose(can, pmv);
                if (rootFragment != null) {
                    pmv.setRootFragmentVersion(rootFragment.getCurrentFragment());
                } else {
                    throw new ImportException("The Root Fragment Version can not be NULL. please check logs for other errors!");
                }
            }
        } catch (RepositoryException re) {
            throw new ImportException("Failed to add the process model " + processName, re);
        }
        return pmv;
    }

    /* Update an existing process with some changes. */
    @Transactional(readOnly = false)
    private ProcessModelVersion updateExistingProcess(Integer processId, String processName, String originalBranchName, Double versionNumber,
            Double originalVersionNumber, String lockStatus, CanonisedProcess cpf, NativeType nativeType)  throws RepositoryException {
        Canonical graph;
        OperationContext rootFragment;
        ProcessModelVersion processModelVersion = null;

        if (lockStatus == null || Constants.UNLOCKED.equals(lockStatus)) {
            throw new RepositoryException("Process model " + processName + " is not locked for the updating session.");
        }
        if (processName == null || originalBranchName == null || originalVersionNumber == null) {
            throw new RepositoryException("Process Name, Branch Name and Version Number need to be supplied to update a process model!");
        }

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
                propagateChangesWithLockRelease(pmVersion.getRootFragmentVersion(), rootFragment.getCurrentFragment(),
                        pmVersion.getFragmentVersions(), versionNumber);
                pmVersion.setNativeType(nativeType);
            }

            processModelVersion = processModelVersionRepo.getProcessModelVersion(processId, originalBranchName, versionNumber);
            processModelVersion.getProcessBranch().setCurrentProcessModelVersion(processModelVersion);
            processModelVersion.setOriginalId(cpf.getCpt().getUri());
            processModelVersion.setNumEdges(graph.countEdges());
            processModelVersion.setNumVertices(graph.countVertices());
            processModelVersion.setLockStatus(Constants.NO_LOCK);
            processModelVersion.setNativeType(nativeType);
        } else {
            LOGGER.error("unable to find the Process Model to update.");
        }
        return processModelVersion;
    }


    /* Delete a Process Model */
    @Transactional(readOnly = false)
    private void deleteProcessModelVersion(final ProcessModelVersion pmv) throws ExceptionDao {
        try {
            // Check is the ProcessModelVersion used by any other Branch (Check the sourceProcessModelVersionId column in branch).
            if (processBranchRepo.countProcessModelBeenForked(pmv) > 0) {
                LOGGER.error("There are other branches forked from this Process Model.");
            } else {
                deleteFragmentVersion(pmv.getRootFragmentVersion(), true);
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
            fragmentVersionDagRepo.deleteChildRelationships(fragmentVersion);
            fragmentVersionRepo.delete(fragmentVersion);
            for (FragmentVersion child : children) {
                deleteFragmentVersion(child, false);
            }
        }
    }


    /* Update a list of native process models with this new meta data, */
    private void updateNative(final Native natve, final String processName, final String username, final Double version)
            throws CanoniserException, JAXBException {
        String natType = natve.getNativeType().getNatType();
        InputStream inStr = new ByteArrayInputStream(natve.getContent().getBytes());

        //TODO why is this done here? apromore should not know about native format outside of canonisers
        if (natType.compareTo("XPDL 2.1") == 0) {
            PackageType pakType = StreamUtil.unmarshallXPDL(inStr);
            StreamUtil.copyParam2XPDL(pakType, processName, version.toString(), username, null, null);
            natve.setContent(StreamUtil.marshallXPDL(pakType));
        }
    }

    /* Inserts a new process into the DB. */
    private Process insertProcess(final String processName, final User user, final NativeType nativeType, final String domain,
            final Integer folderId) throws ImportException {
        LOGGER.debug("Executing operation Insert Process");
        Process process = new Process();

        try {
            process.setName(processName);
            process.setUser(user);
            process.setDomain(domain);
            process.setNativeType(nativeType);
            if (folderId != null) {
                process.setFolder(workspaceSrv.getFolder(folderId));
            }

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
        LOGGER.debug("Executing operation Insert Branch");
        ProcessBranch branch = new ProcessBranch();

        try {
            branch.setProcess(process);
            branch.setBranchName(name);
            branch.setCreateDate(created);
            branch.setLastUpdateDate(lastUpdated);

            process.getProcessBranches().add(branch);

            return processBranchRepo.save(branch);
        } catch (Exception ex) {
            LOGGER.error("Importing a Branch Failed: " + ex.toString());
            throw new ImportException(ex);
        }
    }

    private ProcessModelVersion createProcessModelVersion(final ProcessBranch branch, final Double versionNumber,
            final Canonical proModGrap, final String netId) {
        String now = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
        ProcessModelVersion processModel = new ProcessModelVersion();

        processModel.setProcessBranch(branch);
        processModel.setOriginalId(netId);
        processModel.setVersionNumber(versionNumber);
        processModel.setNumEdges(proModGrap.countEdges());
        processModel.setNumVertices(proModGrap.countVertices());
        processModel.setLockStatus(Constants.NO_LOCK);
        processModel.setCreateDate(now);
        processModel.setLastUpdateDate(now);

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

    /* Insert the Objects to the ProcessModel */
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

    /* Insert the Resources to the ProcessModel */
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



    private void propagateToParentsWithLockRelease(FragmentVersion parent, FragmentVersion originalFragment, FragmentVersion updatedFragment,
            Set<FragmentVersion> composingFragments, Double newVersionNumber) throws RepositoryException {
        LOGGER.debug("Propagating - fragment: " + originalFragment + ", parent: " + parent);
        FragmentVersion newParent = createNewFragmentVersionByReplacingChild(parent, originalFragment, updatedFragment);
        composingFragments.add(newParent);
        fillUnchangedDescendants(newParent, updatedFragment, composingFragments);

        List<ProcessModelVersion> usedProcessModels = processModelVersionRepo.getUsedProcessModelVersions(parent);
        for (ProcessModelVersion pmv : usedProcessModels) {
            createNewProcessModelVersion(pmv, newParent, composingFragments, newVersionNumber);
            lService.unlockProcessModelVersion(pmv);
        }
        lService.unlockFragment(parent);

        List<FragmentVersion> nextLockedParents = fragmentVersionRepo.getLockedParentFragments(parent);
        for (FragmentVersion nextParent : nextLockedParents) {
            propagateToParentsWithLockRelease(nextParent, parent, newParent, composingFragments, newVersionNumber);
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

        Map<String, String> childMappings = new HashMap<>();
        Set<FragmentVersionDag> childFragmentVersionDags = fragmentVersion.getChildFragmentVersionDags();
        for (FragmentVersionDag childFragment : childFragmentVersionDags) {
            childMappings.put(childFragment.getPocketId(), childFragment.getChildFragmentVersion().getId().toString());
            if (childFragment.getChildFragmentVersion().equals(oldChildFragmentVersion)) {
                childMappings.put(childFragment.getPocketId(), newChildFragmentVersion.getId().toString());
            }
        }

        return fService.addFragmentVersion(null, childMappings, fragmentVersion.getId().toString(),
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
            Set<FragmentVersion> composingFragments, final Double newVersionNumber) throws RepositoryException {
        try {
            ProcessModelVersion pdo = addProcessModelVersion(pmv.getProcessBranch(), rootFragment, newVersionNumber, 0, 0);
            for (FragmentVersion fragVersion : composingFragments) {
                fragVersion.addProcessModelVersion(pdo);
                pdo.addFragmentVersion(fragVersion);
            }
        } catch (ExceptionDao de) {
            throw new RepositoryException(de);
        }
    }

    /* Did the request ask for the model in the same format as it was originally added? */
    private boolean isRequestForNativeFormat(Integer processId, Double version, String format) {
        ProcessModelVersion pmv = processModelVersionRepo.getCurrentProcessModelVersion(processId, version);
        return pmv.getNativeType() != null && pmv.getNativeType().getNatType().equals(format);
    }

}
