package org.apromore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.Edge;
import org.apromore.graph.canonical.Node;
import org.apromore.graph.canonical.OrJoin;
import org.apromore.graph.canonical.OrSplit;
import org.jbpt.algo.tree.rpst.RPST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chathura Ekanayake
 */
public class GraphUtil {

    private static final Logger log = LoggerFactory.getLogger(GraphUtil.class);


    /**
     * Copies the graph g to ng by duplicating all vertices and edges.
     *
     * @param g  Source graph
     * @param ng Target graph
     * @return mapping old node Id -> new node Id
     */
    public static Map<String, String> copyContentGraph(Canonical g, Canonical ng) {
        Collection<Node> vertices = g.getVertices();
        Collection<Edge> edges = g.getEdges();
        Map<String, String> vMap = new HashMap<String, String>(0);
        Map<String, String> pocketMap = new HashMap<String, String>(0);

        // copy vertices to the new graph
        for (Node v : vertices) {
            String type = g.getNodeProperty(v.getId(), Constants.TYPE);
            Node newV = new Node(v.getName());

            ng.addVertex(newV);
            ng.setNodeProperty(newV.getId(), Constants.TYPE, type);
            vMap.put(v.getId(), newV.getId());

            if (Constants.POCKET.equals(type)) {
                pocketMap.put(v.getId(), newV.getId());
            }
        }

        // add edges connecting new vertices
        for (Edge e : edges) {
            Node newSource = ng.getNode(vMap.get(e.getSource().getId()));
            Node newTarget = ng.getNode(vMap.get(e.getTarget().getId()));
            ng.addEdge(newSource, newTarget);
        }

        return vMap;
    }

    public static void fillGraph(Canonical g, Canonical sg) {
        fillVertices(g, sg);
        fillEdges(g, sg);
    }

    public static void fillVertices(Canonical g, Canonical sg) {
        Collection<Node> vs = sg.getVertices();
        for (Node v : vs) {
            String type = sg.getNodeProperty(v.getId(), Constants.TYPE);
            g.addVertex(v);
            g.setNodeProperty(v.getId(), Constants.TYPE, type);
        }
    }

    public static void fillEdges(Canonical g, Canonical sg) {
        Collection<Edge> edges = sg.getEdges();
        for (Edge edge : edges) {
            g.addEdge(edge.getSource(), edge.getTarget());
        }
    }

    private void correctGraph(Canonical g) {
        Collection<Node> ns = g.getNodes();
        Set<Node> sources = new HashSet<Node>();
        for (Node n : ns) {
            if (g.getDirectPredecessors(n).isEmpty()) {
                sources.add(n);
            }
        }
    }

    public static RPST<Edge, Node> normalizeGraph(Canonical graph) {
        log.debug("Normalizing graph with size " + graph.getVertices().size());

        Set<Node> srcs = graph.getSourceNodes();
        Set<Node> tgts = graph.getSinkNodes();

        // remove isolated vertices
        List<Node> isolatedVertices = new ArrayList<Node>(0);
        for (Node isrc : srcs) {
            if (tgts.contains(isrc)) {
                isolatedVertices.add(isrc);
            }
        }
        srcs.removeAll(isolatedVertices);
        tgts.removeAll(isolatedVertices);
        graph.removeVertices(isolatedVertices);

        Node entry = null;
        Node exit;

        for (Node src : srcs) {
            String srcLabel = src.getName();
            if ("_entry_".equals(srcLabel)) {
                entry = src;
            }
        }

        for (Node tgt : tgts) {
            String tgtLabel = tgt.getName();
            if ("_exit_".equals(tgtLabel)) {
                exit = tgt;
            }
        }

        if (entry == null) {
            srcs.retainAll(tgts);
            // remove nodes that have no input and output edges
            for (Node v : srcs) {
                graph.removeVertex(v);
            }

            srcs = graph.getSourceNodes();
            tgts = graph.getSinkNodes();

            entry = new Node("_entry_");
            graph.addVertex(entry);

            exit = new Node("_exit_");
            graph.addVertex(exit);

            if (srcs.size() == 1) {
                for (Node tgt : srcs) {
                    graph.addEdge(entry, tgt);
                }
            } else {
                Node sourceAggregator = new OrSplit("OR");
                graph.addNode(sourceAggregator);
                graph.setNodeProperty(sourceAggregator.getId(), Constants.TYPE, Constants.CONNECTOR);
                graph.addEdge(entry, sourceAggregator);
                for (Node tgt : srcs) {
                    graph.addEdge(sourceAggregator, tgt);
                }
            }

            if (tgts.size() == 1) {
                for (Node src : tgts) {
                    graph.addEdge(src, exit);
                }
            } else {
                Node sinkAggregator = new OrJoin("OR");
                graph.addNode(sinkAggregator);
                graph.setNodeProperty(sinkAggregator.getId(), Constants.TYPE, Constants.CONNECTOR);
                graph.addEdge(sinkAggregator, exit);
                for (Node src : tgts) {
                    graph.addEdge(src, sinkAggregator);
                }
            }
        }

        return new RPST<Edge, Node>(graph);
    }

}
