package org.apromore.dao.jpa;

import org.apromore.exception.CacheException;
import org.apromore.dao.CacheRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public CacheRepositoryImpl(EhCacheCacheManager ehCacheCacheManager, String cacheName) {
        this.ehCacheCacheManager = ehCacheCacheManager;
        this.cacheName = cacheName;
    }

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
