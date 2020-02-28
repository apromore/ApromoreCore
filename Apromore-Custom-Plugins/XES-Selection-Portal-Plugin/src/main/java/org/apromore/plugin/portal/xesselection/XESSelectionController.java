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

import java.io.IOException;
import org.apromore.plugin.portal.PortalContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelSet;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.event.ListDataListener;

public class XESSelectionController {

    private static Logger LOGGER = LoggerFactory.getLogger(XESSelectionController.class);

    XESSelectionController(PortalContext portalContext) {
        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/select.zul", null, null);

            ListModelSet<XFactory> model = new ListModelSet<XFactory>(XFactoryRegistry.instance().getAvailable(), false);
            model.addToSelection(XFactoryRegistry.instance().currentDefault());
            model.addListDataListener(new ListDataListener() {
                public void onChange(ListDataEvent event) {
                    if (event.getType() == ListDataEvent.SELECTION_CHANGED) {
                         for (XFactory factory: model.getSelection()) {
                             XFactoryRegistry.instance().setCurrentDefault(factory);
                             LOGGER.info("XFactoryRegistry current default is now: " + factory.getName());
                         }
                    }
                }
            });

            ((Listbox) window.getFellow("xFactoryRegistryListbox")).setModel(model);
            
            ((Button) window.getFellow("okButton")).addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    window.detach();
                }
            });

            window.doModal();

        } catch (IOException e) {
            LOGGER.warn("Unable to execute sample method", e);
            Messagebox.show("Unable to execute sample method", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
