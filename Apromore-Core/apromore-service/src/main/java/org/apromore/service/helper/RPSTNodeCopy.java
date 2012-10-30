package org.apromore.service.helper;

import java.util.ArrayList;
import java.util.Collection;

import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.INode;
import org.apromore.graph.canonical.Node;
import org.jbpt.algo.tree.rpst.RPSTNode;

public class RPSTNodeCopy {

    private Node entry;
    private Node exit;
    private Collection<Node> nodes;
    private Collection<Edge> edges;
    private String readableNodeType;

    public RPSTNodeCopy(RPSTNode<Edge, Node> f) {
        this.entry = f.getEntry();
        this.exit = f.getExit();

        nodes = new ArrayList<Node>();
        Collection<Node> originalVertices = f.getFragment().getGraph().getVertices();
        for (Node v : originalVertices) {
            nodes.add(v);
        }

        edges = new ArrayList<Edge>();
        Collection<Edge> originalEdges = f.getFragment();
        for (Edge e : originalEdges) {
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

    public void setEntry(Node entry) {
        this.entry = entry;
    }

    public Node getExit() {
        return exit;
    }

    public void setExit(Node exit) {
        this.exit = exit;
    }

    public Collection<Node> getNodes() {
        return nodes;
    }

    public void setNodes(Collection<Node> vertices) {
        this.nodes = vertices;
    }

    public Collection<Edge> getEdges() {
        return edges;
    }

    public void setEdges(Collection<Edge> edges) {
        this.edges = edges;
    }
}
