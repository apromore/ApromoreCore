/*
 * $Id: JGraphCompactTreeLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
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

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.SwingConstants;

import org.apromore.jgraph.layout.JGraphFacade;

/**
 * The compact tree layout is a Moen layout, which concentrates on making the
 * graph as compact as possible whilst still allowing correctly for varations in
 * node shapes and sizes
 * 
 * The paper by Moen is called "Drawing Dynamic Trees" and may be purchased from
 * http://csdl.computer.org/comp/mags/so/1990/04/s4021abs.htm
 */
public class JGraphCompactTreeLayout extends JGraphAbstractTreeLayout {

	/**
	 * Value of left to right orientation
	 * 
	 * @deprecated use SwingConstants.WEST instead
	 */
	public static final int LEFT_TO_RIGHT = SwingConstants.WEST;

	/**
	 * Value of up to down orientation
	 * 
	 * @deprecated use SwingConstants.NORTH instead
	 */
	public static final int UP_TO_DOWN = SwingConstants.NORTH;

	/**
	 * Value of default orientation
	 * 
	 * @deprecated use SwingConstants compass directions instead
	 */
	public static final int DEFAULT_ORIENTATION = SwingConstants.WEST;

	/**
	 * Default constructor, sets level distance and orientation to defaults
	 * 
	 */
	public JGraphCompactTreeLayout() {
		orientation = SwingConstants.WEST;
		levelDistance = 30.0;
		nodeDistance = 5.0;
	}

	/**
	 * The run method of this layout that executes a Moen using the cell data
	 * and configuration information in the specified facade It first builds a
	 * representation of the tree using the inner tree class by doing a depth
	 * first search of the graph from the root. It then lays out the graph using
	 * the obtained data and makes the data available for external collection]
	 * 
	 * @param graph
	 *            the facade describing the graph and its configuration
	 */
	public void run(final JGraphFacade graph) {
		super.run(graph);
		for (int i = 0; i < graph.getRootCount(); i++) {
			nodes.clear();
			graph.dfs(graph.getRootAt(i), new JGraphFacade.CellVisitor() {

				public void visit(Object parent, Object cell,
						Object previousSibling, int layer, int sibling) {
					CompactTreeNode parentNode = getTreeLayoutNode(parent);
					CompactTreeNode childNode = getTreeLayoutNode(cell);
					if (parentNode != null) {
						if (previousSibling == null) {
							parentNode.child = childNode;
						} else {
							CompactTreeNode prevNode = getTreeLayoutNode(previousSibling);
							prevNode.sibling = childNode;
						}
					}
				}

			});
			layoutTree(getTreeLayoutNode(graph.getRootAt(i)));
			dispatchResult(nodes.values());
		}
	}

	/**
	 * Top-level layout method for Moen
	 * 
	 * @param root
	 *            the root node of the tree
	 */
	protected void layoutTree(CompactTreeNode root) {
		// kick off Moen's algorithm
		layout(root);

		Rectangle2D rect = graph.getBounds(root.getCell());
		double rootPositionX = rect.getX();
		double rootPositionY = rect.getY();

		switch (orientation) {
		case SwingConstants.WEST:
			leftRightNodeLayout(root, rootPositionX, rootPositionY);
			break;
		case SwingConstants.NORTH:
			upDownNodeLayout(root, null, rootPositionX, rootPositionY);
			break;
		default:
			leftRightNodeLayout(root, rootPositionX, rootPositionY);
		}
	}

	/**
	 * Obtains the mapped node from the internal tree representation used from
	 * the cell specified
	 * 
	 * @param cell
	 *            the cell whose <code>TreeLayoutNode</code> is to be found
	 * @return the internal node representation of the specified cell
	 */
	protected CompactTreeNode getTreeLayoutNode(Object cell) {
		if (cell != null) {
			return getTreeLayoutNode(cell, true);
		}
		return null;
	}

	/**
	 * Obtains the mapped node from the internal tree representation used from
	 * the cell specified
	 * 
	 * @param cell
	 *            the cell whose <code>TreeLayoutNode</code> is to be found
	 * @param createIfNotPresent
	 *            whether or not to create the internal node if it doesn't
	 *            already exist
	 * @return the internal node representation of the specified cell
	 */
	protected CompactTreeNode getTreeLayoutNode(Object cell,
			boolean createIfNotPresent) {
		CompactTreeNode node = (CompactTreeNode) nodes.get(cell);
		if (node == null && createIfNotPresent) {
			node = new CompactTreeNode(cell);
			nodes.put(cell, node);
		}
		return node;
	}

