/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.CustomCalendarRepository;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupFolderRepository;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.GroupUsermetadataRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.StorageRepository;
import org.apromore.dao.SubprocessProcessRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.UsermetadataRepository;
import org.apromore.dao.WorkspaceRepository;
import org.apromore.dao.model.AccessRights;
import org.apromore.dao.model.CustomCalendar;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupLog;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.GroupUsermetadata;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.Storage;
import org.apromore.dao.model.SubprocessProcess;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.dao.model.Workspace;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.service.EventLogFileService;
import org.apromore.service.EventLogService;
import org.apromore.service.FolderService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.model.FolderTreeNode;
import org.apromore.storage.StorageClient;
import org.apromore.storage.StorageType;
import org.apromore.storage.factory.StorageManagementFactory;
import org.apromore.util.UserMetadataTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import static org.apromore.common.Constants.TRUNK_NAME;

@Service("workspaceService")
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
    rollbackFor = Exception.class)
public class WorkspaceServiceImpl implements WorkspaceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceServiceImpl.class);
  private static final Integer ROOT_FOLDER_ID = 0;

  private WorkspaceRepository workspaceRepo;
  private ProcessRepository processRepo;
  private ProcessModelVersionRepository pmvRepo;
  private LogRepository logRepo;
  private UsermetadataRepository usermetadataRepo;
  private FolderRepository folderRepo;
  private UserRepository userRepo;
  private GroupRepository groupRepo;
  private GroupFolderRepository groupFolderRepo;
  private GroupProcessRepository groupProcessRepo;
  private GroupLogRepository groupLogRepo;
  private GroupUsermetadataRepository groupUsermetadataRepo;
  private CustomCalendarRepository customCalendarRepo;
  private SubprocessProcessRepository subprocessProcessRepo;
  private EventLogFileService logFileService;
  private FolderService folderService;
  private StorageRepository storageRepository;
  private EventLogService eventLogService;

  private StorageManagementFactory<StorageClient> storageFactory;
  private ConfigBean config;

  @Value("${storage.logPrefix}")
  private String logPrefix;

  /**
   * Default Constructor allowing Spring to Autowire for testing and normal use.
   *
   * @param workspaceRepository Workspace Repository.
   * @param userRepository User Repository.
   * @param processRepository Process Repository.
   * @param folderRepository Folder Repository.
   */
  @Inject
  public WorkspaceServiceImpl(final WorkspaceRepository workspaceRepository,
      final UserRepository userRepository, final ProcessRepository processRepository,
      final ProcessModelVersionRepository pmvRepository, final LogRepository logRepository,
      final UsermetadataRepository usermetadataRepository, final FolderRepository folderRepository,
      final GroupRepository groupRepository, final GroupFolderRepository groupFolderRepository,
      final GroupProcessRepository groupProcessRepository,
      final GroupLogRepository groupLogRepository,
      final GroupUsermetadataRepository groupUsermetadataRepository,
      final CustomCalendarRepository customCalendarRepository,
      final SubprocessProcessRepository subprocessProcessRepository,
      final EventLogFileService eventLogFileService, final FolderService folderService,
      final StorageManagementFactory storageFactory, final EventLogService eventLogService,
      final StorageRepository storageRepository, final ConfigBean configBean) {

    workspaceRepo = workspaceRepository;
    userRepo = userRepository;
    processRepo = processRepository;
    pmvRepo = pmvRepository;
    logRepo = logRepository;
    usermetadataRepo = usermetadataRepository;
    folderRepo = folderRepository;
    groupRepo = groupRepository;
    groupFolderRepo = groupFolderRepository;
    groupProcessRepo = groupProcessRepository;
    groupLogRepo = groupLogRepository;
    groupUsermetadataRepo = groupUsermetadataRepository;
    customCalendarRepo = customCalendarRepository;
    subprocessProcessRepo = subprocessProcessRepository;
    logFileService = eventLogFileService;
    this.folderService = folderService;
    this.storageFactory = storageFactory;
    this.eventLogService = eventLogService;
    this.storageRepository = storageRepository;
    this.config = configBean;
  }

  @Override
  public Folder getFolder(Integer folderId) {
    if (folderId == null || folderId == 0)
      return null;
    return folderRepo.findById(folderId).orElse(null);
  }

  @Override
  public List<GroupProcess> getGroupProcesses(String userId, Integer folderId) {
    return (folderId == 0) ? groupProcessRepo.findRootProcessesByUser(userId)
        : groupProcessRepo.findAllProcessesInFolderForUser(folderId, userId);
  }

  @Override
  public List<Process> getProcessesByPrefix(String prefix) {
    return processRepo.findWithPrefix(prefix);
  }

  @Override
  public List<Log> getLogsByPrefix(String prefix) {
    return logRepo.findWithPrefix(prefix);
  }

  @Override
  public Page<Process> getProcesses(String userId, Integer folderId, Pageable pageable) {
    return (folderId == 0) ? processRepo.findRootProcessesByUser(userId, pageable)
        : processRepo.findAllProcessesInFolderForUser(folderId, userId, pageable);
  }

  @Override
  public Page<Process> getAllProcesses(Integer folderId, Pageable pageable) {
    return (folderId == 0) ? processRepo.findRootProcesses(pageable)
        : processRepo.findAllProcessesInFolder(folderId,  pageable);
  }

  @Override
  public Page<Log> getLogs(String userId, Integer folderId, Pageable pageable) {
    return (folderId == 0) ? logRepo.findRootLogsByUser(userId, pageable)
        : logRepo.findAllLogsInFolderForUser(folderId, userId, pageable);
  }

  @Override
  public Page<Log> getAllLogs(Integer folderId, Pageable pageable) {
    return (folderId == 0) ? logRepo.findAllRootLogs(pageable)
        : logRepo.findAllLogsInFolder(folderId, pageable);
  }

  @Override
  @Transactional(readOnly = false)
  public Integer createFolder(String userId, String folderName, Integer parentFolderId,
      Boolean isGEDMatrixReady) {
    Folder folder = new Folder();
    folder.setName(folderName);
    folder.setParentFolderChain("0");
    User user = userRepo.findByRowGuid(userId);

    if (parentFolderId != 0) {
      Folder parent = folderRepo.findById(parentFolderId).orElse(null);
      if (parent != null) {
        folder.setParentFolder(parent);
        folder.setParentFolderChain(parent.getParentFolderChain() + "_" + parent.getId());
      }
    }

    Workspace workspace = workspaceRepo.findById(1).orElse(null);
    folder.setWorkspace(workspace);
    folder.setCreatedBy(user);
    folder.setModifiedBy(user);
    folder.setDateCreated(Calendar.getInstance().getTime());
    folder.setDateModified(Calendar.getInstance().getTime());
    folder.setDescription("");
    if (isGEDMatrixReady != null) {
      folder.setGEDMatrixReady(isGEDMatrixReady);
    }
    folder = folderRepo.saveAndFlush(folder);

    Set<GroupFolder> groupFolders = folder.getGroupFolders();

    GroupFolder gf = new GroupFolder();
    gf.setFolder(folder);
    gf.setGroup(user.getGroup());
    AccessRights accessRights = new AccessRights();
    accessRights.setOwnerShip(true);
    accessRights.setWriteOnly(true);
    accessRights.setReadOnly(true);
    gf.setAccessRights(accessRights);
    groupFolders.add(gf);

    // Unless in the root folder, add access rights of its immediately enclosing folder
    if (parentFolderId != 0) {
      Folder parent = folderRepo.findById(parentFolderId).orElse(null);
      if (parent != null) {
        for (GroupFolder groupFolder : parent.getGroupFolders()) {
          if (!Objects.equals(groupFolder.getGroup().getId(), user.getGroup().getId())) { // Avoid
                                                                                          // adding
            // operating user twice
            groupFolders.add(
                new GroupFolder(groupFolder.getGroup(), folder, groupFolder.getAccessRights()));
          }
        }
      }
    }
    folder.setGroupFolders(groupFolders);
    folderRepo.save(folder);

    return folder.getId();
  }

  @Override
  public boolean isGEDReadyFolder(Integer folderId) {
    Folder folder = folderRepo.findById(folderId).get();
    return folder.isGEDMatrixReady();
  }

  @Override
  public void updateFolder(Integer folderId, String folderName, Boolean isGEDMatrixReady, User user)
      throws NotAuthorizedException {
    if (!canUserWriteFolder(user, folderId)) {
      throw new NotAuthorizedException(
          "User " + user.getUsername() + " is not permitted to delete folder with id " + folderId);
    }
    Folder folder = folderRepo.findById(folderId).orElse(null);
    if (folder != null) {
      if (folderName != null && !folderName.isEmpty()) {
        folder.setName(folderName);
      }
      if (isGEDMatrixReady != null) {
        folder.setGEDMatrixReady(isGEDMatrixReady);
        for (Folder subfolder : folder.getSubFolders()) {
          updateFolder(subfolder.getId(), null, isGEDMatrixReady, user);
        }
      }
      folder.setDateModified(Calendar.getInstance().getTime());

      folderRepo.save(folder);
    }
  }

  /**
   * @param user a user
   * @param folderId identifier for a folder
   * @return whether the <var>user</var> should be allowed to update the folder identified by
   *         <var>folderId</var>
   */
  private boolean canUserWriteFolder(User user, Integer folderId) {
    for (GroupFolder gf : groupFolderRepo.findByFolderAndUser(folderId, user.getRowGuid())) {
      if (gf.getAccessRights().isWriteOnly()) {
        return true;
      }
    }
    return false;
  }

  @Override
  @Transactional(readOnly = false)
  public void deleteFolder(Integer folderId, User user) throws Exception {
    if (!canUserWriteFolder(user, folderId)) {
      throw new NotAuthorizedException(
          "User " + user.getUsername() + " is not permitted to delete folder with id " + folderId);
    }
    Folder folder = folderRepo.findById(folderId).orElse(null);
    if (folder != null) {

      // Remove logs that are contained in specified folder and its sub-folders
      List<Folder> subFoldersWithCurrentFolders = folderService.getSubFolders(folderId, true);
      List<Integer> folderIds = new ArrayList<>();

      for (Folder f : subFoldersWithCurrentFolders) {
        folderIds.add(f.getId());
      }

      List<Log> logs = new ArrayList<>(logRepo.findByFolderIdIn(folderIds));
      eventLogService.deleteLogs(logs, user);

      // Remove specified folder and sub-folders
      folderRepo.delete(folder);
    }
  }

  @Override
  public List<FolderTreeNode> getWorkspaceFolderTree(String userId) {
    return folderService.getFolderTreeByUser(0, userId);
  }

  @Override
  public List<Folder> getBreadcrumbs(Integer folderId) {
    List<Folder> folders = new ArrayList<>();

    Folder folder = folderRepo.findById(folderId).orElse(null);
    if (folder != null) {
      folders.add(folder);
      while (folder.getParentFolder() != null && folder.getParentFolder().getId() != 0) {
        folder = folderRepo.findById(folder.getParentFolder().getId()).get();
        folders.add(folder);
      }
    }

    return folders;
  }

  @Override
  public List<GroupFolder> getSubFolders(String userRowGuid, Integer folderId) {
    User user = userRepo.findByRowGuid(userRowGuid);
    List<GroupFolder> folderUsers = new ArrayList<>();
    Map<Integer, GroupFolder> map = new HashMap<>();
    for (GroupFolder gf : groupFolderRepo.findByParentFolderAndUser(folderId, userRowGuid)) {
      GroupFolder fu = map.get(gf.getFolder().getId());
      if (fu == null) {
        fu = new GroupFolder();
        fu.setGroup(gf.getGroup());
        fu.setFolder(gf.getFolder());
        fu.setAccessRights(gf.getAccessRights());
        folderUsers.add(fu);
        map.put(gf.getFolder().getId(), fu);
      }
    }
    return folderUsers;
  }



  @Override
  @Transactional(readOnly = false)
  public Process addProcessToFolder(User user, Integer processId, Integer folderId) {
    if (folderId != null && processId != null) {
      Process process = processRepo.findById(processId).get();
      Folder folder = folderRepo.findById(folderId).orElse(null);

      process.setFolder(folder);

      // Add the user's personal group is done in
      // org.apromore.service.impl.ProcessServiceImpl.insertProcess

      // Unless in the root folder, add access rights of its immediately enclosing folder
      if (folder != null) {
        Set<GroupProcess> groupProcesses = process.getGroupProcesses();
        Set<GroupFolder> groupFolders = folder.getGroupFolders();
        for (GroupFolder gf : groupFolders) {
          if (!Objects.equals(gf.getGroup().getId(), user.getGroup().getId())) { // Avoid adding
                                                                                 // operating
            // user twice
            groupProcesses.add(new GroupProcess(process, gf.getGroup(), gf.getAccessRights()));
          }
        }
        process.setGroupProcesses(groupProcesses);
      }
      return processRepo.save(process);

    } else {
      LOGGER.warn("Missing folderID " + folderId + " Missing processID " + processId);
      return null;
    }
  }

  /**
   * @see org.apromore.service.WorkspaceService#createPublicStatusForUsers(org.apromore.dao.model.Process)
   *      {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public void createPublicStatusForUsers(final Process process) {
    createGroupProcess(groupRepo.findPublicGroup(), process, true, false, false);
  }

  /**
   * @see org.apromore.service.WorkspaceService#removePublicStatusForUsers(org.apromore.dao.model.Process)
   *      {@inheritDoc}
   */
  @Override
  @Transactional(readOnly = false)
  public void removePublicStatusForUsers(final Process process) {
    Group publicGroup = groupRepo.findPublicGroup();
    if (publicGroup == null) {
      LOGGER.warn("No public group in repository");
    } else {
      Set<GroupProcess> freshGroupProcesses = new HashSet<>();
      for (GroupProcess groupProcess : process.getGroupProcesses()) {
        if (!publicGroup.equals(groupProcess.getGroup())) {
          freshGroupProcesses.add(groupProcess);
        }
      }
      process.setGroupProcesses(freshGroupProcesses);
    }
  }

  @Override
  @Transactional(readOnly = false)
  public Log copyLog(Integer logId, Integer newFolderId, String userName, boolean isPublic)
      throws Exception {
    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    String now = dateFormat.format(new Date());
    Log currentLog = logRepo.findUniqueByID(logId);
    Folder newFolder = folderRepo.findUniqueByID(newFolderId);
    User newUser = userRepo.findByUsername(userName);

    Log newLog = new Log();
    newLog.setName(currentLog.getName());
    newLog.setDomain(currentLog.getDomain());
    newLog.setRanking(currentLog.getRanking());
    newLog.setFilePath(currentLog.getFilePath());
    newLog.setUser(newUser);
    newLog.setFolder(newFolder);
    newLog.setCreateDate(now);

    // Set access group
    Set<GroupLog> groupLogs = newLog.getGroupLogs();
    groupLogs.clear();
    // Add user's singleton group
    groupLogs.add(new GroupLog(newUser.getGroup(), newLog, new AccessRights(true, true, true)));
    // Add Public group
    if (isPublic) {
      Group publicGroup = groupRepo.findPublicGroup();
      if (publicGroup == null) {
        LOGGER.warn("No public group present in repository");
      } else {
        groupLogs.add(new GroupLog(publicGroup, newLog, new AccessRights(true, true, false)));
      }
    }
    // Unless in the root folder, add access rights of its immediately enclosing folder
    if (newFolder != null) {
      for (GroupFolder gf : newFolder.getGroupFolders()) {
        if (!Objects.equals(gf.getGroup().getId(), newUser.getGroup().getId())) { // Avoid adding
                                                                                  // operating
          // user twice
          groupLogs.add(new GroupLog(gf.getGroup(), newLog, gf.getAccessRights()));
        }
      }
    }
    newLog.setGroupLogs(groupLogs);

    if (currentLog.getStorage() != null) {
      newLog.setStorage(currentLog.getStorage());
    }

    // Copy file
    final String currentFileFullName =  // For backward compatibility
        currentLog.getFilePath() + "_" + currentLog.getName() + ".xes.gz";
    if (currentLog.getStorage() == null) {
      try (InputStream inputStream = storageFactory
          .getStorageClient("FILE" + StorageType.STORAGE_PATH_SEPARATOR + config.getLogsDir())
          .getInputStream(null, currentFileFullName)) {

        Storage storage = new Storage();
        storage.setKey(currentFileFullName);
        storage.setPrefix(logPrefix);
        storage.setStoragePath(config.getStoragePath());

        try (OutputStream outputStream = storageFactory
            .getStorageClient("FILE" + StorageType.STORAGE_PATH_SEPARATOR + config.getLogsDir())
            .getOutputStream(storage.getPrefix(), storage.getKey())) {

          logFileService.copyFile(inputStream, outputStream);
        }

        newLog.setStorage(storageRepository.saveAndFlush(storage));
        LOGGER.info("User {} copy Log {} to folder {}", userName, currentLog.getName(), null == newFolder ?
                "Home folder" : newFolder.getName());
      }
    }

    // Persist
    try {
      logRepo.save(newLog);
    } catch (Exception e) {
      // log something
      storageFactory.getStorageClient(config.getStoragePath())
                    .delete(logPrefix, currentFileFullName);
    }

    // Deep copy old_log's artifacts to new log
    eventLogService.deepCopyArtifacts(currentLog, newLog,
            Arrays.asList(UserMetadataTypeEnum.CSV_IMPORTER.getUserMetadataTypeId(),
                    UserMetadataTypeEnum.PERSPECTIVE_TAG.getUserMetadataTypeId(),
                    UserMetadataTypeEnum.DASHBOARD.getUserMetadataTypeId(),
                    UserMetadataTypeEnum.DASH_TEMPLATE.getUserMetadataTypeId(),
                    UserMetadataTypeEnum.FILTER.getUserMetadataTypeId(),
                    UserMetadataTypeEnum.FILTER_TEMPLATE.getUserMetadataTypeId(),
                    UserMetadataTypeEnum.COST_TABLE.getUserMetadataTypeId()), userName);

    return newLog;
  }

  @Override
  @Transactional(readOnly = false)
  public Log moveLog(Integer logId, Integer newFolderId) throws Exception {
    Log log = logRepo.findUniqueByID(logId);
    Folder newFolder = folderRepo.findUniqueByID(newFolderId);
    log.setFolder(newFolder);

    // Unless in the root folder, overwrite and inherit access rights from direct parent folder
    if (!ROOT_FOLDER_ID.equals(newFolderId)) {

      Set<GroupLog> groupLogs = log.getGroupLogs();
      groupLogs.clear();

      for (GroupFolder gf : newFolder.getGroupFolders()) {
        groupLogs.add(new GroupLog(gf.getGroup(), log, gf.getAccessRights()));
      }
    }

    logRepo.save(log);
    return log;
  }

  @Override
  @Transactional(readOnly = false)
  public Process copyProcessVersions(Integer processId, List<String> pmvVersions,
      Integer newFolderId, String userName, boolean isPublic) throws Exception {
    Folder newFolder = folderRepo.findUniqueByID(newFolderId);
    User newUser = userRepo.findByUsername(userName);

    Process process = processRepo.findUniqueByID(processId);
    Process newProcess = process.clone();

    ProcessBranch branch = process.getProcessBranches().get(0);
    ProcessBranch newBranch = branch.clone();

    List<ProcessModelVersion> newPMVList =
        this.createNewPMVs(process.getId(), pmvVersions, branch, newBranch);
    if (newPMVList.isEmpty()) {
      throw new Exception("No process model versions were found for processId=" + process.getId()
          + "and versions=" + pmvVersions.toString());
    }

    newBranch.setProcess(newProcess);
    newBranch.setProcessModelVersions(newPMVList);
    newBranch.setCurrentProcessModelVersion(newPMVList.get(newPMVList.size() - 1));

    newProcess.getProcessBranches().clear();
    newProcess.setProcessBranches(Collections.singletonList(newBranch));
    newProcess.setUser(newUser);
    newProcess.setFolder(newFolder);

    // Set access group
    Set<GroupProcess> groupProcesses = newProcess.getGroupProcesses();
    groupProcesses.clear();
    groupProcesses
        .add(new GroupProcess(newProcess, newUser.getGroup(), new AccessRights(true, true, true)));
    if (isPublic) {
      Group publicGroup = groupRepo.findPublicGroup();
      if (publicGroup == null) {
        LOGGER.warn("No public group present in repository");
      } else {
        groupProcesses
            .add(new GroupProcess(newProcess, publicGroup, new AccessRights(true, true, false)));
      }
    }
    // Unless in the root folder, add access rights of its immediately enclosing folder
    if (newFolder != null) {
      for (GroupFolder gf : newFolder.getGroupFolders()) {
        if (!Objects.equals(gf.getGroup().getId(), newUser.getGroup().getId())) { // Avoid adding
                                                                                  // operating
          // user twice
          groupProcesses.add(new GroupProcess(newProcess, gf.getGroup(), gf.getAccessRights()));
        }
      }
    }
    newProcess.setGroupProcesses(groupProcesses);

    // Copy subprocess links
    List<SubprocessProcess> newSubprocessLinks = createNewSubprocessLinks(process, newProcess);

    processRepo.save(newProcess);
    for (ProcessModelVersion pmv : newPMVList) {
      pmvRepo.save(pmv);
    }
    subprocessProcessRepo.saveAll(newSubprocessLinks);

    return newProcess;
  }

  @Override
  @Transactional(readOnly = false)
  public Process copyProcess(Integer processId, Integer newFolderId, String userName,
      boolean isPublic) throws Exception {
    Process process = processRepo.findUniqueByID(processId);

    // Only copy MAIN branch but not DRAFT branch
    ProcessBranch branch = process.getProcessBranches().stream()
            .filter(pb -> TRUNK_NAME.equals(pb.getBranchName()))
            .findAny()
            .orElse(null);

    List<String> pmvVersions = new ArrayList<>();

    if (branch != null) {
      for (ProcessModelVersion pmv : branch.getProcessModelVersions()) {
        pmvVersions.add(pmv.getVersionNumber());
      }
    }

    return copyProcessVersions(processId, pmvVersions, newFolderId, userName, isPublic);
  }

  @Override
  @Transactional(readOnly = false)
  public Process moveProcess(Integer processId, Integer newFolderId) throws Exception {
    Folder newFolder = folderRepo.findUniqueByID(newFolderId);
    Process process = processRepo.findUniqueByID(processId);
    process.setFolder(newFolder);

    // Unless in the root folder, overwrite and inherit access rights from direct parent folder
    if (!ROOT_FOLDER_ID.equals(newFolderId)) {

      Set<GroupProcess> groupProcesses = process.getGroupProcesses();
      groupProcesses.clear();

      for (GroupFolder gf : newFolder.getGroupFolders()) {
        groupProcesses.add(new GroupProcess(process, gf.getGroup(), gf.getAccessRights()));
      }
    }

    processRepo.save(process);

    return process;
  }

  private List<ProcessModelVersion> createNewPMVs(Integer processId, List<String> pmvVersions,
      ProcessBranch oldBranch, ProcessBranch newBranch) throws Exception {
    List<ProcessModelVersion> pmvs = new ArrayList<>();
    for (ProcessModelVersion pmv : oldBranch.getProcessModelVersions()) {
      if (pmvVersions.contains(pmv.getVersionNumber())) {
        ProcessModelVersion newPMV = pmv.clone();
        newPMV.setProcessBranch(newBranch);
        if (pmv.getNativeDocument() != null) {
          newPMV.setNativeDocument(pmv.getNativeDocument().clone());
        }
        if (pmv.getStorage() != null) {
          newPMV.setStorage(new Storage(pmv.getStorage()));
        }

        newPMV.getProcessModelAttributes().clear();
        newPMV.setProcessModelAttributes(new HashSet<>(pmv.getProcessModelAttributes()));
        pmvs.add(newPMV);
      }
    }
    return pmvs;
  }

  private List<SubprocessProcess> createNewSubprocessLinks(Process oldProcess, Process newProcess) {
    List<SubprocessProcess> subprocessProcesses = new ArrayList<>();
    for (SubprocessProcess oldSubprocessProcess : subprocessProcessRepo.getLinkedSubProcesses(oldProcess.getId())) {
      SubprocessProcess newSubprocessProcess = new SubprocessProcess();
      newSubprocessProcess.setSubprocessId(oldSubprocessProcess.getSubprocessId());
      newSubprocessProcess.setLinkedProcess(oldSubprocessProcess.getLinkedProcess());
      newSubprocessProcess.setSubprocessParent(newProcess);
      subprocessProcesses.add(newSubprocessProcess);
    }
    return subprocessProcesses;
  }

  @Override
  public Folder copyFolder(Integer folderId, Integer sourceFolderId, Integer targetFolderId)
      throws Exception {
    return null;
  }

  @Override
  @Transactional
  public Folder moveFolder(Integer folderId, Integer newParentFolderId) {
    Folder folder = folderRepo.findUniqueByID(folderId);
    Folder newParentFolder = folderRepo.findUniqueByID(newParentFolderId);
    folder.setParentFolder(newParentFolder);

    // If newParentFolder is root folder, then set ParentFolderChain to 0 directly to avoid NPE
    if (newParentFolderId.equals(ROOT_FOLDER_ID)) {
      folderService.updateFolderChainForSubFolders(folderId, ROOT_FOLDER_ID + "_" + folderId);
      folder.setParentFolderChain(newParentFolderId.toString());
      folder.setDateModified(Calendar.getInstance().getTime());

      folderRepo.save(folder);
    } else {

      folderService.updateFolderChainForSubFolders(folderId,
          newParentFolder.getParentFolderChain() + "_" + newParentFolderId + "_" + folderId);
      folder.setParentFolderChain(newParentFolder.getParentFolderChain() + "_" + newParentFolderId);

      // Unless in the root folder, overwrite and inherit access rights from direct parent folder
      Set<GroupFolder> inheritGroupFolders = newParentFolder.getGroupFolders();

      // Apply access rights to to-be-removed-folder and its child folders, and all the logs,
      // processes within
      List<Folder> subFoldersWithCurrentFolders = folderService.getSubFolders(folderId, true);
      List<Integer> folderIds = new ArrayList<>();

      for (Folder f : subFoldersWithCurrentFolders) {
        folderIds.add(f.getId());

        Set<GroupFolder> groupFolders = f.getGroupFolders();
        groupFolders.clear();
        for (GroupFolder gf : inheritGroupFolders) {
          groupFolders.add(new GroupFolder(gf.getGroup(), f, gf.getAccessRights()));
        }
      }
      folderRepo.saveAll(subFoldersWithCurrentFolders);

      List<Process> processes = processRepo.findByFolderIdIn(folderIds);
      for (Process process : processes) {
        Set<GroupProcess> groupProcesses = process.getGroupProcesses();
        groupProcesses.clear();
        for (GroupFolder gf : inheritGroupFolders) {
          groupProcesses.add(new GroupProcess(process, gf.getGroup(), gf.getAccessRights()));
        }
      }
      processRepo.saveAll(processes);

      List<Log> logs = logRepo.findByFolderIdIn(folderIds);
      for (Log log : logs) {
        Set<GroupLog> groupLogs = log.getGroupLogs();
        groupLogs.clear();
        for (GroupFolder gf : inheritGroupFolders) {
          groupLogs.add(new GroupLog(gf.getGroup(), log, gf.getAccessRights()));
        }
      }
      logRepo.saveAll(logs);
    }

    return folder;
  }

  @Override
  public List<Folder> getSingleOwnerFolderByUser(User user) {

    // Get all GroupFolder that associated with specified user's singleton group
    List<GroupFolder> groupFolders = groupFolderRepo.findByGroupId(user.getGroup().getId());
    List<Folder> SingleOwnerFolderList = new ArrayList<>();

    if (null == groupFolders || groupFolders.size() == 0) {
      return SingleOwnerFolderList;
    }

    for (GroupFolder gf : groupFolders) {
      List<GroupFolder> ownerGroupFolders =
          groupFolderRepo.findOwnerByFolderId(gf.getFolder().getId());
      if (ownerGroupFolders.size() == 1) {
        GroupFolder groupFolder = ownerGroupFolders.get(0);

        // If specified user's singleton group is the only owner of the folder
        if (Objects.equals(groupFolder.getGroup().getId(), user.getGroup().getId())) {
          SingleOwnerFolderList.add(groupFolder.getFolder());
        }
      }
    }
    // Sort Folder list from children to parents to avoid error during cascading delete
    return sortFolderByLevel(SingleOwnerFolderList);
  }

  private List<Folder> sortFolderByLevel(List<Folder> folders) {

    if (null == folders || folders.size() == 0) {
      return new ArrayList<>();
    }

    Map<Integer, Folder> folderMap = new TreeMap<>(Comparator.reverseOrder());

    for (Folder f : folders) {
      folderMap.put(StringUtils.countMatches(f.getParentFolderChain(), "_"), f);
    }

    return new ArrayList<>(folderMap.values());
  }

  @Override
  public List<Log> getSingleOwnerLogByUser(User user) {

    // Get all GroupLog that associated with specified user's singleton group
    List<GroupLog> groupLogs = groupLogRepo.findByGroupId(user.getGroup().getId());
    List<Log> SingleOwnerLogList = new ArrayList<>();

    for (GroupLog gf : groupLogs) {
      List<GroupLog> ownerGroupLogs = groupLogRepo.findOwnerByLogId(gf.getLog().getId());
      if (ownerGroupLogs.size() == 1) {
        GroupLog groupLog = ownerGroupLogs.get(0);

        // If specified user's singleton group is the only owner of the Log
        if (Objects.equals(groupLog.getGroup().getId(), user.getGroup().getId())) {
          SingleOwnerLogList.add(groupLog.getLog());
        }
      }
    }
    return SingleOwnerLogList;
  }

  @Override
  public List<Process> getSingleOwnerProcessByUser(User user) {

    // Get all GroupLog that associated with specified user's singleton group
    List<GroupProcess> groupProcesss = groupProcessRepo.findByGroupId(user.getGroup().getId());
    List<Process> SingleOwnerProcessList = new ArrayList<>();

    for (GroupProcess gf : groupProcesss) {
      List<GroupProcess> ownerGroupProcesss =
          groupProcessRepo.findOwnerByProcessId(gf.getProcess().getId());
      if (ownerGroupProcesss.size() == 1) {
        GroupProcess groupProcess = ownerGroupProcesss.get(0);

        // If specified user's singleton group is the only owner of the Process
        if (Objects.equals(groupProcess.getGroup().getId(), user.getGroup().getId())) {
          SingleOwnerProcessList.add(groupProcess.getProcess());
        }
      }
    }
    return SingleOwnerProcessList;
  }

  @Override
  public Boolean isOnlyOwner(User user) {
    List<Folder> folders = getSingleOwnerFolderByUser(user);
    List<Log> logs = getSingleOwnerLogByUser(user);
    List<Process> processes = getSingleOwnerProcessByUser(user);

    return folders.size() > 0 || logs.size() > 0 || processes.size() > 0;

  }

  @Override
  @Transactional
  public void transferOwnership(User sourceUser, User targetUser) {

    List<Folder> folders = getSingleOwnerFolderByUser(sourceUser);
    List<Log> logs = getSingleOwnerLogByUser(sourceUser);
    List<Process> processes = getSingleOwnerProcessByUser(sourceUser);
    Set<CustomCalendar> calendars = customCalendarRepo.findByUser(sourceUser);

    for (Folder f : folders) {
      GroupFolder targetUserGF = groupFolderRepo.findByGroupAndFolder(targetUser.getGroup(), f);
      if (targetUserGF != null) {
        groupFolderRepo.delete(targetUserGF);
      }
      GroupFolder gf = groupFolderRepo.findByGroupAndFolder(sourceUser.getGroup(), f);
      gf.setGroup(targetUser.getGroup());
      groupFolderRepo.save(gf);
    }


    for (Log l : logs) {
      GroupLog targetUserGL = groupLogRepo.findByGroupAndLog(targetUser.getGroup(), l);
      if (targetUserGL != null) {
        groupLogRepo.delete(targetUserGL);
      }
      GroupLog gl = groupLogRepo.findByGroupAndLog(sourceUser.getGroup(), l);
      gl.setGroup(targetUser.getGroup());
      groupLogRepo.save(gl);
      l.setUser(targetUser);
      logRepo.save(l);

      // Update Usermetadata
      Set<Usermetadata> usermetadataSet = l.getUsermetadataSet();
      for (Usermetadata u : usermetadataSet) {
        u.setCreatedBy(targetUser.getRowGuid());
      }
      usermetadataRepo.saveAll(usermetadataSet);
    }

    for (Process p : processes) {
      GroupProcess targetUserGP = groupProcessRepo.findByGroupAndProcess(targetUser.getGroup(), p);
      if (targetUserGP != null) {
        groupProcessRepo.delete(targetUserGP);
      }
      GroupProcess gp = groupProcessRepo.findByGroupAndProcess(sourceUser.getGroup(), p);
      gp.setGroup(targetUser.getGroup());
      groupProcessRepo.save(gp);
      p.setUser(targetUser);
      processRepo.save(p);
      transferProcessModelVersions(p, sourceUser, targetUser);
    }

    for (CustomCalendar c : calendars) {
      c.setUser(targetUser);
      customCalendarRepo.save(c);
    }
  }

  private void transferProcessModelVersions(Process process, User sourceUser, User targetUser) {

    // Only transfer PMVs in MAIN branch in order to avoid non-unique result when query PMV by user
    ProcessBranch branch = process.getProcessBranches().stream()
            .filter(pb -> TRUNK_NAME.equals(pb.getBranchName()))
            .findAny()
            .orElse(null);

    if (branch != null) {
      for (ProcessModelVersion pmv : branch.getProcessModelVersions()) {
        if (pmv.getCreator().equals(sourceUser)) {
          pmv.setCreator(targetUser);
          pmvRepo.save(pmv);
        }
      }
    }
  }

  @Override
  public boolean canDeleteOwnerlessFolder(User user) {

    List<Folder> folders = getSingleOwnerFolderByUser(user);

    if (null == folders || folders.size() == 0) {
      return true;
    }

    List<Integer> folderIds = new ArrayList<>();

    for (Folder f : folders) {
      folderIds.add(f.getId());
    }

    List<Log> logs = logRepo.findByFolderIdIn(folderIds);
    List<Process> processes = processRepo.findByFolderIdIn(folderIds);

    // If folders solely owned by User-To-Be-Deleted but contains log/process co-owned, cannot be
    // deleted, but
    // only transferred.
    for (Log l : logs) {
      Set<GroupLog> groupLogs = l.getGroupLogs();
      for (GroupLog gl : groupLogs) {
        if (!user.getGroup().equals(gl.getGroup())) {
          return false;
        }
      }
    }

    for (Process p : processes) {
      Set<GroupProcess> groupProcesses = p.getGroupProcesses();
      for (GroupProcess gp : groupProcesses) {
        if (!user.getGroup().equals(gp.getGroup())) {
          return false;
        }
      }
    }

    return true;

  }

  @Override
  @Transactional
  public void deleteOwnerlessArtifact(User user) {

    folderRepo.deleteAll(getSingleOwnerFolderByUser(user));
    logRepo.deleteAll(getSingleOwnerLogByUser(user));
    processRepo.deleteAll(getSingleOwnerProcessByUser(user));

  }

  /* Save the Sub Folder Permissions. */
  private void saveSubFolderPermissions(Folder folder, Group group, boolean hasRead,
      boolean hasWrite, boolean hasOwnership) {
    for (Folder subFolder : folder.getSubFolders()) {
      createGroupFolder(group, subFolder, hasRead, hasWrite, hasOwnership);
      saveSubFolderPermissions(subFolder, group, hasRead, hasWrite, hasOwnership);
    }
    for (Process process : folder.getProcesses()) {
      createGroupProcess(group, process, hasRead, hasWrite, hasOwnership);
    }
  }

  /* Delete the sub Folder permissions. */
  private void removeSubFolderPermissions(Folder folder, Group group) {
    for (Folder subFolder : folder.getSubFolders()) {
      removeGroupFolder(group, subFolder);
      removeSubFolderPermissions(subFolder, group);
    }
    for (Process process : folder.getProcesses()) {
      removeGroupProcess(group, process);
    }
  }

  private void createGroupFolder(Group group, Folder folder, boolean hasRead, boolean hasWrite,
      boolean hasOwnership) {
    GroupFolder groupFolder = groupFolderRepo.findByGroupAndFolder(group, folder);
    if (groupFolder == null) {
      groupFolder = new GroupFolder();
      groupFolder.setGroup(group);
      groupFolder.setFolder(folder);
    }
    AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
    groupFolder.setAccessRights(accessRights);

    groupFolderRepo.save(groupFolder);
  }

  private void createGroupProcess(Group group, Process process, boolean hasRead, boolean hasWrite,
      boolean hasOwnership) {
    GroupProcess groupProcess = groupProcessRepo.findByGroupAndProcess(group, process);
    AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
    if (groupProcess == null) {

      groupProcess = new GroupProcess(process, group, accessRights);
      process.getGroupProcesses().add(groupProcess);
      // group.getGroupProcesses().add(groupProcess);
    } else {
      groupProcess.setAccessRights(accessRights);
    }
    groupProcessRepo.save(groupProcess);
  }

  private void createGroupLog(Group group, Log log, boolean hasRead, boolean hasWrite,
      boolean hasOwnership) {
    GroupLog groupLog = groupLogRepo.findByGroupAndLog(group, log);
    AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
    if (groupLog == null) {
      groupLog = new GroupLog(group, log, accessRights);
      log.getGroupLogs().add(groupLog);
      // group.getGroupLogs().add(groupLog);
    } else {
      groupLog.setAccessRights(accessRights);
    }
    groupLogRepo.save(groupLog);
  }

  private void createGroupUsermetadata(Group group, Usermetadata usermetadata, boolean hasRead,
      boolean hasWrite, boolean hasOwnership) {
    GroupUsermetadata groupUsermetadata =
        groupUsermetadataRepo.findByGroupAndUsermetadata(group, usermetadata);
    AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
    if (groupUsermetadata == null) {
      groupUsermetadata = new GroupUsermetadata(group, usermetadata, accessRights);
      usermetadata.getGroupUserMetadata().add(groupUsermetadata);
    } else {
      groupUsermetadata.setAccessRights(accessRights);
    }
    groupUsermetadataRepo.save(groupUsermetadata);
  }

  private void removeGroupFolder(Group group, Folder folder) {
    GroupFolder groupFolder = groupFolderRepo.findByGroupAndFolder(group, folder);
    if (groupFolder != null) {
      groupFolderRepo.delete(groupFolder);
    }
  }

  private void removeGroupProcess(Group group, Process process) {
    GroupProcess groupProcess = groupProcessRepo.findByGroupAndProcess(group, process);
    if (groupProcess != null) {
      groupProcessRepo.delete(groupProcess);
    }
  }

  private void removeGroupLog(Group group, Log log) {
    GroupLog groupLog = groupLogRepo.findByGroupAndLog(group, log);
    if (groupLog != null) {
      groupLogRepo.delete(groupLog);
    }
  }

  private void removeGroupUsermetadata(Group group, Usermetadata usermetadata) {
    GroupUsermetadata groupUsermetadata =
        groupUsermetadataRepo.findByGroupAndUsermetadata(group, usermetadata);
    if (groupUsermetadata != null) {
      groupUsermetadataRepo.delete(groupUsermetadata);
    }
  }

  @Override
  public boolean hasWritePermissionOnFolder(User user, List<Integer> selectedFolders) {
		return selectedFolders.stream().allMatch(folderId -> canUserWriteFolder(user, folderId));
  }

  @Override
  public void updateOwnerAfterDeleteUser(User user) {

    List<GroupFolder> groupFolders = groupFolderRepo.findByGroupId(user.getGroup().getId());
    List<Folder> folderList = groupFolders.stream()
        .map(GroupFolder::getFolder)
        .collect(Collectors.toList());
    folderList.removeAll(getSingleOwnerFolderByUser(user));
    for (Folder folder : sortFolderByLevel(folderList)) {
      List<GroupFolder> ownerGroupFolders = groupFolderRepo.findOwnerByFolderId(folder.getId());
      ownerGroupFolders.sort(Comparator.comparingInt(GroupFolder::getId));
      for (GroupFolder gf : ownerGroupFolders) {
        if (!Objects.equals(gf.getGroup().getId(), user.getGroup().getId())){
          folder.setCreatedBy(userRepo.findByUsername(gf.getGroup().getName()));
        }
      }
    }

    List<GroupLog> groupLogs = groupLogRepo.findByGroupId(user.getGroup().getId());
    List<Log> logList = groupLogs.stream()
        .map(GroupLog::getLog)
        .collect(Collectors.toList());
    logList.removeAll(getSingleOwnerLogByUser(user));
    for (Log log : logList) {
      List<GroupLog> ownerGroupLogs = groupLogRepo.findOwnerByLogId(log.getId());
        ownerGroupLogs.sort(Comparator.comparingInt(GroupLog::getId));
      for (GroupLog gl : ownerGroupLogs) {
        if (!Objects.equals(gl.getGroup().getId(), user.getGroup().getId())){
          log.setUser(userRepo.findByUsername(gl.getGroup().getName()));
        }
      }
    }

    List<GroupProcess> groupProcesses = groupProcessRepo.findByGroupId(user.getGroup().getId());
    List<Process> processList = groupProcesses.stream()
        .map(GroupProcess::getProcess)
        .collect(Collectors.toList());
    processList.removeAll(getSingleOwnerProcessByUser(user));
    for (Process process : processList) {
      List<GroupProcess> ownerGroupProcesses = groupProcessRepo.findOwnerByProcessId(process.getId());
      ownerGroupProcesses.sort(Comparator.comparingInt(GroupProcess::getId));
      for (GroupProcess gp : ownerGroupProcesses) {
        if (!Objects.equals(gp.getGroup().getId(), user.getGroup().getId())){
          process.setUser(userRepo.findByUsername(gp.getGroup().getName()));
        }
      }
    }


  }

}
