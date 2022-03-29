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

import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeMultiset;
import org.apromore.similaritysearch.common.similarity.AssingmentProblem;
import org.apromore.similaritysearch.common.similarity.NodeSimilarity;
import org.apromore.similaritysearch.graph.Graph;
import org.apromore.similaritysearch.graph.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Class that implements the algorithm to compute the edit distance between two
 * SimpleGraph instances. Use the algorithm by calling the constructor with the two
 * SimpleGraph instances between which you want to compute the edit distance. Then call
 * compute(), which will return the edit distance.
 */
public class GraphEditDistanceGreedy extends DistanceAlgoAbstr implements DistanceAlgo {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphEditDistanceGreedy.class);

    public int nrSubstitudedVertices = 0;
    private boolean deterministic = true;


    private Set<TwoVertices> times(List<Vertex> a, List<Vertex> b, double labelTreshold) {
        Set<TwoVertices> result = new HashSet<TwoVertices>();
        for (Vertex ea : a) {
            for (Vertex eb : b) {
                double similarity = NodeSimilarity.findNodeSimilarity(ea, eb, labelTreshold);
                if (ea.getType().equals(Vertex.Type.gateway) && eb.getType().equals(Vertex.Type.gateway)
                        && similarity >= cedcutoff) {
                    result.add(new TwoVertices(ea.getID(), eb.getID(), 1 - similarity));
                } else if (((ea.getType().equals(Vertex.Type.event) && eb.getType().equals(Vertex.Type.event))
                        || (ea.getType().equals(Vertex.Type.function) && eb.getType().equals(Vertex.Type.function))
                        || (ea.getType().equals(Vertex.Type.state) && eb.getType().equals(Vertex.Type.state))
                        || (ea.getType().equals(Vertex.Type.node) && eb.getType().equals(Vertex.Type.node)))
                        && AssingmentProblem.canMap(ea, eb) && similarity >= ledcutoff) {
                    result.add(new TwoVertices(ea.getID(), eb.getID(), 1 - similarity));
                }
            }
        }
        return result;
    }

    public Set<TwoVertices> compute(Graph sg1, Graph sg2) {
        init(sg1, sg2);

        //INIT
        BestMapping mapping = new BestMapping();
        Set<TwoVertices> openCouples = times(sg1.getVertices(), sg2.getVertices(), ledcutoff);
        double shortestEditDistance = Double.MAX_VALUE;
        Random randomized = new Random(123456789);
        int stepn = 0;
        //STEP
        boolean doStep = true;
        while (doStep) {
            doStep = false;
            stepn++;
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
                TwoVertices couple;

                // Case 1: Only one candidate pair
                if (bestCandidates.size() == 1) {
                    couple = bestCandidates.firstElement();
                } else {
                    //  CASE 2: Lexicographical order is enough
                    TreeMultimap<String, TwoVertices> tmap = TreeMultimap.create();
                    for (TwoVertices pair: bestCandidates) {
                        String label1 = sg1.getVertexLabel(pair.v1);
                        String label2 = sg2.getVertexLabel(pair.v2);
                        if (label1 != null && label2 != null && label1.compareTo(label2) > 0) {
                            String tmp = label1;
                            label1 = label2;
                            label2 = tmp;
                        }
                        tmap.put(label1+label2, pair);
                    }
                    String firstkey = tmap.keySet().first();

                    if (tmap.get(firstkey).size() == 1) {
                        couple = tmap.get(firstkey).first();
                    } else if (tmap.get(firstkey).size() > 1) {
                        Set<TwoVertices> set = tmap.get(firstkey);
                        TreeMultimap<String, TwoVertices> tmapp = TreeMultimap.create();

                        String label1;
                        String tmpLabel;
                        TreeMultiset<String> mset = TreeMultiset.create();
                        for (TwoVertices pair: set) {
                            label1 = sg1.getVertexLabel(pair.v1);
                            mset.clear();
                            for (Vertex n: sg1.getPreset(pair.v1)) {
                                tmpLabel = sg1.getVertexLabel(n.getID());
                                if (tmpLabel != null) {
                                    mset.add(tmpLabel);
                                }
                            }
                            label1 += mset.toString();
                            mset.clear();
                            for (Vertex n: sg1.getPostset(pair.v1)) {
                                tmpLabel = sg1.getVertexLabel(n.getID());
                                if (tmpLabel != null) {
                                    mset.add(tmpLabel);
                                }
                            }
                            label1 += mset.toString();

                            String label2 = sg2.getVertexLabel(pair.v2);
                            mset.clear();
                            for (Vertex n: sg2.getPreset(pair.v2)) {
                                tmpLabel = sg2.getVertexLabel(n.getID());
                                if (tmpLabel != null) {
                                    mset.add(tmpLabel);
                                }
                            }
                            label2 += mset.toString();
                            mset.clear();
                            for (Vertex n: sg2.getPostset(pair.v2)) {
                                tmpLabel = sg2.getVertexLabel(n.getID());
                                if (tmpLabel != null) {
                                    mset.add(tmpLabel);
                                }
                            }
                            label2 += mset.toString();

                            if (label1.compareTo(label2) > 0) {
                                String tmp = label1;
                                label1 = label2;
                                label2 = tmp;
                            }
                            tmapp.put(label1+label2, pair);
                        }
                        String contextkey = tmapp.keySet().first();
                        // CASE 3: Composite labels (concatenation of labels of nodes surrounding the target vertex)
                        if (tmapp.get(contextkey).size() == 1) {
                            couple = tmapp.get(contextkey).first();
                        } else {
                            // CASE 4: Non deterministic choice (Choose a random candidate)
                            deterministic = false;
                            couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));
                        }
                    } else {
                        // CASE 5: Non deterministic choice (Choose a random candidate)
//                        System.out.println("oops ...");
                        deterministic = false;
                        couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));
                    }
                }

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


    public double computeGED(Graph sg1, Graph sg2) {
        BestMapping mapping = new BestMapping();
        double shortestEditDistance = Double.MAX_VALUE;
        Random randomized = new Random(123456789);

        try {
            // INIT
            init(sg1, sg2);
            Set<TwoVertices> openCouples = times(sg1.getVertices(), sg2.getVertices(), ledcutoff);

            // STEP
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
                    TwoVertices couple;

                    // Case 1: Only one candidate pair
                    if (bestCandidates.size() == 1) {
                        couple = bestCandidates.firstElement();
                    } else {
                        //  CASE 2: Lexicographical order is enough
                        TreeMultimap<String, TwoVertices> tmap = TreeMultimap.create();
                        for (TwoVertices pair: bestCandidates) {
                            String label1 = sg1.getVertexLabel(pair.v1);
                            String label2 = sg2.getVertexLabel(pair.v2);
                            if (label1 != null && label2 != null && label1.compareTo(label2) > 0) {
                                String tmp = label1;
                                label1 = label2;
                                label2 = tmp;
                            }
                            tmap.put(label1+label2, pair);
                        }
                        String firstkey = tmap.keySet().first();

                        if (tmap.get(firstkey).size() == 1) {
                            couple = tmap.get(firstkey).first();
                        } else if (tmap.get(firstkey).size() > 1) {
                            Set<TwoVertices> set = tmap.get(firstkey);
                            TreeMultimap<String, TwoVertices> tmapp = TreeMultimap.create();

                            String label1;
                            String tmpLabel;
                            TreeMultiset<String> mset = TreeMultiset.create();
                            for (TwoVertices pair: set) {
                                label1 = sg1.getVertexLabel(pair.v1);
                                mset.clear();
                                for (Vertex n: sg1.getPreset(pair.v1)) {
                                    tmpLabel = sg1.getVertexLabel(n.getID());
                                    if (tmpLabel != null) {
                                        mset.add(tmpLabel);
                                    }
                                }
                                label1 += mset.toString();
                                mset.clear();
                                for (Vertex n: sg1.getPostset(pair.v1)) {
                                    tmpLabel = sg1.getVertexLabel(n.getID());
                                    if (tmpLabel != null) {
                                        mset.add(tmpLabel);
                                    }
                                }
                                label1 += mset.toString();

                                String label2 = sg2.getVertexLabel(pair.v2);
                                mset.clear();
                                for (Vertex n: sg2.getPreset(pair.v2)) {
                                    tmpLabel = sg2.getVertexLabel(n.getID());
                                    if (tmpLabel != null) {
                                        mset.add(tmpLabel);
                                    }
                                }
                                label2 += mset.toString();
                                mset.clear();
                                for (Vertex n: sg2.getPostset(pair.v2)) {
                                    tmpLabel = sg2.getVertexLabel(n.getID());
                                    if (tmpLabel != null) {
                                        mset.add(tmpLabel);
                                    }
                                }
                                label2 += mset.toString();

                                if (label1.compareTo(label2) > 0) {
                                    String tmp = label1;
                                    label1 = label2;
                                    label2 = tmp;
                                }
                                tmapp.put(label1+label2, pair);
                            }
                            String contextkey = tmapp.keySet().first();
                            // CASE 3: Composite labels (concatenation of labels of nodes surrounding the target vertex)
                            if (tmapp.get(contextkey).size() == 1) {
                                couple = tmapp.get(contextkey).first();
                            } else {
                                // CASE 4: Non deterministic choice (Choose a random candidate)
                                deterministic = false;
                                couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));
                            }
                        } else {
                            // CASE 5: Non deterministic choice (Choose a random candidate)
//                            System.out.println("oops ...");
                            deterministic = false;
                            couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));
                        }
                    }

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
        } catch (Exception e) {
            LOGGER.error("Error occured while processing Distance Greedy Similarity Search ", e);
        }

        // Return the smallest edit distance
        return shortestEditDistance;
    }



    public void resetDeterminismFlag() {
        deterministic = true;
    }
    public boolean isDeterministic() {
        return deterministic;
    }

}
