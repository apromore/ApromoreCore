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
            boolean canViewUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.VIEW_USERS.getRowGuid());
            usersCombobox.setButtonVisible(canViewUsers);
            usersCombobox.setDisabled(!canViewUsers);
            usersCombobox.setModel(usersModel);
            usersCombobox.setReadonly(!canViewUsers);
            usersCombobox.setValue(portalContext.getCurrentUser().getUsername());

            final Textbox firstNameTextbox = (Textbox) window.getFellow("firstNameTextbox");
            final Textbox lastNameTextbox = (Textbox) window.getFellow("lastNameTextbox");
            boolean canEditUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_USERS.getRowGuid());
            firstNameTextbox.setReadonly(!canEditUsers);
            lastNameTextbox.setReadonly(!canEditUsers);

            final Listbox groupsListbox = (Listbox) window.getFellow("groupsListbox");
            boolean canEditGroups = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_GROUPS.getRowGuid());
            groupsModel = new ListModelList<>(securityService.findElectiveGroups(), false);
            groupsModel.setMultiple(true);
            groupsListbox.setModel(groupsModel);
            groupsListbox.setNonselectableTags(canEditGroups ? null : "*");

            final Listbox rolesListbox = (Listbox) window.getFellow("rolesListbox");
            boolean canEditRoles = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_ROLES.getRowGuid());
            rolesModel = new ListModelList<>(securityService.getAllRoles(), false);
            rolesModel.setMultiple(true);
            rolesListbox.setModel(rolesModel);
            rolesListbox.setNonselectableTags(canEditRoles ? null : "*");

            setUser(usersCombobox.getValue(), securityService);

            usersCombobox.addEventListener("onChange", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    boolean canEditUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_USERS.getRowGuid());
                    if (!canViewUsers) {
                        throw new Exception("Cannot view users without permission");
                    }

                    setUser(usersCombobox.getValue(), securityService);
                }
            });

            firstNameTextbox.addEventListener("onOK", new EventListener<KeyEvent>() {
                public void onEvent(KeyEvent event) throws Exception {
                    boolean canEditUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_USERS.getRowGuid());
                    if (!canEditUsers) {
                        throw new Exception("Cannot edit users without permission");
                    }

                    User user = securityService.getUserByName(portalContext.getCurrentUser().getUsername());
                    user.setFirstName(firstNameTextbox.getValue());
                    securityService.updateUser(user);
                }
            });

            lastNameTextbox.addEventListener("onOK", new EventListener<KeyEvent>() {
                public void onEvent(KeyEvent event) throws Exception {
                    boolean canEditUsers = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_USERS.getRowGuid());
                    if (!canEditUsers) {
                        throw new Exception("Cannot edit users without permission");
                    }

                    User user = securityService.getUserByName(portalContext.getCurrentUser().getUsername());
                    user.setLastName(lastNameTextbox.getValue());
                    securityService.updateUser(user);
                }
            });

            groupsListbox.addEventListener("onOK", new EventListener<KeyEvent>() {
                public void onEvent(KeyEvent event) throws Exception {
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
            });

            groupsListbox.addEventListener("onSelect", new EventListener<SelectEvent>() {
                public void onEvent(SelectEvent event) throws Exception {
                    boolean canEditGroups = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_GROUPS.getRowGuid());
                    if (!canEditGroups) {
                        throw new Exception("Cannot edit groups without permission");
                    }

                    User user = securityService.getUserByName(usersCombobox.getValue());
                    user.setGroups(event.getSelectedObjects());
                    securityService.updateUser(user);
                }
            });

            rolesListbox.addEventListener("onSelect", new EventListener<SelectEvent>() {
                public void onEvent(SelectEvent event) throws Exception {
                    boolean canEditRoles = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_ROLES.getRowGuid());
                    if (!canEditRoles) {
                        throw new Exception("Cannot edit roles without permission");
                    }

                    User user = securityService.getUserByName(usersCombobox.getValue());
                    user.setRoles(event.getSelectedObjects());
                    securityService.updateUser(user);
                }
            });

            Button newGroupButton = (Button) window.getFellow("newGroupButton");
            newGroupButton.setVisible(canEditGroups);
            newGroupButton.addEventListener("onClick", new EventListener<Event>() {
                public void onEvent(Event event) throws Exception {
                    boolean canEditGroups = securityService.hasAccess(portalContext.getCurrentUser().getId(), Permissions.EDIT_GROUPS.getRowGuid());
                    if (!canEditGroups) {
                        throw new Exception("Cannot edit groups without permission");
                    }

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
