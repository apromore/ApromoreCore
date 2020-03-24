/*
 * $Id: JGraphAlgebra.java,v 1.1 2009/09/25 15:14:15 david Exp $
 * Copyright (c) 2001-2005, Gaudenz Alder
 * 
 * All rights reserved. 
 * 
 * This file is licensed under the JGraph software license, a copy of which
 * will have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please
 * contact JGraph sales for another copy.
 */
package org.apromore.jgraph.algebra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apromore.jgraph.algebra.cost.JGraphCostFunction;
import org.apromore.jgraph.graph.DefaultGraphModel;
import org.apromore.jgraph.graph.GraphModel;

/**
 * A singleton class that provides algorithms for graphs. Assume the following
 * variable for the following examples: <br>
 * JGraphDistanceCostFunction(graph.getGraphLayoutCache()); <br>
 * JGraphFacade facade = new JGraphFacade(graph); <br>
 * Object[] v = facade.getVertices().toArray(); <br>
 * Object[] e = facade.getEdges().toArray(); <br>
 * JGraphAlgebra alg = JGraphAlgebra.getSharedInstance(); <br>
 * 
 * <h3>Shortest Path (Dijkstra)</h3>
 * 
 * For example, to find the shortest path between the first and the second
 * selected cell in a graph use the following code: <br>
 * <br>
 * <code>Object[] path = alg.getShortestPath(graph.getModel(), sourceVertex,
 * targetVertex, cf, v.length, true)</code>
 * 
 * <h3>Minimum Spanning Tree</h3>
 * 
 * This algorithm finds the set of edges with the minimal length that connect
 * all vertices. This algorithm can be used as follows:
 * <h5>Prim</h5>
 * <code>alg.getMinimumSpanningTree(graph.getModel(), v, cf, true))</code>
 * <h5>Kruskal</h5>
 * <code>alg.getMinimumSpanningTree(graph.getModel(), v, e, cf))</code>
 * 
 * <h3>Connection Components</h3>
 * 
 * The union find may be used as follows to determine whether two cells are
 * connected: <code>boolean connected = uf.differ(vertex1, vertex2)</code>.
 * 
 * @see JGraphCostFunction
 */
public class JGraphAlgebra {

	/**
	 * Holds the shared instance of this class.
	 */
	protected static JGraphAlgebra sharedInstance = new JGraphAlgebra();

	/**
	 * @return Returns the sharedInstance.
	 */
	public static JGraphAlgebra getSharedInstance() {
		return sharedInstance;
	}

	/**
	 * Sets the shared instance of this class.
	 * 
	 * @param sharedInstance
	 *            The sharedInstance to set.
	 */
	public static void setSharedInstance(JGraphAlgebra sharedInstance) {
		JGraphAlgebra.sharedInstance = sharedInstance;
	}

	/**
	 * Subclassers may override to provide special union find and priority queue
	 * datastructures.
	 */
	protected JGraphAlgebra() {
		// empty
	}

	/**
	 * Returns the shortest path between two cells or their descendants
	 * represented as an array of edges in order of traversal. <br>
	 * This implementation is based on the Dijkstra algorithm.
	 * 
	 * @param model
	 *            the model that defines the graph structure
	 * @param from
	 *            the source port or vertex
	 * @param to
	 *            the target port or vertex (aka. sink)
	 * @param cf
	 *            the cost function that defines the edge length
	 * @param steps
	 *            the maximum number of edges to traverse
	 * @param directed
	 *            if edge directions should be taken into account
	 * 
	 * @return Returns the shortest path as an array of edges
	 * 
	 * @see #createPriorityQueue()
	 */
	public Object[] getShortestPath(GraphModel model, Object from, Object to,
			JGraphCostFunction cf, int steps, boolean directed) {

		// Sets up a pqueue and a hashtable to store the predecessor for each
		// cell in tha graph traversal. The pqueue is initialized
		// with the from element at prio 0.
		JGraphFibonacciHeap q = createPriorityQueue();
		Hashtable pred = new Hashtable();
		q.decreaseKey(q.getNode(from, true), 0); // Inserts automatically

		// The main loop of the dijkstra algorithm is based on the pqueue being
		// updated with the actual shortest distance to the source vertex.
		for (int j = 0; j < steps; j++) {
			JGraphFibonacciHeap.Node node = q.removeMin();
			double prio = node.getKey();
			Object obj = node.getUserObject();

			// Exits the loop if the target node or vertex has been reached
			if (obj == to)
				break;

			// Gets all outgoing edges of the closest cell to the source
			Object[] e = (directed) ? DefaultGraphModel.getOutgoingEdges(model,
					obj) : DefaultGraphModel.getEdges(model,
					new Object[] { obj }).toArray();
			if (e != null) {
				for (int i = 0; i < e.length; i++) {
					Object neighbour = DefaultGraphModel.getOpposite(model,
							e[i], obj);

					// Updates the priority in the pqueue for the opposite node
					// to be the distance of this step plus the cost to
					// traverese the edge to the neighbour. Note that the
					// priority queue will make sure that in the next step the
					// node with the smallest prio will be traversed.
					if (neighbour != null && neighbour != obj
							&& neighbour != from) {
						double newPrio = prio
								+ ((cf != null) ? cf.getCost(e[i]) : 1);
						node = q.getNode(neighbour, true);
						double oldPrio = node.getKey();
						if (newPrio < oldPrio) {
							pred.put(neighbour, e[i]);
							q.decreaseKey(node, newPrio);
						}
					}
				}
			}
			if (q.isEmpty())
				break;
		}

		// Constructs a path array by walking backwards through the predessecor
		// map and filling up a list of edges, which is subsequently returned.
		ArrayList list = new ArrayList(steps);
		Object obj = to;
		Object edge = pred.get(obj);
		while (edge != null) {
			list.add(0, edge);
			obj = DefaultGraphModel.getOpposite(model, edge, obj);
			edge = pred.get(obj);
			// System.out.println("edge="+edge+" obj="+obj);
		}
		return list.toArray();
	}

