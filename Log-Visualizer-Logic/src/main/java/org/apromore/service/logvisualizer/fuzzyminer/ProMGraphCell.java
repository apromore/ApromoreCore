package org.apromore.service.logvisualizer.fuzzyminer;

import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.processmining.framework.util.Cleanable;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphNode;
import org.processmining.models.jgraph.ModelOwner;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.views.JGraphShapeView;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class ProMGraphCell extends DefaultGraphCell implements Cleanable, ModelOwner, ProMGraphElement {

    private static final long serialVersionUID = -5170284747077744754L;
    private DirectedGraphNode node;
    private ProMGraphModel model;
    private JGraphShapeView view;

    public ProMGraphCell(DirectedGraphNode node, ProMGraphModel model) {
        super(node.getLabel());
        this.node = node;
        this.model = model;
        // update();
        GraphConstants.setConstrained(getAttributes(), node.getAttributeMap().get(AttributeMap.SQUAREBB, false));
        GraphConstants.setSizeable(getAttributes(), node.getAttributeMap().get(AttributeMap.RESIZABLE, true));
        GraphConstants.setResize(getAttributes(), node.getAttributeMap().get(AttributeMap.AUTOSIZE, false));
        GraphConstants.setInset(getAttributes(), node.getAttributeMap().get(AttributeMap.INSET, 20));
        GraphConstants.setLineWidth(
                getAttributes(),
                new Float(node.getAttributeMap().get(AttributeMap.LINEWIDTH,
                        GraphConstants.getLineWidth(getAttributes()))));

    }


    public DirectedGraphNode getNode() {
        return node;
    }

    public String getUserObject() {
        return (String) super.getUserObject();
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

    }

    public ProMGraphPort addPort(Object userObject) {
        ProMGraphPort port = new ProMGraphPort(userObject, this.model);
        this.add(port);

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

    @Override
    public void updateViewsFromMap() {

    }

    /**
     * This implementation of equals seems to be required by JGraph. Changing it
     * to anything more meaningful will introduce very strange results.
     */
    public boolean equals(Object o) {
        return o == this;
    }

}

