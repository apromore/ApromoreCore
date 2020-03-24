/*
 * $Id: JGraphFastOrganicLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2005, Gaudenz Alder
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

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphLayout;
import org.apromore.jgraph.layout.JGraphLayoutProgress;

/**
 * This layout is an implementation of "Graph Drawing by Force-Directed
 * Placement" by Fruchterman and Reingold (1991). FR layouts are a variation on
 * the basic Eades et al Spring Embedded layout. The paper states that
 * "distributing vertices evenly, making edge lengths uniform, and reflecting
 * symmetry" are its target aims. The variation from the basic embedded is that
 * the attractive force is proportional to the square of the spring length, the
 * natural spring length being zero and the repulsive force is linear with the
 * distance between the nodes. FR layouts are better suited to well connected
 * graphs. <br>
 * 
 * The computational effort per iteration is quadratic, O(|N|^2+|E|). This is
 * due to the way all nodes calculate repulsion from all others. The three user
 * variables are <code>forceConstant</code>,<code>initialTemp</code> and
 * <code>maxIteration</code>.<code>forceConstant</code> is the constant k
 * in the paper and affects the radius around each node around which other nodes
 * would be in equilibrium. <code>initialTemp</code> sets the start
 * temperature of the layout, lower values limit the displacement of each node
 * on each iteration. <code>maxIteration</code> sets the total number of
 * iterations of the layout that occur.
 */
