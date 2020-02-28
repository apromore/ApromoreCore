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

package org.apromore.toolbox.clustering.algorithm.hac;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apromore.toolbox.clustering.containment.ContainmentRelation;
import org.apromore.toolbox.clustering.dissimilarity.DissimilarityMatrix;
import org.apromore.toolbox.clustering.algorithm.hac.dendogram.InternalNode;
import org.apromore.toolbox.clustering.algorithm.hac.dendogram.LeafNode;
import org.apromore.toolbox.clustering.algorithm.hac.dendogram.Node;

public abstract class HierarchicalAgglomerativeClustering {

    private final ContainmentRelation crel;
    private final DissimilarityMatrix dmatrix;

    private SortedSet<InternalNode> sources;
    private double diameterThreshold = 0.4;


    public HierarchicalAgglomerativeClustering(final ContainmentRelation crel, final DissimilarityMatrix dmatrix) {
        this.crel = crel;
        this.dmatrix = dmatrix;
        this.sources = null;
    }

    public SortedSet<InternalNode> cluster() {
        Set<Integer> open = new HashSet<Integer>();
        Set<InternalNode> closed = new HashSet<InternalNode>();

        Map<Integer, Node> map = new HashMap<Integer, Node>();

        TreeSet<Pair> queue = new TreeSet<Pair>(new Comparator<Pair>() {
            public int compare(Pair p1, Pair p2) {
                int result = Double.compare(p1.dissimilarity, p2.dissimilarity);
                if (result == 0) {
                    result = Double.compare(p1.first, p2.first);
                    if (result == 0)
                        result = Double.compare(p1.second, p2.second);
                }
                return result;
            }
        });

        for (int index = 0; index < crel.getNumberOfFragments(); index++) {
            open.add(index);
            map.put(index, new LeafNode(index, crel.getFragmentId(index)));
        }

        // Step 1. Add all initial distances
        for (int i = 0; i < crel.getNumberOfFragments() - 1; i++) {
            for (int j = i + 1; j < crel.getNumberOfFragments(); j++) {
                Double dissim = dmatrix.getDissimilarity(i, j);
                if (dissim != null) {
                    Pair pair = new Pair(i, j, dissim);
                    queue.add(pair);
                }
            }
        }

        while (!queue.isEmpty()) {
            Pair curr = queue.pollFirst();

            if (open.contains(curr.first) && open.contains(curr.second)) {
                open.remove(curr.first);
                open.remove(curr.second);

                int index = map.size();
                InternalNode node1 = new InternalNode(index, map.get(curr.first), map.get(curr.second), curr.dissimilarity);
                map.put(index, node1);

                closed.add(node1);

                // Update distance matrix
                for (Integer indexp : open) {
                    Node node2 = map.get(indexp);
                    boolean containment = false;
                    resetProximityValue();

                    for (Integer child1 : node1.getChildren()) {
                        for (Integer child2 : node2.getChildren()) {
                            if (crel.areInContainmentRelation(crel.getFragmentIndex(child1), crel.getFragmentIndex(child2))) {
                                containment = true;
                                break;
                            } else {
                                Double tmp = dmatrix.getDissimilarity(crel.getFragmentIndex(child1), crel.getFragmentIndex(child2));
                                if (tmp != null) {
                                    updateProximityValue(tmp);
                                }
                            }
                        }
                        if (containment) {
                            break;
                        }
                    }

                    if (!containment && isItAValidProximityValue()) {
                        double dissimilarity = getProximityValue();
                        if (dissimilarity <= diameterThreshold) {
                            queue.add(new Pair(node1.getIndex(), node2.getIndex(), dissimilarity));
                        }
                    }
                }
                open.add(index);
            }
        }

        sources = new TreeSet<InternalNode>(new Comparator<InternalNode>() {
            public int compare(InternalNode p1, InternalNode p2) {
                int result = Double.compare(p1.getProximity(), p2.getProximity());
                if (result == 0) {
                    result = Double.compare(p1.getIndex(), p2.getIndex());
                }
                return result;
            }
        });

        sources.addAll(closed);
        for (Node node : closed) {
            if (node.getFirst() instanceof InternalNode) {
                sources.remove(node.getFirst());
            }
            if (node.getSecond() instanceof InternalNode) {
                sources.remove(node.getSecond());
            }
        }

        return sources;
    }

    public SortedSet<InternalNode> getSources() {
        return sources;
    }

    public void setDiameterThreshold(double diameterThreshold) {
        this.diameterThreshold = diameterThreshold;
    }


    protected abstract void resetProximityValue();

    protected abstract void updateProximityValue(double newValue);

    protected abstract boolean isItAValidProximityValue();

    protected abstract double getProximityValue();


    private class Pair {
        int first, second;
        double dissimilarity;

        Pair(int first, int second, double dissimilarity) {
            this.first = first;
            this.second = second;
            this.dissimilarity = dissimilarity;
        }

        public String toString() {
            return String.format("(%d,%d): %f", first, second, dissimilarity);
        }
    }
}
