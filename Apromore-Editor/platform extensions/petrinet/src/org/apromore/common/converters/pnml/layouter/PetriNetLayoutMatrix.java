package org.apromore.common.converters.pnml.layouter;

import org.apromore.pnml.NodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * This is an implementation of a matrix growing dynamically.
 * For efficient use, there should be some further tweaking and better data structures
 * should be used.
 * @author Kai Schlichting
 */
public class PetriNetLayoutMatrix {

    private List<List<NodeType>> data;
    private int cols;
    private int rows;


    public PetriNetLayoutMatrix() {
        cols = 0;
        rows = 0;
        data = new ArrayList<>();
    }

    public void set(int row, int col, NodeType val) {
        ensureSize(row + 1, col + 1);
        data.get(row).set(col, val);
    }

    public NodeType get(int row, int col) {
        if (row >= rows || col >= cols)
            return null;

        return data.get(row).get(col);
    }

    public boolean contains(NodeType node) {
        for (List<NodeType> col : data) {
            if (col.contains(node)) return true;
        }

        return false;
    }

    protected void ensureSize(int sizeRows, int sizeCols) {
        ensureSizeRows(sizeRows);
        ensureSizeCols(sizeCols);
    }

    protected void ensureSizeRows(int sizeRows) {
        if (this.rows <= sizeRows) {
            this.rows = sizeRows;
            while (data.size() <= sizeRows) {
                data.add(new Vector<NodeType>());
            }
        }
    }

    protected void ensureSizeCols(int sizeCols) {
        if (this.cols <= sizeCols) {
            this.cols = sizeCols;
            for (List<NodeType> col : data) {
                while (col.size() <= sizeCols) {
                    col.add(null);
                }
            }
        }
    }


    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}
