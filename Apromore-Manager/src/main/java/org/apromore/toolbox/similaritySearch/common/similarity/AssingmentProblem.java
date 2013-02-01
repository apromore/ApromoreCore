package org.apromore.toolbox.similaritySearch.common.similarity;

import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.similaritySearch.common.NodePair;
import org.apromore.toolbox.similaritySearch.common.Settings;
import org.apromore.toolbox.similaritySearch.common.stemmer.SnowballStemmer;
import org.apromore.util.GraphUtil;

import java.util.*;


public class AssingmentProblem {

    /**
     * Finds the matching vertices between graphs g1 and g2
     * @param g1 the first Canonical Graph
     * @param g2 the second Canonical Graph
     * @param threshold - if node similarity is >= than threshold then these nodes are considered to be matched.
     * @param stemmer   - stemmer for wrord stemming, if == null, then english stemmer is used
     * @return matching vertex pairs
     */
    public Set<NodePair> getMappingsGraph(Canonical g1, Canonical g2, double threshold, SnowballStemmer stemmer) {
        Set<CPFNode> g1Vertices = GraphUtil.getFunctions(g1);
        Set<CPFNode> g2Vertices = GraphUtil.getFunctions(g2);

        if (Settings.considerEvents) {
            g1Vertices.addAll(GraphUtil.getEvents(g1));
            g2Vertices.addAll(GraphUtil.getEvents(g2));
        }

        return getMappingsNodes(g1Vertices, g2Vertices, threshold, stemmer, 0);
    }


    public static boolean canMap(CPFNode v1, CPFNode v2) {
        return !(v1.getGraph().getAllPredecessors(v1).size() == 0 && v2.getGraph().getAllPredecessors(v2).size() != 0
                || v1.getGraph().getAllPredecessors(v1).size() != 0 && v2.getGraph().getAllPredecessors(v2).size() == 0
                || v1.getGraph().getAllSuccessors(v1).size() == 0 && v2.getGraph().getAllSuccessors(v2).size() != 0
                || v1.getGraph().getAllSuccessors(v1).size() != 0 && v2.getGraph().getAllSuccessors(v2).size() == 0);
    }

    /**
     * Finds the vertex mapping.
     * @param g1Vertices - graph g1 vertices that need to be matched with graph g1 vertices
     * @param g2Vertices - graph g2 vertices
     * @param threshold  - if node similarity is >= than threshold then these nodes are considered to be matched.
     * @param stemmer    - stemmer for wrord stemming, if == null, then english stemmer is used
     * @param gateways   - if == 0, then gateways are not matched, if == 1, then only parent are looked, if == 2, then only children are looked
     * @return matching vertex pairs
     */
    public Set<NodePair> getMappingsNodes(Collection<CPFNode> g1Vertices, Collection<CPFNode> g2Vertices, double threshold,
            SnowballStemmer stemmer, int gateways) {
        Set<NodePair> solutionMappings = new HashSet<NodePair>();

        if (g1Vertices.size() == 0 || g2Vertices.size() == 0) {
            return solutionMappings;
        }
        if (stemmer == null) {
            stemmer = Settings.getEnglishStemmer();
        }

        List<CPFNode> g1Vertices_fe = new ArrayList<CPFNode>();
        List<CPFNode> g2Vertices_fe = new ArrayList<CPFNode>();
        for (CPFNode v : g1Vertices) {
            if (!GraphUtil.isGatewayNode(v)) {
                g1Vertices_fe.add(v);
            }
        }
        for (CPFNode v : g2Vertices) {
            if (!GraphUtil.isGatewayNode(v)) {
                g2Vertices_fe.add(v);
            }
        }

        if (g1Vertices_fe.size() > 0 && g2Vertices_fe.size() > 0) {
            int dimFunc = g1Vertices_fe.size() > g2Vertices_fe.size() ? g1Vertices_fe.size() : g2Vertices_fe.size();
            double costs[][] = new double[dimFunc][dimFunc];
            double costsCopy[][] = new double[dimFunc][dimFunc];
            int nrZeros = 0;

            // function mapping score
            for (int i = 0; i < g1Vertices_fe.size(); i++) {
                for (int j = 0; j < g2Vertices_fe.size(); j++) {
                    double edScore = 0;
                    if (g1Vertices_fe.get(i).getNodeType().equals(g2Vertices_fe.get(j).getNodeType()) &&
                            g1Vertices_fe.get(i).getLabel() != null && g2Vertices_fe.get(j).getLabel() != null) {
                        edScore = LabelEditDistance.edTokensWithStemming(g1Vertices_fe .get(i).getLabel(),
                                g2Vertices_fe.get(j).getLabel(), Settings.STRING_DELIMETER, stemmer, true);
                    }

                    if (edScore < threshold) {
                        edScore = 0;
                    }
                    if (edScore == 0) {
                        nrZeros++;
                    }

                    costs[i][j] = (-1) * edScore;
                }
            }

            if (nrZeros != g1Vertices_fe.size() * g2Vertices_fe.size()) {
                for (int i = 0; i < costs.length; i++) {
                    System.arraycopy(costs[i], 0, costsCopy[i], 0, costs[0].length);
                }

                int[][] result = HungarianAlgorithm.computeAssignments(costsCopy);
                for (int[] aResult : result) {
                    double pairCost = (-1) * costs[aResult[0]][aResult[1]];
                    if (aResult[0] < g1Vertices_fe.size() && aResult[1] < g2Vertices_fe.size() && pairCost >= threshold &&
                            AssingmentProblem.canMap(g1Vertices_fe.get(aResult[0]), g2Vertices_fe.get(aResult[1]))) {
                        solutionMappings.add(new NodePair(g1Vertices_fe.get(aResult[0]), g2Vertices_fe.get(aResult[1]), pairCost));
                    }
                }
            }
        }
        if (gateways > 0) {
            solutionMappings.addAll(getMappingsGateways(g1Vertices, g2Vertices, threshold, stemmer, gateways));
        }
        return solutionMappings;
    }

