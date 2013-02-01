package org.apromore.toolbox.similaritySearch.common.algos;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.similaritySearch.common.similarity.AssingmentProblem;
import org.apromore.toolbox.similaritySearch.common.similarity.NodeSimilarity;
import org.apromore.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that implements the algorithm to compute the edit distance between two
 * SimpleGraph instances. Use the algorithm by calling the constructor with the two
 * SimpleGraph instances between which you want to compute the edit distance. Then call
 * compute(), which will return the edit distance.
 */
public class GraphEditDistanceGreedy extends DistanceAlgoAbstr implements DistanceAlgo {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphEditDistanceGreedy.class);

    public int nrSubstitudedVertices = 0;


    public Set<TwoVertices> compute(Canonical sg1, Canonical sg2) {
        init(sg1, sg2);

        // INIT
        BestMapping mapping = new BestMapping();
        Set<TwoVertices> openCouples = times(sg1, sg2, ledcutoff);
        double shortestEditDistance = Double.MAX_VALUE;
        Random randomized = new Random();

        // STEP
        boolean doStep = true;
        while (doStep) {
            doStep = false;
            Vector<TwoVertices> bestCandidates = new Vector<TwoVertices>();
            double newShortestEditDistance = shortestEditDistance;
            for (TwoVertices couple : openCouples) {
                double newEditDistance = editDistance(mapping, couple);
                if (newEditDistance < newShortestEditDistance) {
                    bestCandidates = new Vector<TwoVertices>();
                    bestCandidates.add(couple);
                    newShortestEditDistance = newEditDistance;
                } else if (newEditDistance == newShortestEditDistance) {
                    bestCandidates.add(couple);
                }
            }

            if (bestCandidates.size() > 0) {
                // Choose a random candidate
                TwoVertices couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));

                Set<TwoVertices> newOpenCouples = new HashSet<TwoVertices>();
                for (TwoVertices p : openCouples) {
                    if (!p.v1.equals(couple.v1) && !p.v2.equals(couple.v2)) {
                        newOpenCouples.add(p);
                    }
                }
                openCouples = newOpenCouples;

                mapping.addPair(couple);
                shortestEditDistance = newShortestEditDistance;
                doStep = true;
            }
        }

        //Return the smallest edit distance
        return mapping.mapping;
    }

    public double computeGED(Canonical sg1, Canonical sg2) {
        init(sg1, sg2);

        //INIT
        BestMapping mapping = new BestMapping();
        Set<TwoVertices> openCouples = times(sg1, sg2, ledcutoff);
        double shortestEditDistance = Double.MAX_VALUE;
        Random randomized = new Random();

        //STEP
        boolean doStep = true;
        while (doStep) {
            doStep = false;
            Vector<TwoVertices> bestCandidates = new Vector<TwoVertices>();
            double newShortestEditDistance = shortestEditDistance;
            for (TwoVertices couple : openCouples) {
                double newEditDistance = this.editDistance(mapping, couple);

                if (newEditDistance < newShortestEditDistance) {
                    bestCandidates = new Vector<TwoVertices>();
                    bestCandidates.add(couple);
                    newShortestEditDistance = newEditDistance;
                } else if (newEditDistance == newShortestEditDistance) {
                    bestCandidates.add(couple);
                }
            }

            if (bestCandidates.size() > 0) {
                //Choose a random candidate
                TwoVertices couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));

                Set<TwoVertices> newOpenCouples = new HashSet<TwoVertices>();
                for (TwoVertices p : openCouples) {
                    if (!p.v1.equals(couple.v1) && !p.v2.equals(couple.v2)) {
                        newOpenCouples.add(p);
                    }
                }
                openCouples = newOpenCouples;
                mapping.addPair(couple);
                shortestEditDistance = newShortestEditDistance;
                doStep = true;
            }
        }

        nrSubstitudedVertices = mapping.size();

        // Return the smallest edit distance
        return shortestEditDistance;
    }


    private Set<TwoVertices> times(Canonical a, Canonical b, double labelTreshold) {
        NodeSimilarity nodeSimilarity = new NodeSimilarity();
        Set<TwoVertices> result = new HashSet<TwoVertices>();
        for (CPFNode ea : a.getNodes()) {
            for (CPFNode eb : b.getNodes()) {
                double similarity = nodeSimilarity.findNodeSimilarity(ea, eb, labelTreshold);

                if (GraphUtil.isGatewayNode(ea) && GraphUtil.isGatewayNode(eb)) {
                    result.add(new TwoVertices(ea.getId(), eb.getId(), 1 - similarity));
                } else if (GraphUtil.isWorkNode(ea) && GraphUtil.isWorkNode(eb) && AssingmentProblem.canMap(ea, eb) && similarity >= ledcutoff) {
                    result.add(new TwoVertices(ea.getId(), eb.getId(), 1 - similarity));
                }
            }
        }
        return result;
    }


}
