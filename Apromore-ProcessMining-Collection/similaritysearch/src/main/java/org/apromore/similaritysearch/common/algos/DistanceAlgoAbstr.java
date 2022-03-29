/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2013 - 2016 Technical University of Eindhoven, University of Tartu, Reina Uba.
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.similaritysearch.common.algos;


import org.apromore.similaritysearch.graph.Graph;
import org.apromore.similaritysearch.graph.Vertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class DistanceAlgoAbstr implements DistanceAlgo {

    public final static int EPSILON = -1; //means: 'no mapping'
    public final static double VERTEX_INSERTION_COST = 0.1; //Only for reproducing Luciano's results
    public final static double VERTEX_DELETION_COST = 0.9; //Only for reproducing Luciano's results

    protected Graph sg1;
    protected Graph sg2;
    protected int totalNrVertices;
    protected int totalNrEdges;

    double weightGroupedVertex;
    double weightSkippedVertex;
    double weightSkippedEdge;
    double weightSubstitutedVertex;
    double ledcutoff;
    double cedcutoff;
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
        this.cedcutoff = 0.0;
        this.usepuredistance = false;
        this.prunewhen = 0;
        this.pruneto = 0;
        this.useepsilon = false;
        this.dogrouping = false;

        for (int i = 0; i < weights.length; i = i + 2) {
            String wname = (String) weights[i];
            Double wvalue = (Double) weights[i + 1];
            if (wname.equals("vweight")) {
                this.weightSkippedVertex = wvalue;
            } else if (wname.equals("sweight")) {
                this.weightSubstitutedVertex = wvalue;
            } else if (wname.equals("gweight")) {
                this.weightGroupedVertex = wvalue;
            } else if (wname.equals("eweight")) {
                this.weightSkippedEdge = wvalue;
            } else if (wname.equals("ledcutoff")) {
                this.ledcutoff = wvalue;
            } else if (wname.equals("cedcutoff")) {
                this.cedcutoff = wvalue;
            } else if (wname.equals("usepuredistance")) {
                if (wvalue == 0.0) {
                    this.usepuredistance = false;
                } else {
                    this.usepuredistance = true;
                }
            } else if (wname.equals("useepsilon")) {
                if (wvalue == 0.0) {
                    this.useepsilon = false;
                } else {
                    this.useepsilon = true;
                }
            } else if (wname.equals("dogrouping")) {
                if (wvalue == 0.0) {
                    this.dogrouping = false;
                } else {
                    this.dogrouping = true;
                }
            } else if (wname.equals("prunewhen")) {
                this.prunewhen = wvalue.intValue();
            } else if (wname.equals("pruneto")) {
                this.pruneto = wvalue.intValue();
            } else {
                System.err.println("ERROR: Invalid weight identifier: " + wname);
            }
        }
    }

    protected void init(Graph sg1, Graph sg2) {
        this.sg1 = sg1;
        this.sg2 = sg2;
        totalNrVertices = sg1.getVertices().size() + sg2.getVertices().size();
        totalNrEdges = sg1.getEdges().size() + sg2.getEdges().size();
    }

    protected double computeScore(double skippedEdges, double skippedVertices, double substitutedVertices, double insertedVertices, double deletedVertices) {
        if (usepuredistance) {
            if (useepsilon) {
                return weightSkippedVertex * (VERTEX_DELETION_COST * deletedVertices + VERTEX_INSERTION_COST * insertedVertices) + weightSkippedEdge * skippedEdges + weightSubstitutedVertex * 2.0 * substitutedVertices;
            } else {
                return weightSkippedVertex * skippedVertices + weightSkippedEdge * skippedEdges + weightSubstitutedVertex * 2.0 * substitutedVertices;
            }
        } else {
            //Return the total edit distance. Multiply each element with its weight.
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
    }

    protected double editDistance(BestMapping bestMapping, TwoVertices addedPair) {
        //Substituted vertices are vertices that >are< mapped.
        //Their distance is 1.0 - string-edit similarity of their labels.
        double substitutedVertices = bestMapping.substitutedVerticesCost + addedPair.weight;

        int addedbyMapping = bestMapping.nrMappedEdges + findNrVerticesByPair(bestMapping, addedPair);

        double skippedEdges = sg1.getEdges().size() + sg2.getEdges().size() - (2 * addedbyMapping);
        double skippedVertices = sg1.getVertices().size() + sg2.getVertices().size() - (2 * (bestMapping.size() + 1));

        return computeScore(skippedEdges, skippedVertices, substitutedVertices, 0.0, 0.0);
    }

    private int findNrVerticesByPair(BestMapping bestMapping,
                                     TwoVertices addedPair) {

        int addedbyMapping = 0;
        // find how many matched edges the new mapping will add
        Vertex left = sg1.getVertexMap().get(addedPair.v1);
        Vertex right = sg2.getVertexMap().get(addedPair.v2);
        if (bestMapping.size() > 0) { // best mapping contains some vertices already
            for (Vertex p : left.getParents()) {
                String mappingRight = bestMapping.mappingRight.get(p.getID());
                // the parent is also mapped and is parent of mapped node
                if (mappingRight != null
                        && right.getParents().contains(sg2.getVertexMap().get(mappingRight))) {
                    addedbyMapping++;
                }
            }
            for (Vertex ch : left.getChildren()) {
                String mappingRight = bestMapping.mappingRight.get(ch.getID());
                // the parent is also mapped and is parent of mapped node
                if (mappingRight != null
                        && right.getChildren().contains(sg2.getVertexMap().get(mappingRight))) {
                    addedbyMapping++;
                }
            }
        }
        return addedbyMapping;
    }

    public class BestMapping {
        public Set<TwoVertices> mapping = new HashSet<TwoVertices>();
        HashMap<String, String> mappingRight = new HashMap<String, String>();
        double substitutedVerticesCost = 0;
        int nrMappedEdges = 0;

        public void addPair(TwoVertices pair) {
            mappingRight.put(pair.v1, pair.v2);
            mapping.add(pair);
            substitutedVerticesCost += pair.weight;
            nrMappedEdges += findNrVerticesByPair(this, pair);
        }

        public Set<TwoVertices> getMapping() {
            return mapping;
        }

        public int size() {
            return mapping.size();
        }
    }
}
