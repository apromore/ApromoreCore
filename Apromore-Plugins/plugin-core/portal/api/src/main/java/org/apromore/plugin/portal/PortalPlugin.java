/*
 * Copyright © 2009-2018 The Apromore Initiative.
 *
 * This file is part of "Apromore".
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
     * Label of the group of plug-ins this one belongs to.
     *
     * This is used for the menu.
     *
     * @param locale (optional locale)
     * @return
     */
    String getGroupLabel(Locale locale);

    /**
     * Call-back that is called when this plug-in is executed.
     *
     * @param context which provides access to the portal (e.g., selected items, create windows, ..)
     */
    void execute(PortalContext context);

}
