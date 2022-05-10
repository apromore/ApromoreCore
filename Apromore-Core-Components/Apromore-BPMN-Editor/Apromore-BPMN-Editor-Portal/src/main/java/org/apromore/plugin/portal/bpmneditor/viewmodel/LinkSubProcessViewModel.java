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
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummariesType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.UserType;
import org.apromore.service.ProcessService;
import org.apromore.service.SecurityService;
import org.apromore.service.helper.UserInterfaceHelper;
import org.apromore.zk.notification.Notification;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LinkSubProcessViewModel {
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

    @Getter @Setter
    private String linkType = LINK_TYPE_NEW;
    @Getter @Setter
    private ProcessSummaryType selectedProcess;
    @Getter
    private boolean processListEnabled;

    @Init
    public void init(@ExecutionArgParam("mainController") final MainController mainC,
                     @ExecutionArgParam("elementId") final String elId,
                     @ExecutionArgParam("parentProcessId") final int parentId) {
        mainController = mainC;
        elementId = elId;
        parentProcessId = parentId;
        currentUser = UserSessionManager.getCurrentUser();
    }

    @Command
    public void linkSubProcess(@BindingParam(WINDOW_PARAM) final Component window) throws Exception {
        switch (linkType) {
            case LINK_TYPE_NEW:
                ProcessSummaryType newProcess = mainController.openNewProcess();
                processService.linkSubprocess(parentProcessId, elementId, newProcess.getId());
                BindUtils.postGlobalCommand(null, null, "onLinkedProcessUpdated", null);
                window.detach();
                break;
            case LINK_TYPE_EXISTING:
                if (selectedProcess == null ) {
                    Notification.error(Labels.getLabel("bpmnEditor_linkSubProcessSelectModel_message",
                        "Please select an existing process model to link"));
                } else {
                    Notification.info(MessageFormat.format(Labels.getLabel("bpmnEditor_linkSubProcessSuccess_message",
                        "Subprocess linked to {0}"), selectedProcess.getName()));
                    processService.linkSubprocess(parentProcessId, elementId, selectedProcess.getId());
                    BindUtils.postGlobalCommand(null, null, "onLinkedProcessUpdated", null);
                    window.detach();
                }
                break;
            default:
                Notification.error(Labels.getLabel("bpmnEditor_linkSubProcessSelectLinkType_message",
                    "Please select a link type"));
        }
    }

    public List<SummaryType> getProcessList() throws UserNotFoundException {
        User user = securityService.getUserById(currentUser.getId());
        SummariesType processSummaries = uiHelper.buildProcessSummaryList(currentUser.getId(), ROOT_FOLDER_ID, 0,
            PAGE_SIZE);
        List<SummaryType> processList = new ArrayList<>(processSummaries.getSummary());
        processList.removeIf(p -> {
            try {
                return !ItemHelpers.canModify(user, p);
            } catch (Exception e) {
                return true;
            }
        });
        return processList;
    }

    @Command
    @NotifyChange("processListEnabled")
    public void onCheckLinkType() {
        processListEnabled = LINK_TYPE_EXISTING.equals(linkType);
    }
}
