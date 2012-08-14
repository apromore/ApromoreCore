package org.apromore.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apromore.common.Constants;
import org.apromore.graph.JBPT.CPF;
import org.apromore.graph.JBPT.CpfEvent;
import org.apromore.graph.JBPT.CpfNode;
import org.apromore.graph.JBPT.CpfXorGateway;
import org.jbpt.graph.algo.rpst.RPST;
import org.jbpt.pm.Activity;
import org.jbpt.pm.ControlFlow;
import org.jbpt.pm.FlowNode;
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
    public static Map<String, String> copyContentGraph(CPF g, CPF ng) {
        Collection<FlowNode> vertices = g.getVertices();
        Collection<ControlFlow<FlowNode>> edges = g.getEdges();
        Map<String, String> vMap = new HashMap<>(0);
        Map<String, String> pocketMap = new HashMap<>(0);

        // copy vertices to the new graph
        for (FlowNode v : vertices) {
            String type = g.getVertexProperty(v.getId(), Constants.TYPE);
            FlowNode newV = new Activity(v.getName());

            ng.addVertex(newV);
            ng.setVertexProperty(newV.getId(), Constants.TYPE, type);
            vMap.put(v.getId(), newV.getId());

            if (Constants.POCKET.equals(type)) {
                pocketMap.put(v.getId(), newV.getId());
            }
        }

        // add edges connecting new vertices
        for (ControlFlow<FlowNode> e : edges) {
            FlowNode newSource = ng.getVertex(vMap.get(e.getSource().getId()));
            FlowNode newTarget = ng.getVertex(vMap.get(e.getTarget().getId()));
            ng.addEdge(newSource, newTarget);
        }

        return vMap;
    }

    public static void fillGraph(CPF g, CPF sg) {
        fillVertices(g, sg);
        fillEdges(g, sg);
    }

    public static void fillVertices(CPF g, CPF sg) {
        Collection<FlowNode> vs = sg.getVertices();
        for (FlowNode v : vs) {
            String type = sg.getVertexProperty(v.getId(), Constants.TYPE);
            g.addVertex(v);
            g.setVertexProperty(v.getId(), Constants.TYPE, type);
        }
    }

    public static void fillEdges(CPF g, CPF sg) {
        Collection<ControlFlow<FlowNode>> edges = sg.getEdges();
        for (ControlFlow<FlowNode> edge : edges) {
            g.addEdge(edge.getSource(), edge.getTarget());
        }
    }

    public static RPST<ControlFlow<FlowNode>, FlowNode> normalizeGraph(CPF graph) {
        log.debug("Normalizing graph with size " + graph.getVertices().size());

        List<FlowNode> srcs = graph.getSourceVertices();
        List<FlowNode> tgts = graph.getSinkVertices();

        // remove isolated vertices
        List<FlowNode> isolatedVertices = new ArrayList<>(0);
        for (FlowNode isrc : srcs) {
            if (tgts.contains(isrc)) {
                isolatedVertices.add(isrc);
            }
        }
        srcs.removeAll(isolatedVertices);
        tgts.removeAll(isolatedVertices);
        graph.removeVertices(isolatedVertices);

        FlowNode entry = null;
        FlowNode exit;

        for (FlowNode src : srcs) {
            String srcLabel = src.getName();
            if ("_entry_".equals(srcLabel)) {
                entry = src;
            }
        }

        if (entry == null) {
            srcs.retainAll(tgts);
            // remove nodes that have no input and output edges
            for (FlowNode v : srcs) {
                graph.removeVertex(v);
            }

            srcs = graph.getSourceVertices();
            tgts = graph.getSinkVertices();

            entry = new CpfEvent("_entry_");
            graph.addVertex(entry);

            exit = new CpfEvent("_exit_");
            graph.addVertex(exit);

            if (srcs.size() == 1) {
                for (FlowNode tgt : srcs) {
                    graph.addEdge(entry, tgt);
                }
            } else {
                FlowNode sourceAggregator = new CpfXorGateway("OR");
                graph.addFlowNode(sourceAggregator);
                graph.setVertexProperty(sourceAggregator.getId(), Constants.TYPE, Constants.CONNECTOR);
                graph.addEdge(entry, sourceAggregator);
                for (FlowNode tgt : srcs) {
                    graph.addEdge(sourceAggregator, tgt);
                }
            }

            if (tgts.size() == 1) {
                for (FlowNode src : tgts) {
                    graph.addEdge(src, exit);
                }
            } else {
                FlowNode sinkAggregator = new CpfXorGateway("OR");
                graph.addFlowNode(sinkAggregator);
                graph.setVertexProperty(sinkAggregator.getId(), Constants.TYPE, Constants.CONNECTOR);
                graph.addEdge(sinkAggregator, exit);
                for (FlowNode src : tgts) {
                    graph.addEdge(src, sinkAggregator);
                }
            }
        }

        return new RPST<>(graph);
    }
}
