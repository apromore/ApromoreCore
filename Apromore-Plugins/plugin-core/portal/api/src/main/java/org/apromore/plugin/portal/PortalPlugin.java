/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
 * Copyright (C) 2018, 2020 The University of Melbourne.
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
package org.apromore.plugin.portal;

import org.apromore.plugin.ParameterAwarePlugin;

import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.util.Locale;

/**
 * Plug-in interface for a Portal functionality (i.e., one command) that will appear somewhere in the portal. For example, in the main menu.
 */
public interface PortalPlugin extends ParameterAwarePlugin {

    /**
     * Label of the plug-in.
     *
     * This is used for the menu item.
     *
     * @param locale (optional locale)
     * @return
     */
    String getLabel(Locale locale);

    /**
     * Icon for the plug-in.
     *
     * This is used for the menu item.
     *
     * @return PNG-formatted image file data, roughly 16x16 size
     */
    RenderedImage getIcon();

    /**
     * @return pathname of the icon resource in this bundle
     */
    String getIconPath();

    /**
     * Label of the group of plug-ins this one belongs to.
     *
     * This is used for the menu.
     *
     * @param locale (optional locale)
     * @return
     */
    String getGroupLabel(Locale locale);

    /**
     * Access a resource provided by the plugin.
     *
     * @param resource  name of the resource
     * @return the resource, or null if absent
     */
    InputStream getResourceAsStream(String resource);

    /**
     * Call-back that is called when this plug-in is executed.
     *
     * @param context which provides access to the portal (e.g., selected items, create windows, ..)
     */
    void execute(PortalContext context);

    /**
     * @param context which provides access to the portal (e.g., selected items, create windows, ..)
     * @return whether the plugin function is available for use in the given <var>context</var>
     */
    Availability getAvailability(PortalContext context);

    /**
     * Return value for {@link #getAvailability).
     *
     * <dl>
     * <dt>AVAILABLE</dt>  <dd>The function can be used, and should have an enabled menu item.</dd>
     * <dt>DISABLED</dt>   <dd>The function can't currently be used, but user action might make it available.  It should have a disabled menu item.</dd>
     * <dt>UNAVILABLE</dt> <dd>The function can't be used.  It should not have a menu item.</dd>
     * </dl>
     */
    enum Availability {
        AVAILABLE, DISABLED, UNAVAILABLE
    }
}
