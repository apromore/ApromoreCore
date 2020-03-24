/*
 * @(#)GraphTransferable.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.Map;

import org.apromore.jgraph.plaf.basic.BasicGraphTransferable;

/**
 * An object that represents the clipboard contents for a graph selection.
 * The object has three representations:
 * <p>
 * 1. Richer: The cells, view attributes and connections for this selection are
 * stored as separate datastructures, which can be inserted using
 * the GraphModel.insert() method.
 * 2. HTML: If one cell is selected, the userObject is returned as HTML.
 * 3. Plain: The userObject of the selected cell is returned as plain text.
 *
 * @author Gaudenz Alder
 * @version 1.0 1/1/02
 *
 */

public class GraphTransferable
	extends BasicGraphTransferable
	implements Serializable, ClipboardOwner {

	/** Local Machine Reference Data Flavor. */
	public static DataFlavor dataFlavor;

	/** Selected cells. */
	protected Object[] cells;

	/** Object that describes the connection between cells. */
	protected ConnectionSet cs;

        /** Object that describes the group structure between cells. */
        protected ParentMap pm;

	/** (Cell, Map) entries that hold the view attributes for the cells. */
	protected Map attributeMap;

	/** Rectangle that defines the former bounds of the views. */
	protected Rectangle2D bounds;

	/**
	 * Constructs a new transferable selection for <code>cells</code>,
	 * <code>cs</code>and <code>attrMap</code>.
	 */
	public GraphTransferable(
		Object[] cells,
		Map attrMap,
		Rectangle2D bounds,
		ConnectionSet cs,
		ParentMap pm) {

		attributeMap = attrMap;
		this.bounds = bounds;
		this.cells = cells;
		this.cs = cs;
		this.pm = pm;
	}

	/**
	 * Returns the <code>cells</code> that represent the selection.
	 */
	public Object[] getCells() {
		return cells;
	}

	/**
	 * Returns the connections between <code>cells</code> (and possibly
	 * other, unselected cells).
	 */
	public ConnectionSet getConnectionSet() {
		return cs;
	}

        public ParentMap getParentMap() {
	    return pm;
	}

	/**
	 * Returns a map of (GraphCell, Map)-pairs that represent the
	 * view attributes for the respecive cells.
	 */
	public Map getAttributeMap() {
		return attributeMap;
	}

	public Rectangle2D getBounds() {
		return bounds;
	}

	// from ClipboardOwner
	public void lostOwnership(Clipboard clip, Transferable contents) {
		// do nothing
	}

	// --- Richer ----------------------------------------------------------

	/**
	 * Returns the jvm-localreference flavors of the transferable.
	 */
	public DataFlavor[] getRicherFlavors() {
		return new DataFlavor[] { dataFlavor };
	}

	/**
	 * Fetch the data in a jvm-localreference format.
	 */
	public Object getRicherData(DataFlavor flavor)
		throws UnsupportedFlavorException {
		if (flavor.equals(dataFlavor))
			return this;
		else
			throw new UnsupportedFlavorException(flavor);
	}

	// --- Plain ----------------------------------------------------------

	/**
	 * Returns true if the transferable support a text/plain format.
	 */
	public boolean isPlainSupported() {
		return (cells != null && cells.length == 1);
	}

	/**
	 * Fetch the data in a text/plain format.
	 */
	public String getPlainData() {
		if (cells[0] instanceof DefaultGraphCell) {
			Object obj = ((DefaultGraphCell) cells[0]).getUserObject();
			if (obj != null)
				return obj.toString();
		}
		return cells[0].toString();
	}

	// --- HTML ---------------------------------------------------------

	/**
	 * Returns true if the transferable support a text/html format.
	 */
	public boolean isHTMLSupported() {
		return isPlainSupported();
	}

	/**
	 * Fetch the data in a text/html format.
	 */
	public String getHTMLData() {
		StringBuffer buf = new StringBuffer();
		buf.append("<html><body><p>");
		buf.append(getPlainData());
		buf.append("</p></body></html>");
		return buf.toString();
	}

	/* Local Machine Reference Data Flavor. */
	static {
		DataFlavor localDataFlavor;
		try {
			localDataFlavor =
				new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType
						+ "; class=org.jgraph.graph.GraphTransferable");
		} catch (ClassNotFoundException cnfe) {
			localDataFlavor = null;
		}
		dataFlavor = localDataFlavor;
	}

}
