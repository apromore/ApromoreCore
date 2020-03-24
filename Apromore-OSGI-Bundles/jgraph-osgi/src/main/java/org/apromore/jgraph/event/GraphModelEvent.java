/*
 * @(#)GraphModelEvent.java	1.0 03-JUL-04
 *
 * Copyright (c) 2001-2005 Gaudenz Alder
 *
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.apromore.jgraph.event;

import java.util.EventObject;

import org.apromore.jgraph.graph.CellView;
import org.apromore.jgraph.graph.ConnectionSet;
import org.apromore.jgraph.graph.GraphLayoutCache;
import org.apromore.jgraph.graph.ParentMap;

/**
 * Encapsulates information describing changes to a graph model, and is used to
 * notify graph model listeners of the change.
 * 
 * @author Gaudenz Alder
 * @version 1.0 1/1/2
 *  
 */

public class GraphModelEvent extends EventObject {

	/**
	 * The object that constitutes the change.
	 */
	protected GraphModelChange change;

	/**
	 * Used to create an event when cells have been changed, inserted, or
	 * removed, identifying the change as a ModelChange object.
	 * 
	 * @param source
	 *            the Object responsible for generating the event (typically the
	 *            creator of the event object passes <code>this</code> for its
	 *            value)
	 * @param change
	 *            the object that describes the change
	 * @see org.apromore.jgraph.graph.GraphCell
	 *  
	 */
	public GraphModelEvent(Object source, GraphModelChange change) {
		super(source);
		this.change = change;
	}

	/**
	 * Returns the object that constitutes the change.
	 * 
	 * @return the object that constitutes the change.
	 */
	public GraphModelChange getChange() {
		return change;
	}

	/**
	 * Defines the interface for objects that may be included into a
	 * GraphModelEvent to describe a model change.
	 */
	public static interface GraphModelChange extends
			GraphLayoutCacheEvent.GraphLayoutCacheChange {

		/**
		 * Returns a connection set representing the graph structure after the
		 * change was applied
		 * 
		 * @return the connection set of the graph after the change
		 */
		public ConnectionSet getConnectionSet();

		/**
		 * Returns a connection set representing the graph structure before the
		 * change was applied ( an "undo" of the change).
		 * 
		 * @return the connection set of the graph before the change
		 */
		public ConnectionSet getPreviousConnectionSet();

		/**
		 * Returns a parent map representing the group structure after the
		 * change was applied
		 * 
		 * @return the changed parent map
		 */
		public ParentMap getParentMap();

		/**
		 * Returns a parent map representing the group structure before the
		 * change was applied ( an "undo" of the change )
		 * 
		 * @return the previous parent map
		 */
		public ParentMap getPreviousParentMap();

		/**
		 * Allows a <code>GraphLayoutCache</code> to store cell views for
		 * cells that have been removed. Such cell views are used for
		 * re-insertion and restoring the visual attributes.
		 * 
		 * @param view
		 *            the <code>GraphLayoutCache</code> to store the removed
		 *            cells
		 * @param cellViews
		 *            the cell views to be stored
		 */
		public void putViews(GraphLayoutCache view, CellView[] cellViews);

		/**
		 * Allows a <code>GraphLayoutCache</code> to retrieve an array of
		 * <code>CellViews</code> that was previously stored with
		 * <code>putViews(GraphLayoutCache, CellView[])</code>.
		 * 
		 * @param view
		 *            the <code>GraphLayoutCache</code> whose stored cells are
		 *            to be retrieved
		 */
		public CellView[] getViews(GraphLayoutCache view);

	}

}