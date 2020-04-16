/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.cache.ehcache;

import net.sf.ehcache.Element;
import org.apromore.cache.Cache;

import java.util.*;

import org.apromore.exception.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apromore {@link org.apromore.cache.Cache} implementation that wraps an {@link net.sf.ehcache.Ehcache} instance.
 *
 */
public class EhCache<K, V> implements Cache<K, V> {

    /**
     * Private internal log instance.
     */
    private static final Logger log = LoggerFactory.getLogger(EhCache.class);

    /**
     * The wrapped Ehcache instance.
     */
    private net.sf.ehcache.Ehcache cache;

    /**
     * Constructs a new EhCache instance with the given cache.
     *
     * @param cache - delegate EhCache instance this Apromore cache instance will wrap.
     */
    public EhCache(net.sf.ehcache.Ehcache cache) {
        if (cache == null) {
            throw new IllegalArgumentException("Cache argument cannot be null.");
        }
        this.cache = cache;
    }

    /**
     * Gets a value of an element which matches the given key.
     *
     * @param key the key of the element to return.
     * @return The value placed into the cache with an earlier put, or null if not found or expired
     */
    public V get(K key) throws CacheException {
        try {
            if (log.isTraceEnabled()) {
                log.trace("Getting object from cache [" + cache.getName() + "] for key [" + key + "]");
            }
            if (key == null) {
                return null;
            } else {
                Element element = cache.get(key);
                if (element == null) {
                    if (log.isTraceEnabled()) {
                        log.trace("Element for [" + key + "] is null.");
                    }
                    return null;
                } else {
                    //noinspection unchecked
                    return (V) element.getObjectValue();
                }
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Puts an object into the cache.
     *
     * @param key   the key.
     * @param value the value.
     */
    public V put(K key, V value) throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Putting object in cache [" + cache.getName() + "] for key [" + key + "]");
        }
        try {
            V previous = get(key);
            Element element = new Element(key, value);
            cache.put(element);
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Removes the element which matches the key.
     *
     * <p>If no element matches, nothing is removed and no Exception is thrown.</p>
     *
     * @param key the key of the element to remove
     */
    public V remove(K key) throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Removing object from cache [" + cache.getName() + "] for key [" + key + "]");
        }
        try {
            V previous = get(key);
            cache.remove(key);
            return previous;
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Removes all elements in the cache, but leaves the cache in a useable state.
     */
    public void clear() throws CacheException {
        if (log.isTraceEnabled()) {
            log.trace("Clearing all objects from cache [" + cache.getName() + "]");
        }
        try {
            cache.removeAll();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    public int size() {
        try {
            return cache.getSize();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    public Set<K> keys() {
        try {
            @SuppressWarnings({"unchecked"})
            List<K> keys = cache.getKeys();
            if (!isEmpty(keys)) {
                return Collections.unmodifiableSet(new LinkedHashSet<K>(keys));
            } else {
                return Collections.emptySet();
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    public Collection<V> values() {
        try {
            @SuppressWarnings({"unchecked"})
            List<K> keys = cache.getKeys();
            if (!isEmpty(keys)) {
                List<V> values = new ArrayList<V>(keys.size());
                for (K key : keys) {
                    V value = get(key);
                    if (value != null) {
                        values.add(value);
                    }
                }
                return Collections.unmodifiableList(values);
            } else {
                return Collections.emptyList();
            }
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Returns the size (in bytes) that this EhCache is using in memory (RAM), or <code>-1</code> if that
     * number is unknown or cannot be calculated.
     *
     * @return the size (in bytes) that this EhCache is using in memory (RAM), or <code>-1</code> if that
     *         number is unknown or cannot be calculated.
     */
    public long getMemoryUsage() {
        try {
            return cache.calculateInMemorySize();
        }
        catch (Throwable t) {
            return -1;
        }
    }

    /**
     * Returns the size (in bytes) that this EhCache's memory store is using (RAM), or <code>-1</code> if
     * that number is unknown or cannot be calculated.
     *
     * @return the size (in bytes) that this EhCache's memory store is using (RAM), or <code>-1</code> if
     *         that number is unknown or cannot be calculated.
     */
    public long getMemoryStoreSize() {
        try {
            return cache.getMemoryStoreSize();
        }
        catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Returns the size (in bytes) that this EhCache's disk store is consuming or <code>-1</code> if
     * that number is unknown or cannot be calculated.
     *
     * @return the size (in bytes) that this EhCache's disk store is consuming or <code>-1</code> if
     *         that number is unknown or cannot be calculated.
     */
    public long getDiskStoreSize() {
        try {
            return cache.getDiskStoreSize();
        } catch (Throwable t) {
            throw new CacheException(t);
        }
    }

    /**
     * Returns &quot;EhCache [&quot; + cache.getName() + &quot;]&quot;
     *
     * @return &quot;EhCache [&quot; + cache.getName() + &quot;]&quot;
     */
    public String toString() {
        return "EhCache [" + cache.getName() + "]";
    }

    //////////////////////////
    // From CollectionUtils //
    //////////////////////////

    private static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }
}
