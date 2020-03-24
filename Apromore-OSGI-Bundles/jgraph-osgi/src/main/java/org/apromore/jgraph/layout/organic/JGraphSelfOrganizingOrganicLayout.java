/*
 * $Id: JGraphSelfOrganizingOrganicLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.organic;

import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphLayout;

/**
 * This layout is an implementation of inverted self-organising maps as
 * described by Bernd Meyer in his 1998 paper "Self-Organizing Graphs - A Neural
 * Network Perspective of Graph Layout". Self-organizing maps have some
 * similarities with force-directed layouts, linked nodes tends to cluster.
 * However, a difference with the maps is that there is a uniform space filling
 * distrubtion of nodes. This makes the bounds within which the layout takes
 * place important to calculate correctly at the start. The implementation
 * assumes an average density by default. ISOM layouts are better suited to well
 * connected graphs. <br>
 * 
 * The computational effort per iteration is linear, O(|N|). This comes from the
 * effort of finding the closest node to the random point. When JGraph
 * implements the spatial index structure this will improve to O(log|N|). Only a
 * selection of nodes are moved per iteration and so a greater number of
 * iterations are required for larger graphs. Generally, the number of
 * iterations required is proportional to the number of vertices and so the
 * computational effort including the number of iterations will always be
 * O(|N|). The paper describes 500 iterations as being enough for 25 nodes, thus
 * <code>maxIterationsMultiple</code>, which defines the vertices to number
 * of iterations factor, defaults to 20. <br>
 * 
 * This implementation attempt to calculate sensible values for certain
 * configuration parameters, based on the input graph. The number of iterations,
 * the start radius used, the bounds of the end graph and the narrowing interval
 * are calculated for the user, if the user does not set their own values. If a
 * layout is used repeatedly, the values calculated may become less suitable as
 * the graph changes. To make the layout re-calculate it's own suggested values,
 * set the appropriate value to zero. The parameters that can be reset like this
 * are: <code>maxIterationsMultiple</code>,<code>startRadius</code> and
 * <code>narrowingInterval</code>.
 */
public class JGraphSelfOrganizingOrganicLayout implements JGraphLayout {

	/**
	 * The bounds of the graph prior to the layout
	 */
	protected Rectangle2D bounds = null;
	
	/**
	 * The layout sets this variable to the number of vertices multipled by
	 * <code>maxIterationsMultiple</code> since the number of iterations
	 * required in linear with the number of nodes
	 */
	protected int totalIterations = 0;

	/**
	 * The multiple of the number of vertices to find the total number of
	 * iterations of this layout applied. Defaults to 20. If the user changes it
	 * to any positive integer, that value is used instead.
	 */
	protected int maxIterationsMultiple = 20;

	/**
	 * The current iteration of the layout
	 */
	protected int iteration = 1;

	/**
	 * The current radius of the layout. The radius actually means the number of
	 * times neighbours are found from the winning node. For example, if the
	 * radius is 2, all of the neighbours of winning node are processed for
	 * moving, as well as all the meighbours of those first neighbours. No node
	 * is processed twice. The idea of the later stages of the layout is for
	 * only linked cells to be drawn into clusters, as the radius reduces down
	 * to <code>minRadius</code> as the layout progresses.
	 */
	protected int radius = 0;

	/**
	 * The radius value at on the first iteration. The radius should reflect
	 * both the number of vertices and the ratio of vertices to edges. Larger
	 * numbers of vertices requires a larger radius ( the relationship is
	 * roughly logarithmic ) and higher edge-to-vertex ratio should require a
	 * lower radius. The value defaults to 3, unless the user sets it to any
	 * positive integer.
	 */
	protected int startRadius = 0;

	/**
	 * The lowest radius value allowed. A value of 1 is generally recommended.
	 * Only use a value of 0 if the adaption is under 0.15 at this point in
	 * the layout process, otherwise the symmetry of the layout may be destroyed.
	 */
	protected int minRadius = 1;

	/**
	 * The factor by which the suggest area of the graph bound is multipled by.
	 * The suggested value is determined from the number of nodes. This value is
	 * only used if set to a value other than zero
	 */
	protected double densityFactor = 0.0;

	/**
	 * The number of iterations after which the radius is decremented. This
	 * value should reflect the total number of iterations, the start radius and
	 * minimum radius so that some part of the layout is spent at the minimum
	 * radius.
	 */
	protected int narrowingInterval = 0;

	/**
	 * The current adaption value
	 */
	protected double adaption = 0;

	/**
	 * The start adaption value
	 */
	protected double maxAdaption = 0.8;

	/**
	 * The minimum adaption value
	 */
	protected double minAdaption = 0.1;

	/**
	 * The rate at which the rate of the change of the graph decreases
	 */
	protected double coolingFactor = 1.0;

	/**
	 * A stack of nodes to be visited in the adjustment phase
	 */
	protected Stack stack = null;

	/**
	 * Local copy of cell neighbours
	 */
	protected int[] neighbours[];

