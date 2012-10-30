package org.apromore.graph;

import java.util.Set;

import org.apromore.graph.canonical.Canonical;
import org.jbpt.graph.Edge;
import org.jbpt.graph.Graph;

public interface Visitor {

    String visitRootSNode(Canonical graph, Set<Edge> edges, Set<Integer> vertices, Integer entry, Integer exit);

    String visitSNode(Canonical graph, Set<Edge> edges, Set<Integer> vertices, Integer entry, Integer exit);

    String visitPNode(Graph graph, Set<Edge> edges, Set<Integer> vertices, Integer entry, Integer exit);

    String visitRNode(Graph graph, Set<Edge> edges, Set<Integer> vertices, Integer entry, Integer exit);

}
