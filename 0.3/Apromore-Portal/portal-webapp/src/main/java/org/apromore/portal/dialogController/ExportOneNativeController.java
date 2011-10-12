package org.apromore.portal.dialogController;

import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionExport;
import org.apromore.portal.manager.RequestToManager;
import org.apromore.model.AnnotationsType;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;
import org.zkoss.zul.api.Row;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ExportOneNativeController extends Window {

    private Window exportNativeW;
    private MainController mainC;
    private ExportListNativeController exportListControllerC;
    private Grid exportNatG;
    private Rows exportNatRs;
    private Label processNameL;
    private Label versionNameL;
    private Row annotationsR;
    private Listbox annotationsLB;
    private Button okB;
    private Button cancelB;
    private Button cancelAllB;
    private Listbox formatsLB;
    private int processId;
    private String versionName;
    private String originalType;
    private List<AnnotationsType> annotations;    // list of available annotations for this process version
    private HashMap<String, String> formats_ext; // <k, v> belongs to nativeTypes: the file extension k
    // is associated with the native type v (<xpdl,XPDL 1.2>)

    public ExportOneNativeController(ExportListNativeController exportListControllerC,
                                     MainController mainC, int processId,
                                     String processName, String originalType, String versionName,
                                     List<AnnotationsType> annotations, HashMap<String, String> formats_ext) throws SuspendNotAllowedException, InterruptedException {

        this.mainC = mainC;
        this.exportListControllerC = exportListControllerC;

        this.exportNativeW = (Window) Executions.createComponents("macros/exportnative.zul", null, null);
        this.processId = processId;
        this.versionName = versionName;
        this.originalType = originalType;
        this.formats_ext = formats_ext;
        this.annotations = annotations;
        String id = this.processId + " " + this.versionName;
        this.exportNativeW.setId(id);
        this.exportNatG = (Grid) this.exportNativeW.getFirstChild().getFirstChild();
        this.exportNatRs = (Rows) exportNatG.getFirstChild().getNextSibling();
        Row processNameR = (Row) exportNatRs.getFirstChild();
        Row versionNameR = (Row) processNameR.getNextSibling();
        Row formatsR = (Row) versionNameR.getNextSibling();
        this.annotationsR = (Row) formatsR.getNextSibling();
        Row buttonsR = (Row) this.annotationsR.getNextSibling();
        this.processNameL = (Label) processNameR.getFirstChild().getNextSibling();
        this.processNameL.setValue(processName);
        this.versionNameL = (Label) versionNameR.getFirstChild().getNextSibling();
        this.versionNameL.setValue(versionName);
        this.annotationsLB = (Listbox) this.annotationsR.getFirstChild().getNextSibling();
        this.formatsLB = (Listbox) formatsR.getFirstChild().getNextSibling();
        this.okB = (Button) buttonsR.getFirstChild().getFirstChild();
        this.cancelB = (Button) this.okB.getNextSibling();
        this.cancelAllB = (Button) this.cancelB.getNextSibling();


        // enable cancelAll button if at least 1 process versions left.
        this.cancelAllB.setVisible(this.exportListControllerC.getToExportList().size() > 0);

        // Build list of available formats for export.
        Listitem cbi;
        // - Canonical format
        cbi = new Listitem();
        this.formatsLB.appendChild(cbi);
        cbi.setLabel(Constants.CANONICAL);
        cbi.setValue(Constants.CANONICAL);
        // - Annotations associated with the process version
        Listitem cba = new Listitem();
        cba = new Listitem();
        cba.setLabel(Constants.NO_ANNOTATIONS);
        cba.setValue(Constants.NO_ANNOTATIONS);
        this.annotationsLB.appendChild(cba);

        for (int i = 0; i < this.annotations.size(); i++) {
            String nat_type = this.annotations.get(i).getNativeType();
            for (int k = 0; k < annotations.get(i).getAnnotationName().size(); k++) {
                cbi = new Listitem();
                this.formatsLB.appendChild(cbi);
                cbi.setLabel(Constants.ANNOTATIONS + " - " + annotations.get(i).getAnnotationName().get(k)
                        + " (" + nat_type + ")");
                cbi.setValue(Constants.ANNOTATIONS + " - " + annotations.get(i).getAnnotationName().get(k));

                cba = new Listitem();
                this.annotationsLB.appendChild(cba);
                cba.setLabel(this.annotations.get(i).getAnnotationName().get(k)
                        + " (" + nat_type + ")");
                cba.setValue(this.annotations.get(i).getAnnotationName().get(k));
                if (Constants.INITIAL_ANNOTATION.compareTo(this.annotations.get(i).getAnnotationName().get(k)) == 0) {
                    cba.setSelected(true);
                }
            }
        }
        // - Available native formats
        Set<String> extensions = this.formats_ext.keySet();
        Iterator<String> it = extensions.iterator();
        while (it.hasNext()) {
            cbi = new Listitem();
            this.formatsLB.appendChild(cbi);
            String nat_format = it.next();
            cbi.setLabel(this.formats_ext.get(nat_format));
            cbi.setValue(this.formats_ext.get(nat_format));
        }
        this.formatsLB.setSelectedItem((Listitem) this.formatsLB.getFirstChild());
        this.formatsLB.addEventListener("onSelect",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        updateActions();
                    }
                });

        this.okB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        export();
                    }
                });
        this.exportNativeW.addEventListener("onOK",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        export();
                    }
                });
        this.cancelB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        cancel();
                    }
                });
        this.cancelAllB.addEventListener("onClick",
                new EventListener() {
                    public void onEvent(Event event) throws Exception {
                        cancelAll();
                    }
                });
        this.exportNativeW.doModal();
    }


    protected void updateActions() {
        this.okB.setDisabled(false);
        // if the selected format is an available native format, display
        // the choice for an annotation
        if (formats_ext.containsValue((String) this.formatsLB.getSelectedItem().getValue())) {
            this.annotationsR.setVisible(true);
        } else {
            this.annotationsR.setVisible(false);
        }
        //this.nativeTypesLB.removeChild(this.emptynative);
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
                Messagebox.show("Please choose a target native type", "Attention", Messagebox.OK,
                        Messagebox.ERROR);
            } else {
                String format = (String) this.formatsLB.getSelectedItem().getValue();
                String ext = null;
                // retrieve the extension associated with the format
                if (Constants.CANONICAL.compareTo(format) == 0) {
                    ext = "cpf";
                } else if (format.startsWith(Constants.ANNOTATIONS)) {
                    ext = "anf";
                } else {
                    Set<String> keys = this.formats_ext.keySet();
                    Iterator<String> it = keys.iterator();
                    while (it.hasNext()) {
                        String k = it.next();
                        if (this.formats_ext.get(k).compareTo(format) == 0) {
                            ext = k;
                            break;
                        }
                    }
                }
                if (ext == null) {
                    throw new ExceptionExport("Format type " + format
                            + " not supported.");
                }
                String processname = this.processNameL.getValue().replaceAll(" ", "_");
                processname = this.processNameL.getValue().replaceAll(".", "_");
                processname = this.processNameL.getValue().replaceAll(",", "_");
                processname = this.processNameL.getValue().replaceAll(":", "_");
                processname = this.processNameL.getValue().replaceAll(";", "_");
                String filename = processname + "." + ext;
                String annotation = null;
                Boolean withAnnotation = false;
                if (this.annotationsLB.getSelectedItem() != null) {
                    annotation = (String) this.annotationsLB.getSelectedItem().getValue();
                    withAnnotation = annotation.compareTo(Constants.NO_ANNOTATIONS) != 0;
                } else {
                    withAnnotation = false;
                }
                RequestToManager request = new RequestToManager();
                InputStream native_is =
                        request.ExportFormat(this.processId, processname, this.versionName, format, annotation, withAnnotation,
                                this.mainC.getCurrentUser().getUsername());
                Filedownload.save(native_is, "text.xml", filename);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            Messagebox.show("Export failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
                    Messagebox.ERROR);
        } catch (ExceptionExport e) {
            e.printStackTrace();
            Messagebox.show("Export failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
                    Messagebox.ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            Messagebox.show("Export failed (" + e.getMessage() + ")", "Attention", Messagebox.OK,
                    Messagebox.ERROR);
        } finally {
            cancel();
        }
    }

    public Window getExportOneNativeWindow() {
        return exportNativeW;
    }
}
