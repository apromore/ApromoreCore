/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2015, 2016 Adriano Augusto.
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
 * %%
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

import org.apromore.aop.Event;
import org.apromore.aop.HistoryEnum;
import org.apromore.common.ConfigBean;
import org.apromore.common.Constants;
import org.apromore.dao.*;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.*;
import org.apromore.exception.*;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.service.*;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.ProcessData;
import org.apromore.util.AccessType;
import org.apromore.util.StreamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.activation.DataHandler;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Implementation of the ProcessService Contract.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class ProcessServiceImpl implements ProcessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private GroupRepository groupRepo;
    private NativeRepository nativeRepo;
    private ProcessBranchRepository processBranchRepo;
    private ProcessRepository processRepo;
    private ProcessModelVersionRepository processModelVersionRepo;
    private GroupProcessRepository groupProcessRepo;
    private UserService userSrv;
    private FormatService formatSrv;
    private UserInterfaceHelper ui;
    private WorkspaceService workspaceSrv;
    private AuthorizationService authorizationService;
    private FolderRepository folderRepository;

    private boolean sanitizationEnabled;

    /**
     *
     * @param nativeRepo Native Repository
     * @param groupRepo Group Repository
     * @param processBranchRepo Process Branch Map Repository
     * @param processRepo Process Repository
     * @param processModelVersionRepo Process Model Version Repository
     * @param groupProcessRepo Group-Process Repository
     * @param lService Lock Service
     * @param userSrv User Service
     * @param formatSrv Format Service
     * @param ui User Interface Helper
     * @param workspaceService Workspace Service
     * @param authorizationService Authorization Service
     * @param folderRepository Folder Repository
     * @param config Config
     */
    @Inject
    public ProcessServiceImpl(final NativeRepository nativeRepo, final GroupRepository groupRepo,
                              final ProcessBranchRepository processBranchRepo, ProcessRepository processRepo,
                              final ProcessModelVersionRepository processModelVersionRepo,
                              final GroupProcessRepository groupProcessRepo,
                              final LockService lService, final UserService userSrv,
                              final FormatService formatSrv, final UserInterfaceHelper ui,
                              final WorkspaceService workspaceService,
                              final AuthorizationService authorizationService,
                              final FolderRepository folderRepository, final ConfigBean config) {
        this.groupRepo = groupRepo;
        this.nativeRepo = nativeRepo;
        this.processBranchRepo = processBranchRepo;
        this.processRepo = processRepo;
        this.processModelVersionRepo = processModelVersionRepo;
        this.groupProcessRepo = groupProcessRepo;
        this.userSrv = userSrv;
        this.formatSrv = formatSrv;
        this.ui = ui;
        this.workspaceSrv = workspaceService;
        this.authorizationService = authorizationService;
        this.folderRepository = folderRepository;

        this.sanitizationEnabled = config.isSanitizationEnabled();
    }


    /**
     * @see org.apromore.service.ProcessService#importProcess(String, Integer, String, Version, String, InputStream, String, String, String, String, boolean)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public ProcessModelVersion importProcess(final String username, final Integer folderId, final String processName,
            final Version version, final String natType, final InputStream nativeStream, final String domain,
            final String documentation, final String created, final String lastUpdate, final boolean publicModel) throws ImportException {
        LOGGER.debug("Executing operation canoniseProcess");

        if (nativeStream == null) {
            LOGGER.error("Process \"{}\" failed to import correctly.", processName);
            throw new ImportException("Process " + processName + " failed to import correctly.");
        }

        ProcessModelVersion pmv;
        try {
            User user = userSrv.findUserByLogin(username);
            NativeType nativeType = formatSrv.findNativeType(natType);

            // Apply data sanitization measures to the uploaded document if configured to do so.
            InputStream sanitizedStream = sanitizationEnabled ? sanitize(nativeStream, nativeType) : nativeStream;
            assert sanitizedStream != null;

            Folder folder = folderRepository.findUniqueByID(folderId);

            if (folder != null) {
                AccessType accessType = authorizationService.getFolderAccessTypeByUser(folderId, user);

                // If user is not the owner of specified folder, then put process in user's home folder
                if (accessType != AccessType.OWNER) {
                    folder = null;
                }
            }
            Integer actualFolderId = folder == null ? 0 : folder.getId();

            Process process = insertProcess(processName, user, nativeType, domain, actualFolderId, created, publicModel);
            if (process.getId() == null) {
                throw new ImportException("Created New process named \"" + processName + "\", but JPA repository assigned a primary key ID of " + process.getId());
            }

            Native nat = formatSrv.storeNative(processName, created, lastUpdate, user, nativeType, Constants.INITIAL_ANNOTATION, sanitizedStream);
            pmv = addProcessModelVersion(process, processName, version, Constants.TRUNK_NAME, created, lastUpdate, nativeType,nat);
            LOGGER.debug("Process model version: {}", pmv);

            workspaceSrv.addProcessToFolder(user, process.getId(), actualFolderId);
            LOGGER.info("Import process model \"{}\" (id {})", processName, process.getId());//call when net is change and then save

        } catch (ImportException e) {
            throw e;

        } catch (Exception e) {
            LOGGER.error("Failed to import process \"{}\" (native type {})", processName, natType);
            LOGGER.error("Original exception was: ", e);
            throw new ImportException(e);
        }

        return pmv;
    }

    private InputStream sanitize(InputStream in, NativeType nativeType) throws Exception {

        if (nativeType == null) {
            throw new ImportException("Unsupported process model format");
        }

        switch (nativeType.getNatType()) {
        case "BPMN 2.0":
            return sanitizeBPMN(in);

        default:
            throw new ImportException("Unsupported process model format: " + nativeType.getNatType());
        }
    }

    /**
     * Filter a BPMN XML stream to sanitize it.
     *
     * This implementation picks out BPMN elements with complex content (i.e. capable of
     * representing <script> tags) and flattens them into simple text.
     *
     * @param in  a BPMN XML document in UTF-8
     * @return a sanitized version of the same BPMN XML document, also in UTF-8
     */
    public static InputStream sanitizeBPMN(final InputStream in) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TransformerFactory.newInstance()
                          .newTransformer(new StreamSource(ProcessServiceImpl.class.getClassLoader().getResourceAsStream("xsd/sanitizeBPMN.xsl")))
                          .transform(new StreamSource(in), new StreamResult(out));

        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Update an existing process model version
     */
    @Override
    @Transactional(readOnly = false)
    @Event(message = HistoryEnum.UPDATE_PROCESS_MODEL)
    public ProcessModelVersion updateProcessModelVersion(final Integer processId, final String branchName, 
            final Version version, final User user, final String lockStatus,
            final NativeType nativeType, final InputStream nativeStream) throws ImportException, RepositoryException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());
        Process process = processRepo.findOne(processId);
        String processName = process.getName();

        try {
            if (user == null) {
                throw new ImportException("Permission to change this model denied.  No user specified.");
            } 
            else if (!canUserWriteProcess(user, processId)) {
                throw new ImportException("Permission to change this model denied.");
            } 
            else {
                ProcessModelVersion pmv = processModelVersionRepo.getProcessModelVersion(processId, branchName, version.toString());
                if (pmv != null) {
                    pmv.setLastUpdateDate(now);
                    pmv.getNativeDocument().setContent(StreamUtil.inputStream2String(nativeStream).trim());
                    pmv.getNativeDocument().setLastUpdateDate(now);
                    processModelVersionRepo.save(pmv);
                    LOGGER.info("Updated existing process model \"{}\"", processName);
                    return pmv;

                } else {
                    LOGGER.error("Unable to find the Process Model to update. Id=" + processId + ", name=" + processName 
                            + ", branch=" + branchName + ", current version=" + version.toString());
                    throw new RepositoryException("Unable to find the Process Model to update. Id=" + processId + ", name=" + processName 
                            + ", branch=" + branchName + ", current version=" + version.toString());
                }
            }
        } catch (RepositoryException e) {
            LOGGER.error("Failed to update process {}", processName);
            LOGGER.error("Original exception was: ", e);
            throw new RepositoryException("Failed to Update process model.", e);
        }

    }

    /**
     * Create new process model version 
     */
    @Override
    @Transactional(readOnly = false)
    @Event(message = HistoryEnum.UPDATE_PROCESS_MODEL)
    public ProcessModelVersion createProcessModelVersion(final Integer processId, final String branchName, 
            final Version newVersion, final Version originalVersion, final User user, final String lockStatus,
            final NativeType nativeType, final InputStream nativeStream) throws ImportException, RepositoryException {
        ProcessModelVersion pmv;
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String now = dateFormat.format(new Date());
        Process process = processRepo.findOne(processId);
        String processName = process.getName();

        try {
            if (user == null) {
                throw new ImportException("Permission to change this model denied.  No user specified.");
            } 
            else if (!canUserWriteProcess(user, processId)) {
                throw new ImportException("Permission to change this model denied.");
            } 
            if (lockStatus == null || Constants.UNLOCKED.equals(lockStatus)) {
                throw new RepositoryException("Process model " + processName + " is not locked for the updating session.");
            }
            else {
                ProcessModelVersion currentVersion = processModelVersionRepo.getProcessModelVersion(processId, branchName, originalVersion.toString());
                if (currentVersion != null) {
                    if (newVersion.toString().equals(currentVersion.getVersionNumber())) {
                        String message = "CONFLICT! The process model " + processName + " - " + branchName + " has been updated by another user." +
                                "\nThis process model version number: " + newVersion + "\nCurrent process model version number: " +
                                currentVersion.getVersionNumber();
                        LOGGER.error(message);
                        throw new RepositoryException(message);
                    }
                    else {
                        Native nat = formatSrv.storeNative(processName, now, now, user, nativeType, newVersion.toString(), nativeStream);
                        pmv = createProcessModelVersion(currentVersion.getProcessBranch(), newVersion, nativeType, null, nat);
                        LOGGER.info("Updated existing process model \"{}\"", processName);
                        return pmv;
                    }

                } else {
                    LOGGER.error("Unable to find the Process Model to update. Id={}, name={}, branch={}, current version={}",
                        processId, processName, branchName, originalVersion);
                    throw new RepositoryException("Unable to find the Process Model to update. Id=" + processId + ", name=" + processName 
                            + ", branch=" + branchName + ", current version=" + originalVersion.toString());
                }
            }
        } catch (RepositoryException | JAXBException | IOException e) {
            LOGGER.error("Failed to update process {}", processName);
            LOGGER.error("Original exception was: ", e);
            throw new RepositoryException("Failed to Update process model.", e);
        }

    }

    /**
     * @param user  a user
     * @param processId  identifier for a process
     * @return whether the <var>user</var> should be allowed to update the process identified by <var>processId</var>
     */
    private boolean canUserWriteProcess(User user, Integer processId) {
        for (GroupProcess gp: groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid())) {
            if (gp.getAccessRights().isWriteOnly()) {
                 return true;
            }
        }
        return false;
    }

    /**
     * @see org.apromore.service.ProcessService#exportProcess(String, Integer, String, Version, String)
     * {@inheritDoc}
     */
    @Override
    public ExportFormatResultType exportProcess(final String name, final Integer processId, final String branch, final Version version,
            final String format)
            throws ExportFormatException {
        try {
            ExportFormatResultType exportResult = new ExportFormatResultType();

            if (isRequestForNativeFormat(processId, branch, version, format)) {
                exportResult.setNative(new DataHandler(new ByteArrayDataSource(
                        nativeRepo.getNative(processId, branch, version.toString(), format).getContent(), "text/xml")));
            } else {
                LOGGER.error("Unsupported: export process {} to format {}", name, format);
                throw new ExportFormatException("Unsupported export format.");
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
            final Version preVersion, final Version newVersion, final String ranking, final boolean tobePublic) throws UpdateProcessException {
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
                boolean isCurrentPublic = !publicGroupProcesses.isEmpty();

                if (!isCurrentPublic && tobePublic) {
                    groupProcesses.add(new GroupProcess(process, publicGroup, new AccessRights(true,true,false)));
                    process.setGroupProcesses(groupProcesses);
                    workspaceSrv.createPublicStatusForUsers(process);

                } else if (isCurrentPublic && !tobePublic) {
                    groupProcesses.removeAll(publicGroupProcesses);
                    process.setGroupProcesses(groupProcesses);
                    workspaceSrv.removePublicStatusForUsers(process);
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
     * @see ProcessService#deleteProcessModel(List, User)
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
                //List<ProcessBranch> branches = process.getProcessBranches();
                LOGGER.debug("Retrieving the Process Model of the current version of " + process.getName() + " to be deleted.");

                try {
                    // Delete the process and branch if there's only one model version 
                    if (pvid.getProcessBranch().getProcessModelVersions().size() > 1) {
                        ProcessBranch branch = pvid.getProcessBranch();
                        List<ProcessModelVersion> pmvs = pvid.getProcessBranch().getProcessModelVersions();
                        deleteProcessModelVersion(pmvs, pvid, branch);
                    } else {
                        processRepo.delete(process);
                    }
                } catch (ExceptionDao e) {
                    throw new UpdateProcessException("Unable to modify " + process.getName(), e);
                }

            }
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

        try {
            // Work out if we are looking at the original format or native format for this model.
            if (isRequestForNativeFormat(processId, branch, version, format)) {
                xmlBPMNProcess = nativeRepo.getNative(processId, branch, version.toString(), format).getContent();
                LOGGER.debug("native");
            } else {
                LOGGER.error("Not supported to retrieve the canonical format");
                throw new RepositoryException("Not supported to retrieve the canonical format");
            }

            //LOGGER.debug("[new method] PROCESS:\n" + xmlBPMNProcess);
            return xmlBPMNProcess;

        } catch (Exception e) {
            LOGGER.error("Failed to retrieve the process!");
            LOGGER.error("Original exception was: ", e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public Folder getFolderByPmv(ProcessModelVersion pmv) {

        Process process = processRepo.findOne(pmv.getId());
        return process.getFolder();
    }

    private void deleteProcessModelVersion(List<ProcessModelVersion> pmvs, ProcessModelVersion pvidToDelete, ProcessBranch branch) throws ExceptionDao {
        ProcessModelVersion newCurrent = getPreviousVersion(pmvs, pvidToDelete);
        if (newCurrent == null) {
            newCurrent = getNextVersion(pmvs, pvidToDelete);
        }
        branch.setCurrentProcessModelVersion(newCurrent);
        branch.getProcessModelVersions().remove(pvidToDelete);
        processBranchRepo.save(branch);

        deleteProcessModelVersion(pvidToDelete);
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
    private ProcessModelVersion addProcessModelVersion(final Process process, final String processName, final Version version, final String branchName,
            final String created, final String lastUpdated, NativeType nativeType,Native nat) throws ImportException {
        ProcessModelVersion pmv;
        ProcessBranch branch = insertProcessBranch(process, created, lastUpdated, branchName);
        pmv = createProcessModelVersion(branch, version, nativeType, null,nat);
        return pmv;
    }


    @Transactional(readOnly = false)
    private void deleteProcessModelVersion(final ProcessModelVersion pmv) throws ExceptionDao {
        try {
            processModelVersionRepo.delete(pmv);
        } catch (Exception e) {
            String msg = "Failed to delete the process model version " + pmv.getId();
            LOGGER.error(msg, e);
            throw new ExceptionDao(msg, e);
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
            groupProcesses.add(new GroupProcess(process, user.getGroup(),new AccessRights(true,true,true)));
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
                    groupProcesses.add(new GroupProcess(process, publicGroup, new AccessRights(true,true,false)));
                }
            }            

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
            branch.setProcess(process);

            process.getProcessBranches().add(branch);

            return processBranchRepo.save(branch);
        } catch (Exception ex) {
            LOGGER.error("Importing a Branch Failed: " + ex.toString());
            throw new ImportException(ex);
        }
    }

    private ProcessModelVersion createProcessModelVersion(final ProcessBranch branch, final Version version, 
            NativeType nativeType, final String netId, Native nat) {
        String now = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
        ProcessModelVersion processModel = new ProcessModelVersion();

        processModel.setProcessBranch(branch);
        processModel.setOriginalId(netId);
        processModel.setVersionNumber(version.toString());
        processModel.setNumEdges(0);
        processModel.setNumVertices(0);
        processModel.setLockStatus(Constants.NO_LOCK);
        processModel.setCreateDate(now);
        processModel.setLastUpdateDate(now);
        processModel.setNativeType(nativeType);
        processModel.setNativeDocument(nat);
        branch.setCurrentProcessModelVersion(processModel);
        branch.getProcessModelVersions().add(processModel);
        
        return processModelVersionRepo.save(processModel);
    }


    /* Did the request ask for the model in the same format as it was originally added? */
    private boolean isRequestForNativeFormat(Integer processId, String branch, Version version, String format) {
        ProcessModelVersion pmv = processModelVersionRepo.getProcessModelVersion(processId, branch, version.toString());
        return pmv != null && pmv.getNativeType() != null && pmv.getNativeType().getNatType().equals(format);
    }

}
