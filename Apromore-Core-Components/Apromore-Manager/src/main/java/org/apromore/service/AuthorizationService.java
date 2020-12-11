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
package org.apromore.service;

import org.apromore.dao.model.Group;
import org.apromore.exception.UserNotFoundException;
import org.apromore.util.AccessType;

import java.util.Map;

public interface AuthorizationService {

    /**
     * Get list of Group and AccessType pair of Log
     * @param logId Log ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getLogAccessType(Integer logId);

    /**
     * Get list of Group and AccessType pair of Process
     * @param processId Process ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getProcessAccessType(Integer processId);

    /**
     * Get list of Group and AccessType pair of Folder
     * @param folderId Folder ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getFolderAccessType(Integer folderId);

    /**
     * Get list of Group and AccessType pair of User metadata
     * @param userMetadataId User metadata ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getUserMetadataAccessType(Integer userMetadataId);


    /**
     * Save new GroupLog or update existing one
     * @param logId Log ID
     * @param groupRowGuid Group UID
     * @param accessType AccessType
     */
    void saveLogAccessType(Integer logId, String groupRowGuid, AccessType accessType, boolean shareUserMetadata);

    /**
     * Save new GroupProcess or update existing one
     * @param processId Process ID
     * @param groupRowGuid Group UID
     * @param accessType AccessType
     */
    void saveProcessAccessType(Integer processId, String groupRowGuid, AccessType accessType);

    /**
     * Save new GroupFolder or update existing one
     * @param folderId Folder ID
     * @param groupRowGuid Group UID
     * @param accessType AccessType
     */
    void saveFolderAccessType(Integer folderId, String groupRowGuid, AccessType accessType);

    /**
     * Save new GroupUsermetadata or update existing one
     * @param userMetadataId User metadata ID
     * @param groupRowGuid Group UID
     * @param accessType AccessType
     */
    void saveUserMetadataAccessType(Integer userMetadataId, String groupRowGuid, AccessType accessType);

    /**
     * Delete one GroupLog record
     * @param logId Log ID
     * @param groupRowGuid Group UID
     * @param username User username
     * @throws UserNotFoundException Client side should handle this exception and prompt user as error
     */
    void deleteLogAccess(Integer logId, String groupRowGuid, String username) throws UserNotFoundException;

    /**
     * Delete one GroupLog record
     * @param processId Process ID
     * @param groupRowGuid Group UID
     */
    void deleteProcessAccess(Integer processId, String groupRowGuid);

    /**
     * Delete one GroupLog record
     * @param folderId Folder ID
     * @param groupRowGuid Group UID
     */
    void deleteFolderAccess(Integer folderId, String groupRowGuid);

    /**
     * Delete one GroupUsermetadata record
     * @param userMetadataId User metadata ID
     * @param groupRowGuid Group UID
     */
    void deleteUserMetadataAccess(Integer userMetadataId, String groupRowGuid);
}
