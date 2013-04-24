package org.apromore.portal.dialogController;

import java.util.List;
import java.util.Map;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.model.VersionSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.dialogController.renderer.ProcessSummaryItemRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;

public class ProcessListboxController extends BaseListboxController {

    private static final long serialVersionUID = -6874531673992239378L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessListboxController.class.getName());

    private Listheader columnScore; // column to display process score for the purpose of answering query

    private Boolean isQueryResult; // says whether the data to be displayed have been produced by a query

    public ProcessListboxController(MainController mainController) {
        super(mainController, "macros/listbox/processSummaryListbox.zul", new ProcessSummaryItemRenderer(mainController));

        this.columnScore = (Listheader) this.getListBox().getFellow("columnScore");

        // TODO should be replaced by ListModel listener in zk 6
        getListBox().addEventListener(Events.ON_SELECT, new EventListener() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (getListBox().getSelectedItems().size() == 1) {
                    Object obj = getListModel().getSelection().iterator().next();
                    if (obj instanceof ProcessSummaryType) {
                        getMainController().displayProcessVersions((ProcessSummaryType) obj);
                    }
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
     * @param subFolders list of folders to display as well in the list.
     * @param processSummaries
     * @param isQueryResult
     */
    public void displayProcessSummaries(List<FolderType> subFolders, ProcessSummariesType processSummaries, Boolean isQueryResult) {
        this.isQueryResult = isQueryResult;
        this.columnScore.setVisible(isQueryResult);

        getListBox().clearSelection();
        getListModel().clear();
        getListModel().addAll(subFolders);
        getListModel().addAll(processSummaries.getProcessSummary());

        if (isQueryResult) {
            // Highlight the first row as this will be the original search model.
            getListBox().getItemAtIndex(0).setStyle(Constants.SELECTED_PROCESS);
        }
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
