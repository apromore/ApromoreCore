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

package org.apromore.plugin.portal.sample;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.custom.gui.plugin.PluginCustomGui;
import org.apromore.portal.custom.gui.tab.PortalTab;
import org.apromore.portal.custom.gui.tab.TabItemExecutor;
import org.apromore.portal.custom.gui.tab.impl.TabHeader;
import org.apromore.portal.custom.gui.tab.impl.TabItem;
import org.apromore.portal.custom.gui.tab.impl.TabRowValue;
import org.springframework.stereotype.Component;
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

        TabHeader tabHeader = createTabHeader("Name");

        addTab("new tab", "", rows, tabHeader, null, context);

        context.getMessageHandler().displayInfo("Executed example plug-in!");
        try {
            // Create a window based on the ZUL file, which is controlled by SampleController
            // Please note that it is important to pass a ClassLoader of a class within the plug-in bundle!
            Window window = (Window) context.getUI().createComponent(getClass().getClassLoader(), "zul/sample.zul", null, null);
            // Show the windows on top of everything
            window.doModal();
        } catch (IOException e) {
            context.getMessageHandler().displayError("Could not load component ", e);
        }
    }

}