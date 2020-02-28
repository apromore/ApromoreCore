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

package org.apromore.plugin.portal.xesselection;

import java.util.Locale;
import javax.inject.Inject;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xeslite.external.XFactoryExternalStore;

@Component("xesSelectionPlugin")
public class XESSelectionPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(XESSelectionPlugin.class);

    private String label = "Select XES Implementation";
    private String groupLabel = "Settings";

    public XESSelectionPlugin() {
        XFactoryRegistry.instance().register(new XFactoryBufferedImpl());
        XFactoryRegistry.instance().register(new XFactoryExternalStore.InMemoryStoreImpl());
        XFactoryRegistry.instance().register(new XFactoryExternalStore.InMemoryStoreAlignmentAwareImpl());
        XFactoryRegistry.instance().register(new XFactoryExternalStore.MapDBDiskImpl());
        XFactoryRegistry.instance().register(new XFactoryExternalStore.MapDBDiskSequentialAccessImpl());
        XFactoryRegistry.instance().register(new XFactoryExternalStore.MapDBDiskWithoutCacheImpl());
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
    public void execute(PortalContext portalContext) {
        new XESSelectionController(portalContext);
    }
}
