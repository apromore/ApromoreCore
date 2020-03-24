/*
 * $Id: JGraphModelFacade.java,v 1.2 2010/01/29 17:04:29 david Exp $ Copyright
 * (c) 2001-2006, Gaudenz Alder Copyright (c) 2005-2006, David Benson
 * 
 * All rights reserved.
 * 
 * This file is licensed under the JGraph software license, a copy of which will
 * have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please contact
 * JGraph sales for another copy.
 */
package org.apromore.jgraph.layout;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.jgraph.algebra.JGraphAlgebra;
import org.apromore.jgraph.algebra.cost.JGraphCostFunction;
import org.apromore.jgraph.algebra.cost.JGraphDistanceCostFunction;
import org.apromore.jgraph.graph.DefaultGraphModel;
import org.apromore.jgraph.graph.GraphModel;

/**
 * An abstract description of a graph that can be used by a layout algorithm.
 * This abstracts visibility, grouping, directed edges, any root cells,
 * translation and scaling functions. It also stores the actual graph to be
 * acted upon by the layout and provides utility method to determine the
 * characteristics of the contained cells. After the layout has been applied
 * this class stores the result of that layout as a nested attribute map.
 * 
 */
public class JGraphModelFacade extends JGraphFacade {

	/**
	 * Constructs a JGraphGraphFacade specifying the graph passed in as the
	 * input graph
	 * 
	 * @param model
	 *            the GraphModel to be laid out
	 */
	public JGraphModelFacade(GraphModel model) {
		this(model, null);
	}

	/**
	 * Constructs a JGraphGraphFacade specifying the graph passed in as the
	 * input graph
	 * 
	 * @param model
	 *            the JGraph to be laid out
	 * @param roots
	 *            the root vertices to be used by tree layouts. This is not the
	 *            same thing as the roots of the graph model.
	 */
	public JGraphModelFacade(GraphModel model, Object[] roots) {
		this(model, roots, true, false, true, true);
	}

	/**
	 * Constructs a JGraphGraphFacade
	 * 
	 * @see #JGraphModelFacade(GraphModel, Object[], boolean, boolean, boolean,
	 *      boolean, JGraphCostFunction, JGraphAlgebra)
	 */
	public JGraphModelFacade(GraphModel model, Object[] roots, boolean ignoresHiddenCells,
			boolean ignoresCellsInGroups, boolean ignoresUnconnectedCells, boolean directed) {
		this(model, roots, ignoresHiddenCells, ignoresCellsInGroups, ignoresUnconnectedCells, directed,
				new JGraphDistanceCostFunction(null), JGraphAlgebra.getSharedInstance());
	}

	/**
	 * Creates a JGraphGraphFacade specifying the graph passed in as the input
	 * graph. Also configures properties of layout, whether or not edge
	 * direction is to be taken into account, whether or not invisible cells are
	 * to be considered and whether or not only root cells are to be considered
	 * or roots and all their children. A root is only used if the isVertex
	 * method returns true.
	 * 
	 * @see #isVertex
	 * 
	 * @param model
	 *            The graph used as input to the layout
	 * @param roots
	 *            the root vertices to be used by tree layouts
	 * @param ignoresHiddenCells
	 * @see #ignoresHiddenCells
	 * @param ignoresCellsInGroups
	 * @see #ignoresCellsInGroups
	 * @param ignoresUnconnectedCells
	 * @see #ignoresUnconnectedCells
	 * @param directed
	 * @see #directed
	 * @param distanceCostFunction
	 *            the cost function that defines the distance metrics
	 * @param algebra
	 *            the algebra used for basic algorithms and functions
	 */
	public JGraphModelFacade(GraphModel model, Object[] roots, boolean ignoresHiddenCells,
			boolean ignoresCellsInGroups, boolean ignoresUnconnectedCells, boolean directed,
			JGraphCostFunction distanceCostFunction, JGraphAlgebra algebra) {
		super(model, roots, ignoresHiddenCells, ignoresCellsInGroups, ignoresUnconnectedCells, directed,
				distanceCostFunction, algebra);
	}

	/**
	 * A shortcut method that calls getNeighbours with no cells to exclude.
	 * 
	 * @see #getNeighbours(Object, Set, boolean)
	 */
	public List getNeighbours(Object cell, boolean ordered) {
		return getNeighbours(cell, null, ordered);
	}

