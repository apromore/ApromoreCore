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
import java.util.*;
import javax.inject.Inject;
import org.apromore.model.*;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.SessionTab;
import org.apromore.portal.common.*;
import org.apromore.portal.dialogController.*;
import org.apromore.portal.exception.*;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.*;
import org.apromore.service.EventLogService;
import org.apromore.service.csvexporter.CSVExporterLogic;

public class DownloadSelectionPlugin extends DefaultPortalPlugin {

    private static Logger LOGGER = LoggerFactory.getLogger(DownloadSelectionPlugin.class);

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
    public String getLabel(Locale locale) {
        return label;
    }

    @Override
    public String getGroupLabel(Locale locale) {
        return groupLabel;
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
                Messagebox.show("Please, select exactly one file.", "Too many selections", Messagebox.OK, Messagebox.INFORMATION);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to download selection", e);
            Messagebox.show("Unable to download selection");
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

        List<Tab> tabs = SessionTab.getSessionTab(portalContext).getTabsSession(UserSessionManager.getCurrentUser().getId());

        for(Tab tab : tabs){
            if(tab.isSelected() && tab instanceof TabQuery){
                TabQuery tabQuery=(TabQuery)tab;
                List<Listitem> items=tabQuery.getListBox().getItems();
                HashMap<SummaryType, List<VersionSummaryType>> processVersion=new HashMap<>();
                for(Listitem item : items){
                    if(item.isSelected() && item instanceof TabListitem){
                        TabListitem tabItem=(TabListitem)item;
                        processVersion.put(tabItem.getProcessSummaryType(),tabItem.getVersionSummaryType());
                    }
                }
                if(processVersion.keySet().size()>0){
                    new ExportListNativeController(mainC, null, processVersion);
                    return;
                }
            }
        }

        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions = mainC.getSelectedElementsAndVersions();
        if (selectedProcessVersions.size() != 0) {
            new ExportListNativeController(mainC, null, selectedProcessVersions);
        } else {
            mainC.displayMessage("No process version selected.");
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
                public void onEvent(Event event) throws Exception {
                    if (format.getSelectedItem().getLabel().equals("CSV")) {
                        rowEncoding.setVisible(true);
                    } else {
                        rowEncoding.setVisible(false);
                    }
                }
            });

            downloadButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    if (format.getSelectedItem().getLabel().equals("CSV")) {
                        exportCSV(logSummary);
                    } else {
                        exportXES(logSummary, mainC);
                    }
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
