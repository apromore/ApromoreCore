/*
 * @(#)AttributeMap 1.0 03-JUL-04
 *
 * Copyright (c) 2001-2005 Gaudenz Alder
 *
 * See LICENSE file in distribution for licensing details of this source file
 */
package org.apromore.jgraph.graph;

import java.awt.geom.Point2D;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A map specifically for the storage of attributes of graph cells. The main
 * advantage of the AttributeMap is that it allows to override cell view
 * behaviour for scaling, translation, diffing, and cloning on a per instance
 * basis without having to change the GraphConstants class
 */
public class AttributeMap extends Hashtable implements Cloneable {

	/**
	 * Shared empty attribute map to return instead of null in applyMap.
	 */
	public static transient AttributeMap emptyAttributeMap = new AttributeMap(0) {
		public Object clone() {
			return this;
		}
	};

	/**
	 * Creates a new attribute map with an initial capacity of 8.
	 */
	public AttributeMap() {
		super(8);
	}

	/**
	 * Creates a new attribute map with the specified initial capacity
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the new map
	 */
	public AttributeMap(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Constructs a new, empty hashtable with the specified initial capacity and
	 * the specified load factor.
	 * 
	 * @param initialCapacity
	 *            the initial capacity of the hashtable.
	 * @param loadCapacity
	 *            the load factor of the hashtable.
	 */
	public AttributeMap(int initialCapacity, float loadCapacity) {
		super(initialCapacity, loadCapacity);
	}

	/**
	 * Constructs a new AttributeMap with the same mappings as the given Map.
	 * 
	 * @param map
	 *            the input map to copy
	 */
	public AttributeMap(Map map) {
		super(map);
	}

	/**
	 * Creates a point of suitable type for this attribute map
	 * 
	 * @return a new point
	 */
	public Point2D createPoint() {
		return new SerializablePoint2D();
	}

	/**
	 * Creates a point of suitable type for this attribute map with the same
	 * values as the point passed in
	 * 
	 * @param p
	 *            the point whose values the new point are to be based on
	 * @return a new copy of the point passed in
	 */
	public Point2D createPoint(Point2D p) {
		if (p != null) {
			return createPoint(p.getX(), p.getY());
		}
		return null;
	}

	/**
	 * Creates a point of suitable type for this attribute map with the same
	 * values as those passed in
	 * 
	 * @param x
	 *            the x-coordinate position of the new point
	 * @param y
	 *            the y-coordinate position of the new point
	 * @return a new point at the coordinates passed in
	 */
	public Point2D createPoint(double x, double y) {
		return new SerializablePoint2D(x, y);
	}

	/**
	 * Creates a rectangle of suitable type for this attribute map
	 * 
	 * @return a new rectangle
	 */
	public Rectangle2D createRect() {
		return new SerializableRectangle2D();
	}

	/**
	 * Creates a rectangle of suitable type for this attribute map with the same
	 * values as those passed in
	 * 
	 * @param x
	 *            the x-coordinate position of the new rectangle
	 * @param y
	 *            the y-coordinate position of the new rectangle
	 * @param w
	 *            the width of the new rectangle
	 * @param h
	 *            the height of the new rectangle
	 * @return a new rectangle at the coordinates and of the dimensions passed
	 *         in
	 */
	public Rectangle2D createRect(double x, double y, double w, double h) {
		return new SerializableRectangle2D(x, y, w, h);
	}

	/**
	 * Creates a rectangle of suitable type for this attribute map at the
	 * position of the point passed in
	 * 
	 * @param pt
	 *            the position of the new rectangle
	 * @return a new rectangle the specified coordinates of zero size
	 */
	public Rectangle2D createRect(Point2D pt) {
		return createRect(pt, 0);
	}

	/**
	 * Creates a rectangle of suitable type for this attribute map at the
	 * position of the point passed in with lengths <code>size</code>
	 * 
	 * @param pt
	 *            the position of the new rectangle
	 * @param size
	 *            the length of both sides of the rectangle
	 * @return a new rectangle the specified position and dimensions
	 */
	public Rectangle2D createRect(Point2D pt, double size) {
		if (pt != null) {
			return createRect(pt.getX(), pt.getY(), size, size);
		}
		return null;
	}

	/**
	 * Clones the rectangle passed in
	 * 
	 * @param rect
	 *            the rectangle to clone
	 * 
	 * @return a copy of the rectangle passed in
	 */
	public Rectangle2D createRect(Rectangle2D rect) {
		if (rect != null) {
			return createRect(rect.getX(), rect.getY(), rect.getWidth(), rect
					.getHeight());
		}
		return null;
	}

	/**
	 * Creates a rectangle of suitable type for this attribute map
	 * 
	 * @param x
	 *            the x-coordinate position of the new rectangle
	 * @param y
	 *            the y-coordinate position of the new rectangle
	 * @param w
	 *            the width of the new rectangle
	 * @param h
	 *            the height of the new rectangle
	 * @param grow1
	 *            the amount both dimensions are to be increased by and the
	 *            position coorindates of the rectangle are to be decreased by
	 * @param grow2
	 *            the additional amount by which both dimensions are to be
	 *            increased by
	 * @return a new rectangle at the coordinates and of the dimensions passed
	 *         in
	 */
	public Rectangle2D createRect(double x, double y, double w, double h,
			double grow1, double grow2) {
		return createRect(x - grow1, y - grow1, w + grow1 + grow2, h + grow1
				+ grow2);
	}

	/**
	 * Creates a clone of the rectangle passed in and manipulates it by
	 * <code>grow1</code> and <code>grow2</code>
	 * 
	 * @param grow1
	 *            the amount both dimensions are to be increased by and the
	 *            position coorindates of the rectangle are to be decreased by
	 * @param grow2
	 *            the additional amount by which both dimensions are to be
	 *            increased by
	 * @return a new rectangle at the coordinates and of the dimensions passed
	 *         in
	 */
	public Rectangle2D createRect(Rectangle2D rect, double grow1, double grow2) {
		if (rect != null) {
			return createRect(rect.getX(), rect.getY(), rect.getWidth(), rect
					.getHeight(), grow1, grow2);
		}
		return null;
	}

	/**
	 * Apply the <code>change</code> to this views attributes.
	 * <code>change</code> must be a <code>Map</code> previously obtained
	 * from this object.
	 * 
	 * @param change
	 *            the change to apply
	 * @return a map that may be used to undo the change to target.
	 */
	public AttributeMap applyMap(Map change) {
		AttributeMap undo = new AttributeMap();
		if (change != null) {
			// Handle Remove All
			if (GraphConstants.isRemoveAll(change)) {
				undo.putAll(this);
				clear();
			}
			// Handle Remove Individual
			Object[] remove = GraphConstants.getRemoveAttributes(change);
			if (remove != null) {
				// don't store command
				for (int i = 0; i < remove.length; i++) {
					Object oldValue = remove(remove[i]);
					if (oldValue != null)
						undo.put(remove[i], oldValue);
				}
			}
			// Attributes that were empty are added to removeattibutes.
			// Performance and transient memory peak are reduced by lazily
			// instantiating the set.
			Set removeAttributes = null;
			Iterator it = change.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				Object key = entry.getKey();
				if (!key.equals(GraphConstants.REMOVEALL)
						&& !key.equals(GraphConstants.REMOVEATTRIBUTES)
						&& !key.equals(GraphConstants.VALUE)) {
					Object oldValue = applyValue(key, entry.getValue());
					if (oldValue == null) {
						if (removeAttributes == null) {
							removeAttributes = new HashSet();
						}
						removeAttributes.add(key);
					}
					else {
						undo.put(key, oldValue);
					}
				}
			}
			if (removeAttributes != null && !removeAttributes.isEmpty()) {
				GraphConstants.setRemoveAttributes(undo, removeAttributes
						.toArray());
			}
		}
		return undo;
	}

