package org.apromore.mapper;

import org.apromore.dao.dataObject.FolderTreeNode;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupProcess;
import org.apromore.model.FolderType;
import org.apromore.model.UserFolderType;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper helper class to convert from the DAO Model to the Webservice Model.
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 * @since 1.0
 */
public class WorkspaceMapper {

    /**
     * Convert a user object to a UserType Webservice object.
     * @param folders the DB User Model
     * @return the Webservice UserType
     */
    public static List<FolderType> convertFolderTreeNodesToFolderTypes(List<FolderTreeNode> folders) {
        List<FolderType> folderTypes = new ArrayList<>();

        for (FolderTreeNode node : folders) {
            FolderType folder = new FolderType();
            folder.setFolderName(node.getName());
            folder.setId(node.getId());
            if (node.getParent() != null) {
                folder.setParentId(node.getParent().getId());
            }
            folder.getFolders().addAll(convertFolderTreeNodesToFolderTypes(node.getSubFolders()));
            folder.setHasRead(node.getHasRead());
            folder.setHasWrite(node.getHasWrite());
            folder.setHasOwnership(node.getHasOwnership());
            folderTypes.add(folder);
        }

        return folderTypes;
    }


    /**
     * Convert a user object to a UserType Webservice object.
     * @param folders the DB User Model
     * @return the Webservice UserType
     */
    public static List<FolderType> convertFoldersToFolderTypes(List<GroupFolder> folders) {
        List<FolderType> folderTypes = new ArrayList<>();

        for (GroupFolder node : folders) {
            FolderType folder = new FolderType();
            folder.setFolderName(node.getFolder().getName());
            folder.setId(node.getFolder().getId());
            if (node.getFolder().getParentFolder() != null) {
                folder.setParentId(node.getFolder().getParentFolder().getId());
            }
            folder.setHasRead(node.isHasRead());
            folder.setHasWrite(node.isHasWrite());
            folder.setHasOwnership(node.isHasOwnership());
            folderTypes.add(folder);
        }

        return folderTypes;
    }

    /**
     * Convert a user object to a UserType Webservice object.
     * @param folders the DB User Model
     * @return the Webservice UserType
     */
    public static List<FolderType> convertFolderListToFolderTypes(List<Folder> folders) {
        List<FolderType> folderTypes = new ArrayList<>();
        for (Folder node : folders) {
            FolderType folder = new FolderType();
            folder.setFolderName(node.getName());
            folder.setId(node.getId());
            if (node.getParentFolder() != null) {
                folder.setParentId(node.getParentFolder().getId());
            }
            folderTypes.add(folder);
        }

        return folderTypes;
    }

    /**
     * Convert group/folder pairs to UserType Webservice objects.
     * @param groupFolders  the DB model
     * @return the Webservice UserType
     */
    public static List<UserFolderType> convertGroupFoldersToFolderUserTypes(List<GroupFolder> groupFolders) {
        List<UserFolderType> userFolderTypes = new ArrayList<>();
        for(GroupFolder node : groupFolders) {
            UserFolderType user = new UserFolderType();
            user.setEmail(node.getGroup().getName() + "@example.com");
            user.setUserId(node.getGroup().getRowGuid());
            user.setFullName(node.getGroup().getName());
            user.setHasRead(node.isHasRead());
            user.setHasWrite(node.isHasWrite());
            user.setHasOwnership(node.isHasOwnership());
            userFolderTypes.add(user);
        }
        return userFolderTypes;
    }

    /**
     * Convert group/process pairs to UserType Webservice objects.
     * @param groupProcesses the DB model
     * @return the Webservice UserType
     */
    public static List<UserFolderType> convertGroupProcessesToFolderUserTypes(List<GroupProcess> groupProcesses) {
        List<UserFolderType> userFolderTypes = new ArrayList<>();
        for(GroupProcess node : groupProcesses) {
            UserFolderType user = new UserFolderType();
            user.setEmail(node.getGroup().getName() + "@example.com");
            user.setUserId(node.getGroup().getRowGuid());
            user.setFullName(node.getGroup().getName());
            user.setHasRead(node.getHasRead());
            user.setHasWrite(node.getHasWrite());
            user.setHasOwnership(node.getHasOwnership());
            userFolderTypes.add(user);
        }
        return userFolderTypes;
    }

}
