/*
 * @(#)DefaultCellViewFactory.java 1.0 29-NOV-04
 * 
 * Copyright (c) 2001-2005 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.io.Serializable;

/**
 * The default implementation of a cell view factory that returns the default
 * views for vertices, edges and ports.
 */
public class DefaultCellViewFactory implements CellViewFactory, Serializable {
	
	/**
	 * Constructs a view for the specified cell and associates it with the
	 * specified object using the specified CellMapper. This calls refresh on
	 * the created CellView to create all dependent views.
	 * <p>
	 * Note: The mapping needs to be available before the views of child cells
	 * and ports are created.
	 * <b>Note: This method must return new instances!</b>
	 * 
	 * @param cell
	 *            reference to the object in the model
	 */
	public CellView createView(GraphModel model, Object cell) {
		CellView view = null;
		if (model.isPort(cell))
			view = createPortView(cell);
		else if (model.isEdge(cell))
			view = createEdgeView(cell);
		else
			view = createVertexView(cell);
		return view;
	}

	/**
	 * Constructs a VertexView view for the specified object.
	 */
	protected VertexView createVertexView(Object cell) {
		return new VertexView(cell);
	}

	/**
	 * Constructs an EdgeView view for the specified object.
	 */
	protected EdgeView createEdgeView(Object cell) {
		if (cell instanceof Edge)
			return createEdgeView((Edge) cell);
		else
			return new EdgeView(cell);
	}

	/**
	 * Constructs a PortView view for the specified object.
	 */
	protected PortView createPortView(Object cell) {
		if (cell instanceof Port)
			return createPortView((Port) cell);
		else
			return new PortView(cell);
	}

	/**
	 * Constructs an EdgeView view for the specified object.
	 * 
	 * @deprecated replaced by {@link #createEdgeView(Object)}since
	 *             JGraph no longer exposes dependecies on GraphCell subclasses
	 *             (Port, Edge)
	 */
	protected EdgeView createEdgeView(Edge cell) {
		return new EdgeView(cell);
	}

	/**
	 * Constructs a PortView view for the specified object.
	 * 
	 * @deprecated replaced by {@link #createPortView(Object)}since
	 *             JGraph no longer exposes dependecies on GraphCell subclasses
	 *             (Port, Edge)
	 */
	protected PortView createPortView(Port cell) {
		return new PortView(cell);
	}
}
