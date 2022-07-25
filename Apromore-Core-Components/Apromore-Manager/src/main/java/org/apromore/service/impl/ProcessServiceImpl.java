/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * Copyright (C) 2015, 2016 Adriano Augusto.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import static org.apromore.common.Constants.DATE_FORMAT;
import static org.apromore.common.Constants.DRAFT_BRANCH_NAME;
import static org.apromore.common.Constants.TRUNK_NAME;
import static org.apromore.service.helper.BPMNDocumentHelper.getBPMNElements;
import static org.apromore.service.helper.BPMNDocumentHelper.getDocument;
import static org.apromore.service.helper.BPMNDocumentHelper.getXMLString;
import static org.apromore.service.helper.BPMNDocumentHelper.replaceSubprocessContents;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apromore.aop.Event;
import org.apromore.aop.HistoryEnum;
import org.apromore.common.Constants;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.NativeRepository;
import org.apromore.dao.ProcessBranchRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.StorageRepository;
import org.apromore.dao.SubprocessProcessRepository;
import org.apromore.dao.model.AccessRights;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Native;
import org.apromore.dao.model.NativeType;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.Storage;
import org.apromore.dao.model.SubprocessProcess;
import org.apromore.dao.model.User;
import org.apromore.exception.CircularReferenceException;
import org.apromore.exception.ExceptionDao;
import org.apromore.exception.ExportFormatException;
import org.apromore.exception.ImportException;
import org.apromore.exception.RepositoryException;
import org.apromore.exception.UpdateProcessException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.helper.Version;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.service.AuthorizationService;
import org.apromore.service.FormatService;
import org.apromore.service.LockService;
import org.apromore.service.ProcessService;
import org.apromore.service.UserService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.model.ProcessData;
import org.apromore.storage.StorageClient;
import org.apromore.storage.exception.ObjectCreationException;
import org.apromore.storage.factory.StorageManagementFactory;
import org.apromore.util.AccessType;
import org.apromore.util.StreamUtil;
import org.apromore.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Implementation of the ProcessService Contract.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service("processService")
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
    rollbackFor = Exception.class)
