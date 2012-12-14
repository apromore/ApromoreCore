package org.apromore.dao.jpa;

import org.apromore.dao.FolderRepositoryCustom;
import org.apromore.dao.FolderUserRepository;
import org.apromore.dao.model.FolderTreeNode;
import org.apromore.dao.model.FolderUser;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * implementation of the org.apromore.dao.ProcessDao interface.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class FolderRepositoryCustomImpl implements FolderRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private FolderUserRepository folderUserRepository;


    /**
     * @see FolderRepositoryCustom#getFolderTreeByUser(int, String)
     * {@inheritDoc}
     */
    @Override
    public List<FolderTreeNode> getFolderTreeByUser(int parentFolderId, String userId) {
        List<FolderUser> folders = folderUserRepository.findByParentFolderAndUser(parentFolderId, userId);

        List<FolderTreeNode> treeNodes = new ArrayList<FolderTreeNode>();
        for(FolderUser folder : folders){
            FolderTreeNode treeNode = new FolderTreeNode();
            treeNode.setId(folder.getFolder().getId());
            treeNode.setName(folder.getFolder().getName());
            treeNode.setParentId(parentFolderId);
            treeNode.setHasRead(folder.isHasRead());
            treeNode.setHasWrite(folder.isHasWrite());
            treeNode.setHasOwnership(folder.isHasOwnership());
            treeNode.setSubFolders(this.getFolderTreeByUser(folder.getFolder().getId(), userId));
            treeNodes.add(treeNode);
        }

        return treeNodes;
    }

}
