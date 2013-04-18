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

    public EditOneProcessController(MainController mainC, EditListProcessesController editListProcessesC,
            ProcessSummaryType process, VersionSummaryType version)
            throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {

        this.mainC = mainC;
        this.editListProcessesC = editListProcessesC;
        this.process = process;
        this.version = version;

        this.chooseNativeW = (Window) Executions.createComponents("macros/choosenative.zul", null, null);
        this.chooseNativeW.setTitle("Edit process " + process.getName() + ", " + version.getName() + ".");
        Rows rows = (Rows) this.chooseNativeW.getFirstChild().getFirstChild().getFirstChild().getNextSibling();
        Row warning = (Row) rows.getFirstChild();
        Row nativeTypesR = (Row) warning.getNextSibling();
        Row annotationR = (Row) nativeTypesR.getNextSibling();
        Row readOnlyR = (Row) annotationR.getNextSibling();
        Row buttonsR = (Row) readOnlyR.getNextSibling();
        this.nativeTypesLB = (Listbox) nativeTypesR.getFirstChild().getNextSibling();
        this.annotationsLB = (Listbox) annotationR.getFirstChild().getNextSibling();
        this.annotationOnlyCB = (Checkbox) readOnlyR.getFirstChild().getNextSibling();
        Button okB = (Button) buttonsR.getFirstChild().getFirstChild();
        Button cancelB = (Button) okB.getNextSibling();
        Button cancelAllB = (Button) cancelB.getNextSibling();

        // enable cancelAll button if at least 1 process versions left.
        cancelAllB.setVisible(this.editListProcessesC.getToEditList().size() > 0);

        // build native format listbox
        HashMap<String, String> formats = this.mainC.getNativeTypes();
        Set<String> extensions = formats.keySet();
        Iterator<String> it = extensions.iterator();
        Listitem cbi;
        while (it.hasNext()) {
            cbi = new Listitem();
            this.nativeTypesLB.appendChild(cbi);
            cbi.setLabel(formats.get(it.next()));
            if (process.getOriginalNativeType().equals(cbi.getLabel())) {
                cbi.setSelected(true);
            }
        }
        //((Listitem) this.nativeTypesLB.getFirstChild()).setSelected(true);

        // Build list of annotations associated with the process version
        for (int i = 0; i < this.version.getAnnotations().size(); i++) {
            String native_type = this.version.getAnnotations().get(i).getNativeType();
            for (int k = 0; k < this.version.getAnnotations().get(i).getAnnotationName().size(); k++) {
                cbi = new Listitem();
                this.annotationsLB.appendChild(cbi);
                cbi.setLabel(this.version.getAnnotations().get(i).getAnnotationName().get(k) + " (" + native_type + ")");
                cbi.setValue(this.version.getAnnotations().get(i).getAnnotationName().get(k));
                if (Constants.INITIAL_ANNOTATION.compareTo((String) cbi.getValue()) == 0) {
                    cbi.setSelected(true);
                }
            }
        }
        noAnnotationI = new Listitem();
        noAnnotationI.setLabel(Constants.NO_ANNOTATIONS);
        annotationsLB.appendChild(this.noAnnotationI);

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
        this.annotationOnlyCB.setDisabled(this.noAnnotationI.isSelected());
    }

    /**
     * If check box "Annotation only" is checked item "- no annotation" has to be disabled
     * @param event the event that triggered this method.
     */
    protected void syncListboxe(Event event) {
        // if users tick "annotation only", disable "no annotation" in the list
        this.noAnnotationI.setDisabled(this.annotationOnlyCB.isChecked());
    }

    protected void cancel() throws Exception {
        // delete process from the list of processes still to be edited
        this.editListProcessesC.deleteFromToBeEdited(this);
        closePopup();
    }

    private void closePopup() {
        this.chooseNativeW.detach();
    }

    protected void cancelAll() {
        this.editListProcessesC.cancelAll();
    }

    protected void editProcess() throws Exception {
        if (this.nativeTypesLB.getSelectedItem() == null || this.nativeTypesLB.getSelectedItem() != null
                && this.nativeTypesLB.getSelectedItem().getLabel().compareTo("") == 0) {
            Messagebox.show(CHOOSE_NATIVE, ATTENTION, Messagebox.OK, Messagebox.ERROR);
        } else {
            String readOnly;
            Listitem cbi = this.nativeTypesLB.getSelectedItem();
            String nativeType = cbi.getLabel();
            String annotation = null;
            if (this.annotationsLB.getSelectedItem() != null
                    && Constants.NO_ANNOTATIONS.compareTo(this.annotationsLB.getSelectedItem().getLabel()) != 0) {
                annotation = (String) this.annotationsLB.getSelectedItem().getValue();
            }
            if (this.annotationOnlyCB.isChecked()) {
                readOnly = TRUE;
            } else {
                readOnly = FALSE;
            }

            this.mainC.editProcess(this.process, this.version, nativeType, annotation, readOnly);
            this.editListProcessesC.deleteFromToBeEdited(this);
            closePopup();
        }
    }



    public Window getEditOneProcessWindow() {
        return chooseNativeW;
    }
}
