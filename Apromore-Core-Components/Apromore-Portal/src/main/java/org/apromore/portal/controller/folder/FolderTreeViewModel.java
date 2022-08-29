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

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.dialogController.MainController;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.SelectorParam;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

@Slf4j
@Getter
@Setter
public class FolderTreeViewModel {

    MainController mainController;
    Tree folderTree;
    FolderTreeNode selectedItem;
    Integer selectedFolderId;

    private Window window;

    @Init
    public void init(
        @ExecutionArgParam("mainController") final MainController mainController,
        @ExecutionArgParam("selectedFolderId") Integer selectedFolderId
    ) {
        this.mainController = mainController;
        this.selectedFolderId = selectedFolderId;
    }

    @AfterCompose
    public void doAfterCompose(
        @ContextParam(ContextType.VIEW) Component view,
        @SelectorParam("#folderTree") final Tree tree
    ) {
        window = (Window) view;
        folderTree = tree;
        if (selectedFolderId != null && selectedFolderId != 0) {
            loadTreeSpace(selectedFolderId);
        } else {
            loadWorkspace();
        }
    }

    /**
     * Loads the workspace.
     */
    public void loadWorkspace() {
        FolderTreeModel model = new FolderTreeModel(new FolderTree(false, mainController).getRoot(), null);
        folderTree.setItemRenderer(new FolderTreeRenderer());
        folderTree.setModel(model);
    }

    /*
        Load Tree with selected open items
     */
    public void loadTreeSpace(Integer folderId) {
        FolderTreeModel model = new FolderTreeModel(new FolderTree(false, mainController).getRoot(), null);
        folderTree.setItemRenderer(new FolderTreeRenderer(folderId));
        folderTree.setModel(model);
    }

    /* Expand or Collapse the tree. */
    @Command
    public void doCollapseExpandAll(
        @BindingParam("component") final Component component,
        @BindingParam("open") boolean open
    ) {
        if (component instanceof Treeitem) {
            Treeitem treeitem = (Treeitem) component;
            treeitem.setOpen(open);
        }
        Collection<?> children = component.getChildren();
        if (children != null) {
            for (Object child : children) {
                doCollapseExpandAll((Component) child, open);

            }
        }
    }

    @Command
    public void cancelCmd(){
        window.detach();
    }

    @Command
    public void okCmd() {
        window.detach();
    }

}
