package org.apromore.service.helper;

import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.INode;
import org.jbpt.algo.tree.rpst.RPSTNode;

import java.util.ArrayList;
import java.util.Collection;

public class RPSTNodeCopy {

    private CPFNode entry;
    private CPFNode exit;
    private Collection<CPFNode> nodes;
    private Collection<CPFEdge> edges;
    private String readableNodeType;

    public RPSTNodeCopy(RPSTNode<CPFEdge, CPFNode> f) {
        this.entry = f.getEntry();
        this.exit = f.getExit();

        nodes = new ArrayList<CPFNode>();
        Collection<CPFNode> originalVertices = f.getFragment().getGraph().getVertices();
        for (CPFNode v : originalVertices) {
            nodes.add(v);
        }

        edges = new ArrayList<CPFEdge>();
        Collection<CPFEdge> originalEdges = f.getFragment();
        for (CPFEdge e : originalEdges) {
            edges.add(e);
        }
    }

    public int getSize() {
        return nodes.size() + edges.size();
    }

    public int getNumVertices() {
        return nodes.size();
    }

    public String getReadableNodeType() {
        return readableNodeType;
    }

    public void setReadableNodeType(String readableNodeType) {
        this.readableNodeType = readableNodeType;
    }

    public INode getEntry() {
        return entry;
    }

    public void setEntry(CPFNode entry) {
        this.entry = entry;
    }

    public CPFNode getExit() {
        return exit;
    }

    public void setExit(CPFNode exit) {
        this.exit = exit;
    }

    public Collection<CPFNode> getNodes() {
        return nodes;
    }

    public void setNodes(Collection<CPFNode> vertices) {
        this.nodes = vertices;
    }

    public Collection<CPFEdge> getEdges() {
        return edges;
    }

    public void setEdges(Collection<CPFEdge> edges) {
        this.edges = edges;
    }
}
