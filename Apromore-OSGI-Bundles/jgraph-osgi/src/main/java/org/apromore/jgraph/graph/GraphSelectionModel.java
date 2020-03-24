/*
 * @(#)GraphSelectionModel.java	0.1 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.beans.PropertyChangeListener;

import org.apromore.jgraph.event.GraphSelectionListener;

/**
  * This interface represents the current state of the selection for
  * the graph component.
  * <p>
  *
  * A GraphSelectionModel can be configured to allow only one
  * cell (<code>SINGLE_GRAPH_SELECTION</code>) or a number of
  * cells (<code>MULTIPLE_GRAPH_SELECTION</code>).
  *
  * @version 0.1 1/1/02
  * @author Gaudenz Alder
  */

public interface GraphSelectionModel {

	/** Selection can only contain one cell at a time. */
	public static final int SINGLE_GRAPH_SELECTION = 1;

	/** Selection can contain any number of items. */
	public static final int MULTIPLE_GRAPH_SELECTION = 4;

	/**
	 * Sets the selection model, which must be either
	 * SINGLE_GRAPH_SELECTION or MULTIPLE_GRAPH_SELECTION.
	 * <p>
	 * This may change the selection if the current selection is not valid
	 * for the new mode.
	 */
	void setSelectionMode(int mode);

	/**
	  * Sets if the selection model allows the selection
	  * of children.
	  */
	void setChildrenSelectable(boolean flag);

	/**
	  * Returns true if the selection model allows the selection
	  * of children.
	  */
	boolean isChildrenSelectable();

	/**
	 * Returns the current selection mode, either
	 * <code>SINGLE_GRAPH_SELECTION</code> or
	 * <code>MULTIPLE_GRAPH_SELECTION</code>.
	 */
	int getSelectionMode();

	/**
	  * Sets the selection to cell. If this represents a change, then
	  * the GraphSelectionListeners are notified. If <code>cell</code> is
	  * null, this has the same effect as invoking <code>clearSelection</code>.
	  *
	  * @param cell new cell to select
	  */
	void setSelectionCell(Object cell);

	/**
	  * Sets the selection to cells. If this represents a change, then
	  * the GraphSelectionListeners are notified. If <code>cells</code> is
	  * null, this has the same effect as invoking <code>clearSelection</code>.
	  *
	  * @param cells new selection
	  */
	void setSelectionCells(Object[] cells);

	/**
	  * Adds cell to the current selection. If cell is not currently
	  * in the selection the GraphSelectionListeners are notified. This has
	  * no effect if <code>cell</code> is null.
	  *
	  * @param cell the new cell to add to the current selection
	  */
	void addSelectionCell(Object cell);

	/**
	  * Adds cells to the current selection.  If any of the cells are
	  * not currently in the selection the GraphSelectionListeners
	  * are notified. This has no effect if <code>cells</code> is null.
	  *
	  * @param cells the new cells to add to the current selection
	  */
	void addSelectionCells(Object[] cells);

	/**
	  * Removes cell from the selection. If cell is in the selection
	  * the GraphSelectionListeners are notified. This has no effect if
	  * <code>cell</code> is null.
	  *
	  * @param cell the cell to remove from the selection
	  */
	void removeSelectionCell(Object cell);

	/**
	  * Removes cells from the selection.  If any of the cells in
	  * <code>cells</code> are in the selection, the
	  * GraphSelectionListeners are notified. This method has no
	  * effect if <code>cells</code> is null.
	  *
	  * @param cells the cells to remove from the selection
	  */
	void removeSelectionCells(Object[] cells);

	/**
	  * Returns the cells that are currently selectable.
	  */
	Object[] getSelectables();

	/**
	  * Returns the first cell in the selection. How first is defined is
	  * up to implementors.
	  */
	Object getSelectionCell();

	/**
	  * Returns the cells in the selection. This will return null (or an
	  * empty array) if nothing is currently selected.
	  */
	Object[] getSelectionCells();

	/**
	 * Returns the number of cells that are selected.
	 */
	int getSelectionCount();

	/**
	  * Returns true if the cell, <code>cell</code>, is in the current
	  * selection.
	  */
	boolean isCellSelected(Object cell);

	/**
	  * Returns true if the cell, <code>cell</code>,
	  * has selected children.
	  */
	boolean isChildrenSelected(Object cell);

	/**
	  * Returns true if the selection is currently empty.
	  */
	boolean isSelectionEmpty();

	/**
	  * Empties the current selection.  If this represents a change in the
	  * current selection, the selection listeners are notified.
	  */
	void clearSelection();

	/**
	 * Adds a PropertyChangeListener to the listener list.
	 * The listener is registered for all properties.
	 * <p>
	 * A PropertyChangeEvent will get fired when the selection mode
	 * changes.
	 *
	 * @param listener  the PropertyChangeListener to be added
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Removes a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered
	 * for all properties.
	 *
	 * @param listener  the PropertyChangeListener to be removed
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	  * Adds x to the list of listeners that are notified each time the
	  * set of selected Objects changes.
	  *
	  * @param x the new listener to be added
	  */
	void addGraphSelectionListener(GraphSelectionListener x);

	/**
	  * Removes x from the list of listeners that are notified each time
	  * the set of selected Objects changes.
	  *
	  * @param x the listener to remove
	  */
	void removeGraphSelectionListener(GraphSelectionListener x);
}
