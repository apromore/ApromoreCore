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
