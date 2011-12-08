 package org.oryxeditor.server.diagram.generic;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.label.LabelSettings;

/**
 * Interface for all shapes in a diagram (including the diagram itself)
 * <p/>
 * Represents an element of the canvas. Stencilset independent. <br/>
 * More advanced functionality may be found in subclasses.
 * 
 * @author Philipp Maschke
 *
 * @param <S> the actual type of shape to be used (must inherit from BasicShape); calls to {@link #getChildShapesReadOnly()}, ... will return this type
 * @param <D> the actual type of diagram to be used (must inherit from {@link GenericDiagram}); {@link #getDiagram()} will return this type
 */
public interface GenericShape<S extends GenericShape<S,D>, D extends GenericDiagram<S,D>> {

	/**
	 * Whether this shape is an edge
	 * @return
	 */
	public boolean isEdge();
	
	/**
	 * Whether this shape is a node. A diagram is NOT considered to be a node!
	 * @return
	 */
	public boolean isNode();
	
	
	/**
	 * Returns the StencilId of a shape
	 * 
	 * @return String stencilId or null if undefined
	 */
	public String getStencilId();

	
	/**
	 * Returns the full stencil id (including the stencilset namespace).
	 * 
	 * The stencilset namespace is retrieved from the shape's diagram. If it does not have a diagram, then the simple stencil id is returned.
	 * 
	 * @see #getStencilId()
	 * @return the full stencil id (including the stencilset namespace, if the shape belongs to a diagram)
	 */
	public String getQualifiedStencilId();
	
	
	/**
	 * Sets the stencil id
	 * 
	 * @param stencilId
	 */
	public void setStencilId(String stencilId);
	
	
	/**
	 * Returns the identifier of this shape.</br> 
	 * <b>Beware:</b> Ids are only required to be unique within one diagram! 
	 * Hence there is <u>no guarantee that two shapes from different diagrams will always have different ids</u>!
	 * 
	 * @return the resourceId
	 */
	public String getResourceId();

	
	/**
	 * Sets the shape id.
	 * Ids are required to be unique within one diagram!
	 * 
	 * @param resourceId
	 *            the resourceId to set
	 */
	public void setResourceId(String resourceId);


	/**
	 * Gives a Map with all explicitly set properties, name as key, value as value.
	 * Does not include default values.
	 * 
	 * Returns an unmodifiable view of the shape's properties
	 * @return all explicitly set properties
	 */
	public Map<String, String> getPropertiesReadOnly();

	
	/**
	 * Returns the set of all property names for which a value was set.
	 * @return all property names of this shape
	 */
	public Set<String> getPropertyNames();
	
	
	/**
	 * Resets all properties using the given mapping
	 * 
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Map<String, String> properties);

	
	/**
	 * Checks whether the shape has a property with the given name.
	 * 
	 * @param name
	 * @return true, if there exists a property with that name
	 */
	public boolean hasProperty(String name);
	
	
	/**
	 * Gives the value of the property with the given name.
	 * 
	 * @param name
	 *            name of the property
	 * @return value of the property or null if no value has been set
	 */
	public String getProperty(String name);
	
	
	public Object getPropertyObject(String name);
	
	public Integer getPropertyInteger(String name);
	
	public Long getPropertyLong(String name);
	
	public Float getPropertyFloat(String name);
	
	public Double getPropertyDouble(String name);
	
	public Boolean getPropertyBoolean(String name);
	
	public JSONObject getPropertyJsonObject(String name);
	
	public JSONArray getPropertyJsonArray(String name);
	
	
	/**
	 * Removes the property with the given name and returns the value (if that property existed; null otherwise)
	 * 
	 * @param name
	 * @return value of the removed property or null
	 */
	public String removeProperty(String name);
	
	
	/**
	 * Changes an existing property with the same name, or adds a new one
	 * 
	 * @param name
	 *            property name with which the specified value is to be
	 *            associated
	 * @param value
	 *            value to be associated with the specified property name
	 * @return the previous value associated with property name, or null if
	 *         there was no mapping for property name. (A null return can also
	 *         indicate that the map previously associated null with the name.)
	 */
	public String setProperty(String name, String value);

	public String setProperty(String name, Object value);
	
	public String setProperty(String name, int value);
	
	public String setProperty(String name, long value);
	
	public String setProperty(String name, double value);
	
	public String setProperty(String name, float value);
	
	public String setProperty(String name, boolean value);
	
	public String setProperty(String name, JSONObject value);
	
