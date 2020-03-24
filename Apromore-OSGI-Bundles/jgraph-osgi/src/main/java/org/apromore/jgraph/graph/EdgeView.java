/*
 * @(#)EdgeView.java	1.0 03-JUL-04
 * 
 * Copyright (c) 2001-2004 Gaudenz Alder
 *  
 */
package org.apromore.jgraph.graph;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.apromore.jgraph.JGraph;
import org.apromore.jgraph.plaf.GraphUI;
import org.apromore.jgraph.plaf.basic.BasicGraphUI;

/**
 * The default implementation of an edge view. The getEdgeRenderer method
 * assumes a renderer of type EdgeRenderer. If you provide a custom renderer to
 * a subclass, you must also override the methods that call this method, namely:
 * getShape, getLabelBounds, getExtraLabelBounds, intersects and getBounds.
 * 
 * @version 1.0 1/1/02
 * @author Gaudenz Alder
 */

public class EdgeView extends AbstractCellView {

	/** Renderer for the class. */
	public static transient EdgeRenderer renderer = new EdgeRenderer();
	
	/** List of points of the edge. May contain ports. */
	protected List points;

	/** Cached source and target portview of the edge. */
	protected CellView source, target;

	protected CellView sourceParentView, targetParentView;

	/** Cached label position of the edge. */
	protected Point2D labelPosition;

	protected Point2D[] extraLabelPositions;

	protected transient Point2D labelVector = null;

	/** Drawing attributes that are created on the fly */
	public transient Shape beginShape, endShape, lineShape;

	/** Shared-path tune-up. */
	public transient GeneralPath sharedPath = null;

	protected transient Rectangle2D cachedBounds = null;

	/** Whether or not pre 5.12.3.3 disconnectable behaviour is to be used.
	 *  This allowed an edge to reconnect to another vertex ever when
	 *  isDisconnectable was false for the edge. Set to false
	 *  with isDisconnectable set to false for the edge forbids 
	 *  any disconnection. Default is true. */
	public static boolean LEGACY_DISCONNECTABLE = true;

	/**
	 * Constructs an empty edge view.
	 */
	public EdgeView() {
		super();
	}

	/**
	 * Constructs an edge view for the specified model object.
	 * 
	 * @param cell
	 *            reference to the model object
	 */
	public EdgeView(Object cell) {
		super(cell);
	}

	//
	// Data Source
	//

	/**
	 * Overrides the parent method to udpate the cached points, source and
	 * target port. If the source or target is removed, a point is inserted into
	 * the array of points.
	 */
	public void refresh(GraphLayoutCache cache, CellMapper mapper,
			boolean createDependentViews) {
		// Makes sure the manual control points are passed to
		// the router instead of the cached control points after
		// changes to the edge (normally manual point changes).
		points = null;
		super.refresh(cache, mapper, createDependentViews);
		// Re-sync source- and targetportviews
		GraphModel model = cache.getModel();
		Object modelSource = model.getSource(cell);
		Object modelTarget = model.getTarget(cell);
		setSource(mapper.getMapping(modelSource, createDependentViews));
		setTarget(mapper.getMapping(modelTarget, createDependentViews));
		if (modelSource != null && getSource() == null)
			sourceParentView = getVisibleParent(model, mapper, modelSource);
		else
			sourceParentView = null;
		if (modelTarget != null && getTarget() == null)
			targetParentView = getVisibleParent(model, mapper, modelTarget);
		else
			targetParentView = null;
	}

	protected CellView getVisibleParent(GraphModel model, CellMapper mapper,
			Object port) {
		CellView view = null;
		do {
			view = mapper.getMapping(port, false);
			port = model.getParent(port);
		} while (view == null && port != null);
		return view;
	}

	/**
	 * Update attributes and recurse children.
	 */
	public void update(GraphLayoutCache cache) {
		super.update(cache);
		// Save the reference to the points so they can be changed
		// in-place by use of setPoint, setSource, setTarget methods.
		List controlPoints = GraphConstants.getPoints(allAttributes);
		if (controlPoints == null) {
			controlPoints = new ArrayList(4);
			controlPoints.add(allAttributes.createPoint(10, 10));
			controlPoints.add(allAttributes.createPoint(20, 20));
			GraphConstants.setPoints(allAttributes, controlPoints);
		}

		// Uses the manual control points while the edge is being routed.
		// Otherwise uses the cached points (eg. for preview).
		if (points == null)
			points = controlPoints;

		Edge.Routing routing = GraphConstants.getRouting(allAttributes);
		List routedPoints = null;
		// Passes the current cached points to the router
		if (routing != null)
			routedPoints = routing.route(cache, this);

		// Shadows the manual control points with the
		// routed control points
		points = (routedPoints != null && !routedPoints.isEmpty()) ? routedPoints
				: controlPoints;

		// Overrides manual point locations with the real port views
		if (points == controlPoints) {
			if (source != null)
				setSource(source);
			if (target != null)
				setTarget(target);
		}

		// Checks and caches label positions
		checkDefaultLabelPosition();
		Point2D[] positions = GraphConstants
				.getExtraLabelPositions(allAttributes);
		if (positions != null) {
			extraLabelPositions = new Point2D[positions.length];
			for (int i = 0; i < positions.length; i++)
				extraLabelPositions[i] = positions[i];
		} else
			extraLabelPositions = null;

		// Clear cached shapes
		beginShape = null;
		endShape = null;
		lineShape = null;
		invalidate();
	}

	/**
	 * Hook for subclassers to avoid default label positions.
	 */
	protected void checkDefaultLabelPosition() {
		labelPosition = GraphConstants.getLabelPosition(allAttributes);
		String label = String.valueOf(getCell());
		if (labelPosition == null && label != null && label.length() > 0) {
			int center = GraphConstants.PERMILLE / 2;
			labelPosition = new Point(center, 0);
			GraphConstants.setLabelPosition(allAttributes, labelPosition);
		}
	}

	/**
	 * Resets the cached values of the edge view
	 */
	protected void invalidate() {
		labelVector = null;
		sharedPath = null;
		cachedBounds = null;
	}

	/**
	 * Returns the shape of the view according to the last rendering state
	 */
	public Shape getShape() {
		if (sharedPath != null)
			return sharedPath;
		else {
			return sharedPath = (GeneralPath) getEdgeRenderer().createShape();
		}
	}

	//
	// View Methods
	//

	/**
	 * Returns true if this view intersects the given rectangle.
	 */
	public boolean intersects(JGraph graph, Rectangle2D rect) {
		boolean intersects = super.intersects(graph, rect);
		if (!isLeaf()) {
			return intersects;
		} else if (intersects) {
			Rectangle r = new Rectangle((int) rect.getX(), (int) rect.getY(),
					(int) rect.getWidth(), (int) rect.getHeight());
			return getEdgeRenderer().intersects(graph, this, r);
		}
		return false;
	}

