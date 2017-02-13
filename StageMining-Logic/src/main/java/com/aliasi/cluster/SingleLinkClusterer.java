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
import com.aliasi.util.ScoredObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A <code>SingleLinkClusterer</code> implements standard single-link
 * agglomerative clustering.  Single link clustering is a greedy
 * algorithm in which the two closest clusters are always merged
 * up to a specified distance threshold.  Distance between clustes for
 * single link clustering is defined to be the minimum of the distances
 * between the members of the clusters.  See {@link CompleteLinkClusterer}
 * for a clusterer that takes the maximum rather than the minimum in
 * making clustering decisions.
 *
 * <P>For example, consider the following proximity matrix
 * representing distances between pairs of elements (the same example
 * is used in {@link CompleteLinkClusterer}):
 *
 * <blockquote>
 * <table border='1' cellpadding='5'>
 * <tr><td>&nbsp;</td><td>A</td><td>B</td><td>C</td><td>D</td><td>E</td></tr>
 * <tr><td>A</td><td>0</td><td>1</td><td>2</td><td>7</td><td>5</td></tr>
 * <tr><td>B</td><td>&nbsp;</td><td>0</td><td>3</td><td>8</td><td>6</td></tr>
 * <tr><td>C</td><td>&nbsp;</td><td>&nbsp;</td><td>0</td><td>5</td><td>9</td></tr>
 * <tr><td>D</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>0</td><td>4</td></tr>
 * <tr><td>E</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>0</td></tr>
 * </table>
 * </blockquote>
 *
 * The result of single-link clustering is the following dendrogram (pardon
 * the ASCII art):
 *
 * <pre>
 *         A   B   C   D   E
 *         |   |   |   |   |
 *      1  -----   |   |   |
 *      2    -------   |   |
 *      3       |      |   |
 *      4       |      -----
 *      5       ----------
 * </pre>
 *
 * <P>First, the objects <code>A</code> and <code>B</code> are merged
 * at distance one.  Then object <code>C</code> is merged into the
 * cluster at distance 2, because that is its distance from
 * <code>A</code>.  Objects <code>D</code> and <code>E</code> are
 * merged next at distance 4, and finally, the two big clusters are
 * merged at distance 5, the distance between <code>A</code> and
 * </code>E</code>.
 *
 * <P>The various clusters at each proximity bound threshold are:
 *
 * <blockquote>
 * <table border='1' cellpadding='5'>
 * <tr><td><i>Threshold Range</i></td><td><i>Clusters</i></td></tr>
 * <tr><td>[Double.NEGATIVE_INFINITY,1)</td><td>{A}, {B}, {C}, {D}, {E}
 * <tr><td>[1,2)</td><td> {A,B}, {C}, {D}, {E}</td></tr>
 * <tr><td>[2,4)</td><td>{A,B,C}, {D}, {E}</td></tr>
 * <tr><td>[4,5)</td><td>{A,B,C}, {D,E}</td></tr>
 * <tr><td>[5,Double.POSITIVE_INFINITY]</td><td>{A,B,C,D,E}</td></tr>
 * </table>
 * </blockquote>
 *
 * The intervals show the clusters returned for thresholds within
 * the specified interval.  As usual, square brackets denote inclusive
 * range bounds and round brackets exclusive bounds.  For instance,
 * if the distance threshold is 1.5, four clusters are returned,
 * whereas if the threshold is 4.0, two clusters are returned.
 *
 * <P>Note that this example has the same clusters as that in {@link
 * CompleteLinkClusterer}.  A simple example where the results are
 * different is the result of clustering four points on the x-axis,
 * <code>x<sub><sub>1</sub></sub>=1</code>, <code>x<sub><sub>2</sub></sub>=3</code>,
 * <code>x<sub><sub>3</sub></sub>=6</code>, <code>x<sub><sub>4</sub></sub>=10</code>.
 * The distance is computed by subtraction, e.g. <code>d(x<sub><sub>2</sub></sub>,
 * x<sub><sub>4</sub></sub>) = x<sub><sub>4</sub></sub>- x<sub><sub>2</sub></sub>.
 * This leads to the following distance matrix:
 *
 * <blockquote>
 * <table border='1' cellpadding='3'>
 * <tr><td>&nbsp;</td><td>x<sub><sub>1</sub></sub></td><td>x<sub><sub>2</sub></sub></td><td>x<sub><sub>3</sub></sub></td><td>x<sub><sub>4</sub></sub></td></tr>
 * <tr><td>x<sub><sub>1</sub></sub></td><td>0</td><td>2</td><td>5</td><td>9</td></tr>
 * <tr><td>x<sub><sub>2</sub></sub></td><td>&nbsp;</td><td>0</td><td>3</td><td>7</td></tr>
 * <tr><td>x<sub><sub>3</sub></sub></td><td>&nbsp;</td><td>&nbsp;</td><td>0</td><td>4</td></tr>
 * <tr><td>x<sub><sub>4</sub></sub></td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>0</td></tr>
 * </table>
 * </blockquote>
 *
 * For this example, the complete link clustering is
 *
 * <code>{{x<sub><sub>1</sub></sub>,x<sub><sub>2</sub></sub>},{x<sub><sub>3</sub></sub>,x<sub><sub>4</sub></sub>}}</code>
 *
 * whereas the single-link clustering is:
 *
 * <code>{{{x<sub><sub>1</sub></sub>,x<sub><sub>2</sub></sub>},x<sub><sub>3</sub></sub>},x<sub><sub>4</sub></sub>}</code>
 *
 * <p><i>Implementation Note:</i> This clusterer is implemented using
 * the minimum-spanning-tree-based algorithm described in:
 *
 * <blockquote>
 * Jain, Anil K. and Richard C. Dubes. 1988.  <i>Algorithms for Clustering
 * Data</i>.  Prentice-Hall.
 * </blockquote>
 *
 * The minimum spanning tree is constructed using Kruskal's algorithm,
 * which is described in:
 *
 * <blockquote>
 * Cormen, Thomas H., Charles E. Leiserson, Ronald L. Rivest, and Clifford Stein.
 * 2001.
 * <i>Introduction to Algorithms</i>. MIT Press.
 * </blockquote>
 *
 * This algorithm requires an efficient lookup of whether two
 * nodes are already in the same connected component, which
 * is implemented by the standard disjoint set algorithm with
 * path compression (also described in Cormen et al.).
 *
 * <P>In brief, the algorithm sorts all pairs of objects in order of
 * increasing distance (with <code>n</code> objects that involves
 * sorting <code>n<sup><sup>2</sup></sup></code> pairs of objects,
 * leading to an initial step with worst case time complexity bound
 * <code>O(n<sup><sup>2</sup></sup> log n)</code>.  The pairs are then
 * merged in order of decreasing distance.  The sets involved in
 * Kruskal's algorithm translate directly into dendrograms with their
 * dereferencing.
 *
 * @author  Bob Carpenter
 * @version 4.1.0
 * @since   LingPipe2.0
 * @param <E> the type of object being clustered
 */
public class SingleLinkClusterer<E>
    extends AbstractHierarchicalClusterer<E> {

    /**
     * Construct a single-link clusterer with the specified distance
     * bound.
     *
     * @param maxDistance Maximum distance for clusters.
     * @param distance Distance measure between objects to cluster.
     * @throws IllegalArgumentException If the specified bound is not
     * a non-negative number.
     */
    public SingleLinkClusterer(double maxDistance, Distance<? super E> distance) {
        super(maxDistance,distance);
    }

    /**
     * Construct a single-link clusterer with no distance bound.
     * The distance bound is set to {@link Double#POSITIVE_INFINITY},
     * which effectively removes the distance bound.
     *
     * @param distance Distance measure between objects to cluster.
     */
    public SingleLinkClusterer(Distance<? super E> distance) {
        this(Double.POSITIVE_INFINITY,distance);
    }

    /**
     * Return the array of clusters derived from the specified
     * distance matrix by performing single-link clustering up to the
     * specified maximum distance bound.  Every pair of elements in
     * the matrix that are within the distance bound will fall in the
     * same dendrogram in the result.  Setting the maximum distance
     * to {@link Double#POSITIVE_INFINITY} results in a complete
     * clustering.
     *
     * @param elementSet Set of elements to cluster.
     * @return Clustering in the form of a set of sets of elements.
     * @throws IllegalArgumentException If the set of elements is empty.
     */
    @Override
    public Dendrogram<E> hierarchicalCluster(Set<? extends E> elementSet) {
        if (elementSet.size() == 0) {
            String msg = "Require non-empty set to form dendrogram."
                + " Found elementSet.size()=" + elementSet.size();
            throw new IllegalArgumentException(msg);
        }
        if (elementSet.size() == 1)
            return new LeafDendrogram<E>(elementSet.iterator().next());
        Object[] elements = toElements(elementSet);
        // need array so identity is shared among leaf dendros
        // require supression for array
        @SuppressWarnings({"unchecked","rawtypes"})
        LeafDendrogram<E>[] leafs
            = (LeafDendrogram<E>[]) new LeafDendrogram[elements.length];
        for (int i = 0; i < leafs.length; ++i) {
            // required for obj array
            @SuppressWarnings("unchecked")
            E elt = (E) elements[i];
            leafs[i] = new LeafDendrogram<E>(elt);
        }
        Set<Dendrogram<E>> clusters
            = new HashSet<Dendrogram<E>>(elements.length);
        for (Dendrogram<E> dendrogram : leafs)
            clusters.add(dendrogram);

        // compute ordered list of pairs of elements to merge
        ArrayList<PairScore<E>> pairScoreList = new ArrayList<PairScore<E>>();
        int len = elements.length;
        double maxDistance = getMaxDistance();
        for (int i = 0; i < len; ++i) {
            // required for obj array
            @SuppressWarnings("unchecked")
            E eI = (E) elements[i];
            Dendrogram<E> dendroI = leafs[i];
            for (int j = i + 1; j < len; ++j) {
                // required for obj array
                @SuppressWarnings("unchecked")
                E eJ = (E) elements[j];
                double distanceIJ = distance().distance(eI,eJ);
                // if (distanceIJ > maxDistance) continue;  // speeds up, but strands elements
                Dendrogram<E> dendroJ = leafs[j];
                pairScoreList.add(new PairScore<E>(dendroI,dendroJ,distanceIJ));
            }
        }
        // required for array
        @SuppressWarnings({"unchecked","rawtypes"})
        PairScore<E>[] pairScores
            = (PairScore<E>[]) new PairScore[pairScoreList.size()];
        pairScoreList.toArray(pairScores);
        Arrays.sort(pairScores,ScoredObject.comparator());  // increasing order of distance

        for (int i = 0;  i < pairScores.length && clusters.size() > 1;  ++i) {
            PairScore<E> ps = pairScores[i];
            if (ps.score() > getMaxDistance()) break;
            Dendrogram<E> d1 = ps.mDendrogram1.dereference();
            Dendrogram<E> d2 = ps.mDendrogram2.dereference();
            if (d1.equals(d2)) {
                continue; // already linked
            }
            clusters.remove(d1);
            clusters.remove(d2);
            LinkDendrogram<E> dLink
                = new LinkDendrogram<E>(d1,d2,pairScores[i].mScore);
            clusters.add(dLink);
        }
        // link up remaining unlinked dendros at +infinity distance
        Iterator<Dendrogram<E>> it = clusters.iterator();
        Dendrogram<E> dendro = it.next(); // skip first - self
        while (it.hasNext())
            dendro = new LinkDendrogram<E>(dendro,it.next(),
                                           Double.POSITIVE_INFINITY);
        return dendro;
    }


}
