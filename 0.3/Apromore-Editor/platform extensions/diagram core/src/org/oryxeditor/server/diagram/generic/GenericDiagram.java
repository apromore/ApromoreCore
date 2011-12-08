package org.oryxeditor.server.diagram.generic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSetReference;


/**
 * Represents a diagram having a certain stencilset, stencilset extensions and
 * shapes
 * 
 * @author Philipp Maschke, Robert Gurol
 *
 * @param <S> the actual type of shape to be used (must inherit from {@link GenericShape}); calls to {@link #getChildShapesReadOnly()}, ... will return this type
 * @param <D> the actual type of diagram to be used (must inherit from {@link GenericDiagram}); {@link #getDiagram()} will return this type
 */
public abstract class GenericDiagram
	<S extends GenericShape<S,D>, D extends GenericDiagram<S,D>> 
	extends GenericShapeImpl<S,D>{
//TODO certain Shape operations don't make sense, such as setDiagram...
	private static final Logger LOGGER = Logger.getLogger(GenericDiagram.class);
	
	private StencilSetReference stencilsetRef;
	private List<S> shapes;
	private List<String> ssextensions = new ArrayList<String>();
	

	/**
	 * Normal shape constructor with additional stencilsetRef
	 * 
	 * @param resourceId
	 *            resourceId of the diagram shape
	 * @param stencilId
	 *            stencil usually Diagram
	 * @param stencilsetRef
	 *            StencilSet with uri and namespace
	 */
	public GenericDiagram(String resourceId, String stencilId,
			StencilSetReference stencilsetRef) {
		this(resourceId, stencilId);
		this.stencilsetRef = stencilsetRef;
	}

	
	/**
	 * @param resourceId
	 */
	public GenericDiagram(String resourceId) {
		this(resourceId, null);
	}
	
	
	/**
	 * 
	 * @param resourceId
	 * @param stencilId
	 */
	public GenericDiagram(String resourceId, String stencilId) {
		super(resourceId, stencilId);
		this.shapes = new ArrayList<S>();
		this.stencilsetRef = null;
	}


	/**
	 * Gives the stencilset reference of a diagram
	 * 
	 * @return the stencilsetRef
	 */
	public StencilSetReference getStencilsetRef() {
		return stencilsetRef;
	}

	/**
	 * Set a new StencilSetReference
	 * 
	 * @param stencilsetRef
	 *            the stencilsetRef to set
	 */
	public void setStencilsetRef(StencilSetReference stencilsetRef) {
		this.stencilsetRef = stencilsetRef;
	}

	/**
	 * Gives a list of all namespaces of stencilset extensions used for this
	 * diagram
	 * 
	 * @return the ssextensions
	 */
	public List<String> getSsextensions() {
		return Collections.unmodifiableList(ssextensions);
	}

	/**
	 * Set the list of all namespaces of stencilset extensions used for this
	 * diagram
	 * 
	 * @param ssextensions
	 *            the ssextensions to set
	 */
	public void setSsextensions(List<String> ssextensions) {
		this.ssextensions.clear();
		if (ssextensions != null)
			this.ssextensions.addAll(ssextensions);
	}

	/**
	 * Adds an additional extension namespace
	 * 
	 * @param ssExt
	 *            the ssextension namespace to set
	 */
	public boolean addSsextension(String ssExt) {
		return this.ssextensions.add(ssExt);
	}

	/**
	 * Returns all shapes of this diagram (not just direct child shapes, excluding the diagram itself) as an unmodifiable(!!!) list
	 * 
	 * @return all shapes of this diagram as an unmodifiable list
	 * @see #addToAllShapes(GenericShape)
	 * @see #removeFromAllShapes(GenericShape)
	 * @see #containsShape(GenericShape)
	 */
	public List<S> getAllShapesReadOnly() {
		return Collections.unmodifiableList(shapes);
	}
	
	
	/**
	 * Returns all shapes of this diagram (not just direct child shapes, including the diagram itself) as an unmodifiable(!!!) list
	 * 
	 * @return all shapes of this diagram and the diagram itself as an unmodifiable list
	 * @see #addToAllShapes(GenericShape)
	 * @see #removeFromAllShapes(GenericShape)
	 * @see #containsShape(GenericShape)
	 */
	public List<S> getAllShapesIncludingDiagramReadOnly(){
		List<S> allShapes = new ArrayList<S>(shapes);
		allShapes.add((S) this);
		return Collections.unmodifiableList(allShapes);
	}
	
	
	/**
	 * Updates the diagram's cache of all shapes by adding a new shape
	 * 
	 * @see #getAllShapesReadOnly()
	 * @param newShape shape that was added to the diagram or a descendant shape
	 */
	protected void addToAllShapes(S newShape){
		shapes.add(newShape);
	}
	
	/**
	 * Updates the diagram's cache of all shapes by removing an existing shape
	 * 
	 * @see #getAllShapesReadOnly()
	 * @param removedShape shape that was removed from the diagram or a descendant shape
	 * @return true if cached list contained the shape 
	 */
	protected boolean removeFromAllShapes(S removedShape){
		return shapes.remove(removedShape);
	}
	
	
	/**
	 * Checks whether the given shape is contained in this diagram, by testing if it is contained in the <b>cached</b> list of all child shapes.
	 * 
	 * @see #getAllShapesReadOnly()
	 * @param shape
	 * @return whether the shape is contained in this diagram
	 */
	public boolean containsShape(S shape){
		return shapes.contains(shape);
	}
	
	
	/**
	 * Brings the cached list of all shapes in this diagram into a new order.
	 * <p/>
	 * Basis for the new ordering is the given list of shape ids. This list is traversed in order and 
	 * shapes are reordered accordingly.
	 * <br/>
	 * Every single existing shape must be referenced in that list; invalid/non-existing ids are ignored.
	 * <br/>
	 * The new ordering is only applied once all ids have been checked. This implies that,
	 *  if an exception is thrown then the original ordering is left intact.
	 * 
	 * @param idsList list of shape ids (must at least contain all ids of currently contained shapes, additional ids are ignored) 
	 * @throws IllegalArgumentException if the reordered list of shapes would not contain all current shapes anymore
	 */
	public void reorderListOfAllShapes(List<String> idsList){
		if (idsList == null)
			throw new IllegalArgumentException("list of ids is null");
		else if (idsList.size() < shapes.size())
			throw new IllegalArgumentException(
					"Ordered list of ids does not have enough members! Required: " + 
					shapes.size() + ", Given: " + idsList.size());
		
		List<S> newList = new ArrayList<S>(shapes.size());
		//go through given id list in order
		for (String id: idsList){
			if (id == null || "".equals(id.trim()))
				continue;
			//find the corresponding shape
			for (S shape: shapes){
				//and add it to the new list (if not already in it)
				if (id.equals(shape.getResourceId()) && !newList.contains(shape)){
					newList.add(shape);
					break;
				}
			}
		}
		
		//check if new list is valid
		if (newList.size() != shapes.size())
			throw new IllegalArgumentException("Number of elements in new list differs from number of actual shapes! New: " + 
					newList.size() + ", Old: " + shapes.size());
		
		//apply new list
		shapes = newList;
	}
	
	
	/**
	 * Returns the shape with the given resource id (may return the diagram itself if the id fits).
	 * <p>
	 * Returns null if the shape is not found.
	 * <p>
	 * Returns null if the input is null or "".
	 * 
	 * @param id
	 * @return
	 */
	public S getShapeById(String id) {
		if (id == null || "".equals(id))
			return null;
		if (id.equals(getResourceId()))
			return (S) this;
		
		for (S shape : this.shapes) {
			if (id.equals(shape.getResourceId()))
				return shape;
		}
		return null;
	}

	/**
	 * Returns the shapes with the given resource ids
	 * 
	 * @param ids
	 * @return a list of shapes that have one of the given ids
	 */
	public List<S> getShapesByIds(List<String> ids) {
		List<S> shapes = new ArrayList<S>();
		if (ids == null)
			return shapes;
		
		for (String id : ids) {
			S shape = this.getShapeById(id);
			if (shape != null)
				shapes.add(shape);
		}
		return shapes;
	}

	/**
	 * Returns all shapes that include the given point (in absolute coordinates).
	 * 
	 * @param point
	 * @return
	 */
	public List<S> getShapesAtPosition(Point point) {
		List<S> shapes = new ArrayList<S>();
		if (point == null)
			return shapes;
		
		for (S shape : this.getAllShapesReadOnly()) {
			if (shape.getAbsoluteBounds().isPointIncluded(point))
				shapes.add(shape);
		}
		return shapes;
	}

	/**
	 * Always returns null!
	 */
	@Override
	public S getParent() {
		return null;
	}
	
	/**
	 * Can't set a parent for a diagram; method call is ignored! 
	 */
	@Override
	public void setParent(S parent) {
		// do nothing
		LOGGER.warn("Tried to set parent for diagram '" + getResourceId() + "', was ignored!");
	}

	/**
	 * Return the string representation of the diagram. Uses the JSONBuilder.
	 * 
	 * @return the JSON string representing this diagram or null (if an exception occurred)
	 * @throws JSONException 
	 */
	public String getString() throws JSONException {
		JSONObject json = getJSON();
		if (json != null)
			return json.toString();
		else
			return null;
	}

	/**
	 * Return the JSON representation of the diagram. Uses the {@link GenericJSONBuilder}.
	 * <p/>
	 * Overwrite this method in subclasses if there is an extended version of the json builder.
	 * 
	 * @return the JSON object representing this diagram or null (if an exception occurred)
	 * @throws JSONException 
	 */
	public JSONObject getJSON() throws JSONException {
		return GenericJSONBuilder.parseModel(this);
	}
	

	@Override
	public D getDiagram() {
		return (D) this;
	}
	
	@Override
	public void setDiagram(D diagram2) {
		//do nothing 
	}
}