	/**
	 * Returns the minimum spanning tree (MST) for the graph defined by G=(E,V).
	 * The MST is defined as the set of all vertices with minimal lengths that
	 * forms no cycles in G.<br>
	 * This implementation is based on the algorihm by Prim-Jarnik. It uses
	 * O(|E|+|V|log|V|) time when used with a Fibonacci heap and a graph whith a
	 * double linked-list datastructure, as is the case with the default
	 * implementation.
	 * 
	 * @param model
	 *            the model that describes the graph
	 * @param v
	 *            the vertices of the graph
	 * @param cf
	 *            the cost function that defines the edge length
	 * 
	 * @return Returns the MST as an array of edges
	 * 
	 * @see #createPriorityQueue()
	 */
	public Object[] getMinimumSpanningTree(GraphModel model, Object[] v,
			JGraphCostFunction cf, boolean directed) {
		ArrayList mst = new ArrayList(v.length);

		// Sets up a pqueue and a hashtable to store the predecessor for each
		// cell in tha graph traversal. The pqueue is initialized
		// with the from element at prio 0.
		JGraphFibonacciHeap q = createPriorityQueue();
		Hashtable pred = new Hashtable();
		Object u = v[0];
		q.decreaseKey(q.getNode(u, true), 0);
		for (int i = 1; i < v.length; i++)
			q.getNode(v[i], true);

		// The main loop of the dijkstra algorithm is based on the pqueue being
		// updated with the actual shortest distance to the source vertex.
		while (!q.isEmpty()) {
			JGraphFibonacciHeap.Node node = q.removeMin();
			u = node.getUserObject();
			Object edge = pred.get(u);
			if (edge != null)
				mst.add(edge);

			// Gets all outgoing edges of the closest cell to the source
			Object[] e = (directed) ? DefaultGraphModel.getOutgoingEdges(model,
					u) : DefaultGraphModel.getEdges(model, new Object[] { u })
					.toArray();
			if (e != null) {
				for (int i = 0; i < e.length; i++) {
					Object neighbour = DefaultGraphModel.getOpposite(model,
							e[i], u);

					// Updates the priority in the pqueue for the opposite node
					// to be the distance of this step plus the cost to
					// traverese the edge to the neighbour. Note that the
					// priority queue will make sure that in the next step the
					// node with the smallest prio will be traversed.
					if (neighbour != null && neighbour != u) {
						node = q.getNode(neighbour, false);
						if (node != null) {
							double newPrio = cf.getCost(e[i]);
							double oldPrio = node.getKey();
							if (newPrio < oldPrio) {
								pred.put(neighbour, e[i]);
								q.decreaseKey(node, newPrio);
							}
						}
					}
				}
			}
		}
		return mst.toArray();
	}

