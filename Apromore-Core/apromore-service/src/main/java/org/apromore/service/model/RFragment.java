package org.apromore.service.model;

import java.util.Collection;

import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;

public class RFragment {

    private TCType type = null;
    private Collection<FlowNode> vertices = null;
    private Collection<ControlFlow<FlowNode>> edges = null;
    private Collection<RFragment> children = null;
    private RFragment parent = null;
    private FlowNode entry = null;
    private FlowNode exit = null;

    public TCType getType() {
        return type;
    }

    public void setType(TCType type) {
        this.type = type;
    }

    public Collection<FlowNode> getVertices() {
        return vertices;
    }

    public void setVertices(Collection<FlowNode> vertices) {
        this.vertices = vertices;
    }

    public void removeVertices(Collection<FlowNode> vertices) {
        this.vertices.removeAll(vertices);
    }

    public Collection<ControlFlow<FlowNode>> getEdges() {
        return edges;
    }

    public void setEdges(Collection<ControlFlow<FlowNode>> edges) {
        this.edges = edges;
    }

    public void removeEdge(ControlFlow<FlowNode> edge) {
        this.edges.remove(edge);
    }

    public Collection<RFragment> getChildren() {
        return children;
    }

    public void setChildren(Collection<RFragment> children) {
        this.children = children;
    }

    public RFragment getParent() {
        return parent;
    }

    public void setParent(RFragment parent) {
        this.parent = parent;
    }

    public FlowNode getEntry() {
        return entry;
    }

    public void setEntry(FlowNode entry) {
        this.entry = entry;
    }

    public FlowNode getExit() {
        return exit;
    }

    public void setExit(FlowNode exit) {
        this.exit = exit;
    }

    public void addVertex(FlowNode vertex) {
        this.vertices.add(vertex);
    }

    public void addControlFlow(FlowNode source, FlowNode target) {
//		this.edges.add(new Con)

    }
}
