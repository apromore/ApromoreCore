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
package org.apromore.jgraph.layout.hierarchical;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingConstants;

import org.apromore.jgraph.graph.DefaultGraphModel;
import org.apromore.jgraph.layout.JGraphFacade;
import org.apromore.jgraph.layout.JGraphLayout;
import org.apromore.jgraph.layout.JGraphLayoutProgress;
import org.apromore.jgraph.layout.hierarchical.model.JGraphHierarchyModel;

/**
 * The top level compound layout of the hierarchical layout. The individual
 * elements of the layout are called in sequence. This layout does not inherit
 * from <code>JGraphCompoundLayout</code> as a complete model of the hierarchy
 * needs to be passed into each step and
 */
public class JGraphHierarchicalLayout implements JGraphLayout,
		JGraphLayout.Stoppable {

	private static final double INITIAL_X_POSITION = 100.0;

	/**
	 * The spacing buffer added between cells on the same layer
	 */
	protected double intraCellSpacing = 30.0;

	/**
	 * The spacing buffer added between cell on adjacent layers
	 */
	protected double interRankCellSpacing = 50.0;

	/**
	 * The spacing buffer between unconnected hierarchies
	 */
	protected double interHierarchySpacing = 60.0;

	/**
	 * The distance between each parallel edge on each ranks for long edges
	 */
	protected double parallelEdgeSpacing = 10.0;

	/**
	 * The position of the root node(s) relative to the laid out graph in
	 */
	protected int orientation = SwingConstants.NORTH;

	/**
	 * Whether or not to perform local optimisations and iterate multiple times
	 * through the algorithm
	 */
	protected boolean fineTuning = true;

	/**
	 * Whether or not to pull together sections of layout into empty space
	 */
	protected boolean compactLayout = false;

	/**
	 * Whether or not cells are ordered according to the order in the graph
	 * model. Defaults to false since sorting usually produces quadratic
	 * performance. Note that since MxGraph returns edges in a deterministic
	 * order, it might be that this layout is always deterministic using that
	 * JGraph regardless of this flag setting (i.e. leave it false in that case)
	 */
	protected boolean deterministic = false;

	/**
	 * Whether or not to fix the position of the root cells. Keep in mind to
	 * turn off features such as move to origin when fixing the roots, move to
	 * origin usually overrides this flag (in JGraph it does).
	 */
	protected boolean fixRoots = false;

	/**
	 * Whether or not the initial scan of the graph to determine the layer
	 * assigned to each vertex starts from the sinks or source (the sinks being
	 * vertices with the fewest, preferable zero, outgoing edges and sources
	 * same with incoming edges). Starting from either direction can tight the
	 * layout up and also produce better results for certain types of graphs. If
	 * the result for the default is not good enough try a few sample layouts
	 * with the value false to see if they improve
	 */
	protected boolean layoutFromSinks = false;

	/**
	 * The internal model formed of the layout
	 */
	protected JGraphHierarchyModel model = null;

	/**
	 * A cycle pre-processing stage
	 */
	protected JGraphHierarchicalLayoutStep cycleStage = null;

	/**
	 * The first stage of a Sugiyama layout
	 */
	protected JGraphHierarchicalLayoutStep layeringStage = null;

	/**
	 * The second stage of a Sugiyama layout
	 */
	protected JGraphHierarchicalLayoutStep crossingStage = null;

	/**
	 * The third stage of a Sugiyama layout
	 */
	protected JGraphHierarchicalLayoutStep placementStage = null;

	/**
	 * The layout progress bar
	 */
	protected JGraphLayoutProgress progress = new JGraphLayoutProgress();

	/** The logger for this class */
	private static Logger logger = Logger
			.getLogger("com.jgraph.layout.hierarchical.JGraphHierarchicalLayout");

	/**
	 * The default constructor
	 * 
	 */
	public JGraphHierarchicalLayout() {
		this(true);
	}

	/**
	 * Creates a hierarchical layout, constructing the components of the layout
	 * stages
	 * 
	 * @param deterministic
	 *            whether or not this layout should be deterministic
	 */
	public JGraphHierarchicalLayout(boolean deterministic) {
		this.deterministic = deterministic;
	}

	/**
	 * The API method used to exercise the layout upon the facade description
	 * and produce a separate description of the vertex position and edge
	 * routing changes made. It runs each stage of the layout that has been
	 * created.
	 * 
	 * @param facade
	 *            the facade object that describes and filters the graph to be
	 *            acted upon
	 */
	@Override
    public void run(JGraphFacade facade) {
		boolean rootsWereDetermined = false;
		if (facade.getRoots() == null || facade.getRoots().size() == 0) {
			/*
			 * Compute proper sources, that is, vertices without incoming edges.
			 */
			List sourceVertices = new ArrayList();
			for (Object vertex : facade.getVertices()) {
				if (facade.getIncomingEdges(vertex, null, true, false)
						.isEmpty()) {
					sourceVertices.add(vertex);
				}
			}
			HashSet unreachableVertices = new HashSet(facade.getVertices());
			unreachableVertices.removeAll(DefaultGraphModel.getDescendants(
					facade.getModel(), sourceVertices.toArray()));

			while (!unreachableVertices.isEmpty()) {
				/*
				 * Some vertices are not reachable from the sources found so
				 * far. Create a pseudo source, that is, an unreachable node
				 * with the maximal difference between numbers of outgoing
				 * versus incoming edges.
				 */
				int maxDiff = 0;
				Object pseudoSource = null;
				for (Object vertex : unreachableVertices) {
					int nrOfIncomingEdges = facade.getIncomingEdges(vertex, null, true,
							false).size();
					int nrOfOutgoingEdges = facade.getOutgoingEdges(vertex, null, true,
							false).size();
					if (pseudoSource == null || nrOfOutgoingEdges - nrOfIncomingEdges > maxDiff) {
						maxDiff = nrOfOutgoingEdges - nrOfIncomingEdges;
						pseudoSource = vertex;
					}
				}
				sourceVertices.add(pseudoSource);
				List newSources = new ArrayList();
				newSources.add(pseudoSource);
				unreachableVertices.removeAll(DefaultGraphModel.getDescendants(
						facade.getModel(), newSources.toArray()));
			}
			
			/*
			 * All vertices are reachable from some source.
			 * These sources will be the roots for the layout.
			 */
			facade.setRoots(sourceVertices);
			rootsWereDetermined = true;
		}
		List descendants = DefaultGraphModel.getDescendants(facade.getModel(),
				facade.getRoots().toArray());

		// List descendants =
		// DefaultGraphModel.getDescendants(facade.getModel(),
		// facade.getVertices().toArray());
		// // If the roots have to be worked out for this layout, clear the
		// roots
		// // after the layout has finished. Not clearing them might cause
		// // another run through with the same facade to go wrong
		// // If the roots of this layout are not set, find them
		// if (facade.getRoots() == null || facade.getRoots().size() == 0) {
		//
		// List sources = new ArrayList();
		// List nextBestSources = new ArrayList();
		// int maxDiff = 0;
		// Object[] vertices = facade.getVertices().toArray();
		// for (int i = 0; i < vertices.length; i++) {
		// // BVD: Begin changes
		// Set inNodes = new HashSet();
		// List inEdges = facade.getIncomingEdges(vertices[i], null, true,
		// false);
		// Iterator it = inEdges.iterator();
		// while (it.hasNext()) {
		// Object v = DefaultGraphModel.getSourceVertex(
		// facade.getModel(), it.next());
		// // check if v is in the descendants
		// if (descendants.contains(v)) {
		// inNodes.add(v);
		// }
		// }
		// int numIncomingEdges = inNodes.size();
		// // BVD: End changes. We stick to the edges in the facade's
		// // vertices
		//
		// if (numIncomingEdges == 0) {
		// sources.add(vertices[i]);
		// } else {
		// // Keeps a reference to the best matching cell(s), ie the
		// // one(s) with the
		// // greatest difference between outgoing and incoming edges
		// // in case
		// // no real roots exist.
		// // BVD: Begin changes
		// Set outNodes = new HashSet();
		// List outEdges = facade.getOutgoingEdges(vertices[i], null,
		// true, false);
		// it = outEdges.iterator();
		// while (it.hasNext()) {
		// Object v = DefaultGraphModel.getTargetVertex(
		// facade.getModel(), it.next());
		// if (descendants.contains(v)) {
		// outNodes.add(v);
		// }
		// }
		// int numOutgoingEdges = outNodes.size();
		// // BVD: End changes. We stick to the edges in the facade's
		// // vertices
		//
		// int diff = numOutgoingEdges - numIncomingEdges;
		// if (diff > maxDiff) {
		// nextBestSources = new ArrayList();
		// nextBestSources.add(vertices[i]);
		// maxDiff = diff;
		// } else if (diff == maxDiff) {
		// nextBestSources.add(vertices[i]);
		// }
		// }
		// }
		// // HV: Add the next-best sources as proper sources. This seems to
		// // prevent cyclic subgraphs to get cluttered in the left upper
		// // corner.
		// sources.addAll(nextBestSources);
		// if (sources.size() > 0) {
		// facade.setRoots(sources);
		// } else if (nextBestSources.size() > 0) {
		// facade.setRoots(nextBestSources);
		// } else {
		// // Must be no vertices at all, don't do anything
		// return;
		// }
		// rootsWereDetermined = true;
		// }

		// Seperate out unconnected hierarchys
		List hierarchyVertices = new ArrayList();
		// Keep track of one root in each hierarchy in case it's fixed position
		List fixedRoots = null;
		List rootLocations = null;
		List affectedEdges = null;
		if (fixRoots) {
			fixedRoots = new ArrayList();
			rootLocations = new ArrayList();
			affectedEdges = new ArrayList();
		}
		Object[] roots = facade.getRoots().toArray();

		// System.out.println("--------------------------------");
		// System.out.println("V: " +
		// Arrays.toString(facade.getVertices().toArray()));
		// System.out.println("R: " + Arrays.toString(roots));

		for (int i = 0; i < roots.length; i++) {
			// First check if this root appears in any of the previous vertex
			// sets
			boolean newHierarchy = true;
			Iterator iter = hierarchyVertices.iterator();
			while (newHierarchy && iter.hasNext()) {
				if (((Set) iter.next()).contains(roots[i])) {
					newHierarchy = false;
				}
			}
			if (newHierarchy) {
				// Obtains set of vertices connected to this root
				Stack cellsStack = new Stack();
				cellsStack.push(roots[i]);
				Set edgeSet = null;
				if (fixRoots) {
					fixedRoots.add(roots[i]);
					Point2D location = facade.getLocation(roots[i]);
					rootLocations.add(location);
					edgeSet = new HashSet();
				}
				Set vertexSet = new HashSet();
				while (!cellsStack.isEmpty()) {
					Object cell = cellsStack.pop();
					if (!vertexSet.contains(cell)) {
						vertexSet.add(cell);
						boolean isDirected = facade.isDirected();
						facade.setDirected(false);

						// BVD: CHANGED
						// Now only keep the neighbours if they are in the
						// vertices of this facade.
						// or, or if their parent is in case of edgePromotion.
						List neighbours = facade.getNeighbours(cell, vertexSet,
								true);
						Set neighboursToDo = new HashSet();
						Iterator it = neighbours.iterator();
						while (it.hasNext()) {
							Object n = it.next();
							n = findParent(facade, descendants, n);
							if (n != null && n != cell) {
								neighboursToDo.add(n);
							}
							// if (facade.getVertices().contains(n)) {
							// neighboursToDo.add(n);
							// } else if (facade.isEdgePromotion() &&
							// facade.getModel().getParent(n) != null) {
							// n = facade.getModel().getParent(n);
							// if (facade.getVertices().contains(n)) {
							// neighboursToDo.add(n);
							// }
							// }
						}
						it = neighboursToDo.iterator();

						if (fixRoots) {
							edgeSet.addAll(facade.getIncomingEdges(cell, null,
									true, false));
						}
						facade.setDirected(isDirected);
						while (it.hasNext()) {
							cellsStack.push(it.next());
						}
					}
				}
				hierarchyVertices.add(vertexSet);
				if (fixRoots) {
					affectedEdges.add(edgeSet);
				}
			}
		}

		// Perform a layout for each seperate hierarchy
		progress.reset(hierarchyVertices.size() * 4 + 1);
		int progressCount = 1;
		// Track initial coordinate x-positioning
		double initialX = INITIAL_X_POSITION;
		Iterator iter = hierarchyVertices.iterator();
		int i = 0;
		while (iter.hasNext()) {
			Set vertexSet = (Set) iter.next();

			model = new JGraphHierarchyModel(facade, vertexSet.toArray(),
					false, deterministic, layoutFromSinks);

			cycleStage = new JGraphMinimumCycleRemover();
			model = cycleStage.run(facade, model);
			if (model == null) {
				throw new RuntimeException(
						"Could not remove cycles in hierarchical layout");
			}
			progress.setProgress(++progressCount);

			if (!progress.isStopped()) {
				layeringStage = new JGraphLongestPathLayering();
				model = layeringStage.run(facade, model);
			}
			progress.setProgress(++progressCount);

			if (!progress.isStopped()) {
				crossingStage = new JGraphMedianHybridCrossingReduction();
				model = crossingStage.run(facade, model);
			}
			progress.setProgress(++progressCount);

			if (!progress.isStopped()) {
				placementStage = new JGraphCoordinateAssignment(
						intraCellSpacing, interRankCellSpacing, orientation,
						compactLayout, initialX, parallelEdgeSpacing);
				model = placementStage.run(facade, model);
				initialX = ((JGraphCoordinateAssignment) placementStage)
						.getLimitX() + interHierarchySpacing;
			}
			progress.setProgress(++progressCount);
			if (fixRoots) {
				// Reposition roots and their hierarchies using their bounds
				// stored eariler
				Object root = fixedRoots.get(i);
				Point2D oldLocation = (Point2D) rootLocations.get(i);
				Point2D newLocation = facade.getLocation(root);
				double diffX = oldLocation.getX() - newLocation.getX();
				double diffY = oldLocation.getY() - newLocation.getY();
				facade.translateCells(vertexSet, diffX, diffY);
				// Also translate connected edges
				Set connectedEdges = (Set) affectedEdges.get(i++);
				facade.translateCells(connectedEdges, diffX, diffY);
			}
		}
		if (rootsWereDetermined == true) {
			facade.setRoots(null);
		}
		// hide all vertices
		for (Object o : facade.getVertices()) {
			facade.getCache().setVisible(o, false);
		}
	}

	private Object findParent(JGraphFacade facade, List vertices, Object cell) {
		if (cell == null) {
			return null;
		}

		Object includedParent = findParent(facade, vertices, facade.getModel()
				.getParent(cell));

		if (includedParent == null) {
			// no parent exists
			if (vertices.contains(cell)) {
				return cell;
			} else {
				return null;
			}
		} else {
			return includedParent;
		}
	}

	/**
	 * Returns <code>Hierarchical</code>, the name of this algorithm.
	 */
	@Override
    public String toString() {
		return "Hierarchical";
	}

	/**
	 * @return Returns the progress.
	 */
	@Override
    public JGraphLayoutProgress getProgress() {
		return progress;
	}

	/**
	 * @return Returns the intraCellSpacing.
	 */
	public double getIntraCellSpacing() {
		return intraCellSpacing;
	}

	/**
	 * @param intraCellSpacing
	 *            The intraCellSpacing to set.
	 */
	public void setIntraCellSpacing(double intraCellSpacing) {
		this.intraCellSpacing = intraCellSpacing;
	}

	/**
	 * @return Returns the interRankCellSpacing.
	 */
	public double getInterRankCellSpacing() {
		return interRankCellSpacing;
	}

	/**
	 * @param interRankCellSpacing
	 *            The interRankCellSpacing to set.
	 */
	public void setInterRankCellSpacing(double interRankCellSpacing) {
		this.interRankCellSpacing = interRankCellSpacing;
	}

	/**
	 * @return Returns the orientation.
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation
	 *            The orientation to set.
	 */
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return Returns the interHierarchySpacing.
	 */
	public double getInterHierarchySpacing() {
		return interHierarchySpacing;
	}

	/**
	 * @param interHierarchySpacing
	 *            The interHierarchySpacing to set.
	 */
	public void setInterHierarchySpacing(double interHierarchySpacing) {
		this.interHierarchySpacing = interHierarchySpacing;
	}

	public double getParallelEdgeSpacing() {
		return parallelEdgeSpacing;
	}

	public void setParallelEdgeSpacing(double parallelEdgeSpacing) {
		this.parallelEdgeSpacing = parallelEdgeSpacing;
	}

	/**
	 * @return Returns the fineTuning.
	 */
	public boolean isFineTuning() {
		return fineTuning;
	}

	/**
	 * @param fineTuning
	 *            The fineTuning to set.
	 */
	public void setFineTuning(boolean fineTuning) {
		this.fineTuning = fineTuning;
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

	/**
	 * @return Returns the compactLayout.
	 */
	public boolean isCompactLayout() {
		return compactLayout;
	}

	/**
	 * @param compactLayout
	 *            The compactLayout to set.
	 */
	public void setCompactLayout(boolean compactLayout) {
		this.compactLayout = compactLayout;
	}

	/**
	 * @return Returns the fixRoots.
	 */
	public boolean isFixRoots() {
		return fixRoots;
	}

	/**
	 * @param fixRoots
	 *            The fixRoots to set.
	 */
	public void setFixRoots(boolean fixRoots) {
		this.fixRoots = fixRoots;
	}

	public boolean isLayoutFromSinks() {
		return layoutFromSinks;
	}

	public void setLayoutFromSinks(boolean layoutFromSinks) {
		this.layoutFromSinks = layoutFromSinks;
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