	/**
	 * Returns the location for this edgeview.
	 */
	public Rectangle2D getBounds() {
		Rectangle2D rect = super.getBounds();
		if (rect == null) {
			if (cachedBounds == null) {
				cachedBounds = getEdgeRenderer().getBounds(this);
			}
			rect = cachedBounds;
		}
		return rect;
	}

	/**
	 * Returns the local renderer. Do not access the renderer field directly.
	 * Use this method instead. Note: This method is package private.
	 */
	EdgeRenderer getEdgeRenderer() {
		return (EdgeRenderer) getRenderer();
	}

	/**
	 * Returns a renderer for the class.
	 */
	public CellViewRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Returns a cell handle for the view.
	 */
	public CellHandle getHandle(GraphContext context) {
		return new EdgeHandle(this, context);
	}

	//
	// Cached Values
	//

	/**
	 * Returns the CellView that represents the source of the edge.
	 */
	public CellView getSource() {
		return source;
	}

	public CellView getSourceParentView() {
		return sourceParentView;
	}

	/**
	 * Sets the <code>sourceView</code> of the edge.
	 */
	public void setSource(CellView sourceView) {
		sourceParentView = null;
		source = sourceView;
		if (source != null)
			points.set(0, source);
		else
			points.set(0, getPoint(0));
		invalidate();
	}

	/**
	 * Returns the CellView that represents the target of the edge.
	 */
	public CellView getTarget() {
		return target;
	}

	public CellView getTargetParentView() {
		return targetParentView;
	}

	/**
	 * Sets the <code>targetView</code> of the edge.
	 */
	public void setTarget(CellView targetView) {
		target = targetView;
		targetParentView = null;
		int n = points.size() - 1;
		if (target != null)
			points.set(n, target);
		else
			points.set(n, getPoint(n));
		invalidate();
	}

	/**
	 * Returns a point that describes the position of the label.
	 */
	public Point2D getExtraLabelPosition(int index) {
		return extraLabelPositions[index];
	}

	/**
	 * Returns a point that describes the position of the label.
	 */
	public Point2D getLabelPosition() {
		return labelPosition;
	}

	/**
	 * Sets the description of the label position.
	 */
	public void setLabelPosition(Point2D pos) {
		labelPosition.setLocation(pos);
		invalidate();
	}

	/**
	 * Sets the description of the label position.
	 */
	public void setExtraLabelPosition(int index, Point2D pos) {
		extraLabelPositions[index].setLocation(pos);
		invalidate();
	}

	//
	// Points
	//

	/**
	 * Returns true if the edge is a loop.
	 */
	public boolean isLoop() {
		return (getSource() != null && getSource() == getTarget())
				|| (sourceParentView != null && sourceParentView == targetParentView)
				|| (sourceParentView != null && getTarget() != null && getTarget()
						.getParentView() == sourceParentView)
				|| (targetParentView != null && getSource() != null && getSource()
						.getParentView() == targetParentView);
	}

	/**
	 * Returns the points.
	 * 
	 * @return List
	 */
	public List getPoints() {
		return points;
	}

	/**
	 * Returns the number of point for this edge.
	 */
	public int getPointCount() {
		if (points != null) {
			return points.size();
		} else {
			return 0;
		}
	}

	/**
	 * Returns the cached points for this edge.
	 */
	public Point2D getPoint(int index) {
		Object obj = points.get(index);
		if (index == 0 && sourceParentView != null) {
			return sourceParentView.getPerimeterPoint(this,
					getCenterPoint(sourceParentView),
					getNearestPoint(index == 0));
		} else if (index == getPointCount() - 1 && targetParentView != null) {
			return targetParentView.getPerimeterPoint(this,
					getCenterPoint(targetParentView),
					getNearestPoint(index == 0));
		} else if (obj instanceof PortView)
			// Port Location Seen From This Edge
			return ((PortView) obj).getLocation(this,
					getNearestPoint(index == 0));
		else if (obj instanceof CellView) {
			// Should not happen
			Rectangle2D r = ((CellView) obj).getBounds();
			return new Point2D.Double(r.getX(), r.getY());
		} else if (obj instanceof Point2D)
			// Regular Point
			return (Point2D) obj;
		return null;
	}

	/**
	 * Returns the nearest point wrt to the source or target. This method
	 * returns the next or previous point or port in the points list, eg. if
	 * source is true it returns the location of the point or port at index 1
	 * without calling the getLocation method on any ports.<br>
	 * Likewise, the method returns the location at index getPointCount()-2 if
	 * source is false.
	 */
	protected Point2D getNearestPoint(boolean source) {
		if (getPointCount() == 2) {
			if (source
					&& target instanceof PortView
					&& GraphConstants.getOffset(target.getAllAttributes()) != null) {
				return ((PortView) target).getLocation(this);
			}
			if (!source
					&& this.source instanceof PortView
					&& GraphConstants.getOffset(this.source.getAllAttributes()) != null) {
				return ((PortView) this.source).getLocation(this);
			}
			if (source && targetParentView != null && targetParentView.isLeaf())
				return getCenterPoint(targetParentView);
			else if (!source && sourceParentView != null
					&& sourceParentView.isLeaf())
				return getCenterPoint(sourceParentView);
		}
		return getPointLocation((source) ? 1 : getPointCount() - 2);
	}

	/**
	 * Returns the point of <code>edge</code> at <code>index</code>. Avoids
	 * calling <code>getLocation</code> on any ports of <code>edge</code>.
	 * <br>
	 * This is used from within getPoint to pass the nearest point to the
	 * portview to find it's location. This uses the center point of the parent
	 * view to determine the port view's location to avoid infinite recursion.
	 */
	protected Point2D getPointLocation(int index) {
		Object obj = points.get(index);
		if (obj instanceof Point2D)
			return (Point2D) obj;
		else if (obj instanceof PortView) {
			CellView vertex = ((CellView) obj).getParentView();
			if (vertex != null)
				return getCenterPoint(vertex);
		}
		return null;
	}

	/**
	 * Sets the point at <code>index</code> to <code>p</code>.
	 */
	public void setPoint(int index, Point2D p) {
		points.set(index, p);
		invalidate();
	}

	/**
	 * Adds <code>p</code> at position <code>index</code>.
	 */
	public void addPoint(int index, Point2D p) {
		points.add(index, p);
		invalidate();
	}

	/**
	 * Removes the point at position <code>index</code>.
	 */
	public void removePoint(int index) {
		points.remove(index);
		invalidate();
	}

