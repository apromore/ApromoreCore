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

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.apromore.common.ConfigBean;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupFolderRepository;
import org.apromore.dao.GroupLogRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.GroupRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.ProcessModelVersionRepository;
import org.apromore.dao.ProcessRepository;
import org.apromore.dao.StorageRepository;
import org.apromore.dao.UserRepository;
import org.apromore.dao.WorkspaceRepository;
import org.apromore.dao.model.AccessRights;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupLog;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessBranch;
import org.apromore.dao.model.ProcessModelAttribute;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.dao.model.Storage;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Workspace;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.EventLogFileService;
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

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class WorkspaceServiceImpl implements WorkspaceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceServiceImpl.class);

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
    private FolderServiceImpl folderService;
    private StorageRepository storageRepository;

    private StorageManagementFactory<StorageClient> storageFacotry;

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
	    final FolderServiceImpl folderService,
	    final StorageManagementFactory storageFacotry,
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
	this.storageFacotry = storageFacotry;
	this.storageRepository=storageRepository;
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
    public void createFolder(String userId, String folderName, Integer parentFolderId, Boolean isGEDMatrixReady) {
	Folder folder = new Folder();
	folder.setName(folderName);
	User user = userRepo.findByRowGuid(userId);

	if (parentFolderId != 0) {
	    Folder parent = folderRepo.findOne(parentFolderId);
	    if (parent != null) {
		folder.setParentFolder(parent);
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
	folder = folderRepo.save(folder);

	GroupFolder gf = new GroupFolder();
	gf.setFolder(folder);
	gf.setGroup(user.getGroup());
	AccessRights accessRights = new AccessRights();
	accessRights.setOwnerShip(true);
	accessRights.setWriteOnly(true);
	accessRights.setReadOnly(true);
	gf.setAccessRights(accessRights);

	groupFolderRepo.save(gf);
    }

    @Override
    public boolean isGEDReadyFolder(Integer folderId) {
	Folder folder = folderRepo.findOne(folderId);
	return folder.isGEDMatrixReady();
    }

    @Override
    public void updateFolder(Integer folderId, String folderName, Boolean isGEDMatrixReady, User user) throws NotAuthorizedException {
	if (!canUserWriteFolder(user, folderId)) {
	    throw new NotAuthorizedException("User " + user.getUsername() + " is not permitted to delete folder with id " + folderId);
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
     *         identified by <var>folderId</var>
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
    public void deleteFolder(Integer folderId, User user) throws NotAuthorizedException {
	if (!canUserWriteFolder(user, folderId)) {
	    throw new NotAuthorizedException("User " + user.getUsername() + " is not permitted to delete folder with id " + folderId);
	}
	Folder folder = folderRepo.findOne(folderId);
	if (folder != null) {
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
    public String saveFolderPermissions(Integer folderId, String groupRowGuid, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
	Folder folder = folderRepo.findOne(folderId);
	Group group = groupRepo.findByRowGuid(groupRowGuid);

	createGroupFolder(group, folder, hasRead, hasWrite, hasOwnership);

	Folder parentFolder = folder.getParentFolder();
	while (parentFolder != null && parentFolder.getId() > 0) {
	    parentFolder = folderRepo.findOne(parentFolder.getId());
	    createGroupFolder(group, parentFolder, true, false, false);
	    parentFolder = parentFolder.getParentFolder();
	}
	saveSubFolderPermissions(folder, group, hasRead, hasWrite, hasOwnership);

	return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String removeFolderPermissions(Integer folderId, String groupRowGuid) {
	Folder folder = folderRepo.findOne(folderId);
	Group group = groupRepo.findByRowGuid(groupRowGuid);
	removeGroupFolder(group, folder);
	removeSubFolderPermissions(folder, group);
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
    public String removeLogPermissions(Integer logId, String groupRowGuid, String username) throws UserNotFoundException {
	Log log = logRepo.findOne(logId);
	Group group = groupRepo.findByRowGuid(groupRowGuid);
	removeGroupLog(group, log);

	// Sync permission with user metadata that linked to specified log
//        userMetadataServ.removeUserMetadataAccessRightsByLogAndGroup(logId, groupRowGuid, username);

	return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String saveProcessPermissions(Integer processId, String groupRowGuid, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
	Process process = processRepo.findOne(processId);
	Group group = groupRepo.findByRowGuid(groupRowGuid);

	createGroupProcess(group, process, hasRead, hasWrite, hasOwnership);

	Folder parentFolder = process.getFolder();
	while (parentFolder != null && parentFolder.getId() > 0) {
	    parentFolder = folderRepo.findOne(parentFolder.getId());
	    createGroupFolder(group, parentFolder, true, false, false);
	    parentFolder = parentFolder.getParentFolder();
	}

	return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String saveLogPermissions(Integer logId, String groupRowGuid, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
	Log log = logRepo.findOne(logId);
	Group group = groupRepo.findByRowGuid(groupRowGuid);

	createGroupLog(group, log, hasRead, hasWrite, hasOwnership);

	Folder parentFolder = log.getFolder();
	while (parentFolder != null && parentFolder.getId() > 0) {
	    parentFolder = folderRepo.findOne(parentFolder.getId());
	    createGroupFolder(group, parentFolder, true, false, false);
	    parentFolder = parentFolder.getParentFolder();
	}

	return "";
    }

    @Override
    @Transactional
    public String saveLogAccessRights(Integer logId, String groupRowGuid, AccessType accessType, boolean shareUserMetadata) {
	Log log = logRepo.findOne(logId);
	Group group = groupRepo.findByRowGuid(groupRowGuid);

	createGroupLog(group, log, accessType.isRead(), accessType.isWrite(), accessType.isOwner());

	Folder parentFolder = log.getFolder();
	while (parentFolder != null && parentFolder.getId() > 0) {
	    parentFolder = folderRepo.findOne(parentFolder.getId());
	    createGroupFolder(group, parentFolder, true, false, false);
	    parentFolder = parentFolder.getParentFolder();
	}

	if (shareUserMetadata) {
	    // Sync permission with user metadata that linked to specified log
//            userMetadataServ.saveUserMetadataAccessRightsByLogAndGroup(logId, groupRowGuid, accessType);
	} else {
	    // Automatically share simulation user metadata when the log is shared
//            userMetadataServ.shareSimulationMetadata(logId, groupRowGuid, accessType);
	}

	return "";
    }

    @Override
    @Transactional(readOnly = false)
    public void addProcessToFolder(Integer processId, Integer folderId) {
	if (folderId != null && processId != null) {
	    Process process = processRepo.findOne(processId);
	    Folder folder = folderRepo.findOne(folderId);

	    process.setFolder(folder);
//            if (folder != null) {
//                folder.addFolderProcess(process);
//            }

	    processRepo.save(process);
	} else {
	    LOGGER.warn("Missing folderID " + folderId + " Missing processID " + processId);
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
	groupLogs.add(new GroupLog(newUser.getGroup(), newLog, new AccessRights(true, true, true)));
	if (isPublic) {
	    Group publicGroup = groupRepo.findPublicGroup();
	    if (publicGroup == null) {
		LOGGER.warn("No public group present in repository");
	    } else {
		groupLogs.add(new GroupLog(publicGroup, newLog, new AccessRights(true, true, false)));
	    }
	}
	newLog.setGroupLogs(groupLogs);

	if (currentLog.getStorage() != null) {
	    newLog.setStorage(currentLog.getStorage());
	}

	// Copy file
	StorageClient storageClient = storageFacotry.getStorageClient(config.getStoragePath());
//        For backward compatible
	final String currentFileFullName = currentLog.getFilePath() + "_" + currentLog.getName() + ".xes.gz";
	if (currentLog.getStorage() == null) {
	    
	    StorageClient storageClientOldFile = storageFacotry.getStorageClient("FILE"+StorageType.STORAGE_PATH_SEPARATOR + config.getLogsDir());
	    
	    OutputStream outputStream = storageClient.getOutputStream("log", currentFileFullName);
	    InputStream inputStream = storageClientOldFile.getInputStream(null, currentFileFullName);
	    logFileService.copyFile(inputStream, outputStream);	    
	    Storage storage=new Storage();
	    storage.setKey(currentFileFullName);
	    storage.setPrefix("log");
	    storage.setStoragePath(config.getStoragePath());
	    newLog.setStorage(storageRepository.saveAndFlush(storage));
	}

	// Persist
	try {
	    logRepo.save(newLog);
	} catch (Exception e) {
//	    log something
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
	logRepo.save(log);
	return log;
    }

    @Override
    @Transactional(readOnly = false)
    public Process copyProcessVersions(Integer processId, List<String> pmvVersions, Integer newFolderId, String userName, boolean isPublic) throws Exception {
	Folder newFolder = folderRepo.findUniqueByID(newFolderId);
	User newUser = userRepo.findByUsername(userName);

	Process process = processRepo.findUniqueByID(processId);
	Process newProcess = process.clone();

	ProcessBranch branch = process.getProcessBranches().get(0);
	ProcessBranch newBranch = branch.clone();

	List<ProcessModelVersion> newPMVList = this.createNewPMVs(process.getId(), pmvVersions, branch, newBranch);
	if (newPMVList.isEmpty()) {
	    throw new Exception("No process model versions were found for processId=" + process.getId() +
		    "and versions=" + pmvVersions.toString());
	}

	newBranch.setProcess(newProcess);
	newBranch.setProcessModelVersions(newPMVList);
	newBranch.setCurrentProcessModelVersion(newPMVList.get(newPMVList.size() - 1));

	newProcess.getProcessBranches().clear();
	newProcess.setProcessBranches(Arrays.asList(new ProcessBranch[] { newBranch }));
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
	newProcess.setGroupProcesses(groupProcesses);

	processRepo.save(newProcess);
	for (ProcessModelVersion pmv : newPMVList) {
	    pmvRepo.save(pmv);
	}

	return newProcess;
    }

    @Override
    @Transactional(readOnly = false)
    public Process copyProcess(Integer processId, Integer newFolderId, String userName, boolean isPublic) throws Exception {
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
    public Folder moveFolder(Integer folderId, Integer newParentFolderId) throws Exception {
	Folder folder = folderRepo.findUniqueByID(folderId);
	Folder newParentFolder = folderRepo.findUniqueByID(newParentFolderId);
	folder.setParentFolder(newParentFolder);
	folderRepo.save(folder);
	return folder;
    }

    /* Save the Sub Folder Permissions. */
    private void saveSubFolderPermissions(Folder folder, Group group, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
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

    private void createGroupFolder(Group group, Folder folder, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
	GroupFolder groupFolder = groupFolderRepo.findByGroupAndFolder(group, folder);
	if (groupFolder == null) {
	    groupFolder = new GroupFolder();
	    groupFolder.setGroup(group);
	    groupFolder.setFolder(folder);
	}
	assert groupFolder != null;
	AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
	groupFolder.setAccessRights(accessRights);

	groupFolderRepo.save(groupFolder);
    }

    private void createGroupProcess(Group group, Process process, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
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
