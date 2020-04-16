/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
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

package org.apromore.cache.ehcache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class EhcacheWrapper<K, V> {
    private final String cacheName;
    private final CacheManager cacheManager;

    public EhcacheWrapper(final String cacheName, final CacheManager cacheManager) {
        this.cacheName = cacheName;
        this.cacheManager = cacheManager;
    }

    public void put(final K key, final V value) {
        getCache().put(new Element(key, value));
    }

    public V get(final K key) {
        Element element = getCache().get(key);
        if (element != null) {
            return (V) element.getObjectValue();
        }
        return null;
    }

    public boolean remove(K key) {
        return getCache().remove(key);
    }

    public int getSize() {
        return getCache().getSize();
    }

    public Ehcache getCache() {
        return cacheManager.getEhcache(cacheName);
    }
}
