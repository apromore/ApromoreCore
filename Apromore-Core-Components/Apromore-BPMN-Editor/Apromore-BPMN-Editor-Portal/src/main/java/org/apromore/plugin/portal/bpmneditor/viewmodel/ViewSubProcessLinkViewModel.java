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

import static org.apromore.plugin.portal.PortalContexts.getPageDefinition;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.ProcessService;
import org.apromore.zk.notification.Notification;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Window;

@Slf4j
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ViewSubProcessLinkViewModel {
    private static final String WINDOW_PARAM = "window";

    private MainController mainController;
    private String elementId;
    private int parentProcessId;
    @Getter
    private boolean viewMode;

    @WireVariable
    private ProcessService processService;

    @Init
    public void init(@ExecutionArgParam("mainController") final MainController mainC,
                     @ExecutionArgParam("elementId") final String elId,
                     @ExecutionArgParam("parentProcessId") final int parentId,
                     @ExecutionArgParam("viewOnly") final boolean viewOnly) {
        mainController = mainC;
        elementId = elId;
        parentProcessId = parentId;
        viewMode = viewOnly;
    }

    @Command
    public void viewLinkedProcess() {
        //Get full ProcessSummaryType and VersionSummaryType
        ProcessSummaryType process = getLinkedProcess();
        VersionSummaryType version = process.getVersionSummaries().stream()
            .filter(v -> v.getVersionNumber().equals(process.getLastVersion()))
            .findFirst().orElse(null);
        try {
            mainController.editProcess2(process, version, process.getOriginalNativeType(), new HashSet<>(), false);
        } catch (InterruptedException e) {
            Notification.error("Unable to view linked process");
            log.error("Unable to view linked process", e);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }

    }

    @Command
    public void editLinkedProcess() {
        String linkProcessWindowPath = "static/bpmneditor/linkSubProcess.zul";
        Map<String, Object> args = new HashMap<>();
        args.put("mainController", mainController);
        args.put("parentProcessId", parentProcessId);
        args.put("elementId", elementId);

        try {
            Window linkSubProcessModal = (Window) Executions
                .createComponents(getPageDefinition(linkProcessWindowPath), null, args);
            linkSubProcessModal.doModal();
        } catch (IOException e) {
            Notification.error("Error creating edit window");
        }

    }

    @Command
    public void unlinkSubProcess(@BindingParam(WINDOW_PARAM) final Component window) {
        processService.unlinkSubprocess(parentProcessId, elementId);
        Notification.info("Process successfully unlinked");
        window.detach();
    }

    @GlobalCommand
    @NotifyChange("linkedProcessMessage")
    public void onLinkedProcessUpdated() {
        // =======================================================================
        // forward triggering notification for updating the linked process message
        // =======================================================================
    }

    public ProcessSummaryType getLinkedProcess() {
        return processService.getLinkedProcess(parentProcessId, elementId);
    }

    public String getLinkedProcessMessage() {
        String linkedProcessMessageFormat = "This activity is already linked to an existing subprocess model {0}";
        return MessageFormat.format(linkedProcessMessageFormat, getLinkedProcess().getName());
    }
}
