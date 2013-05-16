package org.apromore.graph.rpst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jbpt.algo.tree.rpst.IRPST;
import org.jbpt.algo.tree.rpst.IRPSTNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.jbpt.algo.tree.tctree.TCType;
import org.jbpt.graph.abs.AbstractTree;
import org.jbpt.graph.abs.IDirectedEdge;
import org.jbpt.graph.abs.IDirectedGraph;
import org.jbpt.hypergraph.abs.IVertex;


public class vRPST<E extends IDirectedEdge<V>, V extends IVertex> extends AbstractTree<IvRPSTNode<E, V>> implements IvRPST<E, V> {

    protected IDirectedGraph<E, V> diGraph = null;

    public vRPST(IDirectedGraph<E, V> graph) {
        if (graph == null) return;

        this.diGraph = graph;

        this.constructRPST();
    }

    private void constructRPST() {
        if (this.handleBorderCases()) return;

        IRPST<E, V> rpst = new RPST<E, V>(this.diGraph);

        IRPSTNode<E, V> node = rpst.getRoot();
        if (node == null) return;

        Queue<IRPSTNode<E, V>> queue = new ConcurrentLinkedQueue<>();
        queue.add(node);

        HashMap<IRPSTNode<E, V>, IvRPSTNode<E, V>> n2v = new HashMap<>();
        HashMap<IvRPSTNode<E, V>, IRPSTNode<E, V>> v2n = new HashMap<>();

        int i = 0;

        // reconstruct tree
        while (!queue.isEmpty()) {
            IRPSTNode<E, V> n = queue.poll();
            IvRPSTNode<E, V> v = null;

            if (n.getType() != TCType.TRIVIAL) {
                V entry = n.getEntry();
                V exit = n.getExit();
                TCType type = n.getType();
                String name = n.getName();
                Set<E> edges = new HashSet<>();

                Set<V> vs = new HashSet<V>();
                Set<E> vsEntries = new HashSet<E>();
                Set<E> vsExits = new HashSet<E>();
                for (IRPSTNode<E, V> c : rpst.getChildren(n)) {
                    if (c.getType() == TCType.TRIVIAL) {
                        edges.addAll(c.getFragment());

                        if (n.getType() != TCType.POLYGON && this.diGraph.getOutgoingEdges(c.getEntry()).size() == 1 && this.diGraph.getIncomingEdges(c.getEntry()).size() == 0) {
                            if (this.isGateway(c.getExit()))
                                vsEntries.add(c.getFragment().iterator().next());
                            else
                                vs.add(c.getEntry());
                        }

                        if (n.getType() != TCType.POLYGON && this.diGraph.getIncomingEdges(c.getExit()).size() == 1 && this.diGraph.getOutgoingEdges(c.getExit()).size() == 0) {
                            if (this.isGateway(c.getEntry()))
                                vsExits.add(c.getFragment().iterator().next());
                            else
                                vs.add(c.getExit());
                        }
                    }
                }

                v = new vRPSTNode<E, V>(this, entry, exit, type, name, edges, null);
                n2v.put(n, v);
                v2n.put(v, n);

                IvRPSTNode<E, V> p = n2v.get(rpst.getParent(n));
                if (p == null) this.addVertex(v);
                else this.addEdge(p, v);

                if (n.getType() != TCType.POLYGON) {
                    for (V vertex : vs) {
                        IvRPSTNode<E, V> c = new vRPSTNode<E, V>(this, vertex, vertex, TCType.TRIVIAL, "T" + (i++), new HashSet<E>(), vertex);
                        this.addEdge(v, c);
                    }

                    for (E edge : vsEntries) {
                        IvRPSTNode<E, V> c = new vRPSTNode<E, V>(this, null, edge.getTarget(), TCType.TRIVIAL, "T" + (i++), new HashSet<E>(), edge.getSource());
                        this.addEdge(v, c);
                    }

                    for (E edge : vsExits) {
                        IvRPSTNode<E, V> c = new vRPSTNode<E, V>(this, edge.getSource(), null, TCType.TRIVIAL, "T" + (i++), new HashSet<E>(), edge.getTarget());
                        this.addEdge(v, c);
                    }
                }

                queue.addAll(rpst.getChildren(n));
            }
        }

        // handle polygons
        List<IvRPSTNode<E, V>> list = null;

        for (IvRPSTNode<E, V> v : this.getRPSTNodes(TCType.POLYGON)) {
            IRPSTNode<E, V> n = v2n.get(v);

            if (v.getEdges().size() == 2 && rpst.getChildren(n).size() == 2) {
                IvRPSTNode<E, V> p = this.getParent(v);
                this.removeVertex(v);
                p.getEdges().addAll(v.getEdges());
                E e = v.getEdges().iterator().next();
                V trivial = v.getEntry() == e.getSource() ? e.getTarget() : e.getSource();

                IvRPSTNode<E, V> c = new vRPSTNode<E, V>(this, v.getEntry(), v.getExit(), TCType.TRIVIAL, "T" + (i++), new HashSet<E>(), trivial);
                this.addEdge(p, c);
            } else {
                list = new ArrayList<>();

                IRPSTNode<E, V> last = null;
                for (IRPSTNode<E, V> curr : rpst.getPolygonChildren(n)) {
                    if (last == null) {
                        if (curr.getType() == TCType.TRIVIAL) {
                            V trivial = curr.getEntry();
                            if (this.diGraph.getOutgoingEdges(trivial).size() < 2 && this.diGraph.getIncomingEdges(trivial).size() < 2) {
                                IvRPSTNode<E, V> c = new vRPSTNode<E, V>(this, trivial, trivial, TCType.TRIVIAL, "T" + (i++), new HashSet<E>(), trivial);
                                this.addEdge(v, c);
                                list.add(c);
                            }
                        }
                    } else {
                        if (last.getType() == TCType.TRIVIAL && curr.getType() == TCType.TRIVIAL) {
                            V trivial = curr.getEntry();
                            V entry = v.getEntry();
                            V exit = v.getExit();
                            if (!this.isGateway(entry)) entry = trivial;
                            if (!this.isGateway(exit)) exit = trivial;
                            IvRPSTNode<E, V> c = new vRPSTNode<E, V>(this, entry, exit, TCType.TRIVIAL, "T" + (i++), new HashSet<E>(), trivial);
                            this.addEdge(v, c);
                            list.add(c);
                        }
                    }

                    last = curr;

                    if (last.getType() != TCType.TRIVIAL)
                        list.add(n2v.get(curr));
                }

                if (last.getType() == TCType.TRIVIAL) {
                    V trivial = last.getExit();
                    if (this.diGraph.getOutgoingEdges(trivial).size() < 2 && this.diGraph.getIncomingEdges(trivial).size() < 2) {
                        IvRPSTNode<E, V> c = new vRPSTNode<E, V>(this, trivial, trivial, TCType.TRIVIAL, "T" + (i++), new HashSet<E>(), trivial);
                        this.addEdge(v, c);
                        list.add(c);
                    }
                }

                v.getOrderedChildren().addAll(list);
            }
        }

        this.reRoot(n2v.get(rpst.getRoot()));
    }

