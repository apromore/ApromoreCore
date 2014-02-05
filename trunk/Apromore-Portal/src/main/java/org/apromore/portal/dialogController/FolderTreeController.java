package org.apromore.portal.dialogController;

import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.MiscFolderTreeRenderer;
import org.apromore.portal.common.SecurityFolderTreeRenderer;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

/**
 * Controller for the security setup screen to control the Folder tree.
 * @author Igor
 */
public class FolderTreeController extends BaseController {

    public FolderTreeController(Window win) throws DialogException {
        Tree tree = (Tree) win.getFellow("mainTree").getFellow("folderTree");

        FolderTreeModel model = new FolderTreeModel(new FolderTree(false).getRoot());
        tree.setItemRenderer(new MiscFolderTreeRenderer());
        tree.setModel(model);
    }
}
