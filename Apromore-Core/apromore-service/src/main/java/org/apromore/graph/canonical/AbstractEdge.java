package org.apromore.graph.canonical;

import org.jbpt.graph.abs.AbstractDirectedEdge;
import org.jbpt.graph.abs.AbstractDirectedGraph;
import org.jbpt.graph.abs.AbstractMultiDirectedGraph;
import org.jbpt.hypergraph.abs.AbstractMultiDirectedHyperGraph;

/**
 * Implementation of a Canonical flow relation.
 *
 * @author Cameron James
 */
public abstract class AbstractEdge<N extends INode> extends AbstractDirectedEdge<N> implements IEdge<N> {

    /**
     * Constructor of a flow relation.
     * @param g      A directed graph.
     * @param source Source node.
     * @param target Target node.
     */
    protected AbstractEdge(AbstractMultiDirectedGraph<?, N> g, N source, N target) {
        super(g, source, target);
    }
}
