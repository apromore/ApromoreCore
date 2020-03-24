/*
 * Copyright (c) 2005-2006, David Benson
 * 
 * All rights reserved.
 * 
 * This file is licensed under the JGraph software license, a copy of which will
 * have been provided to you in the file LICENSE at the root of your
 * installation directory. If you are unable to locate this file please contact
 * JGraph sales for another copy.
 */
package org.apromore.jgraph.layout.hierarchical.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphFacade.CellVisitor;

/**
 * Internal model of a hierarchical graph. This model stores nodes and edges
 * equivalent to the real graph nodes and edges, but also stores the rank of the
 * cells, the order within the ranks and the new candidate locations of cells.
 * The internal model also reverses edge direction were appropriate , ignores
 * self-loop and groups parallels together under one edge object.
 */
public class JGraphHierarchyModel {

	/**
	 * Whether the rank assignment is done from the sinks or sources.
	 */
	protected boolean scanRanksFromSinks = false;

	/**
	 * Stores the largest rank number allocated
	 */
	public int maxRank;

	/**
	 * Map from graph vertices to internal model nodes
	 */
	protected Map vertexMapper = null;

	/**
	 * Map from graph edges to internal model edges
	 */
	protected Map edgeMapper = null;

	/**
	 * Mapping from rank number to actual rank
	 */
	public Map ranks = null;

	/**
	 * Store of roots of this hierarchy model, these are real graph cells, not
	 * internal cells
	 */
	public Object[] roots = null;

	/**
	 * Count of the number of times the ancestor dfs has been used
	 */
	protected int dfsCount = 0;

	/**
	 * Whether or not cells are ordered according to the order in the graph
	 * model. Defaults to false since sorting usually produces quadratic
	 * performance. Note that since JGraph 6 returns edges in a deterministic
	 * order, it might be that this layout is always deterministic using that
	 * JGraph regardless of this flag setting (i.e. leave it false in that case)
	 */
	protected boolean deterministic = false;

	/** High value to start source layering scan rank value from */
	private final int SOURCESCANSTARTRANK = 100000000;

	/**
	 * Constructor with no parameters creates a default model
	 * 
	 * @param facade
	 *            the facade of the graph to be laid out
	 */
	public JGraphHierarchyModel(JGraphFacade facade) {
		this(facade, facade.getVertices().toArray(), false, false, true);
	}

	/**
	 * Creates an internal ordered graph model using the vertices passed in. If
	 * there are any, leftward edge need to be inverted in the internal model
	 * 
	 * @param facade
	 *            the facade describing the graph to be operated on
	 * @param vertices
	 *            the vertices for this hierarchy
	 * @param ordered
	 *            whether or not the vertices are already ordered
	 * @param deterministic
	 *            whether or not this layout should be deterministic on each
	 *            usage
	 * @param scanRanksFromSinks
	 *            Whether the rank assignment is done from the sinks or sources.
	 */
	public JGraphHierarchyModel(JGraphFacade facade, Object[] vertices,
			boolean ordered, boolean deterministic, boolean scanRanksFromSinks) {
		this.deterministic = deterministic;
		this.scanRanksFromSinks = scanRanksFromSinks;
		roots = facade.getRoots().toArray();
		if (ordered) {
			formOrderedHierarchy(facade, vertices);
		} else {
			if (vertices == null) {
				vertices = facade.getVertices().toArray();
			}
			// map of cells to internal cell needed for second run through
			// to setup the sink of edges correctly. Guess size by number
			// of edges is roughly same as number of vertices.
			vertexMapper = new Hashtable(vertices.length);
			edgeMapper = new Hashtable(vertices.length);
			if (scanRanksFromSinks) {
				maxRank = 0;
			} else {
				maxRank = SOURCESCANSTARTRANK;
			}
			JGraphHierarchyNode[] internalVertices = new JGraphHierarchyNode[vertices.length];
			createInternalCells(facade, vertices, internalVertices);
			// Go through edges set their sink values. Also check the
			// ordering if and invert edges if necessary
			for (int i = 0; i < vertices.length; i++) {
				Collection edges = internalVertices[i].connectsAsSource;
				Iterator iter = edges.iterator();
				while (iter.hasNext()) {
					JGraphHierarchyEdge internalEdge = (JGraphHierarchyEdge) iter
							.next();
					Collection realEdges = internalEdge.edges;
					Iterator iter2 = realEdges.iterator();
					if (iter2.hasNext()) {
						Object realEdge = iter2.next();
						Object targetCell = facade.getTarget(realEdge);
						JGraphHierarchyNode internalTargetCell = (JGraphHierarchyNode) vertexMapper
								.get(targetCell);
						if (internalTargetCell != null
								&& internalVertices[i] != internalTargetCell) {
							internalEdge.target = internalTargetCell;
							if (internalTargetCell.connectsAsTarget.size() == 0) {
								internalTargetCell.connectsAsTarget = new LinkedHashSet(
										4);
							}

							internalTargetCell.connectsAsTarget
									.add(internalEdge);
						}
					}
				}
				// Use the temp variable in the internal nodes to mark this
				// internal vertex as having been visited.
				internalVertices[i].temp[0] = 1;
			}
		}
	}

