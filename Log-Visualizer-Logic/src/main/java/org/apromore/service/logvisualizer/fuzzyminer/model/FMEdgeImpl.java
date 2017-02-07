package org.apromore.service.logvisualizer.fuzzyminer.model;

import org.processmining.models.graphbased.AttributeMap;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class FMEdgeImpl extends FMEdge<FMNode, FMNode> {
    public FMEdgeImpl(FMNode source, FMNode target, double significance, double correlation) {
        super(source, target, significance, correlation);
        this.getAttributeMap().put("ProM_Vis_attr_edge end", AttributeMap.ArrowType.ARROWTYPE_TECHNICAL);
        this.getAttributeMap().put("ProM_Vis_attr_edgeEndFilled", Boolean.valueOf(true));
    }

    public String toString() {
        String sourceLabel = ((FMNode)this.source).getElementName() + "(" + ((FMNode)this.source).getEventType() + ")";
        String targetLabel = ((FMNode)this.target).getElementName() + "(" + ((FMNode)this.target).getEventType() + ")";
        String edgeLabel = sourceLabel + "-->" + targetLabel;
        return edgeLabel;
    }
}
