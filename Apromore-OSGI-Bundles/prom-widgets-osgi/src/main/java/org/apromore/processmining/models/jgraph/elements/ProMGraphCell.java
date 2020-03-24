package org.apromore.processmining.models.jgraph.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingConstants;

import org.apromore.jgraph.graph.DefaultGraphCell;
import org.apromore.jgraph.graph.GraphConstants;
import org.apromore.processmining.models.connections.GraphLayoutConnection;
import org.apromore.processmining.models.graphbased.AttributeMap;
import org.apromore.processmining.models.graphbased.Expandable;
import org.apromore.processmining.models.graphbased.directed.DirectedGraphNode;
import org.apromore.processmining.models.jgraph.ModelOwner;
import org.apromore.processmining.models.jgraph.ProMGraphModel;
import org.apromore.processmining.models.jgraph.views.JGraphShapeView;

public class ProMGraphCell extends DefaultGraphCell implements Cleanable, ModelOwner, ProMGraphElement {

	private static final long serialVersionUID = -5170284747077744754L;
	private DirectedGraphNode node;
	private ProMGraphModel model;
	private JGraphShapeView view;
	private GraphLayoutConnection layoutConnection;


	public ProMGraphCell(DirectedGraphNode node, ProMGraphModel model, GraphLayoutConnection layoutConnection) {
		super(node.getLabel());
		this.node = node;
		this.model = model;
		this.layoutConnection = layoutConnection;
		// update();
		GraphConstants.setConstrained(getAttributes(), node.getAttributeMap().get(AttributeMap.SQUAREBB, false));
		GraphConstants.setSizeable(getAttributes(), node.getAttributeMap().get(AttributeMap.RESIZABLE, true));
		GraphConstants.setResize(getAttributes(), node.getAttributeMap().get(AttributeMap.AUTOSIZE, false));
		GraphConstants.setHorizontalAlignment(getAttributes(), SwingConstants.CENTER);
		GraphConstants.setInset(getAttributes(), node.getAttributeMap().get(AttributeMap.INSET, 20));
		GraphConstants.setLineWidth(
				getAttributes(),
				new Float(node.getAttributeMap().get(AttributeMap.LINEWIDTH,
						GraphConstants.getLineWidth(getAttributes()))));
		GraphConstants.setForeground(getAttributes(), node.getAttributeMap().get(AttributeMap.LABELCOLOR, Color.black));
		GraphConstants.setOrientation(getAttributes(),
				node.getAttributeMap().get(AttributeMap.PREF_ORIENTATION, SwingConstants.NORTH));

		Dimension2D dim;
		Point2D pos;

		if (node instanceof Expandable) {
			dim = ((Expandable) node).getCollapsedSize();
			pos = layoutConnection.getPosition(node);
		} else {
			dim = layoutConnection.getSize(node);
			pos = layoutConnection.getPosition(node);
		}
		if (pos == null) {
			pos = new Point2D.Double(10, 10);
		}
		Rectangle2D rect = new Rectangle2D.Double(pos.getX(), pos.getY(), dim.getWidth(), dim.getHeight());
		GraphConstants.setBounds(getAttributes(), rect);

	}

	public void updateViewsFromMap() {
		assert (view != null);
		// Update the dimension / position
		Dimension2D dim = layoutConnection.getSize(node);
		Point2D pos = layoutConnection.getPosition(node);
		if (pos == null) {
			pos = new Point2D.Double(10, 10);
			dim = new Dimension(10, 10);
		}

		Rectangle2D rect = new Rectangle2D.Double(pos.getX(), pos.getY(), dim.getWidth(), dim.getHeight());
		Rectangle2D bounds = view.getBounds();//GraphConstants.getBounds(getAttributes());

		boolean boundsChanged = !rect.equals(bounds);
		if (boundsChanged) {
			//			GraphConstants.setBounds(getAttributes(), rect);
			view.setBounds(rect);
		}
	}

	public DirectedGraphNode getNode() {
		return node;
	}

	public String getUserObject() {
		return (String) super.getUserObject();
	}

	public void setView(JGraphShapeView view) {
		this.view = view;
	}

	public void cleanUp() {
		for (Object o : getChildren()) {
			if (o instanceof Cleanable) {
				Cleanable p = (Cleanable) o;
				p.cleanUp();
			}
		}
		removeAllChildren();
		if (view != null) {
			view.cleanUp();
		}
		view = null;
		model = null;
		node = null;
		//layoutConnection = null;

	}

	// This method is called by all other addPort methods.
	@Override
	public ProMGraphPort addPort(Point2D offset, Object userObject) {
		ProMGraphPort port = new ProMGraphPort(userObject, model, layoutConnection);
		if (offset == null) {
			add(port);
		} else {
			GraphConstants.setOffset(port.getAttributes(), offset);
			add(port);
		}
		return port;
	}

	public String getLabel() {
		return node.getLabel();
	}

	public int hashCode() {
		return node.hashCode();
	}

	public ProMGraphModel getModel() {
		return model;
	}

	public JGraphShapeView getView() {
		return view;
	}

	/**
	 * This implementation of equals seems to be required by JGraph. Changing it
	 * to anything more meaningful will introduce very strange results.
	 */
	public boolean equals(Object o) {
		return o == this;
	}

}
