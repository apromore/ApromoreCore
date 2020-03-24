/*
 * @(#)ParentMap.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * An object that describes relations between childs and parents.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public class ParentMap implements Serializable {

	/** Contents of the parent map. */
	protected ArrayList entries = new ArrayList();

	/**
	 * Set of changed changedNodes for the parent map. Includes childs and
	 * parents.
	 */
	protected Set changedNodes = new HashSet();

	/** Maps parents to integers with the future number of childs. */
	protected Map childCount = new Hashtable();

	/**
	 * Constructs a <code>ParentMap</code> object.
	 */
	public ParentMap() {
		// empty
	}

	/**
	 * Constructs a <code>ParentMap</code> object.
	 */
	public ParentMap(Object[] children, Object parent) {
		addEntries(children, parent);
	}

	/**
	 * Returns a parent map that represents the insertion or removal of
	 * <code>cells</code> in <code>model</code> based on <code>remove</code>.
	 * Unselected childs of selected nodes are moved to the first unselected
	 * parent of that node.
	 * <p>
	 * <strong>Note:</strong> Consequently, cells "move up" one level when
	 * their parent is removed. <strong>Note:</strong> onlyUsePassedInCells can
	 * be used to indicate if only cells from the passed-in cell array are
	 * allowed parents. This is only used if remove is not true.
	 */
	public static ParentMap create(GraphModel m, Object[] c, boolean remove,
			boolean onlyUsePassedInCells) {
		Set cellSet = new HashSet();
		for (int i = 0; i < c.length; i++) {
			// Do not add null cells otherwise the while loop lower down runs
			// in an infinite loop
			if (c[i] != null) {
				cellSet.add(c[i]);
			}
		}
		ParentMap parentMap = new ParentMap();
		for (int i = 0; i < c.length; i++) {
			// Collect Parent Information
			Object parent = m.getParent(c[i]);
			if (parent != null
					&& (!onlyUsePassedInCells || (!remove && cellSet
							.contains(parent))))
				parentMap.addEntry(c[i], (remove) ? null : parent);
			if (remove) {
				// Move Orphans to First Unselected Parent
				while (cellSet.contains(parent)) {
					// Make sure there are no nulls in the cell set or the
					// while will get stuck in a loop
					parent = m.getParent(parent);
				}
				for (int j = 0; j < m.getChildCount(c[i]); j++) {
					Object child = m.getChild(c[i], j);
					if (!cellSet.contains(child))
						parentMap.addEntry(child, parent);
				}
			}
		}
		return parentMap;
	}

	/**
	 * Add a new entry for this child, parent pair to the parent map. The child
	 * and parent are added to the set of changed nodes. Note: The previous
	 * parent is changed on execution of this parent map and must be added by
	 * the GraphModel and reflected by the GraphChange.getChanged method. TODO:
	 * In general, the GraphModel should be in charge of computing the set of
	 * changed cells.
	 */
	public void addEntry(Object child, Object parent) {
		if (child != null) {
			entries.add(new Entry(child, parent));
			// Update Changed Nodes
			changedNodes.add(child);
			if (parent != null)
				changedNodes.add(parent);
		}
	}

	/**
	 * Adds all child parent pairs using addEntry.
	 */
	public void addEntries(Object[] children, Object parent) {
		for (int i = 0; i < children.length; i++)
			addEntry(children[i], parent);
	}

	/**
	 * Returns the number of entries.
	 */
	public int size() {
		return entries.size();
	}

	/**
	 * Returns an <code>Iterator</code> for the entries in the map.
	 */
	public Iterator entries() {
		return entries.iterator();
	}

	/**
	 * Returns a <code>Set</code> for the nodes, childs and parents, in this
	 * parent map.
	 */
	public Set getChangedNodes() {
		return changedNodes;
	}

	/**
	 * Creates a new parent map based on this parent map, where the child and
	 * parents are mapped using <code>map</code>. If one the cells is not in
	 * <code>map</code>, then the original cell is used instead.
	 * <p>
	 */
	public ParentMap clone(Map map) {
		ParentMap pm = new ParentMap();
		Iterator it = entries();
		while (it.hasNext()) {
			Entry e = (Entry) it.next();
			Object child = map.get(e.getChild());
			Object parent = map.get(e.getParent());
			if (child == null)
				child = e.getChild();
			if (parent == null)
				parent = e.getParent();
			if (child != null && parent != null)
				pm.addEntry(child, parent);
		}
		return pm;
	}

	/**
	 * Object that represents the relation between a child an a parent.
	 */
	public class Entry implements Serializable {

		/** Child and parent of the relation this entry describes. */
		protected Object child, parent;

		/**
		 * Constructs a new relation between <code>child</code> and
		 * <code>parent</code>.
		 */
		public Entry(Object child, Object parent) {
			this.child = child;
			this.parent = parent;
		}

		/**
		 * Returns the child of the relation.
		 */
		public Object getChild() {
			return child;
		}

		/**
		 * Returns the parent of the relation.
		 */
		public Object getParent() {
			return parent;
		}
	}

	public String toString() {
		String s = super.toString() + "\n";
		Iterator it = entries();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			s += " child=" + entry.getChild() + " parent=" + entry.getParent()
					+ "\n";
		}
		return s;
	}

}
