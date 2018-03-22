/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package com.apql.Apql.tree;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.table.TableProcess;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.HashSet;

/**
 * Created by corno on 17/06/2014.
 */
public class FPTreeSelectionListener implements TreeSelectionListener {
    private HashSet<String> locations;
    private HashSet<String> idLocations;
    private QueryController queryController;
    private ViewController viewController;

    public FPTreeSelectionListener(){
        queryController=QueryController.getQueryController();
        viewController= ViewController.getController();
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if(queryController.getVersion().equals(ViewController.CHOOSEVERSION)) {
            TreePath path = e.getPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            System.out.println((node instanceof DraggableNodeProcess) + " "+ (node instanceof DraggableNodeFolder));
            if (node instanceof DraggableNodeProcess) {
                try {
                    TableProcess table=viewController.getTableProcess();
                    table.setVisible(false);
                    table.setRows(133, 25, (DraggableNodeProcess) node);
                    table.revalidate();
                    table.repaint();
                    table.setVisible(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (node instanceof DraggableNodeFolder) {
//                System.out.println("dentro if Folder");
                try {

                    TableProcess table=viewController.getTableProcess();
                    table.setVisible(false);
                    table.cleanRows();
                    table.revalidate();
                    table.repaint();
                    table.setVisible(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
