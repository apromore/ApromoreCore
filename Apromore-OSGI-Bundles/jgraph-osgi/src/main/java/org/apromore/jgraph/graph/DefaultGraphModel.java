/*
 * $Id: DefaultGraphModel.java,v 1.25 2009/06/12 13:58:33 david Exp $
 * 
 * Copyright (c) 2001-2009 Gaudenz Alder
 */
package org.apromore.jgraph.graph;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import org.apromore.jgraph.event.GraphModelEvent;
import org.apromore.jgraph.event.GraphModelListener;

/**
 * The default implementation of a graph model.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public class DefaultGraphModel extends UndoableEditSupport implements Serializable, GraphModel {

	/**
	 * The list of listeners that listen to the model.
	 */
	protected transient EventListenerList listenerList = new EventListenerList();

	/**
	 * Default instance of an empty iterator.
	 */
	protected transient Iterator emptyIterator = new EmptyIterator();

	/**
	 * Set that contains all root cells of this model.
	 */
	protected List roots = null;

	/**
	 * Indicates whether isLeaf is based on a node's allowsChildren value.
	 */
	protected boolean asksAllowsChildren = false;

	/**
	 * Whether or not to remove group cells from the model when all of their
	 * children are removed
	 */
	protected boolean removeEmptyGroups = true;

	/**
	 * The model's own attributes as a map. Defaults to an empty Hashtable.
	 */
	protected AttributeMap attributes = null;

	/**
	 * Counter for the depth of nested transactions. Each call to beginUpdate
	 * increments this counter and each call to endUpdate decrements it. When
	 * the counter reaches 0, the transaction is closed and applied to the
	 * model.
	 */
	protected transient int updateLevel = 0;

	/**
	 * Stores nested transaction added cells
	 */
	protected transient Set transAddedCells = null;

	/**
	 * Stores nested transaction removed cells
	 */
	protected transient Set transRemovedCells = null;

	/**
	 * Stores nested transaction transport attribute maps
	 */
	protected transient Map transEditAttrs = null;

	/**
	 * Stores nested transaction connection sets
	 */
	protected transient ConnectionSet transEditCS = null;

	/**
	 * Stores nested transaction parent maps
	 */
	protected transient ParentMap transEditPM = null;

	/**
	 * Constructs a model that is not an attribute store.
	 */
	public DefaultGraphModel() {
		this(null, null);
	}

	/**
	 * Constructs a model that is not an attribute store.
	 */
	public DefaultGraphModel(List roots, AttributeMap attributes) {
		if (roots != null)
			this.roots = roots;
		else
			this.roots = new ArrayList();
		if (attributes != null)
			this.attributes = attributes;
		else
			this.attributes = new AttributeMap();
	}

	/**
	 * Constructs a model using the specified information to construct the
	 * cells, attributes and connection data.
	 */
	public DefaultGraphModel(List roots, AttributeMap attributes, ConnectionSet cs) {
		this(roots, attributes);
		handleConnectionSet(cs);
	}

	public List getRoots() {
		return roots;
	}

	//
	// Graph Model
	//

	/**
	 * Returns the number of roots in the model. Returns 0 if the model is
	 * empty.
	 * 
	 * @return the number of roots in the model
	 */
	public int getRootCount() {
		return roots.size();
	}

	/**
	 * Returns the root at index <I>index </I> in the model. This should not
	 * return null if <i>index </i> is a valid index for the model (that is
	 * <i>index </i>>= 0 && <i>index </i>< getRootCount()).
	 * 
	 * @return the root of at index <I>index </I>
	 */
	public Object getRootAt(int index) {
		return roots.get(index);
	}

	/**
	 * Returns the index of <code>root</code> in the model. If root is
	 * <code>null</code>, returns -1.
	 * 
	 * @param root
	 *            a root in the model, obtained from this data source
	 * @return the index of the root in the model, or -1 if the parent is
	 *         <code>null</code>
	 */
	public int getIndexOfRoot(Object root) {
		return roots.indexOf(root);
	}

	/**
	 * Returns <code>true</code> if <code>node</code> or one of its ancestors is
	 * in the model.
	 * 
	 * @return <code>true</code> if <code>node</code> is in the model
	 */
	public boolean contains(Object node) {
		Object parentNode = null;
		while ((parentNode = getParent(node)) != null)
			node = parentNode;
		return roots.contains(node);
	}

	/**
	 * Returns a <code>Map</code> that represents the attributes for the
	 * specified cell. This attributes have precedence over each view's
	 * attributes, regardless of isAttributeStore.
	 * 
	 * @return attributes of <code>node</code> as a <code>Map</code>
	 */
	public AttributeMap getAttributes(Object node) {
		if (node instanceof GraphCell)
			return ((GraphCell) node).getAttributes();
		else if (node == null)
			return attributes;
		return null;
	}

	/**
	 * @return Returns the user object of the given cell. This implementation
	 *         checks if the cell is a default mutable tree node and returns
	 *         it's user object.
	 */
	public Object getValue(Object cell) {
		if (cell instanceof DefaultMutableTreeNode)
			return ((DefaultMutableTreeNode) cell).getUserObject();
		return null;
	}

	/**
	 * Returns the graph model's attribute. Shortcut to <code>
	 * getAttributes(null)</code>.
	 * 
	 * @return attributes of <code>node</code> as a <code>Map</code>
	 */
	public Map getAttributes() {
		return getAttributes(null);
	}

	//
	// Graph Structure
	//

	/**
	 * Returns the source of <code>edge</code>. <I>edge </I> must be an object
	 * previously obtained from this data source.
	 * 
	 * @return <code>Object</code> that represents the source of <i>edge </i>
	 */
	public Object getSource(Object edge) {
		if (edge instanceof Edge)
			return ((Edge) edge).getSource();
		return null;
	}

	/**
	 * Returns the target of <code>edge</code>. <I>edge </I> must be an object
	 * previously obtained from this data source.
	 * 
	 * @return <code>Object</code> that represents the target of <i>edge </i>
	 */
	public Object getTarget(Object edge) {
		if (edge instanceof Edge)
			return ((Edge) edge).getTarget();
		return null;
	}

	/**
	 * Returns <code>true</code> if <code>port</code> is a valid source for
	 * <code>edge</code>. <I>edge </I> and <I>port </I> must be objects
	 * previously obtained from this data source.
	 * 
	 * @return <code>true</code> if <code>port</code> is a valid source for
	 *         <code>edge</code>.
	 */
	public boolean acceptsSource(Object edge, Object port) {
		return true;
	}

	/**
	 * Returns <code>true</code> if <code>port</code> is a valid target for
	 * <code>edge</code>. <I>edge </I> and <I>port </I> must be objects
	 * previously obtained from this data source.
	 * 
	 * @return <code>true</code> if <code>port</code> is a valid target for
	 *         <code>edge</code>.
	 */
	public boolean acceptsTarget(Object edge, Object port) {
		return true;
	}

	/**
	 * Returns an iterator of the edges connected to <code>port</code>. <I>port
	 * </I> must be a object previously obtained from this data source. This
	 * method never returns null.
	 * 
	 * @param port
	 *            a port in the graph, obtained from this data source
	 * @return <code>Iterator</code> that represents the connected edges
	 */
	public Iterator edges(Object port) {
		if (port instanceof Port)
			return ((Port) port).edges();
		return emptyIterator;
	}

	/**
	 * Returns <code>true</code> if <code>edge</code> is a valid edge.
	 * 
	 * @return <code>true</code> if <code>edge</code> is a valid edge.
	 */
	public boolean isEdge(Object edge) {
		return edge instanceof Edge;
	}

	/**
	 * Returns <code>true</code> if <code>port</code> is a valid port, possibly
	 * supporting edge connection.
	 * 
	 * @return <code>true</code> if <code>port</code> is a valid port.
	 */
	public boolean isPort(Object port) {
		return port instanceof Port;
	}

	/**
	 * A shortcut method to create a connection set that represents the
	 * connections in this model. Useful for encoding to avoid writing redundant
	 * connection data stored in the cells.
	 */
	public ConnectionSet getConnectionSet() {
		return ConnectionSet.create(this, DefaultGraphModel.getAll(this), false);
	}

	//
	// Group Structure
	//

	/**
	 * Returns a map of (cell, clone)-pairs for all <code>cells</code>. In the
	 * new array, all references are replaced with references to the cloned
	 * cells (ie parent or anchor). This method does only include children which
	 * are in <code>cells</code>. Use JGraph.getDescendants to get a complete
	 * list of all children.
	 */
	public Map cloneCells(Object[] cells) {
		Map map = new Hashtable();
		// Add Cells to Queue
		for (int i = 0; i < cells.length; i++)
			map.put(cells[i], cloneCell(cells[i]));
		// Replace Parent and Anchors
		Iterator it = map.entrySet().iterator();
		Object obj, cell, parent;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			obj = entry.getValue();
			cell = entry.getKey();

			// Replaces the cloned cell's parent with the parent's clone
			parent = getParent(cell);
			if (parent != null)
				parent = map.get(parent);
			if (parent != null)
				((DefaultMutableTreeNode) parent).add((DefaultMutableTreeNode) obj);

			// Replaces the anchors for ports
			if (obj instanceof Port) {
				Object anchor = ((Port) obj).getAnchor();
				if (anchor != null)
					((Port) obj).setAnchor((Port) map.get(anchor));
			}
		}
		return map;
	}

	/**
	 * Sets the parent of the specified cell.
	 */
	protected void setParent(Object child, Object parent) {
		if (child instanceof DefaultMutableTreeNode && parent instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parent;
			parentNode.add((DefaultMutableTreeNode) child);
		}
	}

	/**
	 * Creates a shallow copy of the cell including a copy of the user object.
	 * Subclassers can override the cloneUserObject to provide a custom user
	 * object cloning mechanism.
	 */
	protected Object cloneCell(Object cellObj) {
		if (cellObj instanceof DefaultGraphCell) {
			// Clones the cell
			DefaultGraphCell cell = (DefaultGraphCell) cellObj;
			DefaultGraphCell clone = (DefaultGraphCell) cell.clone();
			// Clones the user object
			clone.setUserObject(cloneUserObject(cell.getUserObject()));
			return clone;
		}
		return cellObj;
	}

	/**
	 * Clones the user object. Helper method that is invoked from cloneCells.
	 * You must use cloneCells (or cloneCell for single cells) to get a deep
	 * copy of a clone. Subclassers must override this and valueForCellChanged
	 * to implement custom user objects. This implementation returns
	 * <code>object</code>.
	 */
	protected Object cloneUserObject(Object userObject) {
		return userObject;
	}

	/**
	 * Returns the parent of <I>child </I> in the model. <I>child </I> must be a
	 * node previously obtained from this data source. This returns null if
	 * <i>child </i> is a root in the model.
	 * 
	 * @param child
	 *            a node in the graph, obtained from this data source
	 * @return the parent of <I>child </I>
	 */
	public Object getParent(Object child) {
		if (child != null && child instanceof TreeNode)
			return ((TreeNode) child).getParent();
		return null;
	}

	/**
	 * Returns the index of child in parent. If either the parent or child is
	 * <code>null</code>, returns -1.
	 * 
	 * @param parent
	 *            a note in the tree, obtained from this data source
	 * @param child
	 *            the node we are interested in
	 * @return the index of the child in the parent, or -1 if either the parent
	 *         or the child is <code>null</code>
	 */
	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;
		return ((TreeNode) parent).getIndex((TreeNode) child);
	}

	/**
	 * Returns the child of <I>parent </I> at index <I>index </I> in the
	 * parent's child array. <I>parent </I> must be a node previously obtained
	 * from this data source. This should not return null if <i>index </i> is a
	 * valid index for <i>parent </i> (that is <i>index </i>>= 0 && <i>index
	 * </i>< getChildCount( <i>parent </i>)).
	 * 
	 * @param parent
	 *            a node in the tree, obtained from this data source
	 * @return the child of <I>parent </I> at index <I>index </I>
	 */
	public Object getChild(Object parent, int index) {
		if (parent instanceof TreeNode)
			return ((TreeNode) parent).getChildAt(index);
		return null;
	}

	/**
	 * Returns the number of children of <I>parent </I>. Returns 0 if the node
	 * is a leaf or if it has no children. <I>parent </I> must be a node
	 * previously obtained from this data source.
	 * 
	 * @param parent
	 *            a node in the tree, obtained from this data source
	 * @return the number of children of the node <I>parent </I>
	 */
	public int getChildCount(Object parent) {
		if (parent instanceof TreeNode)
			return ((TreeNode) parent).getChildCount();
		return 0;
	}

	/**
	 * Returns whether the specified node is a leaf node. The way the test is
	 * performed depends on the.
	 * 
	 * @param node
	 *            the node to check
	 * @return true if the node is a leaf node
	 */
	public boolean isLeaf(Object node) {
		if (asksAllowsChildren && node instanceof TreeNode)
			return !((TreeNode) node).getAllowsChildren();
		return ((TreeNode) node).isLeaf();
	}

	//
	// Change Support
	//

	/**
	 * Inserts the <code>roots</code> and connections into the model. Notifies
	 * the model- and undo listeners of the change. The passed-in edits are
	 * executed if they implement the
	 * <code>GraphModelEvent.ExecutableGraphChange</code> interface in ascending
	 * array-order, after execution of the model change. Note: The passed-in
	 * propertyMap may contain <code>PortView</code> s which must be turned into
	 * <code>Point</code> s when stored in the model.
	 */
	public void insert(Object[] roots, Map attributes, ConnectionSet cs, ParentMap pm, UndoableEdit[] edits) {
		if (updateLevel > 0) {
			// Store the insert in the current transaction
			updateTransaction(roots, null, attributes, cs, pm);
		} else {
			// Implement the insert immediately
			GraphModelEdit edit = createEdit(roots, null, attributes, cs, pm, edits);
			if (edit != null) {
				edit.execute(); // fires graphChangeEvent
				if (edits != null) {
					for (int i = 0; i < edits.length; i++)
						if (edits[i] instanceof GraphLayoutCache.GraphLayoutCacheEdit)
							((GraphLayoutCache.GraphLayoutCacheEdit) edits[i]).execute();
				}
				postEdit(edit); // fires undoableedithappened
			}
		}
	}

	/**
	 * Removes <code>cells</code> from the model. Notifies the model- and undo
	 * listeners of the change.
	 */
	public void remove(Object[] roots) {
		if (updateLevel > 0) {
			// Store the insert in the current transaction
			updateTransaction(null, roots, null, null, null);
		} else {
			GraphModelEdit edit = createRemoveEdit(roots);
			if (edit != null) {
				edit.execute();
				postEdit(edit);
			}
		}
	}

	/**
	 * Shortcut to the new edit method which allows inserts and removes to go
	 * along with an edit.
	 */
	public void edit(Map attributes, ConnectionSet cs, ParentMap pm, UndoableEdit[] edits) {
		edit(null, null, attributes, cs, pm, edits);
	}

	/**
	 * Applies <code>attributes</code> and the connection changes to the model.
	 * The initial <code>edits</code> that triggered the call are considered to
	 * be part of this transaction. The passed-in edits are executed if they
	 * implement the <code>GraphModelEvent.ExecutableGraphChange</code>
	 * interface in ascending array-order, after execution of the model change.
	 * Notifies the model- and undo listeners of the change. <strong>Note:
	 * </strong> If only <code>edits</code> is non-null, the edits are directly
	 * passed to the UndoableEditListeners. Note: The passed-in propertyMap may
	 * contains PortViews which must be turned into Points when stored in the
	 * model.
	 */
	public void edit(Object[] inserted, Object[] removed, Map attributes, ConnectionSet cs, ParentMap pm,
			UndoableEdit[] edits) {
		if (updateLevel > 0) {
			// Store the insert in the current transaction
			updateTransaction(inserted, removed, attributes, cs, pm);
		} else {
			if ((inserted == null || inserted.length == 0) && (removed == null || removed.length == 0)
					&& (attributes == null || attributes.isEmpty()) && (cs == null || cs.isEmpty()) && pm == null
					&& edits != null && edits.length == 1) {
				if (edits[0] instanceof GraphLayoutCache.GraphLayoutCacheEdit)
					((GraphLayoutCache.GraphLayoutCacheEdit) edits[0]).execute();
				postEdit(edits[0]); // UndoableEdit Relay
			} else {
				GraphModelEdit edit = createEdit(inserted, removed, attributes, cs, pm, edits);
				if (edit != null) {
					edit.execute();
					if (edits != null) {
						for (int i = 0; i < edits.length; i++)
							if (edits[i] instanceof GraphLayoutCache.GraphLayoutCacheEdit)
								((GraphLayoutCache.GraphLayoutCacheEdit) edits[i]).execute();
					}
					postEdit(edit);
				}
			}
		}
	}

	/*
	 * Unused, placeholder for JGraph 6 API
	 */
	public synchronized void execute(ExecutableChange change) {
	}

	/*
	 * Read section entitled "Complex Transactions" in the user manual chapter 2
	 * for how to use the update level
	 */
	public int getUpdateLevel() {
		return updateLevel;
	}

	/*
	 * Read section entitled "Complex Transactions" in the user manual chapter 2
	 * for how to use the update level
	 */
	public void beginUpdate() {
		updateLevel++;
		if (updateLevel == 1) {
			transEditAttrs = new Hashtable();
			transEditCS = new ConnectionSet();
			transEditPM = new ParentMap();
			transAddedCells = new HashSet();
			transRemovedCells = new HashSet();
		}
	}

	/*
	 * Read section entitled "Complex Transactions" in the user manual chapter 2
	 * for how to use the update level
	 */
	public void endUpdate() {
		updateLevel--;

		if (updateLevel == 0) {
			// Dispatch the built up transaction
			GraphModelEdit edit = createEdit(transAddedCells.toArray(), transRemovedCells.toArray(), transEditAttrs,
					transEditCS, transEditPM, null);
			if (edit != null) {
				edit.execute(); // fires graphChangeEvent
				postEdit(edit); // fires undoableedithappened
			}
		}
	}

	/**
	 * Updates the current state of the various transaction data
	 * 
	 * @param inserted
	 *            inserted cell to be added to the transaction
	 * @param removed
	 *            removed cells to be removed from the transaction
	 * @param attributes
	 *            nested attribute maps to apply to the transaction
	 * @param cs
	 *            connection sets to add to the transaction
	 * @param pm
	 *            parent maps to add to the transaction
	 */
	protected void updateTransaction(Object[] inserted, Object[] removed, Map attributes, ConnectionSet cs, ParentMap pm) {
		// Inserts
		if (inserted != null && inserted.length > 0) {
			for (int i = 0; i < inserted.length; i++) {
				if (transRemovedCells.contains(inserted[i])) {
					// Does not make sense to remove then insert a cell
					// in same transaction, operations cancel out
					transRemovedCells.remove(inserted[i]);
				} else {
					transAddedCells.add(inserted[i]);
				}
			}
		}
		// Removes
		if (removed != null && removed.length > 0) {
			for (int i = 0; i < removed.length; i++) {
				if (transAddedCells.contains(removed[i])) {
					// Does not make sense to insert then remove a cell
					// in same transaction, operations cancel out
					transAddedCells.remove(removed[i]);
				} else {
					transRemovedCells.add(removed[i]);
				}
			}
		}
		// Attributes
		if (attributes != null) {
			GraphConstants.merge(attributes, transEditAttrs);
		}
		// Connection sets
		if (cs != null) {
			Set connections = transEditCS.getConnections();
			connections.addAll(cs.getConnections());
			transEditCS.setConnections(connections);
			Set edges = transEditCS.getEdges();
			edges.addAll(cs.getEdges());
			transEditCS.setEdges(edges);
		}
		// Parent maps
		if (pm != null) {
			Iterator entries = pm.entries();
			while (entries.hasNext()) {
				ParentMap.Entry entry = (ParentMap.Entry) entries.next();
				transEditPM.addEntry(entry.getChild(), entry.getParent());
			}
		}
	}

	/**
	 * Sends <code>cells</code> to back.
	 */
	public void toBack(Object[] cells) {
		GraphModelLayerEdit edit = createLayerEdit(cells, GraphModelLayerEdit.BACK);
		if (edit != null) {
			edit.execute();
			postEdit(edit);
		}
	}

	/**
	 * Brings <code>cells</code> to front.
	 */
	public void toFront(Object[] cells) {
		GraphModelLayerEdit edit = createLayerEdit(cells, GraphModelLayerEdit.FRONT);
		if (edit != null) {
			edit.execute();
			postEdit(edit);
		}
	}

	protected GraphModelLayerEdit createLayerEdit(Object[] cells, int layer) {
		return new GraphModelLayerEdit(cells, layer);
	}

	//
	// Edit Creation
	//

	/**
	 * Returns an edit that represents an insert.
	 */
	// protected GraphModelEdit createInsertEdit(Object[] cells, Map
	// attributeMap,
	// ConnectionSet cs, ParentMap pm, UndoableEdit[] edits) {
	// return createEdit(cells, null, attributeMap, cs, pm, edits);
	// }
	/**
	 * Returns an edit that represents a remove.
	 */
	protected GraphModelEdit createRemoveEdit(Object[] cells) {
		// Remove from GraphStructure
		ConnectionSet cs = ConnectionSet.create(this, cells, true);
		// Remove from Group Structure
		ParentMap pm = ParentMap.create(this, cells, true, false);
		// Construct Edit
		GraphModelEdit edit = createEdit(null, cells, null, cs, pm, null);
		if (edit != null)
			edit.end();
		return edit;
	}

	protected GraphModelEdit createEdit(Object[] inserted, Object[] removed, Map attributes, ConnectionSet cs,
			ParentMap pm, UndoableEdit[] edits) {
		GraphModelEdit edit = new GraphModelEdit(inserted, removed, attributes, cs, pm);
		if (edit != null) {
			if (edits != null)
				for (int i = 0; i < edits.length; i++)
					edit.addEdit(edits[i]);
			edit.end();
		}
		return edit;
	}

	//
	// Change Handling
	//

	/**
	 * Inserts <code>cells</code> into the model. Returns the cells that were
	 * inserted (including descendants).
	 */
	protected Object[] handleInsert(Object[] cells) {
		Object[] inserted = null;
		if (cells != null) {
			for (int i = 0; i < cells.length; i++)
				// Add to Roots if no parent
				if (getParent(cells[i]) == null)
					roots.add(cells[i]);
			// Return *all* inserted cells
			inserted = getDescendants(this, cells).toArray();
		}
		return inserted;
	}

	/**
	 * Removes <code>cells</code> from the model. Returns the cells that were
	 * removed as roots.
	 */
	protected Object[] handleRemove(Object[] cells) {
		Set removedRoots = new HashSet();
		if (cells != null && cells.length > 0) {
			Set rootsSet = new HashSet(roots);
			for (int i = 0; i < cells.length; i++) {
				if (getParent(cells[i]) == null && rootsSet.contains(cells[i])) {
					removedRoots.add(cells[i]);
				}
			}
			if (removedRoots.size() > 0) {
				// If any roots have been removed, reform the roots
				// lists appropriately, keeping the order the same
				int newRootsSize = roots.size() - removedRoots.size();
				if (newRootsSize < 8) {
					newRootsSize = 8;
				}
				List newRoots = new ArrayList(newRootsSize);
				Iterator iter = roots.iterator();
				while (iter.hasNext()) {
					Object cell = iter.next();
					if (!removedRoots.contains(cell)) {
						newRoots.add(cell);
					}
				}
				roots = newRoots;
			}
		}
		return removedRoots.toArray();
	}

	/**
	 * Applies <code>cells</code> to the model. Returns a parent map that may be
	 * used to undo this change.
	 */
	protected ParentMap handleParentMap(ParentMap parentMap) {
		if (parentMap != null) {
			ParentMap undo = new ParentMap();
			HashSet rootsSet = null;
			HashSet rootsToBeRemoved = null;
			Iterator it = parentMap.entries();
			while (it.hasNext()) {
				ParentMap.Entry entry = (ParentMap.Entry) it.next();
				Object child = entry.getChild();
				Object parent = entry.getParent();
				undo.addEntry(child, getParent(child));
				if (parent == null) {
					if (child instanceof MutableTreeNode) {
						((MutableTreeNode) child).removeFromParent();
					}
				} else {
					if (parent instanceof DefaultMutableTreeNode && child instanceof MutableTreeNode) {
						((DefaultMutableTreeNode) parent).add((MutableTreeNode) child);
					}
				}

				if (rootsSet == null) {
					rootsSet = new HashSet(roots);
				}
				boolean isRoot = rootsSet.contains(child);
				if (parent == null && !isRoot) {
					rootsSet.add(child);
					roots.add(child);
				} else if (parent != null && isRoot) {
					if (rootsToBeRemoved == null) {
						rootsToBeRemoved = new HashSet();
					}
					rootsSet.remove(child);
					rootsToBeRemoved.add(child);
				}
			}
			if (rootsToBeRemoved != null && rootsToBeRemoved.size() > 0) {
				// If any roots have been removed, reform the roots
				// lists appropriately, keeping the order the same
				int newRootsSize = roots.size() - rootsToBeRemoved.size();
				if (newRootsSize < 8) {
					newRootsSize = 8;
				}
				List newRoots = new ArrayList(newRootsSize);
				Iterator iter = roots.iterator();
				while (iter.hasNext()) {
					Object cell = iter.next();
					if (!rootsToBeRemoved.contains(cell)) {
						newRoots.add(cell);
					}
				}
				roots = newRoots;
			}
			return undo;
		}
		return null;
	}

	/**
	 * Applies <code>attributes</code> to the cells specified as keys. Returns
	 * the <code>attributes</code> to undo the change.
	 */
	protected Map handleAttributes(Map attributes) {
		if (attributes != null) {
			Hashtable undo = new Hashtable(attributes.size());
			Iterator it = attributes.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object cell = entry.getKey();
				Map deltaNew = (Map) entry.getValue();
				// System.out.println("deltaNew="+deltaNew);
				// System.out.println("stateOld="+getAttributes(cell));
				// Handle New Values
				Map deltaOld = null;
				AttributeMap attr = getAttributes(cell);
				if (attr != null) {
					deltaOld = attr.applyMap(deltaNew);
					// System.out.println("stateNew="+getAttributes(cell));
					// System.out.println("deltaOld="+deltaOld);
					undo.put(cell, deltaOld);
				} else {
					// Make room for the value
					deltaOld = new Hashtable(2);
				}
				// Handle new values
				Object newValue = deltaNew.get(GraphConstants.VALUE);
				if (newValue != null) {
					Object oldValue = valueForCellChanged(cell, newValue);
					if (oldValue != null)
						GraphConstants.setValue(deltaOld, oldValue);
					// TODO: Userobject of null is probably invalid
					else
						GraphConstants.setRemoveAttributes(deltaOld, new Object[] { GraphConstants.VALUE });
				} else {
					// Special case to handle removal of value attribute
					Object[] remove = GraphConstants.getRemoveAttributes(deltaNew);
					if (remove != null && remove.length > 0) {
						for (int i = 0; i < remove.length; i++) {
							if (remove[i] == GraphConstants.VALUE) {
								Object oldValue = valueForCellChanged(cell, null);
								if (oldValue != null) {
									GraphConstants.setValue(deltaOld, oldValue);
								}
							}
						}
					}
				}
			}
			return undo;
		}
		return null;
	}

	/**
	 * Applies the new value to the specified cell. Unfortunately for cloning
	 * the user object you must still override the attribute map and provide a
	 * custom cloneUserObject method. This is because the cloning of a cell is
	 * local to the cell, which in turn has a reference to its attribute map.
	 * 
	 * @param cell
	 * @param newValue
	 * @return the old value for the cell, if any
	 */
	public Object valueForCellChanged(Object cell, Object newValue) {
		if (cell instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) cell;
			Object oldValue = node.getUserObject();
			node.setUserObject(newValue);
			return oldValue;
		}
		return null;
	}

	//
	// Connection Set Handling
	//

	/**
	 * Applies <code>connectionSet</code> to the model. Returns a connection set
	 * that may be used to undo this change.
	 */
	protected ConnectionSet handleConnectionSet(ConnectionSet cs) {
		if (cs != null) {
			ConnectionSet csundo = new ConnectionSet();
			Iterator it = cs.connections();
			while (it.hasNext()) {
				ConnectionSet.Connection c = (ConnectionSet.Connection) it.next();
				Object edge = c.getEdge();
				if (c.isSource())
					csundo.connect(edge, getSource(edge), true);
				else
					csundo.connect(edge, getTarget(edge), false);
				handleConnection(c, false);
			}

			// When removing edges it is possible that an edge is
			// removed in a later step which has been added in a
			// previous connection establishment (set semantic).
			// Therefore, we first need to remove all old connections
			// and then add all new connections in two steps.
			it = cs.connections();
			while (it.hasNext())
				handleConnection((ConnectionSet.Connection) it.next(), true);
			return csundo;
		}
		return null;
	}

	/**
	 * Inserts the specified connection into the model.
	 */
	protected void handleConnection(ConnectionSet.Connection c, boolean establish) {
		Object edge = c.getEdge();
		Object port = (establish) ? c.getPort() : (c.isSource()) ? getSource(edge) : getTarget(edge);
		connect(edge, port, c.isSource(), establish);
	}

	/**
	 * Connects or disconnects the edge and port in this model based on
	 * <code>remove</code>. Subclassers should override this to update
	 * connectivity datastructures.
	 */
	protected void connect(Object edge, Object port, boolean isSource, boolean insert) {
		if (port instanceof Port)
			if (insert)
				((Port) port).addEdge(edge);

			// Only removes if opposite is not
			// connected to same port
			else if ((isSource) ? getTarget(edge) != port : getSource(edge) != port)
				((Port) port).removeEdge(edge);
		if (!insert)
			port = null;
		if (edge instanceof Edge) {
			if (isSource)
				((Edge) edge).setSource(port);
			else
				((Edge) edge).setTarget(port);
		}
	}

	//
	// GraphModelListeners
	//

	/**
	 * Adds a listener for the GraphModelEvent posted after the graph changes.
	 * 
	 * @see #removeGraphModelListener
	 * @param l
	 *            the listener to add
	 */
	public void addGraphModelListener(GraphModelListener l) {
		listenerList.add(GraphModelListener.class, l);
	}

	/**
	 * Removes a listener previously added with <B>addGraphModelListener() </B>.
	 * 
	 * @see #addGraphModelListener
	 * @param l
	 *            the listener to remove
	 */
	public void removeGraphModelListener(GraphModelListener l) {
		listenerList.remove(GraphModelListener.class, l);
	}

	/**
	 * Invoke this method after you've changed how the cells are to be
	 * represented in the graph.
	 */
	public void cellsChanged(final Object[] cells) {
		if (cells != null) {
			fireGraphChanged(this, new GraphModelEvent.GraphModelChange() {

				public Object[] getInserted() {
					return null;
				}

				public Object[] getRemoved() {
					return null;
				}

				public Map getPreviousAttributes() {
					return null;
				}

				public ConnectionSet getConnectionSet() {
					return null;
				}

				public ConnectionSet getPreviousConnectionSet() {
					return null;
				}

				public ParentMap getParentMap() {
					return null;
				}

				public ParentMap getPreviousParentMap() {
					return null;
				}

				public void putViews(GraphLayoutCache view, CellView[] cellViews) {
				}

				public CellView[] getViews(GraphLayoutCache view) {
					return null;
				}

				public Object getSource() {
					return this;
				}

				public Object[] getChanged() {
					return cells;
				}

				public Map getAttributes() {
					return null;
				}

				public Object[] getContext() {
					return null;
				}

				public Rectangle2D getDirtyRegion() {
					return null;
				}

				public void setDirtyRegion(Rectangle2D dirty) {
				}

			});
		}
	}

	/*
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireGraphChanged(Object source, GraphModelEvent.GraphModelChange edit) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		GraphModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == GraphModelListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new GraphModelEvent(source, edit);
				((GraphModelListener) listeners[i + 1]).graphChanged(e);
			}
		}
	}

	/**
	 * Return an array of all GraphModelListeners that were added to this model.
	 */
	public GraphModelListener[] getGraphModelListeners() {
		return (GraphModelListener[]) listenerList.getListeners(GraphModelListener.class);
	}

	//
	// GraphModelEdit
	//

	/**
	 * An implementation of GraphModelChange that can be added to the model
	 * event.
	 */
	public class GraphModelEdit extends CompoundEdit implements GraphModelEvent.GraphModelChange {

		/* Cells that were inserted/removed/changed during the last execution. */
		protected Object[] insert, changed, remove, context;

		/* Cells that were inserted/removed/changed during the last execution. */
		protected Object[] inserted, removed;

		/*
		 * Property map for the next execution. Attribute Map is passed to the
		 * views on inserts.
		 */
		protected Map attributes, previousAttributes;

		/* Parent map for the next execution. */
		protected ParentMap parentMap, previousParentMap;

		/** The dirty region of the change prior to it happening */
		protected Rectangle2D dirtyRegion = null;

		/* ConnectionSet for the next execution. */
		protected ConnectionSet connectionSet, previousConnectionSet;

		/* Piggybacked undo from the views. */
		protected Map cellViews = new Hashtable();

		/**
		 * Constructs an edit record.
		 * 
		 * @param inserted
		 *            a set of roots that were inserted
		 * @param removed
		 *            a set of elements that were removed
		 * @param attributes
		 *            the attribute changes made by the edit
		 * @param connectionSet
		 *            the set of changed connections
		 * @param parentMap
		 *            the map of changed parents
		 */
		public GraphModelEdit(Object[] inserted, Object[] removed, Map attributes, ConnectionSet connectionSet,
				ParentMap parentMap) {
			super();
			this.insert = inserted;
			this.remove = removed;
			this.connectionSet = connectionSet;
			this.attributes = attributes;
			this.parentMap = parentMap;
			previousAttributes = null;
			previousConnectionSet = connectionSet;
			previousParentMap = parentMap;
			// Remove Empty Parents
			if (parentMap != null) {
				// Compute Empty Group
				Map childCount = new Hashtable();
				Iterator it = parentMap.entries();
				while (it.hasNext()) {
					ParentMap.Entry entry = (ParentMap.Entry) it.next();
					Object child = entry.getChild();
					if (!isPort(child)) {
						Object oldParent = getParent(child);
						Object newParent = entry.getParent();
						if (oldParent != newParent) {
							changeChildCount(childCount, oldParent, -1);
							changeChildCount(childCount, newParent, 1);
						}
					}
				}
				handleEmptyGroups(filterParents(childCount, 0));
			}
		}

		public Object[] filterParents(Map childCount, int children) {
			ArrayList list = new ArrayList();
			Iterator it = childCount.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				if (entry.getValue() instanceof Integer) {
					if (((Integer) entry.getValue()).intValue() == children)
						list.add(entry.getKey());
				}
			}
			return list.toArray();
		}

		protected void changeChildCount(Map childCount, Object parent, int change) {
			if (parent != null) {
				Integer count = (Integer) childCount.get(parent);
				if (count == null) {
					count = new Integer(getChildCount(parent));
				}
				int newValue = count.intValue() + change;
				childCount.put(parent, new Integer(newValue));
			}
		}

		/**
		 * Adds the groups that become empty to the cells that will be removed.
		 * (Auto remove empty cells.) Removed cells will be re-inserted on undo,
		 * and the parent- child relations will be restored.
		 */
		protected void handleEmptyGroups(Object[] groups) {
			if (removeEmptyGroups) {
				if (groups != null && groups.length > 0) {
					if (remove == null)
						remove = new Object[] {};
					Object[] tmp = new Object[remove.length + groups.length];
					System.arraycopy(remove, 0, tmp, 0, remove.length);
					System.arraycopy(groups, 0, tmp, remove.length, groups.length);
					remove = tmp;
				}
			}
		}

		public boolean isSignificant() {
			return true;
		}

		/**
		 * Returns the source of this change. This can either be a view or a
		 * model, if this change is a GraphModelChange.
		 */
		public Object getSource() {
			return DefaultGraphModel.this;
		}

		/**
		 * Returns the cells that have changed. This includes the cells that
		 * have been changed through a call to getAttributes and the edges that
		 * have been changed with the ConnectionSet.
		 */
		public Object[] getChanged() {
			return changed;
		}

		/**
		 * Returns the objects that have not changed explicitly, but implicitly
		 * because one of their dependent cells has changed.
		 */
		public Object[] getContext() {
			return context;
		}

		/**
		 * Returns the cells that were inserted.
		 */
		public Object[] getInserted() {
			return inserted;
		}

		/**
		 * Returns the cells that were inserted.
		 */
		public Object[] getRemoved() {
			return removed;
		}

		/**
		 * Returns a map that contains (object, map) pairs of the attributes
		 * that have been stored in the model.
		 */
		public Map getPreviousAttributes() {
			return previousAttributes;
		}

		/**
		 * Returns a map of (object, view attributes). The objects are model
		 * objects which need to be mapped to views.
		 */
		public Map getAttributes() {
			return attributes;
		}

		/**
		 * Returns the connectionSet.
		 * 
		 * @return ConnectionSet
		 */
		public ConnectionSet getConnectionSet() {
			return connectionSet;
		}

		public ConnectionSet getPreviousConnectionSet() {
			return previousConnectionSet;
		}

		/**
		 * Returns the parentMap.
		 * 
		 * @return ParentMap
		 */
		public ParentMap getParentMap() {
			return parentMap;
		}

		public ParentMap getPreviousParentMap() {
			return previousParentMap;
		}

		public Rectangle2D getDirtyRegion() {
			return dirtyRegion;
		}

		public void setDirtyRegion(Rectangle2D dirty) {
			this.dirtyRegion = dirty;
		}

		/**
		 * Redoes a change.
		 * 
		 * @exception CannotRedoException
		 *                if the change cannot be redone
		 */
		public void redo() throws CannotRedoException {
			super.redo();
			execute();
		}

		/**
		 * Undoes a change.
		 * 
		 * @exception CannotUndoException
		 *                if the change cannot be undone
		 */
		public void undo() throws CannotUndoException {
			super.undo();
			execute();
		}

		/**
		 * Execute this edit such that the next invocation to this method will
		 * invert the last execution.
		 */
		public void execute() {
			//			dirtyRegion = null;
			// Compute Changed Cells
			Set tmp = new HashSet();
			if (attributes != null)
				tmp.addAll(attributes.keySet());
			if (parentMap != null)
				tmp.addAll(parentMap.getChangedNodes());
			// Note: One must also include the previous parents!
			if (connectionSet != null)
				tmp.addAll(connectionSet.getChangedEdges());
			if (remove != null) {
				for (int i = 0; i < remove.length; i++)
					tmp.remove(remove[i]);
			}
			changed = tmp.toArray();
			// Context cells
			Set ctx = getEdges(DefaultGraphModel.this, changed);
			context = ctx.toArray();
			// Do Execute
			inserted = insert;
			removed = remove;
			remove = handleInsert(inserted);
			previousParentMap = parentMap;
			parentMap = handleParentMap(parentMap);
			// Adds previous parents
			if (parentMap != null)
				tmp.addAll(parentMap.getChangedNodes());
			previousConnectionSet = connectionSet;
			connectionSet = handleConnectionSet(connectionSet);
			insert = handleRemove(removed);
			previousAttributes = attributes;
			attributes = handleAttributes(attributes);
			changed = tmp.toArray();
			// Fire Event
			fireGraphChanged(DefaultGraphModel.this, this);
		}

		public void putViews(GraphLayoutCache view, CellView[] views) {
			if (view != null && views != null)
				cellViews.put(view, views);
		}

		public CellView[] getViews(GraphLayoutCache view) {
			return (CellView[]) cellViews.get(view);
		}

		public String toString() {
			String s = new String();
			if (inserted != null) {
				s += "Inserted:\n";
				for (int i = 0; i < inserted.length; i++)
					s += "  " + inserted[i] + "\n";
			} else
				s += "None inserted\n";
			if (removed != null) {
				s += "Removed:\n";
				for (int i = 0; i < removed.length; i++)
					s += "  " + removed[i] + "\n";
			} else
				s += "None removed\n";
			if (changed != null && changed.length > 0) {
				s += "Changed:\n";
				for (int i = 0; i < changed.length; i++)
					s += "  " + changed[i] + "\n";
			} else
				s += "None changed\n";
			if (parentMap != null)
				s += parentMap.toString();
			else
				s += "No parent map\n";
			return s;
		}
	}

	/**
	 * An implementation of GraphViewChange.
	 */
	public class GraphModelLayerEdit extends AbstractUndoableEdit implements GraphModelEvent.GraphModelChange {

		public static final int FRONT = -1, BACK = -2;

		protected Object changeSource;

		protected transient Object[] cells;

		protected transient int[] next, prev;

		protected int layer;

		// The cell that change are the parents, because they need to
		// reload their childs for reordering!
		protected Object[] changed;

		/**
		 * Constructs a GraphModelEdit. This modifies the order of the cells in
		 * the model.
		 */
		public GraphModelLayerEdit(Object[] cells, int layer) {
			this.cells = cells;
			this.layer = layer;
			next = new int[cells.length];
			prev = new int[cells.length];
			updateNext();
			// Compute array of changed cells (roots or parents of cells)
			Set par = new HashSet();
			for (int i = 0; i < cells.length; i++) {
				Object cell = DefaultGraphModel.this.getParent(cells[i]);
				if (cell == null)
					cell = cells[i];
				par.add(cell);
			}
			changed = par.toArray();
		}

		protected void updateNext() {
			for (int i = 0; i < next.length; i++)
				next[i] = layer;
		}

		/**
		 * Returns the source of this change. This can either be a view or a
		 * model, if this change is a GraphModelChange.
		 */
		public Object getSource() {
			return DefaultGraphModel.this;
		}

		/**
		 * Returns the cells that have changed.
		 */
		public Object[] getChanged() {
			return changed;
		}

		/**
		 * Returns the cells that have changed.
		 */
		public Object[] getInserted() {
			return null;
		}

		/**
		 * Returns the cells that have changed.
		 */
		public Object[] getRemoved() {
			return null;
		}

		/**
		 * Returns null.
		 */
		public Object[] getContext() {
			return null;
		}

		/**
		 * Returns null.
		 */
		public Map getAttributes() {
			return null;
		}

		/**
		 * Returns null.
		 */
		public Map getPreviousAttributes() {
			return null;
		}

		public ConnectionSet getConnectionSet() {
			return null;
		}

		public ConnectionSet getPreviousConnectionSet() {
			return null;
		}

		/**
		 * Returns null.
		 */
		public ParentMap getParentMap() {
			return null;
		}

		public ParentMap getPreviousParentMap() {
			return null;
		}

		public Rectangle2D getDirtyRegion() {
			return null;
		}

		public void setDirtyRegion(Rectangle2D dirty) {
		}

		/**
		 * Allows a <code>GraphLayoutCache</code> to add and execute and
		 * UndoableEdit in this change. This does also work if the parent edit
		 * has already been executed, in which case the to be added edit will be
		 * executed immediately, after addition. This is used to handle changes
		 * to the view that are triggered by certain changes of the model. Such
		 * implicit edits may be associated with the view so that they may be
		 * undone and redone correctly, and are stored in the model's global
		 * history together with the parent event as one unit.
		 */
		public void addImplicitEdit(UndoableEdit edit) {
			// ignore
		}

		/**
		 * Returns the views that have not changed explicitly, but implicitly
		 * because one of their dependent cells has changed.
		 */
		public CellView[] getViews(GraphLayoutCache view) {
			return null;
		}

		/**
		 * Returns the views that have not changed explicitly, but implicitly
		 * because one of their dependent cells has changed.
		 */
		public void putViews(GraphLayoutCache view, CellView[] cellViews) {
			// ignore
		}

		/**
		 * Redoes a change.
		 * 
		 * @exception CannotRedoException
		 *                if the change cannot be redone
		 */
		public void redo() throws CannotRedoException {
			super.redo();
			updateNext();
			execute();
		}

		/**
		 * Undoes a change.
		 * 
		 * @exception CannotUndoException
		 *                if the change cannot be undone
		 */
		public void undo() throws CannotUndoException {
			super.undo();
			execute();
		}

		/**
		 * Execute this edit such that the next invocation to this method will
		 * invert the last execution.
		 */
		public void execute() {
			for (int i = 0; i < cells.length; i++) {
				List list = getParentList(cells[i]);
				if (list != null) {
					prev[i] = list.indexOf(cells[i]);
					if (prev[i] >= 0) {
						list.remove(prev[i]);
						int n = next[i];
						if (n == FRONT)
							n = list.size();
						else if (n == BACK)
							n = 0;
						list.add(n, cells[i]);
						next[i] = prev[i];
					}
				}
			}
			updateListeners();
		}

		protected void updateListeners() {
			fireGraphChanged(DefaultGraphModel.this, this);
		}

		/**
		 * Returns the list that exclusively contains <code>view</code>.
		 */
		protected List getParentList(Object cell) {
			List list = null;
			if (cell instanceof DefaultMutableTreeNode) {
				Object parent = ((DefaultMutableTreeNode) cell).getParent();
				if (parent instanceof DefaultGraphCell)
					list = ((DefaultGraphCell) parent).getChildren();
				else
					list = roots;
			}
			return list;
		}

	}

	//
	// Static Methods
	//

	/**
	 * Returns a deep clone of the specified cell, including all children.
	 */
	public static Object cloneCell(GraphModel model, Object cell) {
		Map clones = model.cloneCells(getDescendants(model, new Object[] { cell }).toArray());
		return clones.get(cell);
	}

	/**
	 * Returns a deep clone of the specified cells, including all children.
	 */
	public static Object[] cloneCell(GraphModel model, Object[] cells) {
		Map clones = model.cloneCells(getDescendants(model, cells).toArray());
		for (int i = 0; i < cells.length; i++) {
			cells[i] = clones.get(cells[i]);
		}
		return cells;
	}

	/**
	 * Helper methods that connects the source of <code>edge</code> to
	 * <code>port</code> in <code>model</model>.
	 */
	public static void setSourcePort(GraphModel model, Object edge, Object port) {
		model.edit(null, new ConnectionSet(edge, port, true), null, null);
	}

	/**
	 * Helper methods that connects the source of <code>edge</code> to
	 * <code>port</code> in <code>model</model>.
	 */
	public static void setTargetPort(GraphModel model, Object edge, Object port) {
		model.edit(null, new ConnectionSet(edge, port, false), null, null);
	}

	/**
	 * Returns the source vertex of the edge by calling getParent on getSource
	 * on the specified model.
	 */
	public static Object getSourceVertex(GraphModel model, Object edge) {
		if (model != null)
			return model.getParent(model.getSource(edge));
		return null;
	}

	/**
	 * Returns the target vertex of the edge by calling getParent on getTarget
	 * on the specified model.
	 */
	public static Object getTargetVertex(GraphModel model, Object edge) {
		if (model != null)
			return model.getParent(model.getTarget(edge));
		return null;
	}

	/**
	 * @return Returns the user object of the given cell. This implementation
	 *         checks if the cell is a default mutable tree node and returns
	 *         it's user object.
	 * 
	 * @deprecated Use {@link GraphModel#getValue(Object)} instead.
	 */
	public static Object getUserObject(Object cell) {
		if (cell instanceof DefaultMutableTreeNode)
			return ((DefaultMutableTreeNode) cell).getUserObject();
		return null;
	}

	/**
	 * Checks whether the cell has at least one child which is not a port. This
	 * implementation operates on the model, not taking into account visibility
	 * of cells. It returns true for groups regardless of their folded state.
	 * 
	 * @param cell
	 *            the cell to check for being a group
	 * @return Returns true if the cell contains at least one cell which is not
	 *         a port
	 */
	public static boolean isGroup(GraphModel model, Object cell) {
		for (int i = 0; i < model.getChildCount(cell); i++) {
			if (!model.isPort(model.getChild(cell, i)))
				return true;
		}
		return false;
	}

	/**
	 * Returns all cells of the model in an array.
	 * 
	 * @see #getDescendants(GraphModel, Object[])
	 * 
	 * @return Returns all cells in the model including all descandants.
	 */
	public static Object[] getAll(GraphModel model) {
		return getDescendants(model, getRoots(model)).toArray();
	}

	/**
	 * Returns the roots of the specified model as an array. This implementation
	 * uses the GraphModel interface in the general case, but if the model is a
	 * <code>DefaultGraphModel</code> the performance can be improved to linear
	 * time.
	 */
	public static Object[] getRoots(GraphModel model) {
		Object[] cells = null;
		if (model != null) {
			// If model is DefaultGraphModel, we can do a linear time getRoots
			if (model instanceof DefaultGraphModel) {
				cells = ((DefaultGraphModel) model).getRoots().toArray();
			} else {
				cells = new Object[model.getRootCount()];
				for (int i = 0; i < cells.length; i++) {
					cells[i] = model.getRootAt(i);
				}
			}
		}
		return cells;
	}

	/**
	 * Returns the roots of the specified model as a collection. This
	 * implementation uses the GraphModel interface in the general case, but if
	 * the model is a <code>DefaultGraphModel</code> the performance can be
	 * improved to linear time.
	 */
	public static Collection getRootsAsCollection(GraphModel model) {
		Collection cells = null;
		if (model != null) {
			// If model is DefaultGraphModel, we can do a linear time getRoots
			if (model instanceof DefaultGraphModel) {
				cells = ((DefaultGraphModel) model).getRoots();
			} else {
				cells = new LinkedHashSet(model.getRootCount());
				for (int i = 0; i < cells.size(); i++) {
					cells.add(model.getRootAt(i));
				}
			}
		}
		return cells;
	}

	/**
	 * Returns the roots in <code>cells</code> by checking if their parent is
	 * <code>null</code>. This implementation only uses the GraphModel
	 * interface. This method never returns null.
	 */
	public static Object[] getRoots(GraphModel model, Object[] cells) {
		List roots = new ArrayList();
		if (cells != null) {
			for (int i = 0; i < cells.length; i++) {
				if (model.getParent(cells[i]) == null) {
					roots.add(cells[i]);
				}
			}
		}
		return roots.toArray();
	}

	/**
	 * @return Returns the roots of cells, eg. an array that contains no cell
	 *         having an ancestor in cells.
	 */
	public static Object[] getTopmostCells(GraphModel model, Object[] cells) {
		Set cellSet = new HashSet();
		for (int i = 0; i < cells.length; i++)
			cellSet.add(cells[i]);
		List parents = new ArrayList();
		for (int i = 0; i < cells.length; i++) {
			if (!hasAncestorIn(model, cellSet, cells[i]))
				parents.add(cells[i]);
		}
		return parents.toArray();
	}

	/**
	 * Returns true if the specified child has an ancestor in parents.
	 */
	public static boolean hasAncestorIn(GraphModel model, Set parents, Object child) {
		Object parent = model.getParent(child);
		while (parent != null) {
			if (parents.contains(parent))
				return true;
			parent = model.getParent(parent);
		}
		return false;
	}

	/**
	 * Flattens the given array of root cells by adding the roots and their
	 * descandants. The resulting set contains all cells, which means it
	 * contains branches <strong>and </strong> leafs. Note: This is an iterative
	 * implementation. No recursion used. <br>
	 * Note: This returns a linked list, for frequent read operations you should
	 * turn this into an array, or at least an array list.
	 */
	public static List getDescendants(GraphModel model, Object[] cells) {
		if (cells != null) {
			Stack stack = new Stack();
			for (int i = cells.length - 1; i >= 0; i--)
				stack.add(cells[i]);
			LinkedList result = new LinkedList();
			while (!stack.isEmpty()) {
				Object tmp = stack.pop();
				for (int i = model.getChildCount(tmp) - 1; i >= 0; i--)
					stack.add(model.getChild(tmp, i));
				if (tmp != null)
					result.add(tmp);
			}
			return result;
		}
		return null;
	}

	/**
	 * Orders cells so that they reflect the model order.
	 */
	public static Object[] order(GraphModel model, Object[] cells) {
		if (cells != null) {
			Set cellSet = new HashSet();
			for (int i = 0; i < cells.length; i++)
				cellSet.add(cells[i]);
			Stack stack = new Stack();
			for (int i = model.getRootCount() - 1; i >= 0; i--)
				stack.add(model.getRootAt(i));
			LinkedList result = new LinkedList();
			while (!stack.isEmpty()) {
				Object tmp = stack.pop();
				for (int i = model.getChildCount(tmp) - 1; i >= 0; i--)
					stack.add(model.getChild(tmp, i));
				if (cellSet.remove(tmp))
					result.add(tmp);
			}
			return result.toArray();
		}
		return null;
	}

	/**
	 * Returns the set of all connected edges to <code>cells</code> or their
	 * descendants. The passed-in cells are never returned as part of the result
	 * set. This can be used on vertices, edges and ports.
	 */
	public static Set getEdges(GraphModel model, Object[] cells) {
		Set result = new LinkedHashSet();
		if (cells != null) {
			// We know the minimum initial capacity of this set is cells.length
			// We assume the cell has one port at a minimum
			int setSize = ((int) (cells.length * 1.33) + 1);
			Set allCells = new HashSet(setSize, 0.75f);
			for (int i = 0; i < cells.length; i++) {
				allCells.add(cells[i]);
			}
			// Include descendants
			List descendants = getDescendants(model, cells);
			// Iterate through the list rather than adding all to preserve order
			Iterator desIter = descendants.iterator();
			while (desIter.hasNext()) {
				allCells.add(desIter.next());
			}
			if (allCells != null) {
				Iterator it = allCells.iterator();
				while (it.hasNext()) {
					Object c = it.next();
					Iterator edges = model.edges(c);
					while (edges.hasNext())
						result.add(edges.next());
				}
				for (int i = 0; i < cells.length; i++)
					result.remove(cells[i]);
			}
		}
		return result;
	}

	/**
	 * @return Returns the opposite port or vertex in <code>edge</code>.
	 */
	public static Object getOpposite(GraphModel model, Object edge, Object cell) {

		boolean isPort = model.isPort(cell);
		Object source = (isPort) ? model.getSource(edge) : getSourceVertex(model, edge);
		Object target = (isPort) ? model.getTarget(edge) : getTargetVertex(model, edge);
		if (cell == source) {
			return target;
		} else if (cell == target) {
			return source;
		}
		List descendants = getDescendants(model, new Object[] { cell });

		if (descendants.contains(model.getSource(edge))) {
			// outgoing edge
			return target;
		} else {
			// incoming edge.
			return source;
		}
	}

	/**
	 * Returns true if the given vertices are conntected by a single edge in
	 * this document.
	 */
	public static boolean containsEdgeBetween(GraphModel model, Object v1, Object v2) {
		Object[] edges = getEdgesBetween(model, v1, v2, false);
		return (edges != null && edges.length > 0);
	}

	/**
	 * Returns the edges between two specified ports or two specified vertices.
	 * If directed is true then <code>cell1</code> must be the source of the
	 * returned edges. This method never returns null. If there are no edges
	 * between the specified cells, then an array of length 0 is returned.
	 */
	public static Object[] getEdgesBetween(GraphModel model, Object cell1, Object cell2, boolean directed) {
		boolean isPort1 = model.isPort(cell1);
		boolean isPort2 = model.isPort(cell2);
		ArrayList result = new ArrayList();
		Set edges = DefaultGraphModel.getEdges(model, new Object[] { cell1 });
		Iterator it = edges.iterator();
		while (it.hasNext()) {
			Object edge = it.next();
			// TODO: Handle edge groups
			Object source = (isPort1) ? model.getSource(edge) : getSourceVertex(model, edge);
			Object target = (isPort2) ? model.getTarget(edge) : getTargetVertex(model, edge);
			if ((source == cell1 && target == cell2) || (!directed && source == cell2 && target == cell1))
				result.add(edge);
		}
		return result.toArray();
	}

	/**
	 * Returns the outgoing edges for cell. Cell should be a port or a vertex.
	 */
	public static Object[] getOutgoingEdges(GraphModel model, Object cell) {
		return getEdges(model, cell, false);
	}

	/**
	 * Returns the incoming edges for cell. Cell should be a port or a vertex.
	 */
	public static Object[] getIncomingEdges(GraphModel model, Object cell) {
		return getEdges(model, cell, true);
	}

	/**
	 * Returns the incoming or outgoing edges for cell. Cell should be a port or
	 * a vertex.
	 */
	public static Object[] getEdges(GraphModel model, Object cell, boolean incoming) {
		Set edges = DefaultGraphModel.getEdges(model, new Object[] { cell });
		// Base initial capacity on size of set, it can't be any larger
		ArrayList result = new ArrayList(edges.size());
		Iterator it = edges.iterator();

		List descendants = DefaultGraphModel.getDescendants(model,  new Object[] { cell });
		while (it.hasNext()) {
			Object edge = it.next();
			if (incoming) { 
				if (descendants.contains(model.getTarget(edge))) {
					result.add(edge);
				}
			} else {
				if (descendants.contains(model.getSource(edge))) {
					result.add(edge);
				}
			}
//			// TODO: Handle edge groups
//			Object port = (incoming) ? model.getTarget(edge) : model.getSource(edge);
//			Object parent = model.getParent(port);
//			if (port == cell || parent == cell)
//				result.add(edge);
		}
		return result.toArray();
	}

	/**
	 * Returns <code>true</code> if <code>vertex</code> is a valid vertex.
	 * 
	 * @return <code>true</code> if <code>vertex</code> is a valid vertex.
	 */
	public static boolean isVertex(GraphModel model, Object vertex) {
		return (vertex != null && !model.isEdge(vertex) && !model.isPort(vertex));
	}

	// Serialization support
	private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
		s.defaultReadObject();
		listenerList = new EventListenerList();
		emptyIterator = new EmptyIterator();
	}

	public static class EmptyIterator implements Iterator, Serializable {

		public boolean hasNext() {
			return false;
		}

		public Object next() {
			return null;
		}

		public void remove() {
			// nop
		}
	}

	/**
	 * @return the removeEmptyGroups
	 */
	public boolean isRemoveEmptyGroups() {
		return removeEmptyGroups;
	}

	/**
	 * @param removeEmptyGroups
	 *            the removeEmptyGroups to set
	 */
	public void setRemoveEmptyGroups(boolean removeEmptyGroups) {
		this.removeEmptyGroups = removeEmptyGroups;
	}

}