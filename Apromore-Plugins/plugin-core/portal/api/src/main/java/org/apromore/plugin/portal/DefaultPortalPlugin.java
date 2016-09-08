/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal;

import org.apromore.plugin.DefaultParameterAwarePlugin;

import java.util.Locale;

/**
 * Default implementation for a parameter-aware PortalPlugin. Subclass this class to create a new PortalPlugin rather than implementing the interface directly.
 * Override all methods that you want to customize. By default the plugin returns the label default and does nothing.
 */
public class DefaultPortalPlugin extends DefaultParameterAwarePlugin implements PortalPlugin {

    @Override
    public String getLabel(Locale locale) {
        return "default";
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return "Plugins";
    }

    @Override
    public void execute(PortalContext context) {
    }

}
