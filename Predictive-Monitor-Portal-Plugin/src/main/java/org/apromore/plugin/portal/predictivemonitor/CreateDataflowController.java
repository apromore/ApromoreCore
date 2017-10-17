/*
 * Copyright © 2009-2017 The Apromore Initiative.
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import static java.util.concurrent.TimeUnit.SECONDS;

// Third party packages
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Desktop;
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
import org.apromore.service.EventLogService;

/**
 * In MVC terms, this is a controller whose corresponding model is {@link Dataflow} and corresponding view is <code>predictive_monitor.zul</code>.
 */
public class CreateDataflowController {

    private static Logger LOGGER = LoggerFactory.getLogger(CreateDataflowController.class.getCanonicalName());

    // TODO: pass dataflows and predictors as constructor parameters
    public CreateDataflowController(PortalContext portalContext, EventLogService eventLogService, String kafkaHost, File nirdizatiPath, String pythonPath) throws IOException {

        final Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/createDataflow.zul", null, null);

        final Button  createButton      = (Button) window.getFellow("create");
        final Button  cancelButton      = (Button) window.getFellow("cancel");
        final Textbox nameTextbox       = (Textbox) window.getFellow("name");
        final Listbox predictorsListbox = (Listbox) window.getFellow("predictors");

        // Bind window components
        createButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {

                String name = nameTextbox.getValue();

                List<Predictor> selectedPredictors = new LinkedList<>();
                for (Predictor predictor: Persistent.predictors) {  // .getSelection() only returns unordered Set, so do this the hard way
                    if (Persistent.predictors.isSelected(predictor)) {
                        selectedPredictors.add(predictor);
                    }
                }

                Persistent.dataflows.add(new Dataflow(name, kafkaHost, nirdizatiPath, pythonPath, selectedPredictors));
                window.detach();
            }
        });

        cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                window.onClose();
            }
        });

        predictorsListbox.setModel(Persistent.predictors);

        window.doModal();
    }
}
