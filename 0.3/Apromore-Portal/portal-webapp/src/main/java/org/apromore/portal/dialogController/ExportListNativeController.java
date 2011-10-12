package org.apromore.portal.dialogController;

import org.apromore.portal.exception.ExceptionFormats;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.zkoss.zk.ui.SuspendNotAllowedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ExportListNativeController {
    private MainController mainC;        // the main controller
    private MenuController menuC;        // the menu controller which made the call
    private HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersions;
    private List<ExportOneNativeController> toExportList; // list of exports to do
    private List<ExportOneNativeController> exportedList; //list of exports done

    public ExportListNativeController(MainController mainC, MenuController menuC,
                                      HashMap<ProcessSummaryType, List<VersionSummaryType>> processVersions)
            throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
        this.mainC = mainC;
        this.menuC = menuC;
        this.processVersions = processVersions;
        this.toExportList = new ArrayList<ExportOneNativeController>();
        this.exportedList = new ArrayList<ExportOneNativeController>();
        Set<ProcessSummaryType> keySet = this.processVersions.keySet();
        Iterator<ProcessSummaryType> itP = keySet.iterator();
        while (itP.hasNext()) {
            ProcessSummaryType process = itP.next();
            Iterator<VersionSummaryType> itV = processVersions.get(process).iterator();
            while (itV.hasNext()) {
                VersionSummaryType version = itV.next();
                ExportOneNativeController exportNativeC =
                        new ExportOneNativeController(this, this.mainC, process.getId(), process.getName(), process.getOriginalNativeType(),
                                version.getName(), version.getAnnotations(), this.mainC.getNativeTypes());
                this.toExportList.add(exportNativeC);
            }
        }
    }

    public List<ExportOneNativeController> getExportedList() {
        if (exportedList == null) {
            exportedList = new ArrayList<ExportOneNativeController>();
        }
        return this.exportedList;
    }

    public List<ExportOneNativeController> getToExportList() {
        if (toExportList == null) {
            toExportList = new ArrayList<ExportOneNativeController>();
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
            ;
            this.mainC.reloadProcessSummaries();
        }
        this.mainC.displayMessage(report);
    }

    public void cancelAll() {
        for (int i = 0; i < this.toExportList.size(); i++) {
            if (this.toExportList.get(i).getExportOneNativeWindow() != null) {
                this.toExportList.get(i).getExportOneNativeWindow().detach();
            }
        }
        this.toExportList.clear();
    }
}
