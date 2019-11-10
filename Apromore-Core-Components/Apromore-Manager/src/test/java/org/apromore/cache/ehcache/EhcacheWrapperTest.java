package org.apromore.cache.ehcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apromore.util.EhcacheWrapper;
import org.junit.Test;
import junit.framework.Assert;

/**
 * Test the Ehcache Wrapper class.
 *
 * @author <a href="mailto:yuohuo@gmail.com">Frank Ma</a>
 */
public class EhcacheWrapperTest {

    private String cacheName = "xlog";
    private static CacheManager manager;

    @org.junit.Before
    public void setUp() throws Exception {
        manager = CacheManager.create();

    }

    @org.junit.After
    public void tearDown() throws Exception {
        manager.shutdown();
    }

    @Test
    public void testPut() throws Exception {
        EhcacheWrapper<String,Object> ecw = new EhcacheWrapper<String, Object>(cacheName, manager);
        String keyPut = "keyPut1";
        String valPut = "valPut1";
        ecw.put(keyPut, valPut);
        Assert.assertTrue("Same value should be retrieved ", valPut.equals(ecw.get(keyPut)));
    }

    @Test
    public void testGet() throws Exception {
        EhcacheWrapper<String,Object> ecw = new EhcacheWrapper<String, Object>(cacheName, manager);
        String keyPut = "keyGet1";
        String valPut = "valGet1";
        ecw.put(keyPut, valPut);
        Assert.assertTrue("Same value should be retrieved ", valPut.equals(ecw.get(keyPut)));

    }

    @Test
    public void testRemove() throws Exception {
        EhcacheWrapper<String,Object> ecw = new EhcacheWrapper<String, Object>(cacheName, manager);
        String keyRemove = "keyRemove1";
        String valRemove = "valRemove1";
        ecw.put(keyRemove, valRemove);
        int size = ecw.getSize();
        Assert.assertTrue("Same value should be retrieved ", valRemove.equals(ecw.get(keyRemove)));
        Assert.assertTrue("Key removal should return true", ecw.remove(keyRemove));
        Assert.assertFalse("Non existing Key removal should return false", ecw.remove(keyRemove));
        Assert.assertEquals("size should reduce by 1", size-1, ecw.getSize());

    }

    @Test
    public void testGetSize() throws Exception {
        EhcacheWrapper<String,Object> ecw = new EhcacheWrapper<String, Object>(cacheName, manager);
        String keySize = "keyRemove1";
        String valSize = "valRemove1";
        int size = ecw.getSize();
        ecw.put(keySize, valSize);
        Assert.assertEquals("size should increase by 1", size+1, ecw.getSize());
    }

    @Test
    public void testGetCache() throws Exception {
        EhcacheWrapper<String,Object> ecw = new EhcacheWrapper<String, Object>(cacheName, manager);
        Ehcache wraperCache = ecw.getCache();
        Assert.assertEquals("Wrapper and manager cache names should be same", wraperCache.getName(), cacheName);
        Assert.assertEquals("Wrapper and manager cache managers should be same", wraperCache.getCacheManager(), manager);
    }

    @Test
    public void testSizing()
    {
        EhcacheWrapper<String,Object> ecw = new EhcacheWrapper<String, Object>(cacheName, manager);
        Ehcache wraperCache = ecw.getCache();
//        CacheManager cacheManager = CacheManager.getInstance();
//        Ehcache ehcache = manager.getEhcache( "testCache" );
        for ( int i = 0; i < 30000; i++ )
        {
            if ( ( i % 1000 ) == 0 )
            {
                System.out.println( "heatbeat " + i );
                stats( wraperCache );
            }
            wraperCache.put( new Element( i, new byte[1024] ) );
        }
        stats( wraperCache );
        Assert.assertTrue( true );
    }

    private void stats( Ehcache ehcache )
    {
        System.out.println( "OnHeapSize=" + ehcache.calculateInMemorySize() + ", OnHeapElements="
                + ehcache.getMemoryStoreSize() );
        System.out.println( "OffHeapSize=" + ehcache.calculateOffHeapSize() + ", OffHeapElements="
                + ehcache.getOffHeapStoreSize() );
        System.out.println( "DiskStoreSize=" + ehcache.calculateOnDiskSize() + ", DiskStoreElements="
                + ehcache.getDiskStoreSize() );
    }
}