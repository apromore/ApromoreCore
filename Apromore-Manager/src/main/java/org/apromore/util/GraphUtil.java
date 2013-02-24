package org.apromore.util;

import org.apromore.common.Constants;
import org.apromore.graph.canonical.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
            newV.setGraph(ng);

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
            ng.addEdge(e.getId(), newSource, newTarget);
        }

        return vMap;
    }

    public static void fillGraph(Canonical g, Canonical sg) {
        fillNodes(g, sg);
        fillEdges(g, sg);
    }

    public static void fillNodes(Canonical g, Canonical sg) {
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
            g.addEdge(edge);
        }
    }

    public static Set<CPFNode> getNonGatewayParentNodes(CPFNode node) {
        Set<CPFNode> nodes = new HashSet<CPFNode>(0);
        Canonical graph = node.getGraph();
        for (CPFNode cpfNode : graph.getAllPredecessors(node)) {
            if (!isGatewayNode(cpfNode)) {
                nodes.add(cpfNode);
            }
        }
        return nodes;
    }

    public static Set<CPFNode> getNonGatewayChildrenNodes(CPFNode node) {
        Set<CPFNode> nodes = new HashSet<CPFNode>(0);
        Canonical graph = node.getGraph();
        for (CPFNode cpfNode : graph.getAllSuccessors(node)) {
            if (!isGatewayNode(cpfNode)) {
                nodes.add(cpfNode);
            }
        }
        return nodes;
    }

    public static Set<CPFNode> getEvents(Canonical g) {
        Set<CPFNode> nodes = new HashSet<CPFNode>(0);
        for (CPFNode node : g.getNodes()) {
            if (node.getNodeType().equals(NodeTypeEnum.EVENT) || node.getNodeType().equals(NodeTypeEnum.MESSAGE) ||
                    node.getNodeType().equals(NodeTypeEnum.TIMER)) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    public static Set<CPFNode> getFunctions(Canonical g) {
        Set<CPFNode> nodes = new HashSet<CPFNode>(0);
        for (CPFNode node : g.getNodes()) {
            if (node.getNodeType().equals(NodeTypeEnum.TASK)) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    public static boolean isWorkNode(INode node) {
        boolean result = false;
        if (node != null && node.getNodeType() != null) {
            if (node.getNodeType().equals(NodeTypeEnum.WORK)) {
                result = true;
            }
        }
        return result;
    }

    public static boolean isGatewayNode(INode node) {
        boolean result = false;
        if (node != null && node.getNodeType() != null) {
            if (node.getNodeType().equals(NodeTypeEnum.STATE) ||
                    node.getNodeType().equals(NodeTypeEnum.ORJOIN) || node.getNodeType().equals(NodeTypeEnum.ORSPLIT) ||
                    node.getNodeType().equals(NodeTypeEnum.ANDJOIN) || node.getNodeType().equals(NodeTypeEnum.ANDSPLIT) ||
                    node.getNodeType().equals(NodeTypeEnum.XORJOIN) || node.getNodeType().equals(NodeTypeEnum.XORSPLIT)) {
                result = true;
            }
        }
        return result;
    }

    public static boolean isSplitNode(INode node) {
        boolean result = false;
        if (node.getNodeType().equals(NodeTypeEnum.ORSPLIT) || node.getNodeType().equals(NodeTypeEnum.ANDSPLIT) ||
                node.getNodeType().equals(NodeTypeEnum.XORSPLIT)) {
            result = true;
        }
        return result;
    }

    public static boolean isJoinNode(INode node) {
        boolean result = false;
        if (node.getNodeType().equals(NodeTypeEnum.ORJOIN) || node.getNodeType().equals(NodeTypeEnum.ANDJOIN) ||
                node.getNodeType().equals(NodeTypeEnum.XORJOIN)) {
            result = true;
        }
        return result;
    }

    public static void removeEmptyNodes(Canonical graph) {
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
    }

}
