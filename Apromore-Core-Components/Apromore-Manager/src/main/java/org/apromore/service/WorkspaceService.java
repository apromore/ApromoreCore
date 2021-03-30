/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

import java.util.List;
import java.util.Set;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupLog;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.exception.NotAuthorizedException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.service.model.FolderTreeNode;
import org.apromore.util.AccessType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface for the User Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface WorkspaceService {

    /**
     * Finds a folders and returns it.
     * @param folderId the id of the folder to find.
     * @return the found folder or null.
     */
    Folder getFolder(Integer folderId);

    List<GroupFolder> getGroupFolders(Integer folderId);

    List<GroupProcess> getGroupProcesses(Integer processId);

    List<GroupLog> getGroupLogs(Integer logId);

    List<GroupProcess> getGroupProcesses(String userId, Integer folderId);

    List<Process> getProcessesByPrefix(String prefix);

    List<Log> getLogsByPrefix(String prefix);

    Page<Process> getProcesses(String userId, Integer folderId, Pageable pageable);

    Page<Log> getLogs(String userId, Integer folderId, Pageable pageable);

    Integer createFolder(String userId, String folderName, Integer parentFolderId, Boolean isGEDMatrixReady);

    void addProcessToFolder(User user, Integer processId, Integer folderId);

    boolean isGEDReadyFolder(Integer folderId);

    void updateFolder(Integer folderId, String folderName, Boolean isGEDMatrixReady, User user) throws NotAuthorizedException;

    void deleteFolder(Integer folderId, User user) throws Exception;

    List<FolderTreeNode> getWorkspaceFolderTree(String userId);

    List<Folder> getBreadcrumbs(Integer folderId);

    List<GroupFolder> getSubFolders(String userRowGuid, Integer folderId);

    String saveFolderPermissions(Integer folderId, String groupRowGuid, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String saveProcessPermissions(Integer processId, String groupRowGuid, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String saveLogPermissions(Integer logId, String groupRowGuid, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String saveLogAccessRights(Integer logId, String groupRowGuid, AccessType accessType, boolean shareUserMetadata);

    String removeFolderPermissions(Integer folderId, String userId);

    String removeProcessPermissions(Integer processId, String userId);

    String removeLogPermissions(Integer logId, String userId, String username) throws UserNotFoundException;

    /**
     * Creates the public status for the users to have read rights to this model.
     * @param process the process.
     */
    void createPublicStatusForUsers(final Process process);

    /**
     * Removes all users from having access to this model, except the owner.
     * @param process the process model we are restricting access to.
     */
    void removePublicStatusForUsers(final Process process);
    
    /**
     * Copy log to a new folder for a user
     * @param logId
     * @param targetFolderId
     * @param userName
     * @param isPublic
     * @return: new copied log
     * @throws Exception
     */
    Log copyLog(Integer logId, Integer targetFolderId, String userName, boolean isPublic) throws Exception;
    
    /**
     * Move log to a new folder
     * @param logId
     * @param newFolderId
     * @return the new moved log
     * @throws Exception
     */
    Log moveLog(Integer logId, Integer newFolderId) throws Exception;
    
    /**
     * Copy a set of process model versions to a new folder for a user 
     * @param processId
     * @param pmvVersions
     * @param newFolderId
     * @param userName
     * @param isPublic
     * @return the copied process model
     * @throws Exception
     */
    Process copyProcessVersions(Integer processId, List<String> pmvVersions, Integer newFolderId, String userName, boolean isPublic) throws Exception;
    
    /**
     * Copy a process model (with all process model versions) to a new folder for a user
     * @param processId
     * @param newFolderId
     * @param userName
     * @param isPublic
     * @return the copied process model
     * @throws Exception
     */
    Process copyProcess(Integer processId, Integer newFolderId, String userName, boolean isPublic) throws Exception;
    
    /**
     * Move a process model (with all process model versions) to a new folder
     * @param processId
     * @param newFolderId
     * @return the moved process model
     * @throws Exception
     */
    Process moveProcess(Integer processId, Integer newFolderId) throws Exception;

    /**
     * Copy a folder to a new parent folder; all subfolders and items are copied recursively
     * @param folderId
     * @param sourceFolderId
     * @param targetFolderId
     * @return
     * @throws Exception
     */
    Folder copyFolder(Integer folderId, Integer sourceFolderId, Integer targetFolderId) throws Exception;
    
    /**
     * Move a folder to a new parent folder
     * @param folderId
     * @param newParentFolderId
     * @return the moved folder
     * @throws Exception
     */
    Folder moveFolder(Integer folderId, Integer newParentFolderId) throws Exception;

    /**
     *
     * Get a list of Folders that the specified user's singleton group is the only owner of
     * Note: Only considering singleton group here
     *
     * @param user User
     * @return Return a list of Folders that the specified user's singleton group is the only owner of
     */
    List<Folder> getSingleOwnerFolderByUser(User user);

    /**
     *
     * Get a list of Logs that the specified user's singleton group is the only owner of
     * Note: Only considering singleton group here
     *
     * @param user User
     * @return Return a list of Logs that the specified user's singleton group is the only owner of
     */
    List<Log> getSingleOwnerLogByUser(User user);

    /**
     *
     * Get a list of Processes that the specified user's singleton group is the only owner of
     * Note: Only considering singleton group here
     *
     * @param user User
     * @return Return a list of Processes that the specified user's singleton group is the only owner of
     */
    List<Process> getSingleOwnerProcessByUser(User user);

    /**
     * Whether the specified user is the only owner of any folder, log or process
     *
     * @param user User to be checked
     * @return Whether the specified user is the only owner of any folder, log or process
     */
    Boolean isOnlyOwner(User user);

    /**
     * Transfer the ownership of all the folder, log and process that the user-to-be-deleted is the only owner of
     *
     * @param sourceUser User to be deleted
     * @param targetUser User to transfer ownership to
     */
    void transferOwnership(User sourceUser, User targetUser);

    /**
     * Remove all the folder, log and process that the user-to-be-deleted is the only owner of
     *
     * @param user user to be deleted
     */
    void deleteOwnerlessArtifact(User user);

}
