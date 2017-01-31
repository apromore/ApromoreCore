/*
 * Copyright © 2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

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