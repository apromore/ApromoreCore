/*
 * Copyright © 2009-2015 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.common;

import java.util.Collections;
import java.util.List;

import org.apromore.model.FolderType;
import org.apromore.portal.dialogController.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FolderTreeRenderer.class.getName());
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

        if (folder.getParentId() == null || folder.getParentId() == 0 || checkOpenFolderTree(folder, UserSessionManager.getCurrentFolder())) {
            treeItem.setOpen(true);
            if (UserSessionManager.getCurrentFolder() != null && folder.getId().equals(UserSessionManager.getCurrentFolder().getId())) {
                treeItem.setSelected(true);
            }
        } else {
            treeItem.setOpen(false);
        }

        Hlayout hl = new Hlayout();
        if (folder.getId() == 0) {
            hl.appendChild(new Image("/img/home-folder24.png"));
        } else {
            hl.appendChild(new Image("/img/folder24.png"));
        }

        hl.appendChild(new Label(folder.getFolderName()));
        hl.setSclass("h-inline-block");
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

                    List<FolderType> breadcrumbFolders = mainC.getService().getBreadcrumbs(UserSessionManager.getCurrentUser().getId(), selectedFolderId);
                    Collections.reverse(breadcrumbFolders);
                    String content = "<table cellspacing='0' cellpadding='5' id='breadCrumbsTable'><tr>";

                    int i = 0;
                    for (FolderType breadcrumb : breadcrumbFolders) {
                        if (i > 0) {
                            content += "<td style='font-size: 9pt;'>&gt;</td>";
                        }
                        content += "<td><a class='breadCrumbLink' style='cursor: pointer; font-size: 9pt; color: Blue; text-decoration: underline;' id='" + breadcrumb.getId().toString() + "'>" + breadcrumb.getFolderName() + "</a></td>";
                        i++;
                    }

                    content += "</tr></table>";
                    mainC.breadCrumbs.setContent(content);
                    Clients.evalJavaScript("bindBreadcrumbs();");

                    Html html = (Html) currentComponent.getFellow("folders");

                    if (html != null) {
                        List<FolderType> availableFolders = mainC.getService().getSubFolders(UserSessionManager.getCurrentUser().getId(), selectedFolderId);

                        if (selectedFolder.getFolders().size() == 0) {
                            for (FolderType folderType : availableFolders) {
                                selectedFolder.getFolders().add(folderType);
                            }
                        }

                        UserSessionManager.setPreviousFolder(UserSessionManager.getCurrentFolder());
                        UserSessionManager.setCurrentFolder(selectedFolder);

                        mainC.reloadProcessSummaries();
                    }
                } catch (Exception ex) {
                    LOGGER.error("FolderTree Renderer failed to render an item", ex);
                }
            }
        });
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
