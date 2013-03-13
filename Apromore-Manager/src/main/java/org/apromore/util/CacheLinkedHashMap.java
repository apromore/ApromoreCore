package org.apromore.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implemention of the linked hashMap, nothing special just writing my own removeEldestEntry method.
 *
 * @author <a href="mailto:cam.james@gmail.com">Cameron James</a>
 */
public class CacheLinkedHashMap<K,V> extends LinkedHashMap<K,V> {

    // Default value;
    private int maxEntries = 1000;

    /**
     * My implemention of the Remove Eldest Entry.
     * @param eldest the eldest object.
     * @return true if we should get rid of it.
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxEntries;
    }

    /**
     * Set the mat entries the cache is allowed.
     * @param max the new max entry limit.
     */
    public void setMaxEntries(final int max) {
        maxEntries = max;
    }
}
