/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2019 The University of Tartu.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.csvexporter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.service.EventLogService;
import org.apromore.service.csvexporter.CSVExporterLogic;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.apromore.zk.label.LabelSupplier;

@Component("csvExporterPlugin")
public class CSVExporterPlugin extends DefaultPortalPlugin implements LabelSupplier {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(CSVExporterPlugin.class);

    private String label = "Export log as CSV";
    private String groupLabel = "File";

    @Inject EventLogService eventLogService;
    @Inject private CSVExporterLogic csvExporterLogic;

    @Override
    public String getBundleName() {
        return "csvexporter";
    }

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
    public void execute(PortalContext portalContext) {
        try {
            LogSummaryType logSummary = findSelectedLog(portalContext);
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/encodingList.zul", null, null);

            Button downloadButton = (Button) window.getFellow("downloadButton");
            Listbox selectEncoding = (Listbox) window.getFellow("selectEncoding");
            downloadButton.addEventListener("onClick", new EventListener<Event>() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            String filename = logSummary.getName().replace('.','-');
                            XLog xlog = eventLogService.getXLog(logSummary.getId());

                            File file = csvExporterLogic.exportCSV(xlog);

                            byte[] finalbytes = Files.readAllBytes(file.toPath());
                            Filedownload.save(finalbytes, "application/x-gzip", filename + ".csv.gz");

                            Files.delete(file.toPath());

                            window.invalidate();
                            window.detach();
                        }
            });

            Button cancelButton = (Button) window.getFellow("cancelButton");
            cancelButton.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    window.invalidate();
                    window.detach();
                }

            });

            window.doModal();
        } catch (IOException e) {
            LOGGER.error("Failed to read");
        }  catch (NoUniqueSelectedLogException e) {
            Messagebox.show(getLabel("selectOnlyOneLog"), "Error", Messagebox.OK, Messagebox.INFORMATION);

        } catch (RuntimeException e) {
            LOGGER.error("Unable to export log as CSV", e);
            Messagebox.show(getLabel("unableExportCSV"), "Error", Messagebox.OK, Messagebox.ERROR);
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