    private boolean handleBorderCases() {
        if (this.diGraph.getEdges().size() == 0) {
            if (this.diGraph.getVertices().size() == 0)
                return true;

            if (this.diGraph.getVertices().size() == 1) {
                V v = this.diGraph.getVertices().iterator().next();
                IvRPSTNode<E, V> t = new vRPSTNode<E, V>(this, v, v, TCType.TRIVIAL, "T0", new HashSet<E>(), v);
                this.addVertex(t);
                this.reRoot(t);

                return true;
            }

            IvRPSTNode<E, V> root = new vRPSTNode<E, V>(this, null, null, TCType.BOND, "B0", new HashSet<E>(), null);
            this.addVertex(root);

            int i = 1;
            for (V v : this.diGraph.getVertices()) {
                IvRPSTNode<E, V> c = new vRPSTNode<E, V>(this, v, v, TCType.TRIVIAL, "T" + (i++), new HashSet<E>(), v);
                this.addEdge(root, c);
            }

            this.reRoot(root);

            return true;
        }
        return false;
    }

    private boolean isGateway(V v) {
        return this.diGraph.getOutgoingEdges(v).size() > 1 || this.diGraph.getIncomingEdges(v).size() > 1;
    }

    @Override
    public IDirectedGraph<E, V> getGraph() {
        return this.diGraph;
    }

    @Override
    public Set<IvRPSTNode<E, V>> getRPSTNodes(TCType type) {
        Set<IvRPSTNode<E, V>> result = new HashSet<IvRPSTNode<E, V>>();
        for (IvRPSTNode<E, V> node : this.getVertices())
            if (node.getType() == type)
                result.add(node);

        return result;
    }

    @Override
    public Set<IvRPSTNode<E, V>> getRPSTNodes() {
        return new HashSet<IvRPSTNode<E, V>>(this.getVertices());
    }

    @Override
    public IvRPSTNode<E, V> addChild(IvRPSTNode<E, V> p, IvRPSTNode<E, V> c) {
        throw new UnsupportedOperationException("The RPST cannot be modified!");
    }
}
