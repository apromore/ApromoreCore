/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.mapper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apromore.dao.model.Folder;
import org.apromore.dao.model.GroupFolder;
import org.apromore.dao.model.GroupLog;
import org.apromore.dao.model.GroupProcess;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.GroupAccessType;
import org.apromore.service.model.FolderTreeNode;
import org.apromore.common.Constants;

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
        
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        
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
			if (node.getFolder().getCreatedBy() != null) {
				folder.setOwnerName(node.getFolder().getCreatedBy().getUsername());
			}

			if (node.getFolder().getDateModified() != null) {
				folder.setLastUpdate(dateFormat.format(node.getFolder().getDateModified()));
			} else {
				folder.setLastUpdate(dateFormat.format(node.getFolder().getDateCreated()));
			}
            folder.setCreatedDate(dateFormat.format(node.getFolder().getDateCreated()));
			
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
     * Convert group/folder pairs to GroupAccessType Webservice objects.
     * @param groupFolders  the DB model
     * @return the Webservice GroupAccessType
     */
    public static List<GroupAccessType> convertGroupFoldersToGroupAccessTypes(List<GroupFolder> groupFolders) {
        List<GroupAccessType> groupAccessTypes = new ArrayList<>();
        for(GroupFolder node : groupFolders) {
            GroupAccessType gat = new GroupAccessType();
            gat.setGroupId(node.getGroup().getRowGuid());
            gat.setName(node.getGroup().getName());
            gat.setHasRead(node.isHasRead());
            gat.setHasWrite(node.isHasWrite());
            gat.setHasOwnership(node.isHasOwnership());
            groupAccessTypes.add(gat);
        }
        return groupAccessTypes;
    }

    /**
     * Convert group/process pairs to GroupAccessType Webservice objects.
     * @param groupProcesses the DB model
     * @return the Webservice GroupAccessType
     */
    public static List<GroupAccessType> convertGroupProcessesToGroupAccessTypes(List<GroupProcess> groupProcesses) {
        List<GroupAccessType> groupAccessTypes = new ArrayList<>();
        for(GroupProcess node : groupProcesses) {
            GroupAccessType gat = new GroupAccessType();
            gat.setGroupId(node.getGroup().getRowGuid());
            gat.setName(node.getGroup().getName());
            gat.setHasRead(node.getHasRead());
            gat.setHasWrite(node.getHasWrite());
            gat.setHasOwnership(node.getHasOwnership());
            groupAccessTypes.add(gat);
        }
        return groupAccessTypes;
    }

    /**
     * Convert group/log pairs to GroupAccessType Webservice objects.
     * @param groupLogs the DB model
     * @return the Webservice GroupAccessType
     */
    public static List<GroupAccessType> convertGroupLogsToGroupAccessTypes(List<GroupLog> groupLogs) {
        List<GroupAccessType> groupAccessTypes = new ArrayList<>();
        for(GroupLog node : groupLogs) {
            GroupAccessType gat = new GroupAccessType();
            gat.setGroupId(node.getGroup().getRowGuid());
            gat.setName(node.getGroup().getName());
            gat.setHasRead(node.getHasRead());
            gat.setHasWrite(node.getHasWrite());
            gat.setHasOwnership(node.getHasOwnership());
            groupAccessTypes.add(gat);
        }
        return groupAccessTypes;
    }
}
