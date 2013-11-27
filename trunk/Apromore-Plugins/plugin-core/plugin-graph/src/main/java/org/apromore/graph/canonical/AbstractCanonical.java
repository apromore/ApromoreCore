package org.apromore.graph.canonical;

import org.jbpt.graph.abs.AbstractDirectedGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of ICanonical interface.
 *
 * @author Cameron James
 */
public abstract class AbstractCanonical<E extends IEdge<N>, N extends INode> extends AbstractDirectedGraph<E, N>
        implements ICanonical<E, N> {

    /**
     * Empty constructor.
     */
    public AbstractCanonical() { }



    @Override
    public N addNode(N node) {
        return this.addVertex(node);
    }

    @Override
    public N removeNode(final N node) {
        return this.removeVertex(node);
    }

    @Override
    public Collection<N> removeNodes(final Collection<N> nodes) {
        Collection<N> result = this.removeVertices(nodes);
        return result == null ? new ArrayList<N>() : result;
    }

    @Override
    public E removeFlow(E edge) {
        return this.removeEdge(edge);
    }

    @Override
    public Collection<E> removeFlows(Collection<E> edge) {
        Collection<E> result = this.removeEdges(edge);
        return result == null ? new ArrayList<E>() : result;
    }


    @Override
    public N getNode(String id) {
        N result = null;

        for (N node : this.getNodes()) {
            if (node.getId().equals(id)) {
                result = node;
                break;
            }
        }

        return result;
    }

    @Override
    public Set<N> getNodes() {
        return new HashSet<>(super.getVertices());
    }


    @Override
    public Set<E> getEdges() {
        return new HashSet<>(super.getEdges());
    }


    @Override
    public Set<N> getPostset(N node) {
        return new HashSet<>(this.getDirectSuccessors(node));
    }

    @Override
    public Set<N> getPostset(Collection<N> nodes) {
        return new HashSet<>(this.getDirectSuccessors(nodes));
    }

    @Override
    public Set<N> getPreset(N node) {
        return new HashSet<>(this.getDirectPredecessors(node));
    }

    @Override
    public Set<N> getPreset(Collection<N> nodes) {
        return new HashSet<>(this.getDirectPredecessors(nodes));
    }

    @Override
    public Set<N> getMin() {
        return this.getSourceNodes();
    }

    @Override
    public Set<N> getMax() {
        return this.getSinkNodes();
    }

    @Override
    public void clear() {
        this.removeVertices(this.getVertices());
    }
}
