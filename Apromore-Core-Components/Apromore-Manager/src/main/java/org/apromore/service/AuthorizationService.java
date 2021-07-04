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
package org.apromore.service;

import org.apromore.dao.model.*;
import org.apromore.exception.UserNotFoundException;
import org.apromore.util.AccessType;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AuthorizationService {


    List<GroupFolder> getGroupFolders(Integer folderId);

    List<GroupProcess> getGroupProcesses(Integer processId);

    List<GroupLog> getGroupLogs(Integer logId);

    /**
     * Get list of Group and AccessType pairs that have access to the specified Log
     *
     * @param logId Log ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getLogAccessType(Integer logId);

    /**
     * Get the least restrictive AccessType the specified user has on the specified log
     *
     * How to identify a user’s access type to a Log?
     *
     * User U is a VIEWER of the log L, if and only if one of the following conditions holds:
     * 1. U is a full viewer of L
     * 2. U belongs to a group that is a full viewer of L
     * 3. U is a restricted view of L
     * 4. U belongs to a group G that is a restricted viewer of L
     *
     * User U is an EDITOR of the log L, if and only if one of the following conditions holds:
     * 1. U is an editor of L
     * 2. U belongs to a group that is an editor of L
     *
     * User U is an OWNER of the log L, if and only if one of the following conditions holds:
     * 1. U is an owner of L
     * 2. U belongs to a group that is an owner of L
     *
     * @param logId    Log Id
     * @param user     User
     * @return AccessType
     */
    AccessType getLogAccessTypeByUser(Integer logId, User user);

    /**
     * Same functionality as getLogAccessTypeByUser(Integer logId, User user) but return Map<Group, AccessType>
     *
     * @param logId Log Id
     * @param user User
     * @return Map<Group, AccessType> A map contains Group and corresponding access type
     */
    Map<Group, AccessType> getLogAccessTypeAndGroupByUser(Integer logId, User user);

    /**
     * Get the least restrictive AccessType the specified user has on the specified multi-log
     *
     * How to identify a user’s access type to multi-log?
     *
     * User U is a VIEWER of the multi-log {L1, …, Ln}, if and only if the following condition holds for EACH log Li
     * in {L1, …, Ln}:
     * 1. U is a full viewer of Li
     * 2. U belongs to a group that is a full viewer of Li
     *
     * User U is an EDITOR of the multi-log {L1, …, Ln}, if and only if the following condition holds for EACH log Li
     * in {L1, …, Ln}:
     * 1. U is an owner or an editor of Li
     * 2. U belongs to a group that is an owner or editor of Li
     *
     * User U is an OWNER of the multi-log {L1, …, Ln}, if and only if the following condition holds for EACH log Li
     * in {L1, …, Ln}:
     * 1. U is an owner of Li
     * 2. U belongs to a group that is an owner of Li
     *
     * @param logSet   Set of Log
     * @param user     User
     * @return AccessType
     */
    AccessType getLogsAccessTypeByUser(Set<Log> logSet, User user);

    /**
     * Get the least restrictive AccessType the specified user has on the specified multi-log
     *
     * How to identify a user’s access type to multi-log?
     *
     * User U is a VIEWER of the multi-log {L1, …, Ln}, if and only if the following condition holds for EACH log Li
     * in {L1, …, Ln}:
     * 1. U is a full viewer of Li
     * 2. U belongs to a group that is a full viewer of Li
     *
     * User U is an EDITOR of the multi-log {L1, …, Ln}, if and only if the following condition holds for EACH log Li
     * in {L1, …, Ln}:
     * 1. U is an owner or an editor of Li
     * 2. U belongs to a group that is an owner or editor of Li
     *
     * User U is an OWNER of the multi-log {L1, …, Ln}, if and only if the following condition holds for EACH log Li
     * in {L1, …, Ln}:
     * 1. U is an owner of Li
     * 2. U belongs to a group that is an owner of Li
     *
     * @param logIds   List of Log Id
     * @param user     User
     * @return AccessType
     */
    AccessType getLogsAccessTypeByUser(List<Integer> logIds, User user);

    /**
     * Get list of Group and AccessType pair of Process
     *
     * @param processId Process ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getProcessAccessType(Integer processId);

    /**
     * Get the least restrictive AccessType the specified user has on the specified process
     *
     * @param processId Process Id
     * @param user      user
     * @return AccessType
     */
    AccessType getProcessAccessTypeByUser(Integer processId, User user);

    /**
     * Get list of Group and AccessType pair of Folder
     *
     * @param folderId Folder ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getFolderAccessType(Integer folderId);

    /**
     * Get the least restrictive AccessType the specified user has on the specified folder
     *
     * @param folderId Folder Id
     * @param user     user
     * @return AccessType
     */
    AccessType getFolderAccessTypeByUser(Integer folderId, User user);

    /**
     * Get list of Group and AccessType pair of User metadata
     *
     * @param userMetadataId User metadata ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getUserMetadataAccessType(Integer userMetadataId);


    /**
     * The specified user has access to specified user metadata
     *
     * @param usermetadataId User metadata ID
     * @param user User
     * @return AccessType
     * @throws UserNotFoundException Specified user can't be found
     */
    AccessType getUserMetadataAccessTypeByUser(Integer usermetadataId, User user) throws UserNotFoundException;

    /**
     * The specified group has access to specified user metadata
     *
     * @param usermetadataId User metadata ID
     * @param group Group
     * @return AccessType
     */
    AccessType getUsermetadataAccessTypeByGroup(Integer usermetadataId, Group group);

    /**
     * Save new GroupLog or update existing one
     *
     * @param logId        Log ID
     * @param groupRowGuid Group UID
     * @param accessType   AccessType
     */
    void saveLogAccessType(Integer logId, String groupRowGuid, AccessType accessType, boolean shareUserMetadata);

    /**
     * Save new GroupProcess or update existing one
     *
     * @param processId    Process ID
     * @param groupRowGuid Group UID
     * @param accessType   AccessType
     */
    void saveProcessAccessType(Integer processId, String groupRowGuid, AccessType accessType);

    /**
     * Save new GroupFolder or update existing one
     *
     * @param folderId     Folder ID
     * @param groupRowGuid Group UID
     * @param accessType   AccessType
     */
    void saveFolderAccessType(Integer folderId, String groupRowGuid, AccessType accessType);

    /**
     * Save new GroupUsermetadata or update existing one
     *
     * @param userMetadataId User metadata ID
     * @param groupRowGuid   Group UID
     * @param accessType     AccessType
     */
    void saveUserMetadataAccessType(Integer userMetadataId, String groupRowGuid, AccessType accessType);

    /**
     * Delete one GroupLog record
     *
     * @param logId        Log ID
     * @param groupRowGuid Group UID
     * @param username     User username
     */
    void deleteLogAccess(Integer logId, String groupRowGuid, String username, AccessType accessType);

    /**
     * Delete one GroupLog record
     *
     * @param processId    Process ID
     * @param groupRowGuid Group UID
     */
    void deleteProcessAccess(Integer processId, String groupRowGuid);

    /**
     * Delete one GroupLog record
     *
     * @param folderId     Folder ID
     * @param groupRowGuid Group UID
     */
    void deleteFolderAccess(Integer folderId, String groupRowGuid);

    /**
     * Delete one GroupUsermetadata record
     *
     * @param userMetadataId User metadata ID
     * @param groupRowGuid   Group UID
     */
    void deleteUserMetadataAccess(Integer userMetadataId, String groupRowGuid);

    String saveFolderPermissions(Integer folderId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                                 boolean hasOwnership);

    String saveProcessPermissions(Integer processId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                                  boolean hasOwnership);

    String saveLogPermissions(Integer logId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                              boolean hasOwnership);

    String removeFolderPermissions(Integer folderId, String groupRowGuid);

    String removeProcessPermissions(Integer processId, String groupRowGuid);

    String removeLogPermissions(Integer logId, String groupRowGuid, String username, AccessType accessType);

    String removeUsermetadataPermissions(Integer usermetadataId, String groupRowGuid);


    String saveLogAccessRights(Integer logId, String groupRowGuid, AccessType accessType, boolean shareUserMetadata);

    String saveUserMetadataAccessRights(Integer usermetadataId, String groupRowGuid, AccessType accessType);

}
