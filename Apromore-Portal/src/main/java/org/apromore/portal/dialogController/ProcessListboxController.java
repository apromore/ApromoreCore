/*
 * Copyright © 2009-2016 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apromore.model.FolderType;
import org.apromore.model.ProcessSummariesType;
import org.apromore.model.ProcessSummaryType;
import org.apromore.portal.ConfigBean;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.renderer.ProcessSummaryItemRenderer;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listheader;

import org.apromore.model.FolderType;
import org.apromore.model.UserType;
import org.zkoss.zul.AbstractListModel;

public class ProcessListboxController extends BaseListboxController {

    private static final long serialVersionUID = -6874531673992239378L;

    private Listheader columnScore;

    public ProcessListboxController(MainController mainController) {
        super(mainController, "macros/listbox/processSummaryListbox.zul", new ProcessSummaryItemRenderer(mainController));

        this.columnScore = (Listheader) this.getListBox().getFellow("columnScore");

        // Hide the "Queryable?" column if PQL indexing is disabled
        Listheader queryable = (Listheader) this.getListBox().getFellow("queryable");
        queryable.setVisible(config.isPQLIndexingEnabled());

        // TODO should be replaced by ListModel listener in zk 6
        getListBox().addEventListener(Events.ON_SELECT, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (getListBox().getSelectedItems().size() == 1) {
                    Object obj = getListModel().getSelection().iterator().next();
                    if (obj instanceof ProcessSummaryType) {
                        UserSessionManager.setSelectedFolderIds(new ArrayList<Integer>());
                        getMainController().displayProcessVersions((ProcessSummaryType) obj);
                    } if (obj instanceof FolderType) {
                        List<Integer> folders = new ArrayList<>();
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
        getListBox().addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {
                if (getListBox().getSelectedItems().size() == 1) {
                    Object obj = getListModel().getSelection().iterator().next();
                    if (obj instanceof FolderType) {
                        List<Integer> folders = UserSessionManager.getSelectedFolderIds();
                        folders.add(((FolderType) obj).getId());
                        UserSessionManager.setSelectedFolderIds(folders);
                    }
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

    public ProcessSummaryListModel displayProcessSummaries(List<FolderType> subFolders, boolean isQueryResult) {
	this.columnScore.setVisible(isQueryResult);

	getListBox().clearSelection();
	ProcessSummaryListModel model = new ProcessSummaryListModel(isQueryResult ? Collections.<FolderType>emptyList() : subFolders);
	getListBox().setModel(model);

        if (isQueryResult && getListBox().getItemCount() > 0) {
            getListBox().getItemAtIndex(0).setStyle(Constants.SELECTED_PROCESS);
        }

	return model;
    }

    /**
     * Lazily loading list of @link{ProcessSummaryType}.
     *
     * @see http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/MVC/Model/List_Model#Huge_Amount_of_Data
     */
    class ProcessSummaryListModel extends ListModelList {
	final int pageSize = 10;  // TODO: ought to be externally configurable

	private ProcessSummariesType processSummaries;
	private int currentPageIndex = 0;
        private List<FolderType> subFolders;

        /**
         * Constructor.
         *
         * @param subFolders  will be displayed before processes
         */
        ProcessSummaryListModel(List<FolderType> subFolders) {
            this.subFolders = subFolders;
            setMultiple(true);
        }

	public Object getElementAt(int index) {
            if (index < subFolders.size()) {
                return subFolders.get(index);
            } else {
                int processIndex = index - subFolders.size();
	        return getProcessSummaries(processIndex / pageSize).getProcessSummary().get(processIndex % pageSize);
            }
	}

	public int getSize() {
	    return subFolders.size() + getProcessSummaries(currentPageIndex).getProcessCount().intValue();
	}

	public int getTotalProcessCount() {
	    return getProcessSummaries(currentPageIndex).getTotalProcessCount().intValue();
	}

	private ProcessSummariesType getProcessSummaries(int pageIndex) {
	    if (processSummaries == null || currentPageIndex != pageIndex) {
		UserType user = UserSessionManager.getCurrentUser();
		FolderType currentFolder = UserSessionManager.getCurrentFolder();
		processSummaries = getService().getProcesses(user.getId(), currentFolder == null ? 0 : currentFolder.getId(), pageIndex, pageSize);
		currentPageIndex = pageIndex;
	    }
	    return processSummaries;
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
       // getListModel().add(process);  // This will trigger a UiException from ZK do to the additional complexity of paged result fetching

        FolderType currentFolder = UserSessionManager.getCurrentFolder();
        List<FolderType> subFolders = getService().getSubFolders(UserSessionManager.getCurrentUser().getId(), currentFolder == null ? 0 : currentFolder.getId());
        ProcessListboxController.ProcessSummaryListModel model = displayProcessSummaries(subFolders, false);
    }

}
