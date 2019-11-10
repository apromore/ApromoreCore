package org.apromore.cache.ehcache;

import org.apromore.cache.Cache;
import org.apromore.util.LifecycleUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EhCacheManagerTest {

    private EhCacheManager cacheManager;

    @Before
    public void setUp() {
        cacheManager = new EhCacheManager();
    }

    @After
    public void tearDown() {
        LifecycleUtils.destroy(cacheManager);
    }

    @Test
    public void testCacheManagerCreationDuringInit() {
        net.sf.ehcache.CacheManager ehCacheManager = cacheManager.getCacheManager();
        assertNull(ehCacheManager);
        cacheManager.init();
        //now assert that an internal CacheManager has been created:
        ehCacheManager = cacheManager.getCacheManager();
        assertNotNull(ehCacheManager);
    }

    @Test
    public void testLazyCacheManagerCreationWithoutCallingInit() {
        net.sf.ehcache.CacheManager ehCacheManager = cacheManager.getCacheManager();
        assertNull(ehCacheManager);

        //don't call init here - the ehcache CacheManager should be lazily created
        //because of the default Apromore ehcache.xml file in the classpath.  Just acquire a cache:
        Cache<String, String> cache = cacheManager.getCache("test");

        //now assert that an internal CacheManager has been created:
        ehCacheManager = cacheManager.getCacheManager();
        assertNotNull(ehCacheManager);

        assertNotNull(cache);
        cache.put("hello", "world");
        String value = cache.get("hello");
        assertNotNull(value);
        assertEquals(value, "world");
    }

}