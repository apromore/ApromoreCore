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

package org.apromore.plugin.portal.CSVImporterPortal;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.csvimporter.CSVImporterLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;


@Component("csvFileImporterPlugin")
public class CSVFileImporterPlugin implements FileImporterPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(CSVFileImporterPlugin.class);

    //@Inject
    private CSVImporterLogic csvImporterLogic;

    //@Inject
    private EventLogService eventLogService;

    public void setCsvImporterLogic(CSVImporterLogic newCSVImporterLogic) {
        LOGGER.info("Injected CSV importer logic {}", newCSVImporterLogic);
        this.csvImporterLogic = newCSVImporterLogic;
    }

    public void setEventLogService(EventLogService newEventLogService) {
        LOGGER.info("Injected event log service {}", newEventLogService);
        this.eventLogService = newEventLogService;
    }


    // Implementation of FileImporterPlugin

    @Override
    public Set<String> getFileExtensions() {
        return Collections.singleton("csv");
    }

    @Override
    public void importFile(Media media, PortalContext portalContext, boolean isLogPublic) {

        // Configure the arguments to pass to the CSV importer view
        Map arg = new HashMap();
        arg.put("csvImporterLogic", csvImporterLogic);
        arg.put("eventLogService", eventLogService);
        arg.put("media", media);
        arg.put("portalContext", portalContext);
        arg.put("isLogPublic", isLogPublic);

        // Create a CSV importer view
        Window window = createComponent("/org/apromore/plugin/portal/CSVImporterPortal/csvImporter.zul", getClass().getClassLoader(), null, arg);
        window.doModal();
    }

    private static <T extends org.zkoss.zk.ui.Component> T createComponent(String path, ClassLoader classLoader, org.zkoss.zk.ui.Component parent, Map<?, ?> arg) {

        try {
            InputStream in = classLoader.getResourceAsStream(path);
            if (in == null) {
                throw new IllegalArgumentException(path + " is not in " + classLoader);
            }
            Reader r = new InputStreamReader(in, "UTF-8");
            org.zkoss.zk.ui.Component component = Executions.createComponentsDirectly(r, "zul", parent, arg);

            return (T) component;

        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid ZUL path: " + path, e);
        }
    }
}
