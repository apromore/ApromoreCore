/*
 * $Id: JGraphUnionFind.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved. 
 * 
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.algebra;

import java.util.Hashtable;
import java.util.Map;

/**
 * Implements a union find structure that uses union by rank and path
 * compression. The union by rank guarantees worst case find time of O(log N),
 * while Tarjan shows that in combination with path compression (halving) the
 * average time for an arbitrary sequence of m >= n operations is
 * O(m*alpha(m,n)), where alpha is the inverse of the Ackermann function,
 * defined as follows:
 * <code>alpha(m,n) = min{i &gt;= 1 | A(i, floor(m/n)) &gt; log n} for m &gt;= n &gt;= 1</code>
 * Which yields almost constant time for each individual operation.
 */
public class JGraphUnionFind {

	/**
	 * Maps from elements to nodes
	 */
	protected Map nodes = new Hashtable();

	/**
	 * Constructs a union find structure and initializes it with the specified
	 * elements.
	 * 
	 * @param elements
	 */
	public JGraphUnionFind(Object[] elements) {
		for (int i = 0; i < elements.length; i++) {
			nodes.put(elements[i], new Node());
		}
	}

	/**
	 * Returns the node that represents element.
	 */
	public Node getNode(Object element) {
		return (Node) nodes.get(element);
	}

	/**
	 * Returns the set that contains <code>node</code>. This implementation
	 * provides path compression by halving.
	 */
	public Node find(Node node) {
		while (node.getParent().getParent() != node.getParent()) {
			Node t = node.getParent().getParent();
			node.setParent(t);
			node = t;
		}
		return node.getParent();
	}

	/**
	 * Unifies the sets <code>a</code> and <code>b</code> in constant time
	 * using a union by rank on the tree size.
	 */
	public void union(Node a, Node b) {
		Node set1 = find(a);
		Node set2 = find(b);
		if (set1 != set2) {
			// Limits the worst case runtime of a find to O(log N)
			if (set1.getSize() < set2.getSize()) {
				set2.setParent(set1);
				set1.setSize(set1.getSize() + set2.getSize());
			} else {
				set1.setParent(set2);
				set2.setSize(set1.getSize() + set2.getSize());
			}
		}
	}

	/**
	 * Returns true if element a and element b are not in the same set. This
	 * uses getNode and then find to determine the elements set.
	 * 
	 * @param a
	 *            the first element to compare
	 * @param b
	 *            the second element to compare
	 * @return Returns true if a and b are in the same set
	 * 
	 * @see #getNode(Object)
	 */
	public boolean differ(Object a, Object b) {
		Node set1 = find(getNode(a));
		Node set2 = find(getNode(b));
		return set1 != set2;
	}

	/**
	 * A class that defines the identity of a set.
	 */
	public class Node {

		/**
		 * Reference to the parent node. Root nodes point to themselfes.
		 */
		protected Node parent = this;

		/**
		 * The size of the tree. Initial value is 1.
		 */
		protected int size = 1;

		/**
		 * @return Returns the parent node
		 */
		public Node getParent() {
			return parent;
		}

		/**
		 * @param parent
		 *            the parent node to set
		 */
		public void setParent(Node parent) {
			this.parent = parent;
		}

		/**
		 * @return Returns the size.
		 */
		public int getSize() {
			return size;
		}

		/**
		 * @param size
		 *            The size to set.
		 */
		public void setSize(int size) {
			this.size = size;
		}
	}
}