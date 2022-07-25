/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * %%
 * Copyright (C) 2018 - 2022 Apromore Pty Ltd.
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

package org.apromore.plugin.portal.bpmneditor.viewmodel;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.HashedMap;
import org.apromore.dao.model.Folder;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.FolderTreeNode;
import org.apromore.portal.common.FolderTreeNodeTypes;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.FolderSummaryType;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.service.search.SearchExpressionBuilder;
import org.slf4j.Logger;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;

public class ProcessTreeSearchController {
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(ProcessTreeSearchController.class);

    private final MainController mainController;
    private final Tree tree;
    private Checkbox searchToggleCheckbox;
    private Textbox searchTextbox;
    private Button searchButton;
    private Button searchButtonClear;

    public ProcessTreeSearchController(Tree tree, MainController mainController) {
        this.tree = tree;
        this.searchToggleCheckbox = (Checkbox) tree.query(".ap-listbox-search-toggle");
        this.mainController = mainController;
        this.searchTextbox = (Textbox) tree.query(".ap-listbox-search-input");
        this.searchButton = (Button) tree.query(".ap-listbox-search-btn");
        this.searchButtonClear = (Button) tree.query(".ap-listbox-search-clear");
        searchToggleCheckbox.addEventListener(Events.ON_CHECK, new EventListener<CheckEvent>() {
            @Override
            public void onEvent(CheckEvent event) throws Exception {
                showSearchBox(event.isChecked());
            }
        });
        searchTextbox.addEventListener(Events.ON_OK, new EventListener<Event>() {
            @Override
            public void onEvent(Event e) throws Exception {
                doSearch(searchTextbox.getValue());
            }
        });

        searchButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event e) throws Exception {
                doSearch(searchTextbox.getValue());
            }
        });

        searchButtonClear.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            @Override
            public void onEvent(Event e) throws Exception {
                searchTextbox.setValue("");
                reset();
            }
        });
    }

    public void reset() {
        showSearchBox(false);
        searchToggleCheckbox.setChecked(false);
        searchTextbox.setValue("");
        tree.clearSelection();
        doSearch("");
    }

    public void showSearchBox(boolean visible) {
        Auxhead auxhead = (Auxhead) tree.query(".ap-auxhead");
        auxhead.setVisible(visible);
    }

    private void redrawTree(Map<FolderTreeNodeTypes, List<Integer>> result, int currentFolder) {
        ProcessFolderTree processFolderTree = new ProcessFolderTree(true, currentFolder, mainController);
        tree.setItemRenderer(new SubProcessTreeRenderer(result));
        tree.setModel(new FolderTreeModel(processFolderTree.getRoot(), processFolderTree.getCurrentFolder()));
    }

    private void doSearch(String query) {
        int folderId = 0;
        FolderType selectedFolder = null;
        if (tree.getSelectedItem() != null) {
            FolderTreeNode ctn = tree.getSelectedItem().getValue();
            if (ctn != null && ctn.getType().equals(FolderTreeNodeTypes.Folder)) {
                selectedFolder = ((FolderType) ctn.getData());
                folderId = selectedFolder.getId();
            }
        }

        if ((query == null || query.length() == 0) && folderId == 0) {
            redrawTree(null, folderId);
            return;
        }

        try {
            Map<FolderTreeNodeTypes, List<Integer>> results = initializeMap();
            setResultSummary(results, readProcessSummaries(folderId, query));
            addFolderChain(results, selectedFolder);
            redrawTree(results, folderId);
        } catch (Exception e) {
            LOGGER.error("Failed search", e);
        }

    }

    private void setResultSummary(Map<FolderTreeNodeTypes, List<Integer>> results, SummariesType summaries) {
        if (summaries == null || summaries.getSummary() == null) {
            return;
        }
        for (SummaryType type : summaries.getSummary()) {
            if (type instanceof ProcessSummaryType) {
                results.get(FolderTreeNodeTypes.Process).add(type.getId());
            } else if (type instanceof FolderSummaryType) {
                results.get(FolderTreeNodeTypes.Folder).add(type.getId());
            }
        }
    }

    private void addFolderChain(
        Map<FolderTreeNodeTypes, List<Integer>> results,
        FolderType selectedFolder) {
        if (results == null) {
            return;
        }
        results.get(FolderTreeNodeTypes.Folder).add(0);
        if (selectedFolder == null) {
            return;
        }
        if (selectedFolder.getId() != 0) {
            results.get(FolderTreeNodeTypes.Folder).add(selectedFolder.getId());
            Integer currentParentFolder = selectedFolder.getParentId();
            while (currentParentFolder != null && currentParentFolder != 0) {
                Folder folder = mainController.getWorkspaceService().getFolder(currentParentFolder);
                if (folder != null) {
                    results.get(FolderTreeNodeTypes.Folder).add(selectedFolder.getParentId());
                    if (folder.getParentFolder() == null) {
                        currentParentFolder = null;
                    } else {
                        currentParentFolder = folder.getParentFolder().getId();
                    }
                } else {
                    currentParentFolder = null;
                }
            }
        }
    }

    private Map<FolderTreeNodeTypes, List<Integer>> initializeMap() {
        Map<FolderTreeNodeTypes, List<Integer>> map = new HashedMap();
        map.put(FolderTreeNodeTypes.Folder, new ArrayList<>());
        map.put(FolderTreeNodeTypes.Process, new ArrayList<>());
        return map;
    }

    private SummariesType readProcessSummaries(Integer folderId, String searchCriteria) {
        UserInterfaceHelper uiHelper = (UserInterfaceHelper) SpringUtil.getBean("uiHelper");
        if (uiHelper == null) {
            return null;
        }
        SummariesType processSummaries = null;
        try {
            processSummaries = uiHelper.buildProcessSummaryList(folderId, null,
                SearchExpressionBuilder.buildSimpleSearchConditions(searchCriteria, "p", "processId",
                    "process"), // processes
                SearchExpressionBuilder.buildSimpleSearchConditions(searchCriteria, "l", "logId", "log"), // logs
                SearchExpressionBuilder.buildSimpleSearchConditions(searchCriteria, "f", "folderId",
                    "folder"),
                false
            ); // folders

        } catch (UnsupportedEncodingException usee) {
            LOGGER.error("Failed to get Process Summaries: " + usee.toString(), usee);
        }

        return processSummaries;
    }


}
