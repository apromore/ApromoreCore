/*
 * @(#)DefaultGraphSelectionModel.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.event.GraphSelectionEvent;
import org.apromore.jgraph.event.GraphSelectionListener;

/**
 * Default implementation of GraphSelectionModel. Listeners are notified
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */
public class DefaultGraphSelectionModel implements GraphSelectionModel,
		Cloneable, Serializable {

	/** Property name for selectionMode. */
	public static final String SELECTION_MODE_PROPERTY = "selectionMode";

	/** Value that represents selected state in cellStates. */
	public static final int SELECTED = -1;

	/** Object value that represents the unselected state in cellStates. */
	public static final Integer UNSELECTED = new Integer(0);

	/** Reference to the parent graph. Used to find parents and childs. */
	protected JGraph graph;

	/** Used to message registered listeners. */
	protected SwingPropertyChangeSupport changeSupport;

	/** Event listener list. */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Mode for the selection, will be either SINGLE_TREE_SELECTION,
	 * CONTIGUOUS_TREE_SELECTION or DISCONTIGUOUS_TREE_SELECTION.
	 */
	protected int selectionMode;

	/** Boolean that indicates if the model allows stepping-into groups. */
	protected boolean childrenSelectable = true;

	/** Maps the cells to their selection state. */
	protected Map cellStates = new Hashtable();

	/** List that contains the selected items. */
	protected Set selection = new LinkedHashSet();

	/** Constructs a DefaultGraphSelectionModel for the specified graph. */
	public DefaultGraphSelectionModel(JGraph graph) {
		this.graph = graph;
	}

	/**
	 * Sets the selection mode, which must be one of SINGLE_TREE_SELECTION,
	 */
	public void setSelectionMode(int mode) {
		int oldMode = selectionMode;

		selectionMode = mode;
		if (selectionMode != GraphSelectionModel.MULTIPLE_GRAPH_SELECTION
				&& selectionMode != GraphSelectionModel.SINGLE_GRAPH_SELECTION)
			selectionMode = GraphSelectionModel.MULTIPLE_GRAPH_SELECTION;
		if (oldMode != selectionMode && changeSupport != null)
			changeSupport.firePropertyChange(SELECTION_MODE_PROPERTY,
					new Integer(oldMode), new Integer(selectionMode));
	}

	/**
	 * Returns the selection mode, one of <code>SINGLE_TREE_SELECTION</code>,
	 * <code>DISCONTIGUOUS_TREE_SELECTION</code> or
	 * <code>CONTIGUOUS_TREE_SELECTION</code>.
	 */
	public int getSelectionMode() {
		return selectionMode;
	}

	/**
	 * Sets if the selection model allows the selection of children.
	 */
	public void setChildrenSelectable(boolean flag) {
		childrenSelectable = flag;
	}

	/**
	 * Returns true if the selection model allows the selection of children.
	 */
	public boolean isChildrenSelectable() {
		return childrenSelectable;
	}

	/**
	 * Hook for subclassers for fine-grained control over stepping-into cells.
	 * This implementation returns <code>childrenSelectable</code>&&
	 * isCellSelected.
	 */
	protected boolean isChildrenSelectable(Object cell) {
		AttributeMap attr = graph.getModel().getAttributes(cell);
		if (attr != null && childrenSelectable)
			return GraphConstants.isChildrenSelectable(attr);
		return childrenSelectable;
	}

	/**
	 * Selects the specified cell.
	 * 
	 * @param cell
	 *            the cell to select
	 */
	public void setSelectionCell(Object cell) {
		if (cell == null)
			setSelectionCells(null);
		else
			setSelectionCells(new Object[] { cell });
	}

	/**
	 * Sets the selection to <code>cells</code>. If this represents a change
	 * the GraphSelectionListeners are notified. Potentially paths will be held
	 * by this object; in other words don't change any of the objects in the
	 * array once passed in.
	 * 
	 * @param cells
	 *            new selection
	 */
	public void setSelectionCells(Object[] cells) {
		if (cells != null) {
			if (selectionMode == GraphSelectionModel.SINGLE_GRAPH_SELECTION
					&& cells.length > 0)
				cells = new Object[] { cells[cells.length - 1] };
			cellStates.clear();
			Vector change = new Vector();
			Set newSelection = new LinkedHashSet();
			for (int i = 0; i < cells.length; i++) {
				if (cells[i] != null) {
					selection.remove(cells[i]);
					change.addElement(new CellPlaceHolder(cells[i], !selection
							.remove(cells[i])));
					select(newSelection, cells[i]);
					Object parent = graph.getModel().getParent(cells[i]);
					if (parent != null)
						change.addElement(new CellPlaceHolder(parent, false));
				}
			}
			Iterator it = selection.iterator();
			while (it.hasNext()) {
				Object cell = it.next();
				while (cell != null) {
					change.addElement(new CellPlaceHolder(cell, false));
					cell = graph.getModel().getParent(cell);
				}
			}
			selection = newSelection;
			if (change.size() > 0) {
				notifyCellChange(change);
			}
		}
	}

	/**
	 * Adds the specified cell to the current selection
	 * 
	 * @param cell
	 *            the cell to add to the current selection
	 */
	public void addSelectionCell(Object cell) {
		if (cell != null)
			addSelectionCells(new Object[] { cell });
	}

	/**
	 * Adds cells to the current selection.
	 * 
	 * @param cells
	 *            the cells to be added to the current selection
	 */
	public void addSelectionCells(Object[] cells) {
		if (cells != null) {
			if (selectionMode == GraphSelectionModel.SINGLE_GRAPH_SELECTION)
				setSelectionCells(cells);
			else {
				Vector change = new Vector();
				for (int i = 0; i < cells.length; i++) {
					if (cells[i] != null) {
						boolean newness = select(selection, cells[i]);
						if (newness) {
							change.addElement(new CellPlaceHolder(cells[i],
									true));
							Object parent = graph.getModel()
									.getParent(cells[i]);
							if (parent != null)
								change.addElement(new CellPlaceHolder(parent,
										false));
						}
					}
				}
				if (change.size() > 0)
					notifyCellChange(change);
			}
		}
	}

	/**
	 * Removes the specified cell from the selection.
	 * 
	 * @param cell
	 *            the cell to remove from the current selection
	 */
	public void removeSelectionCell(Object cell) {
		if (cell != null)
			removeSelectionCells(new Object[] { cell });
	}

	/**
	 * Removes the specified cells from the selection.
	 * 
	 * @param cells
	 *            the cells to remove from the current selection
	 */
	public void removeSelectionCells(Object[] cells) {
		if (cells != null) {
			Vector change = new Vector();
			for (int i = 0; i < cells.length; i++) {
				if (cells[i] != null) {
					boolean removed = deselect(cells[i]);
					if (removed) {
						change.addElement(new CellPlaceHolder(cells[i], false));
						Object parent = graph.getModel().getParent(cells[i]);
						if (parent != null)
							change
									.addElement(new CellPlaceHolder(parent,
											false));
					}
				}
			}
			if (change.size() > 0)
				notifyCellChange(change);
		}
	}

	/**
	 * Returns the cells that are currently selectable. The array is ordered so
	 * that the top-most cell appears first. <br>
	 */
	public Object[] getSelectables() {
		if (isChildrenSelectable()) {
			List result = new ArrayList();
			// Roots Are Always Selectable
			Stack s = new Stack();
			GraphModel model = graph.getModel();
			for (int i = 0; i < model.getRootCount(); i++)
				s.add(model.getRootAt(i));
			while (!s.isEmpty()) {
				Object cell = s.pop();
				AttributeMap attrs = graph.getAttributes(cell);
				if (!model.isPort(cell)
						&& (attrs == null || GraphConstants.isSelectable(attrs)))
					result.add(cell);
				if (isChildrenSelectable(cell)) {
					for (int i = 0; i < model.getChildCount(cell); i++)
						s.add(model.getChild(cell, i));
				}
			}
			return result.toArray();
		}
		return graph.getRoots();
	}

	/**
	 * Returns the first cell in the selection. This is useful if there if only
	 * one item currently selected.
	 */
	public Object getSelectionCell() {
		if (selection != null && selection.size() > 0)
			return selection.toArray()[0];
		return null;
	}

	/**
	 * Returns the cells in the selection. This will return null (or an empty
	 * array) if nothing is currently selected.
	 */
	public Object[] getSelectionCells() {
		if (selection != null)
			return selection.toArray();
		return null;
	}

	/**
	 * Returns the number of paths that are selected.
	 */
	public int getSelectionCount() {
		return (selection == null) ? 0 : selection.size();
	}

	/**
	 * Returns true if the cell, <code>cell</code>, is in the current
	 * selection.
	 */
	public boolean isCellSelected(Object cell) {
		int count = getSelectedChildCount(cell);
		return (count == SELECTED);
	}

	/**
	 * Returns true if the cell, <code>cell</code>, has selected children.
	 */
	public boolean isChildrenSelected(Object cell) {
		int count = getSelectedChildCount(cell);
		return (count > 0);
	}

	/**
	 * Returns true if the selection is currently empty.
	 */
	public boolean isSelectionEmpty() {
		return (selection.isEmpty());
	}

	/**
	 * Empties the current selection. If this represents a change in the current
	 * selection, the selection listeners are notified.
	 */
	public void clearSelection() {
		if (selection != null) {
			Vector change = new Vector();
			Iterator it = cellStates.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object cell = entry.getKey();
				while (cell != null) {
					change.addElement(new CellPlaceHolder(cell, false));
					cell = graph.getModel().getParent(cell);
				}
			}
			selection.clear();
			cellStates.clear();
			if (change.size() > 0)
				notifyCellChange(change);
		}
	}

	//
	// Internal Datastructures
	//

	/**
	 * Returns the number of selected childs for <code>cell</code>.
	 */
	protected int getSelectedChildCount(Object cell) {
		if (cell != null) {
			Integer state = (Integer) cellStates.get(cell);
			if (state == null) {
				state = UNSELECTED;
				cellStates.put(cell, state);
			}
			return state.intValue();
		}
		return 0;
	}

	/**
	 * Sets the number of selected childs for <code>cell</code> to
	 * <code>count</code>.
	 */
	protected void setSelectedChildCount(Object cell, int count) {
		Integer i = new Integer(count);
		cellStates.put(cell, i);
	}

	/**
	 * Selects a single cell and updates all datastructures. No listeners are
	 * notified. Override this method to control individual cell selection.
	 */
	protected boolean select(Set set, Object cell) {
		AttributeMap attrs = graph.getAttributes(cell);
		if (!isCellSelected(cell)
				&& graph.getGraphLayoutCache().isVisible(cell)
				&& (attrs == null || GraphConstants.isSelectable(attrs))) {
			GraphModel model = graph.getModel();
			// Deselect and Update All Parents
			Object parent = model.getParent(cell);
			while (parent != null) {
				int count = getSelectedChildCount(parent);
				// Deselect Selected Parents
				if (count == SELECTED)
					count = 0;
				// Increase Child Count
				count++;
				setSelectedChildCount(parent, count);
				// Remove From Selection
				selection.remove(parent);
				// Next Parent
				parent = model.getParent(parent);
			}
			// Deselect All Children
			Object[] tmp = new Object[] { cell };
			List childs = DefaultGraphModel.getDescendants(model, tmp);
			// Remove Current Cell From Flat-View
			// TODO check performance of next line
			childs.remove(cell);
			Iterator it = childs.iterator();
			while (it.hasNext()) {
				Object child = it.next();
				if (child != null && !model.isPort(child)) {
					// Remove Child From Selection
					selection.remove(child);
					// Remove Child State
					cellStates.remove(child);
				}
			}
			// Set Selected State for Current
			setSelectedChildCount(cell, SELECTED);
			// Add Current To HashSet and Return
			return set.add(cell);
		}
		return false;
	}

	/**
	 * Deselects a single cell and updates all datastructures. No listeners are
	 * notified.
	 */
	protected boolean deselect(Object cell) {
		if (isCellSelected(cell)) {
			// Update All Parents
			Object parent = graph.getModel().getParent(cell);
			boolean firstParent = true;
			int change = -1;
			while (parent != null && change != 0) {
				int count = getSelectedChildCount(parent);
				count += change;
				// Select First Parent If No More Children
				if (count == 0 && firstParent) {
					change = 0;
					count = SELECTED;
					selection.add(parent);
				}
				// Update Selection Count
				setSelectedChildCount(parent, count);
				// Next Parent
				parent = graph.getModel().getParent(parent);
				firstParent = false;
			}
			// Remove State of Current Cell
			cellStates.remove(cell);
			// Remove Current from Selection and Return
			return selection.remove(cell);
		}
		return false;
	}

	//
	// Listeners
	//

	/**
	 * Adds x to the list of listeners that are notified each time the set of
	 * selected TreePaths changes.
	 * 
	 * @param x
	 *            the new listener to be added
	 */
	public void addGraphSelectionListener(GraphSelectionListener x) {
		listenerList.add(GraphSelectionListener.class, x);
	}

	/**
	 * Removes x from the list of listeners that are notified each time the set
	 * of selected TreePaths changes.
	 * 
	 * @param x
	 *            the listener to remove
	 */
	public void removeGraphSelectionListener(GraphSelectionListener x) {
		listenerList.remove(GraphSelectionListener.class, x);
	}

	/**
	 * Notifies all listeners that are registered for tree selection events on
	 * this object.
	 * 
	 * @see #addGraphSelectionListener
	 * @see EventListenerList
	 */
	protected void fireValueChanged(GraphSelectionEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// TreeSelectionEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == GraphSelectionListener.class) {
				// Lazily create the event:
				// if (e == null)
				// e = new ListSelectionEvent(this, firstIndex, lastIndex);
				((GraphSelectionListener) listeners[i + 1]).valueChanged(e);
			}
		}
	}

	/**
	 * Returns an array of all the listeners of the given type that were added
	 * to this model.
	 * 
	 * @return all of the objects receiving <em>listenerType</em>
	 *         notifications from this model
	 * 
	 * @since 1.3
	 */
	public EventListener[] getListeners(Class listenerType) {
		return listenerList.getListeners(listenerType);
	}

	/**
	 * Adds a PropertyChangeListener to the listener list. The listener is
	 * registered for all properties.
	 * <p>
	 * A PropertyChangeEvent will get fired when the selection mode changes.
	 * 
	 * @param listener
	 *            the PropertyChangeListener to be added
	 */
	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		if (changeSupport == null) {
			changeSupport = new SwingPropertyChangeSupport(this);
		}
		changeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Removes a PropertyChangeListener from the listener list. This removes a
	 * PropertyChangeListener that was registered for all properties.
	 * 
	 * @param listener
	 *            the PropertyChangeListener to be removed
	 */

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		if (changeSupport == null) {
			return;
		}
		changeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Notifies listeners of a change in path. <code>changePaths</code> should
	 * contain instances of PathPlaceHolder.
	 */
	protected void notifyCellChange(Vector changedCells) {
		int cCellCount = changedCells.size();
		boolean[] newness = new boolean[cCellCount];
		Object[] cells = new Object[cCellCount];
		CellPlaceHolder placeholder;

		for (int counter = 0; counter < cCellCount; counter++) {
			placeholder = (CellPlaceHolder) changedCells.elementAt(counter);
			newness[counter] = placeholder.isNew;
			cells[counter] = placeholder.cell;
		}

		GraphSelectionEvent event = new GraphSelectionEvent(this, cells,
				newness);

		fireValueChanged(event);
	}

	/**
	 * Returns a clone of this object with the same selection. This method does
	 * not duplicate selection listeners and property listeners.
	 * 
	 * @exception CloneNotSupportedException
	 *                never thrown by instances of this class
	 */
	public Object clone() throws CloneNotSupportedException {
		DefaultGraphSelectionModel clone = (DefaultGraphSelectionModel) super
				.clone();
		clone.changeSupport = null;
		if (selection != null)
			clone.selection = new LinkedHashSet(selection);
		clone.listenerList = new EventListenerList();
		return clone;
	}

	/**
	 * Holds a path and whether or not it is new.
	 */
	protected class CellPlaceHolder {
		protected boolean isNew;

		protected Object cell;

		protected CellPlaceHolder(Object cell, boolean isNew) {
			this.cell = cell;
			this.isNew = isNew;
		}

		/**
		 * Returns the cell.
		 * 
		 * @return Object
		 */
		public Object getCell() {
			return cell;
		}

		/**
		 * Returns the isNew.
		 * 
		 * @return boolean
		 */
		public boolean isNew() {
			return isNew;
		}

		/**
		 * Sets the cell.
		 * 
		 * @param cell
		 *            The cell to set
		 */
		public void setCell(Object cell) {
			this.cell = cell;
		}

		/**
		 * Sets the isNew.
		 * 
		 * @param isNew
		 *            The isNew to set
		 */
		public void setNew(boolean isNew) {
			this.isNew = isNew;
		}

	}

}
