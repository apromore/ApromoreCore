package org.apromore.service.logvisualizer.fuzzyminer.model;

import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XEvent;
import org.processmining.models.graphbased.directed.AbstractDirectedGraphNode;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FMColors;
import org.processmining.models.graphbased.directed.fuzzymodel.util.FMLog;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Raffaele Conforti (conforti.raffaele@gmail.com) on 2/2/17.
 */
public class FMNode extends AbstractDirectedGraphNode {
    protected MutableFuzzyGraph graph;
    protected int index;
    protected String nodeLabel;
    protected boolean isInnerPatternGraphNode;

    public FMNode(MutableFuzzyGraph graph, int index, String label) {
        this.graph = graph;
        this.index = index;
        this.nodeLabel = label;
        this.isInnerPatternGraphNode = false;
        this.getAttributeMap().put("ProM_Vis_attr_label", label);
    }

    public boolean isDirectlyConnectedTo(FMNode other) {
        return other instanceof FMClusterNode ?other.isDirectlyConnectedTo(this):this.graph.getBinarySignificance(this.index, other.index) > 0.0D || this.graph.getBinarySignificance(other.index, this.index) > 0.0D;
    }

    public boolean directlyFollows(FMNode other) {
        if(other instanceof FMClusterNode) {
            Set otherPrimitives = ((FMClusterNode)other).getPrimitives();
            Iterator i$ = otherPrimitives.iterator();

            FMNode n;
            do {
                if(!i$.hasNext()) {
                    return false;
                }

                n = (FMNode)i$.next();
            } while(!this.directlyFollows(n));

            return true;
        } else {
            return this.graph.getBinarySignificance(this.index, other.index) > 0.0D;
        }
    }

    public Set<FMNode> getPredecessors() {
        HashSet predecessors = new HashSet();

        for(int x = 0; x < this.graph.getNumberOfInitialNodes(); ++x) {
            if(x != this.index && this.graph.getBinarySignificance(x, this.index) > 0.0D) {
                FMNode pre = this.graph.getNodeMappedTo(x);
                if(pre != null) {
                    predecessors.add(pre);
                }
            }
        }

        return predecessors;
    }

    public Set<FMNode> getSuccessors() {
        HashSet successors = new HashSet();

        for(int y = 0; y < this.graph.getNumberOfInitialNodes(); ++y) {
            if(y != this.index && this.graph.getBinarySignificance(this.index, y) > 0.0D) {
                FMNode post = this.graph.getNodeMappedTo(y);
                if(post != null) {
                    successors.add(post);
                }
            }
        }

        return successors;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public MutableFuzzyGraph getGraph() {
        return this.graph;
    }

    public double getSignificance() {
        return this.graph.getNodeSignificanceMetric().getMeasure(this.index);
    }

    public void setSignificance(double significance) {
        graph.getNodeSignificanceMetric().setMeasure(index, significance);
    }

    public String id() {
        return "node_" + this.index;
    }

    public String toString() {
        return this.getElementName() + " (" + this.getEventType() + ")";
    }

    public String getElementName() {
        String nodeEventName;
        if(!this.isInnerPatternGraphNode) {
            nodeEventName = FMLog.getConceptName((XAttributable)this.graph.getLogEvents().get(this.index));
            return nodeEventName != null?nodeEventName:"";
        } else {
            nodeEventName = nodeLabel;
            int idx = nodeEventName.indexOf(" ");
            nodeEventName = nodeEventName.substring(0, idx);

            return nodeEventName;
        }
    }

    public String getEventType() {
        String nodeEventType;
        if(!this.isInnerPatternGraphNode) {
            nodeEventType = FMLog.getLifecycleTransition((XEvent)this.graph.getLogEvents().get(this.index));
            return nodeEventType != null?nodeEventType:"";
        } else {
            nodeEventType = this.nodeLabel;
            int length = nodeEventType.length();
            int idx = nodeEventType.indexOf(" ");
            nodeEventType = nodeEventType.substring(idx + 4, length);

            return nodeEventType;
        }
    }

    public void setLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
        this.getAttributeMap().put("ProM_Vis_attr_label", nodeLabel);
    }
}