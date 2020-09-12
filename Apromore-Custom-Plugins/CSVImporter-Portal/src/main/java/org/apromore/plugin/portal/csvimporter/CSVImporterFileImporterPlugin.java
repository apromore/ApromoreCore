/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.plugin.portal.csvimporter;

import org.apromore.plugin.portal.FileImporterPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.csvimporter.services.ParquetFactoryProvider;
import org.apromore.service.csvimporter.services.legecy.LogReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.util.*;

public class CSVImporterFileImporterPlugin implements FileImporterPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(CSVImporterFileImporterPlugin.class);

    private ParquetFactoryProvider parquetFactoryProvider;
    LogReader logReader;

    public ParquetFactoryProvider getParquetFactoryProvider() {
        return parquetFactoryProvider;
    }

    public void setParquetFactoryProvider(ParquetFactoryProvider parquetFactoryProvider) {
        this.parquetFactoryProvider = parquetFactoryProvider;
    }

    public LogReader getLogReader() {
        return logReader;
    }

    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
    }
    // Implementation of FileImporterPlugin

    @Override
    public Set<String> getFileExtensions() {

        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream("datalayer.config"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean useParquet = Boolean.parseBoolean(props.getProperty("use.parquet"));

        if (useParquet) {
            return new HashSet<>(Arrays.asList("csv", "parquet"));
        } else {
            return Collections.singleton("csv");
        }
    }

    @Override
    public void importFile(Media media, boolean isLogPublic) {

        // Configure the arguments to pass to the CSV importer view
        Map arg = new HashMap<>();
        arg.put("parquetFactoryProvider", parquetFactoryProvider);
        arg.put("logReader", logReader);
        arg.put("media", media);
        Sessions.getCurrent().setAttribute(CSVImporterController.SESSION_ATTRIBUTE_KEY, arg);

        // Create a CSV importer view
        switch ((String) Sessions.getCurrent().getAttribute("fileimportertarget")) {
            case "page":  // create the view in its own page
                Executions.getCurrent().sendRedirect("import-csv/csvimporter.zul", "_blank");
                break;

            case "modal":
            default:  // create the view in a modal popup within the current page
                PortalContext portalContext = (PortalContext) Sessions.getCurrent().getAttribute("portalContext");
                try {
                    Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "import-csv/csvimporter.zul", null, arg);
                    window.doModal();

                } catch (IOException e) {
                    LOGGER.error("Unable to create window", e);
                }
                break;
        }
    }
}
