/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.portal;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * OSGi-related servlet methods.
 */
public abstract class OSGi {

    /**
     * Access an OSGi configuration from a servlet context.
     *
     * @param pid  the OSGi CM persistent identifier for the configuration; in Apromore, this is "site"
     *     to access <code>site.cfg</code>; never <code>null</code>
     * @param servletContext  never <code>null</code>
     * @return the contents of the configuration as a mapping from property keys to values; all
     *     values are actually {@link String}s because we're using a very early edition of OSGi.
     * @throws IOException if the configuration can't be read
     */
    public static Map<String, Object> getConfiguration(final String pid, final ServletContext servletContext) throws IOException {
        BundleContext bundleContext = (BundleContext) servletContext.getAttribute("osgi-bundlecontext");
        ServiceReference serviceReference = bundleContext.getServiceReference(ConfigurationAdmin.class);
        ConfigurationAdmin configurationAdmin = (ConfigurationAdmin) bundleContext.getService(serviceReference);

        return toMap(configurationAdmin.getConfiguration(pid).getProperties());
    }

    /**
     * Convert {@link Dictionary} to {@link Map}.
     *
     * @param dict  an arbitrary dictionary, or <code>null</code>
     * @return an equivalent map, or <code>null</code> if <var>dict</var> was <code>null</code>
     */
    private static <K, V> Map<K, V> toMap(final Dictionary<K, V> dict) {
        if (dict == null) {
            return null;
        }
        
        Map<K, V> map = new HashMap<>();
        for (Enumeration<K> e = dict.keys(); e.hasMoreElements();) {
            K key = e.nextElement();
            map.put(key, dict.get(key));
        }

        return map;
    }
}
