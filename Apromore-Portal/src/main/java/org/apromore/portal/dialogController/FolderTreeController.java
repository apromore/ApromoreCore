package org.apromore.portal.dialogController;

import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.SecurityFolderTreeRenderer;
import org.apromore.portal.exception.DialogException;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

/**
 * Created by IntelliJ IDEA.
 * User: Igor
 * Date: 2/07/12
 * Time: 5:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderTreeController extends BaseController {

    public FolderTreeController(SecuritySetupController securitySetupController, Window win) throws DialogException {
        MainController mainController = securitySetupController.getMainController();
        Tree tree = (Tree) win.getFellow("mainTree").getFellow("folderTree");

        FolderTreeModel model = new FolderTreeModel(new FolderTree(true).getRoot());
        tree.setItemRenderer(new SecurityFolderTreeRenderer(mainController, securitySetupController.getPermissionsController()));
        tree.setModel(model);
    }
}
