/*
 * $Id: JGraphTreeLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * Copyright (c) 2005-2006, David Benson
 * 
 * All rights reserved. 
 * 
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.tree;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingConstants;

import org.apromore.jgraph.layout.JGraphFacade;

/**
 * An implementation of a basic tree layout. The layout is created using the
 * internal <code>TreeNode</code> structure with appropriate interfaces to the
 * actual graph model. The layout can be configured by orientation, the
 * alignment of the nodes per level, the minimum distance between
 */
public class JGraphTreeLayout extends JGraphAbstractTreeLayout {

	/**
	 * alignment indicates what part of the vertices will be lined up on each
	 * row (level) of the tree. Valid values are SwingConstants.TOP,
	 * SwingConstants.CENTER and SwingConstants.BOTTOM. The default is TOP, i.e.
	 * the top of vertices on any one row line up. It should be noted that the
	 * alignment can sound confusing when the orientation changes. The alignment
	 * is always taken that you are looking at the tree with the root node at
	 * the top. If the root node were at the bottom ( orientation is SOUTH )
	 * then SwingConstants.TOP would actually align the bottoms of the vertices
	 * up as you look at the graph. EAST and WEST orientations follow the same
	 * pattern. If it's confusing have a play with the values, it soon becomes
	 * clear.
	 */
	protected int alignment = SwingConstants.TOP;

	/**
	 * Whether or not to bring all nodes on the same level to the same height in
	 * the tree
	 */
	protected boolean combineLevelNodes = true;

	/**
	 * The API method used to exercise the layout upon the facade description
	 * and produce a separate description of the vertex position and edge
	 * routing changes made. It first builds a representation of the tree using
	 * the inner tree class by doing a depth first search of the graph from the
	 * root. It then lays out the graph using the obtained data
	 * 
	 * @param graph
	 *            the facade describing the graph and its configuration
	 */
	public void run(JGraphFacade graph) {
		super.run(graph);

		for (int i = 0; i < graph.getRootCount(); i++) {
			nodes.clear();
			graph.dfs(graph.getRootAt(i), new JGraphFacade.CellVisitor() {
				public void visit(Object parent, Object cell,
						Object previousSibling, int layer, int sibling) {
					StandardTreeNode parentNode = getTreeNode(parent);
					StandardTreeNode childNode = getTreeNode(cell);
					if (parentNode != null) {
						parentNode.addChild(childNode);
						childNode.setParent(parentNode);
					}
				}
			});
			StandardTreeNode root = getTreeNode(graph.getRootAt(i));
			layout(root);
			if (combineLevelNodes) {
				setLevelHeights(root);
			}
			if (positionMultipleTrees) {
				spaceMultipleTrees(root);
			}
			root.setPosition(null, 0);
		}
	}

	/**
	 * Obtains the tree node corresponding to the specified cell
	 * 
	 * @param cell
	 *            the cell whose tree node is to be found
	 * @return the matching tree node, if any
	 */
	protected StandardTreeNode getTreeNode(Object cell) {
		if (cell != null) {
			StandardTreeNode node = (StandardTreeNode) nodes.get(cell);
			if (node == null) {
				node = new StandardTreeNode(cell);
				nodes.put(cell, node);
			}
			return node;
		}
		return null;
	}

	/**
	 * Top-level method that performs actual layout of tree for a specific node.
	 * Note this acts upon the internal tree node structure
	 * 
	 * @param node
	 *            the tree node to be laid out
	 */
	protected void layout(StandardTreeNode node) {
		if (node.children.size() == 0) {
			// do nothing
		} else if (node.children.size() == 1) {
			StandardTreeNode sub = (StandardTreeNode) node.children.get(0);
			sub.setDepth(node.getDepth() + 1);
			layout(sub);

			sub.leftContour.dx = (sub.width - node.width) / 2;
			sub.rightContour.dx = (sub.width - node.width) / 2;

			node.leftContour.next = sub.leftContour;
			node.rightContour.next = sub.rightContour;
		} else {
			for (Iterator it = node.children.iterator(); it.hasNext();) {
				StandardTreeNode n = (StandardTreeNode) it.next();
				n.setDepth(node.getDepth() + 1);
				layout(n);
			}

			join(node);
		}
	}

