/**
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
package org.apromore.plugin.portal.processpublisher;

import org.apromore.commons.config.ConfigBean;
import org.apromore.dao.model.User;
import org.apromore.plugin.portal.DefaultPortalPlugin;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.portal.common.ItemHelpers;
import org.apromore.portal.dialogController.MainController;
import org.apromore.portal.model.ProcessSummaryType;
import org.apromore.portal.model.SummaryType;
import org.apromore.portal.model.VersionSummaryType;
import org.apromore.service.SecurityService;
import org.apromore.zk.label.LabelSupplier;
import org.apromore.zk.notification.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;

/**
 * The process publisher plugin is responsible for creating and revoking links to view models in view-only mode.
 *
 * @author Jane Hoh.
 */
@Component
public class ProcessPublisherPlugin extends DefaultPortalPlugin implements LabelSupplier {

    @Autowired
    ConfigBean config;

    @Inject
    private SecurityService securityService;

    @Override
    public String getBundleName() {
        return "process_publisher";
    }

    @Override
    public String getLabel(final Locale locale) {
        return Labels.getLabel("plugin_process_publish_text", "Publish model");
    }

    @Override
    public String getIconPath() {
        return "link.svg";
    }

    @Override
    public Availability getAvailability() {
        return config.isEnableModelPublish() ? Availability.AVAILABLE : Availability.UNAVAILABLE;
    }

    @Override
    public void execute(PortalContext portalContext) {
        try {
            ProcessSummaryType processSummaryType = getSelectedModel(portalContext);
            User currentUser = securityService.getUserById(portalContext.getCurrentUser().getId());
            boolean canPublish = ItemHelpers.isOwner(currentUser, processSummaryType);

            if (!canPublish) {
                throw new IllegalAccessException(getLabel("exception_incorrectRights"));
            }

            Map<String, Object> arg = new HashMap<>();
            arg.put("processId", processSummaryType.getId());
            PageDefinition pageDefinition = getPageDefinition("static/processpublisher/zul/publishModel.zul");

            Window window =
                    (Window) Executions.getCurrent().createComponents(pageDefinition, null, arg);

            window.doModal();
        } catch (Exception e) {
            Notification.error(e.getMessage());
        }
    }

    /**
     * Extracts the selected process model from the portal context.
     * @param portalContext
     * @return the selected process model.
     * @throws IllegalArgumentException if the selection does not include exactly one process model.
     */
    public ProcessSummaryType getSelectedModel(PortalContext portalContext) throws IllegalArgumentException {
        MainController mainController = (MainController) portalContext.getMainController();
        Map<SummaryType, List<VersionSummaryType>> selectedProcessVersions =
                mainController.getSelectedElementsAndVersions();
        if (selectedProcessVersions.size() != 1) {
            throw new IllegalArgumentException(getLabel("exception_incorrectNumberOfProcess"));
        }

        SummaryType summaryType = selectedProcessVersions.keySet().iterator().next();
        if (!(summaryType instanceof ProcessSummaryType)) {
            throw new IllegalArgumentException(getLabel("exception_incorrectType"));
        }

        return (ProcessSummaryType) summaryType;
    }

    private PageDefinition getPageDefinition(String uri) throws IOException {
        Execution current = Executions.getCurrent();
        return current.getPageDefinitionDirectly(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream(uri)), "zul");
    }
}
