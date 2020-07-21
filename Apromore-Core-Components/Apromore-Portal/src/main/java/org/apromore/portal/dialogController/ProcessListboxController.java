/*-
 * #%L
 * This file is part of "Apromore Core".
 * 
 * Copyright (C) 2012 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

package org.apromore.portal.dialogController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apromore.plugin.portal.PortalProcessAttributePlugin;
import org.apromore.portal.common.Constants;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.renderer.SummaryItemRenderer;
import org.apromore.portal.model.*;
// import org.apromore.portal.util.SummaryComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listheader;

public class ProcessListboxController extends BaseListboxController {

    private static final long serialVersionUID = -6874531673992239378L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessListboxController.class);

    private Listheader columnScore;
    private Listheader columnName;

    public ProcessListboxController(MainController mainController) {
        super(mainController, "macros/listbox/processSummaryListbox.zul", new SummaryItemRenderer(mainController));

        this.columnScore = (Listheader) this.getListBox().getFellow("columnScore");
        this.columnName = (Listheader) this.getListBox().getFellow("columnName");
        // this.columnName.setSortAscending(new SummaryComparator(true, 1));
        // this.columnName.setSortDescending(new SummaryComparator(false, 1));

        // Add plugin attributes as additional columns
        for (PortalProcessAttributePlugin plugin: (List<PortalProcessAttributePlugin>) SpringUtil.getBean("portalProcessAttributePlugins")) {
            this.getListBox().getListhead().appendChild(plugin.getListheader());
        }

        // TODO should be replaced by ListModel listener in zk 6
        getListBox().addEventListener(Events.ON_SELECT, new EventListener<Event>() {
            @Override
            public void onEvent(Event event) throws Exception {

                // List the selected folders and processes
                List<Integer> folderIdList = new ArrayList<>();
                List<ProcessSummaryType> processSummaryList = new ArrayList<>();
                for (Object selectedItem: getListModel().getSelection()) {
                    if (selectedItem instanceof FolderType) {
                        folderIdList.add(((FolderType) selectedItem).getId());
                    } else if (selectedItem instanceof ProcessSummaryType) {
                        processSummaryList.add((ProcessSummaryType) selectedItem);
                    }
                }

                // If there's a unique selected process, show its versions
                if (processSummaryList.size() == 1) {
                    getMainController().displayProcessVersions(processSummaryList.get(0));
                } else {
                    getMainController().clearProcessVersions();
                }

                // Set the selected folders
                UserSessionManager.setSelectedFolderIds(folderIdList);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see org.apromore.portal.dialogController.BaseListboxController#refreshContent ()
     */
    @Override
    protected void refreshContent() {
        getMainController().reloadSummaries();
    }

    /**
     * Display process versions given in summaries. If isQueryResult this
     * results from a search whose query is versionQ, given processQ
     * @param subFolders list of folders to display.
     * @param summaries the list of processes to display.
     * @param isQueryResult is this a query result from a search or process.
     */
    @SuppressWarnings("unchecked")
    public void displaySummaries(List<FolderType> subFolders, SummariesType summaries, Boolean isQueryResult) {
        this.columnScore.setVisible(isQueryResult);

        getListBox().clearSelection();
        getListBox().setModel(new ListModelList<>());
        getListModel().setMultiple(true);

        //getListModel().addAll(subFolders);
        getListModel().addAll(summaries.getSummary());
        if (isQueryResult && getListBox().getItemCount() > 0) {
            getListBox().getItemAtIndex(0).setStyle(Constants.SELECTED_PROCESS);
        }
    }

    public SummaryListModel displaySummaries(List<FolderType> subFolders, boolean isQueryResult) {
        this.columnScore.setVisible(isQueryResult);

        getListBox().clearSelection();
        SummaryListModel model = new SummaryListModel(isQueryResult ? Collections.<FolderType>emptyList() : subFolders);

        getListBox().setModel(model);

        if (isQueryResult && getListBox().getItemCount() > 0) {
            getListBox().getItemAtIndex(0).setStyle(Constants.SELECTED_PROCESS);
        }

        return model;
    }

//    public SummaryListModel displayProcessSummaries(List<FolderType> subFolders, boolean isQueryResult) {
//        this.columnScore.setVisible(isQueryResult);
//
//        getListBox().clearSelection();
//        SummaryListModel model = new SummaryListModel(isQueryResult ? Collections.<FolderType>emptyList() : subFolders);
//
//        getListBox().setModel(model);
//
//            if (isQueryResult && getListBox().getItemCount() > 0) {
//                getListBox().getItemAtIndex(0).setStyle(Constants.SELECTED_PROCESS);
//            }
//
//        return model;
//    }

    /**
     * Lazily loading list of @link{ProcessSummaryType}.
     *
     * @see http://books.zkoss.org/wiki/ZK_Developer%27s_Reference/MVC/Model/List_Model#Huge_Amount_of_Data
     */
//    class SummaryListModel extends ListModelList {
//        final int pageSize = 10;  // TODO: ought to be externally configurable
//
//        private SummariesType summaries;
//        private int currentPageIndex = 0;
//            private List<FolderType> subFolders;
//
//            /**
//             * Constructor.
//             *
//             * @param subFolders  will be displayed before processes
//             */
//            SummaryListModel(List<FolderType> subFolders) {
//                this.subFolders = subFolders;
//                setMultiple(true);
//            }
//
//        public Object getElementAt(int index) {
//                if (index < subFolders.size()) {
//                    return subFolders.get(index);
//                } else {
//                    int processIndex = index - subFolders.size();
//                return getSummaries(processIndex / pageSize).getSummary().get(processIndex % pageSize);
//                }
//        }
//
//        public int getSize() {
//            return subFolders.size() + getSummaries(currentPageIndex).getCount().intValue();
//        }
//
//        public int getTotalCount() {
//            return getSummaries(currentPageIndex).getTotalCount().intValue();
//        }
//
//        private SummariesType getSummaries(int pageIndex) {
//            if (summaries == null || currentPageIndex != pageIndex) {
//            UserType user = UserSessionManager.getCurrentUser();
//            FolderType currentFolder = UserSessionManager.getCurrentFolder();
//            summaries = getService().getProcessOrLogSummaries(user.getId(), currentFolder == null ? 0 : currentFolder.getId(), pageIndex, pageSize);
//            currentPageIndex = pageIndex;
//            }
//            return summaries;
//        }
//    }

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
        SummaryListModel model = displaySummaries(subFolders, false);
    }

}
