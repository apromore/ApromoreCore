/*-
 * #%L
 * This file is part of "Apromore Core".
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

import org.apromore.dao.*;
import org.apromore.dao.model.*;
import org.apromore.dao.model.Process;
import org.apromore.service.AuthorizationService;
import org.apromore.service.FolderService;
import org.apromore.service.WorkspaceService;
import org.apromore.util.AccessType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class AuthorizationServiceImpl implements AuthorizationService {

    private GroupUsermetadataRepository groupUsermetadataRepository;
    private UsermetadataRepository usermetadataRepository;
    private LogRepository logRepository;
    private FolderService folderService;
    private ProcessRepository processRepository;
    private FolderRepository folderRepository;
    private GroupRepository groupRepository;
    private GroupLogRepository groupLogRepository;
    private GroupProcessRepository groupProcessRepository;
    private GroupFolderRepository groupFolderRepository;

    @Inject
    public AuthorizationServiceImpl(final GroupUsermetadataRepository groupUsermetadataRepository,
                                    final UsermetadataRepository usermetadataRepository,
                                    final LogRepository logRepository,
                                    final FolderService folderService,
                                    final ProcessRepository processRepository,
                                    final FolderRepository folderRepository,
                                    final GroupRepository groupRepository,
                                    final GroupLogRepository groupLogRepository,
                                    final GroupProcessRepository groupProcessRepository,
                                    final GroupFolderRepository groupFolderRepository) {
        this.groupUsermetadataRepository = groupUsermetadataRepository;
        this.usermetadataRepository = usermetadataRepository;
        this.logRepository = logRepository;
        this.folderService = folderService;
        this.processRepository = processRepository;
        this.folderRepository = folderRepository;
        this.groupRepository = groupRepository;
        this.groupLogRepository = groupLogRepository;
        this.groupProcessRepository = groupProcessRepository;
        this.groupFolderRepository = groupFolderRepository;
    }

    @Override
    public List<GroupFolder> getGroupFolders(Integer folderId) {
        return groupFolderRepository.findByFolderId(folderId);
    }

    @Override
    public List<GroupProcess> getGroupProcesses(Integer processId) {
        return groupProcessRepository.findByProcessId(processId);
    }

    @Override
    public List<GroupLog> getGroupLogs(Integer logId) {
        return groupLogRepository.findByLogId(logId);
    }

    @Override
    public Map<Group, AccessType> getLogAccessType(Integer logId) {

        Map<Group, AccessType> groupAccessTypeMap = new HashMap<>();

        List<GroupLog> groupLogs = getGroupLogs(logId);

        for (GroupLog g : groupLogs) {
            AccessRights accessRights = g.getAccessRights();
            groupAccessTypeMap.put(g.getGroup(), getAccessType(accessRights));
        }

        return groupAccessTypeMap;
    }

    @Override
    public AccessType getLogAccessTypeByUser(Integer logId, User user) {

        Map<Group, AccessType> accessTypeMap = getLogAccessType(logId);

        List<AccessType> accessTypes = new ArrayList<>();

        for (Group g : user.getGroups()) {
            if (accessTypeMap.containsKey(g)) {
                accessTypes.add(accessTypeMap.get(g));
            }
        }

        return getLeastRestrictiveAccessType(accessTypes);
    }

    public AccessType getLogAccessTypeByGroup(Integer logId, Group group) {

        Map<Group, AccessType> accessTypeMap = getLogAccessType(logId);

        for (Map.Entry<Group, AccessType> entry : accessTypeMap.entrySet()) {
            if(group.equals(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null;
    }

    @Override
    public Map<Group, AccessType> getLogAccessTypeAndGroupByUser(Integer logId, User user) {

        Map<Group, AccessType> accessTypeMap = getLogAccessType(logId);

        Map<Group, AccessType> result = new HashMap<>();

        for (Group g : user.getGroups()) {
            if (accessTypeMap.containsKey(g)) {
                result.put(g, accessTypeMap.get(g));
            }
        }

        return getLeastRestrictiveAccessTypeAndGroup(result);
    }

    @Override
    public AccessType getLogsAccessTypeByUser(Set<Log> logSet, User user) {

        List<AccessType> accessTypes = new ArrayList<>();

        for (Log l : logSet) {
            AccessType at = getLogAccessTypeByUser(l.getId(), user);
            if (at == null) {
                return null; // If specified user can't access to one of the multi-log, then return null
            }
            accessTypes.add(at);
        }

        return getMostRestrictiveAccessType(accessTypes);
    }

    public AccessType getLogsAccessTypeByGroup(Set<Log> logSet, Group group) {

        List<AccessType> accessTypes = new ArrayList<>();

        for (Log l : logSet) {
            AccessType at = getLogAccessTypeByGroup(l.getId(), group);
            if (at == null) {
                return null; // If specified group can't access to one of the multi-log, then return null
            }
            accessTypes.add(at);
        }

        return getMostRestrictiveAccessType(accessTypes);
    }

    @Override
    public AccessType getLogsAccessTypeByUser(List<Integer> logIds, User user) {
        Set<Log> logSet = new HashSet<>();

        for (Integer logId : logIds) {
            logSet.add(logRepository.findUniqueByID(logId));
        }

        return getLogsAccessTypeByUser(logSet, user);
    }

    public AccessType getLeastRestrictiveAccessType(List<AccessType> accessTypes) {

        if (accessTypes == null || accessTypes.size() == 0) {
            return null;
        }

        if (accessTypes.contains(AccessType.OWNER)) {
            return AccessType.OWNER;
        } else if (accessTypes.contains(AccessType.EDITOR)) {
            return AccessType.EDITOR;
        } else if (accessTypes.contains(AccessType.VIEWER)) {
            return AccessType.VIEWER;
        } else if (accessTypes.contains(AccessType.RESTRICTED)) {
            return AccessType.RESTRICTED;
        } else {
            return null;
        }
    }

    public Map<Group, AccessType> getLeastRestrictiveAccessTypeAndGroup(Map<Group, AccessType> accessTypes) {

        if (accessTypes == null || accessTypes.size() == 0) {
            return null;
        }

        List<AccessType> accessTypeList = new ArrayList<>(accessTypes.values());
        AccessType leastRestrictiveAccessType;

        if (accessTypeList.contains(AccessType.OWNER)) {
            leastRestrictiveAccessType = AccessType.OWNER;
        } else if (accessTypeList.contains(AccessType.EDITOR)) {
            leastRestrictiveAccessType = AccessType.EDITOR;
        } else if (accessTypeList.contains(AccessType.VIEWER)) {
            leastRestrictiveAccessType = AccessType.VIEWER;
        } else if (accessTypeList.contains(AccessType.RESTRICTED)) {
            leastRestrictiveAccessType = AccessType.RESTRICTED;
        } else {
            return null;
        }

        Map<Group, AccessType> result = new HashMap<>();
        for (Map.Entry<Group, AccessType> map : accessTypes.entrySet()) {
            if (leastRestrictiveAccessType.equals(map.getValue())) {
                if (result.put(map.getKey(), map.getValue()) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
        }
        return result;
    }

    public AccessType getMostRestrictiveAccessType(List<AccessType> accessTypes) {

        if (accessTypes == null || accessTypes.size() == 0) {
            return null;
        }

        if (accessTypes.contains(AccessType.RESTRICTED)) {
            return AccessType.RESTRICTED;
        } else if (accessTypes.contains(AccessType.VIEWER)) {
            return AccessType.VIEWER;
        } else if (accessTypes.contains(AccessType.EDITOR)) {
            return AccessType.EDITOR;
        } else {
            return AccessType.OWNER;
        }
    }

    public Map<Group, AccessType> getMostRestrictiveAccessTypeAndGroup(Map<Group, AccessType> accessTypes) {

        if (accessTypes == null || accessTypes.size() == 0) {
            return null;
        }

        List<AccessType> accessTypeList = new ArrayList<>(accessTypes.values());
        AccessType mostRestrictiveAccessType;

        if (accessTypeList.contains(AccessType.RESTRICTED)) {
            mostRestrictiveAccessType = AccessType.RESTRICTED;
        } else if (accessTypeList.contains(AccessType.VIEWER)) {
            mostRestrictiveAccessType = AccessType.VIEWER;
        } else if (accessTypeList.contains(AccessType.EDITOR)) {
            mostRestrictiveAccessType = AccessType.EDITOR;
        } else {
            mostRestrictiveAccessType = AccessType.OWNER;
        }

        Map<Group, AccessType> result = new HashMap<>();
        for (Map.Entry<Group, AccessType> map : accessTypes.entrySet()) {
            if (mostRestrictiveAccessType.equals(map.getValue())) {
                if (result.put(map.getKey(), map.getValue()) != null) {
                    throw new IllegalStateException("Duplicate key");
                }
            }
        }
        return result;
    }

    private AccessType getAccessType(AccessRights accessRights) {
        AccessType accessType;
        accessType = accessRights.hasAll() ?
                AccessType.OWNER : accessRights.hasReadWrite() ?
                AccessType.EDITOR : accessRights.isReadOnly() ?
                AccessType.VIEWER : AccessType.RESTRICTED;
        return accessType;
    }

    @Override
    public Map<Group, AccessType> getProcessAccessType(Integer processId) {

        Map<Group, AccessType> groupAccessTypeMap = new HashMap<>();

        for (GroupProcess g : getGroupProcesses(processId)) {
            AccessRights accessRights = g.getAccessRights();
            groupAccessTypeMap.put(g.getGroup(), getAccessType(accessRights));
        }

        return groupAccessTypeMap;
    }

    @Override
    public AccessType getProcessAccessTypeByUser(Integer processId, User user) {

        Map<Group, AccessType> accessTypeMap = getProcessAccessType(processId);

        List<AccessType> accessTypes = new ArrayList<>();

        for (Group g : user.getGroups()) {
            if (accessTypeMap.containsKey(g)) {
                accessTypes.add(accessTypeMap.get(g));
            }
        }

        return getLeastRestrictiveAccessType(accessTypes);
    }

    @Override
    public Map<Group, AccessType> getFolderAccessType(Integer processId) {

        Map<Group, AccessType> groupAccessTypeMap = new HashMap<>();

        for (GroupFolder g : getGroupFolders(processId)) {
            AccessRights accessRights = g.getAccessRights();
            groupAccessTypeMap.put(g.getGroup(), getAccessType(accessRights));
        }

        return groupAccessTypeMap;
    }

    @Override
    public AccessType getFolderAccessTypeByUser(Integer folderId, User user) {

        Map<Group, AccessType> accessTypeMap = getFolderAccessType(folderId);

        List<AccessType> accessTypes = new ArrayList<>();

        for (Group g : user.getGroups()) {
            if (accessTypeMap.containsKey(g)) {
                accessTypes.add(accessTypeMap.get(g));
            }
        }

        return getLeastRestrictiveAccessType(accessTypes);
    }

    @Override
    public Map<Group, AccessType> getUserMetadataAccessType(Integer userMetadataId) {

        // Used in AccessController, only return result from GroupUsermetadata table

        Map<Group, AccessType> groupAccessTypeMap = new HashMap<>();

        for (GroupUsermetadata g :
                groupUsermetadataRepository.findByUsermetadataId(userMetadataId)) {
            AccessRights accessRights = g.getAccessRights();
            groupAccessTypeMap.put(g.getGroup(), getAccessType(accessRights));
        }

        return groupAccessTypeMap;
    }

    @Override
    public AccessType getUserMetadataAccessTypeByUser(Integer usermetadataId, User user) {

        // Used by UsermetadataListBox, filterList and dashboardList

        Usermetadata u = usermetadataRepository.findById(usermetadataId);
        Set<Log> logSet = u.getLogs();

        AccessType inheritedAccessType = getLogsAccessTypeByUser(logSet, user);

        // Cross checking whether specified user has access to um, which may not be necessary if specified
        // usermetadata is retrieved by using getUsermetadata() method.
        if (AccessType.RESTRICTED.equals(inheritedAccessType)) {
            // Note: RESTRICTED access type only applies to single log artifact
            Map<Group, AccessType> AccessTypes = getLogAccessTypeAndGroupByUser(logSet.iterator().next().getId(), user);

            // Get all the records from GroupUsermetadata tables that are associated with specified groups and um
            List<GroupUsermetadata> gu = new ArrayList<>();
            for (Group g : AccessTypes.keySet()) {
                gu.add(groupUsermetadataRepository.findByGroupAndUsermetadata(g, u));
            }
            if (gu.size() == 0) {
                // If cross checking false, then specified user doesn't has access to um
                return null;
            }
            return AccessType.VIEWER; // override AccessType.RESTRICTED
        }
        return inheritedAccessType;
    }

    @Override
    public AccessType getUsermetadataAccessTypeByGroup(Integer usermetadataId, Group group) {

        // Used by File/Folder Sharing to identify user selections
        Usermetadata u = usermetadataRepository.findById(usermetadataId);
        GroupUsermetadata gu = groupUsermetadataRepository.findByGroupAndUsermetadata(group, u);
        if (gu == null ) {
            return null;
        }
        return getAccessType(gu.getAccessRights());
    }

    @Override
    public void saveLogAccessType(Integer logId, String groupRowGuid, AccessType accessType,
                                  boolean shareUserMetadata) {

        if (accessType != null) {
            saveLogAccessRights(logId, groupRowGuid, accessType, shareUserMetadata);
        }
    }

    @Override
    public void saveProcessAccessType(Integer processId, String groupRowGuid, AccessType accessType) {

        if (accessType != null) {
            saveProcessPermissions(processId, groupRowGuid, accessType.isRead(), accessType.isWrite(),
                    accessType.isOwner());
        }
    }

    @Override
    public void saveFolderAccessType(Integer folderId, String groupRowGuid, AccessType accessType) {

        if (accessType != null) {
            saveFolderPermissions(folderId, groupRowGuid, accessType.isRead(), accessType.isWrite(),
                    accessType.isOwner());
        }
    }

    @Override
    public void saveUserMetadataAccessType(Integer userMetadataId, String groupRowGuid, AccessType accessType) {

        // Explicitly share User metadata disabled in version 7.19
        if (accessType != null) {
            saveUserMetadataAccessRights(userMetadataId, groupRowGuid, accessType);
        }
    }

    // Delete Log's access right may lead to logical deleting of user metadata, which need username to fill UpdateBy
    // field ( TODO: Unnecessary in 7.19 since no need to update user metadata after deleting of log )
    @Override
    public void deleteLogAccess(Integer logId, String groupRowGuid, String username, AccessType accessType) {

        removeLogPermissions(logId, groupRowGuid, username, accessType);
    }

    @Override
    public void deleteProcessAccess(Integer processId, String groupRowGuid) {

        removeProcessPermissions(processId, groupRowGuid);
    }

    @Override
    public void deleteFolderAccess(Integer folderId, String groupRowGuid) {

        removeFolderPermissions(folderId, groupRowGuid);
    }

    @Override
    public void deleteUserMetadataAccess(Integer userMetadataId, String groupRowGuid) {
        // Explicitly share User metadata disabled in version 7.19
        removeUsermetadataPermissions(userMetadataId, groupRowGuid);
    }

    @Override
    @Transactional(readOnly = false)
    public String saveFolderPermissions(Integer folderId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                                        boolean hasOwnership) {

        List<Folder> subFoldersWithCurrentFolders = folderService.getSubFolders(folderId, true);
        Group group = groupRepository.findByRowGuid(groupRowGuid);
        List<Integer> folderIds = new ArrayList<>();

        for (Folder folder : subFoldersWithCurrentFolders) {
            createGroupFolder(group, folder, hasRead, hasWrite, hasOwnership);
            folderIds.add(folder.getId());
        }

        List<Folder> parentFolders = folderService.getParentFolders(folderId);
        for (Folder folder : parentFolders) {
            // Give parent folders viewer access only if specified group doesn't have access to
            if (groupFolderRepository.findByGroupAndFolder(group, folder) == null) {
                createGroupFolder(group, folder, true, false, false);
            }
        }

        for (Process process : processRepository.findByFolderIdIn(folderIds)) {
            createGroupProcess(group, process, hasRead, hasWrite, hasOwnership);
        }

        for (Log log : logRepository.findByFolderIdIn(folderIds)) {
            createGroupLog(group, log, hasRead, hasWrite, hasOwnership);
        }

        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String removeFolderPermissions(Integer folderId, String groupRowGuid) {

//
        List<Folder> subFoldersWithCurrentFolders = folderService.getSubFolders(folderId, true);
        Group group = groupRepository.findByRowGuid(groupRowGuid);
        List<Integer> folderIds = new ArrayList<Integer>();

        for (Folder folder : subFoldersWithCurrentFolders) {
            folderIds.add(folder.getId());
            removeGroupFolder(group, folder);
        }

        for (Process process : processRepository.findByFolderIdIn(folderIds)) {
            removeGroupProcess(group, process);
        }

        for (Log log : logRepository.findByFolderIdIn(folderIds)) {
            removeGroupLog(group, log);
        }
        return "";

    }

    @Override
    @Transactional(readOnly = false)
    public String removeProcessPermissions(Integer processId, String groupRowGuid) {
        Process process = processRepository.findOne(processId);
        Group group = groupRepository.findByRowGuid(groupRowGuid);
        removeGroupProcess(group, process);
        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String removeLogPermissions(Integer logId, String groupRowGuid, String username, AccessType accessType) {
        Log log = logRepository.findOne(logId);
        Group group = groupRepository.findByRowGuid(groupRowGuid);
        removeGroupLog(group, log);

        // TODO: If access type = restricted, remove GroupUsermetadata
        // If access type = restricted, remove permission with user metadata that linked to specified log
        // userMetadataServ.removeUserMetadataAccessRightsByLogAndGroup(logId, groupRowGuid, username);

        if (accessType != AccessType.RESTRICTED) {

            for (Usermetadata u : logRepository.findUniqueByID(logId).getUsermetadataSet()) {
                removeGroupUsermetadata(group, u);
            }
        }

        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String removeUsermetadataPermissions(Integer usermetadataId, String groupRowGuid) {
        Usermetadata um = usermetadataRepository.findOne(usermetadataId);
        Group group = groupRepository.findByRowGuid(groupRowGuid);
        removeGroupUsermetadata(group, um);
        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String saveProcessPermissions(Integer processId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                                         boolean hasOwnership) {
        Process process = processRepository.findOne(processId);
        Group group = groupRepository.findByRowGuid(groupRowGuid);
        createGroupProcess(group, process, hasRead, hasWrite, hasOwnership);

        Folder parentFolder = process.getFolder();
        setReadOnlyParentFolders(group, parentFolder);

        return "";
    }

    @Override
    @Transactional(readOnly = false)
    public String saveLogPermissions(Integer logId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                                     boolean hasOwnership) {
        Log log = logRepository.findOne(logId);
        Group group = groupRepository.findByRowGuid(groupRowGuid);

        createGroupLog(group, log, hasRead, hasWrite, hasOwnership);

        Folder parentFolder = log.getFolder();
        setReadOnlyParentFolders(group, parentFolder);

        return "";
    }

    @Override
    @Transactional
    public String saveLogAccessRights(Integer logId, String groupRowGuid, AccessType accessType,
                                      boolean shareUserMetadata) {
        Log log = logRepository.findOne(logId);
        Group group = groupRepository.findByRowGuid(groupRowGuid);

        createGroupLog(group, log, accessType.isRead(), accessType.isWrite(), accessType.isOwner());

        Folder parentFolder = log.getFolder();
        setReadOnlyParentFolders(group, parentFolder);

        // shareUserMetadata flag is disabled for now

        // If not restricted viewer, then remove all GroupUsermetadata associated with specified log and group
        if (accessType != AccessType.RESTRICTED) {

            for (Usermetadata u : logRepository.findUniqueByID(logId).getUsermetadataSet()) {
                removeGroupUsermetadata(group, u);
            }
        }

        return "";
    }

    @Override
    @Transactional
    public String saveUserMetadataAccessRights(Integer usermetadataId, String groupRowGuid, AccessType accessType) {

        Usermetadata usermetadata = usermetadataRepository.findById(usermetadataId);
        Group group = groupRepository.findByRowGuid(groupRowGuid);

        createGroupUsermetadata(group, usermetadata, accessType.isRead(), accessType.isWrite(), accessType.isOwner());

        return "";
    }


    private void createGroupFolder(Group group, Folder folder, boolean hasRead, boolean hasWrite,
                                   boolean hasOwnership) {
        GroupFolder groupFolder = groupFolderRepository.findByGroupAndFolder(group, folder);
        if (groupFolder == null) {
            groupFolder = new GroupFolder();
            groupFolder.setGroup(group);
            groupFolder.setFolder(folder);
        }
        AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
        groupFolder.setAccessRights(accessRights);

        groupFolderRepository.save(groupFolder);
    }

    private void createGroupProcess(Group group, Process process, boolean hasRead, boolean hasWrite,
                                    boolean hasOwnership) {
        GroupProcess groupProcess = groupProcessRepository.findByGroupAndProcess(group, process);
        AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
        if (groupProcess == null) {

            groupProcess = new GroupProcess(process, group, accessRights);
            process.getGroupProcesses().add(groupProcess);
            // group.getGroupProcesses().add(groupProcess);
        } else {
            groupProcess.setAccessRights(accessRights);
        }
        groupProcessRepository.save(groupProcess);
    }

    private void createGroupLog(Group group, Log log, boolean hasRead, boolean hasWrite, boolean hasOwnership) {
        GroupLog groupLog = groupLogRepository.findByGroupAndLog(group, log);
        AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
        if (groupLog == null) {
            groupLog = new GroupLog(group, log, accessRights);
            log.getGroupLogs().add(groupLog);
            // group.getGroupLogs().add(groupLog);
        } else {
            groupLog.setAccessRights(accessRights);
        }
        groupLogRepository.save(groupLog);
    }

    private void createGroupUsermetadata(Group group, Usermetadata usermetadata, boolean hasRead, boolean hasWrite,
                                         boolean hasOwnership) {
        GroupUsermetadata groupUsermetadata = groupUsermetadataRepository.findByGroupAndUsermetadata(group, usermetadata);
        AccessRights accessRights = new AccessRights(hasRead, hasWrite, hasOwnership);
        if (groupUsermetadata == null) {
            groupUsermetadata = new GroupUsermetadata(group, usermetadata, accessRights);
            usermetadata.getGroupUserMetadata().add(groupUsermetadata);
        } else {
            groupUsermetadata.setAccessRights(accessRights);
        }
        groupUsermetadataRepository.save(groupUsermetadata);
    }

    private void removeGroupFolder(Group group, Folder folder) {
        GroupFolder groupFolder = groupFolderRepository.findByGroupAndFolder(group, folder);
        if (groupFolder != null) {
            groupFolderRepository.delete(groupFolder);
        }
    }

    private void removeGroupProcess(Group group, Process process) {
        GroupProcess groupProcess = groupProcessRepository.findByGroupAndProcess(group, process);
        if (groupProcess != null) {
            groupProcessRepository.delete(groupProcess);
        }
    }

    private void removeGroupLog(Group group, Log log) {
        GroupLog groupLog = groupLogRepository.findByGroupAndLog(group, log);
        if (groupLog != null) {
            groupLogRepository.delete(groupLog);
        }
    }

    private void removeGroupUsermetadata(Group group, Usermetadata usermetadata) {
        GroupUsermetadata groupUsermetadata = groupUsermetadataRepository.findByGroupAndUsermetadata(group, usermetadata);
        if (groupUsermetadata != null) {
            groupUsermetadataRepository.delete(groupUsermetadata);
        }
    }

    private void setReadOnlyParentFolders(Group group, Folder parentFolder) {
        if (parentFolder != null) {
            List<Folder> parentFolders = folderService.getParentFolders(parentFolder.getId());
            parentFolders.add(parentFolder);
            for (Folder folder : parentFolders) {
                GroupFolder groupFolder = groupFolderRepository.findByGroupAndFolder(group, folder);
                if (groupFolder == null) { // Set read access only when specified group doesn't have access to parent
                    // folder
                    createGroupFolder(group, folder, true, false, false);
                }
            }
        }
    }
}
