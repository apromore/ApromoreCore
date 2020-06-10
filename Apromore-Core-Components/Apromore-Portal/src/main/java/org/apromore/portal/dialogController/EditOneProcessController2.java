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

package org.apromore.portal.dialogController;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apromore.canoniser.Canoniser;
import org.apromore.model.PluginInfo;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.portal.util.CollectionUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

public class EditOneProcessController2 extends BaseController {

    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String ATTENTION = "Attention";
    private static final String CHOOSE_NATIVE = "Please select a native type.";

    private Window chooseNativeW;
    private Listbox nativeTypesLB;
    private Listbox annotationsLB;
    private Checkbox annotationOnlyCB;
    private Listitem noAnnotationI;
    private MainController mainC;
    private EditListProcessesController2 editListProcessesC;
    private ProcessSummaryType process;
    private VersionSummaryType version;
    private Set<PluginInfo> canoniserInfos;
    private final PluginPropertiesHelper pluginPropertiesHelper;

    private SelectDynamicListController canoniserCB;

    public EditOneProcessController2(MainController mainController, EditListProcessesController2 editListProcessesController,
            ProcessSummaryType processType, VersionSummaryType versionType)
            throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
        mainC = mainController;
        editListProcessesC = editListProcessesController;
        process = processType;
        version = versionType;

        chooseNativeW = (Window) Executions.createComponents("macros/choosenative.zul", null, null);
        chooseNativeW.setTitle("Edit process " + process.getName() + ", " + version.getName() + ".");
        Rows rows = (Rows) chooseNativeW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row warning = (Row) rows.getFirstChild();
        Row nativeTypesR = (Row) warning.getNextSibling();
        Row annotationR = (Row) nativeTypesR.getNextSibling();
        Row readOnlyR = (Row) annotationR.getNextSibling();
        Row paramsR = (Row) readOnlyR.getNextSibling();
        Row buttonsR = (Row) paramsR.getNextSibling().getNextSibling();
        nativeTypesLB = (Listbox) nativeTypesR.getFirstChild().getNextSibling();
        annotationsLB = (Listbox) annotationR.getFirstChild().getNextSibling();
        annotationOnlyCB = (Checkbox) readOnlyR.getFirstChild().getNextSibling();
        Button okB = (Button) buttonsR.getFirstChild().getFirstChild();
        Button cancelB = (Button) okB.getNextSibling();
        Button cancelAllB = (Button) cancelB.getNextSibling();

        // enable cancelAll button if at least 1 process versions left.
        cancelAllB.setVisible(editListProcessesC.getToEditList().size() > 0);

        // build native format listbox
        HashMap<String, String> formats = mainC.getNativeTypes();
        Set<String> extensions = formats.keySet();
        List<String> sorted = CollectionUtil.asSortedList(extensions);
        Iterator<String> it = sorted.iterator();
        Listitem cbi;
        while (it.hasNext()) {
            String label = formats.get(it.next());
            if (!label.equals("AML fragment")&&!label.equals("XPDL 2.2")) {
                cbi = new Listitem();
                nativeTypesLB.appendChild(cbi);
                cbi.setLabel(label);
                if (process.getOriginalNativeType() != null && process.getOriginalNativeType().equals(cbi.getLabel())) {
                    cbi.setSelected(true);
                }
            }
        }
        if (nativeTypesLB.getSelectedCount() == 0) {
            nativeTypesLB.setSelectedIndex(0);
        }

        // Build list of annotations associated with the process version
        annotationsLB.setDisabled(true);
        annotationOnlyCB.setDisabled(true);
        if (version.getAnnotations().size() > 0) {
            for (int i = 0; i < version.getAnnotations().size(); i++) {
                String native_type = version.getAnnotations().get(i).getNativeType();
                for (int k = 0; k < version.getAnnotations().get(i).getAnnotationName().size(); k++) {
                    cbi = new Listitem();
                    annotationsLB.appendChild(cbi);
                    cbi.setLabel(version.getAnnotations().get(i).getAnnotationName().get(k) + " (" + native_type + ")");
                    cbi.setValue(version.getAnnotations().get(i).getAnnotationName().get(k));
                    if (Constants.INITIAL_ANNOTATION.compareTo((String) cbi.getValue()) == 0 || version.getAnnotations().size() == 1) {
                        cbi.setSelected(true);
                    }
                }
            }
            noAnnotationI = new Listitem();
            noAnnotationI.setLabel(Constants.NO_ANNOTATIONS);
            annotationsLB.appendChild(noAnnotationI);
        } else {
            noAnnotationI = new Listitem();
            noAnnotationI.setLabel(Constants.NO_ANNOTATIONS);
            noAnnotationI.setSelected(true);
            annotationsLB.appendChild(noAnnotationI);
        }

        // setup the class that will help build the parameters for a canoniser
        pluginPropertiesHelper = new PluginPropertiesHelper(getService(), (Grid) this.chooseNativeW.getFellow("canoniserPropertiesGrid"));

