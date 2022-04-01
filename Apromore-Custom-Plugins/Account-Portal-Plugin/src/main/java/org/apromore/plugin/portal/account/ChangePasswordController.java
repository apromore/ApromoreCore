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

package org.apromore.plugin.portal.account;

import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import org.apromore.plugin.portal.PortalContext;
import org.apromore.plugin.portal.PortalLoggerFactory;
import org.apromore.service.SecurityService;
import org.apromore.zk.label.LabelSupplier;

public class ChangePasswordController implements LabelSupplier {

    private static Logger LOGGER = PortalLoggerFactory.getLogger(ChangePasswordController.class);

    @Override
    public String getBundleName() {
        return "account";
    }

    public ChangePasswordController(PortalContext portalContext, SecurityService securityService) {
        try {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/changePassword.zul", null, null);

            Label   username        = (Label)   window.getFellow("username");
            Textbox currentPassword = (Textbox) window.getFellow("currentPassword");
            Textbox newPassword     = (Textbox) window.getFellow("newPassword");
            Textbox confirmPassword = (Textbox) window.getFellow("confirmPassword");

            username.setValue(portalContext.getCurrentUser().getUsername());

            ((Button) window.getFellow("changePasswordButton")).addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    if (!Objects.equals(newPassword.getValue(), confirmPassword.getValue())) {
                        Messagebox.show(getLabel("passwordNotMatch"), null, Messagebox.OK, Messagebox.NONE);
                        return;
                    }

                    if (newPassword.getValue() == null || newPassword.getValue().length() < 6) {
                        Messagebox.show(getLabel("passwordTooShort"), null, Messagebox.OK, Messagebox.NONE);
                        return;
                    }

                    boolean success = securityService.changeUserPassword(username.getValue(), currentPassword.getValue(), newPassword.getValue());

                    if (success) {
                        window.detach();
                    } else {
                        Messagebox.show(getLabel("passwordNotChanged"), null, Messagebox.OK, Messagebox.NONE);
                    }
                }
            });

            ((Button) window.getFellow("cancelButton")).addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    window.detach();
                }
            });

            window.doModal();

        } catch (IOException e) {
            LOGGER.warn("Unable to edit account", e);
            Messagebox.show(getLabel("failedEditAccount"), "Apromore", Messagebox.OK, Messagebox.ERROR);
        }
    }
}
