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

package org.apromore.toolbox.clustering.tasksim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.tue.tm.is.led.StringEditDistance;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityCalc;
import org.apromore.toolbox.clustering.dissimilarity.model.SimpleGraph;

public class TaskDissimCalc implements DissimilarityCalc {

    private double maxDistance = 0.4;
    private double minSim = 0.6;
    private double ledcutoff = 0.5;
    private boolean includeGateways = false;

    private List<SimilarPair> similarPairs = new ArrayList<SimilarPair>();
    private List<SimilarPair> mappedPairs = new ArrayList<SimilarPair>();
    private Map<Integer, List<SimilarPair>> sg1ContainedPairs = new HashMap<Integer, List<SimilarPair>>();
    private Map<Integer, List<SimilarPair>> sg2ContainedPairs = new HashMap<Integer, List<SimilarPair>>();

    private Set<Integer> sg1MappableNodes = new HashSet<Integer>();
    private Set<Integer> sg2MappableNodes = new HashSet<Integer>();

    private SimilarPairComparator comparator = new SimilarPairComparator();

    public TaskDissimCalc(double maxDistance, double ledcutoff) {
        this.maxDistance = maxDistance;
        this.minSim = 1d - maxDistance;
        this.ledcutoff = ledcutoff;
    }

    public TaskDissimCalc(double maxDistance, double ledcutoff, boolean includeGateways) {
        this.includeGateways = includeGateways;

        this.maxDistance = maxDistance;
        this.minSim = 1d - maxDistance;
        this.ledcutoff = ledcutoff;
    }

    private void reset() {
        similarPairs.clear();
        mappedPairs.clear();
        sg1ContainedPairs.clear();
        sg2ContainedPairs.clear();
        sg1MappableNodes.clear();
        sg2MappableNodes.clear();
    }

    @Override
    public String getName() {
        return "TaskDissimCalc";
    }

    @Override
    public double compute(SimpleGraph sg1, SimpleGraph sg2) {
        reset();

        // we only consider functions and events for this similarity (i.e. gateways are ignored).
        // let's build two collections containing nodes of two fragments.

        List<Integer> vs1 = new ArrayList<Integer>(sg1.getFunctions());
        vs1.addAll(sg1.getEvents());
        if (includeGateways) {
            vs1.addAll(sg1.getConnectors());
        }

        List<Integer> vs2 = new ArrayList<Integer>(sg2.getFunctions());
        vs2.addAll(sg2.getEvents());
        if (includeGateways) {
            vs2.addAll(sg2.getConnectors());
        }

        // now let's compute similarity between each pair of nodes

        for (Integer ea : vs1) {
            for (Integer eb : vs2) {
                double sim = StringEditDistance.similarity(sg1.getLabel(ea), sg2.getLabel(eb));
                if (sim >= this.ledcutoff) {
                    SimilarPair sp = new SimilarPair(ea, eb, sim);
                    similarPairs.add(sp);

                    sg1MappableNodes.add(ea);
                    sg2MappableNodes.add(eb);

                    List<SimilarPair> eaPairs = sg1ContainedPairs.get(ea);
                    if (eaPairs == null) {
                        eaPairs = new ArrayList<SimilarPair>();
                        sg1ContainedPairs.put(ea, eaPairs);
                    }
                    eaPairs.add(sp);

                    List<SimilarPair> ebPairs = sg2ContainedPairs.get(eb);
                    if (ebPairs == null) {
                        ebPairs = new ArrayList<SimilarPair>();
                        sg2ContainedPairs.put(eb, ebPairs);
                    }
                    ebPairs.add(sp);
                }
            }
        }

        int m = Math.min(sg1MappableNodes.size(), sg2MappableNodes.size());
        double sizeSim = 2d * m / (vs1.size() + vs2.size());
        if (sizeSim < minSim) {
            return 1.0;
        }

        // let's sort the similar pairs, so that last pair has the highest similarity
        Collections.sort(similarPairs, comparator);

        // now map the best matching pairs
        while (!similarPairs.isEmpty()) {
            // we have sorted the similarPairs earlier. as now we are only removing items, order is preserved.
            // therefore, last pair is always the pair with highest similarity.
            SimilarPair mostSimilarPair = similarPairs.remove(similarPairs.size() - 1);
            mappedPairs.add(mostSimilarPair);

            // after the current most similar pair is decided, we should remove all pairs where v1 or v2 of the most similar pair is present.
            // i.e. once v1 and v2 are mapped, they cannot be mapped to any other node

            int v1 = mostSimilarPair.getVid1();
            List<SimilarPair> v1Pairs = sg1ContainedPairs.get(v1);
            similarPairs.removeAll(v1Pairs);
            sg1ContainedPairs.remove(v1);

            int v2 = mostSimilarPair.getVid2();
            List<SimilarPair> v2Pairs = sg2ContainedPairs.get(v2);
            similarPairs.removeAll(v2Pairs);
            sg2ContainedPairs.remove(v2);
        }

        // now we have mapped nodes in fragment1 to nodes in fragment2. now we can compute the similarity.
        double mappingScore = 0;
        for (SimilarPair pair : mappedPairs) {
            mappingScore += pair.getSim();
        }

        double sim = 2d * mappingScore / (vs1.size() + vs2.size());
        return 1d - sim;
    }

    @Override
    public boolean isAboveThreshold(double disim) {
        return disim > maxDistance;
    }

}
