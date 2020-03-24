/*
 * $Id: JGraphSpringLayout.java,v 1.1 2009/09/25 15:14:15 david Exp $
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
package org.apromore.jgraph.layout.graph;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphLayout;
import org.apromore.jgraph.layout.JGraphLayoutProgress;

/**
 * A basic Spring Embedded Layout Algorithm. Edges on the graph represent
 * spring. All the springs have a natural length, measured in the same units
 * as the screen co-ordination system, which they attempt to achieve constantly.
 * If the spring is shorter than its natural length it extends, pushing the
 * nodes are either end of the edge apart. If the spring is longer than its
 * natural length it contracts, pulling the nodes at either end of the edge
 * together. The force exerted by the spring is proportional to different
 * between its current length and its natural length. A force multiple is
 * also applied indicating the "strength" of the spring.
 * 
 * In addition, all nodes repel each other with a force inversely
 * proportional to the distance between each other. The repelling
 * force is mutliplied by a replusive force factor.
 * 
 * The whole affect is to cause nodes to space out fairly evenly but
 * for nodes linked by edges ( springs ) to cluster together
 * 
 * @deprecated use JGraphFastOrganicLayout instead
 */
public class JGraphSpringLayout implements JGraphLayout, JGraphLayout.Stoppable {

	/**
	 * Stores the temporary positions of each cell during the layout
	 */
	protected transient Map displacement = new Hashtable();

	/**
	 * The multiple by which the force replusive each pair of nodes scaled by
	 * Increase to make nodes force further apart
	 */
	protected double replusiveForce = 10000.0;
	
	/**
	 * The multiple of force applied to the attraction of springs
	 */
	protected double springForce = 0.2;
	
	/**
	 * The natural length of the spring (edge) whereby it imparts no force
	 * on either connected node
	 */
	protected double springLength = 50.0;
	
	/**
	 * current iteration number
	 */
	protected int iteration;
	
	/**
	 * total number of iterations to step through when running
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
	 * An array of locally stored X co-ordinate positions for the vertices
	 */
	protected double cellLocationX[];
	
	/**
	 * An array of locally stored Y co-ordinate positions for the vertices
	 */
	protected double cellLocationY[];
	
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
	 * Creates a new layout of 50 iterations
	 *
	 */
	public JGraphSpringLayout() {
		this(50);
	}

	/**
	 * Creates a new spring layout to be executed over the specified number
	 * of iterations
	 * @param 
	 * 			iterations the number of layout iterations to execute
	 */
	public JGraphSpringLayout(int iterations) {
		setMaxIterations(iterations);
	}

	/**
	 * @return Returns the progress.
	 */
	public JGraphLayoutProgress getProgress() {
		return progress;
	}
	
