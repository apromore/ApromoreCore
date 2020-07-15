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
import java.util.Set;
import org.apromore.dao.model.Group;
import org.apromore.dao.model.Role;
import org.apromore.dao.model.User;
import org.apromore.portal.model.UserType;
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
import org.zkoss.zk.ui.Component;
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

import org.apromore.plugin.portal.useradmin.listbox.*;

public class UserAdminController extends SelectorComposer<Window> {

    private static Logger LOGGER = LoggerFactory.getLogger(UserAdminController.class);

    User currentUser;
    User selectedUser;
    ListModelList<Group> groupsModel;
    ListModelList<Role> rolesModel;
    ListModelList<User> usersModel;

    UsersListbox searchableUsersListbox;
    GroupsListbox searchableGroupsListbox;

    boolean canViewUsers;
    boolean canEditUsers;
    boolean canEditGroups;
    boolean canEditRoles;

    private PortalContext portalContext = (PortalContext) Executions.getCurrent().getArg().get("portalContext");
    private SecurityService securityService = (SecurityService) /*SpringUtil.getBean("securityService");*/ Executions.getCurrent().getArg().get("securityService");

    @Wire("#usersCombobox")       Combobox usersCombobox;
    @Wire("#firstNameTextbox")    Textbox  firstNameTextbox;
    @Wire("#lastNameTextbox")     Textbox  lastNameTextbox;
    @Wire("#usersListbox")        Listbox  usersListbox;
    @Wire("#groupsListbox")       Listbox  groupsListbox;
    @Wire("#rolesListbox")        Listbox  rolesListbox;
    @Wire("#newGroupButton")      Button   newGroupButton;
    @Wire("#newUserButton")       Button   newUserButton;
    @Wire("#deleteUserButton")    Button   deleteUserButton;
    @Wire("#dateCreatedDatebox")  Datebox  dateCreatedDatebox;
    @Wire("#lastActivityDatebox") Datebox  lastActivityDatebox;

    /**
     * Test whether the current user has a permission.
     *
     * @param permission  any permission
     * @return whether the authenticated user has the <var>permission</var>
     */
    private boolean hasPermission(Permissions permission) {
        return securityService.hasAccess(portalContext.getCurrentUser().getId(), permission.getRowGuid());
    }

    @Override
    public void doAfterCompose(Window win) throws Exception {
        super.doAfterCompose(win);

        currentUser = securityService.getUserByName(portalContext.getCurrentUser().getUsername());

        canViewUsers = hasPermission(Permissions.VIEW_USERS);
        canEditUsers = hasPermission(Permissions.EDIT_USERS);
        canEditGroups = hasPermission(Permissions.EDIT_GROUPS);
        canEditRoles = hasPermission(Permissions.EDIT_ROLES);

        rolesModel = new ListModelList<>(securityService.getAllRoles(), false);
        usersModel = new ListModelList<>(securityService.getAllUsers(), false);
        groupsModel = new ListModelList<>(securityService.findElectiveGroups(), false);

        rolesModel.setMultiple(true);
        usersModel.setMultiple(true);
        groupsModel.setMultiple(true);

        searchableUsersListbox = new UsersListbox(usersListbox, usersModel);
        searchableGroupsListbox = new GroupsListbox(groupsListbox, groupsModel);

        usersCombobox.setButtonVisible(canViewUsers);
        usersCombobox.setDisabled(!canViewUsers);
        usersCombobox.setModel(usersModel);
        usersCombobox.setReadonly(!canViewUsers);

        firstNameTextbox.setReadonly(!canEditUsers);
        lastNameTextbox.setReadonly(!canEditUsers);

        newGroupButton.setVisible(canEditGroups);

        // groupsListbox.setModel(groupsModel);
        groupsListbox.setNonselectableTags(canEditGroups ? null : "*");

        rolesListbox.setModel(rolesModel);
        rolesListbox.setNonselectableTags(canEditRoles ? null : "*");

        setUser(currentUser);

        // Register ZK event handler
        EventQueue securityEventQueue = EventQueues.lookup(SecurityService.EVENT_TOPIC, getSelf().getDesktop().getWebApp(), true);
        securityEventQueue.subscribe(new EventListener() {
            @Override public void onEvent(Event event) {
                if (getSelf().getDesktop() == null) {
                    securityEventQueue.unsubscribe(this);

                } else {
                    Map properties = (Map) event.getData();

                    // Update the user combobox
                    if (((String) properties.get("type")).endsWith("_USER")) {
                        ListModelList<User> usersModel = new ListModelList<>(securityService.getAllUsers(), false);
                        usersCombobox.setModel(usersModel);
                        usersCombobox.setValue(selectedUser.getUsername());
                    }

                    // Skip this update if it doesn't apply to the currently displayed user
                    String eventUserName = (String) properties.get("user.name");
                    if (eventUserName != null && !eventUserName.equals(selectedUser.getUsername())) {
                        return;
                    }

                    // Update the user panel
                    if ("UPDATE_USER".equals(properties.get("type"))) {
                        selectedUser = securityService.getUserByName(selectedUser.getUsername());
                        setUser(selectedUser);
                    }

                    // Update the group listbox
                    groupsModel = new ListModelList<>(securityService.findElectiveGroups(), false);
                    groupsModel.setMultiple(true);
                    if (selectedUser != null) {
                        groupsModel.setSelection(securityService.findGroupsByUser(selectedUser));
                    }
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
                    Map<String, Object> properties = new HashMap<>();
                    for (String propertyName: event.getPropertyNames()) {
                        properties.put(propertyName, event.getProperty(propertyName));
                    }
                    securityEventQueue.publish(new Event(event.getTopic(), null, properties));
                }
            }, properties);
        }
    }