    public Set<NodePair> getMappingsNodesUsingNodeMapping(Canonical g1, Canonical g2, double threshold,
            double semanticThreshold) {
        Set<CPFNode> g1Vertices = g1.getNodes();
        Set<CPFNode> g2Vertices = g2.getNodes();
        Set<NodePair> solutionMappings = new HashSet<NodePair>();

        NodeSimilarity nodeSimilarity = new NodeSimilarity();

        if (g1Vertices.size() == 0 || g2Vertices.size() == 0) {
            return solutionMappings;
        }

        if (g1Vertices.size() > 0 && g2Vertices.size() > 0) {
            int dimFunc = g1Vertices.size() > g2Vertices.size() ? g1Vertices.size() : g2Vertices.size();
            double costs[][] = new double[dimFunc][dimFunc];
            double costsCopy[][] = new double[dimFunc][dimFunc];
            int nrZeros = 0;

            CPFNode node1;
            CPFNode node2;

            // function mapping score
            for (int i = 0; i < g1Vertices.size(); i++) {
                for (int j = 0; j < g2Vertices.size(); j++) {
                    node1 = findNode(g1Vertices, i);
                    node2 = findNode(g2Vertices, j);
                    double edScore = nodeSimilarity.findNodeSimilarity(node1, node2, threshold);
                    if (GraphUtil.isGatewayNode(node1) && GraphUtil.isGatewayNode(node2) && edScore < semanticThreshold) {
                        edScore = 0;
                    } else if (!(GraphUtil.isGatewayNode(node1) && GraphUtil.isGatewayNode(node2)) && edScore < threshold) {
                        edScore = 0;
                    }

                    if (edScore == 0) {
                        nrZeros++;
                    }
                    costs[i][j] = (-1) * edScore;
                }
            }

            if (nrZeros != g1Vertices.size() * g2Vertices.size()) {
                for (int i = 0; i < costs.length; i++) {
                    System.arraycopy(costs[i], 0, costsCopy[i], 0, costs[0].length);
                }

                int[][] result = HungarianAlgorithm.computeAssignments(costsCopy);
                for (int[] aResult : result) {
                    node1 = findNode(g1Vertices, aResult[0]);
                    node2 = findNode(g2Vertices, aResult[1]);
                    double pairCost = (-1) * costs[aResult[0]][aResult[1]];
                    if (aResult[0] < g1Vertices.size() && aResult[1] < g2Vertices.size() && pairCost > 0 &&
                            AssingmentProblem.canMap(node1, node2)) {
                        solutionMappings.add(new NodePair(node1, node2, pairCost));
                    }
                }
            }
        }
        return solutionMappings;
    }


