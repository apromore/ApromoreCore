/*
 * Copyright © 2009-2014 The Apromore Initiative.
 *
 * This file is part of “Apromore”.
 *
 * “Apromore” is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * “Apromore” is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.common;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.portal.dialogController.SecurityPermissionsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

import java.util.Collections;
import java.util.List;

/**
 * Handles the item render for the Folder Tree list.
 * @author Igor
 */
public class SecurityFolderTreeRenderer implements TreeitemRenderer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFolderTreeRenderer.class.getName());

    private SecurityPermissionsController permissionsController;

    public SecurityFolderTreeRenderer() {
    }

    public SecurityFolderTreeRenderer(SecurityPermissionsController permissionsController) {
        this.permissionsController = permissionsController;
    }

    public void setController(SecurityPermissionsController permissionsController) {
        this.permissionsController = permissionsController;
    }

    @Override
    public void render(final Treeitem treeItem, Object treeNode, int i) throws Exception {
        FolderTreeNode ctn = (FolderTreeNode) treeNode;

        Treerow dataRow = new Treerow();
        dataRow.setParent(treeItem);
        treeItem.setValue(ctn);
        treeItem.setOpen(true);

        Hlayout hl = new Hlayout();

        switch (ctn.getType()) {
        case Folder:
            FolderType folder = (FolderType) ctn.getData();
            FolderType currentFolder = UserSessionManager.getCurrentFolder();

            if (folder.getParentId() == null || folder.getParentId() == 0 || checkOpenFolderTree(folder, currentFolder)) {
                treeItem.setOpen(true);
                if (currentFolder != null && folder.getId().equals(currentFolder.getId())) {
                    treeItem.setSelected(true);
                }
            } else {
                treeItem.setOpen(false);
            }

            hl.appendChild(new Image(folder.getId() == 0 ? "/img/home-folder24.png" : "/img/folder24.png"));
            String folderName = folder.getFolderName();
            hl.appendChild(new Label(folderName.length() > 15 ? folderName.substring(0, 13) + "..." : folderName));
            break;

        case Process:
            ProcessSummaryType process = (ProcessSummaryType) ctn.getData();
            hl.appendChild(new Image("/img/process24.png"));
            String processName = process.getName();
            hl.appendChild(new Label(processName.length() > 15 ? processName.substring(0, 13) + "..." : processName));
            break;

        default:
            assert false: "Folder tree node with type " + ctn.getType() + " is not implemented";
        }

        hl.setSclass("h-inline-block");
        Treecell treeCell = new Treecell();
        treeCell.appendChild(hl);
        dataRow.appendChild(treeCell);

        dataRow.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                FolderTreeNode clickedNodeValue = ((Treeitem) event.getTarget().getParent()).getValue();

                try {
                    int selectedId = 0;
                    boolean hasOwnership = false;
                    switch (clickedNodeValue.getType()) {
                    case Folder:
                        FolderType selectedFolder = (FolderType) clickedNodeValue.getData();
                        hasOwnership = selectedFolder.isHasOwnership();
                        selectedId = selectedFolder.getId();
                        break;
  
                    case Process:
                        ProcessSummaryType selectedProcess = (ProcessSummaryType) clickedNodeValue.getData();
                        hasOwnership = selectedProcess.isHasOwnership();
                        selectedId = selectedProcess.getId();
                        break;

                    default:
                        assert false: "Clicked tree node with type " + clickedNodeValue.getType() + " is not implemented";
                    }

                    UserSessionManager.setCurrentSecurityOwnership(hasOwnership);
                    UserSessionManager.setCurrentSecurityItem(selectedId);
                    UserSessionManager.setCurrentSecurityType(clickedNodeValue.getType());
                    if (permissionsController != null) {
                        permissionsController.loadUsers(selectedId, clickedNodeValue.getType());
                    }
                } catch (Exception ex) {
                    LOGGER.error("SecurityFolderTree Renderer failed to render an item", ex);
                }
            }
        });
    }


    /**
     * Check the folder tree and make sure we return true if we are looking at a folder that is opened by a user.
     * Could be multiple levels down the tree.
     *
     * @param folder  a folder to search for
     * @param currentFolder  the root of a folder tree to search within
     * @return whether the <var>folder</var> is present within the folder tree rooted at <var>currentFolder</var>
     */
    private boolean checkOpenFolderTree(FolderType folder, FolderType currentFolder) {
        return checkDownTheFolderTree(Collections.singletonList(folder), currentFolder);
    }


    private boolean checkDownTheFolderTree(List<FolderType> subFolders, FolderType currentFolder) {
        for (FolderType folderType : subFolders) {
            if (folderType.getId().equals(currentFolder.getId())) { return true; }
        }
        for (FolderType folderType : subFolders) {
            if (checkDownTheFolderTree(folderType.getFolders(), currentFolder)) { return true; }
        }
        return false;
    }
}
