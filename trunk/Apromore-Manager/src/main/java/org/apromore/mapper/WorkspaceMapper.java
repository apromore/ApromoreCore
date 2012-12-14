package org.apromore.mapper;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.FolderTreeNode;
import org.apromore.dao.model.FolderUser;
import org.apromore.dao.model.ProcessUser;
import org.apromore.dao.model.Workspace;
import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.UserFolderType;
import org.apromore.model.WorkspaceType;

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
     * @param workspace the DB User Model
     * @return the Webservice UserType
     */
    public static WorkspaceType convertWorkspaceType(Workspace workspace) {
        WorkspaceType workspaceType = new WorkspaceType();
        workspaceType.setId(workspace.getId());
        workspaceType.setWorkspaceName(workspace.getName());

        for (Folder folder : workspace.getFolders()) {
            FolderType newFolder = new FolderType();
            newFolder.setId(folder.getId());
            newFolder.setFolderName(folder.getName());
        }

        return workspaceType;
    }

    /**
     * Convert a user object to a UserType Webservice object.
     * @param workspaces the DB User Model
     * @return the Webservice UserType
     */
    public static List<WorkspaceType> convertWorkspaceTypes(List<Workspace> workspaces) {
        List<WorkspaceType> workspaceTypes = new ArrayList<WorkspaceType>();

        for (Workspace workspace : workspaces) {
            WorkspaceType workspaceType = new WorkspaceType();
            workspaceType.setId(workspace.getId());
            workspaceType.setWorkspaceName(workspace.getName());

            for (Folder folder : workspace.getFolders()) {
                FolderType newFolder = new FolderType();
                newFolder.setId(folder.getId());
                newFolder.setFolderName(folder.getName());
            }
            workspaceTypes.add(workspaceType);
        }
        return workspaceTypes;
    }

    /**
     * Convert a user object to a UserType Webservice object.
     * @param folders the DB User Model
     * @return the Webservice UserType
     */
    public static List<FolderType> convertFolderTreeNodesToFolderTypes(List<FolderTreeNode> folders) {
        List<FolderType> folderTypes = new ArrayList<FolderType>();

        for (FolderTreeNode node : folders) {
            FolderType folder = new FolderType();
            folder.setFolderName(node.getName());
            folder.setId(node.getId());
            folder.setParentId(node.getParentId());
            List<FolderType> subFolders = convertFolderTreeNodesToFolderTypes(node.getSubFolders());

            for (FolderType subFolder : subFolders) {
                folder.getFolders().add(subFolder);
            }
            folder.setHasRead(node.getHasRead());
            folder.setHasWrite(node.getHasWrite());
            folder.setHasOwnership(node.getHasOwnership());
            folderTypes.add(folder);
        }
        return folderTypes;
    }

    public static List<ProcessSummaryType> convertProcessUsersToProcessSummaryTypes(List<ProcessUser> processes) {
        List<ProcessSummaryType> processSummaryTypes = new ArrayList<ProcessSummaryType>();

        for (ProcessUser processUser : processes) {
            ProcessSummaryType processSummaryType = new ProcessSummaryType();
            processSummaryType.setDomain(processUser.getProcess().getDomain());
            processSummaryType.setHasRead(processUser.isHasRead());
            processSummaryType.setHasWrite(processUser.isHasWrite());
            processSummaryType.setHasOwnership(processUser.isHasOwnership());
            processSummaryType.setId(processUser.getProcess().getId());
            processSummaryType.setName(processUser.getProcess().getName());
            processSummaryTypes.add(processSummaryType);
        }
        return processSummaryTypes;
    }

    /**
     * Convert a user object to a UserType Webservice object.
     * @param folders the DB User Model
     * @return the Webservice UserType
     */
    public static List<FolderType> convertFoldersToFolderTypes(List<FolderUser> folders) {
        List<FolderType> folderTypes = new ArrayList<FolderType>();

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
        List<FolderType> folderTypes = new ArrayList<FolderType>();

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
        List<UserFolderType> userFolderTypes = new ArrayList<UserFolderType>();

        for(FolderUser node : folders)
        {
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
        List<UserFolderType> userFolderTypes = new ArrayList<UserFolderType>();

        for(ProcessUser node : processUsers)
        {
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
     * @param folder the DB User Model
     * @return the Webservice UserType
     */
    public static FolderType convertFolderTypes(Folder folder) {
        FolderType folderType = new FolderType();
        folderType.setId(folder.getId());
        folderType.setFolderName(folder.getName());

        return folderType;
    }

    /**
     * Convert from the WS (UserType) to the DB model (User).
     * @param workspaceType the userType from the WebService
     * @return the User dao model populated.
     */
    public static Workspace convertFromWorkspaceType(WorkspaceType workspaceType) {

        Workspace workspace = new Workspace();
        workspace.setId(workspaceType.getId());
        workspace.setName(workspaceType.getWorkspaceName());

        for (FolderType folderType : workspaceType.getFolders()) {
            Folder folder = new Folder();
            folder.setId(folderType.getId());
            folder.setName(folderType.getFolderName());
            workspace.getFolders().add(folder);
        }

        return workspace;
    }

}