	/**
	 * Adds an extra label.
	 */
	public void addExtraLabel(Point2D location, Object label) {
		Object[] extraLabels = GraphConstants
				.getExtraLabels(getAllAttributes());
		Point2D[] positions = GraphConstants
				.getExtraLabelPositions(getAllAttributes());

		// Inserts a new extra label
		if (extraLabels == null) {
			extraLabels = new Object[1];
			positions = new Point2D[1];
		} else {
			Object[] tmp = new Object[extraLabels.length + 1];
			System.arraycopy(extraLabels, 0, tmp, 0, extraLabels.length);
			extraLabels = tmp;
			Point2D[] pts = new Point2D[positions.length + 1];
			System.arraycopy(positions, 0, pts, 0, positions.length);
			positions = pts;
		}
		int newIndex = extraLabels.length - 1;
		extraLabels[newIndex] = label;
		positions[newIndex] = location;
		GraphConstants.setExtraLabels(getAllAttributes(), extraLabels);
		GraphConstants.setExtraLabelPositions(getAllAttributes(), positions);
	}

	/**
	 * Removes the point at position <code>index</code>.
	 */
	public void removeExtraLabel(int index) {
		Object[] labels = GraphConstants.getExtraLabels(getAllAttributes());
		Point2D[] pts = GraphConstants
				.getExtraLabelPositions(getAllAttributes());
		if (labels == null || labels.length > 1) {
			Object[] newLabels = new Object[labels.length - 1];
			Point2D[] newPts = new Point2D[pts.length - 1];
			System.arraycopy(labels, 0, newLabels, 0, index);
			if (index < newLabels.length)
				System.arraycopy(labels, index + 1, newLabels, index,
						newLabels.length - index);
			System.arraycopy(pts, 0, newPts, 0, index);
			if (index < newPts.length)
				System.arraycopy(pts, index + 1, newPts, index, newPts.length
						- index);
			GraphConstants.setExtraLabels(getAllAttributes(), newLabels);
			GraphConstants.setExtraLabelPositions(getAllAttributes(), newPts);
		} else {
			// TODO: Remove via REMOVEATTRIBUTES
			GraphConstants.setExtraLabels(getAllAttributes(), new Object[0]);
			GraphConstants.setExtraLabelPositions(getAllAttributes(),
					new Point2D[0]);
		}
	}

	/**
	 * Utility method that returns the first point of the pair that forms the
	 * segment that is relativeX along the edge as a proportion
	 * 
	 * @return the index of the first point. A value of -1 indicate to use the
	 *         first and last points
	 */
	public int getFirstPointOfSegment() {
		boolean exactSegment = GraphConstants
				.isExactSegmentLabel(allAttributes);
		double dx = 0;
		double dy = 0;
		int n = getPointCount();
		if (exactSegment) {
			// Determine the vector based on the actual edge segment the
			// label lies on
			Point2D lastPoint = getPoint(0);
			double totalLength = 0;

			for (int i = 1; i < n; i++) {
				Point2D currentPoint = getPoint(i);
				dx = currentPoint.getX() - lastPoint.getX();
				dy = currentPoint.getY() - lastPoint.getY();
				totalLength += Math.sqrt(dx * dx + dy * dy);
				lastPoint = currentPoint;
			}

			double relativeX = getLabelPosition().getX()/(double)GraphConstants.PERMILLE;
			double labelXPositionDistance = relativeX * totalLength;
			totalLength = 0;
			lastPoint = getPoint(0);
			if (relativeX <= 0.0 || relativeX >= 1.0) {
				return -1;
			} else {
				for (int i = 1; i < n; i++) {
					Point2D currentPoint = getPoint(i);
					dx = currentPoint.getX() - lastPoint.getX();
					dy = currentPoint.getY() - lastPoint.getY();
					totalLength += Math.sqrt(dx * dx + dy * dy);
					if (totalLength > labelXPositionDistance) {
						return i-1;
					}
				}
			}
		}
		else {
			return -1;
		}
		return -1;
	}
	
	/**
	 * Hook to return the vector that is taken as the base vector to compute
	 * relative label positions. Normally, the vector goes from the first to the
	 * last point on the edge, unless these points are equal, in which case the
	 * average distance of all points to the source point is used.
	 */
	public Point2D getLabelVector() {
		if (labelVector == null) {
			Point2D p0 = getPoint(0);
			double dx = 0;
			double dy = 0;
			// Finds an average distance
			int n = getPointCount();
			if (isLoop()) {
				for (int i = 1; i < n; i++) {
					Point2D point = getPoint(i);
					dx += point.getX() - p0.getX();
					dy += point.getY() - p0.getY();
				}
				n /= 2;
				dx /= n;
				dy /= n;
				labelVector = new Point2D.Double(dx, dy);
			} else {
				boolean exactSegment = GraphConstants
						.isExactSegmentLabel(allAttributes);
				if (exactSegment) {
					// Determine the vector based on the actual edge segment the
					// label lies on
					Point2D lastPoint = getPoint(0);
					double totalLength = 0;

					for (int i = 1; i < n; i++) {
						Point2D currentPoint = getPoint(i);
						dx = currentPoint.getX() - lastPoint.getX();
						dy = currentPoint.getY() - lastPoint.getY();
						totalLength += Math.sqrt(dx * dx + dy * dy);
						lastPoint = currentPoint;
					}

					double relativeX = getLabelPosition().getX()/(double)GraphConstants.PERMILLE;
					double labelXPositionDistance = relativeX * totalLength;
					totalLength = 0;
					lastPoint = getPoint(0);
					if (relativeX <= 0.0 || relativeX >= 1.0) {
						exactSegment = false;
					} else {
						for (int i = 1; i < n; i++) {
							Point2D currentPoint = getPoint(i);
							dx = currentPoint.getX() - lastPoint.getX();
							dy = currentPoint.getY() - lastPoint.getY();
							totalLength += Math.sqrt(dx * dx + dy * dy);
							if (totalLength > labelXPositionDistance) {
								labelVector = new Point2D.Double(dx, dy);
								break;
							}
							lastPoint = currentPoint;
						}
					}
				} 
				if (!exactSegment || labelVector == null) {
					Point2D point = getPoint(n - 1);
					dx = point.getX() - p0.getX();
					dy = point.getY() - p0.getY();
					labelVector = new Point2D.Double(dx, dy);
				}
			}
		}
		return labelVector;
	}

	/**
	 * Returns the absolute position of the main label
	 * @return the absolute position of the main label
	 */
	protected Point2D getAbsoluteLabelPosition() {
		Point2D result = getAbsoluteLabelPositionFromRelative(GraphConstants.getLabelPosition(getAllAttributes()));
		return result;
	}
	
	/**
	 * Returns the absolute position of the specified extra label
	 * @param index the index of the extra label
	 * @return the absolute position of the specified extra label
	 */
	protected Point2D getAbsoluteExtraLabelPosition(int index) {
		Point2D[] positions = GraphConstants
				.getExtraLabelPositions(getAllAttributes());
		if (positions != null && positions.length > index) {
			Point2D result = getAbsoluteLabelPositionFromRelative(positions[index]);
			return result;
		}
		return null;
	}
	
