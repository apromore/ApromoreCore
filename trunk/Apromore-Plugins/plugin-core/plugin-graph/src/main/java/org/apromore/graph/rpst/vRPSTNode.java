package org.apromore.graph.rpst;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.Fragment;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.hypergraph.abs.IVertex;
import org.jbpt.hypergraph.abs.Vertex;


public class vRPSTNode<E extends IDirectedEdge<V>, V extends IVertex> extends Vertex implements IvRPSTNode<E, V> {
    // fragment entry
    protected V entry = null;
    // fragment exit
    protected V exit = null;
    // type of the triconnected component which induces this fragment
    protected TCType type = TCType.UNDEFINED;
    // fragment
    protected Fragment<E, V> fragment = null;
    // edges
    protected Set<E> edges = null;
    // trivial
    protected V trivial;
    // rpst
    protected IvRPST<E, V> rpst = null;
    // ordered children (if polygon)
    protected List<IvRPSTNode<E, V>> orderedChildren = new ArrayList<>();

    protected vRPSTNode(IvRPST<E, V> rpst, V entry, V exit, TCType type, String name, Set<E> edges, V trivial) {
        this.entry = entry;
        this.exit = exit;
        this.type = type;
        this.edges = edges;
        this.trivial = trivial;
        this.rpst = rpst;

        this.setName(name);
    }

    @Override
    public TCType getType() {
        return this.type;
    }

    @Override
    public V getEntry() {
        return this.entry;
    }

    @Override
    public V getExit() {
        return this.exit;
    }

    @Override
    public V getTrivial() {
        return this.trivial;
    }

    @Override
    public Set<E> getEdges() {
        return this.edges;
    }

    @Override
    public Fragment<E, V> getFragment() {
        if (this.fragment == null)
            this.constructFragment();

        return this.fragment;
    }

    private void constructFragment() {
        this.fragment = new Fragment<E, V>(this.rpst.getGraph());

        this.fragment.addAll(this.edges);

        for (IvRPSTNode<E, V> c : this.rpst.getChildren(this)) {
            this.fragment.addAll(c.getFragment());
        }
    }

    @Override
    public List<IvRPSTNode<E, V>> getOrderedChildren() {
        return this.orderedChildren;
    }

    @Override
    public String toString() {
        return String.format("%s: (%s,%s)[%s] - %s - %s", this.getName(), this.getEntry(), this.getExit(), this.getTrivial(), this.getEdges().toString(), this.getFragment().toString());
    }
}
