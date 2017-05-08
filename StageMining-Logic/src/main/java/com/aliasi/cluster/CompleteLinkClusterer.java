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

import com.aliasi.util.BoundedPriorityQueue;
import com.aliasi.util.Distance;
import com.aliasi.util.ObjectToSet;
import com.aliasi.util.ScoredObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A <code>CompleteLinkClusterer</code> implements complete link
 * agglomerative clustering.  Complete link clustering is a greedy
 * algorithm in which the two closest clusters are always merged up to
 * a specified distance threshold.  Distance between clusters for
 * complete link clustering is defined be the maximum of the distances
 * between the members of the clusters.  See {@link
 * SingleLinkClusterer} for a clusterer that takes the minimum rather
 * than the maximum in making clustering decisions.
 *
 * <P>For example, consider the following distance matrix (which is
 * also analyzed by way of example in {@link SingleLinkClusterer}):
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
 * The result of complete-link clustering is the following dendrogram:
 *
 * <pre>
 *         A   B   C   D   E
 *         |   |   |   |   |
 *      1  -----   |   |   |
 *      2    |     |   |   |
 *      3    -------   |   |
 *      4       |      -----
 *      5       |        |
 *      6       |        |
 *      7       |        |
 *      8       |        |
 *      9       ----------
 * </pre>
 *
 * <P>First, the objects <code>A</code> and <code>B</code> are merged at
 * distance one.  At distance 3, the object <code>C</code> is merged
 * with the cluster <code>{A,B}</code> because
 * <code>max(distance(C,A),distance(C,B))=3</code>, and that is
 * smaller than any other pair of distances.  Next, <code>D</code>
 * and <code>E</code> are merged at distance 4, because that is the
 * distance between them, and they are both further than 4 away from
 * the cluster <code>{A,B,C}</code>.  This continues with one more
 * step, which happens at distance 9, the distance between <code>C</code>
 * and <code>E</code>, which is the maximum distance between pairs drawn from
 * <code>{A,B,C}</code> and <code>{D,E}</code>.
 *
 * <P>The various clusters at each distance bound threshold are:
 *
 * <blockquote>
 * <table border='1' cellpadding='5'>
 * <tr><td><i>Threshold Range</i></td><td><i>Clusters</i></td></tr>
 * <tr><td>[Double.NEGATIVE_INFINITY,1)</td><td>{A}, {B}, {C}, {D}, {E}
 * <tr><td>[1,3)</td><td> {A,B}, {C}, {D}, {E}</td></tr>
 * <tr><td>[3,4)</td><td>{A,B,C}, {D}, {E}</td></tr>
 * <tr><td>[4,9)</td><td>{A,B,C}, {D,E}</td></tr>
 * <tr><td>[9,Double.POSITIVE_INFINITY]</td><td>{A,B,C,D,E}</td></tr>
 * </table>
 * </blockquote>
 *
 * The intervals show the clusters returned for thresholds within
 * the specified interval.  As usual, square brackets denote inclusive
 * range bounds and round brackets exclusive bounds.  Although this example
 * has the same single-link clustering, see the class documentation in
 * {@link SingleLinkClusterer} for an example that does not.
 *
 * <P>Note that results may not be well-defined in the case of ties
 * between proximities.  If there are ties, there is no guarantee
 * as to how this implementation will break the tie.
 *
 * <P><i>Implementation Note:</i> This algorithm requires worst-case
 * <code><i>O</i>(n<sup><sup>3</sup></sup>)</code> running time to cluster
 * <code>n</code> elements.  The initialization loop considers all pairs
 * of elements, thus requiring <code>n<sup><sup>2</sup></sup></code>
 * running time.  The main loop then walks over these pairs, requiring
 * up to <code>n</code> steps each to compute new distances.  If the
 * initial array of pairs is heavily pruned, the outer loop is smaller
 * and the updates are actually smaller, too.
 *
 * @author Bob Carpenter
 * @version 3.8.3
 * @since   LingPipe2.0
 * @param <E> the type of objects being clustered
 */
