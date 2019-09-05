/*
 * Copyright © 2009-2019 The Apromore Initiative.
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

package org.apromore.plugin.portal.account;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.io.IOException;
import java.util.Locale;
import javax.imageio.ImageIO;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangeThemePlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(ChangeThemePlugin.class);

    private String label = "Change Theme";
    private String groupLabel = "Account";

    // PortalPlugin overrides

    @Override
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
    }

    @Override
    public RenderedImage getIcon() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("/change-theme-icon.png")) {
            BufferedImage icon = ImageIO.read(in);
            return icon;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getIconPath() {
        return "change-theme-icon.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        new ChangeThemeController(portalContext);
    }
}
