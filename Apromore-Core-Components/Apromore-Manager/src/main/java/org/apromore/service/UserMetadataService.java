/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

import java.util.Optional;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.GroupUsermetadata;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.dao.model.Usermetadata;
import org.apromore.exception.UserNotFoundException;
import org.apromore.util.AccessType;
import org.apromore.util.UserMetadataTypeEnum;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;

import java.util.List;
import java.util.Set;

public interface UserMetadataService {

    /**
     * Save as a new User Metadata
     *
     * @param userMetadataName     Name of user metadata
     * @param userMetadataContent  Content of user metadata
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @param username             username
     * @param logIds               List of logId
     * @throws UserNotFoundException Can't find a user with specified username
     */
    Usermetadata saveUserMetadata(String userMetadataName, String userMetadataContent,
                                  UserMetadataTypeEnum userMetadataTypeEnum,
                                  String username,
                                  List<Integer> logIds) throws UserNotFoundException;


    /**
     * Save as a new User Metadata. Use this method when only one log is linked to this metadata.
     *
     * @param userMetadataName     Name of user metadata
     * @param userMetadataContent  Content of user metadata
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @param username             username
     * @param logId                logId
     * @throws UserNotFoundException Can't find a user with specified username
     * @return Usermetadata
     */
    Usermetadata saveUserMetadata(String userMetadataName, String userMetadataContent,
                                        UserMetadataTypeEnum userMetadataTypeEnum,
                                        String username,
                                        Integer logId) throws UserNotFoundException;

    AccessType getUserMetadataAccessTypeByUser(Integer usermetadataId, User user) throws UserNotFoundException;

    /**
     * Save a user metadata which is not linked to log. So it can not be shared to other users at stage one.
     *
     * @param content  Content of user metadata
     * @param username username
     * @throws UserNotFoundException Can't find a user with specified username
     */
    void saveUserMetadataWithoutLog(String content, UserMetadataTypeEnum userMetadataTypeEnum, String username) throws UserNotFoundException;


    /**
     * Save a list of user metadata which is not linked to log.
     *
     * @param contentList          List of content
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @param username             username
     * @return
     * @throws UserNotFoundException Can't find a user with specified username
     */
    List<Usermetadata> saveAllUserMetadataWithoutLog(List<String> contentList,
                                                     UserMetadataTypeEnum userMetadataTypeEnum,
                                                     String username) throws UserNotFoundException;
    /**
     * Assign specified group with permission to all the user metadata that linked to the specified log
     *
     * @param logId        log ID
     * @param groupRowGuid group guid
     * @param accessType   Access type
     */
    void saveUserMetadataAccessRightsByLogAndGroup(Integer logId, String groupRowGuid, AccessType accessType);

    /**
     * Automatically share simulation user metadata when a BIMP simulation MXML Log is shared.
     *
     * @param logId        log ID
     * @param groupRowGuid group guid
     * @param accessType   Access Type
     */
    void shareSimulationMetadata(Integer logId, String groupRowGuid, AccessType accessType);

    /**
     * Implicitly share all associated user metadata when a Log is shared
     *
     * @param logId        log Id
     * @param groupRowGuid group guid
     * @param accessType   Access Type
     */
    void shareUserMetadataWithLog(Integer logId, String groupRowGuid, AccessType accessType);

    /**
     * Remove permissions of user metadata assigned to specified group and log
     *
     * @param logId        log ID
     * @param groupRowGuid group guid
     */
    void removeUserMetadataAccessRightsByLogAndGroup(Integer logId, String groupRowGuid, String username) throws UserNotFoundException;

    /**
     * Update a user metadata.
     *
     * @param usermetadata   user metadata
     * @param username       username
     * @param content        Content of user metadata
     * @throws UserNotFoundException UserNotFoundException
     */
    Usermetadata updateUserMetadata(Usermetadata usermetadata, String username, String content) throws UserNotFoundException, JpaOptimisticLockingFailureException;

    /**
     * Update user metadata's name
     *
     * @param userMetadataId id
     * @param username       username
     * @param name           name of user metadata
     * @throws UserNotFoundException UserNotFoundException
     */
    Usermetadata updateUserMetadataName(Integer userMetadataId, String username, String name) throws UserNotFoundException;

    /**
     * Delete a user metadata logically.
     *
     * @param userMetadataId Id of user metadata
     * @param username       username
     * @throws UserNotFoundException Can't find a user with specified username
     */
    void deleteUserMetadata(Integer userMetadataId, String username) throws UserNotFoundException;

    /**
     * Delete user metadata that linked to specified log
     *
     * @param log  Log
     * @param user User
     * @throws UserNotFoundException
     */
    void deleteUserMetadataByLog(Log log, User user) throws UserNotFoundException;