public class CompleteLinkClusterer<E>
    extends AbstractHierarchicalClusterer<E> {

    /**
     * Construct a complete link clusterer with the specified distance
     * bound.
     *
     * @param maxDistance Maximum distance between pairs of clusters
     * to allow clustering.
     * @param distance Distance measure to use among instances.
     */
    public CompleteLinkClusterer(double maxDistance,
                                 Distance<? super E> distance) {
        super(maxDistance,distance);
    }

    /**
     * Construct a complete link clusterer with no distance bound.
     * This constructor uses {@link Double#POSITIVE_INFINITY} as the
     * bound value, which effectively makes clustering unbounded.
     */
    public CompleteLinkClusterer(Distance<? super E> distance) {
        this(Double.POSITIVE_INFINITY,distance);
    }


    @Override
    public Dendrogram<E> hierarchicalCluster(Set<? extends E> elementSet) {
        if (elementSet.size() == 0) {
            String msg = "Require non-empty set to form dendrogram."
                + " Found elementSet.size()=" + elementSet.size();
            throw new IllegalArgumentException(msg);
        }
        if (elementSet.size() == 1)
            return new LeafDendrogram<E>(elementSet.iterator().next());

        // create queue (reverse because lower is better for distances)
        BoundedPriorityQueue<PairScore<E>> queue
            = new BoundedPriorityQueue<PairScore<E>>(ScoredObject.reverseComparator(),
                                                     Integer.MAX_VALUE);
        ObjectToSet<Dendrogram<E>,PairScore<E>> index
            = new ObjectToSet<Dendrogram<E>,PairScore<E>>();
        E[] elements = toElements(elementSet);

        // required for array
        @SuppressWarnings({"unchecked","rawtypes"})
        LeafDendrogram<E>[] leafs
            = (LeafDendrogram<E>[]) new LeafDendrogram[elements.length];
        for (int i = 0; i < leafs.length; ++i)
            leafs[i] = new LeafDendrogram<E>(elements[i]);
        double maxDistance = getMaxDistance();
        for (int i = 0; i < elements.length; ++i) {
            E eI = elements[i];
            LeafDendrogram<E> dI = leafs[i];
            for (int j = i + 1; j < elements.length; ++j) {
                E eJ = elements[j];
                double score = distance().distance(eI,eJ);
                // used to continue here if too large; patched in 4.0.2, but slow
                LeafDendrogram<E> dJ = leafs[j];
                PairScore<E> psIJ = new PairScore<E>(dI,dJ,score);
                queue.offer(psIJ);
                index.addMember(dI,psIJ);
                index.addMember(dJ,psIJ);
            }
        }

        while (queue.size() > 0) {
            PairScore<E> next = queue.poll();
            Dendrogram<E> dendro1 = next.mDendrogram1.dereference();
            Dendrogram<E> dendro2 = next.mDendrogram2.dereference();
            double dist12 = next.score();
            LinkDendrogram<E> dendro12
                = new LinkDendrogram<E>(dendro1,dendro2,dist12);

            // remove & store distances to dendro1
            Map<Dendrogram<E>,Double> distanceBuf
                = new HashMap<Dendrogram<E>,Double>();
            Set<PairScore<E>> ps3Set = index.remove(dendro1);
            queue.removeAll(ps3Set);
            for (PairScore<E> ps3 : ps3Set) {
                Dendrogram<E> dendro3
                    = ps3.mDendrogram1 == dendro1
                    ? ps3.mDendrogram2
                    : ps3.mDendrogram1;
                index.get(dendro3).remove(ps3);
                double dist1_3 = ps3.score();
                distanceBuf.put(dendro3,Double.valueOf(dist1_3));
            }

            // remove & iterate over distances to dendro2
            ps3Set = index.remove(dendro2);
            queue.removeAll(ps3Set);
            for (PairScore<E> ps3 : ps3Set) {
                Dendrogram<E> dendro3
                    = ps3.mDendrogram1 == dendro2
                    ? ps3.mDendrogram2
                    : ps3.mDendrogram1;
                index.get(dendro3).remove(ps3);
                Double dist1_3D = distanceBuf.get(dendro3);
                if (dist1_3D == null) continue; // dist(dendro2,dendro3) too large
                double dist1_3 = dist1_3D.doubleValue();
                double dist2_3 = ps3.score();
                double dist12_3 = Math.max(dist1_3,dist2_3);
                PairScore<E> ps = new PairScore<E>(dendro12,dendro3,dist12_3);
                queue.offer(ps);
                index.addMember(dendro12,ps);
                index.addMember(dendro3,ps);
            }
            // dendro3 must be linked above threshold
            // by both dendro1 and dendro2
            if (queue.isEmpty()) return dendro12;
        }

        // share following code with Single Link
        Iterator<Dendrogram<E>> it = index.keySet().iterator();
        Dendrogram<E> dendro = it.next(); // skip first element -- self
        while (it.hasNext())
            dendro = new LinkDendrogram<E>(dendro,it.next(),
                                           Double.POSITIVE_INFINITY);
        return dendro;
    }

}
