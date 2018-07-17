/*
 * Copyright © 2009-2018 The Apromore Initiative.
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

package org.apromore.plugin.portal.predictivemonitor;

// Java 2 Standard Edition
import java.io.IOException;

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.Predictor;

/**
 * In MVC terms, this is a controller whose corresponding model is the set of {@link Predictor}s and corresponding view is <code>predictors.zul</code>.
 */
public class PredictorsController {

    private static Logger LOGGER = LoggerFactory.getLogger(PredictorsController.class.getCanonicalName());

    private final Window  window;

    private final Listbox predictorsListbox;
    private final Button  createPredictorButton;
    private final Button  deletePredictorButton;

    public PredictorsController(PortalContext portalContext, PredictiveMonitorService predictiveMonitorService) throws IOException {

        window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/predictors.zul", null, null);

        PredictorsListModel predictorsListModel = new PredictorsListModel(predictiveMonitorService);
        predictorsListbox = (Listbox) window.getFellow("predictors");
        predictorsListbox.setModel(predictorsListModel);

        createPredictorButton = (Button) window.getFellow("createPredictor");
        deletePredictorButton = (Button) window.getFellow("deletePredictor");

        // Bind window components
        createPredictorButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                new CreatePredictorController(portalContext, predictorsListModel);
            }
        });

        deletePredictorButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                predictorsListModel.removeAll(predictorsListModel.getSelection());
            }
        });

        window.doModal();
    }
}
