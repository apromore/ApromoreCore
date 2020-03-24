/*
 * $Id: JGraphAbstractTreeLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
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
import java.util.List;
import java.util.Map;

import javax.swing.SwingConstants;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphLayout;

/**
 * An implementation of a basic tree layout. The layout is created using the
 * internal <code>TreeNode</code> structure with appropriate interfaces to the
 * actual graph model. The layout can be configured by orientation, the
 * alignment of the nodes per level, the minimum distance between
 */
public abstract class JGraphAbstractTreeLayout implements JGraphLayout {

	/**
	 * Stores the mapping between internal tree nodes and graph cells
	 */
	protected transient Map nodes = new Hashtable();

	/**
	 * orientation indicates where, relative to the rest of the tree, the root
	 * node is placed. Default is SwingConstants.NORTH (1), other valid values
	 * are SwingConstants.SOUTH (5), SwingConstants.EAST (3) and
	 * SwingConstants.WEST (7). NORTH means the root is at the top and the
	 * children below, WEST means the root is on the left and the children to
	 * the right, and so on.
	 */
	protected int orientation = SwingConstants.NORTH;

	/**
	 * levelDistance is the distance between the lowest point of any vertex on
	 * one level to the highest point of any vertex on the next level down.
	 */
	protected double levelDistance = 30;

	/**
	 * nodeDistance is the minimum distance between any two vertices on the same
	 * level. Levels closer to the root tend to be spaced a lot further apart
	 * than this.
	 */
	protected double nodeDistance = 20;

	/**
	 * Indicates whether or not to space out multiple trees so that no
	 * overlapping occurs between each tree
	 */
	protected boolean positionMultipleTrees = false;

	/**
	 * If <code>positionMultipleTrees</code> is <code>true</code> this value
	 * is the minimum distance between each overlapping tree structure after
	 * they are separated
	 */
	protected double treeDistance = 30;

	/**
	 * Keeps track of the coordinate that each tree fills space up to. This
	 * value refers to either axis, depending on the orientation of the tree.
	 * This is used when there are multiple trees to ensure the correct minimum
	 * separation distance between each one.
	 */
	protected double treeBoundary;

	/**
	 * Keeps track of the old origin so that the new layout can be aligned to it
	 */
	protected Point2D oldOrigin;
	
	/**
	 * The facade that describes the graph to be acted upon
	 */
	protected JGraphFacade graph;
	
	/**
	 * Whether or not to apply a standard routing algorithm to the edges
	 */
	protected boolean routeTreeEdges = false;
	
	/**
	 * The lowest point for each tree level
	 */
	protected double lowerLevelValues[];
	