	/**
	 * Returns the minimum spanning tree (MST) for the graph defined by G=(E,V).
	 * The MST is defined as the set of all vertices with minimal lenths that
	 * forms no cycles in G.<br>
	 * This implementation is based on the algorihm by Kruskal. It uses
	 * O(|E|log|E|)=O(|E|log|V|) time for sorting the edges, O(|V|) create sets,
	 * O(|E|) find and O(|V|) union calls on the union find structure, thus
	 * yielding no more than O(|E|log|V|) steps. For a faster implementatin
	 * 
	 * @see #getMinimumSpanningTree(GraphModel, Object[], JGraphCostFunction,
	 *      boolean)
	 * 
	 * @param model
	 *            the model that describes the graph
	 * @param v
	 *            the vertices of the graph
	 * @param e
	 *            the edges of the graph
	 * @param cf
	 *            the cost function that defines the edge length
	 * 
	 * @return Returns the MST as an array of edges
	 * 
	 * @see #createUnionFind(Object[])
	 */
	public Object[] getMinimumSpanningTree(GraphModel model, Object[] v,
			Object[] e, JGraphCostFunction cf) {

		// Sorts all edges according to their lengths, then creates a union
		// find structure for all vertices. Then walks through all edges by
		// increasing length and tries adding to the MST. Only edges are added
		// that do not form cycles in the graph, that is, where the source
		// and target are in different sets in the union find structure.
		// Whenever an edge is added to the MST, the two different sets are
		// unified.
		JGraphUnionFind uf = createUnionFind(v);
		Iterator it = sort(e, cf).iterator();
		ArrayList result = new ArrayList(e.length);
		while (it.hasNext()) {
			Object edge = it.next();
			Object source = DefaultGraphModel.getSourceVertex(model, edge);
			Object target = DefaultGraphModel.getTargetVertex(model, edge);
			JGraphUnionFind.Node setA = uf.find(uf.getNode(source));
			JGraphUnionFind.Node setB = uf.find(uf.getNode(target));
			if (setA == null || setB == null || setA != setB) {
				uf.union(setA, setB);
				result.add(edge);
			}
		}
		return result.toArray();
	}

	/**
	 * Returns a union find structure representing the connection components of
	 * G=(E,V).
	 * 
	 * @param model
	 *            the model that describes the graph
	 * @param v
	 *            the vertices of the graph
	 * @param e
	 *            the edges of the graph
	 * 
	 * @return Returns the connection components in G=(E,V)
	 * 
	 * @see #createUnionFind(Object[])
	 */
	public JGraphUnionFind getConnectionComponents(GraphModel model,
			Object[] v, Object[] e) {
		JGraphUnionFind uf = createUnionFind(v);
		for (int i = 0; i < e.length; i++) {
			Object source = DefaultGraphModel.getSourceVertex(model, e[i]);
			Object target = DefaultGraphModel.getTargetVertex(model, e[i]);
			uf.union(uf.find(uf.getNode(source)), uf.find(uf.getNode(target)));
		}
		return uf;
	}

	/**
	 * Returns a sorted set for <code>cells</code> with respect to
	 * <code>cf</code>.
	 * 
	 * @param cells
	 *            the cells to sort
	 * @param cf
	 *            the cost function that defines the order
	 * 
	 * @return Returns an ordered set of <code>cells</code> wrt.
	 *         <code>cf</code>
	 */
	public List sort(Object[] cells, final JGraphCostFunction cf) {
		List result = Arrays.asList(cells);
		Collections.sort(result, new Comparator() {

			public int compare(Object o1, Object o2) {
				Double d1 = new Double(cf.getCost(o1));
				Double d2 = new Double(cf.getCost(o2));
				return d1.compareTo(d2);
			}
		});
		return result;
	}

	/**
	 * Returns the sum of all cost for <code>cells</code> with respect to
	 * <code>cf</code>.
	 * 
	 * @param cells
	 *            the cells to use for the sum
	 * @param cf
	 *            the cost function that defines the costs
	 * 
	 * @return Returns the sum of all cell cost
	 */
	public double sum(Object[] cells, JGraphCostFunction cf) {
		double cost = 0;
		for (int i = 0; i < cells.length; i++)
			cost += cf.getCost(cells[i]);
		return cost;
	}

	/**
	 * Hook for subclassers to provide a custom union find structure.
	 * 
	 * @param v
	 *            the array of all elements
	 * 
	 * @return Returns a union find structure for <code>v</code>
	 */
	protected JGraphUnionFind createUnionFind(Object[] v) {
		return new JGraphUnionFind(v);
	}

	/**
	 * Hook for subclassers to provide a custom fibonacci heap.
	 */
	protected JGraphFibonacciHeap createPriorityQueue() {
		return new JGraphFibonacciHeap();
	}

}
