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
