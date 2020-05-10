/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 *
 */
package org.apromore.toolbox.clustering.algorithm.dbscan;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <a href="mailto:chathura.ekanayake@gmail.com">Chathura C. Ekanayake</a>
 */
public class NeighbourhoodCache {

    private static final Logger log = LoggerFactory.getLogger(NeighbourhoodCache.class);

    private static final int cacheSize = 8000;

    private Map<String, List<FragmentDataObject>> cache = new HashMap<>();
    private Queue<String> fifo = new LinkedList<>();

    public void add(String fragmentId, List<FragmentDataObject> neighborhood) {
        if (cache.containsKey(fragmentId)) {
            return;
        }

        if (cache.size() >= cacheSize) {
            String oldestFragmentId = fifo.poll();
            cache.remove(oldestFragmentId);
        }
        cache.put(fragmentId, neighborhood);
    }

    public List<FragmentDataObject> getNeighborhood(Integer fragmentId) {
        List<FragmentDataObject> neighborhood = cache.get(fragmentId.toString());
        if (neighborhood != null) {
            log.debug("Neighborhood cache hit for fragment: " + fragmentId);
        }
        return neighborhood;
    }

    public void remove(String fragmentId) {
        cache.remove(fragmentId);
    }

    public void clear() {
        cache.clear();
        fifo.clear();
    }
}
