/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.portal.common;

import org.apromore.manager.client.ManagerService;
import org.apromore.model.FolderType;
import org.apromore.model.LogSummaryType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.SummariesType;
import org.apromore.model.SummaryType;

import java.util.HashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 2/07/12
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("unchecked")
public class FolderTree {

    private FolderTreeNode root;
    private boolean loadAll = false;

    public FolderTree(boolean loadAll) {
        this.loadAll = loadAll;
        root = new FolderTreeNode((FolderType) null, null, true, FolderTreeNodeTypes.Folder);

        FolderType folder = new FolderType();
        folder.setId(0);
        folder.setFolderName("Home");
        FolderTreeNode homeNode = new FolderTreeNode(folder, null, true, FolderTreeNodeTypes.Folder);

        root.add(homeNode);
        buildTree(homeNode, UserSessionManager.getTree(), 0, new HashSet<Integer>());
    }

    private FolderTreeNode buildTree(FolderTreeNode node, List<FolderType> folders, int folderId, HashSet<Integer> set) {

        for (FolderType folder : folders) {

            if(!set.contains(folder.getId())) {

                FolderTreeNode childNode = new FolderTreeNode(folder, null, !loadAll, FolderTreeNodeTypes.Folder);
                set.add(folder.getId());

                if (folder.getFolders().size() > 0) {
                    node.add(buildTree(childNode, folder.getFolders(), folder.getId(), set));
                } else {
                    node.add(childNode);
                    addProcessesAndLogs(childNode, folder.getId());
                }
            }else {
                node.add(new FolderTreeNode((SummaryType) null, null, !loadAll, FolderTreeNodeTypes.Process));
            }
        }

        addProcessesAndLogs(node, folderId);

        return node;
    }

    private void addProcessesAndLogs(FolderTreeNode node, int folderId) {
        if (loadAll) {
            final int PAGE_SIZE = 100;

            ManagerService service = UserSessionManager.getMainController().getService();
            String userId = UserSessionManager.getCurrentUser().getId();

            int page = 0;
            SummariesType processes;
            do {
                processes = service.getProcessSummaries(userId, folderId, page, PAGE_SIZE);
                for (SummaryType summaryType : processes.getSummary()) {
                    assert summaryType instanceof ProcessSummaryType;
                    node.add(new FolderTreeNode(summaryType, null, !loadAll, FolderTreeNodeTypes.Process));
                }
            } while(PAGE_SIZE * page++ + processes.getSummary().size() < processes.getCount());

            int logsPage = 0;
            SummariesType logs;
            do {
                logs = service.getLogSummaries(userId, folderId, logsPage, PAGE_SIZE);
                for (SummaryType summaryType : logs.getSummary()) {
                    assert summaryType instanceof LogSummaryType;
                    node.add(new FolderTreeNode(summaryType, null, !loadAll, FolderTreeNodeTypes.Log));
                }
            } while(PAGE_SIZE * (page + logsPage++) + processes.getSummary().size() + logs.getSummary().size() < processes.getCount() + logs.getCount());
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