	/**
	 * An array of all vertices to be laid out
	 */
	protected Object vertexArray[];

	/**
	 * An array of which vertices have been visited during the current
	 * iteration. Avoid the same vertex being processed twice.
	 */
	protected boolean vertexVisited[];

	/**
	 * An array of the number of edges any particular node is from the winning
	 * node. If a node is not in <code>stack</code> then its corresponding
	 * value in this array will not be valid.
	 */
	protected int vertexDistance[];

	/**
	 * An array of locally stored X co-ordinate positions for the vertices
	 */
	protected double cellLocation[][];

	/**
	 * The X-coordinate of the random point (termed the random vector in the
	 * paper)
	 */
	protected double randomX;

	/**
	 * The Y-coordinate of the random point (termed the random vector in the
	 * paper)
	 */
	protected double randomY;

	/**
	 * Runs the ISOM layout using the graph information specified in the facade.
	 * 
	 * @param graph
	 *            the facade describing the input graph
	 */
	public void run(JGraphFacade graph) {
		// Set the facade to be non-directed, directed graphs produce incorrect
		// results. Store the directed state to reset it after this method
		boolean directed = graph.isDirected();
		graph.setDirected(false);

		vertexArray = graph.getVertices().toArray();
		vertexVisited = new boolean[vertexArray.length];
		vertexDistance = new int[vertexArray.length];
		cellLocation = graph.getLocations(vertexArray);
		neighbours = new int[vertexArray.length][];

		Map vertexMap = new Hashtable(vertexArray.length);

		bounds = graph.getGraphBounds();
		if (densityFactor != 0.0 && bounds != null) {
			// Use the density factor to determine the rough end bounds of the
			// laid out graph
			double currentArea = bounds.getWidth() * bounds.getHeight();
			double preferedArea = densityFactor * vertexArray.length;
			double dimensionRatio = Math.sqrt(preferedArea/currentArea);
			bounds.setFrame(bounds.getX(), bounds.getY(),
					bounds.getWidth() * dimensionRatio,
					bounds.getHeight() * dimensionRatio);
		}

		// Create a map of vertices first. This is required for the array of
		// arrays called neighbours which holds, for each vertex, a list of
		// ints which represents the neighbours cells to that vertex as
		// the indices into vertexArray
		for (int i = 0; i < vertexArray.length; i++) {
			// Set up the mapping from array indices to cells
			vertexMap.put(vertexArray[i], new Integer(i));
		}

		for (int i = 0; i < vertexArray.length; i++) {
			// Set up the mapping from array indices to cells
			vertexMap.put(vertexArray[i], new Integer(i));
			// Makes a local copy of all cell locations for performance

			Object cellNeighbours[] = graph.getNeighbours(vertexArray[i], null,
					true).toArray();
			neighbours[i] = new int[cellNeighbours.length];

			for (int j = 0; j < cellNeighbours.length; j++) {
				Integer indexOtherCell = (Integer) vertexMap
						.get(cellNeighbours[j]);
				// Check the connected cell in part of the vertex list to be
				// acted on by this layout
				if (indexOtherCell != null) {
					int k = indexOtherCell.intValue();
					neighbours[i][j] = k;
				} else {
					// The index of the other cell doesn't correspond to any
					// cell listed to be acted upon in this layout. Set the
					// index to the value of this vertex (a dummy self-loop)
					// so the edge is ignored
					neighbours[i][j] = i;
				}
			}
		}
		adaption = maxAdaption;

		// Set the current radius to the start radius value, setting start to
		// default value if not set by user
		if (startRadius == 0)
			startRadius = 3;
		radius = startRadius;

		// The total number of iterations should be proportional to the number
		// of vertices for a good layout
		totalIterations = vertexArray.length * maxIterationsMultiple;
		if (totalIterations < 100)
			totalIterations = 100;

		// determine the narrowing iterval so each radius value gets an equal
		// number of iterations
		if (narrowingInterval == 0) {
			int numRadiusSteps = startRadius - minRadius + 1;
			if (numRadiusSteps < 1)
				numRadiusSteps = 1;
			narrowingInterval = totalIterations / numRadiusSteps;
		}

		// Main layout loop
		for (iteration = 1; iteration <= totalIterations; iteration++) {
			updateToRandomNode();
			updateRadius();
		}

		// Set locations at final values
		graph.setLocations(vertexArray, cellLocation);

		// Reset the directed state of the facade
		graph.setDirected(directed);
}