	/**
	 * Converts relative label position to absolute and allows for
	 * any label offset.
	 * @param geometry the relative label position
	 * @return the absolute label position including any offset
	 */
	protected Point2D getAbsoluteLabelPositionFromRelative(Point2D geometry) {
		Point2D result = convertRelativeLabelPositionToAbsolute(geometry);
		
		if (result != null)
		{
			double offsetX = 0;
			double offsetY = 0;

			Point2D offset = GraphConstants.getOffset(getAllAttributes());

			if (offset != null) {
				offsetX = offset.getX();
				offsetY = offset.getY();
			}

			double x = result.getX() + offsetX;
			double y = result.getY() + offsetY;
			return new Point2D.Double(x, y);
		}
		
		return null;
	}
	
	/**
	 * Converts an relative label position (x is distance along edge and y is
	 * distance above/below edge vector) into an absolute co-ordination point
	 * @param geometry the relative label position
	 * @return the absolute label position
	 */
	protected Point2D convertRelativeLabelPositionToAbsolute(Point2D geometry) {
		Point2D pt = getPoint(0);
		
		if (pt != null)
		{
			double length = 0;
			int pointCount = getPointCount();
			double[] segments = new double[pointCount];
			// Find the total length of the segments and also store the length
			// of each segment
			for (int i = 1; i < pointCount; i++)
			{
				Point2D tmp = getPoint(i);
				
				if (tmp != null)
				{
					double dx = pt.getX() - tmp.getX();
					double dy = pt.getY() - tmp.getY();

					double segment = Math.sqrt(dx * dx + dy * dy);
					
					segments[i - 1] = segment;
					length += segment;
					pt = tmp;
				}
			}

			// Change x to be a value between 0 and 1 indicating how far
			// along the edge the label is
			double x = geometry.getX()/GraphConstants.PERMILLE;
			double y = geometry.getY();
			
			// dist is the distance along the edge the label is
			double dist = x * length;
			length = 0;
			
			int index = 1;
			double segment = segments[0];

			// Find the length up to the start of the segment the label is
			// on (length) and retrieve the length of that segment (segment)
			while (dist > length + segment && index < pointCount - 1)
			{
				length += segment;
				segment = segments[index++];
			}

			// factor is the proportion along this segment the label lies at
			double factor = (dist - length) / segment;
			
			Point2D p0 = getPoint(index - 1);
			Point2D pe = getPoint(index);

			if (p0 != null && pe != null)
			{
				// The x and y offsets of the label from the start point
				// of the segment
				double dx = pe.getX() - p0.getX();
				double dy = pe.getY() - p0.getY();
				
				// The normal vectors of
				double nx = dy / segment;
				double ny = dx / segment;
				
				// The x position is the start x of the segment + the factor of
				// the x offset between the start and end of the segment + the
				// x component of the y (height) offset contributed along the
				// normal vector.
				x = p0.getX() + dx * factor - nx * y;

				// The x position is the start y of the segment + the factor of
				// the y offset between the start and end of the segment + the
				// y component of the y (height) offset contributed along the
				// normal vector.
				y = p0.getY() + dy * factor + ny * y;
				return new Point2D.Double(x, y);
			}
		}
		
		return null;
	}

	//
	// Routing
	//

	public static double getLength(CellView view) {
		double cost = 1;
		if (view instanceof EdgeView) {
			EdgeView edge = (EdgeView) view;
			Point2D last = null, current = null;
			for (int i = 0; i < edge.getPointCount(); i++) {
				current = edge.getPoint(i);
				if (last != null)
					cost += last.distance(current);
				last = current;
			}
		}
		return cost;
	}

	//
	// Handle
	//

	// This implementation uses the point instance to make the change. No index
	// is used for the current point because routing could change the index
	// during
	// the move operation.
	public static class EdgeHandle implements CellHandle, Serializable {

		protected JGraph graph;

		/* Pointer to the edge and its clone. */
		protected EdgeView edge, orig;

		/*
		 * Boolean indicating whether the source, target or label is being
		 * edited.
		 */
		protected boolean label = false, source = false, target = false;

		/**
		 * Holds the index of the current (editing) label or point.
		 */
		protected int currentLabel = -1, currentIndex = -1;

		/* Pointer to the currently selected point. */
		protected Point2D currentPoint;

		/* Array of control points represented as rectangles. */
		protected transient Rectangle2D[] r;

		/* A control point for the label position. */
		protected transient Rectangle2D loc;

		protected transient Rectangle2D[] extraLabelLocations;

		protected boolean firstOverlayCall = true;

		protected boolean isEdgeConnectable = true;

		protected EdgeView relevantEdge = null;

		/**
		 * True if the cell is being edited.
		 */
		protected boolean editing = false;

		/**
		 * Holds the initial location of the label.
		 */
		protected Point2D initialLabelLocation = null;

		/**
		 * Indicates whether the edge has been modified during the last mouse
		 * pressed and dragged operations.
		 */
		protected boolean edgeModified = false;

		/**
		 * Component that is used for highlighting cells if
		 * the graph does not allow XOR painting.
		 */
		protected JComponent highlight = new JPanel();

		public EdgeHandle(EdgeView edge, GraphContext ctx) {
			this.graph = ctx.getGraph();
			this.edge = edge;
			editing = graph.getEditingCell() == edge.getCell();
			loc = new Rectangle();
			Object[] labels = GraphConstants.getExtraLabels(edge
					.getAllAttributes());
			if (labels != null) {
				extraLabelLocations = new Rectangle[labels.length];
				for (int i = 0; i < extraLabelLocations.length; i++)
					extraLabelLocations[i] = new Rectangle();
			}
			orig = (EdgeView) graph.getGraphLayoutCache().getMapping(
					edge.getCell(), false);
			reloadPoints(orig);
			isEdgeConnectable = GraphConstants.isConnectable(edge
					.getAllAttributes());
			
			// Configures the panel for highlighting ports
			highlight = createHighlight();
		}

		/**
		 * Creates the component that is used for highlighting cells if
		 * the graph does not allow XOR painting.
		 */
		protected JComponent createHighlight()
		{
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
			panel.setVisible(false);
			panel.setOpaque(false);

			return panel;
		}

		protected void reloadPoints(EdgeView edge) {
			relevantEdge = edge;
			r = new Rectangle[edge.getPointCount()];
			for (int i = 0; i < r.length; i++)
				r[i] = new Rectangle();
			invalidate();
		}