	/**
	 * Joins nodes underneath the specified tree node
	 * 
	 * @param node
	 *            the node under which the tree is to be formed
	 */
	protected void join(StandardTreeNode node) {
		int distance = 0;
		for (int i = 0; i < node.children.size(); i++) {
			StandardTreeNode n1 = (StandardTreeNode) node.children.get(i);

			for (int j = i + 1; j < node.children.size(); j++) {
				StandardTreeNode n2 = (StandardTreeNode) node.children.get(j);
				int dist = distance(n1.rightContour, n2.leftContour) / (j - i);
				distance = Math.max(distance, dist);
			}

		}

		distance += nodeDistance;

		// set relative position
		int left;
		if (node.children.size() % 2 == 0) {
			left = (node.children.size() / 2 - 1) * distance + distance / 2;
		} else {
			left = node.children.size() / 2 * distance;
		}

		Iterator it = node.children.iterator();
		for (int i = 0; it.hasNext(); i++) {
			((StandardTreeNode) it.next()).x = -left + i * distance;
		}

		// new contour
		StandardTreeNode first = getLeftMostX(node);
		StandardTreeNode last = getRightMostX(node);

		node.leftContour.next = first.leftContour;
		node.rightContour.next = last.rightContour;

		for (int i = 1; i < node.children.size(); i++) {
			StandardTreeNode n = (StandardTreeNode) node.children.get(i);
			merge(node.leftContour.next, n.leftContour, i * distance
					+ node.width);
		}

		for (int i = node.children.size() - 2; i >= 0; i--) {
			StandardTreeNode n = (StandardTreeNode) node.children.get(i);
			merge(node.rightContour.next, n.rightContour, i * distance
					+ node.width);
		}

		distance = (node.children.size() - 1) * distance / 2;

		node.leftContour.next.dx += distance - node.width / 2;
		node.rightContour.next.dx += distance - node.width / 2;
	}

	/**
	 * Obtains the left most point on the sub-tree under the specified tree node
	 * 
	 * @param node
	 *            the start of the sub-tree to be analysed
	 * @return the left-most tree node in the sub-tree
	 */
	protected StandardTreeNode getLeftMostX(StandardTreeNode node) {
		double tmp = Double.MAX_VALUE;
		boolean hasChildren = false;
		StandardTreeNode mostLeft = null;
		Iterator iter = node.getChildren();
		while (iter.hasNext()) {
			StandardTreeNode child = (StandardTreeNode) iter.next();

			double leftPos = child.x - child.getLeftWidth();
			if (leftPos < tmp) {
				mostLeft = child;
				tmp = leftPos;
			}
			hasChildren = true;
		}

		if (mostLeft != null) {
			return mostLeft;
		}
		return (hasChildren) ? (StandardTreeNode) node.children.get(0) : node;
	}

	/**
	 * Obtains the right most point on the sub-tree under the specified tree
	 * node
	 * 
	 * @param node
	 *            the start of the sub-tree to be analysed
	 * @return the right-most tree node in the sub-tree
	 */
	protected StandardTreeNode getRightMostX(StandardTreeNode node) {
		double tmp = Double.MIN_VALUE;
		boolean hasChildren = false;

		StandardTreeNode mostRight = null;
		Iterator iter = node.getChildren();
		while (iter.hasNext()) {
			StandardTreeNode child = (StandardTreeNode) iter.next();

			double rightPos = child.x + child.getRightWidth();
			if (rightPos > tmp) {
				mostRight = child;
				tmp = rightPos;
			}
			hasChildren = true;
		}

		if (mostRight != null) {
			return mostRight;
		}
		return (hasChildren) ? (StandardTreeNode) node.children.get(0) : node;
	}

	/**
	 * Merges two parts of a polyline together
	 * 
	 * @param main
	 *            the main part of the polyline
	 * @param left
	 *            the polyline to be added
	 * @param distance
	 */
	protected void merge(PolyLine main, PolyLine left, double distance) {

		while (main != null) {
			if (left.next == null) {
				return;
			}

			if (main.next == null) {
				left = left.next;
				break;
			}

			distance += main.dx - left.dx;
			main = main.next;
			left = left.next;
		}

		left.dx += -distance;
		main.next = left;
	}

