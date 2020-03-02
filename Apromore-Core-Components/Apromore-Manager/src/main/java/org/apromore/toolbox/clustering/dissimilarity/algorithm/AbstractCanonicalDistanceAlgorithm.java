/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2013 - 2017 Queensland University of Technology.
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.toolbox.clustering.dissimilarity.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.led.StringEditDistance;
import org.apromore.graph.canonical.CPFEdge;
import org.apromore.graph.canonical.CPFNode;
import org.apromore.graph.canonical.Canonical;
import org.apromore.toolbox.clustering.dissimilarity.model.GEDEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implmentation of the Distance Agorithm that uses the Canonical jBPT format.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public abstract class AbstractCanonicalDistanceAlgorithm implements CanonicalDistanceAlgorithm {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCanonicalDistanceAlgorithm.class);

    protected Canonical sg1;
    protected Canonical sg2;
    protected int totalNrNodes;
    protected int totalNrEdges;

    double weightGroupedVertex;
    double weightSkippedVertex;
    double weightSkippedEdge;
    double weightSubstitutedVertex;
    double ledcutoff;
    int prunewhen;
    int pruneto;


    /**
     * Sets the weights for:
     * - skipping vertices (vweight)
     * - substituting vertices (sweight)
     * - skipping edges (eweight)
     * - string edit similarity cutoff (ledcutoff)
     * - use pure edit distance/ use weighted average distance (usepuredistance)
     * Ad usepuredistance: weight is a boolean. If 1.0: uses the pure edit distance, if 0.0: uses weighted average of the fractions of skipped vertices, skipped edges and substitution score.
     * - prune when recursion reaches this depth, 0.0 means no pruning (prunewhen)
     * - prune to recursion of this depth (pruneto)
     * <p/>
     * The argument is an array of objects, interchangably a String ("vweight", "sweight", or "eweight")
     * and a 0.0 <= Double <= 1.0 that is the value that should be set for the given weight.
     * All other weights are set to 0.0.
     *
     * @param weights Pre: for i mod 2 = 0: weights[i] instanceof String /\ weights[i] \in {"vweight", "sweight", or "eweight"}
     *                for i mod 2 = 1: weights[i] instanceof Double /\ 0.0 <= weights[i] <= 1.0
     *                for i: if i < weights.length(), then i+1 < weights.length()
     *                Post: weight identified by weights[i] is set to weights[i+1]
     *                all other weights are set to 0.0
     */
    public void setWeight(Object weights[]) {
        this.weightGroupedVertex = 0.0;
        this.weightSkippedVertex = 0.0;
        this.weightSubstitutedVertex = 0.0;
        this.weightSkippedEdge = 0.0;
        this.ledcutoff = 0.0;
        this.prunewhen = 0;
        this.pruneto = 0;

        for (int i = 0; i < weights.length; i = i + 2) {
            String wname = (String) weights[i];
            Double wvalue = (Double) weights[i + 1];
            switch (wname) {
                case "vweight":
                    this.weightSkippedVertex = wvalue;
                    break;
                case "sweight":
                    this.weightSubstitutedVertex = wvalue;
                    break;
                case "gweight":
                    this.weightGroupedVertex = wvalue;
                    break;
                case "eweight":
                    this.weightSkippedEdge = wvalue;
                    break;
                case "ledcutoff":
                    this.ledcutoff = wvalue;
                    break;
                case "prunewhen":
                    this.prunewhen = wvalue.intValue();
                    break;
                case "pruneto":
                    this.pruneto = wvalue.intValue();
                    break;
                default:
                    System.err.println("ERROR: Invalid weight identifier: " + wname);
                    break;
            }
        }
    }


    protected void init(Canonical sg1, Canonical sg2) {
        this.sg1 = sg1;
        this.sg2 = sg2;
        totalNrNodes = sg1.getNodes().size() + sg2.getNodes().size();
        totalNrEdges = 0;
        for (CPFNode i : sg1.getNodes()) {
            totalNrEdges += sg1.getDirectSuccessors(i).size();
        }
        for (CPFNode i : sg2.getNodes()) {
            totalNrEdges += sg2.getDirectSuccessors(i).size();
        }
    }

    protected double editDistance(Set<GEDEdge> mappings) {
        String label1;
        String label2;
        double substitutionDistance;
        double substitutedNodes = 0.0;

        Set<CPFNode> nodesFrom1Used = new HashSet<>();
        Set<CPFNode> nodesFrom2Used = new HashSet<>();
        Map<CPFNode, CPFNode> node12node2 = new HashMap<>();
        Map<CPFNode, CPFNode> node22node1 = new HashMap<>();

        for (GEDEdge pair : mappings) {
            label1 = pair.getSource().getName().replace('\n', ' ').replace("\\n", " ");
            label2 = pair.getTarget().getName().replace('\n', ' ').replace("\\n", " ");
            nodesFrom1Used.add(pair.getSource());
            nodesFrom2Used.add(pair.getTarget());

            if (((label1.length() == 0) && (label2.length() != 0)) || ((label1.length() != 0) && (label2.length() == 0))) {
                substitutionDistance = Double.MAX_VALUE;
            } else {
                substitutionDistance = 1.0 - StringEditDistance.similarity(label1, label2);
            }
            substitutedNodes += substitutionDistance;

            node12node2.put(pair.getSource(), pair.getTarget());
            node22node1.put(pair.getTarget(), pair.getSource());
        }

        Set<CPFEdge> edgesIn1 = sg1.getEdges();
        edgesIn1.removeAll(getTranslatedEdgesInGraph(sg2, node22node1));

        Set<CPFEdge> edgesIn2 = sg2.getEdges();
        edgesIn2.removeAll(getTranslatedEdgesInGraph(sg1, node12node2));

        double skippedEdges = 1.0 * edgesIn1.size() + 1.0 * edgesIn2.size();
        double skippedNodes = totalNrNodes - nodesFrom1Used.size() - nodesFrom2Used.size();
        return computeScore(skippedEdges, skippedNodes, substitutedNodes);
    }


    protected double computeScore(double skippedEdges, double skippedNodes, double substitutedNodes) {
        double vskip = skippedNodes / (1.0 * totalNrNodes);
        double vsubs = (2.0 * substitutedNodes) / (1.0 * totalNrNodes - skippedNodes);
        double editDistance;
        if (totalNrEdges == 0) {
            editDistance = ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs)) / (weightSkippedVertex + weightSubstitutedVertex);
        } else {
            double eskip = (skippedEdges / (1.0 * totalNrEdges));
            editDistance = ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs) + (weightSkippedEdge * eskip)) / (weightSkippedVertex + weightSubstitutedVertex + weightSkippedEdge);
        }
        return editDistance;
    }


    private Set<GEDEdge> getTranslatedEdgesInGraph(Canonical graph, Map<CPFNode, CPFNode> nodesMapping) {
        CPFNode srcMap;
        CPFNode tgtMap;
        Set<GEDEdge> translatedEdgesIn1 = new HashSet<>();
        for (CPFNode i : graph.getNodes()) {
            for (CPFNode j : graph.getDirectSuccessors(i)) {
                srcMap = nodesMapping.get(i);
                tgtMap = nodesMapping.get(j);
                if ((srcMap != null) && (tgtMap != null)) {
                    translatedEdgesIn1.add(new GEDEdge(srcMap, tgtMap));
                }
            }
        }
        return translatedEdgesIn1;
    }
}
