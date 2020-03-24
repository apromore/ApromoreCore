/*
 * $Id: GraphLayoutCacheEvent.java,v 1.2 2008/02/15 11:09:17 david Exp $
 *
 * Copyright (c) 2001-2008 Gaudenz Alder
 *
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.apromore.jgraph.event;

import java.awt.geom.Rectangle2D;
import java.util.EventObject;
import java.util.Map;

/**
 * Encapsulates information describing changes to a graph layout cache, and is
 * used to notify graph layout cache listeners of the change. Note that graph
 * layout cache events do not repeat information in graph model events if there
 * is no view specific information. The idea of this event is to provide
 * information on what has changed in the graph layout cache only.
 */
public class GraphLayoutCacheEvent extends EventObject {

	/**
	 * The object that constitutes the change.
	 */
	protected GraphLayoutCacheChange change;

	/**
	 * Used to create an event when cells have been changed, inserted, or
	 * removed, identifying the change as a GraphLayoutCacheChange object.
	 * 
	 * @param source
	 *            the Object responsible for generating the event (typically the
	 *            creator of the event object passes <code>this</code> for its
	 *            value)
	 * 
	 * @param change
	 *            the object that describes the change
	 */
	public GraphLayoutCacheEvent(Object source, GraphLayoutCacheChange change) {
		super(source);
		this.change = change;
	}

	/**
	 * Returns the object that constitutes the change.
	 * 
	 * @return the object that constitutes the change
	 */
	public GraphLayoutCacheChange getChange() {
		return change;
	}

	/**
	 * Defines the interface for objects that may be used to represent a change
	 * to the graph layout cache.
	 */
	public static interface GraphLayoutCacheChange {

		/**
		 * Returns the source of this change. This can either be a view or a
		 * model, if this change is a GraphModelChange. Note: This is not
		 * necessarily the same as the source of the event and is used
		 * separately in the graphundomanager.
		 * 
		 * @return the source fo this change
		 */
		public Object getSource();

		/**
		 * Returns the cells that have changed.
		 * 
		 * @return the cell changed
		 */
		public Object[] getChanged();

		/**
		 * Returns the cells that have been inserted.
		 * 
		 * @return the cells that were inserted by the change
		 */
		public Object[] getInserted();

		/**
		 * Returns the cells that have been removed.
		 * 
		 * @return the cells that were removed by the change
		 */
		public Object[] getRemoved();

		/**
		 * Returns a map that contains (object, map) pairs which holds the new
		 * attributes for each changed cell. Note: This returns a map of (cell,
		 * map) pairs for an insert on a model that is not an attribute store.
		 * Use getPreviousAttributes to access the attributes that have been
		 * stored in the model.
		 */
		public Map getAttributes();

		/**
		 * Returns a map that contains (object, map) pairs which holds the
		 * previous attributes for the changed cells.
		 * 
		 * @return map of attributes before the change
		 */
		public Map getPreviousAttributes();

		/**
		 * Returns the dirty region for the original position of the
		 * changed cells before the change happened.
		 * @return the dirty region prior to the event
		 */
		public Rectangle2D getDirtyRegion();
		
		/**
		 * In some cases the class firing this event will not have access
		 * to the dirty region prior to the change. It is then up to the
		 * receiving class to set it once.
		 * @param dirty
		 */
		public void setDirtyRegion(Rectangle2D dirty);
		
		/**
		 * Returns the objects that have not changed explicitly, but implicitly
		 * because one of their dependent cells has changed. This is typically
		 * used to return the edges that are attached to vertices, which in turn
		 * have been resized or moved.
		 * 
		 * @return array of contextual cells
		 */
		public Object[] getContext();

	}

}