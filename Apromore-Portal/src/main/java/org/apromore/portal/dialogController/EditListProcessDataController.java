package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.exception.ExceptionAllUsers;
import org.apromore.portal.exception.ExceptionDomains;
import org.zkoss.zk.ui.SuspendNotAllowedException;

public class EditListProcessDataController extends BaseController {

    private MainController mainC;
    private List<EditOneProcessDataController> toEditList;
    private List<EditOneProcessDataController> editedList;


    public EditListProcessDataController(MainController mainC, Map<ProcessSummaryType, List<VersionSummaryType>> selectedProcessVersions)
            throws SuspendNotAllowedException, InterruptedException, ExceptionAllUsers, ExceptionDomains {
        this.mainC = mainC;
        this.toEditList = new ArrayList<>();
        this.editedList = new ArrayList<>();

        // process versions are edited one by one
        Set<ProcessSummaryType> keys = selectedProcessVersions.keySet();
        for (ProcessSummaryType process : keys) {
            for (Integer i = 0; i < selectedProcessVersions.get(process).size(); i++) {
                VersionSummaryType version = selectedProcessVersions.get(process).get(i);
                EditOneProcessDataController editDataOneProcess = new EditOneProcessDataController(this.mainC, this, process, version);
                this.toEditList.add(editDataOneProcess);
            }
        }
    }

    /**
     * Return list of controllers associated with process versions already edited
     * @return List<EditOneProcessDataController>
     */
    public List<EditOneProcessDataController> getEditedList() {
        if (editedList == null) {
            editedList = new ArrayList<>();
        }
        return editedList;
    }

    /**
     * Return list of controllers associated with process versions still to be edited
     * @return List<EditOneProcessDataController>
     */
    public List<EditOneProcessDataController> getToEditList() {
        if (toEditList == null) {
            toEditList = new ArrayList<>();
        }
        return toEditList;
    }

    /**
     * Remove editOneProcess from list of controllers associated with process
     * versions still to be edited.
     * @param editOneProcess remove a model from the list to be edited.
     * @throws Exception if something goes wrong
     */
    public void deleteFromToBeEdited(EditOneProcessDataController editOneProcess) throws Exception {
        this.toEditList.remove(editOneProcess);
        if (this.toEditList.size() == 0) {
            reportEditData();
        }
    }

    /**
     * Return a message which summarises edition work.
     * If necessary, send request to main controller to refresh
     * the table of process version summaries
     *
     * @throws Exception
     */
    private void reportEditData() throws Exception {
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

    /**
     * Cancel edition of remining process versions: empty the list of
     * controllers associated to process versions still to be edited.
     *
     * @throws Exception
     */
    public void cancelAll() throws Exception {
        for (EditOneProcessDataController aToEditList : this.toEditList) {
            if (aToEditList.getEditDataOneProcessWindow() != null) {
                aToEditList.getEditDataOneProcessWindow().detach();
            }
        }
        this.toEditList.clear();
        reportEditData();
    }

    public MainController getMainController() {
        return mainC;
    }
}

