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

import lombok.Getter;
import lombok.Setter;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.service.ProcessService;
import org.apromore.zk.notification.Notification;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LinkSubProcessViewModel {
    private static final String WINDOW_PARAM = "window";
    private static final String LINK_TYPE_NEW = "NEW";
    private static final String LINK_TYPE_EXISTING = "EXISTING";

    private MainController mainController;
    private String elementId;
    private int parentProcessId;

    @WireVariable
    private ProcessService processService;

    @Getter @Setter
    private String linkType = LINK_TYPE_NEW;

    @Init
    public void init(@ExecutionArgParam("mainController") final MainController mainC,
                     @ExecutionArgParam("elementId") final String elId,
                     @ExecutionArgParam("parentProcessId") final int parentId) {
        mainController = mainC;
        elementId = elId;
        parentProcessId = parentId;
    }

    @Command
    public void linkSubProcess(@BindingParam(WINDOW_PARAM) final Component window) throws Exception {
        switch (linkType) {
            case LINK_TYPE_NEW:
                ProcessSummaryType newProcess = mainController.openNewProcess();
                processService.linkSubprocess(parentProcessId, elementId, newProcess.getId(), UserSessionManager.getCurrentUser().getUsername());
                break;
            case LINK_TYPE_EXISTING:
                Notification.info("Subprocess linking to an existing process coming soon...");
                //TODO: Add list of processes with editor or owner access to choose from and add relation to db
                break;
            default:
                Notification.error("Please select a link type");
        }
        window.detach();
    }
}
