package org.apromore.toolbox.clustering.dissimilarity.algorithm;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeMultiset;
import nl.tue.tm.is.led.StringEditDistance;
import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;
import org.apromore.toolbox.clustering.dissimilarity.model.TwoVertices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that implements the algorithm to compute the edit distance between two
 * SimpleGraph instances. Use the algorithm by calling the constructor with the two
 * SimpleGraph instances between which you want to compute the edit distance. Then call
 * compute(), which will return the edit distance.
 */
public class SimpleGEDDeterministicGreedy extends AbstractSimpleDistanceAlgorithm implements SimpleDistanceAlgorithm {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleGEDDeterministicGreedy.class);

    boolean deterministic = true;

    public void resetDeterminismFlag() {
        deterministic = true;
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    private Set<TwoVertices> times(Set<Integer> a, Set<Integer> b) {
        Set<TwoVertices> result = new HashSet<>();
        for (Integer ea : a) {
            for (Integer eb : b) {
                if (StringEditDistance.similarity(sg1.getLabel(ea), sg2.getLabel(eb)) >= this.ledcutoff) {
                    result.add(new TwoVertices(ea, eb));
                }
            }
        }
        return result;
    }

    public double compute(SimpleGraph sg1, SimpleGraph sg2) {
        init(sg1, sg2);

        TwoVertices couple;
        Vector<TwoVertices> bestCandidates;
        Set<TwoVertices> newMapping;
        Set<TwoVertices> newOpenCouples;
        Set<TwoVertices> mapping = new HashSet<>();
        Set<TwoVertices> openCouples = times(sg1.getVertices(), sg2.getVertices());

        String tmp, label1, label2, contextkey, firstkey;
        double newEditDistance;
        double newShortestEditDistance;
        double shortestEditDistance = Double.MAX_VALUE;
        Random randomized = new Random();

        TreeMultiset<String> mset;
        TreeMultimap<String, TwoVertices> tmap;
        TreeMultimap<String, TwoVertices> tmapp;

        boolean doStep = true;
        while (doStep) {
            doStep = false;
            bestCandidates = new Vector<>();
            newShortestEditDistance = shortestEditDistance;

            for (TwoVertices oCouple : openCouples) {
                newMapping = new HashSet<>(mapping);
                newMapping.add(oCouple);
                newEditDistance = this.editDistance(newMapping);
                if (newEditDistance < newShortestEditDistance) {
                    bestCandidates = new Vector<>();
                    bestCandidates.add(oCouple);
                    newShortestEditDistance = newEditDistance;
                } else if (newEditDistance == newShortestEditDistance) {
                    bestCandidates.add(oCouple);
                }
            }

            if (bestCandidates.size() > 0) {
                if (bestCandidates.size() == 1)
                    couple = bestCandidates.firstElement();
                else {
                    tmap = TreeMultimap.create();
                    for (TwoVertices pair : bestCandidates) {
                        label1 = sg1.getLabel(pair.v1);
                        label2 = sg2.getLabel(pair.v2);
                        if (label1.compareTo(label2) > 0) {
                            tmp = label1;
                            label1 = label2;
                            label2 = tmp;
                        }
                        tmap.put(label1 + label2, pair);
                    }
                    firstkey = tmap.keySet().first();

                    if (tmap.get(firstkey).size() == 1)
                        couple = tmap.get(firstkey).first();
                    else if (tmap.get(firstkey).size() > 1) {
                        Set<TwoVertices> set = tmap.get(firstkey);
                        tmapp = TreeMultimap.create();

                        mset = TreeMultiset.create();
                        for (TwoVertices pair : set) {
                            label1 = sg1.getLabel(pair.v1);
                            mset.clear();
                            for (Integer n : sg1.preSet(pair.v1))
                                mset.add(sg1.getLabel(n));
                            label1 += mset.toString();
                            mset.clear();
                            for (Integer n : sg1.postSet(pair.v1))
                                mset.add(sg1.getLabel(n));
                            label1 += mset.toString();

                            label2 = sg2.getLabel(pair.v2);
                            mset.clear();
                            for (Integer n : sg2.preSet(pair.v2))
                                mset.add(sg2.getLabel(n));
                            label2 += mset.toString();
                            mset.clear();
                            for (Integer n : sg2.postSet(pair.v2))
                                mset.add(sg2.getLabel(n));
                            label2 += mset.toString();

                            if (label1.compareTo(label2) > 0) {
                                tmp = label1;
                                label1 = label2;
                                label2 = tmp;
                            }
                            tmapp.put(label1 + label2, pair);
                        }
                        contextkey = tmapp.keySet().first();

                        if (tmapp.get(contextkey).size() == 1)
                            couple = tmapp.get(contextkey).first();
                        else {
                            deterministic = false;
                            couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));
                        }
                    } else {
                        System.out.println("oops ...");
                        deterministic = false;
                        couple = bestCandidates.get(randomized.nextInt(bestCandidates.size()));
                    }

                }

                newOpenCouples = new HashSet<>();
                for (TwoVertices p : openCouples) {
                    if (!p.v1.equals(couple.v1) && !p.v2.equals(couple.v2)) {
                        newOpenCouples.add(p);
                    }
                }
                openCouples = newOpenCouples;

                mapping.add(couple);
                shortestEditDistance = newShortestEditDistance;
                doStep = true;
            }
        }

        return shortestEditDistance;
    }
}
