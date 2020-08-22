/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

package org.apromore.portal.dialogController;

import java.util.Collection;

import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.common.FolderTreeRenderer;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.FolderType;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

public class NavigationController extends BaseController {

    private MainController mainC;
    private Tree tree;

    public NavigationController(MainController newMainC) throws Exception {
        mainC = newMainC;

        Window treeW = (Window) mainC.getFellow("navigationcomp").getFellow("treeW");
//        treeW.setContentStyle("background-image: none; background-color: white");

        tree = (Tree) treeW.getFellow("tree");
//        tree.setStyle("background-image: none; background-color: white");

        Button expandBtn = (Button) treeW.getFellow("expand");
        Button contractBtn = (Button) treeW.getFellow("contract");
        expandBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                doCollapseExpandAll(tree, true);
            }
        });
        contractBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                doCollapseExpandAll(tree, false);
            }
        });
    }


    /**
     * Loads the workspace.
     */
    public void loadWorkspace() {
        FolderTreeModel model = new FolderTreeModel(new FolderTree(false, mainC).getRoot(), null);
        tree.setItemRenderer(new FolderTreeRenderer(mainC));
        tree.setModel(model);
    }

    /* Expand or Collapse the tree. */
    private void doCollapseExpandAll(Component component, boolean open) {
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
 
    public void currentFolderChanged() {
        updateFolders(tree, mainC.getPortalSession().getCurrentFolder());
    }

    /**
     * Update all folders at or below the passed component, setting them to be correctly opened or selected
     */
    private static boolean updateFolders(Component component, FolderType currentFolder) {

        boolean containsCurrentFolder = false;
        for (Component child: component.getChildren()) {
            boolean childContainsCurrentFolder = updateFolders(child, currentFolder);
            containsCurrentFolder = containsCurrentFolder || childContainsCurrentFolder;
        }

        if (component instanceof Treeitem) {
            Treeitem treeitem = (Treeitem) component;

            Object value = treeitem.getValue();
            if (value instanceof FolderTreeNode) {
                FolderType folder = (FolderType) ((FolderTreeNode) value).getData();
                boolean match = currentFolder.equals(folder);
                treeitem.setSelected(match);
                containsCurrentFolder = containsCurrentFolder || match || folder.getId() == 0;
            }
            treeitem.setOpen(containsCurrentFolder);
        }

        return containsCurrentFolder;
    }
}
