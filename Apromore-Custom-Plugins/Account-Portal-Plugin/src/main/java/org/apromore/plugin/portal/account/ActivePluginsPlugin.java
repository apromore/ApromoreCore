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

import java.util.*;
import org.apromore.manager.client.ManagerService;
import org.apromore.model.PluginInfo;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

public class ActivePluginsPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(ActivePluginsPlugin.class);

    private String label = "Active plugins";
    private String groupLabel = "Account";

    private ManagerService managerService;

    public ActivePluginsPlugin(ManagerService managerService) {
        this.managerService = managerService;
    }

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
        return "active-plugins-icon.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        LOGGER.info("Debug");

        try {
            final Window pluginWindow = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/activePlugins.zul", null, null);
            Listbox infoListBox = (Listbox) pluginWindow.getFellow("pluginInfoListBox");

            List<PluginInfo> installedPlugins = new ArrayList<>(managerService.readInstalledPlugins(null));
            infoListBox.setModel(new ListModelList<>(installedPlugins, false));
            infoListBox.setItemRenderer(new ListitemRenderer() {
                @Override
                public void render(final Listitem item, final Object data, final int index) throws Exception {
                    if (data != null && data instanceof PluginInfo) {
                        PluginInfo info = (PluginInfo) data;
                        item.appendChild(new Listcell(info.getName()));
                        item.appendChild(new Listcell(info.getVersion()));
                        item.appendChild(new Listcell(info.getType()));
                        Listcell dCell = new Listcell();
                        Label dLabel = new Label(info.getDescription());
                        dLabel.setWidth("100px");
                        dLabel.setMultiline(true);
                        dCell.appendChild(dLabel);
                        item.appendChild(dCell);
                        item.appendChild(new Listcell(info.getAuthor()));
                        item.appendChild(new Listcell(info.getEmail()));
                    }
                }
            });
            Button buttonOk = (Button) pluginWindow.getFellow("ok");
            buttonOk.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(final Event event) throws Exception {
                    pluginWindow.detach();
                }
            });
            pluginWindow.doModal();

        } catch (Exception e) {
            Messagebox.show("Error retrieving installed Plugins: "+e.getMessage(), "Error", Messagebox.OK,
                    Messagebox.ERROR);
        }
    }
}
