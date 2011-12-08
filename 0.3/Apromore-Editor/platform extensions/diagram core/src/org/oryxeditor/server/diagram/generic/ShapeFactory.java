package org.oryxeditor.server.diagram.generic;

import java.util.List;

import org.oryxeditor.server.diagram.Point;


public interface ShapeFactory<S extends GenericShape<S,D>, D extends GenericDiagram<S,D>, E extends GenericEdge<S,D>, N extends GenericNode<S,D>>{

	/**
	 * Creates a new diagram
	 * @param resourceId
	 * @return
	 */
	public D createNewDiagram(String resourceId);
	
	/**
	 * Creates a new edge
	 * @param resourceId
	 * @return
	 */
	public E createNewEdge(String resourceId);
	
	/**
	 * Creates a new node
	 * @param resourceId
	 * @return
	 */
	public N createNewNode(String resourceId);
	
	/**
	 * Creates either a new edge or a new node, depending on the given list of dockers. Sets the given dockers as the shape's dockers.
	 * <br/>
	 * If {@link GenericShapeImpl#isEdge(List)} returns true, then a new edge will be created. Otherwise a new node will be returned.
	 * 
	 * @param resourceId
	 * @param dockers the list of dockers for the new shape
	 * @return either a new node or a new edge
	 */
	public S createNewShapeOfCorrectType(String resourceId, List<Point> dockers);
}