	/**
	 * Apply the <code>key</code> to <code>value</code>
	 * 
	 * @param key
	 *            the map key whose value is to be altered
	 * @param value
	 *            the new value to be applied to the specified key
	 * @return the old value.
	 */
	public Object applyValue(Object key, Object value) {
		// In all other cases we put the new value into the
		// map. If we encounter a list (of points) or rectangle
		// these will be cloned before insertion. Cloning includes
		// replacing the rectangle/points with serializable objects.
		if (value instanceof Rectangle2D)
			value = createRect((Rectangle2D) value);
		if (value instanceof Point2D)
			value = createPoint((Point2D) value);
		if (value instanceof Point2D[])
			value = clonePoints((Point2D[]) value);
		if (value instanceof List) // FIXME: PointList interface?
			value = clonePoints((List) value);
		return put(key, value);
	}

	/**
	 * Returns a list where all instances of PortView are replaced by their
	 * correspnding Point instance.
	 * 
	 * @param points
	 *            the points to be cloned
	 * @return the cloned points
	 */
	public Point2D[] clonePoints(Point2D[] points) {
		List pts = clonePoints(points, true);
		Point2D[] newPoints = new Point2D[pts.size()];
		pts.toArray(newPoints);
		return newPoints;
	}

	/**
	 * Returns a list where all instances of PortView are replaced by their
	 * correspnding Point instance.
	 * 
	 * @param points
	 *            the points to be cloned
	 * @return the cloned points
	 */
	public List clonePoints(List points) {
		return clonePoints(points.toArray(), true);
	}

