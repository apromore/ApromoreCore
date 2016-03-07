/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package com.apql.Apql.tree;

import com.apql.Apql.controller.ViewController;
import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class FolderProcessRenderer extends DefaultTreeCellRenderer {
//    private final ImageIcon folderImg = new ImageIcon(getClass().getResource("/icons/folder24.png"));
//    private final ImageIcon modelImg = new ImageIcon(getClass().getResource("/icons/bpmn_22x22.png"));
//    private final ImageIcon homeImg = new ImageIcon(getClass().getResource("/icons/home_folder24.png"));
    private ViewController viewController=ViewController.getController();
    public FolderProcessRenderer(){}
    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(
                tree, getLabel(value), sel,
                expanded, leaf, row,
                hasFocus);

        DraggableNodeTree node=(DraggableNodeTree)value;
        if(node instanceof DraggableNodeProcess ){
            setIcon(viewController.getImageIcon(ViewController.ICONPROCESS));
        }else if(node instanceof DraggableNodeFolder){
            if(node.getName().equals("Home"))
                setIcon(viewController.getImageIcon(ViewController.ICONHOME));
            else
                setIcon(viewController.getImageIcon(ViewController.ICONFOLDER));
        }
        return this;
    }

    private String getLabel(Object value){
        DraggableNodeTree node=(DraggableNodeTree)value;
        if(node instanceof DraggableNodeProcess){
            DraggableNodeProcess process=(DraggableNodeProcess)node;
            return process.getName()+": "+process.getId();
        }else if(node instanceof DraggableNodeFolder) {
            DraggableNodeFolder folder = (DraggableNodeFolder) node;
            return folder.getName() + ": " + folder.getId();
        }
        return null;
    }

}
