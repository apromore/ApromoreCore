package org.apromore.portal.dialogController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

public class EditOneProcessController extends BaseController {

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
    private EditListProcessesController editListProcessesC;
    private ProcessSummaryType process;
    private VersionSummaryType version;

    public EditOneProcessController(MainController mainController, EditListProcessesController editListProcessesController,
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
        Row buttonsR = (Row) readOnlyR.getNextSibling();
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
        Iterator<String> it = extensions.iterator();
        Listitem cbi;
        while (it.hasNext()) {
            String label = formats.get(it.next());
            if (!label.equals("AML fragment")) {
                cbi = new Listitem();
                nativeTypesLB.appendChild(cbi);
                cbi.setLabel(formats.get(it.next()));
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
        for (int i = 0; i < version.getAnnotations().size(); i++) {
            String native_type = version.getAnnotations().get(i).getNativeType();
            for (int k = 0; k < version.getAnnotations().get(i).getAnnotationName().size(); k++) {
                cbi = new Listitem();
                annotationsLB.appendChild(cbi);
                cbi.setLabel(version.getAnnotations().get(i).getAnnotationName().get(k) + " (" + native_type + ")");
                cbi.setValue(version.getAnnotations().get(i).getAnnotationName().get(k));
                if (Constants.INITIAL_ANNOTATION.compareTo((String) cbi.getValue()) == 0) {
                    cbi.setSelected(true);
                }
            }
        }
        noAnnotationI = new Listitem();
        noAnnotationI.setLabel(Constants.NO_ANNOTATIONS);
        annotationsLB.appendChild(noAnnotationI);

        nativeTypesLB.addEventListener(Events.ON_SELECT,
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        syncAnnotationLB(event);
                    }
                });
        annotationsLB.addEventListener(Events.ON_SELECT,
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        syncReadOnlyR(event);
                    }
                });
        annotationOnlyCB.addEventListener(Events.ON_CHECK,
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        syncListboxe(event);
                    }
                });
        okB.addEventListener(Events.ON_CLICK,
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        editProcess();
                    }
                });
        chooseNativeW.addEventListener(Events.ON_OK,
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        editProcess();
                    }
                });
        cancelB.addEventListener(Events.ON_CLICK,
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });
        cancelAllB.addEventListener(Events.ON_CLICK,
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        cancelAll();
                    }
                });
        chooseNativeW.doModal();
    }

    /**
     * If "no annotation" has been selected in the list box, the row
     * with checkbox "annotation only" has to be disabled.
     * @param event the event that triggered this method.
     */
    protected void syncReadOnlyR(Event event) {
        annotationOnlyCB.setDisabled(noAnnotationI.isSelected());
    }

    /**
     * If check box "Annotation only" is checked item "- no annotation" has to be disabled
     * @param event the event that triggered this method.
     */
    protected void syncListboxe(Event event) {
        // if users tick "annotation only", disable "no annotation" in the list
        noAnnotationI.setDisabled(annotationOnlyCB.isChecked());
    }

    /**
     * If the native type is selected is the same as process original type then disable the annotations lb.
     * @param event the on select event.
     */
    protected void syncAnnotationLB(Event event) {
        if (nativeTypesLB.getSelectedItem() != null) {
            String selected = nativeTypesLB.getSelectedItem().getLabel();
            if (process.getOriginalNativeType() != null && process.getOriginalNativeType().equals(selected)) {
                annotationsLB.setDisabled(true);
                annotationOnlyCB.setDisabled(true);
            } else {
                annotationsLB.setDisabled(false);
                annotationOnlyCB.setDisabled(noAnnotationI.isSelected());
            }
        }
    }

    protected void cancel() throws Exception {
        // delete process from the list of processes still to be edited
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
            if (annotationsLB.getSelectedItem() != null
                    && Constants.NO_ANNOTATIONS.compareTo(annotationsLB.getSelectedItem().getLabel()) != 0) {
                annotation = (String) annotationsLB.getSelectedItem().getValue();
            }
            if (annotationOnlyCB.isChecked()) {
                readOnly = TRUE;
            } else {
                readOnly = FALSE;
            }

            mainC.editProcess(process, version, nativeType, annotation, readOnly);
            editListProcessesC.deleteFromToBeEdited(this);
            closePopup();
        }
    }



    public Window getEditOneProcessWindow() {
        return chooseNativeW;
    }
}