	/**
	 * 
	 * @param right
	 *            first part of polyline
	 * @param left
	 *            second part of polyline
	 * @return the distance between the two polylines
	 */
	protected int distance(PolyLine right, PolyLine left) {
		int distance = 0;

		for (int i = 0; right != null && left != null;) {
			i += right.dx + left.dx;
			if (i > 0) {
				distance += i;
				i = 0;
			}

			right = right.next;
			left = left.next;
		}

		return distance;
	}

	/**
	 * Sets the position of the tree nodes specified
	 * 
	 * @param roots
	 *            the tree node whose position is to be set
	 */
	protected void setPosition(List roots) {
		for (Iterator it = roots.iterator(); it.hasNext();) {
			((StandardTreeNode) it.next()).setPosition(null, 0);
		}
	}

	/**
	 * Sets the heights of the level under the specified node
	 * 
	 * @param root
	 *            the node under which level heights will be set
	 */
	protected void setLevelHeights(StandardTreeNode root) {
		List level = root.getNodesByLevel();

		double max = 0.0;
		for (int i = 0; i < level.size(); i++) {
			List l = (List) level.get(i);

			for (int j = 0; j < l.size(); j++) {
				max = Math.max(max, ((StandardTreeNode) l.get(j)).height);
			}

			for (int j = 0; j < l.size(); j++) {
				((StandardTreeNode) l.get(j)).levelheight = max;
			}

			max = 0;
		}
	}

	/**
	 * Ensures that the specified root is spaced far enough from previous trees
	 * so not to overlap any cells.
	 * 
	 * @param root
	 *            the root of the tree to be spaced correctly
	 */
	protected void spaceMultipleTrees(StandardTreeNode root) {
		// Found the appropriate boundary of this tree, allowing for
		// orientation
		Point2D pos = graph.getLocation(root.cell);
		double rootX = 0;
		double rootY = 0;
		if (pos != null) {
			rootX = graph.getLocation(root.cell).getX();
			rootY = graph.getLocation(root.cell).getY();
		}

		if (orientation == SwingConstants.NORTH) {
			int leftMostX = root.getLeftWidth();
			double leftMostTreeX = rootX - leftMostX;
			if (leftMostTreeX < treeBoundary + treeDistance) {
				rootX = treeBoundary + treeDistance + leftMostX;
				graph.setLocation(root.cell, rootX, rootY);
			}

			// Calculate right-most boundary of this tree for next calculation
			int rightMostX = root.getRightWidth();
			treeBoundary = rootX + rightMostX;
		}

		if (orientation == SwingConstants.SOUTH) {
			int rightMostX = root.getRightWidth();
			double rightMostTreeX = rootX - rightMostX;
			if (rightMostTreeX < treeBoundary + treeDistance) {
				rootX = treeBoundary + treeDistance + rightMostX;
				graph.setLocation(root.cell, rootX, rootY);
			}

			// Calculate left-most boundary of this tree for next calculation
			int leftMostX = root.getLeftWidth();
			treeBoundary = rootX + leftMostX;
		}

		if (orientation == SwingConstants.WEST) {
			int topMostY = root.getLeftWidth();
			double topMostTreeY = rootY - topMostY;
			if (topMostTreeY < treeBoundary + treeDistance) {
				rootY = treeBoundary + treeDistance + topMostY;
				graph.setLocation(root.cell, rootX, rootY);
			}

			// Calculate left-most boundary of this tree for next calculation
			int bottomMostY = root.getRightWidth();
			treeBoundary = rootY + bottomMostY;
		}

		if (orientation == SwingConstants.EAST) {
			int topMostY = root.getRightWidth();
			double topMostTreeY = rootY - topMostY;
			if (topMostTreeY < treeBoundary + treeDistance) {
				rootY = treeBoundary + treeDistance + topMostY;
				graph.setLocation(root.cell, rootX, rootY);
			}

			// Calculate right-most boundary of this tree for next calculation
			int bottomMostY = root.getLeftWidth();
			treeBoundary = rootY + bottomMostY + getRightMostX(root).height;
		}
	}

