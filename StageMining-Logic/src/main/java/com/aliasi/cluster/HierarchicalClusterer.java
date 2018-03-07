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

import java.util.Set;

/**
 * The <code>HierarchicalClusterer</code> interface defines a means of
 * clustering a set of objects into a hierarchy of clusters.  
 *
 * <p>The class {@link Dendrogram} is used to represent the results of
 * a hierarchical clustering.  From a dendrogram, it is possible to
 * retrieve clusterings as sets of sets as defined in the
 * super-interface {@link Clusterer}.  These clusterings may be
 * retrieved by requiring clusters to have elements within a given
 * distance, or by taking the best <code>k</code> clusters.  See the
 * {@link Dendrogram} class documentation for more information, as
 * well as the hieararchical clusterer implementations, which explain
 * how they implement <code>Clusterer</code> in terms of
 * <code>HierarchicalClusterer</code>.
 *
 * @author  Bob Carpenter
 * @version 3.0
 * @since   LingPipe2.0
 * @param <E> the type of objects being clustered
 */
public interface HierarchicalClusterer<E> extends Clusterer<E> {
    
    /**
     * Return the dendrogram representing the complete hierarchical
     * clustering of the specified elements.  
     *
     * @param elements Set of elements to cluster.
     * @return The dendrogram representing the hierarchical clustering
     * of the elements.
     */
    public Dendrogram<E> hierarchicalCluster(Set<? extends E> elements);
    
}