	/**
	 * Creates an internal ordered graph model using the vertices passed in. If
	 * there are any, leftward edge need to be inverted in the internal model
	 * 
	 * @param facade
	 *            the facade describing the graph to be operated on
	 * @param vertices
	 *            the vertices to be laid out
	 */
	public void formOrderedHierarchy(JGraphFacade facade, Object[] vertices) {
		if (vertices == null) {
			vertices = facade.getVertices().toArray();
		}
		// map of cells to internal cell needed for second run through
		// to setup the sink of edges correctly. Guess size by number
		// of edges is roughly same as number of vertices.
		vertexMapper = new Hashtable(vertices.length * 2);
		edgeMapper = new Hashtable(vertices.length);
		maxRank = 0;
		JGraphHierarchyNode[] internalVertices = new JGraphHierarchyNode[vertices.length];
		createInternalCells(facade, vertices, internalVertices);
		// Go through edges set their sink values. Also check the
		// ordering if and invert edges if necessary
		// Need a temporary list to store which of these edges have been
		// inverted in the internal model. If connectsAsSource were changed
		// in the following while loop we'd get a
		// ConcurrentModificationException
		List tempList = new ArrayList();
		for (int i = 0; i < vertices.length; i++) {
			Collection edges = internalVertices[i].connectsAsSource;
			Iterator iter = edges.iterator();
			while (iter.hasNext()) {
				JGraphHierarchyEdge internalEdge = (JGraphHierarchyEdge) iter
						.next();
				Collection realEdges = internalEdge.edges;
				Iterator iter2 = realEdges.iterator();
				if (iter2.hasNext()) {
					Object realEdge = iter2.next();
					Object targetCell = facade.getTarget(realEdge);
					JGraphHierarchyNode internalTargetCell = (JGraphHierarchyNode) vertexMapper
							.get(targetCell);
					if (internalTargetCell != null
							&& internalVertices[i] != internalTargetCell) {
						internalEdge.target = internalTargetCell;
						if (internalTargetCell.connectsAsTarget.size() == 0) {
							internalTargetCell.connectsAsTarget = new ArrayList(
									4);
						}

						// The vertices passed in were ordered, check that the
						// target cell has not already been marked as visited
						if (internalTargetCell.temp[0] == 1) {
							// Internal Edge is leftward, reverse it
							internalEdge.invert();
							// There must be a connectsAsSource list already
							internalTargetCell.connectsAsSource
									.add(internalEdge);
							tempList.add(internalEdge);
							internalVertices[i].connectsAsTarget
									.add(internalEdge);
						} else {
							internalTargetCell.connectsAsTarget
									.add(internalEdge);
						}
					}
				}
			}
			// Remove the inverted edges as sources from this node
			Iterator iter2 = tempList.iterator();
			while (iter2.hasNext()) {
				internalVertices[i].connectsAsSource.remove(iter2.next());
			}
			tempList.clear();

			// Use the temp variable in the internal nodes to mark this
			// internal vertex as having been visited.
			internalVertices[i].temp[0] = 1;
		}
	}