	/**
	 * The highest point for each tree level
	 */
	protected double upperLevelValues[];

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
		this.graph = graph;
		// Reset values so layout can be reused
		treeBoundary = -treeDistance;
		oldOrigin = graph.getGraphOrigin();
		if (graph.getRootCount() == 0) {
			graph.findTreeRoots();
		}
	}

	/**
	 * SwingConstants.NORTH SwingConstants.EAST SwingConstants.SOUTH
	 * SwingConstants.WEST are valid inputs to this method
	 * 
	 * @param orientation
	 */
	public void setOrientation(int orientation) {
		if (orientation != SwingConstants.NORTH
				&& orientation != SwingConstants.EAST
				&& orientation != SwingConstants.SOUTH
				&& orientation != SwingConstants.WEST) {
			throw new IllegalArgumentException(
					"Orientation must be one of NORTH (" + SwingConstants.NORTH
							+ "), EAST (" + SwingConstants.EAST + "), SOUTH ("
							+ SwingConstants.SOUTH + ") or WEST ("
							+ SwingConstants.WEST + ")");
		}
		this.orientation = orientation;
	}

	/**
	 * An ADT representing a node in a tree structure.
	 */
	protected class TreeNode {
		/**
		 * The width of this node
		 */
		protected double width;

		/**
		 * The height of this node
		 */
		protected double height;

		/**
		 * The position location of this node
		 */
		protected double x = 0.0;

		protected double y = 0.0;

		/**
		 * The graph cell this node corresponds to
		 */
		protected Object cell;

		/**
		 * Creates tree node corresponding to the specified cell
		 * 
		 * @param cell
		 *            the cell this tree node corresponds to
		 */
		public TreeNode(Object cell) {
			this.cell = cell;
			Rectangle2D rect = graph.getBounds(cell);
			if (rect != null) {
				if (orientation == SwingConstants.NORTH
						|| orientation == SwingConstants.SOUTH) {
					width = rect.getWidth();
					height = rect.getHeight();
				} else {
					width = rect.getHeight();
					height = rect.getWidth();
				}
			}
		}
		
		/**
		 * Routes edges between level so the edge descends halfway down to the
		 * next level of cells, moves across to above the cell and then descends
		 * to join the cell
		 *
		 */
		protected void routeEdges(Point2D parent) {
			// Also set the control point of the vertical node's edge
			List incomingEdges = graph.getIncomingEdges(cell, null, true, false);
			if (incomingEdges != null && incomingEdges.size() > 0) {
				Object edge = incomingEdges.get(0);
				List oldPoints = graph.getPoints(edge);
				List newPoints = new ArrayList(4);
				newPoints.add(oldPoints.get(0));
				// Calculate intermediate points
				boolean vertical = (orientation == SwingConstants.NORTH || orientation == SwingConstants.SOUTH);
				boolean treePositiveDirection = (orientation == SwingConstants.NORTH || orientation == SwingConstants.WEST);
				// Calculate offset for vertical distance above child vertices.
				double offset = treePositiveDirection ? -levelDistance / 2.0 : levelDistance/2.0;
				
				Point2D intermed1 = new Point2D.Double(parent.getX(), (parent.getY() + y) / 2.0 + offset);
				Point2D intermed2 = new Point2D.Double(parent.getX() + x, (parent.getY() + y) / 2.0 + offset);
				newPoints.add(intermed1);
				newPoints.add(intermed2);
				newPoints.add(oldPoints.get(oldPoints.size() - 1));
				graph.setPoints(edge, newPoints);
				graph.disableRouting(edge);
			}
		}

		/**
		 * @return the width
		 */
		public double getWidth() {
			return width;
		}

		/**
		 * @param width the width to set
		 */
		public void setWidth(double width) {
			this.width = width;
		}
	}

	/**
	 * @param distance
	 *            new level distance
	 */
	public void setLevelDistance(double distance) {
		levelDistance = distance;
	}

	/**
	 * @param distance
	 *            new node distance
	 */
	public void setNodeDistance(int distance) {
		nodeDistance = distance;
	}

	/**
	 * @return Returns the levelDistance.
	 */
	public double getLevelDistance() {
		return levelDistance;
	}

	/**
	 * @return Returns the nodeDistance.
	 */
	public double getNodeDistance() {
		return nodeDistance;
	}

	/**
	 * @return Returns the orientation.
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * @return Returns the positionMultipleTrees.
	 */
	public boolean isPositionMultipleTrees() {
		return positionMultipleTrees;
	}

	/**
	 * @param positionMultipleTrees
	 *            The positionMultipleTrees to set.
	 */
	public void setPositionMultipleTrees(boolean positionMultipleTrees) {
		this.positionMultipleTrees = positionMultipleTrees;
	}

	/**
	 * @return Returns the treeDistance.
	 */
	public double getTreeDistance() {
		return treeDistance;
	}

	/**
	 * @param treeDistance
	 *            The treeDistance to set.
	 */
	public void setTreeDistance(int treeDistance) {
		this.treeDistance = treeDistance;
	}

	/**
	 * @return Returns the routeTreeEdges.
	 */
	public boolean getRouteTreeEdges() {
		return routeTreeEdges;
	}

	/**
	 * @param routeTreeEdges The routeTreeEdges to set.
	 */
	public void setRouteTreeEdges(boolean routeTreeEdges) {
		this.routeTreeEdges = routeTreeEdges;
	}
}