/*
 * $Id: OrganizationalChart.java,v 1.2 2010/01/29 17:02:49 david Exp $
 * Copyright (c) 2008, David Benson
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
import java.util.Set;

import javax.swing.SwingConstants;

import org.apromore.jgraph.layout.JGraphFacade;

/**
 * A simple organisational chart. There are a number of switches that cause
 * children cells to be drawn downwards from a vertex with the edges routed
 * on one side. If any of the conditions are true, the vertical style is
 * used for those vertices, regardless of whether the other conditions are
 * true or false.

 */
public class OrganizationalChart extends JGraphTreeLayout {

	/**
	 * The level at which nodes in the tree are switched to be vertically
	 * oriented. The first layer is 0, it cannot be laid out like this
	 * because there can only be 1 root per tree.
	 */
	protected int vertexDepthOrientationSwitch = 1;
	
	/**
	 * The minimum number of children a parent has before all children are
	 * drawn in the vertical orientation style
	 */
	protected int childrenLimitOrientationSwitch = 6;
	
	/**
	 * A set of cells whose children should be drawn in the vertical style
	 */
	protected Set horizontalParentsSet = null;
	
	/**
	 * The inset from left hand side of parent vertices that descending
	 * vertical edges are placed
	 */
	protected int verticalEdgeLeftInset = 15;
	
	/**
	 * The inset right from vertical edges that vertices in the vertical style
	 * are offset by
	 */
	protected int verticalEdgeRightInset = 15;
	
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
			OrganizationalTreeNode node = (OrganizationalTreeNode) nodes.get(cell);
			if (node == null) {
				node = new OrganizationalTreeNode(cell);
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
		} else if (((OrganizationalTreeNode) node).verticalStyleChildren) {
			// vertical styles aren't allowed any children
			// Stored the widest child width and the index in children where it
			// occurred
			int widestIndex = -1;
			int index = -1;
			double widestAmount = 0;
			for (Iterator it = node.children.iterator(); it.hasNext();) {
				index++;
				StandardTreeNode n = (StandardTreeNode) it.next();
				n.setDepth(node.getDepth() + 1);
				double childWidth = n.width;
				if (childWidth > widestAmount) {
					widestAmount = childWidth;
					widestIndex = index;
				}
			}
			if (widestIndex >= 0) {
				StandardTreeNode sub = (StandardTreeNode) node.children.get(widestIndex);
				sub.leftContour.dx = 0;
				sub.rightContour.dx = (sub.width - node.width) / 2 + verticalEdgeLeftInset + verticalEdgeRightInset;

				node.leftContour.next = sub.leftContour;
				node.rightContour.next = sub.rightContour;
			} else {
				StandardTreeNode sub = (StandardTreeNode) node.children.get(0);

				sub.leftContour.dx = (sub.width - node.width) / 2;
				sub.rightContour.dx = (sub.width - node.width) / 2;

				node.leftContour.next = sub.leftContour;
				node.rightContour.next = sub.rightContour;
			}
			
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
	 * An ADT representing a node in an organisational chart structure.
	 */
	protected class OrganizationalTreeNode extends StandardTreeNode {
		
		/** Whether or not children of this node this have vertical children */
		protected boolean verticalStyleChildren = false;
		
		
		public OrganizationalTreeNode(Object cell) {
			super(cell);
			// Check if this cell is in the explicit set of vertical vertices
			if (horizontalParentsSet != null && horizontalParentsSet.contains(cell)) {
				verticalStyleChildren = true;
			}
		}

		/**
		 * Adds a new child to this parent node
		 * @param newChild the child to be added
		 */
		public void addChild(StandardTreeNode newChild) {
			children.add(newChild);
			if (children.size() >= childrenLimitOrientationSwitch) {
				verticalStyleChildren = true;
			}
		}
		
		/**
		 * @return the verticalStyleChildren
		 */
		public boolean isVerticalStyleChildren() {
			return verticalStyleChildren;
		}

		/**
		 * @param verticalStyleChildren the verticalStyleChildren to set
		 */
		public void setVerticalStyleChildren(boolean verticalStyleChildren) {
			this.verticalStyleChildren = verticalStyleChildren;
		}
		
		/**
		 * @param depth the depth to set
		 */
		public void setDepth(int depth) {
			super.setDepth(depth);
			if (depth >= vertexDepthOrientationSwitch) {
				verticalStyleChildren = true;
			}
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

				if (verticalStyleChildren) {
					positionVerticalChildren(parent);
				} else {
					for (Iterator it = children.iterator(); it.hasNext();) {
						((StandardTreeNode) it.next()).setPosition(parent,
								nextLevelHeight);
					}
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

			if (verticalStyleChildren) {
				positionVerticalChildren(pt);
			} else {
				for (Iterator it = children.iterator(); it.hasNext();) {
					((StandardTreeNode) it.next()).setPosition(
							new Point2D.Double(x + parent.getX(), y),
							nextLevelHeight);
				}
			}
		}

		protected void positionVerticalChildren(Point2D parent) {
			double verticalOffset = levelDistance;
			for (Iterator it = children.iterator(); it.hasNext();) {
				OrganizationalTreeNode node = (OrganizationalTreeNode) it
						.next();
				if (getParent() == null) {
					System.out.println("something to break on");
				}
				graph.setLocation(node.cell,
						parent.getX() + verticalEdgeLeftInset
								+ verticalEdgeRightInset, parent.getY() + node.getParent().height +
								+ verticalOffset);
				verticalOffset += levelDistance + node.height;
				// Also set the control point of the vertical node's edge
				List incomingEdges = graph.getIncomingEdges(node.cell, null, true, false);
				if (incomingEdges != null && incomingEdges.size() > 0) {
					Object edge = incomingEdges.get(0);
					List oldPoints = graph.getPoints(edge);
					List newPoints = new ArrayList(3);
					newPoints.add(oldPoints.get(0));
					newPoints.add(new Point2D.Double(
							parent.getX() + verticalEdgeLeftInset, parent.getY()
							+ verticalOffset - node.getParent().height));
					newPoints.add(oldPoints.get(oldPoints.size() - 1));
					graph.setPoints(edge, newPoints);
					graph.disableRouting(edge);
				}
			}
		}
	}
}
