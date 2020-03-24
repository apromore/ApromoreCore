/*
 * @(#)AbstractCellView.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2005 Gaudenz Alder
 *  
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.apromore.jgraph.graph;

import java.awt.Component;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Stack;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apromore.jgraph.JGraph;

/**
 * The abstract base class for all cell views.
 * 
 * @version 1.0 1/3/02
 * @author Gaudenz Alder
 */
public abstract class AbstractCellView implements CellView, Serializable {

	/** Editor for the cell. */
	public static transient GraphCellEditor cellEditor;

	// Headless environment may have no default fonts installed
	static {
		try {
			cellEditor = new DefaultGraphCellEditor();
		} catch (Error e) {
			// No cell editor
		}
	}

	/** Reference to the cell for this view */
	protected Object cell = null;

	/** Cached parent view */
	protected CellView parent = null;

	/** Cached child views. Default is a ArrayList with allocation size 0. */
	protected java.util.List childViews = new ArrayList(0);

	/**
	 * Contains the complete set of attributes, including the cell's attributes.
	 * The values in this map are overriden by the corresponding values in
	 * <code>attributes</code>.
	 */
	protected AttributeMap allAttributes = createAttributeMap();

	/**
	 * Hashtable for attributes. Value in this map override the values in
	 * <code>allAttributes</code>.
	 */
	protected AttributeMap attributes = allAttributes;

	/** Cached bounds of all children if vertex is a group */
	protected transient Rectangle2D groupBounds = VertexView.defaultBounds;

	/**
	 * Constructs an empty abstract cell view. You should set a cell on this
	 * view using setCell before doing anything. Optionally you can also set a
	 * different attribute map using setAttributeMap. Note: To change the
	 * attribute map you should now use the changeAttributes method.
	 */
	public AbstractCellView() {
	}

	/**
	 * Constructs a view for the specified model object, and invokes update on
	 * the new instance.
	 * 
	 * @param cell
	 *            reference to the model object
	 */
	public AbstractCellView(Object cell) {
		setCell(cell);
	}

	/**
	 * Hook for subclassers to avoid creating an empty AttributeMap during
	 * construction of the instance. Override this and return null if you want
	 * to avoid creation of an attribute map at construction time.
	 */
	protected AttributeMap createAttributeMap() {
		return new AttributeMap();
	}

	/**
	 * Returns the model object that this view represents.
	 * 
	 * @return the model object that this view represents
	 */
	public Object getCell() {
		return cell;
	}

	/**
	 * Sets the model object that this view represents to the specified cell
	 * 
	 * @param cell
	 *            the model object this view will represent
	 */
	public void setCell(Object cell) {
		this.cell = cell;
	}

	/**
	 * Create child views and reload properties for this view. Invokes update
	 * first.
	 * 
	 * @param cache
	 *            the graph model to be used
	 * @param mapper
	 *            the cell mapper to be used
	 * @param createDependentViews
	 *            whether or not to create a view if one does not already exist
	 */
	public void refresh(GraphLayoutCache cache, CellMapper mapper,
			boolean createDependentViews) {
		// Re-read global attributes
		GraphModel model = cache.getModel();
		allAttributes = getCellAttributes(model);
		// Cache Parent View
		if (mapper != null && model != null) {
			// Create parent only if it's visible in the graph
			Object par = model.getParent(cell);
			CellView tmp = mapper.getMapping(par, createDependentViews);
			if (tmp != parent)
				removeFromParent();
			parent = tmp;
		}
		// Cache Cell Attributes in View
		update(cache);
		// Re-load Child Views
		childViews.clear();
		for (int i = 0; i < model.getChildCount(cell); i++) {
			Object child = model.getChild(cell, i);
			CellView view = mapper.getMapping(child, createDependentViews);
			if (!model.isPort(child) && view != null)
				childViews.add(view);
		}
	}

	/**
	 * Hook for subclassers to avoid cloning the cell's attributes. Return
	 * model.getAttributes(cell) to avoid cloning.
	 */
	protected AttributeMap getCellAttributes(GraphModel model) {
		return (AttributeMap) model.getAttributes(cell).clone();
	}

	/**
	 * Update attributes for this view and indicate to the parent this child has
	 * been updated
	 */
	public void update(GraphLayoutCache cache) {
		mergeAttributes();
		// Notify Parent
		groupBounds = null;
		childUpdated();
	}

