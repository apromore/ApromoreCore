package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.List;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.renderer.ProcessSummaryItemRenderer;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listheader;

public class ProcessListboxController extends BaseListboxController {

    private static final long serialVersionUID = -6874531673992239378L;

    private Listheader columnScore;

    public ProcessListboxController(MainController mainController) {
        super(mainController, "macros/listbox/processSummaryListbox.zul", new ProcessSummaryItemRenderer(mainController));

        this.columnScore = (Listheader) this.getListBox().getFellow("columnScore");

        // TODO should be replaced by ListModel listener in zk 6
        getListBox().addEventListener(Events.ON_SELECT, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (getListBox().getSelectedItems().size() == 1) {
                    Object obj = getListModel().getSelection().iterator().next();
                    if (obj instanceof ProcessSummaryType) {
                        getMainController().displayProcessVersions((ProcessSummaryType) obj);
                    } else if (obj instanceof FolderType) {
                        List<Integer> folders = UserSessionManager.getSelectedFolderIds();
                        folders.add(((FolderType) obj).getId());
                        UserSessionManager.setSelectedFolderIds(folders);
                    }
                } else if (getListBox().getSelectedItems().size() == 0) {
                    getMainController().clearProcessVersions();
                    UserSessionManager.setSelectedFolderIds(new ArrayList<Integer>());
                } else {
                    getMainController().clearProcessVersions();
                    List<Integer> folders = new ArrayList<>();
                    for (Object obj : getListModel().getSelection()) {
                       if (obj instanceof FolderType) {
                           folders.add(((FolderType) obj).getId());
                       }
                    }
                    UserSessionManager.setSelectedFolderIds(folders);
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.portal.dialogController.BaseListboxController#refreshContent ()
     */
    @Override
    protected void refreshContent() {
        getMainController().reloadProcessSummaries();
    }

    /**
     * Display process versions given in processSummaries. If isQueryResult this
     * results from a search whose query is versionQ, given processQ
     * @param subFolders list of folders to display.
     * @param processSummaries the list of processes to display.
     * @param isQueryResult is this a query result from a search or process.
     */
    @SuppressWarnings("unchecked")
    public void displayProcessSummaries(List<FolderType> subFolders, ProcessSummariesType processSummaries, Boolean isQueryResult) {
        this.columnScore.setVisible(isQueryResult);

        getListBox().clearSelection();
        getListBox().setModel(new ListModelList<>());
        getListModel().setMultiple(true);

        if (!isQueryResult) {
            getListModel().addAll(subFolders);
        }
        getListModel().addAll(processSummaries.getProcessSummary());
        if (isQueryResult && getListBox().getItemCount() > 0) {
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
    @SuppressWarnings("unchecked")
    public void displayNewProcess(ProcessSummaryType process) {
        getListModel().add(process);
    }

}
