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
import org.apromore.dao.model.User;
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

public class CreateUserController extends SelectorComposer<Window> implements LabelSupplier {

    private static final Logger LOGGER = PortalLoggerFactory.getLogger(CreateUserController.class);

    private PortalContext portalContext =
        (PortalContext) Executions.getCurrent().getArg().get("portalContext");
    private SecurityService securityService =
        (SecurityService) /* SpringUtil.getBean("securityService"); */ Executions.getCurrent()
            .getArg().get("securityService");

    @Wire("#userNameTextbox")
    Textbox userNameTextbox;
    @Wire("#firstNameTextbox")
    Textbox firstNameTextbox;
    @Wire("#lastNameTextbox")
    Textbox lastNameTextbox;
    @Wire("#emailTextbox")
    Textbox emailTextbox;
    @Wire("#passwordTextbox")
    Textbox passwordTextbox;

    @Override
    public String getBundleName() {
        return "useradmin";
    }

    @Listen("onClick = #createBtn")
    public void onClickCreateButton() {
        boolean canEditUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(),
            Permissions.EDIT_USERS.getRowGuid());
        if (!canEditUsers) {
            Messagebox.show(getLabel("noPermissionCreateUser_message"));
            return;
        }

        if (!userNameTextbox.isValid()) {
            Messagebox.show(getLabel("failedCreateUserInvalidUsername_message"));
            return;
        }

        if (!emailTextbox.isValid()) {
            Messagebox.show(getLabel("failedCreateUserInvalidEmail_message"));
            return;
        }

        if (!passwordTextbox.isValid()) {
            Messagebox.show(getLabel("failedCreateUserInvalidPassword_message"));
            return;
        }

        //Check for groups with the same name as the user
        if (securityService.getGroupByName(userNameTextbox.getValue()) != null) {
            Messagebox.show(getLabel("failedCreateUser_message"));
            return;
        }

        try {
            User user = new User();
            user.setUsername(userNameTextbox.getValue());
            user.setFirstName(firstNameTextbox.getValue());
            user.setLastName(lastNameTextbox.getValue());

            user.getMembership().setEmail(emailTextbox.getValue());
            user.getMembership().setUser(user);
            securityService.createUser(user, passwordTextbox.getValue());

            Map dataMap = Map.of("type", "CREATE_USER");

            EventQueues.lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true)
                .publish(new Event("User Create", null, dataMap));

        } catch (Exception e) {
            LOGGER.error("Unable to create user", e);
            Messagebox.show(getLabel("failedCreateUser_message"));
        }
        getSelf().detach();
    }

    @Listen("onClick = #cancelBtn")
    public void onClickCancelButton() {
        getSelf().detach();
    }
}
