package org.apromore.common.converters.pnml.layouter;

import org.jbpt.petri.Node;

import java.util.Vector;

/**
 * This is an implementation of a matrix growing dynamically.
 * For efficient use, there should be some further tweaking and better data structures
 * should be used.
 * @author Kai Schlichting
 */
public class PetriNetLayoutMatrix {

    private final Vector<Vector<Node>> data;
    public int sizeCols;
    public int sizeRows;

    public PetriNetLayoutMatrix() {
        sizeCols = 0;
        sizeRows = 0;
        data = new Vector<>(sizeRows);
    }

    public void set(int row, int col, Node val) {
        ensureSize(row + 1, col + 1);
        data.get(row).set(col, val);
    }

    public Node get(int row, int col) {
        if (row >= sizeRows || col >= sizeCols)
            return null;

        return data.get(row).get(col);
    }

    public boolean contains(Node node) {
        for (Vector<Node> col : data) {
            if (col.contains(node)) return true;
        }

        return false;
    }

    protected void ensureSize(int sizeRows, int sizeCols) {
        ensureSizeRows(sizeRows);
        ensureSizeCols(sizeCols);
    }

    protected void ensureSizeRows(int sizeRows) {
        if (this.sizeRows <= sizeRows) {
            this.sizeRows = sizeRows;
            while (data.size() <= sizeRows) {
                data.add(new Vector<Node>());
            }
        }
    }

    protected void ensureSizeCols(int sizeCols) {
        if (this.sizeCols <= sizeCols) {
            this.sizeCols = sizeCols;
            for (Vector<Node> col : data) {
                while (col.size() <= sizeCols) {
                    col.add(null);
                }
            }
        }
    }
}