	/**
	 * Returns a list where all instances of PortView are replaced by their
	 * correspnding Point instance.
	 */
	public List clonePoints(Object[] points, boolean convertPortViews) {
		// TODO: Change the list in-place?
		ArrayList newList = new ArrayList(points.length);
		for (int i = 0; i < points.length; i++) {
			// Clone Point
			Object point = points[i];
			if (point instanceof PortView && convertPortViews)
				point = createPoint(((PortView) point).getLocation());
			else if (point instanceof Point2D)
				point = createPoint((Point2D) point);
			newList.add(point);
		}
		return newList;
	}

	/**
	 * Translates the maps in <code>c</code> using
	 * <code>translate(Map, int, int)</code>.
	 */
	public static void translate(Collection c, double dx, double dy) {
		Iterator it = c.iterator();
		while (it.hasNext()) {
			Object map = it.next();
			if (map instanceof AttributeMap)
				((AttributeMap) map).translate(dx, dy);
		}
	}

	/**
	 * Translates <code>map</code> by the given amount.
	 */
	public void translate(double dx, double dy) {
		// Translate Bounds
		if (GraphConstants.isMoveable(this)) {
			Rectangle2D bounds = GraphConstants.getBounds(this);
			if (bounds != null) {
				int moveableAxis = GraphConstants.getMoveableAxis(this);
				if (moveableAxis == GraphConstants.X_AXIS)
					dy = 0;
				else if (moveableAxis == GraphConstants.Y_AXIS)
					dx = 0;
				bounds.setFrame(bounds.getX() + dx, bounds.getY() + dy, bounds
						.getWidth(), bounds.getHeight());
			}
			// Translate Points
			List points = GraphConstants.getPoints(this);
			if (points != null) {
				for (int i = 0; i < points.size(); i++) {
					Object obj = points.get(i);
					if (obj instanceof Point2D) {
						Point2D pt = (Point2D) obj;
						pt.setLocation(pt.getX() + dx, pt.getY() + dy);
					}
				}
			}
		}
	}

