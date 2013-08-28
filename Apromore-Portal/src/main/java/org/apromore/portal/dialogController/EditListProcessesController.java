package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.exception.ExceptionFormats;
import org.zkoss.zk.ui.SuspendNotAllowedException;

public class EditListProcessesController extends BaseController {

    private MainController mainC;
    private List<EditOneProcessController> toEditList;
    private List<EditOneProcessController> editedList;

    public EditListProcessesController(MainController mainC, MenuController menuC, Map<ProcessSummaryType, List<VersionSummaryType>> processVersions)
            throws SuspendNotAllowedException, InterruptedException, ExceptionFormats {
        this.mainC = mainC;
        this.toEditList = new ArrayList<>();
        this.editedList = new ArrayList<>();
        Set<ProcessSummaryType> keys = processVersions.keySet();
        for (ProcessSummaryType process : keys) {
            for (Integer i = 0; i < processVersions.get(process).size(); i++) {
                VersionSummaryType version = processVersions.get(process).get(i);
                EditOneProcessController editOneProcess = new EditOneProcessController(this.mainC, this, process, version);
                this.toEditList.add(editOneProcess);
            }
        }
    }

    public List<EditOneProcessController> getEditedList() {
        if (editedList == null) {
            editedList = new ArrayList<>();
        }
        return this.editedList;
    }


    public List<EditOneProcessController> getToEditList() {
        if (toEditList == null) {
            toEditList = new ArrayList<>();
        }
        return toEditList;
    }

    public void deleteFromToBeEdited(EditOneProcessController editOneProcess) throws Exception {
        this.toEditList.remove(editOneProcess);
        if (this.toEditList.size() == 0) {
            reportEditProcess();
        }
    }

    private void reportEditProcess() throws Exception {
        String report = "Modification of " + this.editedList.size();
        if (this.editedList.size() == 0) {
            report += " process.";
        } else {
            if (this.editedList.size() == 1) {
                report += " process completed.";
            } else if (this.editedList.size() > 1) {
                report += " processes completed.";
            }
            this.mainC.reloadProcessSummaries();
        }
        this.mainC.displayMessage(report);
    }

    public void cancelAll() {
        for (EditOneProcessController aToEditList : this.toEditList) {
            if (aToEditList.getEditOneProcessWindow() != null) {
                aToEditList.getEditOneProcessWindow().detach();
            }
        }
        this.toEditList.clear();
    }
}
