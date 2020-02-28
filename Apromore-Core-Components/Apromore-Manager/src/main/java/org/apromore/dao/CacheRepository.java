/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
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
 */

package org.apromore.dao;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheRepository {


    EhCacheCacheManager getEhCacheCacheManager();

    String getCacheName();

    /**
     * Get cached object by key
     *
     * @param key key
     * @return
     */
    Object get(Object key);

    /**
     * Put object into cache
     *
     * @param key   key
     * @param value value
     */
    void put(Object key, Object value);

    /**
     * Evict object from cache
     *
     * @param key key
     */
    void evict(Object key);


    /**
     * Get net.sf.ehcache.Cache
     *
     * @return net.sf.ehcache.Cache
     */
    Object getNativeCache();

    /**
     * Returns the size (in bytes) that this EhCache is using in memory (RAM), or <code>-1</code> if that
     * number is unknown or cannot be calculated.
     *
     * @return the size (in bytes) that this EhCache is using in memory (RAM), or <code>-1</code> if that
     *         number is unknown or cannot be calculated.
     */
    long getMemoryUsage();

    /**
     * Returns the number of elements in the memory store.
     *
     * @return the number of elements in the memory store.
     */
    long getMemoryStoreSize();
}
