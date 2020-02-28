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

package org.apromore.plugin.portal.predictivemonitor;

// Java 2 Standard Edition
import java.io.IOException;
import java.util.ArrayList;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.PredictiveMonitor;
import org.apromore.service.predictivemonitor.Predictor;

/**
 * In MVC terms, this is a controller whose corresponding model is {@link PredictiveMonitor} and corresponding view is <code>createPredictiveMonitor.zul</code>.
 */
public class CreatePredictiveMonitorController {

    private static Logger LOGGER = LoggerFactory.getLogger(CreatePredictiveMonitorController.class.getCanonicalName());

    public CreatePredictiveMonitorController(PortalContext portalContext, PredictiveMonitorsListModel predictiveMonitorsListModel, PredictorsListModel predictorsListModel) throws IOException {

        final Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/createPredictiveMonitor.zul", null, null);

        final Button  createButton      = (Button) window.getFellow("create");
        final Button  cancelButton      = (Button) window.getFellow("cancel");
        final Textbox nameTextbox       = (Textbox) window.getFellow("name");
        final Listbox predictorsListbox = (Listbox) window.getFellow("predictors");

        // Bind window components
        createButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {

                String name = nameTextbox.getValue();

                predictiveMonitorsListModel.createPredictiveMonitor(name, new ArrayList<>(predictorsListModel.getSelection()));

                window.detach();
            }
        });

        ((Button) window.getFellow("loadPredictorFile")).addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                new CreatePredictorController(portalContext, predictorsListModel);
            }
        });

        ((Button) window.getFellow("deletePredictors")).addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                predictorsListModel.removeAll(predictorsListModel.getSelection());
            }
        });

        cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                window.onClose();
            }
        });

        predictorsListbox.setModel(predictorsListModel);

        window.doModal();
    }
}
