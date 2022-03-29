/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

package org.apromore.portal.common;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.FolderType;
import org.slf4j.Logger;
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

import java.util.List;

/**
 * Handles the item render for the Folder Tree list.
 *
 * @author Igor
 */
public class MiscFolderTreeRenderer implements TreeitemRenderer {

    MainController mainController;

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(MiscFolderTreeRenderer.class);

    public MiscFolderTreeRenderer(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void render(final Treeitem treeItem, Object treeNode, int i) throws Exception {
        FolderTreeNode ctn = (FolderTreeNode) treeNode;

        if (ctn.getType() == FolderTreeNodeTypes.Folder) {
            Hlayout hl = new Hlayout();
            Treerow dataRow = new Treerow();
            dataRow.setParent(treeItem);
            treeItem.setValue(ctn);
            treeItem.setOpen(true);

            FolderType folder = (FolderType) ctn.getData();
            String name = folder.getFolderName();
            FolderType currentFolder = mainController.getPortalSession().getCurrentFolder();

            if (folder.getParentId() == null || folder.getParentId() == 0 || checkOpenFolderTree(folder, currentFolder)) {
                treeItem.setOpen(true);
                if (currentFolder != null && folder.getId().equals(currentFolder.getId())) {
                    treeItem.setSelected(true);
                    UserSessionManager.setCurrentSecurityItem(folder.getId());
                }
            } else {
                treeItem.setOpen(false);
            }

            if (folder.getId() == 0) {
                hl.appendChild(new Image("/img/icon/svg/folder_home.svg"));
                hl.setSclass("ap-ico-home h-inline-block");
            } else {
                hl.appendChild(new Image("/img/icon/svg/folder_icons.svg"));
                hl.setSclass("ap-ico-folder h-inline-block");
            }

            hl.appendChild(new Label(name.length() > 15 ? name.substring(0, 13) + "..." : name));
            Treecell treeCell = new Treecell();
            treeCell.appendChild(hl);
            dataRow.appendChild(treeCell);

            dataRow.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    FolderTreeNode clickedNodeValue = ((Treeitem) event.getTarget().getParent()).getValue();

                    try {
                        FolderType selectedFolder = (FolderType) clickedNodeValue.getData();
                        //boolean hasOwnership = selectedFolder.isHasOwnership();

                        UserSessionManager.setCurrentSecurityItem(selectedFolder.getId());
                    } catch (Exception ex) {
                        LOGGER.error("SecurityFolderTree Renderer failed to render an item", ex);
                    }
                }
            });
        }
    }


    /* Check the folder tree and make sure we return true if we are looking at a folder that is opened by a user.
 * Could be multiples levels down the tree. */
    private boolean checkOpenFolderTree(FolderType folder, FolderType currentFolder) {
        boolean found = false;
        if (currentFolder != null) {
            if (currentFolder.getId().equals(folder.getId())) {
                found = true;
            }
            if (!found) {
                found = checkDownTheFolderTree(folder.getFolders(), currentFolder);
            }
        }
        return found;
    }


    private boolean checkDownTheFolderTree(List<FolderType> subFolders, FolderType currentFolder) {
        boolean result = false;
        for (FolderType folderType : subFolders) {
            if (folderType.getId().equals(currentFolder.getId())) {
                result = true;
                break;
            }
        }
        if (!result) {
            for (FolderType folderType : subFolders) {
                result = checkDownTheFolderTree(folderType.getFolders(), currentFolder);
                if (result) {
                    break;
                }
            }
        }
        return result;
    }
}