	/**
	 * Creates all edges in the internal model
	 * 
	 * @param facade
	 *            the facade desrcibing the graph to be laid out
	 * @param vertices
	 *            the vertices whom are to have an internal representation
	 *            created
	 * @param internalVertices
	 *            the blank internal vertices to have their information filled
	 *            in using the real vertices
	 */
	protected void createInternalCells(JGraphFacade facade, Object[] vertices,
			JGraphHierarchyNode[] internalVertices) {
		// Create internal edges
		for (int i = 0; i < vertices.length; i++) {
			internalVertices[i] = new JGraphHierarchyNode(vertices[i]);
			vertexMapper.put(vertices[i], internalVertices[i]);

			// If the layout is deterministic, order the cells
			List outgoingCells = facade.getNeighbours(vertices[i], null,
					deterministic, true);
			internalVertices[i].connectsAsSource = new LinkedHashSet(
					outgoingCells.size());
			// Create internal edges, but don't do any rank assignment yet
			// First use the information from the greedy cycle remover to
			// invert the leftward edges internally
			Iterator iter = outgoingCells.iterator();
			while (iter.hasNext()) {
				// Don't add self-loops
				Object cell = iter.next();
				if (cell != vertices[i] && facade.isVertex(cell)) {
					// Allow for parallel edges
					Object[] edges = facade.getEdgesBetween(vertices[i], cell,
							true);
					if (edges != null && edges.length > 0) {
						ArrayList listEdges = new ArrayList(edges.length);
						for (int j = 0; j < edges.length; j++) {
							listEdges.add(edges[j]);
						}
						JGraphHierarchyEdge internalEdge = new JGraphHierarchyEdge(
								listEdges);
						Iterator iter2 = listEdges.iterator();
						while (iter2.hasNext()) {
							edgeMapper.put(iter2.next(), internalEdge);
						}
						internalEdge.source = internalVertices[i];
						internalVertices[i].connectsAsSource.add(internalEdge);
					}
				}
			}
			// Ensure temp variable is cleared from any previous use
			internalVertices[i].temp[0] = 0;
		}
	}

