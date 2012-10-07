package org.apromore.portal.dialogController;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apromore.model.AnnotationsType;
import org.apromore.model.ExportFormatResultType;
import org.apromore.plugin.property.RequestPropertyType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.exception.ExceptionExport;
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

public class ExportOneNativeController extends BaseController {

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
    private final HashMap<String, String> formats_ext;
    // <k, v> belongs to nativeTypes: the file extension k
    // is associated with the native type v (<xpdl,XPDL 1.2>)

    public ExportOneNativeController(final ExportListNativeController exportListControllerC, final MainController mainC, final int processId, final String processName,
            final String originalType, final String versionName, final List<AnnotationsType> annotations, final HashMap<String, String> formats_ext)
            throws SuspendNotAllowedException, InterruptedException {

        this.mainC = mainC;
        this.exportListControllerC = exportListControllerC;

        this.exportNativeW = (Window) Executions.createComponents("macros/exportnative.zul", null, null);
        this.processId = processId;
        this.versionName = versionName;
        this.formats_ext = formats_ext;
        String id = this.processId + " " + this.versionName;
        this.exportNativeW.setId(id);
        Grid exportNatG = (Grid) this.exportNativeW.getFirstChild().getFirstChild();
        Rows exportNatRs = (Rows) exportNatG.getFirstChild().getNextSibling();
        Row processNameR = (Row) exportNatRs.getFirstChild();
        Row versionNameR = (Row) processNameR.getNextSibling();
        Row formatsR = (Row) versionNameR.getNextSibling();
        this.annotationsR = (Row) formatsR.getNextSibling();
        Row buttonsR = (Row) this.annotationsR.getNextSibling();
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

        // Build list of available formats for export.
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
        for (final String extension : extensions) {
            cbi = new Listitem();
            this.formatsLB.appendChild(cbi);
            cbi.setLabel(this.formats_ext.get(extension));
            cbi.setValue(this.formats_ext.get(extension));
        }

        this.formatsLB.setSelectedItem((Listitem) this.formatsLB.getFirstChild());
        this.formatsLB.addEventListener("onSelect", new EventListener() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        updateActions();
                    }
                });
        this.okB.addEventListener("onClick", new EventListener() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        export();
                    }
                });
        this.exportNativeW.addEventListener("onOK", new EventListener() {
                    @Override
                    public void onEvent(final Event event) throws Exception {
                        export();
                    }
                });
        cancelB.addEventListener("onClick", new EventListener() {
            @Override
            public void onEvent(final Event event) throws Exception {
                cancel();
            }
        });
        cancelAllB.addEventListener("onClick", new EventListener() {
            @Override
            public void onEvent(final Event event) throws Exception {
                cancelAll();
            }
        });
        this.exportNativeW.doModal();
    }


    protected void updateActions() {
        this.okB.setDisabled(false);
        // if the selected format is an available native format, display
        // the choice for an annotation
        if (formats_ext.containsValue(this.formatsLB.getSelectedItem().getValue())) {
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
                Messagebox.show("Please choose a target native type", "Attention", Messagebox.OK, Messagebox.ERROR);
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
                String processname = this.processNameL.getValue().replaceAll(" ", "_");
                processname = this.processNameL.getValue().replaceAll(".", "_");
                processname = this.processNameL.getValue().replaceAll(",", "_");
                processname = this.processNameL.getValue().replaceAll(":", "_");
                processname = this.processNameL.getValue().replaceAll(";", "_");
                String filename = processname + "." + ext;
                String annotation = null;
                Boolean withAnnotation;
                if (this.annotationsLB.getSelectedItem() != null) {
                    annotation = (String) this.annotationsLB.getSelectedItem().getValue();
                    withAnnotation = annotation.compareTo(Constants.NO_ANNOTATIONS) != 0;
                } else {
                    withAnnotation = false;
                }
                ExportFormatResultType exportResult = getService().exportFormat(this.processId, processname, this.versionName, format, annotation, withAnnotation,
                        this.mainC.getCurrentUser().getUsername(), new HashSet<RequestPropertyType<?>>());
                InputStream native_is = exportResult.getNative().getInputStream();
                this.mainC.showCanoniserMessages(exportResult.getMessage());
                Filedownload.save(native_is, "text.xml", filename);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Messagebox.show("Export failed (" + e.getMessage() + ")", "Attention", Messagebox.OK, Messagebox.ERROR);
        } finally {
            cancel();
        }
    }

    public Window getExportOneNativeWindow() {
        return exportNativeW;
    }
}
