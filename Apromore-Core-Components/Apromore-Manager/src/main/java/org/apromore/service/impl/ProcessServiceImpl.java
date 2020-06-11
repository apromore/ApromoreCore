/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */
package org.apromore.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.activation.DataHandler;
//import javax.annotation.Resource;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;
import org.apromore.aop.Event;
import org.apromore.canoniser.exception.CanoniserException;
import org.apromore.common.ConfigBean;
import org.apromore.common.Constants;
import org.apromore.dao.AnnotationRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.HistoryEnum;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.User;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.exception.RepositoryException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.graph.canonical.Canonical;
import org.apromore.helper.Version;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.SummariesType;
import org.apromore.plugin.process.ProcessPlugin;
import org.apromore.plugin.property.RequestParameterType;
import org.apromore.service.CanoniserService;
import org.apromore.service.FormatService;
import org.apromore.service.LockService;
import org.apromore.service.ProcessService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.AnnotationHelper;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.ProcessData;
import org.apromore.service.search.SearchExpressionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the ProcessService Contract.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private AnnotationRepository annotationRepo;
    private GroupRepository groupRepo;
    private NativeRepository nativeRepo;
    private ProcessBranchRepository processBranchRepo;
    private ProcessRepository processRepo;
    private ProcessModelVersionRepository processModelVersionRepo;
    private GroupProcessRepository groupProcessRepo;
    //private CanonicalConverter converter;
    //private AnnotationService annotationSrv;
    //private CanoniserService canoniserSrv;
    //private LockService lService;
    private UserService userSrv;
    private FormatService formatSrv;
    //private FragmentService fService;
    //private ComposerService composerSrv;
    //private DecomposerService decomposerSrv;
    private UserInterfaceHelper ui;
    private WorkspaceService workspaceSrv;
    private boolean enableCPF;

    @javax.annotation.Resource
    private Set<ProcessPlugin> processPlugins;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param annotationRepo Annotations repository.
     * @param nativeRepo Native Repository.
     * @param processBranchRepo Process Branch Map Repository.
     * @param processRepo Process Repository
     * @param fragmentVersionRepo Fragment Version Repository.
     * @param fragmentVersionDagRepo Fragment Version Dag Repository.
     * @param processModelVersionRepo Process Model Version Repository.
     * @param groupProcessRepo Group-Process Repository
     * @param annotationSrv Annotation Processing Service
     * @param canoniserSrv Canoniser Service.
     * @param lService Lock Service.
     * @param userSrv User Service
     * @param fService Fragment Service
     * @param formatSrv Format Service.
     * @param ui User Interface Helper.
     * @param workspaceService
     */
    @Inject
    public ProcessServiceImpl(final AnnotationRepository annotationRepo,
            final NativeRepository nativeRepo, final GroupRepository groupRepo,
            final ProcessBranchRepository processBranchRepo, ProcessRepository processRepo,
            final ProcessModelVersionRepository processModelVersionRepo, final GroupProcessRepository groupProcessRepo,
            final CanoniserService canoniserSrv, final LockService lService, final UserService userSrv, 
            final FormatService formatSrv, final UserInterfaceHelper ui, final WorkspaceService workspaceService, final ConfigBean config) {
        this.annotationRepo = annotationRepo;
        this.groupRepo = groupRepo;
        this.nativeRepo = nativeRepo;
        this.processBranchRepo = processBranchRepo;
        this.processRepo = processRepo;
        this.processModelVersionRepo = processModelVersionRepo;
        this.groupProcessRepo = groupProcessRepo;
        //this.converter = converter;
        //this.annotationSrv = annotationSrv;
        //this.canoniserSrv = canoniserSrv;
        //this.lService = lService;
        //this.fService = fService;
        this.userSrv = userSrv;
        this.formatSrv = formatSrv;
        //this.composerSrv = composerSrv;
        //this.decomposerSrv = decomposerSrv;
        this.ui = ui;
        this.workspaceSrv = workspaceService;
        this.enableCPF = config.getEnableCPF();
    }

    /**
     * @see org.apromore.service.ProcessService#readProcessSummaries(Integer, String)
     *      {@inheritDoc}
     */
    @Override
    public SummariesType readProcessSummaries(final Integer folderId, final String userRowGuid, final String searchExpression) {
        SummariesType processSummaries = null;

        try {
            processSummaries = ui.buildProcessSummaryList(folderId, userRowGuid,
                SearchExpressionBuilder.buildSearchConditions(searchExpression, "p", "processId", "process"),  // processes
                SearchExpressionBuilder.buildSearchConditions(searchExpression, "l", "logId",     "log"),      // logs
                SearchExpressionBuilder.buildSearchConditions(searchExpression, "f", "folderId",  "folder"));  // folders

        } catch (UnsupportedEncodingException usee) {
            LOGGER.error("Failed to get Process Summaries: " + usee.toString());
        }

        return processSummaries;
    }


    /**
     * @see org.apromore.service.ProcessService#.getBranchName()importProcess(String, Integer, String, Version, String, org.apromore.service.model.CanonisedProcess, String, String, String, String, boolean)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProcessModelVersion importProcess(final String username, final Integer folderId, final String processName,
            final Version version, final String natType, final InputStream nativeStream, final String domain,
            final String documentation, final String created, final String lastUpdate, final boolean publicModel) throws ImportException {
        LOGGER.debug("Executing operation canoniseProcess");

        if (nativeStream == null) {
            LOGGER.error("Process " + processName + " Failed to import correctly.");
            throw new ImportException("Process " + processName + " Failed to import correctly.");
//        } else if ((folderId.equals(0) && processRepo.findUniqueByName(processName) != null) ||
//                (processRepo.findByNameAndFolderId(processName, folderId) != null)) {
//            LOGGER.error("Process " + processName + " was found to already exist in the Repository.");
//            throw new ImportException("Process " + processName + " was found to already exist in the Repository.");
        }

        ProcessModelVersion pmv;
        try {
            User user = userSrv.findUserByLogin(username);
            NativeType nativeType = formatSrv.findNativeType(natType);
            Process process = insertProcess(processName, user, nativeType, domain, folderId, created, publicModel);
            if (process.getId() == null) {
                throw new ImportException("Created New process named \"" + processName + "\", but JPA repository assigned a primary key ID of " + process.getId());
            }

            pmv = addProcess(process, processName, version, Constants.TRUNK_NAME, created, lastUpdate, nativeType);
            LOGGER.info("Process model version: " + pmv);
            formatSrv.storeNative(processName, pmv, created, lastUpdate, user, nativeType, Constants.INITIAL_ANNOTATION, nativeStream);

            workspaceSrv.addProcessToFolder(process.getId(), folderId);
            LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>IMPORT: "+ processName+" "+process.getId());//call when net is change and then save

            notifyProcessPlugins(pmv);  // Notify process plugin providers

        } catch (UserNotFoundException | JAXBException | IOException e) {
            LOGGER.error("Failed to import process {} with native type {}", processName, natType);
            LOGGER.error("Original exception was: ", e);
            throw new ImportException(e);
        }

        return pmv;
    }

    /**
     * Call the {@link ProcessPlugin#processChanged} method of each of the {@link #processPlugins}.
     *
     * Checked exceptions from the plugins are logged, but otherwise disregarded.
     *
     * @param pmv  the changed process model version
     */
    private void notifyProcessPlugins(ProcessModelVersion pmv) {
        LOGGER.debug("Notifying " + processPlugins.size() + " process plugins of change in " + pmv);
        for (ProcessPlugin processPlugin: processPlugins) {
            LOGGER.info("Notifying process plugin " + processPlugin);
            try {
                int id = pmv.getProcessBranch().getProcess().getId();
                String branch = pmv.getProcessBranch().getBranchName();
                Version version = new Version(pmv.getVersionNumber());
                processPlugin.processChanged(id, branch, version);
            } catch (ProcessPlugin.ProcessChangedException e) {
                LOGGER.warn("Process plugin " + processPlugin + " failed to change process", e);
            } catch (Throwable e) {
                LOGGER.error("Failed to notify process plugin", e);
            }
        }
    }

    /**
     * @see org.apromore.service.ProcessService#updateProcess(Integer, String, String, String, Version, Version, org.apromore.dao.model.User, String, org.apromore.dao.model.NativeType, org.apromore.service.model.CanonisedProcess)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    @Event(message = HistoryEnum.UPDATE_PROCESS_MODEL)
    public ProcessModelVersion updateProcess(final Integer processId, final String processName, final String originalBranchName,
            final String newBranchName, final Version versionNumber, final Version originalVersionNumber, final User user, final String lockStatus,
            final NativeType nativeType, final InputStream nativeStream) throws ImportException, RepositoryException {
        ProcessModelVersion pmv;
        // String now = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());

        try {
            if (!StringUtils.equals(originalBranchName, newBranchName)) {
                Process process = processRepo.findOne(processId);
                pmv = addProcess(process, processName, versionNumber, newBranchName, now, now, nativeType);

                notifyProcessPlugins(pmv);  // Notify process plugin providers
                LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>UPDATE: ", processName);//call when net is change and then save

            } else {
                // Perform the update
                if (user == null) {
                    throw new ImportException("Permission to change this model denied.  No user specified.");
                } else if (canUserWriteProcess(user, processId)) {
                    pmv = updateExistingProcess(processId, processName, originalBranchName, versionNumber, originalVersionNumber, lockStatus, nativeStream, nativeType, now);
                    notifyProcessPlugins(pmv);  // Notify process plugin providers
                    LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>UPDATEEXISTINGPROCESS: ", processName);//call when a net is created, change version
                } else {
                    throw new ImportException("Permission to change this model denied.  Try saving as a new branch instead.");
                }
            }

            formatSrv.storeNative(processName, pmv, now, now, user, nativeType, versionNumber.toString(), nativeStream);
        } catch (RepositoryException | JAXBException | IOException e) {
            LOGGER.error("Failed to update process {}", processName);
            LOGGER.error("Original exception was: ", e);
            throw new RepositoryException("Failed to Update process model.", e);
        }

        return pmv;
    }

    /**
     * @param user  a user
     * @param processId  identifier for a process
     * @return whether the <var>user</var> should be allowed to update the process identified by <var>processId</var>
     */
    private boolean canUserWriteProcess(User user, Integer processId) {
        for (GroupProcess gp: groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid())) {
            if (gp.getHasWrite()) {
                 return true;
            }
        }
        return false;
    }

    /**
     * @see org.apromore.service.ProcessService#exportProcess(String, Integer, String, Version, String, String, boolean, java.util.Set)
     * {@inheritDoc}
     */
    @Override
    public ExportFormatResultType exportProcess(final String name, final Integer processId, final String branch, final Version version,
            final String format, final String annName, final boolean withAnn, Set<RequestParameterType<?>> canoniserProperties)
            throws ExportFormatException {
        try {
            // Debug tracing of the authenticated principal
            org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                LOGGER.info("Authentication principal=" + auth.getPrincipal() + " details=" + auth.getDetails() + " thread=" + Thread.currentThread());
            } else {
                LOGGER.info("Authentication is null");
            }

            ExportFormatResultType exportResult = new ExportFormatResultType();

            // Work out if we are looking at the original format or native format for this model.
            if (isRequestForNativeFormat(processId, branch, version, format)) {
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(
                        nativeRepo.getNative(processId, branch, version.toString(), format).getContent(), "text/xml")));
            } else if (isRequestForAnnotationsOnly(format)) {
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(annotationRepo.getAnnotation(processId, branch,
                        version.toString(), AnnotationHelper.getAnnotationName(annName)).getContent(), "text/xml")));
            } else {
                /*
                CanonicalProcessType cpt = getProcessModelVersion(processId, name, branch, version, false);
                Process process;
                if (format.equals(Constants.CANONICAL)) {
                    exportResult.setNative(new DataHandler(new ByteArrayDataSource(canoniserSrv.CPFtoString(cpt), Constants.XML_MIMETYPE)));
                } else {
                    DecanonisedProcess dp;
                    AnnotationsType anf = null;
                    process = processRepo.findOne(processId);
                    if (withAnn) {
                        Annotation ann = annotationRepo.getAnnotation(processId, branch, version.toString(), annName);
                        if (ann != null) {
                            String annotation = ann.getContent();
                            if (annotation != null && !annotation.equals("")) {
                                ByteArrayDataSource dataSource = new ByteArrayDataSource(annotation, Constants.XML_MIMETYPE);
                                anf = ANFSchema.unmarshalAnnotationFormat(dataSource.getInputStream(), false).getValue();
                            }
                        }

                        if (ann != null && !process.getNativeType().getNatType().equalsIgnoreCase(ann.getNatve().getNativeType().getNatType())) {
                            anf = annotationSrv.preProcess(ann.getNatve().getNativeType().getNatType(), format, cpt, anf);
                        } else {
                            anf = annotationSrv.preProcess(process.getNativeType().getNatType(), format, cpt, anf);
                        }
                    } else if (annName == null) {
                        anf = annotationSrv.preProcess(null, format, cpt, null);
                    }

                    dp = canoniserSrv.deCanonise(format, cpt, anf, canoniserProperties);

                    exportResult.setMessage(PluginHelper.convertFromPluginMessages(dp.getMessages()));
                    exportResult.setNative(new DataHandler(new ByteArrayDataSource(dp.getNativeFormat(), Constants.XML_MIMETYPE)));
                }
                */
                LOGGER.error("Failed to export process model {} to format {}", name, format);
                throw new ExportFormatException("Failed to export process model.");
            }

            return exportResult;
        } catch (Exception e) {
            LOGGER.error("Failed to export process model {} to format {}", name, format);
            LOGGER.debug("Original exception was: ", e);
            throw new ExportFormatException(e);
        }
    }


    /**
     * @see org.apromore.service.ProcessService#updateProcessMetaData(Integer, String, String, String, Version, Version, String, boolean)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void updateProcessMetaData(final Integer processId, final String processName, final String domain, final String username,
            final Version preVersion, final Version newVersion, final String ranking, final boolean isPublic) throws UpdateProcessException {
        LOGGER.debug("Executing operation update process meta data.");

        try {
            ProcessModelVersion processModelVersion = processModelVersionRepo.getCurrentProcessModelVersion(processId, preVersion.toString());
            ProcessBranch branch = processModelVersion.getProcessBranch();
            Process process = processRepo.findOne(processId);

            process.setDomain(domain);
            process.setName(processName);
            process.setRanking(ranking);
            process.setUser(userSrv.findUserByLogin(username));
            processModelVersion.setVersionNumber(newVersion.toString());

            Group publicGroup = groupRepo.findPublicGroup();
            if (publicGroup == null) {
                LOGGER.warn("No public group present in repository");

            } else {
                Set<GroupProcess> groupProcesses = process.getGroupProcesses();
                Set<GroupProcess> publicGroupProcesses = filterPublicGroupProcesses(groupProcesses);

                if (publicGroupProcesses.isEmpty() && isPublic) {
                    groupProcesses.add(new GroupProcess(process, publicGroup, true, true, false));
                    process.setGroupProcesses(groupProcesses);

                } else if (!publicGroupProcesses.isEmpty() && !isPublic) {
                    groupProcesses.removeAll(publicGroupProcesses);
                    process.setGroupProcesses(groupProcesses);
                }

                updateNative(processModelVersion.getNativeDocument(), processName, username, newVersion);
                if (isPublic != !publicGroupProcesses.isEmpty()) {
                    if (isPublic) {
                        workspaceSrv.createPublicStatusForUsers(process);
                    } else {
                        workspaceSrv.removePublicStatusForUsers(process);
                    }
                }
            }

            processRepo.save(process);
            processModelVersionRepo.save(processModelVersion);
            processBranchRepo.save(branch);
        } catch (Exception e) {
            throw new UpdateProcessException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public boolean isPublicProcess(Integer processId) {
        return !filterPublicGroupProcesses(processRepo.findUniqueByID(processId).getGroupProcesses()).isEmpty();
    }

    private Set<GroupProcess> filterPublicGroupProcesses(Set<GroupProcess> groupProcesses) {
        Group publicGroup = groupRepo.findPublicGroup();
        if (publicGroup == null) {
            LOGGER.warn("No public group present in repository");
            return Collections.emptySet();
        }

        Set<GroupProcess> publicGroupProcesses = new HashSet<>(); /* groupProcesses
                .stream()
                .filter(groupProcess -> publicGroup.equals(groupProcess.getGroup()))
                .collect(Collectors.toSet());*/
        for (GroupProcess groupProcess: groupProcesses) {
            if (publicGroup.equals(groupProcess.getGroup())) {
                publicGroupProcesses.add(groupProcess);
            }
        }

        return publicGroupProcesses;
    }


    /**
     * @see ProcessService#addProcessModelVersion(ProcessBranch, FragmentVersion, Version, int, int)
     *      {@inheritDoc}
     */
