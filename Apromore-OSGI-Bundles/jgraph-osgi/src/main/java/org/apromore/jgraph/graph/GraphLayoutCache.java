/*
 * $Id: GraphLayoutCache.java,v 1.42 2009/08/12 11:39:37 david Exp $
 * 
 * Copyright (c) 2001-2009 JGraph Ltd
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;

import javax.swing.event.EventListenerList;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import org.apromore.jgraph.event.GraphLayoutCacheEvent;
import org.apromore.jgraph.event.GraphLayoutCacheListener;
import org.apromore.jgraph.event.GraphModelEvent;
import org.apromore.jgraph.util2.RectUtils;

/**
 * An object that defines the view of a graphmodel. This object maps between
 * model cells and views and provides a set of methods to change these views.
 * The view may also contain its own set of attributes and is therefore an
 * extension of an Observable, which may be observed by the GraphUI. It uses the
 * model to send its changes to the command history.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */
public class GraphLayoutCache implements CellMapper, Serializable {

	/**
	 * True if the cells should be auto-sized when their values change. Default
	 * is false.
	 */
	protected boolean autoSizeOnValueChange = false;

	/**
	 * Boolean indicating whether existing connections should me made visible if
	 * their sources or targets are made visible, given the opposite end of the
	 * edge is already visible or made visible, too. Default is true.
	 */
	protected boolean showsExistingConnections = true;

	/**
	 * Boolean indicating whether connections should be made visible when
	 * reconnected and their source and target port is visible. Default is true.
	 */
	protected boolean showsChangedConnections = true;

	/**
	 * Boolean indicating whether edited cells should be made visible if they
	 * are changed via
	 * {@link #edit(Map, ConnectionSet, ParentMap, UndoableEdit[])}. Default is
	 * false.
	 */
	protected boolean showsInvisibleEditedCells = false;

	/**
	 * Boolean indicating whether inserted should be made visible if they are
	 * inserted via
	 * {@link #insert(Object[], Map, ConnectionSet, ParentMap, UndoableEdit[])}.
	 * Default is true.
	 */
	protected boolean showsInsertedCells = true;

	/**
	 * Boolean indicating whether inserted edges should me made visible if their
	 * sources or targets are already visible. Default is true.
	 */
	protected boolean showsInsertedConnections = true;

	/**
	 * Boolean indicating whether existing connections should be hidden if their
	 * source or target and no parent of the ports is visible, either by hiding
	 * the cell or by changing the source or target of the edge to a hidden
	 * cell. Default is true.
	 */
	protected boolean hidesExistingConnections = true;

	/**
	 * Boolean indicating whether existing connections should be hidden if their
	 * source or target port is removed from the model. Default is false.
	 */
	protected boolean hidesDanglingConnections = false;

	/**
	 * Boolean indicating whether cellviews should be remembered once visible in
	 * this GraphLayoutCache. Default is true.
	 */
	protected boolean remembersCellViews = true;

	/**
	 * Boolean indicating whether inserted cells should automatically be
	 * selected. Default is true. This is ignored if the cache is partial. Note:
	 * Despite the name of this field the implementation is located in the
	 * BasicGraphUI.GraphModelHandler.graphChanged method.
	 */
	protected boolean selectsAllInsertedCells = false;

	/**
	 * Boolean indicating whether cells that are inserted using the local insert
	 * method should automatically be selected. Default is true. This is ignored
	 * if the cache is not partial and selectsAllInsertedCells is true, in which
	 * case the cells will be selected through another mechanism. Note: Despite
	 * the name of this field the implementation is located in the
	 * BasicGraphUI.GraphLayoutCacheObserver.changed method.
	 */
	protected boolean selectsLocalInsertedCells = false;

	/**
	 * Boolean indicating whether children should be moved to the parent group's
	 * origin on expand. Default is true.
	 */
	protected boolean movesChildrenOnExpand = true;

	/**
	 * Boolean indicating whether parents should be moved to the child area
	 * origin on collapse. Default is true.
	 */
	protected boolean movesParentsOnCollapse = true;

	/**
	 * Boolean indicating whether parents should always be resized to the child
	 * area on collapse. If false the size is only initially updated if it has
	 * not yet been assigned. Default is false.
	 */
	protected boolean resizesParentsOnCollapse = false;

	/**
	 * Specified the initial x- and y-scaling factor for initial collapsed group
	 * bounds. Default is 1.0, ie. no scaling.
	 */
	protected double collapseXScale = 1.0, collapseYScale = 1.0;

	/**
	 * Boolean indicating whether edges should be reconneted to visible parents
	 * on collapse/expand. Default is false.
	 * 
	 * @deprecated edges are moved to parent view and back automatically
	 */
	protected boolean reconnectsEdgesToVisibleParent = false;

	/**
	 * The list of listeners that listen to the GraphLayoutCache.
	 */
	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Reference to the graphModel
	 */
	protected GraphModel graphModel;

	/**
	 * Maps cells to views.
	 */
	protected Map mapping = new Hashtable();

	/**
	 * Maps cells to views. The hidden mapping is used to remembed cell views
	 * that are hidden, based on the remembersCellViews setting. hiddenMapping
	 * must use weak keys for the cells since when cells are removed
	 * hiddenMapping is not updated.
	 */
	protected transient Map hiddenMapping = new WeakHashMap();

	/**
	 * Factory to create the views.
	 */
	protected CellViewFactory factory = null;

	/**
	 * The set of visible cells.
	 */
	protected Set visibleSet = new HashSet();

	/**
	 * Ordered list of roots for the view.
	 */
	protected List roots = new ArrayList();

	/**
	 * Cached array of all ports for the view.
	 */
	protected PortView[] ports;

	/**
	 * Only portions of the model are visible.
	 */
	protected boolean partial = false;

	/**
	 * Controls if all attributes are local. If this is false then the
	 * createLocalEdit will check the localAttributes set to see if a specific
	 * attribute is local, otherwise it will assume that all attributes are
	 * local. This allows to make all attributes local without actually knowing
	 * them. Default is false.
	 */
	protected boolean allAttributesLocal = false;

	/**
	 * A set containing all attribute keys that are stored in the cell views, in
	 * other words, the view-local attributes.
	 */
	protected Set localAttributes = new HashSet();

	/**
	 * Constructs a graph layout cache.
	 */
	public GraphLayoutCache() {
		this(new DefaultGraphModel(), new DefaultCellViewFactory());
	}

	/**
	 * Constructs a view for the specified model that uses <code>factory</code>
	 * to create its views.
	 * 
	 * @param model
	 *            the model that constitues the data source
	 */
	public GraphLayoutCache(GraphModel model, CellViewFactory factory) {
		this(model, factory, false);
	}

	/**
	 * Constructs a view for the specified model that uses <code>factory</code>
	 * to create its views.
	 * 
	 * @param model
	 *            the model that constitues the data source
	 */
	public GraphLayoutCache(GraphModel model, CellViewFactory factory,
			boolean partial) {
		this(model, factory, null, null, partial);
	}

	/**
	 * Constructs a view for the specified model that uses <code>factory</code>
	 * to create its views.
	 * 
	 * @param model
	 *            the model that constitues the data source
	 */
	public GraphLayoutCache(GraphModel model, CellViewFactory factory,
			CellView[] cellViews, CellView[] hiddenCellViews, boolean partial) {
		this.factory = factory;
		this.partial = partial;
		if (cellViews != null) {
			graphModel = model;
			for (int i = 0; i < cellViews.length; i++) {
				if (cellViews[i] != null) {
					putMapping(cellViews[i].getCell(), cellViews[i]);
					if (partial)
						visibleSet.add(cellViews[i].getCell());
				}
			}
			insertViews(cellViews);
			// Notify observers for autosizing?
		} else {
			setModel(model);
		}
		if (hiddenCellViews != null) {
			for (int i = 0; i < hiddenCellViews.length; i++)
				hiddenMapping.put(hiddenCellViews[i].getCell(),
						hiddenCellViews[i]);
		}
	}

	//
	// GraphLayoutCacheListeners
	//

	/**
	 * Adds a listener for the GraphLayoutCacheEvent posted after the graph
	 * layout cache changes.
	 * 
	 * @see #removeGraphLayoutCacheListener
	 * @param l
	 *            the listener to add
	 */
	public void addGraphLayoutCacheListener(GraphLayoutCacheListener l) {
		listenerList.add(GraphLayoutCacheListener.class, l);
	}

	/**
	 * Removes a listener previously added with <B>addGraphLayoutCacheListener()
	 * </B>.
	 * 
	 * @see #addGraphLayoutCacheListener
	 * @param l
	 *            the listener to remove
	 */
	public void removeGraphLayoutCacheListener(GraphLayoutCacheListener l) {
		listenerList.remove(GraphLayoutCacheListener.class, l);
	}

	/**
	 * Invoke this method after you've changed how the cells are to be
	 * represented in the graph.
	 */
	public void cellViewsChanged(final CellView[] cellViews) {
		if (cellViews != null) {
			fireGraphLayoutCacheChanged(this,
					new GraphLayoutCacheEvent.GraphLayoutCacheChange() {

						public Object[] getInserted() {
							return null;
						}

						public Object[] getRemoved() {
							return null;
						}

						public Map getPreviousAttributes() {
							return null;
						}

						public Object getSource() {
							return this;
						}

						public Object[] getChanged() {
							return cellViews;
						}

						public Map getAttributes() {
							return null;
						}

						public Object[] getContext() {
							return null;
						}

						public Rectangle2D getDirtyRegion() {
							return null;
						}

						public void setDirtyRegion(Rectangle2D dirty) {
						}

					});
		}
	}

