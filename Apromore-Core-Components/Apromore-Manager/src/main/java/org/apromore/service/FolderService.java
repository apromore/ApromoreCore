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

import groovy.util.ResourceException;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessModelVersion;
import org.apromore.service.model.FolderTreeNode;

import java.util.List;

public interface FolderService {

    List<FolderTreeNode> getFolderTreeByUser(int parentFolderId, String userId);

    List<ProcessModelVersion> getProcessModelVersionByFolderUserRecursive(
            Integer parentFolderId, String userId);

    List<Process> getProcessByFolderUserRecursive(Integer parentFolderId, String userId);

    void updateFolderChainForSubFolders(Integer folderId, String newFolderChainPrefix);

    List<Folder> getParentFolders(Integer id);

    List<Folder> getSubFolders(Integer id, boolean includeCurrentFolder);

    int findFolderIdByPath(String path, String userId, WorkspaceService workspaceService)
        throws ResourceException;
}
