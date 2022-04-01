/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2016 - 2017 Queensland University of Technology.
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

package org.apromore.plugin.portal;

import org.apromore.plugin.DefaultParameterAwarePlugin;
import org.slf4j.Logger;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import javax.imageio.ImageIO;

/**
 * Default implementation for a parameter-aware PortalPlugin. Subclass this class to create a new PortalPlugin rather than implementing the interface directly.
 * Override all methods that you want to customize. By default the plugin returns the label default and does nothing.
 */
public class DefaultPortalPlugin extends DefaultParameterAwarePlugin implements PortalPlugin {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(DefaultPortalPlugin.class);

    private Map params;

    @Override
    public String getId() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public void setSimpleParams(Map params) {
        this.params = params;
    }

    @Override
    public Map getSimpleParams() {
        return params;
    }

    @Override
    public String getLabel(Locale locale) {
        return "default";
    }

    /**
     * Default implementation will look for the resource <code>/icon.png</code> in the
     * current classloader.  If this resource doesn't exist, a green "plugin" icon will
     * be used instead.
     */
    @Override
    public RenderedImage getIcon() {
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("icon.png");
            if (in == null) {
                in = DefaultPortalPlugin.class.getClassLoader().getResourceAsStream("icon.png");
            }
            BufferedImage icon = ImageIO.read(in);
            in.close();
            return icon;
        } catch (IOException e) {
            LOGGER.warn("Unable to get icon", e);
            return null;
        }
    }

    @Override
    public String getIconPath() {
        return "icon.svg";
    }

    @Override
    public InputStream getResourceAsStream(String resource) {
        return getClass().getClassLoader().getResourceAsStream(resource);
    }

    @Override
    public void execute(PortalContext context) {
    }

    /**
     * {@inheritDoc}
     *
     * @return {@link PortalPlugin$Availability#AVAILABLE} always
     */
    @Override
    public Availability getAvailability() {
        return Availability.AVAILABLE;
    }
}