public class ProcessServiceImpl implements ProcessService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceImpl.class);

  /** Feature flag, enabling BPMN storage using storage service instead of native service. */
  @Value("${enableStorageServiceForProcessModels}")
  private boolean enableStorageService;

  /** Prefix (subfolder) to use with the Storage Service when storing new BPMN documents. */
  @Value("${storage.processModelPrefix}")
  private String processModelPrefix;

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
  private StorageRepository storageRepository;
  private String storagePath;
  private StorageManagementFactory<StorageClient> storageFactory;
  private SubprocessProcessRepository subprocessProcessRepository;

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
   * @param storageRepo Storage Repository
   */
  @Inject
  public ProcessServiceImpl(final NativeRepository nativeRepo, final GroupRepository groupRepo,
      final ProcessBranchRepository processBranchRepo, ProcessRepository processRepo,
      final ProcessModelVersionRepository processModelVersionRepo,
      final GroupProcessRepository groupProcessRepo, final LockService lService,
      final UserService userSrv, final FormatService formatSrv, final UserInterfaceHelper ui,
      final WorkspaceService workspaceService, final AuthorizationService authorizationService,
      final FolderRepository folderRepository, final ConfigBean config,
      final StorageRepository storageRepo, final StorageManagementFactory storageFactory,
      final SubprocessProcessRepository subprocessProcessRepo) {
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
    this.storageRepository = storageRepo;
    this.subprocessProcessRepository = subprocessProcessRepo;

    this.sanitizationEnabled = config.isSanitizationEnabled();
    this.storagePath = config.getStoragePath();
    this.storageFactory = storageFactory;
  }


  /**
   * @see org.apromore.service.ProcessService#importProcess(String, Integer, String, Version,
   *      String, InputStream, String, String, String, String, boolean) {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public ProcessModelVersion importProcess(final String username, final Integer folderId,
      final String processName, final Version version, final String natType,
      final InputStream nativeStream, final String domain, final String documentation,
      final String created, final String lastUpdate, final boolean publicModel)
      throws ImportException {
    LOGGER.debug("Executing operation canoniseProcess");

    if (nativeStream == null) {
      LOGGER.error("Process \"{}\" failed to import correctly.", processName);
      throw new ImportException("Process " + processName + " failed to import correctly.");
    }

    try {
      User user = userSrv.findUserByLogin(username);
      NativeType nativeType = formatSrv.findNativeType(natType);

      // Apply data sanitization measures to the uploaded document if configured to do so.
      InputStream sanitizedStream =
          sanitizationEnabled ? sanitize(nativeStream, nativeType) : nativeStream;
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

      Process process = insertProcess(processName, user, nativeType, domain, actualFolderId,
          created, publicModel);
      if (process.getId() == null) {
        throw new ImportException("Created New process named \"" + processName
            + "\", but JPA repository assigned a primary key ID of " + process.getId());
      }

      ProcessBranch branch = insertProcessBranch(process, created, lastUpdate, TRUNK_NAME);
      ProcessModelVersion pmv = insertProcessModelVersion(processName, branch, version, nativeType,
          sanitizedStream, lastUpdate, user);

      LOGGER.debug("Process model version: {}", pmv);

      workspaceSrv.addProcessToFolder(user, process.getId(), actualFolderId);
      LOGGER.info("Import process model \"{}\" (id {})", processName, process.getId());

      return pmv;

    } catch (ImportException e) {
      throw e;

    } catch (Exception e) {
      LOGGER.error("Failed to import process \"{}\" (native type {})", processName, natType, e);
      throw new ImportException(e);
    }
  }

  private ProcessModelVersion insertProcessModelVersion(final String processName,
                                                       final ProcessBranch branch, final Version version, final NativeType nativeType,
                                                       final InputStream sanitizedStream, final String lastUpdate, final User user)
    throws IOException, ObjectCreationException {

    if (enableStorageService) {
      Storage storage = createStorage(processName, version, nativeType, sanitizedStream);

      return createProcessModelVersion(branch, version, nativeType, null, null, storage, user);

    } else {
      Native nat = formatSrv.storeNative(processName, null, lastUpdate, null, nativeType,
          Constants.INITIAL_ANNOTATION, sanitizedStream);

      return createProcessModelVersion(branch, version, nativeType, null, nat, null, user);
    }
  }

  private Storage createStorage(final String processName, final Version version,
                                final NativeType nativeType, final InputStream in)
    throws IOException, ObjectCreationException {

    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    String now = dateFormat.format(new Date());

    Storage storage = new Storage();
    final String name = now + "_" + processName + "_" + version + "." + nativeType.getExtension();
    storage.setKey(name);
    storage.setPrefix(processModelPrefix);
    storage.setStoragePath(storagePath);

    writeInputStreamToStorage(in, storage);
    storageRepository.save(storage);

    return storage;
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
   * This implementation picks out BPMN elements with complex content (i.e. capable of representing
   * <script> tags) and flattens them into simple text.
   *
   * @param in a BPMN XML document in UTF-8
   * @return a sanitized version of the same BPMN XML document, also in UTF-8
   */
  public static InputStream sanitizeBPMN(final InputStream in) throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    TransformerFactory factory = TransformerFactory.newInstance();
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
    factory.newTransformer(new StreamSource(
        ProcessServiceImpl.class.getClassLoader().getResourceAsStream("xsd/sanitizeBPMN.xsl")))
      .transform(new StreamSource(in), new StreamResult(out));

    return new ByteArrayInputStream(out.toByteArray());
  }

  /**
   * Update an existing process model version
   */
  @Override
  @Transactional(readOnly = false)
  @Event(message = HistoryEnum.UPDATE_PROCESS_MODEL)
  public ProcessModelVersion updateProcessModelVersion(final Integer processId,
      final String branchName, final Version version, final User user, final String lockStatus,
      final NativeType nativeType, final InputStream nativeStream)
          throws ImportException, UpdateProcessException {
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String now = dateFormat.format(new Date());
    Process process;
    String processName = null;

    try {
      Optional<Process> processOptional = processRepo.findById(processId);
      if (processOptional.isPresent()) {
        process = processOptional.get();
        processName = process.getName();
      } else {
        throw new RepositoryException("Can not get Process with id: " + processId);
      }

      if (user == null) {
        throw new ImportException("Permission to change this model denied.  No user specified.");
      }

      if (!canUserWriteProcess(user, processId)) {
        throw new ImportException("Permission to change this model denied.");
      }

      ProcessModelVersion pmv;
      if (DRAFT_BRANCH_NAME.equals(branchName)) {
        pmv = processModelVersionRepo
                .getProcessModelVersionByUser(processId, branchName, version.toString(), user.getId());
      } else {
        pmv = processModelVersionRepo
                .getProcessModelVersion(processId, branchName, version.toString());
      }

      if (pmv == null) {
          throw new RepositoryException("Failed to update process " + processName + ". Unable to get storage " +
              "information of this process.");
      }

      pmv.setLastUpdateDate(now);

      if (pmv.getNativeDocument() != null) {
        pmv.getNativeDocument().setContent(StreamUtil.inputStream2String(nativeStream).trim());
        pmv.getNativeDocument().setLastUpdateDate(now);

      } else if (pmv.getStorage() != null) {
        if (processModelVersionRepo.countByStorageId(pmv.getStorage().getId()) < 2) {
          // Nobody else shares this storage reference, so it's safe to overwrite instead of copy
          writeInputStreamToStorage(nativeStream, pmv.getStorage());
          pmv.getStorage().setUpdated(now);

        } else if (enableStorageService) {
          // Copy on write, copying into the storage service
          pmv.setNativeDocument(null);
          pmv.setStorage(createStorage(processName, version, nativeType, nativeStream));

        } else {
          // Copy on write, copying into a native document
          pmv.setNativeDocument(formatSrv.storeNative(processName, null, now, null, nativeType,
            Constants.INITIAL_ANNOTATION, nativeStream));
          pmv.setStorage(null);
        }

      } else {
        LOGGER.error("Unable to find the Process Model to update. Id=" + processId + ", name="
            + processName + ", branch=" + branchName + ", current version=" + version);
        throw new RepositoryException("Unable to find the Process Model to update. Id="
            + processId + ", name=" + processName + ", branch=" + branchName
            + ", current version=" + version);
      }

      processModelVersionRepo.save(pmv);
      LOGGER.debug("Updated existing process model \"{}\"", processName);
      return pmv;

    } catch (RepositoryException | ObjectCreationException | IOException e) {
      LOGGER.error("Failed to update process {}", processName);
      LOGGER.error("Original exception was: ", e);
      throw new UpdateProcessException("Failed to Update process model.", e);
    }
  }

  private void writeInputStreamToStorage(InputStream inputStream, Storage storage)
    throws ObjectCreationException, IOException {

    try (OutputStream outputStream = storageFactory.getStorageClient(storage.getStoragePath())
            .getOutputStream(storage.getPrefix(), storage.getKey())) {
      inputStream.transferTo(outputStream);
    }
  }

  /**
   * Create new process model version
   */
  @Override
  @Transactional(readOnly = false)
  @Event(message = HistoryEnum.UPDATE_PROCESS_MODEL)
  public ProcessModelVersion createProcessModelVersion(final Integer processId,
      final String branchName, final Version newVersion, final Version originalVersion,
      final User user, final String lockStatus, final NativeType nativeType,
      final InputStream nativeStream) throws ImportException, RepositoryException {
    ProcessModelVersion pmv;
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    String now = dateFormat.format(new Date());
    Process process = processRepo.findById(processId).get();
    String processName = process.getName();

    try {
      if (user == null) {
        throw new ImportException("Permission to change this model denied.  No user specified.");
      }

      if (!canUserWriteProcess(user, processId)) {
        throw new ImportException("Permission to change this model denied.");
      }

      if (lockStatus == null || Constants.UNLOCKED.equals(lockStatus)) {
        throw new RepositoryException(
            "Process model " + processName + " is not locked for the updating session.");
      }

      ProcessModelVersion currentVersion = processModelVersionRepo
          .getProcessModelVersion(processId, branchName, originalVersion.toString());

      if (currentVersion == null) {
        LOGGER.error(
            "Unable to find the Process Model to update. Id={}, name={}, branch={}, current version={}",
            processId, processName, branchName, originalVersion);
        throw new RepositoryException("Unable to find the Process Model to update. Id="
            + processId + ", name=" + processName + ", branch=" + branchName
            + ", current version=" + originalVersion.toString());
      }

      if (newVersion.toString().equals(currentVersion.getVersionNumber())) {
        String message = "CONFLICT! The process model " + processName + " - " + branchName
            + " has been updated by another user." + "\nThis process model version number: "
            + newVersion + "\nCurrent process model version number: "
            + currentVersion.getVersionNumber();
        LOGGER.error(message);
        throw new RepositoryException(message);
      }

      pmv = insertProcessModelVersion(processName, currentVersion.getProcessBranch(), newVersion,
          nativeType, nativeStream, now, user);
      LOGGER.info("Updated existing process model \"{}\"", processName);

      return pmv;

    } catch (RepositoryException | IOException | ObjectCreationException e) {
      LOGGER.error("Failed to update process {}", processName);
      LOGGER.error("Original exception was: ", e);
      throw new RepositoryException("Failed to Update process model.", e);
    }

  }

  /**
   * @param user a user
   * @param processId identifier for a process
   * @return whether the <var>user</var> should be allowed to update the process identified by
   *         <var>processId</var>
   */
  private boolean canUserWriteProcess(User user, Integer processId) {
    for (GroupProcess gp : groupProcessRepo.findByProcessAndUser(processId, user.getRowGuid())) {
      if (gp.getAccessRights().isWriteOnly()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @see org.apromore.service.ProcessService#exportProcess(String, Integer, String, Version,
   *      String, String) {@inheritDoc}
   */
  @Override
  public ExportFormatResultType exportProcess(final String name, final Integer processId,
      final String branch, final Version version, final String format, final String username)
      throws ExportFormatException {
    try {
      ExportFormatResultType exportResult = new ExportFormatResultType();
      String xmlProcess = getBPMNRepresentation(name, processId, branch, version, userSrv.findUserByLogin(username).getId());
      exportResult.setNative(new DataHandler(new ByteArrayDataSource(xmlProcess, "text/xml")));
      return exportResult;
    } catch (Exception e) {
      LOGGER.error("Failed to export process model {} to format {}", name, format);
      LOGGER.debug("Original exception was: ", e);
      throw new ExportFormatException(e);
    }
  }

  @Override
  public ExportFormatResultType exportProcess(final String name, final Integer processId,
                                              final String branch, final Version version, final String format,
                                              final String username, final boolean includeLinkedSubprocesses)
      throws ExportFormatException {
    try {
      ExportFormatResultType exportResult = new ExportFormatResultType();
      String xmlProcess = getBPMNRepresentation(name, processId, branch, version, username, includeLinkedSubprocesses);
      exportResult.setNative(new DataHandler(new ByteArrayDataSource(xmlProcess, "text/xml")));
      return exportResult;
    } catch (Exception e) {
      LOGGER.error("Failed to export process model {} to format {}", name, format);
      LOGGER.debug("Original exception was: ", e);
      throw new ExportFormatException(e);
    }
  }

  /**
   * @see org.apromore.service.ProcessService#updateProcessMetaData(Integer, String, String, String,
   *      Version, Version, String, boolean) {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public void updateProcessMetaData(final Integer processId, final String processName,
      final String domain, final String username, final Version preVersion,
      final Version newVersion, final String ranking, final boolean tobePublic)
      throws UpdateProcessException {
    LOGGER.debug("Executing operation update process meta data.");

    try {
      List<ProcessModelVersion> processModelVersions =
          processModelVersionRepo.getCurrentProcessModelVersion(processId, preVersion.toString());
      for (ProcessModelVersion processModelVersion : processModelVersions) {
        ProcessBranch branch = processModelVersion.getProcessBranch();
        Process process = processRepo.findById(processId).get();

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
            groupProcesses
                    .add(new GroupProcess(process, publicGroup, new AccessRights(true, true, false)));
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
      }
    } catch (Exception e) {
      throw new UpdateProcessException(e.getMessage(), e.getCause());
    }
  }

  @Override
  public boolean isPublicProcess(Integer processId) {
    return !filterPublicGroupProcesses(processRepo.findUniqueByID(processId).getGroupProcesses())
        .isEmpty();
  }

  private Set<GroupProcess> filterPublicGroupProcesses(Set<GroupProcess> groupProcesses) {
    Group publicGroup = groupRepo.findPublicGroup();
    if (publicGroup == null) {
      LOGGER.warn("No public group present in repository");
      return Collections.emptySet();
    }

    Set<GroupProcess> publicGroupProcesses =
        new HashSet<>(); /*
                          * groupProcesses .stream() .filter(groupProcess ->
                          * publicGroup.equals(groupProcess.getGroup()))
                          * .collect(Collectors.toSet());
                          */
    for (GroupProcess groupProcess : groupProcesses) {
      if (publicGroup.equals(groupProcess.getGroup())) {
        publicGroupProcesses.add(groupProcess);
      }
    }

    return publicGroupProcesses;
  }



  /**
   * @see ProcessService#deleteProcessModel(List, User) {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public void deleteProcessModel(final List<ProcessData> models, final User user)
      throws UpdateProcessException {
    for (ProcessData entry : models) {
      List<ProcessModelVersion> processModelVersionList = processModelVersionRepo
          .getCurrentProcessModelVersion(entry.getId(), entry.getVersionNumber().toString());
      for (ProcessModelVersion pvid : processModelVersionList) {
        if (pvid != null && TRUNK_NAME.equals(pvid.getProcessBranch().getBranchName())) {
          Process process = pvid.getProcessBranch().getProcess();
          if (!canUserWriteProcess(user, process.getId())) {
            throw new UpdateProcessException("Write permission denied for " + user.getUsername());
          }
          LOGGER.debug("Retrieving the Process Model of the current version of " + process.getName()
                  + " to be deleted.");

          try {
            // Delete the process and branch if there's only one model version
            ProcessBranch branch = pvid.getProcessBranch();
            List<ProcessModelVersion> pmvs = pvid.getProcessBranch().getProcessModelVersions();
            deleteProcessModelVersion(pmvs, pvid, branch);

            // Delete corresponding draft version of all users
            List<ProcessModelVersion> draftPmvsToDelete =
                processModelVersionRepo.getProcessModelVersions(process.getId(), DRAFT_BRANCH_NAME,
                    pvid.getVersionNumber());
            for (ProcessModelVersion draftPmv : draftPmvsToDelete) {
              ProcessBranch draftBranch = draftPmv.getProcessBranch();
              List<ProcessModelVersion> draftPmvs = draftBranch.getProcessModelVersions();
              deleteProcessModelVersion(draftPmvs, draftPmv, draftBranch);
            }
            LOGGER.debug("Main branch has {} versions", pvid.getProcessBranch().getProcessModelVersions().size());
            // Delete the process only when main branch is empty
            if (pvid.getProcessBranch().getProcessModelVersions().isEmpty()) {
              LOGGER.debug("Deleting entire process");
              processRepo.delete(process);
            }
          } catch (ExceptionDao e) {
            throw new UpdateProcessException("Unable to modify " + process.getName(), e);
          }
        }
      }
    }
  }

  @Override
  public String getBPMNRepresentation(final String name, final Integer processId,
                                      final String branch, final Version version) throws RepositoryException {

    return getBPMNRepresentation(name, processId, branch, version, null);
  }


  /**
   * @see org.apromore.service.ProcessService#getBPMNRepresentation(String, Integer, String,
   *      Version, Integer) {@inheritDoc}
   */
  @Override
  public String getBPMNRepresentation(final String name, final Integer processId,
      final String branch, final Version version, @Nullable final Integer userId) throws RepositoryException {
    String xmlBPMNProcess;
    String format = "BPMN 2.0";

    try {
      ProcessModelVersion pmv;
      // The #getProcessModelVersion() method would return more than one result in draft branch
      if (DRAFT_BRANCH_NAME.equals(branch) && userId != null) {
        pmv = processModelVersionRepo
                .getProcessModelVersionByUser(processId, branch, version.toString(), userId);
      } else {
        pmv = processModelVersionRepo
                .getProcessModelVersion(processId, branch, version.toString());
      }
      // Work out if we are looking at the original format or native format for this model.
      if (isRequestForNativeFormat(pmv, format)) {
        Storage storage = pmv.getStorage();
        if (storage != null) {
          try (InputStream in = storageFactory
                .getStorageClient(storage.getStoragePath())
                .getInputStream(storage.getPrefix(), storage.getKey())) {
            xmlBPMNProcess = new String(in.readAllBytes(), StandardCharsets.UTF_8);
          }
        } else {  // null storage indicates we're using the legacy native repository
          xmlBPMNProcess = nativeRepo.getNative(processId, branch, version.toString(), format).getContent();
        }
        return xmlBPMNProcess;
      } else {
        LOGGER.error("Not supported to retrieve the canonical format");
        throw new RepositoryException("Not supported to retrieve the canonical format");
      }
    } catch (Exception e) {
      LOGGER.error("Failed to retrieve the process!");
      LOGGER.error("Original exception was: ", e);
      throw new RepositoryException(e);
    }
  }

  /**
   * @see org.apromore.service.ProcessService#getBPMNRepresentation(String, Integer, String,
   *      Version, String, boolean) {@inheritDoc}
   */
  @Override
  public String getBPMNRepresentation(final String name, final Integer processId,
                                      final String branch, final Version version, final String username,
                                      final boolean includeLinkedSubprocesses)
      throws RepositoryException, ParserConfigurationException, ExportFormatException, CircularReferenceException {

    String bpmnXML = getBPMNRepresentation(name, processId, branch, version);

    if (!includeLinkedSubprocesses) {
      return bpmnXML;
    }

    try {
      Document bpmnDocument = getDocument(bpmnXML);

      Map<String, Integer> subprocessLinks = getLinkedProcesses(processId, username);
      List<Node> subprocessNodes = getBPMNElements(bpmnDocument, "subProcess");

      for (Node subprocessNode : subprocessNodes) {
        String id = subprocessNode.getAttributes().getNamedItem("id").getTextContent();

        if (subprocessLinks.containsKey(id)) {
          Integer linkedProcessId = subprocessLinks.get(id);

          if (isProcessLinked(linkedProcessId, processId, username)) {
            throw new CircularReferenceException("Unable to create bpmn with linked processes due to circular references.");
          }

          Process linkedProcess = processRepo.findUniqueByID(linkedProcessId);
          ProcessModelVersion latestVersion = processModelVersionRepo.getLatestProcessModelVersion(linkedProcessId, branch);
          String linkedProcessBPMN = getBPMNRepresentation(linkedProcess.getName(),
              linkedProcessId, branch, new Version(latestVersion.getVersionNumber()), username, true);
          Document linkedProcessDocument = getDocument(linkedProcessBPMN);

          replaceSubprocessContents(subprocessNode, linkedProcessDocument);
        }
      }

      return getXMLString(bpmnDocument);

    } catch (UserNotFoundException e) {
      throw new RepositoryException("Failed to retrieve the process", e);
    } catch (IOException | SAXException | TransformerException e) {
      throw new ParserConfigurationException(e.getMessage());
    }
  }

  private void deleteProcessModelVersion(List<ProcessModelVersion> pmvs,
      ProcessModelVersion pvidToDelete, ProcessBranch branch) throws ExceptionDao {
    ProcessModelVersion newCurrent = getPreviousVersion(pmvs, pvidToDelete);
    if (newCurrent == null) {
      newCurrent = getNextVersion(pmvs, pvidToDelete);
    }
    branch.setCurrentProcessModelVersion(newCurrent);
    branch.getProcessModelVersions().remove(pvidToDelete);
    processBranchRepo.save(branch);

    deleteProcessModelVersion(pvidToDelete);
  }

  private ProcessModelVersion getPreviousVersion(List<ProcessModelVersion> pmvs,
      ProcessModelVersion pvid) {
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

  private ProcessModelVersion getNextVersion(List<ProcessModelVersion> pmvs,
      ProcessModelVersion pvid) {
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



  @Transactional(readOnly = false)
  private void deleteProcessModelVersion(final ProcessModelVersion pmv) throws ExceptionDao {
    LOGGER.debug("Deleting process model version {}", pmv);
    try {
      processModelVersionRepo.delete(pmv);

      Storage storage = pmv.getStorage();
      if (storage != null) {
        if (processModelVersionRepo.countByStorageId(storage.getId()) == 0) {
          storageFactory.getStorageClient(storage.getStoragePath())
                        .delete(storage.getPrefix(), storage.getKey());
          storageRepository.deleteById(storage.getId());
        }
      }

    } catch (Exception e) {
      String msg = "Failed to delete the process model version " + pmv.getId();
      LOGGER.error(msg, e);
      throw new ExceptionDao(msg, e);
    }
  }


  /* Inserts a new process into the DB. */
  private Process insertProcess(final String processName, final User user,
      final NativeType nativeType, final String domain, final Integer folderId,
      final String created, final boolean publicModel) throws ImportException {
    LOGGER.debug("Executing operation Insert Process");
    Process process = new Process();

    try {
      process.setName(StringUtil.normalizeFilename(processName));
      process.setUser(user);
      process.setDomain(domain);
      process.setNativeType(nativeType);
      process.setCreateDate(created);
      if (folderId != null && folderId != 0) {
        process.setFolder(workspaceSrv.getFolder(folderId));
      }

      Set<GroupProcess> groupProcesses = process.getGroupProcesses();

      // Add the user's personal group
      groupProcesses
          .add(new GroupProcess(process, user.getGroup(), new AccessRights(true, true, true)));
      process.setGroupProcesses(groupProcesses);

      process = processRepo.save(process);

      // TODO: kludging past a cascade issue by adding the public group as a second DB commit
      // really should figure out how to do this in one commit, as per the preceding commented-out
      // code

      groupProcesses = process.getGroupProcesses();

      // Add the public group
      if (publicModel) {
        Group publicGroup = groupRepo.findPublicGroup();
        if (publicGroup == null) {
          LOGGER.warn("No public group present in repository");
        } else {
          groupProcesses
              .add(new GroupProcess(process, publicGroup, new AccessRights(true, true, false)));
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
  private ProcessBranch insertProcessBranch(final Process process, final String created,
      final String lastUpdated, final String name) throws ImportException {
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

  private ProcessModelVersion createProcessModelVersion(final ProcessBranch branch,
      final Version version, NativeType nativeType, final String netId, Native nat,
      Storage storage, final User user) {

    String now = new SimpleDateFormat(DATE_FORMAT).format(new Date());
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
    processModel.setStorage(storage);
    processModel.setCreator(user);
    branch.setCurrentProcessModelVersion(processModel);
    branch.getProcessModelVersions().add(processModel);

    return processModelVersionRepo.save(processModel);
  }


  /* Did the request ask for the model in the same format as it was originally added? */
  private boolean isRequestForNativeFormat(ProcessModelVersion pmv,
      String format) {
    return pmv != null && pmv.getNativeType() != null
        && pmv.getNativeType().getNatType().equals(format);
  }


	@Override
	public boolean hasWritePermissionOnProcess(User user, List<Integer> processIds) {
		return processIds.stream().allMatch(processId -> canUserWriteProcess(user, processId));
	}

    @Override
    public ProcessModelVersion getProcessModelVersionByUser(Integer processId, String branch, String version,
                                                            Integer userId) {

      return processModelVersionRepo.getProcessModelVersionByUser(processId, branch,
              version, userId);
    }

  @Override
  public Process getProcessById(final Integer processId) throws RepositoryException {

    Optional<Process> processOptional = processRepo.findById(processId);
    if (processOptional.isPresent()) {
      return processOptional.get();
    } else {
      throw new RepositoryException("Can not get Process with id: " + processId);
    }
  }

  @Override
  public ProcessModelVersion getProcessModelVersion(Integer processId, String branch, String version) {
    return processModelVersionRepo.getProcessModelVersion(processId, branch, version);
  }

  @Override
  @Transactional
  public ProcessModelVersion createDraft(Integer processId, String processName, String versionNumber,
                                          String nativeType, InputStream nativeStream, String userName) throws ImportException {
    try {
      Process processModel = getProcessById(processId);

      DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
      Date date = new Date();
      String now = dateFormat.format(date);

      // Check whether draft branch for this process model is already exist
      ProcessBranch branch = processModel.getProcessBranches().stream().filter(processBranch ->
              processBranch.getBranchName().equals(DRAFT_BRANCH_NAME))
              .findAny().orElse(null);

      if (branch == null) {
        branch = insertProcessBranch(processModel, now, now,
                DRAFT_BRANCH_NAME);
      }

      return insertProcessModelVersion(processName, branch, new Version(versionNumber)
              , formatSrv.findNativeType(nativeType),
              nativeStream, now, userSrv.findUserByLogin(userName));
    } catch (Exception e) {
      throw new ImportException("Create draft failed caused by {}", e);
    }
  }

  @Override
  public ProcessModelVersion updateDraft(Integer processId, String versionNumber,
                                         String nativeType, InputStream nativeStream, String userName) throws UpdateProcessException {
    try {
      return updateProcessModelVersion(
              processId, DRAFT_BRANCH_NAME, new Version(versionNumber), userSrv.findUserByLogin(userName), "", formatSrv.findNativeType(nativeType),
              nativeStream);
    } catch (Exception e) {
      throw new UpdateProcessException("Update draft failed caused by {}", e);
    }
  }

  @Override
  public void linkSubprocess(Integer subprocessParentId, String subprocessId, Integer processId, String username)
      throws CircularReferenceException, UserNotFoundException {
    if (isProcessLinked(processId, subprocessParentId, username)) {
      throw new CircularReferenceException("Linking these 2 models will create a circular reference.");
    }

    SubprocessProcess subprocessProcessLink = subprocessProcessRepository
        .getExistingLink(subprocessParentId, subprocessId);
    if (subprocessProcessLink == null) {
      subprocessProcessLink = new SubprocessProcess();
    }
    subprocessProcessLink.setSubprocessParent(processRepo.getById(subprocessParentId));
    subprocessProcessLink.setSubprocessId(subprocessId);
    subprocessProcessLink.setLinkedProcess(processRepo.getById(processId));
    subprocessProcessRepository.saveAndFlush(subprocessProcessLink);
  }

  @Override
  public void unlinkSubprocess(Integer subprocessParentId, String subprocessId) {
    SubprocessProcess subprocessProcessLink = subprocessProcessRepository
        .getExistingLink(subprocessParentId, subprocessId);
    if (subprocessProcessLink == null) {
      return;
    }
    subprocessProcessRepository.delete(subprocessProcessLink);
  }

  @Override
  public ProcessSummaryType getLinkedProcess(int subprocessParentId, String subprocessId) {
    Process process = subprocessProcessRepository.getLinkedProcess(subprocessParentId, subprocessId);

    if (process == null) {
      return null;
    }

    return ui.buildProcessSummary(process);
  }

  @Override
  public boolean hasLinkedProcesses(Integer processId, String username) throws UserNotFoundException {
    return !getLinkedProcesses(processId, username).isEmpty();
  }

  @Override
  public Map<String, Integer> getLinkedProcesses(Integer processId, String username) throws UserNotFoundException {
    Map<String, Integer> linkedProcesses = new HashMap<>();
    List<SubprocessProcess> subprocessProcesses = subprocessProcessRepository.getLinkedSubProcesses(processId);
    User user = userSrv.findUserByLogin(username);

    for (SubprocessProcess subprocessProcess : subprocessProcesses) {
      int linkedProcessId = subprocessProcess.getLinkedProcess().getId();
      //Check for user access to the linked process
      if (authorizationService.getProcessAccessTypeByUser(linkedProcessId, user) != null) {
        linkedProcesses.put(subprocessProcess.getSubprocessId(), subprocessProcess.getLinkedProcess().getId());
      }
    }
    return Collections.unmodifiableMap(linkedProcesses);
  }

  @Override
  public Integer getProcessParentFolder(Integer processId) {
    if (processId == null) {
      return 0;
    }
    Process processWithFolder = processRepo.findUniqueByID(processId);
    if (processWithFolder != null && processWithFolder.getFolder() != null) {
      return processWithFolder.getFolder().getId();
    } else {
      return 0;
    }
  }

  /**
   * Check if the processes are linked.
   * @param linkedFromProcessId
   * @param linkedToProcessId
   * @return true if the linkedFromProcessId contains a link to linkedToProcessId.
   */
  private boolean isProcessLinked(int linkedFromProcessId, int linkedToProcessId, String username)
      throws UserNotFoundException {
    return isProcessLinked(linkedFromProcessId, linkedToProcessId, Collections.emptyList(), username);
  }

  /**
   * Check if the processes are linked.
   * @param linkedFromProcessId
   * @param linkedToProcessId
   * @param checkedIds ids of processes that have already been checked. Used to avoid endless loops.
   * @return true if the linkedFromProcessId contains a link to linkedToProcessId.
   */
  private boolean isProcessLinked(int linkedFromProcessId, int linkedToProcessId, List<Integer> checkedIds, String username)
      throws UserNotFoundException {

    if (linkedToProcessId == linkedFromProcessId) {
      return true;
    }

    List<Integer> checkedIdsCopy = new ArrayList<>(checkedIds);
    if (!checkedIdsCopy.contains(linkedFromProcessId)) {
      checkedIdsCopy.add(linkedFromProcessId);
    }

    for (int linkedProcessId : getLinkedProcesses(linkedFromProcessId, username).values()) {
      if (checkedIdsCopy.contains(linkedProcessId)) {
        //Skip if this id has already been checked
        continue;
      }

      if (isProcessLinked(linkedProcessId, linkedToProcessId, checkedIdsCopy, username)) {
        return true;
      }
    }

    return false;
  }
}
