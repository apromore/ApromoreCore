package org.apromore.service.logvisualizer.fuzzyminer;

import org.jgraph.graph.DefaultPort;
import org.processmining.framework.util.Cleanable;
import org.processmining.models.graphbased.directed.BoundaryDirectedGraphNode;
import org.processmining.models.jgraph.ModelOwner;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.views.JGraphPortView;

import java.util.*;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class ProMGraphPort extends DefaultPort implements Cleanable, ModelOwner, ProMGraphElement {

    private static final long serialVersionUID = 34423826783834456L;
    private JGraphPortView view;
    private ProMGraphModel model;
    private boolean isBoundaryNode = false;
    private BoundaryDirectedGraphNode node;

    public ProMGraphPort(Object userObject, ProMGraphModel model) {
        super(userObject);
        this.model = model;
        if (userObject != null && userObject instanceof BoundaryDirectedGraphNode) {
            node = (BoundaryDirectedGraphNode) userObject;

            isBoundaryNode = true;
        }
    }

    @SuppressWarnings("unchecked")
    public void cleanUp() {
        view = null;
        setUserObject(null);
        Iterator<Object> edge = edges();
        java.util.List<Object> edges = new ArrayList<Object>();
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