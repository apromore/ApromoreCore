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

package org.apromore.plugin.portal.bpmneditor.viewmodel;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.apromore.dao.model.User;
import org.apromore.manager.client.ManagerService;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.common.FolderTreeNodeTypes;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.util.FolderTypeComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA. User: Igor Date: 2/07/12 Time: 6:56 PM To change this template use File
 * | Settings | File Templates.
 */
@SuppressWarnings("unchecked")
public class ProcessFolderTree {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessFolderTree.class);

    private FolderTreeNode root;
    private boolean loadAll = false;
    private FolderTreeNode currentFolder;
    MainController mainController;

    public ProcessFolderTree(boolean loadAll, int currentFolderId, MainController mainController) {
        this.mainController = mainController;
        this.loadAll = loadAll;
        root = new FolderTreeNode((FolderType) null, null, true, FolderTreeNodeTypes.Folder);

        if (currentFolderId == 0) {
            currentFolder = root;
        }
        FolderType folder = new FolderType();
        folder.setId(0);
        folder.setFolderName("Home");
        FolderTreeNode homeNode = new FolderTreeNode(folder, null, true, FolderTreeNodeTypes.Folder);

        root.add(homeNode);

        buildProcessTree(homeNode, this.mainController.getManagerService()
            .getWorkspaceFolderTree(UserSessionManager.getCurrentUser().getId()), 0, new HashSet<>());
    }

    public FolderTreeNode getCurrentFolder() {
        return currentFolder;
    }

    private FolderTreeNode buildProcessTree(FolderTreeNode node, List<FolderType> folders, int folderId,
                                     HashSet<Integer> set) {

        Collections.sort(folders, new FolderTypeComparator());

        if (folderId == 0 && folders.isEmpty()) {
            node.setOpen(false);
        }
        for (FolderType folder : folders) {
            if (!set.contains(folder.getId())) {
                treeBuildOnData(node, folderId, set, folder);
            } else {
                node.add(
                    new FolderTreeNode((SummaryType) null, null, !loadAll, FolderTreeNodeTypes.Process));
            }
        }

        addProcesses(node, folderId);

        return node;
    }

    private void treeBuildOnData(FolderTreeNode node, int folderId, HashSet<Integer> set, FolderType folder) {
        boolean open = false;
        if (folderId == 0) {
            open = true;
        }
        FolderTreeNode childNode =
            new FolderTreeNode(folder, null, open, FolderTreeNodeTypes.Folder);
        set.add(folder.getId());

        if (!folder.getFolders().isEmpty()) {
            node.add(buildProcessTree(childNode, folder.getFolders(), folder.getId(), set));
        } else {
            node.add(childNode);
            addProcesses(childNode, folder.getId());
        }
    }

    private void addProcesses(FolderTreeNode node, int folderId) {
        if (loadAll) {
            final int PAGE_SIZE = 100;

            ManagerService service = this.mainController.getManagerService();
            String userId = UserSessionManager.getCurrentUser().getId();
            try {
                User user = this.mainController.getSecurityService().getUserById(userId);
                int page = 0;
                SummariesType processes;
                do {
                    processes = service.getProcessSummaries(userId, folderId, page, PAGE_SIZE);
                    for (SummaryType summaryType : processes.getSummary()) {
                        if (ItemHelpers.canModify(user, summaryType)) {
                            node.add(new FolderTreeNode(summaryType, null, !loadAll, FolderTreeNodeTypes.Process));
                        }
                    }
                } while (PAGE_SIZE * page++ + processes.getSummary().size() < processes.getCount());
            } catch (Exception ex) {
                LOGGER.error("Error in rendering process", ex);
            }
        }
    }

    public FolderTreeNode getRoot() {
        return root;
    }

    public boolean getLoadAll() {
        return loadAll;
    }

    public void setLoadAll(boolean newLoadAll) {
        this.loadAll = newLoadAll;
    }
}
