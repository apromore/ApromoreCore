package org.oryxeditor.server.diagram.generic;

import java.util.List;

import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.exception.TooManyDockersException;

/**
 * Represents a node element in a diagram. Nodes may only have at most one docker. 
 * 
 * @author Philipp Maschke, Robert Gurol
 *
 * @param <S> the actual type of shape to be used (must inherit from {@link GenericShape}); calls to {@link #getChildShapesReadOnly()}, ... will return this type
 * @param <D> the actual type of diagram to be used (must inherit from {@link GenericDiagram}); {@link #getDiagram()} will return this type
 */
public abstract class GenericNode<S extends GenericShape<S,D>, D extends GenericDiagram<S,D>> extends GenericShapeImpl<S,D> {

	public GenericNode(String resourceId) {
		super(resourceId);
	}

	public GenericNode(String resourceId, String stencilId) {
		super(resourceId, stencilId);
	}

	
	/**
	 * Sets the given docker as the nodes docker.
	 * 
	 * @param p
	 * @throws TooManyDockersException if the node already has a docker (nodes may only have one docker)
	 */
	@Override
	public void addDocker(Point p) throws TooManyDockersException {
		if (!getDockersReadOnly().isEmpty())
			throw new TooManyDockersException("Trying to set more than 1 docker for node '" + getResourceId() + "'");
		super.addDocker(p);
	}

	/**
	 * Sets the given docker as the nodes docker.
	 * 
	 * @param p
	 * @param position
	 * @throws TooManyDockersException if the node already has a docker (nodes may only have one docker)
	 */
	@Override
	public void addDocker(Point p, int position) {
		if (!getDockersReadOnly().isEmpty())
			throw new TooManyDockersException("Trying to set more than 1 docker for node '" + getResourceId() + "'");
		
		super.addDocker(p, position);
	}

	/**
	 * Sets the given list of dockers as the nodes dockers.
	 * <p>
	 * <b>Beware:</b> Nodes may only have one docker -> the given list may only have 0 or 1 points or an exception will be thrown
	 * 
	 * @param dockers list containing 0 or 1 docker
	 * @throws TooManyDockersException if p is not null and the node already has a docker (nodes may only have one docker)
	 */
	@Override
	public void setDockers(List<Point> dockers) {
		if (dockers != null && dockers.size() > 1)
			throw new TooManyDockersException("Trying to set more than 1 docker for node '" + getResourceId() + "'");
		
		super.setDockers(dockers);
	}

	// TODO do the other operations actually make sense / have to be redefined
	// here?

}
