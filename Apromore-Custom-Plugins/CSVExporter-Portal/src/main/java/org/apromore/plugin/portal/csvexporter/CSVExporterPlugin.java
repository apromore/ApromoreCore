/*
 * This file is part of "Apromore".
 *
 * Copyright (C) 2019 The University of Tartu.
 * Copyright (C) 2020 The University of Melbourne.
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

package org.apromore.plugin.portal.csvexporter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.apromore.model.LogSummaryType;
import org.apromore.model.SummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.EventLogService;
import org.apromore.service.csvexporter.CSVExporterLogic;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;

@Component("csvExporterPlugin")
public class CSVExporterPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(CSVExporterPlugin.class);

    private String label = "Export log as CSV";
    private String groupLabel = "Settings";

    @Inject EventLogService eventLogService;
    @Inject private CSVExporterLogic csvExporterLogic;


    public void setEventLogService(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    public void setCsvExporterLogic(CSVExporterLogic csvExporterLogic) {
        this.csvExporterLogic = csvExporterLogic;
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
        try {
            LogSummaryType logSummary = findSelectedLog(portalContext);
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/encodingList.zul", null, null);

            Button downloadButton = (Button) window.getFellow("downloadButton");
            Listbox selectEncoding = (Listbox) window.getFellow("selectEncoding");
            downloadButton.addEventListener("onClick", new EventListener<Event>() {
                        public void onEvent(Event event) throws Exception {
                            String filename = logSummary.getName().replace('.','-');
                            XLog xlog = eventLogService.getXLog(logSummary.getId());
                            String csvLog = csvExporterLogic.exportCSV(xlog);

                            InputStream csvLogStream = new ByteArrayInputStream(csvLog.getBytes(Charset.forName(selectEncoding.getSelectedItem().getValue().toString())));
                            Filedownload.save(csvLogStream, "text/csv", filename);
                            window.invalidate();
                            window.detach();
                        }
            });

            Button cancelButton = (Button) window.getFellow("cancelButton");
            cancelButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    window.invalidate();
                    window.detach();
                }

            });

            window.doModal();
        } catch (IOException e) {
            LOGGER.error("Failed to read");
        }  catch (NoUniqueSelectedLogException e) {
            Messagebox.show("Please, select exactly one log.", "Wrong Log Selection", Messagebox.OK, Messagebox.INFORMATION);

        } catch (RuntimeException e) {
            LOGGER.error("Unable to export log as CSV", e);
            Messagebox.show("Unable to export log as CSV.", "Server error", Messagebox.OK, Messagebox.ERROR);
        }
    }


    // Internal methods

    /**
     * Find the selected log in the Portal.
     *
     * This will throw an exception if there isn't exactly one log selected.
     *
     * @param portalContext
     * @return the selected log
     * @throws NoUniqueSelectedLogException if the selection isn't a single log
     */
    private LogSummaryType findSelectedLog(PortalContext portalContext) throws NoUniqueSelectedLogException {

        Map<SummaryType, List<VersionSummaryType>> elements = portalContext.getSelection().getSelectedProcessModelVersions();
        if (elements.size() != 1) {
            throw new NoUniqueSelectedLogException();
        }

        SummaryType summary = elements.keySet().iterator().next();
        if (!(summary instanceof LogSummaryType)) {
            throw new NoUniqueSelectedLogException();
        }

        return (LogSummaryType) summary;
    }




    /**
     * Used to indicate that a {@link PortalContext} did not have a unique selected log.
     */
    private class NoUniqueSelectedLogException extends Exception {}
}
