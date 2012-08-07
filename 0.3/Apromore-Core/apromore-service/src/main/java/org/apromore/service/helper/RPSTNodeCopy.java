package org.apromore.service.helper;

import java.util.ArrayList;
import java.util.Collection;

import org.apromore.graph.JBPT.ICpfNode;
import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.algo.rpst.RPSTNode;
import org.jbpt.hypergraph.abs.IVertex;

public class RPSTNodeCopy {

    private IVertex entry;
    private IVertex exit;
    private Collection<ICpfNode> vertices;
    private Collection<AbstractDirectedEdge> edges;
    private String readableNodeType;

    public RPSTNodeCopy(RPSTNode fragment) {
        this.entry = fragment.getEntry();
        this.exit = fragment.getExit();

        vertices = new ArrayList<ICpfNode>();
        Collection<ICpfNode> originalVertices = fragment.getFragment().getVertices();
        for (ICpfNode v : originalVertices) {
            vertices.add(v);
        }

        edges = new ArrayList<AbstractDirectedEdge>();
        Collection<AbstractDirectedEdge> originalEdges = fragment.getFragment().getEdges();
        for (AbstractDirectedEdge e : originalEdges) {
            edges.add(e);
        }
    }

    public int getSize() {
        return vertices.size() + edges.size();
    }

    public int getNumVertices() {
        return vertices.size();
    }

    public String getReadableNodeType() {
        return readableNodeType;
    }

    public void setReadableNodeType(String readableNodeType) {
        this.readableNodeType = readableNodeType;
    }

    public IVertex getEntry() {
        return entry;
    }

    public void setEntry(IVertex entry) {
        this.entry = entry;
    }

    public IVertex getExit() {
        return exit;
    }

    public void setExit(IVertex exit) {
        this.exit = exit;
    }

    public Collection<ICpfNode> getVertices() {
        return vertices;
    }

    public void setVertices(Collection<ICpfNode> vertices) {
        this.vertices = vertices;
    }

    public Collection<AbstractDirectedEdge> getEdges() {
        return edges;
    }

    public void setEdges(Collection<AbstractDirectedEdge> edges) {
        this.edges = edges;
    }
}
