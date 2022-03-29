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

package org.apromore.plugin.portal.useradmin;

import java.util.Map;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.service.SecurityService;
import org.apromore.zk.label.LabelSupplier;
import org.slf4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class CreateGroupController extends SelectorComposer<Window> implements LabelSupplier {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(CreateGroupController.class);

    private PortalContext portalContext =
        (PortalContext) Executions.getCurrent().getArg().get("portalContext");
    private SecurityService securityService =
        (SecurityService) /* SpringUtil.getBean("securityService"); */ Executions.getCurrent()
            .getArg().get("securityService");

    @Wire("#groupNameTextbox")
    Textbox groupNameTextbox;

    @Override
    public String getBundleName() {
        return "useradmin";
    }

    @Listen("onClick = #createBtn")
    public void onClickCreateButton() {
        boolean canEditGroups = securityService.hasAccess(portalContext.getCurrentUser().getId(),
            Permissions.EDIT_GROUPS.getRowGuid());
        if (!canEditGroups) {
            Messagebox.show(getLabel("noPermissionCreateGroup_message"));
            return;
        }

        try {
            if (securityService.getGroupByName(groupNameTextbox.getValue()) != null) {
                throw new Exception("Group already exists");
            }
            securityService.createGroup(groupNameTextbox.getValue());
            Map dataMap = Map.of("type", "CREATE_GROUP");
            EventQueues.lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true)
                .publish(new Event("Group Created", null, dataMap));
            getSelf().detach();


        } catch (Exception e) {
            LOGGER.error("Unable to create group", e);
            Messagebox.show(getLabel("failedCreateGroup_message"));
        }
        getSelf().detach();
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelButton() {
        getSelf().detach();
    }
}
