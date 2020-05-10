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

import org.apromore.dao.*;
import org.apromore.dao.dataObject.FolderTreeNode;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.service.WorkspaceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Implementation of the SecurityService Contract.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class WorkspaceServiceImpl implements WorkspaceService {

    static private Logger LOGGER = Logger.getLogger(WorkspaceServiceImpl.class.getCanonicalName());

    private WorkspaceRepository workspaceRepo;
    private ProcessRepository processRepo;
    private LogRepository logRepo;
    private FolderRepository folderRepo;
    private UserRepository userRepo;
    private GroupRepository groupRepo;
    private GroupFolderRepository groupFolderRepo;
    private GroupProcessRepository groupProcessRepo;
    private GroupLogRepository groupLogRepo;


    /**
     * Default Constructor allowing Spring to Autowire for testing and normal use.
     * @param workspaceRepository Workspace Repository.
     * @param userRepository User Repository.
     * @param processRepository Process Repository.
     * @param folderRepository Folder Repository.
     */
    @Inject
    public WorkspaceServiceImpl(final WorkspaceRepository workspaceRepository,
                                final UserRepository userRepository,
                                final ProcessRepository processRepository,
                                final LogRepository logRepository,
                                final FolderRepository folderRepository,
                                final GroupRepository groupRepository,
                                final GroupFolderRepository groupFolderRepository,
                                final GroupProcessRepository groupProcessRepository,
                                final GroupLogRepository groupLogRepository) {

        workspaceRepo = workspaceRepository;
        userRepo = userRepository;
        processRepo = processRepository;
        logRepo = logRepository;
        folderRepo = folderRepository;
        groupRepo = groupRepository;
        groupFolderRepo = groupFolderRepository;
        groupProcessRepo = groupProcessRepository;
        groupLogRepo = groupLogRepository;
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
        if(isGEDMatrixReady != null) folder.setGEDMatrixReady(isGEDMatrixReady);
        folder = folderRepo.save(folder);

        GroupFolder gf = new GroupFolder();
        gf.setFolder(folder);
        gf.setGroup(user.getGroup());
        gf.setHasOwnership(true);
        gf.setHasWrite(true);
        gf.setHasRead(true);

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
        if(folderName != null && !folderName.isEmpty()) folder.setName(folderName);
        if(isGEDMatrixReady != null) {
            folder.setGEDMatrixReady(isGEDMatrixReady);
            for(Folder subfolder : folder.getSubFolders()) {
                updateFolder(subfolder.getId(), null, isGEDMatrixReady, user);
            }
        }
    }

    /**
     * @param user  a user
     * @param folderId  identifier for a folder
     * @return whether the <var>user</var> should be allowed to update the folder identified by <var>folderId</var>
     */
    private boolean canUserWriteFolder(User user, Integer folderId) {
        for (GroupFolder gf: groupFolderRepo.findByFolderAndUser(folderId, user.getRowGuid())) {
            if (gf.isHasWrite()) {
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
        return folderRepo.getFolderTreeByUser(0, userId);
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
        for (GroupFolder gf: groupFolderRepo.findByParentFolderAndUser(folderId, userRowGuid)) {
            GroupFolder fu = map.get(gf.getFolder().getId());
            if (fu == null) {
                fu = new GroupFolder();
                fu.setGroup(gf.getGroup());
                fu.setFolder(gf.getFolder());
                fu.setHasRead(gf.isHasRead());
                fu.setHasWrite(gf.isHasWrite());
                fu.setHasOwnership(gf.isHasOwnership());
                folderUsers.add(fu);
                map.put(gf.getFolder().getId(), fu);
            } else {
                fu.setHasRead(fu.isHasRead()           || gf.isHasRead());
                fu.setHasWrite(fu.isHasWrite()         || gf.isHasWrite());
                fu.setHasOwnership(fu.isHasOwnership() || gf.isHasOwnership());
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
    public String removeLogPermissions(Integer logId, String groupRowGuid) {
        Log log = logRepo.findOne(logId);
        Group group = groupRepo.findByRowGuid(groupRowGuid);
        removeGroupLog(group, log);
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
            LOGGER.warning("Missing folderID "+folderId+" Missing processID "+processId);
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
            LOGGER.warning("No public group in repository");
        } else {
            Set<GroupProcess> freshGroupProcesses = new HashSet<>();
            for (GroupProcess groupProcess: process.getGroupProcesses()) {
                if (!publicGroup.equals(groupProcess.getGroup())) {
                    freshGroupProcesses.add(groupProcess);
                }
            }
            process.setGroupProcesses(freshGroupProcesses);
        }
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
        groupFolder.setHasRead(hasRead);
        groupFolder.setHasWrite(hasWrite);
        groupFolder.setHasOwnership(hasOwnership);

        groupFolderRepo.save(groupFolder);
    }

    private void createGroupProcess(Group group, Process process, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        GroupProcess groupProcess = groupProcessRepo.findByGroupAndProcess(group, process);
        if (groupProcess == null) {
            groupProcess = new GroupProcess(process, group, hasRead, hasWrite, hasOwnership);
            process.getGroupProcesses().add(groupProcess);
            //group.getGroupProcesses().add(groupProcess);
        } else {
            groupProcess.setHasRead(hasRead);
            groupProcess.setHasWrite(hasWrite);
            groupProcess.setHasOwnership(hasOwnership);
        }
        groupProcessRepo.save(groupProcess);
    }

    private void createGroupLog(Group group, Log log, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        GroupLog groupLog = groupLogRepo.findByGroupAndLog(group, log);
        if (groupLog == null) {
            groupLog= new GroupLog(group, log, hasRead, hasWrite, hasOwnership);
            log.getGroupLogs().add(groupLog);
            //group.getGroupLogs().add(groupLog);
        } else {
            groupLog.setHasRead(hasRead);
            groupLog.setHasWrite(hasWrite);
            groupLog.setHasOwnership(hasOwnership);
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
