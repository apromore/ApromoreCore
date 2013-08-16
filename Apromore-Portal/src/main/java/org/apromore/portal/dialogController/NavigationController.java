package org.apromore.portal.dialogController;

import org.apromore.portal.common.FolderTree;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.FolderTreeRenderer;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Window;

public class NavigationController extends BaseController {

    private MainController mainC;
    private Tree tree;

    public NavigationController(MainController newMainC) throws Exception {
        mainC = newMainC;
        Panel navigationP = (Panel) mainC.getFellow("navigationcomp").getFellow("navigationPanel");

        Window treeW = (Window) navigationP.getFellow("treeW");
        treeW.setContentStyle("background-image: none; background-color: white");

        this.tree = (Tree) navigationP.getFellow("treeW").getFellow("tree");
        this.tree.setStyle("background-image: none; background-color: white");
    }

    public void loadWorkspace() {
        FolderTreeModel model = new FolderTreeModel(new FolderTree(false).getRoot());
        tree.setItemRenderer(new FolderTreeRenderer(mainC));
        tree.setModel(model);
    }

}
