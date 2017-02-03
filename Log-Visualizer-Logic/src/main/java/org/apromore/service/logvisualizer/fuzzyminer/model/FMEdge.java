package org.apromore.service.logvisualizer.fuzzyminer.model;

import org.jgraph.graph.GraphConstants;
import org.processmining.models.graphbased.AttributeMap;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphEdge;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FMColors;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public abstract class FMEdge<S extends FMNode, T extends FMNode> extends AbstractDirectedGraphEdge<S, T> {

    protected double significance;
    protected double correlation;
    protected double attenuationThreshold;

    public FMEdge(S source, T target, double significance, double correlation) {
        super(source, target);
        this.significance = significance;
        this.correlation = correlation;

        getAttributeMap().put(AttributeMap.STYLE, GraphConstants.STYLE_SPLINE);
        //	GraphConstants.setRouting(getAttributeMap(), GraphConstants.ROUTING_SIMPLE);
        getAttributeMap().put(AttributeMap.SHOWLABEL, false);
        getAttributeMap().put(AttributeMap.EDGEEND, AttributeMap.ArrowType.ARROWTYPE_TECHNICAL);
        getAttributeMap().put(AttributeMap.EDGEENDFILLED, true);
        String label = MutableFuzzyGraph.format(significance) + " " + MutableFuzzyGraph.format(correlation);
        getAttributeMap().put(AttributeMap.LABEL, label);
        getAttributeMap().put(AttributeMap.EDGECOLOR, FMColors.getEdgeColor((float) correlation));
        // getAttributeMap().put(AttributeMap.LINEWIDTH, new Float(2 + Math.log(Math.E) * Math.log(significance*200)));
        double width = 2 + Math.log(Math.E) * Math.log(significance * 100);
        getAttributeMap().put(AttributeMap.LINEWIDTH, new Float(width > 1.0 ? width : 1.0));
        //getAttributeMap().put(AttributeMap.LABEL, source.toString() + " -->>" + target.toString());
    }

    public double getSignificance() {
        return significance;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setSignificance(double significance) {
        this.significance = significance;
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }

    public int hashCode() {
        return (source.hashCode() << 2) + target.hashCode();
    }

    public String toString() {
        return "Edge " + source.id() + " -> " + target.id();
    }

    public void setAttenuationThreshold(double attenuationThreshold) {
        this.attenuationThreshold = attenuationThreshold;
    }

    /**
     * update GUI appearance of this edge
     */
    public void updateEdgeInterface() {
        getAttributeMap().put(AttributeMap.LINEWIDTH, new Float(this.getSignificance() / 2f));
        getAttributeMap().put(AttributeMap.EDGECOLOR, FMColors.getEdgeColor((float) this.getCorrelation()));
        getAttributeMap().put(AttributeMap.LABEL, getEdgeLabel());

    }

    public String[] getEdgeLabel() {
        return new String[] { MutableFuzzyGraph.format(this.significance), MutableFuzzyGraph.format(this.correlation) };
    }

}
