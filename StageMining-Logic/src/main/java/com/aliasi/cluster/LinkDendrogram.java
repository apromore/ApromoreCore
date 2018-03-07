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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.aliasi.util.BoundedPriorityQueue;
import com.aliasi.util.Distance;


/**
 * A <code>LinkDendrogram</code> consists of a pair of sub-dendrograms
 * which are joined at a specified cost.  Although typically used in
 * the case where the sub-dendrograms have lower costs than their
 * parent dendrograms, this condition is not enforced by this
 * implementation.
 *
 * @author Bob Carpenter
 * @version 4.0.0
 * @since   LingPipe2.0
 * @param <E> the type of objects being clustered
 */
public class LinkDendrogram<E> extends Dendrogram<E> {

    private double mCost;
    private Dendrogram<E> mDendrogram1;
    private Dendrogram<E> mDendrogram2;
    private E mSource;
    private E mSink;
    /**
     * The new dendrogram contains a set of nodes, not a subgraph.
     * NOTE: the member set of a dendrogram does not contain the source and the sink node of the graph
     * A transition node belongs to the member set of a preceding dendrogram resulting from a graph cut
     * @param parent: parent dendrogram
     * @param memberSet: the set of nodes contained in this dendrogram
     * @param source: the source node of this dendrogram (transition node), is the source of the graph for the 1st dendrogram
     * @param sink the sink node of this dendrogram (transition node), is the sink of the graph for the last dendrogram
     * @param score: the min cut value to cut the parent graph
     */
	public LinkDendrogram(LinkDendrogram<E> parent, Set<E> memberSet, E source, E sink, double score) {
    	super();
    	this.setMemberSet((Set<E>)memberSet);
    	this.setParent(parent);
    	this.setScore(score);
    	this.setSource(source);
    	this.setSink(sink);
    }
    
    /**
     * Construct a link dendrogram containing the specified object.
     *
     * @param dendrogram1 First dendrogram in cluster.
     * @param dendrogram2 Second dendrogram in cluster.
     * @param cost Cost of creating this dendrogram from the specified
     * dendrograms.
     * @throws IllegalArgumentException If the cost is less than
     * <code>0.0</code>.
     */
    public LinkDendrogram(Dendrogram<E> dendrogram1,
                          Dendrogram <E> dendrogram2,
                          double cost) {
        if (cost < 0.0 || Double.isNaN(cost)) {
            String msg = "Cost must be >= 0.0"
                + " Found cost=" + cost;
            throw new IllegalArgumentException(msg);
        }
        dendrogram1.setParent(this);
        dendrogram2.setParent(this);
        mDendrogram1 = dendrogram1;
        mDendrogram2 = dendrogram2;
        mCost = cost;
    }
    
    /**
     * Returns the cost of this dendogram.  The cost is specified at
     * construction time and is meant to indicate the proximity
     * between the elements.
     *
     * @return The proximity between the pair of component
     * dendrograms making up this dendrogram.
     */
    @Override
    public double score() {
        return mCost;
    }
    
    public void setScore(double score) {
        mCost = score;
    }
    
	public void setSource(E source) {
		mSource = source;
	}
	
	public E getSource() {
		return mSource;
	}	
	
	public void setSink(E sink) {
		mSink = sink;
	}
	
	public E getSink() {
		return mSink;
	}	
   

    @Override
    public Set<E> collectMemberSet() {
        HashSet<E> members = new HashSet<E>();
        addMembers(members); //members will contain all member elements of child dendrograms
        this.memberSet = members;
        return members;
    }

    /**
     * The input set will be added members from child dendrograms
     */
    @Override
    void addMembers(Set<E> set) {
        mDendrogram1.addMembers(set);
        mDendrogram2.addMembers(set);
    }

    @Override
    void split(Collection<Set<E>> resultSet,
               BoundedPriorityQueue<Dendrogram<E>> queue) {
        queue.offer(mDendrogram1);
        queue.offer(mDendrogram2);
    }

    /**
     * Returns the first dendrogram in the linked dendrogram.  This is
     * the first dendrogram in constructor argument order, but the
     * order is irrelevant in the semantics of dendrograms as they
     * represent unordered trees.
     *
     * @return The first dendrogram linked.
     */
    public Dendrogram<E> dendrogram1() {
        return mDendrogram1;
    }
    
    public void setDendrogram1(Dendrogram<E> dendro) {
    	mDendrogram1 = dendro;
    	mDendrogram1.setLevel(this.getLevel() + 1);
    }