	/**
	 * Implements the merging of the cell's attributes, initially stored in
	 * allAttributes, and the location attributes. The result should be stored
	 * in allAttributes. This hook is for subclassers to change the merging
	 * strategy.
	 */
	protected void mergeAttributes() {
		allAttributes.putAll(attributes);
	}

	/**
	 * Indicates to parent, if any, that this child has been updated.
	 */
	public void childUpdated() {
		if (parent != null)
			parent.childUpdated();
		groupBounds = null;
	}

	//
	// Graph Structure
	//

	/**
	 * Returns the parent view for this view.
	 * 
	 * @return the parent view for this view
	 */
	public CellView getParentView() {
		return parent;
	}

	/**
	 * Returns the child views of this view.
	 * 
	 * @return the child views of this view
	 */
	public CellView[] getChildViews() {
		CellView[] array = new CellView[childViews.size()];
		childViews.toArray(array);
		return array;
	}

	/**
	 * Returns all views, including descendants that have a parent in
	 * <code>views</code> without the PortViews. Note: Iterative Implementation
	 * using view.getChildViews. This returns the array in inverse order, ie
	 * with the top most cell view at index 0.
	 * 
	 * @param views
	 *            the cell views whose descendants are to be returned
	 * @return the specified views and all their descendant views
	 */
	public static CellView[] getDescendantViews(CellView[] views) {
		Stack stack = new Stack();
		for (int i = 0; i < views.length; i++)
			stack.add(views[i]);
		ArrayList result = new ArrayList();
		while (!stack.isEmpty()) {
			CellView tmp = (CellView) stack.pop();
			Object[] children = tmp.getChildViews();
			for (int i = 0; i < children.length; i++)
				stack.add(children[i]);
			result.add(tmp);
		}
		CellView[] ret = new CellView[result.size()];
		result.toArray(ret);
		return ret;
	}

	/**
	 * Removes this view from the list of children of the parent.
	 */
	public void removeFromParent() {
		if (parent instanceof AbstractCellView) {
			java.util.List list = ((AbstractCellView) parent).childViews;
			// TODO performance could be quadratic
			list.remove(this);
		}
	}

	/**
	 * Returns <code>true</code> if the view is a leaf.
	 * 
	 * @return <code>true</code> if the view is a leaf
	 */
	public boolean isLeaf() {
		return childViews.isEmpty();
	}

	//
	// View Attributes
	//

	/**
	 * Return the attributes of the view.
	 * 
	 * @return the <code>attributes</code> of this view
	 */
	public AttributeMap getAttributes() {
		return attributes;
	}

	/**
	 * Sets the attributes of this view to the specified value
	 * 
	 * @param attributes
	 *            the new attributes to set
	 */
	public void setAttributes(AttributeMap attributes) {
		this.attributes = attributes;
	}

	/**
	 * Returns the attributes of the view combined with the attributes of the
	 * corresponding cell. The view's attributes override the cell's attributes
	 * with the same key.
	 */
	public AttributeMap getAllAttributes() {
		return allAttributes;
	}

	/**
	 * Applies <code>change</code> to the attributes of the view and calls
	 * update.
	 * 
	 * @param change
	 *            a map of attribute changes to apply
	 * @return the undo map that reverses this change
	 */
	public Map changeAttributes(GraphLayoutCache cache, Map change) {
		if (change != null) {
			Map undo = attributes.applyMap(change);
			update(cache);
			return undo;
		}
		return null;
	}

	//
	// View Methods
	//

	/**
	 * Returns the cached bounds for the group if isleaf is false
	 */
	public Rectangle2D getBounds() {
		if (!isLeaf()) {
			if (groupBounds == null)
				updateGroupBounds();
			return groupBounds;
		}
		return null;
	}

	/**
	 * Returns the bounding box for the specified views.
	 * 
	 * @param views
	 *            the views for whom the bounding box is to be determined
	 * @return the bounding box of the specified views
	 */
	public static Rectangle2D getBounds(CellView[] views) {
		if (views != null && views.length > 0) {
			Rectangle2D ret = null;
			for (int i = 0; i < views.length; i++) {
				if (views[i] != null) {
					Rectangle2D r = views[i].getBounds();
					if (r != null) {
						if (ret == null)
							ret = new Rectangle2D.Double(r.getX(), r.getY(),
									r.getWidth(), r.getHeight());
						else
							Rectangle2D.union(ret, r, ret);
					}
				}
			}
			return ret;
		}
		return null;
	}