        // setup the events
        nativeTypesLB.addEventListener(Events.ON_SELECT,
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        syncAnnotationLB();
                        updateCanoniserParameters();
                    }
                });
        annotationsLB.addEventListener(Events.ON_SELECT,
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        syncReadOnlyR();
                    }
                });
        annotationOnlyCB.addEventListener(Events.ON_CHECK,
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        syncListboxe();
                    }
                });
        okB.addEventListener(Events.ON_CLICK,
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        editProcess();
                    }
                });
        chooseNativeW.addEventListener(Events.ON_OK,
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        editProcess();
                    }
                });
        cancelB.addEventListener(Events.ON_CLICK,
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });
        cancelAllB.addEventListener(Events.ON_CLICK,
                new EventListener<Event>() {
                    public void onEvent(Event event) throws Exception {
                        cancelAll();
                    }
                });
        chooseNativeW.doModal();
    }

    /**
     * If "no annotation" has been selected in the list box, the row
     * with checkbox "annotation only" has to be disabled.
     */
    protected void syncReadOnlyR() {
        annotationOnlyCB.setDisabled(noAnnotationI.isSelected());
    }

    /**
     * If check box "Annotation only" is checked item "- no annotation" has to be disabled
     */
    protected void syncListboxe() {
        noAnnotationI.setDisabled(annotationOnlyCB.isChecked());
    }

    /**
     * If the native type is selected is the same as process original type then disable the annotations lb. But if the annotation format type isn't the same as the process native type (i.e. someone saved a bpmn as an epml) the don't disable.
     */
    protected void syncAnnotationLB() {
        if (nativeTypesLB.getSelectedItem() != null) {
            Listitem selected = nativeTypesLB.getSelectedItem();
            if (process.getOriginalNativeType() != null && process.getOriginalNativeType().equals(selected.getLabel())) {
                Listitem annotation = annotationsLB.getSelectedItem();
                if (annotation != null && annotation.getLabel().contains(process.getOriginalNativeType())) {
                    annotationsLB.setDisabled(true);
                    annotationOnlyCB.setDisabled(true);
                } else {
                    annotationsLB.setDisabled(false);
                    annotationOnlyCB.setDisabled(false);
                }
            } else {
                annotationsLB.setDisabled(false);
                annotationOnlyCB.setDisabled(noAnnotationI.isSelected());
            }
        }
    }

    protected void cancel() throws Exception {
        editListProcessesC.deleteFromToBeEdited(this);
        closePopup();
    }

    private void closePopup() {
        chooseNativeW.detach();
    }

    protected void cancelAll() {
        editListProcessesC.cancelAll();
    }

    protected void editProcess() throws Exception {
        if (nativeTypesLB.getSelectedItem() == null || nativeTypesLB.getSelectedItem() != null
                && nativeTypesLB.getSelectedItem().getLabel().compareTo("") == 0) {
            Messagebox.show(CHOOSE_NATIVE, ATTENTION, Messagebox.OK, Messagebox.ERROR);
        } else {
            String readOnly;
            Listitem cbi = nativeTypesLB.getSelectedItem();
            String nativeType = cbi.getLabel();
            String annotation = null;
            if (annotationsLB.getSelectedItem() != null && Constants.NO_ANNOTATIONS.compareTo(annotationsLB.getSelectedItem().getLabel()) != 0) {
                annotation = annotationsLB.getSelectedItem().getValue();
            }
            
            if (annotationOnlyCB.isChecked()) {
                readOnly = TRUE;
            } else {
                readOnly = FALSE;
            }

            mainC.editProcess2(process, version, nativeType, annotation, readOnly,
                    pluginPropertiesHelper.readPluginProperties(Canoniser.DECANONISE_PARAMETER), false);
            editListProcessesC.deleteFromToBeEdited(this);
            closePopup();
        }
    }



    public Window getEditOneProcessWindow() {
        return chooseNativeW;
    }


    /** Used to update the list of parameters that a canoniser may require for use. */
    private void updateCanoniserParameters() throws InterruptedException {
        if (nativeTypesLB.getSelectedItem() != null) {
            readCanoniserInfos(nativeTypesLB.getSelectedItem().getLabel());
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

                Row canoniserSelectionRow = (Row) this.chooseNativeW.getFellow("canoniserSelectionRow");
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

    /*
    private boolean readCanoniserInfos(final String nativeType) throws InterruptedException {
        try {
            Set<PluginInfo> canoniserInfos = getService().readCanoniserInfo(nativeType);

            if (canoniserInfos.size() >= 1) {
                PluginInfo canoniserInfo = canoniserInfos.iterator().next();
                pluginPropertiesHelper.showPluginProperties(canoniserInfo, Canoniser.CANONISE_PARAMETER);

                return true;
            } else {
                Messagebox.show(MessageFormat.format("Import failed (No Canoniser found for native type {0})", nativeType), "Attention",
                        Messagebox.OK, Messagebox.ERROR);
                return false;
           }
        } catch (Exception e) {
            Messagebox.show("Reading Canoniser info failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
            return false;
        }
    }
    */

}
