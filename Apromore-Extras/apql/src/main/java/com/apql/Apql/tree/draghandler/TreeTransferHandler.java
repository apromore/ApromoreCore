package com.apql.Apql.tree.draghandler;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.tree.DraggableNodeProcess;
import com.apql.Apql.tree.DraggableNodeTree;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Created by conforti on 9/01/15.
 */
public class TreeTransferHandler extends TransferHandler {
    private QueryController queryController;
    private ViewController viewController;

    public TreeTransferHandler(QueryController queryController, ViewController viewController) {
        this.queryController = queryController;
        this.viewController = viewController;
    }

    public void drop(JComponent c) {
        if(!queryController.getVersion().equals(ViewController.CHOOSEVERSION)){
            findProcess();
        }
        viewController.getTableProcess().clearSelection();
    }

    private void findProcess(){
        QueryController queryController=QueryController.getQueryController();
        DraggableNodeTree node;
        HashSet<String> locations=new HashSet<String>();

        JTree tree = ViewController.getController().getFolderProcessTree();
        TreePath[] path = tree.getSelectionPaths();

        for(TreePath tp : path) {
            node =(DraggableNodeTree) tp.getLastPathComponent();
            Enumeration e = node.breadthFirstEnumeration();
            while (e.hasMoreElements()) {
                DraggableNodeTree n = (DraggableNodeTree) e.nextElement();

                if (n instanceof DraggableNodeProcess) {
                    DraggableNodeProcess dnp=(DraggableNodeProcess)n;
                    if(queryController.getVersion().equals(ViewController.LATESTVERSION)){
                        locations.add(dnp.getPathNode()+"{LATESTVERSION}");
                    }else if(queryController.getVersion().equals(ViewController.ALLVERSIONS) || queryController.getVersion().equals(ViewController.CHOOSEVERSION)){
                        locations.add(dnp.getPathNode()+"{ALLVERSION}");
                    }
                }
            }
        }
        queryController.setLocations(locations);

        queryController.addQueryLocation();

        tree.clearSelection();
    }

    protected Transferable createTransferable(JComponent c) {
        drop(c);
        return new StringSelection("");
    }

    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }
}