		// Update and paint control points
		public void paint(Graphics g) {
			invalidate();
			if (!edge.isLeaf())
				return;
			for (int i = 0; i < r.length; i++) {
				if (isEdgeConnectable && !editing)
					g.setColor(graph.getHandleColor());
				else
					g.setColor(graph.getLockedHandleColor());
				g.fill3DRect((int) r[i].getX(), (int) r[i].getY(), (int) r[i]
						.getWidth(), (int) r[i].getHeight(), true);
				CellView port = null;
				if (i == 0 && edge.getSource() != null)
					port = edge.getSource();
				else if (i == r.length - 1 && edge.getTarget() != null)
					port = edge.getTarget();
				if (port != null
						|| (i == 0 && edge.getSourceParentView() != null)
						|| (i == r.length - 1 && edge.getTargetParentView() != null)) {
					g.setColor(graph.getLockedHandleColor());
					Point2D tmp = (port != null) ? GraphConstants
							.getOffset(port.getAllAttributes()) : null;
					if (tmp != null) {
						g.drawLine((int) r[i].getX() + 1,
								(int) r[i].getY() + 1,
								(int) (r[i].getX() + r[i].getWidth()) - 3,
								(int) (r[i].getY() + r[i].getHeight()) - 3);
						g.drawLine((int) r[i].getX() + 1,
								(int) (r[i].getY() + r[i].getHeight()) - 3,
								(int) (r[i].getX() + r[i].getWidth()) - 3,
								(int) r[i].getY() + 1);
					} else
						g.drawRect((int) r[i].getX() + 2,
								(int) r[i].getY() + 2,
								(int) r[i].getWidth() - 5, (int) r[i]
										.getHeight() - 5);
				}
			}
			if (!graph.isXorEnabled()) {
				firstOverlayCall = false;
				overlay(g);
			}
		}

		/**
		 * Highlights the given cell view or removes the highlight if
		 * no cell view is specified.
		 * 
		 * @param graph
		 * @param cellView
		 */
		protected void highlight(JGraph graph, CellView cellView)
		{
			if (cellView != null)
			{
				highlight.setBounds(getHighlightBounds(graph, cellView));

				if (highlight.getParent() == null)
				{
					graph.add(highlight);
					highlight.setVisible(true);
				}
			}
			else
			{
				if (highlight.getParent() != null)
				{
					highlight.setVisible(false);
					highlight.getParent().remove(highlight);
				}
			}
		}

		/**
		 * Returns the bounds to be used to highlight the given cell view.
		 * 
		 * @param graph
		 * @param cellView
		 * @return
		 */
		protected Rectangle getHighlightBounds(JGraph graph, CellView cellView)
		{
			boolean offset = (GraphConstants.getOffset(cellView.getAllAttributes()) != null);
			Rectangle2D r = (offset) ? cellView.getBounds() : cellView
					.getParentView().getBounds();
			r = graph.toScreen((Rectangle2D) r.clone());
			int s = 3;

			return new Rectangle((int) (r.getX() - s), (int) (r.getY() - s),
					(int) (r.getWidth() + 2 * s), (int) (r.getHeight() + 2 * s));
		}

		public void overlay(Graphics g) {
			if (edge != null && !firstOverlayCall && edge.isLeaf()) {
				// g.setColor(graph.getBackground()); // JDK 1.3
				g.setColor(graph.getForeground());
				if (graph.isXorEnabled()) {
					g.setXORMode(graph.getBackground().darker());
				}
				Graphics2D g2 = (Graphics2D) g;
				AffineTransform oldTransform = g2.getTransform();
				g2.scale(graph.getScale(), graph.getScale());
				graph.getUI().paintCell(g, edge, edge.getBounds(), true);
				g2.setTransform(oldTransform);
				
				if (graph.isXorEnabled())
				{
					if (isSourceEditing() && edge.getSource() != null)
						paintPort(g, edge.getSource());
					else if (isTargetEditing() && edge.getTarget() != null)
						paintPort(g, edge.getTarget());
				}
			}
			
			if (!graph.isXorEnabled())
			{
				if (isSourceEditing())
					highlight(graph, edge.getSource());
				else if (isTargetEditing())
					highlight(graph, edge.getTarget());
			}
			
			firstOverlayCall = false;
		}

		protected void paintPort(Graphics g, CellView p) {
			boolean offset = (GraphConstants.getOffset(p.getAllAttributes()) != null);
			Rectangle2D r = (offset) ? p.getBounds() : p.getParentView()
					.getBounds();
			r = graph.toScreen((Rectangle2D) r.clone());
			int s = 3;
			r.setFrame(r.getX() - s, r.getY() - s, r.getWidth() + 2 * s, r
					.getHeight()
					+ 2 * s);
			graph.getUI().paintCell(g, p, r, true);
		}

		protected boolean snap(boolean source, Point2D point) {
			boolean connect = graph.isConnectable() && isEdgeConnectable;
			Object port = graph.getPortForLocation(point.getX(), point.getY());
			if (port != null
					&& graph.getModel().getParent(port) == edge.getCell())
				port = null;
			if (port != null && connect) {
				CellView portView = graph.getGraphLayoutCache().getMapping(
						port, false);
				Rectangle2D dirty = edge.getBounds();
				dirty.add(portView.getParentView().getBounds());
				if (GraphConstants.isConnectable(portView.getParentView()
						.getAllAttributes())) {
					Object cell = edge.getCell();
					if (source && graph.getModel().acceptsSource(cell, port)) {
						if (edge.getSource() != portView) {
							edgeModified = true;
							if (graph.isXorEnabled()) {
								overlay(graph.getGraphics());
							}
							edge.setSource(portView);
							edge.update(graph.getGraphLayoutCache());
							if (graph.isXorEnabled()) {
								overlay(graph.getGraphics());
							} else {
								dirty.add(edge.getBounds());
								graph.repaint((int) dirty.getX(), (int) dirty
										.getY(), (int) dirty.getWidth(),
										(int) dirty.getHeight());
							}
						}
						return true;
					} else if (!source
							&& graph.getModel().acceptsTarget(cell, port)) {
						if (edge.getTarget() != portView) {
							edgeModified = true;
							if (graph.isXorEnabled()) {
								overlay(graph.getGraphics());
							}
							edge.setTarget(portView);
							edge.update(graph.getGraphLayoutCache());
							if (graph.isXorEnabled()) {
								overlay(graph.getGraphics());
							} else {
								dirty.add(edge.getBounds());
								graph.repaint((int) dirty.getX(), (int) dirty
										.getY(), (int) dirty.getWidth(),
										(int) dirty.getHeight());
							}
						}
						return true;
					}
				}
			}
			return false;
		}

		public boolean isConstrainedMoveEvent(MouseEvent e) {
			GraphUI ui = graph.getUI();
			if (ui instanceof BasicGraphUI)
				return ((BasicGraphUI) ui).isConstrainedMoveEvent(e);
			return false;
		}

		/**
		 * Returning true signifies a mouse event adds a new point to an edge.
		 */
		public boolean isAddPointEvent(MouseEvent event) {
			return event.isPopupTrigger()
					|| SwingUtilities.isRightMouseButton(event);
		}

		/**
		 * Returning true signifies a mouse event removes a given point.
		 */
		public boolean isRemovePointEvent(MouseEvent event) {
			return event.isPopupTrigger()
					|| SwingUtilities.isRightMouseButton(event);
		}

		protected boolean isSourceEditing() {
			return source;
		}

		protected boolean isTargetEditing() {
			return target;
		}