	/**
	 * Sets the new positions of the cells in the graph based on the information
	 * from the interal tree nodes. Note this doesn't apply the position to the
	 * actual graph, only stores the bounds values to be applied later
	 * 
	 * @param treeLayoutNodes
	 *            the nodes of the internal tree structure that describe the new
	 *            tree layout
	 */
	protected void dispatchResult(Collection treeLayoutNodes) {
		Iterator it = treeLayoutNodes.iterator();
		while (it.hasNext()) {
			CompactTreeNode node = (CompactTreeNode) it.next();
			graph.setLocation(node.getCell(), node.x, node.y);
		}
	}

	/**
	 * Laids out the specified tree node in the internal tree representation
	 * 
	 * @param t
	 *            the node to be laid out
	 */
	protected void layout(CompactTreeNode t) {
		CompactTreeNode c;

		if (t == null) {
			return;
		}

		c = t.child;
		while (c != null) {
			layout(c);
			c = c.sibling;
		}

		if (t.child != null) {
			attachParent(t, join(t));
		} else {
			layoutLeaf(t);
		}
	}

	/**
	 * Attaches the specified tree node in a parent-child relationship taking
	 * into account node shape
	 * 
	 * @param t
	 *            the internal tree node
	 */
	protected void attachParent(CompactTreeNode t, double h) {
		final double x;
		double y1;
		final double y2;

		x = nodeDistance + levelDistance;
		y2 = (h - t.width) / 2 - nodeDistance;
		y1 = y2 + t.width + 2 * nodeDistance - h;
		t.child.offsetX = x + t.height;
		t.child.offsetY = y1;
		t.contour.upperHead = new PolyLine(t.height, 0, new PolyLine(x, y1,
				t.contour.upperHead));
		t.contour.lowerHead = new PolyLine(t.height, 0, new PolyLine(x, y2,
				t.contour.lowerHead));
	}

	/**
	 * Laids out a tree node as a leaf, taking into account node shape
	 * 
	 * @param t
	 *            the node to be laid out
	 */
	protected void layoutLeaf(CompactTreeNode t) {
		t.contour.upperTail = new PolyLine(t.height + 2 * nodeDistance, 0, null);
		t.contour.upperHead = t.contour.upperTail;
		t.contour.lowerTail = new PolyLine(0, -t.width - 2 * nodeDistance, null);
		t.contour.lowerHead = new PolyLine(t.height + 2 * nodeDistance, 0,
				t.contour.lowerTail);
	}

	/**
	 * joins the specified tree node
	 * 
	 * @param t
	 *            the tree node to be joined
	 * @return Returns the size of the tree.
	 */
	protected double join(CompactTreeNode t) {
		CompactTreeNode c;
		double d, h, sum;

		c = t.child;
		t.contour = c.contour;
		sum = h = c.width + 2 * nodeDistance;
		c = c.sibling;
		while (c != null) {
			d = merge(t.contour, c.contour);
			c.offsetY = d + h;
			c.offsetX = 0;
			h = c.width + 2 * nodeDistance;
			sum += d + h;
			c = c.sibling;
		}

		return sum;
	}

	/**
	 * 
	 * @param c1
	 * @param c2
	 * @return Returns the width of the layout
	 */
	protected double merge(Polygon c1, Polygon c2) {
		double x, y, total, d;
		PolyLine lower, upper, b;

		x = y = total = 0;
		upper = c1.lowerHead;
		lower = c2.upperHead;

		while (lower != null && upper != null) {

			d = offset(x, y, lower.dx, lower.dy, upper.dx, upper.dy);
			y += d;
			total += d;

			if (x + lower.dx <= upper.dx) {
				y += lower.dy;
				x += lower.dx;
				lower = lower.link;
			} else {
				y -= upper.dy;
				x -= upper.dx;
				upper = upper.link;
			}
		}

		if (lower != null) {
			b = bridge(c1.upperTail, 0, 0, lower, x, y);
			c1.upperTail = (b.link != null) ? c2.upperTail : b;
			c1.lowerTail = c2.lowerTail;
		} else {
			b = bridge(c2.lowerTail, x, y, upper, 0, 0);
			if (b.link == null) {
				c1.lowerTail = b;
			}
		}

		c1.lowerHead = c2.lowerHead;

		return total;
	}