	/*
	 * Notify all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created using the
	 * parameters passed into the fire method.
	 * 
	 * @see EventListenerList
	 */
	protected void fireGraphLayoutCacheChanged(Object source,
			GraphLayoutCacheEvent.GraphLayoutCacheChange edit) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		GraphLayoutCacheEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == GraphLayoutCacheListener.class) {
				// Lazily create the event:
				if (e == null)
					e = new GraphLayoutCacheEvent(source, edit);
				((GraphLayoutCacheListener) listeners[i + 1])
						.graphLayoutCacheChanged(e);
			}
		}
	}

	/**
	 * Return an array of all GraphLayoutCacheListener that were added to this
	 * model.
	 */
	public GraphLayoutCacheListener[] getGraphLayoutCacheListeners() {
		return (GraphLayoutCacheListener[]) listenerList
				.getListeners(GraphLayoutCacheListener.class);
	}

	//
	// Accessors
	//

	/**
	 * Sets the factory that creates the cell views.
	 */
	public void setFactory(CellViewFactory factory) {
		this.factory = factory;
	}

	/**
	 * Returns the factory that was passed to the constructor.
	 */
	public CellViewFactory getFactory() {
		return factory;
	}

	/**
	 * Sets the current model.
	 */
	public void setModel(GraphModel model) {
		roots.clear();
		mapping.clear();
		hiddenMapping.clear();
		visibleSet.clear();
		graphModel = model;
		if (!isPartial()) {
			Object[] cells = DefaultGraphModel.getRoots(getModel());
			CellView[] cellViews = getMapping(cells, true);
			insertViews(cellViews);
		}
		// Update PortView Cache and Notify Observers
		update();
	}

	/**
	 * Sets the current model.
	 */
	public void update() {
		updatePorts();
		cellViewsChanged(getRoots());
	}

	/**
	 * @return Returns an unordered array of all visible cellviews.
	 */
	public CellView[] getCellViews() {
		Collection coll = mapping.values();
		CellView[] result = new CellView[coll.size()];
		coll.toArray(result);
		return result;
	}

	/**
	 * Returns the bounding box for the specified cell views.
	 */
	public static Rectangle2D getBounds(CellView[] views) {
		if (views != null && views.length > 0) {
			Rectangle2D r = (views[0] != null) ? views[0].getBounds() : null;
			Rectangle2D ret = (r != null) ? (Rectangle2D) r.clone() : null;
			for (int i = 1; i < views.length; i++) {
				r = (views[i] != null) ? views[i].getBounds() : null;
				if (r != null) {
					if (ret == null)
						ret = (r != null) ? (Rectangle2D) r.clone() : null;
					else
						Rectangle2D.union(ret, r, ret);
				}
			}
			return ret;
		}
		return null;
	}

	/**
	 * A helper method to return various arrays of cells that are visible in
	 * this cache. For example, to get all selected vertices in a graph, do
	 * <code>graph.getSelectionCells(graph.getGraphLayoutCache().getCells(false, true,
	 false, false));</code>
	 */
	public Object[] getCells(boolean groups, boolean vertices, boolean ports,
			boolean edges) {
		CellView[] views = getCellViews();
		List result = new ArrayList(views.length);
		GraphModel model = getModel();
		for (int i = 0; i < views.length; i++) {
			Object cell = views[i].getCell();
			boolean isEdge = model.isEdge(cell);
			if ((ports || !model.isPort(cell))) {
				if (((((ports || vertices) && !isEdge) || (edges && isEdge)) && views[i]
						.isLeaf()) || (groups && !views[i].isLeaf()))
					result.add(views[i].getCell());

			}
		}
		return result.toArray();
	}

	/**
	 * Returns a nested map of (cell, map) pairs that represent all attributes
	 * of all cell views in this view.
	 * 
	 * @see #getCellViews
	 */
	public Map createNestedMap() {
		CellView[] cellViews = getCellViews();
		Map nested = new Hashtable();
		for (int i = 0; i < cellViews.length; i++) {
			nested.put(cellViews[i].getCell(), new Hashtable((Map) cellViews[i]
					.getAllAttributes().clone()));
		}
		return nested;
	}

	/**
	 * @return Returns an unordered array of all hidden cellviews.
	 */
	public CellView[] getHiddenCellViews() {
		Collection coll = hiddenMapping.values();
		CellView[] result = new CellView[coll.size()];
		coll.toArray(result);
		return result;
	}

	/**
	 * Remaps all existing views using the CellViewFactory and replaces the
	 * respective root views.
	 */
	public synchronized void reload() {
		List newRoots = new ArrayList();
		Map oldMapping = new Hashtable(mapping);
		mapping.clear();
		Iterator it = oldMapping.keySet().iterator();
		Set rootsSet = new HashSet(roots);
		while (it.hasNext()) {
			Object cell = it.next();
			CellView oldView = (CellView) oldMapping.get(cell);
			CellView newView = getMapping(cell, true);
			newView.changeAttributes(this, oldView.getAttributes());
			// newView.refresh(getModel(), this, false);
			if (rootsSet.contains(oldView))
				newRoots.add(newView);
		}
		// replace hidden
		hiddenMapping.clear();
		roots = newRoots;
	}

	/**
	 * Returns the current model.
	 */
	public GraphModel getModel() {
		return graphModel;
	}

	/**
	 * Returns the roots of the view.
	 */
	public CellView[] getRoots() {
		CellView[] views = new CellView[roots.size()];
		roots.toArray(views);
		return views;
	}

	/**
	 * Return all root cells that intersect the given rectangle.
	 */
	public CellView[] getRoots(Rectangle2D clip) {
		java.util.List result = new ArrayList();
		CellView[] views = getRoots();
		for (int i = 0; i < views.length; i++)
			if (views[i].getBounds().intersects(clip))
				result.add(views[i]);
		views = new CellView[result.size()];
		result.toArray(views);
		return views;
	}

	/**
	 * Returns a an array with the visible cells in <code>cells</code>.
	 */
	public Object[] getVisibleCells(Object[] cells) {
		if (cells != null) {
			List result = new ArrayList(cells.length);
			for (int i = 0; i < cells.length; i++)
				if (isVisible(cells[i]))
					result.add(cells[i]);
			return result.toArray();
		}
		return null;
	}

	/**
	 * Returns the ports of the view.
	 */
	public PortView[] getPorts() {
		return ports;
	}

	/**
	 * Updates the cached array of ports.
	 */
	protected void updatePorts() {
		Object[] roots = DefaultGraphModel.getRoots(graphModel);
		List list = DefaultGraphModel.getDescendants(graphModel, roots);
		if (list != null) {
			ArrayList result = new ArrayList();
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Object cell = it.next();
				if (graphModel.isPort(cell)) {
					CellView portView = getMapping(cell, false);
					if (portView != null) {
						result.add(portView);
						portView.refresh(this, this, false);
					}
				}
			}
			ports = new PortView[result.size()];
			result.toArray(ports);
		}
	}

	public void refresh(CellView[] views, boolean create) {
		if (views != null)
			for (int i = 0; i < views.length; i++)
				refresh(views[i], create);
	}

	public void refresh(CellView view, boolean create) {
		if (view != null) {
			view.refresh(this, this, create);
			CellView[] children = view.getChildViews();
			for (int i = 0; i < children.length; i++)
				refresh(children[i], create);
		}
	}

	public void update(CellView[] views) {
		if (views != null)
			for (int i = 0; i < views.length; i++)
				update(views[i]);
	}

	public void update(CellView view) {
		if (view != null) {
			view.update(this);
			CellView[] children = view.getChildViews();
			for (int i = 0; i < children.length; i++)
				update(children[i]);
		}
	}

	//
	// Update View based on Model Change
	//
	/**
	 * Called from BasicGraphUI.ModelHandler to update the view based on the
	 * specified GraphModelEvent.
	 */
	public void graphChanged(GraphModelEvent.GraphModelChange change) {
		// Get Old Attributes From GraphModelChange (Undo) -- used to remap
		// removed cells
		CellView[] views = change.getViews(this);
		if (views != null) {
			// Only ex-visible views are piggybacked
			for (int i = 0; i < views.length; i++)
				if (views[i] != null) {
					// Do not use putMapping because cells are invisible
					mapping.put(views[i].getCell(), views[i]);
				}
			// Ensure visible state
			setVisibleImpl(getCells(views), true);
		}
		// Fetch View Order Of Changed Cells (Before any changes)
		Object[] changed = change.getChanged();
		// Fetch Views to Insert before Removal (Special case: two step process,
		// see setModel)
		getMapping(change.getInserted(), true);
		// Remove and Hide Roots
		views = removeCells(change.getRemoved());
		// Store Removed Attributes In GraphModelChange (Undo)
		change.putViews(this, views);
		// Insert New Roots
		// insertViews(insertViews);
		// Hide edges with invisible source or target
		if (isPartial()) {
			// Then show
			showCellsForChange(change);
			// First hide
			hideCellsForChange(change);
		}
		// Refresh Changed Cells
		if (changed != null && changed.length > 0) {
			// Restore All Cells in Model Order (Replace Roots)
			for (int i = 0; i < changed.length; i++) {
				CellView view = getMapping(changed[i], false);
				if (view != null) {
					view.refresh(this, this, true);
					// Update child edges in groups (routing)
					update(view);
				}
			}
		}
		reloadRoots();
		// Refresh Context of Changed Cells (=Connected Edges)
		refresh(getMapping(getContext(change), false), false);
		updatePorts();
	}

	/**
	 * Completely reloads all roots from the model in the order returned by
	 * DefaultGraphModel.getAll. This uses the current visibleSet and mapping to
	 * fetch the cell views for the cells.
	 */
	protected void reloadRoots() {
		// Reorder roots
		Object[] orderedCells = DefaultGraphModel.getAll(graphModel);
		List newRoots = new ArrayList();
		for (int i = 0; i < orderedCells.length; i++) {
			CellView view = getMapping(orderedCells[i], false);
			if (view != null) {
				view.refresh(this, this, true);
				if (view.getParentView() == null) {
					newRoots.add(view);
				}
			}
		}
		roots = newRoots;
	}

	/**
	 * Hook for subclassers to augment the context for a graphChange. This means
	 * you can add additional cells that should be refreshed on a special change
	 * event. eg. parallel edges when one is removed or added.
	 */
	protected Object[] getContext(GraphModelEvent.GraphModelChange change) {
		return change.getContext();
	}

	protected void hideCellsForChange(GraphModelEvent.GraphModelChange change) {
		// Hide visible edges between invisible vertices
		// 1. Remove attached edges of removed cells
		// 2. Remove edges who's source or target has changed to
		// invisible.
		Object[] tmp = change.getRemoved();
		Set removed = new HashSet();
		if (tmp != null)
			for (int i = 0; i < tmp.length; i++)
				removed.add(tmp[i]);
		if (hidesDanglingConnections || hidesExistingConnections) {
			Object[] changed = change.getChanged();
			for (int i = 0; i < changed.length; i++) {
				CellView view = getMapping(changed[i], false);
				if (view instanceof EdgeView) {
					EdgeView edge = (EdgeView) view;
					Object oldSource = (edge.getSource() == null) ? null : edge
							.getSource().getCell();
					Object oldTarget = (edge.getTarget() == null) ? null : edge
							.getTarget().getCell();
					Object newSource = graphModel.getSource(changed[i]);
					Object newTarget = graphModel.getTarget(changed[i]);
					boolean hideExisting = (hidesExistingConnections && ((newSource != null && !hasVisibleParent(
							newSource, null)) || (newTarget != null && !hasVisibleParent(
							newTarget, null))));
					if ((hidesDanglingConnections && (removed
							.contains(oldSource) || removed.contains(oldTarget)))
							|| hideExisting) {
						setVisibleImpl(new Object[] { changed[i] }, false);
					}
				}
			}
		}
	}

	/**
	 * Checks if the port or one of its parents is visible.
	 */
	protected boolean hasVisibleParent(Object cell, Set invisible) {
		boolean isVisible = false;
		do {
			isVisible = (invisible == null || !invisible.contains(cell)) ? isVisible(cell)
					: false;
			cell = getModel().getParent(cell);
		} while (cell != null && !isVisible);
		return isVisible;
	}

	protected void showCellsForChange(GraphModelEvent.GraphModelChange change) {
		Object[] inserted = change.getInserted();
		if (inserted != null && showsInsertedConnections) {
			for (int i = 0; i < inserted.length; i++) {
				if (!isVisible(inserted[i])) {
					Object source = graphModel.getSource(inserted[i]);
					Object target = graphModel.getTarget(inserted[i]);
					if ((source != null || target != null)
							&& (isVisible(source) && isVisible(target)))
						setVisibleImpl(new Object[] { inserted[i] }, true);
				}
			}
		}
		if (change.getConnectionSet() != null) {
			Set changedSet = change.getConnectionSet().getChangedEdges();
			if (changedSet != null && showsChangedConnections) {
				Object[] changed = changedSet.toArray();
				for (int i = 0; i < changed.length; i++) {
					if (!isVisible(changed[i])) {
						Object source = graphModel.getSource(changed[i]);
						Object target = graphModel.getTarget(changed[i]);
						if ((source != null || target != null)
								&& (isVisible(source) && isVisible(target))
								&& !isVisible(changed[i]))
							setVisibleImpl(new Object[] { changed[i] }, true);
					}
				}
			}
		}
	}

	/**
	 * Adds the specified model root cells to the view. Do not add a view that
	 * is already in roots.
	 */
	public void insertViews(CellView[] views) {
		if (views != null) {
			refresh(views, true);
			for (int i = 0; i < views.length; i++) {
				if (views[i] != null
						&& getMapping(views[i].getCell(), false) != null) {
					CellView parentView = views[i].getParentView();
					Object parent = (parentView != null) ? parentView.getCell()
							: null;
					if (!graphModel.isPort(views[i].getCell())
							&& parent == null) {
						roots.add(views[i]);
					}
				}
			}
		}
	}

	/**
	 * Removes the specified model root cells from the view by removing the
	 * mapping between the cell and its view and makes the cells invisible.
	 */
	public CellView[] removeCells(Object[] cells) {
		if (cells != null && cells.length > 0) {
			CellView[] views = new CellView[cells.length];
			// Store views to be removed from roots in an intermediate
			// set for performance reasons
			Set removedRoots = null;
			for (int i = 0; i < cells.length; i++) {
				views[i] = removeMapping(cells[i]);
				if (views[i] != null) {
					views[i].removeFromParent();
					if (removedRoots == null) {
						removedRoots = new HashSet();
					}
					removedRoots.add(views[i]);
					visibleSet.remove(views[i].getCell());
				}
			}
			if (removedRoots != null && removedRoots.size() > 0) {
				// If any roots have been removed, reform the roots
				// lists appropriately, keeping the order the same
				int newRootsSize = roots.size() - removedRoots.size();
				if (newRootsSize < 8) {
					newRootsSize = 8;
				}
				List newRoots = new ArrayList(newRootsSize);
				Iterator iter = roots.iterator();
				while (iter.hasNext()) {
					Object cell = iter.next();
					if (!removedRoots.contains(cell)) {
						newRoots.add(cell);
					}
				}
				roots = newRoots;
			}
			return views;
		}
		return null;
	}

	//
	// Cell Mapping
	//
	/**
	 * Takes an array of views and returns the array of the corresponding cells
	 * by using <code>getCell</code> for each view.
	 */
	public Object[] getCells(CellView[] views) {
		if (views != null) {
			Object[] cells = new Object[views.length];
			for (int i = 0; i < views.length; i++)
				if (views[i] != null)
					cells[i] = views[i].getCell();
			return cells;
		}
		return null;
	}

	/**
	 * Returns the view for the specified cell. If create is true and no view is
	 * found then a view is created using createView(Object).
	 */
	public CellView getMapping(Object cell, boolean create) {
		if (cell == null)
			return null;
		CellView view = (CellView) mapping.get(cell);
		if (view == null && create && isVisible(cell)) {
			view = (CellView) hiddenMapping.get(cell);
			if (view != null) {
				putMapping(cell, view);
				hiddenMapping.remove(cell);
			} else {
				view = factory.createView(graphModel, cell);
				putMapping(cell, view);
				view.refresh(this, this, true); // Create Dependent
				// Views
				view.update(this);
			}
		}
		return view;
	}

	/**
	 * Returns the views for the specified array of cells without creating these
	 * views on the fly.
	 */
	public CellView[] getMapping(Object[] cells) {
		return getMapping(cells, false);
	}

	/**
	 * Returns the views for the specified array of cells. Returned array may
	 * contain null pointers if the respective cell is not mapped in this view
	 * and <code>create</code> is <code>false</code>.
	 */
	public CellView[] getMapping(Object[] cells, boolean create) {
		if (cells != null) {
			CellView[] result = new CellView[cells.length];
			for (int i = 0; i < cells.length; i++)
				result[i] = getMapping(cells[i], create);
			return result;
		}
		return null;
	}

	/**
	 * Associates the specified model cell with the specified view.
	 */
	public void putMapping(Object cell, CellView view) {
		if (cell != null && view != null)
			mapping.put(cell, view);
	}

	/**
	 * Removes the association for the specified model cell and returns the view
	 * that was previously associated with the cell. Updates the portlist if
	 * necessary.
	 */
	public CellView removeMapping(Object cell) {
		if (cell != null) {
			CellView view = (CellView) mapping.remove(cell);
			return view;
		}
		return null;
	}

	/**
	 * Whether or not the specified cell is visible. NOTE: Your GraphLayoutCache
	 * must be <code>partial</code> (set <code>partial</code> to
	 * <code>true</code> in the constructor) in order to use the visibility
	 * functionality of expand/collapse, setVisible, etc. null is always visible
	 * 
	 * @param cell
	 *            the whose visibility to determine
	 * @return whether or not the cell is visible
	 */
	public boolean isVisible(Object cell) {
		return !isPartial() || visibleSet.contains(cell) || cell == null;
	}

	/**
	 * Returns the set of visible sets in this view.
	 * 
	 * @return the set of visible sets in this view.
	 */
	public Set getVisibleSet() {
		return new HashSet(visibleSet);
	}

	/**
	 * Applies the specified set of cells as being those visible
	 * 
	 * @param visible
	 *            the set of visible cells
	 */
	public void setVisibleSet(Set visible) {
		visibleSet = visible;
	}

	/**
	 * Makes the specified cell visible or invisible depending on the flag
	 * passed in. Note the cell really is a cell, not a cell view. NOTE: Your
	 * GraphLayoutCache must be <code>partial</code> (set <code>partial</code>
	 * to <code>true</code> in the constructor) in order to use the visibility
	 * functionality of expand/collapse, setVisible, etc.
	 * 
	 * @param cell
	 *            the cell whose visibility is to be changed
	 * @param visible
	 *            <code>true</code> if cell is to be made visible
	 */
	public void setVisible(Object cell, boolean visible) {
		setVisible(new Object[] { cell }, visible);
	}

	/**
	 * Makes the specified cells visible or invisible depending on the flag
	 * passed in. Note the cells really are cells, not cell views. NOTE: Your
	 * GraphLayoutCache must be <code>partial</code> (set <code>partial</code>
	 * to <code>true</code> in the constructor) in order to use the visibility
	 * functionality of expand/collapse, setVisible, etc.
	 * 
	 * @param cells
	 *            the cells whose visibility is to be changed
	 * @param visible
	 *            <code>true</code> if the cells are to be made visible
	 */
	public void setVisible(Object[] cells, boolean visible) {
		if (visible)
			setVisible(cells, null);
		else
			setVisible(null, cells);
	}

	/**
	 * Changes the visibility state of the cells passed in. Note that the arrays
	 * must contain cells, not cell views. NOTE: Your GraphLayoutCache must be
	 * <code>partial</code> (set <code>partial</code> to <code>true</code> in
	 * the constructor) in order to use the visibility functionality of
	 * expand/collapse, setVisible, etc.
	 * 
	 * @param visible
	 *            cells to be made visible
	 * @param invisible
	 *            cells to be made invisible
	 */
	public void setVisible(Object[] visible, Object[] invisible) {
		setVisible(visible, invisible, null);
	}

	/**
	 * Changes the visibility state of the cells passed in. Note that the arrays
	 * must contain cells, not cell views. NOTE: Your GraphLayoutCache must be
	 * <code>partial</code> (set <code>partial</code> to <code>true</code> in
	 * the constructor) in order to use the visibility functionality of
	 * expand/collapse, setVisible, etc.
	 * 
	 * @param visible
	 *            cells to be made visible
	 * @param invisible
	 *            cells to be made invisible
	 * @param cs
	 *            a <code>ConnectionSet</code> describing the new state of edge
	 *            connections in the graph
	 */
	public void setVisible(Object[] visible, Object[] invisible,
			ConnectionSet cs) {
		setVisible(visible, invisible, null, cs);
	}

	/**
	 * Changes the visibility state of the cells passed in. Note that the arrays
	 * must contain cells, not cell views. NOTE: Your GraphLayoutCache must be
	 * <code>partial</code> (set <code>partial</code> to <code>true</code> in
	 * the constructor) in order to use the visibility functionality of
	 * expand/collapse, setVisible, etc.
	 * 
	 * @param visible
	 *            cells to be made visible
	 * @param invisible
	 *            cells to be made invisible
	 * @param attributes
	 *            a nested attribute map of cells/attribute maps
	 * @param cs
	 *            a <code>ConnectionSet</code> describing the new state of edge
	 *            connections in the graph
	 */
	public void setVisible(Object[] visible, Object[] invisible,
			Map attributes, ConnectionSet cs) {
		GraphLayoutCacheEdit edit = new GraphLayoutCacheEdit(null, attributes,
				visible, invisible);
		edit.end();
		graphModel.edit(attributes, cs, null, new UndoableEdit[] { edit });
	}

	// This is used to augment the array passed to the setVisible method.
	protected Object[] addVisibleDependencies(Object[] cells, boolean visible) {
		if (cells != null) {
			if (visible) {
				// Make ports and source and target vertex visible
				Set all = new HashSet();
				for (int i = 0; i < cells.length; i++) {
					all.add(cells[i]);
					// Add ports
					all.addAll(getPorts(cells[i]));
					// Add source vertex and ports
					Collection coll = getParentPorts(graphModel
							.getSource(cells[i]));
					if (coll != null)
						all.addAll(coll);
					// Add target vertex and ports
					coll = getParentPorts(graphModel.getTarget(cells[i]));
					if (coll != null)
						all.addAll(coll);
				}
				if (showsExistingConnections) {
					Set tmp = DefaultGraphModel.getEdges(getModel(), cells);
					Iterator it = tmp.iterator();
					while (it.hasNext()) {
						Object obj = it.next();
						Object source = graphModel.getSource(obj);
						Object target = graphModel.getTarget(obj);
						if ((isVisible(source) || all.contains(source))
								&& (isVisible(target) || all.contains(target)))
							all.add(obj);
					}
				}
				all.removeAll(visibleSet);
				all.remove(null);
				return all.toArray();
			} else {
				if (hidesExistingConnections) {
					Set all = new HashSet();
					for (int i = 0; i < cells.length; i++) {
						all.addAll(getPorts(cells[i]));
						all.add(cells[i]);
					}
					Iterator it = DefaultGraphModel.getEdges(graphModel, cells)
							.iterator();
					while (it.hasNext()) {
						Object edge = it.next();
						Object newSource = graphModel.getSource(edge);
						Object newTarget = graphModel.getTarget(edge);
						// Note: At this time the cells are not yet hidden
						if ((newSource != null && !hasVisibleParent(newSource,
								all))
								|| (newTarget != null && !hasVisibleParent(
										newTarget, all))) {
							all.add(edge);
						}
					}
					all.remove(null);
					return all.toArray();
				}
			}
		}
		return cells;
	}

	/**
	 * The actual implementation of changing cells' visibility state. This
	 * method does not deal with creating the undo or updating the
	 * GraphLayoutCache correctly. The <code>setVisible</code> methods in this
	 * class are intended to be the main public way to change visiblilty.
	 * However, if you do not require the undo to be formed, this method is much
	 * quicker, just note that you must call <code>updatePorts</code> if this
	 * method returns true.
	 * 
	 * NOTE: Your GraphLayoutCache must be <code>partial</code> (set
	 * <code>partial</code> to <code>true</code> in the constructor) in order to
	 * use the visibility functionality of expand/collapse, setVisible, etc.
	 * 
	 * @param cells
	 * @param visible
	 * @return whether or not the ports needed updating in the calling method
	 */
	public boolean setVisibleImpl(Object[] cells, boolean visible) {
		cells = addVisibleDependencies(cells, visible);
		if (cells != null && isPartial()) {
			boolean updatePorts = false;
			// Update Visible Set
			CellView[] views = new CellView[cells.length];
			if (!visible) {
				views = removeCells(cells);
			}
			// Set used for model roots contains call for performance
			Set modelRoots = null;
			for (int i = 0; i < cells.length; i++) {
				if (cells[i] != null) {
					if (visible) {
						visibleSet.add(cells[i]);
						views[i] = getMapping(cells[i], true);
					} else {
						if (views[i] != null) {
							if (modelRoots == null) {
								modelRoots = new HashSet(
										DefaultGraphModel
												.getRootsAsCollection(getModel()));
							}
							if (modelRoots.contains(views[i].getCell())
									&& remembersCellViews) {
								hiddenMapping.put(views[i].getCell(), views[i]);
							}
							updatePorts = true;
						}
					}
				}
			}
			// Make Cell Views Visible (if not already in place)
			if (visible) {
				Set parentSet = new HashSet();
				Set removedRoots = null;
				for (int i = 0; i < views.length; i++) {
					if (views[i] != null) {
						CellView view = views[i];
						// Remove all children from roots
						CellView[] children = AbstractCellView
								.getDescendantViews(new CellView[] { view });
						for (int j = 0; j < children.length; j++) {
							if (removedRoots == null) {
								removedRoots = new HashSet();
							}
							removedRoots.add(children[j]);
						}
						view.refresh(this, this, false);
						// Link cellView into graphLayoutCache
						CellView parentView = view.getParentView();
						if (parentView != null)
							parentSet.add(parentView);
						updatePorts = true;
					}
				}
				if (removedRoots != null && removedRoots.size() > 0) {
					// If any roots have been removed, reform the roots
					// lists appropriately, keeping the order the same
					List newRoots = new ArrayList();
					Iterator iter = roots.iterator();
					while (iter.hasNext()) {
						Object cell = iter.next();
						if (!removedRoots.contains(cell)) {
							newRoots.add(cell);
						}
					}
					roots = newRoots;
				}

				CellView[] parentViews = new CellView[parentSet.size()];
				parentSet.toArray(parentViews);
				refresh(parentViews, true);
			}
			return updatePorts;
		}
		return false;
	}

	protected Collection getParentPorts(Object cell) {
		// does nothing if a parent is already visible
		Object parent = graphModel.getParent(cell);
		while (parent != null) {
			if (isVisible(parent))
				return null;
			parent = graphModel.getParent(parent);
		}

		// Else returns the parent and all ports
		parent = graphModel.getParent(cell);
		Collection collection = getPorts(parent);
		collection.add(parent);
		return collection;
	}

	protected Collection getPorts(Object cell) {
		LinkedList list = new LinkedList();
		for (int i = 0; i < graphModel.getChildCount(cell); i++) {
			Object child = graphModel.getChild(cell, i);
			if (graphModel.isPort(child))
				list.add(child);
		}
		return list;
	}

	//
	// Change Support
	//
	public boolean isPartial() {
		return partial;
	}

	/**
	 * Required for XML persistence
	 * 
	 * @return whether or not the cache is partial
	 */
	public boolean getPartial() {
		return isPartial();
	}

	/**
	 * Messaged when the user has altered the value for the item identified by
	 * cell to newValue. If newValue signifies a truly new value the model
	 * should post a graphCellsChanged event. This calls
	 * augmentNestedMapForValueChange.
	 */
	public void valueForCellChanged(Object cell, Object newValue) {
		Map nested = null;
		if (isAutoSizeOnValueChange()) {
			CellView view = getMapping(cell, false);
			if (view != null) {
				AttributeMap attrs = view.getAllAttributes();
				Rectangle2D bounds = GraphConstants.getBounds(attrs);
				Rectangle2D dummyBounds = null;
				// Force the model to store the old bounds
				if (bounds != null) {
					dummyBounds = attrs.createRect(bounds.getX(),
							bounds.getY(), 0, 0);
				} else {
					dummyBounds = attrs.createRect(0, 0, 0, 0);
				}
				nested = GraphConstants.createAttributes(new Object[] { cell },
						new Object[] { GraphConstants.RESIZE,
								GraphConstants.BOUNDS }, new Object[] {
								Boolean.TRUE, dummyBounds });
			}
		} else {
			nested = new Hashtable();
			nested.put(cell, new Hashtable());
		}
		augmentNestedMapForValueChange(nested, cell, newValue);
		edit(nested, null, null, null);
	}

	/**
	 * Hook for subclassers to add more stuff for value changes. Currently this
	 * adds the new value to the change.
	 */
	protected void augmentNestedMapForValueChange(Map nested, Object cell,
			Object newValue) {
		Map attrs = (Map) nested.get(cell);
		if (attrs != null)
			GraphConstants.setValue(attrs, newValue);
	}

	/**
	 * Inserts the <code>cells</code> and connections into the model, and
	 * absorbs the local attributes. This implementation sets the inserted cells
	 * visible and selects the new roots depending on graph.selectNewCells.
	 */
	public void insert(Object[] roots, Map attributes, ConnectionSet cs,
			ParentMap pm, UndoableEdit[] e) {
		Object[] visible = null;
		if (isPartial() && showsInsertedCells) {
			List tmp = DefaultGraphModel.getDescendants(graphModel, roots);
			tmp.removeAll(visibleSet);
			if (!tmp.isEmpty())
				visible = tmp.toArray();
		}
		// Absorb local attributes
		GraphLayoutCacheEdit edit = createLocalEdit(roots, attributes, visible,
				null);
		if (edit != null)
			e = augment(e, edit);
		graphModel.insert(roots, attributes, cs, pm, e);
	}

	/**
	 * Inserts the cloned cells from the clone map and clones the passed-in
	 * arguments according to the clone map before insertion and returns the
	 * clones in order of the cells. This example shows how to clone the current
	 * selection and get a reference to the clones:
	 * 
	 * <pre>
	 * Object[] cells = graph.getDescendants(graph.order(graph.getSelectionCells()));
	 * ConnectionSet cs = ConnectionSet.create(graphModel, cells, false);
	 * ParentMap pm = ParentMap.create(graphModel, cells, false, true);
	 * cells = graphLayoutCache.insertClones(cells, graph.cloneCells(cells),
	 * 		attributes, cs, pm, 0, 0);
	 * </pre>
	 */
	public Object[] insertClones(Object[] cells, Map clones, Map nested,
			ConnectionSet cs, ParentMap pm, double dx, double dy) {
		if (cells != null) {
			if (cs != null)
				cs = cs.clone(clones);
			if (pm != null)
				pm = pm.clone(clones);
			if (nested != null) {
				nested = GraphConstants.replaceKeys(clones, nested);
				AttributeMap.translate(nested.values(), dx, dy);
			}
			// Replace cells in order
			Object[] newCells = new Object[cells.length];
			for (int i = 0; i < cells.length; i++)
				newCells[i] = clones.get(cells[i]);
			// Insert into cache/model
			insert(newCells, nested, cs, pm, null);
			return newCells;
		}
		return null;
	}

	/**
	 * Inserts the specified vertex into the graph model. This method does in
	 * fact nothing, it calls insert edge with the vertex and the source and
	 * target port set to null. This example shows how to add a vertex with a
	 * port and a black border:
	 * 
	 * <pre>
	 * DefaultGraphCell vertex = new DefaultGraphCell(&quot;Hello, world!&quot;);
	 * Map attrs = vertex.getAttributes();
	 * GraphConstants.setOpaque(attrs, false);
	 * GraphConstants.setBorderColor(attrs, Color.black);
	 * DefaultPort port = new DefaultPort();
	 * vertex.add(port);
	 * port.setParent(vertex);
	 * graph.getGraphLayoutCache().insert(vertex);
	 * </pre>
	 * 
	 * @param cell
	 *            inserts the specified cell in the cache
	 */
	public void insert(Object cell) {
		insert(new Object[] { cell });
	}

	/**
	 * Inserts the specified edge into the graph model. This method does in fact
	 * nothing, it calls insert with a default connection set.
	 * 
	 * @param edge
	 *            the edge to be inserted
	 * @param source
	 *            the source port this edge is connected to
	 * @param target
	 *            the target port this edge is connected to
	 */
	public void insertEdge(Object edge, Object source, Object target) {
		insert(new Object[] { edge }, new Hashtable(), new ConnectionSet(edge,
				source, target), new ParentMap());
	}

	/**
	 * Inserts the specified cells into the graph model. This method is a
	 * general implementation of cell insertion. If the source and target port
	 * are null, then no connection set is created. The method uses the
	 * attributes from the specified edge and the egdge's children to construct
	 * the insert call. This example shows how to insert an edge with a special
	 * arrow between two known vertices:
	 * 
	 * <pre>
	 * Object source = graph.getDefaultPortForCell(sourceVertex).getCell();
	 * Object target = graph.getDefaultPortForCell(targetVertex).getCell();
	 * DefaultEdge edge = new DefaultEdge(&quot;Hello, world!&quot;);
	 * edge.setSource(source);
	 * edge.setTarget(target);
	 * Map attrs = edge.getAttributes();
	 * GraphConstants.setLineEnd(attrs, GraphConstants.ARROW_TECHNICAL);
	 * graph.getGraphLayoutCache().insert(edge);
	 * </pre>
	 */
	public void insert(Object[] cells) {
		insert(cells, new Hashtable(), new ConnectionSet(), new ParentMap());
	}

	/**
	 * Variant of the insert method that allows to pass a default connection set
	 * and parent map and nested map.
	 */
	public void insert(Object[] cells, Map nested, ConnectionSet cs,
			ParentMap pm) {
		if (cells != null) {
			if (nested == null)
				nested = new Hashtable();
			if (cs == null)
				cs = new ConnectionSet();
			if (pm == null)
				pm = new ParentMap();
			for (int i = 0; i < cells.length; i++) {
				// Using the children of the vertex we construct the parent map.
				int childCount = getModel().getChildCount(cells[i]);
				for (int j = 0; j < childCount; j++) {
					Object child = getModel().getChild(cells[i], j);
					pm.addEntry(child, cells[i]);

					// And add their attributes to the nested map
					AttributeMap attrs = getModel().getAttributes(child);
					if (attrs != null)
						nested.put(child, attrs);
				}

				// A nested map with the vertex as key
				// and its attributes as the value
				// is required for the model.
				Map attrsTmp = (Map) nested.get(cells[i]);
				Map attrs = getModel().getAttributes(cells[i]);
				if (attrsTmp != null)
					attrs.putAll(attrsTmp);
				nested.put(cells[i], attrs);

				// Check if we have parameters for a connection set.
				Object sourcePort = getModel().getSource(cells[i]);
				if (sourcePort != null)
					cs.connect(cells[i], sourcePort, true);

				Object targetPort = getModel().getTarget(cells[i]);
				if (targetPort != null)
					cs.connect(cells[i], targetPort, false);
			}
			// Create an array with the parent and its children.
			cells = DefaultGraphModel.getDescendants(getModel(), cells)
					.toArray();

			// Finally call the insert method on the parent class.
			insert(cells, nested, cs, pm, null);
		}
	}

	/**
	 * Inserts the specified cell as a parent of children. Note: All cells that
	 * are not yet in the model will be inserted. This example shows how to
	 * group the current selection and pass the group default bounds in case it
	 * is later collapsed:
	 * 
	 * <pre>
	 * DefaultGraphCell group = new DefaultGraphCell(&quot;Hello, world!&quot;);
	 * Object[] cells = DefaultGraphModel.order(graph.getModel(),
	 * 		graph.getSelectionCells());
	 * Rectangle2D bounds = graph.getCellBounds(cells);
	 * if (bounds != null) {
	 * 	bounds = new Rectangle2D.Double(bounds.getX() + bounds.getWidth() / 4,
	 * 			bounds.getY() + bounds.getHeight() / 4, bounds.getWidth() / 2,
	 * 			bounds.getHeight() / 2);
	 * 	GraphConstants.setBounds(group.getAttributes(), bounds);
	 * }
	 * graph.getGraphLayoutCache().insertGroup(group, cells);
	 * </pre>
	 */
	public void insertGroup(Object group, Object[] children) {
		if (group != null && children != null && children.length > 0) {
			Map nested = new Hashtable();

			// List to store all children that are not in the model
			List newCells = new ArrayList(children.length + 1);

			// Plus the group cell at pos 0
			if (!getModel().contains(group)) {
				newCells.add(group);
			}

			// Create a parent map for the group and the children, and
			// store the children's attributes in the nested map.
			// Note: This implementation assumes that the children have
			// not yet been added to the group object. Therefore,
			// the insert method will only collect the group
			// attributes, but will ignore the child attributes.
			ParentMap pm = new ParentMap();
			for (int i = 0; i < children.length; i++) {
				pm.addEntry(children[i], group);
				if (!getModel().contains(children[i])) {
					newCells.add(children[i]);
					AttributeMap attrs = getModel().getAttributes(children[i]);
					if (attrs != null)
						nested.put(children[i], attrs);
				}
			}
			if (newCells.isEmpty())
				edit(nested, null, pm, null);
			else
				insert(newCells.toArray(), nested, null, pm);
		}
	}

	/**
	 * Removes <code>cells</code> from the model.
	 */
	public void remove(Object[] cells) {
		graphModel.remove(cells);
	}

	/**
	 * Removes cells from the model, including all children and connected edges
	 * if <code>children</code> or <code>edges</code> is true, respectively.
	 * 
	 * @param cells
	 *            The cells to remove.
	 * @param descendants
	 *            Whether to remove all descendants as well.
	 * @param edges
	 *            Whether to remove all connected edges as well.
	 */
	public void remove(Object[] cells, boolean descendants, boolean edges) {
		if (cells != null && cells.length > 0) {
			if (edges) {
				Object[] tmp = DefaultGraphModel.getEdges(getModel(), cells)
						.toArray();
				Object[] newCells = new Object[cells.length + tmp.length];
				System.arraycopy(cells, 0, newCells, 0, cells.length);
				System.arraycopy(tmp, 0, newCells, cells.length, tmp.length);
				cells = newCells;
			}
			if (descendants)
				cells = DefaultGraphModel.getDescendants(getModel(), cells)
						.toArray();
			remove(cells);
		}
	}

	/**
	 * Hides the specified cells with all children if <code>descandants</code>
	 * is true. NOTE: Your GraphLayoutCache must be <code>partial</code> (set
	 * <code>partial</code> to <code>true</code> in the constructor) in order to
	 * use the visibility functionality of expand/collapse, setVisible, etc.
	 */
	public void hideCells(Object[] cells, boolean descandants) {
		if (cells != null && cells.length > 0) {
			if (descandants)
				cells = DefaultGraphModel.getDescendants(getModel(), cells)
						.toArray();
			setVisible(cells, false);
		}
	}

	/**
	 * Shows the specified cells with all children if <code>descandants</code>
	 * is true. NOTE: Your GraphLayoutCache must be <code>partial</code> (set
	 * <code>partial</code> to <code>true</code> in the constructor) in order to
	 * use the visibility functionality of expand/collapse, setVisible, etc.
	 */
	public void showCells(Object[] cells, boolean descandants) {
		if (cells != null && cells.length > 0) {
			if (descandants)
				cells = DefaultGraphModel.getDescendants(getModel(), cells)
						.toArray();
			setVisible(cells, true);
		}
	}

	/**
	 * Ungroups all groups in cells and returns the children that are not ports.
	 * Note: This replaces the parents with their group cells in the group
	 * structure.
	 */
	public Object[] ungroup(Object[] cells) {
		if (cells != null && cells.length > 0) {
			ArrayList toRemove = new ArrayList();
			ArrayList children = new ArrayList();
			boolean groupExists = false;
			for (int i = 0; i < cells.length; i++) {
				boolean childExists = false;
				ArrayList tempPortList = new ArrayList();
				for (int j = 0; j < getModel().getChildCount(cells[i]); j++) {
					Object child = getModel().getChild(cells[i], j);
					if (!getModel().isPort(child)) {
						children.add(child);
						childExists = true;
					} else {
						tempPortList.add(child);
					}
				}
				if (childExists) {
					toRemove.addAll(tempPortList);
					toRemove.add(cells[i]);
					groupExists = true;
				}
			}
			if (groupExists)
				remove(toRemove.toArray());
			return children.toArray();
		}
		return null;
	}

	/**
	 * Toggles the collapsed state of the specified cells. NOTE: Your
	 * GraphLayoutCache must be <code>partial</code> (set <code>partial</code>
	 * to <code>true</code> in the constructor) in order to use the visibility
	 * functionality of expand/collapse, setVisible, etc.
	 * 
	 * @param cells
	 *            The cells to toggle the collapsed state for.
	 * @param collapseOnly
	 *            Whether cells should only be collapsed.
	 * @param expandOnly
	 *            Whether cells should only be expanded.
	 * 
	 */
	public void toggleCollapsedState(Object[] cells, boolean collapseOnly,
			boolean expandOnly) {
		List toExpand = new ArrayList();
		List toCollapse = new ArrayList();
		for (int i = 0; i < cells.length; i++) {
			Object cell = cells[i];
			CellView view = getMapping(cell, false);
			if (view != null) {

				// Adds to list of expansion cells if it is a leaf in the layout
				// cache and we do not only want to collapse.
				if (view.isLeaf() && !collapseOnly)
					toExpand.add(view.getCell());

				// Else adds to list of to-be-collapsed cells if it is not a
				// leaf in the layout cache we do not only want to expand.
				else if (!view.isLeaf() && !expandOnly)
					toCollapse.add(view.getCell());
			}
		}
		if (!toCollapse.isEmpty() || !toExpand.isEmpty())
			setCollapsedState(toCollapse.toArray(), toExpand.toArray());
	}

	/**
	 * Collapses all groups by hiding all their descendants. NOTE: Your
	 * GraphLayoutCache must be <code>partial</code> (set <code>partial</code>
	 * to <code>true</code> in the constructor) in order to use the visibility
	 * functionality of expand/collapse, setVisible, etc.
	 * 
	 * @param groups
	 */
	public void collapse(Object[] groups) {
		setCollapsedState(groups, null);
	}

	/**
	 * Expands all groups by showing all children. (Note: This does not show all
	 * descandants, but only the first generation of children.) NOTE: Your
	 * GraphLayoutCache must be <code>partial</code> (set <code>partial</code>
	 * to <code>true</code> in the constructor) in order to use the visibility
	 * functionality of expand/collapse, setVisible, etc.
	 */
	public void expand(Object[] cells) {
		setCollapsedState(null, cells);
	}

	/**
	 * Collapses and/or expands the specified cell(s) NOTE: Your
	 * GraphLayoutCache must be <code>partial</code> (set <code>partial</code>
	 * to <code>true</code> in the constructor) in order to use the visibility
	 * functionality of expand/collapse, setVisible, etc.
	 * 
	 * @param collapse
	 *            the cells to be collapsed
	 * @param expand
	 *            the cells to be expanded
	 */
	public void setCollapsedState(Object[] collapse, Object[] expand) {
		// Get all descandants for the groups
		ConnectionSet cs = new ConnectionSet();

		// Collapse cells
		List toHide = DefaultGraphModel.getDescendants(getModel(), collapse);
		if (collapse != null) {
			// Remove the groups themselfes
			for (int i = 0; i < collapse.length; i++) {
				toHide.remove(collapse[i]);
				cellWillCollapse(collapse[i]);
			}
			// Remove the ports (will be hidden automatically)
			for (int i = 0; i < collapse.length; i++) {
				int childCount = getModel().getChildCount(collapse[i]);
				if (childCount > 0) {
					for (int j = 0; j < childCount; j++) {
						Object child = getModel().getChild(collapse[i], j);
						if (getModel().isPort(child)) {
							toHide.remove(child);
						}
					}
				}
			}
		}

		// Expand cells
		Set toShow = new HashSet();
		if (expand != null) {
			for (int i = 0; i < expand.length; i++) {
				int childCount = getModel().getChildCount(expand[i]);
				for (int j = 0; j < childCount; j++) {
					toShow.add(getModel().getChild(expand[i], j));
				}
			}
		}
		setVisible(toShow.toArray(),
				(toHide != null) ? toHide.toArray() : null, cs);
	}

	/**
	 * Hook for subclassers to return the first or last visible port to replace
	 * the current source or target port of the edge. This is called when groups
	 * are collapsed for the edges that cross the group, ie. go from a child
	 * cell to a cell which is outside the group. This implementation returns
	 * the first port of the parent group if source is true, otherwise it
	 * returns the last port of the parent group.
	 */
	protected Object getParentPort(Object edge, boolean source) {
		// Contains the parent of the parent vertex, eg. the group
		Object parent = getModel().getParent(
				(source) ? DefaultGraphModel.getSourceVertex(getModel(), edge)
						: DefaultGraphModel.getTargetVertex(getModel(), edge));
		// Finds a port in the group
		int c = getModel().getChildCount(parent);
		for (int i = (source) ? c - 1 : 0; i < getModel().getChildCount(parent)
				&& i >= 0; i += (source) ? -1 : +1) {
			Object child = getModel().getChild(parent, i);
			if (getModel().isPort(child)) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Hook for subclassers to return the port to be used for edges that have
	 * been connected to the group. This is called from expand. This returns the
	 * first port of the first or last vertex depending on <code>source</code>.
	 */
	protected Object getChildPort(Object edge, boolean source) {
		GraphModel model = getModel();
		// Contains the parent of the port, eg. the group
		Object parent = (source) ? DefaultGraphModel.getSourceVertex(model,
				edge) : DefaultGraphModel.getTargetVertex(model, edge);
		// Finds a vertex in the group
		int c = model.getChildCount(parent);
		for (int i = (source) ? c - 1 : 0; i < c && i >= 0; i += (source) ? -1
				: +1) {
			Object child = model.getChild(parent, i);
			if (!model.isEdge(child) && !model.isPort(child)) {
				// Finds a port in the vertex
				for (int j = 0; j < model.getChildCount(child); j++) {
					Object port = model.getChild(child, j);
					if (model.isPort(port)) {
						return port;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Applies the <code>propertyMap</code> and the connection changes to the
	 * model. The initial <code>edits</code> that triggered the call are
	 * considered to be part of this transaction. Notifies the model- and undo
	 * listeners of the change. Note: The passed in attributes may contain
	 * PortViews.
	 */
	public void edit(Map attributes, ConnectionSet cs, ParentMap pm,
			UndoableEdit[] e) {
		if (attributes != null || cs != null || pm != null || e != null) {
			Object[] visible = null;
			if (isPartial() && showsInvisibleEditedCells) {
				Set tmp = new HashSet();
				if (attributes != null)
					tmp.addAll(attributes.keySet());
				if (cs != null)
					tmp.addAll(cs.getChangedEdges());
				if (pm != null)
					tmp.addAll(pm.getChangedNodes());
				tmp.removeAll(visibleSet);
				if (!tmp.isEmpty())
					visible = tmp.toArray();
			}
			GraphLayoutCacheEdit edit = createLocalEdit(null, attributes,
					visible, null);
			if (edit != null)
				e = augment(e, edit);
			// Pass to model
			graphModel.edit(attributes, cs, pm, e);
		}
	}

	/**
	 * A shortcut method that takes a nested map and passes it to the edit
	 * method.
	 */
	public void edit(Map attributes) {
		edit(attributes, null, null, null);
	}

	/**
	 * Applies the <code>attributes</code> to all <code>cells</code> by creating
	 * a map that contains the attributes for each cell and passing it to edit
	 * on this layout cache. Example:
	 * 
	 * <pre>
	 * Map attrs = new java.util.Hashtable();
	 * GraphConstants.setBackground(attrs, Color.RED);
	 * graph.getGraphLayoutCache().edit(graph.getSelectionCells(), attrs);
	 * </pre>
	 */
	public void edit(Object[] cells, Map attributes) {
		if (attributes != null && cells != null && cells.length > 0) {
			Map nested = new Hashtable();
			for (int i = 0; i < cells.length; i++)
				nested.put(cells[i], attributes);
			edit(nested, null, null, null);
		}
	}

	/**
	 * Applies the <code>attributes</code> to a single <code>cell</code> by
	 * creating a map that contains the attributes for this cell and passing it
	 * to edit on this layout cache. Example:
	 * 
	 * <pre>
	 * Map attrs = new java.util.Hashtable();
	 * GraphConstants.setBackground(attrs, Color.RED);
	 * graph.getGraphLayoutCache().editCell(graph.getSelectionCell(), attrs);
	 * </pre>
	 */
	public void editCell(Object cell, Map attributes) {
		if (attributes != null && cell != null) {
			edit(new Object[] { cell }, attributes);
		}
	}

	protected UndoableEdit[] augment(UndoableEdit[] e, UndoableEdit edit) {
		if (edit != null) {
			int size = (e != null) ? e.length + 1 : 1;
			UndoableEdit[] result = new UndoableEdit[size];
			if (e != null)
				System.arraycopy(e, 0, result, 0, size - 2);
			result[size - 1] = edit;
			return result;
		}
		return e;
	}

	/**
	 * Sends <code>cells</code> to back. Note: This expects an array of cells!
	 */
	public void toBack(Object[] cells) {
		if (cells != null && cells.length > 0) {
			graphModel.toBack(cells);
		}
	}

	/**
	 * Brings <code>cells</code> to front. Note: This expects an array of cells!
	 */
	public void toFront(Object[] cells) {
		if (cells != null && cells.length > 0) {
			graphModel.toFront(cells);
		}
	}

	/**
	 * Creates a local edit for the specified change. A local operation contains
	 * all visibility changes, as well as all changes to attributes that are
	 * local, and all control attributes. <br>
	 * Note: You must use cells as keys for the nested map, not cell views.
	 */
	protected GraphLayoutCacheEdit createLocalEdit(Object[] inserted,
			Map nested, Object[] visible, Object[] invisible) {
		// Create an edit if there are any view-local attributes set
		if ((nested != null && !nested.isEmpty())
				&& (!localAttributes.isEmpty() || isAllAttributesLocal())) {
			// Move or Copy Local Attributes to Local View
			Map globalMap = new Hashtable();
			Map localMap = new Hashtable();
			Map localAttr;
			Iterator it = nested.entrySet().iterator();
			while (it.hasNext()) {
				localAttr = new Hashtable();
				Map.Entry entry = (Map.Entry) it.next();
				// (cell, Hashtable)
				Object cell = entry.getKey();
				Map attr = (Map) entry.getValue();
				// Create Difference of Existing and New Attributes
				CellView tmpView = getMapping(cell, false);
				if (tmpView != null)
					attr = tmpView.getAllAttributes().diff(attr);
				// End of Diff
				Iterator it2 = attr.entrySet().iterator();
				while (it2.hasNext()) {
					Map.Entry entry2 = (Map.Entry) it2.next();
					// (key, value)
					Object key = entry2.getKey();
					Object value = entry2.getValue();
					boolean isControlAttribute = isControlAttribute(cell, key,
							value);
					if (isAllAttributesLocal() || isControlAttribute
							|| isLocalAttribute(cell, key, value)) {
						localAttr.put(key, value);
						if (!isControlAttribute)
							it2.remove();
					}
				}
				if (!localAttr.isEmpty())
					localMap.put(cell, localAttr);
				if (!attr.isEmpty())
					globalMap.put(cell, attr);
			}
			nested.clear();
			nested.putAll(globalMap);
			if (visible != null || invisible != null || !localMap.isEmpty()) {
				GraphLayoutCacheEdit edit = new GraphLayoutCacheEdit(inserted,
						new Hashtable(localMap), visible, invisible);
				edit.end();
				return edit;
			}
		} else if (visible != null || invisible != null) {
			GraphLayoutCacheEdit edit = new GraphLayoutCacheEdit(inserted,
					null, visible, invisible);
			edit.end();
			return edit;
		}
		return null;
	}

	/**
	 * Returns true if the set of local attributes contains <code>key</code>
	 */
	protected boolean isLocalAttribute(Object cell, Object key, Object value) {
		return localAttributes.contains(key);
	}

	/**
	 * Returns true if <code>key</code> is a control attribute
	 */
	protected boolean isControlAttribute(Object cell, Object key, Object value) {
		return GraphConstants.REMOVEALL.equals(key)
				|| GraphConstants.REMOVEATTRIBUTES.equals(key);
	}

	/**
	 * Handles the removal of view local attributes. Since these attributes are
	 * only being stored in the view, the option is provided to copy the values
	 * for that key into the model. Without this, those values are lost.
	 * 
	 * @param key
	 *            the key of the view local attribute
	 * @param addToModel
	 *            whether or not to move the attribute values to the graph model
	 * @param override
	 *            whether or not to override the key's value in the model cell's
	 *            attribute map if it exists
	 * @return whether or not the operation completed sucessfully
	 */
	public boolean removeViewLocalAttribute(Object key, boolean addToModel,
			boolean override) {
		if (localAttributes.contains(key)) {
			if (addToModel) {
				// Iterate through all views copying this attribute to the
				// cell.
				copyRemovedViewValue(key, addToModel, override,
						mapping.values());
				copyRemovedViewValue(key, addToModel, override,
						hiddenMapping.values());
			}
			localAttributes.remove(key);
			return true;
		}
		return false;
	}

	/**
	 * Helper method to copy removed view local attributes to model cell's
	 * 
	 * @param key
	 *            the key of the view local attribute
	 * @param addToModel
	 *            whether or not to move the attribute values to the graph model
	 * @param override
	 *            whether or not to override the key's value in the model cell's
	 *            attribute map if it exists
	 * @param coll
	 *            the current collection being analysed
	 */
	private void copyRemovedViewValue(Object key, boolean addToModel,
			boolean override, Collection coll) {
		Iterator iter = coll.iterator();
		while (iter.hasNext()) {
			CellView cellView = (CellView) iter.next();
			Map attributes = cellView.getAttributes();
			if (attributes.containsKey(key)) {
				if (addToModel) {
					Object cell = cellView.getCell();
					Map cellAttributes = graphModel.getAttributes(cell);
					if (cellAttributes != null) {
						boolean cellContainsKey = cellAttributes
								.containsKey(key);
						// Write the model cell's attribute map key
						// if overriding is enabled or if key doesn't
						// exist
						if (!override || !cellContainsKey) {
							Object value = attributes.get(key);
							cellAttributes.put(key, value);
						}
					}
				}
				attributes.remove(key);
			}
		}
	}

	/**
	 * An implementation of GraphLayoutCacheChange.
	 */
	public class GraphLayoutCacheEdit extends CompoundEdit implements
			GraphLayoutCacheEvent.GraphLayoutCacheChange {

		protected Object[] cells, previousCells = null;

		protected CellView[] context, hidden;

		protected Map attributes, previousAttributes;

		protected Object[] visible, invisible;

		/**
		 * The dirty region associated with this event prior to the change
		 */
		protected Rectangle2D dirtyRegion = null;

		// Remember which cells have changed for finding their context
		protected Set changedCells = new HashSet();

		/**
		 * Constructs a GraphViewEdit. This modifies the attributes of the
		 * specified views and may be used to notify UndoListeners.
		 * 
		 * @param nested
		 *            the map that defines the new attributes
		 */
		public GraphLayoutCacheEdit(Map nested) {
			this(null, nested, null, null);
			attributes = nested;
		}

		/**
		 * Constructs a GraphViewEdit. This modifies the attributes of the
		 * specified views and may be used to notify UndoListeners. This should
		 * also take an array of removed cell views, but it is not possible to
		 * add further UndoableEdits to an already executed CompoundEdit, such
		 * as a GraphModel change. Thus, to handle implicit changes -- rather
		 * than piggybacking on the model's event -- the CompoundEdit's addEdit
		 * method should be extended to accept and instantly execute sub-
		 * sequent edits (implicit changes to the view, such as removing a
		 * mapping, hiding a view or the like).
		 * 
		 * @param inserted
		 *            an array of inserted cells
		 * @param attributes
		 *            the map that defines the new attributes
		 * @param visible
		 *            an array defining which cells are visible
		 * @param invisible
		 *            an array defining which cells are invisible
		 */
		public GraphLayoutCacheEdit(Object[] inserted, Map attributes,
				Object[] visible, Object[] invisible) {
			super();
			this.attributes = attributes;
			this.previousAttributes = attributes;
			this.cells = inserted;
			this.visible = visible;
			this.invisible = invisible;
		}

		public Object getSource() {
			return GraphLayoutCache.this;
		}

		public boolean isSignificant() {
			return true;
		}

		/**
		 * Returns the cell views that have changed.
		 */
		public Object[] getChanged() {
			return changedCells.toArray();
		}

		/**
		 * Returns the cells that habe been made visible.
		 */
		public Object[] getInserted() {
			return invisible;
		}

		/**
		 * Returns the cells that have changed.
		 */
		public Object[] getRemoved() {
			return visible;
		}

		/**
		 * Returns the views that have not changed explicitly, but implicitly
		 * because one of their dependent cells has changed.
		 */
		public Object[] getContext() {
			return context;
		}

		/**
		 * Returns a map of (cell view, attribute) pairs.
		 */
		public Map getAttributes() {
			return attributes;
		}

		/**
		 * Returns a map of (cell view, attribute) pairs.
		 */
		public Map getPreviousAttributes() {
			return previousAttributes;
		}

		public Rectangle2D getDirtyRegion() {
			return dirtyRegion;
		}

		public void setDirtyRegion(Rectangle2D dirty) {
			this.dirtyRegion = dirty;

		}

		/**
		 * Redoes a change.
		 * 
		 * @exception CannotRedoException
		 *                if the change cannot be redone
		 */
		public void redo() throws CannotRedoException {
			super.redo();
			execute();
		}

		/**
		 * Undoes a change.
		 * 
		 * @exception CannotUndoException
		 *                if the change cannot be undone
		 */
		public void undo() throws CannotUndoException {
			super.undo();
			execute();
		}

		/**
		 * Execute this edit such that the next invocation to this method will
		 * invert the last execution.
		 */
		public void execute() {
			GraphModel model = getModel();
			changedCells.clear();
			// Remember or restore hidden cells
			if (hidden != null)
				for (int i = 0; i < hidden.length; i++)
					if (hidden[i] != null)
						mapping.put(hidden[i].getCell(), hidden[i]);

			if (invisible != null && invisible.length > 0) {
				CellView[] invisibleViews = new CellView[invisible.length];
				invisibleViews = getMapping(invisible, true);
				Rectangle2D changedBounds = getBounds(invisibleViews);
				dirtyRegion = RectUtils.union(dirtyRegion, changedBounds);
			}

			if (!remembersCellViews) // already remembered
				hidden = getMapping(invisible);
			// Handle visibility
			boolean updatePorts = setVisibleImpl(visible, true)
					| setVisibleImpl(invisible, false);
			if (visible != null) {
				for (int i = 0; i < visible.length; i++) {
					changedCells.add(visible[i]);

					// Only calls if not inserted
					if (cells == null)
						cellExpanded(visible[i]);
				}
			}
			if (invisible != null)
				for (int i = 0; i < invisible.length; i++)
					changedCells.add(invisible[i]);
			// Swap arrays
			Object[] tmp = visible;
			visible = invisible;
			invisible = tmp;
			// Handle attributes
			if (attributes != null) {
				previousAttributes = attributes;
				changedCells.addAll(attributes.keySet());
			}
			if (updatePorts)
				updatePorts();
			// Add ancestor cells to changed cells
			Set parentSet = new HashSet();
			Iterator it = changedCells.iterator();
			while (it.hasNext()) {
				Object parent = model.getParent(it.next());
				while (parent != null) {
					parentSet.add(parent);
					parent = model.getParent(parent);
				}
			}
			changedCells.addAll(parentSet);
			Set ctx = DefaultGraphModel.getEdges(getModel(),
					changedCells.toArray());
			context = getMapping(ctx.toArray());
			Set allChangedCells = new HashSet(changedCells);
			allChangedCells.addAll(ctx);
			CellView[] allChangedCellViews = getMapping(allChangedCells
					.toArray());
			Rectangle2D changedBounds = getBounds(allChangedCellViews);
			dirtyRegion = RectUtils.union(dirtyRegion, changedBounds);
			if (attributes != null) {
				attributes = handleAttributes(attributes);
			}
			// Refresh all changed cells
			refresh(getMapping(changedCells.toArray(), false), false);
			// Updates the connected edges. Make sure that changedCells
			// contains no edges, as these will be removed from the result.
			refresh(context, false);
			tmp = cells;
			cells = previousCells;
			previousCells = tmp;
			reloadRoots();
			fireGraphLayoutCacheChanged(GraphLayoutCache.this, this);
		}
	}

	/**
	 * Called when a child has been made visible by expanding its parent. This
	 * implementation translates the child so that it reflects the offset of the
	 * parent group since the child was last visible (see
	 * {@link #movesChildrenOnExpand}).
	 */
	protected void cellExpanded(Object cell) {
		GraphModel model = getModel();
		// Moves the child to the group origin if it is not a port
		if (movesChildrenOnExpand && !model.isPort(cell)) {
			CellView view = getMapping(cell, false);

			if (view != null) {
				CellView parent = getMapping(model.getParent(cell), false);
				if (parent != null) {
					if (DefaultGraphModel.isVertex(model, parent)) {
						// Computes the offset of the parent group
						Rectangle2D src = GraphConstants.getBounds(parent
								.getAllAttributes());
						Rectangle2D rect = parent.getBounds();
						if (rect != null && src != null) {
							double dx = src.getX() - rect.getX();
							double dy = src.getY() - rect.getY();

							// Gets the attributes from the cell view or
							// cell and translates the bounds or points
							AttributeMap attrs = view.getAttributes();
							if (!attrs.contains(GraphConstants.BOUNDS))
								attrs = model.getAttributes(view.getCell());
							attrs.translate(dx, dy);
						}
					}
				}
			}
		}
	}

	protected void cellWillCollapse(Object cell) {
		GraphModel model = getModel();
		if (movesParentsOnCollapse) {
			CellView view = getMapping(cell, false);
			if (view != null && !view.isLeaf()) {
				// Uses view-local attribute if available
				AttributeMap attrs = view.getAttributes();
				if (!attrs.contains(GraphConstants.BOUNDS)
						&& !localAttributes.contains(GraphConstants.BOUNDS))
					attrs = model.getAttributes(cell);

				// Moves the group to the origin of the children
				Rectangle2D src = GraphConstants.getBounds(attrs);
				Rectangle2D b = view.getBounds();
				// FIXME: What if the group is exactly at "defaultBounds"?
				if (resizesParentsOnCollapse || src == null
						|| src.equals(VertexView.defaultBounds)) {
					src = attrs.createRect(b.getX(), b.getY(), b.getWidth()
							* collapseXScale, b.getHeight() * collapseYScale);
					attrs.applyValue(GraphConstants.BOUNDS, src);
				} else {
					src.setFrame(b.getX(), b.getY(), src.getWidth(),
							src.getHeight());
				}
			}
		}
	}

	/**
	 * Attention: Undo will not work for routing-change if ROUTING and POINTS
	 * are stored in different locations. This happens if the model holds the
	 * routing attribute and the routing changes from unrouted to routed. In
	 * this case the points in the view are already routed according to the new
	 * scheme when written to the command history (-> no undo).
	 */
	protected Map handleAttributes(Map attributes) {
		Map undo = new Hashtable();
		CellView[] views = new CellView[attributes.size()];
		Iterator it = attributes.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			CellView cv = getMapping(entry.getKey(), false);
			views[i] = cv;
			i += 1;
			if (cv != null && cv.getAttributes() != null) {
				Map deltaNew = (Map) entry.getValue();
				// System.out.println("state=" + cv.getAttributes());
				// System.out.println("change=" + deltaNew);
				Map deltaOld = cv.getAttributes().applyMap(deltaNew);
				cv.refresh(this, this, false);
				// System.out.println("state'=" + cv.getAttributes());
				// System.out.println("change'=" + deltaOld);
				undo.put(cv.getCell(), deltaOld);
			}
		}
		// Re-route all child edges
		update(views);
		return undo;
	}

	//
	// Static Methods
	//
	/**
	 * Translates the specified views by the given amount.
	 * 
	 * @param views
	 *            an array of cell view to each be translated
	 * @param dx
	 *            the amount to translate the views in the x-axis
	 * @param dy
	 *            the amount to translate the views in the x-axis
	 */
	public static void translateViews(CellView[] views, double dx, double dy) {
		for (int i = 0; i < views.length; i++) {
			if (views[i] instanceof AbstractCellView) {
				((AbstractCellView) views[i]).translate(dx, dy);
			}
		}
	}

	/**
	 * Returns a collection of cells that are connected to the specified cell by
	 * edges. Any cells specified in the exclude set will be ignored.
	 * 
	 * @param cell
	 *            The cell from which the neighbours will be determined
	 * @param exclude
	 *            The set of cells to ignore when searching
	 * @param directed
	 *            whether or not direction of edges should be taken into account
	 * @param visibleCells
	 *            whether or not to only consider visible cells
	 * @return Returns the list of neighbours for <code>cell</code>
	 */
	public List getNeighbours(Object cell, Set exclude, boolean directed,
			boolean visibleCells) {
		// Traverse Graph
		GraphModel model = getModel();
		Object[] fanout = (directed) ? DefaultGraphModel.getOutgoingEdges(
				model, cell) : DefaultGraphModel.getEdges(model,
				new Object[] { cell }).toArray();
		List neighbours = new ArrayList(fanout.length);
		Set localExclude = new HashSet(fanout.length + 8, (float) 0.75);
		for (int i = 0; i < fanout.length; i++) {
			// if only visible cells are being processed, check that this
			// edge is visible before looking for neighbours with it
			if (!visibleCells || isVisible(fanout[i])) {
				Object neighbour = DefaultGraphModel.getOpposite(model,
						fanout[i], cell);
				if (neighbour != null
						&& (exclude == null || !exclude.contains(neighbour))
						&& !localExclude.contains(neighbour)
						&& (!visibleCells || isVisible(neighbour))) {
					localExclude.add(neighbour);
					neighbours.add(neighbour);
				}
			}
		}
		return neighbours;
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
		return getEdges(cell, exclude, visibleCells, selfLoops, false);
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
		return getEdges(cell, exclude, visibleCells, selfLoops, true);
	}

	/**
	 * Returns the incoming or outgoing edges for cell. Cell should be a port or
	 * a vertex.
	 * 
	 * @param cell
	 *            The cell from which the edges will be determined
	 * @param exclude
	 *            The set of edges to ignore when searching
	 * @param visibleCells
	 *            whether or not only visible cells should be processed
	 * @param selfLoops
	 *            whether or not to include self loops in the returned list
	 * @param incoming
	 *            <code>true</code> if incoming edges are to be obtained,
	 *            <code>false</code> if outgoing edges are to be obtained
	 * @return Returns the list of incoming or outgoing edges for
	 *         <code>cell</code>
	 */
	protected List getEdges(Object cell, Set exclude, boolean visibleCells,
			boolean selfLoops, boolean incoming) {
		GraphModel model = getModel();
		Object[] edges = DefaultGraphModel.getEdges(model, cell, incoming);

		List edgeList = new ArrayList(edges.length);
		Set localExclude = new HashSet(edges.length);
		for (int i = 0; i < edges.length; i++) {
			// Check that the edge is neiter in the passed in exclude set or
			// the local exclude set. Also, if visibleCells is true check
			// the edge is visible in the cache.
			if ((exclude == null || !exclude.contains(edges[i]))
					&& !localExclude.contains(edges[i])
					&& (!visibleCells || isVisible(edges[i]))) {
				// Add the edge to the list if all edges, including self loops
				// are allowed. If self loops are not allowed, ensure the
				// source and target of the edge are different
				if (selfLoops == true
						|| model.getSource(edges[i]) != model
								.getTarget(edges[i])) {
					edgeList.add(edges[i]);
				}
				localExclude.add(edges[i]);
			}
		}
		return edgeList;
	}

	/**
	 * Returns all views, shortcut to getAllDescendants(getRoots())
	 */
	public CellView[] getAllViews() {
		return getAllDescendants(getRoots());
	}

	/**
	 * Returns all views, including descendants that have a parent in
	 * <code>views</code>, especially the PortViews. Note: Iterative
	 * Implementation using model.getChild and getMapping on this cell mapper.
	 */
	public CellView[] getAllDescendants(CellView[] views) {
		Stack stack = new Stack();
		for (int i = 0; i < views.length; i++)
			if (views[i] != null)
				stack.add(views[i]);
		ArrayList result = new ArrayList();
		while (!stack.isEmpty()) {
			CellView tmp = (CellView) stack.pop();
			Object[] children = tmp.getChildViews();
			for (int i = 0; i < children.length; i++)
				stack.add(children[i]);
			result.add(tmp);
			// Add Port Views
			for (int i = 0; i < graphModel.getChildCount(tmp.getCell()); i++) {
				Object child = graphModel.getChild(tmp.getCell(), i);
				if (graphModel.isPort(child)) {
					CellView view = getMapping(child, false);
					if (view != null)
						stack.add(view);
				}
			}
		}
		CellView[] ret = new CellView[result.size()];
		result.toArray(ret);
		return ret;
	}

	/**
	 * Returns the hiddenMapping.
	 * 
	 * @return Map
	 */
	public Map getHiddenMapping() {
		return hiddenMapping;
	}

	/**
	 * Sets the showsExistingConnections
	 * 
	 * @param showsExistingConnections
	 */
	public void setShowsExistingConnections(boolean showsExistingConnections) {
		this.showsExistingConnections = showsExistingConnections;
	}

	/**
	 * Returns the showsExistingConnections.
	 * 
	 * @return boolean
	 */
	public boolean isShowsExistingConnections() {
		return showsExistingConnections;
	}

	/**
	 * Sets the showsInsertedConnections
	 * 
	 * @param showsInsertedConnections
	 */
	public void setShowsInsertedConnections(boolean showsInsertedConnections) {
		this.showsInsertedConnections = showsInsertedConnections;
	}

	/**
	 * Returns the showsInsertedConnections.
	 * 
	 * @return boolean
	 */
	public boolean isShowsInsertedConnections() {
		return showsInsertedConnections;
	}

	/**
	 * Sets the hidesExistingConnections
	 * 
	 * @param hidesExistingConnections
	 */
	public void setHidesExistingConnections(boolean hidesExistingConnections) {
		this.hidesExistingConnections = hidesExistingConnections;
	}

	/**
	 * Returns the hidesExistingConnections.
	 * 
	 * @return boolean
	 */
	public boolean isHidesExistingConnections() {
		return hidesExistingConnections;
	}

	/**
	 * Sets the hidesDanglingConnections
	 * 
	 * @param hidesDanglingConnections
	 */
	public void setHidesDanglingConnections(boolean hidesDanglingConnections) {
		this.hidesDanglingConnections = hidesDanglingConnections;
	}

	/**
	 * Returns the hidesDanglingConnections.
	 * 
	 * @return boolean
	 */
	public boolean isHidesDanglingConnections() {
		return hidesDanglingConnections;
	}

	/**
	 * Sets the rememberCellViews.
	 * 
	 * @param rememberCellViews
	 *            The rememberCellViews to set
	 */
	public void setRemembersCellViews(boolean rememberCellViews) {
		this.remembersCellViews = rememberCellViews;
	}

	/**
	 * Returns the remembersCellViews.
	 * 
	 * @return boolean
	 */
	public boolean isRemembersCellViews() {
		return remembersCellViews;
	}

	/**
	 * Sets the hiddenSet.
	 * 
	 * NOTE: Your GraphLayoutCache must be <code>partial</code> (set
	 * <code>partial</code> to <code>true</code> in the constructor) in order to
	 * use the visibility functionality of expand/collapse, setVisible, etc.
	 * 
	 * @param hiddenSet
	 *            The hiddenSet to set
	 */
	public void setHiddenSet(Map hiddenSet) {
		this.hiddenMapping = hiddenSet;
	}

	/**
	 * @return Returns the localAttributes.
	 */
	public Set getLocalAttributes() {
		return localAttributes;
	}

	/**
	 * @param localAttributes
	 *            The localAttributes to set.
	 */
	public void setLocalAttributes(Set localAttributes) {
		this.localAttributes = localAttributes;
	}

	/**
	 * @return Returns the askLocalAttribute.
	 */
	public boolean isAllAttributesLocal() {
		return allAttributesLocal;
	}

	/**
	 * @param allAttributesLocal
	 *            The allAttributesLocal to set.
	 */
	public void setAllAttributesLocal(boolean allAttributesLocal) {
		this.allAttributesLocal = allAttributesLocal;
	}

	/**
	 * Returns true if cells should be auto-sized when their values change
	 * 
	 * @return true if cells should be auto-sized when their values change
	 */
	public boolean isAutoSizeOnValueChange() {
		return autoSizeOnValueChange;
	}

	/**
	 * Determines whether cells should be auto-sized when their values change.
	 * Fires a property change event if the new setting is different from the
	 * existing setting.
	 * 
	 * @param flag
	 *            a boolean value, true if cells should be auto-sized when their
	 *            values change
	 */
	public void setAutoSizeOnValueChange(boolean flag) {
		this.autoSizeOnValueChange = flag;
	}

	/**
	 * @return Returns the selectsAllInsertedCells.
	 */
	public boolean isSelectsAllInsertedCells() {
		return selectsAllInsertedCells;
	}

	/**
	 * @param selectsAllInsertedCells
	 *            The selectsAllInsertedCells to set.
	 */
	public void setSelectsAllInsertedCells(boolean selectsAllInsertedCells) {
		this.selectsAllInsertedCells = selectsAllInsertedCells;
	}

	/**
	 * @return Returns the selectsLocalInsertedCells.
	 */
	public boolean isSelectsLocalInsertedCells() {
		return selectsLocalInsertedCells;
	}

	/**
	 * @param selectsLocalInsertedCells
	 *            The selectsLocalInsertedCells to set.
	 */
	public void setSelectsLocalInsertedCells(boolean selectsLocalInsertedCells) {
		this.selectsLocalInsertedCells = selectsLocalInsertedCells;
	}

	/**
	 * @return Returns the reconnectsEdgesToVisibleParent.
	 * @deprecated edges are moved to parent view and back automatically
	 */
	public boolean isReconnectsEdgesToVisibleParent() {
		return reconnectsEdgesToVisibleParent;
	}

	/**
	 * @param reconnectsEdgesToVisibleParent
	 *            The reconnectsEdgesToVisibleParent to set.
	 * @deprecated edges are moved to parent view and back automatically
	 */
	public void setReconnectsEdgesToVisibleParent(
			boolean reconnectsEdgesToVisibleParent) {
		this.reconnectsEdgesToVisibleParent = reconnectsEdgesToVisibleParent;
	}

	/**
	 * @return Returns the showsChangedConnections.
	 */
	public boolean isShowsChangedConnections() {
		return showsChangedConnections;
	}

	/**
	 * @param showsChangedConnections
	 *            The showsChangedConnections to set.
	 */
	public void setShowsChangedConnections(boolean showsChangedConnections) {
		this.showsChangedConnections = showsChangedConnections;
	}

	/**
	 * @return Returns the moveChildrenOnExpand.
	 */
	public boolean isMovesChildrenOnExpand() {
		return movesChildrenOnExpand;
	}

	/**
	 * @param moveChildrenOnExpand
	 *            The moveChildrenOnExpand to set.
	 */
	public void setMovesChildrenOnExpand(boolean moveChildrenOnExpand) {
		this.movesChildrenOnExpand = moveChildrenOnExpand;
	}

	public boolean isShowsInvisibleEditedCells() {
		return showsInvisibleEditedCells;
	}

	public void setShowsInvisibleEditedCells(boolean showsInvisibleEditedCells) {
		this.showsInvisibleEditedCells = showsInvisibleEditedCells;
	}

	/**
	 * @return Returns the collapseXScale.
	 */
	public double getCollapseXScale() {
		return collapseXScale;
	}

	/**
	 * @param collapseXScale
	 *            The collapseXScale to set.
	 */
	public void setCollapseXScale(double collapseXScale) {
		this.collapseXScale = collapseXScale;
	}

	/**
	 * @return Returns the collapseYScale.
	 */
	public double getCollapseYScale() {
		return collapseYScale;
	}

	/**
	 * @param collapseYScale
	 *            The collapseYScale to set.
	 */
	public void setCollapseYScale(double collapseYScale) {
		this.collapseYScale = collapseYScale;
	}

	/**
	 * @return Returns the movesParentsOnCollapse.
	 */
	public boolean isMovesParentsOnCollapse() {
		return movesParentsOnCollapse;
	}

	/**
	 * @param movesParentsOnCollapse
	 *            The movesParentsOnCollapse to set.
	 */
	public void setMovesParentsOnCollapse(boolean movesParentsOnCollapse) {
		this.movesParentsOnCollapse = movesParentsOnCollapse;
	}

	/**
	 * @return Returns the resizesParentsOnCollapse.
	 */
	public boolean isResizesParentsOnCollapse() {
		return resizesParentsOnCollapse;
	}

	/**
	 * @param resizesParentsOnCollapse
	 *            The resizesParentsOnCollapse to set.
	 */
	public void setResizesParentsOnCollapse(boolean resizesParentsOnCollapse) {
		this.resizesParentsOnCollapse = resizesParentsOnCollapse;
	}

	/**
	 * Serialization support.
	 */
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.defaultWriteObject();
		// Write out the hidden mapping
		Map map = new Hashtable(hiddenMapping);
		s.writeObject(map);
	}

	/**
	 * Serialization support.
	 */
	private void readObject(ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		s.defaultReadObject();
		// Read the hidden mapping
		Map map = (Map) s.readObject();
		hiddenMapping = new WeakHashMap(map);
	}

}
