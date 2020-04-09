/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 - 2020 The University of Melbourne.
 * Copyright (C) 2019 - 2020 The University of Tartu.
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

package org.apromore.plugin.portal.CSVImporterPortal;

import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.apromore.service.csvimporter.LogSample;
import org.springframework.stereotype.Component;
import org.zkoss.util.media.Media;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Window;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Component("csvImporterPortalPlugin")
public class CSVImporterPortal implements FileImporterPlugin, Constants {

    @Inject
    private CSVImporterLogic csvImporterLogic;
    @Inject
    private EventLogService eventLogService;

    public void setCsvImporterLogic(CSVImporterLogic newCSVImporterLogic) {
        this.csvImporterLogic = newCSVImporterLogic;
    }

    public void setEventLogService(EventLogService newEventLogService) {
        this.eventLogService = newEventLogService;
    }


    // FileImporterPlugin implementation
    @Override
    public Set<String> getFileExtensions() {
        return new HashSet<>(Collections.singletonList("csv"));
    }

    @Override
    public void importFile(Media media, PortalContext portalContext, boolean isLogPublic) {

        ConstructCSVSample CSVSample = new ConstructCSVSample(csvImporterLogic, media);
        if (!Arrays.asList(allowedExtensions).contains(media.getFormat())) {
            Messagebox.show("Please select CSV file!", "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }

        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/csvimporter.zul", null, null);
            // Initialize the character encoding drop-down menu
            Combobox setEncoding = (Combobox) window.getFellow(setEncodingId);
            setEncoding.setModel(new ListModelList<>(fileEncoding));
            LogSample sample = CSVSample.getCSVSample(getFileEncoding(window), logSampleSize);

            ConstructUserInterface userInterface = new ConstructUserInterface(sample, media, window, csvImporterLogic, eventLogService, portalContext, isLogPublic);

            if(sample != null){
                userInterface.setUpUI();
                window.doModal();
            }

            setEncoding.addEventListener("onSelect", event -> {
                LogSample mySample = CSVSample.getCSVSample(getFileEncoding(userInterface.getWindow()), logSampleSize);
                if (mySample != null) {
                    userInterface.setSample(mySample);
                    userInterface.renderGridContent();
                }
                    }
            );

        } catch (IOException e) {
            Messagebox.show("Unable to import file : " + e, "Error", Messagebox.OK, Messagebox.ERROR);
            return;
        }
    }


    private String getFileEncoding(Window window) {
        Combobox setEncoding = (Combobox) window.getFellow(setEncodingId);
        return setEncoding.getValue().contains(" ")
                ? setEncoding.getValue().substring(0, setEncoding.getValue().indexOf(' '))
                : setEncoding.getValue();
    }

}
