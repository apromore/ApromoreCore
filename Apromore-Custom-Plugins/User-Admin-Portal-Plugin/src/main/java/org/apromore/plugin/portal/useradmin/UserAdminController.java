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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.exception.UserNotFoundException;
import org.apromore.model.UserType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class UserAdminController {

    private static Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);

    final Window window;
    ListModelList<Group> groupsModel;
    final ListModelList<Role> rolesModel;

    UserAdminController(PortalContext portalContext, SecurityService securityService) throws IOException {
            window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/users.zul", null, null);

            final Combobox usersCombobox = (Combobox) window.getFellow("usersCombobox");
            ListModelList<User> usersModel = new ListModelList<>(securityService.getAllUsers(), false);
            usersCombobox.setModel(usersModel);
            usersCombobox.setValue(portalContext.getCurrentUser().getUsername());

            final Listbox groupsListbox = (Listbox) window.getFellow("groupsListbox");
            groupsModel = new ListModelList<>(securityService.findElectiveGroups(), false);
            groupsModel.setMultiple(true);
            groupsListbox.setModel(groupsModel);

            final Listbox rolesListbox = (Listbox) window.getFellow("rolesListbox");
            rolesModel = new ListModelList<>(securityService.getAllRoles(), false);
            rolesModel.setMultiple(true);
            rolesListbox.setModel(rolesModel);

            setUser(usersCombobox.getValue(), securityService);

            usersCombobox.addEventListener("onChange", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    setUser(usersCombobox.getValue(), securityService);
                }
            });

            groupsListbox.addEventListener("onOK", new EventListener<KeyEvent>() {
                public void onEvent(KeyEvent event) throws Exception {
                    LOGGER.info("Change name of group: data=" + event.getData() + " name=" + event.getName() + " reference=" + event.getReference() + " target=" + event.getTarget());
                    Textbox textbox = (Textbox) event.getReference();
                    Group group = securityService.findGroupByRowGuid(textbox.getId());
                    if ("".equals(textbox.getValue())) {
                        securityService.deleteGroup(group);
                        groupsModel.remove(group);

                    } else {
                        group.setName(textbox.getValue());
                        securityService.updateGroup(group);
                    }
                }
            });

            groupsListbox.addEventListener("onSelect", new EventListener<SelectEvent>() {
                public void onEvent(SelectEvent event) throws Exception {
                    User user = securityService.getUserByName(usersCombobox.getValue());
                    user.setGroups(event.getSelectedObjects());
                    securityService.updateUser(user);
                }
            });

            rolesListbox.addEventListener("onSelect", new EventListener<SelectEvent>() {
                public void onEvent(SelectEvent event) throws Exception {
                    User user = securityService.getUserByName(usersCombobox.getValue());
                    user.setRoles(event.getSelectedObjects());
                    securityService.updateUser(user);
                }
            });

            ((Button) window.getFellow("newGroupButton")).addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    securityService.createGroup("New group");

                    // Update groupsModel
                    User user = securityService.getUserByName(usersCombobox.getValue());
                    groupsModel = new ListModelList<>(securityService.findElectiveGroups(), false);
                    groupsModel.setMultiple(true);
                    groupsModel.setSelection(securityService.findGroupsByUser(user));
                    ((Listbox) window.getFellow("groupsListbox")).setModel(groupsModel);
                }
            });
            
            ((Button) window.getFellow("okButton")).addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    window.detach();
                }
            });

            window.doModal();
    }

    private void setUser(final String username, final SecurityService securityService) {
        LOGGER.info("Changed to " + username);

        User user = securityService.getUserByName(username);
        if (user == null) {
            // TODO: should clear existing fields
            return;
        }

        ((Textbox) window.getFellow("firstNameTextbox")).setValue(user.getFirstName());
        ((Textbox) window.getFellow("lastNameTextbox")).setValue(user.getLastName());
        ((Datebox) window.getFellow("dateCreatedDatebox")).setValue(user.getDateCreated());
        ((Datebox) window.getFellow("lastActivityDatebox")).setValue(user.getLastActivityDate());

        groupsModel.setSelection(securityService.findGroupsByUser(user));
        rolesModel.setSelection(securityService.findRolesByUser(user));
    }
}
