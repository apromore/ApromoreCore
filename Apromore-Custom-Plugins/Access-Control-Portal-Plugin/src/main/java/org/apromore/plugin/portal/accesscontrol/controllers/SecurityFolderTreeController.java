/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2014 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal.accesscontrol.controllers;

import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.dialogController.MainController;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

import org.apromore.plugin.portal.accesscontrol.renderer.SecurityFolderTreeRenderer;

/**
 * Controller for the security setup screen to control the Folder tree.
 * @author Igor
 */
public class SecurityFolderTreeController extends BaseController {

    public SecurityFolderTreeController(SecuritySetupController securitySetupController, Window win, int currentFolderId) {
        Tree tree = (Tree) win.getFellow("mainTree").getFellow("folderTree");

        MainController mainController = securitySetupController.getMainController();
//        FolderTreeModel model = new FolderTreeModel(new FolderTree(false).getRoot());
        FolderTree folderTree = new FolderTree(true, currentFolderId, mainController, true);
        FolderTreeModel model = new FolderTreeModel(folderTree.getRoot(), folderTree.getCurrentFolder());
        tree.setItemRenderer(new SecurityFolderTreeRenderer(securitySetupController));
        tree.setModel(model);
    }
}