	public String setProperty(String name, JSONArray value);
	
	
	/**
	 * Unmodifiable view of the shape's child shapes (elements can not be added or removed, but elements themselves are modifiable). 
	 * 
	 * Use {@link #addChildShape(GenericShape)} or {@link #removeChildShape(GenericShape)} to modify child shapes.
	 * @return the childShapes
	 */
	public List<S> getChildShapesReadOnly();

	
	/**
	 * Recursively gathers all child shapes, and their child shapes, etc.
	 * 
	 * Returned set is unmodifiable (elements can not be added or removed, but elements themselves are modifiable).
	 * @return a (possibly empty) set, never null.
	 */
	public Set<S> getDescendantShapesReadOnly();

	
	/**
	 * Recursively gathers all ancestor shapes (parent, parent's parent...), and
	 * returns them.
	 * The first element is the shape's direct parent; the last element is the highest ancestor (usually the diagram).
	 * <p/>
	 * Returned list is unmodifiable (elements can not be added or removed, but elements themselves are modifiable).
	 * @return a (possibly empty) list of Shapes, never null.
	 */
	public List<S> getAncestorShapesReadOnly();

	
	/**
	 * @param childShapes
	 *            the childShapes to set
	 */
	public void setChildShapes(List<S> childShapes);

	
	/**
	 * Adds a child to the list of children; parent-child relation is updated,
	 * and the diagram's shape cache is adjusted
	 * 
	 * @param shape
	 *            the new shape
	 */
	public void addChildShape(S shape);


	/**
	 * Returns the number of direct child shapes
	 * @return number of child shapes
	 */
	public int getNumChildShapes();
	
	
	/**
	 * Sets the diagram that this shape belongs to.
	 * @param diagram2
	 */
	public void setDiagram(D diagram2);

	
	/**
	 * Removes the shape from the list of children, sets its parent
	 * appropriately, updates the diagram's shape cache.
	 * 
	 * @param shape
	 */
	public void removeChildShape(S shape);
	
	
	/**
	 * Recursively removes all child shapes from this shape and the diagram's shape cache.
	 * 
	 */
	public void removeAllChildShapes();


	/**
	 * @return the parent
	 */
	public S getParent();

	
	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(S parent);

	
	/**
	 * Set the shapes parent to a specific shape, and update the old parent's
	 * child shapes and the new parent's child shapes.
	 * 
	 * @param parent
	 */
	public void setParentAndUpdateItsChildShapes(S parent);

	
	/**
	 * Returns the uppermost parent, the diagram.
	 * 
	 * @return null if none is found
	 */
	public D getDiagram();

	
	/**
	 * Add p as new last docker.
	 * 
	 * @param p
	 */
	public void addDocker(Point p);

	
	/**
	 * Add p as docker at a given position.
	 * 
	 * @param p
	 * @param index
	 */
	public void addDocker(Point p, int index);

	
	/**
	 * Gives the point list of dockers for a shape, dockers usually appear on
	 * edges
	 * 
	 * Returned list is unmodifiable (elements can not be added or removed, but elements themselves are modifiable).
	 * @return the dockers, a list of points
	 */
	public List<Point> getDockersReadOnly();

	
	/**
	 * Returns the size of the dockers list.
	 * 
	 * @return
	 */
	public int getNumDockers();
	
	
	/**
	 * Returns the docker at the specified position or null if there is no docker at the given index
	 * 
	 * @param index
	 * @return
	 */
	public Point getDockerAt(int index);
	
	
	/**
	 * Removes the docker at the given index.
	 * 
	 * @param index
	 */
	public void removeDockerAt(int index);
	
	
	/**
	 * Set the list with all attached dockers for a shape, dockers usually
	 * appear on edges
	 * 
	 * @param dockers
	 *            the list of points to set
	 */
	public void setDockers(List<Point> dockers);

	
	/**
	 * Returns a copy of the bounds of this shape. Bounds define the space a shape spans
	 * over the canvas<br>
	 * The bounds' coordinates are relative to the parents position.
	 * <p> 
	 * Note that this only gives you a copy of the bounds. 
	 * If you need to edit them you must use {@link #setBounds(Bounds)}
	 * 
	 * @return the bounds object of the shape (as a copy!)
	 */
	public Bounds getBounds();

	
	/**
	 * Returns the absolute bounds of this shape.
	 * 
	 * @return
	 */
	public Bounds getAbsoluteBounds();

	
	/**
	 * Set a new bounds for a shape
	 * 
	 * @param bounds
	 *            the bounds to set
	 */
	public void setBounds(Bounds bounds);


