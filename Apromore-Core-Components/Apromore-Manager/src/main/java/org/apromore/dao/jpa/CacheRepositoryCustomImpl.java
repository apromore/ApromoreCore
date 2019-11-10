package org.apromore.dao.jpa;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;

public class CacheRepositoryCustomImpl {

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
     * Get cached object by key
     *
     * @param key key
     * @return
     */
    public Object get(Object key) {
        ValueWrapper valueWrapper = getCache().get(key);
        if (valueWrapper != null) {
            return getCache().get(key).get();
        }
        return valueWrapper;
    }

    /**
     * Put object into cache
     *
     * @param key key
     * @param value value
     */
    public void put(Object key, Object value) {
        getCache().put(key, value);
    }

    /**
     * Evict object from cache
     *
     * @param key key
     */
    public void evict(Object key) {
        getCache().evict(key);
    }


    /**
     * Get net.sf.ehcache.Cache
     *
     * @return net.sf.ehcache.Cache对象
     */
    public Object getNativeCache() {
        return getCache().getNativeCache();
    }

}