	/**
	 * Basic determination of minimum layer ranking by working from from sources
	 * or sinks and working through each node in the relevant edge direction.
	 * Starting at the sinks is basically a longest path layering algorithm.
	 * 
	 */
	public void initialRank() {
		Collection internalNodes = vertexMapper.values();
		LinkedList startNodes = new LinkedList();
		if (!scanRanksFromSinks && roots != null) {
			for (int i = 0; i < roots.length; i++) {
				Object internalNode = vertexMapper.get(roots[i]);
				if (internalNode != null) {
					startNodes.add(internalNode);
				}
			}
		}

		if (scanRanksFromSinks) {
			Iterator iter = internalNodes.iterator();
			while (iter.hasNext()) {
				JGraphHierarchyNode internalNode = (JGraphHierarchyNode) iter
						.next();
				if (internalNode.connectsAsSource == null
						|| internalNode.connectsAsSource.isEmpty()) {
					startNodes.add(internalNode);
				}
			}
		}
		if (startNodes.isEmpty()) {
			// Start list from sources
			Iterator iter = internalNodes.iterator();
			while (iter.hasNext()) {
				JGraphHierarchyNode internalNode = (JGraphHierarchyNode) iter
						.next();
				if (internalNode.connectsAsTarget == null
						|| internalNode.connectsAsTarget.isEmpty()) {
					startNodes.add(internalNode);
				}
			}
		}
		Iterator iter = internalNodes.iterator();
		while (iter.hasNext()) {
			JGraphHierarchyNode internalNode = (JGraphHierarchyNode) iter
					.next();
			// Mark the node as not having had a layer assigned
			internalNode.temp[0] = -1;
		}

		List startNodesCopy = new ArrayList(startNodes);
		while (!startNodes.isEmpty()) {
			JGraphHierarchyNode internalNode = (JGraphHierarchyNode) startNodes
					.getFirst();
			Collection layerDeterminingEdges;
			Collection edgesToBeMarked;
			if (scanRanksFromSinks) {
				layerDeterminingEdges = internalNode.connectsAsSource;
				edgesToBeMarked = internalNode.connectsAsTarget;
			} else {
				layerDeterminingEdges = internalNode.connectsAsTarget;
				edgesToBeMarked = internalNode.connectsAsSource;
			}

			// flag to keep track of whether or not all layer determining
			// edges have been scanned
			boolean allEdgesScanned = true;
			// Work out the layer of this node from the layer determining
			// edges
			Iterator iter2 = layerDeterminingEdges.iterator();
			// The minimum layer number of any node connected by one of
			// the layer determining edges variable. If we are starting
			// from sources, need to start at some huge value and
			// normalise down afterwards
			int minimumLayer = 0;
			if (!scanRanksFromSinks) {
				minimumLayer = SOURCESCANSTARTRANK;
			}
			while (allEdgesScanned && iter2.hasNext()) {
				JGraphHierarchyEdge internalEdge = (JGraphHierarchyEdge) iter2
						.next();
				if (internalEdge.temp[0] == 5270620) {
					// This edge has been scanned, get the layer of the
					// node on the other end
					JGraphHierarchyNode otherNode;
					if (scanRanksFromSinks) {
						otherNode = internalEdge.target;
					} else {
						otherNode = internalEdge.source;
					}
					if (scanRanksFromSinks) {
						minimumLayer = Math.max(minimumLayer,
								otherNode.temp[0] + 1);
					} else {
						minimumLayer = Math.min(minimumLayer,
								otherNode.temp[0] - 1);
					}
				} else {
					allEdgesScanned = false;
				}
			}
			// If all edge have been scanned, assign the layer, mark all
			// edges in the other direction and remove from the nodes list
			if (allEdgesScanned) {
				internalNode.temp[0] = minimumLayer;
				if (scanRanksFromSinks) {
					maxRank = Math.max(maxRank, minimumLayer);
				} else {
					maxRank = Math.min(maxRank, minimumLayer);
				}
				if (edgesToBeMarked != null) {
					Iterator iter3 = edgesToBeMarked.iterator();
					while (iter3.hasNext()) {
						JGraphHierarchyEdge internalEdge = (JGraphHierarchyEdge) iter3
								.next();
						// Assign unique stamp ( y/m/d/h )
						internalEdge.temp[0] = 5270620;
						// Add node on other end of edge to LinkedList of
						// nodes
						// to be analysed
						JGraphHierarchyNode otherNode;
						if (scanRanksFromSinks) {
							otherNode = internalEdge.source;
						} else {
							otherNode = internalEdge.target;
						}
						// Only add node if it hasn't been assigned a layer
						if (otherNode.temp[0] == -1) {
							startNodes.addLast(otherNode);
							// Mark this other node as neither being
							// unassigned nor assigned so it isn't
							// added to this list again, but it's
							// layer isn't used in any calculation.
							otherNode.temp[0] = -2;
						}
					}
				}
				startNodes.removeFirst();
			} else {
				// Not all the edges have been scanned, get to the back of
				// the class and put the dunces cap on
				Object removedCell = startNodes.removeFirst();
				startNodes.addLast(internalNode);
				if (removedCell == internalNode && startNodes.size() == 1) {
					// This is an error condition, we can't get out of
					// this loop. It could happen for more than one node
					// but that's a lot harder to detect. Log the error
					// TODO make log comment
					break;
				}
			}
		}

		if (scanRanksFromSinks) {
			// Tighten the rank 0 nodes as far as possible
			for (int i = 0; i < startNodesCopy.size(); i++) {
				JGraphHierarchyNode internalNode = (JGraphHierarchyNode) startNodesCopy
						.get(i);
				int currentMinLayer = 1000000;
				Collection layerDeterminingEdges = internalNode.connectsAsTarget;
				Iterator iter2 = layerDeterminingEdges.iterator();
				while (iter2.hasNext()) {
					JGraphHierarchyEdge internalEdge = (JGraphHierarchyEdge) iter2
							.next();
					JGraphHierarchyNode otherNode = internalEdge.source;
					internalNode.temp[0] = Math.min(currentMinLayer,
							otherNode.temp[0] - 1);
					currentMinLayer = internalNode.temp[0];
				}
			}
		} else {
			// Normalize the ranks down from their large starting value to place
			// at least 1 sink on layer 0
			iter = internalNodes.iterator();
			while (iter.hasNext()) {
				JGraphHierarchyNode internalNode = (JGraphHierarchyNode) iter
						.next();
				// Mark the node as not having had a layer assigned
				internalNode.temp[0] -= maxRank;
			}
			// Reset the maxRank to that which would be expected for a from-sink
			// scan
			maxRank = SOURCESCANSTARTRANK - maxRank;
		}
	}

