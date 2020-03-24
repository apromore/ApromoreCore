/*
 * @(#)GraphContext.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.jgraph.JGraph;

/*
 * This object is used for interactive operations such as move and copy. It
 * offers two basic operations: 1. Create temporary views for a set of cells,
 * and 2. Create temporary views for a set of edges that are connected to a set
 * of cells.<p> <strong>Note:</strong>The views are consistent subgraphs.
 * Consequently, if an edge points to a selected and an unselected port, the
 * selected port is replaced by its temporary view, but the unselected port is
 * not.
 * 
 * @version 1.0 1/1/02 @author Gaudenz Alder
 */

public class GraphContext implements CellMapper {

	/**
	 * Switch to enable the preview of edge groups, that is, edges that 1 or
	 * more children, as part of the context cells.
	 */
	public static boolean PREVIEW_EDGE_GROUPS = false;

	/** Reference to the parent graph. */
	protected JGraph graph;

	/** Reference to the graphs GraphLayoutCache. */
	protected transient GraphLayoutCache graphLayoutCache;

	/** Reference to the cells. */
	protected Object[] cells;

	/** Set of all cells including all descendants. */
	protected Set allCells, cellSet;

	/** Number of all descendants without ports. */
	protected int cellCount;

	/** Map of (cell, view) pairs including ports. */
	protected Map views = new Hashtable();

	/**
	 * Constructs a graph context for <code>cells</code> with respect to the
	 * connections defined in the model, and the views in the view of
	 * <code>graph</code>.
	 */
	public GraphContext(JGraph graph, Object[] cells) {
		GraphModel model = graph.getModel();
		allCells = new HashSet(DefaultGraphModel.getDescendants(model, cells));
		graphLayoutCache = graph.getGraphLayoutCache();
		this.graph = graph;
		this.cells = cells;
		// Count Visible Non-Port Cells
		cellSet = new HashSet();
		Iterator it = allCells.iterator();
		while (it.hasNext()) {
			Object cell = it.next();
			if (graphLayoutCache.isVisible(cell)) {
				cellSet.add(cell);
				if (!model.isPort(cell))
					cellCount++;
			}
		}
	}

	/**
	 * Returns <code>true</code> if this object contains no cells.
	 */
	public boolean isEmpty() {
		return (cells == null || cells.length == 0);
	}

	/**
	 * Returns the number of all objects (cells and children) in this object.
	 */
	public int getDescendantCount() {
		return cellCount;
	}

	/**
	 * Returns the graph that was passed to the constructor.
	 */
	public JGraph getGraph() {
		return graph;
	}

	/**
	 * Returns the array that was passed to the constructor.
	 */
	public Object[] getCells() {
		return cells;
	}

	/**
	 * Returns <code>true</code> if <code>node</code> or one of its
	 * ancestors is contained in this object and visible in the original graph.
	 */
	public boolean contains(Object node) {
		return cellSet.contains(node);
	}

	/**
	 * Returns an new consistent array of views for <code>cells</code>.
	 */
	public CellView[] createTemporaryCellViews() {
		CellView[] cellViews = new CellView[cells.length];
		for (int i = 0; i < cells.length; i++)
			// Get View For Cell
			cellViews[i] = getMapping(cells[i], true);
		return cellViews;
	}

	/**
	 * Returns an new consistent array of views for the ports.
	 */
	public CellView[] createTemporaryPortViews() {
		GraphModel model = graph.getModel();
		ArrayList result = new ArrayList();
		Iterator it = allCells.iterator();
		while (it.hasNext()) {
			Object cand = it.next();
			if (model.isPort(cand)
					&& graph.getGraphLayoutCache().isVisible(cand))
				result.add(getMapping(cand, true));
		}
		// List -> CellView[] Conversion
		CellView[] array = new CellView[result.size()];
		result.toArray(array);
		return array;
	}

	/**
	 * Returns an new consistent array of views for the edges that are connected
	 * to and not contained in <code>cells</code>.
	 */
	public CellView[] createTemporaryContextViews() {
		return createTemporaryContextViews(cellSet);
	}

