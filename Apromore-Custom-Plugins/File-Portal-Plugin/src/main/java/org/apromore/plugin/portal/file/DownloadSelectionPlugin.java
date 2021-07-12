/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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
package org.apromore.plugin.portal.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.notification.Notification;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.model.ExportFormatResultType;
import org.apromore.portal.model.ExportLogResultType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.EventLogService;
import org.apromore.service.csvexporter.CSVExporterLogic;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

public class DownloadSelectionPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(DownloadSelectionPlugin.class);

    private String label = "Download";
    private String groupLabel = "File";
    @Inject EventLogService eventLogService;
    @Inject private CSVExporterLogic csvExporterLogic;

    Listbox selectedEncoding;
    Radiogroup format;

    public void setEventLogService(EventLogService eventLogService) {
        this.eventLogService = eventLogService;
    }

    public void setCsvExporterLogic(CSVExporterLogic csvExporterLogic) {
        this.csvExporterLogic = csvExporterLogic;
    }

    // PortalPlugin overrides

    @Override
    public String getItemCode(Locale locale) { return label; }

    @Override
    public String getGroup(Locale locale) {
        return "File";
    }

    @Override
    public String getLabel(Locale locale) {
        return Labels.getLabel("plugin_file_download_text",label);
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return Labels.getLabel("plugin_file_title_text", groupLabel);
    }


    @Override
    public String getIconPath() {
        return "download.svg";
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            MainController mainController = (MainController) portalContext.getMainController();

            if(mainController.getSelectedElements().size() == 1) {
                SummaryType summaryType = mainController.getSelectedElements().iterator().next();
                if (summaryType instanceof LogSummaryType) {
                    exportLog(mainController, portalContext, (LogSummaryType)summaryType);
                } else if (summaryType instanceof ProcessSummaryType) {
                    exportProcessModel(mainController, portalContext);
                }
            } else {
                Notification.info("Please select exactly one file");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to download selection", e);
            Notification.error("Unable to download selection");
        }
    }

    /**
     * Export all selected process versions, each of which in a native format to be chosen by the user
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     * @throws org.apromore.portal.exception.ExceptionFormats
     */
    protected void exportProcessModel(MainController mainC, PortalContext portalContext) throws SuspendNotAllowedException, InterruptedException, ExceptionFormats, ParseException {
        mainC.eraseMessage();

        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedElementsAndVersions();
        if (selectedProcessVersions.size() == 1) {
            ProcessSummaryType model = (ProcessSummaryType)selectedProcessVersions.keySet().iterator().next();
            VersionSummaryType version = selectedProcessVersions.get(model).get(0);
            try {
                ExportFormatResultType exportResult = mainC.getService().exportFormat(model.getId(), model.getName(), version.getName(),
                        version.getVersionNumber(), model.getOriginalNativeType(), UserSessionManager.getCurrentUser().getUsername());
                InputStream nativeStream = exportResult.getNative().getInputStream();
                Filedownload.save(nativeStream, "text/xml", model.getName() + ".bpmn");
                LOGGER.info("User {} downloaded process model \"{}\" (id {}, version {}/{})", UserSessionManager.getCurrentUser().getUsername(),
                    model.getName(), model.getId(), version.getName(), version.getVersionNumber());
            }
            catch (Exception e) {
                LOGGER.error("Export process model failed", e);
                Messagebox.show("Export failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
            }
        } else {
            Notification.error("Please select one process model");
        }
    }

    public void exportLog(MainController mainC, PortalContext portalContext, LogSummaryType logSummary) {
        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/downloadLog.zul", null, null);
            Button downloadButton = (Button) window.getFellow("downloadButton");
            Row rowEncoding = (Row) window.getFellow("rowEncoding");

            selectedEncoding = (Listbox) window.getFellow("selectEncoding");
            format = (Radiogroup) window.getFellow("format");

            format.addEventListener("onCheck", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    if (format.getSelectedItem().getLabel().equals("CSV")) {
                        rowEncoding.setVisible(true);
                    } else {
                        rowEncoding.setVisible(false);
                    }
                }
            });

            downloadButton.addEventListener("onClick", new EventListener<Event>() {
                @Override
                public void onEvent(Event event) throws Exception {
                    if (format.getSelectedItem().getLabel().equals("CSV")) {
                        exportCSV(logSummary);
                    } else {
                        exportXES(logSummary, mainC);
                    }
                    LOGGER.info("User {} downloaded log \"{}\" (id {}) in format {}", UserSessionManager.getCurrentUser().getUsername(),
                        logSummary.getName(), logSummary.getId(), format.getSelectedItem().getLabel());
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
        }
    }

    /**
     * Export all selected process versions, each of which in a native format to be chosen by the user
     * @throws InterruptedException
     * @throws SuspendNotAllowedException
     * @throws org.apromore.portal.exception.ExceptionFormats
     */
    protected void exportXES(LogSummaryType logSummary, MainController mainController) throws Exception {
        String filename = logSummary.getName().replace('.','-');
        ExportLogResultType exportResult = mainController.getService().exportLog(logSummary.getId(), filename);
        try (InputStream native_is = exportResult.getNative().getInputStream()) {
            mainController.showPluginMessages(exportResult.getMessage());
            Filedownload.save(native_is, "application/x-gzip", filename + ".xes.gz");
        }
    }

    public void exportCSV(LogSummaryType logSummary) throws Exception {
        try {
            String filename = logSummary.getName().replace('.','-');
            XLog xlog = eventLogService.getXLog(logSummary.getId());
            String csvLog = csvExporterLogic.exportCSV(xlog);

            InputStream csvLogStream = new ByteArrayInputStream(csvLog.getBytes(Charset.forName(selectedEncoding.getSelectedItem().getValue().toString())));
            Filedownload.save(csvLogStream, "text/csv", filename);
        } catch (RuntimeException e) {
            LOGGER.error("Unable to export log as CSV", e);
            Messagebox.show("Unable to export log as CSV.", "Server error", Messagebox.OK, Messagebox.ERROR);
        }
    }

}
