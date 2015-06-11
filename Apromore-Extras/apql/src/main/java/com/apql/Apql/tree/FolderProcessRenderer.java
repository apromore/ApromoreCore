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
