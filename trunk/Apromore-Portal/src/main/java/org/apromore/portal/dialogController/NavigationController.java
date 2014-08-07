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

package org.apromore.portal.dialogController;

import java.util.Collection;

import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.FolderTreeRenderer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Window;

public class                                         NavigationController extends BaseController {

    private MainController mainC;
    private Tree tree;

    public NavigationController(MainController newMainC) throws Exception {
        mainC = newMainC;

        Window treeW = (Window) mainC.getFellow("navigationcomp").getFellow("treeW");
        treeW.setContentStyle("background-image: none; background-color: white");

        tree = (Tree) treeW.getFellow("tree");
        tree.setStyle("background-image: none; background-color: white");

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
        FolderTreeModel model = new FolderTreeModel(new FolderTree(false).getRoot());
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

}