//    @Override
//    @Transactional(readOnly = false)
//    public ProcessModelVersion addProcessModelVersion(final ProcessBranch branch, final FragmentVersion rootFragmentVersion,
//                final Version version, final int numVertices, final int numEdges) throws ExceptionDao {
//        ProcessModelVersion pmv = new ProcessModelVersion();
//
//        pmv.setProcessBranch(branch);
//        pmv.setRootFragmentVersion(rootFragmentVersion);
//        pmv.setVersionNumber(version.toString());
//        pmv.setNumVertices(numVertices);
//        pmv.setNumEdges(numEdges);
//        pmv.setCreateDate(SimpleDateFormat.getDateInstance().format(new Date()));
//        pmv.setLastUpdateDate(pmv.getCreateDate());
//
//        return processModelVersionRepo.save(pmv);
//    }




    /**
     * @see ProcessService#getCanonicalFormat(org.apromore.dao.model.ProcessModelVersion)
     * {@inheritDoc}
     */
    /*
    @Override
    @Transactional(readOnly = false)
    public CanonicalProcessType getCanonicalFormat(final ProcessModelVersion pmv) {
        String processName = pmv.getProcessBranch().getProcess().getName();
        String branchName = pmv.getProcessBranch().getBranchName();
        return getCanonicalFormat(pmv, processName, branchName, false);
    }
    */

    /**
     * @see ProcessService#getCanonicalFormat(org.apromore.dao.model.ProcessModelVersion)
     * {@inheritDoc}
     */
    /*
    @Override
    @Transactional(readOnly = false)
    public Canonical getCanonicalFormat(Integer processId, String branchName, String versionNumber) {
        ProcessModelVersion pmv = processModelVersionRepo.getProcessModelVersion(processId, branchName, versionNumber);
        CanonicalProcessType cpf = getCanonicalFormat(pmv);
        return converter.convert(cpf);
    }
    */

    /**
     * @see ProcessService#getCanonicalFormat(ProcessModelVersion, String, String, boolean)
     * {@inheritDoc}
     */
    /*
    @Override
    @Transactional(readOnly = false)
    public CanonicalProcessType getCanonicalFormat(final ProcessModelVersion pmv, final String processName, final String branchName, final boolean lock) {
        Canonical canonical;
        CanonicalProcessType tmp = new CanonicalProcessType();
        try {
            canonical = composerSrv.compose(pmv.getRootFragmentVersion());
            //canonical = pmv.getCanonicalDocument();
            canonical.setProperty(Constants.PROCESS_NAME, processName);
            canonical.setProperty(Constants.BRANCH_NAME, branchName);
            canonical.setProperty(Constants.BRANCH_ID, pmv.getProcessBranch().getId().toString());
            canonical.setProperty(Constants.VERSION_NUMBER, pmv.getVersionNumber());
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
            Map<String, Variants.Variant> variantMap = new HashMap<>();
            for (ProcessModelAttribute attribute: pmv.getProcessModelAttributes()) {
                Element extensionElements = XMLUtils.stringToAnyElement(attribute.getAny());
                if (extensionElements != null) {
                    java.lang.Object o = JAXBContext.newInstance(org.apromore.cpf.ObjectFactory.class,
                                                                 com.processconfiguration.ObjectFactory.class,
                                                                 com.signavio.ObjectFactory.class)
                                                    .createUnmarshaller()
                                                    .unmarshal(extensionElements);
                    canonical.setProperty(attribute.getName(), attribute.getValue(), o);

                    // Populate a map from variant IDs to the actual JAXB Variant objects
                    if (o instanceof Variants) {
                        Variants variants = (Variants) o;
                        for (Variants.Variant variant: variants.getVariant()) {
                            variantMap.put(variant.getId(), variant);
                        }
                    } else {
                        LOGGER.warn("Ignoring extension element " + o);
                    }
                } else {
                    canonical.setProperty(attribute.getName(), attribute.getValue(), extensionElements);
                }
            }

            // Kludge to work around limitations in JAXB, populating configurationAnnotation/configuration@variantRef ID references
            for (Map.Entry entry: canonical.variantMap.entrySet()) {
                ((ConfigurationAnnotation.Configuration) entry.getKey()).setVariantRef(variantMap.get(entry.getValue()));
            }

            tmp = converter.convert(canonical);
        } catch (ExceptionDao | JAXBException e) {
            String msg = "Failed to retrieve the current version of the process model " + processName + " - " + branchName;
            LOGGER.error(msg, e);
        }
        return tmp;
    }
    */

    /**
     * @see ProcessService#getCurrentProcessModel(String, String, boolean)
     * {@inheritDoc}
     */
    /*
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
    */

    /**
     * @see ProcessService#getProcessModelVersion(Integer, String, String, Version, boolean)
     * {@inheritDoc}
     */
    /*
    @Override
    @Transactional(readOnly = false)
    public CanonicalProcessType getProcessModelVersion(final Integer processId, final String processName, final String branchName,
            final Version version, final boolean lock) throws LockFailedException {
        ProcessModelVersion pmv = null;

        if (version != null) {
            pmv = processModelVersionRepo.getProcessModelVersion(processId, branchName, version.toString());
        }

        if (pmv == null) {
            pmv = processModelVersionRepo.getLatestProcessModelVersion(processId, "MAIN");
        }
        if (lock) {
            boolean locked = lService.lockFragment(pmv.getRootFragmentVersion().getId());
            if (!locked) {
                throw new LockFailedException();
            }
        }

        return getCanonicalFormat(pmv, processName, branchName, lock);
    }
    */


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
    /*
    @Override
    @Transactional(readOnly = false)
    public void propagateChangesWithLockRelease(final FragmentVersion originalFragment, final FragmentVersion updatedFragment,
            final Set<FragmentVersion> composingFragments, final Version newVersionNumber) throws RepositoryException {
        // create new versions for all process models, which use this fragment as the root fragment, and unlock those process models.
        List<ProcessModelVersion> usedProcessModels = processModelVersionRepo.getUsedProcessModelVersions(originalFragment);
        for (ProcessModelVersion pmv : usedProcessModels) {
            createNewProcessModelVersion(pmv, updatedFragment, composingFragments, newVersionNumber);
        }

        if (originalFragment != null) {
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
    }
    */



    /**
     * @see ProcessService#deleteProcessModel(java.util.List)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void deleteProcessModel(final List<ProcessData> models, final User user) throws UpdateProcessException {
        for (ProcessData entry : models) {
            ProcessModelVersion pvid = processModelVersionRepo.getCurrentProcessModelVersion(entry.getId(), entry.getVersionNumber().toString());

            if (pvid != null) {
                Process process = pvid.getProcessBranch().getProcess();
                if (!canUserWriteProcess(user, process.getId())) {
                    throw new UpdateProcessException("Write permission denied for " + user.getUsername());
                }
                List<ProcessBranch> branches = process.getProcessBranches();

                LOGGER.debug("Retrieving the Process Model of the current version of " + process.getName() + " to be deleted.");

                try {
                    // Only delete the version selected, but if there is only a single version then remove all of the process
                    if (branches.size() > 1 || (branches.size() == 1 && pvid.getProcessBranch().getProcessModelVersions().size() > 1)) {
                        ProcessBranch branch = pvid.getProcessBranch();
                        List<ProcessModelVersion> pmvs = pvid.getProcessBranch().getProcessModelVersions();
                        updateBranch(pmvs, pvid, branch);
                    } else {
                        deleteProcessModelVersion(pvid);
                        processRepo.delete(process);
                    }
                } catch (ExceptionDao e) {
                    throw new UpdateProcessException("Unable to modify " + process.getName(), e);
                }

                // Also delete the PQL index for this process model version
                //notifyDelete(pvid);

                // Notify process plugin providers
                notifyProcessPlugins(pvid);
            }
/*
            } catch (Exception e) {
                String msg = "Failed to delete the current version of the Process with id: " + entry.getId();
                LOGGER.error(msg, e);
            }
*/
        }
    }


    /**
     * @see org.apromore.service.ProcessService#getBPMNRepresentation(String, Integer, String, Version)
     * {@inheritDoc}
     */
    @Override
    public String getBPMNRepresentation(final String name, final Integer processId, final String branch, final Version version) throws RepositoryException {
        String xmlBPMNProcess;
        String format = "BPMN 2.0";
        String annName = "BPMN 2.0";

        try {
            // Debug tracing of the authenticated principal
            org.springframework.security.core.Authentication auth =
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                LOGGER.info("Authentication principal=" + auth.getPrincipal() + " details=" + auth.getDetails() + " thread=" + Thread.currentThread());
            } else {
                LOGGER.info("Authentication is null");
            }

            // Work out if we are looking at the original format or native format for this model.
            if (isRequestForNativeFormat(processId, branch, version, format)) {
                xmlBPMNProcess = nativeRepo.getNative(processId, branch, version.toString(), format).getContent();
                LOGGER.info("native");
            } else {
                /*
                LOGGER.info("notNative");
                CanonicalProcessType cpt = getProcessModelVersion(processId, name, branch, version, false);
                Process process = processRepo.findOne(processId);
                DecanonisedProcess dp;
                AnnotationsType anf = null;

                Annotation ann = annotationRepo.getAnnotation(processId, branch, version.toString(), annName);
                if (ann != null) {
                    String annotation = ann.getContent();
                    if (annotation != null && !annotation.equals("")) {
                        ByteArrayDataSource dataSource = new ByteArrayDataSource(annotation, Constants.XML_MIMETYPE);
                        anf = ANFSchema.unmarshalAnnotationFormat(dataSource.getInputStream(), false).getValue();
                    }
                }

                if (ann != null && !process.getNativeType().getNatType().equalsIgnoreCase(ann.getNatve().getNativeType().getNatType())) {
                    anf = annotationSrv.preProcess(ann.getNatve().getNativeType().getNatType(), format, cpt, anf);
                } else {
                    anf = annotationSrv.preProcess(process.getNativeType().getNatType(), format, cpt, anf);
                }
                dp = canoniserSrv.deCanonise(format, cpt, anf, new HashSet<RequestParameterType<?>>());
                xmlBPMNProcess = IOUtils.toString(dp.getNativeFormat(), "UTF-8");
                */
                LOGGER.error("Not supported to retrieve the canonical format");
                throw new RepositoryException("Not supported to retrieve the canonical format");
            }

            //LOGGER.info("[new method] PROCESS:\n" + xmlBPMNProcess);
            return xmlBPMNProcess;

        } catch (Exception e) {
            LOGGER.error("Failed to retrive the process!");
            LOGGER.error("Original exception was: ", e);
            throw new RepositoryException(e);
        }
    }

    private void updateBranch(List<ProcessModelVersion> pmvs, ProcessModelVersion pvid, ProcessBranch branch) throws ExceptionDao {
        ProcessModelVersion newCurrent = getPreviousVersion(pmvs, pvid);
        if (newCurrent == null) {
            newCurrent = getNextVersion(pmvs, pvid);
        }
        branch.setCurrentProcessModelVersion(newCurrent);
        branch.getProcessModelVersions().remove(pvid);
        processBranchRepo.save(branch);

        deleteProcessModelVersion(pvid);
    }

    private ProcessModelVersion getPreviousVersion(List<ProcessModelVersion> pmvs, ProcessModelVersion pvid) {
        ProcessModelVersion result = null;
        for (ProcessModelVersion pmv : pmvs) {
            if (pmv.equals(pvid)) {
                continue;
            }
            if (result == null) {
                result = pmv;
            }
            if (pmv.getId() < pvid.getId() && pmv.getId() > result.getId()) {
                result = pmv;
            }
        }
        return result;
    }

    private ProcessModelVersion getNextVersion(List<ProcessModelVersion> pmvs, ProcessModelVersion pvid) {
        ProcessModelVersion result = null;
        for (ProcessModelVersion pmv : pmvs) {
            if (pmv.equals(pvid)) {
                continue;
            }
            if (result == null) {
                result = pmv;
            }
            if (pmv.getId() > pvid.getId() && pmv.getId() < result.getId()) {
                result = pmv;
            }
        }
        return result;
    }


    /* Does the processing of ImportProcess. */
    @Transactional(readOnly = false)
    private ProcessModelVersion addProcess(final Process process, final String processName, final Version version, final String branchName,
            final String created, final String lastUpdated, NativeType nativeType) throws ImportException {
        ProcessModelVersion pmv;
        ProcessBranch branch = insertProcessBranch(process, created, lastUpdated, branchName);

//        if (enableCPF) {
//            Canonical can = converter.convert(cpf.getCpt());
//            pmv = createProcessModelVersion(branch, version, nativeType, can, cpf.getCpt().getUri());
//            try {
//                if (can.getEdges().size() > 0 && can.getNodes().size() > 0) {
//                    OperationContext rootFragment = decomposerSrv.decompose(can, pmv);
//                    if (rootFragment != null) {
//                        pmv.setRootFragmentVersion(rootFragment.getCurrentFragment());
//                    } else {
//                        throw new ImportException("The Root Fragment Version can not be NULL. please check logs for other errors!");
//                    }
//                }
//            } catch (RepositoryException re) {
//                throw new ImportException("Failed to add the process model " + processName, re);
//            }
//        } else {
//            pmv = createProcessModelVersion(branch, version, nativeType, null, null);
//        }
        
        pmv = createProcessModelVersion(branch, version, nativeType, null, null);

        return pmv;
    }

    /* Update an existing process with some changes. */
    @Transactional(readOnly = false)
    private ProcessModelVersion updateExistingProcess(Integer processId, String processName, String originalBranchName, Version version,
            Version originalVersionNumber, String lockStatus, InputStream nativeStream, NativeType nativeType, String lastUpdate)  throws RepositoryException {
        if (lockStatus == null || Constants.UNLOCKED.equals(lockStatus)) {
            throw new RepositoryException("Process model " + processName + " is not locked for the updating session.");
        }
        if (processName == null || originalBranchName == null || originalVersionNumber == null) {
            throw new RepositoryException("Process Name, Branch Name and Version Number need to be supplied to update a process model!");
        }

        ProcessModelVersion pmVersion = processModelVersionRepo.getProcessModelVersion(processId, originalBranchName,
                originalVersionNumber.toString());
        if (pmVersion != null) {
            if (version.toString().equals(pmVersion.getVersionNumber())) {
                String message = "CONFLICT! The process model " + processName + " - " + originalBranchName + " has been updated by another user." +
                        "\nThis process model version number: " + version + "\nCurrent process model version number: " +
                        pmVersion.getVersionNumber();
                LOGGER.error(message);
                throw new RepositoryException(message);
            }
            else {
                ProcessModelVersion pmv = createProcessModelVersion(pmVersion.getProcessBranch(), version, nativeType, null, null);
                return pmv;
            }

            /*
            graph = converter.convert(cpf.getCpt());
            rootFragment = decomposerSrv.decompose(graph, pmVersion);
            if (rootFragment != null) {
                propagateChangesWithLockRelease(pmVersion.getRootFragmentVersion(), rootFragment.getCurrentFragment(),
                        pmVersion.getFragmentVersions(), version);
            }

            processModelVersion = processModelVersionRepo.getProcessModelVersion(processId, originalBranchName, version.toString());
            if (processModelVersion.getRootFragmentVersion() == null) {
                assert rootFragment != null;
                processModelVersion.setRootFragmentVersion(rootFragment.getCurrentFragment());
            }
            processModelVersion.getProcessBranch().setCurrentProcessModelVersion(processModelVersion);
            processModelVersion.setOriginalId(cpf.getCpt().getUri());
            processModelVersion.setNumEdges(graph.countEdges());
            processModelVersion.setNumVertices(graph.countVertices());
            processModelVersion.setLockStatus(Constants.NO_LOCK);
            processModelVersion.setNativeType(nativeType);
            processModelVersion.setLastUpdateDate(lastUpdate);
            */
        } else {
            LOGGER.error("Unable to find the Process Model to update. Id=" + processId + ", name=" + processName);
            throw new RepositoryException("Unable to find the Process Model to update. Id=" + processId + ", name=" + processName);
        }
        //return processModelVersion;
    }


    /* Delete a Process Model */
    @Transactional(readOnly = false)
    private void deleteProcessModelVersion(final ProcessModelVersion pmv) throws ExceptionDao {
        try {
            // Check is the ProcessModelVersion used by any other Branch (Check the sourceProcessModelVersionId column in branch).
            if (processBranchRepo.countProcessModelBeenForked(pmv) > 0) {
                throw new Exception("There are other branches forked from this Process Model.");
            } else {
                //deleteFragmentVersion(pmv.getRootFragmentVersion(), true);
            }
        } catch (Exception e) {
            String msg = "Failed to delete the process model version " + pmv.getId();
            LOGGER.error(msg, e);
            throw new ExceptionDao(msg, e);
        }
    }