    private void setUser(final User user) {
        usersCombobox.setValue(user.getUsername());
        firstNameTextbox.setValue(user.getFirstName());
        lastNameTextbox.setValue(user.getLastName());
        dateCreatedDatebox.setValue(user.getDateCreated());
        lastActivityDatebox.setValue(user.getLastActivityDate());

        groupsModel.setSelection(securityService.findGroupsByUser(user));
        rolesModel.setSelection(securityService.findRolesByUser(user));
    }

    @Listen("onSelect = #usersListbox")
    public void onSelectUsersListbox(SelectEvent event) throws Exception {
        if (!hasPermission(Permissions.VIEW_USERS)) {
            throw new Exception("Cannot view users without permission");
        }
        Set<User> selectedUsers = event.getSelectedItems();
        Set<User> unselectedUsers = event.getUnselectedItems();
        // selectedUser = securityService.getUserByName();
        // setUser(selectedUser);
    }

    @Listen("onChange = #usersCombobox")
    public void onChangeUsersCombobox() throws Exception {
        if (!hasPermission(Permissions.VIEW_USERS)) {
            throw new Exception("Cannot view users without permission");
        }

        selectedUser = securityService.getUserByName(usersCombobox.getValue());
        setUser(selectedUser);
    }

    @Listen("onOK = #firstNameTextbox")
    public void onOKFirstNameTextbox(KeyEvent event) throws Exception {
        if (!hasPermission(Permissions.EDIT_USERS)) {
            throw new Exception("Cannot edit users without permission");
        }

        selectedUser.setFirstName(firstNameTextbox.getValue());
        securityService.updateUser(selectedUser);
    }

    @Listen("onOK = #lastNameTextbox")
    public void onOKLastNameTextbox(KeyEvent event) throws Exception {
        if (!hasPermission(Permissions.EDIT_USERS)) {
            throw new Exception("Cannot edit users without permission");
        }

        selectedUser.setLastName(lastNameTextbox.getValue());
        securityService.updateUser(selectedUser);
    }

    @Listen("onOK = #groupsListbox")
    public void onOKGroupsListbox(KeyEvent event) throws Exception {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
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
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            groupsListbox.setSelectedItems(event.getPreviousSelectedItems());
            throw new Exception("Cannot edit groups without permission");
        }

        selectedUser.setGroups(event.getSelectedObjects());
        securityService.updateUser(selectedUser);
    }

    @Listen("onSelect = #rolesListbox")
    public void onSelectRolesListbox(SelectEvent event) throws Exception {
        if (!hasPermission(Permissions.EDIT_ROLES)) {
            rolesListbox.setSelectedItems(event.getPreviousSelectedItems());
            throw new Exception("Cannot edit roles without permission");
        }

        selectedUser.setRoles(event.getSelectedObjects());
        securityService.updateUser(selectedUser);
    }

    @Listen("onClick = #newGroupButton")
    public void onClickNewGroupButton() throws Exception {
        if (!hasPermission(Permissions.EDIT_GROUPS)) {
            throw new Exception("Cannot edit groups without permission");
        }

        try {
            Map arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            Window window = (Window) Executions.getCurrent().createComponents("create-group.zul", getSelf(), arg);
            window.doModal();

        } catch(Exception e) {
            LOGGER.error("Unable to create group creation dialog", e);
            Messagebox.show("Unable to create group creation dialog");
        }
    }

    @Listen("onClick = #newUserButton")
    public void onClickNewUserButton() throws Exception {
        if (!hasPermission(Permissions.EDIT_USERS)) {
            throw new Exception("Cannot edit users without permission");
        }

        try {
            Map arg = new HashMap<>();
            arg.put("portalContext", portalContext);
            arg.put("securityService", securityService);
            Window window = (Window) Executions.getCurrent().createComponents("create-user.zul", getSelf(), arg);
            window.doModal();

        } catch(Exception e) {
            LOGGER.error("Unable to create user creation dialog", e);
            Messagebox.show("Unable to create user creation dialog");
        }
    }

    @Listen("onClick = #deleteUserButton")
    public void onClickDeleteUserButton() throws Exception {
        if (!hasPermission(Permissions.EDIT_USERS)) {
            throw new Exception("Cannot edit users without permission");
        }

        if (selectedUser.getUsername().equals(portalContext.getCurrentUser().getUsername())) {
            throw new Exception("Cannot delete yourself");
        }

        LOGGER.info("Deleting user " + selectedUser.getUsername());
        securityService.deleteUser(selectedUser);

        // Since we just deleted the selected user, select the current user instead
        selectedUser = securityService.getUserByName(portalContext.getCurrentUser().getUsername());
        setUser(selectedUser);
    }

    @Listen("onClick = #okButton")
    public void onClickOkButton() {
        getSelf().detach();
    }
}
