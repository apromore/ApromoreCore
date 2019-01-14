/*
 * LingPipe v. 4.1.0
 * Copyright (C) 2003-2011 Alias-i
 *
 * This program is licensed under the Alias-i Royalty Free License
 * Version 1 WITHOUT ANY WARRANTY, without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Alias-i
 * Royalty Free License Version 1 for more details.
 *
 * You should have received a copy of the Alias-i Royalty Free License
 * Version 1 along with this program; if not, visit
 * http://alias-i.com/lingpipe/licenses/lingpipe-license-1.txt or contact
 * Alias-i, Inc. at 181 North 11th Street, Suite 401, Brooklyn, NY 11211,
 * +1 (718) 290-9170.
 */

package com.aliasi.cluster;

import com.aliasi.util.Distance;
import com.aliasi.util.Scored;


import java.util.HashSet;
import java.util.Set;

/**
 * An <code>AbstractHierachicalClusterer</code> provides an adapter
 * for clustering for hierarchical clusterers.  The abstract method
 * {@link #hierarchicalCluster(Set)} defines hierarchical
 * clustering for the specified input set, returning a dendrogram.
 * The basic clustering interface {@link #cluster(Set)} is defined
 * by specifying a cutoff in terms of distance.
 *
 * <P>Distance measures between elements provide measures of
 * dissimilarity in that the larger the distance the more dissimilar
 * the members.  Zero values indicate perfect similarity and larger
 * numbers indicate less similarity.  The typical example is a
 * distance measure of some kind; closer objects are clustered more
 * readily in these cases.  A typical distance metric is Euclidean
 * distance between vector objects. Other Minkowski metrics are also
 * common, such as the Manhattan metric, which reduces to Hamming
 * distance for binary vectors.  Edit distance, as implemented in the
 * {@link com.aliasi.spell} package is another popular dissimilarity
 * metric for text.  Two texts,
 * <code>text<sub><sub>1</sub></sub></code> and
 * <code>text<sub><sub>2</sub></sub></code>, may be compared by sample
 * cross-entropy.  If <code>M<sub><sub>i</sub></sub></code> is the
 * result of training a language model on
 * <code>text<sub><sub>i</sub></sub></code>, then a symmetric measure of of
 * dissimilarity is
 * <code>M<sub><sub>1</sub></sub>.crossEntropy(text<sub><sub>2</sub></sub>)
 * +
 * M<sub><sub>2</sub></sub>.crossEntropy(text<sub><sub>1</sub></sub>)</code>.
 * Averages, min or max may also be used.
 *
 * @author  Bob Carpenter
 * @version 3.8
 * @since   LingPipe2.0
 * @param <E> the type of objects being clustered
 */
public abstract class AbstractHierarchicalClusterer<E>
    implements HierarchicalClusterer<E> {

    private double mMaxDistance;
    private final Distance<? super E> mDistance;

    /**
     * Construct an abstract hierarchical clusterer with the specified
     * maximum distance.  The distance must be a number greater than or
     * equal to zero, but it may be positive infinity.
     *
     * @param maxDistance Maximum distance between clusters that can
     * be linked.
     // * @param minClusters Minimum number of clusters to return.
     // * @param maxClusters Maximum number of clusters to return.
     * @throws IllegalArgumentException If the specified distance is not
     * a non-negative number.
     */
    public AbstractHierarchicalClusterer(double maxDistance,
                                         Distance<? super E> distance) {
        setMaxDistance(maxDistance);
        mDistance = distance;
    }

    /**
     * Returns the distance function for this hierarchical clusterer.
     *
     * @return The distance function for this hierarchical clusterer.
     */
    public Distance<? super E> distance() {
        return mDistance;
    }


    /**
     * Returns the array of clusters derived from performing
     * clustering with this class's specified maximum distance.
     * Setting the maximum distance to {@link
     * Double#POSITIVE_INFINITY} should result in a complete
     * clustering.
     *
     * @param elements Set of objects to cluster.
     */
    public abstract Dendrogram<E>
        hierarchicalCluster(Set<? extends E> elements);

    /**
     * Returns the clustering of the specified elements.  The
     * clustering is determined by splitting a complete hierarchical
     * clustering at this class's distance bound.  Thus the pairwise
     * distances between the sets in the clustering returned
     * will all be greater than this clusterer's maximum distance.
     *
     * @param elements Elements to cluster.
     * @return Clustering of elements.
     */
    public Set<Set<E>> cluster(Set<? extends E> elements) {
        if (elements.isEmpty())
            return new HashSet<Set<E>>();
        Dendrogram<E> dendrogram
            = hierarchicalCluster(elements);
        return dendrogram.partitionDistance(mMaxDistance);
    }

    /**
     * Returns the maximum distance for clusters in a dendrogram.
     *
     *@return The maximimum distance score for a dendrogram to
     * remain after cutting.
     */
    public double getMaxDistance() {
        return mMaxDistance;
    }

    /**
     * Sets the maximum distance at which two clusters may
     * be merged.
     *
     * @param maxDistance New value for maximum distance.
     */
    public final void setMaxDistance(double maxDistance) {
        assertValidDistanceBound(maxDistance);
        mMaxDistance = maxDistance;
    }



    static void assertValidDistanceBound(double maxDistance) {
        if (maxDistance < 0.0 || Double.isNaN(maxDistance)) {
            String msg = "Max distance must be non-negative number."
                + " Found maxDistance=" + maxDistance;
            throw new IllegalArgumentException(msg);
        }
    }


    E[] toElements(Set<? extends E> elementSet) {
        int len = elementSet.size();
        // required for array
        @SuppressWarnings("unchecked")
        E[] elements = (E[]) new Object[len];
        elementSet.toArray(elements);
        return elements;
    }



    static class PairScore<E> implements Scored {
        final Dendrogram<E> mDendrogram1;
        final Dendrogram<E> mDendrogram2;
        final double mScore;
        public PairScore(Dendrogram<E> dendrogram1, Dendrogram<E> dendrogram2,
                         double score) {
            mDendrogram1 = dendrogram1;
            mDendrogram2 = dendrogram2;
            mScore = score;
        }
        public double score() {
            return mScore;
        }
        @Override
        public String toString() {
            return "ps("
                + mDendrogram1
                + ","
                + mDendrogram2
                + ":"
                + mScore
                + ") ";
        }
    }

}