	/**
	 * 
	 * @param p1
	 * @param p2
	 * @param a1
	 * @param a2
	 * @param b1
	 * @param b2
	 * @return Returns the actual offset
	 */
	protected double offset(double p1, double p2, double a1, double a2,
			double b1, double b2) {
		double d, s, t;

		if (b1 <= p1 || p1 + a1 <= 0) {
			return 0.0;
		}

		t = b1 * a2 - a1 * b2;
		if (t > 0.0) {
			if (p1 < 0.0) {
				s = p1 * a2;
				d = s / a1 - p2;
			} else if (p1 > 0.0) {
				s = p1 * b2;
				d = s / b1 - p2;
			} else {
				d = -p2;
			}
		} else if (b1 < p1 + a1) {
			s = (b1 - p1) * a2;
			d = b2 - (p2 + s / a1);
		} else if (b1 > p1 + a1) {
			s = (a1 + p1) * b2;
			d = s / b1 - (p2 + a2);
		} else {
			d = b2 - (p2 + a2);
		}

		if (d > 0.0) {
			return d;
		} else {
			return 0.0;
		}
	}

	/**
	 * 
	 * @param line1
	 * @param x1
	 * @param y1
	 * @param line2
	 * @param x2
	 * @param y2
	 * @return Returns a <code>PolyLine</code>
	 */
	protected PolyLine bridge(PolyLine line1, double x1, double y1,
			PolyLine line2, double x2, double y2) {
		double dy, dx, s;
		PolyLine r;

		dx = x2 + line2.dx - x1;
		if (line2.dx == 0) {
			dy = line2.dy;
		} else {
			s = dx * line2.dy;
			dy = s / line2.dx;
		}

		r = new PolyLine(dx, dy, line2.link);
		line1.link = new PolyLine(0, y2 + line2.dy - dy - y1, r);

		return r;
	}

	protected void branch(CompactTreeNode parent, CompactTreeNode child,
			CompactTreeNode sibling) {
		unzip(parent);
		child.parent = parent;
		if (sibling != null) {
			child.sibling = sibling.sibling;
			sibling.sibling = child;
		} else {
			child.sibling = parent.child;
			parent.child = child;
		}

		zip(parent);
	}

	protected void unzip(CompactTreeNode node) {
		if (node.parent != null) {
			unzip(node.parent);
		}

		if (node.child != null) {
			// TODO detachParent(node);
			// TODO split(node);
		} else {
			// TODO rubout(node);
		}
	}

	protected void zip(CompactTreeNode node) {
		if (node.child != null) {
			attachParent(node, join(node));
		} else {
			layoutLeaf(node);
		}

		if (node.parent != null) {
			zip(node.parent);
		}
	}

	/**
	 * Lays out a Moen in the left-right orientation
	 * 
	 * @param node
	 *            the current node being laid out
	 * @param off_x
	 *            x-axis offset
	 * @param off_y
	 *            y-axis offset
	 */
	protected void leftRightNodeLayout(CompactTreeNode node, double off_x,
			double off_y) {
		CompactTreeNode child, s;
		double siblingOffset;

		node.x += off_x + node.offsetX;
		node.y += off_y + node.offsetY;

		child = node.child; // topmost child

		if (child != null) {
			leftRightNodeLayout(child, node.x, node.y);
			s = child.sibling;
			siblingOffset = node.y + child.offsetY;
			while (s != null) {
				leftRightNodeLayout(s, node.x + child.offsetX, siblingOffset);
				siblingOffset += s.offsetY;
				s = s.sibling;
			}
		}
	}