	/**
	 * Sets the bounds of this <code>view</code>. Calls translateView and
	 * scaleView.
	 * 
	 * @param bounds
	 *            the new bounds for this cell view
	 */
	public void setBounds(Rectangle2D bounds) {
		Rectangle2D oldBounds = getBounds();
		if (oldBounds == null)
			oldBounds = new Rectangle2D.Double();
		Point2D p0 = new Point2D.Double(oldBounds.getX(), oldBounds.getY());
		Point2D pe = new Point2D.Double(bounds.getX(), bounds.getY());
		Rectangle2D localBounds = new Rectangle2D.Double(bounds.getX(),
				bounds.getY(), bounds.getWidth(), bounds.getHeight());
		if (GraphConstants.isMoveable(getAllAttributes()) && !pe.equals(p0))
			translate(pe.getX() - p0.getX(), pe.getY() - p0.getY());
		else
			localBounds.setFrame(localBounds.getX(), localBounds.getY(),
					bounds.getWidth() - pe.getX() + p0.getX(),
					bounds.getHeight() - pe.getY() + p0.getY());
		double lbw = localBounds.getWidth(), lbh = localBounds.getHeight();
		double obw = oldBounds.getWidth(), obh = oldBounds.getHeight();
		if ((lbw != obw || lbh != obh) && obw > 0 && obh > 0)
			scale(lbw / obw, lbh / obh, pe);
	}

	/**
	 * Updates the bounds of this view and its children
	 * 
	 */
	protected void updateGroupBounds() {
		// Note: Prevent infinite recursion by removing
		// child edges that point to their parent.
		CellView[] childViews = getChildViews();
		LinkedList result = new LinkedList();
		for (int i = 0; i < childViews.length; i++)
			if (includeInGroupBounds(childViews[i]))
				result.add(childViews[i]);
		childViews = new CellView[result.size()];
		result.toArray(childViews);
		Rectangle2D r = getBounds(childViews);
		int groupBorder = GraphConstants.getInset(getAllAttributes());
		if (r != null)
			r.setFrame(r.getX() - groupBorder, r.getY() - groupBorder,
					r.getWidth() + 2 * groupBorder, r.getHeight() + 2
							* groupBorder);
		groupBounds = r;
	}

