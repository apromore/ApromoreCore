package org.apromore.portal.dialogController;

import org.apromore.portal.dialogController.renderer.ProcessSummaryItemRenderer;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;

import java.util.List;
import java.util.Map;

public class ProcessListboxController extends BaseListboxController {

    private static final long serialVersionUID = -6874531673992239378L;

    private Listheader columnScore; // column to display process score for the
    // purpose of answering query

    private Boolean isQueryResult; // says whether the data to be displayed have
    // been produced by a query

    private ProcessSummaryType processQ;
    private VersionSummaryType versionQ;

    public ProcessListboxController(MainController mainController) {
        super(mainController, "macros/listbox/processSummaryListbox.zul", new ProcessSummaryItemRenderer());

        this.columnScore = (Listheader) this.getListBox().getFellow("columnScore");

        // TODO should be replaced by ListModel listener in zk 6
        getListBox().addEventListener("onSelect", new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (getListBox().getSelectedItems().size() == 1) {
                    getMainController().displayProcessVersions((ProcessSummaryType) getListModel().getSelection().iterator().next());
                } else {
                    getMainController().clearProcessVersions();
                }
            }
        });
    }

    /*
      * (non-Javadoc)
      *
      * @see
      * org.apromore.portal.dialogController.BaseListboxController#refreshContent
      * ()
      */
    @Override
    protected void refreshContent() {
        getMainController().reloadProcessSummaries();
    }

    /**
     * Display process versions given in processSummaries. If isQueryResult this
     * results from a search whose query is versionQ, given processQ
     * @param processSummaries
     * @param isQueryResult
     * @param processQ
     * @param versionQ
     */
    public void displayProcessSummaries(ProcessSummariesType processSummaries, Boolean isQueryResult, ProcessSummaryType processQ, VersionSummaryType versionQ) {
        this.isQueryResult = isQueryResult;
        this.processQ = processQ;
        this.versionQ = versionQ;
        this.columnScore.setVisible(isQueryResult);

        getListBox().clearSelection();
        getListModel().clear();
        getListModel().addAll(processSummaries.getProcessSummary());
    }

    /**
     * refresh the display without reloading the data. Keeps selection if any.
     */
    protected void refresh() {
        getListBox().renderAll();
    }

    /**
     * Add the process to the table
     */
    public void displayNewProcess(ProcessSummaryType process) {
        getListModel().add(process);
    }

    public void unDisplay(Map<ProcessSummaryType, List<VersionSummaryType>> processVersions) {
        // Workaround until better solution
        refreshContent();
        // for (Map.Entry<ProcessSummaryType, List<VersionSummaryType>> entry :
        // processVersions
        // .entrySet()) {
        //
        // ProcessSummaryType deletedProcess = entry.getKey();
        //
        // for (VersionSummaryType deletedVersion : entry.getValue()) {
        //
        // }
        //
        // }
    }

    public Listbox getProcessSummariesGrid() {
        return getListBox();
    }

    public Boolean getIsQueryResult() {
        return isQueryResult;
    }

}
