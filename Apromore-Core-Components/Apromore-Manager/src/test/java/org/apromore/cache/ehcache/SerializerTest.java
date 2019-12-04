package org.apromore.cache.ehcache;

import org.apromore.cache.ehcache.model.Description;
import org.apromore.cache.ehcache.model.Employee;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.util.XTimer;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.spi.service.StatisticsService;
import org.ehcache.core.statistics.CacheStatistics;
import org.ehcache.core.statistics.DefaultStatisticsService;
import org.ehcache.impl.config.persistence.CacheManagerPersistenceConfiguration;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SerializerTest {

    private static final String PERSISTENCE_PATH = "/Users/frank/terracotta";

    @Test
    @Ignore
    public void runTest () throws Exception {

        // tag::persistentKryoSerializer[]
        CacheConfiguration<Long, Employee> cacheConfig =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        Long.class, Employee.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .heap(10, EntryUnit.ENTRIES).offheap(5, MemoryUnit.MB).disk(10, MemoryUnit.MB, true))
                        .withValueSerializer(PersistentKryoSerializer.class)
                        .build();

        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(new CacheManagerPersistenceConfiguration(new File(PERSISTENCE_PATH)))
                .withCache("employeeCache", cacheConfig)
                .build(true);

        Cache<Long, Employee> employeeCache = cacheManager.getCache("employeeCache", Long.class, Employee.class);
        Employee emp =  new Employee(1234, "foo", 23, new Description("bar", 879));
        employeeCache.put(1L, emp);
        assertThat(employeeCache.get(1L), is(emp));

        cacheManager.close();
        cacheManager.init();
        employeeCache = cacheManager.getCache("employeeCache", Long.class, Employee.class);
        assertThat(employeeCache.get(1L), is(emp));
        // end::persistentKryoSerializer[]
    }

    @Test
    public void testPersistentSerializer() throws Exception {
        // tag::persistentSerializerGoodSample[]
        CacheConfiguration<Long, String> cacheConfig =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        Long.class, String.class,
                        ResourcePoolsBuilder.newResourcePoolsBuilder()
                                .heap(10, EntryUnit.ENTRIES).disk(10, MemoryUnit.MB, true))
                        .withValueSerializer(SimplePersistentStringSerializer.class)   // <1>
                        .build();

        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(new CacheManagerPersistenceConfiguration(new File(PERSISTENCE_PATH)))
                .withCache("fruitsCache", cacheConfig)
                .build(true);

        Cache<Long, String> fruitsCache = cacheManager.getCache("fruitsCache", Long.class, String.class);
        fruitsCache.put(1L, "apple");
        fruitsCache.put(2L, "mango");
        fruitsCache.put(3L, "orange");
        assertThat(fruitsCache.get(1L), is("apple"));

        cacheManager.close();
        cacheManager.init();
        fruitsCache = cacheManager.getCache("fruitsCache", Long.class, String.class);
        assertThat(fruitsCache.get(1L), is("apple"));
        // end::persistentSerializerGoodSample[]
    }

    @Test
    public void testKryoSerializer() throws Exception {
        // tag::thirdPartySerializer[]
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        CacheConfiguration<Long, Employee> cacheConfig =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Employee.class, ResourcePoolsBuilder.heap(10))
                        .withValueSerializer(KryoSerializer.class)  // <1>
                        .build();

        Cache<Long, Employee> employeeCache = cacheManager.createCache("employeeCache", cacheConfig);
        Employee emp =  new Employee(1234, "foo", 23, new Description("bar", 879));
        employeeCache.put(1L, emp);
        assertThat(employeeCache.get(1L), is(emp));
        // end::thirdPartySerializer[]
    }

    @Test
    @Ignore
    public void testTransientKryoSerializer() throws Exception {
        // tag::transientKryoSerializer[]
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        CacheConfiguration<Long, Employee> cacheConfig =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Employee.class, ResourcePoolsBuilder.heap(10))
                        .withValueSerializer(TransientKryoSerializer.class)
                        .build();

        Cache<Long, Employee> employeeCache = cacheManager.createCache("employeeCache", cacheConfig);
        Employee emp =  new Employee(1234, "foo", 23, new Description("bar", 879));
        employeeCache.put(1L, emp);
        assertThat(employeeCache.get(1L), is(emp));
        // end::transientKryoSerializer[]
    }

    @Test
    @Ignore
    public void testPersistentKryoSerializer() throws Exception {
        // tag::persistentKryoSerializer[]
        CacheConfiguration<Long, Employee> cacheConfig =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        Long.class, Employee.class,
                        ResourcePoolsBuilder.heap(10).disk(10, MemoryUnit.MB, true))
                        .withValueSerializer(PersistentKryoSerializer.class)
                        .build();

        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(new CacheManagerPersistenceConfiguration(new File(PERSISTENCE_PATH)))
                .withCache("employeeCache", cacheConfig)
                .build(true);

        Cache<Long, Employee> employeeCache = cacheManager.getCache("employeeCache", Long.class, Employee.class);
        Employee emp =  new Employee(1234, "foo", 23, new Description("bar", 879));
        employeeCache.put(1L, emp);
        Employee newEmp = employeeCache.get(1L);
        assertThat(newEmp, is(emp));

        cacheManager.close();
        cacheManager.init();
        employeeCache = cacheManager.getCache("employeeCache", Long.class, Employee.class);
        assertThat(employeeCache.get(1L), is(emp));
        // end::persistentKryoSerializer[]
    }

    @Test
    public void testTransientXLogKryoSerializer() throws Exception {
        // tag::transientKryoSerializer[]
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
        CacheConfiguration<Long, XLog> cacheConfig =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, XLog.class, ResourcePoolsBuilder.heap(10))
                        .withValueSerializer(TransientXLogKryoSerializer.class)
                        .build();

        Cache<Long, XLog> employeeCache = cacheManager.createCache("xLogCache", cacheConfig);

        List<XLog> parsedLog = null;
        XLogImpl xLog;
        XFactory factory = XFactoryRegistry.instance().currentDefault();
        XesXmlParser parser = new XesXmlParser(factory);
        try {
            Path lgPath = Paths.get(ClassLoader.getSystemResource("XES_logs/SepsisCases.xes.gz").getPath());
            parsedLog = parser.parse(new GZIPInputStream(new FileInputStream(lgPath.toFile())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        xLog = (XLogImpl) parsedLog.iterator().next();


        employeeCache.put(1L, xLog);
        assertThat(employeeCache.get(1L), is(xLog));
        // end::transientKryoSerializer[]
    }

    @Test
    public void testPersistentXLogKryoSerializer() throws Exception {
        CacheConfiguration<Long, XLog> cacheConfig =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        Long.class, XLog.class,
                        ResourcePoolsBuilder.heap(1).disk(10000, MemoryUnit.MB, true))
                        .withValueSerializer(PersistentXLogKryoSerializer.class)
                        .build();

        StatisticsService statisticsService = new DefaultStatisticsService();

        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(new CacheManagerPersistenceConfiguration(new File(PERSISTENCE_PATH)))
                .withCache("xLogCache", cacheConfig)
                .using(statisticsService)
                .build(true);

        Cache<Long, XLog> xLogCache = cacheManager.getCache("xLogCache", Long.class, XLog.class);

        XTimer timer = new XTimer();
        List<XLog> parsedLog = null;
        XLog xLog;
        XFactory factory = XFactoryRegistry.instance().currentDefault();
        XesXmlParser parser = new XesXmlParser(factory);
        try {
            Path lgPath = Paths.get(ClassLoader.getSystemResource("XES_logs/SepsisCases.xes.gz").getPath());
            parsedLog = parser.parse(new GZIPInputStream(new FileInputStream(lgPath.toFile())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        xLog = parsedLog.iterator().next();

        timer.stop();
        System.out.println("Imported log:");
        System.out.println("Duration: " + timer.getDurationString());

        timer.start();
        xLogCache.put(1L, xLog);
        timer.stop();
        System.out.println("Cached log:");
        System.out.println("Duration: " + timer.getDurationString());

        XLog newEmp = xLogCache.get(1L);
        assertThat(newEmp, is(xLog));

        cacheManager.close();
        cacheManager.init();
        xLogCache = cacheManager.getCache("xLogCache", Long.class, XLog.class);
        CacheStatistics ehCacheStat = statisticsService.getCacheStatistics("xLogCache");


        timer.start();
        XLog recoveredXLog = xLogCache.get(1L);
        System.out.println("Recovered log:");
        System.out.println("Duration: " + timer.getDurationString());

        // We rely here on the alphabetical order matching the depth order so from highest to lowest we have
        // OnHeap, OffHeap, Disk, Clustered
        System.out.println("OnHeap cache size: " + ehCacheStat.getTierStatistics().get("OnHeap").getOccupiedByteSize());
        System.out.println("OnDisk cache size: " + ehCacheStat.getTierStatistics().get("Disk").getOccupiedByteSize());

        assertThat(recoveredXLog, is(xLog));
    }

}