	/**
	 * This is used to exclude certain cell views from the group bounds
	 * computation. This implementation returns false for edges that connect to
	 * one of their ancestor groups (eg. parent).
	 * 
	 * @param view
	 *            the cell view to be included in the group bounds or not
	 * @return whether or not to include the specified cell in the group bounds
	 */
	protected boolean includeInGroupBounds(CellView view) {
		if (view instanceof EdgeView
				&& getCell() instanceof DefaultMutableTreeNode) {
			EdgeView edgeView = (EdgeView) view;
			if (edgeView.getCell() instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode edge = (DefaultMutableTreeNode) edgeView
						.getCell();
				Object src = null;
				if (edgeView.getSource() != null
						&& edgeView.getSource().getParentView() != null)
					src = edgeView.getSource().getParentView().getCell();
				else if (edgeView.getSourceParentView() != null)
					src = edgeView.getSourceParentView().getCell();
				if (src instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode source = (DefaultMutableTreeNode) src;
					if (source.isNodeDescendant(edge))
						return false;
				}
				Object tgt = null;
				if (edgeView.getTarget() != null
						&& edgeView.getTarget().getParentView() != null)
					tgt = edgeView.getTarget().getParentView().getCell();
				else if (edgeView.getTargetParentView() != null)
					tgt = edgeView.getTargetParentView().getCell();
				if (tgt instanceof DefaultMutableTreeNode) {
					DefaultMutableTreeNode target = (DefaultMutableTreeNode) tgt;
					if (target.isNodeDescendant(edge)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Translates <code>view</code> (group) by <code>dx, dy</code>.
	 * 
	 * @param dx
	 *            the x-coordinate amount to translate by
	 * @param dy
	 *            the y-coordinate amount to translate by
	 */
	public void translate(double dx, double dy) {
		if (isLeaf())
			getAllAttributes().translate(dx, dy);
		else {
			int moveableAxis = GraphConstants
					.getMoveableAxis(getAllAttributes());
			if (moveableAxis == GraphConstants.X_AXIS)
				dy = 0;
			else if (moveableAxis == GraphConstants.Y_AXIS)
				dx = 0;
			Iterator it = childViews.iterator();
			while (it.hasNext()) {
				Object view = it.next();
				if (view instanceof AbstractCellView) {
					AbstractCellView child = (AbstractCellView) view;
					child.translate(dx, dy);
				}
			}
		}
	}

	/**
	 * Scale <code>view</code> (group) by <code>sx, sy</code>.
	 * 
	 * @param sx
	 *            the multiple by which the x coordinate position of the cell
	 *            view is to be scaled
	 * @param sy
	 *            the multiple by which the y coordinate position of the cell
	 *            view is to be scaled
	 * @param origin
	 *            the origin point from which the scaling will calculate
	 */
	public void scale(double sx, double sy, Point2D origin) {
		if (isLeaf())
			getAttributes().scale(sx, sy, origin);
		else {
			int sizeableAxis = GraphConstants
					.getSizeableAxis(getAllAttributes());
			if (sizeableAxis == GraphConstants.X_AXIS)
				sy = 1;
			else if (sizeableAxis == GraphConstants.Y_AXIS)
				sx = 1;
			Iterator it = childViews.iterator();
			while (it.hasNext()) {
				Object view = it.next();
				if (view instanceof AbstractCellView) {
					AbstractCellView child = (AbstractCellView) view;
					Map attrs = child.getAttributes();
					if (GraphConstants.isSizeable(attrs)
							|| GraphConstants.isAutoSize(attrs))
						child.scale(sx, sy, origin);
				}
			}
		}
	}

	/**
	 * Returns true if the view intersects the given rectangle.
	 * 
	 * @param graph
	 *            the <code>JGraph</code> instance of the view
	 * @param rect
	 *            the rectangle within which intersection is being checked for
	 * 
	 * @return whether or not the rectangle specified intersects the view
	 */
	public boolean intersects(JGraph graph, Rectangle2D rect) {
		if (isLeaf() || GraphConstants.isGroupOpaque(getAllAttributes())) {
			Rectangle2D bounds = getBounds();
			if (bounds != null)
				return bounds.intersects(rect);
		} else { // Check If Children Intersect
			Iterator it = childViews.iterator();
			while (it.hasNext())
				if (((CellView) it.next()).intersects(graph, rect))
					return true;
		}
		return false;
	}

	//
	// View Editors
	//

	/**
	 * Returns a renderer component, configured for the view. The method used to
	 * obtain the renderer instance must install the necessary attributes from
	 * this view
	 * 
	 * @param graph
	 *            the <code>JGraph</code> instance of the view
	 * @param selected
	 *            whether or not this view is selected
	 * @param focus
	 *            whether or not this view is the focus
	 * @param preview
	 *            whether or not it is a preview of the view
	 * 
	 * @return the renderer component for this view with this views attributes
	 *         installed
	 */
	public Component getRendererComponent(JGraph graph, boolean selected,
			boolean focus, boolean preview) {
		CellViewRenderer cvr = getRenderer();
		if (cvr != null)
			return cvr.getRendererComponent(graph, this, selected, focus,
					preview);
		return null;
	}

	/**
	 * Obtains the renderer instance for this view
	 * 
	 * @return the renderer instance for this view
	 */
	public abstract CellViewRenderer getRenderer();

	/**
	 * Returns a cell handle for the view.
	 * 
	 * @param context
	 *            the context of this cell view (cells indirectly affected by
	 *            it)
	 * @return the cell handle for this view
	 */
	public abstract CellHandle getHandle(GraphContext context);

	/**
	 * Returns a cell editor for the view.
	 * 
	 * @return the cell editor for this view
	 */
	public GraphCellEditor getEditor() {
		return cellEditor;
	}

	public static Point2D getCenterPoint(CellView vertex) {
		Rectangle2D r = vertex.getBounds();
		if (r != null)
			return new Point2D.Double(r.getCenterX(), r.getCenterY());
		return null;
	}

	/**
	 * Returns the intersection of the bounding rectangle and the straight line
	 * between the source and the specified point p. The specified point is
	 * expected not to intersect the bounds. Note: You must override this method
	 * if you use a different renderer. This is because this method relies on
	 * the VertexRenderer interface, which can not be safely assumed for
	 * subclassers.
	 */
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		return getCenterPoint(this);
	}

}
