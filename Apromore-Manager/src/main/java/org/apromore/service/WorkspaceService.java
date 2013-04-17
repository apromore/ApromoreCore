package org.apromore.service;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.FolderTreeNode;
import org.apromore.dao.model.FolderUser;
import org.apromore.dao.model.ProcessUser;

import java.util.List;

/**
 * Interface for the User Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface WorkspaceService {

    Folder getFolder(Integer folderId);

    List<FolderUser> getFolderUsers(Integer folderId);

    List<ProcessUser> getProcessUsers(Integer processId);

    List<ProcessUser> getUserProcessesOrig(String userId, Integer folderId);

    void createFolder(String userId, String folderName, Integer parentFolderId);

    void addProcessToFolder(Integer processId, Integer folderId);

    void updateFolder(Integer folderId, String folderName);

    void deleteFolder(Integer folderId);

    List<FolderTreeNode> getWorkspaceFolderTree(String userId);

    List<Folder> getBreadcrumbs(String userId, Integer folderId);

    List<FolderUser> getSubFolders(String userId, Integer folderId);

    String saveFolderPermissions(Integer folderId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String saveProcessPermissions(Integer processId, String userId, boolean hasRead, boolean hasWrite, boolean hasOwnership);

    String removeFolderPermissions(Integer folderId, String userId);

    String removeProcessPermissions(Integer processId, String userId);
    
}
