/*-
 * #%L
 * This file is part of "Apromore Core".
 *
 * Copyright (C) 2011 - 2017 Queensland University of Technology.
 * Copyright (C) 2012 Felix Mannhardt.
 * Copyright (C) 2015 Adriano Augusto.
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

package org.apromore.portal.dialogController;

import org.apromore.portal.context.PortalPluginResolver;
import org.apromore.portal.model.UserType;
import org.apromore.service.EventLogService;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.Menubar;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class UserMenuController extends BaseMenuController {

  @WireVariable
  private EventLogService eventLogService;

  protected AutowireCapableBeanFactory beanFactory;

    @Override
    public void doAfterCompose(Menubar menubar) {
        super.doAfterCompose(menubar);

        // If there are portal plugins, create the menus for launching them
        if (!PortalPluginResolver.resolve().isEmpty()) {
            loadMenu(menubar, "user-menu");

            // The signOutQueue receives events whose data is a ZK session which has signed out
            // If this desktop is part of a signed-out session, close the browser tab or switch to login
            EventQueues.lookup("signOutQueue", EventQueues.DESKTOP, true)
                    .subscribe(event -> {
                        Session session = Sessions.getCurrent();

                        if (session != null) {
                            if (!eventLogService.getConfigBean().getKeycloak().isEnabled()) {
                                session.invalidate();
                            }
                            Executions.sendRedirect("/logout");
                        }
                    });

            // Force logout a user that has been deleted by admin
            EventQueues.lookup("forceSignOutQueue", EventQueues.APPLICATION, true)
                    .subscribe(event -> {
                        Session session = Sessions.getCurrent();

                        UserType userType = (UserType) Sessions.getCurrent().getAttribute("USER");
                        if (session == null || event.getData().equals(userType.getUsername())) {
                            Executions.sendRedirect("/logout");
                        }
                    });
        }
    }
}
