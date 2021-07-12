/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2021 Apromore Pty Ltd.
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

package org.apromore.portal.dialogController.workspaceOptions;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Messagebox;

import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.notification.Notification;
import org.apromore.portal.dialogController.BaseController;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.LogSummaryType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.portal.dialogController.MainController;
import org.apromore.dao.model.User;

public class CopyAndPasteController extends BaseController {

    private final int MAX_RECURSIVE = 4;
    private final String DOMAIN = "";
    private final String NATIVE_TYPE = "BPMN 2.0";
    private final boolean IS_PUBLIC = false;
    private final int PAGE_INDEX = 0;
    private final int PAGE_SIZE = 1000;

    private boolean isCut = false;
    private MainController mainController;
    private UserType user;
    private User currentUser;
    private String userName;
    private String userId;
    private Integer selectedTargetFolderId = null;
    private ArrayList<Object> selectedItems = new ArrayList<>();
    private static final Logger LOGGER = PortalLoggerFactory.getLogger(CopyAndPasteController.class);

    public CopyAndPasteController(MainController mainController, UserType user) {
        super();
        this.mainController = mainController;
        this.user = user;
        this.userName = user.getUsername();
        this.userId = user.getId();
        try {
            this.currentUser = getSecurityService().getUserById(this.userId);
        } catch (Exception e) {
            this.currentUser = null;
        }
    }

    public boolean isInside(Integer folderId, Integer targetFolderId, int level) {
        if (targetFolderId.equals(folderId)) {
            return true;
        }

        if (level > MAX_RECURSIVE) {
            return true;
        }
        List<FolderType> subFolders = getSubFolders(folderId);
        for(FolderType subFolder: subFolders) {
            Integer subFolderId = subFolder.getId();
            if (targetFolderId.equals(subFolderId)) {
                return true;
            }
            boolean result = isInside(subFolderId, targetFolderId, level + 1);
            if (result) {
                return true;
            }
        }
        return false;
    }

    public List<FolderType> getSubFolders(Integer folderId) {
        return getService().getSubFolders(userId, folderId == null ? 0 : folderId);
    }

    public Set<Integer> getSubFolderIds(Integer folderId) {
        List<FolderType> subFolders = getSubFolders(folderId);
        Set<Integer> idSet = new HashSet<Integer>();
        for(FolderType subFolder: subFolders) {
            idSet.add(subFolder.getId());
        }
        return idSet;
    }

    public Integer createFolder(Integer targetFolderId, String folderName) throws Exception {
        Set<Integer> targetSubFolderIds = getSubFolderIds(targetFolderId);
        getService().createFolder(userId, folderName, targetFolderId);
        Set<Integer> newTargetSubFolderIds = getSubFolderIds(targetFolderId);
        newTargetSubFolderIds.removeAll(targetSubFolderIds);
        Integer newFolderId;
        try {
            newFolderId = (Integer)newTargetSubFolderIds.iterator().next();
        } catch (Exception e) {
            newFolderId = -1;
        }
        return newFolderId;
    }

    private void cloneFolder(FolderType folder, Integer targetFolderId, int level) throws Exception {
        Integer folderId = folder.getId();
        String folderName = folder.getFolderName();
        List<FolderType> sourceSubFolders = getSubFolders(folderId);
        Integer newTargetFolderId = createFolder(targetFolderId, folderName);
        if (newTargetFolderId < 0) {
            LOGGER.error("Fail to clone folder");
            return;
        }

        SummariesType logSummaries = getService().getLogSummaries(userId, folderId, PAGE_INDEX, PAGE_SIZE);
        for(SummaryType summaryType : logSummaries.getSummary()) {
            cloneLog((LogSummaryType) summaryType, newTargetFolderId);
        }
        SummariesType processSummaries = getService().getProcessSummaries(userId, folderId, PAGE_INDEX, PAGE_SIZE);
        for(SummaryType summaryType : processSummaries.getSummary()) {
            cloneProcess((ProcessSummaryType) summaryType, newTargetFolderId);
        }
        if (level < MAX_RECURSIVE) {
            for(FolderType subFolder: sourceSubFolders) {
                cloneFolder(subFolder, newTargetFolderId, level + 1);
            }
        }
    }

    private void cloneLog(LogSummaryType log, Integer targetFolderId) throws Exception {
        if (log == null || targetFolderId == null) {
            LOGGER.error("No log or target folder is defined");
            return;
        }
        getWorkspaceService().copyLog(log.getId(), targetFolderId, userName, false);
    }

    private void cloneProcess(ProcessSummaryType model, Integer targetFolderId) throws Exception {
        if (model == null || targetFolderId == null) {
            LOGGER.error("No process or target folder is defined");
            return;
        }
        getWorkspaceService().copyProcess(model.getId(), targetFolderId, userName, false);
    }

    private void moveFolder(FolderType folder, Integer targetFolderId, int level) throws Exception {
        getWorkspaceService().moveFolder(folder.getId(), targetFolderId);
    }

    private void moveLog(LogSummaryType log, Integer targetFolderId) throws Exception {
        if (log == null || targetFolderId == null) {
            LOGGER.error("No log or target folder is defined");
            return;
        }
        getWorkspaceService().moveLog(log.getId(), targetFolderId);
    }

    private void moveProcess(ProcessSummaryType model, Integer targetFolderId) throws Exception {
        if (model == null || targetFolderId == null) {
            LOGGER.error("No process or target folder is defined");
            return;
        }
        getWorkspaceService().moveProcess(model.getId(), targetFolderId);
    }

