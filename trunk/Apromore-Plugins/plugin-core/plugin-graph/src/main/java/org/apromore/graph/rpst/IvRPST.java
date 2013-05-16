package org.apromore.graph.rpst;

import java.util.Set;

import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.graph.abs.ITree;
import org.jbpt.hypergraph.abs.IVertex;

public interface IvRPST<E extends IDirectedEdge<V>, V extends IVertex> extends ITree<IvRPSTNode<E, V>> {

    /**
     * Get original graph.
     *
     * @return Original graph.
     */
    public IDirectedGraph<E, V> getGraph();


    /**
     * Get RPST nodes induced by the triconnected components of a given {@link org.jbpt.algo.tree.tctree.TCType} type.
     *
     * @param {@link TCType} type.
     * @return Set of RPST nodes induced by the given {@link org.jbpt.algo.tree.tctree.TCType} type.
     */
    public Set<IvRPSTNode<E, V>> getRPSTNodes(TCType type);

    /**
     * Get RPST nodes.
     *
     * @return Set of RPST nodes.
     */
    public Set<IvRPSTNode<E, V>> getRPSTNodes();

}