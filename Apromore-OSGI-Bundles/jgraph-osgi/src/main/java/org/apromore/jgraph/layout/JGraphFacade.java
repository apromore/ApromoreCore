/*
 * $Id: JGraphFacade.java,v 1.2 2009/11/10 21:14:24 david Exp $ Copyright (c)
 * 2001-2009, JGraph Ltd
 * 
 * All rights reserved.
 * 
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.apromore.jgraph.layout;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingConstants;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.algebra.JGraphAlgebra;
import org.apromore.jgraph.algebra.JGraphUnionFind;
import org.apromore.jgraph.algebra.cost.JGraphCostFunction;
import org.apromore.jgraph.algebra.cost.JGraphDistanceCostFunction;
import org.apromore.jgraph.graph.AttributeMap;
import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.DefaultGraphModel;
import org.apromore.jgraph.graph.Edge;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.jgraph.graph.GraphLayoutCache;
import org.apromore.jgraph.graph.GraphModel;
import org.apromore.jgraph.graph.Port;
import org.apromore.jgraph.graph.AttributeMap.SerializablePoint2D;
import org.apromore.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

/**
 * An abstract description of a graph that can be used by a layout algorithm.
 * This abstracts visibility, grouping, directed edges, any root cells,
 * translation and scaling functions. It also stores the actual graph to be
 * acted upon by the layout and provides utility method to determine the
 * characteristics of the contained cells. After the layout has been applied
 * this class stores the result of that layout as a nested attribute map.
 * 
 */
public class JGraphFacade {

	/**
	 * Stores whether or not the layout is to act on only visible cells i.e.
	 * <code>true</code> means only act on visible cells, <code>false</code> act
	 * on cells regardless of their visibility. Default is <code>true</code>.
	 */
	protected boolean ignoresHiddenCells = true;

	/**
	 * Stores whether or not the layout is to act on only cells that have at
	 * least one connection. <code>true</code> means only act on connected
	 * cells, <code>false</code> act on cells regardless of their connections.
	 * Default is <code>true</code>.
	 */
	protected boolean ignoresUnconnectedCells = true;

	/**
	 * Stores whether or not the layout is to only act on root cells in the
	 * model. <code>true</code> means only act on root cells, <code>false</code>
	 * means act upon roots and their children. Default is <code>false</code>.
	 */
	protected boolean ignoresCellsInGroups = false;

	/**
	 * Stores whether or not the graph is to be treated as a directed graph.
	 * <code>true</code> means follow edges in target to source direction,y
	 * 
	 * <code>false</code> means treat edges as directionless
	 */
	protected boolean directed;

	/**
	 * Whether or not edges connected to collapsed children are promoted to
	 * their first visible parent within the facade, not the actual model
	 */
	protected boolean edgePromotion = false;

	/**
	 * Whether or not cells should be returned in the same order as found in the
	 * model. Set to true to obtain deterministic results for things such as the
	 * order of cells with a particular level of a tree layout. Note that
	 * setting this variable to true can cause quadratic performance, therefore
	 * it defaults to false.
	 */
	protected boolean ordered = false;

	/**
	 * The JGraph to have the layout applied to it. There is no accessor to the
	 * graph for the layouts. If you need access to the graph, try to factor out
	 * the methods into a custom facade, and pass an instance of that facade to
	 * your layout's run method.
	 */
	protected transient JGraph graph = null;

	/**
	 * The layout cache to have the layout applied to it. There is no accessor
	 * to the graph for the layouts. If you need access to the graph, try to
	 * factor out the methods into a custom facade, and pass an instance of that
	 * facade to your layout's run method.
	 */
	protected transient GraphLayoutCache graphLayoutCache = null;

	/**
	 * The model to have the layout applied to it. There is no accessor to the
	 * graph for the layouts. If you need access to the graph, try to factor out
	 * the methods into a custom facade, and pass an instance of that facade to
	 * your layout's run method.
	 */
	protected transient GraphModel model = null;

	/**
	 * The map of attribute changes made be the layout. Maps from cells to maps.
	 */
	protected transient Hashtable attributes = new Hashtable();

	/**
	 * The default comparator to be used where ordering is required in layouts
	 */
	protected transient Comparator order = new DefaultComparator2();

	/**
	 * The default cost function used for shortest path search.
	 */
	protected transient JGraphCostFunction distanceCostFunction;

	/**
	 * The default graph algebra used for basic algorithms and functions.
	 */
	protected transient JGraphAlgebra algebra;

	/**
	 * The root vertex to be used by tree layouts.
	 */
	protected transient List roots = new ArrayList();

	/**
	 * If instaniated, this set defines which vertices are to be processed in
	 * any layouts. Set to null to apply no filtered set
	 */
	protected transient Set verticesFilter = null;

	/**
	 * A collection of groups of sibling vertices
	 */
	protected transient List groupHierarchies = null;

	/**
	 * A collection of directions of groups of sibling vertices
	 */
	protected transient List<Integer> groupOrientations = null;

	/**
	 * The factor by which to multiple the radius of the circle layout
	 */
	protected double circleRadiusFactor = 1.0;

	/** The logger for this class */
	private static Logger logger = Logger
			.getLogger("com.jgraph.layout.JGraphFacade");

	/**
	 * Constructs a JGraphGraphFacade specifying the graph passed in as the
	 * input graph
	 * 
	 * @param graph
	 *            the JGraph to be laid out
	 */
	public JGraphFacade(JGraph graph) {
		this(graph, null);
	}

	/**
	 * Constructs a JGraphGraphFacade specifying the graph passed in as the
	 * input graph
	 * 
	 * @param graph
	 *            the JGraph to be laid out
	 * @param roots
	 *            the root vertices to be used by tree and hierarchical layouts
	 *            - NOTE, any roots will be subject to the facade filters at the
	 *            time of construction.
	 */
	public JGraphFacade(JGraph graph, Object[] roots) {
		this(graph, roots, true, false, true, true);
	}

