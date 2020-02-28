/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.context;

import org.apromore.plugin.portal.PortalPlugin;
import org.zkoss.spring.SpringUtil;

import java.util.List;

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

}
