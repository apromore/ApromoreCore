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

import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.exception.UserNotFoundException;
import org.apromore.util.UserMetadataTypeEnum;

import java.util.List;
import java.util.Set;

public interface UserMetadataService {

    /**
     * Save new user metadata
     * @param metadata
     * @param userMetadataTypeEnum
     * @param username
     * @param logId
     * @throws UserNotFoundException
     */
    void saveUserMetadata (String metadata, UserMetadataTypeEnum userMetadataTypeEnum, String username, Integer logId) throws UserNotFoundException;

    /**
     * Update user metadata content only
     * @param usermetadataId
     * @param username
     * @throws UserNotFoundException
     */
    void updateUserMetadata (Integer usermetadataId, String username, String content) throws UserNotFoundException;

    /**
     * Delete one user metadata
     * @param usermetadataId
     * @throws UserNotFoundException
     */
    void deleteUserMetadata (Integer usermetadataId, String username) throws UserNotFoundException;

    Set<Usermetadata> getUserMetadata (String username, Integer logId, Integer typeId) throws UserNotFoundException;

    boolean canUserEditMetadata (String username, Integer UsermetadataId) throws UserNotFoundException;


}
