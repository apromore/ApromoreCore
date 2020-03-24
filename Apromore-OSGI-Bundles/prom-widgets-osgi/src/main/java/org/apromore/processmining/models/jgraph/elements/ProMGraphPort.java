package org.apromore.processmining.models.jgraph.elements;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apromore.jgraph.graph.DefaultPort;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.processmining.models.connections.GraphLayoutConnection;
import org.apromore.processmining.models.graphbased.directed.BoundaryDirectedGraphNode;
import org.apromore.processmining.models.jgraph.ModelOwner;
import org.apromore.processmining.models.jgraph.ProMGraphModel;
import org.apromore.processmining.models.jgraph.views.JGraphPortView;

public class ProMGraphPort extends DefaultPort implements Cleanable, ModelOwner, ProMGraphElement {

	private static final long serialVersionUID = 34423826783834456L;
	private JGraphPortView view;
	private ProMGraphModel model;
	private boolean isBoundaryNode = false;
	private BoundaryDirectedGraphNode node;
	private final GraphLayoutConnection layoutConnection;

	public ProMGraphPort(Object userObject, ProMGraphModel model, GraphLayoutConnection layoutConnection) {
		super(userObject);
		this.model = model;
		this.layoutConnection = layoutConnection;
		if (userObject != null && userObject instanceof BoundaryDirectedGraphNode) {
			node = (BoundaryDirectedGraphNode) userObject;
			layoutConnection.getPortOffset(node);
			Dimension size = layoutConnection.getSize(node);
			Point2D offset = layoutConnection.getPortOffset(node);

			GraphConstants.setSize(getAttributes(), size);
			GraphConstants.setOffset(getAttributes(), offset);
			isBoundaryNode = true;
		}
	}

	@SuppressWarnings("unchecked")
	public void cleanUp() {
		view = null;
		setUserObject(null);
		Iterator<Object> edge = edges();
		List<Object> edges = new ArrayList<Object>();
		while (edge.hasNext()) {
			edges.add(edge.next());
		}
		for (Object e : edges) {
			removeEdge(e);
		}
		model = null;
	}

	public void setView(JGraphPortView view) {
		this.view = view;
	}

	public JGraphPortView getView() {
		return view;
	}

	public ProMGraphModel getModel() {
		return model;
	}

	public void updateViewsFromMap() {
		assert (view != null);

		if ((getUserObject() instanceof BoundaryDirectedGraphNode) ? ((BoundaryDirectedGraphNode) getUserObject())
				.getBoundingNode() != null : false) {

			// Update the port size
			// Note: the width and the height of a port should always be equal 			
			Dimension size = layoutConnection.getSize(node);
			Dimension currSize = new Dimension((int) view.getBounds().getWidth(), (int) view.getBounds().getHeight());// GraphConstants.getSize(getAttributes());
			if (!size.equals(currSize)) {
				//				GraphConstants.setSize(getAttributes(), size);
				view.setPortSize((int) size.getWidth());
			}

			Point2D offset = layoutConnection.getPortOffset(node);
			//			Point2D currOffset = new Point2D.Double(view.getBounds().getX(), view.getBounds().getY()); // GraphConstants.getOffset(getAttributes());
			Point2D currOffset = GraphConstants.getOffset(view.getAttributes());
			if (!offset.equals(currOffset)) {
				//				GraphConstants.setOffset(getAttributes(), offset);
				GraphConstants.setOffset(view.getAttributes(), offset);
			}
		}
	}

	/**
	 * This implementation of equals seems to be required by JGraph. Changing it
	 * to anything more meaningful will introduce very strange results.
	 */
	public boolean equals(Object o) {
		return o == this;
	}

	public boolean isBoundaryNode() {
		return isBoundaryNode;
	}

	public BoundaryDirectedGraphNode getBoundingNode() {
		return node;
	}

}
