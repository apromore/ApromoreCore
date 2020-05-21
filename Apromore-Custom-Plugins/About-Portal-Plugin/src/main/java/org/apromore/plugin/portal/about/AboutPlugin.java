/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
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

package org.apromore.plugin.portal.about;

import java.util.*;

import org.apromore.model.PluginInfo;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.ConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

public class AboutPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(AboutPlugin.class);

    private String label = "About Apromore";
    private String groupLabel = "About";

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
    public String getIconPath() {
        return "icon.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        LOGGER.info("Debug");

        try {
            ConfigBean config = (ConfigBean) SpringUtil.getBean("portalConfig");
            Map args = new HashMap();
            args.put("community", config.isCommunity());
            args.put("edition", config.getVersionEdition());
            args.put("version", config.getMajorVersionNumber() + " (commit " + config.getMinorVersionNumber() + " built on " + config.getVersionBuildDate() + ")");
            final Window pluginWindow = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/about.zul", null, args);
            pluginWindow.setAttribute("version", "dummy");

            Button buttonOk = (Button) pluginWindow.getFellow("ok");
            buttonOk.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    pluginWindow.detach();
                }
            });
            pluginWindow.doModal();

        } catch (Exception e) {
            Messagebox.show(e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