//    /* Delete a Fragment Version from the Database. Check if it is used by other PMV or FV first. */
//    private void deleteFragmentVersion(FragmentVersion fragmentVersion, boolean rootFragmentVersion) {
//        long processCount = processModelVersionRepo.countFragmentUsesInProcessModels(fragmentVersion);
//        long fragmentCount =  fragmentVersionRepo.countFragmentUsesInFragmentVersions(fragmentVersion);
//        if ((rootFragmentVersion && processCount == 1 && fragmentCount == 0) ||
//                (!rootFragmentVersion && processCount == 0 && fragmentCount == 0)) {
//            List<FragmentVersion> children = fragmentVersionRepo.getChildFragmentsByFragmentVersion(fragmentVersion);
//            fragmentVersionDagRepo.deleteChildRelationships(fragmentVersion);
//            fragmentVersionRepo.delete(fragmentVersion);
//            for (FragmentVersion child : children) {
//                deleteFragmentVersion(child, false);
//            }
//        }
//    }


    /* Update a list of native process models with this new meta data, */
    private void updateNative(final Native natve, final String processName, final String username, final Version version)
            throws CanoniserException, JAXBException {
        if (natve != null) {
            String natType = natve.getNativeType().getNatType();
            InputStream inStr = new ByteArrayInputStream(natve.getContent().getBytes());
        }
    }

    /* Inserts a new process into the DB. */
    private Process insertProcess(final String processName, final User user, final NativeType nativeType, final String domain,
            final Integer folderId, final String created, final boolean publicModel) throws ImportException {
        LOGGER.debug("Executing operation Insert Process");
        Process process = new Process();

        try {
            process.setName(processName);
            process.setUser(user);
            process.setDomain(domain);
            process.setNativeType(nativeType);
            process.setCreateDate(created);
            if (folderId != null) {
                process.setFolder(workspaceSrv.getFolder(folderId));
            }

            Set<GroupProcess> groupProcesses = process.getGroupProcesses();

            // Add the user's personal group
            groupProcesses.add(new GroupProcess(process, user.getGroup(), true, true, true));
            process.setGroupProcesses(groupProcesses);

            process = processRepo.save(process);

            // TODO: kludging past a cascade issue by adding the public group as a second DB commit
            //       really should figure out how to do this in one commit, as per the preceding commented-out code

            groupProcesses = process.getGroupProcesses();

            // Add the public group
            if (publicModel) {
                Group publicGroup = groupRepo.findPublicGroup();
                if (publicGroup == null) {
                    LOGGER.warn("No public group present in repository");
                } else {
                    groupProcesses.add(new GroupProcess(process, publicGroup, true, true, false));
                }
            }

            process.setGroupProcesses(groupProcesses);

            process = processRepo.saveAndFlush(process);

            return process;
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

    private ProcessModelVersion createProcessModelVersion(final ProcessBranch branch, final Version version, NativeType nativeType,
            final Canonical proModGrap, final String netId) {
        String now = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
        ProcessModelVersion processModel = new ProcessModelVersion();

        processModel.setProcessBranch(branch);
        processModel.setOriginalId(netId);
        processModel.setVersionNumber(version.toString());
        if (enableCPF) {
//            processModel.setNumEdges(proModGrap.countEdges());
//            processModel.setNumVertices(proModGrap.countVertices());
        }
        processModel.setNumEdges(0);
        processModel.setNumVertices(0);
        processModel.setLockStatus(Constants.NO_LOCK);
        processModel.setCreateDate(now);
        processModel.setLastUpdateDate(now);
        processModel.setNativeType(nativeType);

        branch.setCurrentProcessModelVersion(processModel);

        if (enableCPF) {
//            addAttributesToProcessModel(proModGrap, processModel);
//            addObjectsToProcessModel(proModGrap, processModel);
//            addResourcesToProcessModel(proModGrap, processModel);
//            updateResourcesOnProcessModel(proModGrap.getResources(), processModel);
        }

        return processModelVersionRepo.save(processModel);
    }


    /* Insert the Attributes to the ProcessModel */

//    private void addAttributesToProcessModel(final Canonical proModGrap, final ProcessModelVersion process) {
//        for (Map.Entry<String, IAttribute> obj : proModGrap.getProperties().entrySet()) {
//            ProcessModelAttribute pmvAtt = new ProcessModelAttribute();
//            pmvAtt.setName(obj.getKey());
//            pmvAtt.setValue(obj.getValue().getValue());
//            java.lang.Object any = obj.getValue().getAny();
//            if (any instanceof Element) {
//                pmvAtt.setAny(XMLUtils.anyElementToString((Element) any));
//            }
//            else if (any instanceof Variants) {
//                String s = XMLUtils.extensionElementToString(any);
//                LOGGER.info("Variants any=" + any + " string=" + s);
//                pmvAtt.setAny(s);
//            }
//            else if (any != null) {
//                throw new IllegalArgumentException("Parsed an unsupported extension: " + any);
//            }
//            pmvAtt.setProcessModelVersion(process);
//            process.getProcessModelAttributes().add(pmvAtt);
//        }
//    }
//
//    /* Insert the Objects to the ProcessModel */
//    private void addObjectsToProcessModel(final Canonical proModGrap, final ProcessModelVersion process) {
//        Object objTyp;
//        if (proModGrap.getObjects() != null) {
//            for (ICPFObject cpfObj : proModGrap.getObjects()) {
//                objTyp = new org.apromore.dao.model.Object();
//                objTyp.setUri(cpfObj.getId());
//                objTyp.setName(cpfObj.getName());
//                objTyp.setNetId(cpfObj.getNetId());
//                objTyp.setConfigurable(cpfObj.isConfigurable());
//                objTyp.setProcessModelVersion(process);
//                if (cpfObj.getObjectType().equals(ObjectTypeEnum.HARD)) {
//                    objTyp.setType(ObjectTypeEnum.HARD);
//                } else {
//                    objTyp.setType(ObjectTypeEnum.SOFT);
//                    objTyp.setSoftType(cpfObj.getSoftType());
//                }
//
//                addObjectAttributes(objTyp, cpfObj);
//
//                process.getObjects().add(objTyp);
//            }
//        }
//    }
//
//    /* Add Attributes to the Object Reference. */
//    private void addObjectAttributes(final org.apromore.dao.model.Object object, final ICPFObject cpfObject) {
//        ObjectAttribute objAtt;
//        for (Map.Entry<String, IAttribute> e : cpfObject.getAttributes().entrySet()) {
//            objAtt = new ObjectAttribute();
//            objAtt.setName(e.getKey());
//            objAtt.setValue(e.getValue().getValue());
//            if (e.getValue().getAny() instanceof Element) {
//                objAtt.setAny(XMLUtils.anyElementToString((Element) e.getValue().getAny()));
//            }
//            objAtt.setObject(object);
//
//            object.getObjectAttributes().add(objAtt);
//        }
//    }
//
//    /* Insert the Resources to the ProcessModel */
//    private void addResourcesToProcessModel(final Canonical proModGrap, final ProcessModelVersion process) {
//        Resource resTyp;
//        if (proModGrap.getResources() != null) {
//            for (ICPFResource cpfRes : proModGrap.getResources()) {
//                resTyp = new Resource();
//                resTyp.setUri(cpfRes.getId());
//                resTyp.setName(cpfRes.getName());
//                resTyp.setOriginalId(cpfRes.getOriginalId());
//                resTyp.setConfigurable(cpfRes.isConfigurable());
//                if (cpfRes.getResourceType() != null) {
//                    if (cpfRes.getResourceType().equals(ResourceTypeEnum.HUMAN)) {
//                        resTyp.setType(ResourceTypeEnum.HUMAN);
//                        if (cpfRes.getHumanType() != null) {
//                            resTyp.setTypeName(cpfRes.getHumanType().value());
//                        }
//                    } else {
//                        resTyp.setType(ResourceTypeEnum.NONHUMAN);
//                        if (cpfRes.getNonHumanType() != null) {
//                            resTyp.setTypeName(cpfRes.getNonHumanType().value());
//                        }
//                    }
//                }
//                resTyp.setProcessModelVersion(process);
//
//                addResourceAttributes(resTyp, cpfRes);
//
//                process.getResources().add(resTyp);
//            }
//        }
//    }
//
//    /* Add Attributes to the Object Reference. */
//    private void addResourceAttributes(final Resource resource, final ICPFResource cpfResource) {
//        ResourceAttribute resAtt;
//        for (Map.Entry<String, IAttribute> e : cpfResource.getAttributes().entrySet()) {
//            resAtt = new ResourceAttribute();
//            resAtt.setName(e.getKey());
//            resAtt.setValue(e.getValue().getValue());
//            if (e.getValue().getAny() instanceof Element) {
//                resAtt.setAny(XMLUtils.anyElementToString((Element) e.getValue().getAny()));
//            }
//            resAtt.setResource(resource);
//
//            resource.getResourceAttributes().add(resAtt);
//        }
//    }
//
//    /* Update to Process Models Resource information, specifically the Specialisation Id's. This can't be done at
//       time of original entry as we might not have all the Resources in memory. */
//    private void updateResourcesOnProcessModel(Set<ICPFResource> resources, ProcessModelVersion processModel) {
//        if (resources != null) {
//            for (ICPFResource cpfRes : resources) {
//                addSpecialisations(processModel, cpfRes, findResource(processModel.getResources(), cpfRes.getId()));
//            }
//        }
//    }
//
//    /* Update the Resource with it's specialisations Ids. */
//    private void addSpecialisations(ProcessModelVersion processModel, ICPFResource cpfRes, Resource resource) {
//        if (cpfRes.getSpecializationIds() != null) {
//            for (String resourceId : cpfRes.getSpecializationIds()) {
//                resource.getSpecialisations().add(findResource(processModel.getResources(), resourceId));
//            }
//        }
//    }
//
//
//    /* Finds the Resource using the resource Id supplied. */
//    private Resource findResource(Set<Resource> resources, String resourceId) {
//        Resource found = null;
//        for (Resource resource : resources) {
//            if (resource.getUri().equals(resourceId)) {
//                found = resource;
//                break;
//            }
//        }
//        if (found == null) {
//            LOGGER.warn("Could not find Resource with Id: " + resourceId);
//        }
//        return found;
//    }


    /*
    private void propagateToParentsWithLockRelease(FragmentVersion parent, FragmentVersion originalFragment, FragmentVersion updatedFragment,
            Set<FragmentVersion> composingFragments, Version newVersionNumber) throws RepositoryException {
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
    */

    /*
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
    */

    /*
    private void createNewProcessModelVersion(ProcessModelVersion pmv, FragmentVersion rootFragment,
            Set<FragmentVersion> composingFragments, final Version newVersionNumber) throws RepositoryException {
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
    */

    /* Did the request ask for the model in the same format as it was originally added? */
    private boolean isRequestForNativeFormat(Integer processId, String branch, Version version, String format) {
        ProcessModelVersion pmv = processModelVersionRepo.getProcessModelVersion(processId, branch, version.toString());
        return pmv != null && pmv.getNativeType() != null && pmv.getNativeType().getNatType().equals(format);
    }

    /* Did the request ask for the Annotations for this model without the actual model? */
    private boolean isRequestForAnnotationsOnly(String format) {
        return format.startsWith("Annotations") ;
    }
}