    /**
     * Find a set of user metadata that linked to specified user and log/s
     *
     * @param username             username
     * @param logIds               List of logId
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @return A set of user metadata
     * @throws UserNotFoundException Can't find a user with specified username
     */
    Set<Usermetadata> getUserMetadata(String username, List<Integer> logIds,
                                      UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException;

    /**
     * Find a set of user metadata by username and type
     *
     * @param username             username
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @return A set of user metadata
     * @throws UserNotFoundException Can't find a user with specified username
     */
    Set<Usermetadata> getUserMetadataByUser(String username, UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException;

    /**
     * Find a list of user metadata by username and type
     *
     * @param username             username
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @return A list of user metadata
     * @throws UserNotFoundException Can't find a user with specified username
     */
    List<Usermetadata> getUserMetadataListByUser(String username, UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException;

    /**
     * Find a set of user metadata that are linked to specified list of Logs and type
     *
     * @param logIds               List of logId
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @return A set of user metadata
     */
    Set<Usermetadata> getUserMetadataByLogs(List<Integer> logIds, UserMetadataTypeEnum userMetadataTypeEnum);

    Set<Usermetadata> getUserMetadataByUserAndLogs(String username, List<Integer> logIds,
                                            UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException;

    /**
     * FInd a set of user metadata that specified group has restricted access to
     *
     * @param group group
     * @param logId log id
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @return A set of user metadata
     */
    Set<Usermetadata> getUserMetadataWithRestrictedViewer(Group group, Integer logId,
                                                          UserMetadataTypeEnum userMetadataTypeEnum);

    /**
     * Find a set of user metadata that are linked to specified Log and type
     *
     * @param logId                Log Id
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @return A set of user metadata
     */
    Set<Usermetadata> getUserMetadataByLog(Integer logId, UserMetadataTypeEnum userMetadataTypeEnum);

    /**
     * Find a set of user metadata that are linked to specified User, Log and type
     *
     * @param username             Name of User
     * @param logId                Log Id
     * @param userMetadataTypeEnum Type of UserMetadata, get from UserMetadataTypeEnum
     * @return A set of user metadata
     * @throws UserNotFoundException Can't find a user with specified username
     */
    Set<Usermetadata> getUserMetadataByUserAndLog(String username, Integer logId,
                                                  UserMetadataTypeEnum userMetadataTypeEnum) throws UserNotFoundException;

    /**
     * Find whether is specified user can write to this user metadata
     *
     * @param username       username
     * @param UsermetadataId Id of user metadata
     * @return boolean
     * @throws UserNotFoundException Can't find a user with specified username
     */
    boolean canUserEditMetadata(String username, Integer UsermetadataId) throws UserNotFoundException;

    /**
     * Return whether this specified user can create user metadata on the specified log
     *
     * @param username username
     * @param logId    Id of Log
     * @return boolen
     * @throws UserNotFoundException Can't find a user with specified username
     */
    boolean canUserCreateMetadata(String username, Integer logId) throws UserNotFoundException;

    /**
     * Find AccessType given a Group and Usermetadata
     * @param group Group
     * @param usermetadata Usermetadata
     * @return AccessType
     */
    AccessType getUserMetadataAccessType(Group group, Usermetadata usermetadata);

    /**
     * Find a set of user metadata which are associated with the logs that
     * this specified user has access to
     *
     * @param username username
     * @return A set of user metadata
     * @throws UserNotFoundException Can't find a user with specified username
     */
    Set<Usermetadata> getUserMetadataWithoutLog(UserMetadataTypeEnum userMetadataTypeEnum, String username) throws UserNotFoundException;


    /**
     * Get dependent logs of a specified user metadata
     *
     * @param usermetadata Usermetadata
     * @return List of Log
     */
    List<Log> getDependentLog(Usermetadata usermetadata);

    /**
     * Get dependent processes of a specified user metadata
     *
     * @param usermetadata Usermetadata
     * @return List of processes
     */
    List<Process> getDependentProcess(Usermetadata usermetadata);

    /**
     * Get User by unique id
     *
     * @param rowGuid unique id
     * @return User
     * @throws UserNotFoundException Can't find a user with specified username
     */
    User findUserByRowGuid(String rowGuid) throws UserNotFoundException;

    /**
     * Get a list of GroupUsermetadata by user metadata id
     *
     * @param userMetadataId User metadata id
     * @return a list of GroupUsermetadata
     */
    List<GroupUsermetadata> getGroupUserMetadata(Integer userMetadataId);

    /**
     * Update or save UserMetadata access rights
     *
     * @param userMetadataId id
     * @param groupRowGuid   group UID
     * @param hasRead        flag
     * @param hasWrite       flag
     * @param hasOwnership   flag
     */
    void saveUserMetadataAccessRights(Integer userMetadataId, String groupRowGuid, boolean hasRead, boolean hasWrite,
                                      boolean hasOwnership);

    /**
     * Update or save UserMetadata access rights
     *
     * @param userMetadataId
     * @param groupRowGuid
     * @param accessType
     */
    void saveUserMetadataAccessType(Integer userMetadataId, String groupRowGuid, AccessType accessType);

    /**
     * Remove a group's access rights to one user metadata
     * @param userMetadataId
     * @param groupRowGuid
     */
    void removeUserMetadataAccessRights(Integer userMetadataId, String groupRowGuid);

    /**
     * @param id
     * @return
     */
    Usermetadata findById(Integer id);

    /**
     * Delete one user metadata only when removing the last OWNER access type
     * @param userMetadataId
     * @param groupRowGuid
     * @return
     */
    boolean canDeleteUserMetadata(Integer userMetadataId, String groupRowGuid);

    /**
     * Check whether the specified group has access (READER, EDITOR or OWNER) to the associated log of specified user
     * metadata
     *
     * @param userMetadataId
     * @param groupRowGuid
     * @return
     */
    boolean canAccessAssociatedLog(Integer userMetadataId, String groupRowGuid);

    Optional<Usermetadata> getUserMetadataById(Integer id);
}
