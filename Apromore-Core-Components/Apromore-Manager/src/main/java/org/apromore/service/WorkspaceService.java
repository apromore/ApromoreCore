/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

import java.util.List;

import org.apromore.dao.dataObject.FolderTreeNode;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupLog;
import org.apromore.dao.model.GroupProcess;
import org.apromore.dao.model.Log;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.User;
import org.apromore.exception.NotAuthorizedException;
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

    Page<Process> getProcesses(String userId, Integer folderId, Pageable pageable);

    Page<Log> getLogs(String userId, Integer folderId, Pageable pageable);

    void createFolder(String userId, String folderName, Integer parentFolderId, Boolean isGEDMatrixReady);

    void addProcessToFolder(Integer processId, Integer folderId);

    boolean isGEDReadyFolder(Integer folderId);

    void updateFolder(Integer folderId, String folderName, Boolean isGEDMatrixReady, User user) throws NotAuthorizedException;

    void deleteFolder(Integer folderId, User user) throws NotAuthorizedException;

    List<FolderTreeNode> getWorkspaceFolderTree(String userId);

    List<Folder> getBreadcrumbs(Integer folderId);

    List<GroupFolder> getSubFolders(String userRowGuid, Integer folderId);

    String saveFolderPermissions(Integer folderId, String groupRowGuid, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String saveProcessPermissions(Integer processId, String groupRowGuid, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String saveLogPermissions(Integer logId, String groupRowGuid, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String removeFolderPermissions(Integer folderId, String userId);

    String removeProcessPermissions(Integer processId, String userId);

    String removeLogPermissions(Integer logId, String userId);

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
    
    Log copyLog(Integer logId, Integer targetFolderId, String userName, boolean isPublic) throws Exception;
    
    Log moveLog(Integer logId, Integer newFolderId) throws Exception;
    
    Process copyProcess(Integer processId, List<Integer> pmvIDs, Integer newFolderId, String userName, boolean isPublic) throws Exception;
    
    Process moveProcess(Integer processId, Integer newFolderId) throws Exception;
    
    Folder copyFolder(Integer folderId, Integer sourceFolderId, Integer targetFolderId) throws Exception;
    
    Folder moveFolder(Integer folderId, Integer newParentFolderId) throws Exception;
}
