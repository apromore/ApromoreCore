/*
 * Copyright © 2009-2019 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */

package org.apromore.plugin.portal.useradmin;

import java.io.IOException;
import org.apromore.dao.model.User;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Window;

public class UserAdminController {

    private static Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);

    UserAdminController(PortalContext portalContext, UserService userService) throws IOException {
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/users.zul", null, null);

            ListModelList<User> model = new ListModelList<>(userService.findAllUsers(), false);
            ((Listbox) window.getFellow("usersListbox")).setModel(model);
            
            ((Button) window.getFellow("okButton")).addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    window.detach();
                }
            });

            window.doModal();
    }
}
