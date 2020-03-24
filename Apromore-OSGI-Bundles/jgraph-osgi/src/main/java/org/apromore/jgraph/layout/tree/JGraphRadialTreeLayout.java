/*
 * $Id: JGraphRadialTreeLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphLayout;

/**
 * Lays out the nodes in a graph as a radial tree (root at the centre, children
 * in concentric ovals).
 */
public class JGraphRadialTreeLayout implements JGraphLayout {

	/**
	 * Define PI multiplied by 2
	 */
	private static final double TWO_PI = Math.PI * 2.0;

	/**
	 * An object that may be used as a key for a virtual root.
	 */
	protected Object virtualRootCell = new Object();

	/**
	 * Specifies whether root cells should be moved. Note: Single roots are
	 * never moved by this layout.
	 */
	protected boolean moveRoots = false;

	/**
	 * The initial offset to compute the angle position.
	 */
	protected double angleOffset = 0.5;

	/**
	 * Specifies if the radios should be computed automatically.
	 */
	protected boolean autoRadius = false;

	/**
	 * Specifies the minimum and maximum autoradius
	 */
	protected double minradiusx = 80, minradiusy = 80;

	/**
	 * Specifies the minimum and maximum autoradius
	 */
	protected double maxradiusx = 1000, maxradiusy = 1000;

	/**
	 * x-axis radius of each circle
	 */
	protected double radiusx = 100;

	/**
	 * y-axis radius of each circle
	 */
	protected double radiusy = 100;

	/**
	 * x-axis root of the layout
	 */
	protected double rootx;

	/**
	 * y-axis root of the layout
	 */
	protected double rooty;

	/**
	 * Store of mapping from tree nodes to graph cells
	 */
	protected transient Map nodes = new Hashtable();

	/**
	 * Applies a radial tree layout to nodes in the jgraph with respect to the
	 * supplied configuration.
	 * 
	 * @param graph
	 *            the facade describing the graph and its configuration
	 */

	public void run(JGraphFacade graph) {
		if (graph.getRootCount() == 0)
			graph.findTreeRoots();

		nodes.clear();
		for (int i = 0; i < graph.getRootCount(); i++) {
			graph.dfs(graph.getRootAt(i), new JGraphFacade.CellVisitor() {
				public void visit(Object parent, Object cell,
						Object previousSibling, int layer, int sibling) {
					if (!nodes.keySet().contains(cell)) {
						TreeNode parentNode = getTreeNode(parent);
						TreeNode childNode = getTreeNode(cell);
						if (parentNode != null)
							parentNode.children.add(childNode);
					}
				}
			});
		}

		Object root = (graph.getRootCount() == 1) ? graph.getRootAt(0) : null;
		TreeNode tree = getTreeNode(root);

		if (null == tree) {
			return;
		}
		double depth = tree.getDepth();

		// The bounds of the component
		if (graph.getRootCount() == 1) {
			Point2D loc = graph.getLocation(graph.getRootAt(0));
			rootx = (int) loc.getX();
			rooty = (int) loc.getY();
		} else {
			Rectangle2D rect = graph.getGraphBounds();
			if (rect != null) {
				rootx = (int) rect.getX() + (rect.getWidth() / 2);
				rooty = (int) rect.getY() + (rect.getHeight() / 2);
			}
		}

		if (autoRadius) {
			radiusx = Math.min(maxradiusx, Math.max(minradiusx, rootx / depth));
			radiusy = Math.min(maxradiusx, Math.max(minradiusy, rooty / depth));
		}

		layoutTree0(graph, tree);
		// dispatchResult(graph, nodes.values());
	}

	/**
	 * @param cell
	 *            the cell whose tree node is to be obtained
	 * @return the tree node corresponding to the specified cell
	 */
	public TreeNode getTreeNode(Object cell) {
		if (cell == null)
			cell = virtualRootCell;
		if (cell != null) {
			TreeNode node = (TreeNode) nodes.get(cell);
			if (node == null) {
				node = new TreeNode(cell);
				nodes.put(cell, node);
			}
			return node;
		}
		return null;
	}