	/**
	 * Executes the spring layout of the specified facade data
	 * 
	 * @param graph
	 *            the description of the graph to be acted upon
	 */
	public void run(JGraphFacade graph) {
		// Set the facade to be non-directed, directed graphs produce incorrect
		// results. Store the directed state to reset it after this method
		boolean directed = graph.isDirected();
		graph.setDirected(true);

		Collection vertices = graph.getVertices();
		if (vertices.isEmpty()) return;
		
		// Allocate memory for local cached arrays
		vertexArray = vertices.toArray();
		dispX = new double[vertexArray.length];
		dispY = new double[vertexArray.length];
		cellLocationX = new double[vertexArray.length];
		cellLocationY = new double[vertexArray.length];
		isMoveable = new boolean[vertexArray.length];
		neighbours = new int[vertexArray.length][];

		// If max number of iterations has not been set, guess it
		if (maxIterations == 0) {
			maxIterations = 20 * (int)Math.sqrt(vertexArray.length);
		}
		progress.reset(maxIterations);
		
		// Temporary map used to setup neighbour array of arrays
		Map vertexMap = new Hashtable(vertexArray.length);
		
		// Create a map of vertices first. This is required for the array of
		// arrays called neighbours which holds, for each vertex, a list of
		// ints which represents the neighbours cells to that vertex as
		// the indices into vertexArray
		for (int i=0; i < vertexArray.length; i++) {
			// Set up the mapping from array indices to cells
			vertexMap.put(vertexArray[i],new Integer(i));
		}

		for (int i=0; i < vertexArray.length; i++) {
			dispX[i] = 0;
			dispY[i] = 0;
			// Makes a local copy of all cell locations for performance
			Point2D pos = graph.getLocation(vertexArray[i]);
			cellLocationX[i] = pos.getX();
			cellLocationY[i] = pos.getY();
			isMoveable[i] = graph.isMoveable(vertexArray[i]);
			
			// Get lists of neighbours to all vertices, translate the cells
			// obtained in indices into vertexArray and store as an array
			// against the orginial cell index
			Object cellNeighbours[] = graph.getNeighbours(vertexArray[i], null, false).toArray();
			neighbours[i] = new int[cellNeighbours.length];
			
			for (int j=0; j<cellNeighbours.length; j++) {
				Integer indexOtherCell = (Integer)vertexMap.get(cellNeighbours[j]);
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

		for (iteration = 0; iteration < maxIterations && !progress.isStopped(); iteration++) {
			progress.setProgress(iteration);
			repulse();
			attract();
			reposition(graph);
		}

		// Reset the directed state of the facade
		graph.setDirected(directed);
}

	/**
	 * Calculates a repulsion force between the specified cells
	 * and stores the cumulative displacement applied to each cell
	 */
	protected void repulse() {
		for (int i = 0; i < vertexArray.length; i++) {
			// Only go through rest of array and displace both
			// vertices at once. Saves 50% CPU
			for (int j = i; j < vertexArray.length; j++) {
				if (progress.isStopped())
					return;
				
				if (i != j) {
					// NOTE - this loop forms the quadratic component of the 
					// layout performance. It needs to contain as little as
					// possible, the square root method tends to dominate
					
					// delta is short hand for the difference
					// vector between the positions of the two vertices
					double deltaX = cellLocationX[i] - cellLocationX[j];
					double deltaY = cellLocationY[i] - cellLocationY[j];

					// get the distance between the ends of the edge
					double nodeDistance = Math.sqrt(deltaX*deltaX + deltaY*deltaY);

					if (nodeDistance < 0.1) {
						nodeDistance = 0.1;
						deltaX = 0.1;
						deltaY = 0.1;
					}
					
					// Ensure no divide by zero
					double fr = replusiveForce
							/ (nodeDistance * nodeDistance);
					
					// Don't allow the replusive force to be greater than the spring
					// length. Sometimes on early iteration nodes get very close
					// together and can be thrown out a great distance. Assume
					// the natural spring length is the maximum distance thrown.
					// Another iteration will be required after this one to settle
					// it down
					if (fr > springLength) {
						fr = springLength;
					} else if (fr < -springLength) {
						fr = -springLength;
					}

					double deltaNormX = deltaX / nodeDistance;
					double displacementX = deltaNormX * fr;
					double deltaNormY = deltaY / nodeDistance;
					double displacementY = deltaNormY * fr;

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
	 * Calculates an attractive force between the cells connected by the
	 * specified edge
	 */
	protected void attract() {
		// Check the neighbours of each vertex and calculate the attractive
		// force of the edge connecting them
		for (int i=0; i < vertexArray.length; i++) {
			for (int k=0; k < neighbours[i].length; k++) {
				if (progress.isStopped())
					return;
				
				// Get the index of the othe cell in the vertex array
				int j = neighbours[i][k];
				// Do not process self-loops
				if (i != j) {
					
					double xDelta = cellLocationX[i] - cellLocationX[j];
					double yDelta = cellLocationY[i] - cellLocationY[j];
					
					// The distance between the nodes
					double edgeLength = Math.sqrt((xDelta * xDelta)
							+ (yDelta * yDelta));
					
					// Calculate the length by which the spring is extended
					double extensionLength = edgeLength - springLength;
					
					// Avoid divide by zero
					if (edgeLength < 1.0)
						edgeLength = 1.0;
					// calculate the attractive forces
					double fa = extensionLength * springForce;
					
					double deltaNormX = xDelta / edgeLength;
					double deltaNormY = yDelta / edgeLength;
					double displacementX = deltaNormX * fa;
					double displacementY = deltaNormY * fa;
					
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
	 * repositions the specified cells using the positioning
	 * data obtained through repulse and attract phases
	 * @param graph
	 * 				the description of the graph to be laid out
	 */
	protected void reposition(JGraphFacade graph) {
		for (int index=0; index < vertexArray.length; index++) {
			if (isMoveable[index]) {
				// Update the cached cell locations
				cellLocationX[index] += dispX[index];
				cellLocationY[index] += dispY[index];
				
				// reset displacements
				dispX[index] = 0;
				dispY[index] = 0;

				// If this is the last iteration, write the node locations
				// back to the facade
				if (iteration == maxIterations-1) {
					graph.setLocation(vertexArray[index],
							cellLocationX[index],
							cellLocationY[index]);
				}
			}
		}
	}

	/**
	 * @param iterations the value to set <code>maxIterations</code> to
	 */
	public void setMaxIterations(int iterations) {
		if (iterations < 0) {
			throw new IllegalArgumentException(
					"iterations must be a positive integer");
		}
		this.maxIterations = iterations;
	}

	/**
	 * @return Returns the total number of iterations.
	 */
	public int getMaxIterations() {
		return maxIterations;
	}
	/**
	 * @return Returns the springLength.
	 */
	public double getSpringLength() {
		return springLength;
	}
	/**
	 * @param springLength The springLength to set.
	 */
	public void setSpringLength(double springLength) {
		// Must be finite and positive
		if (springLength < 0.001) {
			throw new IllegalArgumentException(
			"spring length must be postive and non-zero");
		}
		this.springLength = springLength;
	}
	/**
	 * @return Returns the springForce.
	 */
	public double getSpringForce() {
		return springForce;
	}
	/**
	 * @param springForce The springForce to set.
	 */
	public void setSpringForce(double springForce) {
		this.springForce = springForce;
	}
	/**
	 * @return Returns the replusiveForce.
	 */
	public double getReplusiveForce() {
		return replusiveForce;
	}
	/**
	 * @param replusiveForce The replusiveForce to set.
	 */
	public void setReplusiveForce(double replusiveForce) {
		this.replusiveForce = replusiveForce;
	}
	
	/**
	 * Returns <code>Spring</code>, the name of this algorithm.
	 */
	public String toString() {
		return "Spring";
	}
}
