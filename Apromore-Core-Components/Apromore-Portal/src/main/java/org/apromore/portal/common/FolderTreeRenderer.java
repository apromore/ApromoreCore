/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.common;

import java.util.Collections;
import java.util.List;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.FolderType;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Html;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

/**
 * Used to help render the items in the tree list view on the left side of the app screen.
 *
 * @author Igor
 */
public class FolderTreeRenderer implements TreeitemRenderer {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(FolderTreeRenderer.class);
    private MainController mainC;


    public FolderTreeRenderer(MainController controller) {
        this.mainC = controller;
    }

    @Override
    public void render(final Treeitem treeItem, Object treeNode, int index) throws Exception {
        FolderTreeNode ctn = (FolderTreeNode) treeNode;
        FolderType folder = (FolderType) ctn.getData();
        Treerow dataRow = new Treerow();
        dataRow.setParent(treeItem);
        treeItem.setValue(ctn);

        // Select (only) the current folder
        if (mainC.getPortalSession().getCurrentFolder() != null &&
                folder.getId().equals(mainC.getPortalSession().getCurrentFolder().getId())) {
            treeItem.setSelected(true);
        }

        // Open all super-folders of the current folder
        treeItem.setOpen(folder.getId() == 0 || folderContainsSubfolder(folder, mainC.getPortalSession().getCurrentFolder()));

        Hlayout hl = new Hlayout();
        hl.setValign("middle");

        if (folder.getId() == 0) {
            hl.appendChild(new Image("/img/icon/svg/folder_home.svg"));
            hl.setSclass("ap-ico-home h-inline-block");
        } else {
            hl.appendChild(new Image("/img/icon/svg/folder_icons.svg"));
            hl.setSclass("ap-ico-folder h-inline-block");
        }

        hl.appendChild(new Label(folder.getFolderName()));
        Treecell treeCell = new Treecell();
        treeCell.appendChild(hl);
        dataRow.appendChild(treeCell);

        dataRow.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                FolderTreeNode clickedNodeValue = ((Treeitem) event.getTarget().getParent()).getValue();
                FolderType selectedFolder = (FolderType) clickedNodeValue.getData();

                try {
                    int selectedFolderId = selectedFolder.getId();
                    Component currentComponent = event.getTarget().getParent();
                    while (!currentComponent.getId().equalsIgnoreCase("mainW")) {
                        currentComponent = currentComponent.getParent();
                    }

                    Html html = (Html) currentComponent.getFellow("folders");

                    if (html != null) {
                        List<FolderType> availableFolders = mainC.getService().getSubFolders(UserSessionManager.getCurrentUser().getId(), selectedFolderId);

                        if (selectedFolder.getFolders().size() == 0) {
                            for (FolderType folderType : availableFolders) {
                                selectedFolder.getFolders().add(folderType);
                            }
                        }

                        mainC.getPortalSession().setPreviousFolder(mainC.getPortalSession().getCurrentFolder());
                        mainC.getPortalSession().setCurrentFolder(selectedFolder);

                        mainC.reloadSummaries2();
                        mainC.clearProcessVersions();
                    }
                } catch (Exception ex) {
                    LOGGER.error("FolderTree Renderer failed to render an item", ex);
                }
            }
        });
    }


    /* Check the folder tree and make sure we return true if we are looking at a folder that is opened by a user.
     * Could be multiples levels down the tree. */
    private boolean folderContainsSubfolder(FolderType folder, FolderType currentFolder) {
        boolean found = false;
        if (currentFolder != null) {
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
