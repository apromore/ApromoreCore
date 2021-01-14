/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.portal.context;

import org.apromore.plugin.portal.PortalPlugin;
import org.zkoss.spring.SpringUtil;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Looking up portal plug-ins
 */
public class PortalPluginResolver {

    public static List<PortalPlugin> resolve() {
        Object portalPlugins = SpringUtil.getBean("portalPlugins");
        if (portalPlugins != null) {
            return (List<PortalPlugin>) portalPlugins;
        } else {
            throw new RuntimeException("Could not get list of portal plug-ins!");
        }
    }

    public static Map<String, PortalPlugin> getPortalPluginMap() {
        Map<String, PortalPlugin> portalPluginMap = new HashMap<String, PortalPlugin>();

        Object portalPlugins = SpringUtil.getBean("portalPlugins");
        if (portalPlugins != null) {
            for (PortalPlugin plugin :  (List<PortalPlugin>) portalPlugins) {
                String key = Optional.ofNullable(plugin.getID()).orElse(plugin.getLabel(Locale.getDefault()));
                portalPluginMap.put(key, plugin);
            }
            return portalPluginMap;
        } else {
            throw new RuntimeException("Could not get list of portal plug-ins!");
        }
    }

}
