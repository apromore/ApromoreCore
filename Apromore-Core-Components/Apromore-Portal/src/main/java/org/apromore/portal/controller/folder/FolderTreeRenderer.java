/*-
 * #%L
 * This file is part of "Apromore Enterprise Edition".
 * %%
 * Copyright (C) 2019 - 2022 Apromore Pty Ltd. All Rights Reserved.
 * %%
 * NOTICE:  All information contained herein is, and remains the
 * property of Apromore Pty Ltd and its suppliers, if any.
 * The intellectual and technical concepts contained herein are
 * proprietary to Apromore Pty Ltd and its suppliers and may
 * be covered by U.S. and Foreign Patents, patents in process,
 * and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission
 * is obtained from Apromore Pty Ltd.
 * #L%
 */

package org.apromore.portal.controller.folder;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.model.FolderType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

@Slf4j
public class FolderTreeRenderer implements TreeitemRenderer<FolderTreeNode> {

    private Integer selectedFolderId;

    public FolderTreeRenderer() {
    }

    public FolderTreeRenderer(Integer selectedFolderId) {
        this.selectedFolderId = selectedFolderId;
    }

    public void render(final Treeitem treeItem, FolderTreeNode folderTreeNode, int index) throws Exception {
        FolderType folder = (FolderType) folderTreeNode.getData();
        Treerow treerow = new Treerow();
        treerow.setParent(treeItem);
        treeItem.setValue(folderTreeNode);

        // Open all super-folders of the current folder
        treeItem.setOpen(
            folder.getId() == 0 ||
                folderContainsSubfolder(folder, selectedFolderId)
        );

        if ((folder.getFolders().isEmpty() && folder.getId() != 0) ||
            (folder.getId() == 0 && !folderTreeNode.isOpen())) {
            treerow.addSclass("ap-tree-leaf-node");
        }

        if (selectedFolderId != null && selectedFolderId.equals(folder.getId())) {
            treeItem.setOpen(true);
            treeItem.setSelected(true);
        }
        if (folder.getId() == 0) {
            treeItem.setOpen(true);
        }
        Hlayout hl = new Hlayout();
        hl.setValign("middle");

        if (folder.getId() == 0) {
            hl.appendChild(new Image("~./img/icon/svg/folder_home.svg"));
            hl.setSclass("ap-ico-home h-inline-block");
        } else {
            hl.appendChild(new Image("~./img/icon/svg/folder_icons.svg"));
            hl.setSclass("ap-ico-folder h-inline-block");
        }

        hl.appendChild(new Label(folder.getFolderName()));
        Treecell treecell = new Treecell();
        treecell.appendChild(hl);
        treerow.appendChild(treecell);

        treerow.addEventListener(Events.ON_CLICK, (Event event) -> {
            Treeitem treeitem = (Treeitem) event.getTarget().getParent();
            FolderTreeNode clickedNodeValue = treeitem.getValue();
            FolderType selectedFolder = (FolderType) clickedNodeValue.getData();
            treeitem.setOpen(true);
            log.info("Select folder" + selectedFolder.getId());
        });
    }

    /*
     * Check the folder tree and make sure we return true if we are looking at a folder that is opened
     * by a user. Could be multiples levels down the tree.
     */
    private boolean folderContainsSubfolder(FolderType folder, Integer selectedFolderId) {
        boolean found = false;
        if (selectedFolderId != null) {
            found = checkDownTheFolderTree(folder.getFolders(), selectedFolderId);
        }
        return found;
    }

    private boolean checkDownTheFolderTree(List<FolderType> subFolders, Integer selectedFolderId) {
        boolean result = false;
        for (FolderType folderType : subFolders) {
            if (folderType.getId().equals(selectedFolderId)) {
                result = true;
                break;
            }
        }
        if (!result) {
            for (FolderType folderType : subFolders) {
                result = checkDownTheFolderTree(folderType.getFolders(), selectedFolderId);
                if (result) {
                    break;
                }
            }
        }
        return result;
    }
}
