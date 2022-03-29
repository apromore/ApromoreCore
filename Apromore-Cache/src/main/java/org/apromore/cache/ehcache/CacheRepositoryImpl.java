/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
 * %%
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

package org.apromore.cache.ehcache;

import org.apromore.cache.exception.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;

public class CacheRepositoryImpl implements CacheRepository {

    /**
     * This class's private log instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheRepositoryImpl.class);

    /**
     *  EhCache Cache Manager
     */
    private EhCacheCacheManager ehCacheCacheManager;

    /**
     * Name of cache
     */
    private String cacheName;

    public EhCacheCacheManager getEhCacheCacheManager() {
        return ehCacheCacheManager;
    }

    public void setEhCacheCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.ehCacheCacheManager = ehCacheCacheManager;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    /**
     * Get Cache instance
     *
     * @return
     */
    private Cache getCache() {
        return this.ehCacheCacheManager.getCache(this.cacheName);
    }

    /**
     * Gets a value of an element which matches the given key.
     *
     * @param key the key of the element to return.
     * @return The value placed into the cache with an earlier put, or null if not found or expired
     */
    public Object get(Object key) throws CacheException {
        try {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Getting object from cache [" + LOGGER.getName() + "] for key [" + key + "]");
            }
            if (key == null) {
                return null;
            } else{
                ValueWrapper valueWrapper = getCache().get(key);
                if (valueWrapper == null) {
                    if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Element for [" + key + "] is null.");
                    }
                    return null;
                } else {
                    return valueWrapper.get();
                }
            }

        } catch (Throwable t) {
            throw new CacheException(t);
        }

    }

    /**
     * Put an object into the cache
     *
     * @param key the key
     * @param value the value
     */
    public void put(Object key, Object value) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Putting object in cache [" + cacheName + "] for key [" + key + "]");
        }
        getCache().put(key, value);
    }

    /**
     * Removes the element which matches the key.
     *
     * <p>If no element matches, nothing is removed and no Exception is thrown.</p>
     *
     * @param key the key of the element to remove
     */
    public void evict(Object key) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Removing object from cache [" + cacheName + "] for key [" + key + "]");
        }
        getCache().evict(key);
    }


    /**
     * Get net.sf.ehcache.Cache
     *
     * @return net.sf.ehcache.Cache
     */
    public Object getNativeCache() {
        return getCache().getNativeCache();
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
            net.sf.ehcache.Cache ehcache = (net.sf.ehcache.Cache) this.getNativeCache();
            return ehcache.calculateInMemorySize();
        }
        catch (Throwable t) {
            return -1;
        }
    }

    /**
     * Returns the number of elements in the memory store.
     *
     * @return the number of elements in the memory store.
     */
    public long getMemoryStoreSize() {
        try {
            net.sf.ehcache.Cache ehcache = (net.sf.ehcache.Cache) this.getNativeCache();
            return ehcache.getMemoryStoreSize();
        }
        catch (Throwable t) {
            throw new CacheException(t);
        }
    }

}
