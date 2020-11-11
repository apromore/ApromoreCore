/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import org.apromore.dao.model.*;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.AuthorizationService;
import org.apromore.service.UserMetadataService;
import org.apromore.service.WorkspaceService;
import org.apromore.util.AccessType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = true, rollbackFor = Exception.class)
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationServiceImpl.class);

    private WorkspaceService workspaceService;
    private UserMetadataService userMetadataService;

    @Inject
    public AuthorizationServiceImpl(final WorkspaceService workspaceService, UserMetadataService userMetadataService) {
        this.workspaceService = workspaceService;
        this.userMetadataService = userMetadataService;
    }

    @Override
    public Map<Group, AccessType> getLogAccessType(Integer logId) {

        Map<Group, AccessType> groupAccessTypeMap = new HashMap<>();

        for (GroupLog g : workspaceService.getGroupLogs(logId)) {
            AccessRights accessRights = g.getAccessRights();
            groupAccessTypeMap.put(g.getGroup(), getAccessType(accessRights));
        }

        return groupAccessTypeMap;
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
    public Map<Group, AccessType> getFolderAccessType(Integer processId) {

        Map<Group, AccessType> groupAccessTypeMap = new HashMap<>();

        for (GroupFolder g : workspaceService.getGroupFolders(processId)) {
            AccessRights accessRights = g.getAccessRights();
            groupAccessTypeMap.put(g.getGroup(), getAccessType(accessRights));
        }

        return groupAccessTypeMap;
    }

    @Override
    public Map<Group, AccessType> getUserMetadataAccessType(Integer userMetadataId) {

        Map<Group, AccessType> groupAccessTypeMap = new HashMap<>();

        for (GroupUsermetadata g : userMetadataService.getGroupUserMetadata(userMetadataId)) {
            AccessRights accessRights = g.getAccessRights();
            groupAccessTypeMap.put(g.getGroup(), getAccessType(accessRights));
        }

        return groupAccessTypeMap;
    }

    @Override
    public void saveLogAccessType(Integer logId, String groupRowGuid, AccessType accessType, boolean shareUserMetadata) {

        if (!accessType.equals(AccessType.NONE)) {
            workspaceService.saveLogAccessRights(logId, groupRowGuid, accessType.isRead(), accessType.isWrite(),
                    accessType.isOwner(), shareUserMetadata);
        }

    }

    @Override
    public void saveProcessAccessType(Integer processId, String groupRowGuid, AccessType accessType) {

        if (!accessType.equals(AccessType.NONE)) {
            workspaceService.saveProcessPermissions(processId, groupRowGuid, accessType.isRead(), accessType.isWrite(),
                    accessType.isOwner());
        }
    }

    @Override
    public void saveFolderAccessType(Integer folderId, String groupRowGuid, AccessType accessType) {

        if (!accessType.equals(AccessType.NONE)) {
            workspaceService.saveFolderPermissions(folderId, groupRowGuid, accessType.isRead(), accessType.isWrite(),
                    accessType.isOwner());
        }
    }

    @Override
    public void saveUserMetadtarAccessType(Integer userMetadataId, String groupRowGuid, AccessType accessType) {

        if (!accessType.equals(AccessType.NONE)) {
            userMetadataService.saveUserMetadataAccessRights(userMetadataId, groupRowGuid, accessType.isRead(),
                    accessType.isWrite(), accessType.isOwner());
        }
    }

    // Delete Log's access right may lead to logical deleting of user metadata, which need username to fill UpdateBy
    // field
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
        userMetadataService.removeUserMetadataAccessRights(userMetadataId, groupRowGuid);
    }
}
