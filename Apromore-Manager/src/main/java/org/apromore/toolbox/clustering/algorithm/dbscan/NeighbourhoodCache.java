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