	/**
	 * Return the list of incoming shapes (nodes and edges).
	 * <p/>
	 * Returned list is unmodifiable (elements can not be added or removed, but elements themselves are modifiable).
	 * @return
	 */
	public List<S> getIncomingsReadOnly();

	
	/**
	 * Checks whether this shape has the given shape as one of it's incoming shapes
	 * @param shape
	 * @return whether the given shape is an incoming shape
	 */
	public boolean hasIncoming(S shape);
	
	
	/**
	 * Returns the number of incoming shapes
	 * @return number of incoming shapes
	 */
	public int getNumIncomings();
	
	
	/**
	 * Sets new incomings. The outgoings of the old incoming shapes and the new
	 * incoming shapes are updated (with 'this').
	 * 
	 * @param incomings
	 *            shapes to be added
	 */
	public void setIncomingsAndUpdateTheirOutgoings(List<S> incomings);

	
	/**
	 * Add the shape to the incoming shapes. Updates the shape's outgoings
	 * appropriately.
	 * 
	 * @param shape
	 * @return true iff it was actually removed
	 */
	public boolean addIncomingAndUpdateItsOutgoings(S shape);

	
	/**
	 * Remove the shape from the incoming shapes. Updates the shape's outgoings
	 * appropriately.
	 * 
	 * @param shape
	 * @return true iff it was actually removed
	 */
	public boolean removeIncomingAndUpdateItsOutgoings(S shape);

	
	/**
	 * Return a list of outgoing shapes (nodes and edges).
	 * <p/>
	 * Returned list is unmodifiable (elements can not be added or removed, but elements themselves are modifiable).
	 * @return the outgoing shape objects of the shape
	 */
	public List<S> getOutgoingsReadOnly();

	
	/**
	 * Checks whether this shape has the given shape as one of it's outgoing shapes
	 * @param shape
	 * @return whether the given shape is an outgoing shape
	 */
	public boolean hasOutgoing(S shape);
	
	
	/**
	 * Returns the number of outgoing shapes
	 * @return number of outgoing shapes
	 */
	public int getNumOutgoings();
	
	
	/**
	 * Sets new outgoings. The incomings of the old outgoing shapes and the new
	 * outgoing shapes are updated (with 'this').
	 * 
	 * @param outgoings
	 *            shapes to be added
	 */
	public void setOutgoingsAndUpdateTheirIncomings(List<S> outgoings);


	
	/**
	 * Adds a new shape to the outgoings. The incomings of this shape are
	 * updated with 'this'.
	 * 
	 * @param shape
	 *            shape to be added
	 * @return true iff it has actually been added
	 */
	public boolean addOutgoingAndUpdateItsIncomings(S shape);


	
	/**
	 * Removes the shape from this shape's outgoings. Updates the incomings of the shape accordingly.
	 * @param shape
	 * @return
	 */
	public boolean removeOutgoingAndUpdateItsIncomings(S shape);

	
	/**
	 * Returns all succeeding oder preceding shapes
	 * <p/>
	 * Returned list is unmodifiable (elements can not be added or removed, but elements themselves are modifiable).
	 * @return
	 */
	public List<S> getConnectedShapesReadOnly();

	
	/**
	 * Returns a copy of the upper left point.
	 * 
	 * @return Point
	 */
	public Point getUpperLeft();

	
	/**
	 * Returns a copy of the lower right point.
	 * 
	 * @return Point
	 */
	public Point getLowerRight();

	
	/**
	 * Returns the shape's height (its maximum vertical extension).
	 */
	public double getHeight();

	
	/**
	 * Returns the shape's width (its maximum horizontal extension).
	 */
	public double getWidth();


	/**
	 * Overwritten hash code method, based on resourceId
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode();

	
	/**
	 * Overwritten equals method, based on resourceId
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj);

	
	/**
	 * Returns the settings for the label with the given reference name
	 * 
	 * @return settings for the given label or null if no setting found
	 */
	public LabelSettings getLabelSettingsForReference(String referencedLabel);

	
	/**
	 * Returns all positioning informations for all labels of this shape (not
	 * for labels of child shapes)
	 * 
	 * @return
	 */
	public Collection<LabelSettings> getLabelSettings();

	
	/**
	 * Sets positioning information for all labels of this shape
	 * 
	 * @param labelSettings
	 */
	public void setLabelSettings(Collection<LabelSettings> labelSettings);


	/**
	 * Adds a new settings object to the existing collection.
	 * @param newSetting
	 */
	public void addLabelSetting(LabelSettings newSetting);
	
	
	/**
	 * Returns true iff the given point is within or on the shape's
	 * <b><i><u>relative</u></i></b> bounds.
	 * 
	 * @param p
	 *            a point
	 */
	public boolean isPointIncluded(Point p);

	
	/**
	 * Returns true iff the given point is within or on the shape's absolute
	 * bounds.
	 * 
	 * @param p
	 *            a point
	 */
	public boolean isPointIncludedAbsolute(Point p);


	/**
	 * Returns true iff the given shape is a child shape of the callee.
	 * 
	 * @param s
	 *            a shape
	 */
	public boolean hasChild(S s);

	
	/**
	 * Returns true iff the given shape is a descendant shape of the callee.
	 * 
	 * @param s
	 *            a shape
	 */
	public boolean contains(S s);
}
