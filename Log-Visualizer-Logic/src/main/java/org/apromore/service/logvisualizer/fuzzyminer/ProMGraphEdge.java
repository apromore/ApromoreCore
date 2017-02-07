package org.apromore.service.logvisualizer.fuzzyminer;

import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.GraphConstants;
import org.processmining.framework.util.Cleanable;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.DirectedGraphEdge;
import org.processmining.models.jgraph.ModelOwner;
import org.processmining.models.jgraph.ProMGraphModel;
import org.processmining.models.jgraph.ProMLoopRouting;
import org.processmining.models.jgraph.views.JGraphEdgeView;


/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class ProMGraphEdge extends DefaultEdge implements Cleanable, ModelOwner, ProMGraphElement {

    private static final long serialVersionUID = 663907031594522244L;
    private ProMGraphModel model;
    private JGraphEdgeView view;
    private DirectedGraphEdge<?, ?> edge;

    public ProMGraphEdge(DirectedGraphEdge<?, ?> edge, ProMGraphModel model) {
        super(edge.getLabel());

        GraphConstants.setRouting(getAttributes(), ProMLoopRouting.ROUTER);
        GraphConstants.setLabelAlongEdge(getAttributes(), edge.getAttributeMap()
                .get(AttributeMap.LABELALONGEDGE, false));
        GraphConstants.setLineStyle(getAttributes(),
                edge.getAttributeMap().get(AttributeMap.STYLE, GraphConstants.STYLE_SPLINE));
        //GraphConstants.setLineStyle(getAttributes(), GraphConstants.STYLE_SPLINE);
        GraphConstants.setLineWidth(
                getAttributes(),
                new Float(edge.getAttributeMap().get(AttributeMap.LINEWIDTH,
                        GraphConstants.getLineWidth(getAttributes()))));

        float[] pattern = edge.getAttributeMap().get(AttributeMap.DASHPATTERN, new float[0]);
        if (pattern.length > 0f) {

            GraphConstants.setDashPattern(getAttributes(), pattern);
            GraphConstants.setDashOffset(getAttributes(), edge.getAttributeMap().get(AttributeMap.DASHOFFSET, 0f));
        }

        Integer arrow = null;
        AttributeMap.ArrowType type = edge.getAttributeMap().get(AttributeMap.EDGESTART, AttributeMap.ArrowType.ARROWTYPE_NONE);
        if (type == AttributeMap.ArrowType.ARROWTYPE_CLASSIC) {
            arrow = GraphConstants.ARROW_CLASSIC;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_CIRCLE) {
            arrow = GraphConstants.ARROW_CIRCLE;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_DIAMOND) {
            arrow = GraphConstants.ARROW_DIAMOND;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_DOUBLELINE) {
            arrow = GraphConstants.ARROW_DOUBLELINE;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_LINE) {
            arrow = GraphConstants.ARROW_LINE;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_SIMPLE) {
            arrow = GraphConstants.ARROW_SIMPLE;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_TECHNICAL) {
            arrow = GraphConstants.ARROW_TECHNICAL;
        }

        if (arrow != null) {
            GraphConstants.setLineBegin(getAttributes(), arrow);
            GraphConstants
                    .setBeginFill(getAttributes(), edge.getAttributeMap().get(AttributeMap.EDGESTARTFILLED, true));
        }

        arrow = null;
        type = edge.getAttributeMap().get(AttributeMap.EDGEEND, AttributeMap.ArrowType.ARROWTYPE_NONE);
        if (type == AttributeMap.ArrowType.ARROWTYPE_CLASSIC) {
            arrow = GraphConstants.ARROW_CLASSIC;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_CIRCLE) {
            arrow = GraphConstants.ARROW_CIRCLE;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_DIAMOND) {
            arrow = GraphConstants.ARROW_DIAMOND;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_DOUBLELINE) {
            arrow = GraphConstants.ARROW_DOUBLELINE;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_LINE) {
            arrow = GraphConstants.ARROW_LINE;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_SIMPLE) {
            arrow = GraphConstants.ARROW_SIMPLE;
        } else if (type == AttributeMap.ArrowType.ARROWTYPE_TECHNICAL) {
            arrow = GraphConstants.ARROW_TECHNICAL;
        }

        if (arrow != null) {
            GraphConstants.setLineEnd(getAttributes(), arrow);
            GraphConstants.setEndFill(getAttributes(), edge.getAttributeMap().get(AttributeMap.EDGEENDFILLED, true));
        }



        this.edge = edge;
        this.model = model;

    }

    public String toString() {
        return edge.toString();
    }

    public DirectedGraphEdge<?, ?> getEdge() {
        return edge;
    }

    public String getUserObject() {
        return (String) super.getUserObject();
    }

    public ProMGraphPort getSource() {
        return (ProMGraphPort) super.getSource();
    }

    public ProMGraphPort getTarget() {
        return (ProMGraphPort) super.getTarget();
    }

    public void setView(JGraphEdgeView view) {
        this.view = view;
    }

    public JGraphEdgeView getView() {
        return view;
    }

    @Override
    public void updateViewsFromMap() {

    }

    public void cleanUp() {
        if (view != null) {
            view.cleanUp();
        }
        model = null;
        view = null;
        edge = null;
    }

    public String getLabel() {
        return edge.getLabel();
    }

    public int hashCode() {
        return edge.hashCode();
    }

    /**
     * This implementation of equals seems to be required by JGraph. Changing it
     * to anything more meaningful will introduce very strange results.
     */
    public boolean equals(Object o) {
        return o == this;
    }

    public ProMGraphModel getModel() {
        return model;
    }

}