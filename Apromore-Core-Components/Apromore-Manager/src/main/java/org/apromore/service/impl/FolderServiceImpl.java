/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.service.impl;

import lombok.Getter;
import lombok.Setter;
import org.apromore.dao.FolderRepository;
import org.apromore.dao.GroupFolderRepository;
import org.apromore.dao.GroupProcessRepository;
import org.apromore.dao.model.Process;
import org.apromore.dao.model.*;
import org.apromore.service.FolderService;
import org.apromore.service.model.FolderTreeNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Getter
@Setter
public class FolderServiceImpl implements FolderService {

    @Inject
    private GroupFolderRepository groupFolderRepository;

    @Inject
    private GroupProcessRepository groupProcessRepository;

    @Inject
    private FolderRepository folderRepository;

    @Override
    public List<FolderTreeNode> getFolderTreeByUser(int parentFolderId, String userId) {
        List<GroupFolder> folders = groupFolderRepository.findByParentFolderAndUser(parentFolderId, userId);
        Map<Integer, FolderTreeNode> map = new HashMap<>();

        List<FolderTreeNode> treeNodes = new ArrayList<>();
        for (GroupFolder folder : folders) {
            if (map.containsKey(folder.getFolder().getId())) {
                // This is not the first group granting folder access to the user, so just merge
                // in additional permissions
                FolderTreeNode treeNode = map.get(folder.getFolder().getId());
                treeNode.setHasRead(treeNode.getHasRead() || folder.isHasRead());
                treeNode.setHasWrite(treeNode.getHasWrite() || folder.isHasWrite());
                treeNode.setHasOwnership(treeNode.getHasOwnership() || folder.isHasOwnership());
            } else {
                // This is the first group granting folder access to the user, so add the folder
                // to the tree
                FolderTreeNode treeNode = new FolderTreeNode();
                map.put(folder.getFolder().getId(), treeNode);
                treeNode.setId(folder.getFolder().getId());
                treeNode.setName(folder.getFolder().getName());
                treeNode.setHasRead(folder.isHasRead());
                treeNode.setHasWrite(folder.isHasWrite());
                treeNode.setHasOwnership(folder.isHasOwnership());
                treeNode.setSubFolders(this.getFolderTreeByUser(folder.getFolder().getId(), userId));

                for (FolderTreeNode subFolders : treeNode.getSubFolders()) {
                    subFolders.setParent(treeNode);
                }

                treeNodes.add(treeNode);
            }
        }

        return treeNodes;
    }

    @Override
    public List<ProcessModelVersion> getProcessModelVersionByFolderUserRecursive(Integer parentFolderId,
                                                                                 String userId) {
        List<ProcessModelVersion> processes = new ArrayList<>();
        processes.addAll(getProcessModelVersions(
                groupProcessRepository.findAllProcessesInFolderForUser(parentFolderId, userId)));

        for (GroupFolder folder : groupFolderRepository.findByParentFolderAndUser(parentFolderId, userId)) {
            processes.addAll(getProcessModelVersionByFolderUserRecursive(folder.getFolder().getId(), userId));
        }

        return processes;
    }

    @Override
    public List<Process> getProcessByFolderUserRecursive(Integer parentFolderId, String userId) {
        List<Process> processes = new ArrayList<>();
        if (parentFolderId == 0) {
            parentFolderId = null;
        }
        processes.addAll(getProcesses(groupProcessRepository.findAllProcessesInFolderForUser(parentFolderId, userId)));

        for (GroupFolder folder : groupFolderRepository.findByParentFolderAndUser(parentFolderId, userId)) {
            processes.addAll(getProcessByFolderUserRecursive(folder.getFolder().getId(), userId));
        }

        return processes;
    }

    private List<Process> getProcesses(List<GroupProcess> processUsers) {
        List<Process> processes = new ArrayList<>();

        for (GroupProcess ps : processUsers) {
            processes.add(ps.getProcess());
        }

        return processes;
    }

    private List<ProcessModelVersion> getProcessModelVersions(List<GroupProcess> processUsers) {
        List<ProcessModelVersion> pmvs = new ArrayList<>();

        for (GroupProcess ps : processUsers) {
            for (ProcessBranch branch : ps.getProcess().getProcessBranches()) {
                pmvs.addAll(branch.getProcessModelVersions());
            }
        }

        return pmvs;
    }

    @Transactional
    public void updateFolderChainForSubFolders(Integer oldfolderId, String newFolderChainPrefix) {
        Folder oldFolder = folderRepository.findUniqueByID(oldfolderId);
        String oldChain = oldFolder.getParentFolderChain() + "_" + oldfolderId;
        List<Folder> folders = getSubFolders(oldfolderId, false);

        for (Folder folder : folders) {
            String folderChain = folder.getParentFolderChain();
            folder.setParentFolderChain(folderChain.replaceAll(getEscapedString(oldChain), newFolderChainPrefix));
        }

        folderRepository.save(folders);

    }

    private String getEscapedString(String prefix) {
        return prefix.replaceAll("\\_", "\\\\_");
    }


    /*
     * This method gets a list of Subfolders using parent chain query
     * Example : folders with parent chain 0_1_2_%
     */
    @Override
    public List<Folder> getSubFolders(Integer id, boolean includeCurrentFolder) {
        Folder oldFolder = folderRepository.findUniqueByID(id);
        String chain = oldFolder.getParentFolderChain() + "_" + id;
        String prefix = chain + "_";
        prefix = getEscapedString(prefix) + "%";
        List<Folder> folders = new ArrayList(folderRepository.findByParentFolderIdOrParentFolderChainLike(id, prefix));
        if (includeCurrentFolder) {
            folders.add(oldFolder);
        }
        return folders;
    }

    @Override
    public List<Folder> getParentFolders(Integer id) {
        Folder folder = folderRepository.findUniqueByID(id);
        String[] parentChain = folder.getParentFolderChain().split("\\_");
        List<Integer> parentChainIn = getIntListFromStringArray(parentChain);
        return folderRepository.findByIdIn(parentChainIn);
    }

    private List<Integer> getIntListFromStringArray(String[] parentChain) {
        List<Integer> parentChainList = new ArrayList<Integer>();
        for (String parentId : parentChain) {
            parentChainList.add(Integer.parseInt(parentId));
        }
        return parentChainList;
    }

}
