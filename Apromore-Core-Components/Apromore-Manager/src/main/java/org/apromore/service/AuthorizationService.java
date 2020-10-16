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
     * Get list of Group and AccessType pair of Log
     * @param processId Process ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getProcessAccessType(Integer processId);

    /**
     * Get list of Group and AccessType pair of Log
     * @param folderId Folder ID
     * @return list of Group and AccessType pair
     */
    Map<Group, AccessType> getFolderAccessType(Integer folderId);

    /**
     * Save new GroupLog or update existing one
     * @param logId Log ID
     * @param groupRowGuid Group UID
     * @param accessType AccessType
     */
    void saveLogAccessType(Integer logId, String groupRowGuid, AccessType accessType);

    /**
     * Save new GroupProcess or update existing one
     * @param logId Log ID
     * @param groupRowGuid Group UID
     * @param accessType AccessType
     */
    void saveProcessAccessType(Integer logId, String groupRowGuid, AccessType accessType);

    /**
     * Save new GroupFolder or update existing one
     * @param logId Log ID
     * @param groupRowGuid Group UID
     * @param accessType AccessType
     */
    void saveFolderAccessType(Integer logId, String groupRowGuid, AccessType accessType);

    /**
     * Delete one GroupLog record
     * @param logId Log ID
     * @param groupRowGuid Group UID
     * @param username User username
     * @throws UserNotFoundException Client side should handle this exception and prompt user as error
     */
    void deleteLogAccessType(Integer logId, String groupRowGuid, String username) throws UserNotFoundException;

    /**
     * Delete one GroupLog record
     * @param processId Process ID
     * @param groupRowGuid Group UID
     */
    void deleteProcessAccessType(Integer processId, String groupRowGuid);

    /**
     * Delete one GroupLog record
     * @param folderId Process ID
     * @param groupRowGuid Group UID
     */
    void deleteFolderAccessType(Integer folderId, String groupRowGuid);
}