	/**
	 * Returns a collection of cells that are connected to the specified cell by
	 * edges. Any cells specified in the exclude set will be ignored.
	 * 
	 * @param cell
	 *            The cell from which the neighbours will be determined
	 * @param exclude
	 *            The set of cells to ignore when searching
	 * @param ordered
	 *            whether or not to order the returned value in the order of the
	 *            current <code>order</code> comparator. <b>Be very careful</b>
	 *            using the default comparator on the default graph model,
	 *            <code>getIndexOfRoot</code> has linear performance and so
	 *            sorting the entire model roots will have quadratic
	 *            performance.
	 * @return Returns the set of neighbours for <code>cell</code>
	 */
	public List getNeighbours(Object cell, Set exclude, boolean ordered) {
		Object[] fanout = (directed) ? DefaultGraphModel.getOutgoingEdges(model, cell) : DefaultGraphModel.getEdges(
				model, new Object[] { cell }).toArray();
		List connectedCells = new ArrayList(fanout.length);
		Set localExclude = new HashSet(fanout.length + 8, (float) 0.75);
		for (int i = 0; i < fanout.length; i++) {
			Object neighbour = DefaultGraphModel.getOpposite(model, fanout[i], cell);

			if (neighbour != null && (exclude == null || !exclude.contains(neighbour))
					&& !localExclude.contains(neighbour)) {
				localExclude.add(neighbour);
				connectedCells.add(neighbour);
			}
		}
		if (ordered && order != null)
			Collections.sort(connectedCells, order);
		return connectedCells;
	}

	/**
	 * Returns the outgoing edges for cell. Cell should be a port or a vertex.
	 * 
	 * @param cell
	 *            The cell from which the outgoing edges will be determined
	 * @param exclude
	 *            The set of edges to ignore when searching
	 * @param visibleCells
	 *            whether or not only visible cells should be processed
	 * @param selfLoops
	 *            whether or not to include self loops in the returned list
	 * @return Returns the list of outgoing edges for <code>cell</code>
	 */
	public List getOutgoingEdges(Object cell, Set exclude, boolean visibleCells, boolean selfLoops) {
		Object[] edges = DefaultGraphModel.getEdges(model, cell, false);

		List edgeList = new ArrayList(edges.length);
		Set localExclude = new HashSet(edges.length);
		for (int i = 0; i < edges.length; i++) {
			// Check that the edge is neiter in the passed in exclude set or
			// the local exclude set. Also, if visibleCells is true check
			// the edge is visible in the cache.
			if ((exclude == null || !exclude.contains(edges[i])) && !localExclude.contains(edges[i])) {
				// Add the edge to the list if all edges, including self loops
				// are allowed. If self loops are not allowed, ensure the
				// source and target of the edge are different
				if (selfLoops == true || model.getSource(edges[i]) != model.getTarget(edges[i])) {
					edgeList.add(edges[i]);
				}
				localExclude.add(edges[i]);
			}
		}
		return edgeList;
	}

	/**
	 * Returns the incoming edges for cell. Cell should be a port or a vertex.
	 * 
	 * @param cell
	 *            The cell from which the incoming edges will be determined
	 * @param exclude
	 *            The set of edges to ignore when searching
	 * @param visibleCells
	 *            whether or not only visible cells should be processed
	 * @param selfLoops
	 *            whether or not to include self loops in the returned list
	 * @return Returns the list of incoming edges for <code>cell</code>
	 */
	public List getIncomingEdges(Object cell, Set exclude, boolean visibleCells, boolean selfLoops) {
		Object[] edges = DefaultGraphModel.getEdges(model, cell, true);

		List edgeList = new ArrayList(edges.length);
		Set localExclude = new HashSet(edges.length);
		for (int i = 0; i < edges.length; i++) {
			// Check that the edge is neiter in the passed in exclude set or
			// the local exclude set. Also, if visibleCells is true check
			// the edge is visible in the cache.
			if ((exclude == null || !exclude.contains(edges[i])) && !localExclude.contains(edges[i])) {
				// Add the edge to the list if all edges, including self loops
				// are allowed. If self loops are not allowed, ensure the
				// source and target of the edge are different
				if (selfLoops == true || model.getSource(edges[i]) != model.getTarget(edges[i])) {
					edgeList.add(edges[i]);
				}
				localExclude.add(edges[i]);
			}
		}
		return edgeList;
	}

	/**
	 * Returns the minimal rectangular bounds that enclose all the elements in
	 * the <code>bounds</code> map. After a layout has completed this method
	 * will return the collective bounds of the new laid out graph.
	 * 
	 * @return <code>null</code>
	 */
	public Rectangle2D getGraphBounds() {
		return getCellBounds();
	}
}
