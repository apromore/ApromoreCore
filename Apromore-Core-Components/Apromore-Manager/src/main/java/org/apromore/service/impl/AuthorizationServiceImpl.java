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

import org.apromore.dao.GroupUsermetadataRepository;
import org.apromore.dao.LogRepository;
import org.apromore.dao.UsermetadataRepository;
import org.apromore.dao.model.*;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.AuthorizationService;
import org.apromore.service.WorkspaceService;
import org.apromore.util.AccessType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class AuthorizationServiceImpl implements AuthorizationService {

    private WorkspaceService workspaceService;
    private GroupUsermetadataRepository groupUsermetadataRepository;
    private UsermetadataRepository usermetadataRepository;
    private LogRepository logRepository;

    @Inject
    public AuthorizationServiceImpl(final WorkspaceService workspaceService,
                                    final GroupUsermetadataRepository groupUsermetadataRepository,
                                    final UsermetadataRepository usermetadataRepository,
                                    final LogRepository logRepository) {
        this.workspaceService = workspaceService;
        this.groupUsermetadataRepository = groupUsermetadataRepository;
        this.usermetadataRepository = usermetadataRepository;
        this.logRepository = logRepository;
    }

    @Override
    public Map<Group, AccessType> getLogAccessType(Integer logId) {

        Map<Group, AccessType> groupAccessTypeMap = new HashMap<>();

        List<GroupLog> groupLogs = workspaceService.getGroupLogs(logId);

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

    @Override
    public AccessType getLogsAccessTypeByUser(Set<Log> logSet, User user) {

        List<AccessType> accessTypes = new ArrayList<>();

        for (Log l : logSet) {
            AccessType at = getLogAccessTypeByUser(l.getId(), user);
            if ( at == AccessType.NONE) {
                return AccessType.NONE; // If specified user can't access one of the multi-log, then return NONE
            }
            accessTypes.add(at);
        }

        return getMostRestrictiveAccessType(accessTypes);
    }

    @Override
    public AccessType getLogsAccessTypeByUser(List<Integer> logIds, User user) {
        Set<Log> logSet = new HashSet<>();

        for(Integer logId : logIds) {
            logSet.add(logRepository.findUniqueByID(logId));
        }

        return getLogsAccessTypeByUser(logSet, user);
    }

    public AccessType getLeastRestrictiveAccessType(List<AccessType> accessTypes) {

        if (accessTypes == null || accessTypes.size() == 0) {
            return AccessType.NONE;
        }

        if (accessTypes.contains(AccessType.OWNER)) {
            return AccessType.OWNER;
        } else if (accessTypes.contains(AccessType.EDITOR)) {
            return AccessType.EDITOR;
        } else if (accessTypes.contains(AccessType.VIEWER)) {
            return AccessType.VIEWER;
        } else return AccessType.NONE;
    }

    public AccessType getMostRestrictiveAccessType(List<AccessType> accessTypes) {

        if (accessTypes == null || accessTypes.size() == 0) {
            return AccessType.NONE;
        }

        if (accessTypes.contains(AccessType.NONE)) {
            return AccessType.NONE;
        } else if (accessTypes.contains(AccessType.VIEWER)) {
            return  AccessType.VIEWER;
        } else if (accessTypes.contains(AccessType.EDITOR)) {
            return  AccessType.EDITOR;
        } else return  AccessType.OWNER;
    }

    private AccessType getAccessType(AccessRights accessRights) {
        AccessType accessType;
        accessType = accessRights.hasAll() ?
                AccessType.OWNER  : accessRights.hasReadWrite() ?
                AccessType.EDITOR : accessRights.isReadOnly() ?
                AccessType.VIEWER : AccessType.NONE;
        return accessType;
    }

    @Override
    public Map<Group, AccessType> getProcessAccessType(Integer processId) {

        Map<Group, AccessType> groupAccessTypeMap = new HashMap<>();

        for (GroupProcess g : workspaceService.getGroupProcesses(processId)) {
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

        for (GroupFolder g : workspaceService.getGroupFolders(processId)) {
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

        // User metadata list on share window is disabled in version 7.19

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

        Usermetadata u = usermetadataRepository.findById(usermetadataId);
        Set<Log> logSet = u.getLogs();

        return getLogsAccessTypeByUser(logSet, user);
    }

    @Override
    public void saveLogAccessType(Integer logId, String groupRowGuid, AccessType accessType, boolean shareUserMetadata) {

        if (!AccessType.NONE.equals(accessType)) {
            workspaceService.saveLogAccessRights(logId, groupRowGuid, accessType, shareUserMetadata);
        }
    }

    @Override
    public void saveProcessAccessType(Integer processId, String groupRowGuid, AccessType accessType) {

        if (!AccessType.NONE.equals(accessType)) {
            workspaceService.saveProcessPermissions(processId, groupRowGuid, accessType.isRead(), accessType.isWrite(),
                    accessType.isOwner());
        }
    }

    @Override
    public void saveFolderAccessType(Integer folderId, String groupRowGuid, AccessType accessType) {

        if (!AccessType.NONE.equals(accessType)) {
            workspaceService.saveFolderPermissions(folderId, groupRowGuid, accessType.isRead(), accessType.isWrite(),
                    accessType.isOwner());
        }
    }

    @Override
    public void saveUserMetadataAccessType(Integer userMetadataId, String groupRowGuid, AccessType accessType) {

        // Explicitly share User metadata disabled in version 7.19
    }

    // Delete Log's access right may lead to logical deleting of user metadata, which need username to fill UpdateBy
    // field ( TODO: Unnecessary in 7.19 since no need to update user metadata after deleting of log )
    @Override
    public void deleteLogAccess(Integer logId, String groupRowGuid, String username) throws UserNotFoundException {

        workspaceService.removeLogPermissions(logId, groupRowGuid, username);
    }

    @Override
    public void deleteProcessAccess(Integer processId, String groupRowGuid) {

        workspaceService.removeProcessPermissions(processId, groupRowGuid);
    }

    @Override
    public void deleteFolderAccess(Integer folderId, String groupRowGuid) {

        workspaceService.removeFolderPermissions(folderId, groupRowGuid);
    }

    @Override
    public void deleteUserMetadataAccess(Integer userMetadataId, String groupRowGuid) {
        // Explicitly share User metadata disabled in version 7.19
    }

}
