/*
 * @(#)Edge.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.io.Serializable;
import java.util.List;

/**
 * Defines the requirements for an object that represents an Edge in a
 * GraphModel.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public interface Edge extends GraphCell {

	/**
	 * Returns the source of the edge.
	 */
	Object getSource();

	/**
	 * Returns the target of the edge.
	 */
	Object getTarget();

	/**
	 * Sets the source of the edge.
	 */
	void setSource(Object port);

	/**
	 * Returns the target of <code>edge</code>.
	 */
	void setTarget(Object port);

	//
	// Routing
	//

	public static interface Routing extends Serializable {

		public static final int NO_PREFERENCE = -1;

		/**
		 * Returns the points to be used for the edge.
		 * @param cache TODO
		 * @param edge
		 *            The edge view to route the points for.
		 */
		public List route(GraphLayoutCache cache, EdgeView edge);

		/**
		 * Returns the preferred line style for this routing. A return value of
		 * {@link #NO_PREFERENCE} means no preference.
		 */
		public int getPreferredLineStyle(EdgeView edge);

	}

}