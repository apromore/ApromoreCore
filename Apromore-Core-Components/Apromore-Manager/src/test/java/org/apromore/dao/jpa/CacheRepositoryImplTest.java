package org.apromore.dao.jpa;

import junit.framework.Assert;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apromore.cache.ehcache.EhcacheWrapper;
import org.apromore.dao.CacheRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import static org.junit.Assert.*;

public class CacheRepositoryImplTest {

    private String cacheName = "test";
    private static EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
    private net.sf.ehcache.CacheManager cacheManager = CacheManager.getInstance();

    @Before
    public void setUp() throws Exception {
        ehCacheCacheManager.setCacheManager(cacheManager);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getEhCacheCacheManager() {
    }

    @Test
    public void setEhCacheCacheManager() {
    }

    @Test
    public void getCacheName() {
    }

    @Test
    public void setCacheName() {
    }

    @Test
    public void get() {
        CacheRepository cacheRepository = new CacheRepositoryImpl(ehCacheCacheManager, cacheName);
        String keyPut = "keyGet1";
        String valPut = "valGet1";
        cacheRepository.put(keyPut, valPut);
        Assert.assertTrue("Same value should be retrieved ", valPut.equals(cacheRepository.get(keyPut)));
    }

    @Test
    public void put() {
        CacheRepository cacheRepository = new CacheRepositoryImpl(ehCacheCacheManager, cacheName);
        String keyPut = "keyPut1";
        String valPut = "valPut1";
        cacheRepository.put(keyPut, valPut);
        Assert.assertTrue("Same value should be retrieved ", valPut.equals(cacheRepository.get(keyPut)));
    }

    @Test
    public void evict() {
        CacheRepository cacheRepository = new CacheRepositoryImpl(ehCacheCacheManager, cacheName);
        String keyRemove = "keyRemove1";
        String valRemove = "valRemove1";
        cacheRepository.put(keyRemove, valRemove);
        long size = cacheRepository.getMemoryStoreSize();
        Assert.assertTrue("Same value should be retrieved ", valRemove.equals(cacheRepository.get(keyRemove)));
        System.out.println("1 ->" + cacheRepository.getMemoryStoreSize());

        cacheRepository.evict(keyRemove);
        System.out.println("2 ->" + cacheRepository.getMemoryStoreSize());
        Assert.assertEquals("size should reduce by 1", size-1, cacheRepository.getMemoryStoreSize());
        cacheRepository.evict(keyRemove);
        Assert.assertEquals("Non existing Key removal, size should be the same as last time", size-1,
                cacheRepository.getMemoryStoreSize());
        System.out.println("3 ->" + cacheRepository.getMemoryStoreSize());
    }

    @Test
    public void getNativeCache() {
    }

    @Test
    public void getMemoryUsage() {
    }

    @Test
    public void getMemoryStoreSize() {
    }

    @Test
    public void testSizing()
    {
//        EhcacheWrapper<String,Object> ecw = new EhcacheWrapper<String, Object>(cacheName, manager);
//        Ehcache wraperCache = ecw.getCache();
////        CacheManager cacheManager = CacheManager.getInstance();
////        Ehcache ehcache = manager.getEhcache( "testCache" );

        CacheRepository cacheRepository = new CacheRepositoryImpl(ehCacheCacheManager, cacheName);

        for ( int i = 0; i < 30000; i++ )
        {
            if ( ( i % 1000 ) == 0 )
            {
                System.out.println( "heatbeat " + i );
                System.out.println("MemoryStoreSize = " + cacheRepository.getMemoryStoreSize());
                System.out.println("MemoryUsage = " + cacheRepository.getMemoryUsage() / 1024 / 1024 + "MB");
            }
            cacheRepository.put(i, new byte[1024 * 1000]);
        }
        System.out.println("MemoryStoreSize = " + cacheRepository.getMemoryStoreSize());
        System.out.println("MemoryUsage = " + cacheRepository.getMemoryUsage() / 1024 / 1024 + "MB");
        Assert.assertTrue( true );
    }
}