    public void clearSelectedItems() {
        selectedItems.clear();
    }

    public ArrayList<Object> getSelectedItems() {
        return selectedItems;
    }

    public int getSelectedItemsSize() {
        return selectedItems.size();
    }

    private void updateSelectedItems(Set<Object> selections) {
        this.clearSelectedItems();
        for (Object obj : selections) {
            if (obj instanceof FolderType) {
                selectedItems.add((FolderType) obj);
            } else if (obj instanceof LogSummaryType) {
                selectedItems.add((LogSummaryType) obj);
            } else if (obj instanceof ProcessSummaryType) {
                selectedItems.add((ProcessSummaryType) obj);
            }
        }
    }

    public void cloneSelectedItems() throws Exception {
        for (Object obj : selectedItems) {
            if (obj instanceof FolderType) {
                FolderType folder = (FolderType) obj;
                if (isInside(folder.getId(), selectedTargetFolderId, 0)) {
                    Notification.error(Labels.getLabel("portal_noCopyFolderToSub_message"));
                } else {
                    cloneFolder(folder, selectedTargetFolderId, 0);
                }
            } else if (obj instanceof LogSummaryType) {
                cloneLog((LogSummaryType) obj, selectedTargetFolderId);
            } else if (obj instanceof ProcessSummaryType) {
                cloneProcess((ProcessSummaryType) obj, selectedTargetFolderId);
            }
        }
    }

    public void moveSelectedItems() throws Exception {
        for (Object obj : selectedItems) {
            if (obj instanceof FolderType) {
                FolderType folder = (FolderType) obj;
                if (isInside(folder.getId(), selectedTargetFolderId, 0)) {
                    Notification.error(Labels.getLabel("portal_noMoveFolderToSub_message"));
                } else {
                    moveFolder(folder, selectedTargetFolderId, 0);
                }
            } else if (obj instanceof LogSummaryType) {
                moveLog((LogSummaryType) obj, selectedTargetFolderId);
            } else if (obj instanceof ProcessSummaryType) {
                moveProcess((ProcessSummaryType) obj, selectedTargetFolderId);
            }
        }
    }

    public boolean checkImmediateOwnership(Set<Object> selections) throws Exception {
        for (Object obj : selections) {
            if (!ItemHelpers.isOwner(this.currentUser, obj)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkContext(Set<Object> selections, int selectionCount, FolderType currentFolder) {
        if (currentFolder == null) {
            Notification.error(Labels.getLabel("portal_failedFind_message"));
            return false;
        } else if (selectionCount == 0) {
            clearSelectedItems();
            Notification.error(Labels.getLabel("portal_selectOneItem_message"));
            return false;
        }
        return true;
    }

    public void cut(Set<Object> selections, int selectionCount, FolderType currentFolder) {

        if (checkContext(selections, selectionCount, currentFolder)) {
            try {
                if (!checkImmediateOwnership(selections)) {
                    Notification.error(Labels.getLabel("portal_onlyOwnerCanCutItems}"));
                } else if (ItemHelpers.isOwner(this.currentUser, currentFolder)) {
                    isCut = true;
                    updateSelectedItems(selections);
                    Notification.info(
                        MessageFormat.format(Labels.getLabel("portal_itemsSelectedToMove_message"), getSelectedItemsSize())
                    );
                } else {
                    Notification.error(Labels.getLabel("portal_onlyOwnerCanCutFromCurrent_message"));
                }
            } catch (Exception e) {
                Messagebox.show(Labels.getLabel("portal_failedCut_message"), "Apromore", Messagebox.OK,
                    Messagebox.ERROR);
                LOGGER.error(e.getMessage());
            }
        }
    }

    public void copy(Set<Object> selections, int selectionCount, FolderType currentFolder) {

        if (checkContext(selections, selectionCount, currentFolder)) {
            try {
                isCut = false;
                updateSelectedItems(selections);
                Notification.info(
                    MessageFormat.format(Labels.getLabel("portal_itemsSelectedToCopy_message"), getSelectedItemsSize())
                );
            } catch (Exception e) {
                Messagebox.show(Labels.getLabel("portal_failedCopy_message"), "Apromore", Messagebox.OK,
                    Messagebox.ERROR);
                LOGGER.error(e.getMessage());
            }
        }
    }

    public void paste(FolderType currentFolder) throws Exception {

        if (currentFolder == null) {
            Notification.error(Labels.getLabel("portal_failedFind_message"));
            return;
        }
        if (selectedItems.isEmpty()) {
            Notification.error(Labels.getLabel("portal_selectOneItemAndCutCopy_message"));
            return;
        }
        if (!ItemHelpers.isOwner(this.currentUser, currentFolder)) {
            Notification.error(Labels.getLabel("portal_onlyOwnerCanPasteToCurrent_message"));
            return;
        }
        try {
            selectedTargetFolderId = currentFolder.getId();
            if (isCut) {
                moveSelectedItems();
            } else {
                cloneSelectedItems();
            }
            Notification.info(
                MessageFormat.format(Labels.getLabel("portal_itemsPasted_message"), getSelectedItemsSize())
            );
            clearSelectedItems();
        } catch (Exception e) {
            Messagebox.show(Labels.getLabel("portal_failedPaste_message"), "Apromore", Messagebox.OK,
                Messagebox.ERROR);
            LOGGER.error(e.getMessage());
        }
    }
}
