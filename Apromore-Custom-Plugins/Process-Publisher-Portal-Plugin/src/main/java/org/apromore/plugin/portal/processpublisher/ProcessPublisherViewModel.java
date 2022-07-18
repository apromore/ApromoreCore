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
package org.apromore.plugin.portal.processpublisher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.apromore.dao.model.ProcessPublish;
import org.apromore.exception.UserNotFoundException;
import org.apromore.portal.common.UserSessionManager;
import org.apromore.portal.model.UserType;
import org.apromore.service.ProcessPublishService;
import org.apromore.service.ProcessService;
import org.apromore.util.AccessType;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ExecutionArgParam;
import org.zkoss.bind.annotation.Init;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Messagebox;

@Data
@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class ProcessPublisherViewModel implements LabelSupplier {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessPublisherViewModel.class);
    private static final String PUBLISH_LINK_FORMAT = "%s://%s/zkau/web/openModelInBPMNio.zul?view=true&publishId=%s";
    private static final String WINDOW_PARAM = "window";

    @WireVariable
    private ProcessPublishService processPublishService;

    @WireVariable
    private ProcessService processService;

    private String publishId = "";
    private boolean publish = false;
    private boolean hasLinkedSubprocesses = false;
    private boolean publishLinkedSubprocesses = false;
    private boolean newPublishRecord = true;
    private int processId;

    @Init
    public void init(@ExecutionArgParam("processId") final int processId) {
        this.processId = processId;
        ProcessPublish processPublishDetails = processPublishService.getPublishDetails(processId);
        newPublishRecord = processPublishDetails == null;
        publish = !newPublishRecord && processPublishDetails.isPublished();
        publishId = newPublishRecord ?
                UUID.randomUUID().toString() : processPublishDetails.getPublishId();

        UserType user = UserSessionManager.getCurrentUser();
        try {
            hasLinkedSubprocesses = user != null && processService.hasLinkedProcesses(processId, user.getUsername());
        } catch (UserNotFoundException e) {
            hasLinkedSubprocesses = false;
        }
    }

    @Command
    public void updatePublishRecord(@BindingParam(WINDOW_PARAM) final Component window) {
        String publishNotificationKey;
        if (newPublishRecord) {
            processPublishService.savePublishDetails(processId, publishId, publish);
            publishNotificationKey = publish ? "publish_link_success_msg" : "new_inactive_link_msg";
        } else {
            processPublishService.updatePublishStatus(publishId, publish);
            publishNotificationKey = publish ? "publish_link_success_msg" : "unpublish_link_success_msg";
        }

        try {
            if (publish && publishLinkedSubprocesses) {
                updateLinkedSubprocessesPublishStatus(processId, List.of(processId));
            } else if (!publish && hasLinkedSubprocesses) {
                Messagebox.show(getLabel("unpublish_linked_subprocess_models_msg"),
                    Labels.getLabel("plugin_process_unpublish_text"), Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                    event -> {
                        if (Messagebox.ON_YES.equals(event.getName())) {
                            updateLinkedSubprocessesPublishStatus(processId, List.of(processId));
                        }
                    });
            }
        } catch (UserNotFoundException e) {
            LOGGER.error(e.getMessage());
        }

        Notification.info(getLabel(publishNotificationKey));
        Clients.evalJavaScript(String.format("onUpdatePublishState(%s)", publish));
        window.detach();
    }

    @Override
    public String getBundleName() {
        return "process_publisher";
    }

    public String getPublishLink() {
        String scheme = Executions.getCurrent().getScheme();
        String serverName = Executions.getCurrent().getServerName();

        if ("localhost".equals(serverName)) {
            serverName += ":" + Executions.getCurrent().getServerPort();
        }

        return String.format(PUBLISH_LINK_FORMAT, scheme, serverName, publishId);
    }

    private List<Integer> updateLinkedSubprocessesPublishStatus(int pId, List<Integer> publishedProcesses)
        throws UserNotFoundException {
        List<Integer> skipList = new ArrayList<>(publishedProcesses);
        UserType user = UserSessionManager.getCurrentUser();
        if (user == null) {
            throw new UserNotFoundException("Unable to get current user from the session");
        }

        Collection<Integer> linkedProcesses = processService
            .getLinkedProcesses(pId, user.getUsername(), AccessType.OWNER)
            .values();

        for (int linkedProcessId : linkedProcesses) {
            if (skipList.contains(linkedProcessId)) {
                continue;
            }

            if (processPublishService.getPublishDetails(linkedProcessId) == null) {
                processPublishService.savePublishDetails(linkedProcessId, UUID.randomUUID().toString(), publish);
            } else {
                processPublishService.updatePublishStatus(linkedProcessId, publish);
            }

            skipList.add(linkedProcessId);
            skipList.addAll(updateLinkedSubprocessesPublishStatus(linkedProcessId, skipList));
        }
        return skipList;
    }
}
