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
import com.aliasi.util.SmallSet;

import java.util.Collection;
import java.util.Set;

/**
 * A <code>LeafDendrogram</code> represents a dendrogram consisting
 * of a single object with link cost of 0.0.
 *
 * @author Bob Carpenter
 * @version 4.0.0
 * @since   LingPipe2.0
 * @param <E> the type of objects being clustered
 */
public class LeafDendrogram<E> extends Dendrogram<E> {

    private final E mObject;

    /**
     * Construct a leaf dendrogram containing the specified object.
     *
     * @param object Object contained in the constructed dendrogram.
     */
    public LeafDendrogram(E object) {
        mObject = object;
    }

    /**
     * Returns <code>0.0</code>, the cost of a leaf dendrogram.
     *
     * @return 0.0.
     */
    @Override
    public double score() {
        return 0.0;
    }
    
    

    /**
     * Return the single object underlying this leaf dendrogram.
     * 
     * @return The object underlying this leaf dendrogram.
     */
    public E object() {
        return mObject;
    }


    /**
     * Returns <code>1</code>, the size of a leaf dendrogram.
     *
     * @return <code>1</code>.
     */
    @Override
    public int size() {
        return 1;
    }

    /**
     * Returns the singleton member of this dendrogram.
     *
     * @return The singleton member of this dendrogram.
     */
    @Override
    public Set<E> collectMemberSet() {
        return SmallSet.<E>create(mObject);
    }
    
    @Override
    void split(Collection<Set<E>> resultSet,
               BoundedPriorityQueue<Dendrogram<E>> queue) {
        resultSet.add(this.getMemberSet());
    }
    
    @Override
    int copheneticCorrelation(int i, double[] xs, double[] ys,
                              Distance<? super E> distance) {
        return i;
    }


    @Override
    void addMembers(Set<E> set) {
        set.add(mObject);
    }

    @Override
    void toString(StringBuilder sb, int depth) {
        sb.append(mObject);
    }

    @Override
    void prettyPrint(StringBuilder sb, int depth) {
        indent(sb,depth);
        sb.append(mObject);
    }
}
