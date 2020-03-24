/*
 * @(#)CellView.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import org.apromore.jgraph.JGraph;

/**
 * Defines the requirements for an object that
 * represents a view for a model cell.
 *
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public interface CellView {

	//
	// Data Source
	//

	/**
	 * Returns the model object that this view represents.
	 */
	Object getCell();

	/**
	 * Refresh this view based on the model cell. This is
	 * messaged when the model cell has changed.
	 */
	void refresh(GraphLayoutCache cache, CellMapper mapper, boolean createDependentViews);

	/**
	 * Update this view's attributes. This is messaged whenever refresh is
	 * messaged, and additionally when the context of the cell has changed,
	 * and during live-preview changes to the view.
	 * @param cache TODO
	 */
	void update(GraphLayoutCache cache);

	void childUpdated();

	//
	// Group Structure
	//

	/**
	 * Returns the parent of view of this view.
	 */
	CellView getParentView();

	/**
	 * Returns the child views of this view.
	 */
	CellView[] getChildViews();

	/**
	 * Removes this view from the list of childs of the parent.
	 */
	void removeFromParent();

	/**
	 * Returns true if the view is a leaf.
	 */
	boolean isLeaf();

	//
	// View Methods
	//

	/**
	 * Returns the bounds for the view.
	 */
	Rectangle2D getBounds();

	/**
	 * Returns true if the view intersects the given rectangle.
	 */
	boolean intersects(JGraph g, Rectangle2D rect);

	/**
	 * Returns the intersection of the bounding rectangle and the straight line
	 * between the source and the specified point p. The specified point is
	 * expected not to intersect the bounds. Note: You must override this method
	 * if you use a different renderer. This is because this method relies on
	 * the VertexRenderer interface, which can not be safely assumed for
	 * subclassers.
	 */
	Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p);
	
	/**
	 * Apply the specified map of attributes on the view.
	 */
	Map changeAttributes(GraphLayoutCache cache, Map map);

	/**
	 * Returns all attributes of the view as a map.
	 */
	AttributeMap getAttributes();

	AttributeMap getAllAttributes();

	//
	// Renderer, Editor and Handle
	//

	/**
	 * Returns a renderer component, configured for the view.
	 */
	Component getRendererComponent(
		JGraph graph,
		boolean selected,
		boolean focus,
		boolean preview);

	/**
	 * Returns a cell handle for the view.
	 */
	CellHandle getHandle(GraphContext context);

	/**
	 * Returns a cell editor for the view.
	 */
	GraphCellEditor getEditor();

}