    /**
     * Returns the second dendrogram in the linked dendrogram.  This
     * is the second dendrogram in constructor argument order, but the
     * order is irrelevant in the semantics of dendrograms as they
     * represent unordered trees.
     *
     * @return The second dendrogram linked.
     */
    public Dendrogram<E> dendrogram2() {
        return mDendrogram2;
    }
    
    public void setDendrogram2(Dendrogram<E> dendro) {
    	mDendrogram2 = dendro;
    	mDendrogram2.setLevel(this.getLevel() + 1);
    }
    
    @Override
    void subpartitionDistance(LinkedList<Dendrogram<E>> stack) {
        stack.addFirst(dendrogram1());
        stack.addFirst(dendrogram2());
    }

    @Override
    int copheneticCorrelation(int i, double[] xs, double[] ys,
                              Distance<? super E> distance) {
        for (E e1 : mDendrogram1.getMemberSet()) {
            for (E e2 : mDendrogram2.getMemberSet()) {
                xs[i] = score();
                ys[i] = distance.distance(e1,e2);
                ++i;
            }
        }
        return i;
    }

    @Override
    void toString(StringBuilder sb, int depth) {
        sb.append('{');
        mDendrogram1.toString(sb,depth+1);
        sb.append('+');
        mDendrogram2.toString(sb,depth+1);
        sb.append("}:");
        sb.append(mCost);
    }
    
    @Override
    void prettyPrint(StringBuilder sb, int depth) {
        indent(sb,depth);
        sb.append(score());
        if (mDendrogram1 != null) mDendrogram1.prettyPrint(sb,depth+1);
        if (mDendrogram2 != null) mDendrogram2.prettyPrint(sb,depth+1);
    }

    
    /*
     * Return a partition of this dendrogram produced by cutting this dendrogram
     * at a specified level. This is based on the level attribute of one dendrogram.
     * The level of the top one is zero. Children of the top one has level 1, and so on.
     * This is done based on depth-first traversal from the top one. Note that
     * the result must contain a dendrogram even if it does not have a child one
     * at the specified level.
     * So, this is like drawing a horizontal line across the dendrogram picture 
     * The returned result will have all the nodes at the specified level from
     * left to right order.
     */
    @SuppressWarnings("unchecked")
	public List<Set<E>> partitionLevel(int level) throws Exception {
    	List<Set<E>> result = new ArrayList<Set<E>>();
    	if (this.getLevel() >= level) {
    		throw new Exception("Cannot cut a dendrogram at a level lower than or equal to its own level");
    	}
    	Boolean stop= false;
    	this.drill(this, this.getLevel(), level, result, stop);
    	//add the source node of the original graph to the first phase
//    	if (!result.isEmpty()) result.get(0).add((E)this.getGraph().getSource());
    	return result;
    }
    
    /*
     * node - node being visited
     * clevel - current level
     * rlevel - requested level
     * result - result queue
     */
    private void drill (LinkDendrogram<E> node, int clevel, int rlevel, List<Set<E>> result, Boolean stop) {
      if (clevel == rlevel || node.dendrogram1() == null) { //add the node to result even it has no children.
    	  result.add(node.getMemberSet());
    	  stop = true;
      }
      else if (node.dendrogram1() != null) { //this means it has both dendrogram1 and 2.
          drill((LinkDendrogram<E>)node.dendrogram1(), clevel + 1, rlevel, result, stop);
          if (!stop) drill((LinkDendrogram<E>)node.dendrogram2(), clevel + 1, rlevel, result, stop);
      }
    }
    
    /**
     * Search for the leaf dendrogram that contains a
     * specified member. Use bread-first-search.
     * @param member
     * @return leaf dendrogram
     */
    public LinkDendrogram<E> search(E member) {
    	Queue<LinkDendrogram<E>> queue = new LinkedList<LinkDendrogram<E>>();
    	queue.add(this);
    	while (!queue.isEmpty()) {
    		LinkDendrogram<E> head = queue.poll();
    		if (head.dendrogram1() == null) { // head is a leaf dendrogram
    			if (head.getMemberSet().contains(member)) {
    				return head;
    			}
    		}
    		else {
    			queue.add((LinkDendrogram<E>)head.dendrogram1());
    			queue.add((LinkDendrogram<E>)head.dendrogram2());
    		}
    	}
    	return null;
    }
}
