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
 * The <code>Clusterer</code> interface defines a means of clustering
 * a set of input elements
 *
 * <p>Typically, the clusters returned by a clusterer will form a
 * partition of the input elements.  This means that the clusters
 * will be disjoint (that is, have an empty intersection), and
 * that <code>elements</code> and <code>foundElements</code> will
 * be equal after the following:
 *
 * <blockquote><pre>
 * Clusterer&lt;E&gt; clusterer = ...
 * Set&lt;E&gt; elements = ...
 * Set&lt;Set&lt;E&gt;&gt; clusters = clusterer.cluster(elements);
 * Set&lt;E&gt; foundElements = new HashSet&lt;E&gt;();
 * for (Set&lt;E&gt; cluster : clusters)
 *     foundElements.addAll(cluster);
 * elements.equals(foundElements); // true
 * </pre></blockquote>
 *

 * @author  Bob Carpenter
 * @version 3.0
 * @since   LingPipe2.0
 * @param <E> the type of objects being clustered
 */
public interface Clusterer<E> {

    /**
     * Return a clustering of the specified set of elements.
     *
     * @param elements The objects to cluster.
     * @return A clustering of the specified elements.
     */
    public Set<Set<E>> cluster(Set<? extends E> elements);
 
}