	/**
	 * An ADT representing a node in a tree structure.
	 */
	protected class StandardTreeNode extends TreeNode {
		/**
		 * Collection of children of this node
		 */
		List children;

		/**
		 * the height of this level of nodes
		 */
		double levelheight;

		/**
		 * the left-hand polyline of this node
		 */
		PolyLine leftContour;

		/**
		 * the right-hand polyline of this node
		 */
		PolyLine rightContour;

		/** the layer depth of this node in the tree */
		private int depth;

		/** the parent node of this node */
		protected TreeNode parent;
		
		/**
		 * Creates tree node corresponding to the specified cell
		 * 
		 * @param cell
		 *            the cell this tree node corresponds to
		 */
		public StandardTreeNode(Object cell) {
			super(cell);
			this.children = new ArrayList();
			this.leftContour = new PolyLine(width / 2.0);
			this.rightContour = new PolyLine(width / 2.0);
			this.depth = 0;
		}

		/**
		 * @return the children of this node
		 */
		public Iterator getChildren() {
			return children.iterator();
		}

		/**
		 * @return the left-most point of the left contour
		 */
		public int getLeftWidth() {
			int width = 0;

			PolyLine poly = leftContour;
			int tmp = 0;
			while (poly != null) {
				tmp += poly.dx;
				if (tmp > 0) {
					width += tmp;
					tmp = 0;
				}
				poly = poly.next;
			}

			return width;
		}

		/**
		 * @return the right-most point of the right contour
		 */
		public int getRightWidth() {
			int width = 0;

			PolyLine poly = rightContour;
			int tmp = 0;
			while (poly != null) {
				tmp += poly.dx;
				if (tmp > 0) {
					width += tmp;
					tmp = 0;
				}
				poly = poly.next;
			}

			return width;
		}

		/**
		 * @return the height of this node
		 */
		public double getHeight() {
			if (children.isEmpty()) {
				return levelheight;
			}

			double height = 0;

			for (Iterator it = children.iterator(); it.hasNext();) {
				height = Math.max(height, ((StandardTreeNode) it.next()).getHeight());
			}

			return height + levelDistance + levelheight;
		}

		/**
		 * Adds a new child to this parent node
		 * @param newChild the child to be added
		 */
		public void addChild(StandardTreeNode newChild) {
			children.add(newChild);
		}
		
		/**
		 * Sets the position of this node
		 * 
		 * @param parent
		 *            the parent of this node
		 * @param levelHeight
		 *            the height of nodes on the same level
		 */
		public void setPosition(Point2D parent, double levelHeight) {
			double nextLevelHeight = 0.0;
			for (Iterator it = children.iterator(); it.hasNext();) {
				nextLevelHeight = Math.max(nextLevelHeight, ((StandardTreeNode) it
						.next()).height);
			}

			Point2D pt = graph.getLocation(cell);
			// If the position isn't set, set an arbitary one and presume the
			// final thing is being flushed to the origin or something
			if (pt == null) {
				pt = new Point2D.Double(0.0, 0.0);
			}
			if (parent == null) {

				if (orientation == SwingConstants.WEST
						|| orientation == SwingConstants.EAST) {
					pt.setLocation(pt.getY(), pt.getX());
				}

				if (orientation == SwingConstants.NORTH
						|| orientation == SwingConstants.WEST) {
					parent = new Point2D.Double(pt.getX() + width / 2, pt
							.getY()
							+ height);
				} else if (orientation == SwingConstants.SOUTH
						|| orientation == SwingConstants.EAST) {
					parent = new Point2D.Double(pt.getX() + width / 2, pt
							.getY());
				}

				for (Iterator it = children.iterator(); it.hasNext();) {
					((StandardTreeNode) it.next()).setPosition(parent, nextLevelHeight);
				}

				return;
			}

			if (combineLevelNodes) {
				levelHeight = this.levelheight;
			}

			pt = new Point2D.Double(width, height);

			if (orientation == SwingConstants.NORTH
					|| orientation == SwingConstants.WEST) {
				pt.setLocation(x + parent.getX() - width / 2, parent.getY()
						+ levelDistance);
			} else {
				pt.setLocation(x + parent.getX() - width / 2, parent.getY()
						- levelDistance - levelheight);
			}

			if (alignment == SwingConstants.CENTER) {
				pt.setLocation(pt.getX(), pt.getY() + (levelHeight - height)
						/ 2);
			} else if (alignment == SwingConstants.BOTTOM) {
				pt.setLocation(pt.getX(), pt.getY() + levelHeight - height);
			}

			if (orientation == SwingConstants.WEST
					|| orientation == SwingConstants.EAST) {
				pt.setLocation(pt.getY(), pt.getX());
			}

			graph.setLocation(cell, pt.getX(), pt.getY());

			if (orientation == SwingConstants.NORTH
					|| orientation == SwingConstants.WEST) {
				y = (int) (parent.getY() + levelDistance + levelHeight);
			} else {
				y = (int) (parent.getY() - levelDistance - levelHeight);
			}

			for (Iterator it = children.iterator(); it.hasNext();) {
				((StandardTreeNode) it.next()).setPosition(new Point2D.Double(x
						+ parent.getX(), y), nextLevelHeight);
			}
			if (routeTreeEdges) {
				routeEdges(parent);
			}

		}