	/**
	 * Fixes the layer assignments to the values stored in the nodes. Also needs
	 * to create dummy nodes for edges that cross layers.
	 */
	public void fixRanks() {
		final Collection[] rankList = new JGraphHierarchyRank[maxRank + 1];
		ranks = new LinkedHashMap(maxRank + 1);
		for (int i = 0; i < maxRank + 1; i++) {
			rankList[i] = new JGraphHierarchyRank();
			ranks.put(new Integer(i), rankList[i]);
		}
		// Perform a DFS to obtain an initial ordering for each rank.
		// Without doing this you would end up having to process
		// crossings for a standard tree.
		Object rootsArray[] = null;
		if (roots != null) {
			rootsArray = new Object[roots.length];
			for (int i = 0; i < roots.length; i++) {
				Object node = roots[i];
				JGraphHierarchyNode internalNode = (JGraphHierarchyNode) vertexMapper
						.get(node);
				rootsArray[i] = internalNode;
			}
		}
		dfs(new JGraphFacade.CellVisitor() {
			public void visit(Object parent, Object cell,
					Object connectingEdge, int layer, int seen) {
				JGraphHierarchyNode node = (JGraphHierarchyNode) cell;
				if (seen == 0 && node.maxRank < 0 && node.minRank < 0) {
					rankList[node.temp[0]].add(cell);
					node.maxRank = node.temp[0];
					node.minRank = node.temp[0];
					// Set temp[0] to the nodes position in the rank
					node.temp[0] = rankList[node.maxRank].size() - 1;
				}
				if (parent != null && connectingEdge != null) {
					int parentToCellRankDifference = ((JGraphHierarchyNode) parent).maxRank
							- node.maxRank;
					if (parentToCellRankDifference > 1) {
						// There are ranks in between the parent and current
						// cell
						JGraphHierarchyEdge edge = (JGraphHierarchyEdge) connectingEdge;
						edge.maxRank = ((JGraphHierarchyNode) parent).maxRank;
						edge.minRank = ((JGraphHierarchyNode) cell).maxRank;
						edge.temp = new int[parentToCellRankDifference - 1];
						edge.x = new double[parentToCellRankDifference - 1];
						edge.y = new double[parentToCellRankDifference - 1];
						for (int i = edge.minRank + 1; i < edge.maxRank; i++) {
							// The connecting edge must be added to the
							// appropriate
							// ranks
							rankList[i].add(edge);
							edge.setGeneralPurposeVariable(i,
									rankList[i].size() - 1);
						}
					}
				}
			}
		}, rootsArray, false, null);
	}

