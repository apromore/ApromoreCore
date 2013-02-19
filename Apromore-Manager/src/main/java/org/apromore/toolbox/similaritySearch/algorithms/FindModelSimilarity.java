package org.apromore.toolbox.similaritySearch.algorithms;

import java.util.LinkedList;

import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.similaritySearch.common.NodePair;
import org.apromore.toolbox.similaritySearch.common.algos.GraphEditDistanceGreedy;
import org.apromore.toolbox.similaritySearch.common.similarity.AssingmentProblem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FindModelSimilarity {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindModelSimilarity.class);

    public double findProcessSimilarity(Canonical g1, Canonical g2, String algortithm, double... param) {
        double weight = 0.0;
        AssingmentProblem assingmentProblem = new AssingmentProblem();

        if (algortithm.equals("Greedy")) {
            GraphEditDistanceGreedy gedepc = new GraphEditDistanceGreedy();
            Object weights[] = {"ledcutoff", param[0], "cedcutoff", param[1], "vweight", param[2], "sweight", param[3], "eweight", param[4]};
            gedepc.setWeight(weights);
            weight = gedepc.computeGED(g1, g2);
            return (1 - (weight < 0.0000001 ? 0 : (weight > 1 ? 1 : weight)));

        } else if (algortithm.equals("Hungarian")) {
            LinkedList<NodePair> mapping = assingmentProblem.getMappingsNodesUsingNodeMapping(g1, g2, param[0], param[1]);
            weight = 0.0;
            for (NodePair vp : mapping) {
                weight += vp.getWeight();
            }
            return (weight / Math.max(g1.getVertices().size(), g2.getVertices().size()));
        }

        return weight;
    }
}
