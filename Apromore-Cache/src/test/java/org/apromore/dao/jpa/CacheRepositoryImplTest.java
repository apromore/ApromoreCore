/*-
 * #%L
 * This file is part of "Apromore Core".
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

package org.apromore.dao.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.apromore.cache.ehcache.CacheRepository;
import org.apromore.cache.ehcache.CacheRepositoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.cache.ehcache.EhCacheCacheManager;

class CacheRepositoryImplTest {

    private static EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
    private String cacheName = "xlog";
    private net.sf.ehcache.CacheManager cacheManager = CacheManager.getInstance();

    @BeforeEach
    void setUp() throws Exception {
    	cacheName = "xlog";
        ehCacheCacheManager.setCacheManager(cacheManager);

    }

    @Test
    void get() {

        CacheRepository cacheRepository = new CacheRepositoryImpl();
        ((CacheRepositoryImpl) cacheRepository).setCacheName("xlog");
        ((CacheRepositoryImpl) cacheRepository).setEhCacheCacheManager(ehCacheCacheManager);

        String keyPut = "keyGet1";
        String valPut = "valGet1";
        cacheRepository.put(keyPut, valPut);
        assertEquals(valPut, cacheRepository.get(keyPut), "Same value should be retrieved ");
    }

    @Test
    void put() {

        CacheRepository cacheRepository = new CacheRepositoryImpl();
        ((CacheRepositoryImpl) cacheRepository).setCacheName(cacheName);
        ((CacheRepositoryImpl) cacheRepository).setEhCacheCacheManager(ehCacheCacheManager);

        String keyPut = "keyPut1";
        String valPut = "valPut1";
        cacheRepository.put(keyPut, valPut);
        assertEquals(valPut, cacheRepository.get(keyPut), "Same value should be retrieved ");
    }

    @Test
    void evict() {

        CacheRepository cacheRepository = new CacheRepositoryImpl();
        ((CacheRepositoryImpl) cacheRepository).setCacheName(cacheName);
        ((CacheRepositoryImpl) cacheRepository).setEhCacheCacheManager(ehCacheCacheManager);

        String keyRemove = "keyRemove1";
        String valRemove = "valRemove1";
        cacheRepository.put(keyRemove, valRemove);
        long size = cacheRepository.getMemoryStoreSize();
        assertEquals(valRemove, cacheRepository.get(keyRemove), "Same value should be retrieved ");
        System.out.println("1 ->" + cacheRepository.getMemoryStoreSize());

        cacheRepository.evict(keyRemove);
        System.out.println("2 ->" + cacheRepository.getMemoryStoreSize());
        assertEquals(size - 1, cacheRepository.getMemoryStoreSize(), "size should reduce by 1");
        cacheRepository.evict(keyRemove);
        assertEquals(size - 1, cacheRepository.getMemoryStoreSize(),
            "Non existing Key removal, size should be the same as last time");
        System.out.println("3 ->" + cacheRepository.getMemoryStoreSize());
    }

    @Test
    @Disabled("For pressure testing only")
    void testSizing() {

        CacheRepository cacheRepository = new CacheRepositoryImpl();
        ((CacheRepositoryImpl) cacheRepository).setCacheName(cacheName);
        ((CacheRepositoryImpl) cacheRepository).setEhCacheCacheManager(ehCacheCacheManager);

        for (int i = 0; i < 3000; i++) {
            if ((i % 100) == 0) {
                System.out.println("heatbeat " + i);
                stats((net.sf.ehcache.Ehcache) cacheRepository.getNativeCache());
            }
            cacheRepository.put(i, new byte[1024 * 1024]);
        }
        stats((net.sf.ehcache.Ehcache) cacheRepository.getNativeCache());
        assertTrue(true);
    }

    private void stats(Ehcache ehcache) {
        System.out.println("OnHeapSize=" + ehcache.calculateInMemorySize() / 1024 / 1024 + "MB, OnHeapElements="
                + ehcache.getMemoryStoreSize());
        System.out.println("OffHeapSize=" + ehcache.calculateOffHeapSize() / 1024 / 1024 + "MB, OffHeapElements="
                + ehcache.getOffHeapStoreSize());
        System.out.println("DiskStoreSize=" + ehcache.calculateOnDiskSize() / 1024 / 1024 + "MB, DiskStoreElements="
                + ehcache.getDiskStoreSize());
    }
}
