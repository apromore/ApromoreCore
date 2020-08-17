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

import org.apromore.dao.model.Usermetadata;
import org.apromore.exception.UserNotFoundException;
import org.apromore.util.UserMetadataTypeEnum;

import java.util.List;
import java.util.Set;

public interface UserMetadataService {

    /**
     *
     * @param metadata Content of user metadata
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @param username username
     * @param logIds List of logId
     * @throws UserNotFoundException
     */
    void saveUserMetadata(String metadata, UserMetadataTypeEnum userMetadataTypeEnum, String username, List<Integer> logIds) throws UserNotFoundException;


    /**
     * Save user
     *
     * @param userMetadataContent  Content of user metadata
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @param username username
     * @param logId logId
     * @throws UserNotFoundException
     */
    void saveUserMetadataLinkedToOneLog(String userMetadataContent, UserMetadataTypeEnum userMetadataTypeEnum,
                                        String username,
                                        Integer logId) throws UserNotFoundException;

    /**
     *
     * @param userMetadataId  Id of user metadata
     * @param username username
     * @param content Content of user metadata
     * @throws UserNotFoundException
     */
    void updateUserMetadata(Integer userMetadataId, String username, String content) throws UserNotFoundException;

    /**
     *
     * @param userMetadataId Id of user metadata
     * @param username username
     * @throws UserNotFoundException
     */
    void deleteUserMetadata(Integer userMetadataId, String username) throws UserNotFoundException;

    /**
     *
     * @param username username
     * @param logId logId
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @return
     * @throws UserNotFoundException
     */
    Set<Usermetadata> getUserMetadata(String username, Integer logId, UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException;

    /**
     *
     * @param username
     * @param UsermetadataId Id of user metadata
     * @return
     * @throws UserNotFoundException
     */
    boolean canUserEditMetadata(String username, Integer UsermetadataId) throws UserNotFoundException;


}
