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

package org.apromore.portal.dialogController;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apromore.canoniser.Canoniser;
import org.apromore.model.AnnotationsType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.model.PluginInfo;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.exception.ExceptionExport;
import org.apromore.portal.util.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;
import org.zkoss.zul.Row;

public class ExportOneNativeController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportOneNativeController.class);

    private final Window exportNativeW;
    private final MainController mainC;
    private final ExportListNativeController exportListControllerC;
    private final Label processNameL;
    private final Row annotationsR;
    private final Listbox annotationsLB;
    private final Button okB;
    private final Listbox formatsLB;
    private final int processId;
    private final String versionName;
    private final String versionNumber;
    private final HashMap<String, String> formats_ext;
    // <k, v> belongs to nativeTypes: the file extension k
    // is associated with the native type v (<xpdl,XPDL 1.2>)
    private Set<PluginInfo> canoniserInfos;
    private final PluginPropertiesHelper pluginPropertiesHelper;

    private SelectDynamicListController canoniserCB;

    public ExportOneNativeController(final ExportListNativeController exportListControllerC, final MainController mainC, final int processId,
            final String processName, final String originalType, final String versionName, String versionNumber,
            final List<AnnotationsType> annotations, final HashMap<String, String> formats_ext) throws SuspendNotAllowedException, InterruptedException {

        this.mainC = mainC;
        this.exportListControllerC = exportListControllerC;

        this.exportNativeW = (Window) Executions.createComponents("macros/exportnative.zul", null, null);
        this.processId = processId;
        this.versionName = versionName;
        this.versionNumber = versionNumber;
        this.formats_ext = formats_ext;
        String id = this.processId + " " + this.versionName;
        this.exportNativeW.setId(id);
        Grid exportNatG = (Grid) this.exportNativeW.getFirstChild().getFirstChild();
        Rows exportNatRs = (Rows) exportNatG.getFirstChild().getNextSibling();
        Row warning = (Row) exportNatRs.getFirstChild();
        Row processNameR = (Row) warning.getNextSibling();
        Row versionNameR = (Row) processNameR.getNextSibling();
        Row formatsR = (Row) versionNameR.getNextSibling();
        this.annotationsR = (Row) formatsR.getNextSibling();
        Row buttonsR = (Row) this.annotationsR.getNextSibling().getNextSibling().getNextSibling().getNextSibling();
        this.processNameL = (Label) processNameR.getFirstChild().getNextSibling();
        this.processNameL.setValue(processName);
        Label versionNameL = (Label) versionNameR.getFirstChild().getNextSibling();
        versionNameL.setValue(versionName);
        this.annotationsLB = (Listbox) this.annotationsR.getFirstChild().getNextSibling();
        this.formatsLB = (Listbox) formatsR.getFirstChild().getNextSibling();
        this.okB = (Button) buttonsR.getFirstChild().getFirstChild();
        Button cancelB = (Button) this.okB.getNextSibling();
        Button cancelAllB = (Button) cancelB.getNextSibling();

        // enable cancelAll button if at least 1 process versions left.
        cancelAllB.setVisible(this.exportListControllerC.getToExportList().size() > 0);

        // Build list of available formats for exportFile.
        Listitem cbi;
        cbi = new Listitem();
        this.formatsLB.appendChild(cbi);
        cbi.setLabel(Constants.CANONICAL);
        cbi.setValue(Constants.CANONICAL);

        Listitem cba;
        cba = new Listitem();
        cba.setLabel(Constants.NO_ANNOTATIONS);
        cba.setValue(Constants.NO_ANNOTATIONS);
        this.annotationsLB.appendChild(cba);

        for (AnnotationsType annotation : annotations) {
            String nat_type = annotation.getNativeType();
            for (int k = 0; k < annotation.getAnnotationName().size(); k++) {
                cbi = new Listitem();
                this.formatsLB.appendChild(cbi);
                cbi.setLabel(Constants.ANNOTATIONS + " - " + annotation.getAnnotationName().get(k) + " (" + nat_type + ")");
                cbi.setValue(Constants.ANNOTATIONS + " - " + annotation.getAnnotationName().get(k));

                cba = new Listitem();
                this.annotationsLB.appendChild(cba);
                cba.setLabel(annotation.getAnnotationName().get(k) + " (" + nat_type + ")");
                cba.setValue(annotation.getAnnotationName().get(k));
                if (Constants.INITIAL_ANNOTATION.compareTo(annotation.getAnnotationName().get(k)) == 0) {
                    cba.setSelected(true);
                }
            }
        }
        // - Available native formats
        Set<String> extensions = this.formats_ext.keySet();
        List<String> sorted = CollectionUtil.asSortedList(extensions);
        for (final String extension : sorted) {
            if (!extension.equalsIgnoreCase("aml")) {
                cbi = new Listitem();
                this.formatsLB.appendChild(cbi);
                cbi.setLabel(this.formats_ext.get(extension));
                cbi.setValue(this.formats_ext.get(extension));
            }
        }
        formatsLB.setSelectedItem((Listitem) formatsLB.getFirstChild());

        pluginPropertiesHelper = new PluginPropertiesHelper(getService(), (Grid) this.exportNativeW.getFellow("canoniserPropertiesGrid"));

        formatsLB.addEventListener("onSelect", new EventListener<Event>() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        updateActions();
                    }
                });
        okB.addEventListener("onClick", new EventListener<Event>() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        export();
                    }
                });
        exportNativeW.addEventListener("onOK", new EventListener<Event>() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        export();
                    }
                });
        cancelB.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                cancel();
            }
        });
        cancelAllB.addEventListener("onClick", new EventListener<Event>() {
            @Override
            public void onEvent(final Event event) throws Exception {
                cancelAll();
            }
        });
        exportNativeW.doModal();
    }


    protected void updateActions() throws InterruptedException {
        this.okB.setDisabled(false);

        // if the selected format is an available native format, display the choice for an annotation
        String selectedFormat = (String) this.formatsLB.getSelectedItem().getValue();
        if (formats_ext.containsValue(selectedFormat)) {
            this.annotationsR.setVisible(true);
            readCanoniserInfos(selectedFormat);
            this.exportNativeW.getFellow("canoniserSelectionRow").setVisible(true);
            this.exportNativeW.getFellow("canoniserPropertiesRow").setVisible(true);
            this.exportNativeW.getFellow("canoniserMandatoryFieldsRow").setVisible(true);
        } else {
            // Export of canonical format requested
            this.annotationsR.setVisible(false);
        }
    }

    private void readCanoniserInfos(final String nativeType) throws InterruptedException {
        try {
            canoniserInfos = getService().readCanoniserInfo(nativeType);

            if (canoniserInfos.size() >= 1) {
                List<String> canoniserNames = new ArrayList<>();
                for (PluginInfo cInfo: canoniserInfos) {
                    canoniserNames.add(cInfo.getName());
                }

                Row canoniserSelectionRow = (Row) this.exportNativeW.getFellow("canoniserSelectionRow");
                if (canoniserCB != null) {
                    canoniserCB.detach();
                }
                canoniserCB = new SelectDynamicListController(canoniserNames);
                canoniserCB.setAutodrop(true);
                canoniserCB.setWidth("85%");
                canoniserCB.setHeight("100%");
                canoniserCB.setAttribute("hflex", "1");
                canoniserCB.setSelectedIndex(0);
                canoniserSelectionRow.appendChild(canoniserCB);

                canoniserCB.addEventListener("onSelect", new EventListener<Event>() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        if (event instanceof SelectEvent) {
                            String selectedCanoniser = ((SelectEvent) event).getSelectedItems().iterator().next().toString();
                            for (PluginInfo info: canoniserInfos) {
                                if (info.getName().equals(selectedCanoniser)) {
                                    pluginPropertiesHelper.showPluginProperties(info, Canoniser.DECANONISE_PARAMETER);
                                }
                            }
                        }
                    }
                });

                PluginInfo canoniserInfo = canoniserInfos.iterator().next();
                pluginPropertiesHelper.showPluginProperties(canoniserInfo, Canoniser.DECANONISE_PARAMETER);

            } else {
                Messagebox.show(MessageFormat.format("Import failed (No Canoniser found for native type {0})", nativeType), "Attention", Messagebox.OK, Messagebox.ERROR);
                cancel();
            }
        } catch (Exception e) {
            Messagebox.show("Reading Canoniser info failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        }
    }

    private void cancel() {
        this.exportNativeW.detach();
    }

    protected void cancelAll() {
        this.exportListControllerC.cancelAll();
    }

    private void export() throws InterruptedException {
        try {
            if (this.formatsLB.getSelectedItem().getValue() == null) {
                Messagebox.show("Please choose a target native type", "Attention", Messagebox.OK, Messagebox.ERROR);
            } else {
                String format = this.formatsLB.getSelectedItem().getValue();
                String ext = null;
                // retrieve the extension associated with the format
                if (Constants.CANONICAL.compareTo(format) == 0) {
                    ext = "cpf";
                } else if (format.startsWith(Constants.ANNOTATIONS)) {
                    ext = "anf";
                } else {
                    Set<String> keys = this.formats_ext.keySet();
                    for (final String k : keys) {
                        if (this.formats_ext.get(k).compareTo(format) == 0) {
                            ext = k;
                            break;
                        }
                    }
                }
                if (ext == null) {
                    throw new ExceptionExport("Format type " + format + " not supported.");
                }
                String processName = this.processNameL.getValue().replaceAll(" ", "_");
                processName = this.processNameL.getValue().replaceAll(".", "_");
                processName = this.processNameL.getValue().replaceAll(",", "_");
                processName = this.processNameL.getValue().replaceAll(":", "_");
                processName = this.processNameL.getValue().replaceAll(";", "_");
                String filename = processName + "." + ext;
                String annotation;
                Boolean withAnnotation;
                if (this.annotationsLB.getSelectedItem() != null) {
                    annotation = this.annotationsLB.getSelectedItem().getValue();
                    withAnnotation = annotation.compareTo(Constants.NO_ANNOTATIONS) != 0;
                } else {
                    annotation = format;
                    withAnnotation = false;
                }
                ExportFormatResultType exportResult = getService().exportFormat(this.processId, processName, this.versionName,
                        this.versionNumber, format, annotation, withAnnotation, UserSessionManager.getCurrentUser().getUsername(),
                        pluginPropertiesHelper.readPluginProperties(Canoniser.DECANONISE_PARAMETER));
                try (InputStream native_is = exportResult.getNative().getInputStream()) {
                    this.mainC.showPluginMessages(exportResult.getMessage());
                    Filedownload.save(native_is, "text/xml", filename);
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            Messagebox.show("Export failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } finally {
            cancel();
        }
    }

    public Window getExportOneNativeWindow() {
        return exportNativeW;
    }
}
