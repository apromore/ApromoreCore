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

// Third party packages
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Fileupload;
import org.zkoss.zul.ListModelList;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

// Local packages
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.predictivemonitor.PredictiveMonitorService;
import org.apromore.service.predictivemonitor.Predictor;

/**
 * UI for specifying the parameters of and creating a new {@link Predictor}.
 */
public class CreatePredictorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePredictorController.class.getCanonicalName());

    private final Window  window;

    private final Textbox    nameTextbox;
    private final Combobox   typeCombobox;
    private final Fileupload pklFileupload;
    private final Button     okButton;
    private final Button     cancelButton;

    private Media pklMedia;

    public CreatePredictorController(PortalContext portalContext, PredictorsListModel predictorsListModel) throws IOException {

        window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/createPredictor.zul", null, null);

        nameTextbox   = (Textbox)    window.getFellow("name");
        typeCombobox  = (Combobox)   window.getFellow("type");
        pklFileupload = (Fileupload) window.getFellow("pkl");
        okButton      = (Button)     window.getFellow("ok");
        cancelButton  = (Button)     window.getFellow("cancel");

        // Bind window components

        pklFileupload.addEventListener("onUpload", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                LOGGER.info("Uploading pkl");
                pklMedia = ((UploadEvent) event).getMedia();
                LOGGER.info("Uploaded pkl " + event);
            }
        });

        okButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                if (pklMedia == null) {
                    LOGGER.info("Not creating predictor; no pkl uploaded");
                    return;
                }

                LOGGER.info("Creating predictor");
                final String[] typeCodes = { "-1", "next", "remtime" };
                Predictor predictor = predictorsListModel.createPredictor(nameTextbox.getValue(), typeCodes[typeCombobox.getSelectedIndex()], pklMedia.getStreamData());
                LOGGER.info("Created predictor " + predictor.getName());

                window.detach();
            }
        });

        cancelButton.addEventListener("onClick", new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                window.onClose();
            }
        });

        window.doModal();
    }
}
