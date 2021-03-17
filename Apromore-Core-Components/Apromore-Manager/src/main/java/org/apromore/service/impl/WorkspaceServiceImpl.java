/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

import org.apromore.common.ConfigBean;
import org.apromore.dao.*;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.*;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.EventLogFileService;
import org.apromore.service.EventLogService;
import org.apromore.service.FolderService;
import org.apromore.service.WorkspaceService;
import org.apromore.service.model.FolderTreeNode;
import org.apromore.storage.StorageClient;
import org.apromore.storage.StorageType;
import org.apromore.storage.factory.StorageManagementFactory;
import org.apromore.util.AccessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor =
        Exception.class)
public class WorkspaceServiceImpl implements WorkspaceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceServiceImpl.class);
    private static final Integer ROOT_FOLDER_ID = 0;

    private WorkspaceRepository workspaceRepo;
    private ProcessRepository processRepo;
    private ProcessModelVersionRepository pmvRepo;
    private LogRepository logRepo;
    private FolderRepository folderRepo;
    private UserRepository userRepo;
    private GroupRepository groupRepo;
    private GroupFolderRepository groupFolderRepo;
    private GroupProcessRepository groupProcessRepo;
    private GroupLogRepository groupLogRepo;
    private EventLogFileService logFileService;
    private FolderService folderService;
    private StorageRepository storageRepository;
    private EventLogService eventLogService;

    private StorageManagementFactory<StorageClient> storageFactory;

    @Resource
    private ConfigBean config;

    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     *
     * @param workspaceRepository Workspace Repository.
     * @param userRepository      User Repository.
     * @param processRepository   Process Repository.
     * @param folderRepository    Folder Repository.
     */
    @Inject
    public WorkspaceServiceImpl(final WorkspaceRepository workspaceRepository,
                                final UserRepository userRepository,
                                final ProcessRepository processRepository,
                                final ProcessModelVersionRepository pmvRepository,
                                final LogRepository logRepository,
                                final FolderRepository folderRepository,
                                final GroupRepository groupRepository,
                                final GroupFolderRepository groupFolderRepository,
                                final GroupProcessRepository groupProcessRepository,
                                final GroupLogRepository groupLogRepository,
                                final EventLogFileService eventLogFileService,
                                final FolderService folderService,
                                final StorageManagementFactory storageFactory,
                                final EventLogService eventLogService,
                                final StorageRepository storageRepository) {

        workspaceRepo = workspaceRepository;
        userRepo = userRepository;
        processRepo = processRepository;
        pmvRepo = pmvRepository;
        logRepo = logRepository;
        folderRepo = folderRepository;
        groupRepo = groupRepository;
        groupFolderRepo = groupFolderRepository;
        groupProcessRepo = groupProcessRepository;
        groupLogRepo = groupLogRepository;
        logFileService = eventLogFileService;
        this.folderService = folderService;
        this.storageFactory = storageFactory;
        this.eventLogService = eventLogService;
        this.storageRepository = storageRepository;
    }

    @Override
    public Folder getFolder(Integer folderId) {
        return folderRepo.findOne(folderId);
    }

    @Override
    public List<GroupFolder> getGroupFolders(Integer folderId) {
        return groupFolderRepo.findByFolderId(folderId);
    }

    @Override
    public List<GroupProcess> getGroupProcesses(Integer processId) {
        return groupProcessRepo.findByProcessId(processId);
    }

    @Override
    public List<GroupLog> getGroupLogs(Integer logId) {
        return groupLogRepo.findByLogId(logId);
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
    public Page<Log> getLogs(String userId, Integer folderId, Pageable pageable) {
        return (folderId == 0) ? logRepo.findRootLogsByUser(userId, pageable)
                : logRepo.findAllLogsInFolderForUser(folderId, userId, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Integer createFolder(String userId, String folderName, Integer parentFolderId, Boolean isGEDMatrixReady) {
        Folder folder = new Folder();
        folder.setName(folderName);
        folder.setParentFolderChain("0");
        User user = userRepo.findByRowGuid(userId);

        if (parentFolderId != 0) {
            Folder parent = folderRepo.findOne(parentFolderId);
            if (parent != null) {
                folder.setParentFolder(parent);
                folder.setParentFolderChain(parent.getParentFolderChain() + "_" + parent.getId());
            }
        }

        Workspace workspace = workspaceRepo.findOne(1);
        folder.setWorkspace(workspace);
        folder.setCreatedBy(user);
        folder.setModifiedBy(user);
        folder.setDateCreated(Calendar.getInstance().getTime());
        folder.setDateModified(Calendar.getInstance().getTime());
        folder.setDescription("");
        if (isGEDMatrixReady != null)
            folder.setGEDMatrixReady(isGEDMatrixReady);
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
            Folder parent = folderRepo.findOne(parentFolderId);
            if (parent != null) {
                for (GroupFolder groupFolder : parent.getGroupFolders()) {
                    if (!Objects.equals(groupFolder.getGroup().getId(), user.getGroup().getId())) { // Avoid adding operating user twice
                        groupFolders.add(new GroupFolder(groupFolder.getGroup(), folder, groupFolder.getAccessRights()));
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
        Folder folder = folderRepo.findOne(folderId);
        return folder.isGEDMatrixReady();
    }

    @Override
    public void updateFolder(Integer folderId, String folderName, Boolean isGEDMatrixReady, User user)
            throws NotAuthorizedException {
        if (!canUserWriteFolder(user, folderId)) {
            throw new NotAuthorizedException(
                    "User " + user.getUsername() + " is not permitted to delete folder with id " + folderId);
        }
        Folder folder = folderRepo.findOne(folderId);
        if (folderName != null && !folderName.isEmpty())
            folder.setName(folderName);
        if (isGEDMatrixReady != null) {
            folder.setGEDMatrixReady(isGEDMatrixReady);
            for (Folder subfolder : folder.getSubFolders()) {
                updateFolder(subfolder.getId(), null, isGEDMatrixReady, user);
            }
        }
        folderRepo.save(folder);
    }

    /**
     * @param user     a user
     * @param folderId identifier for a folder
     * @return whether the <var>user</var> should be allowed to update the folder
     * identified by <var>folderId</var>
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
        Folder folder = folderRepo.findOne(folderId);
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

        Folder folder = folderRepo.findOne(folderId);
        if (folder != null) {
            folders.add(folder);
            while (folder.getParentFolder() != null && folder.getParentFolder().getId() != 0) {
                folder = folderRepo.findOne(folder.getParentFolder().getId());
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
    public String saveFolderPermissions(Integer folderId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                                        boolean hasOwnership) {

        List<Folder> subFoldersWithCurrentFolders = folderService.getSubFolders(folderId, true);
        Group group = groupRepo.findByRowGuid(groupRowGuid);
        List<Integer> folderIds = new ArrayList<>();

        for (Folder folder : subFoldersWithCurrentFolders) {
            createGroupFolder(group, folder, hasRead, hasWrite, hasOwnership);
            folderIds.add(folder.getId());
        }

        List<Folder> parentFolders = folderService.getParentFolders(folderId);
        for (Folder folder : parentFolders) {
            createGroupFolder(group, folder, true, false, false);
        }

        for (Process process : processRepo.findByFolderIdIn(folderIds)) {
            createGroupProcess(group, process, hasRead, hasWrite, hasOwnership);
        }

        for (Log log : logRepo.findByFolderIdIn(folderIds)) {
            createGroupLog(group, log, hasRead, hasWrite, hasOwnership);
        }

        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String removeFolderPermissions(Integer folderId, String groupRowGuid) {

//	
        List<Folder> subFoldersWithCurrentFolders = folderService.getSubFolders(folderId, true);
        Group group = groupRepo.findByRowGuid(groupRowGuid);
        List<Integer> folderIds = new ArrayList<Integer>();

        for (Folder folder : subFoldersWithCurrentFolders) {
            folderIds.add(folder.getId());
            removeGroupFolder(group, folder);
        }

        for (Process process : processRepo.findByFolderIdIn(folderIds)) {
            removeGroupProcess(group, process);
        }

        for (Log log : logRepo.findByFolderIdIn(folderIds)) {
            removeGroupLog(group, log);
        }
        return "";

    }

    @Override
    @Transactional(readOnly = false)
    public String removeProcessPermissions(Integer processId, String groupRowGuid) {
        Process process = processRepo.findOne(processId);
        Group group = groupRepo.findByRowGuid(groupRowGuid);
        removeGroupProcess(group, process);
        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String removeLogPermissions(Integer logId, String groupRowGuid, String username)
            throws UserNotFoundException {
        Log log = logRepo.findOne(logId);
        Group group = groupRepo.findByRowGuid(groupRowGuid);
        removeGroupLog(group, log);

        // Sync permission with user metadata that linked to specified log
//        userMetadataServ.removeUserMetadataAccessRightsByLogAndGroup(logId, groupRowGuid, username);

        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String saveProcessPermissions(Integer processId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                                         boolean hasOwnership) {
        Process process = processRepo.findOne(processId);
        Group group = groupRepo.findByRowGuid(groupRowGuid);
        createGroupProcess(group, process, hasRead, hasWrite, hasOwnership);

        Folder parentFolder = process.getFolder();
        setReadOnlyParentFolders(group, parentFolder);

        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String saveLogPermissions(Integer logId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                                     boolean hasOwnership) {
        Log log = logRepo.findOne(logId);
        Group group = groupRepo.findByRowGuid(groupRowGuid);

        createGroupLog(group, log, hasRead, hasWrite, hasOwnership);

        Folder parentFolder = log.getFolder();
        setReadOnlyParentFolders(group, parentFolder);

        return "";
    }

    private void setReadOnlyParentFolders(Group group, Folder parentFolder) {
        if (parentFolder != null) {
            List<Folder> parentFolders = folderService.getParentFolders(parentFolder.getId());
            parentFolders.add(parentFolder);
            for (Folder folder : parentFolders) {
                GroupFolder groupFolder = groupFolderRepo.findByGroupAndFolder(group, folder);
                if (groupFolder == null) { // Set read access only when specified group doesn't have access to parent
                    // folder
                    createGroupFolder(group, folder, true, false, false);
                }
            }
        }
    }

    @Override
    @Transactional
    public String saveLogAccessRights(Integer logId, String groupRowGuid, AccessType accessType,
                                      boolean shareUserMetadata) {
        Log log = logRepo.findOne(logId);
        Group group = groupRepo.findByRowGuid(groupRowGuid);

        createGroupLog(group, log, accessType.isRead(), accessType.isWrite(), accessType.isOwner());

        Folder parentFolder = log.getFolder();
        setReadOnlyParentFolders(group, parentFolder);

        // shareUserMetadata flag is disabled for now

        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public void addProcessToFolder(User user, Integer processId, Integer folderId) {
        if (folderId != null && processId != null) {
            Process process = processRepo.findOne(processId);
            Folder folder = folderRepo.findOne(folderId);

            process.setFolder(folder);

            // Add the user's personal group is done in org.apromore.service.impl.ProcessServiceImpl.insertProcess

            // Unless in the root folder, add access rights of its immediately enclosing folder
            if (folder != null) {
                Set<GroupProcess> groupProcesses = process.getGroupProcesses();
                Set<GroupFolder> groupFolders = folder.getGroupFolders();
                for (GroupFolder gf : groupFolders) {
                    if (!Objects.equals(gf.getGroup().getId(), user.getGroup().getId())) { // Avoid adding operating
                        // user twice
                        groupProcesses.add(new GroupProcess(process, gf.getGroup(), gf.getAccessRights()));
                    }
                }
                process.setGroupProcesses(groupProcesses);
            }
            processRepo.save(process);

        } else {
            LOGGER.warn("Missing folderID " + folderId + " Missing processID " + processId);
        }
    }

    /**
     * @see org.apromore.service.WorkspaceService#createPublicStatusForUsers(org.apromore.dao.model.Process)
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = false)
    public void createPublicStatusForUsers(final Process process) {
        createGroupProcess(groupRepo.findPublicGroup(), process, true, false, false);
    }

    /**
     * @see org.apromore.service.WorkspaceService#removePublicStatusForUsers(org.apromore.dao.model.Process)
     * {@inheritDoc}
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
    public Log copyLog(Integer logId, Integer newFolderId, String userName, boolean isPublic) throws Exception {
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
        newLog.setUser(currentLog.getUser());
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
                if (!Objects.equals(gf.getGroup().getId(), newUser.getGroup().getId())) { // Avoid adding operating user twice
                    groupLogs.add(new GroupLog(gf.getGroup(), newLog, gf.getAccessRights()));
                }
            }
        }
        newLog.setGroupLogs(groupLogs);

        if (currentLog.getStorage() != null) {
            newLog.setStorage(currentLog.getStorage());
        }

        // Copy file
        StorageClient storageClient = storageFactory.getStorageClient(config.getStoragePath());
        // For backward compatible
        final String currentFileFullName = currentLog.getFilePath() + "_" + currentLog.getName() + ".xes.gz";
        if (currentLog.getStorage() == null) {
            // change spelling of factory
            StorageClient storageClientOldFile = storageFactory
                    .getStorageClient("FILE" + StorageType.STORAGE_PATH_SEPARATOR + config.getLogsDir());

            OutputStream outputStream = storageClient.getOutputStream("log", currentFileFullName);
            InputStream inputStream = storageClientOldFile.getInputStream(null, currentFileFullName);
            logFileService.copyFile(inputStream, outputStream);
            Storage storage = new Storage();
            storage.setKey(currentFileFullName);
            storage.setPrefix("log");
            storage.setStoragePath(config.getStoragePath());
            newLog.setStorage(storageRepository.saveAndFlush(storage));
        }

        // Persist
        try {
            logRepo.save(newLog);
        } catch (Exception e) {
            // log something
            storageClient.delete("log", currentFileFullName);
        }

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
    public Process copyProcessVersions(Integer processId, List<String> pmvVersions, Integer newFolderId,
                                       String userName, boolean isPublic) throws Exception {
        Folder newFolder = folderRepo.findUniqueByID(newFolderId);
        User newUser = userRepo.findByUsername(userName);

        Process process = processRepo.findUniqueByID(processId);
        Process newProcess = process.clone();

        ProcessBranch branch = process.getProcessBranches().get(0);
        ProcessBranch newBranch = branch.clone();

        List<ProcessModelVersion> newPMVList = this.createNewPMVs(process.getId(), pmvVersions, branch, newBranch);
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
        groupProcesses.add(new GroupProcess(newProcess, newUser.getGroup(), new AccessRights(true, true, true)));
        if (isPublic) {
            Group publicGroup = groupRepo.findPublicGroup();
            if (publicGroup == null) {
                LOGGER.warn("No public group present in repository");
            } else {
                groupProcesses.add(new GroupProcess(newProcess, publicGroup, new AccessRights(true, true, false)));
            }
        }
        // Unless in the root folder, add access rights of its immediately enclosing folder
        if (newFolder != null) {
            for (GroupFolder gf : newFolder.getGroupFolders()) {
                if (!Objects.equals(gf.getGroup().getId(), newUser.getGroup().getId())) { // Avoid adding operating user twice
                    groupProcesses.add(new GroupProcess(newProcess, gf.getGroup(), gf.getAccessRights()));
                }
            }
        }
        newProcess.setGroupProcesses(groupProcesses);

        processRepo.save(newProcess);
        for (ProcessModelVersion pmv : newPMVList) {
            pmvRepo.save(pmv);
        }

        return newProcess;
    }

    @Override
    @Transactional(readOnly = false)
    public Process copyProcess(Integer processId, Integer newFolderId, String userName, boolean isPublic)
            throws Exception {
        Process process = processRepo.findUniqueByID(processId);
        ProcessBranch branch = process.getProcessBranches().get(0);
        List<String> pmvVersions = new ArrayList<>();
        for (ProcessModelVersion pmv : branch.getProcessModelVersions()) {
            pmvVersions.add(pmv.getVersionNumber());
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
                groupProcesses.add(new GroupProcess(process, gf.getGroup(),  gf.getAccessRights()));
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
                newPMV.setNativeDocument(pmv.getNativeDocument().clone());
                newPMV.getProcessModelAttributes().clear();
                for (ProcessModelAttribute pma : pmv.getProcessModelAttributes()) {
                    newPMV.getProcessModelAttributes().add(pma.clone());
                }
                pmvs.add(newPMV);
            }
        }
        return pmvs;
    }

    @Override
    public Folder copyFolder(Integer folderId, Integer sourceFolderId, Integer targetFolderId) throws Exception {
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

            folderRepo.save(folder);
        } else {

            folderService.updateFolderChainForSubFolders(folderId,
                    newParentFolder.getParentFolderChain() + "_" + newParentFolderId + "_" + folderId);
            folder.setParentFolderChain(newParentFolder.getParentFolderChain() + "_" + newParentFolderId);

            // Unless in the root folder, overwrite and inherit access rights from direct parent folder
            Set<GroupFolder> inheritGroupFolders = newParentFolder.getGroupFolders();

            // Apply access rights to to-be-removed-folder and its child folders, and all the logs, processes within
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
            folderRepo.save(subFoldersWithCurrentFolders);

            List<Process> processes = processRepo.findByFolderIdIn(folderIds);
            for (Process process : processes) {
                Set<GroupProcess> groupProcesses = process.getGroupProcesses();
                groupProcesses.clear();
                for (GroupFolder gf : inheritGroupFolders) {
                    groupProcesses.add(new GroupProcess(process, gf.getGroup(), gf.getAccessRights()));
                }
            }
            processRepo.save(processes);

            List<Log> logs = logRepo.findByFolderIdIn(folderIds);
            for (Log log : logs) {
                Set<GroupLog> groupLogs = log.getGroupLogs();
                groupLogs.clear();
                for (GroupFolder gf : inheritGroupFolders) {
                    groupLogs.add(new GroupLog(gf.getGroup(), log, gf.getAccessRights()));
                }
            }
            logRepo.save(logs);
        }

        return folder;
    }

    @Override
    public List<Folder> getSingleOwnerFolderByUser(User user) {

        // Get all GroupFolder that associated with specified user's singleton group
        List<GroupFolder> groupFolders = groupFolderRepo.findByGroupId(user.getGroup().getId());
        List<Folder> SingleOwnerFolderList = new ArrayList<>();

        for (GroupFolder gf : groupFolders) {
            List<GroupFolder> ownerGroupFolders = groupFolderRepo.findOwnerByFolderId(gf.getFolder().getId());
            if (ownerGroupFolders.size() == 1) {
                GroupFolder groupFolder = ownerGroupFolders.get(0);
                
                // If specified user's singleton group is the only owner of the folder
                if (Objects.equals(groupFolder.getGroup().getId(), user.getGroup().getId())) {
                    SingleOwnerFolderList.add(groupFolder.getFolder());
                }
            }
        }
        return SingleOwnerFolderList;
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
            List<GroupProcess> ownerGroupProcesss = groupProcessRepo.findOwnerByProcessId(gf.getProcess().getId());
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

        for (Folder f : folders) {
            GroupFolder gf = groupFolderRepo.findByGroupAndFolder(sourceUser.getGroup(), f);
            gf.setGroup(targetUser.getGroup());
            groupFolderRepo.save(gf);
        }

        for (Log l : logs) {
            GroupLog gl = groupLogRepo.findByGroupAndLog(sourceUser.getGroup(), l);
            gl.setGroup(targetUser.getGroup());
            groupLogRepo.save(gl);
        }

        for (Process p : processes) {
            GroupProcess gp = groupProcessRepo.findByGroupAndProcess(sourceUser.getGroup(), p);
            gp.setGroup(targetUser.getGroup());
            groupProcessRepo.save(gp);
        }
    }

    @Override
    @Transactional
    public void deleteOwnerlessArtifact(User user) {

        folderRepo.delete(getSingleOwnerFolderByUser(user));
        logRepo.delete(getSingleOwnerLogByUser(user));
        processRepo.delete(getSingleOwnerProcessByUser(user));

    }

    /* Save the Sub Folder Permissions. */
    private void saveSubFolderPermissions(Folder folder, Group group, boolean hasRead, boolean hasWrite,
                                          boolean hasOwnership) {
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

    private void createGroupLog(Group group, Log log, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
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

}