	/**
	 * Lays out the central tree circle
	 * 
	 * @param graph
	 *            the description of the graph to be laid out
	 * @param node
	 *            the root of the tree
	 */
	private void layoutTree0(JGraphFacade graph, TreeNode node) {
		node.angle = 0;
		node.x = rootx;
		node.y = rooty;
		node.rightBisector = 0;
		node.rightTangent = 0;
		node.leftBisector = TWO_PI;
		node.leftTangent = TWO_PI;

		List parent = new ArrayList(1);
		parent.add(node);
		layoutTreeN(graph, 1, parent);
	}

	/**
	 * Lays out a peripheral radial tree
	 * 
	 * @param graph
	 *            the description of the graph being laid out
	 * @param level
	 *            which level this sub-tree is on
	 * @param nodes
	 *            the nodes in this sub-tree
	 */
	private void layoutTreeN(JGraphFacade graph, int level, List nodes) {
		int rootLevel = (graph.getRootCount() > 1) ? 1 : 0;
		double i;
		double prevAngle = 0.0;
		TreeNode parent, node, firstParent = null, prevParent = null;
		List parentNodes = new ArrayList();

		Iterator nitr = nodes.iterator();
		while (nitr.hasNext()) {
			parent = (TreeNode) nitr.next();

			List children = parent.getChildren();
			double rightLimit = parent.rightLimit();
			double angleSpace = (parent.leftLimit() - rightLimit)
					/ children.size();

			Iterator itr = children.iterator();
			for (i = angleOffset; itr.hasNext(); i++) {
				node = (TreeNode) itr.next();
				Object cell = node.getCell();

				node.angle = rightLimit + (i * angleSpace);
				if (moveRoots || level > rootLevel) {
					node.x = rootx + ((level * radiusx) * Math.cos(node.angle));
					node.y = rooty + ((level * radiusy) * Math.sin(node.angle));
					graph.setLocation(cell, node.x, node.y);
				}

				// Is it a parent node?
				if (node.hasChildren()) {
					parentNodes.add(node);

					if (null == firstParent) {
						firstParent = node;
					}

					// right bisector limit
					double prevGap = node.angle - prevAngle;
					node.rightBisector = node.angle - (prevGap / 2.0);
					if (null != prevParent) {
						prevParent.leftBisector = node.rightBisector;
					}

					double arcAngle = level / (level + 1.0);
					double arc = 2.0 * Math.asin(arcAngle);

					node.leftTangent = node.angle + arc;
					node.rightTangent = node.angle - arc;

					prevAngle = node.angle;
					prevParent = node;
				}
			}
		}

		if (null != firstParent) {
			double remaningAngle = TWO_PI - prevParent.angle;
			firstParent.rightBisector = (firstParent.angle - remaningAngle) / 2.0;
			if (firstParent.rightBisector < 0) {
				prevParent.leftBisector = firstParent.rightBisector + TWO_PI
						+ TWO_PI;
			} else {
				prevParent.leftBisector = firstParent.rightBisector + TWO_PI;
			}
		}

		if (parentNodes.size() > 0) {
			layoutTreeN(graph, level + 1, parentNodes);
		}
	}

	/**
	 * An abstraction of a tree node
	 */
	private static class TreeNode {

		/**
		 * The graph cell this tree node corresponds to
		 */
		private Object cell;

		/**
		 * A colection of children of this node
		 */
		private List children = new ArrayList();

		public double angle, x, y, rightBisector, leftBisector, rightTangent,
				leftTangent;

		/**
		 * Creates a new tree node
		 * 
		 * @param cell
		 *            the graph cell this tree node corresponds to
		 */
		TreeNode(Object cell) {
			this.cell = cell;
		}

		/**
		 * @return the depth of this node in the tree
		 */
		public int getDepth() {
			int depth = 1;
			Iterator itr = children.iterator();
			while (itr.hasNext()) {
				TreeNode node = (TreeNode) itr.next();
				int childDepth = node.getDepth();
				if (childDepth >= depth) {
					depth = childDepth + 1;
				}
			}
			return depth;
		}