	/**
	 * Picks a random point and detemines to the closest nodes to that point
	 */
	protected void updateToRandomNode() {
		double temp = Math.exp(-coolingFactor
				* (1.0 * iteration / totalIterations));
		adaption = Math.max(minAdaption, temp * maxAdaption);
		// Get a random point in the current graph bounds
		randomX = Math.random() * bounds.getWidth();
		randomY = Math.random() * bounds.getHeight();

		// find the vertex closest to this point
		int indexClosestVertex = -1;
		double smallestDelta = Double.MAX_VALUE;

		for (int i = 0; i < vertexArray.length; i++) {
			// This is the performance dominating loop and is proportional
			// to the number of vertices
			// Set up arrays
			vertexDistance[i] = 0;
			vertexVisited[i] = false;

			double distanceSquared = (randomX - cellLocation[i][0]) *
				(randomX - cellLocation[i][0]) + (randomY - cellLocation[i][1])
				* (randomY - cellLocation[i][1]);
			if (distanceSquared < smallestDelta) {
				// This is the closest vertex to the random point so far
				indexClosestVertex = i;
				smallestDelta = distanceSquared;
			}
		}

		// reposition the choosen vertex
		if (indexClosestVertex > -1) {
			moveVertex(indexClosestVertex);
		}
	}

	/**
	 * Check whether or not a number of iterations equal to the narrowing
	 * interval have elapse. If so, decrement the radius, unless already equal
	 * to the minimum radius.
	 */
	private void updateRadius() {
		if ((radius > minRadius) && (iteration % narrowingInterval == 0)) {
			radius--;
		}
	}

	/**
	 * Moved the specified vertex by a factor proportional to
	 * <code>adaption</code>. Also move any neighbours who have a path to the
	 * specified node of less than <code>radius</code> edges by the factor
	 * suitably reduce according to the number of edges between it and the
	 * winning node
	 * 
	 * @param vertexIndex
	 *            the winning node
	 */
	private void moveVertex(int vertexIndex) {
		if (stack == null) {
			stack = new Stack();
		}
		vertexVisited[vertexIndex] = true;
		stack.push(new Integer(vertexIndex));
		int current;

		while (!stack.isEmpty()) {
			current = ((Integer) (stack.pop())).intValue();

			double dx = randomX - cellLocation[current][0];
			double dy = randomY - cellLocation[current][1];

			double factor = adaption / ( 1 << vertexDistance[current]);

			cellLocation[current][0] += factor * dx;
			cellLocation[current][1] += factor * dy;

			if (vertexDistance[current] < radius) {
				for (int i = 0; i < neighbours[vertexIndex].length; i++) {
					// Get the index of the othe cell in the vertex array
					int j = neighbours[vertexIndex][i];
					// Do not proceed self-loops
					if (vertexIndex != j) {
						if (!vertexVisited[j]) {
							vertexVisited[j] = true;
							vertexDistance[j] = vertexDistance[current] + 1;
							stack.push(new Integer(j));
						}
					}
				}
			}
		}
	}

	/**
	 * @return Returns the coolingFactor.
	 */
	public double getCoolingFactor() {
		return coolingFactor;
	}

	/**
	 * @param coolingFactor
	 *            The coolingFactor to set.
	 */
	public void setCoolingFactor(double coolingFactor) {
		this.coolingFactor = coolingFactor;
	}

	/**
	 * @return Returns the maxIterationsMultiple.
	 */
	public int getMaxIterationsMultiple() {
		return maxIterationsMultiple;
	}

	/**
	 * @param maxIterationsMultiple
	 *            The maxIterationsMultiple to set.
	 */
	public void setMaxIterationsMultiple(int maxIterationsMultiple) {
		this.maxIterationsMultiple = maxIterationsMultiple;
	}

	/**
	 * @return Returns the minAdaption.
	 */
	public double getMinAdaption() {
		return minAdaption;
	}

	/**
	 * @param minAdaption
	 *            The minAdaption to set.
	 */
	public void setMinAdaption(double minAdaption) {
		this.minAdaption = minAdaption;
	}

	/**
	 * @return Returns the startRadius.
	 */
	public int getStartRadius() {
		return startRadius;
	}

	/**
	 * @param startRadius
	 *            The startRadius to set.
	 */
	public void setStartRadius(int startRadius) {
		this.startRadius = startRadius;
	}

	/**
	 * @return Returns the maxAdaption.
	 */
	public double getMaxAdaption() {
		return maxAdaption;
	}

	/**
	 * @param maxAdaption
	 *            The maxAdaption to set.
	 */
	public void setMaxAdaption(double maxAdaption) {
		this.maxAdaption = maxAdaption;
	}

	/**
	 * @return Returns the minRadius.
	 */
	public int getMinRadius() {
		return minRadius;
	}

	/**
	 * @param minRadius
	 *            The minRadius to set.
	 */
	public void setMinRadius(int minRadius) {
		this.minRadius = minRadius;
	}

	/**
	 * @return Returns the densityFactor.
	 */
	public double getDensityFactor() {
		return densityFactor;
	}

	/**
	 * @param densityFactor
	 *            The densityFactor to set.
	 */
	public void setDensityFactor(double densityFactor) {
		this.densityFactor = densityFactor;
	}
	
	/**
	 * Returns <code>Self Organizing</code>, the name of this algorithm.
	 */
	public String toString() {
		return "Self Organizing";
	}
}
