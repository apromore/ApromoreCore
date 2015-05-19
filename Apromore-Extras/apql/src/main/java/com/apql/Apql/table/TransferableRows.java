package com.apql.Apql.table;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

/**
 * Created by corno on 11/08/2014.
 */
public class TransferableRows implements Transferable {
    private TableProcess table;
    public static final DataFlavor flavors[] = { TableRowFlavor.TABLEROWFLAVOUR };

    public TransferableRows(TableProcess table){
        this.table=table;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[0];
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavors[0].equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return table;
    }
}