    /**
     * Finds the match between gateways, the decision is made based on the match of gateway parents/children
     * match, if the parent/child is also a gateway, then the decision is done recursively
     *
     * @param g1Vertices  - graph g1 vertices that need to be matched with graph g1 vertices
     * @param g2Vertices  - graph g2 vertices
     * @param threshold   - if node similarity is >= than threshold then these nodes are considered to
     *                    be matched.
     * @param stemmer     - stemmer for wrord stemming, if == null, then english stemmer is used
     * @param lookParents - if == 0, then gateways are not matched, if == 1, then only parent are looked,
     *                    if == 2, then only children are looked
     * @return List of Vertex Pairs
     */
    public Set<NodePair> getMappingsGateways(Collection<CPFNode> g1Vertices, Collection<CPFNode> g2Vertices, double threshold,
            SnowballStemmer stemmer, int lookParents) {
        Set<CPFNode> g1Gateways = new HashSet<CPFNode>();
        Set<CPFNode> g2Gateways = new HashSet<CPFNode>();
        Set<NodePair> possibleMatches = new HashSet<NodePair>();

        for (CPFNode v : g1Vertices) {
            if (GraphUtil.isGatewayNode(v)) {
                g1Gateways.add(v);
            }
        }
        for (CPFNode v : g2Vertices) {
            if (GraphUtil.isGatewayNode(v)) {
                g2Gateways.add(v);
            }
        }

        if (g1Gateways.size() == 0 || g2Gateways.size() == 0) {
            return possibleMatches;
        }

        int dimFunc = g1Gateways.size() > g2Gateways.size() ? g1Gateways.size() : g2Gateways.size();
        double costs[][] = new double[dimFunc][dimFunc];
        double costsCopy[][] = new double[dimFunc][dimFunc];
        CPFNode node1;
        CPFNode node2;
        Set<NodePair> map;

        for (int i = 0; i < g1Gateways.size(); i++) {
            for (int j = 0; j < g2Gateways.size(); j++) {
                double edScore = 0;
                node1 = findNode(g1Gateways, i);
                node2 = findNode(g2Gateways, j);
                if (lookParents == 2) {
                    map = getMappingsNodes(node1.getGraph().getAllPredecessors(node1), node2.getGraph().getAllPredecessors(node2),
                            threshold, stemmer, lookParents);
                    for (NodePair vp : map) {
                        edScore += vp.getWeight();
                    }

                    edScore = map.size() == 0 ? 0 : edScore / map.size();
                } else if (lookParents == 1) {
                    map = getMappingsNodes(node1.getGraph().getAllSuccessors(node1), node2.getGraph().getAllSuccessors(node2),
                            threshold, stemmer, lookParents);
                    for (NodePair vp : map) {
                        edScore += vp.getWeight();
                    }

                    edScore = map.size() == 0 ? 0 : edScore / map.size();
                }

                if (edScore < threshold) {
                    edScore = 0;
                }

                costs[i][j] = (-1) * edScore;
            }
        }

        for (int i = 0; i < costs.length; i++) {
            System.arraycopy(costs[i], 0, costsCopy[i], 0, costs[0].length);
        }

        int[][] result = HungarianAlgorithm.computeAssignments(costsCopy);
        for (int[] aResult : result) {
            double pairCost = (-1) * costs[aResult[0]][aResult[1]];
            if (aResult[0] < g1Gateways.size() && aResult[1] < g2Gateways.size() && pairCost > 0) {
                node1 = findNode(g1Gateways, aResult[0]);
                node2 = findNode(g2Gateways, aResult[1]);
                possibleMatches.add(new NodePair(node1, node2, pairCost));
            }
        }
        return possibleMatches;
    }



    private CPFNode findNode(Set<CPFNode> nodes, int index) {
        int count = 0;
        CPFNode result = null;
        for (CPFNode node : nodes) {
            count++;
            if (count == index) {
                result = node;
                break;
            }
        }
        return result;
    }

}