	/**
	 * A depth first search through the internal heirarchy model
	 * 
	 * @param visitor
	 *            the visitor pattern to be called for each node
	 * @param trackAncestors
	 *            whether or not the search is to keep track all nodes directly
	 *            above this one in the search path
	 */
	public void dfs(CellVisitor visitor, Object[] dfsRoots,
			boolean trackAncestors, Set seenNodes) {
		// Run dfs through on all roots
		if (dfsRoots != null) {
			for (int i = 0; i < dfsRoots.length; i++) {
				JGraphHierarchyNode internalNode = (JGraphHierarchyNode) dfsRoots[i];
				if (internalNode != null) {
					if (seenNodes == null) {
						seenNodes = new HashSet();
					}
					if (trackAncestors) {
						// Set up hash code for root
						internalNode.hashCode = new int[2];
						internalNode.hashCode[0] = dfsCount;
						internalNode.hashCode[1] = i;
						dfs(null, internalNode, null, visitor, seenNodes,
								internalNode.hashCode, i, 0);
					} else {
						dfs(null, internalNode, null, visitor, seenNodes, 0);
					}
				}
			}
			dfsCount++;
		}
	}

	/**
	 * Performs a depth first search on the internal hierarchy model
	 * 
	 * @param parent
	 *            the parent internal node of the current internal node
	 * @param root
	 *            the current internal node
	 * @param connectingEdge
	 *            the internal edge connecting the internal node and the parent
	 *            internal node, if any
	 * @param visitor
	 *            the visitor pattern to be called for each node
	 * @param seen
	 *            a set of all nodes seen by this dfs a set of all of the
	 *            ancestor node of the current node
	 * @param layer
	 *            the layer on the dfs tree ( not the same as the model ranks )
	 */
	public void dfs(JGraphHierarchyNode parent, JGraphHierarchyNode root,
			JGraphHierarchyEdge connectingEdge, CellVisitor visitor, Set seen,
			int layer) {
		if (root != null) {
			if (!seen.contains(root)) {
				visitor.visit(parent, root, connectingEdge, layer, 0);
				seen.add(root);
				// Copy the connects as source list so that visitors
				// can change the original for edge direction inversions
				final Object[] outgoingEdges = root.connectsAsSource.toArray();
				for (int i = 0; i < outgoingEdges.length; i++) {
					JGraphHierarchyEdge internalEdge = (JGraphHierarchyEdge) outgoingEdges[i];
					JGraphHierarchyNode targetNode = internalEdge.target;
					// Root check is O(|roots|)
					dfs(root, targetNode, internalEdge, visitor, seen,
							layer + 1);
				}
			} else {
				// Use the int field to indicate this node has been seen
				visitor.visit(parent, root, connectingEdge, layer, 1);
			}
		}
	}

