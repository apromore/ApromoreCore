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

import com.aliasi.stats.Statistics;
import com.aliasi.util.BoundedPriorityQueue;
import com.aliasi.util.Distance;
import com.aliasi.util.Scored;
import com.aliasi.util.ScoredObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * A <code>Dendrogram</code> represents the result of a weighted
 * hierarchical clustering of a set of objects.  A dendrogram's cost
 * is interpreted as the cost of forming the cluster of its members;
 * this may be a join cost for a bottom-up (agglomerative) clusterer
 * or a split cost for a top-down (divisive) clusterer.  The minimum
 * cost is 0.0, and each dendrogram's sub-dendrogram's must have a
 * lower cost than it.
 *
 * <P>There are two subclasses of dendrogram, leafs and links.  A
 * {@link LeafDendrogram} is a single element cluster with a cost of
 * 0.0.  A {@link LinkDendrogram} is constructed by merging two
 * sub-dendrograms with a given cost.  The resulting link dendrogram then
 * becomes the parent of the sub-dendrograms.  
 *
 * <p>Dendrograms that are linked point to their parent dendrogram
 * through the method {@link #parent()}.  The parent is always a link
 * dendrogram.  The method {@link #dereference()} returns the result
 * of following the chain of parent links to the final, top-level
 * dendrogram.  All of the members of a given dendrogram will dereference
 * to the same parent.
 *
 * <P>Dendrograms return their set of elements through {@link
 * #memberSet()}.  They may also be partitioned into clusterings in
 * one of two ways.  These clusterings are sets of sets that cover the
 * members of the dendrogram.  The method {@link
 * #partitionDistance(double)} returns all top-level dendrograms
 * where all links are within the specified score.  The method {@link
 * #partitionK(int)} returns a partition of the specified size.  Both
 * operate by breaking links in the dendrogram.  The thresholded
 * method breaks all links whose score are above a threshold.  The
 * size-based method breaks links in order of decreasing distance
 * until the specified number of partitions is generated.
 *
 * <p>The method {@link #copheneticCorrelation(Distance)} may be used
 * to evaluate the distances between objects implied by the dendrogram
 * and the distances in a specified distance function.  The distances
 * implied by the dendrogram are so-called cophenetic distances.  The
 * cophenetic distance between a pair of elements in the member set of
 * a dendrogram is the distance of the sub-dendrogram linking them.
 *
 * <p>Given a distance measure and number of clusters, the method
 * {@link #withinClusterScatter(int,Distance)} evaluates the within-cluster
 * scatter, as defined in the class documentation for {@link ClusterScore}.
 *
 * @author Bob Carpenter
 * @author Mike Ross
 * @version 4.0.0
 * @since   LingPipe2.0
 * @param <E> the type of objects being clustered
 */
public abstract class Dendrogram<E> implements Scored {

    private LinkDendrogram<E> mParent;
    private Dendrogram<E> mReferenceLink;
    protected Set<E> memberSet = new HashSet<E>();
    private int mLevel=1; //the root dendrogram will have level 1.

    Dendrogram() {
        mReferenceLink = this;
    }

    /**
     * Returns the dendrogram that immediately contains
     * this dendrogram or <code>null</code> if this is a top-level
     * dendrogram.
     *
     * @return the parent of this dendrogram or <code>null</code>
     * if this is a root dendrogram.
     */
    public LinkDendrogram<E> parent() {
        return mParent;
    }

    /**
     * Returns the top-level cluster of which this dendrogram
     * is a member.  A dendrogram with a <code>null</code> parent
     * dereferences to itself.
     *
     * @return The top-level cluster of which this dendrogram
     * is a member.
     */
    public Dendrogram<E> dereference() {
        Dendrogram<E> ancestor = mReferenceLink.parent();
        if (ancestor == null) return mReferenceLink;
        for (LinkDendrogram<E> nextAncestor = null;
             (nextAncestor = ancestor.parent()) != null;
             ancestor = nextAncestor);
        mReferenceLink = ancestor; // path compression
        return mReferenceLink;
    }

    /**
     * Returns the size of this dendrogram measured by the number
     * of objects.  This result is consistent with the interpretation
     * of the dendrogram as a set.
     *
     * @return The size of this dendrogram.
     */
    public int size() {
        return getMemberSet().size();
    }
    
    /**
     * Returns <code>true</code> if this dendrogram contains the
     * specified object as a leaf.
     *
     * @param elt Object to test.
     * @return <code>true</code> if this dendrogram contains the
     * specified object as a leaf.
     */
    public boolean contains(E elt) {
        return getMemberSet().contains(elt);
    }


    /**
     * Returns the partition produced by cutting this dendrogram at
     * the specified maximum distance.  Two elements will be in the
     * same set within the partition if they were merged in the
     * dendrogram at a distance less than or equal to the specified
     * distance.
     *
     * <p>This operation may be understood in terms of the isomorphism
     * between equivalence relations and partitions.  An equivalence
     * relation is a transitive, symmetric, reflexive relation between
     * objects.  An equivalence relation determines a partition with
     * clusters corresponding to sets of mutually equivalent objects.
     * For this method, objects are equivalent if the distance between
     * them is less than or equal to the specified maximum distance
     * in the dendrogram.
     *
     * @param maxDistance Maximum dendrogram distance at which
     * elements are considered to be part of the same cluster.
     * @return The partition of the elements.
     */
    public Set<Set<E>> partitionDistance(double maxDistance) {
        HashSet<Set<E>> clustering = new HashSet<Set<E>>();
        LinkedList<Dendrogram<E>> stack = new LinkedList<Dendrogram<E>>();
        stack.addFirst(this);
        while(!stack.isEmpty()) {
            Dendrogram<E> curDendro = stack.removeFirst();
            if (curDendro.score() <= maxDistance)
                clustering.add(curDendro.getMemberSet());
            else
                curDendro.subpartitionDistance(stack);
        }
        return clustering;
    }
    
    
    /**
     * Return the partition of this dendrogram with the specified
     * number of clusters.  This is computed by breaking links from
     * the top down; graphically, this is like drawing a line through
     * the dendrogram.  Note that if two clusters have the same score,
     * one will be chosen to split first.
     *
     * @param numClusters Number of clusters in returned partition.
     * @return The partition with the specified number of clusters.  
     * @throws IllegalArgumentException If the specified number of clusters is
     * less than one or larger than the number of elements.
     */
    public Set<Set<E>> partitionK(int numClusters) {
        if (numClusters < 1) {
            String msg = "Require at least one cluster. "
                + " Found numClusters=" + numClusters;
            throw new IllegalArgumentException(msg);
        }
        if (size() < numClusters) {
            String msg = "This dendrogram contains only "
                + size() + " elements. "
                + " Require at least numClusters=" + numClusters;
            throw new IllegalArgumentException(msg);
        }
        BoundedPriorityQueue<Dendrogram<E>> queue 
            = new BoundedPriorityQueue<Dendrogram<E>>(ScoredObject.comparator(),
                                                      numClusters+1);
        queue.offer(this);
        HashSet<Set<E>> resultSet
            = new HashSet<Set<E>>(numClusters);
        while (queue.size() + resultSet.size() < numClusters) {
            Dendrogram<E> toSplit = queue.poll();
            toSplit.split(resultSet,queue);
        }
        for (Dendrogram<E> d : queue)
            resultSet.add(d.getMemberSet());
        return resultSet;
    }


    void subpartitionDistance(LinkedList<Dendrogram<E>> stack) {
        /* no op overridden in LinkDendrogram subclass */
    }

    abstract void split(Collection<Set<E>> resultSet,
                        BoundedPriorityQueue<Dendrogram<E>> queue);
                   

    /**
     * Returns a pretty-printed version of this dendrogram with
     * entries on a single line and indenting with scores to show the
     * merges.
     *
     * @return A pretty string representation of this dendrogram.
     */
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        this.prettyPrint(sb,0);
        return sb.toString();
    }

    /**
     * Returns a string-based representation of this dendrogram.
     *
     * @return A string-based representation of this dendrogram.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb,1);
        return sb.toString();
    }

    /**
     * Returns the members of this dendrogram.
     * This is to collect members from child dendrograms
     *
     * @return The members of this dendrogram.
     */
    public abstract Set<E> collectMemberSet();
    
    /*
     * Bruce: note that member set does not 
     * contain the source and the sink (transition nodes)
     */
    public Set<E> getMemberSet() {
    	return this.memberSet;
    }
    
    /**
     * This is used for assigning members from top-down
     * @param memberSet
     */
    public void setMemberSet(Set<E> memberSet) {
    	this.memberSet = memberSet;
    }

    /**
     * Returns the proximity between the components of this
     * dendrogram.
     *
     * @return The proximity between this dendrogram's components.
     */
    public abstract double score();
    
    abstract void prettyPrint(StringBuilder sb, int indent);

    abstract void addMembers(Set<E> set);

    abstract void toString(StringBuilder sb, int depth);

    /**
     * Returns the within-cluster scatter relative to the specified
     * distance of the clustering determined by splitting this
     * dendrogram into the specified number of clusters.  Thus the
     * result is
     * <code>ClusterScore.withinClusterScatter(partitionK(numClusters),distance)</code>.
     *
     * <p>See
     * <code>ClusterScore.withinClusterScatter(Set,Distance)</code>
     * for more information on the scatter evaluation metric.
     *
     * @throws IllegalArgumentException If the number of clusters is
     * not between 1 and the size of this dendrogram inclusive.
     */
    public double withinClusterScatter(int numClusters, Distance<? super E> distance) {
        if (numClusters < 1 || numClusters > size()) {
            String msg = "Require number of clusters between 1 and size."
                + " Found numClusters=" + numClusters
                + " size()=" + size();
            throw new IllegalArgumentException(msg);
        }
        Set<Set<E>> clustering = partitionK(numClusters); 
        return ClusterScore.<E>withinClusterScatter(clustering,distance);
    }

    /**
     * Returns the Pearson correlation coefficient between the
     * inter-object distances and the cophenetic distances defined by
     * this dendrogram.  
     *
     * <p>The cophenetic distance between a pair of elements is
     * defined to be the score of the dendrogram which joins the two
     * elements together; that is, they are in distinct sub-dendrograms
     * of the dendrogram.
     *
     * <P>A cophenetic distance forms what is known as an ultrametric.
     * It forms a usual metric distance satisfying the following
     * requirements:
     *
     * <blockquote><pre>
     * d(x,y) &gt;= 0
     * d(x,x) = 0
     * d(x,y) = d(y,x)
     * d(x,y) + d(y,z) &gt;= d(x,z)</pre></blockquote>
     *
     * In addition, an ultrametric satisfies the following:
     *
     * <blockquote><code>
     * d(x,z) &lt;< max(d(x,y), d(y,z))
     * </code></blockquote>
     *
     * Geometrically, this means that for all triples <code>x,y,z</code>
     * at least two of <code>d(x,y),d(x,z),d(y,z)</code> are
     * the same.
     *
     * <p>Given the cophenetic distance ultrametric from a dendrogram,
     * both single-link and complete-link clusterers will produce the
     * same dendrogram when clustering based on the cophenetic
     * distance.
     *
     * <UL>
     * 
     * <LI>
     * Weisstein, Eric W. <a
     * href="http://mathworld.wolfram.com/Ultrametric.html">Ultrametric</a>.
     * From MathWorld--A Wolfram Web Resource.
     *
     * <LI> Jain, Anil K. and Richard C. Dubes. 1988.  <i>Algorithms 
     * for Clustering Data</i>.  Prentice-Hall.
     *
     * </UL>

     * @param distance Underlying distance measure.
     */
    public double copheneticCorrelation(Distance<? super E> distance) {
        int size = (size() * (size() - 1))/2;
        double[] xs = new double[size];
        double[] ys = new double[size];
        copheneticCorrelation(0,xs,ys,distance);
        return Statistics.correlation(xs,ys);
    }

    /**
     * Return {@code true} if the specified dendrograms are
     * structurally equivalent.  Two dendrograms are structurally
     * equivalent if and only if they have the same score, and they
     * are either both leaf dendrograms with the same object, or they
     * are both link dendrograms with equivalent daughters (in either
     * order).
     *
     * <p>Note that this is not the same relation as {@link
     * Dendrogram#equals(Object)}, which is defined by reference, not
     * by structural equivalence.  Nor is it the same relation as
     * defined by the score comparators.  
     *
     * @param dendrogram1 First dendrogram to compare.
     * @param dendrogram2 Second dendrogram to compare.
     * @return {@code true} if the two dendrograms are equivalent.
     */
    public static <E> boolean structurallyEquivalent(Dendrogram<E> dendrogram1,
                                                     Dendrogram<E> dendrogram2) {

        if (dendrogram1 instanceof LeafDendrogram) {

            if (!(dendrogram2 instanceof LeafDendrogram))
                return false;
            
            LeafDendrogram<E> leafDendrogram1 = (LeafDendrogram<E>) dendrogram1;
            LeafDendrogram<E> leafDendrogram2 = (LeafDendrogram<E>) dendrogram2;
            return leafDendrogram1.object().equals(leafDendrogram2.object())
                && leafDendrogram1.score() == leafDendrogram2.score();

        } else if (!(dendrogram2 instanceof LinkDendrogram)) {
            return false;
        } 

        LinkDendrogram<E> linkDendrogram1 = (LinkDendrogram<E>) dendrogram1;
        LinkDendrogram<E> linkDendrogram2 = (LinkDendrogram<E>) dendrogram2;
        if (linkDendrogram1.score() != linkDendrogram2.score())
            return false;
        
        return ( structurallyEquivalent(linkDendrogram1.dendrogram1(),
                                        linkDendrogram2.dendrogram1())
                 && structurallyEquivalent(linkDendrogram1.dendrogram2(),
                                           linkDendrogram2.dendrogram2()) )
            || ( structurallyEquivalent(linkDendrogram1.dendrogram1(),
                                        linkDendrogram2.dendrogram2())
                 && structurallyEquivalent(linkDendrogram1.dendrogram2(),
                                           linkDendrogram2.dendrogram1()) );
    }

    abstract int copheneticCorrelation(int i, double[] xs, double[] ys,
                                       Distance<? super E> distance);
    


    void indent(StringBuilder sb, int indent) {
        sb.append('\n');
        for (int i = 0; i < indent; ++i)
            sb.append("    ");
    }

    void setParent(LinkDendrogram<E> parent) {
        mParent = parent;
        mReferenceLink = parent;
    }
    
    /**
     * Level of this dendrogram from the root node
     * The root node has level 1.
     * @return
     */
    public int getLevel() {
    	return mLevel;
    }
    
    public void setLevel(int level) {
    	mLevel = level;
    }

}

