package org.apromore.dao;

import org.apromore.dao.dataObject.FolderTreeNode;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.ProcessModelVersion;

import java.util.List;

/**
 * Interface domain model Data access object Workspace.
 *
 * @see org.apromore.dao.model.Workspace
 * @author <a href="mailto:cam.james@gmail.com">Igor Goldobin</a>
 * @version 1.0
 */
public interface FolderRepositoryCustom {

    /**
     * Get the Folder tree by the Users Id and Parent Folder.
     * @param parentFolderId the parent's folders Id
     * @param userId the users Id.
     * @return the list of folder tree Nodes
     */
    List<FolderTreeNode> getFolderTreeByUser(int parentFolderId, String userId);

    /**
     * Get all the ProcessModelVersion in a folder and all of it's sub folders to the end of the tree.
     * @param parentFolderId the parent's folders Id
     * @param userId the users Id.
     * @return the list of ProcessModelVersion.
     */
    List<ProcessModelVersion> getProcessModelVersionByFolderUserRecursive(int parentFolderId, String userId);


    /**
     * Get all the processes in a folder and all of it's sub folders to the end of the tree.
     * @param parentFolderId the parent's folders Id
     * @param userId the users Id.
     * @return the list of ProcessModelVersion.
     */
    List<Process> getProcessByFolderUserRecursive(Integer parentFolderId, String userId);

}