		/*
		 * Returns true if either the source, target, label or a point is being
		 * edited.
		 */
		protected boolean isEditing() {
			return source || target || label || currentLabel >= 0
					|| currentPoint != null;
		}

		/**
		 * Invoked when the mouse pointer has been moved on a component (with no
		 * buttons down).
		 */
		public void mouseMoved(MouseEvent event) {
			for (int i = 0; i < r.length; i++)
				if (r[i].contains(event.getPoint())) {
					graph.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
					event.consume();
					return;
				}
			if (loc.contains(event.getPoint()) && graph.isMoveable()
					&& GraphConstants.isMoveable(edge.getAllAttributes())) {
				graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
				event.consume();
			}
			if (extraLabelLocations != null && graph.isMoveable()
					&& GraphConstants.isMoveable(edge.getAllAttributes())) {
				for (int i = 0; i < extraLabelLocations.length; i++) {
					if (extraLabelLocations[i].contains(event.getPoint())) {
						graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
						event.consume();
					}
				}
			}
		}

		// Handle mouse pressed event.
		public void mousePressed(MouseEvent event) {
			/* INV: currentPoint = null; source = target = label = false; */
			if (!edge.isLeaf())
				return;
			boolean bendable = graph.isBendable()
					&& GraphConstants.isBendable(edge.getAllAttributes());
			int x = event.getX();
			int y = event.getY();
			// Detect hit on control point
			int index = 0;
			for (index = 0; index < r.length; index++)
			{
				if (r[index].contains(x, y))
				{
					if (EdgeView.LEGACY_DISCONNECTABLE)
					{
						currentPoint = edge.getPoint(index);
						currentIndex = index;
						source = index == 0;
						target = index == r.length - 1;
						break;
					}
					else
					{
						if ((index > 0 && index < r.length - 1)
								|| GraphConstants.isDisconnectable(edge
										.getAllAttributes()))
						{
							currentPoint = edge.getPoint(index);
							currentIndex = index;
							source = index == 0;
							target = index == r.length - 1;
							break;
						}
						else
						{
							event.consume();
						}
					}
				}
			}
			
			// Detect hit on label
			if (!isEditing() && graph.isMoveable()
					&& GraphConstants.isMoveable(edge.getAllAttributes())
					&& loc != null && loc.contains(x, y)
					&& !isAddPointEvent(event) && !isRemovePointEvent(event)
					&& graph.getEdgeLabelsMovable()) {
				initialLabelLocation = (Point2D) edge.getLabelPosition()
						.clone();
				label = true;
			}
			// Detect hit on extra labels
			else if (extraLabelLocations != null && !isEditing()
					&& graph.isMoveable() && graph.getEdgeLabelsMovable()
					&& GraphConstants.isMoveable(edge.getAllAttributes())) {
				for (int i = 0; i < extraLabelLocations.length; i++) {
					if (extraLabelLocations[i] != null
							&& extraLabelLocations[i].contains(x, y)) {
						currentLabel = i;
						initialLabelLocation = (Point2D) edge
								.getExtraLabelPosition(currentLabel).clone();
						if (isRemovePointEvent(event)) {
							edge.removeExtraLabel(i);
							edgeModified = true;
							mouseReleased(event);
						}
						break;
					}
				}
			}
			// Remove Point
			if (isRemovePointEvent(event)
					&& currentPoint != null
					&& !source
					&& !target
					&& bendable
					&& (edge.getSource() == null || currentIndex > 0)
					&& (edge.getTarget() == null || currentIndex < edge
							.getPointCount() - 1)) {
				edge.removePoint(index);
				edgeModified = true;
				mouseReleased(event);
				// Add Point
			} else if (isAddPointEvent(event) && !isEditing() && bendable) {
				int s = graph.getHandleSize();
				Rectangle2D rect = graph.fromScreen(new Rectangle(x - s, y - s,
						2 * s, 2 * s));
				if (edge.intersects(graph, rect)) {
					Point2D point = graph.fromScreen(graph.snap(new Point(event
							.getPoint())));
					double min = Double.MAX_VALUE, dist = 0;
					for (int i = 0; i < edge.getPointCount() - 1; i++) {
						Point2D p = edge.getPoint(i);
						Point2D p1 = edge.getPoint(i + 1);
						dist = new Line2D.Double(p, p1).ptSegDistSq(point);
						if (dist < min) {
							min = dist;
							index = i + 1;
						}
					}
					edge.addPoint(index, point);
					edgeModified = true;
					currentPoint = point;
					reloadPoints(edge);
					paint(graph.getGraphics());
				}
			}
			if (isEditing())
				event.consume();
		}

