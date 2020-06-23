/*-
 * #%L
 * This file is part of "Apromore Core".
 * %%
 * Copyright (C) 2018 - 2020 Apromore Pty Ltd.
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

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.model.UserType;
import org.apromore.plugin.portal.PortalContext;
import org.apromore.service.SecurityService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;;
//import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class UserAdminController extends SelectorComposer<Window> {

    private static Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);

    ListModelList<Group> groupsModel;
    ListModelList<Role> rolesModel;

    private PortalContext portalContext = (PortalContext) Executions.getCurrent().getArg().get("portalContext");
    private SecurityService securityService = (SecurityService) /*SpringUtil.getBean("securityService");*/ Executions.getCurrent().getArg().get("securityService");

    @Wire("#usersCombobox")       Combobox usersCombobox;
    @Wire("#firstNameTextbox")    Textbox  firstNameTextbox;
    @Wire("#lastNameTextbox")     Textbox  lastNameTextbox;
    @Wire("#groupsListbox")       Listbox  groupsListbox;
    @Wire("#rolesListbox")        Listbox  rolesListbox;
    @Wire("#newGroupButton")      Button   newGroupButton;
    @Wire("#dateCreatedDatebox")  Datebox  dateCreatedDatebox;
    @Wire("#lastActivityDatebox") Datebox  lastActivityDatebox;

    @Override
    public void doFinally() throws Exception {
        super.doFinally();

        ListModelList<User> usersModel = new ListModelList<>(securityService.getAllUsers(), false);
        String userId = portalContext.getCurrentUser().getId();

        boolean canViewUsers = securityService.hasAccess(userId, Permissions.VIEW_USERS.getRowGuid());
        usersCombobox.setButtonVisible(canViewUsers);
        usersCombobox.setDisabled(!canViewUsers);
        usersCombobox.setModel(usersModel);
        usersCombobox.setReadonly(!canViewUsers);
        usersCombobox.setValue(portalContext.getCurrentUser().getUsername());

        boolean canEditUsers = securityService.hasAccess(userId, Permissions.EDIT_USERS.getRowGuid());
        firstNameTextbox.setReadonly(!canEditUsers);
        lastNameTextbox.setReadonly(!canEditUsers);

        boolean canEditGroups = securityService.hasAccess(userId, Permissions.EDIT_GROUPS.getRowGuid());
        newGroupButton.setVisible(canEditGroups);
        groupsModel = new ListModelList<>(securityService.findElectiveGroups(), false);
        groupsModel.setMultiple(true);
        groupsListbox.setModel(groupsModel);
        groupsListbox.setNonselectableTags(canEditGroups ? null : "*");

        boolean canEditRoles = securityService.hasAccess(userId, Permissions.EDIT_ROLES.getRowGuid());
        rolesModel = new ListModelList<>(securityService.getAllRoles(), false);
        rolesModel.setMultiple(true);
        rolesListbox.setModel(rolesModel);
        rolesListbox.setNonselectableTags(canEditRoles ? null : "*");

        setUser(usersCombobox.getValue());

        // Register ZK event handler
        EventQueue securityEventQueue = EventQueues.lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true);
        securityEventQueue.subscribe(new EventListener() {
            @Override public void onEvent(Event event) {
                if (getSelf().getDesktop() == null) {
                    securityEventQueue.unsubscribe(this);

                } else {
                    groupsModel = new ListModelList<>(securityService.findElectiveGroups(), false);
                    groupsModel.setMultiple(true);
                    groupsListbox.setModel(groupsModel);
                }
            }
        });

        // Register OSGi event handler
        BundleContext bundleContext = (BundleContext) getSelf().getDesktop().getWebApp().getServletContext().getAttribute("osgi-bundlecontext");
        String filter = "(" + EventConstants.EVENT_TOPIC + "=" + SecurityService.EVENT_TOPIC + ")";
        Collection<ServiceReference> forwarders = bundleContext.getServiceReferences(EventHandler.class, filter);
        if (forwarders.isEmpty()) {
            Dictionary<String, Object> properties = new Hashtable<>();
            properties.put(EventConstants.EVENT_TOPIC, SecurityService.EVENT_TOPIC);
            bundleContext.registerService(EventHandler.class.getName(), new EventHandler() {
                @Override
                public final void handleEvent(org.osgi.service.event.Event event) {
                    securityEventQueue.publish(new Event("onGroupEvent", null, event.getProperty("group.name")));
                }
            }, properties);
        }
    }

    @Listen("onChange = #usersCombobox")
    public void onChangeUsersCombobox() throws Exception {
        boolean canViewUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.VIEW_USERS.getRowGuid());
        if (!canViewUsers) {
            throw new Exception("Cannot view users without permission");
        }

        setUser(usersCombobox.getValue());
    }

    private void setUser(final String username) {
        LOGGER.info("Changed to " + username);

        User user = securityService.getUserByName(username);
        if (user == null) {
            // TODO: should clear existing fields
            return;
        }

        firstNameTextbox.setValue(user.getFirstName());
        lastNameTextbox.setValue(user.getLastName());
        dateCreatedDatebox.setValue(user.getDateCreated());
        lastActivityDatebox.setValue(user.getLastActivityDate());

        groupsModel.setSelection(securityService.findGroupsByUser(user));
        rolesModel.setSelection(securityService.findRolesByUser(user));
    }

    @Listen("onOK = #firstNameTestbox")
    public void onOKFirstNameTestbox(KeyEvent event) throws Exception {
        boolean canEditUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_USERS.getRowGuid());
        if (!canEditUsers) {
            throw new Exception("Cannot edit users without permission");
        }

        User user = securityService.getUserByName(portalContext.getCurrentUser().getUsername());
        user.setFirstName(firstNameTextbox.getValue());
        securityService.updateUser(user);
    }

    @Listen("onOK = #lastNameTextbox")
    public void onOKLastNameTextbox(KeyEvent event) throws Exception {
        boolean canEditUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_USERS.getRowGuid());
        if (!canEditUsers) {
            throw new Exception("Cannot edit users without permission");
        }

        User user = securityService.getUserByName(portalContext.getCurrentUser().getUsername());
        user.setLastName(lastNameTextbox.getValue());
        securityService.updateUser(user);
    }

    @Listen("onOK = #groupsListbox")
    public void onOKGroupsListbox(KeyEvent event) throws Exception {
        boolean canEditGroups = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_GROUPS.getRowGuid());
        if (!canEditGroups) {
            throw new Exception("Cannot edit groups without permission");
        }

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

    @Listen("onSelect = #groupsListbox")
    public void onSelectGroupsListbox(SelectEvent event) throws Exception {
        boolean canEditGroups = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_GROUPS.getRowGuid());
        if (!canEditGroups) {
            throw new Exception("Cannot edit groups without permission");
        }

        User user = securityService.getUserByName(usersCombobox.getValue());
        user.setGroups(event.getSelectedObjects());
        securityService.updateUser(user);
    }

    @Listen("onSelect = #rolesListbox")
    public void onSelectRolesListbox(SelectEvent event) throws Exception {
        boolean canEditRoles = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_ROLES.getRowGuid());
        if (!canEditRoles) {
            throw new Exception("Cannot edit roles without permission");
        }

        User user = securityService.getUserByName(usersCombobox.getValue());
        user.setRoles(event.getSelectedObjects());
        securityService.updateUser(user);
    }

    @Listen("onClick = #newGroupButton")
    public void onClickNewGroupButton() throws Exception {
        boolean canEditGroups = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_GROUPS.getRowGuid());
        if (!canEditGroups) {
            throw new Exception("Cannot edit groups without permission");
        }

        try {
            Map arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            Window window = (Window) portalContext.getUI().createComponent(getClass().getClassLoader(), "zul/create-group.zul", getSelf(), arg);
            window.doModal();

        } catch(Exception e) {
            LOGGER.error("Unable to create group creation dialog", e);
            Messagebox.show("Unable to create group creation dialog");
        }
    }

    @Listen("onClick = #okButton")
    public void onClickOkButton() {
        getSelf().detach();
    }
}
