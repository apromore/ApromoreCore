/*-
 * #%L
 * This file is part of "Apromore Core".
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apromore.dao.model.Folder;
import org.apromore.dao.model.User;
import org.apromore.exception.CircularReferenceException;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.common.FolderTreeModel;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.FolderType;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.bind.annotation.SelectorParam;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tree;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LinkSubProcessViewModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkSubProcessViewModel.class);
    private static final String WINDOW_PARAM = "window";
    private static final String LINK_TYPE_NEW = "NEW";
    private static final String LINK_TYPE_EXISTING = "EXISTING";
    private static final int ROOT_FOLDER_ID = 0;
    public static final int PAGE_SIZE = 10000;

    private MainController mainController;
    private String elementId;
    private int parentProcessId;
    private UserType currentUser;

    @WireVariable
    private ProcessService processService;

    @WireVariable
    private SecurityService securityService;

    @WireVariable
    private UserInterfaceHelper uiHelper;

    @Getter
    @Setter
    private String linkType = LINK_TYPE_NEW;
    @Getter
    @Setter
    private ProcessSummaryType selectedProcess;
    @Getter
    private boolean processListEnabled;
    private List<SummaryType> processList;

    @Init
    public void init(@ExecutionArgParam("mainController") final MainController mainC,
                     @ExecutionArgParam("elementId") final String elId,
                     @ExecutionArgParam("parentProcessId") final int parentId) {
        mainController = mainC;
        elementId = elId;
        parentProcessId = parentId;
        currentUser = UserSessionManager.getCurrentUser();


        try {
            ProcessSummaryType linkedProcess = processService.getLinkedProcess(parentId, elId);

            if (linkedProcess != null) {
                selectedProcess = (ProcessSummaryType) getProcessList().stream()
                    .filter(p -> p.getId().equals(linkedProcess.getId()))
                    .findFirst().orElse(null);
            }

            if (selectedProcess != null) {
                linkType = LINK_TYPE_EXISTING;
                processListEnabled = true;
            }
        } catch (UserNotFoundException e) {
            Messagebox.show("Could not find the current logged in user", "Error", Messagebox.OK, Messagebox.ERROR);
        }
    }

    @AfterCompose
    public void doAfterCompose(@SelectorParam("#tree") final Tree tree) {
        try {
            EventQueues.lookup("linkSubProcessControl", EventQueues.DESKTOP, true).subscribe(evt -> {
                    selectedProcess = null;
                    if ("onSelect".equals(evt.getName())) {
                        Object selItem = evt.getData();
                        if (selItem instanceof ProcessSummaryType) {
                            selectedProcess = (ProcessSummaryType) selItem;
                        }
                    }
            });

            List<Integer> processFolderChain = getProcessFolderChain(
                selectedProcess == null ? 0 : processService.getProcessParentFolder(selectedProcess.getId()));
            tree.setItemRenderer(
                new SubProcessTreeRenderer(mainController.getPortalSession().getCurrentFolder(), selectedProcess,
                    processFolderChain));
            ProcessFolderTree
                processFolderTree = new ProcessFolderTree(true, 0, mainController);
            new ProcessTreeSearchController(tree, mainController);
            tree.setModel(new FolderTreeModel(processFolderTree.getRoot(), processFolderTree.getCurrentFolder()));
        } catch (Exception ex) {
            LOGGER.error("Error in loading tree structure", ex);
        }
    }

    @Command
    public void linkSubProcess(@BindingParam(WINDOW_PARAM) final Component window) throws Exception {
        try {
            switch (linkType) {
                case LINK_TYPE_NEW:
                    ProcessSummaryType newProcess = mainController.openNewProcess();
                    processService.linkSubprocess(parentProcessId, elementId, newProcess.getId(), currentUser.getUsername());
                    BindUtils.postGlobalCommand(null, null, "onLinkedProcessUpdated", null);
                    window.detach();
                    Clients.evalJavaScript("setLinkedSubProcess('" + elementId + "','Untitled (v1.0)');");
                    break;
                case LINK_TYPE_EXISTING:
                    if (selectedProcess == null) {
                        Notification.error(Labels.getLabel("bpmnEditor_linkSubProcessSelectModel_message",
                            "Please select an existing process model to link"));
                    } else {
                        processService.linkSubprocess(parentProcessId, elementId, selectedProcess.getId(), currentUser.getUsername());
                        Notification.info(MessageFormat.format(Labels.getLabel("bpmnEditor_linkSubProcessSuccess_message",
                            "Subprocess linked to {0}"), selectedProcess.getName()));
                        BindUtils.postGlobalCommand(null, null, "onLinkedProcessUpdated", null);
                        window.detach();

                        String linkedProcessName =
                            selectedProcess.getName() + " (v" + selectedProcess.getLastVersion() + ")";
                        Clients.evalJavaScript("setLinkedSubProcess('" + elementId + "','" + linkedProcessName + "');");
                    }
                    break;
                default:
                    Notification.error(Labels.getLabel("bpmnEditor_linkSubProcessSelectLinkType_message",
                        "Please select a link type"));
            }
        } catch (CircularReferenceException e) {
            Messagebox.show(Labels.getLabel("bpmnEditor_linkSubprocessCircularReference_message",
                    "You cannot perform this operation. This linkage will create a loop between the linked models."),
                Labels.getLabel("common_unknown_title", "Error"), Messagebox.OK, Messagebox.ERROR);
        }

    }

    public List<SummaryType> getProcessList() throws UserNotFoundException {
        if (processList == null) {
            processList = getProcesses(null);
            User user = securityService.getUserById(currentUser.getId());
            processList.removeIf(p -> {
                try {
                    return !ItemHelpers.canModify(user, p);
                } catch (Exception e) {
                    return true;
                }
            });
        }
        return processList;
    }

    /**
     * Get all the processes in a folder and its subfolders.
     *
     * @param folder the folder which contains the processes or null to include all processes.
     * @return a list of processes in this folder and its subfolders.
     */
    private List<SummaryType> getProcesses(final FolderType folder) {
        int folderId = (folder == null) ? ROOT_FOLDER_ID : folder.getId();
        List<FolderType> subFolders = (folder == null)
            ? mainController.getPortalSession().getTree() : folder.getFolders();

        SummariesType processSummaries = uiHelper.buildProcessSummaryList(currentUser.getId(), folderId, 0, PAGE_SIZE);
        List<SummaryType> processes = new ArrayList<>(processSummaries.getSummary());

        for (FolderType f : subFolders) {
            processes.addAll(getProcesses(f));
        }
        return processes;
    }

    @Command
    @NotifyChange("processListEnabled")
    public void onCheckLinkType() {
        processListEnabled = LINK_TYPE_EXISTING.equals(linkType);
    }


    private List<Integer> getProcessFolderChain(
        Integer parentFolder) {
        List<Integer> upperChainFolder = new ArrayList<>();
        upperChainFolder.add(0);
        if (parentFolder == null || parentFolder == 0) {
            return upperChainFolder;
        }

        while (parentFolder != null && parentFolder != 0) {
            Folder folder = mainController.getWorkspaceService().getFolder(parentFolder);
            if (folder != null) {
                upperChainFolder.add(folder.getId());
                if (folder.getParentFolder() == null) {
                    parentFolder = null;
                } else {
                    parentFolder = folder.getParentFolder().getId();
                }
            } else {
                parentFolder = null;
            }
        }
        return upperChainFolder;
    }


}