	/**
	 * Lays out a Moen in the up-down orientation
	 * 
	 * @param node
	 *            the current node being laid out
	 * @param parent
	 *            the parent of the current node
	 * @param off_x
	 *            the total x-axis offset of the parent node
	 * @param off_y
	 *            the total y-axis offset of the parent node
	 */
	protected void upDownNodeLayout(CompactTreeNode node, CompactTreeNode parent,
			double off_x, double off_y) {
		CompactTreeNode child, s;
		double siblingOffset;
		node.x += off_x + node.offsetY;
		node.y += off_y + node.offsetX;
		child = node.child; // leftmost child

		if (child != null) {
			upDownNodeLayout(child, node, node.x, node.y);
			s = child.sibling;
			siblingOffset = node.x + child.offsetY;
			while (s != null) {
				upDownNodeLayout(s, node, siblingOffset, node.y
						+ child.offsetX);
				siblingOffset += s.offsetY;
				s = s.sibling;
			}
		}
	}

	/**
	 * SwingConstants.NORTH SwingConstants.WEST are valid inputs to this method
	 * 
	 * @param orientation
	 */
	public void setOrientation(int orientation) {
		if (orientation != SwingConstants.NORTH
				&& orientation != SwingConstants.WEST) {
			throw new IllegalArgumentException("Orientation must be NORTH ("
					+ SwingConstants.NORTH + "), or WEST ("
					+ SwingConstants.WEST + ")");
		}
		this.orientation = orientation;
	}

	/**
	 * @return Returns the orientation.
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Sets the minimum distance, upon the axis of orientation of the layout,
	 * that a parent will be from any of its children
	 * @deprecated use setLevelDistance
	 * 
	 * @param distance
	 *            the minimum distance
	 */
	public void setChildParentDistance(double distance) {
		setLevelDistance(distance);
	}

	/**
	 * Get the minimum distance between a parent from any of its children
	 * @deprecated use getLevelDistance
	 * 
	 * @return the level distance
	 */
	public double getChildParentDistance() {
		return getLevelDistance();
	}

	/**
	 * @return Returns the nodeBorder.
	 */
	public double getNodeBorder() {
		return nodeDistance;
	}

	/**
	 * @param nodeBorder
	 *            The nodeBorder to set.
	 */
	public void setNodeBorder(double nodeBorder) {
		if (nodeBorder < 0.0) {
			nodeBorder = 0.0;
		}
		this.nodeDistance = nodeBorder;
	}

	/**
	 * Returns <code>Compact Tree</code>, the name of this algorithm.
	 */
	public String toString() {
		return "Compact Tree";
	}

	/**
	 * Abstraction of node in a tree structure
	 */
	protected class CompactTreeNode extends TreeNode {
		/**
		 * The parent of this node
		 */
		CompactTreeNode parent;

		/**
		 * A child of this node
		 */
		CompactTreeNode child;

		/**
		 * The next sibling of this node
		 */
		CompactTreeNode sibling;

		/**
		 * The offset of this node from the root node
		 */
		protected double offsetX = 0.0;

		protected double offsetY = 0.0;

		/**
		 * The contour polygon defining the bounds of this node using polylines
		 */
		Polygon contour;

		/**
		 * Creates a node corresponding to the specified graph cell and of the
		 * given dimensions. Interally, the width and height are stored reversed
		 * for a horizontal tree to reduce the processing required when laying
		 * the tree out
		 * 
		 * @param cell
		 *            the corresponding graph cell
		 */
		public CompactTreeNode(Object cell) {
			super(cell);
			contour = new Polygon();
		}

		/**
		 * @return the graph cell associated with this node
		 */
		public Object getCell() {
			return cell;
		}
	}

	/**
	 * A polygon class based on polylines
	 */
	private static class Polygon {
		/**
		 * Composite polyline
		 */
		PolyLine lowerHead, lowerTail;

		/**
		 * Composite polyline
		 */
		PolyLine upperHead, upperTail;
	}

	/**
	 * A straight line section of a line
	 */
	private static class PolyLine {
		/**
		 * x-offset for this section of the polyline
		 */
		final double dx;

		/**
		 * y-offset for this section of the polyline
		 */
		final double dy;

		/**
		 * Next section of polyline this one connects to
		 */
		PolyLine link;

		/**
		 * Creates a new section of polyline
		 * 
		 * @param dx
		 *            x-offset for this section of the polyline
		 * @param dy
		 *            y-offset for this section of the polyline
		 * @param link
		 *            next section of polyline this one connects to
		 */
		PolyLine(double dx, double dy, PolyLine link) {
			this.dx = dx;
			this.dy = dy;
			this.link = link;
		}
	}
}