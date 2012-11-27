package org.apromore.util;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.Canonical;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.jbpt.algo.tree.rpst.RPST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Collection<CPFNode> vertices = g.getVertices();
        Collection<CPFEdge> edges = g.getEdges();
        Map<String, String> vMap = new HashMap<String, String>(0);
        Map<String, String> pocketMap = new HashMap<String, String>(0);

        // copy vertices to the new graph
        for (CPFNode v : vertices) {
            String type = g.getNodeProperty(v.getId(), Constants.TYPE);
            CPFNode newV = new CPFNode();

            ng.addVertex(newV);
            ng.setNodeProperty(newV.getId(), Constants.TYPE, type);
            vMap.put(v.getId(), newV.getId());

            if (Constants.POCKET.equals(type)) {
                pocketMap.put(v.getId(), newV.getId());
            }
        }

        // add edges connecting new vertices
        for (CPFEdge e : edges) {
            CPFNode newSource = ng.getNode(vMap.get(e.getSource().getId()));
            CPFNode newTarget = ng.getNode(vMap.get(e.getTarget().getId()));
            ng.addEdge(newSource, newTarget);
        }

        return vMap;
    }

    public static void fillGraph(Canonical g, Canonical sg) {
        fillVertices(g, sg);
        fillEdges(g, sg);
    }

    public static void fillVertices(Canonical g, Canonical sg) {
        Collection<CPFNode> vs = sg.getVertices();
        for (CPFNode v : vs) {
            String type = sg.getNodeProperty(v.getId(), Constants.TYPE);
            g.addVertex(v);
            g.setNodeProperty(v.getId(), Constants.TYPE, type);
        }
    }

    public static void fillEdges(Canonical g, Canonical sg) {
        Collection<CPFEdge> edges = sg.getEdges();
        for (CPFEdge edge : edges) {
            g.addEdge(edge.getSource(), edge.getTarget());
        }
    }

    private void correctGraph(Canonical g) {
        Collection<CPFNode> ns = g.getNodes();
        Set<CPFNode> sources = new HashSet<CPFNode>();
        for (CPFNode n : ns) {
            if (g.getDirectPredecessors(n).isEmpty()) {
                sources.add(n);
            }
        }
    }

    public static RPST<CPFEdge, CPFNode> normalizeGraph(Canonical graph) {
        log.debug("Normalizing graph with size " + graph.getVertices().size());

        Set<CPFNode> srcs = graph.getSourceNodes();
        Set<CPFNode> tgts = graph.getSinkNodes();

        // remove isolated vertices
        List<CPFNode> isolatedVertices = new ArrayList<CPFNode>(0);
        for (CPFNode isrc : srcs) {
            if (tgts.contains(isrc)) {
                isolatedVertices.add(isrc);
            }
        }
        srcs.removeAll(isolatedVertices);
        tgts.removeAll(isolatedVertices);
        graph.removeVertices(isolatedVertices);

        CPFNode entry = null;
        CPFNode exit;

        for (CPFNode src : srcs) {
            String srcLabel = src.getName();
            if ("_entry_".equals(srcLabel)) {
                entry = src;
            }
        }

        for (CPFNode tgt : tgts) {
            String tgtLabel = tgt.getName();
            if ("_exit_".equals(tgtLabel)) {
                exit = tgt;
            }
        }

        if (entry == null) {
            srcs.retainAll(tgts);
            // remove nodes that have no input and output edges
            for (CPFNode v : srcs) {
                graph.removeVertex(v);
            }

            srcs = graph.getSourceNodes();
            tgts = graph.getSinkNodes();

            entry = new CPFNode();
            entry.setName("__entry__");
            graph.addVertex(entry);

            exit = new CPFNode();
            exit.setName("_exit_");
            graph.addVertex(exit);

            if (srcs.size() == 1) {
                for (CPFNode tgt : srcs) {
                    graph.addEdge(entry, tgt);
                }
            } else {
//                Node sourceAggregator = new OrSplit("OR");
//                graph.addNode(sourceAggregator);
//                graph.setNodeProperty(sourceAggregator.getId(), Constants.TYPE, Constants.CONNECTOR);
//                graph.addEdge(entry, sourceAggregator);
//                for (Node tgt : srcs) {
//                    graph.addEdge(sourceAggregator, tgt);
//                }
            }

            if (tgts.size() == 1) {
                for (CPFNode src : tgts) {
                    graph.addEdge(src, exit);
                }
            } else {
//                Node sinkAggregator = new OrJoin("OR");
//                graph.addNode(sinkAggregator);
//                graph.setNodeProperty(sinkAggregator.getId(), Constants.TYPE, Constants.CONNECTOR);
//                graph.addEdge(sinkAggregator, exit);
//                for (Node src : tgts) {
//                    graph.addEdge(src, sinkAggregator);
//                }
            }
        }

        return new RPST<CPFEdge, CPFNode>(graph);
    }

}
