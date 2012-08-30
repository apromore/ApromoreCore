package org.oryxeditor.server.diagram.generic;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;

/**
 * Represents an edge element in a diagram.
 * 
 * @author Philipp Maschke, Robert Gurol
 *
 * @param <S> the actual type of shape to be used (must inherit from {@link GenericShape}); calls to {@link #getChildShapesReadOnly()}, ... will return this type
 * @param <D> the actual type of diagram to be used (must inherit from {@link GenericDiagram}); {@link #getDiagram()} will return this type
 */
public abstract class GenericEdge<S extends GenericShape<S,D>, D extends GenericDiagram<S,D>> extends GenericShapeImpl<S, D> {

	protected S target;
	protected S source;

	public GenericEdge(String resourceId) {
		super(resourceId);
	}

	public GenericEdge(String resourceId, String stencilId) {
		super(resourceId, stencilId);
	}


	/* getters & setters */
	/**
	 * Returns the source of this shape; usually a node.<br>
	 * 
	 * 
	 * @return the source shape of this shape
	 */
	public S getSource() {
		return this.source;
	}

	/**
	 * Set the source of a shape</br>
	 * The source is also set as an incoming shape of this Edge.</br>
	 * If there is a current source, it is replaced and also removed from the
	 * incoming shapes of this edge.
	 * 
	 * @param shape
	 * 
	 * @deprecated Use {@link #connectToASource(GenericShape)} instead
	 */
	public void setSourceAndUpdateIncomings(S shape) {
		if (this.getSource() != null) {
			this.removeIncoming(this.getSource());
		}
		this.source = shape;
		if (shape != null)
			this.addIncoming(shape);
	}


	/**
	 * Gives the target of shape, which defined another associated shape
	 * 
	 * @return the target shape
	 */
	public S getTarget() {
		return target;
	}

	/**
	 * Set a (new) target shape for a shape. <br/>
	 * The target is also set as an outgoing shape of this Edge. <br/>
	 * If there is a current target, it is replaced and also removed from the
	 * outgoing shapes of this edge.
	 * 
	 * @param target
	 *            the target shape to set
	 *            
	 * @deprecated Use {@link #connectToATarget(GenericShape)} instead
	 */
	public void setTargetAndUpdateOutgoings(S target) {		
		if (this.getTarget() != null) {
			this.removeOutgoing(this.getTarget());
		}
		this.target = target;
		if (target != null)
			this.addOutgoing(target);
	}

	
	/**
	 * Connects to a source, updates the source's outgoing shapes as well.
	 * Disconnects from the previous source first, as
	 * {@link #disconnectFromSource()} would.
	 * 
	 * @param newSource
	 *            the shape to connect to
	 */
	public void connectToASource(S newSource) {
		if (this.source != null)
			this.removeIncomingAndUpdateItsOutgoings(this.source);
		
		this.source = newSource;
		
		if(newSource != null)
			this.addIncomingAndUpdateItsOutgoings(newSource);
	}

	
	/**
	 * Disconnects the Edge from its source, updates the source's outgoing
	 * shapes as well. If there is no source (source equals null), nothing is
	 * done.
	 * 
	 * @return true if it has actually disconnected
	 */
	public boolean disconnectFromSource() {
		if (this.source == null)
			return false;

		this.removeIncomingAndUpdateItsOutgoings(this.source);
		this.source = null;

		return true;
	}

	
	/**
	 * Connects to a target, updates the target's incoming shapes as well.
	 * Disconnects from the previous target first, as
	 * {@link #disconnectFromTarget()} would.
	 * 
	 * @param s
	 *            the shape to connect to
	 */
	public void connectToATarget(S newTarget) {		
		if (this.target != null)
			this.removeOutgoingAndUpdateItsIncomings(this.target);
		
		this.target = newTarget;
		
		if(newTarget != null)
			this.addOutgoingAndUpdateItsIncomings(newTarget);
	}

	
	/**
	 * Disconnects the Edge from its target, updates the target's incoming
	 * shapes as well.
	 * 
	 * @return true if it has actually disconnected
	 */
	public boolean disconnectFromTarget() {
		if (this.target == null)
			return false;

		this.removeOutgoingAndUpdateItsIncomings(this.target);
		this.target = null;

		return true;
	}
	
	
	/**
	 * Add p as new last docker; updates the edge's bounds.
	 * @param p
	 */
	@Override
	public void addDocker(Point p){
		super.addDocker(p);
		this.updateBounds();
	}
	
	/**
	 * Add p as docker at a given position; updates the edge's bounds.
	 * 
	 * @param p
	 * @param position
	 */
	@Override
	public void addDocker(Point p, int position){
		super.addDocker(p, position);
		this.updateBounds();
	}
	
	@Override
	public void setDockers(List<Point> dockers) {
		super.setDockers(dockers);
		this.updateBounds();
	}
	
	
	protected void updateBounds() {
		double maxX = Integer.MIN_VALUE;
		double maxY = Integer.MIN_VALUE;
		double minX = Integer.MAX_VALUE;
		double minY = Integer.MAX_VALUE;

		// position of first and last docker may be relative! have to get the
		// absolute value and then calculate!
		List<Point> l = new ArrayList<Point>(this.getDockersReadOnly());
		if (l != null) {
			if (this.getSource() != null) {
				// relative coordinates!
				if (this.getDockersReadOnly().size() != 0) {
					Point p = this.getSource().getAbsoluteBounds()
							.getUpperLeft();
					p.add(this.getDockersReadOnly().get(0));
					l.set(0, p);
				}
			}
			if (this.getTarget() != null) {
				// in case of 1 docker (which should not happen with an edge,
				// anyway), the one docker is the source docker
				if (this.getDockersReadOnly().size() > 1) {
					Point p = this.getTarget().getAbsoluteBounds()
							.getUpperLeft();
					p.add(this.getDockersReadOnly().get(this.getDockersReadOnly().size() - 1));
					l.set(this.getDockersReadOnly().size() - 1, p);
				}
			}

			for (Point p : l) {
				if (p == null)
					continue;
				if (p.getX() < minX)
					minX = p.getX();
				if (p.getY() < minY)
					minY = p.getY();
				if (p.getX() > maxX)
					maxX = p.getX();
				if (p.getY() > maxY)
					maxY = p.getY();
			}
		}
		this.setBounds(new Bounds(new Point(minX, minY), new Point(maxX, maxY)));
	}
}
