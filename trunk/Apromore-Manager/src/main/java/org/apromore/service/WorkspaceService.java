package org.apromore.service;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.FolderTreeNode;
import org.apromore.dao.model.FolderUser;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessUser;
import org.apromore.dao.model.User;

import java.util.List;

/**
 * Interface for the User Service. Defines all the methods that will do the majority of the work for
 * the Apromore application.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public interface WorkspaceService {

    /**
     * Finds a folders and returns it.
     * @param folderId the id oif the folder to find.
     * @return the found folder or null.
     */
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

    /**
     * Updates the Folders security so all users now have read access to that folder (from the root folder up).
     * NOTE: this method is recursive, we update this folder then move up the folder tree until the root.
     * @param folder the folder we have a public model in.
     * @param users all the users we giving access.
     */
    void updatePublicFoldersForUsers(final Folder folder, final List<User> users);

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
     * For this User make sure all the public models and folders are accessible.
     * @param user the user to update.
     */
    void updateUsersPublicModels(User user);
}
