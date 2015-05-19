package com.apql.Apql.table.draghandler;

import com.apql.Apql.controller.QueryController;
import com.apql.Apql.controller.ViewController;
import com.apql.Apql.table.TableProcess;
import com.apql.Apql.table.TableRow;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.HashSet;

/**
 * Created by conforti on 9/01/15.
 */
/**
 * Created by conforti on 9/01/15.
 */
public class TableRowTransferHandler extends TransferHandler {
    private QueryController queryController;
    private ViewController viewController;

    public TableRowTransferHandler(QueryController queryController, ViewController viewController) {
        this.queryController = queryController;
        this.viewController = viewController;
    }

    public void drop(JComponent c) {
        System.out.println(c.getClass().toString());
        System.out.println("TABLE ROW");
        TableProcess dragContents = (TableProcess)c.getParent().getParent().getParent().getParent();
        HashSet<String> locationsVersion = new HashSet<>();
        for(TableRow row : dragContents.getRows()) {
            if (row.isSelected()) {
                locationsVersion.add(row.toString());
                row.clearSelection();
            }
        }
        queryController.setLocations(locationsVersion);
        queryController.addQueryLocation();
        viewController.getTableProcess().clearSelection();
    }


    protected Transferable createTransferable(JComponent c) {
        drop(c);
        return new StringSelection("");
    }

    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }
}