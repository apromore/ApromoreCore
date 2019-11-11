package org.apromore.dao;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Repository;

@Repository
public interface CacheRepository {


    EhCacheCacheManager getEhCacheCacheManager();

    void setEhCacheCacheManager(EhCacheCacheManager ehCacheCacheManager);

    String getCacheName();

    void setCacheName(String cacheName);

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
}