		/**
		 * @return the graph cell this tree node corresponds to
		 */
		public Object getCell() {
			return cell;
		}

		/**
		 * Adds a tree node as a child
		 * 
		 * @param node
		 *            the tree node to be added
		 */
		public void addChild(TreeNode node) {
			children.add(node);
		}

		/**
		 * @return a collection of children nodes
		 */
		public List getChildren() {
			return children;
		}

		/**
		 * @return whether or not this node has any children
		 */
		public boolean hasChildren() {
			return children.size() > 0;
		}

		/**
		 * 
		 * @return the left-most limit of the sub-tree beneath this node
		 */
		public double leftLimit() {
			return Math.min(normalize(leftBisector), (leftTangent));
		}

		/**
		 * 
		 * @return the right-most limit of the sub-tree beneath this node
		 */
		public double rightLimit() {
			return Math.max(normalize(rightBisector), (rightTangent));
		}

		/**
		 * @param angle
		 *            the angle to normalize
		 * @return the normal of the angle
		 */
		private double normalize(double angle) {
			/*
			 * while (angle > TWO_PI) { angle -= TWO_PI; } while (angle <
			 * -TWO_PI) { angle += TWO_PI; }
			 */
			return angle;
		}
	}

	/**
	 * @return the value of radiusx
	 */
	public double getRadiusx() {
		return radiusx;
	}

	/**
	 * @param radiusx
	 *            value to set radiusx to
	 */
	public void setRadiusx(double radiusx) {
		this.radiusx = radiusx;
	}

	/**
	 * @return the value of radiusy
	 */
	public double getRadiusy() {
		return radiusy;
	}

	/**
	 * @param radiusy
	 *            value to set radiusx to
	 */
	public void setRadiusy(double radiusy) {
		this.radiusy = radiusy;
	}

	/**
	 * @return Returns the angleOffset.
	 */
	public double getAngleOffset() {
		return angleOffset;
	}

	/**
	 * @param angleOffset
	 *            The angleOffset to set.
	 */
	public void setAngleOffset(double angleOffset) {
		this.angleOffset = angleOffset;
	}

	/**
	 * @return Returns the autoRadius.
	 */
	public boolean isAutoRadius() {
		return autoRadius;
	}

	/**
	 * @param autoRadius
	 *            The autoRadius to set.
	 */
	public void setAutoRadius(boolean autoRadius) {
		this.autoRadius = autoRadius;
	}

	/**
	 * @return Returns the moveRoots.
	 */
	public boolean isMoveRoots() {
		return moveRoots;
	}

	/**
	 * @param moveRoots
	 *            The moveRoots to set.
	 */
	public void setMoveRoots(boolean moveRoots) {
		this.moveRoots = moveRoots;
	}

	/**
	 * @return Returns the maxradiusx.
	 */
	public double getMaxradiusx() {
		return maxradiusx;
	}

	/**
	 * @param maxradiusx
	 *            The maxradiusx to set.
	 */
	public void setMaxradiusx(double maxradiusx) {
		this.maxradiusx = maxradiusx;
	}

	/**
	 * @return Returns the maxradiusy.
	 */
	public double getMaxradiusy() {
		return maxradiusy;
	}

	/**
	 * @param maxradiusy
	 *            The maxradiusy to set.
	 */
	public void setMaxradiusy(double maxradiusy) {
		this.maxradiusy = maxradiusy;
	}

	/**
	 * @return Returns the minradiusx.
	 */
	public double getMinradiusx() {
		return minradiusx;
	}

	/**
	 * @param minradiusx
	 *            The minradiusx to set.
	 */
	public void setMinradiusx(double minradiusx) {
		this.minradiusx = minradiusx;
	}

	/**
	 * @return Returns the minradiusy.
	 */
	public double getMinradiusy() {
		return minradiusy;
	}

	/**
	 * @param minradiusy
	 *            The minradiusy to set.
	 */
	public void setMinradiusy(double minradiusy) {
		this.minradiusy = minradiusy;
	}

	/**
	 * Returns <code>Radialtree</code>, the name of this algorithm.
	 */
	public String toString() {
		return "Radialtree";
	}

}
