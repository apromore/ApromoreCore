package org.apromore.graph.rpst;

import java.util.List;
import java.util.Set;

import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IFragment;
import org.jbpt.hypergraph.abs.IVertex;

public interface IvRPSTNode<E extends IDirectedEdge<V>, V extends IVertex> extends IVertex {

    public TCType getType();

    public V getEntry();

    public V getExit();

    public V getTrivial();

    public Set<E> getEdges();

    public IFragment<E, V> getFragment();

    public List<IvRPSTNode<E, V>> getOrderedChildren();
}