	/**
	 * Performs a depth first search on the internal hierarchy model. This dfs
	 * extends the default version by keeping track of cells ancestors, but it
	 * should be only used when necessary because of it can be computationally
	 * intensive for deep searches.
	 * 
	 * @param parent
	 *            the parent internal node of the current internal node
	 * @param root
	 *            the current internal node
	 * @param connectingEdge
	 *            the internal edge connecting the internal node and the parent
	 *            internal node, if any
	 * @param visitor
	 *            the visitor pattern to be called for each node
	 * @param seen
	 *            a set of all nodes seen by this dfs
	 * @param ancestors
	 *            the parent hash code
	 * @param childHash
	 *            the new hash code for this node
	 * @param layer
	 *            the layer on the dfs tree ( not the same as the model ranks )
	 */
	public void dfs(JGraphHierarchyNode parent, JGraphHierarchyNode root,
			JGraphHierarchyEdge connectingEdge, CellVisitor visitor, Set seen,
			int[] ancestors, int childHash, int layer) {
		// Explaination of custom hash set.
		// Previously, the ancestors variable was passed through the dfs as a
		// HashSet.
		// The ancestors were copied into a new HashSet and when the new child
		// was
		// processed it was also added to the set. If the current node was in
		// its
		// ancestor list it meant there is a cycle in the graph and this
		// information is passed to the visitor.visit() in the seen parameter.
		// The HashSet clone was very expensive on CPU so a custom hash was
		// developed using primitive types. temp[] couldn't be used so
		// hashCode[] was
		// added to each node. Each new child adds another int to the array,
		// copying
		// the prefix from its parent. Child of the same parent add different
		// ints
		// (the limit is therefore 2^32 children per parent...). If a node has a
		// child with the hashCode already set then the child code is compared
		// to the
		// same portion of the current nodes array. If they match there is a
		// loop.
		// Note that the basic mechanism would only allow for 1 use of this
		// functionality, so the root nodes have two ints. The second int is
		// incremented
		// through each node root and the first is incremented through each run
		// of the
		// dfs algorithmn (therefore the dfs is not thread safe). The hash code
		// of each
		// node is set if not already set, or if the first int does not match
		// that of
		// the current run.
		if (root != null) {
			if (parent != null) {
				// Form this nodes hash code if necessary, that is, if the
				// hashCode variable has not been initialised or if the
				// start of the parent hash code does not equal the start of
				// this nodes hash code, indicating the code was set on a
				// previous run of this dfs.
				if (root.hashCode == null
						|| root.hashCode[0] != parent.hashCode[0]) {
					int hashCodeLength = parent.hashCode.length + 1;
					root.hashCode = new int[hashCodeLength];
					System.arraycopy(parent.hashCode, 0, root.hashCode, 0,
							parent.hashCode.length);
					root.hashCode[hashCodeLength - 1] = childHash;
				}
			}
			if (!seen.contains(root)) {
				visitor.visit(parent, root, connectingEdge, layer, 0);
				seen.add(root);
				// Copy the connects as source list so that visitors
				// can change the original for edge direction inversions
				final Object[] outgoingEdges = root.connectsAsSource.toArray();
				for (int i = 0; i < outgoingEdges.length; i++) {
					JGraphHierarchyEdge internalEdge = (JGraphHierarchyEdge) outgoingEdges[i];
					JGraphHierarchyNode targetNode = internalEdge.target;
					// Root check is O(|roots|)
					dfs(root, targetNode, internalEdge, visitor, seen,
							root.hashCode, i, layer + 1);
				}
			} else {
				// Use the int field to indicate this node has been seen
				visitor.visit(parent, root, connectingEdge, layer, 1);
			}
		}
	}

	/**
	 * @return Returns the vertexMapping.
	 */
	public Map getVertexMapping() {
		if (vertexMapper == null) {
			vertexMapper = new Hashtable();
		}
		return vertexMapper;
	}

	/**
	 * @param vertexMapping
	 *            The vertexMapping to set.
	 */
	public void setVertexMapping(Map vertexMapping) {
		this.vertexMapper = vertexMapping;
	}

	/**
	 * @return Returns the edgeMapper.
	 */
	public Map getEdgeMapper() {
		return edgeMapper;
	}

	/**
	 * @param edgeMapper
	 *            The edgeMapper to set.
	 */
	public void setEdgeMapper(Map edgeMapper) {
		this.edgeMapper = edgeMapper;
	}

	/**
	 * @return Returns the dfsCount.
	 */
	public int getDfsCount() {
		return dfsCount;
	}

	/**
	 * @param dfsCount
	 *            The dfsCount to set.
	 */
	public void setDfsCount(int dfsCount) {
		this.dfsCount = dfsCount;
	}

	/**
	 * @return Returns the deterministic.
	 */
	public boolean isDeterministic() {
		return deterministic;
	}

	/**
	 * @param deterministic
	 *            The deterministic to set.
	 */
	public void setDeterministic(boolean deterministic) {
		this.deterministic = deterministic;
	}

	public boolean isSinksAtLayerZero() {
		return scanRanksFromSinks;
	}

	public void setSinksAtLayerZero(boolean sinksAtLayerZero) {
		this.scanRanksFromSinks = sinksAtLayerZero;
	}

}