/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
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

package org.apromore.portal.dialogController;

import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.MiscFolderTreeRenderer;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

/**
 * Controller for the security setup screen to control the Folder tree.
 * @author Igor
 */
public class FolderTreeController extends BaseController {

    public FolderTreeController(Window win, MainController mainController) throws DialogException {
        Tree tree = (Tree) win.getFellow("mainTree").getFellow("folderTree");

        FolderTreeModel model = new FolderTreeModel(new FolderTree(false, mainController).getRoot(), null);
        tree.setItemRenderer(new MiscFolderTreeRenderer(mainController));
        tree.setModel(model);
    }
}
