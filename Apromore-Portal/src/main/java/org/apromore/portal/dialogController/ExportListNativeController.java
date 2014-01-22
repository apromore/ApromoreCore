package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.SuspendNotAllowedException;

public class ExportListNativeController extends BaseController {
    private MainController mainC;        // the main controller
    private List<ExportOneNativeController> toExportList; // list of exports to do
    private List<ExportOneNativeController> exportedList; // list of exports done

    public ExportListNativeController(MainController mainC, MenuController menuC,
            HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersions)
            throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
        this.mainC = mainC;
        this.toExportList = new ArrayList<>();
        this.exportedList = new ArrayList<>();
        Set<ProcessSummaryType> keySet = processVersions.keySet();
        for (final ProcessSummaryType process : keySet) {
            for (final VersionSummaryType version : processVersions.get(process)) {
                ExportOneNativeController exportNativeC =
                        new ExportOneNativeController(this, this.mainC, process.getId(), process.getName(), process.getOriginalNativeType(),
                                version.getName(), version.getVersionNumber(), version.getAnnotations(), this.mainC.getNativeTypes());
                this.toExportList.add(exportNativeC);
            }
        }
    }

    public List<ExportOneNativeController> getExportedList() {
        if (exportedList == null) {
            exportedList = new ArrayList<>();
        }
        return this.exportedList;
    }

    public List<ExportOneNativeController> getToExportList() {
        if (toExportList == null) {
            toExportList = new ArrayList<>();
        }
        return this.toExportList;
    }

    public void deleteFromToBeEdited(ExportOneNativeController exportNative) throws Exception {
        this.toExportList.remove(exportNative);
        if (this.toExportList.size() == 0) {
            reportExportProcess();
        }
    }

    private void reportExportProcess() throws Exception {
        String report = "Export of " + this.exportedList.size();
        if (this.exportedList.size() == 0) {
            report += " process.";
        } else {
            if (this.exportedList.size() == 1) {
                report += " process completed.";
            } else if (this.exportedList.size() > 1) {
                report += " processes completed.";
            }
            this.mainC.reloadProcessSummaries();
        }
        this.mainC.displayMessage(report);
    }

    public void cancelAll() {
        for (ExportOneNativeController aToExportList : this.toExportList) {
            if (aToExportList.getExportOneNativeWindow() != null) {
                aToExportList.getExportOneNativeWindow().detach();
            }
        }
        this.toExportList.clear();
    }
}