	/**
	 * Constructs a JGraphGraphFacade
	 * 
	 * @see #JGraphFacade(JGraph, Object[], boolean, boolean, boolean, boolean,
	 *      JGraphCostFunction, JGraphAlgebra)
	 */
	public JGraphFacade(JGraph graph, Object[] roots,
			boolean ignoresHiddenCells, boolean ignoresCellsInGroups,
			boolean ignoresUnconnectedCells, boolean directed) {
		this(graph, roots, ignoresHiddenCells, ignoresCellsInGroups,
				ignoresUnconnectedCells, directed,
				new JGraphDistanceCostFunction(graph.getGraphLayoutCache()),
				JGraphAlgebra.getSharedInstance());
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
	 * @param graph
	 *            The graph used as input to the layout
	 * @param roots
	 *            the root vertices to be used by tree and hierarchical layouts
	 *            - NOTE, any roots will be subject to the facade filters at the
	 *            time of construction.
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
	public JGraphFacade(JGraph graph, Object[] roots,
			boolean ignoresHiddenCells, boolean ignoresCellsInGroups,
			boolean ignoresUnconnectedCells, boolean directed,
			JGraphCostFunction distanceCostFunction, JGraphAlgebra algebra) {
		this(graph == null ? null : graph.getModel(), graph == null ? null
				: graph.getGraphLayoutCache(), roots, ignoresHiddenCells,
				ignoresCellsInGroups, ignoresUnconnectedCells, directed,
				distanceCostFunction, algebra);
		this.graph = graph;
	}

	/**
	 * Creates a JGraphFacade specifying the graph passed in as the input graph.
	 * 
	 * @param cache
	 *            The GraphLayoutCache to be used as input to the layout
	 */
	public JGraphFacade(GraphLayoutCache cache) {
		this(cache, null, true, false, true, true,
				new JGraphDistanceCostFunction(cache), JGraphAlgebra
						.getSharedInstance());

	}

	/**
	 * Creates a JGraphFacade specifying the graph passed in as the input graph.
	 * Also configures properties of layout, whether or not edge direction is to
	 * be taken into account, whether or not invisible cells are to be
	 * considered and whether or not only root cells are to be considered or
	 * roots and all their children. A root is only used if the isVertex method
	 * returns true.
	 * 
	 * @see #isVertex
	 * 
	 * @param cache
	 *            The GraphLayoutCache to be used as input to the layout
	 * @param roots
	 *            the root vertices to be used by tree and hierarchical layouts
	 *            - NOTE, any roots will be subject to the facade filters at the
	 *            time of construction.
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
	public JGraphFacade(GraphLayoutCache cache, Object[] roots,
			boolean ignoresHiddenCells, boolean ignoresCellsInGroups,
			boolean ignoresUnconnectedCells, boolean directed,
			JGraphCostFunction distanceCostFunction, JGraphAlgebra algebra) {
		this(cache == null ? null : cache.getModel(), cache, roots,
				ignoresHiddenCells, ignoresCellsInGroups,
				ignoresUnconnectedCells, directed, distanceCostFunction,
				algebra);
	}

	/**
	 * Creates a JGraphGenericFacade specifying the graph passed in as the input
	 * graph. Also configures properties of layout, whether or not edge
	 * direction is to be taken into account, whether or not invisible cells are
	 * to be considered and whether or not only root cells are to be considered
	 * or roots and all their children. A root is only used if the isVertex
	 * method returns true.
	 * 
	 * @see #isVertex
	 * 
	 * @param model
	 *            The GraphModel to be used as input to the layout
	 * @param roots
	 *            the root vertices to be used by tree and hierarchical layouts
	 *            - NOTE, any roots will be subject to the facade filters at the
	 *            time of construction.
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
	public JGraphFacade(GraphModel model, Object[] roots,
			boolean ignoresHiddenCells, boolean ignoresCellsInGroups,
			boolean ignoresUnconnectedCells, boolean directed,
			JGraphCostFunction distanceCostFunction, JGraphAlgebra algebra) {
		this(model, null, roots, ignoresHiddenCells, ignoresCellsInGroups,
				ignoresUnconnectedCells, directed, distanceCostFunction,
				algebra);
	}

	/**
	 * Creates a JGraphGenericFacade specifying the graph passed in as the input
	 * graph. Also configures properties of layout, whether or not edge
	 * direction is to be taken into account, whether or not invisible cells are
	 * to be considered and whether or not only root cells are to be considered
	 * or roots and all their children. A root is only used if the isVertex
	 * method returns true.
	 * 
	 * @see #isVertex
	 * 
	 * @param model
	 *            The GraphModel to be used as input to the layout
	 * @param cache
	 *            The GraphLayoutCache to be used as input to the layout
	 * @param roots
	 *            the root vertices to be used by tree and hierarchical layouts
	 *            - NOTE, any roots will be subject to the facade filters at the
	 *            time of construction.
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
	public JGraphFacade(GraphModel model, GraphLayoutCache cache,
			Object[] roots, boolean ignoresHiddenCells,
			boolean ignoresCellsInGroups, boolean ignoresUnconnectedCells,
			boolean directed, JGraphCostFunction distanceCostFunction,
			JGraphAlgebra algebra) {
		this.model = model;
		this.graphLayoutCache = cache;
		if (model == null) {
			// Cannot obtain model
			throw new RuntimeException(
					"GraphModel not available in JGraphFacade");
		}
		this.ignoresHiddenCells = ignoresHiddenCells;
		// If the graph layout cache is null, this flag cannot be
		// taken into account and must be forced to false
		if (cache == null) {
			ignoresHiddenCells = false;
		}
		this.ignoresCellsInGroups = ignoresCellsInGroups;
		this.ignoresUnconnectedCells = ignoresUnconnectedCells;
		this.directed = directed;
		this.distanceCostFunction = distanceCostFunction;
		this.algebra = algebra;
		if (roots != null) {
			for (int i = 0; i < roots.length; i++)
				if (isVertex(roots[i]))
					this.roots.add(roots[i]);
		}
		setLoggerLevel(Level.OFF);
	}

	/**
	 * The main method to execute layouts
	 * 
	 * @param layout
	 *            the layout to be executed
	 * @param processByGroups
	 *            Whether or not to process cell only at the level of their own
	 *            group When true, children are only processed with siblings and
	 *            their parent only with its siblings and so on
	 */
	public void run(JGraphLayout layout, boolean processByGroups) {
		if (processByGroups) {
			// determine orientations
			int[] orientations = new int[2];

			// Run the layout individual on each sibling group
			// then combine the result
			Collection allVertices = new HashSet(getVertices());
			determineLayoutHierarchies(SwingConstants.NORTH);
			Set oldVertexFilter = verticesFilter;
			Object[] hierarchies = groupHierarchies.toArray();
			for (int i = 0; i < hierarchies.length; i++) {
				verticesFilter = (Set) hierarchies[i];
				layout.run(this);
			}
			graphLayoutCache.setVisible(allVertices.toArray(), true);

			verticesFilter = oldVertexFilter;
		} else {
			layout.run(this);
		}
		fixParallelEdges(15);
	}

	/**
	 * The main method to execute layouts
	 * 
	 * @param layout
	 *            the layout to be executed
	 * @param processByGroups
	 *            Whether or not to process cell only at the level of their own
	 *            group When true, children are only processed with siblings and
	 *            their parent only with its siblings and so on
	 */
	public void run(JGraphHierarchicalLayout layout, boolean processByGroups) {
		if (processByGroups) {
			// Run the layout individual on each sibling group
			// then combine the result
			Collection allVertices = new HashSet(getVertices());
			determineLayoutHierarchies(layout.getOrientation());
			Set oldVertexFilter = verticesFilter;
			Object[] hierarchies = groupHierarchies.toArray();
			for (int i = 0; i < hierarchies.length; i++) {
				verticesFilter = (Set) hierarchies[i];
				layout.setOrientation(groupOrientations.get(i));
				layout.run(this);
			}
			graphLayoutCache.setVisible(allVertices.toArray(), true);

			verticesFilter = oldVertexFilter;
		} else {
			layout.run(this);
		}
		fixParallelEdges(15);
	}

	protected void fixParallelEdges(double spacing) {
		ArrayList edges = new ArrayList(getEdges());
		for (Object edge : edges) {
			List points = getPoints(edge);
			if (points.size() != 2) {
				continue;
			}
			Object sourceCell = getSource(edge);
			Object targetCell = getTarget(edge);
			Object sourcePort = getSourcePort(edge);
			Object targetPort = getTargetPort(edge);
			Object[] between = getEdgesBetween(sourcePort, targetPort, false);
			if ((between.length == 1) && !(sourcePort == targetPort)) {
				continue;
			}
			Rectangle2D sCP = getBounds(sourceCell);
			Rectangle2D tCP = getBounds(targetCell);
			Point2D sPP = GraphConstants.getOffset(((Port) sourcePort)
					.getAttributes());
			// facade. getBounds (sourcePort ) ;

			if (sPP == null) {
				sPP = new Point2D.Double(sCP.getCenterX(), sCP.getCenterY());
			}
			Point2D tPP = GraphConstants.getOffset(((Port) targetPort)
					.getAttributes());
			// facade.getBounds(sourcePort);

			if (tPP == null) {
				tPP = new Point2D.Double(tCP.getCenterX(), tCP.getCenterY());
			}

			if (sourcePort == targetPort) {
				assert (sPP.equals(tPP));
				double x = sPP.getX();
				double y = sPP.getY();
				for (int i = 2; i < between.length + 2; i++) {
					List newPoints = new ArrayList(5);
					newPoints.add(new Point2D.Double(x
							- (spacing + i * spacing), y));
					newPoints.add(new Point2D.Double(x
							- (spacing + i * spacing), y
							- (spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x, y
							- (2 * spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x
							+ (spacing + i * spacing), y
							- (spacing + i * spacing)));
					newPoints.add(new Point2D.Double(x + (spacing), y
							- (spacing / 2 + i * spacing)));
					setPoints(between[i - 2], newPoints);
				}

				continue;
			}

			double dx = (sPP.getX()) - (tPP.getX());
			double dy = (sPP.getY()) - (tPP.getY());
			double mx = (tPP.getX()) + dx / 2.0;
			double my = (tPP.getY()) + dy / 2.0;
			double slope = Math.sqrt(dx * dx + dy * dy);
			for (int i = 0; i < between.length; i++) {
				List newPoints = new ArrayList(3);
				double pos = 2 * i - (between.length - 1);
				if (getSourcePort(between[i]) == sourcePort) {
					newPoints.add(sPP);
					newPoints.add(tPP);
				} else {
					newPoints.add(tPP);
					newPoints.add(sPP);
				}
				if (pos != 0) {
					pos = pos / 2;
					double x = mx + pos * spacing * dy / slope;
					double y = my - pos * spacing * dx / slope;
					newPoints.add(1, new SerializablePoint2D.Double(x, y));
				}
				setPoints(between[i], newPoints);
			}
		}
	}

	/**
	 * Resets the control points of all moveable edges in the graph.
	 */
	public void resetControlPoints() {
		resetControlPoints(false, null);
	}

	/**
	 * Resets the control points of all moveable edges in the graph. Also set
	 * the routing on the edges to the specified value if the parameter flag
	 * indicates to do so
	 * 
	 * @param whether
	 *            or not to set a new routing style on each edge
	 * @param the
	 *            routing style to set on each edge if <code>setRouting</code>
	 *            is <code>true</code>
	 */
	public void resetControlPoints(boolean setRouting, Edge.Routing routing) {
		Iterator it = getEdges().iterator();
		while (it.hasNext()) {
			Object edge = it.next();
			if (isMoveable(edge)) {
				Map map = getAttributes(edge);

				// Resets the control points by removing all but
				// the first and the last point from the points
				// list.
				List pts = GraphConstants.getPoints(map);
				if (pts != null && pts.size() > 2) {
					List newPoints = new ArrayList();
					newPoints.add(pts.get(0));
					newPoints.add(pts.get(pts.size() - 1));
					GraphConstants.setPoints(map, newPoints);
				}
				if (setRouting) {
					GraphConstants.setRouting(map, routing);
				}
			}
		}
	}

	/**
	 * Returns whether or not the specified cell is a vertex and should be taken
	 * into account by the layout
	 * 
	 * @param cell
	 *            the cell that is to be classified as a vertex or not
	 * @return Returns true if <code>cell</code> is a vertex
	 */
	public boolean isVertex(Object cell) {
		if (verticesFilter != null) {
			if (!verticesFilter.contains(cell)) {
				return false;
			}
		}
		// If we're dealing with an edge or a port we
		// return false in all cases
		if (DefaultGraphModel.isVertex(model, cell)) {
			if (ignoresUnconnectedCells) {
				Object[] edges = getEdges(cell);
				if (edges == null || edges.length == 0)
					return false;
				else {
					if (ignoresHiddenCells && graphLayoutCache != null) {

						// Check if at least one edge is visible
						boolean connected = false;
						for (int i = 0; i < edges.length; i++) {
							connected = connected
									|| graphLayoutCache.isVisible(edges[i]);
						}
						if (!connected)
							return false;
					}
				}
			}
			if (ignoresHiddenCells && graphLayoutCache != null) {

				// If only visible cells should be returned
				// we check if there is a cell view for the cell
				// and return if based on it's isLeaf property.
				CellView view = graphLayoutCache.getMapping(cell, false);
				if (view != null) {
					// Root cell views have no parent view
					if (ignoresCellsInGroups) {
						return (view.getParentView() == null);
					} else {
						return true;
					}
				}
				return false;
			}
			if (ignoresCellsInGroups && model.getParent(cell) != null) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns whether or not the specified cell is an edge and should be taken
	 * into account by the layout
	 * 
	 * @param cell
	 *            the cell that is to be classified as an edge or not
	 * @return Returns true if the cell is an edge
	 */
	public boolean isEdge(Object cell) {
		// Hint: "Edge groups" need special attention
		// Unconnected edges are ignored
		if (model.getSource(cell) == null || model.getTarget(cell) == null) {
			return false;
		}
		if (ignoresHiddenCells && graphLayoutCache != null) {
			if (!model.isEdge(cell)) {
				return false;
			}
			CellView view = graphLayoutCache.getMapping(cell, false);
			if (view != null) {
				if (ignoresCellsInGroups) {
					return view.isLeaf() && view.getParentView() == null;
				} else {
					return view.isLeaf();
				}
			}
			return false;
		} else {
			// Returns false if we find a child that is not a port
			if (ignoresCellsInGroups && model.getParent(cell) != null) {
				return false;
			}
			return model.isEdge(cell);
		}
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
		return getNeighbours(cell, exclude, ordered, false);
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
	public List getNeighbours(Object cell, Set exclude, boolean ordered,
			boolean includeVisible) {
		LinkedList neighbours = new LinkedList();
		if (graphLayoutCache != null && graphLayoutCache.isPartial()
				&& edgePromotion) {
			Set cells = getHiddenChildren(cell, includeVisible);
			Iterator iter = cells.iterator();
			Set connectedCellsPromoted = new HashSet();
			while (iter.hasNext()) {
				Object currentCell = iter.next();
				List connectedCells = graphLayoutCache.getNeighbours(
						currentCell, exclude, directed, false);
				Iterator iter2 = connectedCells.iterator();
				while (iter2.hasNext()) {
					Object otherCell = iter2.next();
					if (!cells.contains(otherCell)) {
						while (model.getParent(otherCell) != null && //
								!graphLayoutCache.isVisible(otherCell)) {
							otherCell = model.getParent(otherCell);
						}
						if (graphLayoutCache.isVisible(otherCell)) {
							connectedCellsPromoted.add(otherCell);
						}
					}
				}
			}
			neighbours.addAll(connectedCellsPromoted);
		} else {
			List connectedCells = graphLayoutCache.getNeighbours(cell, exclude,
					directed, ignoresHiddenCells);
			neighbours.addAll(connectedCells);
		}
		if (ordered && order != null)
			Collections.sort(neighbours, order);
		return neighbours;
	}

	/**
	 * Obtains all hidden vertices of the specified cell
	 * 
	 * @param cell
	 *            the cell whose children are to be determined
	 * @param includeInvisible
	 * @return all the child hidden vertices
	 */
	private Set getHiddenChildren(Object cell, boolean includeVisible) {
		List cellChildren = DefaultGraphModel.getDescendants(model,
				new Object[] { cell });
		Set cells = new HashSet();
		cells.add(cell);
		Iterator iter = cellChildren.iterator();
		while (iter.hasNext()) {
			Object childCell = iter.next();
			if (DefaultGraphModel.isVertex(model, childCell)
					&& (includeVisible || !graphLayoutCache
							.isVisible(childCell))) {
				cells.add(childCell);
			}
		}
		return cells;
	}

	/**
	 * Returns the length of the specified edge wrt
	 * <code>distanceFunction</code>.
	 * 
	 * @param edge
	 *            the edge whos length is returned
	 * 
	 * @return Returns the length of <code>edge</code>
	 * 
	 * @see #distanceCostFunction
	 * @see #getPath(Object, Object, int, JGraphCostFunction)
	 */
	public double getLength(Object edge) {
		return distanceCostFunction.getCost(edge);
	}

	/**
	 * Returns the length of the shortest path connecting <code>v1</code> and
	 * <code>v2</code> wrt <code>distanceFunction</code>. The path has no more
	 * than <code>maxHops</code> elements.
	 * 
	 * @param v1
	 *            the source vertex
	 * @param v2
	 *            the target vertex
	 * @param maxHops
	 *            the maximum number of edges the path may have
	 * 
	 * @return Returns the length of the shortest path between v1 and v2
	 * 
	 * @see #distanceCostFunction
	 * @see #getPath(Object, Object, int, JGraphCostFunction)
	 */
	public double getDistance(Object v1, Object v2, int maxHops) {
		Object[] path = getPath(v1, v2, maxHops, distanceCostFunction);
		return algebra.sum(path, distanceCostFunction);
	}

	/**
	 * Returns the shortest path connecting <code>v1</code> and <code>v2</code>
	 * wrt <code>cf</code> with traverses no more than <code>steps</code> edges.
	 * The cost function defines the metric that is used as the edges length.
	 * 
	 * @param v1
	 *            the source vertex
	 * @param v2
	 *            the target vertex
	 * @param steps
	 *            the maximum number of edges in the path
	 * @param cf
	 *            the cost function that defines the edge lengths
	 * 
	 * @return Returns shortest array of edges connecting v1 and v2
	 * 
	 * @see JGraphAlgebra#getShortestPath(GraphModel, Object, Object,
	 *      JGraphCostFunction, int, boolean)
	 */
	public Object[] getPath(Object v1, Object v2, int steps,
			JGraphCostFunction cf) {
		return algebra.getShortestPath(model, v1, v2, cf, steps, isDirected());
	}

	/**
	 * Returns a union find structure representing the connection components of
	 * G=(E,V). The union find may be used as follows to determine whether two
	 * cells are connected:
	 * <p>
	 * Object[] v = facade.getVertices(); <br>
	 * Object[] e = facade.getEdges(); <br>
	 * JGraphUnionFind uf = facade.getConnectionComponents(v, e); <br>
	 * boolean connected = uf.differ(vertex1, vertex2); <br>
	 * 
	 * @param v
	 *            the vertices of the graph
	 * @param e
	 *            the edges of the graph
	 * 
	 * @return Returns the connection components in G=(E,V)
	 * 
	 * @see JGraphAlgebra#getConnectionComponents(GraphModel, Object[],
	 *      Object[])
	 */
	public JGraphUnionFind getConnectionComponents(Object[] v, Object[] e) {
		return algebra.getConnectionComponents(model, v, e);
	}

	/**
	 * Returns the minimum spanning tree (MST) for the graph defined by G=(E,V).
	 * The MST is defined as the set of all vertices with minimal lengths that
	 * forms no cycles in G.
	 * 
	 * @param v
	 *            the vertices of the graph
	 * 
	 * @return Returns the MST as an array of edges
	 * 
	 * @see JGraphAlgebra#getMinimumSpanningTree(GraphModel, Object[],
	 *      JGraphCostFunction, boolean)
	 */
	public Object[] getMinimumSpanningTree(Object[] v, JGraphCostFunction cf) {
		return algebra.getMinimumSpanningTree(model, v, cf, directed);
	}

	/**
	 * Returns all vertices in the graph. <br>
	 * Note: This returns a linked list, for frequent read operations you should
	 * turn this into an array, or at least an array list.
	 * 
	 * @return Returns all cells that the layout should take into account
	 * 
	 * @see #isVertex(Object)
	 */
	public Collection getVertices() {
		return getCells(getAll(), false, false);
	}

	/**
	 * Returns all unconnected vertices in the graph. <br>
	 * 
	 * @return Returns all the unconnected cells that the layout should take
	 *         into account
	 */
	public Collection getUnconnectedVertices(boolean ordered) {
		Collection vertices = getAll();
		Set result = null;
		if (ordered && order != null) {
			result = new TreeSet(order);
		} else {
			result = new LinkedHashSet();
		}
		Iterator it = vertices.iterator();
		while (it.hasNext()) {
			Object cell = it.next();
			// Check if cell is unconnected vertex
			if (DefaultGraphModel.isVertex(model, cell)) {
				Object[] edges = getEdges(cell);
				if (edges == null || edges.length == 0) {
					result.add(cell);
				}
			}
		}
		return result;
	}

	/**
	 * Returns all edges in the graph. <br>
	 * Note: This returns a linked list, for frequent read operations you should
	 * turn this into an array, or at least an array list.
	 * 
	 * @return Returns all edges that the layout should take into account
	 * 
	 * @see #isEdge(Object)
	 */
	public Collection getEdges() {
		return getCells(getAll(), true, false);
	}

	/**
	 * Returns the connected edges for a cell. Cell should be a port or a
	 * vertex.
	 * 
	 * @param cell
	 *            the cell whose edges are to be obtained
	 * @return Returns the array of all connected edges
	 */
	public Object[] getEdges(Object cell) {
		return DefaultGraphModel.getEdges(model, new Object[] { cell })
				.toArray();
	}

	/**
	 * Returns the incoming or outgoing edges for cell. Cell should be a port or
	 * a vertex.
	 * 
	 * @param cell
	 *            the graph cell whose edges are to be obtained
	 * @param incoming
	 *            whether or not to obtain incoming edges only
	 */
	public Object[] getEdges(Object cell, boolean incoming) {
		return DefaultGraphModel.getEdges(model, cell, incoming);
	}

	/**
	 * Returns the vertex that is connected to the source end of the specified
	 * edge
	 * 
	 * @param edge
	 *            the reference edge
	 * @return any vertex connected as the source the specified edge
	 */
	public Object getSource(Object edge) {
		Object cell = null;
		cell = DefaultGraphModel.getSourceVertex(model, edge);
		if (cell != null && !isVertex(cell)) {
			// Check to see if the edge has been promoted
			if (edgePromotion) {
				while (model.getParent(cell) != null && !isVertex(cell)) {
					cell = model.getParent(cell);
				}
			} else {
				return null;
			}
			if (isVertex(cell)) {
				return cell;
			} else {
				return null;
			}
		}
		return cell;
	}

	/**
	 * Returns the vertex that is connected to the target end of the specified
	 * edge
	 * 
	 * @param edge
	 *            the reference edge
	 * @return any vertex connected as the target the specified edge
	 */
	public Object getTarget(Object edge) {
		Object cell = null;
		cell = DefaultGraphModel.getTargetVertex(model, edge);
		if (cell != null && !isVertex(cell)) {
			// Check to see if the edge has been promoted
			if (edgePromotion) {
				while (model.getParent(cell) != null && !isVertex(cell)) {
					cell = model.getParent(cell);
				}
			} else {
				return null;
			}
			if (isVertex(cell)) {
				return cell;
			} else {
				return null;
			}
		}
		return cell;
	}

	/**
	 * Returns the port that is connected to the source end of the specified
	 * edge
	 * 
	 * @param edge
	 *            the reference edge
	 * @return any vertex connected as the source the specified edge
	 */
	public Object getSourcePort(Object edge) {
		Object cell = null;
		cell = model.getSource(edge);
		return cell;
	}

	/**
	 * Returns the port that is connected to the target end of the specified
	 * edge
	 * 
	 * @param edge
	 *            the reference edge
	 * @return any vertex connected as the target the specified edge
	 */
	public Object getTargetPort(Object edge) {
		Object cell = null;
		cell = model.getTarget(edge);
		return cell;
	}

	/**
	 * Returns all cells including all descendants.
	 */
	protected List getAll() {
		return DefaultGraphModel.getDescendants(model, DefaultGraphModel
				.getRoots(model));
	}

	/**
	 * Returns a collection of cells in the current graph. Roots are flattened
	 * and returned also. It can be specified whether or not to return edges in
	 * the graph using the appropriate parameter. If the <code>ordered</code>
	 * flag is set to <code>true</code> the result will be ordered by the
	 * current comparator set for this facade. <br>
	 * Note: This returns a set, for frequent read operations you should turn
	 * this into an array, or at least an array list.
	 * 
	 * @param cells
	 *            the cells to be filtered and return the correct cell types
	 * @param edges
	 *            whether or not to return the edges of the graph
	 * @param ordered
	 *            whether or not to order the returned value in the order of the
	 *            current <code>order</code> comparator. <b>Be very careful</b>
	 *            using the default comparator on the default graph model,
	 *            <code>getIndexOfRoot</code> has linear performance and so
	 *            sorting the entire model roots will have quadratic
	 *            performance.
	 * @return collection of cells in the graph
	 */
	protected Collection getCells(Collection cells, boolean edges,
			boolean ordered) {
		Set result = null;
		if (ordered && order != null) {
			result = new TreeSet(order);
		} else {
			result = new LinkedHashSet();
		}
		Iterator it = cells.iterator();
		while (it.hasNext()) {
			Object cell = it.next();
			if ((edges && isEdge(cell)) && (getSource(cell) != null)
					&& (getTarget(cell) != null))
				result.add(cell);
			if (!edges && isVertex(cell)) {
				result.add(cell);
			}
		}
		return result;
	}

	/**
	 * Obtains the cell view corresponding the cell passed in
	 * 
	 * @param cell
	 *            the cell whose view is to be obtained
	 * @return the cell view, if any, assoicated with this cell
	 */
	public Object getCellView(Object cell) {
		if (graphLayoutCache != null) {
			Object view = graphLayoutCache.getMapping(cell, false);
			return view;
		}

		return null;
	}

	/**
	 * Returns a collection of vertices found in the specified collection.
	 * 
	 * @param cells
	 *            the set of potential vertices
	 * @param ordered
	 *            whether or not to order the returned value in the order of the
	 *            current <code>order</code> comparator. <b>Be very careful</b>
	 *            using the default comparator on the default graph model,
	 *            <code>getIndexOfRoot</code> has linear performance and so
	 *            sorting the entire model roots will have quadratic
	 *            performance.
	 * @return Returns the collection of vertices on the collection
	 * 
	 * @see #isVertex(Object)
	 */
	public Collection getVertices(Collection cells, boolean ordered) {
		return getCells(cells, false, ordered);
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
	public List getOutgoingEdges(Object cell, Set exclude,
			boolean visibleCells, boolean selfLoops) {
		return graphLayoutCache.getOutgoingEdges(cell, exclude, visibleCells,
				selfLoops);
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
	public List getIncomingEdges(Object cell, Set exclude,
			boolean visibleCells, boolean selfLoops) {
		LinkedList incomingEdges = new LinkedList();
		if (graphLayoutCache != null && graphLayoutCache.isPartial()
				&& edgePromotion) {
			Set cells = getHiddenChildren(cell, false);
			Iterator iter = cells.iterator();
			Set connectedCellsPromoted = new HashSet();
			while (iter.hasNext()) {
				Object currentCell = iter.next();
				List connectedCells = graphLayoutCache.getIncomingEdges(
						currentCell, exclude, false, selfLoops);
				Iterator iter2 = connectedCells.iterator();
				while (iter2.hasNext()) {
					Object otherEdge = iter2.next();
					Object otherPort = model.getSource(otherEdge);
					Object otherCell = null;
					if (DefaultGraphModel.isVertex(model, otherPort)) {
						otherCell = otherPort;
					} else {
						otherCell = model.getParent(otherPort);
					}
					if (!cells.contains(otherCell)) {
						if (graphLayoutCache.isVisible(otherCell)
								&& visibleCells) {
							connectedCellsPromoted.add(otherEdge);
						} else if (graphLayoutCache.isVisible(otherCell)) {
							connectedCellsPromoted.add(otherEdge);
						}
					}
				}
			}
			incomingEdges.addAll(connectedCellsPromoted);
		} else {
			List connectedCells = graphLayoutCache.getIncomingEdges(cell,
					exclude, visibleCells, selfLoops);
			incomingEdges.addAll(connectedCells);
		}

		return incomingEdges;
	}

	/**
	 * Creates and returns nested attribute map specifying what changes the
	 * layout made to the input graph. After a layout is run this method should
	 * be queried to see what positional changes were made. This method applied
	 * snapping to the graph if enabled and only fills the map with the bounds
	 * values since these are the only values layout change
	 * 
	 * @return a nested <code>Map</code> of the changes the layout made upon the
	 *         input graph
	 * 
	 * @deprecated as of version 1.1
	 * @see #createNestedMap(boolean, boolean)
	 * @see GraphConstants#merge(Map, Map)
	 */
	@Deprecated
    public Map createNestedMap(Map nestedMap) {
		Map targetMap = createNestedMap(false, false);
		return GraphConstants.merge(nestedMap, targetMap);
	}

	/**
	 * Compatibility method to invoke {@link #createNestedMap(boolean, Point2D)}
	 * with an origin or null depending on <code>flushOrigin</code>.
	 * 
	 * @param ignoreGrid
	 *            whether or not the map returned is snapped to the current grid
	 * @param flushOrigin
	 *            whether or not the bounds of the graph should be moved to
	 *            (0,0)
	 * 
	 * @return a nested <code>Map</code> of the changes the layout made upon the
	 *         input graph
	 */
	public Map createNestedMap(boolean ignoreGrid, boolean flushOrigin) {
		return createNestedMap(ignoreGrid, (flushOrigin) ? new Point2D.Double(
				0, 0) : null);
	}

	/**
	 * Creates and returns nested attribute map specifying what changes the
	 * layout made to the input graph. After a layout is run this method should
	 * be queried to see what positional changes were made. This method applied
	 * snapping to the graph if enabled and only fills the map with the bounds
	 * values since these are the only values layout change
	 * 
	 * @param ignoreGrid
	 *            whether or not the map returned is snapped to the current grid
	 * @param origin
	 *            the new origin to which the graph bounds will be flushed to
	 * 
	 * @return a nested <code>Map</code> of the changes the layout made upon the
	 *         input graph
	 */
	public Map createNestedMap(boolean ignoreGrid, Point2D origin) {
		Rectangle2D rect = getCellBounds();
		if (rect == null)
			return null;

		// Makes sure the graph is not below zero and flushes to origin
		if (origin != null) {
			translateCells(getAttributes().keySet(), -rect.getX()
					+ origin.getX(), -rect.getY() + origin.getY());
		} else if ((graph == null || !graph.isMoveBelowZero())
				&& (rect.getX() < 0 || rect.getY() < 0)) {
			scale(getAttributes().keySet(), 1, 1, (rect.getX() < 0) ? Math
					.abs(rect.getX()) : 0, (rect.getY() < 0) ? Math.abs(rect
					.getY()) : 0);
		}

		// Contructs a graph change (nested map) by cloning all local attributes
		// for each cell, making sure the bounds are aligned to the grid if not
		// ignoreGrid is set.
		Map nested = new Hashtable();
		Iterator it = getAttributes().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object cell = entry.getKey();
			Map attrs = new Hashtable((Map) entry.getValue());
			if (!ignoreGrid && graph != null) {
				graph.snap(GraphConstants.getBounds(attrs));
			}
			nested.put(cell, attrs);
		}
		return nested;
	}

	/**
	 * Calculates a list of non-connected graph components for the current
	 * graph.
	 * 
	 * @return a collection of seperate graph components
	 */
	public List getComponents() {
		// Seperate out unconnected hierarchys
		List graphs = new LinkedList();
		Object[] vertices = getVertices().toArray();
		for (int i = 0; i < vertices.length; i++) {
			// First check if this vertex appears in any of the previous vertex
			// sets
			boolean newGraph = true;
			Iterator iter = graphs.iterator();
			while (newGraph && iter.hasNext()) {
				if (((Set) iter.next()).contains(vertices[i])) {
					newGraph = false;
				}
			}
			if (newGraph) {
				// Obtains set of vertices connected to this root
				Stack cellsStack = new Stack();
				cellsStack.push(vertices[i]);
				Set vertexSet = new HashSet();
				while (!cellsStack.isEmpty()) {
					Object cell = cellsStack.pop();
					if (!vertexSet.contains(cell)) {
						vertexSet.add(cell);
						boolean directed = isDirected();
						setDirected(false);
						Iterator it = getNeighbours(cell, vertexSet, false)
								.iterator();
						setDirected(directed);
						while (it.hasNext()) {
							cellsStack.push(it.next());
						}
					}
				}
				graphs.add(vertexSet);
			}
		}
		return graphs;
	}

	/**
	 * Calculates the euklidische Norm for the point p.
	 * 
	 * @param p
	 *            the point to calculate the norm for
	 * @return the euklidische Norm for the point p
	 */
	public double norm(Point2D p) {
		double x = p.getX();
		double y = p.getY();
		double norm = Math.sqrt(x * x + y * y);
		return norm;
	}

	/**
	 * Returns the nested map that specifies what changes the layout has made to
	 * the input graph.
	 * 
	 * @return The map that stores all attributes.
	 */
	public Hashtable getAttributes() {
		return attributes;
	}

	/**
	 * Sets the map that stores all attributes that comprise the changes made by
	 * the layout to the input graph
	 * 
	 * @param attributes
	 *            the new map of cell, map pairs
	 */
	public void setAttributes(Hashtable attributes) {
		this.attributes = attributes;
	}

	/**
	 * Returns the local attributes for the specified cell.
	 */
	public Map getAttributes(Object cell) {
		AttributeMap map = (AttributeMap) getAttributes().get(cell);
		if (map == null) {

			// First tries to get a view for the cell. If no view is available
			// tries to get the attributes from the model. Then stores a local
			// clone and associate it with the cell for future reference.
			CellView view = null;
			// Treat the bounds as a special case. If available, get the bounds
			// from the view, since this will return the correct bounds for
			// group cells
			Rectangle2D bounds = null;
			if (graphLayoutCache != null) {
				view = graphLayoutCache.getMapping(cell, false);
			}
			if (view != null) {
				map = view.getAllAttributes();
				bounds = (Rectangle2D) view.getBounds().clone();
			}
			if (map == null)
				map = model.getAttributes(cell);
			if (map != null) {
				map = (AttributeMap) map.clone();
				if (bounds != null) {
					GraphConstants.setBounds(map, bounds);
				}
				getAttributes().put(cell, map);
			}

		}
		return map;
	}

	/**
	 * Returns true if the cell is moveable. If this returns false then the
	 * cells bounds cannot be changed via the facade. The default implementation
	 * checks the <code>moveable</code> attribute. Subclassers can override this
	 * eg. to check if a cell is not selected in the graph.
	 */
	public boolean isMoveable(Object cell) {
		return GraphConstants.isMoveable(getAttributes(cell));
	}

	/**
	 * Sets the local attributes for the specified cell.
	 * 
	 * @param cell
	 *            the cell to set the attributes for
	 * @param map
	 *            the new attributes for the cell
	 */
	public void setAttributes(Object cell, Map map) {
		getAttributes().put(cell, map);
	}

	/**
	 * Returns the minimal rectangular bounds that enclose the specified
	 * vertices
	 * 
	 * @param vertices
	 *            the vertices whose collective bounds are to be determined
	 * @return the collective bounds of the input vertices
	 */
	public Rectangle2D getBounds(List vertices) {
		Rectangle2D ret = null;
		Iterator it = vertices.iterator();
		while (it.hasNext()) {
			Rectangle2D r = getBounds(it.next());
			if (r != null) {
				if (ret == null)
					ret = (Rectangle2D) r.clone();
				else
					Rectangle2D.union(ret, r, ret);
			}
		}
		return ret;
	}

	/**
	 * Returns the minimal rectangular bounds that enclose all the elements in
	 * the <code>bounds</code> map. After a layout has completed this method
	 * will return the collective bounds of the new laid out graph. Note this
	 * method may return null and should be checked before using.
	 * 
	 * @return the collective bounds of the elements in <code>bounds</code>
	 */
	public Rectangle2D getGraphBounds() {
		return GraphLayoutCache.getBounds(graphLayoutCache.getCellViews());
	}

	/**
	 * Returns the origin of the graph (ie the top left corner of the root
	 * cells) for the original geometry.
	 * 
	 * @return The origin of the graph.
	 */
	public Point2D getGraphOrigin() {
		Object[] cells = DefaultGraphModel.getRoots(model);
		if (cells != null && cells.length > 0) {
			Rectangle2D ret = null;
			Rectangle2D r = null;
			for (int i = 0; i < cells.length; i++) {
				if (graphLayoutCache != null) {
					CellView view = graphLayoutCache
							.getMapping(cells[i], false);
					if (view != null) {
						r = view.getBounds();
					}
				} else if (model != null) {
					Map attributes = model.getAttributes(cells[i]);
					if (attributes != null) {
						r = GraphConstants.getBounds(attributes);
					}
				}
				if (r != null) {
					if (ret == null) {
						ret = (r != null) ? (Rectangle2D) r.clone() : null;
					} else {
						Rectangle2D.union(ret, r, ret);
					}
				}
			}
			if (ret != null) {
				return new Point2D.Double(Math.max(0, ret.getX()), Math.max(0,
						ret.getY()));
			}
		}
		return null;
	}

	/**
	 * Returns the minimal rectangular bounds that enclose all the elements in
	 * the <code>bounds</code> map. After a layout has completed this method
	 * will return the collective bounds of the new laid out graph.
	 * 
	 * @return the collective bounds of the elements in <code>bounds</code>
	 */
	public Rectangle2D getCellBounds() {
		Rectangle2D ret = null;
		Hashtable nestedMap = getAttributes();
		// Clone the nested map to avoid to a ConcurrentModificationException
		Set nestedCopy = new HashSet(nestedMap.keySet());
		Iterator it = nestedCopy.iterator();
		while (it.hasNext()) {
			Object cell = it.next();
			Rectangle2D r = getBounds(cell);
			if (r != null) {
				if (ret == null) {
					ret = (Rectangle2D) r.clone();
				} else {
					Rectangle2D.union(ret, r, ret);
				}
			}
		}
		return ret;
	}

	/**
	 * Translates the bounds of the specified cells adding <code>dx</code> and
	 * <code>dy</code> to the respective location axes of the cell,
	 * 
	 * @param dx
	 *            the amount to be added to be x-axis positions of the vertices
	 *            before scaling is applied
	 * @param dy
	 *            the amount to be added to be y-axis positions of the vertices
	 *            before scaling is applied
	 */
	public void translateCells(Collection cells, double dx, double dy) {
		scale(cells, 1, 1, dx, dy);
	}

	/**
	 * Scales the graph bounds defined in <code>bounds</code> to fit into the
	 * specified frame
	 * 
	 * @param frame
	 *            the frame the <code>bounds</code> map colective bounds is to
	 *            be scaled to
	 */
	public void scale(Rectangle2D frame) {
		Rectangle2D rect = getCellBounds();
		double scalex = frame.getWidth() / rect.getWidth();
		double scaley = frame.getHeight() / rect.getHeight();
		double dx = frame.getX() - rect.getX();
		double dy = frame.getY() - rect.getY();
		scale(getAttributes().keySet(), scalex, scaley, dx, dy);
	}

	/**
	 * Scales the bounds of the specified cells adding <code>dx</code> and
	 * <code>dy</code> to the respective location axes of the cell, then by
	 * scaling them by <code>scalex</code> and <code>scaley</code>
	 * 
	 * @param vertices
	 *            the collection of vertices to be scaled
	 * @param scalex
	 *            the amount by which the x-axis positions of the vertices will
	 *            be scaled
	 * @param scaley
	 *            the amount by which the y-axis positions of the vertices will
	 *            be scaled
	 * @param dx
	 *            the amount to be added to be x-axis positions of the vertices
	 *            before scaling is applied
	 * @param dy
	 *            the amount to be added to be y-axis positions of the vertices
	 *            before scaling is applied
	 */
	public void scale(Collection vertices, double scalex, double scaley,
			double dx, double dy) {
		Iterator it = vertices.iterator();
		while (it.hasNext()) {
			Object cell = it.next();
			Point2D location = getLocation(cell);
			if (location != null) {
				location.setLocation((location.getX() + dx) * scalex, (location
						.getY() + dy)
						* scaley);
				setLocation(cell, location.getX(), location.getY(), false);
			}
			if (isEdge(cell)) {
				List points = getPoints(cell);
				if (points != null) {
					Iterator it2 = points.iterator();
					while (it2.hasNext()) {
						Object obj = it2.next();
						if (obj instanceof Point2D) {
							Point2D point = (Point2D) obj;
							point.setLocation((point.getX() + dx) * scalex,
									(point.getY() + dy) * scaley);
						}
					}
				}
			}
		}
	}

	/**
	 * Moves the specified vertices to random locations in the x and y axes
	 * directions between zero and a specified maximum. The maximum amounts can
	 * be specified seperately for the x and y axes.
	 * 
	 * @param vertices
	 *            the collection of vertices to be moved
	 * @param maxx
	 *            the maximum translation that may occur in the x-axis
	 * @param maxy
	 *            the maximum translation that may occur in the y-axis
	 */
	public void randomize(Collection vertices, int maxx, int maxy) {
		Random random = new Random();
		Iterator it = vertices.iterator();
		while (it.hasNext()) {
			if (maxx > 0 && maxy > 0) {
				int x = random.nextInt(maxx);
				int y = random.nextInt(maxy);
				setLocation(it.next(), x, y);
			}
		}
	}

	/**
	 * Simulates a 'nudge' to the graph, moving the specified vertices a random
	 * distance in the x and y axes directions between zero and a specified
	 * maximum. The maximum amounts can be specified seperately for the x and y
	 * axes.
	 * 
	 * @param vertices
	 *            the collection of vertices to be moved
	 * @param maxx
	 *            the maximum translation that may occur in the x-axis
	 * @param maxy
	 *            the maximum translation that may occur in the y-axis
	 */
	public void tilt(Collection vertices, int maxx, int maxy) {
		Random random = new Random();
		Iterator it = vertices.iterator();
		while (it.hasNext()) {
			int x = random.nextInt(maxx);
			int y = random.nextInt(maxy);
			translate(it.next(), x, y);
		}
	}

	/**
	 * Arrange the specified vertices into a circular shape, with a regular
	 * distance between each vertex
	 * 
	 * @param vertices
	 *            the collection of vertices to be arranged
	 */
	public void circle(Collection vertices) {
		Dimension d = getMaxSize(vertices);
		double max = Math.max(d.width, d.height);
		Object[] v = vertices.toArray();
		double r = v.length * max / Math.PI * circleRadiusFactor;
		double phi = 2 * Math.PI / vertices.size();
		for (int i = 0; i < v.length; i++)
			setLocation(v[i], r + r * Math.sin(i * phi), r + r
					* Math.cos(i * phi));
	}

	/**
	 * Returns the current bounds for the specified cell.
	 * 
	 * @param cell
	 *            the cell whose bounds are to be determined
	 * @return the bounds of the specified cell
	 */
	public Rectangle2D getBounds(Object cell) {
		Map map = getAttributes(cell);
		if (isEdge(cell)) {
			Rectangle2D rect = GraphConstants.getBounds(map);
			List points = GraphConstants.getPoints(map);
			if (points != null) {
				Iterator iter = points.iterator();
				while (iter.hasNext()) {
					Object point = iter.next();
					if (point instanceof Point2D) {
						if (rect == null) {
							rect = new Rectangle2D.Double(((Point2D) point)
									.getX(), ((Point2D) point).getY(), 1.0, 1.0);
						} else {
							rect.add((Point2D) point);
						}
					}
				}
			}
			setBounds(cell, rect);
			return rect;
		} else {
			// If this is a group cell we need to allow for the movement of any
			// child cells
			int numChildren = model.getChildCount(cell);
			Rectangle2D newChildBounds = null;
			Rectangle2D oldChildBounds = null;
			// Track whether any of the child vertices have changed. If at least
			// one has
			// work out the collective bounds of all children again, using the
			// new bound
			// values if available, otherwise the old.
			boolean childHasChanged = false;
			for (int i = 0; i < numChildren; i++) {
				Object child = model.getChild(cell, i);
				if ((cell != child) && //
						DefaultGraphModel.isVertex(model, child)) {
					// if (graphLayoutCache != null &&
					// !graphLayoutCache.isVisible(child)) {
					// // If visiblity information is available and the child is
					// // not visible do not add it to the bounds of the parent
					// } else {
					AttributeMap cellAttributes = (AttributeMap) getAttributes()
							.get(child);

					if (cellAttributes != null) {
						childHasChanged = true;
						Rectangle2D cellBounds = (Rectangle2D) GraphConstants
								.getBounds(cellAttributes).clone();
						if (newChildBounds == null) {
							newChildBounds = cellBounds;
						} else {
							newChildBounds = newChildBounds
									.createUnion(cellBounds);
						}
					} else {
						Rectangle2D cellBounds = (Rectangle2D) getBounds(child)
								.clone();
						if (oldChildBounds == null) {
							oldChildBounds = cellBounds;
						} else {
							oldChildBounds = oldChildBounds
									.createUnion(cellBounds);
						}
						// }
					}
				}
			}
			if (childHasChanged) {
				Rectangle2D cellBounds = null;
				// Return the union of the child vertices
				if (newChildBounds != null && oldChildBounds != null) {
					Rectangle2D groupBounds = newChildBounds
							.createUnion(oldChildBounds);
					cellBounds = groupBounds;
				} else if (newChildBounds == null && oldChildBounds != null) {
					cellBounds = oldChildBounds;
				} else if (newChildBounds != null && oldChildBounds == null) {
					cellBounds = newChildBounds;
				}
				// Allow for group inset
				int inset = GraphConstants.getInset(map);
				if (inset != 0) {
					cellBounds.setFrame(cellBounds.getX() - inset, cellBounds
							.getY()
							- inset, cellBounds.getWidth() + inset * 2,
							cellBounds.getHeight() + inset * 2);
				}
				setBounds(cell, cellBounds);
				return cellBounds;

			}
			// Return the default bounds value
			return GraphConstants.getBounds(map);
		}
	}

	/**
	 * Reads the bounds from the nested map for each cell and invokes setBounds
	 * for that cell with a clone of the bounds.
	 * 
	 * @param nestedMap
	 *            A map of (cell, map) pairs
	 * 
	 * @see GraphConstants#getBounds(Map)
	 */
	public void setBounds(Map nestedMap) {
		if (nestedMap != null) {
			Iterator it = nestedMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Rectangle2D bounds = GraphConstants.getBounds((Map) entry
						.getValue());
				if (bounds != null) {
					setBounds(entry.getKey(), (Rectangle2D) bounds.clone());
				}
			}
		}
	}

	/**
	 * Sets the current bounds for the specified cell.
	 * 
	 * @param cell
	 *            the cell whose bounds are to be set
	 * @param rect
	 *            the new bounds of the specified cell
	 */
	public void setBounds(Object cell, Rectangle2D rect) {
		Map map = getAttributes(cell);
		GraphConstants.setBounds(map, rect);
	}

	/**
	 * Returns an array of arrays (index 0 is x-coordinate, index 1 is
	 * y-coordinate in the second array) that fast layouts can operate upon.
	 * <p>
	 * This method is normally used at the beginning of a layout to setup fast
	 * internal datastructures. The layout then changes the array in-place and
	 * when finished, writes the result back using the setLocations(Object[]
	 * cells, double[][] locations) method:
	 * <p>
	 * public void run(JGraphFacade facade) { 1. vertices =
	 * facade.getVertices().toArray(); 2. locations =
	 * facade.getLocations(vertices); 3. perform layout on local arrays 4.
	 * return result: facade.setLocations(vertices, locations); }
	 * 
	 * @param cells
	 *            The cells to return the locations for
	 * @return Returns the locations of the cells as an array of arrays
	 */
	public double[][] getLocations(Object[] cells) {
		double[][] locations = new double[cells.length][2];
		for (int i = 0; i < cells.length; i++) {
			Point2D location = getLocation(cells[i]);
			locations[i][0] = location.getX();
			locations[i][1] = location.getY();
		}
		return locations;
	}

	/**
	 * Same as getLocations, but with width and height added at index 3 and 4
	 * respectively.
	 * 
	 * @param cells
	 *            The cells to return the bounds for
	 * @return Returns the bounds of the cells as an array of arrays
	 * 
	 * @see #getLocations(Object[])
	 */
	public double[][] getBounds(Object[] cells) {
		double[][] locations = new double[cells.length][4];
		for (int i = 0; i < cells.length; i++) {
			Rectangle2D bounds = getBounds(cells[i]);
			locations[i][0] = bounds.getX();
			locations[i][1] = bounds.getY();
			locations[i][2] = bounds.getWidth();
			locations[i][3] = bounds.getHeight();
		}
		return locations;
	}

	/**
	 * Returns the current location of the specified cell
	 * 
	 * @param cell
	 *            the cell whose location is to be determined
	 * @return Returns the current location of the specified cell
	 */
	public Point2D getLocation(Object cell) {
		Rectangle2D rect = getBounds(cell);
		if (rect != null)
			return new Point2D.Double(rect.getX(), rect.getY());
		return null;
	}

	/**
	 * Sets the locations of the specified cells according to the arrays
	 * specified in <code>locations</code>. The cells and locations array must
	 * contain the same number of elements.
	 * 
	 * @param cells
	 *            The cells to change the locations for
	 * @param locations
	 *            The new locations as an array of arrays
	 * 
	 * @see #getLocations(Object[])
	 */
	public void setLocations(Object[] cells, double[][] locations) {
		if (cells != null && locations != null
				&& cells.length == locations.length) {
			for (int i = 0; i < cells.length; i++)
				setLocation(cells[i], locations[i][0], locations[i][1], true);
		}
	}

	/**
	 * Same as setLocations, but with width and height added at index 3 and 4
	 * respectively.
	 * 
	 * @param cells
	 *            The cells to change the bounds for
	 * @param locations
	 *            The new bounds as an array of arrays
	 * 
	 * @see #getLocations(Object[])
	 */
	public void setBounds(Object[] cells, double[][] locations) {
		if (cells != null && locations != null
				&& cells.length == locations.length) {
			for (int i = 0; i < cells.length; i++)
				setBounds(cells[i], new Rectangle2D.Double(locations[i][0],
						locations[i][1], locations[i][2], locations[i][3]));
		}
	}

	/**
	 * Sets the current location of the specified cell. This checks if the cell
	 * is moveable.
	 * 
	 * @param cell
	 *            the cell whose location is to be set
	 * @param x
	 *            the new x-axs location of the cell
	 * @param y
	 *            the new y-axs location of the cell
	 * 
	 * @see #isMoveable(Object)
	 */
	public void setLocation(Object cell, double x, double y) {
		setLocation(cell, x, y, true);
	}

	/**
	 * Sets the current location of the specified cell. This checks if the cell
	 * is moveable.
	 * 
	 * @param cell
	 *            the cell whose location is to be set
	 * @param x
	 *            the new x-axs location of the cell
	 * @param y
	 *            the new y-axs location of the cell
	 * @param moveGroups
	 *            whether or not to move group cells
	 * 
	 * @see #isMoveable(Object)
	 */
	public void setLocation(Object cell, double x, double y, boolean moveGroups) {
		if (cell != null) {
			Rectangle2D rect = getBounds(cell); // construct the rectangle
			// System.out.println("set Location for cell " +
			// model.getValue(cell) + " to " + x + "," + y);
			// System.out.println("set Location, old position = " + rect.getX()
			// + "," + rect.getY());
			// System.out.println("Dimensions = " + rect.getWidth() + " , " +
			// rect.getHeight());
			if (isMoveable(cell) && rect != null) {
				if (moveGroups) {
					// Check for child cells
					double translationX = x - rect.getX();
					double translationY = y - rect.getY();
					int numChildrenCells = model.getChildCount(cell);
					for (int i = 0; i < numChildrenCells; i++) {
						Object childCell = model.getChild(cell, i);
						if (cell != childCell) {
							translate(childCell, translationX, translationY);
						}

					}
					rect.setFrame(x, y, rect.getWidth(), rect.getHeight());
				} else {
					rect.setFrame(x, y, rect.getWidth(), rect.getHeight());
				}
			}
			if (rect == null) {
				rect = new Rectangle2D.Double(x, y, 0, 0);
				setBounds(cell, rect);
			}
		}
	}

	/**
	 * Moved the specified cell by the specified x and y co-ordinate amounts
	 * 
	 * @param cell
	 *            the cell to be moved
	 * @param dx
	 *            the amount by which the cell will be translated in the x-axis
	 * @param dy
	 *            the amount by which the cell will be translated in the y-axis
	 */
	@SuppressWarnings("unchecked")
	public void translate(Object cell, double dx, double dy) {
		Rectangle2D rect = getBounds(cell);
		if (isMoveable(cell) && rect != null) {
			int numChildCells = model.getChildCount(cell);
			boolean hasChildren = false;
			for (int i = 0; i < numChildCells; i++) {
				Object childCell = model.getChild(cell, i);
				if (DefaultGraphModel.isVertex(model, childCell)
				// BVD:REMOVED
						// && graphLayoutCache.isVisible(childCell)
						&& cell != childCell) {
					translate(childCell, dx, dy);
					hasChildren = true;
				} else if (model.isEdge(childCell) && cell != childCell) {
					hasChildren = true;
				}
			}

			if (!hasChildren) {
				// these children are not ports
				if (DefaultGraphModel.isVertex(model, cell)) {
					rect.setFrame(rect.getX() + dx, rect.getY() + dy, rect
							.getWidth(), rect.getHeight());
				} else {
					// ADDED BY BVD. NEEDED TO MOVE INTERNAL POINTS OF EDGES
					// AROUND
					ArrayList cells = new ArrayList(1);
					cells.add(cell);
					scale(cells, 1, 1, dx, dy);
				}
			}
		}
	}

	public GraphModel getModel() {
		return model;
	}

	public GraphLayoutCache getCache() {
		return graphLayoutCache;
	}

	/**
	 * Obtains the maximum width or height dimension of any of the vertices in
	 * the specified collection
	 * 
	 * @param vertices
	 *            collection of vertices to be analysed
	 * @return the maximum width or height of any of the vertices
	 */
	public Dimension getMaxSize(Collection vertices) {
		// Maximum width or height
		Dimension d = new Dimension(0, 0);
		// Iterate over all vertices
		Iterator it = vertices.iterator();
		while (it.hasNext()) {
			Dimension2D size = getSize(it.next());
			// Update Maximum
			if (size != null)
				d.setSize(Math.max(d.getWidth(), size.getWidth()), Math.max(d
						.getHeight(), size.getHeight()));
		}
		return d;
	}

	/**
	 * Sets the current size of the specified cell.
	 * 
	 * @param cell
	 *            the cell whose size is to be set
	 * @param width
	 *            the new width of the cell
	 * @param height
	 *            the new height of the cell
	 */
	public void setSize(Object cell, double width, double height) {
		Rectangle2D rect = getBounds(cell);
		rect.setFrame(rect.getX(), rect.getY(), width, height);
	}

	/**
	 * Return the size of the specified cell
	 * 
	 * @param cell
	 *            the cell whose size is to be returned
	 * @return Returns the current size of the specified cell.
	 */
	public Dimension2D getSize(Object cell) {
		Rectangle2D rect = getBounds(cell);
		return new Dimension((int) rect.getWidth(), (int) rect.getHeight());
	}

	/**
	 * Returns the points of the specified edge. The list may contain PortView
	 * instances. Do a typecheck when iterating through the elements of this
	 * list, and use PortView.getLocation to get the position of the port.
	 * 
	 * @param edge
	 *            the cell whose points are returned
	 * @return Returns the points of the specified edge
	 */
	public List getPoints(Object edge) {
		Map map = getAttributes(edge);
		List points = GraphConstants.getPoints(map);
		if (points == null) {
			points = new ArrayList(4);
			points.add(new AttributeMap.SerializablePoint2D(10, 10));
			points.add(new AttributeMap.SerializablePoint2D(20, 20));
		}
		return points;
	}

	/**
	 * Sets the points of the specified edge
	 * 
	 * @param edge
	 *            the edge whose points are to be set
	 * @param points
	 *            the new list of points for the edge
	 */
	public void setPoints(Object edge, List points) {
		Map map = getAttributes(edge);
		GraphConstants.setPoints(map, points);
	}

	/**
	 * Disables per-edge on the specified edge
	 * 
	 * @param edge
	 *            the edge to have per-edge routing disabled
	 */
	public void disableRouting(Object edge) {
		Map map = getAttributes(edge);
		GraphConstants.setRemoveAttributes(map,
				new Object[] { GraphConstants.ROUTING });
	}

	/**
	 * Returns the edges between two specified ports or two specified vertices.
	 * If directed is true then <code>cell1</code> must be the source of the
	 * returned edges.
	 * 
	 * @param cell1
	 *            the first of the pair of cells to find edges between
	 * @param cell2
	 *            the second of the pair of cells to find edges between
	 * @param directed
	 *            whether or not only edges going from <code>cell1</code> to
	 *            <code>cell2</code> should be returned and not edges in the
	 *            other direction
	 */
	public Object[] getEdgesBetween(Object cell1, Object cell2, boolean directed) {
		if (graphLayoutCache != null && graphLayoutCache.isPartial()
				&& edgePromotion) {
			Set cells1 = getHiddenChildren(cell1, true);
			Set cells2 = getHiddenChildren(cell2, true);

			// Optimise for the standard case of no child cells
			if (cells1.size() == 1 && cells2.size() == 1) {
				return DefaultGraphModel.getEdgesBetween(model, cell1, cell2,
						directed);
			}
			// The object array to be returned
			Object[] edgesBetween = null;
			Iterator iter1 = cells1.iterator();
			while (iter1.hasNext()) {
				Object tempCell1 = iter1.next();
				Iterator iter2 = cells2.iterator();
				while (iter2.hasNext()) {
					Object tempCell2 = iter2.next();
					Object[] edges = DefaultGraphModel.getEdgesBetween(model,
							tempCell1, tempCell2, directed);
					if (edges.length > 0) {
						if (edgesBetween == null) {
							edgesBetween = edges;
						} else {
							// need to copy everything into a new array
							Object[] newArray = new Object[edges.length
									+ edgesBetween.length];
							System.arraycopy(edgesBetween, 0, newArray, 0,
									edgesBetween.length);
							System.arraycopy(edges, 0, newArray,
									edgesBetween.length, edges.length);
							edgesBetween = newArray;
						}
					}
				}
			}
			return edgesBetween;

		} else {
			return DefaultGraphModel.getEdgesBetween(model, cell1, cell2,
					directed);
		}
	}

	/**
	 * Divides the graph into groups of sibling vertices, vertices that share
	 * the same parent. This is mostly used for layouting of cell relative to
	 * their group context.
	 * 
	 */
	protected void determineLayoutHierarchies(int orientation) {
		int index = 0;
		if (model != null) {
			groupHierarchies = new ArrayList();
			groupOrientations = new ArrayList();
			Set rootsSet = null;
			Object[] modelRoots = DefaultGraphModel.getRoots(model);
			for (int i = 0; i < modelRoots.length; i++) {
				if (DefaultGraphModel.isVertex(model, modelRoots[i])) {
					populateGroupHierarchies(modelRoots[i]);
					if (rootsSet == null) {
						rootsSet = new LinkedHashSet();
					}
					rootsSet.add(modelRoots[i]);
				}
			}
			if (rootsSet != null) {
				groupHierarchies.add(rootsSet);
				groupOrientations.add(orientation);
			}

		}
	}

	/**
	 * Creates a set of sibling vertices and adds them to the group hierarchy
	 * collection. The list of hierarchies will naturally form in an order
	 * 
	 * @param vertex
	 *            The parent vertex to the returned vertices
	 */
	protected void populateGroupHierarchies(Object vertex) {
		LinkedHashSet result = null;
		int orientation = GraphConstants.getOrientation(model
				.getAttributes(vertex));
		if (vertex != null) {
			// vertex is the parent. Let's check if Orientation is set
			for (int i = 0; i < model.getChildCount(vertex); i++) {
				Object child = model.getChild(vertex, i);
				if (DefaultGraphModel.isVertex(model, child)) {
					if (result == null) {
						result = new LinkedHashSet();
					}
					result.add(child);
					populateGroupHierarchies(child);
				}
			}
		}
		if (groupHierarchies == null) {
			groupHierarchies = new ArrayList();
			groupOrientations = new ArrayList();
		}
		if (result != null) {
			groupHierarchies.add(result);
			groupOrientations.add(orientation);
		}
	}

	/**
	 * Returns the number of root vertices to be used by tree layouts for tree
	 * traversal.
	 * 
	 * @return the number of root vertices to be used by tree layouts
	 */
	public int getRootCount() {
		return roots.size();
	}

	/**
	 * Returns the root at <code>index</code> to be used by tree layouts for
	 * tree traversal.
	 * 
	 * @return the root vertex to be used by tree layouts
	 * 
	 * @see #dfs(Object, JGraphFacade.CellVisitor)
	 */
	public Object getRootAt(int index) {
		return roots.get(index);
	}

	/**
	 * Returns true if <code>cell</code> is a root.
	 * 
	 * @param cell
	 *            the cell to test
	 * 
	 * @return Returns true if <code>cell</code> is a root
	 */
	public boolean isRoot(Object cell) {
		return roots.contains(cell);
	}

	/**
	 * Returns the list of root vertices.
	 * 
	 * @return Returns the {@link #roots}
	 */
	public List getRoots() {
		return roots;
	}

	/**
	 * @param roots
	 *            The roots to set.
	 */
	public void setRoots(List roots) {
		this.roots = roots;
	}

	/**
	 * @return Returns the directed.
	 */
	public boolean isDirected() {
		return directed;
	}

	/**
	 * @param directed
	 *            The directed to set.
	 */
	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	/**
	 * @return Returns the order.
	 */
	public Comparator getOrder() {
		return order;
	}

	/**
	 * @param order
	 *            The order to set.
	 */
	public void setOrder(Comparator order) {
		this.order = order;
	}

	/**
	 * @return Returns the ignoresCellsInGroups.
	 */
	public boolean IsIgnoresCellsInGroups() {
		return ignoresCellsInGroups;
	}

	/**
	 * @param ignoresCellsInGroups
	 *            Sets ignoresCellsInGroups.
	 */
	public void setIgnoresCellsInGroups(boolean ignoresCellsInGroups) {
		this.ignoresCellsInGroups = ignoresCellsInGroups;
	}

	/**
	 * @return Returns the ignoresHiddenCells.
	 */
	public boolean isIgnoresHiddenCells() {
		return ignoresHiddenCells;
	}

	/**
	 * The GraphLayoutCache instance on the JGraphFacade object must be set
	 * correctly in order to change this flag. If the graphLayoutCache is null,
	 * this flag will be forced to false
	 * 
	 * @param ignoresHiddenCells
	 *            The ignoresHiddenCells to set.
	 */
	public void setIgnoresHiddenCells(boolean ignoresHiddenCells) {
		if (graphLayoutCache != null) {
			this.ignoresHiddenCells = ignoresHiddenCells;
		} else {
			this.ignoresHiddenCells = false;
		}
	}

	/**
	 * @return Returns the ignoresUnconnectedCells.
	 */
	public boolean isIgnoresUnconnectedCells() {
		return ignoresUnconnectedCells;
	}

	/**
	 * @param ignoresUnconnectedCells
	 *            The ignoresUnconnectedCells to set.
	 */
	public void setIgnoresUnconnectedCells(boolean ignoresUnconnectedCells) {
		this.ignoresUnconnectedCells = ignoresUnconnectedCells;
	}

	/**
	 * @return Returns the edgePromotion.
	 */
	public boolean isEdgePromotion() {
		return edgePromotion;
	}

	/**
	 * @param edgePromotion
	 *            The edgePromotion to set.
	 */
	public void setEdgePromotion(boolean edgePromotion) {
		this.edgePromotion = edgePromotion;
	}

	/**
	 * @return Returns the verticesFilter.
	 */
	public Set getVerticesFilter() {
		return verticesFilter;
	}

	/**
	 * @param verticesFilter
	 *            The verticesFilter to set.
	 */
	public void setVerticesFilter(Set verticesFilter) {
		this.verticesFilter = verticesFilter;
	}

	/**
	 * @return the groupHierarchies
	 */
	public List getGroupHierarchies() {
		return groupHierarchies;
	}

	/**
	 * @param groupHierarchies
	 *            the groupHierarchies to set
	 */
	public void setGroupHierarchies(List groupHierarchies) {
		this.groupHierarchies = groupHierarchies;
	}

	/**
	 * @return the circleRadiusFactor
	 */
	public double getCircleRadiusFactor() {
		return circleRadiusFactor;
	}

	/**
	 * @param circleRadiusFactor
	 *            the minCircleRadius to set
	 */
	public void setCircleRadiusFactor(double circleRadiusFactor) {
		this.circleRadiusFactor = circleRadiusFactor;
	}

	/**
	 * Performs a depth-first search of the input graph from the specified root
	 * cell using the specified visitor to extract the tree information.
	 * isVertex must return true on the passed-in root cell in order to
	 * continue.
	 * 
	 * @param root
	 *            the node to start the search from
	 * @param visitor
	 *            the visitor that defines the operations to be performed upon
	 *            the graph model
	 */
	public void dfs(Object root, CellVisitor visitor) {
		// DFS should return maximum depth
		if (isVertex(root)) {
			dfs(null, root, null, visitor, new HashSet(), 0, 0);
		}
	}

	/**
	 * Performs a depth-first search of the input graph from the specified root
	 * cell using the specified visitor to extract the tree information
	 * 
	 * @param parent
	 *            the parent of the current cell
	 * @param root
	 *            the node to start the search from
	 * @param previousSibling
	 *            the last neighbour of the current cell found
	 * @param visitor
	 *            the visitor that defines the operations to be performed upon
	 *            the graph model
	 * @param seen
	 *            the set of cells that have already been seen
	 * @param layer
	 *            the current layer of the tree
	 * @param sibling
	 *            the number of siblings to the current cell
	 */
	public void dfs(Object parent, Object root, Object previousSibling,
			CellVisitor visitor, Set seen, int layer, int sibling) {
		if (root != null && !seen.contains(root)) {
			seen.add(root);
			visitor.visit(parent, root, previousSibling, layer, sibling);
			// Recurse unseen neighbours
			sibling = 0;
			Object previous = null;
			Iterator it = getNeighbours(root, seen, ordered).iterator();
			while (it.hasNext()) {
				Object current = it.next();
				// Root check is O(|roots|)
				if (isVertex(current) && !isRoot(current)) {
					dfs(root, current, previous, visitor, seen, layer + 1,
							sibling);
					previous = current;
					sibling++;
				}
			}
		}
	}

	/**
	 * Performs a depth-first search of the input graph from the specified root
	 * cell using the specified visitor to extract the tree information
	 * 
	 * @param parent
	 *            the parent of the current cell
	 * @param root
	 *            the node to start the search from
	 * @param previousSibling
	 *            the last neighbour of the current cell found
	 * @param visitor
	 *            the visitor that defines the operations to be performed upon
	 *            the graph model
	 * @param seen
	 *            the set of cells that have already been seen
	 * @param layer
	 *            the current layer of the tree
	 * @param sibling
	 *            the number of siblings to the current cell
	 */
	public void dfs(Object parent, Object root, Object previousSibling,
			CellVisitor visitor, Set seen, Set ancestors, int layer, int sibling) {
		if (root != null) {
			if (parent != null) {
				ancestors.add(parent);
			}
			visitor.visit(parent, root, previousSibling, layer, sibling);
			if (!seen.contains(root)) {
				seen.add(root);
				// Recurse unseen neighbours
				sibling = 0;
				Object previous = null;
				Iterator it = getNeighbours(root, seen, true).iterator();
				while (it.hasNext()) {
					Object current = it.next();
					// Root check is O(|roots|)
					if (isVertex(current) && !isRoot(current)) {
						dfs(root, current, previous, visitor, seen,
								new HashSet(ancestors), layer + 1, sibling);
						previous = current;
						sibling++;
					}
				}
			}
		}
	}

	/**
	 * Performs a breath-first search of the input graph from the specified root
	 * cell using the specified visitor to extract the tree information.
	 * 
	 * @param visitor
	 *            the visitor that defines the operations to be performed upon
	 *            the graph model
	 */
	public void bfs(Object root, CellVisitor visitor) {
		// Track the number of cells on the next level, and the number of
		// unprocessed cells on the current level
		int numCellsCurrentLevel = 1;
		int numCellsNextLevel = 0;
		int currentLayer = 0;

		Stack cellStack = new Stack();
		cellStack.push(root);
		Set seen = new HashSet();

		while (!cellStack.isEmpty()) {
			Object current = cellStack.pop();
			if (!seen.contains(current)) {
				seen.add(root);
				visitor.visit(null, current, null, currentLayer, 0);
				// Recurse unseen neighbours
				Iterator it = getNeighbours(current, seen, true).iterator();
				while (it.hasNext()) {
					Object childCell = it.next();
					if (!seen.contains(childCell)) {
						cellStack.push(childCell);
						numCellsNextLevel++;
					}
				}
			}
			// Work out if we have finished the current level
			numCellsCurrentLevel--;
			if (--numCellsCurrentLevel <= 0) {
				numCellsCurrentLevel = numCellsNextLevel;
				numCellsNextLevel = 0;
				currentLayer++;
			}
		}
	}

	/**
	 * Utility method to update the array of tree roots in a graph. This sets
	 * all cells that have no incoming and one or more outgoing edges, or the
	 * cell with the largest difference between outgoing and incoming edges if
	 * no root cells exist.
	 */
	public void findTreeRoots() {
		Object[] vertices = getCells(getAll(), false, false).toArray();

		List roots = new ArrayList();
		int maxDiff = 0;
		Object root = null;
		for (int i = 0; i < vertices.length; i++) {
			int fanin = getIncomingEdges(vertices[i], null, true, false).size();
			int fanout = getOutgoingEdges(vertices[i], null, true, false)
					.size();
			if (fanin == 0)
				roots.add(vertices[i]);

			// Keeps a reference to the best matching cell, ie the one with the
			// greatest difference between outgoing and incoming edges in case
			// no real roots exist.
			int diff = fanout - fanin;
			if (diff >= maxDiff) {
				root = vertices[i];
				maxDiff = diff;
			}
		}

		// Returns the best match in case no real roots exist
		if (roots.isEmpty() && root != null)
			roots.add(root);
		this.roots = roots;
	}

	/**
	 * Defines the interface that visitors use to perform operations upon the
	 * graph information during depth first search (dfs) or other tree-traversal
	 * strategies implemented by subclassers.
	 */
	public interface CellVisitor {

		/**
		 * The method within which the visitor will perform operations upon the
		 * graph model
		 * 
		 * @param parent
		 *            the parent cell the current cell
		 * @param cell
		 *            the current cell visited
		 * @param previousSibling
		 *            the last neighbour cell found
		 * @param layer
		 *            the current layer of the tree
		 * @param sibling
		 *            the number of sibling to the current cell found
		 */
		public void visit(Object parent, Object cell, Object previousSibling,
				int layer, int sibling);
	}

	/**
	 * A default comparator for ordering cell views. Returns the order of the
	 * cells as ordered in <code>roots</code> in the model. Enables layouts with
	 * levels to be laid out deterministically. <b>Be very careful</b> using the
	 * default comparator on the default graph model,
	 * <code>getIndexOfRoot</code> has linear performance and so sorting the
	 * entire model roots will have quadratic performance.
	 */
	public class DefaultComparator implements Comparator {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
        public int compare(Object c1, Object c2) {
			Object p1 = model.getParent(c1);
			Object p2 = model.getParent(c2);
			int index1 = (p1 == null) ? model.getIndexOfRoot(c1) : model
					.getIndexOfChild(p1, c1);
			int index2 = (p2 == null) ? model.getIndexOfRoot(c2) : model
					.getIndexOfChild(p2, c2);
			return new Integer(index1).compareTo(new Integer(index2));
		}

	}
	
    // Bruce experimenting 27.11.2019
    public class DefaultComparator2 implements Comparator {

        /*
         * (non-Javadoc)
         * 
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Object c1, Object c2) {
            String value1 = model.getValue(c1).toString();
            String value2 = model.getValue(c2).toString();
            
            String value1Compare = value1;
            int brPos = value1.indexOf("<br>");
            if (brPos >= 0) value1Compare = value1.substring(6,brPos); 

            String value2Compare = value2;
            brPos = value2.indexOf("<br>");
            if (brPos >= 0) value2Compare = value2.substring(6,brPos); 

            return value1Compare.compareTo(value2Compare);
//          Object p1 = model.getParent(c1);
//          Object p2 = model.getParent(c2);
//          int index1 = (p1 == null) ? model.getIndexOfRoot(c1) : model
//                  .getIndexOfChild(p1, c1);
//          int index2 = (p2 == null) ? model.getIndexOfRoot(c2) : model
//                  .getIndexOfChild(p2, c2);
//          return new Integer(index1).compareTo(new Integer(index2));
        }

    }	

	/**
	 * @return Returns the ordered.
	 */
	public boolean isOrdered() {
		return ordered;
	}

	/**
	 * @param ordered
	 *            The ordered to set.
	 */
	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	/**
	 * Sets the logging level of this class
	 * 
	 * @param level
	 *            the logging level to set
	 */
	public void setLoggerLevel(Level level) {
		try {
			logger.setLevel(level);
		} catch (SecurityException e) {
			// Probably running in an applet
		}
	}
}