		public void mouseDragged(MouseEvent event) {
			Rectangle2D dirty = edge.getBounds();
			Point2D p = graph.fromScreen(new Point(event.getPoint()));
			// Move Label
			if (label || currentLabel >= 0) {
				Rectangle2D r = edge.getBounds();
				if (r != null) {
					edgeModified = true;
					if (graph.isXorEnabled()) {
						overlay(graph.getGraphics());
					}
					if (!GraphConstants.isLabelAlongEdge(edge.getAllAttributes())) {
						p = getRelativeLabelPosition(edge, p);
					} else {
						double x = p.getX();
						double y = p.getY();

						Point2D p0 = edge.getPoint(0);

						double p0x = p0.getX();
						double p0y = p0.getY();

						Point2D vector = edge.getLabelVector();
						double dx = vector.getX();
						double dy = vector.getY();

						double pex = p0.getX() + dx;
						double pey = p0.getY() + dy;

						double len = Math.sqrt(dx * dx + dy * dy);
						if (len > 0) {
							double u = GraphConstants.PERMILLE;
							double posy = len
									* (-y * dx + p0y * dx + x * dy - p0x * dy)
									/ (-pey * dy + p0y * dy - dx * pex + dx * p0x);
							double posx = u
									* (-y * pey + y * p0y + p0y * pey - p0y * p0y
											- pex * x + pex * p0x + p0x * x - p0x
											* p0x)
									/ (-pey * dy + p0y * dy - dx * pex + dx * p0x);
							p = new Point2D.Double(posx, posy);
						} else {
							p = new Point2D.Double(x - p0.getX(), y - p0.getY());
						}
					}
					if (label) {
						edge.setLabelPosition(p);
					} else {
						edge.setExtraLabelPosition(currentLabel, p);
					}
					edge.update(graph.getGraphLayoutCache());
					if (graph.isXorEnabled()) {
						overlay(graph.getGraphics());
					} else {
						graph.repaint((int) dirty.getX() - 1,
								(int) dirty.getY() - 1, (int) dirty.getWidth() + 2,
								(int) dirty.getHeight() + 2);
					}
				}
			} else if (isEditing() && currentPoint != null) {
				boolean disconnectable = (!source && !target)
						|| (graph.isDisconnectable() && GraphConstants
								.isDisconnectable(orig.getAllAttributes()));
				if (source)
					disconnectable = disconnectable
							&& ((orig.getSource() == null && orig
									.getSourceParentView() == null)
									|| (orig.getSource() != null && GraphConstants
											.isDisconnectable(orig.getSource()
													.getParentView()
													.getAllAttributes())) || (orig
									.getSourceParentView() != null && GraphConstants
									.isDisconnectable(orig
											.getSourceParentView()
											.getAllAttributes())));
				if (target)
					disconnectable = disconnectable
							&& ((orig.getTarget() == null && orig
									.getTargetParentView() == null)
									|| (orig.getTarget() != null && GraphConstants
											.isDisconnectable(orig.getTarget()
													.getParentView()
													.getAllAttributes())) || (orig
									.getTargetParentView() != null && GraphConstants
									.isDisconnectable(orig
											.getTargetParentView()
											.getAllAttributes())));
				// Find Source/Target Port
				if (!((source && snap(true, event.getPoint())) || (target && snap(
						false, event.getPoint())))
						&& disconnectable) {
					// Else Use Point
					boolean acceptSource = source
							&& (graph.getModel().acceptsSource(edge.getCell(),
									null) || graph.isPreviewInvalidNullPorts());
					boolean acceptTarget = target
							&& (graph.getModel().acceptsTarget(edge.getCell(),
									null) || graph.isPreviewInvalidNullPorts());
					if (acceptSource || acceptTarget || !(source || target)) {
						edgeModified = true;
						if (edge.getSource() != null) {
							dirty.add(edge.getSource().getParentView()
									.getBounds());
						}
						if (edge.getTarget() != null) {
							dirty.add(edge.getTarget().getParentView()
									.getBounds());
						}
						if (graph.isXorEnabled()) {
							overlay(graph.getGraphics());
						}
						p = graph.fromScreen(graph.snap(new Point(event
								.getPoint())));
						// Constrained movement
						if (isConstrainedMoveEvent(event) && currentIndex >= 0) {
							// Reset Initial Positions
							EdgeView orig = (EdgeView) graph
									.getGraphLayoutCache().getMapping(
											edge.getCell(), false);
							Point2D origPoint = orig.getPoint(currentIndex);
							double totDx = p.getX() - origPoint.getX();
							double totDy = p.getY() - origPoint.getY();
							if (Math.abs(totDx) < Math.abs(totDy))
								p.setLocation(origPoint.getX(), p.getY());
							else
								p.setLocation(p.getX(), origPoint.getY());
						}
						// Do not move into negative space
						p.setLocation(Math.max(0, p.getX()), Math.max(0, p
								.getY()));
						currentPoint.setLocation(p);
						if (source) {
							edge.setPoint(0, p);
							edge.setSource(null);
						} else if (target) {
							edge.setPoint(edge.getPointCount() - 1, p);
							edge.setTarget(null);
						}
						edge.update(graph.getGraphLayoutCache());
						dirty.add(edge.getBounds());
						if (graph.isXorEnabled()) {
							overlay(graph.getGraphics());
						} else {
							if (edge.getSource() != null) {
								dirty.add(edge.getSource().getParentView()
										.getBounds());
							}
							if (edge.getTarget() != null) {
								dirty.add(edge.getTarget().getParentView()
										.getBounds());
							}
							dirty = graph.toScreen((Rectangle2D) dirty.clone());
							graph.repaint((int) dirty.getX(), (int) dirty
									.getY(), (int) dirty.getWidth(),
									(int) dirty.getHeight());
						}
					}
				}
				else if (!graph.isXorEnabled())
				{
					dirty.add(edge.getBounds());
					dirty = graph.toScreen((Rectangle2D) dirty.clone());
					graph.repaint((int) dirty.getX(), (int) dirty
							.getY(), (int) dirty.getWidth(),
							(int) dirty.getHeight());
				}
			}
		}
		
		protected Point2D getRelativeLabelPosition(EdgeView edge, Point2D p) {
			int pointCount = edge.getPointCount();
			
			double totalLength = 0;
			double[] segments = new double[pointCount];
			
			Point2D p0 = edge.getPoint(0);
			Point2D pt = p0;
			
			// Calculate the total length of the edge
			for (int i = 1; i < pointCount; i++)
			{
				Point2D tmp = edge.getPoint(i);
				
				if (tmp != null)
				{
					double dx = pt.getX() - tmp.getX();
					double dy = pt.getY() - tmp.getY();

					double segment = Math.sqrt(dx * dx + dy * dy);
					
					segments[i - 1] = segment;
					totalLength += segment;
					pt = tmp;
				}
			}
			
			// Work which line segment the point of the label is closest to
			Point2D last = edge.getPoint(1);
			Line2D line = new Line2D.Double(p0, last);
			double minDist = line.ptSegDistSq(p);
			
			int index = 0;
			double tmp = 0;
			double length = 0;
			
			for (int i = 2; i < pointCount; i++)
			{
				tmp += segments[i-2];
				
				line = new Line2D.Double(edge.getPoint(i), last);
				double dist = line.ptSegDistSq(p);
				
				if (dist < minDist) {
					minDist = dist;
					index = i-1;
					length = tmp;
				}
				
				last = edge.getPoint(i);
			}
			
			double seg = segments[index];
			
			pt = edge.getPoint(index);
			
			double x2 = pt.getX();
			double y2 = pt.getY();
			
			Point2D pt2 = edge.getPoint(index+1);
			
			double x1 = pt2.getX();
			double y1 = pt2.getY();
			
			double px = p.getX();
			double py = p.getY();
			
			double xSegment = x2 - x1;
			double ySegment = y2 - y1;
	
			px -= x1;
			py -= y1;
	
			double projlenSq = 0;
	
		    px = xSegment - px;
		    py = ySegment - py;
		    double dotprod = px * xSegment + py * ySegment;

		    if (dotprod <= 0.0)
		    {
				projlenSq = 0;
		    }
		    else
		    {
				projlenSq = dotprod * dotprod / (xSegment * xSegment + ySegment * ySegment);
		    }

		    double projlen = Math.sqrt(projlenSq);
			if (projlen > seg)
			{
				projlen = seg;						
			}

			double yDistance = Line2D.ptLineDist(pt2.getX(), pt2.getY(), pt.getX(), pt.getY(), p.getX(), p.getY());
			int direction = Line2D.relativeCCW(pt2.getX(), pt2.getY(), pt.getX(), pt.getY(), p.getX(), p.getY());
			
			if (direction == -1) {
				yDistance = -yDistance;
			}
			
			// Constructs the relative point for the label
			Point2D result = new Point2D.Double(((((totalLength/2 - length - projlen)/
					totalLength)*-2)+1)*GraphConstants.PERMILLE / 2, yDistance);
			
			// Use the utility method to find 
			Point2D storedRelativePosition = edge.convertRelativeLabelPositionToAbsolute(result);
			if (p.equals(storedRelativePosition)) {
				GraphConstants.setRemoveAttributes(edge.getAllAttributes(), new Object[] {GraphConstants.OFFSET});
				edge.getAllAttributes().remove(GraphConstants.OFFSET);
			} else {
				Point2D off = new Point2D.Double(p.getX() - storedRelativePosition.getX(), p.getY() - storedRelativePosition.getY());
				GraphConstants.setOffset(edge.getAllAttributes(), off);
			}
			return result;
		}

