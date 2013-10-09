package org.apromore.mapper;

import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.FolderTreeNode;
import org.apromore.dao.model.FolderUser;
import org.apromore.dao.model.ProcessUser;
import org.apromore.model.FolderType;
import org.apromore.model.UserFolderType;

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
    public static List<FolderType> convertFoldersToFolderTypes(List<FolderUser> folders) {
        List<FolderType> folderTypes = new ArrayList<>();

        for (FolderUser node : folders) {
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
     * Convert a user object to a UserType Webservice object.
     * @param folders the DB User Model
     * @return the Webservice UserType
     */
    public static List<UserFolderType> convertFolderUsersToFolderUserTypes(List<FolderUser> folders) {
        List<UserFolderType> userFolderTypes = new ArrayList<>();
        for(FolderUser node : folders) {
            UserFolderType user = new UserFolderType();
            user.setEmail(node.getUser().getUsername());
            user.setUserId(node.getUser().getRowGuid());              
            user.setHasRead(node.isHasRead());
            user.setFullName(node.getUser().getFirstName() + " " + node.getUser().getLastName());
            user.setHasWrite(node.isHasWrite());
            user.setHasOwnership(node.isHasOwnership());
            userFolderTypes.add(user);
        }
        return userFolderTypes;
    }

    /**
     * Convert a user object to a UserType Webservice object.
     * @param processUsers the DB User Model
     * @return the Webservice UserType
     */
    public static List<UserFolderType> convertProcessUsersToFolderUserTypes(List<ProcessUser> processUsers) {
        List<UserFolderType> userFolderTypes = new ArrayList<>();
        for(ProcessUser node : processUsers) {
            UserFolderType user = new UserFolderType();
            user.setEmail(node.getUser().getUsername());
            user.setUserId(node.getUser().getRowGuid());
            user.setHasRead(node.isHasRead());
            user.setFullName(node.getUser().getFirstName() + " " + node.getUser().getLastName());
            user.setHasWrite(node.isHasWrite());
            user.setHasOwnership(node.isHasOwnership());
            userFolderTypes.add(user);
        }
        return userFolderTypes;
    }

}