	/**
	 * Returns an new consistent array of views for the edges that are connected
	 * to and not contained in <code>cellSet</code>.
	 */
	public CellView[] createTemporaryContextViews(Set cellSet) {
		Object[] cells = cellSet.toArray();
		// Retrieve Edges From Model (recursively for edges connected
		// to edges connected to edges...)
		List result = new ArrayList();
		Set delta = DefaultGraphModel.getEdges(graph.getModel(), cells);
		do {
			// Iterate over Edges
			Iterator it = delta.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				CellView edge = graphLayoutCache.getMapping(obj, false);
				// If Edge not in cellset, add its view is visible in graphview
				if (!cellSet.contains(obj) && graphLayoutCache.isVisible(obj)
						&& edge != null
						&& (PREVIEW_EDGE_GROUPS || edge.isLeaf())) {
					// Note: Do not use getMapping, it ignores the create flag
					CellView preview = createMapping(obj);
					result.add(preview);
					// Create temporary children which refer to this
					// preview and adopt children in the preview
					CellView[] children = preview.getChildViews();
					for (int i=0; i<children.length; i++) {
						children[i] = createMapping(children[i].getCell());
					}
					// Adopts the children
					preview.refresh(graph.getGraphLayoutCache(), this, false);
				}
			}
			delta = DefaultGraphModel.getEdges(graph.getModel(), delta
					.toArray());
		} while (!delta.isEmpty());
		// List -> CellView[] Conversion
		CellView[] array = new CellView[result.size()];
		result.toArray(array);
		return array;
	}

	/**
	 * Returns the <code>CellView</code> that is mapped to <code>cell</code>
	 * in the graph context. New views are created based on whether cell is
	 * contained in the context. The <code>create</code>-flag is ignored.
	 */
	public CellView getMapping(Object cell, boolean create) {
		if (cell != null) {
			CellView view = (CellView) views.get(cell);
			if (view != null)
				return view;
			else if (contains(cell)
					|| (graph.getModel().isPort(cell) && create && graph
							.getGraphLayoutCache().isVisible(cell)))
				return createMapping(cell);
			else
				return graphLayoutCache.getMapping(cell, false);
		}
		return null;
	}

	public CellView createMapping(Object cell) {
		CellView view = graphLayoutCache.getFactory().createView(
				graph.getModel(), cell);
		putMapping(cell, view);
		view.refresh(graph.getGraphLayoutCache(), this, true); // Create Dependent Views
		// Fetch Attributes From Original View
		CellView src = graphLayoutCache.getMapping(cell, false);
		if (src != null) {
			view.changeAttributes(graphLayoutCache, (AttributeMap) src.getAttributes().clone());
			// Inserts portviews into points list
			view.refresh(graph.getGraphLayoutCache(), this, false);
		}
		return view;
	}

	/**
	 * Disconnects the edges in <code>cells</code> from the sources and
	 * targets that are not in this context and returns a ConnectionSet that
	 * defines the disconnection.
	 */
	public ConnectionSet disconnect(CellView[] cells) {
		ConnectionSet cs = new ConnectionSet();
		for (int i = 0; i < cells.length; i++) {
			if (cells[i] instanceof EdgeView) {
				EdgeView view = (EdgeView) cells[i];
				CellView port = view.getSource();
				if (GraphConstants.isDisconnectable(view.getAllAttributes())) {
					if (port != null
							&& GraphConstants.isDisconnectable(port
									.getParentView().getAllAttributes())
							&& !contains(port.getCell())) {
						view.setSource(null);
						cs.disconnect(view.getCell(), true);
					}
					port = view.getTarget();
					if (port != null
							&& GraphConstants.isDisconnectable(port
									.getParentView().getAllAttributes())
							&& !contains(port.getCell())) {
						view.setTarget(null);
						cs.disconnect(view.getCell(), false);
					}
				}
			}
		}
		return cs;
	}

	/**
	 * Associates <code>cell</code> with <code>view</code> in the graph
	 * context.
	 */
	public void putMapping(Object cell, CellView view) {
		views.put(cell, view);
	}

}