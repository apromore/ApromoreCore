/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 The University of Melbourne.
 * %%
 * Copyright (C) 2020, Apromore Pty Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
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

package org.apromore.plugin.portal.csvimporter;

import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.csvimporter.services.impl.SampleLogGenerator;
import org.apromore.service.csvimporter.io.LogReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CSVFileImporterPlugin implements FileImporterPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(CSVFileImporterPlugin.class);

    private LogReader logReader;
    private SampleLogGenerator sampleLogGenerator;

    public void setLogReader(LogReader newLogReader) {
        LOGGER.info("Injected CSV importer logic {}", newLogReader);
        this.logReader = newLogReader;
    }

    public void setSampleLogGenerator(SampleLogGenerator sampleLogGenerator) {
        this.sampleLogGenerator = sampleLogGenerator;
    }

    @Override
    public Set<String> getFileExtensions() {
        return Collections.singleton("csv");
    }

    @Override
    public void importFile(Media media, boolean isLogPublic) {

        // Configure the arguments to pass to the CSV importer view
        Map arg = new HashMap<>();
        arg.put("logReader", logReader);
        arg.put("sampleLogGenerator", sampleLogGenerator);
        arg.put("media", media);

        // Create a CSV importer view
        String zul = "/org/apromore/plugin/portal/csvimporter/csvimporter.zul";
        PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), zul, null, arg);
            window.doModal();

        } catch (IOException e) {
            LOGGER.error("Unable to create window", e);
        }
    }
}