public class JGraphFastOrganicLayout implements JGraphLayout,
		JGraphLayout.Stoppable {

	/**
	 * The force constant by which the attractive forces are divided and the
	 * replusive forces are multiple by the square of. The value equates to the
	 * average radius there is of free space around each node.
	 */
	protected double forceConstant = 50;

	/**
	 * Cache of <code>forceConstant</code>^2 for performance
	 */
	protected double forceConstantSquared = 0;

	/**
	 * Temperature to limit displacement at later stages of layout
	 */
	protected double temperature = 0;

	/**
	 * Start value of temperature
	 */
	protected double initialTemp = 200;

	/**
	 * Current iteration count
	 */
	protected int iteration = 0;

	/**
	 * Total number of iterations to run the layout though
	 */
	protected int maxIterations = 0;

	/**
	 * An array of all vertices to be laid out
	 */
	protected Object vertexArray[];

	/**
	 * An array of locally stored X co-ordinate displacements for the vertices
	 */
	protected double dispX[];

	/**
	 * An array of locally stored Y co-ordinate displacements for the vertices
	 */
	protected double dispY[];

	/**
	 * An array of locally stored co-ordinate positions for the vertices
	 */
	protected double cellLocation[][];

	/**
	 * The approximate radius of each cell, nodes only
	 */
	protected double radius[];

	/**
	 * The approximate radius squared of each cell, nodes only
	 */
	protected double radiusSquared[];

	/**
	 * Local copy of isMoveable
	 */
	protected boolean isMoveable[];

	/**
	 * Local copy of cell neighbours
	 */
	protected int[] neighbours[];

	/**
	 * An object to monitor and control progress.
	 */
	protected JGraphLayoutProgress progress = new JGraphLayoutProgress();

	/**
	 * prevents from dividing with zero
	 */
	protected double minDistanceLimit = 2;

	/**
	 * cached version of <code>minDistanceLimit</code> squared
	 */
	protected double minDistanceLimitSquared = 4;

	/**
	 * @return Returns the progress.
	 */
	public JGraphLayoutProgress getProgress() {
		return progress;
	}

	/**
	 * Executes the Fruchterman-Reingold layout using the graph description from
	 * the specified facade
	 * 
	 * @param graph
	 *            the facade describing the graph to be acted upon
	 */
	public void run(JGraphFacade graph) {
		// Set the facade to be non-directed, directed graphs produce incorrect
		// results. Store the directed state to reset it after this method
		boolean directed = graph.isDirected();
		graph.setDirected(false);

		// Allocate memory for local cached arrays
		vertexArray = graph.getVertices().toArray();
		dispX = new double[vertexArray.length];
		dispY = new double[vertexArray.length];
		cellLocation = graph.getLocations(vertexArray);
		isMoveable = new boolean[vertexArray.length];
		neighbours = new int[vertexArray.length][];
		radius = new double[vertexArray.length];
		radiusSquared = new double[vertexArray.length];

		// Temporary map used to setup neighbour array of arrays
		Map vertexMap = new Hashtable(vertexArray.length);

		if (forceConstant < 0.001)
			forceConstant = 0.001;
		forceConstantSquared = forceConstant * forceConstant;

		// Create a map of vertices first. This is required for the array of
		// arrays called neighbours which holds, for each vertex, a list of
		// ints which represents the neighbours cells to that vertex as
		// the indices into vertexArray
		for (int i = 0; i < vertexArray.length; i++) {
			// Set up the mapping from array indices to cells
			vertexMap.put(vertexArray[i], new Integer(i));
			Rectangle2D bounds = graph.getBounds(vertexArray[i]);
			// Set the X,Y value of the internal version of the cell to
			// the center point of the vertex for better positioning
			double width = bounds.getWidth();
			double height = bounds.getHeight();
			cellLocation[i][0] += width / 2.0;
			cellLocation[i][1] += height / 2.0;
			radius[i] = Math.min(width, height);
			radiusSquared[i] = radius[i] * radius[i];
		}

		for (int i = 0; i < vertexArray.length; i++) {
			dispX[i] = 0;
			dispY[i] = 0;
			isMoveable[i] = graph.isMoveable(vertexArray[i]);

			// Get lists of neighbours to all vertices, translate the cells
			// obtained in indices into vertexArray and store as an array
			// against the orginial cell index
			Object cellNeighbours[] = graph.getNeighbours(vertexArray[i], null,
					false).toArray();
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
					// so the attraction force of the edge is not calculated
					neighbours[i][j] = i;
				}
			}
		}

		temperature = initialTemp;

		// If max number of iterations has not been set, guess it
		if (maxIterations == 0) {
			maxIterations = 20 * (int) Math.sqrt(vertexArray.length);
		}
		progress.reset(maxIterations);
		// Main iteration loop
		for (iteration = 0; iteration < maxIterations && !progress.isStopped(); iteration++) {
			progress.setProgress(iteration);

			// Calculate repulsive forces on all vertices
			calcRepulsion();

			// Calculate attractive forces through edges
			calcAttraction();

			calcPositions();

			reduceTemperature();
		}

		// Moved cell location back to top-left from center locations used in
		// algorithm
		for (int i = 0; i < vertexArray.length; i++) {
			Rectangle2D bounds = graph.getBounds(vertexArray[i]);
			cellLocation[i][0] -= bounds.getWidth() / 2.0;
			cellLocation[i][1] -= bounds.getHeight() / 2.0;
		}

		// Write result back
		graph.setLocations(vertexArray, cellLocation);

		// Reset the directed state of the facade
		graph.setDirected(directed);
	}

	/**
	 * Takes the displacements calculated for each cell and applies them to the
	 * local cache of cell positions. Limits the displacement to the current
	 * temperature.
	 */
	public void calcPositions() {
		for (int index = 0; index < vertexArray.length; index++) {
			if (isMoveable[index]) {
				// Get the distance of displacement for this node for this
				// iteration
				double deltaLength = Math.sqrt(dispX[index] * dispX[index]
						+ dispY[index] * dispY[index]);

				if (deltaLength < 0.001)
					deltaLength = 0.001;

				// Scale down by the current temperature if less than the
				// displacement distance
				double newXDisp = dispX[index] / deltaLength
						* Math.min(deltaLength, temperature);

				double newYDisp = dispY[index] / deltaLength
						* Math.min(deltaLength, temperature);

				// reset displacements
				dispX[index] = 0;
				dispY[index] = 0;

				// Update the cached cell locations
				cellLocation[index][0] += newXDisp;
				cellLocation[index][1] += newYDisp;
			}
		}
	}

	/**
	 * Calculates the attractive forces between all laid out nodes linked by
	 * edges
	 */
	public void calcAttraction() {
		// Check the neighbours of each vertex and calculate the attractive
		// force of the edge connecting them
		for (int i = 0; i < vertexArray.length; i++) {
			for (int k = 0; k < neighbours[i].length; k++) {
				if (progress.isStopped())
					return;

				// Get the index of the othe cell in the vertex array
				int j = neighbours[i][k];
				// Do not proceed self-loops
				if (i != j) {

					double xDelta = cellLocation[i][0] - cellLocation[j][0];
					double yDelta = cellLocation[i][1] - cellLocation[j][1];

					// The distance between the nodes
					double deltaLengthSquared = xDelta * xDelta + yDelta
							* yDelta - radiusSquared[i] - radiusSquared[j];

					if (deltaLengthSquared < minDistanceLimitSquared) {
						deltaLengthSquared = minDistanceLimitSquared;
					}
					double deltaLength = Math.sqrt(deltaLengthSquared);

					double force = (deltaLengthSquared) / forceConstant;

					double displacementX = (xDelta / deltaLength) * force;
					double displacementY = (yDelta / deltaLength) * force;
					if (isMoveable[i]) {
						dispX[i] -= displacementX;
						dispY[i] -= displacementY;
					}
					if (isMoveable[j]) {
						dispX[j] += displacementX;
						dispY[j] += displacementY;
					}
				}
			}
		}
	}

	/**
	 * Calculates the repulsive forces between all laid out nodes
	 */
	public void calcRepulsion() {
		for (int i = 0; i < vertexArray.length; i++) {
			for (int j = i; j < vertexArray.length; j++) {
				if (progress.isStopped())
					return;

				if (j != i) {
					double xDelta = cellLocation[i][0] - cellLocation[j][0];
					double yDelta = cellLocation[i][1] - cellLocation[j][1];
					
					if (xDelta == 0)
					{
						xDelta = 0.01 + Math.random();
					}

					if (yDelta == 0)
					{
						yDelta = 0.01 + Math.random();
					}
					
					// Distance between nodes
					double deltaLength = Math.sqrt((xDelta * xDelta)
							+ (yDelta * yDelta));

					double deltaLengthWithRadius = deltaLength - radius[i]
							- radius[j];
					if (deltaLengthWithRadius < minDistanceLimit)
						deltaLengthWithRadius = minDistanceLimit;

					double force = forceConstantSquared / deltaLengthWithRadius;

					double displacementX = (xDelta / deltaLength) * force;
					double displacementY = (yDelta / deltaLength) * force;
					if (isMoveable[i]) {
						dispX[i] += displacementX;
						dispY[i] += displacementY;
					}

					if (isMoveable[j]) {
						dispX[j] -= displacementX;
						dispY[j] -= displacementY;
					}
				}
			}
		}
	}

	/**
	 * Reduces the temperature of the layout from an initial setting in a linear
	 * fashion to zero.
	 */
	private void reduceTemperature() {
		temperature = initialTemp * (1.0 - iteration / (double) maxIterations);
	}

	/**
	 * @return Returns the forceConstant.
	 */
	public double getForceConstant() {
		return forceConstant;
	}

	/**
	 * @param forceConstant
	 *            The forceConstant to set.
	 */
	public void setForceConstant(double forceConstant) {
		this.forceConstant = forceConstant;
	}

	/**
	 * @return Returns the maxIterations.
	 */
	public int getMaxIterations() {
		return maxIterations;
	}

	/**
	 * @param maxIterations
	 *            The maxIterations to set.
	 */
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}

	/**
	 * @return Returns the initialTemp.
	 */
	public double getInitialTemp() {
		return initialTemp;
	}

	/**
	 * @param initialTemp
	 *            The initialTemp to set.
	 */
	public void setInitialTemp(double initialTemp) {
		this.initialTemp = initialTemp;
	}

	/**
	 * Returns <code>Fast Organic</code>, the name of this algorithm.
	 */
	public String toString() {
		return "Fast Organic";
	}
}