		// Handle mouse released event
		public void mouseReleased(MouseEvent e) {
			highlight(graph, null); // removes the highlight
			boolean clone = e.isControlDown() && graph.isCloneable();
			GraphModel model = graph.getModel();
			Object source = (edge.getSource() != null) ? edge.getSource()
					.getCell() : null;
			Object target = (edge.getTarget() != null) ? edge.getTarget()
					.getCell() : null;
			if (edgeModified && model.acceptsSource(edge.getCell(), source)
					&& model.acceptsTarget(edge.getCell(), target)) {

				// Creates an extra label if the label was cloned
				if (clone && initialLabelLocation != null) {

					// Resets the dragging label position and adds a new label
					// instead. Note: label locations are modified in-place
					// which is why we need to clone at beginning.
					Object value = null;
					Point2D location = null;
					Object[] extraLabels = GraphConstants.getExtraLabels(edge
							.getAllAttributes());
					if (label) {
						location = (Point2D) edge.getLabelPosition().clone();
						value = graph.convertValueToString(orig);
						edge.setLabelPosition(initialLabelLocation);
					} else {
						location = (Point2D) edge.getExtraLabelPosition(
								currentLabel).clone();
						value = extraLabels[currentLabel];
						edge.setExtraLabelPosition(currentLabel,
								initialLabelLocation);
					}
					edge.addExtraLabel(location, value);
					edge.update(graph.getGraphLayoutCache());
					clone = false;
				}

				// Creates the data required for the edit/insert call
				ConnectionSet cs = createConnectionSet(edge, clone);
				Map nested = GraphConstants.createAttributes(
						new CellView[] { edge }, null);

				// The cached points may be different from what's
				// in the attribute map if the edge is routed.
				Map tmp = (Map) nested.get(edge.getCell());
				List controlPoints = GraphConstants.getPoints(tmp);
				List currentPoints = edge.getPoints();

				// Checks if we're dealing with a routing algorithm
				// and if we are, replaces only the source and target
				// in the control point list.
				if (controlPoints != currentPoints) {
					controlPoints.set(0, edge.getPoint(0));
					controlPoints.set(controlPoints.size() - 1, edge
							.getPoint(edge.getPointCount() - 1));
				}

				if (clone) {
					Map cellMap = graph.cloneCells(graph
							.getDescendants(new Object[] { edge.getCell() }));
					processNestedMap(nested, true);
					nested = GraphConstants.replaceKeys(cellMap, nested);
					cs = cs.clone(cellMap);
					Object[] cells = cellMap.values().toArray();
					graph.getGraphLayoutCache().insert(cells, nested, cs, null,
							null);
				} else {
					processNestedMap(nested, false);
					graph.getGraphLayoutCache().edit(nested, cs, null, null);
				}
			} else {
				if (graph.isXorEnabled()) {
					overlay(graph.getGraphics());
				} else {
					Rectangle2D dirty = edge.getBounds();
					graph.repaint((int) dirty.getX(), (int) dirty.getY(),
							(int) dirty.getWidth(), (int) dirty.getHeight());
				}
				edge.refresh(graph.getGraphLayoutCache(), graph.getGraphLayoutCache(),
						false);
			}
			initialLabelLocation = null;
			currentPoint = null;
			this.edgeModified = false;
			this.label = false;
			this.source = false;
			this.target = false;
			currentLabel = -1;
			currentIndex = -1;
			firstOverlayCall = true;
			e.consume();
		}

		protected void processNestedMap(Map nested, boolean clone) {
			// subclassers can override this to modify the attributes
		}

		protected ConnectionSet createConnectionSet(EdgeView view,
				boolean verbose) {
			Object edge = view.getCell();
			GraphModel model = graph.getModel();
			ConnectionSet cs = new ConnectionSet();
			Object sourcePort = null, targetPort = null;
			if (view.getSource() != null)
				sourcePort = view.getSource().getCell();
			else if (view.getSourceParentView() != null)
				sourcePort = model.getSource(edge);
			if (view.getTarget() != null)
				targetPort = view.getTarget().getCell();
			else if (view.getTargetParentView() != null)
				targetPort = model.getTarget(edge);
			if (view.getTarget() != null)
				targetPort = view.getTarget().getCell();
			if (verbose || (sourcePort != model.getSource(edge) && source))
				cs.connect(edge, sourcePort, true);
			if (verbose || (targetPort != model.getTarget(edge) && target))
				cs.connect(edge, targetPort, false);
			return cs;
		}

		// Update control points
		protected void invalidate() {
			EdgeView e = relevantEdge;
			int handlesize = graph.getHandleSize();
			EdgeRenderer er = (EdgeRenderer) edge.getRenderer();
			Point2D labelPosition = er.getLabelPosition(e);
			Point2D p = null;
			if (labelPosition != null) {
				p = (Point2D)labelPosition.clone();
				graph.toScreen(p);
			}
			Dimension d = er.getLabelSize(e, graph.convertValueToString(e));
			if (p != null && d != null) {
				Point2D s = graph.toScreen(new Point2D.Double(d.width,
						d.height));
				loc.setFrame(p.getX() - s.getX() / 2, p.getY() - s.getY()
						/ 2, s.getX(), s.getY());
			}
			for (int i = 0; i < r.length; i++) {
				p = e.getPoint(i);
				p = graph.toScreen(new Point2D.Double(p.getX(), p.getY()));
				r[i].setFrame(p.getX() - handlesize, p.getY() - handlesize,
						2 * handlesize, 2 * handlesize);

			}
			if (extraLabelLocations != null) {
				for (int i = 0; i < extraLabelLocations.length; i++) {
					p = er.getExtraLabelPosition(e, i);
					if (p != null) {
						p = graph.toScreen((Point2D) p.clone());
						d = er.getExtraLabelSize(graph, e, i);
						if (d != null) {
							Point2D s = graph.toScreen(new Point2D.Double(
									d.width, d.height));
							extraLabelLocations[i].setFrame(p.getX() - s.getX()
									/ 2, p.getY() - s.getY() / 2, s.getX(), s
									.getY());
						}
					}
				}
			}
		}

	}

	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		if (getPointCount() > 2)
			return getPoint(getPointCount() / 2);
		Point2D p0 = getPoint(0);
		Point2D pe = getPoint(getPointCount() - 1);
		return new Point2D.Double((pe.getX() + p0.getX()) / 2, (pe.getY() + p0
				.getY()) / 2);
	}
}