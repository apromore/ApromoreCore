/*
 * Copyright (c) 2005, David Benson
 *
 * All rights reserved.
 *
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.hierarchical;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyModel;

/**
 * A Greedy Cycle removal algorithm for pre-processing cyclic graphs prior to
 * applying a hierarchical layout. The cycles are only reversed in the internal
 * model of the hierarchy.
 */
public class JGraphGreedyCycleRemover implements JGraphHierarchicalLayoutStep {

	/**
	 * A list of the start point nodes in the layout
	 */
	protected List sources = null;

	/**
	 * A list of the end point nodes in the layout
	 */
	protected List sinks = null;

	/**
	 * The root cells of this hierarchy
	 */
	protected Object[] roots = null;

	/**
	 * Constructor that has the roots specified
	 * @param roots the roots of this hierarchy
	 */
	public JGraphGreedyCycleRemover(Object[] roots) {
		this.roots = roots;
	}
	
	/**
	 * Reverses cycles in the internal model using the graph information
	 * specified
	 * 
	 * @param facade
	 *            the facade describing the input graph
	 * @param model
	 *            an internal model of the hierarchical layout
	 * @return the updated hierarchy model
	 */
	public JGraphHierarchyModel run(JGraphFacade facade,
			JGraphHierarchyModel model) {
		if (model == null) {
			Object[] vertices = getOrderedVertices(facade);
			// create model using this ordering
			model = new JGraphHierarchyModel(facade, vertices, true, false, false);
		} else {
			// else we're removing the cycles in a currently constructed HM
			// TODO
			return model;
		}
		return model;
	}

	/**
	 * Performs the actual greedy cycle removal routine.
	 * 
	 * @param facade
	 *            the hierarchy facade to be acted upon
	 * @return the ordered vertices
	 */
	protected Object[] getOrderedVertices(JGraphFacade facade) {
		Object[] vertices = facade.getVertices().toArray();

		if (roots == null) {
			// Create a list of vertices with no incoming edges
			sources = new ArrayList();
			for (int i = 0; i < vertices.length; i++) {
				int numIncomingEdges = facade.getIncomingEdges(vertices[i],
						null, true, false).size();
				if (numIncomingEdges == 0) {
					sources.add(vertices[i]);
				}
			}
		} else {
			sources= new ArrayList(roots.length);
			for (int i = 0; i < roots.length; i++) {
				sources.add(roots[i]);
			}
		}

		// Create a list of vertices with no outgoing edges
		sinks = new ArrayList();

		// Create a map that stores linked lists of vertices with the same
		// (numOutgoingEdges - numIncomingEdges)
		Map vertexDegreesMap = new Hashtable();

		// Create a list of all delta degree values
		List degreeValues = new ArrayList();

		for (int i = 0; i < vertices.length; i++) {
			int numOutgoingEdges = facade.getOutgoingEdges(vertices[i], null,
					true, false).size();
			int numIncomingEdges = facade.getIncomingEdges(vertices[i], null,
					true, false).size();
			if (numOutgoingEdges == 0) {
				sinks.add(vertices[i]);
			} else {
				Integer degreeDelta = new Integer(numOutgoingEdges
						- numIncomingEdges);
				// Insert this value into the correct linked list found through
				// vertexDegrees map using the value of degreeDelta as key
				if (vertexDegreesMap.containsKey(degreeDelta)) {
					// List for this delta degree value already exists
					List sameDegreeList = (List) vertexDegreesMap
							.get(degreeDelta);
					sameDegreeList.add(vertices[i]);
				} else {
					// List for this delta degree value does not already exist
					List sameDegreeList = new LinkedList();
					sameDegreeList.add(vertices[i]);
					vertexDegreesMap.put(degreeDelta, sameDegreeList);

					// Add delta degree to list of degree values
					degreeValues.add(degreeDelta);
				}
			}
		}

		Object[] sourcesArray = sources.toArray();
		Object[] sinksArray = sinks.toArray();

		// Order the middle nodes into an array of decreasing
		// (outdegree-indegree) value
		Object[] orderedDegrees = degreeValues.toArray();
		Arrays.sort(orderedDegrees);

		// Add the sources, then the ordered middle nodes, , then the sinks
		// re-using the vertices array
		System.arraycopy(sourcesArray, 0, vertices, 0, sourcesArray.length);
		// Keep count of where we are in filling up the vertices array
		int vertexIndex = sourcesArray.length;

		for (int i = orderedDegrees.length - 1; i >= 0; i--) {
			// Get the list from the map and append it to the vertices array
			List sameDegreeList = (List) vertexDegreesMap
					.get(orderedDegrees[i]);
			Object[] sameDegrees = sameDegreeList.toArray();
			System.arraycopy(sameDegrees, 0, vertices, vertexIndex,
					sameDegrees.length);
			vertexIndex += sameDegrees.length;
		}

		System.arraycopy(sinksArray, 0, vertices, vertexIndex,
				sinksArray.length);

		return vertices;
	}
}