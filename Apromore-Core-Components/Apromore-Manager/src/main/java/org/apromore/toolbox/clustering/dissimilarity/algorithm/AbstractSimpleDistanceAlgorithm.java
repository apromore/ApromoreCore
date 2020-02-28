/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.led.StringEditDistance;
import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;
import org.apromore.toolbox.clustering.dissimilarity.model.TwoVertices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimpleDistanceAlgorithm implements SimpleDistanceAlgorithm {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimpleDistanceAlgorithm.class);

    protected SimpleGraph sg1;
    protected SimpleGraph sg2;
    protected int totalNrVertices;
    protected int totalNrEdges;

    double weightGroupedVertex;
    double weightSkippedVertex;
    double weightSkippedEdge;
    double weightSubstitutedVertex;
    double ledcutoff;
    boolean usepuredistance;
    int prunewhen;
    int pruneto;
    boolean useepsilon;
    boolean dogrouping;

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
        this.usepuredistance = true;
        this.prunewhen = 0;
        this.pruneto = 0;
        this.useepsilon = false;
        this.dogrouping = false;

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
                case "usepuredistance":
                    this.usepuredistance = wvalue != 0.0;
                    break;
                case "useepsilon":
                    this.useepsilon = wvalue != 0.0;
                    break;
                case "dogrouping":
                    this.dogrouping = wvalue != 0.0;
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


    protected void init(SimpleGraph sg1, SimpleGraph sg2) {
        this.sg1 = sg1;
        this.sg2 = sg2;
        totalNrVertices = sg1.getVertices().size() + sg2.getVertices().size();
        totalNrEdges = 0;
        for (Integer i : sg1.getVertices()) {
            totalNrEdges += sg1.postSet(i).size();
        }
        for (Integer i : sg2.getVertices()) {
            totalNrEdges += sg2.postSet(i).size();
        }
    }

    protected double computeScore(double skippedEdges, double skippedVertices, double substitutedVertices) {
        double vskip = skippedVertices / (1.0 * totalNrVertices);
        double vsubs = (2.0 * substitutedVertices) / (1.0 * totalNrVertices - skippedVertices);
        double editDistance;
        if (totalNrEdges == 0) {
            editDistance = ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs)) / (weightSkippedVertex + weightSubstitutedVertex);
        } else {
            double eskip = (skippedEdges / (1.0 * totalNrEdges));
            editDistance = ((weightSkippedVertex * vskip) + (weightSubstitutedVertex * vsubs) + (weightSkippedEdge * eskip)) / (weightSkippedVertex + weightSubstitutedVertex + weightSkippedEdge);
        }
        return editDistance;
    }

    protected double editDistance(Set<TwoVertices> m) {
        Set<Integer> verticesFrom1Used = new HashSet<>();
        Set<Integer> verticesFrom2Used = new HashSet<>();
        Map<Integer, Integer> vid1tovid2 = new HashMap<>();
        Map<Integer, Integer> vid2tovid1 = new HashMap<>();

        double substitutedVertices = 0.0;
        for (TwoVertices pair : m) {
            double substitutionDistance;
            String label1 = sg1.getLabel(pair.v1);
            String label2 = sg2.getLabel(pair.v2);
            verticesFrom1Used.add(pair.v1);
            verticesFrom2Used.add(pair.v2);

            if (((label1.length() == 0) && (label2.length() != 0)) || ((label1.length() != 0) && (label2.length() == 0))) {
                substitutionDistance = Double.MAX_VALUE;
            } else {
                substitutionDistance = 1.0 - StringEditDistance.similarity(label1, label2);
            }
            substitutedVertices += substitutionDistance;

            vid1tovid2.put(pair.v1, pair.v2);
            vid2tovid1.put(pair.v2, pair.v1);
        }

        Collection<TwoVertices> edgesIn1 = sg1.getEdgesAsCollection();
        Collection<TwoVertices> edgesIn2 = sg2.getEdgesAsCollection();
        Set<TwoVertices> translatedEdgesIn1 = new HashSet<>();
        for (Integer i : sg1.getVertices()) {
            for (Integer j : sg1.postSet(i)) {
                Integer srcMap = vid1tovid2.get(i);
                Integer tgtMap = vid1tovid2.get(j);
                if ((srcMap != null) && (tgtMap != null)) {
                    translatedEdgesIn1.add(new TwoVertices(srcMap, tgtMap));
                }
            }
        }
        edgesIn2.removeAll(translatedEdgesIn1);
        Set<TwoVertices> translatedEdgesIn2 = new HashSet<>();
        for (Integer i : sg2.getVertices()) {
            for (Integer j : sg2.postSet(i)) {
                Integer srcMap = vid2tovid1.get(i);
                Integer tgtMap = vid2tovid1.get(j);
                if ((srcMap != null) && (tgtMap != null)) {
                    translatedEdgesIn2.add(new TwoVertices(srcMap, tgtMap));
                }
            }
        }
        edgesIn1.removeAll(translatedEdgesIn2);
        double skippedEdges = 1.0 * edgesIn1.size() + 1.0 * edgesIn2.size();
        double skippedVertices = sg1.getVertices().size() + sg2.getVertices().size() - verticesFrom1Used.size() - verticesFrom2Used.size();

        return computeScore(skippedEdges, skippedVertices, substitutedVertices);
    }
}