	/**
	 * Scales <code>map</code> by the given amount.
	 */
	public void scale(double sx, double sy, Point2D origin) {
		// Scale Bounds
		Rectangle2D bounds = GraphConstants.getBounds(this);
		if (bounds != null) {
			Point2D p = createPoint(bounds.getX(), bounds.getY());
			Point2D loc = (Point2D) p.clone();
			p.setLocation(origin.getX()
					+ Math.round((p.getX() - origin.getX()) * sx), origin
					.getY()
					+ Math.round((p.getY() - origin.getY()) * sy));
			if (!p.equals(loc)) // Scale Location
				translate(p.getX() - loc.getX(), p.getY() - loc.getY());
			int sizeableAxis = GraphConstants.getSizeableAxis(this);
			if (sizeableAxis == GraphConstants.X_AXIS)
				sy = 1;
			else if (sizeableAxis == GraphConstants.Y_AXIS)
				sx = 1;
			double w = Math.max(1, Math.round(bounds.getWidth() * sx));
			double h = Math.max(1, Math.round(bounds.getHeight() * sy));
			// Scale Bounds
			bounds.setFrame(bounds.getX(), bounds.getY(), w, h);
		}
		// Scale Points
		List points = GraphConstants.getPoints(this);
		if (points != null) {
			Iterator it = points.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof Point2D) {
					// Scale Point
					Point2D loc = (Point2D) obj;
					Point2D p = (Point2D) loc.clone();
					p.setLocation(origin.getX()
							+ Math.round((p.getX() - origin.getX()) * sx),
							origin.getY()
									+ Math.round((p.getY() - origin.getY())
											* sy));
					// Move Point
					loc.setLocation(p);
				}
			}
		}
	}

	/**
	 * Returns a new map that contains all (key, value)-pairs of
	 * <code>newState</code> where either key is not used or value is
	 * different for key in <code>oldState</code>. In other words, this
	 * method removes the common entries from oldState and newState, and returns
	 * the "difference" between the two.
	 * 
	 * This method never returns null.
	 */
	public Map diff(Map newState) {
		Map diff = new Hashtable();
		Iterator it = newState.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object newValue = entry.getValue();
			Object oldValue = get(key);
			if (oldValue == null || !oldValue.equals(newValue))
				diff.put(key, newValue);
		}
		return diff;
	}

	/**
	 * Returns a clone of <code>map</code>, from keys to values. If the map
	 * contains bounds or points, these are cloned as well. References to
	 * <code>PortViews</code> are replaces by points. <br>
	 * <b>Note: </b> Extend this method to clone custom user objects.
	 */
	public Object clone() {
		// TODO, is cloning the hash table excessive?
		return cloneEntries((AttributeMap) super.clone());
	}

	/**
	 * Clones special object entried in the given map.
	 */
	public AttributeMap cloneEntries(AttributeMap newMap) {
		// Clone Bounds
		Rectangle2D bounds = GraphConstants.getBounds(newMap);
		if (bounds != null)
			GraphConstants.setBounds(newMap, (Rectangle2D) (bounds.clone()));
		// Clone List Of Points
		List points = GraphConstants.getPoints(newMap);
		if (points != null)
			GraphConstants.setPoints(newMap, clonePoints(points));
		// Clone extra label positions
		Point2D[] positions = GraphConstants.getExtraLabelPositions(newMap);
		if (positions != null)
			GraphConstants.setExtraLabelPositions(newMap,
					clonePoints(positions));
		// Clone Edge Label
		Point2D label = GraphConstants.getLabelPosition(newMap);
		if (label != null)
			GraphConstants.setLabelPosition(newMap, (Point2D) label.clone());
		return newMap;
	}

	public static class SerializablePoint2D extends Point2D.Double implements
			Serializable {

		public SerializablePoint2D() {
			super();
		}

		public SerializablePoint2D(double x, double y) {
			super(x, y);
		}

		public void setX(double x) {
			setLocation(x, getY());
		}

		public void setY(double y) {
			setLocation(getX(), y);
		}

		private void writeObject(ObjectOutputStream out) throws IOException {
			out.defaultWriteObject();
			out.writeObject(new java.lang.Double(getX()));
			out.writeObject(new java.lang.Double(getY()));
		}

		private void readObject(ObjectInputStream in) throws IOException,
				ClassNotFoundException {
			in.defaultReadObject();
			java.lang.Double x = (java.lang.Double) in.readObject();
			java.lang.Double y = (java.lang.Double) in.readObject();
			setLocation(x.doubleValue(), y.doubleValue());
		}

	}

	public static class SerializableRectangle2D extends Rectangle2D.Double
			implements Serializable {

		public SerializableRectangle2D() {
			super();
		}

		public SerializableRectangle2D(double x, double y, double width,
				double height) {
			super(x, y, width, height);
		}

		public void setX(double x) {
			setFrame(x, getY(), getWidth(), getHeight());
		}

		public void setY(double y) {
			setFrame(getX(), y, getWidth(), getHeight());
		}

		public void setWidth(double width) {
			setFrame(getX(), getY(), width, getHeight());
		}

		public void setHeight(double height) {
			setFrame(getX(), getY(), getWidth(), height);
		}

		private void writeObject(ObjectOutputStream out) throws IOException {
			out.defaultWriteObject();
			out.writeObject(new java.lang.Double(getX()));
			out.writeObject(new java.lang.Double(getY()));
			out.writeObject(new java.lang.Double(getWidth()));
			out.writeObject(new java.lang.Double(getHeight()));
		}

		private void readObject(ObjectInputStream in) throws IOException,
				ClassNotFoundException {
			in.defaultReadObject();
			java.lang.Double x = (java.lang.Double) in.readObject();
			java.lang.Double y = (java.lang.Double) in.readObject();
			java.lang.Double width = (java.lang.Double) in.readObject();
			java.lang.Double height = (java.lang.Double) in.readObject();
			setFrame(x.doubleValue(), y.doubleValue(), width.doubleValue(),
					height.doubleValue());
		}
	}
}