		/**
		 * Return the nodes of the level of this node
		 * 
		 * @return collection of nodes of same level
		 */
		public List getNodesByLevel() {
			List level = new ArrayList();
			for (Iterator it = children.iterator(); it.hasNext();) {
				List l2 = ((StandardTreeNode) it.next()).getNodesByLevel();

				if (level.size() < l2.size()) {
					List tmp = level;
					level = l2;
					l2 = tmp;
				}

				for (int i = 0; i < l2.size(); i++) {
					((List) level.get(i)).addAll((List) l2.get(i));
				}
			}

			ArrayList node = new ArrayList();
			node.add(this);
			level.add(0, node);

			return level;
		}

		/**
		 * @return the depth
		 */
		public int getDepth() {
			return depth;
		}

		/**
		 * @param depth the depth to set
		 */
		public void setDepth(int depth) {
			this.depth = depth;
		}

		/**
		 * @return the parent
		 */
		public TreeNode getParent() {
			return parent;
		}

		/**
		 * @param parent the parent to set
		 */
		public void setParent(TreeNode parent) {
			this.parent = parent;
		}

	}

	/**
	 * ADT of a straight part of a polyline
	 */
	protected class PolyLine {
		/**
		 * delta value of this section of the polyline
		 */
		double dx;

		/**
		 * The next section of the polyline
		 */
		PolyLine next;

		/**
		 * Create a new polyline
		 * 
		 * @param dx
		 *            the length of this section
		 */
		public PolyLine(double dx) {
			this.dx = dx;
		}
	}

	/**
	 * @return Returns the alignment.
	 */
	public int getAlignment() {
		return alignment;
	}

	/**
	 * SwingConstants.TOP SwingConstants.CENTER SwingConstants.BOTTOM are valid
	 * inputs to this method
	 * 
	 * @param alignment
	 */
	public void setAlignment(int alignment) {
		if (alignment != SwingConstants.TOP
				&& alignment != SwingConstants.CENTER
				&& alignment != SwingConstants.BOTTOM) {
			throw new IllegalArgumentException(
					"Alignment must be one of TOP, CENTER or BOTTOM");
		}

		this.alignment = alignment;
	}

	/**
	 * @return Returns the combineLevelNodes.
	 */
	public boolean isCombineLevelNodes() {
		return combineLevelNodes;
	}

	/**
	 * @param combineLevelNodes
	 *            The combineLevelNodes to set.
	 */
	public void setCombineLevelNodes(boolean combineLevelNodes) {
		this.combineLevelNodes = combineLevelNodes;
	}

	/**
	 * Returns <code>Tree</code>, the name of this algorithm.
	 */
	public String toString() {
		return "Tree";
	}
}