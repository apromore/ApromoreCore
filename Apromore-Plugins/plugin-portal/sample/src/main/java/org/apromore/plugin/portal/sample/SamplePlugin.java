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

package org.apromore.plugin.portal.sample;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.custom.gui.plugin.PluginCustomGui;
import org.apromore.portal.custom.gui.tab.impl.TabRowValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * An example Portal Plugin, which display an Hello World dialog
 */
@Component("plugin")
public class SamplePlugin extends PluginCustomGui {

    private Logger LOGGER = LoggerFactory.getLogger(SamplePlugin.class);

    @Override
    public String getLabel(Locale locale) {
        return "Example";
    }

    @Override
    public void execute(PortalContext context) {
        // Show a message on the portal

        List<TabRowValue> rows = new ArrayList<>();
        rows.add(createTabRowValue("A"));
        rows.add(createTabRowValue("B"));

        List<Listheader> listheaders = new ArrayList<>();
        listheaders.add(new Listheader("Name"));

        addTab("new tab", "", rows, listheaders, null, context);

        LOGGER.info("Executed example plug-in!");

        try {
            // Create a window based on the ZUL file, which is controlled by SampleController
            // Please note that it is important to pass a ClassLoader of a class within the plug-in bundle!
            Window window = (Window) context.getUI().createComponent(getClass().getClassLoader(), "zul/sample.zul", null, null);
            // Show the windows on top of everything
            window.doModal();
        } catch (IOException e) {
            Messagebox.show("Could not load component: " + e.getMessage(), "Error", Messagebox.OK, Messagebox.ERROR);
            LOGGER.error("Could not load component", e);
        }
    }

}
